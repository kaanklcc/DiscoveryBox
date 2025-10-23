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
class HikayeViewModel @Inject constructor (val dbRepo: DiscoveryBoxRepository) : ViewModel() {
     var hikayeOlustur= MutableLiveData<String>()
     private var currentPrompt: String = ""
    val hikaye = MutableLiveData<Hikaye>()
    val storyPages = MutableLiveData<List<com.kaankilic.discoverybox.entitiy.StoryPage>>()


    private var storyMainCharacter = ""
    private var storySetting = ""
    
    fun generateStory(prompt: String, storyLength: String) {
        CoroutineScope(Dispatchers.Main).launch {
            currentPrompt = prompt
            val pageCount = when(storyLength.lowercase()) {
                "short", "kısa" -> 2
                "long", "uzun" -> 4
                else -> 3
            }
            
            val enhancedPrompt = "$prompt Hikayeyi $pageCount bölüme ayır. Her bölümü '---SAYFA---' ile ayır. ÖNEMLİ: Eğer karakterler Shrek, Sindirella, Pamuk Prenses gibi bilinen masal karakterleriyse, onların gerçek görünümlerini ve özelliklerini koru (örn: Shrek yeşil dev, Sindirella sarı saçlı prenses). Her sayfada karakterlerin fiziksel görünümünü tutarlı tut."
            
            val fullStory = dbRepo.generateStory(enhancedPrompt)
            hikayeOlustur.value = fullStory
            
            val pages = fullStory.split("---SAYFA---").filter { it.isNotBlank() }
            val storyPagesList = pages.mapIndexed { index, content ->
                com.kaankilic.discoverybox.entitiy.StoryPage(
                    pageNumber = index + 1,
                    content = content.trim(),
                    imagePrompt = "Scene from page ${index + 1}: ${content.take(100)}"
                )
            }
            storyPages.value = storyPagesList
        }
    }
    
    fun setStoryContext(mainCharacter: String, setting: String) {
        storyMainCharacter = mainCharacter
        storySetting = setting
    }
    
    fun getStoryContext() = Pair(storyMainCharacter, storySetting)

    fun getCurrentPrompt(): String {
        return currentPrompt
    }
    
    fun generateImagesForPages(context: android.content.Context, metinViewModel: MetinViewModel, isPremium: Boolean, mainCharacter: String, setting: String) {
        storyPages.value?.forEach { page ->
            val consistentPrompt = "Professional children's book illustration, vibrant fantasy art. Main character $mainCharacter (same appearance throughout) in $setting. Scene: ${page.content.take(100)}. IMPORTANT: NO book pages, NO text overlays, NO page borders, pure scene illustration only. Consistent character design."
            metinViewModel.queryTextToImageForPage(consistentPrompt, isPremium, context) { bitmap ->
                page.imageBitmap = bitmap
                storyPages.value = storyPages.value
            }
        }
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














