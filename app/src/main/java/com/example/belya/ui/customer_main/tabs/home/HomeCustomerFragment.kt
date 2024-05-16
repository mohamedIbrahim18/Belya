package com.example.belya.ui.customer_main.tabs.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
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
import com.example.belya.api.ApiManger
import com.example.belya.api.modeApi.SearchRequestByJob
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

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
        if (!check.isConnectedToInternet(requireContext())) {
            check.showInternetDisconnectedDialog(requireContext())
        }
        initViews()

    }

    private fun initViews() {
        initRecyclerCategories()
        initRecyclerImportant()
        initSearch()
    }

    private fun initSearch() {
        viewBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchOnText(newText)
                return true
            }
        })
    }

    private fun searchOnText(newText: String?) {
        if (newText.isNullOrBlank() || newText.isNullOrEmpty()){
            fetchDataAndUpdateList()
        }
        else {
            newText.let { query ->
                if (query.isNotBlank()) {
                    lifecycleScope.launch {
                        try {
                            val response = ApiManger.getApis().searchUsersByJob(SearchRequestByJob(query))
                            if (response.isSuccessful) {
                                val usersInTheSameJobList = response.body()
                                usersInTheSameJobList?.let { userList ->
                                    importantAdapter.submitList(userList)
                                }
                            } else {
                                Log.e("API_ERROR", "Failed to fetch data: ${response.message()}")
                            }
                        } catch (e: Exception) {
                            Log.e("API_FAILURE", "Failed to fetch data: ${e.message}")
                        }
                    }
                }
            }

        }
    }



    private fun initRecyclerCategories() {
        categoryItemList = listOf(
            CategoriesItem(R.drawable.ic_plumber, "Plumber", "0"),
            CategoriesItem(R.drawable.ic_pharmacist, "Pharmacist", "0"),
            CategoriesItem(R.drawable.ic_mechanic, "Mechanic", "0"),
            CategoriesItem(R.drawable.ic_electrician, "Electrician", "0"),
            CategoriesItem(R.drawable.ic_ngar, "Carpenter", "0"),
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
        importantItemList = mutableListOf()
        importantAdapter = ImportantAdapter(importantItemList)
        viewBinding.recyclerMostImportant.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = importantAdapter
            importantAdapter.onItemSelectedClickListnner = object : ImportantAdapter.OnItemSelectedClick{
                override fun onItemSelectedClick(position: Int, task: UserApiModelItem) {
                    val bundle = Bundle()
                    bundle.putParcelable("importantMan",task)
                    findNavController().navigate(
                        R.id.action_homeCustomerFragment_to_technicianDetailsFragment,bundle)
                }

            }
        }
        fetchDataAndUpdateList()
    }


    private fun fetchDataAndUpdateList() {
        lifecycleScope.launch {
            try {
                val response = ApiManger.getApis().getPersonFromRate()
                if (response.isSuccessful) {
                    val userApiModelList = response.body()
                    userApiModelList?.let { userList ->
                        importantAdapter.submitList(userList)
                    }
                } else {
                    Log.e("API_ERROR", "Failed to fetch data: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("API_FAILURE", "Failed to fetch data: ${e.message}")
            }
        }
    }

    private fun showWhoInThisCategory(nameOfJob: String) {
        val action =
            HomeCustomerFragmentDirections.actionHomeCustomerFragmentToPersonInCategoryFragment(
                nameOfJob
            )
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
            if (value != null) {
                val count = value.size()
                updateCategoryCount(countOfJob, count)
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