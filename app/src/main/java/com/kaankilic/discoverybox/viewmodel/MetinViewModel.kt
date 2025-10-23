package com.kaankilic.discoverybox.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel

class MetinViewModel@Inject constructor (val dbRepo: DiscoveryBoxRepository) : ViewModel() {
    private var currentImage: String = ""
    //var dbRepo= DiscoveryBoxRepository()
    var imageBitmap = MutableLiveData<Bitmap?>()
    val imageSaved = MutableLiveData<Boolean>()
    val storySaved = MutableLiveData<Boolean>()
    val imageSavedUrl = MutableLiveData<String?>()

    private val _ttsState = MutableLiveData<String>()
    private var mediaPlayer: MediaPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isPaused = false
    private var pausedPosition = 0

    /*fun handleTTS(context: Context, apiKey: String, text: String) {
        viewModelScope.launch {
            val (premium, usedFreeTrial, remaining) = dbRepo.isUserPremium()
            //val canUseGPTTTS = premium || !usedFreeTrial || remaining > 0
            val canUseGPTTTS = premium || (!usedFreeTrial && remaining > 0)


            if (canUseGPTTTS) {
                val result = dbRepo.generateGPTTTS(context, apiKey, text)
                _ttsState.postValue(result)
            } else {
                dbRepo.generateGoogleTTS(context, text) { tts, result ->
                    textToSpeech = tts
                    _ttsState.postValue(result)
                }
            }
        }
    }*/

    fun handleTTS(context: Context, apiKey: String, text: String, onDone: () -> Unit) {
        viewModelScope.launch {
            val (premium, usedFreeTrial, remaining) = dbRepo.isUserPremium()
            val canUseGPTTTS = premium || (!usedFreeTrial && remaining > 0)

            if (canUseGPTTTS) {
                val result = dbRepo.generateGPTTTS(context, apiKey, text)
                _ttsState.postValue(result)
            } else {
                dbRepo.generateGoogleTTS(context, text) { tts, result ->
                    textToSpeech = tts
                    _ttsState.postValue(result)
                }
            }

            onDone() // 🎯 her iki durumda da callback çağrılır
        }
    }


    fun queryTextToImage(prompt: String, isPro: Boolean, context: Context) {
        CoroutineScope(Dispatchers.Main).launch {
            currentImage = prompt
            imageBitmap.value = dbRepo.queryTextToImage(prompt, isPro, context)
        }
    }
    
    fun queryTextToImageForPage(prompt: String, isPro: Boolean, context: Context, onComplete: (Bitmap?) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = dbRepo.queryTextToImage(prompt, isPro, context)
            onComplete(bitmap)
        }
    }

    fun stopMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()

            }
            it.release()
        }
        mediaPlayer = null
    }

    fun saveImageToStorage(bitmap: Bitmap, userId: String) {
        dbRepo.saveImageToStorage(bitmap, userId) { success, url ->
            imageSaved.value = success
            imageSavedUrl.value = url // URL'yi burada saklayın
        }
    }

    fun saveStoryForUser(title: String, story: String, imageUrl: String, userId: String){
        dbRepo.saveStoryForUser(title,story,imageUrl,userId){success ->
            storySaved.value= success

        }

    }

    fun initTTS(context: Context, language: String, country: String) {
        dbRepo.initTTS(context, language, country)
        CoroutineScope(Dispatchers.Main).launch {
            delay(500) // Başlatılması zaman alabilir, küçük gecikme (opsiyonel)
            textToSpeech = dbRepo.getTTS()
        }
    }

    fun speak(text: String) {
        if (isPaused && pausedPosition > 0) {
            val remainingText = text.substring(pausedPosition.coerceAtMost(text.length))
            textToSpeech?.speak(remainingText, TextToSpeech.QUEUE_FLUSH, null, null)
            isPaused = false
        } else {
            pausedPosition = 0
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun pause() {
        textToSpeech?.stop()
        isPaused = true
    }
    
    fun stop() {
        textToSpeech?.stop()
        isPaused = false
        pausedPosition = 0
    }
    
    fun pauseMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                pausedPosition = it.currentPosition
                it.pause()
                isPaused = true
            }
        }
    }
    
    fun resumeMediaPlayer() {
        mediaPlayer?.let {
            if (isPaused) {
                it.seekTo(pausedPosition)
                it.start()
                isPaused = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }






}