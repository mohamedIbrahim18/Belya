package com.example.belya.ui.registration.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.belya.databinding.ActivitySignUpBinding
import com.example.belya.Constant
import com.example.belya.base.LoadingDialog
import com.example.belya.model.User
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.example.belya.ui.registration.technicianinfo.TechnicianInfoActivity
import com.example.belya.ui.registration.customerinfo.CustomerInfoActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var viewBinding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }
val loading = LoadingDialog(this)
    private fun initViews() {
        auth = Firebase.auth
        viewBinding.loadingProgressBar.isVisible = false
        viewBinding.btnCreateAccount.setOnClickListener {
            loading.startLoading()
            chooseWhoUseThisApp()
        }
        viewBinding.haveAcoountLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }


    private fun chooseWhoUseThisApp() {
        //get selected radio button from radiogroup
        val buttonId: Int = viewBinding.typeContainer.checkedRadioButtonId
        if (buttonId == View.NO_ID) {
            Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show()
        } else {
            //TODO make validate
            val selectedRadioButton = findViewById<RadioButton>(buttonId)
            if (selectedRadioButton.text.equals("Customer")) {
                // navigateToCustomerPage()
                doRegisterCustomer()
            } else if (selectedRadioButton.text.equals("Technician")) {
                // navigateToFactorDetails()
                doRegisterTechnician()
            }
        }
    }


    private fun validForm(): Boolean {
        var isValid = true
        if (viewBinding.firstName.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutFirstname.error = "Please enter first name"
            isValid = false
        } else {
            viewBinding.inputlayoutFirstname.error = null
        }

        if (viewBinding.lastname.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutLastname.error = "Please enter last name"
            isValid = false
        } else {
            viewBinding.inputlayoutLastname.error = null
        }

        if (viewBinding.email.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutEmail.error = "Please enter your email"
            isValid = false
        } else {
            viewBinding.inputlayoutEmail.error = null

        }

        if (viewBinding.password.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutPassword.error = "Please enter a strong password"
            isValid = false
        } else {
            viewBinding.inputlayoutPassword.error = null
        }

        if (viewBinding.passwordEd.text.toString() != viewBinding.repasswordEd.text.toString()) {
            // show error
            viewBinding.inputlayoutRepassword.error = "Password doesn't match"
            isValid = false
        } else {
            viewBinding.inputlayoutRepassword.error = null
        }

        return isValid
    }

    fun doRegisterTechnician() {
        auth = FirebaseAuth.getInstance()
        viewBinding.apply {
            val userTechnician = User(
                firstnameEd.text.toString(),
                lastnameEd.text.toString(),
                emailEd.text.toString(),
                "",
                "",
                "",
                "",
                "",
                0.0,
                "Technician",
                "",
                "",emptyList(),
                emptyList(),
            )
            var passwordEd = viewBinding.passwordEd.text.toString()
            var rePasswordEd = viewBinding.repasswordEd.text.toString()
            if (passwordEd.isBlank()) {
                // show error
                viewBinding.inputlayoutPassword.error = "Please enter a strong password"
            } else {
                viewBinding.inputlayoutPassword.error = null
            }

            if (passwordEd != rePasswordEd) {
                // show error
                viewBinding.inputlayoutRepassword.error = "Password doesn't match"
            } else {
                viewBinding.inputlayoutRepassword.error = null
            }
            registerTechnician(userTechnician, passwordEd)

        }
    }

    private fun registerTechnician(userTechnician: User, password: String) {
        viewBinding.loadingProgressBar.isVisible = true

        if (!validForm()) {
            setUiEnabled(true)
        } else {
            setUiEnabled(false)
            auth.createUserWithEmailAndPassword(userTechnician.email!!, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User creation successful
                        val userId = task.result?.user?.uid ?: ""
                        userTechnician.userID = userId
                        task.result.user.let {
                            saveUserTechnicianDataBase(it?.uid!!, userTechnician)
                            navigateToTechnicianDetails()
                        }
                    } else {
                        // User creation failed
                        Snackbar.make(
                            viewBinding.root,
                            task.exception?.localizedMessage!!,
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                    setUiEnabled(true)
                }


        }
    }

    private fun saveUserTechnicianDataBase(uId: String, userTechnician: User) {
        db.collection(Constant.USER).document(uId)
            .set(userTechnician)
            .addOnSuccessListener { documentReference ->
                // Done
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    private fun saveUserCustomerDataBase(uId: String, userCustomer: User) {
        db.collection(Constant.USER).document(uId)
            .set(userCustomer)
            .addOnSuccessListener { documentReference ->
                // Done
                Log.e("TAG", "Good $uId")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    private fun registerCustomer(userCustomer: User, password: String) {
        viewBinding.loadingProgressBar.isVisible = true

        if (!validForm()) {
            setUiEnabled(true)
        } else {
            setUiEnabled(false)
            auth.createUserWithEmailAndPassword(userCustomer.email!!, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User creation successful
                        val userId = task.result?.user?.uid ?: ""
                        userCustomer.userID = userId
                        task.result.user.let {
                            saveUserCustomerDataBase(it?.uid!!, userCustomer)
                            navigateToCustomerDetails()
                        }
                    } else {
                        // User creation failed
                        Snackbar.make(
                            viewBinding.root,
                            task.exception?.localizedMessage!!,
                            Snackbar.LENGTH_LONG
                        ).show()

                    }
                    setUiEnabled(true)
                }


        }
    }

    fun doRegisterCustomer() {
        auth = FirebaseAuth.getInstance()
        viewBinding.apply {
            val userCustomer = User(
                firstnameEd.text.toString(),
                lastnameEd.text.toString(),
                emailEd.text.toString(),
                "",
                "",
                "",
                "",
                "",
                0.0,
                "Customer",
                "",
                "",
                emptyList(),
                emptyList(),
            )
            val passwordEd = viewBinding.passwordEd.text.toString()
            val rePasswordEd = viewBinding.repasswordEd.text.toString()
            if (passwordEd.isBlank()) {
                // show error
                viewBinding.inputlayoutPassword.error = "Please enter a strong password"
            } else {
                viewBinding.inputlayoutPassword.error = null
            }

            if (passwordEd != rePasswordEd) {
                // show error
                viewBinding.inputlayoutRepassword.error = "Password doesn't match"
            } else {
                viewBinding.inputlayoutRepassword.error = null
            }
            registerCustomer(userCustomer, passwordEd)

        }
    }

    private fun setUiEnabled(enabled: Boolean) {
        viewBinding.firstName.isEnabled = enabled
        viewBinding.lastnameEd.isEnabled = enabled
        viewBinding.emailEd.isEnabled = enabled
        viewBinding.passwordEd.isEnabled = enabled
        viewBinding.repasswordEd.isEnabled = enabled
        viewBinding.btnCreateAccount.isEnabled = enabled
        viewBinding.loadingProgressBar.isVisible = !enabled
    }


    private fun navigateToTechnicianDetails() {
        val intent = Intent(this, TechnicianInfoActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCustomerDetails() {
        val intent = Intent(this, CustomerInfoActivity::class.java)
        startActivity(intent)
        finish()
    }

}