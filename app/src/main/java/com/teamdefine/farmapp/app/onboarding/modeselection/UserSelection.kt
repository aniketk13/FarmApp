package com.teamdefine.farmapp.app.onboarding.modeselection

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.teamdefine.farmapp.buyer.MainBuyerActivity
import com.teamdefine.farmapp.databinding.FragmentUserSelectionBinding
import com.teamdefine.farmapp.farmer.MainFarmerActivity

class UserSelection : Fragment() {
    private lateinit var binding: FragmentUserSelectionBinding
    lateinit var radioButton: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.apply {
            doneButton.setOnClickListener {
                val selectedButton: Int? = radioGroup.checkedRadioButtonId
                selectedButton?.let {
                    radioButton = root.findViewById<RadioButton>(selectedButton)
                    if (radioButton == binding.farmer) {
                        startActivity(
                            Intent(
                                requireActivity(),
                                MainFarmerActivity::class.java
                            ).putExtra("isRegistered", false)
                        )
                    } else if (radioButton == binding.buyer) {
                        startActivity(
                            Intent(
                                requireActivity(),
                                MainBuyerActivity::class.java
                            ).putExtra("isRegistered", false)
                        )
                    }
                }
            }
        }
    }

}