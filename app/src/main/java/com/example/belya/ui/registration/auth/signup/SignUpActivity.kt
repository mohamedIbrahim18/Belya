package com.example.belya.ui.registration.auth.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.belya.R
import com.example.belya.databinding.ActivitySignUpBinding
import com.example.belya.databinding.ActivitySplashScreenBinding
import com.example.belya.ui.registration.auth.login.LoginActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.btnCreateAccount.setOnClickListener {
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}