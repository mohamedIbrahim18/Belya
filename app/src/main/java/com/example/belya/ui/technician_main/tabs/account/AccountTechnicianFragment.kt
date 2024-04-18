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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentAccountTechnicianBinding
import com.example.belya.model.User
import com.example.belya.ui.customer_main.tabs.account.AccountCustomerFragment
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.example.belya.utils.base.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class AccountTechnicianFragment : Fragment() {
    lateinit var viewBinding: FragmentAccountTechnicianBinding
    private lateinit var auth: FirebaseAuth
    private var selectedImg: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentAccountTechnicianBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val check = Common()
        if (!check.isConnectedToInternet(requireContext())){
            check.showInternetDisconnectedDialog(requireContext())
        }
        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading Image...")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCanceledOnTouchOutside(false)
        initViews()
        fetchDataFromFireStore()
    }

    private fun initViews() {
        viewBinding.accountChangeEmail.setOnClickListener {
            findNavController().navigate(R.id.action_accountTechnicianFragment2_to_changeEmailFragment)
        }
        viewBinding.accountChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_accountTechnicianFragment2_to_changePasswordFragment)
        }
        viewBinding.acceptedTickets.setOnClickListener {
            findNavController().navigate(R.id.action_accountTechnicianFragment2_to_acceptedTicketsFragment)
        }
        viewBinding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            navigateToLoginPage()
        }
        viewBinding.accountProfilePic.setOnClickListener{
            openImageChooser()

        }
        fetchDataFromFireStore()

    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, AccountTechnicianFragment.REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AccountTechnicianFragment.REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
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
                fetchDataFromFireStore()
                showToast("Finished")
            }
            .addOnFailureListener { e ->
                handleUploadFailure(e)
            }
    }

    private fun fetchDataFromFireStore() {
        val currentUID = FirebaseAuth.getInstance().uid
        FirebaseFirestore.getInstance().collection(Constant.USER).document(currentUID!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("Error", "${error.localizedMessage}")
                    return@addSnapshotListener
                }
                value?.let { snapshot ->
                    val userDetails = snapshot.toObject(User::class.java)
                    userDetails?.let {
                        val fullName: String = it.firstName + " " + it.lastName
                        viewBinding.myName.text = fullName
                        viewBinding.myEmail.text = it.email
                        Glide.with(viewBinding.accountProfilePic)
                            .load(it.imagePath)
                            .placeholder(R.drawable.ic_profileimg)
                            .into(viewBinding.accountProfilePic)

                    }
                }
            }
    }

    private fun navigateToLoginPage() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    companion object {
        private const val REQUEST_CODE_IMAGE = 1
    }
}