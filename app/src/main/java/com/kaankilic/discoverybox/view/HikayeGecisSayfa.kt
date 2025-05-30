package com.kaankilic.discoverybox.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaankilic.discoverybox.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikayeGecis(navController: NavController) {
    val delbold= FontFamily(Font(R.font.delbold))

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFA5D6A7), // Nane Yeşili (rahatlatıcı ve doğayla bağlantılı)
            Color(0xFF81D4FA), // Açık Mavi (huzur ve hayal gücü)
            Color(0xFFFFAB91)  // Açık Şeftali Rengi (sıcaklık ve sevimlilik)
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = stringResource(R.string.Story), fontSize = 35.sp, textAlign = TextAlign.Center,fontFamily = delbold) }
                ,colors = TopAppBarColors( Color(0xFF81D4FA), Color(0xFF81D4FA), Color(0xFF81D4FA), Color.White, Color.White))

        }
    ){paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(gradientBrush)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .clickable { navController.navigate("hikaye")

                    }
                    .background(Color.Transparent)
            ) {
                Image(
                    painter = painterResource(R.drawable.create),
                    contentDescription = "Kaan Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            shadowElevation = 8.dp.toPx()
                            shape = RoundedCornerShape(30.dp)
                            clip = true
                        }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = Color.Black.copy(alpha = 0.1f),
                                size = size
                            )
                        }
                )
                Text(
                    text =stringResource(R.string.CreatetheStory),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = delbold,
                    modifier = Modifier
                        .align(Alignment.TopCenter) // Yazıyı ortalar
                        .padding(8.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .clickable { navController.navigate("saveSayfa") }
                    .background(Color.Transparent)
            ) {
                Image(
                    painter = painterResource(R.drawable.savestory),
                    contentDescription = "Kılıç Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            shadowElevation = 8.dp.toPx()
                            shape = RoundedCornerShape(30.dp)
                            clip = true
                        }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                color = Color.Black.copy(alpha = 0.1f),
                                size = size
                            )
                        }
                )
                Text(
                    text = stringResource(R.string.MYSTORIES),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = delbold,
                    modifier = Modifier
                        .align(Alignment.TopCenter) // Yazıyı ortalar
                        .padding(8.dp)
                )
            }
        }


    }

}

