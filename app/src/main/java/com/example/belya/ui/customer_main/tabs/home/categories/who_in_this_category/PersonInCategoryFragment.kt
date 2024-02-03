package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentPersonInCategoryBinding
import com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.adapter.PersonAdapter
import com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.adapter.PersonItem

class PersonInCategoryFragment : Fragment() {

lateinit var viewBinding : FragmentPersonInCategoryBinding
private lateinit var personList : List<PersonItem>
lateinit var personAdapter : PersonAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentPersonInCategoryBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {
        initRecyclerPerson()
    }

    private fun initRecyclerPerson() {
        personList = listOf(
            PersonItem(R.drawable.ic_test, "TAta", "Ngar"),
            PersonItem(R.drawable.ic_test_most, "MMMMM", "Ngar"),
            PersonItem(R.drawable.ic_test, "MMMMM", "Ngar"),
            PersonItem(R.drawable.ic_test_most, "MMMMM", "Ngar"),
            PersonItem(R.drawable.ic_test, "MMMMM", "Ngar"),
        )
        personAdapter = PersonAdapter(personList)
        viewBinding.recyclerCategories.apply {
            viewBinding.recyclerCategories.addItemDecoration(HorizontalItemDecoration())
            adapter = personAdapter

        }
    }

}