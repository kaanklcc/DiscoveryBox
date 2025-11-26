package com.kaankilic.discoverybox.view

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaankilic.discoverybox.R

@Composable
fun Audio() {

    val infiniteTransition = rememberInfiniteTransition()
    val barHeights = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = (index * 10 + 10).toFloat(),
            targetValue = (index * 10 + 50).toFloat(),
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
            .size(250.dp,400.dp)
            .background(Color.LightGray.copy(alpha = 0.35f), shape = RectangleShape),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Box(modifier = Modifier.size(300.dp,180.dp)) {
            // Hikaye fotoğrafını ortalar
            Image(
                painter = painterResource(id = R.drawable.story),
                contentDescription = "story",
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center) // Fotoğrafı ekranın ortasına yerleştirir
            )

            // Çarpı ikonunu sağ üst köşeye yerleştirir
            Image(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd) // Çarpıyı sağ üst köşeye hizalar
                    .padding(end = 10.dp) // Sağ üst köşeye biraz boşluk ekler (isteğe bağlı)
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


            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                Image(
                    painter = painterResource(id = R.drawable.playstory),
                    contentDescription = "play icon",
                    modifier = Modifier
                        .size(40.dp) // İkonun boyutunu ayarlamak için
                        .clip(CircleShape) // Yuvarlak yapmak için CircleShape kullanılır
                        .background(Color.LightGray) // İsteğe bağlı arka plan rengi
                        .border(2.dp, Color.Gray, CircleShape) // İsteğe bağlı kenarlık
                )


                Image(
                    painter = painterResource(id = R.drawable.pausestory),
                    contentDescription = "play icon",
                    modifier = Modifier
                        .size(40.dp) // İkonun boyutunu ayarlamak için
                        .clip(CircleShape) // Yuvarlak yapmak için CircleShape kullanılır
                        .background(Color.LightGray) // İsteğe bağlı arka plan rengi
                        .border(2.dp, Color.Gray, CircleShape) // İsteğe bağlı kenarlık
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
        // Çubukların çizimi
    }
}

@Preview
@Composable
private fun Audyo() {
    Audio()
}