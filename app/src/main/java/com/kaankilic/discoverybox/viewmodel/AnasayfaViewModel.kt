package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

}