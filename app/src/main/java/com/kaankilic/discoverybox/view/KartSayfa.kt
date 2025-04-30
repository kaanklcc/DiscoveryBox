package com.kaankilic.discoverybox.view

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
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

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
fun MeyveKartSirali(viewModel: CardSayfaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val wordList = remember { viewModel.words } // Tüm kelimeler
    var currentIndex by remember { mutableStateOf(0) } // Şu anki kartın indeksi
    var isFlipped by remember { mutableStateOf(false) } // Kartın dönme durumu
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val delbold= FontFamily(Font(R.font.delbold))
    val currentLang = context.resources.configuration.locales[0].language


    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFf6d162), // Üstteki renk
            Color(0xFF84d6b0)  // Alttaki renk
        ),
        startY = 0f,
        endY = 1800f // Bu değeri ekran yüksekliğine göre ayarlayabilirsiniz.
    )

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    androidx.compose.material3.Text(
                        text = stringResource(R.string.WordQuiz),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,fontFamily = delbold
                    )
                },
                modifier = Modifier.background(Color.Transparent),
                colors = TopAppBarColors(
                    Color(0xFF386195),
                    Color(0xFF386195),
                    Color(0xFF386195),
                    Color.White,
                    Color.White
                ),

            )
        },
        modifier = Modifier.background(gradientBackground)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            // Arka planda çimen görseli
            Image(
                painter = painterResource(id = R.drawable.sky),
                contentDescription = "sky",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Görselin ekranı tamamen doldurması için
            )


            Column(
                modifier = Modifier.fillMaxSize()
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (wordList.isNotEmpty()) {
                    val currentWord = wordList[currentIndex]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
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
                                        .aspectRatio(1f)
                                        .align(Alignment.CenterVertically),
                                    contentScale = ContentScale.Fit // Orijinal oranı koruyarak sığdırır

                                )

                                Box(
                                    modifier = Modifier.offset(y=(-45).dp, x = (-30).dp)

                                        .background(
                                            color = Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(14.dp)
                                        .align(Alignment.CenterVertically)
                                )
                                {
                                    Text(
                                        text = stringResource(R.string.WordLearnWelcomeMessage),
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontSize = 12.sp
                                        ),fontFamily = delbold
                                    )
                                }


                            }


                        }

                        // Kart Animasyonu
                        val rotation by animateFloatAsState(
                            targetValue = if (isFlipped) 180f else 0f,
                            animationSpec = tween(durationMillis = 600)
                        )

                        Box(
                            modifier = Modifier
                                .size(250.dp)
                                .graphicsLayer {
                                    rotationY = rotation
                                    cameraDistance = 12 * density
                                }
                                .clickable { isFlipped = !isFlipped }, // Tıklayınca kart döner
                            contentAlignment = Alignment.Center
                        ) {
                            if (rotation <= 90f) {
                                // Ön yüz
                                FrontCardContent(currentWord)
                            } else {
                                // Arka yüz (180° görünümü düzeltiliyor)
                                BackCardContent(currentWord, flipped = true)
                            }
                        }

                       // Spacer(modifier = Modifier.height(10.dp))

                        // "Sonraki" Butonu
                        Button(
                            modifier = Modifier.size(height = 52.dp, width = 120.dp)
                                .offset(y = (-20).dp),
                            colors = ButtonDefaults.buttonColors(Color(0xFFf6d162)),
                            onClick = {
                                isFlipped = false
                                currentIndex = (currentIndex + 1) % wordList.size
                            }
                        ) {
                            Text("Next", fontSize = 22.sp, color = Color.White,fontFamily = delbold)
                        }
                    }
                } else {
                    // Liste boşsa yüklenme mesajı
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = /*"Cards Are Loading..."*/stringResource(R.string.CardsAreLoading___),fontFamily = delbold)
                    }
                }
            }
        }
    }


}

@Composable
fun FrontCardContent(word: Word) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = (-20).dp)
            //.background(Color.White)
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = word.imageUrl),
            contentDescription = word.nameTr,
            modifier = Modifier.fillMaxSize()
                .clip(CircleShape)
                .size(250.dp)
                .aspectRatio(1f),
                    contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun BackCardContent(word: Word, flipped: Boolean = false) {
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val delbold= FontFamily(Font(R.font.delbold))
    val currentLang = context.resources.configuration.locales[0].language
    val displayText = if (currentLang == "tr") word.nameTr else word.nameEn

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




     Box (modifier = Modifier.fillMaxSize()){

         Image(
             painter = painterResource(id = R.drawable.sky),
             contentDescription = "sky",
             modifier = Modifier.fillMaxSize(),
             contentScale = ContentScale.Crop,
             alpha = (0.25f)
         )


     }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = 180f // Metni düzeltmek için kartın tam 180 derece döndüğünü belirtiyoruz
            }
            .padding(6.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // İngilizce Kelime
        Text(
            text = displayText,
            color = Color.White,
            fontSize = 45.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,fontFamily = delbold

        )

        Spacer(modifier = Modifier.height(16.dp)) // Metin ile ikon arasında boşluk

        // Seslendirme İkonu
        Image(
            painter = painterResource(id = R.drawable.greenvoice),
            contentDescription = "hizlandir icon",
            modifier = Modifier
                .size(100.dp) // İkonun boyutunu ayarlamak için
                .clip(CircleShape) // Yuvarlak yapmak için CircleShape kullanılır
                .background(Color.LightGray) // İsteğe bağlı arka plan rengi
                .clickable {
                    textToSpeech?.speak(displayText, TextToSpeech.QUEUE_FLUSH, null, null)
                }
        )
    }


}



