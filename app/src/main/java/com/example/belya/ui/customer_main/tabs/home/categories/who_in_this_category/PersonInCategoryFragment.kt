package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentPersonInCategoryBinding
import com.example.belya.model.User
import com.example.belya.adapter.PersonAdapter
import com.example.belya.utils.base.Common
import com.google.firebase.firestore.FirebaseFirestore

class PersonInCategoryFragment : Fragment() {
    private lateinit var viewBinding: FragmentPersonInCategoryBinding
    private lateinit var personInCategoryAdapter: PersonAdapter

    private val personList = mutableListOf<User>()
    private var categoryName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentPersonInCategoryBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!Common().isConnectedToInternet(requireContext())) {
            Common().showInternetDisconnectedDialog(requireContext())
        }
        initViews()
    }

    private fun initViews() {
        categoryName = arguments?.getString("jobName") ?: ""
        initRecyclerPerson()
        fetchPersonFromDatabase(categoryName) // // Fetch persons initially
    viewBinding.imgSearch.setOnClickListener {
        val bundle = Bundle()
        bundle.putString("jobToSearchInCity",categoryName)
        findNavController().navigate(
            R.id.action_personInCategoryFragment_to_searchFragment,bundle)
    }
    }

    private fun initRecyclerPerson() {
        personInCategoryAdapter = PersonAdapter(personList)
        viewBinding.recyclerCategories.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = personInCategoryAdapter

            personInCategoryAdapter.onItemSelectedClickListnner = object : PersonAdapter.OnItemSelectedClick {
                override fun onItemSelectedClick(position: Int, task: User) {
                    navigateToTechnicianDetails(task)
                }
            }
        }
    }



    private fun fetchPersonFromDatabase(categoryName: String) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .whereEqualTo("job", categoryName)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FIRESTORE_ERROR", "Error fetching data: ${error.message}")
                    return@addSnapshotListener
                }
                value?.let { snapshot ->
                    val updatedPersonList = mutableListOf<User>()
                    for (document in snapshot.documents) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            Log.e("fetch Data", it.job!!)
                            updatedPersonList.add(it)
                        }
                    }
                    personInCategoryAdapter.newList(updatedPersonList)
                }
            }
    }



    private fun navigateToTechnicianDetails(user: User) {
        val bundle = Bundle().apply {
            putParcelable("importantPerson", user)
        }
        findNavController().navigate(R.id.action_personInCategoryFragment_to_technicianDetailsFragment, bundle)
    }

}
