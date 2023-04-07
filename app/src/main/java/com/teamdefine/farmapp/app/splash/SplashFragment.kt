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
import com.teamdefine.farmapp.buyer.MainBuyerActivity
import com.teamdefine.farmapp.databinding.FragmentSplashBinding
import com.teamdefine.farmapp.farmer.main.MainFarmerActivity

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    var mainActivityVM: MainActivityVM? = null
    var isUserFarmer = false
    var isUserBuyer = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            navigateToLanguageSelectionFragment()
//            navigateToOnBoardingFragment()
        }
    }

    private fun navigateToLanguageSelectionFragment() {
        Handler().postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLanguageSelectionFragment())
        }, 2000)
    }

    private fun initObservers() {
        mainActivityVM?.userIsFarmer?.observe(requireActivity(), Observer {
            it?.let { isFarmer ->
                if (isFarmer) {
                    isUserFarmer = true
                    getLanguagePreference(firebaseAuth, firebaseFirestore, "Farmers")
                } else {
                    isUserBuyer = true
                    getLanguagePreference(firebaseAuth, firebaseFirestore, "Buyers")
                }
            }
        })

        mainActivityVM?.userLanguagePref?.observe(requireActivity(), Observer { languagePref ->
            languagePref?.let {
                it.getContentIfNotHandled()?.let { lang ->
                    Handler().postDelayed({
                        if (isUserFarmer) {
                            startFarmerActivity(lang)
                        } else {
                            startBuyerActivity(lang)
                        }
                    }, 2000)
                }
            }
        })
    }

    private fun getLanguagePreference(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        userType: String
    ) {
        mainActivityVM?.getLanguagePreference(firebaseAuth, firebaseFirestore, userType)
    }

    private fun checkIfUserIsFarmerOrBuyer(
        firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore
    ) {
        mainActivityVM?.checkIfUserIsFarmerOrBuyer(firebaseAuth, firebaseFirestore)
    }

    private fun startBuyerActivity(languagePref: String?) {
        startActivity(
            Intent(activity, MainBuyerActivity::class.java).putExtra("isRegistered", true)
                .putExtra("languagePref", languagePref)
        )
        activity?.finish()
    }

    private fun startFarmerActivity(languagePref: String?) {
        startActivity(
            Intent(activity, MainFarmerActivity::class.java).putExtra("isRegistered", true)
                .putExtra("languagePref", languagePref)
        )
        activity?.finish()
    }

    private fun navigateToOnBoardingFragment() {
        Handler().postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment())
        }, 2000)
    }

    private fun checkUserExists(firebaseAuth: FirebaseAuth): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) return true
        return false
    }
}