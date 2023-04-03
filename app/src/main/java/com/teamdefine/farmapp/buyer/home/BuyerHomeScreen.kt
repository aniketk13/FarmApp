package com.teamdefine.farmapp.buyer.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.databinding.FragmentBuyerHomeScreenBinding

class BuyerHomeScreen : Fragment() {

    private lateinit var viewModel: BuyerHomeScreenViewModel
    private lateinit var binding: FragmentBuyerHomeScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?=FragmentBuyerHomeScreenBinding.inflate(inflater,container,false).also {
        binding=it
        firebaseAuth=FirebaseAuth.getInstance()
        firebaseFirestore=FirebaseFirestore.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initObservers()
    }

    private fun initObservers() {

    }

    private fun initClickListeners() {
    }

}