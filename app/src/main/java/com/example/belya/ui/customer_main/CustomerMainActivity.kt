package com.example.belya.ui.customer_main


import androidx.navigation.fragment.NavHostFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.example.belya.R
import com.example.belya.databinding.ActivityCustomerMainBinding
import com.example.belya.utils.base.Common

class CustomerMainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCustomerMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomerMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val check = Common()
        if (!check.isConnectedToInternet(this)){
            check.showInternetDisconnectedDialog(this)
        }
        initViews()
    }

    private fun initViews() {
        val navHost =supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHost.navController
        viewBinding.navigationMenu.setupWithNavController(navController)
        viewBinding.navigationMenu.setOnItemSelectedListener { item ->
            if (item.itemId != navController.currentDestination?.id) {
                navController.navigate(item.itemId)
            }
            true
        }
    }
}
