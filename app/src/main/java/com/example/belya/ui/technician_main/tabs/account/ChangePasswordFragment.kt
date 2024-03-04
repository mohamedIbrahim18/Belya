package com.example.belya.ui.technician_main.tabs.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.belya.databinding.FragmentChangePasswordBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChangePasswordFragment : Fragment() {
    private lateinit var auth: FirebaseUser
    private lateinit var viewBinding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance().currentUser!!
        initViews()
    }

    private fun initViews() {
        viewBinding.updateBtn.setOnClickListener {
            updatePassword()
        }
    }

    private fun updatePassword() {
        val oldPassword = viewBinding.currentPasswordTv.text.toString()
        val newPassword = viewBinding.passwordTv.text.toString()
        if (checkValidate()) {
            val credential = EmailAuthProvider.getCredential(auth.email!!, oldPassword)
            auth.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    auth.updatePassword(newPassword)
                        .addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                Snackbar.make(
                                    viewBinding.root,
                                    "Password updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Snackbar.make(
                                    viewBinding.root,
                                    "Failed to update password: ${passwordTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Snackbar.make(
                        viewBinding.root,
                        "Authentication failed: Check Your Old Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkValidate(): Boolean {
        var isValid = true

        if (viewBinding.currentPasswordTv.text.isNullOrBlank()) {
            viewBinding.currentPasswordLayout.error = "Please enter your current password"
            isValid = false
        } else {
            viewBinding.currentPasswordLayout.error = null
        }

        if (viewBinding.passwordTv.text.isNullOrBlank()) {
            viewBinding.updatePasswordLayout.error = "Please enter your new password"
            isValid = false
        } else {
            viewBinding.updatePasswordLayout.error = null
        }

        if (viewBinding.confirmPasswordTv.text.isNullOrBlank()) {
            viewBinding.updateConfirmPasswordLayout.error =
                "Please confirm your new password"
            isValid = false
        } else {
            viewBinding.updateConfirmPasswordLayout.error = null
        }

        if (viewBinding.passwordTv.text.toString() != viewBinding.confirmPasswordTv.text.toString()) {
            viewBinding.updatePasswordLayout.error = "Passwords do not match"
            viewBinding.updateConfirmPasswordLayout.error = "Passwords do not match"
            isValid = false
        }

        return isValid
    }
}
