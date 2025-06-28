
import android.annotation.SuppressLint
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
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
import androidx.core.graphics.scale
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.BuildConfig
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import kotlinx.coroutines.delay
import java.util.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.DONUT)
@Composable
fun Metin(navController: NavController,
          hikayeViewModel: HikayeViewModel,
          metinViewModel: MetinViewModel,
          hikayeId:String?,
          anasayfaViewModel: AnasayfaViewModel) {

    val hikayeyiOlustur by hikayeViewModel.hikayeOlustur.observeAsState("")
    val context = LocalContext.current
    val kaan by hikayeViewModel.hikaye.observeAsState(Hikaye())
    val generatedImage by metinViewModel.imageBitmap.observeAsState(null)
    var audioVisible by remember { mutableStateOf(false) }
    var audioVisibleStory by remember { mutableStateOf(false) }
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val andikabody= FontFamily(Font(R.font.andikabody))
    val sandtitle= FontFamily(Font(R.font.sandtitle))
    val language = stringResource(R.string.language)
    val country = stringResource(R.string.country)
    var hasTrial by remember { mutableStateOf(false) }
    var isPremium by remember { mutableStateOf(false) }
    var showSuccessAnimation by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }
    val imageSaved by metinViewModel.imageSaved.observeAsState()
    val imageSavedUrl by metinViewModel.imageSavedUrl.observeAsState()





    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    LaunchedEffect(hikayeId) {
        hikayeId?.let {
            hikayeViewModel.getStoryById(it)
        }
    }

    LaunchedEffect(Unit) {
        anasayfaViewModel.checkUserAccess { hasTrialStatus, isPremiumStatus,usedFreeTrial ->
            hasTrial = hasTrialStatus
            isPremium = isPremiumStatus
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.saveanimation))
    var isAnimationPlaying by remember { mutableStateOf(false) }
    val animationProgress = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isAnimationPlaying,
        iterations = 1,
        speed = 1.5f
    )

    // Animasyon bittiğinde gizle
    LaunchedEffect(animationProgress.progress) {
        if (animationProgress.progress == 1f) {
            showSuccessAnimation = false
            isAnimationPlaying = false
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6BB7C0), // Üstteki renk
            Color(0xFF21324A)  // Alttaki renk
        ),
        startY = 0f,
        endY = 1800f // Bu değeri ekran yüksekliğine göre ayarlayabilirsiniz.
    )

    DisposableEffect(key1 = context) {
        metinViewModel.initTTS(context, language, country)

        onDispose {
            metinViewModel.stop()
            metinViewModel.stopMediaPlayer()
            // shutdown yapmana gerek yok çünkü ViewModel onCleared içinde yapıyor
        }
    }

        val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(paddingValues)
                    .verticalScroll(scrollState)
                    .background(gradientBackground),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

                ) {
                if (kaan.title.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(480.dp)

                        ) {
                            AsyncImage(
                                model = kaan.imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            // SADECE alt kısmı saydamlaştıran overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent, // Üst kısım görünür
                                                Color.Transparent, // Ortalar da görünür
                                                Color(0xFF305063).copy(alpha = 0.1f),
                                                Color(0xFF305063).copy(alpha = 0.3f),
                                                Color(0xFF305063).copy(alpha = 0.9f),
                                                Color(0xFF305063).copy(alpha = 0.9f),
                                                Color(0xFF305063), // Tamamen arka plana geçiş
                                                Color(0xFF305063) // Tamamen arka plana geçiş
                                            ),
                                            startY = 750f, // Geçiş daha geç başlasın
                                            endY = 1500f    // Alt kısımda bitiş
                                        )
                                    )
                            )

                            Image(
                                painter = painterResource(id = R.drawable.mavidenemeee),
                                contentDescription = null,
                                alignment = Alignment.TopEnd,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(
                                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(), // <-- bu eklendi
                                        end = 10.dp
                                    )
                                    .clickable {
                                        audioVisibleStory = true
                                    }
                                    .clip(CircleShape)
                                    .size(95.dp)
                            )
                        }



                    }

                    Text(
                    text = kaan.title,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .padding(10.dp)
                        ,
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 50.sp,
                        fontFamily = sandtitle

                )
                    Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = kaan.content,
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    lineHeight = 35.sp,
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                    fontSize = 25.sp,
                   fontFamily = andikabody

                )


                Button(onClick = {
                    navController.navigate("saveSayfa")
                }, colors = ButtonDefaults.buttonColors(Color(0xFF6BB7C0)), modifier = Modifier.padding(bottom = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(), // <-- bu eklendi
                )) {
                    Text(text = stringResource(R.string.MYSTORIES),fontSize = 22.sp, fontFamily = sandtitle)

                }
                //bitiş
            }else{
                    if (hikayeyiOlustur.isEmpty() || generatedImage == null) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedLoadingImages()
                            Text("Yeni Bir Dünya Yaratılıyor...", fontFamily = andikabody, fontSize = 20.sp, color = Color.White)
                            Text("Karakterler Canlandırılıyor...",fontFamily = andikabody, fontSize = 20.sp, color = Color.White)
                            Text("Sesler Duyulmaya Başlanıyor...",fontFamily = andikabody, fontSize = 20.sp, color = Color.White)


                        }
                    } else {
                        if (hikayeyiOlustur.isNotEmpty() && generatedImage != null) {
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .height(480.dp)
                                ) {
                                    generatedImage?.let { bitmap ->
                                        val scaledBitmap = bitmap.scale(1024, 1024) // İstediğiniz boyuta göre ayarlayın
                                        Image(
                                            bitmap = scaledBitmap.asImageBitmap(),
                                            contentDescription = "Generated Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxSize()

                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent, // Üst kısım görünür
                                                            Color.Transparent, // Ortalar da görünür
                                                            Color(0xFF305063).copy(alpha = 0.1f),
                                                            Color(0xFF305063).copy(alpha = 0.3f),
                                                            Color(0xFF305063).copy(alpha = 0.9f),
                                                            Color(0xFF305063).copy(alpha = 0.9f),
                                                            Color(0xFF305063), // Tamamen arka plana geçiş
                                                            Color(0xFF305063) // Tamamen arka plana geçiş
                                                        ),
                                                        startY = 750f, // Geçiş daha geç başlasın
                                                        endY = 1500f    // Alt kısımda bitiş
                                                    )
                                                )
                                        )
                                    }
                                    Image(
                                        painter = painterResource(id = R.drawable.mavidenemeee),
                                        contentDescription = "",
                                        alignment = Alignment.TopEnd,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                                                end = 10.dp)
                                            .clickable {
                                                audioVisible = true
                                            }
                                            .clip(CircleShape)
                                            .size(95.dp)
                                        // .padding(end = 10.dp)
                                    )
                                }

                            }

                            Spacer(Modifier.height(6.dp))

                            IconButton(
                                onClick = {
                                    val userId = getCurrentUserId()
                                    if (userId != null && generatedImage != null) {
                                        metinViewModel.saveImageToStorage(generatedImage!!, userId)
                                    } else {
                                        Toast.makeText(context, "Kayıt başarısız", Toast.LENGTH_SHORT).show()
                                    }

                                   /* if (userId != null && generatedImage != null) {
                                        metinViewModel.saveImageToStorage(generatedImage!!, userId)

                                        metinViewModel.imageSaved.observe(context as LifecycleOwner) { saved ->
                                            if (saved) {
                                                CoroutineScope(Dispatchers.Main).launch{
                                                    metinViewModel.imageSavedUrl.observe(context as LifecycleOwner) { imageUrl ->
                                                        if (imageUrl != null) {
                                                            metinViewModel.saveStoryForUser(
                                                                hikayeId!!,
                                                                hikayeyiOlustur,
                                                                imageUrl,
                                                                userId
                                                            )
                                                            isSaved = true // ✅ ikon artık sarı olacak
                                                            showSuccessAnimation = true
                                                            isAnimationPlaying = true
                                                            Toast.makeText(
                                                                context,
                                                                "Hikaye Kaydedildi",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Kayıt başarısız",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }*/
                                }
                            ) {
                                Icon(
                                    modifier = Modifier.size(100.dp),
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Kaydet",
                                    tint = if (isSaved) Color.Yellow else Color.Gray
                                )
                            }
                            LaunchedEffect(imageSaved, imageSavedUrl) {
                                if (imageSaved == true && imageSavedUrl != null) {
                                    val userId = getCurrentUserId()
                                    if (userId != null) {
                                        metinViewModel.saveStoryForUser(
                                            hikayeId!!,
                                            hikayeyiOlustur,
                                            imageSavedUrl!!,
                                            userId
                                        )
                                        isSaved = true
                                        showSuccessAnimation = true
                                        isAnimationPlaying = true
                                        Toast.makeText(context, "Hikaye Kaydedildi", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            if (showSuccessAnimation) {
                                val scale by animateFloatAsState(
                                    targetValue = if (showSuccessAnimation) 1.2f else 0f,
                                    animationSpec = tween(
                                        durationMillis = 400,
                                        easing = FastOutSlowInEasing
                                    )
                                )

                                val alpha by animateFloatAsState(
                                    targetValue = if (showSuccessAnimation) 1f else 0f,
                                    animationSpec = tween(durationMillis = 400)
                                )

                                LottieAnimation(
                                    composition = composition,
                                    progress = { animationProgress.progress },
                                    modifier = Modifier
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale,
                                            alpha = alpha,
                                            translationY = (-20 * (1 - scale)).dp.value // fırlama etkisi yukarı
                                        )
                                        .size(250.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }



                            Spacer(Modifier.height(6.dp))

                            Text(text = hikayeyiOlustur,
                                modifier = Modifier.padding(
                                    start = 12.dp,
                                    end = 12.dp,
                                    top = 4.dp,
                                    bottom = 4.dp
                                ),
                                lineHeight = 35.sp,
                                color = Color.White,
                                textAlign = TextAlign.Justify,
                                fontSize = 25.sp,
                                fontFamily = andikabody)
                            Button(onClick = {
                                val prompt = hikayeViewModel.getCurrentPrompt()
                                hikayeViewModel.generateStory(prompt)

                            },colors = ButtonDefaults.buttonColors(Color(0xFF6BB7C0)),modifier = Modifier
                                .padding(bottom = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(), // <-- bu eklendi
                                ))  {
                                Text(text =stringResource(R.string.Rebuild),fontSize = 22.sp, fontFamily = sandtitle)
                            }

                            Button(onClick = {
                                navController.navigate("saveSayfa")
                            },colors = ButtonDefaults.buttonColors(Color(0xFF6BB7C0)), modifier = Modifier
                                .padding(bottom = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(), // <-- bu eklendi
                                )) {
                                Text(text = stringResource(R.string.MYSTORIES),fontSize = 22.sp, fontFamily = sandtitle)
                            }

                        }



                    }

                }


        }
        if (audioVisible ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.5f))
                .clickable {
                    audioVisible = false
                },
                contentAlignment = Alignment.Center


                ){
                Audio(navController, hikayeViewModel,metinViewModel, onClose = { audioVisible = false })
            }
        }

    if (audioVisibleStory) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.5f))
            .clickable {
                audioVisibleStory = false // Doğru state'i kapatma
            },
            contentAlignment = Alignment.Center
        ) {
            AudioSave(navController, hikayeViewModel, metinViewModel, onClose = { audioVisibleStory = false }) // State'i onClose'da kapat
        }
    }
}


