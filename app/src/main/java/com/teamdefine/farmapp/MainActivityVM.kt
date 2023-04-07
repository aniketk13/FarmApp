package com.teamdefine.farmapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.app.utils.Event

class MainActivityVM : ViewModel() {
    private val _userIsFarmer: MutableLiveData<Boolean?> = MutableLiveData(null)
    val userIsFarmer: LiveData<Boolean?>
        get() = _userIsFarmer

    private val _progressLoad: MutableLiveData<Boolean?> = MutableLiveData(true)
    val progressLoad: LiveData<Boolean?>
        get() = _progressLoad

//    private val _userLanguagePref: MutableLiveData<String?> =
//        MutableLiveData(null)
//    val userLanguagePref: LiveData<String?>
//        get() = _userLanguagePref

    private val _userLanguagePref: MutableLiveData<Event<String?>> =
        MutableLiveData(null)
    val userLanguagePref: LiveData<Event<String?>>
        get() = _userLanguagePref

    fun checkIfUserIsFarmerOrBuyer(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ) {
        val currentUser = firebaseAuth.currentUser?.uid
        val user = firebaseFirestore.collection("Farmers")
            .document(currentUser.toString())

        user.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val doc = task.result
                if (doc != null) {
                    Log.d("MainActivityVM Auth", "Document already exists.")
                    if (doc.exists()) {
                        _userIsFarmer.postValue(true)
                    } else {
                        _userIsFarmer.postValue(false)
                    }
                } else {
                    _progressLoad.postValue(false)
                    Log.e("MainActivityVM Auth", "Error: ", task.exception)
                }
            }
        }
    }

    fun getLanguagePreference(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        userType: String
    ) {
        firebaseFirestore.collection(userType).document(firebaseAuth.currentUser?.uid.toString())
            .get().addOnSuccessListener { document ->
                val languagePref: String = document.get("LanguagePreference") as String
                _progressLoad.postValue(false)
                _userLanguagePref.value = Event(languagePref)
            }.addOnFailureListener {
                _progressLoad.postValue(false)
                Log.e("MainActivityVM Auth", "Error: ", it)
            }
    }
}