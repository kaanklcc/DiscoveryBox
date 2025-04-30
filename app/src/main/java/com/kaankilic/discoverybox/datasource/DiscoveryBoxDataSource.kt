package com.kaankilic.discoverybox.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaankilic.discoverybox.BuildConfig
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.entitiy.ImageRequest
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.entitiy.TTSRequest
import com.kaankilic.discoverybox.entitiy.getAllGames
import com.kaankilic.discoverybox.retrofit.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale



class DiscoveryBoxDataSource {
    val firestore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    suspend fun GetAllGame(): List<Story> = withContext(Dispatchers.IO){
        return@withContext getAllGames()
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateImageWithGpt(prompt: String): Bitmap? {
        val request = ImageRequest(prompt = prompt)
        val api = api
        return withContext(Dispatchers.IO) {
            try {
                val response = api.generateImage(request,
                    BuildConfig.IMAGE_GENERATION_API_KEY)
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data?.firstOrNull()?.url
                    imageUrl?.let {
                        val imageRequest = Request.Builder().url(it).build()
                        val client = OkHttpClient()
                        val imageResponse = client.newCall(imageRequest).execute()
                        val inputStream = imageResponse.body?.byteStream()
                        BitmapFactory.decodeStream(inputStream)
                    }
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

     fun getDefaultImage(context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.story)
    }

    suspend fun generateStory(prompt: String): String = withContext(Dispatchers.IO) {
        val response = generativeModel.generateContent(prompt)
        return@withContext response.text.toString()
    }

    /*fun generateGoogleTTS(context: Context, text: String, onDone: (String) -> Unit) {
         tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("tr", "TR"))
                if (result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    onDone("Google TTS başarıyla başlatıldı")
                } else {
                    onDone("Google TTS için dil ayarları hatalı.")
                }
            } else {
                onDone("Google TTS başlatılamadı.")
            }
        }

    }*/

    fun generateGoogleTTS(context: Context, text: String, onDone: (TextToSpeech?, String) -> Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("tr", "TR"))
                if (result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    onDone(tts, "Google TTS başarıyla başlatıldı")
                } else {
                    onDone(null, "Google TTS için dil ayarları hatalı.")
                }
            } else {
                onDone(null, "Google TTS başlatılamadı.")
            }
        }
    }



    fun initTTS(context: Context, language: String, country: String) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale(language, country))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TextToSpeech", "Dil desteklenmiyor.")
                }
            } else {
                Log.e("TextToSpeech", "Başlatma başarısız.")
            }
        }
    }

    fun getTTS(): TextToSpeech? {
        return tts
    }

   /* suspend fun generateGPTTTS(context: Context, apiKey: String, text: String): String = withContext(Dispatchers.IO) {
        val request = TTSRequest(input = text)
        val response = api.generateSpeech(request, "Bearer $apiKey")

        if (response.isSuccessful) {
            response.body()?.let { body ->
                val file = File(context.cacheDir, "output.mp3")
                file.outputStream().use { it.write(body.bytes()) }

                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(file.absolutePath)
                mediaPlayer.prepare()
                mediaPlayer.start()
                return@withContext "GPT TTS başarıyla başlatıldı"
            }
        }
        return@withContext "Hata: ${response.errorBody()?.string()}"
    }*/

    suspend fun generateGPTTTS(context: Context, apiKey: String, text: String): String = withContext(Dispatchers.IO) {
        val request = TTSRequest(input = text)
        val response = api.generateSpeech(request, "Bearer $apiKey")

        if (response.isSuccessful) {
            response.body()?.let { body ->
                val file = File(context.cacheDir, "output.mp3")
                file.outputStream().use { it.write(body.bytes()) }

                // ViewModel'deki mediaPlayer'a referans atıyoruz!
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                    setOnCompletionListener {
                        it.release()
                        mediaPlayer = null
                    }
                }

                return@withContext "GPT TTS başarıyla başlatıldı"
            }
        }
        return@withContext "Hata: ${response.errorBody()?.string()}"
    }


    /*suspend fun queryTextToImage(prompt: String, context: Context): Bitmap? {
       // val API_URL = "https://api-inference.huggingface.co/models/CompVis/stable-diffusion-v1-4"
         val API_URL = "https://api-inference.huggingface.co/models/HiDream-ai/HiDream-I1-Fast"

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
    }*/

   /* private fun getDefaultImage(context: Context): Bitmap {
        // "default_image" drawable klasöründe olmalı
        return BitmapFactory.decodeResource(context.resources, R.drawable.story)
    }*/

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

    /*fun saveUserData(userId: String, ad: String, soyad: String, email: String, onResult: (Boolean, String?) -> Unit) {
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
    }*/

    fun saveUserData(userId: String, ad: String, soyad: String, email: String, onResult: (Boolean, String?) -> Unit) {
        val user = hashMapOf(
            "ad" to ad,
            "soyad" to soyad,
            "email" to email,
            "usedFreeTrial" to false,
            "premium" to false
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

    fun signInWithGoogle(
        credential: AuthCredential,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { authResult ->
                if (authResult.isSuccessful) {
                    val user = auth.currentUser
                    val userRef = firestore.collection("users").document(user!!.uid)

                    userRef.get().addOnSuccessListener { document ->
                        if (!document.exists()) {
                            val userData = hashMapOf(
                                "ad" to (user.displayName ?: ""),
                                "soyad" to "",
                                "email" to (user.email ?: ""),
                                "usedFreeTrial" to false,
                                "premium" to false,
                                "remainingChatgptUses" to 0
                            )

                            userRef.set(userData)
                                .addOnSuccessListener {
                                    onResult(true, "Google ile giriş başarılı")
                                }
                                .addOnFailureListener { e ->
                                    onResult(false, "Kullanıcı kaydedilemedi: ${e.message}")
                                }
                        } else {
                            onResult(true, "Google ile giriş başarılı (kayıtlı kullanıcı)")
                        }
                    }.addOnFailureListener {
                        onResult(false, "Kullanıcı bilgisi alınamadı: ${it.message}")
                    }
                } else {
                    onResult(false, "Google giriş başarısız: ${authResult.exception?.message}")
                }
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

    suspend fun signOut(context: Context) = withContext(Dispatchers.IO) {
        auth.signOut()

        // Google çıkışı da yapılıyor
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)

        googleSignInClient.signOut().await() // Bu işlem Task döner, suspend kullanabilmek için await() lazım
    }

    suspend fun deleteStory(userId: String, storyId: String, onResult: (Boolean, String?) -> Unit) = withContext(Dispatchers.IO){
        firestore.collection("users")
            .document(userId) // Kullanıcı ID'si
            .collection("hikayeler") // Hikayeler koleksiyonu
            .document(storyId) // Silinecek hikaye ID'si
            .delete()
            .addOnSuccessListener {
                onResult(true, "Hikaye başarıyla silindi")
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }

    }

    suspend fun reauthenticateUser(password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = withContext(Dispatchers.IO){

        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user?.email ?: "", password)

        user?.reauthenticate(credential)
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { onFailure(it) }

    }

    fun decrementChatGptUse(userId: String, onComplete: (Boolean) -> Unit) {
        val userRef = firestore.collection("users").document(userId)


        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentUses = snapshot.getLong("remainingChatgptUses") ?: 0

            if (currentUses > 0) {
                transaction.update(userRef, "remainingChatgptUses", currentUses - 1)
            }
        }.addOnSuccessListener {
            onComplete(true)
        }.addOnFailureListener {
            onComplete(false)
        }
    }


}