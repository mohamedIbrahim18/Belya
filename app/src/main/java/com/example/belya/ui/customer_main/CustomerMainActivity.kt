package com.example.belya.ui.customer_main

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.belya.R
import com.example.belya.databinding.ActivityCustomerMainBinding
import com.example.belya.ui.customer_main.tabs.account.AccountCustomerFragment
import com.example.belya.ui.customer_main.tabs.chat.ChatCustomerFragment
import com.example.belya.ui.customer_main.tabs.home.HomeCustomerFragment
import com.example.belya.ui.customer_main.tabs.notification.NotificationCustomerFragment
import com.google.android.material.navigation.NavigationBarView

class CustomerMainActivity : AppCompatActivity() {
    lateinit var viewBinding : ActivityCustomerMainBinding
    private lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomerMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        navController = findNavController(R.id.fragment_container)
        viewBinding.navigationMenu.setupWithNavController(navController)

        viewBinding.navigationMenu.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.homeCustomerFragment -> {
                        if (navController.currentDestination?.id != R.id.homeCustomerFragment) {
                            navController.navigate(R.id.homeCustomerFragment)
                        }
                        true
                    }

                    R.id.notificationCustomerFragment ->{
                        if (navController.currentDestination?.id!=R.id.notificationCustomerFragment){
                            navController.navigate(R.id.notificationCustomerFragment)
                        }
                        true
                    }

                    R.id.chatCustomerFragment -> {
                        if (navController.currentDestination?.id != R.id.chatCustomerFragment) {
                            navController.navigate(R.id.chatCustomerFragment)
                        }
                        true
                    }

                    R.id.accountCustomerFragment -> {
                        if (navController.currentDestination?.id != R.id.accountCustomerFragment) {
                            navController.navigate(R.id.accountCustomerFragment)
                        }
                        true
                    }

                    else -> false
                }
            }
        }
//        viewBinding.navigationMenu.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home -> {
//                    showTabFragment(HomeCustomerFragment())
//                }
//
//                R.id.notification -> {
//                    showTabFragment(NotificationCustomerFragment())
//                }
//
//                R.id.chat -> {
//                    showTabFragment(ChatCustomerFragment())
//                }
//
//                R.id.my_Account -> {
//                    showTabFragment(AccountCustomerFragment())
//                }
//            }
//            true
//        }
//        viewBinding.navigationMenu.selectedItemId = R.id.home
    }
/*
    private fun showTabFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment)
        .commit()
    }
 */
