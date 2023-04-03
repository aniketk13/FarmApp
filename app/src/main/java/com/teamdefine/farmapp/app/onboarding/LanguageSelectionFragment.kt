package com.teamdefine.farmapp.app.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamdefine.farmapp.MainActivity
import com.teamdefine.farmapp.app.utils.Utility.updateLocale
import com.teamdefine.farmapp.databinding.FragmentLanguageSelectionBinding
import com.teamdefine.farmapp.farmer.models.AppLanguageModel

class LanguageSelectionFragment : Fragment() {
    private lateinit var binding: FragmentLanguageSelectionBinding
    private var adapter: RecyclerView.Adapter<LanguageSelectionAdapter.ViewHolder>? = null
    var selectedLanguage = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentLanguageSelectionBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arrayOfLanguages = createArrayListOfLanguages()

        initAdapter(arrayOfLanguages)
        initClickListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            selectedLanguage = savedInstanceState.getInt("currentSelectedLanguage", 0)
        }
    }

    private fun initClickListeners() {
        with(binding) {
            setAppLanguageButton.setOnClickListener {
                if (selectedLanguage == 0)
                    navigateToOnBoardingFragment("en")
                else
                    navigateToOnBoardingFragment("hi")
            }
        }
    }

    private fun navigateToOnBoardingFragment(languagePref: String) {
        findNavController().navigate(
            LanguageSelectionFragmentDirections.actionLanguageSelectionFragmentToOnBoardingFragment(
                languagePref
            )
        )
    }

    private fun createArrayListOfLanguages(): ArrayList<AppLanguageModel> {
        val arrayOfLanguages: ArrayList<AppLanguageModel> = arrayListOf()
        val langEnglish = AppLanguageModel("E", "English", "en")
        val langHindi = AppLanguageModel("ह", "Hindi", "hi")

        arrayOfLanguages.add(langEnglish)
        arrayOfLanguages.add(langHindi)

        return arrayOfLanguages
    }

    private fun initAdapter(arrayOfLanguages: ArrayList<AppLanguageModel>) {
        adapter = LanguageSelectionAdapter(
            arrayOfLanguages,
            object : LanguageSelectionAdapter.ItemClickListener {
                override fun onItemClickListener(clickedLanguagePosition: Int, language: String) {
                    selectedLanguage = clickedLanguagePosition
                    updateLocale(language)
                    (activity as MainActivity).recreate()
                    Log.i("LanguageSelectionFragment", clickedLanguagePosition.toString())
                }
            }, selectedLanguage
        )
        binding.languages.adapter = adapter
        binding.languages.layoutManager = LinearLayoutManager(activity)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentSelectedLanguage", selectedLanguage)
    }
}