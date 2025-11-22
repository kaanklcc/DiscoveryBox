package com.kaankilic.discoverybox.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.BuildConfig
import com.kaankilic.discoverybox.util.DeviceTrialManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugMenu(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val deviceTrialManager = remember { DeviceTrialManager(context) }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    
    var deviceId by remember { mutableStateOf("") }
    var deviceUsed by remember { mutableStateOf(false) }
    var userTrialUsed by remember { mutableStateOf(false) }
    var remainingUses by remember { mutableStateOf(0) }
    var isPremium by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    suspend fun loadTrialInfo() {
        isLoading = true
        try {
            deviceUsed = deviceTrialManager.hasDeviceUsedTrial()
            
            auth.currentUser?.uid?.let { userId ->
                val userDoc = firestore.collection("users").document(userId).get().await()
                userTrialUsed = userDoc.getBoolean("usedFreeTrial") ?: false
                remainingUses = userDoc.getLong("remainingChatgptUses")?.toInt() ?: 0
                isPremium = userDoc.getBoolean("premium") ?: false
            }
        } finally {
            isLoading = false
        }
    }
    
    LaunchedEffect(Unit) {
        deviceId = deviceTrialManager.getDeviceId()
        loadTrialInfo()
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🧪 DEBUG MENU",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFCD34D)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
                
                Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                
                // Test Mode Status
                InfoCard(
                    title = "Test Modu",
                    value = if (BuildConfig.TEST_MODE) "🟢 AÇIK" else "🔴 KAPALI",
                    color = if (BuildConfig.TEST_MODE) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                
                Spacer(Modifier.height(8.dp))
                
                // Device Info
                InfoCard(
                    title = "Cihaz ID",
                    value = deviceId.take(16) + "...",
                    color = Color(0xFF3B82F6)
                )
                
                InfoCard(
                    title = "Cihaz Deneme Kullanılmış",
                    value = if (deviceUsed) "✅ EVET" else "❌ HAYIR",
                    color = if (deviceUsed) Color(0xFFEF4444) else Color(0xFF10B981)
                )
                
                Spacer(Modifier.height(8.dp))
                
                // User Info
                InfoCard(
                    title = "Kullanıcı Deneme Kullanılmış",
                    value = if (userTrialUsed) "✅ EVET" else "❌ HAYIR",
                    color = if (userTrialUsed) Color(0xFFEF4444) else Color(0xFF10B981)
                )
                
                InfoCard(
                    title = "Kalan Hak",
                    value = remainingUses.toString(),
                    color = Color(0xFF0055AA)
                )
                
                InfoCard(
                    title = "Premium",
                    value = if (isPremium) "✅ EVET" else "❌ HAYIR",
                    color = if (isPremium) Color(0xFFFCD34D) else Color.Gray
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Actions
                Button(
                    onClick = {
                        scope.launch {
                            deviceTrialManager.logDeviceTrialInfo()
                            Toast.makeText(context, "Logcat'e bakın!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                ) {
                    Text("📋 Logcat'e Yazdır")
                }
                
                Spacer(Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            loadTrialInfo()
                            Toast.makeText(context, "Yenilendi!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("🔄 Bilgileri Yenile")
                }
                
                Spacer(Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            deviceTrialManager.resetDeviceTrial()
                            loadTrialInfo()
                            Toast.makeText(context, "Cihaz kaydı silindi! Yeni hesap açabilirsiniz.", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("🗑️ Cihaz Kaydını Sıfırla")
                }
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    "⚠️ Bu menü sadece test içindir!",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 14.sp, color = Color.White)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
    Spacer(Modifier.height(4.dp))
}
