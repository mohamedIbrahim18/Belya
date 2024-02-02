package com.example.belya.ui.customer_main

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.belya.R
import com.example.belya.databinding.ActivityCustomerMainBinding
import com.example.belya.ui.customer_main.tabs.account.AccountCustomerFragment
import com.example.belya.ui.customer_main.tabs.chat.ChatCustomerFragment
import com.example.belya.ui.customer_main.tabs.home.HomeCustomerFragment
import com.example.belya.ui.customer_main.tabs.notification.NotificationCustomerFragment
import com.google.android.material.navigation.NavigationBarView

class CustomerMainActivity : AppCompatActivity() {
    lateinit var viewBinding : ActivityCustomerMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomerMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.navigationMenu.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    showTabFragment(HomeCustomerFragment())
                }

                R.id.notification -> {
                    showTabFragment(NotificationCustomerFragment())
                }

                R.id.chat -> {
                    showTabFragment(ChatCustomerFragment())
                }

                R.id.my_Account -> {
                    showTabFragment(AccountCustomerFragment())
                }
            }
            true
        }
        viewBinding.navigationMenu.selectedItemId = R.id.home
    }

    private fun showTabFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(R.id.fragment_container,fragment)
        .commit()
    }
}
