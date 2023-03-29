package com.teamdefine.farmapp.farmer.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.farmer.models.FarmerData

class MainFarmerVM : ViewModel() {

    private val _farmerData: MutableLiveData<FarmerData?> = MutableLiveData(null)
    val farmerData: LiveData<FarmerData?>
        get() = _farmerData

    fun getFarmerData(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ) {
        firebaseFirestore.collection("Farmers")
            .document(firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _farmerData.postValue(document.toObject(FarmerData::class.java))
                    Log.i("MainFarmerVM Data", document.toObject(FarmerData::class.java).toString())
                }
            }
            .addOnFailureListener {
                Log.e("MainFarmerVM Error", it.message.toString())
            }
    }
}