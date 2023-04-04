package com.teamdefine.farmapp.farmer.models

class FarmerCrops {
    var CropImageURI: String? = null
        private set
    var CropListingDate: String? = null
        private set
    var CropName: String? = null
        private set
    var CropOfferPrice: Long? = null
        private set
    var FarmerName: String? = null
        private set
    var FarmerUID: String? = null
        private set

    init {
        this.CropImageURI = CropImageURI
        this.CropListingDate = CropListingDate
        this.CropName = CropName
        this.CropOfferPrice = CropOfferPrice
        this.FarmerName = FarmerName
        this.FarmerUID = FarmerUID
    }
}
