package com.teamdefine.farmapp.buyer.bidding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.buyer.bidding.models.BiddingData
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class BuyerBiddingVM : ViewModel() {
    private val _firstTime: MutableLiveData<Boolean> = MutableLiveData(null)
    val firstTime: LiveData<Boolean>
        get() = _firstTime

    private val _crop: MutableLiveData<FarmerCrops> = MutableLiveData(null)
    val crop: LiveData<FarmerCrops>
        get() = _crop

    private val _bidAdded: MutableLiveData<Boolean> = MutableLiveData(null)
    val bidAdded: LiveData<Boolean>
        get() = _bidAdded

    private val _bidData: MutableLiveData<BiddingData> = MutableLiveData(null)
    val bidData: LiveData<BiddingData>
        get() = _bidData

    private val _bitUpdated: MutableLiveData<Boolean> = MutableLiveData(null)
    val bitUpdated: LiveData<Boolean>
        get() = _bitUpdated

    fun checkIfBidExists(database: FirebaseFirestore, cropKey: String, buyerId: String?) {
        database.collection("Bidding").whereEqualTo("BuyerId", buyerId)
            .whereEqualTo("CropId", cropKey).get()
            .addOnCompleteListener {
                Log.i("BuyerBidding", it.result.toString())
                for (doc in it.result)
                    Log.i("Bid", doc.toObject(BuyerBidding::class.java).toString())
                if (it.result.isEmpty) {
                    _firstTime.postValue(true)
                } else
                    _firstTime.postValue(false)
            }
    }


    fun getCrop(database: FirebaseFirestore, cropId: String) {
        database.collection("Crops").document(cropId).get()
            .addOnCompleteListener { taskResult ->
                if (taskResult.isSuccessful) {
                    _crop.postValue(taskResult.result.toObject(FarmerCrops::class.java))
                }
            }
    }

    fun addBidToDatabase(database: FirebaseFirestore, bid: MutableMap<String, Any>) {
        val bidId = bid["BidId"].toString()
        database.collection("Bidding").document(bidId).set(bid)
            .addOnSuccessListener {
                _bidAdded.postValue(true)
            }
    }

    fun getBid(database: FirebaseFirestore, cropId: String, buyerId: String?) {
        Log.i("BuyerBidding", "Inside Get Bid")
        database.collection("Bidding").whereEqualTo("BuyerId", buyerId)
            .whereEqualTo("CropId", cropId).get()
            .addOnCompleteListener {
                for (document in it.result)
                    _bidData.postValue(document.toObject(BiddingData::class.java))
            }
    }

    fun updateBid(database: FirebaseFirestore, buyerBid: String, bidId: String) {
        database.collection("Bidding").document(bidId).update("BuyerBid", buyerBid)
            .addOnSuccessListener {
                _bitUpdated.postValue(true)
            }
    }

    fun updateBuyerActiveBids(database: FirebaseFirestore, buyerId: String) {
        database.collection("Buyers").document(buyerId)
            .update("ActiveBids", FieldValue.increment(1))
            .addOnSuccessListener {
                Log.i("ActiveBids", "Updated")
            }
    }

    fun updateFarmerActiveDeals(database: FirebaseFirestore, farmerId: String) {
        database.collection("Farmers").document(farmerId)
            .update("ActiveDeals", FieldValue.increment(1))
            .addOnSuccessListener {
                Log.i("ActiveDeals", "Updated")
            }
    }


}