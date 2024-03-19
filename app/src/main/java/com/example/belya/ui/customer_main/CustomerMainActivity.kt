package com.example.belya.ui.customer_main


import androidx.navigation.fragment.NavHostFragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.ActivityCustomerMainBinding
import com.example.belya.utils.Common
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerMainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCustomerMainBinding
    private lateinit var navController: NavController
    lateinit var badgeDrawable : BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomerMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val check = Common()
        if (!check.isConnectedToInternet(this)){
            check.showInternetDisconnectedDialog(this)
        }
        initViews()
      //  fetchAcceptedUsers()
    }

    private fun initViews() {
        val navHost =supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHost.navController
        viewBinding.navigationMenu.setupWithNavController(navController)

//
//        badgeDrawable =
//            viewBinding.navigationMenu.getOrCreateBadge(R.id.notificationCustomerFragment)
//        badgeDrawable.setVisible(true)

        viewBinding.navigationMenu.setOnItemSelectedListener { item ->
            if (item.itemId != navController.currentDestination?.id) {
                navController.navigate(item.itemId)
            }
            true
        }


    }
    /*private fun fetchAcceptedUsers() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            FirebaseFirestore.getInstance().collection(Constant.USER)
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        Log.e("NotificationCustomer", "Error fetching users", exception)
                        return@addSnapshotListener
                    }

                    querySnapshot?.documents?.forEach { document ->
                        val acceptedList = document.get("acceptedList") as? List<String>
                        if (acceptedList != null && currentUserId in acceptedList) {
                            // Fetch the worker ID from the accepted user's document
                            val workerId =
                                document.getString("userID") // Assuming "workerId" is the field containing the worker ID
                            if (workerId != null) {
                                val count = acceptedList?.size ?: 0
                                badgeDrawable.number = count
                            } else {
                                Log.e("ERROR IN Log", "Worker ID")
                            }
                        }
                    }
                }
        }
    }
*/
}
