package com.teamdefine.farmapp.buyer.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.databinding.FragmentBuyerHomeScreenBinding
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class BuyerHomeScreen : Fragment() {

    private val viewModel: BuyerHomeScreenViewModel by viewModels()
    private lateinit var binding: FragmentBuyerHomeScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var adapter: RecyclerView.Adapter<BuyerHomeScreenAdapter.ViewHolder>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentBuyerHomeScreenBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        Log.i("BuyerHomeScreen", "OnResume")
        initViews()
    }

    private fun initViews() {
        viewModel.getBuyerData(firebaseAuth, firebaseFirestore)
        viewModel.getFarmerCrops(firebaseAuth, firebaseFirestore)
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        viewModel.farmerCrops.observe(requireActivity()) {
            it?.let {
                setUpDataInRecyclerView(it)
            }
        }
        viewModel.buyerData.observe(requireActivity()) {
            it?.let {
                binding.nameTv.text = it.Name
                binding.activeBids.text = "${it.ActiveBids.toString()} Active Bids"
                binding.closedBids.text = "${it.ClosedBids.toString()} Closed Bids"
            }
        }
    }

    private fun setUpDataInRecyclerView(farmerCrops: ArrayList<FarmerCrops>) {
        adapter = activity?.let {
            BuyerHomeScreenAdapter(
                it,
                farmerCrops,
                object : BuyerHomeScreenAdapter.ItemClickListener {
                    override fun onItemClickListener(clickedFarmerCrop: FarmerCrops) {
                        toast("Item Clicked")
                    }
                })
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

    }

    private fun initClickListeners() {
    }

}