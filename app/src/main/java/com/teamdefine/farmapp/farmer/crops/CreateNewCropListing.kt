package com.teamdefine.farmapp.farmer.crops

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Utility
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.databinding.FragmentCreateNewCropListingBinding

class CreateNewCropListing : Fragment() {
    private lateinit var binding: FragmentCreateNewCropListingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private val viewModel: CreateNewCropVM by viewModels()
    private var savedDocUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentCreateNewCropListingBinding.inflate(inflater, container, false).also {
        Log.i("FragmentCropNew", "OnCreateView")
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(requireContext())
    }.root

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("FragmentCropNew", "OnViewCreated")
        initClickListeners()
        initObservers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FragmentCropNew", "OnCreate")
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToFarmerHomeFragment()
            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("FragmentCropNew", "OnAttach")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("FragmentCropNew", "OnDetach")
    }

    override fun onStart() {
        super.onStart()
        Log.i("FragmentCropNew", "OnStart")
    }

    override fun onStop() {
        super.onStop()
        Log.i("FragmentCropNew", "OnStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("FragmentCropNew", "OnDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("FragmentCropNew", "OnDestroyView")
    }

    private fun initObservers() {
        viewModel.savedDocUri.observe(requireActivity(), Observer { docUri ->
            savedDocUri = docUri
        })
        viewModel.progressDialog.observe(requireActivity(), Observer { ifShowProgessDialog ->
            ifShowProgessDialog?.let {
                if (!it) {
                    if (progressDialog.isShowing)
                        progressDialog.dismiss()
                }
            }
        })
        viewModel.savedCropSuccess.observe(requireActivity(), Observer { cropSaved ->
            cropSaved?.let {
                if (it) {
                    binding.progressBar.visibility = View.GONE
                    navigateBackToFarmerHomeFragment()
                } else {
                    binding.progressBar.visibility = View.GONE
                    toast("Seems like our servers are down. Try again later.")
                    navigateBackToFarmerHomeFragment()
                }
            }
        })
    }

    private fun navigateBackToFarmerHomeFragment() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(CreateNewCropListingDirections.actionCreateNewCropListingToFarmerHomeScreen())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initClickListeners() {
        binding.apply {
            cropImages.isClickable = false
            inputCropImages.isClickable = false
            inputCropImages.isFocusable = false
            cropImages.isFocusable = false

            uploadReport.setOnClickListener {
                Log.i("NewCrop", "Inside Click Image")
                uploadCropImages()
            }
            submitButton.setOnClickListener {
                if (binding.inputCropName.text.toString()
                        .isEmpty() || binding.inputOfferPrice.text.toString().isEmpty()
                )
                    toast("All fields are mandatory")
                else if (savedDocUri == null)
                    toast("Upload your crop images first")
                else {
                    binding.progressBar.visibility = View.VISIBLE
                    saveCropToDatabase()
                }
            }
            binding.backButton.setOnClickListener {
                navigateBackToFarmerHomeFragment()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveCropToDatabase() {
        val crop: MutableMap<String, Any> = HashMap()

        firebaseAuth.currentUser?.let {
            crop["FarmerName"] = it.displayName.toString()
            crop["FarmerUID"] = firebaseAuth.currentUser?.uid.toString()
            crop["CropName"] = binding.inputCropName.text.toString()
            crop["CropOfferPrice"] = binding.inputOfferPrice.text.toString().toInt()
            crop["CropImageURI"] = savedDocUri.toString()
            crop["CropListingDate"] = Utility.getCurrentDate()

            viewModel.saveCropToDatabase(firebaseFirestore, firebaseAuth, crop)
        }
    }

    private fun uploadCropImages() {
        Log.i("FarmerRegistration", "Inside Upload")
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            showProgressDialog("Uploading...")
            val fileUri = data?.data
            val returnCursor: Cursor? =
                fileUri?.let {
                    requireContext().contentResolver.query(it, null, null, null, null)
                }
            val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val fileName = returnCursor.getString(nameIndex)
            binding.inputCropImages.setText(fileName)

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