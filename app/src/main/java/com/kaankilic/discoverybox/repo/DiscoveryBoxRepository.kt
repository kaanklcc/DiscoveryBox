package com.kaankilic.discoverybox.repo

import android.content.Context
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.datasource.DiscoveryBoxDataSource
import com.kaankilic.discoverybox.entitiy.Hikaye
import kotlinx.coroutines.tasks.await


class DiscoveryBoxRepository(val dbds :DiscoveryBoxDataSource) {

    //var dbds= DiscoveryBoxDataSource()
    private val firestore = FirebaseFirestore.getInstance()


    suspend fun getAllGame() : List<Story> = dbds.GetAllGame()


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

    suspend fun generateGPTTTS(context: Context, apiKey: String, text: String): String {
        return dbds.generateGPTTTS(context, apiKey, text)
    }


    suspend fun isUserPremium(): Triple<Boolean, Boolean, Long> {
        val user = FirebaseAuth.getInstance().currentUser ?: return Triple(false, false, 0)
        val doc = FirebaseFirestore.getInstance().collection("users").document(user.uid).get().await()

        val premium = doc.getBoolean("premium") ?: false
        val usedFreeTrial = doc.getBoolean("usedFreeTrial") ?: false
        val remaining = doc.getLong("remainingChatgptUses") ?: 0

        return Triple(premium, usedFreeTrial, remaining)
    }




    suspend fun queryTextToImage(prompt: String, isPro: Boolean, context: Context): Bitmap? {
        return if (isPro) {
            dbds.generateImageWithGpt(prompt) ?: dbds.getDefaultImage(context)
        } else {
            dbds.getDefaultImage(context)
        }
    }



    suspend fun generateStory(prompt: String): String = dbds.generateStory(prompt)

    //suspend fun queryTextToImage(prompt: String,context: Context): Bitmap? = dbds.queryTextToImage(prompt,context)

    fun saveImageToStorage(bitmap: Bitmap, userId: String, onResult: (Boolean, String?) -> Unit)= dbds.saveImageToStorage(bitmap,userId,onResult)

    fun saveStoryForUser(title: String, story: String, imageUrl: String, userId: String, onResult: (Boolean) -> Unit) = dbds.saveStoryForUser(title,story,imageUrl,userId,onResult)

    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) =dbds.signInWithEmail(email,password,onResult)

    fun signUpWithEmail(email:String,password:String,onResult:(Boolean,String?)->Unit)= dbds.signUpWithEmail(email,password,onResult)

    fun signInWithGoogle(credential: AuthCredential, onResult: (Boolean, String?) -> Unit) = dbds.signInWithGoogle(credential, onResult)

    fun saveUserData(userId: String, ad: String, soyad: String, email: String, onResult: (Boolean, String?) -> Unit)=dbds.saveUserData(userId,ad,soyad,email,onResult)

    fun getUserStories(userId: String, onResult: (List<Hikaye>) -> Unit) = dbds.getUserStories(userId,onResult)

    fun getStoryById(storyId: String, onResult: (Hikaye) -> Unit) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        firestore.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("hikayeler")
            .document(storyId)
            .get()
            .addOnSuccessListener { document ->
                val hikaye = Hikaye(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    content = document.getString("hikaye") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    timestamp = document.getTimestamp("timestamp")
                )
                onResult(hikaye)
            }
            .addOnFailureListener {
                // Hata durumunda uygun bir yanıt döndür
                onResult(Hikaye()) // Boş bir Hikaye döndür
            }
    }

    suspend fun signOut(context: Context) = dbds.signOut(context)

    suspend fun deleteStory(userId: String, storyId: String, onResult: (Boolean, String?) -> Unit) = dbds.deleteStory(userId,storyId,onResult)
    suspend fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = dbds.reauthenticateUser(password,onSuccess,onFailure)

    fun decrementChatGptUseIfNotPro(userId: String, isPro: Boolean, onComplete: (Boolean) -> Unit) {
        if (!isPro) {
            dbds.decrementChatGptUse(userId, onComplete)
        } else {
            onComplete(true)
        }
    }
    fun markUsedFreeTrialIfNeeded(userId: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            val usedFreeTrial = document.getBoolean("usedFreeTrial") ?: false
            val premium = document.getBoolean("premium") ?: false

            if (!premium && !usedFreeTrial) {
                userRef.update("usedFreeTrial", true)
            }
        }
    }


}