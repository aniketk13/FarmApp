package com.teamdefine.farmapp.buyer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.app.utils.Utility.updateLocale

class MainBuyerActivity : AppCompatActivity() {
    var isRegistered: Boolean? = false
    var languagePref: String? = "en"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_buyer)
        isRegistered = intent.getBooleanExtra("isRegistered", false)
        languagePref = intent.getStringExtra("languagePref")
        languagePref?.let { updateLocale(it) }

    }
}