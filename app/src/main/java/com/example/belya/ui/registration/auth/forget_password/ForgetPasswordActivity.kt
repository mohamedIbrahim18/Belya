package com.example.belya.ui.registration.auth.forget_password
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.belya.Constant
import com.example.belya.databinding.ActivityForgetPasswordBinding
import com.example.belya.ui.registration.auth.login.LoginActivity
import com.example.belya.utils.Common
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var auth: FirebaseAuth
    private val usersCollection = FirebaseFirestore.getInstance().collection(Constant.USER)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val check = Common()
        if (!check.isConnectedToInternet(this)){
            check.showInternetDisconnectedDialog(this)
        }
        initViews()
    }

    private fun initViews() {
        binding.btnForgotPasswordBack.setOnClickListener {
            finish()
        }
        auth = Firebase.auth
        checkEmail()
    }

    private fun checkEmail() {
        binding.btnReset.setOnClickListener {
            val userEmail = binding.edtForgotPasswordEmail.text.toString().trim()
            if (!TextUtils.isEmpty(userEmail)) {
                checkEmailExists(userEmail)
            } else {
                binding.edtForgotPasswordEmail.error = "Email field can't be empty"
            }
        }
    }

    private fun checkEmailExists(userEmail: String) {
        usersCollection.whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Email does not exist in the database
                    Snackbar.make(binding.root, "User with this email does not exist", Snackbar.LENGTH_SHORT).show()
                } else {
                    // Email exists in the database
                    resetPassword(userEmail)
                }
            }
            .addOnFailureListener { e ->
                // Error occurred while querying the database
                Snackbar.make(binding.root, "Error: ${e.localizedMessage}", Snackbar.LENGTH_SHORT).show()
            }
    }

    private fun resetPassword(userEmail: String) {
        binding.forgetPasswordProgressbar.visibility = View.VISIBLE
        binding.btnReset.visibility = View.INVISIBLE
        auth.sendPasswordResetEmail(userEmail)
            .addOnSuccessListener {
                Toast.makeText(this, "Reset Password email sent", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
            .addOnFailureListener { e ->
                Snackbar.make(binding.root, "Error: ${e.localizedMessage}", Snackbar.LENGTH_SHORT).show()
                binding.forgetPasswordProgressbar.visibility = View.INVISIBLE
                binding.btnReset.visibility = View.VISIBLE
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
