package com.kaankilic.discoverybox.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.R
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilSayfa(navController: NavController) {
    val context = LocalContext.current
    val sandtitle = FontFamily(Font(R.font.sandtitle))
    val andikabody = FontFamily(Font(R.font.andikabody))
    
    var userName by remember { mutableStateOf("") }
    var isPremium by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showTermsOfUse by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(3) }
    
    // Kullanıcı bilgilerini yükle
    LaunchedEffect(Unit) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            try {
                val doc = Firebase.firestore.collection("users").document(userId).get().await()
                userName = doc.getString("ad") ?: "Kullanıcı"
                isPremium = doc.getBoolean("premium") ?: false
            } catch (e: Exception) {
                userName = "Kullanıcı"
            }
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.profile),
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
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF003366)
                )
            )
        },
        bottomBar = {
            CommonBottomBar(
                navController = navController,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF003366),
                            Color(0xFF004080),
                            Color(0xFF0055AA)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Profil Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(R.drawable.parskedi),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Kullanıcı Adı
            Text(
                text = userName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sandtitle,
                color = Color.White
            )
            
            // Premium Durumu
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPremium) Color(0xFFFCD34D).copy(alpha = 0.3f) 
                    else Color(0xFF0055AA).copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(if (isPremium) R.drawable.crown else R.drawable.starimage),
                            contentDescription = null,
                            tint = if (isPremium) Color(0xFFFCD34D) else Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = stringResource(R.string.account_status),
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontFamily = andikabody
                            )
                            Text(
                                text = if (isPremium) stringResource(R.string.premium_member) 
                                else stringResource(R.string.free_member),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = sandtitle
                            )
                        }
                    }
                    if (!isPremium) {
                        Button(
                            onClick = { navController.navigate("premium") },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.upgrade),
                                color = Color(0xFF003366),
                                fontFamily = sandtitle
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Gizlilik Politikası
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.privacy_policy),
                onClick = { showPrivacyPolicy = true }
            )
            
            // Kullanım Şartları
            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.terms_of_use),
                onClick = { showTermsOfUse = true }
            )
            
            // AI Kullanımı Bilgilendirmesi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFFCD34D), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFCD34D).copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFFCD34D),
                        modifier = Modifier.size(24.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.ai_disclosure_title),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = sandtitle
                        )
                        Text(
                            text = stringResource(R.string.ai_disclosure_message),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontFamily = andikabody,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Çıkış Yap
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFF003366)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.logout),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = sandtitle,
                    color = Color(0xFF003366)
                )
            }
            
            // Hesabı Sil
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.delete_account),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = sandtitle,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Çıkış Onay Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    stringResource(R.string.logout_confirmation_title),
                    fontWeight = FontWeight.Bold,
                    fontFamily = sandtitle
                )
            },
            text = {
                Text(
                    stringResource(R.string.logout_confirmation_message),
                    fontFamily = andikabody
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("girisSayfa") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF003366))
                ) {
                    Text(stringResource(R.string.yes), fontFamily = andikabody)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.no), fontFamily = andikabody)
                }
            }
        )
    }
    
    // Hesap Silme Onay Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    stringResource(R.string.delete_account_title),
                    fontWeight = FontWeight.Bold,
                    fontFamily = sandtitle,
                    color = Color(0xFFEF4444)
                )
            },
            text = {
                Text(
                    stringResource(R.string.delete_account_message),
                    fontFamily = andikabody
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = Firebase.auth.currentUser?.uid
                        if (userId != null) {
                            // Firestore'dan kullanıcı verilerini sil
                            Firebase.firestore.collection("users").document(userId).delete()
                            // Firebase Auth'dan hesabı sil
                            Firebase.auth.currentUser?.delete()?.addOnSuccessListener {
                                navController.navigate("girisSayfa") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFFEF4444))
                ) {
                    Text(stringResource(R.string.delete), fontFamily = andikabody)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel), fontFamily = andikabody)
                }
            }
        )
    }
    
    // Gizlilik Politikası WebView
    if (showPrivacyPolicy) {
        WebViewDialog(
            title = stringResource(R.string.privacy_policy),
            htmlContent = getPrivacyPolicyHtml(),
            onDismiss = { showPrivacyPolicy = false }
        )
    }
    
    // Kullanım Şartları WebView
    if (showTermsOfUse) {
        WebViewDialog(
            title = stringResource(R.string.terms_of_use),
            htmlContent = getTermsOfUseHtml(),
            onDismiss = { showTermsOfUse = false }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0055AA).copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFFFCD34D),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun PolicyDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    content,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat")
            }
        }
    )
}

