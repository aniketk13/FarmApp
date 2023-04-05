package com.teamdefine.farmapp.farmer.cropBids

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.app.utils.Utility.loadImageUsingGlide
import com.teamdefine.farmapp.buyer.bidding.models.BiddingData
import com.teamdefine.farmapp.farmer.home.FarmerHomeScreenAdapter
import com.teamdefine.farmapp.farmer.models.FarmerCrops

class FarmerCropBidsAdapter(
    private val context: Context,
    private val cropBids: ArrayList<BiddingData>,
    private val clickListener: ItemClickListener
) :
    RecyclerView.Adapter<FarmerCropBidsAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClickListener(clickedCropBid: BiddingData)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bidAmount: AppCompatTextView = itemView.findViewById(R.id.bidAmount)
        val nameOfBuyer: AppCompatTextView = itemView.findViewById(R.id.nameOfBuyer)
        val acceptButton: Button =itemView.findViewById(R.id.acceptButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_farmer_crop, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBid = cropBids[position]
        holder.bidAmount.text = currentBid.BuyerId
        holder.acceptButton.setOnClickListener {
            clickListener.onItemClickListener(currentBid)
        }
    }

    override fun getItemCount() = cropBids.size
}