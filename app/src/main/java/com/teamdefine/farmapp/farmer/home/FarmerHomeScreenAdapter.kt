package com.teamdefine.farmapp.farmer.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class FarmerHomeScreenAdapter(private val farmerCrops: ArrayList<FarmerCrops>) :
    RecyclerView.Adapter<FarmerHomeScreenAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropIv: AppCompatImageView = itemView.findViewById(R.id.cropIvCv)
        val cropNameTv: AppCompatTextView = itemView.findViewById(R.id.cropNameTv)
        val dateTv: AppCompatTextView = itemView.findViewById(R.id.dateTv)
        val priceTv: AppCompatTextView = itemView.findViewById(R.id.actualRateTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_farmer_crop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        TODO("Not yet implemented")
    }

    override fun getItemCount() = farmerCrops.size
}