package com.example.belya.ui.customer_main.tabs.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentHomeCustomerBinding
import com.example.belya.ui.customer_main.tabs.home.categories.adapter.CategoriesAdapter
import com.example.belya.ui.customer_main.tabs.home.categories.adapter.CategoriesItem
import com.example.belya.ui.customer_main.tabs.home.importantAdapter.ImportantAdapter
import com.example.belya.ui.customer_main.tabs.home.importantAdapter.ImportantItem
import com.google.android.material.snackbar.Snackbar

class HomeCustomerFragment : Fragment() {
    lateinit var viewBinding: FragmentHomeCustomerBinding
    private lateinit var categoryItemList: List<CategoriesItem>
    lateinit var categoriesItemAdpater: CategoriesAdapter

    private lateinit var importantItemList: List<ImportantItem>
    lateinit var importantAdapter: ImportantAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentHomeCustomerBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()


    }

    private fun initViews() {
        initRecyclerCategories()
        initRecyclerImportant()
    }

    private fun initRecyclerImportant() {
        importantItemList = listOf(
            ImportantItem(R.drawable.ic_test, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_test, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_test, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_test, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_test, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_test, "TSADASDAS", "30"),
        )
        importantAdapter = ImportantAdapter(importantItemList)
        viewBinding.recyclerMostImportant.apply {
            viewBinding.recyclerMostImportant.addItemDecoration(HorizontalItemDecoration())
            adapter = importantAdapter
        }
    }

    private fun initRecyclerCategories() {
        categoryItemList = listOf(
            CategoriesItem(R.drawable.ic_test, "TAta", "30"),
            CategoriesItem(R.drawable.ic_test_most, "MMMMM", "30"),
            CategoriesItem(R.drawable.ic_test, "MMMMM", "30"),
            CategoriesItem(R.drawable.ic_test_most, "MMMMM", "30"),
            CategoriesItem(R.drawable.ic_test, "MMMMM", "30"),
        )
        categoriesItemAdpater = CategoriesAdapter(categoryItemList)
        viewBinding.recyclerCategories.apply {
            viewBinding.recyclerCategories.addItemDecoration(HorizontalItemDecoration())
            adapter = categoriesItemAdpater
            categoriesItemAdpater.onItemSelectedClickListnner = object : CategoriesAdapter.OnItemSelectedClick{
                override fun onItemSelectedClick(position: Int, task: CategoriesItem) {
                    showWhoInThisCategory(position)
                }

            }
        }
    }

    private fun showWhoInThisCategory(position:Int) {
            findNavController().navigate(R.id.action_homeCustomerFragment_to_personInCategoryFragment)

    }


}