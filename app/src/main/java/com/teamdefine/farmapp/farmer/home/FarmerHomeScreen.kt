package com.teamdefine.farmapp.farmer.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.MainActivity
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.databinding.FragmentFarmerHomeScreenBinding
import com.teamdefine.farmapp.farmer.main.MainFarmerActivity
import com.teamdefine.farmapp.farmer.main.MainFarmerVM
import com.teamdefine.farmapp.farmer.models.FarmerCrops
import com.teamdefine.farmapp.farmer.models.FarmerData

class FarmerHomeScreen : Fragment() {
    private lateinit var binding: FragmentFarmerHomeScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var mainFarmerVM: MainFarmerVM
    private var adapter: RecyclerView.Adapter<FarmerHomeScreenAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerHomeScreenBinding.inflate(inflater, container, false).also {
        Log.i("FragmentHomeScreen", "OnCreateView")
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        mainFarmerVM = (activity as MainFarmerActivity).mainFarmerVM
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("FragmentHomeScreen", "OnViewCreated")
        initClickListeners()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        Log.i("FragmentHomeScreen", "OnResume")
        initViews()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FragmentHomeScreen", "OnCreateView")
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("FragmentHomeScreen", "OnAttach")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("FragmentHomeScreen", "OnDetach")
    }

    override fun onStart() {
        super.onStart()
        Log.i("FragmentHomeScreen", "OnStart")
    }

    override fun onStop() {
        super.onStop()
        Log.i("FragmentHomeScreen", "OnStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("FragmentHomeScreen", "OnDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("FragmentHomeScreen", "OnDestroyView")
    }

    private fun initObservers() {
        mainFarmerVM.farmerData.observe(requireActivity(), Observer {
            it?.let { farmersData ->
                setUpDataInViews(farmersData)
            }
        })
        mainFarmerVM.farmerCrops.observe(requireActivity(), Observer { farmerCrops ->
            farmerCrops?.let {
                setupDataInRecyclerView(it)
            }
        })
    }

    private fun setupDataInRecyclerView(farmerCrops: ArrayList<FarmerCrops>) {
        adapter = activity?.let {
            FarmerHomeScreenAdapter(
                it,
                farmerCrops,
                object : FarmerHomeScreenAdapter.ItemClickListener {
                    override fun onItemClickListener(clickedFarmerCrop: FarmerCrops) {
                    }
                })
        }
        binding.cropRecyclerView.adapter = adapter
        binding.cropRecyclerView.layoutManager = LinearLayoutManager(activity)
        if (binding.swipeRefresh.isRefreshing)
            binding.swipeRefresh.isRefreshing = false
    }

    private fun setUpDataInViews(farmersData: FarmerData? = null) {
        farmersData?.let {
            binding.apply {
                nameTv.text = farmersData.Name
                closedDealsCountTv.text = farmersData.ClosedDeals.toString()
                activeDealsCountTv.text = farmersData.ActiveDeals.toString()
            }
        }
    }

    private fun initClickListeners() {
        binding.apply {
            addButton.setOnClickListener {
                navigateToAddCropListingFragment()
            }
            swipeRefresh.setOnRefreshListener {
                getFarmerData()
            }
            signOut.setOnClickListener {
                signOutUser()
            }
        }
    }

    private fun signOutUser() {
        showAlert()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Do you really want to sign out?")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            firebaseAuth.signOut()
            toast("Signed out successfully")
            navigateToMainActivity()
        }
        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        activity?.finish()
    }


    private fun navigateToAddCropListingFragment() {
        findNavController().navigate(FarmerHomeScreenDirections.actionFarmerHomeScreenToCreateNewCropListing())
    }

    private fun initViews() {
        getFarmerData()
    }

    private fun getFarmerData() {
        mainFarmerVM.getFarmerData(firebaseAuth, firebaseFirestore)
        mainFarmerVM.getFarmerCrops(firebaseAuth, firebaseFirestore)
    }
}