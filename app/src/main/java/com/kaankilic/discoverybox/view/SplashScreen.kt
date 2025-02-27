package com.kaankilic.discoverybox.view

import android.graphics.pdf.PdfDocument.Page
import android.provider.CalendarContract.Colors
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kaankilic.discoverybox.R
import kotlinx.coroutines.delay


@Composable
fun GradientBackground(colors:List<Color>,modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    //colors = listOf(Color(0xFFDCEEFF), Color(0xFFFAD6FF)), // Açık mavi → Açık pembe
                    colors = colors, // Açık mavi → Açık pembe
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

@Composable
fun LoginSplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000) // 3 saniye bekle
        navController.navigate("anasayfa") { // "home" yerine gideceğiniz route'u yazın
            popUpTo("loginSplash") { inclusive = true } // Splash ekranını geri dönülemez yapar
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        GradientBackground(listOf(Color(0xFFC857DA), Color(0xFFF6A79E)))

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp) // Dairenin boyutu
                    .clip(CircleShape) // Yuvarlak şekil
                    .background(Color.White), // Beyaz arka plan
                contentAlignment = Alignment.Center
            ){

                Column( verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoyapay),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                        modifier = Modifier
                            .size(300.dp) // Görselin boyutu (çerçeve içinde)
                    )
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
            Text("DISCOVERY BOX", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp)

        }

    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen1(navController: NavController) {

    Scaffold(

    ){ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(250.dp) // Dairenin boyutu
                        .clip(CircleShape) // Yuvarlak şekil
                        .background(Color.White), // Beyaz arka plan
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.magicbook),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                        modifier = Modifier
                            .size(150.dp) // Görselin boyutu (çerçeve içinde)
                        //.clip(CircleShape) // Görselin de yuvarlak olması
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))

                Text("StoryPals", fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(10.dp))

                Text("Where Stories Come Alive!", fontWeight = FontWeight.Normal, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(60.dp))

                PageIndicator(3,0)
                Spacer(modifier = Modifier.height(10.dp))
                ShortBar()



            }
            IconButton(
                onClick = {
                    navController.navigate("splashScreen2")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(33.dp, 50.dp)
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Geç",
                    modifier = Modifier.size(50.dp),
                    tint = Color.DarkGray,
                )
            }

        }

    }





}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen2(navController: NavController) {
    Scaffold(

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GradientBackground(listOf(Color(0xFFE9FCE3), Color(0xFFE9FCE3)))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Learn Through Play",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Fun Educational Games", fontWeight = FontWeight.Medium, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp) // Dairenin boyutu
                            .clip(RoundedCornerShape(18.dp))
                            // Yuvarlak şekil
                            .background(Color.White), // Beyaz arka plan
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.colorimage),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                            modifier = Modifier
                                .size(90.dp) // Görselin boyutu (çerçeve içinde)

                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(140.dp) // Dairenin boyutu
                            .clip(RoundedCornerShape(18.dp)) // Yuvarlak şekil
                            .background(Color.White), // Beyaz arka plan
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.numbersimage),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                            modifier = Modifier
                                .size(90.dp) // Görselin boyutu (çerçeve içinde)

                        )
                    }

                }
                Spacer(modifier = Modifier.height(70.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .offset(y = (-35.dp))// Dairenin boyutu
                            .clip(RoundedCornerShape(18.dp)) // Yuvarlak şekil
                            .background(Color.White), // Beyaz arka plan
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.wordsimage),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                            modifier = Modifier
                                .size(90.dp) // Görselin boyutu (çerçeve içinde)

                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .offset(y = (-35.dp))// Dairenin boyutu
                            .clip(RoundedCornerShape(18.dp)) // Yuvarlak şekil
                            .background(Color.White), // Beyaz arka plan
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.matchingimage),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                            modifier = Modifier
                                .size(90.dp) // Görselin boyutu (çerçeve içinde)

                        )
                    }


                }
                Spacer(modifier = Modifier.height(10.dp))
                PageIndicator(3, 1)
                Spacer(modifier = Modifier.height(10.dp))
                ShortBar()
            }
            IconButton(
                onClick = {
                    navController.navigate("splashScreen3")
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(33.dp, 40.dp)
                    .offset(y = (-10.dp))
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Geç",
                    modifier = Modifier.size(50.dp),
                    tint = Color.DarkGray,
                )
            }

        }
    }





}

@Composable
fun SplashScreen3(navController: NavController) {

    Scaffold(

    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GradientBackground(listOf(Color(0xFFFCEEE4), Color(0xFFFFE3E1)))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.purplestar),
                    contentDescription = "Purple Star",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp) // Küçük yıldızın boyutu
                        .align(Alignment.Start) // Sağ üst köşeye hizala
                        .offset(x = (20).dp, y = (20).dp) // Hafif içeri kaydır (İsteğe bağlı)
                )

                Box(
                    modifier = Modifier
                        .size(220.dp) // Dairenin boyutu
                        .clip(CircleShape) // Yuvarlak şekil
                        .background(Color.White), // Beyaz arka plan
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.starimage),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                        modifier = Modifier
                            .size(150.dp) // Görselin boyutu (çerçeve içinde)
                        //.clip(CircleShape) // Görselin de yuvarlak olması
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.purplestar),
                    contentDescription = "Purple Star",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp) // Küçük yıldızın boyutu
                        .align(Alignment.End) // Sağ üst köşeye hizala
                        .offset(x = (-20).dp, y = (-30).dp) // Hafif içeri kaydır (İsteğe bağlı)
                )
                Spacer(Modifier.height(25.dp))
                Text(
                    "Get",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = Color.DarkGray
                )
                Text(
                    "Started!",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(10.dp))
                Text("Touch to Begin Your", fontWeight = FontWeight.Normal, fontSize = 16.sp)
                Text("Adventure", fontWeight = FontWeight.Normal, fontSize = 16.sp)
                Spacer(Modifier.height(40.dp))
                Button(
                    modifier = Modifier.size(230.dp, 55.dp),
                    colors = ButtonColors(
                        Color(0xFFFC6A67),
                        Color(0xFFFFFFFF),
                        Color(0xFFFFE3E1),
                        Color(0xFFFFE3E1),
                    ),
                    onClick = {
                        navController.navigate("girisSayfa")

                    }
                ) {
                    Text("Start Now ->", fontWeight = FontWeight.Normal, fontSize = 20.sp)
                }
                Spacer(Modifier.height(20.dp))
                PageIndicator(3, 2)
                Spacer(Modifier.height(10.dp))
                ShortBar()
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