package com.teamdefine.farmapp.farmer.cropBids

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Utility.loadImageUsingGlide
import com.teamdefine.farmapp.buyer.bidding.models.BiddingData
import com.teamdefine.farmapp.databinding.FragmentFarmerCropBidsBinding
import com.teamdefine.farmapp.farmer.home.FarmerHomeScreenAdapter
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class FarmerCropBids : Fragment() {

    private val viewModel: FarmerCropBidsVM by viewModels()
    private lateinit var binding: FragmentFarmerCropBidsBinding
    private val args: FarmerCropBidsArgs by navArgs()
    private var cropId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private var adapter: RecyclerView.Adapter<FarmerCropBidsAdapter.ViewHolder>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerCropBidsBinding.inflate(inflater, container, false).also {
        binding = it
        args.cropId.let { cropId ->
            this.cropId = cropId
        }
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initClickListeners()
        initObservers()

    }

    private fun initViews() {
        getCrop()
        getBids()
    }

    private fun getBids() {
        viewModel.getBids(database,cropId)
    }

    private fun getCrop() {
        viewModel.getCrop(database,cropId)
    }

    private fun initObservers() {
        viewModel.crop.observe(requireActivity()){
            it?.let {
                showCropDetails(it)
            }
        }
        viewModel.cropBids.observe(requireActivity()){
            it?.let {
                setupDataInRecyclerView(it)
            }
        }
    }

    private fun setupDataInRecyclerView(cropBids: ArrayList<BiddingData>) {
        adapter = activity?.let {
            FarmerCropBidsAdapter(
                it,
                cropBids,
                object : FarmerCropBidsAdapter.ItemClickListener {
                    override fun onItemClickListener(clickedCropBid: BiddingData) {
                        Log.i("FarmerCropBids", clickedCropBid.CropId.toString())
                    }
                })
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
//        if (binding.swipeRefresh.isRefreshing)
//            binding.swipeRefresh.isRefreshing = false
    }

    private fun showCropDetails(crop: FarmerCrops) {
        binding.apply {
            cropNameTv.text = crop.CropName
            requireContext().loadImageUsingGlide(crop.CropImageURI.toString(), cropIv)
            farmerNameTv.text=crop.FarmerName
            dateTv.text=crop.CropListingDate
            priceOfCrop.text=crop.CropOfferPrice.toString()
        }
    }

    private fun initClickListeners() {
        TODO("Not yet implemented")
    }
}