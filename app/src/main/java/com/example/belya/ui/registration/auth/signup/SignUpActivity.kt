package com.example.belya.ui.registration.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.belya.databinding.ActivitySignUpBinding
import com.example.belya.ui.Constent
import com.example.belya.ui.Customer_Main.CustomerMainActivity
import com.example.belya.ui.registration.factorinfo.FactorInfoActivity
import com.example.belya.ui.userCustomer
import com.example.belya.ui.userFactor
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var viewBinding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        auth = Firebase.auth
        viewBinding.loadingProgressBar.isVisible = false
        viewBinding.btnCreateAccount.setOnClickListener {
            chooseWhoUseThisApp()
        }
    }

    fun doRegisterFactor(){
        viewBinding.apply {
            btnCreateAccount.setOnClickListener {
                val userFactor = userFactor(
                          firstnameEd.text.toString(),
                            lastnameEd.text.toString(),
                            emailEd.text.toString(),
                    "",
                    "",
                    "",
                    ""
                )
                var passwordEd = viewBinding.passwordEd.text.toString()
               var rePasswordEd = viewBinding.repasswordEd.text.toString()
                if (passwordEd.isBlank()) {
                    // show error
                    viewBinding.inputlayoutPassword.error = "Please enter a strong password"
                } else {
                    viewBinding.inputlayoutPassword.error = null
                }

                if (passwordEd != rePasswordEd) {
                    // show error
                    viewBinding.inputlayoutRepassword.error = "Password doesn't match"
                } else {
                    viewBinding.inputlayoutRepassword.error = null
                }
                registerFactor(userFactor,passwordEd)
            }
        }
    }

    private fun chooseWhoUseThisApp() {
            //get selected radio button from radiogroup
            val buttonId: Int = viewBinding.typeContainer.checkedRadioButtonId
            if (buttonId == View.NO_ID) {
                Toast.makeText(this, "Please Select Choose...", Toast.LENGTH_SHORT).show()
            } else {
                //TODO make validate
                val selectedRadioButton = findViewById<RadioButton>(buttonId)
                if (selectedRadioButton.text.equals("Customer")){
                   // navigateToCustomerPage()
                    doRegisterCustomer()
                } else if(selectedRadioButton.text.equals("Factor")){
                   // navigateToFactorDetails()
                    doRegisterFactor()
                }
            }
    }
    private fun navigateToFactorDetails() {
        val intent = Intent(this, FactorInfoActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCustomerPage() {
        val intent = Intent(this, CustomerMainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validForm(): Boolean {
        var isValid = true
        if (viewBinding.firstName.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutFirstname.error = "Please enter first name"
            isValid = false
        } else {
            viewBinding.inputlayoutFirstname.error = null
        }

        if (viewBinding.lastname.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutLastname.error = "Please enter last name"
            isValid = false
        } else {
            viewBinding.inputlayoutLastname.error = null
        }

        if (viewBinding.email.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutEmail.error = "Please enter your email"
            isValid = false
        } else {
            viewBinding.inputlayoutEmail.error = null

        }

        if (viewBinding.password.text.isNullOrBlank()) {
            // show error
            viewBinding.inputlayoutPassword.error = "Please enter a strong password"
            isValid = false
        } else {
            viewBinding.inputlayoutPassword.error = null
        }

        if (viewBinding.passwordEd.text.toString() != viewBinding.repasswordEd.text.toString()) {
            // show error
            viewBinding.inputlayoutRepassword.error = "Password doesn't match"
            isValid = false
        } else {
            viewBinding.inputlayoutRepassword.error = null
        }

        return isValid
    }

    private fun registerFactor(userFactor: userFactor, password:String) {
        viewBinding.loadingProgressBar.isVisible = true

        if (!validForm()) {
            setUiEnabled(true)
        } else {
            setUiEnabled(false)
                auth.createUserWithEmailAndPassword(userFactor.email!!, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User creation successful
                            task.result.user.let {
                                saveUserFactorDataBase(it?.uid!!,userFactor)
                                navigateToFactorDetails()
                            }
                        } else {
                            // User creation failed
                            Snackbar.make(viewBinding.root,task.exception?.localizedMessage!!,Snackbar.LENGTH_LONG).show()

                        }
                        setUiEnabled(true)
                    }


        }
    }

    private fun saveUserFactorDataBase(uId:String,userFactor: userFactor) {
        db.collection(Constent.USER_FACTOR_COLLECTION).document(uId)
            .set(userFactor)
            .addOnSuccessListener {documentReference->
                // Done
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }

    private fun saveUserCustomerDataBase(uId:String,userCustomer: userCustomer) {
        db.collection(Constent.USER_CUSTOMER_COLLECTION).document(uId)
            .set(userCustomer)
            .addOnSuccessListener {documentReference->
                // Done
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }
    private fun registerCustomer(userCustomer: userCustomer ,password:String) {
        viewBinding.loadingProgressBar.isVisible = true

        if (!validForm()) {
            setUiEnabled(true)
        } else {
            setUiEnabled(false)
            auth.createUserWithEmailAndPassword(userCustomer.email!!, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User creation successful
                        task.result.user.let {
                            saveUserCustomerDataBase(it?.uid!!,userCustomer)
                            navigateToCustomerPage()
                        }
                    } else {
                        // User creation failed
                        Snackbar.make(viewBinding.root,task.exception?.localizedMessage!!,Snackbar.LENGTH_LONG).show()

                    }
                    setUiEnabled(true)
                }


        }
    }
    fun doRegisterCustomer(){
        viewBinding.apply {
            btnCreateAccount.setOnClickListener {
                val userCustomer = userCustomer(
                    firstnameEd.text.toString(),
                    lastnameEd.text.toString(),
                    emailEd.text.toString(),
                )
                var passwordEd = viewBinding.passwordEd.text.toString()
                var rePasswordEd = viewBinding.repasswordEd.text.toString()
                if (passwordEd.isBlank()) {
                    // show error
                    viewBinding.inputlayoutPassword.error = "Please enter a strong password"
                } else {
                    viewBinding.inputlayoutPassword.error = null
                }

                if (passwordEd != rePasswordEd) {
                    // show error
                    viewBinding.inputlayoutRepassword.error = "Password doesn't match"
                } else {
                    viewBinding.inputlayoutRepassword.error = null
                }
                registerCustomer(userCustomer,passwordEd)
            }
        }
    }


    private fun setUiEnabled(enabled: Boolean) {
        viewBinding.firstName.isEnabled = enabled
        viewBinding.lastnameEd.isEnabled = enabled
        viewBinding.emailEd.isEnabled = enabled
        viewBinding.passwordEd.isEnabled = enabled
        viewBinding.repasswordEd.isEnabled = enabled
        viewBinding.btnCreateAccount.isEnabled = enabled
        viewBinding.loadingProgressBar.isVisible = !enabled
    }
}