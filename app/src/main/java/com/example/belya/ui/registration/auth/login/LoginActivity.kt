package com.example.belya.ui.registration.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.belya.databinding.ActivityLoginBinding
import com.example.belya.Constant
import com.example.belya.ui.customer_main.CustomerMainActivity
import com.example.belya.ui.technician_main.TechnicianMainActivity
import com.example.belya.ui.registration.auth.forget_password.ForgetPasswordActivity
import com.example.belya.ui.registration.auth.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        viewBinding.progressBar.visibility = View.GONE
        viewBinding.btnLogin.setOnClickListener {
            viewBinding.progressBar.visibility = View.VISIBLE
            viewBinding.btnLogin.visibility = View.GONE
            val email = viewBinding.emailEd.text.toString()
            val password = viewBinding.passwordEd.text.toString()
            if(checkValidate()){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Check user type and navigate accordingly
                    checkUserTypeAndNavigate()
                    viewBinding.progressBar.visibility = View.GONE
                    viewBinding.btnLogin.visibility = View.VISIBLE


                } else {
                    Snackbar.make(viewBinding.root,it.exception?.localizedMessage!!,Snackbar.LENGTH_LONG).show()
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
                                    Snackbar.LENGTH_LONG).show()
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
                        Snackbar.LENGTH_LONG).show()
                }
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