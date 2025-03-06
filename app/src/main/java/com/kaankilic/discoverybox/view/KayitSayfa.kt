package com.kaankilic.discoverybox.view

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Start
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.KayitSayfaViewModel

@Composable
fun KayitSayfa(navController: NavController,kayitSayfaViewModel: KayitSayfaViewModel) {

    var email by remember { mutableStateOf(("")) }
    var password by remember { mutableStateOf(("")) }
    var ad by remember { mutableStateOf(("")) }
    var soyad by remember { mutableStateOf(("")) }
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val signUpResult by kayitSayfaViewModel.signUpResult.observeAsState()
    val context = LocalContext.current
    val regSuc = stringResource(R.string.Registrationsuccessful)
    val regFail = stringResource(R.string.Registrationfailed)

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }


    Scaffold(

    ) { paddingValues ->


        Box(
            modifier = Modifier
                .fillMaxSize().padding(paddingValues)
        ) {

            GradientBackgroundd(listOf(Color(0xFFFDEAF2), Color(0xFFD6F8FA)))


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

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
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    /*"WELCOME TO "*/stringResource(R.string.WELCOMETO),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    color = Color.DarkGray
                )
                Text(
                    /*"Discovery Box"*/stringResource(R.string.DiscoveryBox),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .size(300.dp, 350.dp) // Dairenin boyutu
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
                            value = ad,
                            onValueChange = { ad = it },
                            label = { Text(text = /*"Name"*/stringResource(R.string.Name), textAlign = Start) },
                        )
                        TextField(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            value = soyad,
                            onValueChange = { soyad = it },
                            label = { Text(text = /*"Surname"*/stringResource(R.string.Surname), textAlign = Start) },
                        )


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
                                kayitSayfaViewModel.signUpWithEmail(email, password)

                                signUpResult?.let { (success, message) ->
                                    if (success) {
                                        kayitSayfaViewModel.saveUserData(
                                            getCurrentUserId()!!,
                                            ad,
                                            soyad,
                                            email
                                        )
                                        navController.navigate("anasayfa")
                                        Toast.makeText(
                                            context,
                                            /*"Registration successful"*/regSuc,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Registration failed: $message",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFFE0BACD))//9148fc
                        ) {
                            Text(text = /*"Sign Up"*/stringResource(R.string.SignUp), color = Color.White, fontWeight = FontWeight.ExtraBold
                                , fontSize = 20.sp)
                        }

                    }
                }


            }


        }
    }
}


