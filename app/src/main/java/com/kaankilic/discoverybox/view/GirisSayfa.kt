package com.kaankilic.discoverybox.view

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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

import androidx.compose.ui.text.style.TextAlign.Companion.Start
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import com.kaankilic.discoverybox.util.GoogleSignInHelper
import com.kaankilic.discoverybox.viewmodel.GirisSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.KayitSayfaViewModel

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
    val sandtitle= FontFamily(Font(R.font.sandtitle))
    val andikabody= FontFamily(Font(R.font.andikabody))

    val activity = context as Activity

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        GoogleSignInHelper.handleGoogleSignInResult(
            data = result.data,
            viewModel = GirisSayfaViewModel,
            onSuccess = {
                navController.navigate("anasayfa")
            },
            onFailure = {
                Toast.makeText(context, "Giriş başarısız", Toast.LENGTH_SHORT).show()
            }
        )
    }



    Scaffold(
    ) { paddingValues ->


        Box(
            modifier = Modifier
                .fillMaxSize().padding(paddingValues)
        ) {
            GradientBackgroundd(listOf( Color(0xFFfbdceb),Color(0xFFd5e0fe)))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
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
                            .size(250.dp) // Dairenin boyutu
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White), // Beyaz arka plan
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.logoyapay),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop, // Görseli kırpmadan ortalar
                            modifier = Modifier
                                .size(250.dp) // Görselin boyutu (çerçeve içinde)

                        )

                    }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                   stringResource(R.string.DiscoveryBox),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = Color.DarkGray, fontFamily = sandtitle
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.WelcomeToMagicWorld), fontWeight = FontWeight.Normal, fontSize = 24.sp, fontFamily = andikabody,
                    modifier = Modifier.padding(start = 3.dp, end = 3.dp))

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    GoogleSignInHelper.signInWithGoogle(
                                        context = context,
                                        launcher = googleSignInLauncher
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFFFCFCFC)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .padding(horizontal = 20.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.google),
                                        contentDescription = "Google Sign-In",
                                        modifier = Modifier
                                            .size(20.dp), tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Continue with Google",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                }

                            }
                            Spacer(Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    GoogleSignInHelper.signInWithGoogle(
                                        context = context,
                                        launcher = googleSignInLauncher,
                                        //activity = activity
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .padding(horizontal = 20.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                colors = ButtonDefaults.buttonColors(Color(0xFFFFFFFF))
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.google), // Google logosunu eklemelisin
                                        contentDescription = "Google Sign-In",
                                        modifier = Modifier
                                            .size(20.dp), tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Sign Up with Google",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                }
                            }

                        }
                    }

                }

            }
        }


@Composable
fun LanguageSwitcher(context: Context) {
    var locale by remember { mutableStateOf(Locale.getDefault()) }
    var showMenu by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopEnd
    ) {
        Button(onClick = { showMenu = true },
            colors = ButtonDefaults.buttonColors(Color.White),) {
            Text(text = "\uD83C\uDDF9\uD83C\uDDF7 / \uD83C\uDDFA\uD83C\uDDF8" , color = Color.Black)
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

    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("language_code", locale.language).apply()

    // Activity'yi yeniden başlat (Compose'da etkisini görmek için)
   if (context is Activity) {
        context.recreate()
    }
}







