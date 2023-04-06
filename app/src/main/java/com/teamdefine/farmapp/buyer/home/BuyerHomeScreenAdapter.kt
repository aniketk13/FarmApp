package com.teamdefine.farmapp.buyer.home

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

class BuyerHomeScreenAdapter(
    private val context: Context,
    private val farmerCrops: ArrayList<Map<String, FarmerCrops>>,
    private val clickListener: ItemClickListener
) :
    RecyclerView.Adapter<BuyerHomeScreenAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClickListener(clickedCropKey: String)
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
        val currentEntry = farmerCrops[position]
        val key = currentEntry.keys.elementAt(0)
        val currentFarmerCrop = currentEntry.get(key)
        currentFarmerCrop?.let {
            holder.cropNameTv.text = it.CropName
            holder.dateTv.text = it.CropListingDate.toString()
            holder.priceTv.text = "₹ ${it.CropOfferPrice}/ ${
                context.resources.getString(
                    com.teamdefine.farmapp.R.string.quintal
                )
            }"
            holder.itemView.setOnClickListener {
                clickListener.onItemClickListener(key)
            }
            context.loadImageUsingGlide(it.CropImageURI.toString(), holder.cropIv)
        }
    }

    override fun getItemCount() = farmerCrops.size
}
