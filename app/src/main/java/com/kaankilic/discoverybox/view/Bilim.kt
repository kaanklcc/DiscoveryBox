package com.kaankilic.discoverybox.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
fun Bilim(navController: NavController) {

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF475949),
            Color(0xFFA68160) // Dark purple (indigo hue)
            //#F2BE5C
        )
    )
    val kategori = remember { mutableStateOf("") }
    val donem = remember { mutableStateOf("") }
    val altKonu = remember { mutableStateOf("") }
    val zaman = remember { mutableStateOf("") }
    val (selectedChip, setSelectedChip) = remember { mutableStateOf("") }
    val chipOptions = listOf("Temel", "Orta", "İleri")

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Bilim",color = Color.Black, fontSize = 35.sp) },
                modifier = Modifier.background(gradientBrush),
                colors = TopAppBarColors(
                    Color(0xFF475949),
                    Color(0xFF475949),
                    Color(0xFF475949),
                    Color(0xFF475949),
                    Color(0xFF475949)
                )
            )
        }
    ){ paddingValues ->
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(state = rememberScrollState())
            .background(gradientBrush),

            horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Text(text = "Kategori",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)
            TextField(
                value = kategori.value,
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color(0xFFDBDBDB),
                    containerColor = Color(0xFFA69A94),
                    focusedIndicatorColor = Color(0xFFDBDBDB),
                    unfocusedIndicatorColor = Color(0xFFDBDBDB),
                    focusedTextColor =Color(0xFFDBDBDB),
                    unfocusedTextColor = Color(0xFFDBDBDB)



                ),
                onValueChange = { kategori.value = it },
                label = { Text(text = "Örn: Tarih,matematik,fizik,sosyal bilimler")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )
            Text(text = "Dönem",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)

            TextField(
                value = donem.value,
                onValueChange = { donem.value = it },
                label = { Text(text = "Örn: Osmanlı Dönemi, fizik1")},
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color(0xFFDBDBDB),
                    containerColor = Color(0xFFA69A94),
                    focusedIndicatorColor = Color(0xFFDBDBDB),
                    unfocusedIndicatorColor = Color(0xFFDBDBDB),
                    focusedTextColor =Color(0xFFDBDBDB),
                    unfocusedTextColor = Color(0xFFDBDBDB)



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )
            Text(text = "Alt Konu",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)

            TextField(
                value = altKonu.value,
                onValueChange = { altKonu.value = it },
                label = {Text(text = "Örn: 2.Dünya savaşı, Atom Teorisi, Bitki Biyolojisi")},
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color(0xFFDBDBDB),
                    containerColor = Color(0xFFA69A94),
                    focusedIndicatorColor = Color(0xFFDBDBDB),
                    unfocusedIndicatorColor = Color(0xFFDBDBDB),
                    focusedTextColor =Color(0xFFDBDBDB),
                    unfocusedTextColor = Color(0xFFDBDBDB)



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)

            )
            Text(text = "Zaman Aralığı",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black,

                )

            TextField(
                value = zaman.value,
                onValueChange = { zaman.value = it },
                label = {Text(text = "Örn: 19.yüzyıl, 20.yüzyılın ilk yarısı")},
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color(0xFFDBDBDB),
                    containerColor = Color(0xFFA69A94),
                    focusedIndicatorColor = Color(0xFFDBDBDB),
                    unfocusedIndicatorColor = Color(0xFFDBDBDB),
                    focusedTextColor =Color(0xFFDBDBDB),
                    unfocusedTextColor = Color(0xFFDBDBDB)



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )
            Text(text = "Düzey",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp, bottom = 10.dp),
                fontSize = 18.sp,
                color = Color.Black)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                chipOptions.forEach { option ->
                    FilterChip(
                        selected = selectedChip == option,
                        onClick = { setSelectedChip(option) },
                        label = {
                            Text(
                                text = option,
                                color = if (selectedChip == option) Color.White else Color.Black
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (selectedChip == option) Color.Black // Mor renk (seçili)
                            else  Color(0xFFDBDBDB) // Beyaz (seçili değil)
                        ),
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                }
            }
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

