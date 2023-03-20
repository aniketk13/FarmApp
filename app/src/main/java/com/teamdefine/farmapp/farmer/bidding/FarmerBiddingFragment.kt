package com.teamdefine.farmapp.farmer.bidding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.teamdefine.farmapp.databinding.FragmentFarmerBiddingBinding

class FarmerBiddingFragment : Fragment() {
    private val viewModel: FarmerBiddingViewModel by viewModels()
    private lateinit var binding: FragmentFarmerBiddingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val args:FarmerBiddingFragmentArgs by navArgs()
    private var adapter: RecyclerView.Adapter<FarmerBiddingAdapter.ViewHolder>? = null //adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerBiddingBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        adapter = FarmerBiddingAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    }
}