package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.technician_details

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.Constant
import com.example.belya.databinding.FragmentTechnicianDeatilsBinding
import com.example.belya.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TechnicianDetailsFragment : Fragment() {
    lateinit var viewBinding: FragmentTechnicianDeatilsBinding
    private lateinit var person: User
    private lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()

   private lateinit var otherUID : String
   private lateinit var currentUserId :String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentTechnicianDeatilsBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        // initViews()
        arguments?.let {
            it.getParcelable<User>("pokemon")?.let { task ->
                person = task
               otherUID = person.userID
                currentUserId = auth.currentUser!!.uid
                // viewBinding.imagePersonDetails.setImageResource(task.imagePath!!)
                val name = person.firstName + " " + person.lastName
                viewBinding.firstnamePersonDetails.text = name
                val city = person.city
                viewBinding.cityPersonDetails.text = city
                val rating = person.person_rate
                viewBinding.ratingbarPersonDetails.rating = rating.toFloat()

                // average price

            }
        }
        viewBinding.bookNowPersonDetails.setOnClickListener {
            viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
            viewBinding.bookNowPersonDetails.visibility = View.GONE
            // Check if the price is not empty
            if (viewBinding.edittextPersonDetails.text?.isNotEmpty() == true) {
                val price = viewBinding.edittextPersonDetails.text.toString().trim()
                // Hide the error message if the price is not empty
                viewBinding.textinputLayoutPersonDetails.error = null

                // Proceed to make a ticket

                    makeTicket(this.currentUserId,otherUID,price)


            } else {
                // Show an error message if the price is empty
                viewBinding.textinputLayoutPersonDetails.error = "Please Enter a price"
                // Hide the progress bar since the ticket creation process is not initiated
                viewBinding.progressBarPersonDeatails.visibility = View.GONE
                viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            }
        }


    }



    private fun makeTicket(currentUserId: String, technicianId: String, price: String) {
        // Show progress bar when making the ticket
        viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
        viewBinding.bookNowPersonDetails.visibility = View.GONE

        // Fetch the current user details
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(currentUserId).get().addOnSuccessListener { documentSnapshot ->
                val currentUser = documentSnapshot.toObject(User::class.java)
                if (currentUser != null) {
                    // Set the price to the current user model
                    currentUser.price = price

                    // Now add the currentUser to the technician
                    val otherUserRef = db.collection(Constant.USER).document(technicianId)
                        .collection("Tickets")
                    val batch = db.batch()

                    // Add customer request to ticket sub collection
                    val customerRequestDoc = otherUserRef.document(currentUser.userID)
                    batch.set(customerRequestDoc, currentUser)

                    // Commit the batch operation
                    batch.commit().addOnSuccessListener {
                        // Handle success if needed
                        Log.d(TAG, "Ticket created successfully")
                        // Hide progress bar on success
                        viewBinding.progressBarPersonDeatails.visibility = View.GONE
                        viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
                    }.addOnFailureListener { e ->
                        // Handle failure if needed
                        Log.e(TAG, "Error creating ticket", e)
                        // Hide progress bar on failure
                        viewBinding.progressBarPersonDeatails.visibility = View.GONE
                        viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
                    }
                } else {
                    Log.e(TAG, "Current user is null")
                    // Hide progress bar on failure
                    viewBinding.progressBarPersonDeatails.visibility = View.GONE
                    viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
                }
            }.addOnFailureListener { e ->
                // Handle failure if needed
                Log.e(TAG, "Error getting current user", e)
                // Hide progress bar on failure
                viewBinding.progressBarPersonDeatails.visibility = View.GONE
                viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            }
    }


    /*
    private fun sendNotification(notification: Sender) = CoroutineScope(Dispatchers.IO).launch {
        try {

            // Calling the api to send a notification
            val response = FCM.api.sendNotification(notification)
            if (response.isSuccessful) {
                Log.d("DODA", Gson().toJson(response))
            } else {
                Log.d("DODA", response.errorBody().toString())

            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Snackbar.make(viewBinding.root, e.localizedMessage!!, Snackbar.LENGTH_LONG).show()

            }
        }

    }

*/

}