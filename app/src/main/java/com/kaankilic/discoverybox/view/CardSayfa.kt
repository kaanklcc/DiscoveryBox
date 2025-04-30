package com.kaankilic.discoverybox.view

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Word
import com.kaankilic.discoverybox.viewmodel.CardSayfaViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchGameScreen(cardSayfaViewModel: CardSayfaViewModel, isEnglish: Boolean) {
    val words = cardSayfaViewModel.words
    val shuffledWords = cardSayfaViewModel.shuffledWords
    val shuffledImages = cardSayfaViewModel.shuffledImages
    val selectedImage = remember { mutableStateOf("") }
    val isMatch = remember { mutableStateOf(false) }
    val currentGroupIndex = remember { mutableStateOf(0) }
    val matchedItem = remember { mutableStateOf<String?>(null) }
    val showCelebration = remember { mutableStateOf(false) }
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val isGameOver = remember { mutableStateOf(false) }
    val delbold= FontFamily(Font(R.font.delbold))
    val currentLang = context.resources.configuration.locales[0].language





    DisposableEffect(key1 = context) {

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Türkçe dilini ayarla
                val result = textToSpeech?.setLanguage(Locale("en", "US"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TextToSpeech", "Dil desteklenmiyor.")
                }
            } else {
                Log.e("TextToSpeech", "Başlatma başarısız.")
            }
        }

        onDispose {
            textToSpeech?.shutdown()
        }
    }


   val infiniteTransition = rememberInfiniteTransition()
   val animatedAlpha = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF34909a),
            Color(0xFF34909a),
        ),
        startY = 0f,
        endY = 1800f
    )

    val groupSize = 3
    val currentWords = shuffledWords.drop(currentGroupIndex.value * (groupSize/3)).take(groupSize/3)
    val currentImages = shuffledImages.drop(currentGroupIndex.value * groupSize ).take(groupSize )
    val currentTargetWord = currentWords.first().nameEn

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = /*"MATCHING GAME"*/stringResource(R.string.MATCHINGGAME),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = delbold
                    )
                },
                modifier = Modifier.background(Color.Transparent),
                colors = TopAppBarColors(
                    Color(0xFF34909a),
                    Color(0xFF34909a),
                    Color(0xFF34909a),
                    Color.White,
                    Color.White
                )
            )
        },
        containerColor = Color.DarkGray,
        modifier = Modifier.background(gradientBackground)
    ) { paddingValues ->

        if (isGameOver.value) {
            Log.d("isgameover girildi", "isgameovergirildi")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text =/* "Congratulations, Game is Over!"*/stringResource(R.string.CongratulationsGameisOver),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = delbold
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.cimen),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,

                    )

                Column(
                    modifier = Modifier.fillMaxSize().padding(top = 4.dp),
                  // verticalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showCelebration.value) {
                        CelebrationAnimation(
                            onAnimationEnd = {
                                showCelebration.value = false
                                Log.d("CelebrationAnimation", "Celebration animation started")
                                // Sonraki gruba geç
                                cardSayfaViewModel.loadNextGroup(currentGroupIndex.value, 3,isGameOver)
                                Log.d("CelebrationAnimation", "Next group loaded and celebration ended")
                            },
                            cardSayfaViewModel = cardSayfaViewModel, // Burada view model'i geçiriyoruz
                            currentGroupIndex = currentGroupIndex,
                            isGameOver = isGameOver
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ){
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        ){
                            Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {

                                Image(
                                    painter = painterResource(id = R.drawable.pars),
                                    contentDescription = "pars",
                                    modifier = Modifier.size(200.dp)
                                        .align(Alignment.CenterVertically)
                                )

                                Box(
                                    modifier = Modifier.offset(y=(-45).dp, x = (-30).dp)

                                        .background(
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(16.dp)
                                        .align(Alignment.CenterVertically)
                                )
                                {
                                    Text(
                                        text = stringResource(R.string.matchWelcomeMessage),
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontSize = 12.sp,
                                            fontFamily = delbold
                                        )
                                    )
                                }


                            }


                        }
                        Spacer(modifier = Modifier.height(10.dp))


                        if (showCelebration.value) {
                            CelebrationAnimation(
                                onAnimationEnd = {
                                    showCelebration.value = false
                                    Log.d("CelebrationAnimation", "Celebration animation started")
                                    // Sonraki gruba geç
                                    cardSayfaViewModel.loadNextGroup(currentGroupIndex.value, 3,isGameOver)
                                    Log.d("CelebrationAnimation", "Next group loaded and celebration ended")
                                },
                                cardSayfaViewModel = cardSayfaViewModel, // Burada view model'i geçiriyoruz
                                currentGroupIndex = currentGroupIndex,
                                isGameOver = isGameOver
                            )
                        }
                        else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                // Kelimeleri Göster
                                LazyColumn(
                                    modifier = Modifier.fillMaxWidth().offset(y=(-110).dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    items(currentWords) { word ->
                                        val displayText = if (currentLang == "tr") word.nameTr else word.nameEn

                                        Box(
                                            modifier = Modifier
                                                .size(350.dp, 350.dp)
                                                .padding(paddingValues)
                                                .background(Color.LightGray.copy(alpha = 0.55f), shape = RectangleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(2.dp)
                                                    .padding(6.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {

                                                Text(
                                                    text = displayText,
                                                    color = Color.White,
                                                    fontSize = 70.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Image(
                                                    painter = painterResource(id = R.drawable.greenvoice),
                                                    contentDescription = "voice image",
                                                    modifier = Modifier
                                                        .size(100.dp)
                                                        .clip(CircleShape)
                                                        //.background(Color.LightGray)
                                                        .clickable {
                                                            textToSpeech?.speak(displayText, TextToSpeech.QUEUE_FLUSH, null, null)
                                                        }
                                                        .aspectRatio(1f),
                                                    contentScale = ContentScale.Fit
                                                )
                                            }

                                        }
                                    }
                                }

                                // Görselleri Göster
                                LazyRow(
                                    modifier = Modifier.offset(y = (-79).dp)
                                        .padding(bottom = 10.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    items(currentImages) { image ->
                                        Image(
                                            painter = rememberAsyncImagePainter(image.imageUrl),
                                            contentDescription = image.nameTr,
                                            modifier = Modifier
                                                .size(110.dp) // Genel boyutu belirle
                                                .aspectRatio(1f) // Kare orana yakın tutar
                                                .clip(CircleShape) // Eğer yuvarlak istiyorsan
                                                .background(Color.Transparent)
                                                .clickable {
                                                    selectedImage.value = image.imageUrl
                                                    checkMatch(
                                                        selectedImage.value,
                                                        words,
                                                        isMatch,
                                                        matchedItem,
                                                        cardSayfaViewModel,
                                                        currentGroupIndex,
                                                        showCelebration,
                                                        isGameOver,
                                                        currentTargetWord,
                                                        context


                                                    )
                                                },
                                            contentScale = ContentScale.Fit // Crop yerine Cover ya da Fit dene

                                        )
                                    }
                                }

                            }
                        }



                    }

                }
            }

            // Diğer oyun içeriği burada
        }



    }
}

