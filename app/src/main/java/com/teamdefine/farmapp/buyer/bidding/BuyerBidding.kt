package com.teamdefine.farmapp.buyer.bidding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.teamdefine.farmapp.databinding.FragmentBuyerBiddingBinding

class BuyerBidding : Fragment() {

    private lateinit var binding: FragmentBuyerBiddingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentBuyerBiddingBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}