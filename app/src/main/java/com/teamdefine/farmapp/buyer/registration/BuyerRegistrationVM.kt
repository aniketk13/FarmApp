package com.teamdefine.farmapp.buyer.registration

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BuyerRegistrationVM : ViewModel() {
    private val _savedDocUri: MutableLiveData<Uri> = MutableLiveData()
    val savedDocUri: LiveData<Uri>
        get() = _savedDocUri

    private val _progressDialog: MutableLiveData<Boolean?> = MutableLiveData(null)
    val progressDialog: LiveData<Boolean?>
        get() = _progressDialog
    fun saveIdCard(fileName: String?, fileUri: Uri) {
        val storageRef=Firebase.storage.reference.child("buyer/identification/$fileName")
        storageRef.putFile(fileUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                _savedDocUri.postValue(it)
                _progressDialog.postValue(false)
            }
        }
    }

}