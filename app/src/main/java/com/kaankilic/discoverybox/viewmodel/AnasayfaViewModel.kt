package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AnasayfaViewModel : ViewModel() {
    var dbRepo= DiscoveryBoxRepository()
    var konular = MutableLiveData<List<Story>>()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        konulariYukle()

    }
    fun konulariYukle(){
        CoroutineScope(Dispatchers.Main).launch {
            konular.value= dbRepo.getAllGame()
        }


    }

    fun signOut(){
        CoroutineScope(Dispatchers.Main).launch {
            dbRepo.signOut()
        }

    }

    fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            dbRepo.reauthenticateUser(password, onSuccess, onFailure)
        }
    }



}