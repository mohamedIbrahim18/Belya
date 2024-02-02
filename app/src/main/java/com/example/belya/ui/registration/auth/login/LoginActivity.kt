package com.example.belya.ui.registration.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.belya.databinding.ActivityLoginBinding
import com.example.belya.Constent
import com.example.belya.ui.customer_main.CustomerMainActivity
import com.example.belya.ui.technician_main.TechnicianMainActivity
import com.example.belya.ui.registration.auth.forget_password.ForgetPasswordActivity
import com.example.belya.ui.registration.auth.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        viewBinding.btnLogin.setOnClickListener {
            val email = viewBinding.emailEd.text.toString()
            val password = viewBinding.passwordEd.text.toString()
            if(checkValidate()){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Check user type and navigate accordingly
                    checkUserTypeAndNavigate()
                } else {
                    Snackbar.make(viewBinding.root,it.exception?.localizedMessage!!,Snackbar.LENGTH_LONG).show()
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

    private fun checkValidate() : Boolean{
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
        val intent = Intent(this,ForgetPasswordActivity::class.java)
        startActivity(intent) }

    private fun navigateToSignUp() {
        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)
        finish()     }

    /*private fun checkUserTypeAndNavigate() {
        if(Constent.TYPE==1){
            navigateToCustomer()
        } else if (Constent.TYPE==0){
            navigateToTechnician()
        }
    }*/
    private fun checkUserTypeAndNavigate() {
        val sharedPreferences = getSharedPreferences(Constent.USER_PREFERENCES, MODE_PRIVATE)
        val userType = sharedPreferences.getInt(Constent.USER_TYPE, -1)

        when (userType) {
            0 -> navigateToTechnician()
            1 -> navigateToCustomer()
        }
    }

    private fun navigateToCustomer() {
        val intent = Intent(this,CustomerMainActivity::class.java)
        startActivity(intent)
        finish()    }

    private fun navigateToTechnician() {
        val intent = Intent(this,TechnicianMainActivity::class.java)
        startActivity(intent)
        finish()
    }


}