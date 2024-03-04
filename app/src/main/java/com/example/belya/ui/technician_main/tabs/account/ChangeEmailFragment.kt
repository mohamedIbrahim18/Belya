package com.example.belya.ui.technician_main.tabs.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentAccountTechnicianBinding
import com.example.belya.databinding.FragmentChangeEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeEmailFragment : Fragment() {

    lateinit var viewBinding: FragmentChangeEmailBinding
    private lateinit var auth: FirebaseAuth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentChangeEmailBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        viewBinding.updateBtn.setOnClickListener {
            val user = auth.currentUser
            val email = viewBinding.emailTv.text.toString()
            user?.verifyBeforeUpdateEmail(email)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // show toast
                    Toast.makeText(requireContext(), "Done AUTH", Toast.LENGTH_SHORT).show()
                    if(updateUserEmailInFirestore(auth.uid!!,email)){
                        Toast.makeText(requireContext(), "Done EVERY THing", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "{${it.exception?.localizedMessage}}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateUserEmailInFirestore(uid: String, newEmail: String)  : Boolean{
        var isDone = false
        // Update email in Firestore
        val userRef = firestore.collection(Constant.USER).document(uid)
        userRef.update("email", newEmail)
            .addOnSuccessListener {
                // Email updated in Firestore successfully
                Toast.makeText(requireContext(), "Email updated successfully in fire base", Toast.LENGTH_LONG)
                    .show()
                isDone = true
            }
            .addOnFailureListener { e ->
                // Failed to update email in Firestore
                Toast.makeText(
                    requireContext(),
                    "Failed to update email in Firestore: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                isDone = false
            }
        return  isDone
    }
}