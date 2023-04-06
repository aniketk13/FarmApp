package com.teamdefine.farmapp.farmer.crops

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.teamdefine.farmapp.app.utils.Utility

class CreateNewCropVM : ViewModel() {
    private val _savedDocUri: MutableLiveData<Uri> = MutableLiveData()
    val savedDocUri: LiveData<Uri>
        get() = _savedDocUri

    private val _progressDialog: MutableLiveData<Boolean?> = MutableLiveData(null)
    val progressDialog: LiveData<Boolean?>
        get() = _progressDialog

    private val _savedCropSuccess: MutableLiveData<Boolean?> = MutableLiveData(null)
    val savedCropSuccess: LiveData<Boolean?>
        get() = _savedCropSuccess

    fun saveSoilReportToStorage(fileName: String?, fileUri: Uri) {
        val storageRef = Firebase.storage.reference.child("farmer/crop/$fileName")

        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                Log.i("CropVM SaveFile", "Saved User")
                storageRef.downloadUrl.addOnSuccessListener {
                    _savedDocUri.postValue(it)
                    _progressDialog.postValue(false)
                }
            }
            .addOnFailureListener { exception ->
                _progressDialog.postValue(false)
                Log.e("CropVM SaveFileError", exception.toString())
            }
    }

    fun saveCropToDatabase(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        crop: MutableMap<String, Any>
    ) {
        val randomId = Utility.generateUUID()
        crop["CropId"] = "${firebaseAuth.currentUser?.uid}-$randomId"
        firebaseFirestore.collection("Crops").document("${firebaseAuth.currentUser?.uid}-$randomId")
            .set(crop)
            .addOnSuccessListener {
                Log.i("FarmerRegistrationVM SaveUser", "Farmer Save Success")
                _savedCropSuccess.postValue(true)
            }
            .addOnFailureListener {
                _savedCropSuccess.postValue(false)
                Log.e("FarmerRegistrationVM SaveUserError", it.toString())
            }
    }
}