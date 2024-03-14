package com.example.belya.ui.registration.technicianinfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivityTechnicianInfoBinding
import com.example.belya.Constant
import com.example.belya.ui.technician_main.TechnicianMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class TechnicianInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTechnicianInfoBinding
    private val auth = FirebaseAuth.getInstance()
    private var job: String? = null
    private var selectedImg: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTechnicianInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initSpinner()
        initViews()
    }

    private fun initViews() {
        viewBinding.profileImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        viewBinding.progressBar.visibility = View.GONE
        viewBinding.saveChanges.setOnClickListener {
            // Check if essential fields are not empty
            if (isFieldsValid()) {
                // update the new attributes
                selectedImg?.let {
                    uploadImage(it)
                }
            } else {
                // Show error message or handle empty fields
                // For example:
                // Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (data.data != null) {
                    selectedImg = data.data
                    viewBinding.profileImg.setImageURI(data.data)
                }
            }
        }
    }


    private fun initSpinner() {
        // Dummy job data
        val jobOptions = arrayOf("Plumping", "Pharmacy", "Carpentry", "Electricity", "Mechanics")
        val madapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jobOptions)
        viewBinding.jobSpinner.apply {
            adapter = madapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    job = jobOptions[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle Nothing Selected
                }
            }
        }
    }

    private fun isFieldsValid(): Boolean {
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val city = viewBinding.cityEd.text.toString().trim()
        return phoneNumber.isNotEmpty() && city.isNotEmpty()
    }

    private fun uploadImage(selectedImg: Uri) {
        val reference = FirebaseStorage.getInstance().reference.child("Profile").child(Date().time.toString())
        reference.putFile(selectedImg).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.storage?.downloadUrl?.addOnSuccessListener { uri ->
                    uploadUserInfo(uri.toString())
                }?.addOnFailureListener { e ->
                    handleUploadFailure(e)
                }
            } else {
                handleUploadFailure(task.exception)
            }
        }
    }

    private fun uploadUserInfo(imageUrl: String) {
        val userFactorData = getTheNewData(imageUrl)
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid!!
        val userFactorsCollection = db.collection(Constant.USER)
        // Update the document with the new data
        userFactorsCollection.document(documentId)
            .update(userFactorData)
            .addOnSuccessListener {
                // Update successful
                navigateToFactorPage()
            }
            .addOnFailureListener { e ->
                // Handle the error
                handleUpdateFailure(e)
            }
    }

    private fun handleUploadFailure(exception: Exception?) {
        Log.e("UPLOAD_IMAGE", "Failed to upload image: ${exception?.message}")
        viewBinding.progressBar.visibility = View.GONE
        viewBinding.saveChanges.visibility = View.VISIBLE
    }

    private fun handleUpdateFailure(exception: Exception?) {
        Log.e("ERROR_HANDLE", exception?.localizedMessage ?: "Unknown error")
        viewBinding.progressBar.visibility = View.GONE
        viewBinding.saveChanges.visibility = View.VISIBLE
    }

    private fun getTheNewData(imageUrl: String): Map<String, Any> {
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val workExperience = viewBinding.workExperienceEd.text.toString().trim()
        val city = viewBinding.cityEd.text.toString().trim()

        val newData = mutableMapOf<String, Any>()
        newData["phoneNumber"] = phoneNumber
        newData["imagePath"] = imageUrl
        newData["city"] = city
        newData["job"] = job ?: ""
        newData["work_experience"] = workExperience

        return newData
    }

    private fun navigateToFactorPage() {
        val intent = Intent(this, TechnicianMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
