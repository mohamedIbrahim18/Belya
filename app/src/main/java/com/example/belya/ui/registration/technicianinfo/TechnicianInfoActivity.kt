package com.example.belya.ui.registration.technicianinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivityTechnicianInfoBinding
import com.example.belya.Constent
import com.example.belya.ui.technician_main.TechnicianMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TechnicianInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTechnicianInfoBinding
    private val auth = FirebaseAuth.getInstance()
    var job: String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTechnicianInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initSpinner()
        initViews()
    }

    private fun initViews() {
        viewBinding.saveChanges.setOnClickListener {
            // update the new attributes

            updateFactorData()
        }
    }

    private fun initSpinner() {
        // Dummy job data
        val jobOptions = arrayOf("mechanical", "plumber", "Carpenter", "electrical","Smith")
        val madapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jobOptions)
        viewBinding.jobSpinner.apply {
            adapter = madapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                     job = jobOptions[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }
    }

    private fun getTheNewData(): Map<String, Any> {

        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val workExperience = viewBinding.workExperienceEd.text.toString().trim()

        val newData = mutableMapOf<String, Any>()
        if (phoneNumber.isNotEmpty()) {
            newData["phoneNumber"] = phoneNumber
        }
        newData["job"] = job?:""
        newData["work_experience"] = workExperience

        return newData
    }

    private fun updateFactorData() {
        val userFactorData = getTheNewData()
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid!!
        val userFactorsCollection = db.collection(Constent.USER_TECHNICIAN_COLLECTION)
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
        val intent = Intent(this,TechnicianMainActivity::class.java)
        startActivity(intent)
        finish()
    }


}