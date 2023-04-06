package com.teamdefine.farmapp.buyer.bidding.models

class BiddingData {
    var BidId: String? = null
        private set
    var BuyerId: String? = null
        private set
    var CropId: String? = null
        private set
    var FarmerBid: Long? = null
        private set
    var BuyerBid: Long? = null
        private set

    init {
        this.BidId = BidId
        this.BuyerId = BuyerId
        this.CropId = CropId
        this.FarmerBid = FarmerBid
        this.BuyerBid = BuyerBid
    }
}