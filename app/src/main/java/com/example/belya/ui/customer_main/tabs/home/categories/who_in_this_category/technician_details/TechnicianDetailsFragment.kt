package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.technician_details

import FeedbackAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import coil.load
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
    private lateinit var feedbackList: MutableList<Feedback> // Change the type to MutableList
    lateinit var feedbackAdapter: FeedbackAdapter

    private lateinit var technicianId: String
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTechnicianDeatilsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        feedbackList = mutableListOf()
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

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
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(technicianId).collection("Reviews").get()
            .addOnSuccessListener { documents ->
                val feedbackList = mutableListOf<Feedback>()
                for (document in documents) {
                    val userName = document.getString("userName") ?: ""
                    val userImageResId =
                        R.drawable.ic_mechanic // You need to replace this with actual image resource ID
                    val message = document.getString("message") ?: ""
                    val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
                    val timestamp = document.getTimestamp("time")
                    val feedback = Feedback(userName, userImageResId, message, rating, timestamp)

                    feedbackList.add(feedback)
                }
                feedbackAdapter.updateData(feedbackList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching reviews: ", exception)
            }
    }

    private fun setupRecyclerFeedbacks() {
        feedbackAdapter = FeedbackAdapter(feedbackList)
        viewBinding.recyclerFeedback.apply {
            viewBinding.recyclerFeedback.addItemDecoration(HorizontalItemDecoration())
            adapter = feedbackAdapter
        }
    }

    private fun setupUI(technicianId: String) {
        val name = "${person.firstName} ${person.lastName}"
        viewBinding.firstnamePersonDetails.text = name
        viewBinding.cityPersonDetails.text = person.city
        viewBinding.imagePersonDetails.load(person.imagePath){
            placeholder(R.drawable.ic_profileimg)
        }

        // Query the "Reviews" collection for the specified technician ID
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(technicianId)
            .collection("Reviews")
            .get()
            .addOnSuccessListener { documents ->
                var totalRating = 0.0
                var count = 0

                // Iterate over the documents to calculate the total rating and count
                for (document in documents) {
                    val rating = document.getDouble("rating")
                    rating?.let {
                        totalRating += it
                        count++
                    }
                }
                // Calculate the average rating
                val averageRating = if (count > 0) totalRating / count else 0.0
                // Set the average rating to the rating bar
                viewBinding.ratingbarPersonDetails.rating = averageRating.toFloat()
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.e(TAG, "Error getting reviews: $exception")
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
        techID: String,
        listName: String,
        customerID: String,
        callback: (Boolean) -> Unit
    ) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(techID)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching $listName: ${error.message}", error)
                    return@addSnapshotListener
                }

                val list = documentSnapshot?.get(listName) as? List<String>
                val isContained = list != null && customerID in list
                callback.invoke(isContained)

                // Call the appropriate UI update method based on the list name
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
        viewBinding.bookNowPersonDetails.text = "Accepted"
        viewBinding.bookNowPersonDetails.isClickable = false
        // TODO
        viewBinding.bookNowPersonDetails.setBackgroundResource(R.color.purple_200)

    }

    private fun updateUIForNotAccepted() {
        // Do nothing, as we're handling it in updateUIForPending and updateUIForNotPending
    }

    private fun makeTicket(currentUserId: String, technicianId: String, price: String) {
        viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
        viewBinding.bookNowPersonDetails.visibility = View.GONE

        val ticketRef = FirebaseFirestore.getInstance()
            .collection(Constant.USER)
            .document(technicianId)
            .collection("Tickets")
            .document(currentUserId)

        // Create a new document in the Tickets collection
        ticketRef.set(hashMapOf("userId" to currentUserId))
            .addOnSuccessListener {
                // Document created successfully, now update its fields
                // Update price in the ticket document
                ticketRef.update("price", price)
                    .addOnSuccessListener {
                        // Handle success
                        Log.d(TAG, "Price updated successfully")
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.e(TAG, "Error updating price: $e")
                    }

                // Update pendingList in the technician document
                FirebaseFirestore.getInstance().collection(Constant.USER)
                    .document(technicianId)
                    .update("pendingList", FieldValue.arrayUnion(currentUserId))
                    .addOnSuccessListener {
                        // Handle success
                        Log.d(TAG, "User added to pending list successfully")
                        uiTicketCreated() // Notify UI after updating pending list
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.e(TAG, "Error adding user to pending list: $e")
                    }
            }
            .addOnFailureListener { e ->
                // Handle failure in creating the document
                Log.e(TAG, "Error creating ticket document: $e")
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
