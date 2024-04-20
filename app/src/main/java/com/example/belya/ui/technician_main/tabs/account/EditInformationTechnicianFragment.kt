package com.example.belya.ui.technician_main.tabs.account

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.belya.Constant
import com.example.belya.databinding.FragmentEditInformationTechnicianBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class EditInformationTechnicianFragment : Fragment() {
    private lateinit var viewBinding: FragmentEditInformationTechnicianBinding
    private val auth = FirebaseAuth.getInstance()
    private var selectedImg: Uri? = null
    private var selectedJob: String = ""
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentEditInformationTechnicianBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Selecting Image...")
        progressDialog.setCancelable(false)
        setupSpinner()
        initViews()
    }

    private fun initViews() {
        viewBinding.accountProfilePic.setOnClickListener{
            openImageChooser()
        }
        viewBinding.penToChangeImage.setOnClickListener {
            openImageChooser()
        }
        viewBinding.saveChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val firstName = viewBinding.firstnameEd.text.toString().trim()
        val lastName = viewBinding.lastnameEd.text.toString().trim()
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val workExperience = viewBinding.workExperienceEd.text.toString().trim()

        if (!validateData(firstName, lastName, phoneNumber,workExperience)) {
            return
        }
        updateFirestore(firstName, lastName, phoneNumber, workExperience)
    }

    private fun validateData(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        workExperience: String
    ): Boolean {
        if (firstName.isEmpty()) {
            viewBinding.firstnameEd.error = "Please enter your first name"
            return false
        }

        if (lastName.isEmpty()) {
            viewBinding.lastnameEd.error = "Please enter your last name"
            return false
        }
        if (workExperience.isEmpty()){
            viewBinding.workExperienceEd.error = "Please enter your experience"
            return false
        }

        if (phoneNumber.isEmpty()) {
            viewBinding.phoneEd.error = "Please enter your phone number"
            return false
        } else if (phoneNumber.length != 11) {
            viewBinding.phoneEd.error = "Phone number must be 11 digits long"
            return false
        }

        return true
    }

    private fun updateFirestore(firstName: String, lastName: String, phoneNumber: String, workExperience: String) {
        val newData = getNewUserData(firstName, lastName, phoneNumber, workExperience)
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid ?: ""
        val userRef = db.collection(Constant.USER).document(documentId)

        userRef.update(newData)
            .addOnSuccessListener {
                showToast("Changes saved successfully")
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                handleUploadFailure(e)
            }
    }

    private fun getNewUserData(firstName: String, lastName: String, phoneNumber: String, workExperience: String): Map<String, Any> {
        val newData = mutableMapOf<String, Any>()
        newData["firstName"] = firstName
        newData["lastName"] = lastName
        newData["phoneNumber"] = phoneNumber
        newData["work_experience"] = workExperience
        newData["job"] = selectedJob

        return newData
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, EditInformationTechnicianFragment.REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditInformationTechnicianFragment.REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                selectedImg = it
                viewBinding.accountProfilePic.setImageURI(it)
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.show() // Show ProgressDialog before starting upload

        selectedImg?.let { imgUri ->
            val reference = FirebaseStorage.getInstance().reference.child("Profile")
                .child("${Date().time}")
            reference.putFile(imgUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        uploadInfo(uri.toString())
                    }.addOnFailureListener { e ->
                        handleUploadFailure(e)
                    }
                }
                .addOnFailureListener { e ->
                    handleUploadFailure(e)
                }
        } ?: run {
            uploadInfo("") // If no image selected, proceed with empty image URL
        }
    }

    private fun handleUploadFailure(e: Exception) {
        progressDialog.dismiss() // Dismiss ProgressDialog on failure
        Log.e("UPLOAD_IMAGE", "Failed to upload image: ${e.message}")
        showToast("Failed to upload image")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun uploadInfo(imageUrl: String) {
        // Update only the "imagePath" attribute with the new value
        val updatedUserData = mapOf("imagePath" to imageUrl)

        // Update the specific attribute "imagePath" in Firestore
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid ?: ""
        val userRef = db.collection(Constant.USER).document(documentId)
        userRef.update(updatedUserData)
            .addOnSuccessListener {
                progressDialog.dismiss() // Dismiss ProgressDialog on success
                // After updating the image path, fetch all data for the document
                showToast("Finished")
            }
            .addOnFailureListener { e ->
                handleUploadFailure(e)
            }
    }

    private fun setupSpinner() {
        val jobOptions = arrayOf("Plumbing", "Pharmacy", "Carpentry", "Electricity", "Mechanics")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jobOptions)
        viewBinding.jobSpinner.apply {
            this.adapter = adapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedJob = jobOptions[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle Nothing Selected
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 1
    }
}