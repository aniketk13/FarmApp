package com.teamdefine.farmapp.app.application

import android.app.Application
import com.google.firebase.FirebaseApp

class FirstYield : Application() {
    override fun onCreate() {
        super.onCreate()
        initInstances()
    }

    private fun initInstances() {
        FirebaseApp.initializeApp(this)
    }
}