package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentPersonInCategoryBinding
import com.example.belya.model.User
import com.example.belya.adapter.PersonAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonInCategoryFragment : Fragment() {

    lateinit var viewBinding: FragmentPersonInCategoryBinding
    lateinit var personInCategoryAdapter: PersonAdapter
    private lateinit var personList: MutableList<User>
    lateinit var newList: MutableList<User>
    var categoryName: String = ""

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentPersonInCategoryBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        // Initialize personList and newList
        personList = mutableListOf()
        newList = mutableListOf()

        // Initialize RecyclerView and fetch data
        initRecyclerPerson()
        categoryName = arguments?.getString("jobName") ?: ""
        fetchPersonFromDatabase(categoryName)
    }

    private fun fetchPersonFromDatabase(categoryName: String) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .whereEqualTo("job", categoryName).addSnapshotListener { value, error ->
                if (error != null) {
                    // handle the error
                    Log.e("Error Fetch category", "Error fetching data", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    // Clear the existing list before adding new data
                    newList.clear()
                    for (document in value.documents) {
                        val result = document.toObject(User::class.java)
                        result?.let {
                            newList.add(it)
                        }
                    }
                    // Update the adapter with the new list
                    personInCategoryAdapter.newList(newList)
                }
            }
    }

    private fun initRecyclerPerson() {
        // Initialize personAdapter with personList
        personInCategoryAdapter = PersonAdapter(personList)
        viewBinding.recyclerCategories.apply {
            viewBinding.recyclerCategories.addItemDecoration(HorizontalItemDecoration())
            adapter = personInCategoryAdapter

            personInCategoryAdapter.onItemSelectedClickListnner = object : PersonAdapter.OnItemSelectedClick{
                override fun onItemSelectedClick(position: Int, task: User) {
                    //showPersonDetails(task)
                    val bundle = Bundle()
                    bundle.putParcelable("pokemon",task)
                    findNavController().navigate(
                        R.id.action_personInCategoryFragment_to_technicianDetailsFragment,bundle) }
            }
        }
    }

}