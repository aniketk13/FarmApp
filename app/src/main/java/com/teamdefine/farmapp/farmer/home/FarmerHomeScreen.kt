package com.teamdefine.farmapp.farmer.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentFarmerHomeScreenBinding
import com.teamdefine.farmapp.farmer.main.MainFarmerActivity
import com.teamdefine.farmapp.farmer.main.MainFarmerVM
import com.teamdefine.farmapp.farmer.models.FarmerData

class FarmerHomeScreen : Fragment() {
    private lateinit var binding: FragmentFarmerHomeScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var mainFarmerVM: MainFarmerVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerHomeScreenBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        mainFarmerVM = (activity as MainFarmerActivity).mainFarmerVM
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initClickListeners()
        initObservers()
    }

    private fun initObservers() {
        mainFarmerVM.farmerData.observe(requireActivity(), Observer {
            it?.let { farmersData ->
                setUpDataInViews(farmersData)
            }
        })
    }

    private fun setUpDataInViews(farmersData: FarmerData? = null) {
        farmersData?.let {
            binding.apply {
                nameTv.text = farmersData.Name
                closedBidsCountTv.text = farmersData.ClosedDeals.toString()
                activeBidsCountTv.text = farmersData.ActiveDeals.toString()
            }
        }
    }

    private fun initClickListeners() {
        binding.apply {
            addButton.setOnClickListener {

            }
        }
    }

    private fun initViews() {
        getFarmerData()
    }

    private fun getFarmerData() {
        mainFarmerVM.getFarmerData(firebaseAuth, firebaseFirestore)
    }
}