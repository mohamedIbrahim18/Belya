package com.example.belya.ui.customer_main.tabs.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.belya.Constant
import com.example.belya.databinding.FragmentNotificationCustomerBinding
import com.example.belya.model.StatusOfCustomer
import com.example.belya.model.User
import com.example.belya.adapter.StatusCustomerAdapter
import com.example.belya.utils.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationCustomerFragment : Fragment() {
    private lateinit var viewBinding: FragmentNotificationCustomerBinding
    private lateinit var statusOfCustomerAdapter: StatusCustomerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentNotificationCustomerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val check = Common()
        if (!check.isConnectedToInternet(requireContext())){
            check.showInternetDisconnectedDialog(requireContext())
        }
        initViews()
        fetchAcceptedUsers()
    }

    private fun initViews() {
        viewBinding.recyclerNotification.apply {
            statusOfCustomerAdapter = StatusCustomerAdapter()
            adapter = statusOfCustomerAdapter
        }
    }

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
                            val workerId =
                                document.getString("userID") // Assuming "workerId" is the field containing the worker ID
                            if (workerId != null) {
                                fetchUserDetails(workerId, currentUserId)
                            } else {
                                Log.e("ERROR IN Log", "Worker ID")
                            }
                        }
                    }
                }
        }
    }

    private fun fetchUserDetails(workerId: String, currentUserId: String) {
        val usersRef = FirebaseFirestore.getInstance().collection(Constant.USER)
        usersRef.document(workerId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.let {
                        val fullName = "${it.firstName} ${it.lastName}"
                        val statusOfCustomer = StatusOfCustomer("$fullName, Accepted Your Offer",null)
                        // get time and update it in adapter
                        usersRef.document(workerId).collection("Tickets").document(currentUserId)
                            .addSnapshotListener { value, error ->
                                if (error != null) {
                                    Log.e("Fetching Tickets Error", "Error fetching tickets", error)
                                    return@addSnapshotListener
                                }
                                val timestamp = value?.getTimestamp("acceptedTime")
                                if (timestamp != null) {
                                    // convert
                                    val date = timestamp.toDate()
                                    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                                    val formattedDate = dateFormat.format(date)

                                    // Add timestamp to statusOfCustomer
                                    statusOfCustomer.timeNow = formattedDate
                                    // Update adapter with statusOfCustomer
                                    statusOfCustomerAdapter.addStatus(statusOfCustomer)
                                }
                            }
                    }
                } else {
                    Log.d(
                        "NotificationCustomer",
                        "User document not found for userID: $currentUserId"
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.e("NotificationCustomer", "Error fetching user details", exception)
            }
    }
}
