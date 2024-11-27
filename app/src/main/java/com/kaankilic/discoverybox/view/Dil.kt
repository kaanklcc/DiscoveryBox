package com.kaankilic.discoverybox.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaankilic.discoverybox.viewmodel.DilViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dil(navController: NavController,dilViewModel: DilViewModel) {
    val hikayeyiOlustur by dilViewModel.hikayeOlustur.observeAsState("")
    var dil by remember { mutableStateOf(TextFieldValue(""))}
    val (selectedChip, setSelectedChip) = remember { mutableStateOf("") }
    val chipOptions = listOf("A1", "A2", "B1", "B2", "C1")
    var konu by remember { mutableStateOf(TextFieldValue("")) }
    var mekan by remember {mutableStateOf(TextFieldValue("")) }

    val gecmisZaman = remember { mutableStateOf(false) }
    val simdikiZaman = remember { mutableStateOf(false) }
    val gelecekZaman = remember { mutableStateOf(false) }
    val genisZaman = remember { mutableStateOf(false) }
    val(secilenChip , ayarSecilenChip) = remember { mutableStateOf("") }
    var chipAyar = listOf("Kısa","Orta","Uzun")
    var generatedStory by remember { mutableStateOf("") }


    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color.White,
            Color(0xFF2888B3) // Dark purple (indigo hue)
        )
    )


    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Dil Öğrenme", color = Color.Black )},
            modifier = Modifier.background(gradientBrush),
            colors = TopAppBarColors(Color.White,Color.DarkGray,Color.Gray,Color.White,Color.White)
        )
    },
        containerColor = Color.DarkGray, // Transparent to show gradient background
        modifier = Modifier.background(gradientBrush)) {paddingValues ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(state = rememberScrollState())
            .background(gradientBrush),

            horizontalAlignment = Alignment.CenterHorizontally,
            ) {
            Text(text = "Öğrenmek istediğiniz dil",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)
            TextField(
                value = dil,
                label = { Text(text = "Örn: İngilizce,Türkçe,İspanyolca")},
                onValueChange = { dil = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
            )

            Text(text = "Konu",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)
            TextField(value = konu, onValueChange ={konu=it} ,modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp), label = { Text(
                text = "Örn: Restorantta Siparişi Verme"
            )})

            Text(text = "Mekan",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)
            TextField(value = mekan, onValueChange = {mekan=it},modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp), label = { Text(
                text = "Örn: Restorant,Ofis,Stadyum"
            )})
            Text(
                text = "Metin Uzunluğu",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                chipAyar.forEach { option ->
                    FilterChip(
                        selected = secilenChip == option,
                        onClick = { ayarSecilenChip(option) },
                        label = { Text(text = option, color = Color.Black) },
                        modifier = Modifier
                            .clickable {}
                            .padding(bottom = 10.dp)
                    )
                }
            }

            Text(text = "Zaman",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.Black)

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {

                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Checkbox(checked = gecmisZaman.value, onCheckedChange ={gecmisZaman.value=it} )
                    Text(text = "Geçmiş ")

                }
                Spacer(modifier = Modifier.width(30.dp))
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Checkbox(checked = simdikiZaman.value, onCheckedChange ={simdikiZaman.value=it} )
                    Text(text = "Şimdiki ")

                }
                Spacer(modifier = Modifier.width(30.dp))
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Checkbox(checked = gelecekZaman.value, onCheckedChange ={gelecekZaman.value=it} )
                    Text(text = "Gelecek")

                }
                Spacer(modifier = Modifier.width(30.dp))
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

                    Checkbox(checked = genisZaman.value, onCheckedChange ={genisZaman.value=it} )
                    Text(text = "Geniş")

                }





            }
            Text(text = "Seviye",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
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
                        label = { Text(text = option, color = Color.Black) },
                        modifier = Modifier
                            .clickable {}
                            .padding(bottom = 30.dp)

                    )
                }
            }
            Button(
                colors = ButtonDefaults.buttonColors(Color.DarkGray),

                onClick = {
                    MainScope().launch {
                        val metinUzunlugu = if (selectedChip.isNotEmpty()) "Tema: $selectedChip" else ""
                        val seviye = if (secilenChip.isNotEmpty()) "Uzunluk: $secilenChip" else ""

                        generatedStory = "bana bir metin yaz . öğrenmek istediğim dil: ${dil.text}, konusu: ${konu.text}, " +
                                "mekanı: ${mekan.text},metin uzunlugu: ${metinUzunlugu}, " +
                                "seviyesi: $seviye olsun."
                        dilViewModel.generateStory(generatedStory)

                    }

                    //


                },
                modifier = Modifier.padding(bottom = 30.dp)
            ) {
                Text(text = "Oluştur")
            }
            Text(
                text = hikayeyiOlustur,
                modifier = Modifier.padding(16.dp) // Metin için kenar boşluğu
            )

        }

    }

}

