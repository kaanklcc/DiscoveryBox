package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import com.kaankilic.discoverybox.util.GoogleAuthViewModel

class GirisSayfaViewModel : ViewModel(),GoogleAuthViewModel {
    var dbRepo= DiscoveryBoxRepository()

    val loginResult = MutableLiveData<Pair<Boolean, String?>>()


    fun signInWithEmail(email: String, password: String)  {
        dbRepo.signInWithEmail(email,password){ success,message ->
            loginResult.value=Pair(success,message)


        }
    }

    override fun signInWithGoogleCredential(
        credential: AuthCredential,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        dbRepo.signInWithGoogle(credential) { isSuccess, _ ->
            if (isSuccess) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }
}