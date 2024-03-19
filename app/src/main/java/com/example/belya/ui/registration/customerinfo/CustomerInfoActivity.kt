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
    val loading = LoadingDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCustomerInfoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.profileImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        viewBinding.saveChanges.setOnClickListener {
            loading.startLoading()
            viewBinding.saveChanges.visibility = View.GONE
            updateCustomerData()
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

    private fun getTheNewData(): Map<String, Any> {
        val phoneNumber = viewBinding.phoneEd.text.toString().trim()

        val newData = mutableMapOf<String, Any>()
        if (phoneNumber.isNotEmpty()) {
            newData["phoneNumber"] = phoneNumber
        }
        selectedImg?.let { newData["imagePath"] = it.toString() }

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
                        viewBinding.saveChanges.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("UPLOAD_IMAGE", "Failed to upload image: ${task.exception?.message}")
                    viewBinding.saveChanges.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun uploadInfo(imageUrl: String) {
        val userCustomerData = getTheNewData().toMutableMap()
        userCustomerData["imagePath"] = imageUrl
        // Update user data in Firestore
        val db = FirebaseFirestore.getInstance()
        val documentId = auth.uid!!
        val userRef = db.collection(Constant.USER).document(documentId)
        userRef.update(userCustomerData).addOnSuccessListener {
            // Update successful
            navigateToCustomerPage()
            viewBinding.saveChanges.visibility = View.VISIBLE
        }.addOnFailureListener { e ->
            // Handle the error
            Log.e("UPDATE_USER", "Failed to update user data: ${e.message}")
            viewBinding.saveChanges.visibility = View.VISIBLE
        }
    }

    private fun updateCustomerData() {
        uploadImage()
    }

    private fun navigateToCustomerPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
