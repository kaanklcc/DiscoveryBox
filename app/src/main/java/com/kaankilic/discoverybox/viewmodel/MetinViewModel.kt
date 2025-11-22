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

    /**
     * TTS işleme fonksiyonu - PREMIUM SYSTEM
     * Premium users + First-time trial users: GPT TTS (premium voice)
     * Saved stories (playback): Google TTS (default voice)
     */
    private var currentOnComplete: (() -> Unit)? = null
    
    fun handleTTS(context: Context, apiKey: String, text: String, onDone: () -> Unit, onComplete: (() -> Unit)? = null) {
        currentOnComplete = onComplete
        
        viewModelScope.launch {
            val (premium, usedFreeTrial, remainingChatgptUses) = dbRepo.isUserPremium()
            
            // Premium users OR first-time trial users can use GPT TTS
            val canUseGPTTTS = (premium && remainingChatgptUses > 0) || (!usedFreeTrial && remainingChatgptUses > 0)

            if (canUseGPTTTS) {
                // Premium voice - GPT TTS
                val result = dbRepo.generateGPTTTS(context, apiKey, text)
                _ttsState.postValue(result)
                
                // Get MediaPlayer and add completion listener
                kotlinx.coroutines.delay(500)
                mediaPlayer = dbRepo.getMediaPlayer()
                mediaPlayer?.setOnCompletionListener {
                    currentOnComplete?.invoke()
                }
            } else {
                // Default voice - Google TTS (for saved stories playback)
                dbRepo.generateGoogleTTS(context, text) { tts, result ->
                    textToSpeech = tts
                    _ttsState.postValue(result)
                    
                    // Add TTS completion listener
                    tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {}
                        override fun onDone(utteranceId: String?) {
                            currentOnComplete?.invoke()
                        }
                        override fun onError(utteranceId: String?) {}
                    })
                }
            }

            onDone() // Callback is called in both cases
        }
    }


    fun queryTextToImage(prompt: String, canCreateFullStory: Boolean, context: Context, decrementUsage: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            currentImage = prompt
            imageBitmap.value = dbRepo.queryTextToImage(prompt, canCreateFullStory, context, decrementUsage)
        }
    }
    
    fun queryTextToImageForPage(prompt: String, canCreateFullStory: Boolean, context: Context, decrementUsage: Boolean = false, onComplete: (Bitmap?) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmap = dbRepo.queryTextToImage(prompt, canCreateFullStory, context, decrementUsage)
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
    
    // Birden fazla görseli sırayla kaydetme
    fun saveMultipleImagesToStorage(bitmaps: List<Bitmap>, userId: String, onComplete: (List<String>) -> Unit) {
        val imageUrls = mutableListOf<String>()
        var savedCount = 0
        
        bitmaps.forEach { bitmap ->
            dbRepo.saveImageToStorage(bitmap, userId) { success, url ->
                if (success && url != null) {
                    imageUrls.add(url)
                }
                savedCount++
                
                if (savedCount == bitmaps.size) {
                    onComplete(imageUrls)
                }
            }
        }
    }
    
    fun saveStoryWithMultipleImages(title: String, story: String, imageUrls: List<String>, userId: String) {
        dbRepo.saveStoryForUserWithMultipleImages(title, story, imageUrls, userId) { success ->
            storySaved.value = success
        }
    }

    fun initTTS(context: Context, language: String, country: String) {
        dbRepo.initTTS(context, language, country)
        CoroutineScope(Dispatchers.Main).launch {
            delay(500) // Başlatılması zaman alabilir, küçük gecikme (opsiyonel)
            textToSpeech = dbRepo.getTTS()
        }
    }

    private var currentText: String = ""
    
    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        currentText = text
        
        textToSpeech?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                if (!isPaused) {
                    onComplete?.invoke()
                }
            }
            override fun onError(utteranceId: String?) {}
        })
        
        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts_id")
        
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "tts_id")
        isPaused = false
    }

    fun pause() {
        if (textToSpeech?.isSpeaking == true) {
            textToSpeech?.stop()
            isPaused = true
        }
    }
    
    fun resume() {
        if (isPaused && currentText.isNotEmpty()) {
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts_id")
            textToSpeech?.speak(currentText, TextToSpeech.QUEUE_FLUSH, params, "tts_id")
            isPaused = false
        }
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