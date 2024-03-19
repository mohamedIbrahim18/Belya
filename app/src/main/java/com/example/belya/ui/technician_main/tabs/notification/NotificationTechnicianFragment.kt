package com.example.belya.ui.technician_main.tabs.notification

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentNotificationTechnicianBinding
import com.example.belya.model.User
import com.example.belya.adapter.RequestsAdapter
import com.example.belya.utils.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class NotificationTechnicianFragment : Fragment() {
    lateinit var viewBinding: FragmentNotificationTechnicianBinding
    lateinit var requestsAdapter: RequestsAdapter
    lateinit var listRequests: MutableList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentNotificationTechnicianBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val check = Common()
        if (!check.isConnectedToInternet(requireContext())){
            check.showInternetDisconnectedDialog(requireContext())
        }
        listRequests = mutableListOf()
        initRequestsRecycler()
        fetchTickets()
        viewBinding.swiprefresh.setOnRefreshListener {
            requestsAdapter.notifyDataSetChanged()
            viewBinding.swiprefresh.isRefreshing = false
        }
    }

    private fun fetchTickets() {
        val currentUID = FirebaseAuth.getInstance().uid
        FirebaseFirestore.getInstance().collection(Constant.USER).document(currentUID!!)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    Log.e("TEST", "Error fetching user data: ${error.message}", error)
                    return@addSnapshotListener
                }

                documentSnapshot?.let { snapshot ->
                    val pendingList = snapshot.get("pendingList") as? List<String>
                    pendingList?.let { list ->
                        listRequests.clear()
                        list.forEach { userID ->
                            FirebaseFirestore.getInstance().collection(Constant.USER)
                                .document(userID)
                                .get()
                                .addOnSuccessListener { userDocument ->
                                    val user = userDocument.toObject(User::class.java)
                                    user?.let {
                                        // Retrieve price from the Tickets collection
                                        getIdFromTickets(userID, currentUID, user)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("TEST", "Error fetching user details: ${e.message}", e)
                                }
                        }
                    }
                }
            }
    }

    private fun getIdFromTickets(userID: String, currentUID: String, user: User) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(currentUID)
            .collection("Tickets")
            .document(userID)
            .get()
            .addOnSuccessListener { ticketDocument ->
                // Retrieve the price from the ticketDocument
                val price = ticketDocument.getString("price")
                // Update the user object with the price
                user.price = price ?: "0" // Default value if price is null
                // Add the user object to the listRequests
                listRequests.add(user)
                // Notify the adapter of the data change
                requestsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("TEST", "Error fetching ticket details: ${e.message}", e)
            }
    }


    private fun initRequestsRecycler() {
        requestsAdapter = RequestsAdapter(listRequests)
        viewBinding.recyclerViewRequests.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = requestsAdapter

            requestsAdapter.onItemSelectedClickListnner =
                object : RequestsAdapter.OnItemSelectedClick {
                    override fun onItemSelectedClick(position: Int, task: User) {
                        acceptTicket(task)
                    }
                }
            requestsAdapter.onItemRejectedClickListnner = object : RequestsAdapter.OnItemRejectedClick{
                override fun onItemRejectedClick(position: Int, task: User) {
                    rejectTicket(task)
                }

            }
        }
    }

    private fun rejectTicket(task: User) {
        val currentUserId = FirebaseAuth.getInstance().uid
        if (currentUserId != null) {
            val firestore = FirebaseFirestore.getInstance()
            val batch = firestore.batch()
            val techRef = firestore.collection(Constant.USER).document(currentUserId)

            // Remove the ticket from the pendingList
            batch.update(techRef, "pendingList", FieldValue.arrayRemove(task.userID))

            // Delete the ticket document from the Tickets collection
            val ticketRef = techRef.collection("Tickets").document(task.userID)
            batch.delete(ticketRef)

            batch.commit()
                .addOnSuccessListener {
                    Log.d("RejectTicket", "Ticket removed successfully")
                    // Remove the task from the listRequests
                    listRequests.remove(task)
                    // Notify the adapter that the data set has changed
                    requestsAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("RejectTicket", "Error rejecting ticket", e)
                }
        }
    }


    private fun acceptTicket(task: User) {
        Log.e("Fetch taskID", task.userID)
        val currentUserId = FirebaseAuth.getInstance().uid

        if (currentUserId != null) {
            val batch = FirebaseFirestore.getInstance().batch()
            val techRef = FirebaseFirestore.getInstance().collection(Constant.USER)
                .document(currentUserId)
            val currentTimeStamp = FieldValue.serverTimestamp()
            val acceptedOfferData = mapOf(
                "user" to task,
                "acceptedTime" to currentTimeStamp
            )

            // Update the acceptedOffers collection with the new document
            batch.set(techRef.collection("Tickets").document(task.userID), acceptedOfferData)

            // Update the acceptedList and pendingList fields in the user document
            batch.update(techRef, "acceptedList", FieldValue.arrayUnion(task.userID))
            batch.update(techRef, "pendingList", FieldValue.arrayRemove(task.userID))

            batch.commit()
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Accepted successfully")
                    // navigate to chat and start chat
                    findNavController().navigate(R.id.action_notificationTechnicianFragment2_to_chatTechnicianFragment2)
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error accepting request", e)
                }
        }
    }
}
