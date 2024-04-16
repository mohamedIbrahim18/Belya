package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
        val check = Common()
        if (!check.isConnectedToInternet(requireContext())){
            check.showInternetDisconnectedDialog(requireContext())
        }
        initViews()
    }

    private fun initViews() {
        initRecyclerPerson()
        categoryName = arguments?.getString("jobName") ?: ""
        fetchPersonFromDatabase(categoryName)
    }

    private fun fetchPersonFromDatabase(categoryName: String) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .whereEqualTo("job", categoryName)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                value?.let { snapshot ->
                    personList.clear()
                    for (document in snapshot.documents) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            personList.add(it)
                            personInCategoryAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
    }
    private fun initRecyclerPerson() {
        personInCategoryAdapter = PersonAdapter(personList)
        viewBinding.recyclerCategories.apply {
            viewBinding.recyclerCategories.addItemDecoration(HorizontalItemDecoration())
            adapter = personInCategoryAdapter

            personInCategoryAdapter.onItemSelectedClickListnner = object : PersonAdapter.OnItemSelectedClick{
                override fun onItemSelectedClick(position: Int, task: User) {
                    val bundle = Bundle()
                    bundle.putParcelable("pokemon",task)
                    findNavController().navigate(
                        R.id.action_personInCategoryFragment_to_technicianDetailsFragment,bundle) }
            }
        }
    }

}