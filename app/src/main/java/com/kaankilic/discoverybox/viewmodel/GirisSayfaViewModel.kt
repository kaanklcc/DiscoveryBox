package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository

class GirisSayfaViewModel : ViewModel() {
    var dbRepo= DiscoveryBoxRepository()

    val loginResult = MutableLiveData<Pair<Boolean, String?>>()


    fun signInWithEmail(email: String, password: String)  {
        dbRepo.signInWithEmail(email,password){ success,message ->
            loginResult.value=Pair(success,message)


        }
    }
}