package com.teamdefine.farmapp.farmer.crops

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentCreateNewCropListingBinding

class CreateNewCropListing : Fragment() {
    private lateinit var binding: FragmentCreateNewCropListingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentCreateNewCropListingBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
    }.root
}