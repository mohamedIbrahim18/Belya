package com.example.belya.ui.registration.customerinfo

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.belya.Constant
import com.example.belya.utils.base.LoadingDialog
import com.example.belya.databinding.ActivityCustomerInfoBinding
import com.example.belya.ui.customer_main.CustomerMainActivity
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class CustomerInfoActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCustomerInfoBinding
    private val auth = FirebaseAuth.getInstance()
    private var selectedImg: Uri? = null
    private val loading by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomerInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.profileImg.setOnClickListener {
            openImageChooser()
        }
        viewBinding.penToChangeImage.setOnClickListener {
            openImageChooser()
        }

        viewBinding.saveChanges.setOnClickListener {
            saveChanges()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    private fun saveChanges() {
        loading.startLoading()
        viewBinding.saveChanges.visibility = View.GONE
        uploadImage()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let {
                selectedImg = it
                viewBinding.profileImg.setImageURI(it)
            }
        }
    }

    private fun uploadImage() {
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

    private fun uploadInfo(imageUrl: String) {
        val userCustomerData = getTheNewData().toMutableMap()
        userCustomerData["imagePath"] = imageUrl
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid ?: ""
        val userRef = db.collection(Constant.USER).document(documentId)
        userRef.update(userCustomerData)
            .addOnSuccessListener {
                navigateToCustomerPage()
            }
            .addOnFailureListener { e ->
                handleUpdateFailure(e)
            }
    }

    private fun getTheNewData(): Map<String, Any> {
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()
        val newData = mutableMapOf<String, Any>()
        if (phoneNumber.isNotEmpty()) {
            newData["phoneNumber"] = phoneNumber
        }
        return newData
    }

    private fun handleUploadFailure(e: Exception) {
        Log.e("UPLOAD_IMAGE", "Failed to upload image: ${e.message}")
        showToast("Failed to upload image")
        viewBinding.saveChanges.visibility = View.VISIBLE
        loading.isDismiss()
    }

    private fun handleUpdateFailure(e: Exception) {
        Log.e("UPDATE_USER", "Failed to update user data: ${e.message}")
        showToast("Failed to update user data")
        viewBinding.saveChanges.visibility = View.VISIBLE
        loading.isDismiss()
    }

    private fun navigateToCustomerPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 1
    }
}