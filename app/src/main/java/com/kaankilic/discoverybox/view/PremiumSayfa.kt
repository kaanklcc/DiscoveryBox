package com.kaankilic.discoverybox.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSayfa(navController: NavController) {
    val sandtitle = FontFamily(Font(R.font.sandtitle))
    val andikabody = FontFamily(Font(R.font.andikabody))
    var selectedPackage by remember { mutableStateOf("weekly") } // "weekly" veya "monthly"
    val scope = rememberCoroutineScope()
    var isPurchasing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "⭐ Premium Paketler",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF003366)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF003366),
                            Color(0xFF004080),
                            Color(0xFF0055AA)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Premium Özellikleri
            Text(
                "Premium ile neler yapabilirsiniz?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sandtitle,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Özellik Kartları
            PremiumFeatureCard(
                icon = "✨",
                title = "AI Görsel Oluşturma",
                description = "Hikayenize özel yapay zeka ile görsel oluşturun",
                fontFamily = andikabody
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PremiumFeatureCard(
                icon = "🎵",
                title = "Premium Ses",
                description = "GPT-4 TTS ile profesyonel ses deneyimi",
                fontFamily = andikabody
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            PremiumFeatureCard(
                icon = "📖",
                title = "Sınırsız Hikaye",
                description = "Paketinize göre dilediğiniz kadar hikaye oluşturun",
                fontFamily = andikabody
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Paket Seçin",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sandtitle,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Haftalık Paket
            PremiumPackageCard(
                title = "Haftalık Paket",
                price = "₺29.99",
                duration = "7 gün",
                stories = "7 hikaye hakkı",
                isSelected = selectedPackage == "weekly",
                onClick = { selectedPackage = "weekly" },
                fontFamily = sandtitle,
                bodyFont = andikabody
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Aylık Paket (Popüler)
            Box {
                Column {
                    // "Popüler" rozeti
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                            .offset(x = (-16).dp, y = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFBBF24))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "🔥 POPÜLER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sandtitle,
                            color = Color(0xFF0055AA)
                        )
                    }
                    
                    PremiumPackageCard(
                        title = "Aylık Paket",
                        price = "₺79.99",
                        duration = "30 gün",
                        stories = "30 hikaye hakkı",
                        isSelected = selectedPackage == "monthly",
                        onClick = { selectedPackage = "monthly" },
                        fontFamily = sandtitle,
                        bodyFont = andikabody,
                        highlight = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Satın Al Butonu
            Button(
                onClick = {
                    isPurchasing = true
                    scope.launch {
                        try {
                            val userId = Firebase.auth.currentUser?.uid
                            if (userId != null) {
                                val userRef = Firebase.firestore.collection("users").document(userId)
                                
                                val updates = hashMapOf<String, Any>(
                                    "premium" to true,
                                    "premiumStartDate" to com.google.firebase.Timestamp.now(),
                                    "premiumDurationDays" to if (selectedPackage == "weekly") 7L else 30L,
                                    "remainingChatgptUses" to if (selectedPackage == "weekly") 7L else 30L,
                                    "usedFreeTrial" to true // Premium aldığında deneme hakkı tüketilmiş sayılır
                                )
                                
                                userRef.update(updates).addOnSuccessListener {
                                    isPurchasing = false
                                    showSuccessDialog = true
                                }.addOnFailureListener {
                                    isPurchasing = false
                                }
                            }
                        } catch (e: Exception) {
                            isPurchasing = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBBF24)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = !isPurchasing
            ) {
                if (isPurchasing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF0055AA)
                    )
                } else {
                    Text(
                        "Satın Al - ${if (selectedPackage == "weekly") "₺29.99" else "₺79.99"}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle,
                        color = Color(0xFF0055AA)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "💡 İpucu: Aylık paket ile %40 daha fazla tasarruf edin!",
                fontSize = 12.sp,
                fontFamily = andikabody,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Başarı Dialog'u
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                navController.popBackStack()
            },
            title = {
                Text(
                    "🎉 Satın Alma Başarılı!",
                    fontFamily = sandtitle
                )
            },
            text = {
                Text(
                    "Premium üyeliğiniz aktif edildi. Artık tüm özelliklere erişebilirsiniz!",
                    fontFamily = andikabody
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Tamam", fontFamily = sandtitle)
                }
            }
        )
    }
}

@Composable
fun PremiumFeatureCard(
    icon: String,
    title: String,
    description: String,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            icon,
            fontSize = 32.sp,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = fontFamily
            )
            Text(
                description,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = fontFamily
            )
        }
    }
}

@Composable
fun PremiumPackageCard(
    title: String,
    price: String,
    duration: String,
    stories: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    fontFamily: FontFamily,
    bodyFont: FontFamily,
    highlight: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.1f))
                    )
                }
            )
            .border(
                width = if (highlight) 3.dp else 0.dp,
                color = if (highlight) Color(0xFFFBBF24) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = fontFamily
                )
                Text(
                    price,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = fontFamily
                )
                Text(
                    duration,
                    fontSize = 13.sp,
                    color = if (isSelected) Color(0xFF0055AA).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f),
                    fontFamily = bodyFont
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "✅ $stories",
                    fontSize = 14.sp,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = bodyFont
                )
                Text(
                    "✅ AI Görsel",
                    fontSize = 14.sp,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = bodyFont
                )
                Text(
                    "✅ Premium Ses",
                    fontSize = 14.sp,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = bodyFont
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seçili",
                    tint = Color(0xFF0055AA),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


