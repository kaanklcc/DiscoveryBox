package com.kaankilic.discoverybox.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository

class SaveSayfaViewModel:ViewModel() {
    var dbRepo= DiscoveryBoxRepository()
    val stories = MutableLiveData<List<Hikaye>>()

    fun getUserStories(userId: String) {
        dbRepo.getUserStories(userId) { userStories ->
            stories.value = userStories
        }
    }

}