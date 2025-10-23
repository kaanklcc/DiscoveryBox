package com.kaankilic.discoverybox.view


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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    LaunchedEffect(Unit) {
        delay(3000) // 3 saniye bekle
        navController.navigate("anasayfa")
    }

    Box(modifier = Modifier.fillMaxSize()){
        GradientBackground(listOf(Color(0xFFd5e0fe), Color(0xFFfbdceb)))

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
            Text("DISCOVERY BOX", color = Color(0xFF353BA4), fontWeight = FontWeight.ExtraBold, fontSize = 44.sp,fontFamily = sandtitle)

        }

    }

}

@Composable
fun SplashScreen1(navController: NavController) {
    val andikabody= FontFamily(Font(R.font.andikabody))
    val sandtitle= FontFamily(Font(R.font.sandtitle))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6B4FA0))
    ){
        Image(
            painter = painterResource(id = R.drawable.starimage),
            contentDescription = "Star",
            modifier = Modifier
                .size(30.dp)
                .offset(x = 40.dp, y = 40.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.starimage),
            contentDescription = "Star",
            modifier = Modifier
                .size(25.dp)
                .offset(x = 200.dp, y = 80.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.starimage),
            contentDescription = "Star",
            modifier = Modifier
                .size(20.dp)
                .offset(x = 60.dp, y = 550.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.starimage),
            contentDescription = "Star",
            modifier = Modifier
                .size(25.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-40).dp, y = (-120).dp)
        )

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
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF9800), Color(0xFFFFB84D))
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
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.starimage),
                    contentDescription = "Star",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Welcome to",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = andikabody
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.starimage),
                    contentDescription = "Star",
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = "Story Magic!",
                color = Color(0xFFFFD700),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sandtitle
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0xFFFF6B6B).copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE0DAD2)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.castle),
                    contentDescription = "House Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF8B6FB8))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.starimage),
                            contentDescription = "Star",
                            modifier = Modifier.size(16.dp)
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.starimage),
                        contentDescription = "Star",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
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
                            colors = listOf(Color(0xFFFF6B6B), Color(0xFFFF8E8E))
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(25.dp)
                    )
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
            PageIndicator(3, 0)
            Spacer(modifier = Modifier.height(10.dp))
            ShortBar()
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen2(navController: NavController) {
    val andikabody= FontFamily(Font(R.font.andikabody))
    val sandtitle= FontFamily(Font(R.font.sandtitle))
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

                Spacer(modifier = Modifier.height(70.dp))


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
    val andikabody= FontFamily(Font(R.font.andikabody))
    val sandtitle= FontFamily(Font(R.font.sandtitle))
    val delbold= FontFamily(Font(R.font.delbold))


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
                    stringResource(R.string.get),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    color = Color.DarkGray,fontFamily = sandtitle
                )
                Text(
                    stringResource(R.string.started),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    color = Color.DarkGray,fontFamily = sandtitle
                )
                Spacer(Modifier.height(10.dp))
                Text(stringResource(R.string.touch), fontWeight = FontWeight.Normal, fontSize = 22.sp,fontFamily = andikabody)
                Text(stringResource(R.string.Advanturemacera), fontWeight = FontWeight.Normal, fontSize = 22.sp,fontFamily = andikabody)
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
                        navController.navigate("anasayfa") {
                            popUpTo("splashScreen1") { inclusive = true }
                        }
                    }
                ) {
                    Text(stringResource(R.string.StartNow), fontWeight = FontWeight.Normal, fontSize = 22.sp,fontFamily = sandtitle)
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