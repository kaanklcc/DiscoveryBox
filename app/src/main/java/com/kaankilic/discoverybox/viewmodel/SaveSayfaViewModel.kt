package com.kaankilic.discoverybox.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class SaveSayfaViewModel@Inject constructor (val dbRepo: DiscoveryBoxRepository):ViewModel() {
    //var dbRepo= DiscoveryBoxRepository()
    val stories = MutableLiveData<List<Hikaye>>()
    val deleteStatus = MutableLiveData<Boolean>() // Silme işlemi durumu için LiveData

    fun getUserStories(userId: String) {
        dbRepo.getUserStories(userId) { userStories ->
            stories.value = userStories
        }
    }

    fun deleteStory(userId: String, storyId: String){
        CoroutineScope(Dispatchers.Main).launch {
            dbRepo.deleteStory(userId,storyId){success,message ->
                if (success) {
                    // Başarıyla silindiyse, listeyi güncelle
                    getUserStories(userId)
                    deleteStatus.postValue(true)
                } else {
                    Log.e("Firestore", "Silme hatası: $message")
                    deleteStatus.postValue(false)
                }
            }
        }
    }









}