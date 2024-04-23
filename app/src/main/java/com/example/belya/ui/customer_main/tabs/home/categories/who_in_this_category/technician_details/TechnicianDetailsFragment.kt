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
import com.example.belya.api.modeApi.UserApiModelItem
import com.example.belya.databinding.FragmentTechnicianDeatilsBinding
import com.example.belya.model.Feedback
import com.example.belya.model.User
import com.example.belya.utils.base.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class TechnicianDetailsFragment : Fragment() {
    private lateinit var viewBinding: FragmentTechnicianDeatilsBinding
    private lateinit var person: User
    private lateinit var importantPerson: UserApiModelItem
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
        val check = Common()
        if (!check.isConnectedToInternet(requireContext())) {
            check.showInternetDisconnectedDialog(requireContext())
        }
        db = FirebaseFirestore.getInstance()
        feedbackList = mutableListOf()

        handelFirstCase()
        handelSecondCase()
        getTheAveragePrice()

        viewBinding.canselRequestPersonDetails.setOnClickListener {
            cancelRequest(currentUserId!!, technicianId)
        }

        // fun to cancel when i make ticket and i'm pending i can cancel request


        viewBinding.bookNowPersonDetails.setOnClickListener {
            viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
            viewBinding.bookNowPersonDetails.visibility = View.GONE

            val price = viewBinding.edittextPersonDetails.text.toString()
            val description = viewBinding.edittextDescriptionTask.text.toString()

            if (price.isNotEmpty() && price.all { it.isDigit() }) {
                if (description.isNotEmpty()) {
                    Log.d("price in Text View", price)
                    Log.d("my current id ", currentUserId!!)
                    makeTicket(currentUserId!!, technicianId, price, description)
                } else {
                    viewBinding.textinputLayoutDescriptionTask.error = "Please enter a description"
                    viewBinding.progressBarPersonDeatails.visibility = View.GONE
                    viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
                }
            } else {
                viewBinding.textinputLayoutPersonDetails.error = "Please enter a valid price"
                viewBinding.progressBarPersonDeatails.visibility = View.GONE
                viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            }
        }
    }

    private fun cancelRequest(currentUserId: String, technicianId: String) {
        val userRef = db.collection(Constant.USER).document(technicianId)

        // Check if the request is pending before canceling
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val pendingList = documentSnapshot.get("pendingList") as? List<String>
                if (pendingList != null && currentUserId in pendingList) {
                    // Remove the current user from the pending list
                    userRef.update("pendingList", FieldValue.arrayRemove(currentUserId))
                        .addOnSuccessListener {
                            Log.d(TAG, "Request canceled successfully.")
                            // Update the UI to reflect the change
                            updateUIForNotPending()
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error canceling request: ", exception)
                            // Handle the error here
                        }
                } else {
                    Log.d(TAG, "Cannot cancel request. The request is not pending.")
                    // You can optionally show a message to the user indicating that the request cannot be canceled.
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error checking request state: ", exception)
                // Handle the error here
            }
    }


    private fun handelSecondCase() {
        arguments?.let {
            it.getParcelable<UserApiModelItem>("importantMan")?.let { task ->
                importantPerson = task
                technicianId = importantPerson.userID!!
                currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                setSecondUI(technicianId)
                checkMyIdWithOtherId(technicianId, currentUserId)
                checkMyIdWithOtherIdIfAccepted(technicianId, currentUserId)
                setupRecyclerFeedbacks()
                fetchReviewsCollocation(technicianId)
                viewBinding.addFeedback.setOnClickListener {
                    val action =
                        TechnicianDetailsFragmentDirections.actionTechnicianDetailsFragmentToAddFeedbackFragment(
                            technicianId
                        )
                    view?.findNavController()?.navigate(action)
                }
            }
        }
    }

    private fun handelFirstCase() {
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
                    view?.findNavController()?.navigate(action)
                }
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
        viewBinding.workExperiencePersonDetails.text = person.work_experience
        viewBinding.cityPersonDetails.text = person.city
        Glide.with(viewBinding.imagePersonDetails).load(person.imagePath)
            .placeholder(R.drawable.profile_pic).into(viewBinding.imagePersonDetails)


