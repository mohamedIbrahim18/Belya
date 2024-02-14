package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.technician_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.databinding.FragmentTechnicianDeatilsBinding
import com.example.belya.model.userTechnician

class TechnicianDetailsFragment : Fragment() {
    lateinit var viewBinding: FragmentTechnicianDeatilsBinding
    lateinit var person: userTechnician
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentTechnicianDeatilsBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initViews()
        arguments?.let {
            it.getParcelable<userTechnician>("pokemon")?.let { task ->
                person = task
               // viewBinding.imagePersonDetails.setImageResource(task.imagePath!!)
                val name = person.firstName + " "+ person.lastName
                viewBinding.firstnamePersonDetails.text = name
                viewBinding.cityPersonDetails.text = person.city
                viewBinding.ratingbarPersonDetails.rating = person.person_rate.toFloat()
                // average price

                //

                }
            }
        viewBinding.progressBarPersonDeatails.visibility = View.GONE
        viewBinding.bookNowPersonDetails.setOnClickListener {
            checkPriceAndMakeAction()
        }
    }

    private fun checkPriceAndMakeAction() {
        viewBinding.bookNowPersonDetails.visibility = View.GONE
        viewBinding.progressBarPersonDeatails.visibility = View.VISIBLE
        if (viewBinding.edittextPersonDetails.text?.isNotEmpty() == true) {
            // hide error
            viewBinding.textinputLayoutPersonDetails.error = null
            // make a request to manage price
            // Assuming you have some method to handle the request here
        } else {
            // show error
            viewBinding.textinputLayoutPersonDetails.error = "Please Enter a price"
            viewBinding.bookNowPersonDetails.visibility = View.VISIBLE
            viewBinding.progressBarPersonDeatails.visibility = View.GONE
        }
    }

}