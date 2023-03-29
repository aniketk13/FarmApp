package com.teamdefine.farmapp.farmer.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.R

class MainFarmerActivity : AppCompatActivity() {
    var isRegistered: Boolean? = false
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    val mainFarmerVM: MainFarmerVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_farmer)

        isRegistered = intent.getBooleanExtra("isRegistered", false)
//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseFirestore = FirebaseFirestore.getInstance()
//        mainFarmerVM.getFarmerData(firebaseAuth, firebaseFirestore)
    }
}