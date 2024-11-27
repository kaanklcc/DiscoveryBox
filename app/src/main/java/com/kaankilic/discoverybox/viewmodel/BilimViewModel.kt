package com.kaankilic.discoverybox.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BilimViewModel : ViewModel() {
    var dbRepo= DiscoveryBoxRepository()


    var hikayeOlustur= MutableLiveData<String>()



    fun generateStory(prompt:String){
        CoroutineScope(Dispatchers.Main).launch {
            hikayeOlustur.value= dbRepo.generateStory(prompt)


        }
    }

}