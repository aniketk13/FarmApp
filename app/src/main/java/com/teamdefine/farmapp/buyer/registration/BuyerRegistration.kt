package com.teamdefine.farmapp.buyer.registration

import android.app.Activity
import android.app.ProgressDialog
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
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Utility.toast
import com.teamdefine.farmapp.buyer.MainBuyerActivity
import com.teamdefine.farmapp.databinding.FragmentBuyerRegistrationBinding

class BuyerRegistration : Fragment() {

    private val buyerRegistrationVM: BuyerRegistrationVM by viewModels()
    private lateinit var binding: FragmentBuyerRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private var buyerDocUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentBuyerRegistrationBinding.inflate(inflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(requireContext())
        if ((activity as MainBuyerActivity).isRegistered == true)
            navigateToHomeScreen()

    }.root

    private fun navigateToHomeScreen() {
        findNavController().navigate(BuyerRegistrationDirections.actionBuyerRegistrationToBuyerHomeScreen())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initClickListeners()
        initObservers()
    }

    private fun initViews() {
        firebaseAuth.currentUser?.let {
            binding.apply {
                inputName.setText(it.displayName)
                inputEmail.setText(it.email)
                inputEmail.isFocusable = false
            }
        }
    }

    private fun initObservers() {
        buyerRegistrationVM.savedDocUri.observe(requireActivity()) {
            buyerDocUri = it
        }

        buyerRegistrationVM.savedBuyerSuccess.observe(requireActivity()) {
            it?.let {
                if (it) {
                    binding.progressBar.visibility = View.GONE
                    toast("Buyer saved to DB")
                    navigateToHomeScreen()
                } else {
                    toast("Some error occoured")
                }
            }
        }
        buyerRegistrationVM.progressDialog.observe(requireActivity()) { ifShowProgessDialog ->
            ifShowProgessDialog?.let {
                if (!it) {
                    if (progressDialog.isShowing)
                        progressDialog.dismiss()
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.apply {
            idCard.isClickable = false
            inputIdCard.isClickable = false
            inputIdCard.isFocusable = false
            uploadId.setOnClickListener {
                uploadDocument()
            }
            submitButton.setOnClickListener {
                if (inputName.text.toString().isEmpty() || inputEmail.text.toString()
                        .isEmpty() || inputPhone.text.toString().isEmpty()
                ) {
                    toast("All fields are mandatory")
                } else if (buyerDocUri == null) {
                    toast("Upload your ID first")
                } else
                    saveUserToDataBase()
            }
        }
    }

    private fun saveUserToDataBase() {
        binding.progressBar.visibility = View.VISIBLE
        val buyer: MutableMap<String, Any> = HashMap()
        firebaseAuth.currentUser?.let {
            buyer["Name"] = it.displayName.toString()
            buyer["Email"] = it.email.toString()
            buyer["MobileNumber"] = "+91-${binding.inputPhone.text}"
            buyer["Personal ID"] = buyerDocUri.toString()
            buyer["ClosedBids"] = 0
            buyer["ActiveBids"] = 0

            saveBuyerToDatabase(buyer)
        }
    }

    private fun saveBuyerToDatabase(currentBuyer: MutableMap<String, Any>) {
        buyerRegistrationVM.saveBuyerToDatabase(currentBuyer, firebaseFirestore, firebaseAuth)
    }

    private fun uploadDocument() {
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
            binding.inputIdCard.setText(fileName)

            //saving file to storage
            saveFileToStorage(fileName, fileUri)
        }
    }

    private fun saveFileToStorage(fileName: String?, fileUri: Uri) {
        buyerRegistrationVM.saveIdCard(fileName, fileUri)
    }

    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

}