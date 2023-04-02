package com.teamdefine.farmapp.farmer.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Event
import com.teamdefine.farmapp.farmer.models.FarmerCrops
import com.teamdefine.farmapp.farmer.models.FarmerData


class MainFarmerVM : ViewModel() {

    private val _farmerData: MutableLiveData<FarmerData?> = MutableLiveData(null)
    val farmerData: LiveData<FarmerData?>
        get() = _farmerData

    private val _updatedActiveDeals: MutableLiveData<Event<Boolean>?> = MutableLiveData(null)
    val updatedActiveDeals: LiveData<Event<Boolean>?>
        get() = _updatedActiveDeals

    private val _farmerCrops: MutableLiveData<ArrayList<FarmerCrops>?> =
        MutableLiveData(null)
    val farmerCrops: LiveData<ArrayList<FarmerCrops>?>
        get() = _farmerCrops

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

    fun updateActiveCropsByOne(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore) {
        firebaseFirestore.collection("Farmers").document(firebaseAuth.currentUser?.uid.toString())
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    val currentActiveDeals: Long = document.get("ActiveDeals") as Long
                    firebaseFirestore.collection("Farmers")
                        .document(firebaseAuth.currentUser?.uid.toString())
                        .update("ActiveDeals", currentActiveDeals + 1).addOnSuccessListener {
                            _updatedActiveDeals.value = Event(true)
                        }
                        .addOnFailureListener {
                            _updatedActiveDeals.value = Event(false)
                        }
                } else {
                    _updatedActiveDeals.value = Event(false)
                }
            }
    }

    fun getFarmerCrops(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore) {
        firebaseFirestore.collection("Crops").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val farmersCrops: ArrayList<FarmerCrops> = arrayListOf()
                    for (document in task.result) {
                        val currentDocId = document.id
                        if (currentDocId.contains(firebaseAuth.currentUser?.uid.toString())) {
                            farmersCrops.add(document.toObject(FarmerCrops::class.java))
                        }
                        _farmerCrops.postValue(farmersCrops)
                    }
                    Log.d("MainFarmerVM", farmersCrops.toString())
                }
            }.addOnFailureListener {
                Log.d("MainFarmerVM", "Failure in getting crops")
            }
    }
}