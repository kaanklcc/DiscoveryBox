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
fun GuncelHayat(navController: NavController) {
    val tarih = remember { mutableStateOf("") }
    val konum = remember { mutableStateOf("") }
    val kategori = remember { mutableStateOf("") }
    val spesifikOlay = remember { mutableStateOf("") }
    val kendinSec = remember { mutableStateOf("") }
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0E1A59),
            Color(0xFFF2BE5C) // Dark purple (indigo hue)
            //#F2BE5C
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Güncel Hayat",color = Color.Black, fontSize = 35.sp) },
                modifier = Modifier.background(gradientBrush),
                colors = TopAppBarColors(Color(0xFF0E1A59),Color(0xFF0E1A59),Color(0xFF0E1A59),Color(0xFF0E1A59),Color(0xFF0E1A59))
                )
        }
    ) {paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(state = rememberScrollState()
            ).background(gradientBrush),
            horizontalAlignment = Alignment.CenterHorizontally,) {
            Text(text = "Tarih",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)
            TextField(
                value = tarih.value,
                onValueChange = { tarih.value = it },
                label = { Text(text = "Örn: Eylül 2024, 21-30 Aralık 2022")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )
            Text(text = "Konum",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)

            TextField(
                value = konum.value,
                onValueChange = { konum.value = it },
                label = { Text(text = "Örn: Türkiye, Dünya, New York")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )
            Text(text = "Kategori",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)

            TextField(
                value = kategori.value,
                onValueChange = { kategori.value = it },
                label = {Text(text = "Örn: Spor,Siyaset,Ekonomi")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)

            )
            Text(text = "Spesifik Olay",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black,

                )

            TextField(
                value = spesifikOlay.value,
                onValueChange = { spesifikOlay.value = it },
                label = {Text(text = "Örn: Futbol, Deprem")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )
            Text(text = "Kendin Bir Olay Gir",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)

            TextField(
                value = kendinSec.value,
                onValueChange = { kendinSec.value = it },
                label = {Text(text = "Örn: Deprem bölgesindeki son durum nedir? ")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )
            Button(
                colors = ButtonDefaults.buttonColors(Color.DarkGray),

                onClick = { /* Hikayeyi oluştur */ },
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Text(text = "Oluştur")
            }


        }

    }




}

