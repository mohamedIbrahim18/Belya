package com.example.belya.ui.customer_main.tabs.account

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.R
import com.example.belya.databinding.FragmentAccountCustomerBinding
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class AccountCustomerFragment : Fragment() {
    lateinit var viewBinding : FragmentAccountCustomerBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentAccountCustomerBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            navigateToLoginPage()
        }
    }

    private fun navigateToLoginPage() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


}