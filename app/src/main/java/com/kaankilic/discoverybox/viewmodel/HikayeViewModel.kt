package com.kaankilic.discoverybox.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Base64

class HikayeViewModel : ViewModel() {
     var dbRepo= DiscoveryBoxRepository()
     var metinViewModel= MetinViewModel()
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














