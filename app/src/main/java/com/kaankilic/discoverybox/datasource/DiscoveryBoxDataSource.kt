package com.kaankilic.discoverybox.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.entitiy.getAllGames
import com.kaankilic.discoverybox.entitiy.getAllStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException

class DiscoveryBoxDataSource {
    val firestore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    suspend fun GetAllGame(): List<Story> = withContext(Dispatchers.IO){
        return@withContext getAllGames()
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyDqVkaCBrlFoa9h_PQOj5VsHhrph5O2Cio"
    )

    suspend fun generateStory(prompt: String): String = withContext(Dispatchers.IO) {
        val response = generativeModel.generateContent(prompt)
        return@withContext response.text.toString()
    }



    suspend fun queryTextToImage(prompt: String, context: Context): Bitmap? {
        val API_URL = "https://api-inference.huggingface.co/models/CompVis/stable-diffusion-v1-4"
        val client = OkHttpClient.Builder()
            .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val mediaType = "application/json".toMediaType()
        val body = """{"inputs": "$prompt", "resolution": "1920x1024"}""".toRequestBody(mediaType)

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer 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".trim())
            .post(body)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                response.body?.let {
                    val bytes = it.bytes()
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                getDefaultImage(context)
            }
        }
    }
    private fun getDefaultImage(context: Context): Bitmap {
        // "default_image" drawable klasöründe olmalı
        return BitmapFactory.decodeResource(context.resources, R.drawable.story)
    }

    fun saveImageToStorage(bitmap: Bitmap, userId: String,  onResult: (Boolean, String?) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${userId}/${System.currentTimeMillis()}.png")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        val uploadTask = imagesRef.putBytes(data)

        uploadTask.addOnSuccessListener {
            // Resim başarıyla yüklendi, URL'yi al
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                onResult(true, uri.toString()) // URL ile birlikte başarılı sonucu döndür
            }
        }.addOnFailureListener { exception ->
            onResult(false, exception.message)
        }
    }

    /*fun saveStoryForUser(story: String, userId: String, onResult: (Boolean) -> Unit) {
        val storyData = mapOf("hikaye" to story, "timestamp" to FieldValue.serverTimestamp())

        firestore.collection("users")
            .document(userId) // Kullanıcının UID'si
            .collection("hikayeler") // Kullanıcıya ait hikaye koleksiyonu
            .add(storyData) // Hikayeyi kaydet
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }*/

    fun saveStoryForUser(title: String, story: String, imageUrl: String, userId: String, onResult: (Boolean) -> Unit) {
        val storyData = mapOf(

            "title" to title,
            "hikaye" to story,
            "imageUrl" to imageUrl,
            "timestamp" to FieldValue.serverTimestamp() // Zaman damgası
        )

        firestore.collection("users")
            .document(userId) // Kullanıcının UID'si
            .collection("hikayeler") // Kullanıcıya ait hikaye koleksiyonu
            .add(storyData) // Hikayeyi kaydet
            .addOnSuccessListener {

                onResult(true)
            }
            .addOnFailureListener {e ->
                Log.e("Firestore", "Hata: ${e.message}")

                onResult(false)
            }
    }


    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Giriş başarılı")
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun signUpWithEmail(email:String,password:String,onResult:(Boolean,String?)->Unit){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    onResult(true,"Kayıt Başarılı")
                }else{
                    onResult(false,task.exception?.message)
                }
            }
    }

    fun saveUserData(userId: String, ad: String, soyad: String, email: String, onResult: (Boolean, String?) -> Unit) {
        val user = hashMapOf(
            "ad" to ad,
            "soyad" to soyad,
            "email" to email
        )

        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                onResult(true, "Kullanıcı bilgileri kaydedildi")
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun getUserStories(userId: String, onResult: (List<Hikaye>) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("hikayeler")
            .get()
            .addOnSuccessListener { result ->
                val stories = result.map { document ->
                    Hikaye(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        content = document.getString("hikaye") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        timestamp = document.getTimestamp("timestamp")
                    )
                }
                onResult(stories)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

}