package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category

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
import com.example.belya.databinding.FragmentPersonInCategoryBinding
import com.example.belya.model.userTechnician
import com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.who_in_this_category_adapter.PersonAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonInCategoryFragment : Fragment() {

    lateinit var viewBinding: FragmentPersonInCategoryBinding
    lateinit var personAdapter: PersonAdapter
    private lateinit var personList: MutableList<userTechnician>
    lateinit var newList: MutableList<userTechnician>
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
        Snackbar.make(viewBinding.root, "Test : $categoryName", Snackbar.LENGTH_LONG).show()
        fetchPersonFromDatabase(categoryName)
    }

    private fun fetchPersonFromDatabase(categoryName: String) {
        FirebaseFirestore.getInstance().collection(Constent.USER_TECHNICIAN_COLLECTION)
            .whereEqualTo("job", categoryName).addSnapshotListener { value, error ->
                if (error != null) {
                    // handle the error
                    Log.e("TESTFUN", "Error fetching data", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    // Clear the existing list before adding new data
                    newList.clear()
                    for (document in value.documents) {
                        val result = document.toObject(userTechnician::class.java)
                        result?.let {
                            Log.d("DATA", result.toString())
                            newList.add(it)
                        }
                    }
                    Log.d("DATA", "Number of documents: ${value.documents.size}")
                    // Update the adapter with the new list
                    personAdapter.newList(newList)
                }
            }
    }

    private fun initRecyclerPerson() {
        // Initialize personAdapter with personList
        personAdapter = PersonAdapter(personList)
        viewBinding.recyclerCategories.apply {
            viewBinding.recyclerCategories.addItemDecoration(HorizontalItemDecoration())
            adapter = personAdapter

            personAdapter.onItemSelectedClickListnner = object : PersonAdapter.OnItemSelectedClick{
                override fun onItemSelectedClick(position: Int, task: userTechnician) {
                    //showPersonDetails(task)
                    val bundle = Bundle()
                    bundle.putParcelable("pokemon",task)
                    findNavController().navigate(
                        R.id.action_personInCategoryFragment_to_technicianDetailsFragment,bundle) }

            }
        }
    }

    private fun showPersonDetails(task: userTechnician) {
        val action = PersonInCategoryFragmentDirections.actionPersonInCategoryFragmentToTechnicianDetailsFragment(task.email?:"")
        view?.findNavController()?.navigate(action)
    }
}