package com.kaankilic.discoverybox.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Diger(navController: NavController) {
    val diger = remember { mutableStateOf("") }
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFE541),
            Color(0xFFBB80FF) // Dark purple (indigo hue)
        )
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Diğer", color = Color.Black, fontSize = 35.sp) },
                modifier = Modifier.background(gradientBrush),
                colors = TopAppBarColors(
                    Color(0xFFFFE541),
                    Color(0xFFFFE541),
                    Color(0xFFFFE541),
                    Color(0xFFFFE541),
                    Color(0xFFFFE541)
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(state = rememberScrollState()
                ).background(gradientBrush),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            Text(text = "İstediğiniz bir konu", fontSize = 20.sp)

            TextField(value = diger.value, onValueChange ={diger.value=it} ,
                label = {
                    Text(text = "Örn: yazılım tavsiyesi, motivasyon konuşması, mesleki gelişim tavsiyeleri")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp))

            Button(
                colors = ButtonDefaults.buttonColors(Color(0xFFBB80FF)),

                onClick = { /* Hikayeyi oluştur */ },
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Text(text = "Oluştur")
            }


        }


    }
}

