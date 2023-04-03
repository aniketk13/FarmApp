package com.teamdefine.farmapp.farmer.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.teamdefine.farmapp.R

class MainFarmerActivity : AppCompatActivity() {
    var isRegistered: Boolean? = false
    val mainFarmerVM: MainFarmerVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_farmer)

        isRegistered = intent.getBooleanExtra("isRegistered", false)
    }
}