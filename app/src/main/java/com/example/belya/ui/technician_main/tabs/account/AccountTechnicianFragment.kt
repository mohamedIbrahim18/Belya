package com.example.belya.ui.technician_main.tabs.account

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentAccountTechnicianBinding
import com.example.belya.model.User
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AccountTechnicianFragment : Fragment() {
    lateinit var viewBinding: FragmentAccountTechnicianBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentAccountTechnicianBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
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
                        viewBinding.accountProfilePic.load(it.imagePath){
                            placeholder(R.drawable.ic_profileimg)
                        }
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