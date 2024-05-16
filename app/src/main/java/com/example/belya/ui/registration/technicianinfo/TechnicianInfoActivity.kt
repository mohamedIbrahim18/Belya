package com.example.belya.ui.registration.technicianinfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.adapter.JobOptionAdapter
import com.example.belya.utils.base.LoadingDialog
import com.example.belya.databinding.ActivityTechnicianInfoBinding
import com.example.belya.model.JobOption
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.example.belya.ui.technician_main.TechnicianMainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class TechnicianInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityTechnicianInfoBinding
    private val auth = FirebaseAuth.getInstance()
    private var selectedImg: Uri? = null
    private var selectedJob: String = ""
    val loading = LoadingDialog(this)

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTechnicianInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupSpinner()
        initViews()
    }

    private fun initViews() {
        viewBinding.profileImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
        viewBinding.penToChangeImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        viewBinding.saveChanges.setOnClickListener {
            // check the fieldls not emptht and phone number ong 11 digiht
            loading.startLoading()
            viewBinding.saveChanges.visibility = View.GONE
            updateTechnicianData()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null && data.data != null) {
                selectedImg = data.data
                viewBinding.profileImg.setImageURI(data.data)
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
        selectedImg?.let { newData["imagePath"] = it.toString() }
        newData["work_experience"] = workExperience
        newData["job"] = selectedJob
        newData["city"] = ""

        return newData
    }

    private fun uploadImage() {
        selectedImg?.let { imgUri ->
            val reference = FirebaseStorage.getInstance().reference.child("Profile")
                .child(Date().time.toString())
            reference.putFile(imgUri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.storage?.downloadUrl?.addOnSuccessListener { uri ->
                        uploadInfo(uri.toString())
                    }?.addOnFailureListener { e ->
                        Log.e("UPLOAD_IMAGE", "Failed to upload image: ${e.message}")
                        handleUploadFailure()
                    }
                } else {
                    Log.e("UPLOAD_IMAGE", "Failed to upload image: ${task.exception?.message}")
                    handleUploadFailure()
                }
            }
        }
    }

    private fun uploadInfo(imageUrl: String) {
        val userTechnicianData = getTheNewData().toMutableMap()
        if (imageUrl.isNotEmpty()) {
            userTechnicianData["imagePath"] = imageUrl
        }
        // Update user data in Firestore
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid ?: ""
        val userRef = db.collection(Constant.USER).document(documentId)
        userRef.update(userTechnicianData).addOnSuccessListener {
            // Update successful
            Snackbar.make(viewBinding.root, "successfully you created account go to login", Snackbar.LENGTH_LONG)
                .show()
            navigateToTechnicianPage()
        }.addOnFailureListener { e ->
            // Handle the error
            Log.e("UPDATE_USER", "Failed to update user data: ${e.message}")
            handleUploadFailure()
        }
    }

    private fun updateTechnicianData() {
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val workExperience = viewBinding.workExperienceEd.text.toString().trim()

        viewBinding.inputlayoutPhone.error = null
        viewBinding.inputlayoutWorkExperience.error = null

        var isValid = true
        if (phoneNumber.isEmpty()) {
            viewBinding.inputlayoutPhone.error = "Phone number is required"
            isValid = false
        } else if (phoneNumber.length != 11) {
            viewBinding.inputlayoutPhone.error = "Phone number must be 11 digits"
            isValid = false
        }

        if (workExperience.isEmpty()) {
            viewBinding.inputlayoutWorkExperience.error = "Work experience is required"
            isValid = false
        }

        if (isValid) {
            if (selectedImg != null) {
                uploadImage()
            } else {
                uploadInfo("")
            }
        } else {
            // Display error message and enable save button
            showSnackBar("Please fix the errors before saving")
            loading.isDismiss()
            viewBinding.saveChanges.visibility = View.VISIBLE
        }
    }

    private fun showSnackBar(s: String) {
        Snackbar.make(viewBinding.root, s, Snackbar.LENGTH_LONG).show()
    }


    private fun navigateToTechnicianPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupSpinner() {
        val jobOptions = listOf(
            JobOption("Plumber", R.drawable.ic_plumber),
            JobOption("Pharmacist", R.drawable.ic_pharmacist),
            JobOption("Carpenter", R.drawable.ic_ngar),
            JobOption("Electrician", R.drawable.ic_electrician),
            JobOption("Mechanic", R.drawable.ic_mechanic)
        )

        val adapter = JobOptionAdapter(this, jobOptions)
        viewBinding.jobSpinner.apply {
            this.adapter = adapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedJob = jobOptions[position].name
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle Nothing Selected
                }
            }
        }
    }

    private fun handleUploadFailure() {
        viewBinding.saveChanges.visibility = View.VISIBLE
    }
}
