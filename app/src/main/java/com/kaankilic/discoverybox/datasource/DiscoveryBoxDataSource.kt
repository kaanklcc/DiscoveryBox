package com.kaankilic.discoverybox.datasource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.kaankilic.discoverybox.BuildConfig
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.entitiy.ImageRequest
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.entitiy.TTSRequest

import com.kaankilic.discoverybox.retrofit.api
import com.kaankilic.discoverybox.util.getTodayDateString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale



class DiscoveryBoxDataSource(var firestore : FirebaseFirestore, var auth: FirebaseAuth, private val context: Context ) {
    //val firestore = FirebaseFirestore.getInstance()
    //val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var tts: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    private val deviceTrialManager = com.kaankilic.discoverybox.util.DeviceTrialManager(context)
   /* suspend fun GetAllGame(): List<Story> = withContext(Dispatchers.IO){
        return@withContext getAllGames()
    }*/

    private val textGenerativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY
    )



    suspend fun generateImageWithGemini(prompt: String): Bitmap? {
        // TEST MODU: Gerçek API çağrısı yapmadan default görsel döndür
        if (com.kaankilic.discoverybox.BuildConfig.TEST_MODE) {
            Log.w("TEST_MODE", "🧪 Görsel oluşturma: DEFAULT GÖRSEL kullanılıyor (Gemini Image API atlandı)")
            kotlinx.coroutines.delay(500) // API simülasyonu
            return getDefaultImage(context)
        }
        
        Log.i("PRODUCTION", "✅ Görsel oluşturma: Gemini Image API kullanılıyor")
        
        val enhancedPrompt = "Generate an image: $prompt. Style: professional children's book illustration, digital art, vibrant colors, fantasy style. No text, no words, no letters, no page borders, pure illustration only."
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                val jsonBody = JSONObject().apply {
                    put("contents", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", org.json.JSONArray().apply {
                                put(JSONObject().put("text", enhancedPrompt))
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("responseModalities", org.json.JSONArray().apply {
                            put("IMAGE")
                            put("TEXT")
                        })
                    })
                }.toString()

                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    jsonBody
                )

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "")
                    
                    // 💰 Maliyet hesapla (usageMetadata varsa)
                    try {
                        val usageMetadata = jsonResponse.optJSONObject("usageMetadata")
                        if (usageMetadata != null) {
                            val totalTokens = usageMetadata.optInt("totalTokenCount", 0)
                            if (totalTokens > 0) {
                                com.kaankilic.discoverybox.util.ApiCostTracker.calculateImageCost(totalTokens)
                                Log.i("GeminiImage", "✅ Gerçek token sayısı: $totalTokens")
                            } else {
                                Log.w("GeminiImage", "⚠️ usageMetadata var ama token=0, response'u kontrol edin")
                            }
                        } else {
                            Log.w("GeminiImage", "⚠️ usageMetadata yok, maliyet hesaplanamadı")
                        }
                    } catch (e: Exception) {
                        Log.w("GeminiImage", "Maliyet hesaplanamadı: ${e.message}")
                    }
                    
                    val candidates = jsonResponse.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        
                        if (parts != null) {
                            for (i in 0 until parts.length()) {
                                val part = parts.getJSONObject(i)
                                val inlineData = part.optJSONObject("inlineData")
                                val imageData = inlineData?.optString("data")
                                
                                if (!imageData.isNullOrEmpty()) {
                                    val decodedBytes = android.util.Base64.decode(imageData, android.util.Base64.DEFAULT)
                                    return@withContext BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                }
                            }
                        }
                    }
                    Log.e("GeminiImage", "No image data in response")
                } else {
                    Log.e("GeminiImage", "Response failed: ${response.code} - ${response.message}")
                }
                null
            } catch (e: Exception) {
                Log.e("GeminiImage", "Error: ${e.message}", e)
                null
            }
        }
    }
    suspend fun decrementRemainingChatgptUses() {
        val userId = auth.currentUser?.uid ?: return
        val userRef = firestore.collection("users").document(userId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val current = snapshot.getLong("remainingChatgptUses") ?: 0
            if (current > 0) {
                transaction.update(userRef, "remainingChatgptUses", current - 1)
            }
        }.await()
    }


    fun getDefaultImage(context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.story)
    }

    suspend fun generateStory(prompt: String): String = withContext(Dispatchers.IO) {
        // TEST MODU: Gerçek API çağrısı yapmadan mock data döndür
        if (com.kaankilic.discoverybox.BuildConfig.TEST_MODE) {
            Log.w("TEST_MODE", "🧪 Hikaye oluşturma: MOCK DATA kullanılıyor (Gemini API atlandı)")
            kotlinx.coroutines.delay(1000) // API çağrısı simülasyonu
            return@withContext when {
                prompt.contains("Uzunluk: Uzun", ignoreCase = true) || prompt.contains("Uzunluk: long", ignoreCase = true) -> 
                    com.kaankilic.discoverybox.util.MockData.getMockLongStory(prompt)
                prompt.contains("Uzunluk: Kısa", ignoreCase = true) || prompt.contains("Uzunluk: short", ignoreCase = true) -> 
                    com.kaankilic.discoverybox.util.MockData.getMockShortStory(prompt)
                else -> com.kaankilic.discoverybox.util.MockData.getMockStory(prompt)
            }
        }
        
        Log.i("PRODUCTION", "✅ Hikaye oluşturma: Gemini API kullanılıyor")
        
        try {
            val response = textGenerativeModel.generateContent(prompt)
            
            // 💰 Maliyet hesapla ve logla
            com.kaankilic.discoverybox.util.ApiCostTracker.calculateTextCost(response)
            
            return@withContext response.text ?: ""
        } catch (e: Exception) {
            Log.e("GeminiStory", "Error: ${e.message}")
            return@withContext "Hikaye oluşturulurken bir hata oluştu."
        }
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
    
    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
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
        // TEST MODU: Gerçek TTS API çağrısı yapmadan Google TTS kullan
        if (com.kaankilic.discoverybox.BuildConfig.TEST_MODE) {
            Log.w("TEST_MODE", "🧪 Ses oluşturma: GOOGLE TTS kullanılıyor (OpenAI TTS atlandı)")
            return@withContext "TEST MODU: Google TTS kullanılıyor (GPT TTS atlandı)"
        }
        
        Log.i("PRODUCTION", "✅ Ses oluşturma: OpenAI TTS kullanılıyor")
        
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
                // NOT: Hak azaltma kaldırıldı - sadece hikaye başlangıcında azaltılıyor

                return@withContext "GPT TTS başarıyla başlatıldı"
            }
        }
        return@withContext "Hata: ${response.errorBody()?.string()}"
    }

    fun saveImageToStorage(bitmap: Bitmap, userId: String,  onResult: (Boolean, String?) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${userId}/${System.currentTimeMillis()}.jpg")

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
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
        CoroutineScope(Dispatchers.IO).launch {
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

    }
    
    fun saveStoryForUserWithMultipleImages(title: String, story: String, imageUrls: List<String>, userId: String, onResult: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val storyData = mapOf(
                "title" to title,
                "hikaye" to story,
                "imageUrls" to imageUrls, // Birden fazla görsel URL'si
                "timestamp" to FieldValue.serverTimestamp()
            )

            firestore.collection("users")
                .document(userId)
                .collection("hikayeler")
                .add(storyData)
                .addOnSuccessListener {
                    Log.d("Firestore", "Hikaye başarıyla kaydedildi: ${imageUrls.size} görsel")
                    onResult(true)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Hikaye kaydetme hatası: ${e.message}")
                    onResult(false)
                }
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
        // Yeni kayıt için cihaz kontrolü
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deviceUsedTrial = deviceTrialManager.hasDeviceUsedTrial()
                
                val user = hashMapOf(
                    "ad" to ad,
                    "soyad" to soyad,
                    "email" to email,
                    "usedFreeTrial" to deviceUsedTrial, // Cihaz kullanılmışsa true
                    "premium" to false,
                    "remainingChatgptUses" to if (deviceUsedTrial) 0 else 1, // Cihaz yeniyse 1 hak
                    "premiumStartDate" to null,
                    "premiumDurationDays" to 0L,
                    "remainingFreeUses" to 0, // Başlangıçta reklam hakkı yok
                    "adsWatchedToday" to 0,
                    "maxAdsPerDay" to 2,
                    "lastFreeUseReset" to getTodayDateString(),
                    "deviceId" to deviceTrialManager.getDeviceId()
                )

                firestore.collection("users").document(userId)
                    .set(user)
                    .await()
                
                // Cihazı kaydet (henüz kullanılmış olarak işaretleme)
                if (!deviceUsedTrial) {
                    deviceTrialManager.registerDeviceForUser(userId, isFirstTime = true)
                }
                
                withContext(Dispatchers.Main) {
                    if (deviceUsedTrial) {
                        onResult(true, "Kayıt başarılı. Bu cihazdan daha önce deneme hakkı kullanılmış.")
                    } else {
                        onResult(true, "Kayıt başarılı! 1 ücretsiz deneme hakkınız var.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResult(false, e.message)
                }
            }
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
                            // Yeni kullanıcı - cihaz kontrolü yap
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    // Cihazın daha önce kullanılıp kullanılmadığını kontrol et
                                    val deviceUsedTrial = deviceTrialManager.hasDeviceUsedTrial()
                                    
                                    val userData = hashMapOf(
                                        "ad" to (user.displayName ?: ""),
                                        "soyad" to "",
                                        "email" to (user.email ?: ""),
                                        "usedFreeTrial" to deviceUsedTrial, // Cihaz kullanılmışsa true
                                        "premium" to false,
                                        "remainingChatgptUses" to if (deviceUsedTrial) 0 else 1, // Cihaz yeniyse 1 hak
                                        "premiumStartDate" to null,
                                        "premiumDurationDays" to 0L,
                                        "remainingFreeUses" to 0, // Başlangıçta reklam hakkı yok
                                        "adsWatchedToday" to 0,
                                        "maxAdsPerDay" to 2,
                                        "lastFreeUseReset" to getTodayDateString(),
                                        "deviceId" to deviceTrialManager.getDeviceId() // Device ID'yi kaydet
                                    )

                                    userRef.set(userData).await()
                                    
                                    // Yeni kullanıcıyı cihaza kaydet (henüz kullanılmış olarak işaretleme)
                                    if (!deviceUsedTrial) {
                                        deviceTrialManager.registerDeviceForUser(user.uid, isFirstTime = true)
                                    }
                                    
                                    withContext(Dispatchers.Main) {
                                        if (deviceUsedTrial) {
                                            onResult(true, "Giriş başarılı. Bu cihazdan daha önce deneme hakkı kullanılmış.")
                                        } else {
                                            onResult(true, "Google ile giriş başarılı! 1 ücretsiz deneme hakkınız var.")
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        onResult(false, "Kullanıcı kaydedilemedi: ${e.message}")
                                    }
                                }
                            }
                        } else {
                            // Mevcut kullanıcı - giriş başarılı
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

    fun rewardAfterAd(userId: String, onComplete: (Boolean, String) -> Unit) {
        val userRef = Firebase.firestore.collection("users").document(userId)
        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val today = getTodayDateString()
            val lastReset = snapshot.getString("lastFreeUseReset") ?: ""
            var adsWatchedToday = snapshot.getLong("adsWatchedToday") ?: 0
            var remainingFreeUses = snapshot.getLong("remainingFreeUses") ?: 0

            if (lastReset != today) {
                adsWatchedToday = 0
                remainingFreeUses = 1
            }

            if (adsWatchedToday >= 2) {
                throw Exception("Max ad rewards reached.")
            }

            transaction.update(userRef, mapOf(
                "adsWatchedToday" to adsWatchedToday + 1,
                "remainingFreeUses" to remainingFreeUses + 1,
                "lastFreeUseReset" to today
            ))
        }.addOnSuccessListener {
            onComplete(true, "Yeni bir hak kazandınız!")
        }.addOnFailureListener {
            onComplete(false, "Bugün daha fazla reklam izleyemezsiniz.")
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
                        imageUrls = (document.get("imageUrls") as? List<String>) ?: emptyList(),
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
                true // işlem yapıldı
            } else {
                false // hak zaten yoktu
            }
        }.addOnSuccessListener { success ->
            onComplete(success)
        }.addOnFailureListener {
            onComplete(false)
        }
    }
    
    /**
     * Cihazın deneme hakkını kullanıldı olarak işaretle
     */
    suspend fun markDeviceTrialAsUsed(userId: String) {
        deviceTrialManager.markDeviceTrialUsed(userId)
    }
    
    /**
     * Cihaz deneme durumunu kontrol et
     */
    suspend fun checkDeviceTrialEligibility(userId: String): Pair<Boolean, String> {
        return deviceTrialManager.checkTrialEligibility(userId)
    }

}