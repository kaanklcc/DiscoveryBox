package com.kaankilic.discoverybox.view


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.offset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Anasayfa(navController: NavController, anasayfaViewModel: AnasayfaViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val sandtitle = FontFamily(Font(R.font.sandtitle))
    val andikabody = FontFamily(Font(R.font.andikabody))
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1B4B),
                modifier = Modifier.height(90.dp)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (selectedTab == 0) Color(0xFFC084FC) else Color(0xFFE9D5FF)
                        )
                    },
                    label = { Text(stringResource(R.string.home), fontSize = 10.sp, color = Color(0xFFE9D5FF)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFC084FC),
                        unselectedIconColor = Color(0xFFE9D5FF),
                        indicatorColor = Color(0xFF7C3AED).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate("hikaye")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = "Create",
                            tint = if (selectedTab == 1) Color(0xFFF472B6) else Color(0xFFFCE7F3)
                        )
                    },
                    label = { Text(stringResource(R.string.create), fontSize = 10.sp, color = Color(0xFFFCE7F3)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF472B6),
                        unselectedIconColor = Color(0xFFFCE7F3),
                        indicatorColor = Color(0xFFEC4899).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        navController.navigate("saveSayfa")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Saved",
                            tint = if (selectedTab == 2) Color(0xFFFBBF24) else Color(0xFFFEF3C7)
                        )
                    },
                    label = { Text(stringResource(R.string.saved), fontSize = 10.sp, color = Color(0xFFFEF3C7)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFBBF24),
                        unselectedIconColor = Color(0xFFFEF3C7),
                        indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        Firebase.auth.signOut()
                        navController.navigate("girisSayfa") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = if (selectedTab == 3) Color(0xFF22D3EE) else Color(0xFFCFFAFE)
                        )
                    },
                    label = { Text(stringResource(R.string.logout), fontSize = 10.sp, color = Color(0xFFCFFAFE)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF22D3EE),
                        unselectedIconColor = Color(0xFFCFFAFE),
                        indicatorColor = Color(0xFF06B6D4).copy(alpha = 0.2f)
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4C1D95),
                            Color(0xFF6B21A8),
                            Color(0xFF7E22CE)
                        )
                    )
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {

                    Text(
                        stringResource(R.string.taleteller),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontFamily = sandtitle
                    )
                    Text(
                        stringResource(R.string.ai_story_friend),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            // Mascot Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.parskedi),
                            contentDescription = "Mascot",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFBBF24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 14.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(14.dp)
                ) {
                    Text(
                        stringResource(R.string.welcome_little_storyteller),
                        color = Color(0xFF5B21B6),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = andikabody
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Creating Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFA855F7),
                                Color(0xFF8B5CF6),
                                Color(0xFF7C3AED)
                            )
                        )
                    )
                    .clickable { navController.navigate("hikaye") }
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.book),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text("✨", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.start_creating_story),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle
                    )
                    Text(
                        stringResource(R.string.decide_your_story),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontFamily = andikabody
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFBBF24))
                            .clickable { navController.navigate("hikaye") }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.create_magic_story),
                            color = Color(0xFF5B21B6),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sandtitle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Featured Stories
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.featured_stories),
                        color = Color(0xFFE9D5FF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle
                    )
                    Text(
                        stringResource(R.string.view_all_stories),
                        color = Color(0xFFE9D5FF),
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { navController.navigate("saveSayfa") }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                val featuredStories = remember {
                    listOf(
                        Triple("featured_1", "Sihirli Orman Macerası", "Sihirli Orman Macerası\n" +
                                "\n" +
                                "Bir varmış, bir yokmuş…\n" +
                                "Uzak diyarlarda, tepeleri bulutlarla kaplı, gökyüzüne kadar uzanan yemyeşil bir orman varmış. Bu ormanın adı Işıltı Ormanıymış. Güneş doğduğunda ağaçların yapraklarından binlerce renk süzülür, gece olduğunda ise çiçekler yıldızlar gibi parıldarmış.\n" +
                                "\n" +
                                "Ama bu ormanın gizli bir sırrı varmış:\n" +
                                "Yalnızca kalbi temiz olanlar ormanın sihirli tarafını görebilirmiş.\n" +
                                "\n" +
                                "\uD83C\uDF3F Bir Gün…\n" +
                                "\n" +
                                "Küçük bir köyde yaşayan Elif adında meraklı bir kız varmış. Elif sekiz yaşındaymış, kocaman kahverengi gözleri ve iki örgülü saçıyla herkesin sevgisini kazanmış. En çok da macera kitapları okumayı severmiş. Her gece yıldızlara bakar ve “Keşke bir gün ben de bir maceraya çıkabilsem,” dermiş.\n" +
                                "\n" +
                                "Bir sabah, güneş henüz dağların arkasından yeni doğarken, Elif evinin önünde bir parlak tüy bulmuş. Tüy öyle parlıyormuş ki sanki gökkuşağının içinden düşmüş. Elif onu eline alınca tüy birden parlamış ve içinden minik bir ses duyulmuş:\n" +
                                "\n" +
                                "“Yardım et bana! Sihirli Orman tehlikede!”\n" +
                                "\n" +
                                "Elif şaşırmış ama korkmamış. Cesurca sormuş:\n" +
                                "— Kim konuşuyor?\n" +
                                "\n" +
                                "Tüyün içinden incecik bir peri çıkmış! Adı Lila’ymış.\n" +
                                "Lila, Sihirli Orman’ın bekçilerinden biriymiş. Ormandaki Işık Taşı çalındığı için ormanın büyüsü zayıflamaya başlamış. Ağaçlar soluyor, çiçekler ışığını kaybediyormuş.\n" +
                                "\n" +
                                "“Elif, yalnızca sen bizi kurtarabilirsin,” demiş Lila.\n" +
                                "\n" +
                                "Elif hiç düşünmeden:\n" +
                                "— “Tamam! Hadi gidelim!” demiş.\n" +
                                "\n" +
                                "Ve böylece sihirli macera başlamış.\n" +
                                "\n" +
                                "\uD83C\uDF32 Ormanın Kapısında\n" +
                                "\n" +
                                "Lila, Elif’in elini tutmuş, tüy birden büyümüş ve onları gökyüzüne kaldırmış. Rüzgârın içinden geçip ışıklar arasında süzülürken Elif kalbinin hızla çarptığını hissetmiş.\n" +
                                "Gözlerini açtığında karşısında kocaman, parlak bir orman kapısı varmış. Kapı, kristallerden yapılmış ve üstünde şu yazı parlıyormuş:\n" +
                                "\n" +
                                "“Cesaretle giren, kalbiyle yol bulur.”\n" +
                                "\n" +
                                "Elif kapıyı itmiş ve içeri girmiş.\n" +
                                "Bir anda her yer renklenmiş: kelebekler şarkı söylüyor, ağaçlar fısıldaşıyor, nehirler gülüyormuş.\n" +
                                "\n" +
                                "Ama Lila üzgün görünüyormuş:\n" +
                                "\n" +
                                "“Işık Taşı kuzeydeki Gölge Mağarası’nda. Oraya ulaşmak için üç engelden geçmeliyiz.”\n" +
                                "\n" +
                                "Elif kararlıymış:\n" +
                                "— “Üç engel mi? Hadi o zaman!”\n" +
                                "\n" +
                                "\uD83E\uDEB5 1. Engel: Konuşan Ağaçlar Labirenti\n" +
                                "\n" +
                                "İlk durak, dalları gökyüzüne kadar uzanan devasa bir labirentmiş. Her yol birbirine benziyormuş. Elif bir yöne gitmek istemiş ama ağaçlardan biri gür bir sesle konuşmuş:\n" +
                                "\n" +
                                "“Doğru yolu bulmak istiyorsan kalbini dinle!”\n" +
                                "\n" +
                                "Elif durup düşünmüş. Sonra gözlerini kapamış, derin bir nefes almış ve kalbinin ona “sağa dön” dediğini hissetmiş. Her dönüşte aynı şeyi yapmış.\n" +
                                "\n" +
                                "Sonunda labirentin merkezine ulaşmış ve orada gülümseyen yaşlı bir ağaç belirmiş.\n" +
                                "\n" +
                                "“Cesaretini gösterdin, kalbini dinledin. Yolun açık olsun küçük gezgin,” demiş.\n" +
                                "\n" +
                                "Yaşlı ağaç Elif’e bir yaprak madalyon vermiş.\n" +
                                "\n" +
                                "“Bu seni kötülükten koruyacak.”\n" +
                                "\n" +
                                "\uD83D\uDC38 2. Engel: Gözyaşı Gölü\n" +
                                "\n" +
                                "Bir sonraki durak, berrak ama sessiz bir gölmüş. Su o kadar durgunmuş ki sanki gökyüzü orada uyuyormuş.\n" +
                                "Ama gölün ortasında ağlayan bir kurbağa varmış.\n" +
                                "\n" +
                                "Elif hemen yanına gitmiş:\n" +
                                "— “Neden ağlıyorsun küçük kurbağa?”\n" +
                                "\n" +
                                "Kurbağa hıçkırarak anlatmış:\n" +
                                "\n" +
                                "“Kralım bir buz büyüsüyle taşa döndü. Sadece içten gelen bir dostluk sözü onu çözer.”\n" +
                                "\n" +
                                "Elif düşünmeden elini uzatmış:\n" +
                                "— “Ben senin dostunum, elimden geleni yaparım!”\n" +
                                "\n" +
                                "O anda göl parlamış, buzlar erimiş, ve koca bir kurbağa kral suyun içinden çıkmış!\n" +
                                "\n" +
                                "“Teşekkür ederim küçük kahraman. İşte sana yardımım: Gölge Mağarası’na giden yolu gösteren ışık taşı parçası.”\n" +
                                "\n" +
                                "\uD83C\uDF0C 3. Engel: Fısıltı Vadisi\n" +
                                "\n" +
                                "Son engel, rüzgârların konuştuğu bir vadiden geçiyormuş. Burada karanlık fısıltılar Elif’in kulağına “geri dön” diyormuş.\n" +
                                "Ama Lila ona,\n" +
                                "\n" +
                                "“Bu seslere inanma, sadece korkuların konuşuyor,” demiş.\n" +
                                "\n" +
                                "Elif cesaretini toplamış ve yüksek sesle bağırmış:\n" +
                                "— “Ben korkmuyorum! Ormanı kurtaracağım!”\n" +
                                "\n" +
                                "Birden vadinin içinden güçlü bir ışık çıkmış ve karanlık fısıltılar yok olmuş.\n" +
                                "\n" +
                                "\uD83D\uDD6F\uFE0F Gölge Mağarası\n" +
                                "\n" +
                                "Sonunda Elif ve Lila, mağaraya ulaşmış. Mağara karanlıkmış, ama Elif’in yaprak madalyonu kendi kendine parlamaya başlamış.\n" +
                                "Işık sayesinde içeri ilerlemişler ve taşın olduğu yere varmışlar.\n" +
                                "\n" +
                                "Tam taşın yanına yaklaşacakken, karşılarına Karanlık Ruh çıkmış.\n" +
                                "\n" +
                                "“Bu taş benim olacak! Sihirli Orman artık benim hükmümde!”\n" +
                                "\n" +
                                "Elif korkmuş ama geri adım atmamış. Kalbindeki cesaretle bağırmış:\n" +
                                "— “Karanlık, ışığı asla yenemez!”\n" +
                                "\n" +
                                "Madalyon daha da parlamış, gölün kristal parçası ışık saçmış ve Karanlık Ruh duman gibi yok olmuş.\n" +
                                "\n" +
                                "Lila hemen taşı yerine koymuş. O an orman yeniden canlanmış: ağaçlar ışıldamış, kuşlar şarkı söylemiş, gökyüzü gökkuşağıyla dolmuş.\n" +
                                "\n" +
                                "\uD83C\uDF08 Eve Dönüş\n" +
                                "\n" +
                                "Elif veda ederken Lila ona sarılmış:\n" +
                                "\n" +
                                "“Orman seni asla unutmayacak Elif. Artık sen de Sihirli Orman’ın bir kahramanısın.”\n" +
                                "\n" +
                                "Elif gözlerini kapamış ve bir rüzgar esmiş. Gözlerini açtığında evinin önündeymiş.\n" +
                                "Elinde hâlâ parlak tüy varmış.\n" +
                                "\n" +
                                "Her sabah tüyü eline aldığında, uzaklardan bir peri sesi fısıldarmış:\n" +
                                "\n" +
                                "“Teşekkürler, cesur kalpli kız.”\n" +
                                "\n" +
                                "Ve Elif her seferinde gülümsermiş. Çünkü artık biliyormuş…\n" +
                                "Gerçek sihir kalpte saklıymış."),
                        Triple("featured_2", "Uzay Yolculuğu", "Bir zamanlar küçük bir kasabada yaşayan Kaan adında meraklı bir çocuk vardı. Kaan, her gece yatağa gitmeden önce penceresinden gökyüzüne bakar, “Bir gün oraya, yıldızların arasına gideceğim!” derdi.\n" +
                                "\n" +
                                "Bir akşam, gökyüzü her zamankinden farklıydı. Ay parlak, yıldızlar sanki dans ediyordu. Kaan teleskobuyla en parlak yıldızı izlerken birden yıldızın yanında gökkuşağı gibi parlayan bir ışık noktası gördü. Işık büyüdü, büyüdü ve fıııııışt! diye bir sesle odasının ortasında minik bir uzay gemisi belirdi!\n" +
                                "\n" +
                                "Geminin kapağı açıldı, içinden mavi renkli, ışıl ışıl parlayan bir uzaylı çıktı.\n" +
                                "“Selam Kaan! Ben Zuzu, Yıldız Tozu Gemisi’nin kaptanıyım!” dedi.\n" +
                                "Kaan şaşkın bir şekilde, “Gerçekten uzaydan mı geldin?” diye sordu.\n" +
                                "Zuzu gülümsedi: “Evet! Evreni dolaşırken senin merak sinyallerini yakaladım. Demek uzaya gitmek istiyorsun?”\n" +
                                "\n" +
                                "Kaan heyecanla başını salladı.\n" +
                                "“Peki ama yalnız gitmem,” dedi, “arkadaşlarım Anıl ve Miralp de gelmeli!”\n" +
                                "\n" +
                                "Zuzu gülümsedi, sihirli antenini salladı ve bir anda Anıl ile Miralp de Kaan’ın odasında belirdi!\n" +
                                "“Ne oluyor burada?” dedi Anıl şaşkınlıkla.\n" +
                                "“Uzaya gidiyoruz!” dedi Kaan heyecanla.\n" +
                                "\n" +
                                "Üç arkadaş gemiye atladı. Gemi ışıl ışıl parladı ve bir anda pencerenin içinden geçip gökyüzüne doğru fırladı! \uD83D\uDE80\n" +
                                "\n" +
                                "Önce Ay’ın kraterlerini gördüler. Miralp hemen notlar aldı: “Bunlar devasa çukurlar! Meteorlar oluşturmuş olmalı!”\n" +
                                "Sonra Satürn’ün halkalarına uğradılar. Anıl elini cama dayayıp, “Bu halkalar sanki sihirli tozlardan yapılmış gibi!” dedi.\n" +
                                "Zuzu gülerek açıkladı: “Aslında buz ve taş parçaları! Ama yıldız ışığı altında parlayınca büyü gibi görünür.”\n" +
                                "\n" +
                                "Daha sonra gemi Rüya Bulutu Galaksisi’ne ulaştı. Burada gökyüzü mor ve turuncu renklere bürünmüştü. Dev yıldız kelebekleri süzülüyordu.\n" +
                                "Kaan hayranlıkla, “Bunu kimseye anlatsam inanmaz!” dedi.\n" +
                                "\n" +
                                "Bir süre sonra Zuzu, “Artık eve dönme zamanı,” dedi.\n" +
                                "Kaan içinden “Keşke hiç bitmese,” diye düşündü ama Anıl ve Miralp’le birbirlerine bakıp gülümsediler. Onlar artık sadece arkadaş değil, uzay yolcularıydı.\n" +
                                "\n" +
                                "Gemi ışık hızında dönüp Dünya’ya indi. Üçü odada gözlerini açtığında sabah olmuştu. Teleskobun yanında küçük bir parlayan yıldız tozu şişesi duruyordu.\n" +
                                "\n" +
                                "Kaan fısıldadı:\n" +
                                "“Demek hepsi gerçekti...”\n" +
                                "\n" +
                                "Ve o günden sonra her gece, Kaan, Anıl ve Miralp birlikte gökyüzüne bakıp yeni bir sinyal gönderdiler —\n" +
                                "belki bir gün Zuzu yeniden gelir diye"),
                        Triple("featured_3", "Deniz Altı Krallığı", "Bir zamanlar, masmavi bir denizin kıyısında küçük bir balıkçı kasabasında Alya adında meraklı bir kız yaşarmış. Alya’nın en sevdiği şey, her sabah dalgaların sesini dinlemek ve denizin altındaki gizemleri hayal etmekmiş.\n" +
                                "\n" +
                                "Bir gün Alya sahilde yürürken, kumların arasında ışıl ışıl parlayan mavi bir deniz kabuğu bulmuş. Kabuğu kulağına götürünce içinden ince bir ses duyulmuş:\n" +
                                "\n" +
                                "“Alya... yardım et... Deniz Altı Krallığı tehlikede!”\n" +
                                "\n" +
                                "Alya önce korkmuş, sonra cesaretini toplayarak, “Ben sana nasıl yardım edebilirim?” diye sormuş. Kabuğun içinden bir ışık yükselmiş ve Alya bir anda kendini denizin içinde, nefes alabiliyor halde bulmuş!\n" +
                                "\n" +
                                "\uD83D\uDC1A Mercan Şehri\n" +
                                "\n" +
                                "Alya gözlerini açtığında çevresini rengârenk mercanlar, deniz yıldızları ve süzülen balıklar sarmış. Karşısında gümüş pullu, zarif bir denizkızı belirmiş.\n" +
                                "\n" +
                                "“Ben Mira, Deniz Altı Krallığı’nın koruyucusuyum,” demiş. “Kral Triton’un ışık incisi çalındı! O inci denizimize ışık ve yaşam verir. Onsuz her şey kararacak.”\n" +
                                "\n" +
                                "Alya hemen, “O inciyi bulmana yardım edeceğim!” demiş.\n" +
                                "\n" +
                                "\uD83E\uDD91 Karanlık Mağara\n" +
                                "\n" +
                                "Mira ve Alya birlikte denizin en derin yerlerine dalmışlar. Yolda neşeli bir ahtapot olan Pippo onlara katılmış. Pippo sekiz koluyla komik danslar yapıyor, Alya’yı güldürüyormuş.\n" +
                                "\n" +
                                "Üçlü sonunda Karanlık Mağaraya ulaşmış. İçerisi sessiz ve ürkütücüymüş. Tavandan sarkan yosunlar arasında küçük bir parıltı fark etmişler.\n" +
                                "\n" +
                                "“İşte ışık incisi!” demiş Mira. Ama hemen ardından büyük bir müren balığı ortaya çıkmış!\n" +
                                "\n" +
                                "“Bu inci artık benim!” diye hırlamış mürense.\n" +
                                "\n" +
                                "Alya korkmuş ama aklına bir fikir gelmiş. Pippo’ya fısıldamış:\n" +
                                "\n" +
                                "“Onu oyalayabilir misin? Ben incinin yanına gideceğim.”\n" +
                                "\n" +
                                "Pippo sekiz koluyla dans etmeye başlamış, müreni şaşırtmış. Alya o sırada yavaşça süzülüp inciyi almış. İnci eline değer değmez etrafı güneş gibi aydınlanmış!\n" +
                                "\n" +
                                "\uD83D\uDC2C Krallığın Kutlaması\n" +
                                "\n" +
                                "Mira, Alya ve Pippo inciyi krallığa geri getirmiş. Kral Triton büyük bir sevinçle onlara teşekkür etmiş:\n" +
                                "\n" +
                                "“Cesaretin olmasa deniz karanlığa gömülecekti, Alya.”\n" +
                                "\n" +
                                "Krallığın bütün canlıları—balıklar, kaplumbağalar, deniz atları—birlikte dans etmiş, şarkılar söylemiş.\n" +
                                "\n" +
                                "Alya veda ederken Mira gülümsemiş:\n" +
                                "\n" +
                                "“Ne zaman deniz kabuğunu kulağına götürürsen, biz seni duyacağız.”\n" +
                                "\n" +
                                "Alya bir anda yeniden sahildeymiş. Elinde hâlâ o mavi deniz kabuğu varmış. Kabuğu kulağına koyduğunda derinlerden gelen bir ses duymuş:\n" +
                                "\n" +
                                "“Teşekkürler, Deniz Krallığı’nın kahramanı Alya!” \uD83C\uDF0A✨"),
                        Triple("featured_4", "Rüya Dünyası", "Bir zamanlar, küçük bir kasabada yaşayan meraklı bir kız vardı: Necla. Necla hayal kurmayı çok severdi. Bazen gökyüzündeki bulutlara bakar, onların şekillerini değiştirerek hikâyeler uydururdu. Ama bir gece, her zamankinden farklı bir şey oldu…\n" +
                                "\n" +
                                "O gece Necla, yastığına başını koyar koymaz göz kapakları ağırlaştı. Birden etrafında parlak ışıklar belirdi. Gözlerini açtığında, kendini yumuşacık pamuktan yapılmış bir yerde buldu. Etrafında gökyüzü gibi parlayan mavi, pembe ve mor tonlarında bulutlar uçuşuyordu.\n" +
                                "\n" +
                                "“Burası da neresi?” diye sordu kendi kendine.\n" +
                                "\n" +
                                "Tam o sırada, altın sarısı kanatları olan minik bir kuş yanına geldi.\n" +
                                "“Rüya Dünyası’na hoş geldin Necla!” dedi cıvıldayarak. “Ben Luma! Burada herkes kendi hayallerini yaşar.”\n" +
                                "\n" +
                                "Necla şaşkınlıkla etrafına bakındı. Gökyüzünde uçan dondurmalar, konuşan yastıklar ve renk değiştirip dans eden çiçekler vardı. “Bu harika!” dedi.\n" +
                                "\n" +
                                "Ama Luma’nın yüzü birden ciddileşti.\n" +
                                "“Rüya Dünyası tehlikede Necla! Karanlık Gölge, insanların kötü rüyalarından güç alıyor. Eğer onu durdurmazsak, güzel rüyalar yok olacak!”\n" +
                                "\n" +
                                "Necla hemen cesurca, “O zaman hemen gidelim!” dedi.\n" +
                                "\n" +
                                "İkili, Parlak Orman’dan geçtiler; burada ağaçların yaprakları gümüş gibi parlıyordu. Ardından Yansıma Nehri’ni aştılar; nehirde yüzen balıkların her biri bir rüya parçasıydı.\n" +
                                "\n" +
                                "Sonunda Karanlık Gölge’nin yaşadığı Uykusuz Mağara’ya geldiler. İçeriden soğuk bir rüzgâr esiyordu. Necla kalbini hızla atarken, Luma’nın kanatlarına tutundu.\n" +
                                "\n" +
                                "Karanlık Gölge dev bir sis bulutu gibiydi.\n" +
                                "“Necla… Korkuların beni güçlendiriyor!” diye gürledi.\n" +
                                "\n" +
                                "Necla derin bir nefes aldı ve gözlerini kapadı. Kalbinden geçen güzel şeyleri düşündü: ailesini, arkadaşlarını, en sevdiği kitabı, sabah güneşini…\n" +
                                "Birden içinden altın renkli bir ışık çıktı.\n" +
                                "\n" +
                                "“Rüya Dünyası korkuyla değil, umutla yaşar!” diye bağırdı.\n" +
                                "\n" +
                                "O ışık Karanlık Gölge’yi sardı ve bir anda gölge kayboldu. Rüya Dünyası yeniden ışıl ışıl oldu.\n" +
                                "\n" +
                                "Luma Necla’ya sarıldı. “Başardın! Artık herkes güzel rüyalar görebilecek.”\n" +
                                "\n" +
                                "Necla gülümsedi. “Ben sadece kalbimi dinledim,” dedi.\n" +
                                "\n" +
                                "Sabah olduğunda gözlerini açtı. Yatağının yanında küçük, altın renkli bir tüy duruyordu…\n" +
                                "Luma’nın tüyü! \uD83C\uDF1F\n" +
                                "\n" +
                                "Necla o günden sonra her gece rüyaya dalmadan önce bir dilek diledi:\n" +
                                "“Bugün herkesin güzel bir rüya görmesini dilerim.”\n" +
                                "\n" +
                                "Ve o dilek, her gece Rüya Dünyası’na bir ışık daha ekledi. \uD83D\uDCAB"),
                        Triple("featured_5", "Ejderha Dostluğu", "Bir zamanlar, bulutların gölgesinde kalan küçük bir köyde Elif adında cesur bir kız yaşarmış. Elif, her gün ormanın kenarına gidip uzaklardaki dağlara bakarmış. Çünkü o dağların ardında, kimsenin cesaret edip gitmediği Ejderha Vadisi varmış. Köylüler orada korkunç bir ejderhanın yaşadığına inanır, oraya yaklaşmaktan bile çekinirlermiş.\n" +
                                "\n" +
                                "Ama Elif farklıymış. O, ejderhalardan korkmak yerine onları merak edermiş.\n" +
                                "Bir gün cesaretini toplamış ve küçük sırt çantasına biraz ekmek, su ve sevdiği pelüş oyuncağını koyarak ormana doğru yola çıkmış.\n" +
                                "\n" +
                                "Uzun yürüyüşün sonunda sislerin arasında kocaman bir mağara görmüş. Mağaranın önünde ise yaralı, minik bir ejderha yatıyormuş! Tüyleri yeşil, gözleri parlayan zümrüt gibiymiş. Elif önce korkmuş ama sonra ejderhanın acı çektiğini fark etmiş.\n" +
                                "\n" +
                                "“Merhaba… Sana zarar vermeyeceğim,” demiş Elif yavaşça yaklaşarak.\n" +
                                "Ejderha da hafif bir iniltiyle başını kaldırmış. Ayağına bir taş saplanmış!\n" +
                                "\n" +
                                "Elif hemen küçük bir sopayla taşı dikkatlice çıkarmış, sonra çantasındaki suyla yaranın üstünü temizlemiş. Ejderha minnettarlıkla burnundan sıcak bir buhar üflemiş — neredeyse bir teşekkür gibiymiş.\n" +
                                "\n" +
                                "Elif, ejderhaya “Kıvılcım” adını vermiş. O günden sonra her gün gizlice vadideki dostunu ziyaret etmiş. Ona yemek getirmiş, oyunlar oynamışlar, hatta Elif bazen sırtına binip bulutların üzerine kadar uçmuş! \uD83C\uDF24\uFE0F\n" +
                                "\n" +
                                "Fakat bir gün köylüler gökyüzünde ejderhayı görünce çok korkmuşlar. Ellerine meşaleler alıp vadinin yolunu tutmuşlar. Elif hemen Kıvılcım’ı saklamış. Köylülere, “O kötü değil! O benim dostum! Benim hayatımı kurtardı!” diye bağırmış.\n" +
                                "\n" +
                                "Kıvılcım da gökyüzüne yükselmiş, kuyruğuyla kalp şeklinde bir duman çizmiş. Köylüler o an anlamışlar ki bu ejderha zararsızmış.\n" +
                                "\n" +
                                "O günden sonra Ejderha Vadisi korku değil, dostluğun ve cesaretin vadisi olarak anılmış. Elif ve Kıvılcım her gün gökyüzünde dolaşmış, köylülere uzaktan el sallamışlar.\n" +
                                "\n" +
                                "Ve böylece küçük bir kızın cesareti, koca bir köyün kalbini değiştirmiş. \uD83D\uDC96"),
                        Triple("featured_6", "Zaman Yolcusu", "Bir zamanlar, küçük bir kasabada yaşayan meraklı bir çocuk varmış. Adı Zeki’ymiş. Zeki, diğer çocuklardan farklı olarak oyun oynamaktan çok eski eşyalarla uğraşmayı severmiş. Babasının tamir atölyesinde bozulmuş saatleri söker, içlerindeki dişlilerin nasıl çalıştığını anlamaya çalışırmış.\n" +
                                "\n" +
                                "Bir gün, kasabanın kenarındaki eski antikacıya girmiş. Rafların arasında dolaşırken gözü tozlu bir cep saatine takılmış. Saatin kapağında “Zaman seni bekliyor” yazıyormuş. Zeki hemen merakla saati alıp kurmuş. Tam o anda ortalıkta parlak bir ışık belirmiş ve Zeki bir anda kendini bambaşka bir yerde bulmuş!\n" +
                                "\n" +
                                "Etrafına baktığında, kalpak takmış insanların at arabalarıyla gezdiği, elektrik direklerinin bile olmadığı bir meydandaymış. Bir tabelada “Yıl 1890 – Kasaba Meydanı” yazıyormuş.\n" +
                                "Zeki hayretle, “Demek gerçekten zamanda yolculuk yaptım!” demiş kendi kendine.\n" +
                                "\n" +
                                "İlk başta korkmuş ama sonra merakı galip gelmiş. Meydanda bir çocukla tanışmış, adı Hasan’mış. Hasan, Zeki’nin kıyafetlerine bakıp şaşırmış:\n" +
                                "— Bu ne biçim elbise? Kumaşı bile farklı! Nereden geldin sen?\n" +
                                "Zeki gülerek, “Uzak bir yerden…” demiş, fazla açıklama yapmadan.\n" +
                                "\n" +
                                "İkisi hemen arkadaş olmuşlar. Hasan, Zeki’yi kasabayı gezdirmiş, su değirmenini, eski okul binasını ve köy pazarını göstermiş. Zeki, geçmişteki hayatın ne kadar farklı olduğunu hayranlıkla izlemiş. Ama akşam olunca bir şeyi fark etmiş:\n" +
                                "Cebindeki saat titriyormuş ve ibreleri tersine dönüyormuş!\n" +
                                "\n" +
                                "Hasan’a veda ederken, “Bir gün yine görüşürüz,” demiş Zeki. Işıklar yeniden parlamış ve Zeki kendini tekrar kendi odasında bulmuş. Saate baktığında ibre durmuş ama altındaki yazı değişmişti:\n" +
                                "“Zaman senin dostun oldu.”\n" +
                                "\n" +
                                "O günden sonra Zeki, sadece geçmişe değil, bilginin de yolcusu olmuş. Tarihi, bilimi ve zamanı anlamak için daha çok çalışmaya başlamış. Çünkü artık biliyordu ki, merak eden herkes biraz zaman yolcusudur.\n" +
                                "\n" +
                                "\uD83C\uDF1F Son.\n" +
                                "\n"
                                )
                    )
                }
                
                val storyImages = mapOf(
                    "featured_1" to R.drawable.orman1,
                    "featured_2" to R.drawable.uzay1,
                    "featured_3" to R.drawable.deniz1,
                    "featured_4" to R.drawable.zaman1,
                    "featured_5" to R.drawable.ejder1,
                    "featured_6" to R.drawable.zeki1
                )
                
                featuredStories.chunked(2).forEachIndexed { rowIndex, rowStories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowStories.forEachIndexed { colIndex, (id, title, content) ->
                            val storyImage = storyImages[id] ?: R.drawable.story
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.75f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { 
                                        navController.navigate("metin/$id")
                                        anasayfaViewModel.setFeaturedStory(title, content, storyImage)
                                    }
                            ) {
                                Image(
                                    painter = painterResource(storyImage),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                )
                                Text(
                                    title,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

    }
}










