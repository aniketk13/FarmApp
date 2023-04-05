package com.teamdefine.farmapp.farmer.cropBids

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentFarmerCropBidsBinding

class FarmerCropBids : Fragment() {

    private val viewModel: FarmerCropBidsVM by viewModels()
    private lateinit var binding: FragmentFarmerCropBidsBinding
    private val args: FarmerCropBidsArgs by navArgs()
    private var cropId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
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
        showCropDetails()
    }

    private fun showCropDetails() {
        viewModel.getCrop(database,cropId)
    }

    private fun initObservers() {
        TODO("Not yet implemented")
    }

    private fun initClickListeners() {
        TODO("Not yet implemented")
    }
}