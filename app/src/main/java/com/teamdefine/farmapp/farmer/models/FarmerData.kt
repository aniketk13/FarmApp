package com.teamdefine.farmapp.farmer.models

class FarmerData {
    var ActiveDeals: Int? = null
        private set
    var ClosedDeals: Int? = null
        private set
    var Email: String? = null
        private set
    var LandSize: Int? = null
        private set
    var MobileNumber: String? = null
        private set
    var Name: String? = null
        private set
    var SoilReportURI: String? = null
        private set

    init {
        this.ActiveDeals = ActiveDeals
        this.ClosedDeals = ClosedDeals
        this.Email = Email
        this.LandSize = LandSize
        this.MobileNumber = MobileNumber
        this.Name = Name
        this.SoilReportURI = SoilReportURI
    }
}