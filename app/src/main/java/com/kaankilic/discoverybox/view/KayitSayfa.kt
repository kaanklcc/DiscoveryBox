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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {

        Text(
            text = " DISCOVERY BOX",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp, start = 5.dp, end = 5.dp),
            textAlign = TextAlign.Center, // Metni ortalamak
            fontSize = 40.sp, // Yazı boyutu
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = painterResource(id = R.drawable.geminikutu),
                contentDescription = "",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = ad,
                onValueChange = { ad = it },
                label = { Text(text = "Ad", textAlign = Start) },
            )
            Spacer(modifier = Modifier.height(30.dp))


            TextField(
                value = soyad,
                onValueChange = { soyad = it },
                label = { Text(text = "Soyad", textAlign = Start) },
            )
            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email", textAlign = Start) },
            )

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation() // Şifreyi gizlemek için
            )
            Spacer(modifier = Modifier.height(15.dp))
            Button(onClick = {
               kayitSayfaViewModel.signUpWithEmail(email,password)

                signUpResult?.let { (success , message ) ->
                    if (success){
                        kayitSayfaViewModel.saveUserData(getCurrentUserId()!!,ad,soyad,email)
                        navController.navigate("anasayfa")
                        Toast.makeText(context, "Kayıt başarılı", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "Kayıt başarısız: $message", Toast.LENGTH_SHORT).show()

                    }
                }
            },
                colors = ButtonDefaults.buttonColors(Color.LightGray)
            ) {
                Text(text = "Kayıt ol", color = Color.Black)
            }
        }
    }
}



