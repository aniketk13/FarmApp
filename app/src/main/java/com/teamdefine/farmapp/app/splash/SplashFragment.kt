package com.teamdefine.farmapp.app.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentSplashBinding
import com.teamdefine.farmapp.farmer.MainFarmerActivity

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSplashBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = checkUserExists(firebaseAuth)
        if (currentUser) {
            checkIfUserIsFarmerOrBuyer()
        } else {
            navigateToUserAuthenticationFragment()
        }
    }

    private fun checkIfUserIsFarmerOrBuyer() {
        Handler().postDelayed({
            view?.post {
                val currentUserUID = firebaseAuth.currentUser?.uid
                val user = FirebaseFirestore.getInstance().collection("Farmers")
                    .document(currentUserUID.toString())

                user.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val doc = task.result
                        if (doc != null) {
                            Log.d("SplashFragment User", "Document already exists.")
                            if (doc.exists()) {
                                startFarmerActivity()
                            } else {
                                startBuyerActivity()
                            }
                        } else {
                            Log.e("SplashFragment User", "Error: ", task.exception)
                        }
                    }
                }
            }
        }, 1000)
    }

    private fun startBuyerActivity() {
        startActivity(Intent(activity, MainFarmerActivity::class.java))
    }

    private fun startFarmerActivity() {
        startActivity(Intent(activity, MainFarmerActivity::class.java))
    }

    private fun navigateToUserAuthenticationFragment() {
    }

    fun checkUserExists(firebaseAuth: FirebaseAuth): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null)
            return true
        return false
    }
}