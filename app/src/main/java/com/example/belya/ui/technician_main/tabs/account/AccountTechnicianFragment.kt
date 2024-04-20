package com.example.belya.ui.technician_main.tabs.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentAccountTechnicianBinding
import com.example.belya.model.User
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.example.belya.utils.base.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class AccountTechnicianFragment : Fragment() {
    lateinit var viewBinding: FragmentAccountTechnicianBinding
    lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAccountTechnicianBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val check = Common()
        if (!check.isConnectedToInternet(requireContext())){
            check.showInternetDisconnectedDialog(requireContext())
        }
        auth = FirebaseAuth.getInstance()
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
        viewBinding.editProfileDetails.setOnClickListener {
            findNavController().navigate(R.id.action_accountTechnicianFragment2_to_editInformationFragment)
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
                        viewBinding.myJob.text = it.job
                        viewBinding.myCity.text = it.city
                        viewBinding.myNumber.text = it.phoneNumber
                        viewBinding.myWorkEx.text = it.work_experience
                        Glide.with(viewBinding.accountProfilePic)
                            .load(it.imagePath)
                            .placeholder(R.drawable.profile_pic)
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
}