@Composable
fun getPrivacyPolicyText(): String {
    return """
GIZLILIK POLITIKASI

Son Güncelleme: ${java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date())}

Discovery Box ("biz", "bizim" veya "uygulama"), kullanıcılarımızın gizliliğini korumayı taahhüt eder. Bu Gizlilik Politikası, Discovery Box mobil uygulamasını kullandığınızda kişisel bilgilerinizin nasıl toplandığını, kullanıldığını ve korunduğunu açıklar.

1. TOPLANAN BİLGİLER

1.1 Kişisel Bilgiler
• Ad ve Soyad
• E-posta adresi
• Google hesap bilgileri (Google ile giriş yapıldığında)

1.2 Kullanım Bilgileri
• Oluşturulan hikayeler
• Uygulama kullanım istatistikleri
• Cihaz bilgileri (model, işletim sistemi)

1.3 Premium Üyelik Bilgileri
• Satın alma geçmişi
• Abonelik durumu

2. BİLGİLERİN KULLANIMI

Topladığımız bilgiler şu amaçlarla kullanılır:
• Hesap oluşturma ve yönetimi
• Hikaye oluşturma ve saklama hizmetlerinin sağlanması
• Premium üyelik hizmetlerinin yönetimi
• Uygulama performansının iyileştirilmesi
• Kullanıcı deneyiminin kişiselleştirilmesi
• Teknik destek sağlanması

3. BİLGİLERİN SAKLANMASI

• Verileriniz Firebase (Google Cloud) sunucularında güvenli bir şekilde saklanır
• Veriler şifreleme teknolojileri ile korunur
• Hesabınızı sildiğinizde tüm verileriniz kalıcı olarak silinir

4. BİLGİLERİN PAYLAŞIMI

Kişisel bilgileriniz aşağıdaki durumlar dışında üçüncü taraflarla paylaşılmaz:
• Yasal zorunluluklar
• Kullanıcının açık izni
• Hizmet sağlayıcılar (Firebase, Google Play)

5. ÇOCUKLARIN GİZLİLİĞİ

Uygulamamız çocuklar için tasarlanmıştır. Ebeveyn veya vasi gözetimi önerilir. 13 yaşından küçük çocukların verilerini bilerek toplamayız.

6. GÜVENLİK

Verilerinizin güvenliğini sağlamak için endüstri standardı güvenlik önlemleri kullanırız:
• SSL/TLS şifreleme
• Firebase Authentication
• Güvenli veri depolama

7. KULLANICI HAKLARI

Kullanıcılarımız şu haklara sahiptir:
• Kişisel verilerine erişim
• Verilerin düzeltilmesi
• Verilerin silinmesi
• Hesabın kapatılması

8. ÇEREZLER VE İZLEME

Uygulama, kullanıcı deneyimini iyileştirmek için Firebase Analytics kullanır. Bu hizmetler anonim kullanım verileri toplar.

9. DEĞİŞİKLİKLER

Bu Gizlilik Politikası zaman zaman güncellenebilir. Önemli değişiklikler uygulama içinde bildirilecektir.

10. İLETİŞİM

Gizlilik politikamız hakkında sorularınız için:
E-posta: kan.klc.1903@gmail.com

Bu politikayı kabul ederek, yukarıda belirtilen şartları okuduğunuzu ve anladığınızı onaylarsınız.
    """.trimIndent()
}

