package com.example.belya.ui.technician_main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivityTechnicianMainBinding

class TechnicianMainActivity : AppCompatActivity() {
    lateinit var viewBinding : ActivityTechnicianMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTechnicianMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}