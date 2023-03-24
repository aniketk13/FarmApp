package com.teamdefine.farmapp.app.splash

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
import com.teamdefine.farmapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var firebaseAuth: FirebaseAuth //firebase auth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance() //getting instance

        val loggedIn = checkUser(firebaseAuth)
        if (loggedIn) {
            Handler().postDelayed({
                view?.post {
                    val currentUser = firebaseAuth.currentUser?.uid
                    val user = FirebaseFirestore.getInstance().collection("Farmers")
                        .document(currentUser.toString())
                    user.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val doc = task.result
                            if (doc != null) {
                                if (doc.exists()) {
                                    Log.d("TAG", "Document already exists.")
                                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToFarmerHomeScreen())
                                } else {
                                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToBuyerHomeScreen())
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
//                    val intent= Intent(activity,AuthenticationActivity::class.java)
//                    startActivity(intent)
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToUserAuthentication())
                }
            }, 3000)
        }

        return binding.root
    }

    fun checkUser(firebaseAuth: FirebaseAuth): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null)
            return true
        return false
    }
}