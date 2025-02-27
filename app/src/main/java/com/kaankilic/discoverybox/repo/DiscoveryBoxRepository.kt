package com.kaankilic.discoverybox.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.datasource.DiscoveryBoxDataSource
import com.kaankilic.discoverybox.entitiy.Hikaye
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64


class DiscoveryBoxRepository {
    var dbds= DiscoveryBoxDataSource()
    private val firestore = FirebaseFirestore.getInstance()


    suspend fun getAllGame() : List<Story> = dbds.GetAllGame()

    suspend fun generateStory(prompt: String): String = dbds.generateStory(prompt)

    suspend fun queryTextToImage(prompt: String,context: Context): Bitmap? = dbds.queryTextToImage(prompt,context)

    fun saveImageToStorage(bitmap: Bitmap, userId: String, onResult: (Boolean, String?) -> Unit)= dbds.saveImageToStorage(bitmap,userId,onResult)

    fun saveStoryForUser(title: String, story: String, imageUrl: String, userId: String, onResult: (Boolean) -> Unit) = dbds.saveStoryForUser(title,story,imageUrl,userId,onResult)

    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) =dbds.signInWithEmail(email,password,onResult)

    fun signUpWithEmail(email:String,password:String,onResult:(Boolean,String?)->Unit)= dbds.signUpWithEmail(email,password,onResult)

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

    suspend fun signOut() = dbds.signOut()

    suspend fun deleteStory(userId: String, storyId: String, onResult: (Boolean, String?) -> Unit) = dbds.deleteStory(userId,storyId,onResult)
    suspend fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = dbds.reauthenticateUser(password,onSuccess,onFailure)












}