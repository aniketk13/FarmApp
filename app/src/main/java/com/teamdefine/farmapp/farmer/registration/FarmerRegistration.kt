package com.teamdefine.farmapp.farmer.registration

import android.app.Activity.RESULT_CANCELED
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.databinding.FragmentFarmerRegistrationBinding
import com.teamdefine.farmapp.farmer.main.MainFarmerActivity

class FarmerRegistration : Fragment() {
    private lateinit var binding: FragmentFarmerRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val viewModel: FarmerRegistrationVM by viewModels()
    private lateinit var progressDialog: ProgressDialog
    private var savedDocUri: Uri? = null

    private fun navigateToFarmerHomeScreen() {
        findNavController().navigate(FarmerRegistrationDirections.actionFarmerRegistrationToFarmerHomeScreen())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerRegistrationBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(requireContext())

        if ((activity as MainFarmerActivity).isRegistered == true)
            navigateToFarmerHomeScreen()
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
            savedFarmerSuccess?.let {
                if (it) {
                    binding.progressBar.visibility = View.GONE
                    navigateToFarmerHomeScreen()
                    toast("Farmer saved to DB")
                } else {
                    toast("Some error occoured")
                }
            }
        })

        viewModel.progressDialog.observe(requireActivity(), Observer { ifShowProgessDialog ->
            ifShowProgessDialog?.let {
                if (!it) {
                    if (progressDialog.isShowing)
                        progressDialog.dismiss()
                }
            }
        })
    }

    private fun initClickListeners() {
        binding.apply {
            soilReport.isClickable = false
            inputSoilReport.isClickable = false
            inputSoilReport.isFocusable = false
            soilReport.isFocusable = false

            uploadReport.setOnClickListener {
                Log.i("FarmerRegistration", "Inside Click Report")
                uploadSoilReports()
            }
            submitButton.setOnClickListener {
                if (binding.inputName.text.toString()
                        .isEmpty() || binding.inputPhone.text.toString()
                        .isEmpty() || binding.inputLandSize.text.toString().isEmpty()
                ) toast("All fields are mandatory")
                else if (savedDocUri == null)
                    toast("Upload your soil reports first")
                else
                    saveUserToDataBase()
            }
        }
    }

    private fun saveUserToDataBase() {
        binding.progressBar.visibility = View.VISIBLE
        val farmer: MutableMap<String, Any> = HashMap()

        firebaseAuth.currentUser?.let {
            farmer["Name"] = it.displayName.toString()
            farmer["Email"] = it.email.toString()
            farmer["MobileNumber"] = "+91-${binding.inputPhone.text}"
            farmer["LandSize"] = binding.inputLandSize.text.toString().toInt()
            farmer["SoilReportURI"] = savedDocUri.toString()
            farmer["LanguagePreference"] = (activity as MainFarmerActivity).languagePref.toString()
            farmer["ClosedDeals"] = 0
            farmer["ActiveDeals"] = 0

            saveFarmerToDatabase(farmer)
        }
    }

    private fun saveFarmerToDatabase(currentFarmer: MutableMap<String, Any>) {
        viewModel.saveFarmerToDatabase(currentFarmer, firebaseFirestore, firebaseAuth)
    }

    private fun uploadSoilReports() {
        Log.i("FarmerRegistration", "Inside Upload")
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            showProgressDialog("Uploading...")
            val fileUri = data?.data
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
    }

    private fun saveFileToStorage(fileName: String?, fileUri: Uri) {
        viewModel.saveSoilReportToStorage(fileName, fileUri)
    }

    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
}