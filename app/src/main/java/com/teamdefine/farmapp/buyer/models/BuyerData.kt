package com.teamdefine.farmapp.buyer.models

class BuyerData {
    var ActiveBids: Int? = null
        private set
    var ClosedBids: Int? = null
        private set
    var Email: String? = null
        private set
    var MobileNumber: String? = null
        private set
    var Name: String? = null
        private set
    var PersonalIdURI: String? = null
        private set

    init {
        this.ActiveBids = ActiveBids
        this.ClosedBids = ClosedBids
        this.Email = Email
        this.MobileNumber = MobileNumber
        this.Name = Name
        this.PersonalIdURI = PersonalIdURI
    }
}