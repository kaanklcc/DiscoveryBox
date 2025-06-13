package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class DilViewModel@Inject constructor (val dbRepo: DiscoveryBoxRepository) : ViewModel() {
    //var dbRepo= DiscoveryBoxRepository()


    var hikayeOlustur= MutableLiveData<String>()



    fun generateStory(prompt:String){
        CoroutineScope(Dispatchers.Main).launch {
            hikayeOlustur.value= dbRepo.generateStory(prompt)


        }
    }

}