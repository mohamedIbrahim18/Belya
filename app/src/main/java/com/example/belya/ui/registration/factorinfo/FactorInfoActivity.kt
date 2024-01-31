package com.example.belya.ui.registration.factorinfo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivityFactorInfoBinding
import com.example.belya.ui.Constent
import com.example.belya.ui.Factor_Main.FactorMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FactorInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFactorInfoBinding
    val auth = FirebaseAuth.getInstance()
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

    private fun getTheNewData() {

    }

    private fun updateFactorData() {
        getTheNewData()
        val userFactorData = /* your updated data, replace this with your actual data */
        val db = FirebaseFirestore.getInstance()

        val documentId = auth.uid!! // replace this with the actual document ID
        val userFactorsCollection = db.collection(Constent.USER_FACTOR_COLLECTION)

        // Update the document with the new data
        userFactorsCollection.document(documentId)
            .update(userFactorData)
            .addOnSuccessListener {
                // Update successful
                // You can add any additional logic or UI updates here
            }
            .addOnFailureListener { e ->
                // Handle the error
                // You can add error logging or show an error message to the user
            }
    }


    private fun navigateToFactorPage() {
        val intent = Intent(this,FactorMainActivity::class.java)
        startActivity(intent)
        finish()
    }


}