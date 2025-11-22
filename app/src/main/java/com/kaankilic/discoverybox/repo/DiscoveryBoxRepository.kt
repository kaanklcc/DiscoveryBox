package com.kaankilic.discoverybox.repo

import android.content.Context
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.datasource.DiscoveryBoxDataSource
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.entitiy.UserData
import com.kaankilic.discoverybox.util.UseStatus
import com.kaankilic.discoverybox.util.getTodayDateString
import kotlinx.coroutines.tasks.await


class DiscoveryBoxRepository(val dbds :DiscoveryBoxDataSource) {

    //var dbds= DiscoveryBoxDataSource()
    private val firestore = FirebaseFirestore.getInstance()


   // suspend fun getAllGame() : List<Story> = dbds.GetAllGame()


   fun generateGoogleTTS(context: Context, text: String, onDone: (TextToSpeech?, String) -> Unit) {
       dbds.generateGoogleTTS(context, text) { tts, message ->
           onDone(tts, message)
       }
   }


    fun initTTS(context: Context, language: String, country: String) {
        dbds.initTTS(context,language,country)
    }
    fun getTTS(): TextToSpeech? {
        return dbds.getTTS()
    }
    
    fun getMediaPlayer(): android.media.MediaPlayer? {
        return dbds.getMediaPlayer()
    }

    suspend fun generateGPTTTS(context: Context, apiKey: String, text: String): String {
        return dbds.generateGPTTTS(context, apiKey, text)
    }


    suspend fun isUserPremium(): Triple<Boolean, Boolean, Long> {
        val user = FirebaseAuth.getInstance().currentUser ?: return Triple(false, true, 0)
        val doc = FirebaseFirestore.getInstance().collection("users").document(user.uid).get().await()

        val premium = doc.getBoolean("premium") ?: false
        val usedFreeTrial = doc.getBoolean("usedFreeTrial") ?: true
        val remaining = doc.getLong("remainingChatgptUses") ?: 0

        return Triple(premium, usedFreeTrial, remaining)
    }




    /*suspend fun queryTextToImage(prompt: String, isPro: Boolean, context: Context): Bitmap? {
        return if (isPro) {
            dbds.generateImageWithGpt(prompt) ?: dbds.getDefaultImage(context)
        } else {
            dbds.getDefaultImage(context)
        }
    }*/
    /**
     * Image generation function - PREMIUM SYSTEM
     * @param prompt Image generation prompt
     * @param canCreateFullStory Can create full-featured story?
     * @param context Context
     * @param decrementUsage Unused (kept for backward compatibility)
     * @return Bitmap (AI-generated or default)
     * 
     * ONLY canCreateFullStory=true generates AI images
     * Premium users + First-time trial users get AI images
     * Others get default placeholder image
     */
    suspend fun queryTextToImage(prompt: String, canCreateFullStory: Boolean, context: Context, decrementUsage: Boolean = false): Bitmap? {
        // Generate AI image if user can create full-featured stories
        return if (canCreateFullStory) {
            val bitmap = dbds.generateImageWithGemini(prompt)
            bitmap ?: dbds.getDefaultImage(context)
        } else {
            // Default placeholder image for non-premium users
            dbds.getDefaultImage(context)
        }
    }





    suspend fun generateStory(prompt: String): String = dbds.generateStory(prompt)

    //suspend fun queryTextToImage(prompt: String,context: Context): Bitmap? = dbds.queryTextToImage(prompt,context)

    fun saveImageToStorage(bitmap: Bitmap, userId: String, onResult: (Boolean, String?) -> Unit)= dbds.saveImageToStorage(bitmap,userId,onResult)

    fun saveStoryForUser(title: String, story: String, imageUrl: String, userId: String, onResult: (Boolean) -> Unit) = dbds.saveStoryForUser(title,story,imageUrl,userId,onResult)
    
