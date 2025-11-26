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
    var selectedPackage by remember { mutableStateOf("monthly") }
    val scope = rememberCoroutineScope()
    var isPurchasing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsOfUse by remember { mutableStateOf(false) }
    
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "•",
                        fontSize = 16.sp,
                        color = Color(0xFFFCD34D),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Gemini ile kişiselleştirilmiş hikaye üretimi",
                        fontSize = 16.sp,
                        fontFamily = andikabody,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "•",
                        fontSize = 16.sp,
                        color = Color(0xFFFCD34D),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Gemini Nano ile AI görsel üretimi",
                        fontSize = 16.sp,
                        fontFamily = andikabody,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "•",
                        fontSize = 16.sp,
                        color = Color(0xFFFCD34D),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "OpenAI GPT-TTS ile premium ses",
                        fontSize = 16.sp,
                        fontFamily = andikabody,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "•",
                        fontSize = 16.sp,
                        color = Color(0xFFFCD34D),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Reklamsız kullanım",
                        fontSize = 16.sp,
                        fontFamily = andikabody,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PremiumPackageCard(
                    title = "Haftalık Paket",
                    price = "₺199.99",
                    duration = "7 gün",
                    stories = "7 jeton",
                    isSelected = selectedPackage == "weekly",
                    onClick = { selectedPackage = "weekly" },
                    fontFamily = sandtitle,
                    bodyFont = andikabody
                )
                
                Box {
                    Column {
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .offset(x = (-12).dp, y = 6.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFBBF24))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "🔥 %65 İNDİRİM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = sandtitle,
                                color = Color(0xFF0055AA)
                            )
                        }
                        
                        PremiumPackageCard(
                            title = "Aylık Paket",
                            price = "₺699.99",
                            duration = "30 gün",
                            stories = "30 jeton",
                            isSelected = selectedPackage == "monthly",
                            onClick = { selectedPackage = "monthly" },
                            fontFamily = sandtitle,
                            bodyFont = andikabody,
                            highlight = true
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                        "usedFreeTrial" to true
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
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBBF24)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isPurchasing
                ) {
                    if (isPurchasing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF0055AA)
                        )
                    } else {
                        Text(
                            "Pro'yu Satın Al - ${if (selectedPackage == "weekly") "₺199.99" else "₺699.99"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sandtitle,
                            color = Color(0xFF0055AA)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    "Abonelik otomatik yenilenir. Google Play → Ödemeler'den iptal edebilirsiniz.",
                    fontSize = 10.sp,
                    fontFamily = andikabody,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Terms of Use",
                        fontSize = 11.sp,
                        fontFamily = andikabody,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.clickable { showTermsOfUse = true }
                    )
                    Text(
                        "Privacy Policy",
                        fontSize = 11.sp,
                        fontFamily = andikabody,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.clickable { showPrivacyPolicy = true }
                    )
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFCD34D).copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFCD34D).copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            "⚠️ AI İçerik Bildirimi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = sandtitle
                        )
                        Text(
                            "Hikaye, görsel ve ses AI ile üretilir. Uygunsuz içerik için 'Report' kullan. Support: kan.klc.1903@gmail.com",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontFamily = andikabody,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
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
    
    if (showPrivacyPolicy) {
        WebViewDialog(
            title = "Privacy Policy",
            htmlContent = getPrivacyPolicyHtml(),
            onDismiss = { showPrivacyPolicy = false }
        )
    }
    
    if (showTermsOfUse) {
        WebViewDialog(
            title = "Terms of Use",
            htmlContent = getTermsOfUseHtml(),
            onDismiss = { showTermsOfUse = false }
        )
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
            .clip(RoundedCornerShape(12.dp))
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
                width = if (highlight) 2.dp else 0.dp,
                color = if (highlight) Color(0xFFFBBF24) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = fontFamily
                )
                Text(
                    duration,
                    fontSize = 12.sp,
                    color = if (isSelected) Color(0xFF0055AA).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f),
                    fontFamily = bodyFont
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    stories,
                    fontSize = 13.sp,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = bodyFont
                )
                Text(
                    "AI Görsel",
                    fontSize = 13.sp,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = bodyFont
                )
                Text(
                    "Premium Ses",
                    fontSize = 13.sp,
                    color = if (isSelected) Color(0xFF0055AA) else Color.White,
                    fontFamily = bodyFont
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seçili",
                    tint = Color(0xFF0055AA),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}


