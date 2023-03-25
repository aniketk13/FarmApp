package com.teamdefine.farmapp.app.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.MainActivity
import com.teamdefine.farmapp.MainActivityVM
import com.teamdefine.farmapp.databinding.FragmentSplashBinding
import com.teamdefine.farmapp.farmer.MainFarmerActivity

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    var mainActivityVM: MainActivityVM? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSplashBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        mainActivityVM = (activity as MainActivity).mainActivityVM
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        val currentUser = checkUserExists(firebaseAuth)
        if (currentUser) {
            checkIfUserIsFarmerOrBuyer(firebaseAuth, firebaseFirestore)
        } else {
            navigateToOnBoardingFragment()
        }
    }

    private fun initObservers() {
        mainActivityVM?.userIsFarmer?.observe(requireActivity(), Observer {
            it?.let { isFarmer ->
                Handler().postDelayed({
                    if (isFarmer) {
                        startFarmerActivity()
                    } else {
                        startBuyerActivity()
                    }
                }, 2000)
            }
        })
    }

    private fun checkIfUserIsFarmerOrBuyer(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ) {
        mainActivityVM?.checkIfUserIsFarmerOrBuyer(firebaseAuth, firebaseFirestore)
    }

    private fun startBuyerActivity() {
        startActivity(Intent(activity, MainFarmerActivity::class.java))
        activity?.finish()
    }

    private fun startFarmerActivity() {
        startActivity(Intent(activity, MainFarmerActivity::class.java))
        activity?.finish()
    }

    private fun navigateToOnBoardingFragment() {
        Handler().postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment())
        }, 2000)

    }

    private fun checkUserExists(firebaseAuth: FirebaseAuth): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null)
            return true
        return false
    }
}