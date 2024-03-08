package com.example.belya.ui.customer_main.tabs.notification

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.belya.Constant
import com.example.belya.databinding.FragmentNotificationCustomerBinding
import com.example.belya.model.StatusOfCustomer
import com.example.belya.model.User
import com.example.belya.utils.StatusOfCustomerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
class NotificationCustomerFragment : Fragment() {
    private lateinit var viewBinding: FragmentNotificationCustomerBinding
    private lateinit var statusOfCustomerAdapter: StatusOfCustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentNotificationCustomerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        fetchAcceptedUsers()
    }

    private fun initViews() {
        viewBinding.recyclerNotification.apply {
            statusOfCustomerAdapter = StatusOfCustomerAdapter()
            adapter = statusOfCustomerAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchAcceptedUsers() {
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
                            val userId = document.id
                            // Fetch the worker ID from the accepted user's document
                            val workerId = document.getString("userID") // Assuming "workerId" is the field containing the worker ID
                            fetchUserDetails(userId)
                            if (workerId != null) {
                                fetchTime(workerId, currentUserId)
                            }
                        }
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchTime(workerId: String, currentUserId: String) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(workerId)
            .collection("Tickets")
            .document(currentUserId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val timestamp = documentSnapshot.getTimestamp("acceptedTime")
                    if (timestamp != null) {
                        // Convert timestamp to LocalTime
                        val acceptedTime = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
                        val formattedTime = acceptedTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                        Log.d("Worker ID" , workerId)
                        Log.d("Customer ID" , currentUserId)
                        statusOfCustomerAdapter.updateTime(currentUserId, formattedTime)
                    } else {
                        Log.d("NotificationCustomer", "Accepted time is null for userID: $workerId")
                    }
                } else {
                    Log.d("NotificationCustomer", "Ticket document not found for userID: $workerId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationCustomer", "Error fetching accepted time", exception)
            }
    }

    private fun fetchUserDetails(userID: String) {
        val usersRef = FirebaseFirestore.getInstance().collection(Constant.USER)

        usersRef.document(userID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.let {
                        val fullName = "${it.firstName} ${it.lastName}"
                        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        val statusOfCustomer = StatusOfCustomer("$fullName, Accepted Your Offer", "", currentTime)
                        statusOfCustomerAdapter.addStatus(statusOfCustomer)
                    }
                } else {
                    Log.d("NotificationCustomer", "User document not found for userID: $userID")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationCustomer", "Error fetching user details", exception)
            }
    }

}