@Composable
fun Audio(navController: NavController,hikayeViewModel: HikayeViewModel, metinViewModel: MetinViewModel,  onClose: () -> Unit) {
    val hikayeyiOlustur by hikayeViewModel.hikayeOlustur.observeAsState("")
    //var dbRepo= DiscoveryBoxRepository()
    val dbRepo = hikayeViewModel.dbRepo
    val context = LocalContext.current
    val generatedImage by metinViewModel.imageBitmap.observeAsState(null)
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var isStopped by remember { mutableStateOf(false) }  // Pause sonrası durumu kontrol etmek için ekledik.
    val language = stringResource(R.string.language)
    val country = stringResource(R.string.country)
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var isAudioLoading by remember { mutableStateOf(false) }



    val infiniteTransition = rememberInfiniteTransition()
    val barHeights = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = if (isPlaying) (index * 10 + 10).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            targetValue = if (isPlaying) (index * 10 + 50).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 100),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val barColors = List(6) {
        infiniteTransition.animateColor(
            initialValue = Color(0xFF4CAF50),
            targetValue = Color(0xFFFFC107),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        modifier = Modifier
            .size(250.dp, 400.dp)
            .background(Color.LightGray.copy(alpha = 0.50f), shape = RectangleShape),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.size(300.dp,180.dp)) {
            // Hikaye fotoğrafını ortalar
            generatedImage?.let { bitmap ->
                val scaledBitmap = bitmap.scale(512, 512) // İstediğiniz boyuta göre ayarlayın
                Image(
                    bitmap = scaledBitmap.asImageBitmap(),
                    contentDescription = "Generated Image",
                    modifier = Modifier
                        .size(512.dp)
                        .padding(2.dp)
                )
            }

            // Çarpı ikonunu sağ üst köşeye yerleştirir
            Image(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd) // Çarpıyı sağ üst köşeye hizalar
                    .padding(end = 10.dp)
                    .clickable {
                        //textToSpeech?.shutdown()
                        onClose()

                    }// Sağ üst köşeye biraz boşluk ekler (isteğe bağlı)
            )
        }



        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(65.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Çubukları oluşturma
                barHeights.forEachIndexed { index, barHeight ->
                    AudioBar(height = barHeight.value, color = barColors[index].value)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))


        /*Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Image(
                painter = painterResource(id = R.drawable.play),
                contentDescription = "hizlandir icon",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
                    .graphicsLayer(
                        alpha = if (isPlaying || isStopped) 0.5f else 1f // Solma efekti
                    )
                    .clickable(enabled = !isPlaying && !isStopped) {
                        isAudioLoading = true // 🎯 Yükleme başlasın
                        metinViewModel.handleTTS(context, BuildConfig.OPENAI_API_KEY, hikayeyiOlustur) {
                            isAudioLoading = false // 🎯 Ses hazır
                        }
                        dbRepo.markUsedFreeTrialIfNeeded(userId)
                        isPlaying = true
                    }
            )
            if (isAudioLoading) {
                Column(modifier = Modifier.size(100.dp)
                , verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sesler oluşturuluyor")
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 8.dp)
                    )
                }

            }




            Image(
                painter = painterResource(id = R.drawable.pause),
                contentDescription = "play icon",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
                    .graphicsLayer(
                        alpha = if (isStopped) 0.5f else 1f // Solma efekti
                    )
                    .clickable(enabled = isPlaying && !isStopped) {
                        metinViewModel.stopMediaPlayer()
                        metinViewModel.stop()
                        isStopped = true // Pause tuşuna basıldığında
                    }
            )
        }*/

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // 🔊 Play butonu + yükleme
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable(enabled = !isPlaying && !isStopped) {
                        isAudioLoading = true
                        metinViewModel.handleTTS(
                            context,
                            BuildConfig.OPENAI_API_KEY,
                            hikayeyiOlustur
                        ) {
                            isAudioLoading = false
                        }
                        dbRepo.markUsedFreeTrialIfNeeded(userId)
                        isPlaying = true
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "play",
                    modifier = Modifier.size(28.dp) 
                )

                if (isAudioLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        strokeWidth = 2.dp,
                        color = Color.Gray
                    )
                }
            }

            // 🟠 Ses yükleniyor yazısı (isteğe bağlı)
            if (isAudioLoading) {
                Text(
                    text = "Ses oluşturuluyor...",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            // ⏸️ Pause butonu
            Image(
                painter = painterResource(id = R.drawable.pause),
                contentDescription = "pause",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
                    .graphicsLayer(
                        alpha = if (isStopped) 0.5f else 1f
                    )
                    .clickable(enabled = isPlaying && !isStopped) {
                        metinViewModel.stopMediaPlayer()
                        metinViewModel.stop()
                        isStopped = true
                    }
            )
        }


    }
}

