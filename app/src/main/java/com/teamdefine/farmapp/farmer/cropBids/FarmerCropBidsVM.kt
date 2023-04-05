package com.teamdefine.farmapp.farmer.cropBids

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.buyer.bidding.models.BiddingData
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class FarmerCropBidsVM : ViewModel() {
    private val _cropBids: MutableLiveData<ArrayList<BiddingData>> = MutableLiveData(null)
    val cropBids: LiveData<ArrayList<BiddingData>>
        get() = _cropBids

    private val _crop: MutableLiveData<FarmerCrops> = MutableLiveData(null)
    val crop: LiveData<FarmerCrops>
        get() = _crop

    fun getCrop(database:FirebaseFirestore,cropId: String?) {
        database.collection("Crops").document(cropId!!).get()
            .addOnCompleteListener {
                _crop.postValue(it.result.toObject(FarmerCrops::class.java))
            }
    }

    fun getBids(database: FirebaseFirestore, cropId: String?) {
        var bids:ArrayList<BiddingData>?= arrayListOf()
        database.collection("Bidding").whereEqualTo("CropId",cropId).get()
            .addOnCompleteListener {
                for(document in it.result){
                    bids?.add(document.toObject(BiddingData::class.java))
                }
            }
    }
}