package com.teamdefine.farmapp.buyer.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.buyer.models.BuyerData
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class BuyerHomeScreenViewModel : ViewModel() {

    private val _buyerData: MutableLiveData<BuyerData?> = MutableLiveData(null)
    val buyerData: LiveData<BuyerData?>
        get() = _buyerData

    private val _farmerCrops: MutableLiveData<ArrayList<Map<String, FarmerCrops>>?> =
        MutableLiveData(null)
    val farmerCrops: LiveData<ArrayList<Map<String, FarmerCrops>>?>
        get() = _farmerCrops

    fun getFarmerCrops(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore) {
        firebaseFirestore.collection("Crops").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val farmersCrops: ArrayList<Map<String, FarmerCrops>> = arrayListOf()
                    for (document in task.result) {
                        val map = mapOf(document.id to document.toObject(FarmerCrops::class.java))
                        farmersCrops.add(map)
                    }
                    _farmerCrops.postValue(farmersCrops)

                    Log.d("BuyerHomeVM", farmersCrops.toString())
                }
            }.addOnFailureListener {
                Log.d("BuyerHomeVM", "Failure in getting crops")
            }
    }

    fun getBuyerData(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ) {
        firebaseFirestore.collection("Buyers")
            .document(firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _buyerData.postValue(document.toObject(BuyerData::class.java))
                    Log.i("BuyerHomeVM Data", document.toObject(BuyerData::class.java).toString())
                }
            }
            .addOnFailureListener {
                Log.e("BuyerHomeVM Error", it.message.toString())
            }
    }
}