package com.example.belya.ui.customer_main.tabs.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.belya.Constent
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentHomeCustomerBinding
import com.example.belya.ui.customer_main.tabs.home.categories.categories_adapter.CategoriesAdapter
import com.example.belya.ui.customer_main.tabs.home.categories.categories_adapter.CategoriesItem
import com.example.belya.ui.customer_main.tabs.home.important.important_adapter.ImportantAdapter
import com.example.belya.ui.customer_main.tabs.home.important.important_adapter.ImportantItem
import com.google.firebase.firestore.FirebaseFirestore

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

    private fun initRecyclerCategories() {
        categoryItemList = listOf(
            CategoriesItem(R.drawable.ic_plumber, "Plumping", "0"),
            CategoriesItem(R.drawable.ic_pharmacist, "Pharmacy", "0"),
            CategoriesItem(R.drawable.ic_mechanic, "Mechanics", "0"),
            CategoriesItem(R.drawable.ic_electrician, "Electricity", "0"),
            CategoriesItem(R.drawable.ic_ngar, "Carpentry", "0"),
        )
        categoriesItemAdpater = CategoriesAdapter(categoryItemList)
        viewBinding.recyclerCategories.apply {
            viewBinding.recyclerCategories.addItemDecoration(HorizontalItemDecoration())
            adapter = categoriesItemAdpater
            categoriesItemAdpater.onItemSelectedClickListnner =
                object : CategoriesAdapter.OnItemSelectedClick {
                    override fun onItemSelectedClick(position: Int, task: CategoriesItem) {
                        showWhoInThisCategory(task.nameOfJob)
                    }
                }
        }

        // Fetch the number of jobs for each category
        categoryItemList.forEach { category ->
            fetchNumberOfCategory(category.nameOfJob)
        }
    }


    private fun initRecyclerImportant() {
        importantItemList = listOf(
            ImportantItem(R.drawable.ic_plumber, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_plumber, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_plumber, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_plumber, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_plumber, "TSADASDAS", "30"),
            ImportantItem(R.drawable.ic_plumber, "TSADASDAS", "30"),
        )
        importantAdapter = ImportantAdapter(importantItemList)
        viewBinding.recyclerMostImportant.apply {
            viewBinding.recyclerMostImportant.addItemDecoration(HorizontalItemDecoration())
            adapter = importantAdapter
        }
    }


    private fun showWhoInThisCategory(nameOfJob: String) {
//            findNavController().navigate(R.id.action_homeCustomerFragment_to_personInCategoryFragment)
        val action = HomeCustomerFragmentDirections.actionHomeCustomerFragmentToPersonInCategoryFragment(nameOfJob)
        view?.findNavController()?.navigate(action)
    }

    private fun fetchNumberOfCategory(countOfJob: String) {
        val docRef = FirebaseFirestore.getInstance().collection(Constent.USER_TECHNICIAN_COLLECTION)
            .whereEqualTo("job", countOfJob)
        docRef.addSnapshotListener { value, error ->
            if (error != null) {
                // handle the error
                Log.e("TESTFUN", "Error fetching data", error)
                return@addSnapshotListener
            }
            if (value !=null){
                val count = value.size()
                updateCategoryCount(countOfJob,count)
            }

        }
    }

    private fun updateCategoryCount(countOfJob: String, count: Int) {
        val index = categoryItemList.indexOfFirst { it.nameOfJob == countOfJob }
        if (index != -1) {
            categoryItemList[index].countOfJob = count.toString()
            categoriesItemAdpater.notifyItemChanged(index)
        }
    }




}