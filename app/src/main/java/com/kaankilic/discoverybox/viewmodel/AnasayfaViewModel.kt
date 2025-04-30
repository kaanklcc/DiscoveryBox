package com.kaankilic.discoverybox.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AnasayfaViewModel : ViewModel() {
    var dbRepo= DiscoveryBoxRepository()
    var konular = MutableLiveData<List<Story>>()

    init {
        konulariYukle()

    }
    fun konulariYukle(){
        CoroutineScope(Dispatchers.Main).launch {
            konular.value= dbRepo.getAllGame()
        }


    }

    fun signOut(context: Context, onSignedOut: () -> Unit) {
        viewModelScope.launch {
            dbRepo.signOut(context)
            onSignedOut()
        }
    }
    /*fun checkUserAccess(onResult: (Boolean, Boolean) -> Unit) {
        val user = Firebase.auth.currentUser ?: return
        val userDoc = Firebase.firestore.collection("users").document(user.uid)

        userDoc.get().addOnSuccessListener { doc ->
            val remainingUses = doc.getLong("remainingChatgptUses") ?: 0
            val isPremium = doc.getBoolean("premium") ?: false
            onResult(remainingUses > 0, isPremium)
        }.addOnFailureListener {
            onResult(false, false) // hata varsa ücretsiz gibi davran
        }
    }*/
    fun checkUserAccess(onResult: (Boolean, Boolean, Boolean) -> Unit) {
        val user = Firebase.auth.currentUser ?: return
        val userDoc = Firebase.firestore.collection("users").document(user.uid)

        userDoc.get().addOnSuccessListener { doc ->
            val remainingUses = doc.getLong("remainingChatgptUses") ?: 0
            val isPremium = doc.getBoolean("premium") ?: false
            val usedFreeTrial = doc.getBoolean("usedFreeTrial") ?: false

            val hasTrial = remainingUses > 0 || !usedFreeTrial
            onResult(hasTrial, isPremium, usedFreeTrial)
        }.addOnFailureListener {
            onResult(false, false, true) // hata varsa hak yok gibi davran
        }
    }




    fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            dbRepo.reauthenticateUser(password, onSuccess, onFailure)
        }
    }



}