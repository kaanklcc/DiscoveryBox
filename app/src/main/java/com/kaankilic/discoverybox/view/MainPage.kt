package com.kaankilic.discoverybox.view

import android.annotation.SuppressLint
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaankilic.discoverybox.R
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    //val konular by anasayfaViewModel.konular.observeAsState(emptyList()) // Boş bir liste ile başlatıyoruz
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val message = stringResource(R.string.logOutMessage)
    val actionLabel = stringResource(R.string.Yes)
    val exitedApp = stringResource(R.string.exitMessage)
    val wrongPassword = stringResource(R.string.WrongPassword)
    val context = LocalContext.current

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFCD7D7), // Sarı
            Color(0xFFB0D2FD), // Açık Mavi
           // Color(0xFFFF8A80)  // Pembe
        )
    )
    val gradientBrush2 = Brush.linearGradient(
        colors = listOf(
            Color(0xFFC5FCC6), // Sarı
            Color(0xFFFCC1FC), // Açık Mavi
            // Color(0xFFFF8A80)  // Pembe
        )
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.DiscoveryBox),
                            fontSize = 37.sp,
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent // Arka planı şeffaf yap
                    ),


                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                val sb = snackbarHostState
                                    .showSnackbar(
                                        message = /*"Do you want to log out?"*/message,
                                        actionLabel = /*"Yes"*/actionLabel
                                    )
                                if (sb == SnackbarResult.ActionPerformed) {
                                    snackbarHostState.showSnackbar(message = /*"exited the application"*/exitedApp)
                                    /*anasayfaViewModel.signOut()
                                    navController.navigate("girisSayfa")*/

                                }
                            }


                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White,
                                modifier = Modifier.size(45.dp)
                            )

                        }
                    }
                )


        }
            ) { paddingValues ->

        if ("blabla"=="blala") {
            Text(
                text = /*"There is no story to show"*/stringResource(R.string.NoStoryMessage),
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                textAlign = TextAlign.Center
            )
        } else {

            Column(modifier = Modifier.fillMaxSize().padding(top = 80.dp).background(Color(
                0xFFFFFFFF
            )
            ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .padding(17.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(gradientBrush)
                        .wrapContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), // Tüm genişliği kaplasın ki TextAlign.Left etkili olsun
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start // Sola hizalama sağlandı
                    ) {
                        Text(
                            "Educational Games",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Text(
                            "Learn while playing!",
                            textAlign = TextAlign.Left,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Image(
                            painter = painterResource(R.drawable.child),
                            contentDescription = "Kaan Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                    shape = RoundedCornerShape(10.dp)
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
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp), // Köşeleri yuvarlatır
                            onClick = {
                                // Buton aksiyonu
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFF3B5EF8))
                        ) {
                            Text("Game Side", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                    }
                }

                Image(
                    painter = painterResource(R.drawable.robot),
                    contentDescription = "Kaan Image",
                    contentScale = ContentScale.Crop,

                )

                Box(
                    modifier = Modifier
                        .padding(17.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(gradientBrush2)
                        .wrapContentSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), // Tüm genişliği kaplasın ki TextAlign.Left etkili olsun
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start // Sola hizalama sağlandı
                    ) {
                        Text(
                            "Create Stories with AI",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            fontSize = 24.sp,
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Text(
                            "Let your imagination fly!",
                            textAlign = TextAlign.Left,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Image(
                            painter = painterResource(R.drawable.parent),
                            contentDescription = "Kaan Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                    shape = RoundedCornerShape(10.dp)
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
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            // .padding(horizontal = 16.dp),
                            onClick = {
                                // Buton aksiyonu
                            }, colors = ButtonDefaults.buttonColors(Color(0xFFFA6868))
                        ) {
                            Text("Play Time", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Center)
                {
                    Image(
                        painter = painterResource(R.drawable.img),
                        contentDescription = "Kaan Image",
                        contentScale = ContentScale.Crop,

                        )
                    Spacer(Modifier.width(3.dp))
                    Text("Safe for Kids")
                }


            }
        }

    }
}

@Preview
@Composable
private fun öylesine() {
    MainPage()
}