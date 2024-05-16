package com.example.belya.ui.technician_main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.ActivityTechnicianMainBinding
import com.example.belya.model.User
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class TechnicianMainActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityTechnicianMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTechnicianMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHost.navController
        viewBinding.navigationMenu.setupWithNavController(navController)
        val badgeDrawable: BadgeDrawable =
            viewBinding.navigationMenu.getOrCreateBadge(R.id.notificationTechnicianFragment2)
        badgeDrawable.setVisible(true)
        val currentId = FirebaseAuth.getInstance().uid
        FirebaseFirestore.getInstance().collection(Constant.USER).document(currentId!!).addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("TEST", "Error fetching Pending List: ${error.message}", error)
                return@addSnapshotListener
            }
            val pendingList = value?.get("pendingList") as? List<*>
            val count = pendingList?.size ?: 0
            badgeDrawable.number = count
        }




        viewBinding.navigationMenu.setOnItemSelectedListener { item ->
            if (item.itemId != navController.currentDestination?.id) {
                navController.navigate(item.itemId)
            }
            true
        }


    }

}