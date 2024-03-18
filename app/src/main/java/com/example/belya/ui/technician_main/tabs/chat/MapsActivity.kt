package com.example.belya.ui.technician_main.tabs.chat

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.belya.Constant
import com.example.belya.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.belya.databinding.ActivityMapsBinding
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    val REQUEST_LOCATION_CODE = 120

    private var googleMap: GoogleMap? = null
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getUserLocation()

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
    private fun saveLocationToFirestore(latitude: Double, longitude: Double) {
        val currentUser = FirebaseAuth.getInstance().uid
        currentUser?.let {
            val userRef = FirebaseFirestore.getInstance().collection(Constant.USER).document(it)
            val locationData = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )
            userRef.collection("Locations").document(it).set(locationData).addOnSuccessListener {
            }
                .addOnFailureListener { e ->
                    Log.w("Fail", "Error adding location", e)
                }
        }
    }
    val locationRequest = LocationRequest.create()?.apply {
        interval = 1000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    var userMarker: Marker? = null


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (resultCode == RESULT_OK) {
                getUserLocation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val otherUserId = intent.getStringExtra("otherUserId")
        otherUserId?.let {
            drawUserMarkerOnMap(it)
        }
    }

    private fun drawUserMarkerOnMap(userId: String) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(userId).collection("Locations").document(userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Error", "Error fetching user location: $error")
                    return@addSnapshotListener
                }
                if (value != null && value.exists()) {
                    val latitude = value.getDouble("latitude") ?: 0.0
                    val longitude = value.getDouble("longitude") ?: 0.0
                    val latLng = LatLng(latitude, longitude)

                    if (userMarker == null) {
                        val markerOptions =
                            MarkerOptions().position(latLng).title("Current Location")
                        userMarker = googleMap?.addMarker(markerOptions)
                    } else {
                        userMarker?.position = latLng
                    }
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 2.0f))
                } else {
                    Log.e("User location", "Document does not exist for user: $userId")
                }
            }
    }
}
