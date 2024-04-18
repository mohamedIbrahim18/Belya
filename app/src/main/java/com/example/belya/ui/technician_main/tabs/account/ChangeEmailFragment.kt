package com.example.belya.ui.technician_main.tabs.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.FragmentChangeEmailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeEmailFragment : Fragment() {

    private lateinit var viewBinding: FragmentChangeEmailBinding
    private lateinit var auth: FirebaseAuth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentChangeEmailBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        viewBinding.updateBtn.setOnClickListener {
            val newEmail = viewBinding.emailTv.text.toString()
            if (newEmail.isEmpty()) {
                Snackbar.make(viewBinding.root, "Please enter a new email", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                showConfirmationDialog(newEmail)
            }
        }
    }

    private fun showConfirmationDialog(newEmail: String) {
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure you want to update your email to $newEmail?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                updateEmail(newEmail)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateEmail(newEmail: String) {
        val user = auth.currentUser
        user?.verifyBeforeUpdateEmail(newEmail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updateUserEmailInFirestore(user.uid, newEmail)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to update email: ${task.exception?.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateUserEmailInFirestore(uid: String, newEmail: String) {
        val userRef = firestore.collection(Constant.USER).document(uid)
        userRef.update("email", newEmail)
            .addOnSuccessListener {
                AlertDialog.Builder(requireContext()).setMessage("Please Go Confirm Your Email")
                    .setPositiveButton("Ok"){ d,_ ->
                        d.dismiss()
                    }.show()
            }
            .addOnFailureListener { e ->
                Snackbar.make(
                    viewBinding.root,
                    "Failed to update email in Firestore: ${e.localizedMessage}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }
}

