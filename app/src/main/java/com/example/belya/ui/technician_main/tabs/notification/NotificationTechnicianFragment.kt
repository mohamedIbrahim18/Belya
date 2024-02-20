package com.example.belya.ui.technician_main.tabs.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentNotificationTechnicianBinding
import com.example.belya.model.RequestsItem
import com.example.belya.model.User
import com.example.belya.utils.RequestsAdapter

class NotificationTechnicianFragment : Fragment() {
    lateinit var viewBinding: FragmentNotificationTechnicianBinding
    lateinit var requestsAdapter: RequestsAdapter
    lateinit var listRequests: MutableList<RequestsItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentNotificationTechnicianBinding.inflate(layoutInflater)
        return viewBinding.root
       // initViews()
    }
/*
    private fun initViews() {
        listRequests = listOf(
            RequestsItem(
                User("ahmed", "lala", "sdasdasdasdsa", "", "", ""), "250", false
            ),
                    RequestsItem(
                        User("ahmed", "lala", "sdasdasdasdsa", null, "", "cas","","",0.,), "250", false
                        ""
        ),
            RequestsItem(
                User("ahmed", "lala", "sdasdasdasdsa", null, "", ""), "250", false
            ),
            RequestsItem(
                User("ahmed", "lala", "sdasdasdasdsa", null, "", ""), "250", false
            )
        ).toMutableList()
        requestsAdapter = RequestsAdapter(listRequests)
        viewBinding.recyclerViewRequests.apply {
            viewBinding.recyclerViewRequests.addItemDecoration(HorizontalItemDecoration())
            adapter = requestsAdapter
        }
    }
*/
}