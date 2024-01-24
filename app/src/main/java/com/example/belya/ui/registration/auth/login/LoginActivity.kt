package com.example.belya.ui.registration.auth.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.belya.R
import com.example.belya.databinding.ActivityLoginBinding
import com.example.belya.ui.registration.whouse.WhoUseThisActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.btnLogin.setOnClickListener {
            goToWhoUseThis()
        }
    }

    private fun goToWhoUseThis() {
        val intent = Intent(this, WhoUseThisActivity::class.java)
        startActivity(intent)
    }
}