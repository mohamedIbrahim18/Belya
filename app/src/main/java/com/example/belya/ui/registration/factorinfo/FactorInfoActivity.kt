package com.example.belya.ui.registration.factorinfo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivityFactorInfoBinding
import com.example.belya.ui.Factor_Main.FactorMainActivity

class FactorInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFactorInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFactorInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.saveChanges.setOnClickListener {
            navigateToFactorPage()
        }
    }

    private fun navigateToFactorPage() {
        val intent = Intent(this,FactorMainActivity::class.java)
        startActivity(intent)
        finish()
    }


}