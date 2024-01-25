package com.example.belya.ui.registration.auth.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.belya.databinding.ActivitySignUpBinding
import com.example.belya.ui.registration.whouse.WhoUseThisActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        auth = Firebase.auth
        viewBinding.loadingProgressBar.isVisible = false
        viewBinding.btnCreateAccount.setOnClickListener {
            register()
        }
    }

    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var passwordEd: String? = null
    var repasswordEd: String? = null

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

    private fun register() {
        viewBinding.loadingProgressBar.isVisible = true

        firstName = viewBinding.firstnameEd.text.toString()
        lastName = viewBinding.lastnameEd.text.toString()
        email = viewBinding.emailEd.text.toString()
        passwordEd = viewBinding.passwordEd.text.toString()
        repasswordEd = viewBinding.repasswordEd.text.toString()

        if (!validForm()) {
            setUiEnabled(true)
        } else {
            setUiEnabled(false)
            auth.createUserWithEmailAndPassword(email!!, passwordEd!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User creation successful
                        // Toast doesn't show
                        //Toast.makeText(applicationContext, "User signed up successfully", Toast.LENGTH_LONG).show()
                        Snackbar.make(viewBinding.root, "User signed up successfully: ${task.result.user?.uid!!}", Snackbar.LENGTH_LONG).show()
                        navigateToWhoUseThisApp()
                    } else {
                        // User creation failed
                        Snackbar.make(viewBinding.root, "Error: ${task.exception?.message}", Snackbar.LENGTH_LONG).show()
                    }
                    setUiEnabled(true)
                }
        }
    }

    private fun navigateToWhoUseThisApp() {
        val intent = Intent(this,WhoUseThisActivity::class.java)
        startActivity(intent)
        finish()
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
}