package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.technician_details

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentTechnicianDeatilsBinding
import com.example.belya.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class TechnicianDetailsFragment : Fragment() {
    private lateinit var viewBinding: FragmentTechnicianDeatilsBinding
    private lateinit var person: User
    private lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()

    private lateinit var otherUID: String
    private lateinit var currentUserId: String

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
                otherUID = person.userID
                currentUserId = auth.currentUser!!.uid

                setupUI()
                checkMyIdWithOtherId(otherUID, currentUserId)
                checkMyIdWithOtherIdIfAccepted(otherUID, currentUserId)
            }
        }

        viewBinding.bookNowPersonDetails.setOnClickListener {
            viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
            viewBinding.bookNowPersonDetails.visibility = View.GONE

            if (viewBinding.edittextPersonDetails.text?.isNotEmpty() == true) {
                val price = viewBinding.edittextPersonDetails.text.toString()
                makeTicket(currentUserId, otherUID, price)
            } else {
                viewBinding.textinputLayoutPersonDetails.error = "Please Enter a price"
                viewBinding.progressBarPersonDeatails.visibility = View.GONE
                viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            }
        }
    }

    private fun setupUI() {
        val name = person.firstName + " " + person.lastName
        viewBinding.firstnamePersonDetails.text = name
        viewBinding.cityPersonDetails.text = person.city
        viewBinding.ratingbarPersonDetails.rating = person.person_rate.toFloat()
    }

    private fun checkMyIdWithOtherId(techID: String, customerID: String) {
        fetchListFromFirestore(techID, "pendingList", customerID) { isContained ->
            if (isContained) {
                updateUIForPending()
            } else {
                updateUIForNotPending()
            }
        }
    }

    private fun checkMyIdWithOtherIdIfAccepted(techID: String, customerID: String) {
        fetchListFromFirestore(techID, "acceptedList", customerID) { isContained ->
            if (isContained) {
                updateUIForAccepted()
            } else {
                updateUIForNotAccepted()
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

        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(currentUserId).get().addOnSuccessListener { documentSnapshot ->
                val currentUser = documentSnapshot.toObject(User::class.java)
                if (currentUser != null) {
                    currentUser.price = price

                    val otherUserRef = db.collection(Constant.USER).document(technicianId)
                        .collection("Tickets")
                    val batch = db.batch()

                    val customerRequestDoc = otherUserRef.document(currentUser.userID)
                    batch.set(customerRequestDoc, currentUser)
                    val techRef = FirebaseFirestore.getInstance().collection(Constant.USER)
                        .document(technicianId)
                    batch.update(techRef, "pendingList", FieldValue.arrayUnion(currentUserId))

                    batch.commit().addOnSuccessListener {
                        Log.d(TAG, "Ticket created successfully")
                        viewBinding.progressBarPersonDeatails.visibility = View.GONE
                        viewBinding.bookNowPersonDetails.visibility = View.VISIBLE

                        viewBinding.bookNowPersonDetails.setBackgroundColor(Color.RED)
                        viewBinding.bookNowPersonDetails.isClickable = false
                        viewBinding.bookNowPersonDetails.text = "Pending"
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error creating ticket", e)
                        viewBinding.progressBarPersonDeatails.visibility = View.GONE
                        viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
                    }
                } else {
                    Log.e(TAG, "Current user is null")
                    viewBinding.progressBarPersonDeatails.visibility = View.GONE
                    viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Error getting current user", e)
                viewBinding.progressBarPersonDeatails.visibility = View.GONE
                viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            }
    }

    companion object {
        private const val TAG = "TechnicianDetailsFragment"
    }
}
