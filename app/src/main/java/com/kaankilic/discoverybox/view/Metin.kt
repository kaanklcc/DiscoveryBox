
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
    val storyPages by hikayeViewModel.storyPages.observeAsState(emptyList())
    var currentPageIndex by remember { mutableStateOf(0) }
    val generatedImage by metinViewModel.imageBitmap.observeAsState(null)
    var showSaveAnimation by remember { mutableStateOf(false) }
    var isPlayingAudio by remember { mutableStateOf(false) }
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
    
    LaunchedEffect(storyPages) {
        if (storyPages.isNotEmpty()) {
            val (mainChar, setting) = hikayeViewModel.getStoryContext()
            storyPages.forEachIndexed { index, page ->
                if (page.imageBitmap == null) {
                    val consistentPrompt = "Professional children's book illustration, vibrant fantasy art. Main character $mainChar (same appearance throughout) in $setting. Scene: ${page.content.take(100)}. IMPORTANT: NO book pages, NO text overlays, NO page borders, pure scene illustration only. Consistent character design."
                    metinViewModel.queryTextToImageForPage(consistentPrompt, isPremium || hasTrial, context) { bitmap ->
                        page.imageBitmap = bitmap
                        hikayeViewModel.storyPages.value = hikayeViewModel.storyPages.value
                    }
                }
            }
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

    val gradientBackground = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF3B0764),
            Color(0xFF581C87),
            Color(0xFF6B21A8)
        )
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
                val featuredTitle by anasayfaViewModel.featuredStoryTitle.observeAsState("")
                val featuredContent by anasayfaViewModel.featuredStoryContent.observeAsState("")
                val featuredImage by anasayfaViewModel.featuredStoryImage.observeAsState(R.drawable.story)
                
                if (featuredTitle.isNotEmpty() && hikayeId?.startsWith("featured_") == true) {
                    val featuredPages = remember { 
                        val sentences = featuredContent.split(". ").filter { it.isNotBlank() }
                        val pageCount = 4
                        val sentencesPerPage = (sentences.size + pageCount - 1) / pageCount
                        sentences.chunked(sentencesPerPage).mapIndexed { index, chunk ->
                            com.kaankilic.discoverybox.entitiy.StoryPage(index + 1, chunk.joinToString(". ") + ".", "", null)
                        }.take(4)
                    }
                    var currentFeaturedPageIndex by remember { mutableStateOf(0) }
                    
                    val storyImages = remember {
                        when(hikayeId) {
                            "featured_1" -> listOf(R.drawable.orman1, R.drawable.orman2, R.drawable.orman3, R.drawable.orman4)
                            "featured_2" -> listOf(R.drawable.uzay1, R.drawable.uzay2, R.drawable.uzay3, R.drawable.uzay4)
                            "featured_3" -> listOf(R.drawable.deniz1, R.drawable.deniz2, R.drawable.deniz3, R.drawable.deniz4)
                            "featured_4" -> listOf(R.drawable.zaman1, R.drawable.zaman2, R.drawable.zaman3, R.drawable.zaman4)
                            "featured_5" -> listOf(R.drawable.ejder1, R.drawable.ejder2, R.drawable.ejder3, R.drawable.ejder4)
                            "featured_6" -> listOf(R.drawable.zeki1, R.drawable.zeki2, R.drawable.zeki3, R.drawable.zeki4)
                            else -> listOf(featuredImage, featuredImage, featuredImage, featuredImage)
                        }
                    }
                    
                    if (featuredPages.isNotEmpty()) {
                        val currentFeaturedPage = featuredPages.getOrNull(currentFeaturedPageIndex) ?: featuredPages[0]
                        val currentImage = storyImages.getOrNull(currentFeaturedPageIndex) ?: featuredImage
                        
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Box(modifier = Modifier.fillMaxWidth().height(480.dp)) {
                                Image(
                                    painter = painterResource(currentImage),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                )
                                Box(
                                    modifier = Modifier.fillMaxSize().background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Transparent,
                                                Color(0xFF3B0764).copy(alpha = 0.2f),
                                                Color(0xFF3B0764).copy(alpha = 0.5f),
                                                Color(0xFF3B0764).copy(alpha = 0.8f),
                                                Color(0xFF3B0764)
                                            ),
                                            startY = 0f,
                                            endY = 1500f
                                        )
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(
                                            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                                            end = 16.dp
                                        )
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFCD34D))
                                        .clickable {
                                            if (isPlayingAudio) {
                                                metinViewModel.pause()
                                                isPlayingAudio = false
                                            } else {
                                                metinViewModel.speak(currentFeaturedPage.content)
                                                isPlayingAudio = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(if (isPlayingAudio) R.drawable.pause else R.drawable.play),
                                        contentDescription = if (isPlayingAudio) "Pause" else "Play",
                                        tint = Color(0xFF6B46C1),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                        
                        Text(
                            text = featuredTitle,
                            modifier = Modifier.padding(5.dp).fillMaxWidth().padding(10.dp),
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 45.sp,
                            fontFamily = sandtitle
                        )
                        
                        Text(
                            text = "${stringResource(R.string.page)} ${currentFeaturedPage.pageNumber} / ${featuredPages.size}",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                        
                        Text(
                            text = currentFeaturedPage.content,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                            lineHeight = 35.sp,
                            color = Color.White,
                            textAlign = TextAlign.Justify,
                            fontSize = 25.sp,
                            fontFamily = andikabody
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { if (currentFeaturedPageIndex > 0) currentFeaturedPageIndex-- },
                                enabled = currentFeaturedPageIndex > 0,
                                colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                            ) {
                                Text(stringResource(R.string.previous), color = Color(0xFF6B46C1), fontFamily = sandtitle)
                            }
                            
                            Button(
                                onClick = { if (currentFeaturedPageIndex < featuredPages.size - 1) currentFeaturedPageIndex++ },
                                enabled = currentFeaturedPageIndex < featuredPages.size - 1,
                                colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                            ) {
                                Text(stringResource(R.string.next), color = Color(0xFF6B46C1), fontFamily = sandtitle)
                            }
                        }
                        
                        Button(
                            onClick = { navController.navigate("anasayfa") },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D)),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.return_home),
                                fontSize = 22.sp,
                                fontFamily = sandtitle,
                                color = Color(0xFF6B46C1)
                            )
                        }
                    }
                } else if (kaan.title.isNotEmpty()) {
                    val savedStoryPages = remember { kaan.content.split("\n\nSayfa ").filter { it.isNotBlank() }.mapIndexed { index, content -> 
                        com.kaankilic.discoverybox.entitiy.StoryPage(index + 1, content.removePrefix("${index + 1}:\n"), "", null)
                    }}
                    var currentSavedPageIndex by remember { mutableStateOf(0) }
                    
                    if (savedStoryPages.isNotEmpty()) {
                        val currentSavedPage = savedStoryPages.getOrNull(currentSavedPageIndex) ?: savedStoryPages[0]
                        
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Box(modifier = Modifier.fillMaxWidth().height(480.dp)) {
                                AsyncImage(
                                    model = kaan.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                )
                                Box(
                                    modifier = Modifier.fillMaxSize().background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Transparent,
                                                Color(0xFF3B0764).copy(alpha = 0.2f),
                                                Color(0xFF3B0764).copy(alpha = 0.5f),
                                                Color(0xFF3B0764).copy(alpha = 0.8f),
                                                Color(0xFF3B0764)
                                            ),
                                            startY = 0f,
                                            endY = 1500f
                                        )
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(
                                            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                                            end = 16.dp
                                        )
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFCD34D))
                                        .clickable {
                                            if (isPlayingAudio) {
                                                metinViewModel.pause()
                                                isPlayingAudio = false
                                            } else {
                                                metinViewModel.speak(currentSavedPage.content)
                                                isPlayingAudio = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(if (isPlayingAudio) R.drawable.pause else R.drawable.play),
                                        contentDescription = if (isPlayingAudio) "Pause" else "Play",
                                        tint = Color(0xFF6B46C1),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                        
                        Text(
                            text = "${stringResource(R.string.page)} ${currentSavedPage.pageNumber} / ${savedStoryPages.size}",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                        
                        Text(
                            text = currentSavedPage.content,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                            lineHeight = 35.sp,
                            color = Color.White,
                            textAlign = TextAlign.Justify,
                            fontSize = 25.sp,
                            fontFamily = andikabody
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { if (currentSavedPageIndex > 0) currentSavedPageIndex-- },
                                enabled = currentSavedPageIndex > 0,
                                colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                            ) {
                                Text(stringResource(R.string.previous), color = Color(0xFF6B46C1), fontFamily = sandtitle)
                            }
                            
                            Button(
                                onClick = { if (currentSavedPageIndex < savedStoryPages.size - 1) currentSavedPageIndex++ },
                                enabled = currentSavedPageIndex < savedStoryPages.size - 1,
                                colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                            ) {
                                Text(stringResource(R.string.next), color = Color(0xFF6B46C1), fontFamily = sandtitle)
                            }
                        }
                        
                        Button(
                            onClick = { navController.navigate("saveSayfa") },
                            colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D)),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.MYSTORIES),
                                fontSize = 22.sp,
                                fontFamily = sandtitle,
                                color = Color(0xFF6B46C1)
                            )
                        }
                    }
            }else{
                    if (storyPages.isEmpty() && (hikayeyiOlustur.isEmpty() || generatedImage == null)) {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedLoadingImages()
                            Text(stringResource(R.string.new_world_creating), fontFamily = andikabody, fontSize = 20.sp, color = Color.White)
                            Text(stringResource(R.string.characters_coming_alive),fontFamily = andikabody, fontSize = 20.sp, color = Color.White)
                            Text(stringResource(R.string.sounds_starting),fontFamily = andikabody, fontSize = 20.sp, color = Color.White)


                        }
                    } else if (storyPages.isNotEmpty()) {
                        val currentPage = storyPages.getOrNull(currentPageIndex)
                        if (currentPage != null) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Box(modifier = Modifier.fillMaxWidth().height(480.dp)) {
                                    currentPage.imageBitmap?.let { bitmap ->
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Page Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                                        )
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Transparent,
                                                        Color(0xFF3B0764).copy(alpha = 0.2f),
                                                        Color(0xFF3B0764).copy(alpha = 0.5f),
                                                        Color(0xFF3B0764).copy(alpha = 0.8f),
                                                        Color(0xFF3B0764)
                                                    ),
                                                    startY = 0f,
                                                    endY = 1500f
                                                )
                                            )
                                        )
                                    } ?: CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                    
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(
                                                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                                                end = 16.dp
                                            )
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFCD34D))
                                            .clickable {
                                                if (isPlayingAudio) {
                                                    metinViewModel.pause()
                                                    isPlayingAudio = false
                                                } else {
                                                    metinViewModel.speak(currentPage.content)
                                                    isPlayingAudio = true
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(if (isPlayingAudio) R.drawable.pause else R.drawable.play),
                                            contentDescription = if (isPlayingAudio) "Pause" else "Play",
                                            tint = Color(0xFF6B46C1),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                            
                            Text(
                                text = "${stringResource(R.string.page)} ${currentPage.pageNumber} / ${storyPages.size}",
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                            
                            Text(
                                text = currentPage.content,
                                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                                lineHeight = 35.sp,
                                color = Color.White,
                                textAlign = TextAlign.Justify,
                                fontSize = 25.sp,
                                fontFamily = andikabody
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { if (currentPageIndex > 0) currentPageIndex-- },
                                    enabled = currentPageIndex > 0,
                                    colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                                ) {
                                    Text(stringResource(R.string.previous), color = Color(0xFF6B46C1), fontFamily = sandtitle)
                                }
                                
                                Button(
                                    onClick = { if (currentPageIndex < storyPages.size - 1) currentPageIndex++ },
                                    enabled = currentPageIndex < storyPages.size - 1,
                                    colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                                ) {
                                    Text(stringResource(R.string.next), color = Color(0xFF6B46C1), fontFamily = sandtitle)
                                }
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        val userId = getCurrentUserId()
                                        if (userId != null && currentPage.imageBitmap != null) {
                                            val fullStory = storyPages.joinToString("\n\n") { "Sayfa ${it.pageNumber}:\n${it.content}" }
                                            metinViewModel.saveImageToStorage(currentPage.imageBitmap!!, userId)
                                            metinViewModel.imageSavedUrl.observe(context as androidx.lifecycle.LifecycleOwner) { imageUrl ->
                                                if (imageUrl != null) {
                                                    metinViewModel.saveStoryForUser(
                                                        hikayeId ?: "Hikaye",
                                                        fullStory,
                                                        imageUrl,
                                                        userId
                                                    )
                                                }
                                            }
                                            showSaveAnimation = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                                ) {
                                    Text(stringResource(R.string.save), color = Color(0xFF6B46C1), fontFamily = sandtitle)
                                }
                                
                                Button(
                                    onClick = { navController.navigate("saveSayfa") },
                                    colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D))
                                ) {
                                    Text(
                                        text = stringResource(R.string.MYSTORIES),
                                        fontSize = 18.sp,
                                        fontFamily = sandtitle,
                                        color = Color(0xFF6B46C1)
                                    )
                                }
                            }
                            
                            if (showSaveAnimation) {
                                LaunchedEffect(Unit) {
                                    delay(2000)
                                    showSaveAnimation = false
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF10B981))
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        stringResource(R.string.story_saved),
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = sandtitle
                                    )
                                }
                            }
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
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(
                                                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                                                end = 16.dp
                                            )
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFCD34D))
                                            .clickable { audioVisible = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.play),
                                            contentDescription = "Play",
                                            tint = Color(0xFF6B46C1),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
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
                            Button(
                                onClick = {
                                    val prompt = hikayeViewModel.getCurrentPrompt()
                                    hikayeViewModel.generateStory(prompt, "Medium")
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D)),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.Rebuild),
                                    fontSize = 22.sp,
                                    fontFamily = sandtitle,
                                    color = Color(0xFF6B46C1)
                                )
                            }

                            Button(
                                onClick = { navController.navigate("saveSayfa") },
                                colors = ButtonDefaults.buttonColors(Color(0xFFFCD34D)),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.MYSTORIES),
                                    fontSize = 22.sp,
                                    fontFamily = sandtitle,
                                    color = Color(0xFF6B46C1)
                                )
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
    val dbRepo = hikayeViewModel.dbRepo
    val context = LocalContext.current
    val generatedImage by metinViewModel.imageBitmap.observeAsState(null)
    var isPlaying by remember { mutableStateOf(false) }
    var isStopped by remember { mutableStateOf(false) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var isAudioLoading by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val barHeights = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = if (isPlaying) (index * 10 + 10).toFloat() else 0f,
            targetValue = if (isPlaying) (index * 10 + 50).toFloat() else 0f,
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
            initialValue = Color(0xFFFCD34D),
            targetValue = Color(0xFFA855F7),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        modifier = Modifier
            .size(320.dp, 450.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6B46C1))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            generatedImage?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Generated Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            IconButton(
                onClick = { onClose() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
                    .background(Color.White.copy(0.9f), CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_close_24),
                    contentDescription = "Close",
                    tint = Color(0xFF6B46C1)
                )
            }
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
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFCD34D))
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
                if (isAudioLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = Color(0xFF6B46C1)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = "play",
                        tint = Color(0xFF6B46C1),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            if (isAudioLoading) {
                Text(
                    text = "Ses oluşturuluyor...",
                    fontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = {
                    metinViewModel.stopMediaPlayer()
                    metinViewModel.stop()
                    isStopped = true
                },
                enabled = isPlaying && !isStopped,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(if (isStopped) 0.3f else 0.9f), CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.pause),
                    contentDescription = "pause",
                    tint = Color(0xFF6B46C1)
                )
            }
        }


    }
}

