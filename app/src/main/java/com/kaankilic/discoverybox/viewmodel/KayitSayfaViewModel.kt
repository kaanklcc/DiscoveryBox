package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository

class KayitSayfaViewModel : ViewModel() {
    var dbRepo= DiscoveryBoxRepository()

    val signUpResult = MutableLiveData<Pair<Boolean, String?>>()

    val saveUserResult = MutableLiveData<Pair<Boolean, String?>>()


    fun signUpWithEmail(email: String, password: String)  {
        dbRepo.signUpWithEmail(email,password){ success,message ->
            signUpResult.value=Pair(success,message)


        }
    }

    fun saveUserData(userId: String, ad: String, soyad: String, email: String) {
        dbRepo.saveUserData(userId, ad, soyad, email) { success, message ->
            saveUserResult.value = Pair(success, message)
        }
    }


}