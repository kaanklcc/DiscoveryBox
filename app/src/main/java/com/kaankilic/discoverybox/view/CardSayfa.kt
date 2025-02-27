package com.kaankilic.discoverybox.view

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
import androidx.compose.ui.text.TextStyle
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
    val selectedWord = remember { mutableStateOf("") }
    val selectedImage = remember { mutableStateOf("") }
    val isMatch = remember { mutableStateOf(false) }
    val currentGroupIndex = remember { mutableStateOf(0) }
    val matchedItem = remember { mutableStateOf<String?>(null) }
    val showCelebration = remember { mutableStateOf(false) }
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val isGameOver = remember { mutableStateOf(false) }




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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "MATCHING GAME",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
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
                    text = "Tebrikler, oyun bitti!",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
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
                    modifier = Modifier.fillMaxSize(),
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
                                    androidx.compose.material.Text(
                                        text =  "Welcome again!. " +
                                                "Let's match the words",
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontSize = 20.sp
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

                                        Box(
                                            modifier = Modifier
                                                .size(350.dp, 350.dp)
                                                .padding(paddingValues)
                                                .background(Color.LightGray.copy(alpha = 0.55f), shape = RectangleShape),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                   // .size(300.dp, 300.dp)
                                                    .padding(2.dp)
                                                    .padding(6.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {

                                                Text(
                                                    text = word.nameEn,
                                                    color = Color.White,
                                                    fontSize = 70.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    textAlign = TextAlign.Center
                                                )

                                                Spacer(modifier = Modifier.height(5.dp))

                                                Image(
                                                    painter = painterResource(id = R.drawable.greenvoice),
                                                    contentDescription = "voice image",
                                                    modifier = Modifier
                                                        .size(100.dp)
                                                        .clip(CircleShape)
                                                        //.background(Color.LightGray)
                                                        .clickable {
                                                            textToSpeech?.speak(word.nameEn, TextToSpeech.QUEUE_FLUSH, null, null)
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
                                                        isGameOver


                                                    )
                                                },    contentScale = ContentScale.Crop // Resmi kırpmadan büyütmek için

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Congratulations!",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(id = R.drawable.pars),
                contentDescription = "pars",
                modifier = Modifier.size(400.dp)


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
    isGameOver: MutableState<Boolean>
) {
    val matchedWord = words.find { it.imageUrl == selectedImage }
    isMatch.value = matchedWord != null

    if (isMatch.value) {
        matchedItem.value = matchedWord?.nameEn
        matchedWord?.let { cardSayfaViewModel.removeWord(it) }

        if (cardSayfaViewModel.shuffledWords.none { it.isVisible }) {
           showCelebration.value=true
            cardSayfaViewModel.loadNextGroup(currentGroupIndex.value,3,isGameOver)
        }
    }
}












