package com.example.belya.ui.technician_main.tabs.account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.adapter.AcceptedTicketsAdapter
import com.example.belya.databinding.FragmentAcceptedTicketsBinding
import com.example.belya.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AcceptedTicketsFragment : Fragment() {
    lateinit var viewBinding: FragmentAcceptedTicketsBinding
    lateinit var acceptedAdapter: AcceptedTicketsAdapter
    lateinit var listOfAcceptedTickets: MutableList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentAcceptedTicketsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        listOfAcceptedTickets = mutableListOf()
        initRecyclerAcceptedList()
        fetchAcceptedTickets()


    }

    private fun fetchAcceptedTickets() {
        val currentUID = FirebaseAuth.getInstance().uid
        val firestore = FirebaseFirestore.getInstance()
        currentUID ?: return

        // Fetch the acceptedList from the current user's document in the "users" collection
        firestore.collection(Constant.USER).document(currentUID)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                documentSnapshot?.let { snapshot ->
                    val acceptedList = snapshot.get("acceptedList") as? List<String>
                    acceptedList?.forEach { userID ->
                        firestore.collection(Constant.USER).document(userID)
                            .get()
                            .addOnSuccessListener { userDocumentSnapshot ->
                                val user = userDocumentSnapshot.toObject(User::class.java)
                                user?.let { user ->
                                    getPriceFromTicketsWithCustomerID(userID, currentUID, user)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "AcceptedTicketsFragment",
                                    "Error fetching user details: ${e.message}",
                                    e
                                )
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AcceptedTicketsFragment", "Error fetching accepted list: ${e.message}", e)
            }
    }

    private fun getPriceFromTicketsWithCustomerID(userID: String, currentUID: String, user: User) {
        FirebaseFirestore.getInstance()
            .collection(Constant.USER)
            .document(currentUID)
            .collection("Tickets")
            .document(userID)
            .get()
            .addOnSuccessListener { ticketDocumentSnapshot ->
                // Extract the user object from ticket document
                val ticketUser = ticketDocumentSnapshot.get("user") as? Map<*, *>
                ticketUser?.let { ticketUserMap ->
                    val price = ticketUserMap["price"] as? String
                    user.price = price ?: "0"
                    listOfAcceptedTickets.add(user)
                    acceptedAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AcceptedTicketsFragment", "Error fetching ticket details: ${e.message}", e)
            }
    }

    private fun initRecyclerAcceptedList() {
        acceptedAdapter = AcceptedTicketsAdapter(listOfAcceptedTickets)
        viewBinding.recyclerAcceptedTickets.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = acceptedAdapter

            acceptedAdapter.onItemSelectedClickListener =
                object : AcceptedTicketsAdapter.OnItemSelectedClick {
                    override fun onItemSelectedClick(position: Int, task: User) {
                        finishTicket(task)
                        Toast.makeText(
                            requireContext(),
                            "Thank you for finishing the job",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun finishTicket(task: User) {
        val currentUserId = FirebaseAuth.getInstance().uid
        if (currentUserId != null) {
            val fireStore = FirebaseFirestore.getInstance()
            val techRef = fireStore.collection(Constant.USER).document(currentUserId)

            techRef.update("acceptedList", FieldValue.arrayRemove(task.userID))
                .addOnSuccessListener {
                    val ticketRef = techRef.collection("Tickets").document(task.userID)
                    ticketRef.delete()
                        .addOnSuccessListener {
                            listOfAcceptedTickets.remove(task)
                            acceptedAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "AcceptedTicketsFragment",
                                "Failed to delete ticket: ${e.message}",
                                e
                            )
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(
                        "AcceptedTicketsFragment",
                        "Failed to update accepted list: ${e.message}",
                        e
                    )
                }
        }
    }
}
