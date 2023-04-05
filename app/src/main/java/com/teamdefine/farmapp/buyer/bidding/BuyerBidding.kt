package com.teamdefine.farmapp.buyer.bidding

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Utility.generateUUID
import com.teamdefine.farmapp.app.utils.Utility.loadImageUsingGlide
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.buyer.bidding.models.BiddingData
import com.teamdefine.farmapp.databinding.FragmentBuyerBiddingBinding
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class BuyerBidding : Fragment() {

    private lateinit var binding: FragmentBuyerBiddingBinding
    private var cropKey: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private val args: BuyerBiddingArgs by navArgs()
    private val viewModel: BuyerBiddingVM by viewModels()
    private var firstTime: Boolean? = null
    val bid: MutableMap<String, Any> = HashMap()
    private var currentCrop: FarmerCrops? = null
    private var buyerId: String? = null
    private var buyerName: String? = null
    private var bidId: String? = null
    private var farmerId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentBuyerBiddingBinding.inflate(inflater, container, false).also {
        binding = it
        cropKey = args.cropKey
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        buyerId = firebaseAuth.currentUser?.uid
        buyerName = firebaseAuth.currentUser?.displayName
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.submitButton.isClickable = false
        binding.submitButton.isActivated = false
        initViews()
        initClickListeners()
        initObservers()
    }


    private fun initViews() {
        Log.i("BuyerBidding", "Inside init views")
        cropKey?.let { cropId ->
            getCropDetails(cropId)
        }
    }

    private fun getCropDetails(cropId: String) {
        viewModel.getCrop(database, cropId)
    }

    @SuppressLint("SetTextI18n")
    private fun initObservers() {
        viewModel.firstTime.observe(requireActivity()) {
            it?.let {
                firstTime = it == true
                if (!it)
                    getBidDetails(cropKey!!, buyerId)
            }
        }
        viewModel.crop.observe(requireActivity()) {

            binding.submitButton.isClickable = true
            binding.submitButton.isActivated = true
            it?.let { crop ->
                currentCrop = crop
                farmerId = crop.FarmerUID
                val price = crop.CropOfferPrice
                binding.apply {
                    priceTv.text = "₹${price}/क्विंटल"
                    showCropDetails(crop)
                }
                checkIfBidExists(cropKey!!, buyerId)
            }
        }
        viewModel.bidAdded.observe(requireActivity()) {
            it?.let {
                if (it) {
                    toast("Bidding Done!")
                    navigateToHomeScreen()
                }
            }
        }
        viewModel.bidData.observe(requireActivity()) {
            it?.let {
                showBid(it)
            }
        }
        viewModel.bitUpdated.observe(requireActivity()) {
            it?.let {
                if (it) {
                    toast("Bidding Done!")
                    navigateToHomeScreen()
                }
            }
        }
    }

    private fun checkIfBidExists(cropKey: String, buyerId: String?) {
        viewModel.checkIfBidExists(database, cropKey, buyerId)
    }

    private fun getBidDetails(cropKey: String, buyerId: String?) {
        viewModel.getBid(database, cropKey, buyerId)
    }

    private fun showCropDetails(crop: FarmerCrops) {
        binding.apply {
            titleItemTv.text = crop.CropName
            requireContext().loadImageUsingGlide(crop.CropImageURI.toString(), mainIv)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showBid(bidData: BiddingData) {
        Log.i("BuyerBidding", "Inside Show Bid")
        bidId = bidData.BidId
        binding.apply {
            priceTv.text = "₹${bidData.FarmerBid}/क्विंटल"
            inputNewPrice.setText(bidData.BuyerBid.toString())
        }
    }

    private fun navigateToHomeScreen() {
        findNavController().navigate(BuyerBiddingDirections.actionBuyerBiddingToBuyerHomeScreen())
    }

    private fun initClickListeners() {
        binding.submitButton.setOnClickListener {
            val buyerBid = binding.inputNewPrice.text.toString()
            firebaseAuth.currentUser?.let {
                if (buyerBid != "" && firstTime == true)
                    addBidToDatabase(buyerBid.toLong())
                else if (buyerBid != "" && firstTime == false)
                    updateBid(buyerBid)
                else
                    toast("Enter a Bid First")
            }
        }
    }

    private fun updateBid(buyerBid: String) {
        viewModel.updateBid(database, buyerBid, bidId!!)
    }

    private fun addBidToDatabase(buyerBid: Long) {
        bidId = generateUUID()
        bid["BidId"] = bidId!!
        bid["BuyerId"] = buyerId!!
        bid["CropId"] = cropKey!!
        bid["FarmerBid"] = currentCrop?.CropOfferPrice!!
        bid["BuyerBid"] = buyerBid
        bid["BuyerName"] = buyerName!!
        updateBuyerActiveBids(buyerId!!)
        updateFarmerActiveDeals(farmerId!!)
        viewModel.addBidToDatabase(database, bid)
    }

    private fun updateFarmerActiveDeals(farmerId: String) {
        viewModel.updateFarmerActiveDeals(database, farmerId)
    }

    private fun updateBuyerActiveBids(buyerId: String) {
        viewModel.updateBuyerActiveBids(database, buyerId)
    }

}