    fun saveStoryForUserWithMultipleImages(title: String, story: String, imageUrls: List<String>, userId: String, onResult: (Boolean) -> Unit) = dbds.saveStoryForUserWithMultipleImages(title,story,imageUrls,userId,onResult)

    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) =dbds.signInWithEmail(email,password,onResult)

    fun signUpWithEmail(email:String,password:String,onResult:(Boolean,String?)->Unit)= dbds.signUpWithEmail(email,password,onResult)

    fun signInWithGoogle(credential: AuthCredential, onResult: (Boolean, String?) -> Unit) = dbds.signInWithGoogle(credential, onResult)

    fun saveUserData(userId: String, ad: String, soyad: String, email: String, onResult: (Boolean, String?) -> Unit)=dbds.saveUserData(userId,ad,soyad,email,onResult)

    fun getUserStories(userId: String, onResult: (List<Hikaye>) -> Unit) = dbds.getUserStories(userId,onResult)

    fun getStoryById(storyId: String, onResult: (Hikaye) -> Unit) {
        if (storyId.startsWith("featured_")) {
            onResult(Hikaye())
            return
        }
        
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            onResult(Hikaye())
            return
        }
        
        firestore.collection("users")
            .document(userId)
            .collection("hikayeler")
            .document(storyId)
            .get()
            .addOnSuccessListener { document ->
                val hikaye = Hikaye(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    content = document.getString("hikaye") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    imageUrls = (document.get("imageUrls") as? List<String>) ?: emptyList(),
                    timestamp = document.getTimestamp("timestamp")
                )
                onResult(hikaye)
            }
            .addOnFailureListener {
                onResult(Hikaye())
            }
    }

    suspend fun signOut(context: Context) = dbds.signOut(context)

    suspend fun deleteStory(userId: String, storyId: String, onResult: (Boolean, String?) -> Unit) = dbds.deleteStory(userId,storyId,onResult)
    suspend fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = dbds.reauthenticateUser(password,onSuccess,onFailure)

    /*fun decrementChatGptUseIfNotPro(userId: String, isPro: Boolean, onComplete: (Boolean) -> Unit) {
        if (!isPro) {
            dbds.decrementChatGptUse(userId, onComplete)
        } else {
            onComplete(true)
        }
    }*/
    /**
     * Decrement story creation usage - PREMIUM SYSTEM
     * @param userId User ID
     * @param isFullStory Is this a full-featured story (image+audio+text)?
     * @param onComplete Result callback
     * 
     * FULL-FEATURED: Decrements remainingChatgptUses (premium or first-time trial)
     * Note: Ad-based system has been removed. Only premium/trial users can create stories.
     */
    fun decrementChatGptUseIfNotPro(
        userId: String,
        isFullStory: Boolean,
        onComplete: (UseStatus) -> Unit
    ) {
        val userRef = Firebase.firestore.collection("users").document(userId)
        
        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentDate = getTodayDateString()
            val premium = snapshot.getBoolean("premium") ?: false
            val usedFreeTrial = snapshot.getBoolean("usedFreeTrial") ?: true
            val remainingChatgptUses = snapshot.getLong("remainingChatgptUses") ?: 0L
            val lastReset = snapshot.getString("lastFreeUseReset") ?: ""
            var remainingFreeUses = snapshot.getLong("remainingFreeUses") ?: 0L
            var adsWatchedToday = snapshot.getLong("adsWatchedToday") ?: 0L

            // Gün değiştiyse reklam haklarını ve sayaçları sıfırla
            if (lastReset != currentDate) {
                remainingFreeUses = 0
                adsWatchedToday = 0
                transaction.update(userRef, mapOf(
                    "remainingFreeUses" to 0,
                    "adsWatchedToday" to 0,
                    "lastFreeUseReset" to currentDate
                ))
            }

            if (isFullStory) {
                // Full-featured story creation (AI images + GPT-TTS)
                if (remainingChatgptUses <= 0) {
                    throw Exception("No more premium uses.")
                }
                
                transaction.update(userRef, "remainingChatgptUses", remainingChatgptUses - 1)
                
                // Mark free trial as used if this was the first-time trial
                if (!premium && !usedFreeTrial && remainingChatgptUses - 1 <= 0) {
                    transaction.update(userRef, "usedFreeTrial", true)
                }
            } else {
                // Ad-based system removed - this branch should not be used
                // Kept for backward compatibility only
                if (remainingFreeUses <= 0) {
                    throw Exception("No more free uses.")
                }
                
                transaction.update(userRef, "remainingFreeUses", remainingFreeUses - 1)
            }
            
            null
        }.addOnSuccessListener {
            onComplete(UseStatus.SUCCESS)
        }.addOnFailureListener { e ->
            if (e.message?.contains("No more free uses") == true || e.message?.contains("No more premium uses") == true) {
                onComplete(UseStatus.NO_FREE_USES)
            } else {
                onComplete(UseStatus.ERROR)
            }
        }
    }



    fun getUserData(userId: String, onComplete: (UserData?) -> Unit) {
        val userRef = Firebase.firestore.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = UserData(
                        adsWatchedToday = document.getLong("adsWatchedToday")?.toInt() ?: 0,
                        maxAdsPerDay = document.getLong("maxAdsPerDay")?.toInt() ?: 2,
                        remainingFreeUses = document.getLong("remainingFreeUses")?.toInt() ?: 0,
                        lastFreeUseReset = document.getString("lastFreeUseReset") ?: "",
                        premium = document.getBoolean("premium") ?: false
                    )
                    onComplete(userData)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
    
    fun rewardAfterAd(userId: String, onComplete: (Boolean, String) -> Unit) {
        dbds.rewardAfterAd(userId, onComplete)
    }



}