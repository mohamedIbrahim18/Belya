package com.example.belya.ui.registration.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.belya.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        //initViews()
    }
/*
    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        viewBinding.btnLogin.setOnClickListener {
            val email = viewBinding.emailEd.text.toString()
            val password = viewBinding.passwordEd.text.toString()

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Check user type and navigate accordingly
                    checkUserTypeAndNavigate(email)
                } else {
                    binding.passwordContainer.error = "Wrong email or password"
                }
            }
        }
    }
*/

}