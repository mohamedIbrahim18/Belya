package com.example.belya.ui.technician_main.tabs.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.R
import com.example.belya.databinding.FragmentSpecificChatBinding
import com.example.belya.model.User
import com.google.android.material.snackbar.Snackbar


class SpecificChatFragment : Fragment() {
    private lateinit var viewBinding : FragmentSpecificChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding =
            FragmentSpecificChatBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.let {
            it?.getParcelable<User>("CHAT_DATA").let {user->
                if (user!=null) {
                    Snackbar.make(viewBinding.root, "${user.phoneNumber}",Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

}