@Composable
fun getTermsOfUseText(): String {
    return """
KULLANIM ŞARTLARI

Son Güncelleme: ${java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date())}

Discovery Box mobil uygulamasını kullanarak aşağıdaki şartları kabul etmiş olursunuz.

1. HİZMET TANIMI

Discovery Box, yapay zeka destekli çocuk hikayeleri oluşturma ve dinleme platformudur. Uygulama, kullanıcıların özel hikayeler oluşturmasına, kaydetmesine ve dinlemesine olanak tanır.

2. KULLANICI HESABI

2.1 Hesap Oluşturma
• Hesap oluşturmak için geçerli bir e-posta adresi gereklidir
• Google hesabı ile giriş yapılabilir
• Kullanıcılar hesap bilgilerinin güvenliğinden sorumludur

2.2 Hesap Güvenliği
• Şifrenizi kimseyle paylaşmayın
• Şüpheli aktivite durumunda derhal bildirin
• Hesabınızdan yapılan tüm işlemlerden siz sorumlusunuz

3. PREMIUM ÜYELİK

3.1 Premium Özellikler
• Sınırsız hikaye oluşturma
• Yapay zeka destekli görsel oluşturma
• Premium ses kalitesi (GPT-TTS)
• Reklamsız deneyim

3.2 Ödeme ve İptal
• Premium üyelik Google Play Store üzerinden satın alınır
• İptal işlemleri Google Play Store ayarlarından yapılır
• İade politikası Google Play Store şartlarına tabidir

4. İÇERİK KULLANIMI

4.1 Oluşturulan Hikayeler
• Oluşturduğunuz hikayeler size aittir
• Hikayeleri kişisel kullanım için saklayabilirsiniz
• Ticari amaçla kullanım yasaktır

4.2 Yasak İçerik
Aşağıdaki içeriklerin oluşturulması yasaktır:
• Şiddet içeren içerik
• Uygunsuz veya yetişkin içeriği
• Nefret söylemi
• Telif hakkı ihlali
• Yanıltıcı veya zararlı içerik

5. YAPAY ZEKA KULLANIMI

• Hikayeler yapay zeka (Gemini, GPT) tarafından oluşturulur
• Oluşturulan içeriğin kalitesi ve uygunluğu garanti edilmez
• Kullanıcılar oluşturulan içeriği kontrol etmekle sorumludur

6. HİZMET SINIRLAMALARI

• Hizmet kesintileri yaşanabilir
• Özellikler önceden haber verilmeksizin değiştirilebilir
• Kötüye kullanım durumunda hesap askıya alınabilir

7. FİKRİ MÜLKİYET

• Uygulama tasarımı ve kodu Discovery Box'a aittir
• Uygulama logosu ve marka telif hakkı koruması altındadır
• İzinsiz kopyalama yasaktır

8. SORUMLULUK REDDİ

• Uygulama "olduğu gibi" sunulur
• Hizmet kesintilerinden sorumlu değiliz
• Veri kaybından sorumlu değiliz
• Üçüncü taraf hizmetlerden (Firebase, Google) sorumlu değiliz

9. HESAP ASKIYA ALMA VE SONLANDIRMA

Aşağıdaki durumlarda hesabınız askıya alınabilir veya sonlandırılabilir:
• Kullanım şartlarının ihlali
• Yasak içerik oluşturma
• Kötüye kullanım
• Yasal zorunluluklar

10. DEĞİŞİKLİKLER

Bu Kullanım Şartları zaman zaman güncellenebilir. Önemli değişiklikler uygulama içinde bildirilecektir.

11. UYGULANACAK HUKUK

Bu şartlar Türkiye Cumhuriyeti yasalarına tabidir.

12. İLETİŞİM

Kullanım şartları hakkında sorularınız için:
E-posta: kan.klc.1903@gmail.com

Bu şartları kabul ederek, yukarıda belirtilen tüm maddeleri okuduğunuzu ve anladığınızı onaylarsınız.
    """.trimIndent()
}
