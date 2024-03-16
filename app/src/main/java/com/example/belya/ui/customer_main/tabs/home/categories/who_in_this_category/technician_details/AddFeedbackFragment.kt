package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.technician_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentAddFeedbackBinding
import com.example.belya.model.Feedback
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddFeedbackFragment : Fragment() {
    private lateinit var viewBinding: FragmentAddFeedbackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentAddFeedbackBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val techId = arguments?.getString("techID") ?: ""
        viewBinding.btnSubmitReview.setOnClickListener {
            createFeedback(techId)
        }
    }

    private fun createFeedback(techId: String) {
        val myCurrentId = FirebaseAuth.getInstance().uid
        myCurrentId?.let { userId ->
            FirebaseFirestore.getInstance().collection(Constant.USER).document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val firstName = documentSnapshot.getString("firstName")
                    val lastName = documentSnapshot.getString("lastName")
                    val image = documentSnapshot.getString("imagePath")
                    val fullName = "$firstName $lastName"
                    val message = viewBinding.edReviewDescription.text.toString()
                    val rating = viewBinding.ratingBar.rating

                    val currentTimeStamp = FieldValue.serverTimestamp()

                    val feedback = Feedback(
                        userName = fullName,
                        imagePath = image?:"", // You need to replace this with actual image resource ID
                        message = message,
                        rating = rating,
                        time = Timestamp.now(), // Store timestamp as a string
                    )
                    FirebaseFirestore.getInstance().collection(Constant.USER)
                        .document(techId).collection("Reviews").document(myCurrentId).set(feedback)
                        .addOnSuccessListener {
                            // Handle success, if needed
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
}