@Composable
fun CelebrationAnimation(
    onAnimationEnd: () -> Unit,
    cardSayfaViewModel: CardSayfaViewModel,
    currentGroupIndex: MutableState<Int>,
    isGameOver: MutableState<Boolean>// MutableState<Int> alıyoruz

) {
    val delbold= FontFamily(Font(R.font.delbold))
    val context = LocalContext.current
    fun playSoundEffect(resId: Int) {
        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener { it.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        playSoundEffect(R.raw.correctlong)

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.Congratulations),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = delbold

            )
            Image(
                painter = painterResource(id = R.drawable.pars),
                contentDescription = "pars",
                modifier = Modifier.size(400.dp),



            )

        }


    }

    // Animasyon süresi boyunca gösterim
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000) // 3 saniye gösterim
        onAnimationEnd() // Animasyon bitince çağır
        if (!isGameOver.value) { // Eğer oyun bitmemişse
            onAnimationEnd()
            cardSayfaViewModel.loadNextGroup(currentGroupIndex.value, 3,isGameOver)
        }
        onAnimationEnd()
    }
}


fun checkMatch(
    selectedImage: String,
    words: List<Word>,
    isMatch: MutableState<Boolean>,
    matchedItem: MutableState<String?>,
    cardSayfaViewModel: CardSayfaViewModel,
    currentGroupIndex: MutableState<Int>,
    showCelebration: MutableState<Boolean>,
    isGameOver: MutableState<Boolean>,
    currentTargetWord: String,  // 🔴 Üstte gösterilen kelime
    context: Context
) {
    Log.d("checkMatch", "🖼 Seçilen Görsel URL: $selectedImage")
    Log.d("checkMatch", "🔠 Hedef Kelime: $currentTargetWord")  // 🔴 Üstteki kelimeyi loga bas

    words.forEach { word ->
        Log.d("checkMatch", "🔎 Kontrol Edilen: ${word.nameEn} | Görsel URL: ${word.imageUrl}")
    }

    // Seçilen görseli liste içinde bul
    val matchedWord = words.find { it.imageUrl.trim() == selectedImage.trim() }

    if (matchedWord != null && matchedWord.nameEn.equals(currentTargetWord, ignoreCase = true)) {
        // 🔥 Eğer hem görsel doğru hem de üstteki kelimeyle eşleşiyorsa doğru eşleşme
        isMatch.value = true
        matchedItem.value = matchedWord.nameEn

        Log.d("checkMatch", "✅ DOĞRU EŞLEŞME! Kelime: ${matchedWord.nameEn}")

        playSoundEffect(context, R.raw.correctshort)

        // Eşleşen kelimeyi listeden kaldır
        matchedWord.let { cardSayfaViewModel.removeWord(it) }

        if (cardSayfaViewModel.shuffledWords.none { it.isVisible }) {
            showCelebration.value = true
            cardSayfaViewModel.loadNextGroup(currentGroupIndex.value, 3, isGameOver)
        }
    } else {
        // 🚨 Yanlış eşleşme (ya görsel yanlış ya da kelime yanlış)
        isMatch.value = false
        Log.d("checkMatch", "❌ YANLIŞ EŞLEŞME! Seçilen: ${matchedWord?.nameEn ?: "Bulunamadı"}")
        playSoundEffect(context, R.raw.wrong)
    }
}








fun playSoundEffect(context: Context, resId: Int) {
    val mediaPlayer = MediaPlayer.create(context, resId)
    mediaPlayer?.start()
    mediaPlayer?.setOnCompletionListener { it.release() }
}













