package com.example.belya.ui.customer_main.tabs.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentHomeCustomerBinding
import com.example.belya.adapter.CategoriesAdapter
import com.example.belya.model.CategoriesItem
import com.example.belya.adapter.ImportantAdapter
import com.example.belya.api.modeApi.UserApiModelItem
import com.example.belya.utils.base.Common
import com.example.foodapplication.retrofit.api.ApiManger
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeCustomerFragment : Fragment() {
    lateinit var viewBinding: FragmentHomeCustomerBinding
    private lateinit var categoryItemList: List<CategoriesItem>
    lateinit var categoriesItemAdpater: CategoriesAdapter

    private lateinit var importantItemList: List<UserApiModelItem>
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
        val check = Common()
        if (!check.isConnectedToInternet(requireContext())){
            check.showInternetDisconnectedDialog(requireContext())
        }
        initViews()

    }

    private fun initViews() {
        initRecyclerCategories()
        initRecyclerImportant()
    }

    private fun initRecyclerCategories() {
        categoryItemList = listOf(
            CategoriesItem(R.drawable.ic_plumber, "Plumbing", "0"),
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
        // Initialize an empty list for the important items
        importantItemList = mutableListOf()

        // Initialize the adapter with the empty list
        importantAdapter = ImportantAdapter(importantItemList)

        // Set up the recycler view with the adapter
        viewBinding.recyclerMostImportant.apply {
            // Add item decoration if needed
            addItemDecoration(HorizontalItemDecoration())
            adapter = importantAdapter
        }
        fetchDataAndUpdateList()
    }



    private fun fetchDataAndUpdateList() {
        lifecycleScope.launch {
            try {
                val apiService = ApiManger.getApis()
                val response = withContext(Dispatchers.IO) {
                    apiService.getPersonFromRate()
                }
                if (response.isSuccessful) {
                    val userApiModel = response.body()
                    userApiModel?.let {
                        val users = it.userApiModel ?: emptyList() // Handling null userApiModel
                        importantAdapter.submitList(users)
                    }
                } else {
                    // Handle error response
                    Log.e("API_ERROR", "Failed to fetch data: ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle failure
                Log.e("API_FAILURE", "Failed to fetch data: ${e.message}")
            }
        }
    }



    private fun showWhoInThisCategory(nameOfJob: String) {
        val action = HomeCustomerFragmentDirections.actionHomeCustomerFragmentToPersonInCategoryFragment(nameOfJob)
        view?.findNavController()?.navigate(action)
    }

    private fun fetchNumberOfCategory(countOfJob: String) {
        val docRef = FirebaseFirestore.getInstance().collection(Constant.USER)
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