package com.kaankilic.discoverybox.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    val sandtitle= FontFamily(Font(R.font.sandtitle))


    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE3F2FD), // Nane Yeşili (rahatlatıcı ve doğayla bağlantılı)

        )
    )

    val gradientBrush1 = Brush.linearGradient(
        colors = listOf(
            Color(0xFFd5e0fe),
            Color(0xFFfbdceb), // Sarı

            // Açık Mavi
        )
    )
    val gradientBrush2 = Brush.linearGradient(
        colors = listOf(
            Color(0xFFfbdceb), // Sarı
            Color(0xFFd5e0fe),
            // Açık Mavi

        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = stringResource(R.string.Story), fontSize = 42.sp, textAlign = TextAlign.Center,fontFamily = sandtitle) }
                ,colors = TopAppBarColors( Color(0xFFE3F2FD), Color(0xFF81D4FA), Color(0xFF81D4FA), Color(0xFF353BA4), Color(0xFF353BA4)),
                navigationIcon  = {
                    IconButton(modifier = Modifier.padding(start = 8.dp) .size(55.dp), onClick = {
                        navController.navigate("anasayfa")
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            contentScale = ContentScale.Crop,

                            )

                    }
                }


                )

        }
    ){paddingValues ->

        Column(
            modifier = Modifier
                .background(Color(0xFFE3F2FD))
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(17.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(gradientBrush1)
                    .wrapContentSize()
                    .clickable {
                        navController.navigate("hikaye")
                    }

            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), // Tüm genişliği kaplasın ki TextAlign.Left etkili olsun
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start // Sola hizalama sağlandı
                ) {
                    Text(
                        stringResource(R.string.CreatetheStory),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 26.sp
                        , fontFamily = sandtitle,

                       // color =  Color(0xFFF454a94),
                        modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                    )

                    Image(
                        painter = painterResource(R.drawable.create),
                        contentDescription = "Kaan Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .graphicsLayer {
                               /* shadowElevation = 8.dp.toPx()
                                shape = RoundedCornerShape(10.dp)
                                clip = true*/
                            }
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    color = Color.Transparent.copy(alpha = 0f),
                                    size = size
                                )
                            }
                    )

                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(17.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(gradientBrush2)
                    .wrapContentSize()
                    .clickable {
                        navController.navigate("saveSayfa")
                    }

            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(), // Tüm genişliği kaplasın ki TextAlign.Left etkili olsun
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start // Sola hizalama sağlandı
                ) {
                    Text(
                        stringResource(R.string.MYSTORIES),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 26.sp
                        , fontFamily = sandtitle,
                        modifier = Modifier.fillMaxWidth(), // Text'in genişliğini doldurması için
                    )

                    Image(
                        painter = painterResource(R.drawable.savestory),
                        contentDescription = "Kaan Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .graphicsLayer {
                                /* shadowElevation = 8.dp.toPx()
                                 shape = RoundedCornerShape(10.dp)
                                 clip = true*/
                            }
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    color = Color.Transparent.copy(alpha = 0f),
                                    size = size
                                )
                            }
                    )

                }
            }

        }


    }

}

