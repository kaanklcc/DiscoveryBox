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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Start
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.GirisSayfaViewModel
import kotlinx.coroutines.withContext

@Composable
fun GirisSayfa(navController: NavController,GirisSayfaViewModel: GirisSayfaViewModel) {

    var email by remember { mutableStateOf(("")) }
    var password by remember { mutableStateOf(("")) }
    val context = LocalContext.current
    val loginResult by GirisSayfaViewModel.loginResult.observeAsState()


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
            fontSize = 50.sp, // Yazı boyutu
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp),
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
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email", textAlign = Start)},
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
                GirisSayfaViewModel.signInWithEmail(email,password)

                loginResult?.let { (success, message) ->
                    if (success) {
                        Toast.makeText(context, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                        navController.navigate("anasayfa")
                    } else {
                        Toast.makeText(context, "Giriş başarısız: $message", Toast.LENGTH_SHORT).show()
                    }
                }


            },

                colors = ButtonDefaults.buttonColors(Color.LightGray)

                ) {
                Text(text = "Giris Yap", color = Color.Black)
            }




            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text(text = "Don't you have account?", color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Sign Up",
                    modifier = Modifier.clickable {
                        navController.navigate("kayitSayfa")

                    }, color = Color.White)
            }


        }
    }
}



