package com.kaankilic.discoverybox.view

import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.GameViewModel

import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val context = LocalContext.current
    val currentData by viewModel.currentData.collectAsState()

    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(key1 = context) {

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Türkçe dilini ayarla
                val result = textToSpeech?.setLanguage(Locale("eng", "ENG"))
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


    if (currentData != null) {
        val (currentColour, words) = currentData!!

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "COLOR MATCHINGG GAME", fontSize = 35.sp, textAlign = TextAlign.Center) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = getColorFromName(currentColour), // Dinamik renk burada
                        titleContentColor = Color.White, // Başlık rengi
                        actionIconContentColor = Color.White // İkon rengi (varsa)
                    )
                )

            }
        ){paddingValues ->
            Box( // Arka plan rengini bu Box ile kontrol edelim
                modifier = Modifier
                    .fillMaxSize()
                    .background(getColorFromName(currentColour)) // Renk burada uygulanır
                    .padding(paddingValues)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize(),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    Spacer(Modifier.height(0.1.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ){
                        Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Image(
                                painter = painterResource(id = R.drawable.pars),
                                contentDescription = "pars",
                                modifier = Modifier.size(170.dp).offset(y=(-25).dp, x = (-20).dp)
                                    .align(Alignment.CenterVertically)

                            )

                            Box(
                                modifier = Modifier.offset(y=(-65).dp, x = (-50).dp)

                                    .background(
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            {
                                androidx.compose.material.Text(
                                    text =  "Welcome again!." +
                                            "Let's find the right colors",
                                    style = TextStyle(
                                        color = Color.Black,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }

                    }
                    // İngilizce Renk
                    Text(
                        text = currentColour,
                        color = Color.Black,
                        fontSize = 55.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y=(-45).dp)

                    )
                    // Seslendirme İkonu
                    Image(
                        painter = painterResource(id = R.drawable.greenvoice),
                        contentDescription = "hizlandir icon",
                        modifier = Modifier
                            .offset(y=(-35).dp)
                            .size(80.dp) // İkonun boyutunu ayarlamak için
                            .clip(CircleShape) // Yuvarlak yapmak için CircleShape kullanılır
                            .background(Color.LightGray) // İsteğe bağlı arka plan rengi
                            .clickable {
                                textToSpeech?.speak(currentColour, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                    )


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        words.forEach { word ->
                            Image(
                                painter = rememberImagePainter(data = word.imageUrl),
                                contentDescription = word.nameEn,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clickable {
                                        if (word.colour == currentColour) {
                                            Toast.makeText(
                                                context, // Burada context kullanıyoruz
                                                "Doğru!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            viewModel.loadNewData()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Yanlış!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            )
                        }
                    }
                }


            }


        }

    }
}



fun getColorFromName(colorName: String): Color {
    return when (colorName.lowercase()) {
        "red" -> Color.Red
        "green" -> Color.Green
        "blue" -> Color(0xFF2196F3)
        "yellow" -> Color.Yellow
        "black" -> Color.Black
        "white" -> Color.White
        "orange" -> Color(0xFFFFA500)
        "purple" -> Color(0xFFA402A4)
        "pink" -> Color(0xFFFFC0CB)
        "brown" -> Color(0xFF4F3004)
        else -> Color.White// Tanımlı olmayan renkler için varsayılan renk
    }
}
@Composable
fun GameApp() {
    val viewModel: GameViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.loadNewData()
    }

    GameScreen(viewModel = viewModel)
}


