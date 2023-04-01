package com.teamdefine.farmapp.buyer.registration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamdefine.farmapp.R

class BuyerRegistration : Fragment() {

    companion object {
        fun newInstance() = BuyerRegistration()
    }

    private lateinit var viewModel: BuyerRegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buyer_registration, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BuyerRegistrationVM::class.java)
        // TODO: Use the ViewModel
    }

}