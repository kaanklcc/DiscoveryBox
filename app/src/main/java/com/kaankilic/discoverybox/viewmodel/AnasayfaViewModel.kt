package com.kaankilic.discoverybox.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnasayfaViewModel @Inject constructor(val dbRepo: DiscoveryBoxRepository) : ViewModel() {
   // var dbRepo= DiscoveryBoxRepository()
    var konular = MutableLiveData<List<Story>>()
    var featuredStoryTitle = MutableLiveData<String>()
    var featuredStoryContent = MutableLiveData<String>()
    var featuredStoryImage = MutableLiveData<Int>()
    
    fun setFeaturedStory(title: String, content: String, imageRes: Int) {
        featuredStoryTitle.value = title
        featuredStoryContent.value = content
        featuredStoryImage.value = imageRes
    }

  /*  init {
        konulariYukle()

    }*/
    /*fun konulariYukle(){
        CoroutineScope(Dispatchers.Main).launch {
            konular.value= dbRepo.getAllGame()
        }


    }*/

    fun signOut(context: Context, onSignedOut: () -> Unit) {
        viewModelScope.launch {
            dbRepo.signOut(context)
            onSignedOut()
        }
    }
    /**
     * Kullanıcı erişim kontrolü - YENİ SİSTEM
     * @param onResult (canCreateFullStory: Boolean, canCreateTextOnly: Boolean, isPremium: Boolean, usedFreeTrial: Boolean)
     * 
     * canCreateFullStory: Görsel + Ses + Metin oluşturabilir mi?
     * canCreateTextOnly: Sadece Metin oluşturabilir mi? (Reklam izleyerek)
     * isPremium: Premium kullanıcı mı?
     * usedFreeTrial: Ücretsiz denemeyi kullanmış mı?
     */
    fun checkUserAccess(onResult: (Boolean, Boolean, Boolean, Boolean) -> Unit) {
        val user = Firebase.auth.currentUser ?: run {
            onResult(false, false, false, true)
            return
        }
        
        val userDocRef = Firebase.firestore.collection("users").document(user.uid)

        userDocRef.get().addOnSuccessListener { doc ->
            val remainingChatgptUses = doc.getLong("remainingChatgptUses") ?: 0
            var isPremium = doc.getBoolean("premium") ?: false
            val usedFreeTrial = doc.getBoolean("usedFreeTrial") ?: true
            val remainingFreeUses = doc.getLong("remainingFreeUses") ?: 0
            val premiumStartDate = doc.getTimestamp("premiumStartDate")
            val premiumDurationDays = doc.getLong("premiumDurationDays") ?: 0L

            var premiumExpired = false

            // 1️⃣ Premium süresi dolmuş mu kontrol et
            if (isPremium && premiumStartDate != null) {
                val nowMillis = System.currentTimeMillis()
                val startMillis = premiumStartDate.toDate().time
                val expireMillis = startMillis + premiumDurationDays * 24 * 60 * 60 * 1000

                if (nowMillis > expireMillis) {
                    premiumExpired = true
                }
            }

            // 2️⃣ Premium hakkı bitmiş mi?
            val usageExpired = isPremium && remainingChatgptUses <= 0

            // 3️⃣ Premium bitmişse kapat
            if (isPremium && (premiumExpired || usageExpired)) {
                isPremium = false
                userDocRef.update(
                    mapOf(
                        "premium" to false,
                        "remainingChatgptUses" to 0
                    )
                )
            }

            // 4️⃣ TAM ÖZELLİKLİ (Görsel + Ses + Metin) hikaye oluşturabilir mi?
            val canCreateFullStory = when {
                isPremium && remainingChatgptUses > 0 -> true // Premium ve hakkı var
                !usedFreeTrial && remainingChatgptUses > 0 -> true // İlk ücretsiz deneme hakkı var
                else -> false
            }

            // 5️⃣ SADECE METİN hikaye oluşturabilir mi? (Reklam izleyerek)
            val canCreateTextOnly = !isPremium && remainingFreeUses > 0

            onResult(canCreateFullStory, canCreateTextOnly, isPremium, usedFreeTrial)
        }.addOnFailureListener {
            onResult(false, false, false, true) // hata varsa hak yok
        }
    }







    fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            dbRepo.reauthenticateUser(password, onSuccess, onFailure)
        }
    }



}