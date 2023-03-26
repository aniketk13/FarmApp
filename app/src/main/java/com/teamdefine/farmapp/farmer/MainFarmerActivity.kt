package com.teamdefine.farmapp.farmer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamdefine.farmapp.R

class MainFarmerActivity : AppCompatActivity() {
    var isRegistered: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_farmer)

        isRegistered = intent.getBooleanExtra("isRegistered", false)
    }
}