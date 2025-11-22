package com.kaankilic.discoverybox.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.R
import kotlinx.coroutines.delay


@Composable
fun GradientBackground(colors:List<Color>,modifier: Modifier = Modifier) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = colors, // Açık mavi → Açık pembe
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

@Composable
fun LoginSplashScreen(navController: NavController) {
    val sandtitle= FontFamily(Font(R.font.sandtitle))
    val auth = FirebaseAuth.getInstance()
    
    LaunchedEffect(Unit) {
        delay(2000)

        val currentUser = auth.currentUser
        if (currentUser != null) {

            navController.navigate("anasayfa") {
                popUpTo("loginSplash") { inclusive = true }
            }
        } else {

            navController.navigate("splashScreen1") {
                popUpTo("loginSplash") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        GradientBackground(listOf(Color(0xFF003366),
            Color(0xFF004080),
            Color(0xFF0055AA)))

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                    Image(
                        painter = painterResource(id = R.drawable.applogo),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(300.dp)
                    )
            Spacer(modifier = Modifier.height(30.dp))
            Text("Fablette", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp,fontFamily = sandtitle)

        }

    }

}

@Composable
fun SplashScreen1(navController: NavController) {
    val andikabody = FontFamily(Font(R.font.andikabody))
    val sandtitle = FontFamily(Font(R.font.sandtitle))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars,
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF003366),
                            Color(0xFF004080),
                            Color(0xFF0055AA)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))


                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(3.dp, Color.White, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFAA3D), Color(0xFFFFBF67))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.book),
                        contentDescription = "Book Icon",
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Başlık
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.pencil),
                        contentDescription = "pencil",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.welcome_to),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = andikabody
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.pencil),
                        contentDescription = "pencil",
                    )
                }

                Text(
                    text = stringResource(R.string.story_magic_title),
                    color = Color(0xFFFFD700),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = sandtitle
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Kale görseli kutusu (Glow efektiyle)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                alpha = 0.8f
                                scaleX = 1.05f
                                scaleY = 1.05f
                            }
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B6B).copy(alpha = 0.9f),
                                        Color.Transparent
                                    ),
                                    radius = 600f
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .blur(40.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFE0DAD2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.castle),
                            contentDescription = "Castle Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Açıklama kutusu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0055AA))
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.pencil),
                                contentDescription = "pencil",
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Create amazing stories with AI magic!",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = sandtitle,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Every tale is unique and specially made just for you. Let your imagination soar!",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sandtitle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(16.dp))

                // Buton
                Button(
                    onClick = { navController.navigate("splashScreen2") },
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(140.dp)
                        .height(50.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFCD34D), Color(0xFFFBBF24))
                            ),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .border(3.dp, Color.White, RoundedCornerShape(25.dp))
                ) {
                    Text(
                        text = "Next",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.pencil),
                        contentDescription = "Pencil",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                PageIndicator(2, 0)
                Spacer(modifier = Modifier.height(10.dp))
                ShortBar()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}



@Composable
fun SplashScreen2(navController: NavController) {
    val andikabody = FontFamily(Font(R.font.andikabody))
    val sandtitle = FontFamily(Font(R.font.sandtitle))

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.systemBars // ✅ sistem paddings otomatik
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF003366),
                            Color(0xFF004080),
                            Color(0xFF0055AA)
                        )
                    )
                )
        ) {


            // 📜 İçerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))

                // 🖋️ Kalem ikonu
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(3.dp, Color.White, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFAA3D), Color(0xFFFFBF67))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.pencil),
                        contentDescription = "Magic Pen",
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Başlık kısmı
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.pencil),
                        contentDescription = "pencil",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Safe & Trusted",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = andikabody
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(R.drawable.pencil),
                        contentDescription = "pencil",
                    )
                }

                Text(
                    text = "For Parents",
                    color = Color(0xFFFFD700),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = sandtitle
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 🏰 Görsel alanı
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                alpha = 0.8f
                                scaleX = 1.05f
                                scaleY = 1.05f
                            }
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF6B6B).copy(alpha = 0.9f),
                                        Color.Transparent
                                    ),
                                    radius = 600f
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .blur(40.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.s2),
                            contentDescription = "Parent Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Bilgilendirme kutusu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0055AA))
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.pencil),
                                contentDescription = "pencil",
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Trusted AI for Your Children",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = sandtitle,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Safe, age-appropriate stories created with AI. Monitor your child's creativity and imagination in a secure environment.",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sandtitle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(16.dp))


                // 🔘 Buton
                Button(
                    onClick = { navController.navigate("girisSayfa") },
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(140.dp)
                        .height(50.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFCD34D), Color(0xFFFBBF24))
                            ),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .border(3.dp, Color.White, RoundedCornerShape(25.dp))
                ) {
                    Text(
                        text = "Next",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.pencil),
                        contentDescription = "Pencil",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                PageIndicator(2, 1)
                Spacer(modifier = Modifier.height(10.dp))
                ShortBar()
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}


@Preview
@Composable
private fun preva() {
    val navController = rememberNavController()
    LoginSplashScreen(navController)

}




@Composable
fun PageIndicator(pageCount: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(15.dp)
                    .background(
                        color = if (index == currentPage) Color(0xFFFC6A67) else Color.Gray,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun ShortBar() {
    Box(
        modifier = Modifier
            .width(140.dp)  // Çubuğun genişliği
            .height(6.dp)  // Çubuğun yüksekliği
            .background(Color.DarkGray, shape = RoundedCornerShape(3.dp)) // Yuvarlatılmış köşeler
    )
}
