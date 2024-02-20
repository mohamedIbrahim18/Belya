package com.example.belya.ui.registration.customerinfo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.belya.Constant
import com.example.belya.databinding.ActivityCustomerInfoBinding
import com.example.belya.ui.customer_main.CustomerMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCustomerInfoBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomerInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.saveChanges.setOnClickListener {
            // update the new attributes
            updateCustomerData()
        }
    }

    private fun getTheNewData(): Map<String, Any> {
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val location = viewBinding.locationEd.text.toString().trim()

        val newData = mutableMapOf<String, Any>()
        if (phoneNumber.isNotEmpty()) {
            newData["phoneNumber"] = phoneNumber
        }
        newData["location"] = location

        return newData
    }

    private fun updateCustomerData() {
        val userCustomerData = getTheNewData()
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid!!
        val User = db.collection(Constant.USER)
        // Update the document with the new data
        User.document(documentId)
            .update(userCustomerData)
            .addOnSuccessListener {
                // Update successful
                navigateToCustomerPage()
            }
            .addOnFailureListener { e ->
                // Handle the error
                Log.e("ERROR_HANDLE", e.localizedMessage!!)
            }
    }

    private fun navigateToCustomerPage() {
        val intent = Intent(this, CustomerMainActivity::class.java)
        startActivity(intent)
        finish()
    }

}