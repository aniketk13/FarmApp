package com.teamdefine.farmapp.farmer.registration

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FarmerRegistrationVM : ViewModel() {
    private val _savedDocUri: MutableLiveData<Uri> = MutableLiveData()
    val savedDocUri: LiveData<Uri>
        get() = _savedDocUri

    private val _savedFarmerSuccess: MutableLiveData<Boolean?> = MutableLiveData(null)
    val savedFarmerSuccess: LiveData<Boolean?>
        get() = _savedFarmerSuccess

    private val _progressDialog: MutableLiveData<Boolean?> = MutableLiveData(null)
    val progressDialog: LiveData<Boolean?>
        get() = _progressDialog

    fun saveSoilReportToStorage(fileName: String?, fileUri: Uri) {
        val storageRef = Firebase.storage.reference.child("farmer/$fileName")

        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                Log.i("FarmerRegistrationVM SaveFile", "Saved User")
                storageRef.downloadUrl.addOnSuccessListener {
                    _savedDocUri.postValue(it)
                    _progressDialog.postValue(false)
                }
            }
            .addOnFailureListener { exception ->
                _progressDialog.postValue(false)
                Log.e("FarmerRegistrationVM SaveFileError", exception.toString())
            }
    }

    fun saveFarmerToDatabase(
        currentFarmer: MutableMap<String, Any>,
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth
    ) {
        firebaseFirestore.collection("Farmers").document(firebaseAuth.currentUser?.uid.toString())
            .set(currentFarmer)
            .addOnSuccessListener {
                Log.i("FarmerRegistrationVM SaveUser", "Farmer Save Success")
                _savedFarmerSuccess.postValue(true)
            }
            .addOnFailureListener {
                _savedFarmerSuccess.postValue(false)
                Log.e("FarmerRegistrationVM SaveUserError", it.toString())
            }
    }
}