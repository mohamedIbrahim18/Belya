package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.technician_details

import FeedbackAdapter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentTechnicianDeatilsBinding
import com.example.belya.model.Feedback
import com.example.belya.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class TechnicianDetailsFragment : Fragment() {
    private lateinit var viewBinding: FragmentTechnicianDeatilsBinding
    private lateinit var person: User
    private lateinit var db: FirebaseFirestore
    private lateinit var feedbackList: MutableList<Feedback>
    lateinit var feedbackAdapter: FeedbackAdapter

    private lateinit var technicianId: String
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTechnicianDeatilsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        feedbackList = mutableListOf()

        arguments?.let {
            it.getParcelable<User>("pokemon")?.let { task ->
                person = task
                technicianId = person.userID
                currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                setupUI(technicianId)
                checkMyIdWithOtherId(technicianId, currentUserId)
                checkMyIdWithOtherIdIfAccepted(technicianId, currentUserId)
                setupRecyclerFeedbacks()
                fetchReviewsCollocation(technicianId)
                viewBinding.addFeedback.setOnClickListener {
                    val action =
                        TechnicianDetailsFragmentDirections.actionTechnicianDetailsFragmentToAddFeedbackFragment(
                            technicianId
                        )
                    view.findNavController().navigate(action)
                }
            }
        }

        viewBinding.bookNowPersonDetails.setOnClickListener {
            viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
            viewBinding.bookNowPersonDetails.visibility = View.GONE

            if (viewBinding.edittextPersonDetails.text?.isNotEmpty() == true) {
                val price = viewBinding.edittextPersonDetails.text.toString()
                Log.d("price in Text View", price)
                Log.d("my current id ", currentUserId!!)
                makeTicket(currentUserId!!, technicianId, price)
            } else {
                viewBinding.textinputLayoutPersonDetails.error = "Please Enter a price"
                viewBinding.progressBarPersonDeatails.visibility = View.GONE
                viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchReviewsCollocation(technicianId: String) {
        db.collection(Constant.USER).document(technicianId).collection("Reviews").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val feedbackList = mutableListOf<Feedback>()
                    for (document in task.result!!) {
                        val userName = document.getString("userName") ?: ""
                        val userImagePath = document.getString("imagePath")
                            ?: "" // Replace "imagePath" with your actual field name
                        val message = document.getString("message") ?: ""
                        val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                        val timestamp = document.getTimestamp("time")
                        val feedback = Feedback(userName, userImagePath, message, rating, timestamp)
                        feedbackList.add(feedback)
                    }
                    feedbackAdapter.updateData(feedbackList)
                } else {
                    Log.e(TAG, "Error fetching reviews: ", task.exception)
                }
            }
    }

    private fun setupRecyclerFeedbacks() {
        feedbackAdapter = FeedbackAdapter(feedbackList)
        viewBinding.recyclerFeedback.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = feedbackAdapter
        }
    }

    private fun setupUI(technicianId: String) {
        val name = "${person.firstName} ${person.lastName}"
        viewBinding.firstnamePersonDetails.text = name
        viewBinding.cityPersonDetails.text = person.city
        Glide.with(viewBinding.imagePersonDetails).load(person.imagePath)
            .placeholder(R.drawable.ic_profileimg).into(viewBinding.imagePersonDetails)
        db.collection(Constant.USER)
            .document(technicianId)
            .collection("Tickets")
            .get()
            .addOnSuccessListener { ticketQuerySnapshot ->
                var totalprice = 0.0
                var count = 0

                for (ticketDocumentSnapshot in ticketQuerySnapshot.documents) {
                    val ticketUser = ticketDocumentSnapshot.get("user") as? Map<*, *>
                    ticketUser?.let { ticketUserMap ->
                        val priceString = ticketUserMap["price"] as? String
                        priceString?.let {
                            val price = it.toDoubleOrNull()
                            price?.let {
                                totalprice += it
                                count++
                            }
                        }
                    }
                }

                val averagePrice = if (count > 0) totalprice / count else 0.0
                Log.d("Price Debug", "Average Price: $averagePrice")
                viewBinding.averagePricePersonDetails.text =
                    "Average Price Between $averagePrice to ${averagePrice + 30} per hour"
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error getting documents: ", exception)
                // Handle the error here
            }


        db.collection(Constant.USER).document(technicianId).collection("Reviews").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var totalRating = 0.0
                    var count = 0

                    for (document in task.result!!) {
                        val rating = document.getDouble("rating")
                        rating?.let {
                            totalRating += it
                            count++
                        }
                    }
                    val averageRating = if (count > 0) totalRating / count else 0.0
                    viewBinding.ratingbarPersonDetails.rating = averageRating.toFloat()
                } else {
                    Log.e(TAG, "Error getting reviews: ", task.exception)
                }
            }
    }

    private fun checkMyIdWithOtherId(techID: String, customerID: String?) {
        customerID?.let {
            fetchListFromFirestore(techID, "pendingList", it) { isContained ->
                if (isContained) {
                    updateUIForPending()
                } else {
                    updateUIForNotPending()
                }
            }
        }
    }

    private fun checkMyIdWithOtherIdIfAccepted(techID: String, customerID: String?) {
        customerID?.let {
            fetchListFromFirestore(techID, "acceptedList", it) { isContained ->
                if (isContained) {
                    updateUIForAccepted()
                } else {
                    updateUIForNotAccepted()
                }
            }
        }
    }

    private fun fetchListFromFirestore(
        techID: String, listName: String, customerID: String, callback: (Boolean) -> Unit
    ) {
        db.collection(Constant.USER).document(techID)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching $listName: ${error.message}", error)
                    return@addSnapshotListener
                }

                val list = documentSnapshot?.get(listName) as? List<String>
                val isContained = list != null && customerID in list
                callback.invoke(isContained)

                when (listName) {
                    "pendingList" -> {
                        if (isContained) {
                            updateUIForPending()
                        } else {
                            updateUIForNotPending()
                        }
                    }

                    "acceptedList" -> {
                        if (isContained) {
                            updateUIForAccepted()
                        } else {
                            updateUIForNotAccepted()
                        }
                    }
                }
            }
    }

    private fun updateUIForPending() {
        viewBinding.bookNowPersonDetails.text = "Pending"
        viewBinding.bookNowPersonDetails.isClickable = false
        viewBinding.bookNowPersonDetails.setBackgroundColor(Color.RED)
    }

    private fun updateUIForNotPending() {
        viewBinding.bookNowPersonDetails.text = "Book Now"
        viewBinding.bookNowPersonDetails.isClickable = true
        viewBinding.bookNowPersonDetails.setBackgroundColor(Color.BLUE)
    }

    private fun updateUIForAccepted() {

        val customColor = Color.parseColor("#3AB449")
        viewBinding.bookNowPersonDetails.text = "Accepted"
        viewBinding.bookNowPersonDetails.isClickable = false
        viewBinding.bookNowPersonDetails.setBackgroundColor(customColor)
    }

    private fun updateUIForNotAccepted() {

    }

    private fun makeTicket(currentUserId: String, technicianId: String, price: String) {
        viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
        viewBinding.bookNowPersonDetails.visibility = View.GONE

        val ticketRef = db.collection(Constant.USER).document(technicianId).collection("Tickets")
            .document(currentUserId)

        ticketRef.set(hashMapOf("userId" to currentUserId)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ticketRef.update("price", price).addOnCompleteListener { priceUpdateTask ->
                    if (priceUpdateTask.isSuccessful) {
                        db.collection(Constant.USER).document(technicianId)
                            .update("pendingList", FieldValue.arrayUnion(currentUserId))
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    uiTicketCreated()
                                } else {
                                    Log.e(
                                        TAG,
                                        "Error adding user to pending list: ",
                                        updateTask.exception
                                    )
                                }
                            }
                    } else {
                        Log.e(TAG, "Error updating price: ", priceUpdateTask.exception)
                    }
                }
            } else {
                Log.e(TAG, "Error creating ticket document: ", task.exception)
            }
        }
    }

    private fun uiTicketCreated() {
        viewBinding.progressBarPersonDeatails.visibility = View.GONE
        viewBinding.bookNowPersonDetails.visibility = View.VISIBLE

        viewBinding.bookNowPersonDetails.setBackgroundColor(Color.RED)
        viewBinding.bookNowPersonDetails.isClickable = false
        viewBinding.bookNowPersonDetails.text = "Pending"
    }

    companion object {
        private const val TAG = "TechnicianDetailsFragment"
    }
}
