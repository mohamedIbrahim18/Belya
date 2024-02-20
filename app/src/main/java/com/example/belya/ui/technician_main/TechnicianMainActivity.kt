package com.example.belya.ui.technician_main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.belya.R
import com.example.belya.databinding.ActivityTechnicianMainBinding

class TechnicianMainActivity : AppCompatActivity() {
    lateinit var viewBinding : ActivityTechnicianMainBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTechnicianMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }
    private fun initViews() {
        navController = findNavController(R.id.fragment_container)
        viewBinding.navigationMenu.setupWithNavController(navController)
/*
        viewBinding.navigationMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeTechnicianFragment2 -> {
                    if (navController.currentDestination?.id != R.id.homeTechnicianFragment2) {
                        navController.navigate(R.id.homeTechnicianFragment2)
                    }
                    true
                }

                R.id.notificationTechnicianFragment2 ->{
                    if (navController.currentDestination?.id!= R.id.notificationTechnicianFragment2){
                        navController.navigate(R.id.notificationTechnicianFragment2)
                    }
                    true
                }

                R.id.chatTechnicianFragment2 -> {
                    if (navController.currentDestination?.id != R.id.chatTechnicianFragment2) {
                        navController.navigate(R.id.chatTechnicianFragment2)
                    }
                    true
                }

                R.id.accountTechnicianFragment2 -> {
                    if (navController.currentDestination?.id != R.id.accountTechnicianFragment2) {
                        navController.navigate(R.id.accountTechnicianFragment2)
                    }
                    true
                }

                else -> false
            }
        }
  */
    }

}