package com.teamdefine.farmapp.buyer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.teamdefine.farmapp.R

class MainBuyerActivity : AppCompatActivity() {
    var isRegistered: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_buyer)
        isRegistered = intent.getBooleanExtra("isRegistered", false)

    }
}