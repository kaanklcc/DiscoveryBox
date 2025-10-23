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
    fun checkUserAccess(onResult: (Boolean, Boolean, Boolean) -> Unit) {
        val user = Firebase.auth.currentUser ?: return
        val userDocRef = Firebase.firestore.collection("users").document(user.uid)

        userDocRef.get().addOnSuccessListener { doc ->
            val remainingUses = doc.getLong("remainingChatgptUses") ?: 0
            var isPremium = doc.getBoolean("premium") ?: false
            var usedFreeTrial = doc.getBoolean("usedFreeTrial") ?: false
            val premiumStartDate = doc.getTimestamp("premiumStartDate")
            val premiumDurationDays = doc.getLong("premiumDurationDays") ?: 0L

            var premiumExpired = false

            // 1️⃣ Premium süresi dolmuş mu?
            if (isPremium && premiumStartDate != null) {
                val nowMillis = System.currentTimeMillis()
                val startMillis = premiumStartDate.toDate().time
                val expireMillis = startMillis + premiumDurationDays * 24 * 60 * 60 * 1000

                if (nowMillis > expireMillis) {
                    premiumExpired = true
                }
            }

            // 2️⃣ Premium hak bitmiş mi?
            val usageExpired = isPremium && remainingUses <= 0

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

            // ✅ 4️⃣ Premium satın aldıysa usedFreeTrial = true yap
            if (isPremium && !usedFreeTrial) {
                usedFreeTrial = true
                userDocRef.update("usedFreeTrial", true)
            }

            // 5️⃣ Trial hakkı var mı? (sadece kalan kullanım varsa)
            val hasTrial = remainingUses > 0  && !usedFreeTrial

            onResult(hasTrial, isPremium, usedFreeTrial)
        }.addOnFailureListener {
            onResult(false, false, true) // hata varsa hak yok gibi davran
        }
    }







    fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            dbRepo.reauthenticateUser(password, onSuccess, onFailure)
        }
    }



}