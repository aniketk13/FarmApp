package com.teamdefine.farmapp.app.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.buyer.MainBuyerActivity
import com.teamdefine.farmapp.databinding.FragmentSplashBinding
import com.teamdefine.farmapp.farmer.MainFarmerActivity

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var firebaseAuth: FirebaseAuth //firebase auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSplashBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = checkUserExists(firebaseAuth)
        if (currentUser) {
            Handler().postDelayed({
                view?.post {
                    val currentUserUID = firebaseAuth.currentUser?.uid
                    val user = FirebaseFirestore.getInstance().collection("Farmers")
                        .document(currentUserUID.toString())
                    user.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val doc = task.result
                            if (doc != null) {
                                if (doc.exists()) {
                                    Log.d("TAG", "Document already exists.")
                                    startActivity(Intent(activity, MainFarmerActivity::class.java))
//                                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToFarmerHomeScreen())
                                } else {
                                    startActivity(Intent(activity, MainBuyerActivity::class.java))
//                                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToBuyerHomeScreen())
                                }
                            } else {
                                Log.d("TAG", "Error: ", task.exception)
                            }
                        }
                    }


                }
            }, 500)
        } else {
            Handler().postDelayed({
                view?.post {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToUserAuthentication())
                }
            }, 3000)
        }
    }


    fun checkUserExists(firebaseAuth: FirebaseAuth): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null)
            return true
        return false
    }
}