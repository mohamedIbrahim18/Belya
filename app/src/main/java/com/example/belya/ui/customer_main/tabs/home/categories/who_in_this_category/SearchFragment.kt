package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.adapter.PersonAdapterByCity
import com.example.belya.api.ApiManger
import com.example.belya.api.modeApi.SearchRequestByCity
import com.example.belya.api.modeApi.UserApiModelItem
import com.example.belya.databinding.FragmentSearchBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var viewBinding: FragmentSearchBinding
    private val personInCity = mutableListOf<UserApiModelItem>()
    private lateinit var personInCategoryByCityAdapter: PersonAdapterByCity
    private var categoryName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSearchBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryName = arguments?.getString("jobToSearchInCity")!!

        initViews()
        viewBinding.imgSearchPerson.setOnClickListener {
            searchMeals()
        }
    }

    private fun initViews() {
        initRecyclerPersonByCity()
        initSearch()
        setOnPersonClickListener()
    }

    private fun setOnPersonClickListener() {
        personInCategoryByCityAdapter.onItemSelectedClickListnner = object : PersonAdapterByCity.OnItemSelectedClick {
            override fun onItemSelectedClick(position: Int, task: UserApiModelItem) {
                navigateToTechnicianDetails(task)
            }
        }
    }

    private fun navigateToTechnicianDetails(task: UserApiModelItem) {
        val bundle = Bundle().apply {
            putParcelable("importantMan", task)
        }
        findNavController().navigate(R.id.action_searchFragment_to_technicianDetailsFragment, bundle)
    }

    private fun initRecyclerPersonByCity() {
        personInCategoryByCityAdapter = PersonAdapterByCity(personInCity)
        viewBinding.recViewSearchPerson.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(HorizontalItemDecoration())
            adapter = personInCategoryByCityAdapter
        }
    }

    private fun initSearch() {
        var searchJob: Job? = null

        viewBinding.searchBox.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(500)
                searchOnText(it.toString())
            }
        }
    }

    private fun searchMeals() {
        val searchQuery = viewBinding.searchBox.text.toString()
        Log.d("Text", searchQuery)
        if (searchQuery.isNotEmpty()) {
            searchOnText(searchQuery)
        }
    }

    private fun searchOnText(newText: String?) {
        if (!newText.isNullOrBlank()) {
            newText.let { query ->
                lifecycleScope.launch {
                    try {
                        val response = ApiManger.getApis().searchUserByCity(SearchRequestByCity(city = query, categoryName))
                        if (response.isSuccessful) {
                            response.body()?.let { userList ->
                                updatePersonListByCity(userList)
                            }
                        } else {
                            Log.e("API_ERROR", "Failed to fetch data: ${response.message()}")
                        }
                    } catch (e: Exception) {
                        Log.e("API_FAILURE", "Failed to fetch data: ${e.message}")
                    }
                }
            }
        } else {
            // Fetch all users from the database when search query is blank or null or empty
        }
    }

    private fun updatePersonListByCity(userList: List<UserApiModelItem>) {
        personInCity.clear()
        personInCity.addAll(userList)
        personInCategoryByCityAdapter.notifyDataSetChanged()
    }
}
