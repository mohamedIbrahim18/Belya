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
    lateinit var viewBinding : FragmentAccountCustomerBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        viewBinding.accountChangeEmail.setOnClickListener {
            findNavController().navigate(R.id.action_accountCustomerFragment_to_changeEmailFragment2)
        }
        viewBinding.accountChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_accountCustomerFragment_to_changePasswordFragment2)
        }
        viewBinding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            navigateToLoginPage()
        }
        fetchDataFromFireStore()

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
                    // Update UI with fetched user detailsval fullName: String = it.firstName + " " + it.lastName
                    viewBinding.myName.text = fullName
                    viewBinding.myEmail.text = it.email
                    viewBinding.myNumber.text = it.phoneNumber
                    viewBinding.myCity.text =it.city
                    Glide.with(viewBinding.accountProfilePic)
                        .load(it.imagePath)
                        .placeholder(R.drawable.profile_pic)
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

}