// Retrieve all documents from the "Tickets" collection
        getTheAveragePrice()




        getTheAverageRate()
    }

    private fun getTheAverageRate() {
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
                    saveAverageRatingInDataBase(averageRating)
                } else {
                    Log.e(TAG, "Error getting reviews: ", task.exception)
                }
            }
    }

    private fun getTheAveragePrice() {
        val ticketsCollectionRef = db.collection(Constant.USER)
            .document(technicianId)
            .collection("Tickets")

        ticketsCollectionRef.get()
            .addOnSuccessListener { ticketQuerySnapshot ->
                var totalPrice = 0.0
                var count = 0
                for (ticketDocumentSnapshot in ticketQuerySnapshot.documents) {
                    val userData = ticketDocumentSnapshot.get("user") as? Map<String, Any>
                    val price = userData?.get("price") as? String
                    Log.d("Price ONLY", "price Price: $price")
                    val priceDouble = price?.toDouble()
                    if (price != null) {
                        if (priceDouble != null) {
                            totalPrice += priceDouble
                            count++
                            Log.e("Price total", totalPrice.toString())
                        }
                    } else {
                        Log.e("Price total", "Price is null or cannot be parsed")
                    }
                }
                // Calculate average price
                val averagePrice = if (count > 0) totalPrice / count else 0.0
                Log.d("Price Debug", "Average Price: $averagePrice")
                if (averagePrice == 0.0) {
                    "The technician's average price: 50"
                    updateAveragePriceInuserDocument("50")
                }
                viewBinding.averagePricePersonDetails.text =
                    "The technician's average price: $averagePrice"
                updateAveragePriceInuserDocument(averagePrice.toString())
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error getting documents: ", exception)
                // Handle the error here
            }
    }

    private fun updateAveragePriceInuserDocument(totalprice: String) {
        val userRef = db.collection(Constant.USER).document(technicianId)
        userRef.update("price", totalprice)
            .addOnSuccessListener {
                Log.d("Firestore", "Average price updated successfully.")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error updating average price: ", exception)
                // Handle the error here
            }
    }

    private fun saveAverageRatingInDataBase(averageRating: Double) {
        db.collection(Constant.USER).document(technicianId).update("person_rate", averageRating)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

    private fun setSecondUI(technicianId: String) {
        val name = "${importantPerson.firstName} ${importantPerson.lastName}"
        viewBinding.firstnamePersonDetails.text = name
        viewBinding.workExperiencePersonDetails.text = importantPerson.workExperience
        viewBinding.cityPersonDetails.text = importantPerson.city
        Glide.with(viewBinding.imagePersonDetails).load(importantPerson.imagePath)
            .placeholder(R.drawable.profile_pic).into(viewBinding.imagePersonDetails)
        val ticketsCollectionRef = db.collection(Constant.USER)
            .document(technicianId)
            .collection("Tickets")

// Retrieve all documents from the "Tickets" collection
        ticketsCollectionRef.get()
            .addOnSuccessListener { ticketQuerySnapshot ->
                var totalPrice = 0.0
                var count = 0
                for (ticketDocumentSnapshot in ticketQuerySnapshot.documents) {
                    val userData = ticketDocumentSnapshot.get("user") as? Map<String, Any>
                    val price = userData?.get("price") as? String
                    Log.d("Price ONLY", "price Price: $price")
                    val priceDouble = price?.toDouble()
                    if (price != null) {
                        if (priceDouble != null) {
                            totalPrice += priceDouble
                            count++
                            Log.e("Price total", totalPrice.toString())
                        }
                    } else {
                        Log.e("Price total", "Price is null or cannot be parsed")
                    }
                }
                // Calculate average price
                val averagePrice = if (count > 0) totalPrice / count else 0.0
                Log.d("Price Debug", "Average Price: $averagePrice")

                if (averagePrice == 0.0) {
                    "The technician's average price: 50"
                    updateAveragePriceInuserDocument("50")
                }
                viewBinding.averagePricePersonDetails.text =
                    "The technician's average price: $averagePrice"
                updateAveragePriceInuserDocument(averagePrice.toString())
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
                    saveAverageRatingInDataBase(averageRating)

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
        viewBinding.canselRequestPersonDetails.isClickable = true
        viewBinding.bookNowPersonDetails.setBackgroundColor(Color.RED)
    }

    private fun updateUIForNotPending() {
        viewBinding.bookNowPersonDetails.text = "Book Now"
        viewBinding.bookNowPersonDetails.isClickable = true
        viewBinding.canselRequestPersonDetails.isClickable = false
        viewBinding.bookNowPersonDetails.setBackgroundColor(Color.BLUE)
    }

    private fun updateUIForAccepted() {

        val customColor = Color.parseColor("#3AB449")
        viewBinding.bookNowPersonDetails.text = "Accepted"
        viewBinding.bookNowPersonDetails.isClickable = false
        viewBinding.canselRequestPersonDetails.isClickable = false
        viewBinding.bookNowPersonDetails.setBackgroundColor(customColor)
    }

    private fun updateUIForNotAccepted() {

    }

    private fun makeTicket(
        currentUserId: String,
        technicianId: String,
        price: String,
        description: String
    ) {
        viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
        viewBinding.bookNowPersonDetails.visibility = View.GONE

        val ticketRef = db.collection(Constant.USER).document(technicianId).collection("Tickets")
            .document(currentUserId)

        ticketRef.set(hashMapOf("userId" to currentUserId)).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ticketRef.update("price", price, "description", description)
                    .addOnCompleteListener { priceUpdateTask ->
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
