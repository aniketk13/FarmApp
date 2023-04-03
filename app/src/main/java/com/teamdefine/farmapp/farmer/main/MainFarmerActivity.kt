package com.teamdefine.farmapp.farmer.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.app.utils.Utility.updateLocale

class MainFarmerActivity : AppCompatActivity() {
    var isRegistered: Boolean? = false
    var languagePref: String? = "en"
    val mainFarmerVM: MainFarmerVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_farmer)

        isRegistered = intent.getBooleanExtra("isRegistered", false)
        languagePref = intent.getStringExtra("languagePref")

        languagePref?.let { updateLocale(it) }
    }
}