package com.teamdefine.farmapp.buyer.bidding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.teamdefine.farmapp.databinding.FragmentBuyerBiddingBinding

class BuyerBidding : Fragment() {

    private lateinit var binding: FragmentBuyerBiddingBinding
    private var cropKey:String?=null
    private val args:BuyerBiddingArgs by navArgs()
    private val viewModel:BuyerBiddingVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentBuyerBiddingBinding.inflate(inflater, container, false).also {
        binding = it
        cropKey=args.cropKey
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        initViews()
    }

    private fun initViews() {
        cropKey?.let {
            viewModel.getCropData(it)
        }
    }

    private fun initObservers() {
    }

    private fun initClickListeners() {
    }

}

