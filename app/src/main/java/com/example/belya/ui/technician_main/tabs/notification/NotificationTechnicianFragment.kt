package com.example.belya.ui.technician_main.tabs.notification

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentNotificationTechnicianBinding
import com.example.belya.model.User
import com.example.belya.utils.RequestsAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationTechnicianFragment : Fragment() {
    lateinit var viewBinding: FragmentNotificationTechnicianBinding
    lateinit var requestsAdapter: RequestsAdapter
    lateinit var listRequests: MutableList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentNotificationTechnicianBinding.inflate(layoutInflater)
        return viewBinding.root
        // initViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listRequests = mutableListOf() // Initialize listRequests here
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
            .collection("Tickets").addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("Error", "${error.localizedMessage}")
                    return@addSnapshotListener
                }
                value?.let { snapshot ->
                    val userDetails = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(User::class.java)
                    }
                    listRequests.clear()
                    listRequests.addAll(userDetails)
                    requestsAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun initRequestsRecycler() {
        requestsAdapter = RequestsAdapter(listRequests)
        viewBinding.recyclerViewRequests.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = requestsAdapter
            // when user reject the offer
            requestsAdapter.onItemRejectedClickListnner =
                object : RequestsAdapter.OnItemRejectedClick {
                    override fun onItemRejectedClick(position: Int, task: User) {
                        // Remove it from Firestore
                        val currentUID = FirebaseAuth.getInstance().uid
                        val doc = task.userID
                        if (currentUID != null && doc.isNotEmpty()) {
                            FirebaseFirestore.getInstance().collection(Constant.USER)
                                .document(currentUID)
                                .collection("Tickets")
                                .document(doc).delete()
                                .addOnSuccessListener {
                                    // Remove it from the local list
                                    listRequests.remove(task)
                                    requestsAdapter.notifyDataSetChanged()
                                }
                                .addOnFailureListener { e ->
                                    Snackbar.make(
                                        viewBinding.root,
                                        "Error: ${e.message}",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                }
            // when user accepted the offer
            requestsAdapter.onItemSelectedClickListnner =
                object : RequestsAdapter.OnItemSelectedClick {
                    override fun onItemSelectedClick(position: Int, task: User) {
                        Snackbar.make(viewBinding.root, "Accepted", Snackbar.LENGTH_LONG).show()
                        // Save accepted offer to Firestore
                        val currentUID = FirebaseAuth.getInstance().uid
                        val doc = task.userID
                        if (currentUID != null && doc.isNotEmpty()) {
                            // Save the accepted offer to Firestore
                            FirebaseFirestore.getInstance().collection("chats").document(currentUID)
                                .collection("acceptedOffers").document(doc).set(task)
                                .addOnSuccessListener {
                                    // After successfully saving to Firestore, navigate to the chat fragment
                                    val bundle = Bundle()
                                    bundle.putParcelable("passData", task)
                                    findNavController().navigate(
                                        R.id.action_notificationTechnicianFragment2_to_chatTechnicianFragment2,
                                        bundle
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Snackbar.make(
                                        viewBinding.root,
                                        "Error: ${e.message}",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }
                }
        }
    }
}
