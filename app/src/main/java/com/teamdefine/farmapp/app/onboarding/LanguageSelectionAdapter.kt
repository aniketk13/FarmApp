package com.teamdefine.farmapp.app.onboarding

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.databinding.ItemAppLanguageBinding
import com.teamdefine.farmapp.farmer.models.AppLanguageModel

class LanguageSelectionAdapter(
    val appLanguges: ArrayList<AppLanguageModel>,
    val itemClickListener: ItemClickListener,
    val selectedLanguageBeforeRecreation: Int
) :
    RecyclerView.Adapter<LanguageSelectionAdapter.ViewHolder>() {

    private var currentSelectedItem = -1
    private var previousSelectedItem = -1
    private var currentViewHolder: LanguageSelectionAdapter.ViewHolder? = null
    private var previousViewHolder: LanguageSelectionAdapter.ViewHolder? = null

    interface ItemClickListener {
        fun onItemClickListener(clickedLanguagePosition: Int, language: String)
    }

    inner class ViewHolder(val binding: ItemAppLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAppLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentLanguage = appLanguges[position]
        with(holder) {
            binding.languageInitial.text = currentLanguage.localInitial
            binding.language.text = currentLanguage.localName

            if (selectedLanguageBeforeRecreation == position) {
                changeCurrentItem(position, holder)
                selectTheCurrentItem(currentViewHolder, itemView)
                changePreviousItem(currentSelectedItem, holder)
            }

            binding.languageCard.setOnClickListener {
                changeCurrentItem(position, holder)
                if (currentSelectedItem != previousSelectedItem) {
                    selectTheCurrentItem(currentViewHolder, itemView)
                    deselectThePreviousItem(previousViewHolder, itemView)
                    changePreviousItem(currentSelectedItem, holder)
                }
                itemClickListener.onItemClickListener(position, currentLanguage.locale)
            }
        }
    }

    private fun changePreviousItem(
        currentSelectedItem: Int,
        holder: LanguageSelectionAdapter.ViewHolder
    ) {
        previousSelectedItem = currentSelectedItem
        previousViewHolder = holder
    }

    private fun changeCurrentItem(position: Int, holder: LanguageSelectionAdapter.ViewHolder) {
        currentSelectedItem = position
        currentViewHolder = holder
    }

    private fun deselectThePreviousItem(previousViewHolder: ViewHolder?, itemView: View) {
        previousViewHolder?.let {
            with(it.binding) {
                languageCard.strokeColor = itemView.context.getColor(R.color.theme_light_orange)
                selected.visibility = View.GONE
            }
        }
    }

    private fun selectTheCurrentItem(currentViewHolder: ViewHolder?, itemView: View) {
        currentViewHolder?.let {
            with(it.binding) {
                languageCard.strokeColor = itemView.context.getColor(R.color.theme_orange)
                selected.visibility = View.VISIBLE
            }
        }
    }

    private fun deselectTheCurrentItem(currentViewHolder: ViewHolder?, itemView: View) {
        currentViewHolder?.let {
            with(it.binding) {
                languageCard.strokeColor = itemView.context.getColor(R.color.theme_light_orange)
                selected.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = appLanguges.size
}

//on tap previous item logic
// else {
// deselectTheCurrentItem(currentViewHolder, itemView)
// previousSelectedItem = -1
// previousViewHolder = null
// }