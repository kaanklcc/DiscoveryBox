package com.kaankilic.discoverybox.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HikayeViewModel : ViewModel() {
     var dbRepo= DiscoveryBoxRepository()
     var hikayeOlustur= MutableLiveData<String>()
     private var currentPrompt: String = ""
    val hikaye = MutableLiveData<Hikaye>()


    fun generateStory(prompt: String) {
        CoroutineScope(Dispatchers.Main).launch {
            currentPrompt = prompt
            hikayeOlustur.value = dbRepo.generateStory(prompt)

        }
    }

    fun getCurrentPrompt(): String {
        return currentPrompt
    }

    fun getStoryById(hikayeId: String?) {
        hikayeId?.let {
            Log.d("Hikaye", "getStoryById çağrıldı: $hikayeId")
            dbRepo.getStoryById(it) { retrievedHikaye ->
                hikaye.value = retrievedHikaye
                Log.d("Hikaye", "Başlık: ${retrievedHikaye.title}, İçerik: ${retrievedHikaye.content}")
            }
        }
    }



}














