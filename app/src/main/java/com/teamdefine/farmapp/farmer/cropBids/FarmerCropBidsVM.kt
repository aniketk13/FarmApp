package com.teamdefine.farmapp.farmer.cropBids

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class FarmerCropBidsVM : ViewModel() {
    fun getCrop(database:FirebaseFirestore,cropId: String?) {
        database.collection("Bidding").whereEqualTo("CropId",cropId).get()
            .addOnCompleteListener {
                for(document in it.result){

                }
            }
    }
}