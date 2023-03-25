package com.teamdefine.farmapp.farmer.registration

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.databinding.FragmentFarmerRegistrationBinding

class FarmerRegistration : Fragment() {
    private lateinit var binding: FragmentFarmerRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val viewModel: FarmerRegistrationVM by viewModels()
    private var savedDocUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerRegistrationBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.savedDocUri.observe(requireActivity(), Observer { docUri ->
            savedDocUri = docUri
        })

        viewModel.savedFarmerSuccess.observe(requireActivity(), Observer { savedFarmerSuccess ->
            if (savedFarmerSuccess) {
                toast("Farmer saved to DB")
            } else {
                toast("Some error occoured")
            }
        })
    }

    private fun initClickListeners() {
        binding.apply {
            soilReport.setOnClickListener {
                uploadSoilReports()
            }
            submitButton.setOnClickListener {
                if (savedDocUri == null)
                    toast("Upload your soil reports first")
                else
                    saveUserToDataBase()
            }
        }
    }

    private fun saveUserToDataBase() {
        val farmer: MutableMap<String, Any> = HashMap()

        firebaseAuth.currentUser?.let {
            farmer["Name"] = it.displayName.toString()
            farmer["Email"] = it.email.toString()
            farmer["MobileNumber"] = binding.inputPhone.text.toString()
            farmer["LandSize"] = binding.inputLandSize.text.toString()
            farmer["SoilReportURI"] = savedDocUri.toString()

            saveFarmerToDatabase(farmer)
        }
    }

    private fun saveFarmerToDatabase(currentFarmer: MutableMap<String, Any>) {
        viewModel.saveFarmerToDatabase(currentFarmer, firebaseFirestore, firebaseAuth)
    }

    private fun uploadSoilReports() {
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fileUri = data?.data!!
        val returnCursor: Cursor? =
            fileUri?.let {
                requireContext().contentResolver.query(it, null, null, null, null)
            }
        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val fileName = returnCursor.getString(nameIndex)
        binding.inputSoilReport.setText(fileName)

        //saving file to storage
        saveFileToStorage(fileName, fileUri)
    }

    private fun saveFileToStorage(fileName: String?, fileUri: Uri) {
        viewModel.saveSoilReportToStorage(fileName, fileUri)
    }
}