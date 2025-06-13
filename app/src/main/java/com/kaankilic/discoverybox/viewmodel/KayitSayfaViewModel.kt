package com.kaankilic.discoverybox.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential

import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import com.kaankilic.discoverybox.util.GoogleAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class KayitSayfaViewModel@Inject constructor (val dbRepo: DiscoveryBoxRepository) : ViewModel(),GoogleAuthViewModel {
   // var dbRepo= DiscoveryBoxRepository()
    val signUpResult = MutableLiveData<Pair<Boolean, String?>>()
    val saveUserResult = MutableLiveData<Pair<Boolean, String?>>()
    val googleSignInResult = MutableLiveData<Pair<Boolean, String?>>()


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

    /*fun signInWithGoogleCredential(
        credential: AuthCredential,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        dbRepo.signInWithGoogle(credential) { isSuccess, message ->
            if (isSuccess) {
                onSuccess()
            } else {
                Log.e("GoogleSignIn", message ?: "Bilinmeyen hata")
                onFailure()
            }
        }
    }*/

    override fun signInWithGoogleCredential(
        credential: AuthCredential,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        dbRepo.signInWithGoogle(credential) { isSuccess, _ ->
            if (isSuccess) onSuccess() else onFailure()
        }
    }




}