@Composable
fun AudioSave(navController: NavController,hikayeViewModel: HikayeViewModel, metinViewModel: MetinViewModel,  onClose: () -> Unit) {
    val hikayeyiOlustur by hikayeViewModel.hikayeOlustur.observeAsState("")
    val context = LocalContext.current
    val kaan by hikayeViewModel.hikaye.observeAsState(Hikaye())
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }
    val language = stringResource(R.string.language)
    val country = stringResource(R.string.country)

    val infiniteTransition = rememberInfiniteTransition()
    val barHeights = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = if (isPlaying) (index * 10 + 10).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            targetValue = if (isPlaying) (index * 10 + 50).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 100),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val barColors = List(6) {
        infiniteTransition.animateColor(
            initialValue = Color(0xFF4CAF50),
            targetValue = Color(0xFFFFC107),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        modifier = Modifier
            .size(250.dp, 400.dp)
            .background(Color.LightGray.copy(alpha = 0.50f), shape = RectangleShape),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.size(300.dp,180.dp)) {
            AsyncImage(model = kaan.imageUrl, contentDescription = "", modifier = Modifier.size(512.dp,512.dp))


            Image(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 10.dp)
                    .clickable {
                        onClose()

                    }
            )

        }



        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(65.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {

                barHeights.forEachIndexed { index, barHeight ->
                    AudioBar(height = barHeight.value, color = barColors[index].value)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))


        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            Image(
                painter = painterResource(id = R.drawable.play),
                contentDescription = "hizlandir icon",
                modifier = Modifier
                    .size(45.dp) // İkonun boyutunu ayarlamak için
                    .clip(CircleShape) // Yuvarlak yapmak için CircleShape kullanılır
                    .background(Color.Transparent) // İsteğe bağlı arka plan rengi
                    .border(2.dp, Color.Gray, CircleShape)

                    .clickable {
                        metinViewModel.speak(kaan.content)
                        isPlaying = true

                    },
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.pause),
                contentDescription = "play icon",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        metinViewModel.stopMediaPlayer() // GPT TTS için
                        metinViewModel.stop()
                        //textToSpeech?.stop()
                        isPlaying = false

                    },
                        contentScale = ContentScale.Crop

            )



        }

    }
}


@Composable
fun AudioBar(height: Float, color: Color) {
    Canvas(
        modifier = Modifier
            .size(10.dp, height.dp)
            .background(color)
    ) {

    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedLoadingImages() {
    val images = listOf(
        R.drawable.anne,
        R.drawable.ay,
        R.drawable.sleepy
    )

    var currentIndex by remember { mutableStateOf(0) }

    // Görselleri sırayla değiştirme
    LaunchedEffect(Unit) {
        while (true) {
            delay(1500) // her 1 saniyede bir değişsin
            currentIndex = (currentIndex + 1) % images.size
        }
    }

    // Geçiş animasyonu
    AnimatedContent(
        targetState = currentIndex,
        transitionSpec = {
            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
        }
    ) { targetIndex ->
        Image(
            painter = painterResource(id = images[targetIndex]),
            contentDescription = "Yükleniyor",
            modifier = Modifier
                .size(230.dp)
                .padding(12.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}


