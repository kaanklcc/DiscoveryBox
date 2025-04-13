package com.kaankilic.discoverybox.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Start
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.GirisSayfaViewModel
import kotlinx.coroutines.withContext
import java.util.Locale


@Composable
fun GradientBackgroundd(colors:List<Color>,modifier: Modifier = Modifier) {
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
fun GirisSayfa(navController: NavController,GirisSayfaViewModel: GirisSayfaViewModel) {

    var email by remember { mutableStateOf(("")) }
    var password by remember { mutableStateOf(("")) }
    val context = LocalContext.current
    val loginResult by GirisSayfaViewModel.loginResult.observeAsState()
    val logsuc = stringResource(R.string.LoginSuccesful)
    val logfail = stringResource(R.string.LoginFailed)
    val delbold= FontFamily(Font(R.font.delbold))


    Scaffold(

    ) { paddingValues ->


        Box(
            modifier = Modifier
                .fillMaxSize().padding(paddingValues)
        ) {
            GradientBackgroundd(listOf( Color(0xFFFDEAF2),Color(0xFFD6F8FA)))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 15.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp), // Sağdan 16 dp boşluk bırak
                    contentAlignment = Alignment.TopEnd
                ) {
                    LanguageSwitcher(context = LocalContext.current)
                }

                    Box(
                        modifier = Modifier
                            .size(150.dp) // Dairenin boyutu
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White), // Beyaz arka plan
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.logoyapay),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                            modifier = Modifier
                                .size(300.dp) // Görselin boyutu (çerçeve içinde)

                        )

                    }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    /*"Discovery Box"*/stringResource(R.string.DiscoveryBox),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    color = Color.DarkGray, fontFamily = delbold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(/*"Welcome To Magic World"*/stringResource(R.string.WelcomeToMagicWorld), fontWeight = FontWeight.Normal, fontSize = 24.sp, fontFamily = delbold)
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .size(300.dp,250.dp) // Dairenin boyutu
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White), // Beyaz arka plan
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(text = "Email", textAlign = Start) },
                        )
                        TextField(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(text = "Password") },
                            visualTransformation = PasswordVisualTransformation() // Şifreyi gizlemek için
                        )
                        Button(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .padding(start = 20.dp, end = 20.dp),
                            onClick = {

                                if (email.isEmpty() || password.isEmpty() ){
                                    Toast.makeText(context, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()

                                }else{
                                    GirisSayfaViewModel.signInWithEmail(email, password)

                                    loginResult?.let { (success, message) ->
                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                /*"Login Succesful"*/logsuc,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigate("anasayfa")
                                        } else {
                                            Toast.makeText(
                                                context,
                                                /*"Login Failed:*/ "logfail $message",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                }





                            },

                            colors = ButtonDefaults.buttonColors(Color(0xFFFA69AF))//9148fc

                        ) {
                            Text(text = /*"Sign in"*/stringResource(R.string.SignIn), color = Color.White, fontWeight = FontWeight.ExtraBold
                            , fontSize = 20.sp)
                        }


                    }

                }
                Spacer(Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = /*"New To DiscoveryBox?"*/stringResource(R.string.NewToDiscoveryBox), color = Color.DarkGray)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = /*"Sign Up"*/stringResource(R.string.SignUp),
                        modifier = Modifier.clickable {
                            navController.navigate("kayitSayfa")

                        }, color = Color(0xFFE0BACD), fontWeight = FontWeight.ExtraBold
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGirisSayfa() {
    val fakeNavController = rememberNavController() // Fake NavController oluştur
    val fakeViewModel = GirisSayfaViewModel() // Eğer içinde canlı veri yoksa direkt oluşturulabilir

    GirisSayfa(navController = fakeNavController, GirisSayfaViewModel = fakeViewModel)
}

@Composable
fun LanguageSwitcher(context: Context) {
    var locale by remember { mutableStateOf(Locale.getDefault()) }
    var showMenu by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopEnd
    ) {
        Button(onClick = { showMenu = true },
            colors = ButtonDefaults.buttonColors(Color.DarkGray),) {
            Text(text = "🌍")
        }

        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(text = { Text("Türkçe 🇹🇷") }, onClick = {
                locale = Locale("tr")
                updateLocale(context, locale)
                showMenu = false
            })
            DropdownMenuItem(text = { Text("English 🇺🇸") }, onClick = {
                locale = Locale("en")
                updateLocale(context, locale)
                showMenu = false
            })
        }
    }
}


fun updateLocale(context: Context, locale: Locale) {
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    val resources = context.resources
    resources.updateConfiguration(config, resources.displayMetrics)

    // Activity'yi yeniden başlat (Compose'da etkisini görmek için)
   if (context is Activity) {
        context.recreate()
    }
}







