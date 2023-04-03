package com.teamdefine.farmapp.buyer.registration

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BuyerRegistrationVM : ViewModel() {
    private val _savedDocUri: MutableLiveData<Uri> = MutableLiveData()
    val savedDocUri: LiveData<Uri>
        get() = _savedDocUri

    private val _progressDialog: MutableLiveData<Boolean?> = MutableLiveData(null)
    val progressDialog: LiveData<Boolean?>
        get() = _progressDialog

    private val _savedBuyerSuccess: MutableLiveData<Boolean?> = MutableLiveData(null)
    val savedBuyerSuccess: LiveData<Boolean?>
        get() = _savedBuyerSuccess
    fun saveIdCard(fileName: String?, fileUri: Uri) {
        val storageRef=Firebase.storage.reference.child("buyer/identification/$fileName")
        storageRef.putFile(fileUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                _savedDocUri.postValue(it)
                _progressDialog.postValue(false)
            }
        }
    }

    fun saveBuyerToDatabase(currentBuyer: MutableMap<String, Any>,
                            firebaseFirestore: FirebaseFirestore,
                            firebaseAuth: FirebaseAuth)
    {
        firebaseFirestore.collection("Buyers").document(firebaseAuth.currentUser?.uid.toString())
            .set(currentBuyer)
            .addOnSuccessListener {
                Log.i("BuyerRegistrationVM SaveUser", "Buyer Save Success")
                _savedBuyerSuccess.postValue(true)
            }
            .addOnFailureListener {
                _savedBuyerSuccess.postValue(false)
                Log.e("BuyerRegistrationVM SaveUserError", it.toString())
            }
    }

}