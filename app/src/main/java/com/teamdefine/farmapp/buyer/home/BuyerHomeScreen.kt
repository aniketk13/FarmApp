package com.teamdefine.farmapp.buyer.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
            it?.let { buyerData ->
                binding.apply {
                    nameTv.text = "Hello, ${buyerData.Name}"
                    activeBidsCountTv.text = buyerData.ActiveBids.toString()
                    closedBidsCountTv.text = buyerData.ClosedBids.toString()
                }
            }
        }
    }

    private fun setUpDataInRecyclerView(farmerCrops: ArrayList<Map<String, FarmerCrops>>) {
        adapter = activity?.let {
            BuyerHomeScreenAdapter(
                it,
                farmerCrops,
                object : BuyerHomeScreenAdapter.ItemClickListener {
                    override fun onItemClickListener(clickedCropKey: String) {
                        toast("Item Clicked")
                        navigateToBiddingScreen(clickedCropKey)
                    }
                })
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        if (binding.swipeRefresh.isRefreshing)
            binding.swipeRefresh.isRefreshing = false
    }

    private fun navigateToBiddingScreen(clickedCropKey: String) {
        findNavController().navigate(
            BuyerHomeScreenDirections.actionBuyerHomeScreenToBuyerBidding(
                clickedCropKey
            )
        )
    }

    private fun initClickListeners() {
        binding.swipeRefresh.setOnRefreshListener {
            getBuyerData()
        }
    }

    private fun getBuyerData() {
        viewModel.getBuyerData(firebaseAuth, firebaseFirestore)
        viewModel.getFarmerCrops(firebaseAuth, firebaseFirestore)
    }

}