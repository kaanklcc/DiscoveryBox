package com.kaankilic.discoverybox.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.auth
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import com.kaankilic.discoverybox.util.ApiCostTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HikayeViewModel @Inject constructor (val dbRepo: DiscoveryBoxRepository) : ViewModel() {
     var hikayeOlustur= MutableLiveData<String>()
     private var currentPrompt: String = ""
    val hikaye = MutableLiveData<Hikaye>()
    val storyPages = MutableLiveData<List<com.kaankilic.discoverybox.entitiy.StoryPage>>()
    
    // 💰 Maliyet takibi
    private var textCostInfo: ApiCostTracker.CostInfo? = null
    private val imageCostInfoList = mutableListOf<ApiCostTracker.CostInfo>()


    private var storyMainCharacter = ""
    private var storySetting = ""
    
    fun generateStoryWithImages(prompt: String, storyLength: String, context: android.content.Context, canCreateFullStory: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            // 💰 Maliyet takibini sıfırla
            imageCostInfoList.clear()
            
            // 🔒 Hikaye oluşturmadan önce hak azalt
            val userId = com.google.firebase.Firebase.auth.currentUser?.uid
            if (userId != null) {
                // Hak azaltma işlemi
                dbRepo.decrementChatGptUseIfNotPro(userId, canCreateFullStory) { status ->
                    when (status) {
                        com.kaankilic.discoverybox.util.UseStatus.SUCCESS -> {
                            // Başarılı, hikaye oluşturmaya devam et
                            CoroutineScope(Dispatchers.Main).launch {
                                generateStoryInternal(prompt, storyLength, context, canCreateFullStory)
                            }
                        }
                        com.kaankilic.discoverybox.util.UseStatus.NO_FREE_USES -> {
                            android.widget.Toast.makeText(context, "Hikaye oluşturma hakkınız kalmadı!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                        com.kaankilic.discoverybox.util.UseStatus.ERROR -> {
                            android.widget.Toast.makeText(context, "Bir hata oluştu!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    private suspend fun generateStoryInternal(prompt: String, storyLength: String, context: android.content.Context, canCreateFullStory: Boolean) {
        currentPrompt = prompt
        val pageCount = when(storyLength.lowercase()) {
            "short", "kısa" -> 2
            "long", "uzun" -> 4
            else -> 3
        }
        
        val enhancedPrompt = "$prompt\n\nKRİTİK KURALLAR:\n1. Hikayeyi TAM OLARAK $pageCount bölüme ayır, daha fazla veya daha az olmasın.\n2. Her bölümü '---SAYFA---' ile ayır.\n3. Her bölüm 2-3 paragraf olsun.\n4. Bilinen masal karakterlerinin (Shrek, Sindirella vb.) gerçek görünümlerini koru.\n5. Karakterlerin fiziksel görünümünü tüm sayfalarda tutarlı tut."
        
        val fullStory = withContext(Dispatchers.IO) {
            dbRepo.generateStory(enhancedPrompt)
        }
        
        val pages = fullStory.split("---SAYFA---").filter { it.isNotBlank() }
        val storyPagesList = pages.mapIndexed { index, content ->
            com.kaankilic.discoverybox.entitiy.StoryPage(
                pageNumber = index + 1,
                content = content.trim(),
                imagePrompt = "Scene from page ${index + 1}: ${content.take(100)}"
            )
        }
        
        storyPagesList.forEachIndexed { index, page ->
            val consistentPrompt = "Professional children's book illustration, vibrant fantasy art. Main character $storyMainCharacter (same appearance throughout) in $storySetting. Scene: ${page.content.take(100)}. IMPORTANT: NO book pages, NO text overlays, NO page borders, pure scene illustration only. Consistent character design."
            val bitmap = withContext(Dispatchers.IO) {
                // Artık hak azaltma yapmıyoruz, sadece görsel oluşturuyoruz
                dbRepo.queryTextToImage(consistentPrompt, canCreateFullStory, context, decrementUsage = false)
            }
            page.imageBitmap = bitmap
        }
        
        hikayeOlustur.value = fullStory
        storyPages.value = storyPagesList
    }
    
    fun generateStory(prompt: String, storyLength: String) {
        CoroutineScope(Dispatchers.Main).launch {
            currentPrompt = prompt
            val pageCount = when(storyLength.lowercase()) {
                "short", "kısa" -> 2
                "long", "uzun" -> 4
                else -> 3
            }
            
            val enhancedPrompt = "$prompt\n\nKRİTİK KURALLAR:\n1. Hikayeyi TAM OLARAK $pageCount bölüme ayır, daha fazla veya daha az olmasın.\n2. Her bölümü '---SAYFA---' ile ayır.\n3. Her bölüm 2-3 paragraf olsun.\n4. Bilinen masal karakterlerinin (Shrek, Sindirella vb.) gerçek görünümlerini koru.\n5. Karakterlerin fiziksel görünümünü tüm sayfalarda tutarlı tut."
            
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
    
    fun generateImagesForPages(context: android.content.Context, metinViewModel: MetinViewModel, canCreateFullStory: Boolean, mainCharacter: String, setting: String) {
        storyPages.value?.forEach { page ->
            val consistentPrompt = "Professional children's book illustration, vibrant fantasy art. Main character $mainCharacter (same appearance throughout) in $setting. Scene: ${page.content.take(100)}. IMPORTANT: NO book pages, NO text overlays, NO page borders, pure scene illustration only. Consistent character design."
            metinViewModel.queryTextToImageForPage(consistentPrompt, canCreateFullStory, context) { bitmap ->
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














