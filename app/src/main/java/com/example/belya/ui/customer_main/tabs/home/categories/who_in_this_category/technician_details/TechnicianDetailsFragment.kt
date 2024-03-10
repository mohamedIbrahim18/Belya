package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.technician_details

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentTechnicianDeatilsBinding
import com.example.belya.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class TechnicianDetailsFragment : Fragment() {
    private lateinit var viewBinding: FragmentTechnicianDeatilsBinding
    private lateinit var person: User
    private lateinit var db: FirebaseFirestore

    private lateinit var technicianId: String
    private var currentUserId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTechnicianDeatilsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        arguments?.let {
            it.getParcelable<User>("pokemon")?.let { task ->
                person = task
                technicianId = person.userID
                currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                setupUI()
                checkMyIdWithOtherId(technicianId, currentUserId)
                checkMyIdWithOtherIdIfAccepted(technicianId, currentUserId)
            }
        }

        viewBinding.bookNowPersonDetails.setOnClickListener {
            viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
            viewBinding.bookNowPersonDetails.visibility = View.GONE

            if (viewBinding.edittextPersonDetails.text?.isNotEmpty() == true) {
                val price = viewBinding.edittextPersonDetails.text.toString()
                Log.d("price in Text View", price)
                Log.d("my current id ", currentUserId!!)
                makeTicket(currentUserId!!,technicianId,price)
            } else {
                viewBinding.textinputLayoutPersonDetails.error = "Please Enter a price"
                viewBinding.progressBarPersonDeatails.visibility = View.GONE
                viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUI() {
        val name = "${person.firstName} ${person.lastName}"
        viewBinding.firstnamePersonDetails.text = name
        viewBinding.cityPersonDetails.text = person.city
        viewBinding.ratingbarPersonDetails.rating = person.person_rate.toFloat()
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
        viewBinding.bookNowPersonDetails.setBackgroundResource(R.color.accept_button_color)

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