@Composable
fun AudioSave(navController: NavController,hikayeViewModel: HikayeViewModel, metinViewModel: MetinViewModel,  onClose: () -> Unit) {
    val kaan by hikayeViewModel.hikaye.observeAsState(Hikaye())
    var isPlaying by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val barHeights = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = if (isPlaying) (index * 10 + 10).toFloat() else 0f,
            targetValue = if (isPlaying) (index * 10 + 50).toFloat() else 0f,
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
            initialValue = Color(0xFFFCD34D),
            targetValue = Color(0xFFA855F7),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Column(
        modifier = Modifier
            .size(320.dp, 450.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6B46C1))
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = kaan.imageUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )

            IconButton(
                onClick = { onClose() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(36.dp)
                    .background(Color.White.copy(0.9f), CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_close_24),
                    contentDescription = "Close",
                    tint = Color(0xFF6B46C1)
                )
            }
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


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = {
                    metinViewModel.speak(kaan.content)
                    isPlaying = true
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFFCD34D), CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = "Play",
                    tint = Color(0xFF6B46C1)
                )
            }

            IconButton(
                onClick = {
                    metinViewModel.stopMediaPlayer()
                    metinViewModel.stop()
                    isPlaying = false
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(0.9f), CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.pause),
                    contentDescription = "Pause",
                    tint = Color(0xFF6B46C1)
                )
            }
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


