package com.example.belya.ui.customer_main.tabs.account
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentAccountCustomerBinding
import com.example.belya.model.User
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class AccountCustomerFragment : Fragment() {
    private lateinit var viewBinding: FragmentAccountCustomerBinding
    private lateinit var auth: FirebaseAuth
    private var selectedImg: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAccountCustomerBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewBinding.apply {
            accountChangeEmail.setOnClickListener {
                findNavController().navigate(R.id.action_accountCustomerFragment_to_changeEmailFragment2)
            }
            accountChangePassword.setOnClickListener {
               findNavController().navigate(R.id.action_accountCustomerFragment_to_changePasswordFragment2)
            }
            logout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                navigateToLoginPage()
            }
            accountProfilePic.setOnClickListener {
                openImageChooser()
            }
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
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
            .get()
            .addOnSuccessListener { documentSnapshot ->
                // Fetch all data for the document
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    // Update UI with fetched user details
                    val fullName: String = it.firstName + " " + it.lastName
                    viewBinding.myName.text = fullName
                    viewBinding.myEmail.text = it.email
                    Glide.with(viewBinding.accountProfilePic)
                        .load(it.imagePath)
                        .placeholder(R.drawable.ic_profileimg)
                        .into(viewBinding.accountProfilePic)
                }
            }
            .addOnFailureListener { e ->
                Log.d("Error", "${e.localizedMessage}")
            }
    }

    private fun navigateToLoginPage() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 1
    }
}
