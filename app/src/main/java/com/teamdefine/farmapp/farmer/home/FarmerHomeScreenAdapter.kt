package com.teamdefine.farmapp.farmer.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.app.utils.Utility.loadImageUsingGlide
import com.teamdefine.farmapp.farmer.models.FarmerCrops


class FarmerHomeScreenAdapter(
    private val context: Context,
    private val farmerCrops: ArrayList<FarmerCrops>,
    private val clickListener: ItemClickListener
) :
    RecyclerView.Adapter<FarmerHomeScreenAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClickListener(clickedFarmerCrop: FarmerCrops)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropIv: AppCompatImageView = itemView.findViewById(R.id.roundedImageView)
        val cropNameTv: AppCompatTextView = itemView.findViewById(R.id.cropNameTv)
        val dateTv: AppCompatTextView = itemView.findViewById(R.id.dateTv)
        val priceTv: AppCompatTextView = itemView.findViewById(R.id.actualRateTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_farmer_crop, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFarmerCrop = farmerCrops[position]
        holder.cropNameTv.text = currentFarmerCrop.CropName.toString()
        holder.dateTv.text = currentFarmerCrop.CropListingDate.toString()
        holder.priceTv.text = "₹ ${currentFarmerCrop.CropOfferPrice}/ ${
            context.resources.getString(
                com.teamdefine.farmapp.R.string.quintal
            )
        }"
        holder.itemView.setOnClickListener {
            clickListener.onItemClickListener(currentFarmerCrop)
        }

        context.loadImageUsingGlide(currentFarmerCrop.CropImageURI.toString(), holder.cropIv)
    }

    override fun getItemCount() = farmerCrops.size
}