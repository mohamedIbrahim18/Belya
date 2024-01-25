package com.example.belya.ui.splashscreen

import com.example.belya.ui.registration.auth.signup.SignUpActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivitySplashScreenBinding

class SplashScreenActivity: AppCompatActivity() {
    private lateinit var viewBinding : ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding= ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            gotoSignUp()
        },1000)
    }

    private fun gotoSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}