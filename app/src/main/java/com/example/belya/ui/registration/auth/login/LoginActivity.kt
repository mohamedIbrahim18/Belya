package com.example.belya.ui.registration.auth.login

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.belya.databinding.ActivityLoginBinding
import com.example.belya.Constant
import com.example.belya.base.showDialog
import com.example.belya.ui.customer_main.CustomerMainActivity
import com.example.belya.ui.technician_main.TechnicianMainActivity
import com.example.belya.ui.registration.auth.forget_password.ForgetPasswordActivity
import com.example.belya.ui.registration.auth.signup.SignUpActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    val REQUEST_LOCATION_CODE = 120

    private lateinit var viewBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initViews()
    }

    private fun initViews() {
        if (isGpsPermissionAllowed()) {
            getUserLocation()
        } else {
            requestPermission()
        }
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        viewBinding.progressBar.visibility = View.GONE
        viewBinding.btnLogin.setOnClickListener {
            viewBinding.progressBar.visibility = View.VISIBLE
            viewBinding.btnLogin.visibility = View.GONE
            val email = viewBinding.emailEd.text.toString()
            val password = viewBinding.passwordEd.text.toString()
            if (checkValidate()) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Check user type and navigate accordingly
                        checkUserTypeAndNavigate()
                        viewBinding.progressBar.visibility = View.GONE
                        viewBinding.btnLogin.visibility = View.VISIBLE


                    } else {
                        Snackbar.make(
                            viewBinding.root,
                            it.exception?.localizedMessage!!,
                            Snackbar.LENGTH_LONG
                        ).show()
                        viewBinding.progressBar.visibility = View.GONE
                        viewBinding.btnLogin.visibility = View.VISIBLE
                    }
                }
            }

        }
        viewBinding.dontHaveAccount.setOnClickListener {
            navigateToSignUp()
        }
        viewBinding.forgetYourPassword.setOnClickListener {
            navigateToForgetPassword()
        }
    }

    private fun checkValidate(): Boolean {
        var isValid = true
        if (viewBinding.emailEd.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutEmail.error = "Please enter your email"
            isValid = false
        } else {
            viewBinding.inputlayoutEmail.error = null

        }

        if (viewBinding.passwordEd.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutPassword.error = "Please enter a strong password"
            isValid = false
        } else {
            viewBinding.inputlayoutPassword.error = null
        }
        return isValid
    }

    private fun navigateToForgetPassword() {
        val intent = Intent(this, ForgetPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkUserTypeAndNavigate() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            db.collection(Constant.USER)
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userType = document.getString("userType")
                        userType?.let {
                            if (it == "Technician") {
                                navigateToTechnician()
                            } else if (it == "Customer") {
                                navigateToCustomer()
                            } else {
                                // Handle other user types if needed
                                Snackbar.make(
                                    viewBinding.root,
                                    "Error",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        // Document doesn't exist or user type is not set
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Snackbar.make(
                        viewBinding.root,
                        e.localizedMessage,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun navigateToCustomer() {
        val intent = Intent(this, CustomerMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToTechnician() {
        val intent = Intent(this, TechnicianMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val requestGPSPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // user accept take location
            getUserLocation()
        } else {
            // user decline take location
            showDialog(
                message = "We can't get the nearest people to you..." + " Sorry for you",
                context = this,
                posActionName = "ok",
                posAction = { dialog, i ->
                    dialog.dismiss()
                }
            )
        }
    }

    private fun getUserLocation() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@addOnSuccessListener
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this,
                        REQUEST_LOCATION_CODE
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
    }


    private fun requestPermission() {
        // lma y3ml deny ana hna bshr7 leh el permission mohm
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            showDialog(
                this,
                message = "Please enable location permission to get the nearest tech",
                posActionName = "Yes",
                posAction = { dialog, _ ->
                    dialog.dismiss()
                    // show permission for location
                    requestGPSPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                },
                negActionName = "No",
                negAction = { dialog, _ ->
                    dialog.dismiss()
                }
            )
        } else {
            // request permission
            requestGPSPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun isGpsPermissionAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            for (location in result.locations) {
                Log.e("location update", "" + location.latitude + " " + location.longitude)
                saveLocationToFirestore(location.latitude, location.longitude)

            }
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }



    val locationRequest = LocationRequest.create()?.apply {
        interval = 1000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun saveLocationToFirestore(latitude: Double, longitude: Double) {
        val currentUser = auth.currentUser
        val id = auth.currentUser?.uid
        currentUser?.let {
            val userRef = db.collection(Constant.USER).document(it.uid)
            val locationData = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )
            userRef.collection("Locations").document(id!!).set(locationData).addOnSuccessListener {
            }
                .addOnFailureListener { e ->
                    Log.w("Fail", "Error adding location", e)
                }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}