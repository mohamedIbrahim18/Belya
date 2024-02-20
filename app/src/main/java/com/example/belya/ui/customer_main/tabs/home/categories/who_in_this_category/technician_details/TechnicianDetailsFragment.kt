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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
class TechnicianDetailsFragment : Fragment() {
    lateinit var viewBinding: FragmentTechnicianDeatilsBinding
    private lateinit var person: User
    private lateinit var db: FirebaseFirestore
    private val auth = FirebaseAuth.getInstance()

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
        // initViews()
        db = FirebaseFirestore.getInstance()
        arguments?.let {
            it.getParcelable<User>("pokemon")?.let { task ->
                person = task
                // viewBinding.imagePersonDetails.setImageResource(task.imagePath!!)
                val name = person.firstName + " " + person.lastName
                viewBinding.firstnamePersonDetails.text = name
              val city = person.city
                 viewBinding.cityPersonDetails.text = city
                val rating = person.person_rate
                    viewBinding.ratingbarPersonDetails.rating= rating.toFloat()

                // average price

                //
                // doc in tech coll have tickets
                // name + price + city + image
                // customer have doc tickets

            }
        }
        viewBinding.progressBarPersonDeatails.visibility = View.GONE
        viewBinding.bookNowPersonDetails.setOnClickListener {
            val currentUserDocumentRef = db.collection(Constant.USER).document(auth.uid!!)

            // Create a new collection within the user's document
            currentUserDocumentRef.collection(Constant.REQUEST)
                .document(auth.uid!!)
                .update("Requests", FieldValue.arrayUnion(auth.uid))
                .addOnSuccessListener {
                    // Document successfully updated
                    // Handle success, if needed
                }
                .addOnFailureListener { e ->
                    // Handle errors
                     Log.e(TAG, "Error updating document", e)
                }
        }

    }




    private fun checkPriceAndMakeAction() {
        viewBinding.bookNowPersonDetails.visibility = View.GONE
        viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
        if (viewBinding.edittextPersonDetails.text?.isNotEmpty() == true) {
            // hide error
            viewBinding.textinputLayoutPersonDetails.error = null
            // make a request to manage price
            // Assuming you have some method to handle the request here
        } else {
            // show error
            viewBinding.textinputLayoutPersonDetails.error = "Please Enter a price"
            viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            viewBinding.progressBarPersonDeatails.visibility = View.GONE
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