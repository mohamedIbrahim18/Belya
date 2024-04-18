package com.example.belya.ui.registration.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.example.belya.databinding.ActivitySignUpBinding
import com.example.belya.Constant
import com.example.belya.utils.base.LoadingDialog
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
    private lateinit var viewBinding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val loading by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(viewBinding.root)
        initViews()
    }

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
    private fun doRegisterCustomer() {
        val user = getUserFromFields("Customer")
        registerUser(user)
    }

    private fun doRegisterTechnician() {
        val user = getUserFromFields("Technician")
        registerUser(user)
    }

    private fun getUserFromFields(userType: String): User {
        val firstName = viewBinding.firstnameEd.text.toString()
        val lastName = viewBinding.lastnameEd.text.toString()
        val email = viewBinding.emailEd.text.toString()
        return User(
            firstName,
            lastName,
            email,
            "",
            "",
            "",
            "",
            "",
            0.0,
            userType,
            "",
            "",
            emptyList(),
            emptyList()
        )
    }

    private fun registerUser(user: User) {
        val password = viewBinding.passwordEd.text.toString()
        if (!validateForm()) {
            loading.isDismiss()
            setUiEnabled(true)
            return
        }

        setUiEnabled(false)
        auth.createUserWithEmailAndPassword(user.email!!, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: ""
                    user.userID = userId
                    saveUserToDatabase(userId, user)
                    navigateToUserDetails(user.userType)
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "User creation failed"
                    if (errorMessage.contains("email address is already in use", ignoreCase = true)) {
                        showSnackbar("Email address is already in use")
                    } else {
                        showSnackbar(errorMessage)
                    }
                    setUiEnabled(true)
                    loading.isDismiss()
                }
            }
    }


    private fun validateForm(): Boolean {
        var isValid = true
        // Validation logic here
        // Check if fields are not empty, passwords match, etc.
        // Set isValid to false and show appropriate error messages if validation fails
        return isValid
    }

    private fun saveUserToDatabase(userId: String, user: User) {
        db.collection(Constant.USER).document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User added to database")
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error adding user to database", e)
            }
    }

    private fun navigateToUserDetails(userType: String) {
        val intentClass = when (userType) {
            "Customer" -> CustomerInfoActivity::class.java
            "Technician" -> TechnicianInfoActivity::class.java
            else -> LoginActivity::class.java // Default to login activity
        }
        val intent = Intent(this, intentClass)
        startActivity(intent)
        finish()
    }

    private fun setUiEnabled(enabled: Boolean) {
        viewBinding.apply {
            firstName.isEnabled = enabled
            lastnameEd.isEnabled = enabled
            emailEd.isEnabled = enabled
            passwordEd.isEnabled = enabled
            repasswordEd.isEnabled = enabled
            btnCreateAccount.isEnabled = enabled
            loadingProgressBar.isVisible = !enabled
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(viewBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}