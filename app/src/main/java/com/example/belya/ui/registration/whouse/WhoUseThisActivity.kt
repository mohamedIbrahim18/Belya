package com.example.belya.ui.registration.whouse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.belya.databinding.ActivityWhoUseThisBinding
import com.example.belya.ui.Customer_Main.CustomerMainActivity
import com.example.belya.ui.registration.factorinfo.FactorInfoActivity


class WhoUseThisActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityWhoUseThisBinding
    var type :Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWhoUseThisBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {
        viewBinding.continuee.setOnClickListener(View.OnClickListener {
            //get selected radio button from radiogroup
            val buttonId: Int = viewBinding.typeContainer.checkedRadioButtonId
            if (buttonId == View.NO_ID) {
                Toast.makeText(this, "Please Select Choose...", Toast.LENGTH_SHORT).show()
            } else {
                //TODO
                val selectedRadioButton = findViewById<RadioButton>(buttonId)
                Toast.makeText(this,"Wellcome bro..." + selectedRadioButton.text,Toast.LENGTH_SHORT).show()
                if (selectedRadioButton.text.equals("Customer")){
                    type =0
                    navigateToCustomerPage()
                } else if(selectedRadioButton.text.equals("Factor")){
                    type =1;
                    navigateToFactorDetails()
                }
            }
        })
    }

    private fun navigateToFactorDetails() {
        val intent = Intent(this,FactorInfoActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCustomerPage() {
        val intent = Intent(this,CustomerMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}