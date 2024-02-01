package com.example.belya.ui.registration.factorinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivityFactorInfoBinding
import com.example.belya.ui.Constent
import com.example.belya.ui.Factor_Main.FactorMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FactorInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFactorInfoBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFactorInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.saveChanges.setOnClickListener {
            // update the new attributes
            updateFactorData()
        }
    }

    private fun getTheNewData(): Map<String, Any> {
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val occupation = viewBinding.occupationEd.text.toString().trim()
        val workExperience = viewBinding.workExperienceEd.text.toString().trim()

        val newData = mutableMapOf<String, Any>()
        if (phoneNumber.isNotEmpty()) {
            newData["phoneNumber"] = phoneNumber
        }
        newData["occupation"] = occupation
        newData["work_experience"] = workExperience

        return newData
    }

    private fun updateFactorData() {
        val userFactorData = getTheNewData()
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid!!
        val userFactorsCollection = db.collection(Constent.USER_FACTOR_COLLECTION)
        // Update the document with the new data
        userFactorsCollection.document(documentId)
            .update(userFactorData)
            .addOnSuccessListener {
                // Update successful
                navigateToFactorPage()
            }
            .addOnFailureListener { e ->
                // Handle the error
                Log.e("ERROR_HANDLE", e.localizedMessage!!)
            }
    }

    private fun navigateToFactorPage() {
        val intent = Intent(this,FactorMainActivity::class.java)
        startActivity(intent)
        finish()
    }


}