package com.kaankilic.discoverybox.util

import android.content.Context
import android.provider.Settings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Device ID bazlı deneme kontrolü
 * Aynı cihazdan birden fazla hesap açılmasını engellemek için
 */
class DeviceTrialManager(private val context: Context) {
    
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Cihazın benzersiz ID'sini döndürür (Android ID)
     */
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
    
    /**
     * Bu cihazdan daha önce deneme kullanılıp kullanılmadığını kontrol eder
     */
    suspend fun hasDeviceUsedTrial(): Boolean {
        val deviceId = getDeviceId()
        val doc = firestore.collection("device_trials")
            .document(deviceId)
            .get()
            .await()
        
        return doc.exists() && doc.getBoolean("used") == true
    }
    
    /**
     * Cihazın deneme hakkını kullandığını işaretler
     */
    suspend fun markDeviceTrialUsed(userId: String) {
        val deviceId = getDeviceId()
        val updateData = hashMapOf(
            "used" to true,
            "used_at" to com.google.firebase.Timestamp.now()
        )
        
        firestore.collection("device_trials")
            .document(deviceId)
            .update(updateData as Map<String, Any>)
            .await()
    }
    
    /**
     * Kullanıcının deneme durumunu kontrol eder
     * @return Pair<Boolean, String> (canUseTrial, message)
     */
    suspend fun checkTrialEligibility(userId: String): Pair<Boolean, String> {
        // 1. Cihaz bazlı kontrol
        val deviceUsedTrial = hasDeviceUsedTrial()
        
        // 2. Kullanıcı bazlı kontrol (Firestore'dan)
        val userDoc = firestore.collection("users")
            .document(userId)
            .get()
            .await()
        
        val isPremium = userDoc.getBoolean("premium") ?: false
        val usedFreeTrial = userDoc.getBoolean("usedFreeTrial") ?: false
        
        return when {
            isPremium -> Pair(true, "Premium kullanıcı")
            deviceUsedTrial -> Pair(false, "Bu cihazdan daha önce deneme hakkı kullanılmış")
            usedFreeTrial -> Pair(false, "Bu hesapla daha önce deneme hakkı kullanılmış")
            else -> Pair(true, "Deneme hakkı kullanılabilir")
        }
    }
    
    /**
     * İlk kayıt sırasında cihaz ve kullanıcıyı birbirine bağlar
     */
    suspend fun registerDeviceForUser(userId: String, isFirstTime: Boolean = true) {
        if (isFirstTime) {
            // Cihazın daha önce kullanılıp kullanılmadığını kontrol et
            val deviceUsedTrial = hasDeviceUsedTrial()
            
            if (!deviceUsedTrial) {
                // Cihazı işaretle ama henüz kullanılmış olarak işaretleme
                val deviceId = getDeviceId()
                val trialData = hashMapOf(
                    "device_id" to deviceId,
                    "user_id" to userId,
                    "used" to false,
                    "registered_at" to com.google.firebase.Timestamp.now()
                )
                
                firestore.collection("device_trials")
                    .document(deviceId)
                    .set(trialData)
                    .await()
            }
        }
    }
    
    // ========== DEBUG/TEST FONKSİYONLARI ==========
    
    /**
     * 🧪 TEST: Cihaz deneme kaydını sıfırlar (sadece test için!)
     */
    suspend fun resetDeviceTrial() {
        val deviceId = getDeviceId()
        firestore.collection("device_trials")
            .document(deviceId)
            .delete()
            .await()
        android.util.Log.w("DeviceTrialManager", "🔄 Cihaz deneme kaydı silindi: $deviceId")
    }
    
    /**
     * 🧪 TEST: Cihaz bilgilerini loglar
     */
    suspend fun logDeviceTrialInfo() {
        val deviceId = getDeviceId()
        val doc = firestore.collection("device_trials")
            .document(deviceId)
            .get()
            .await()
        
        android.util.Log.i("DeviceTrialManager", "📱 Device ID: $deviceId")
        android.util.Log.i("DeviceTrialManager", "📊 Kayıt var mı: ${doc.exists()}")
        if (doc.exists()) {
            android.util.Log.i("DeviceTrialManager", "✅ Kullanılmış mı: ${doc.getBoolean("used")}")
            android.util.Log.i("DeviceTrialManager", "👤 User ID: ${doc.getString("user_id")}")
            android.util.Log.i("DeviceTrialManager", "📅 Kayıt tarihi: ${doc.getTimestamp("registered_at")}")
        }
    }
}

/**
 * Deneme durumu sonuçları
 */
sealed class TrialStatus {
    object Available : TrialStatus()
    data class AlreadyUsed(val reason: String) : TrialStatus()
    data class Error(val message: String) : TrialStatus()
}

