package com.kaankilic.discoverybox.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Hikaye(navController: NavController,hikayeViewModel: HikayeViewModel,metinViewModel: MetinViewModel) {
    var konu by remember { mutableStateOf(TextFieldValue("")) }
    var mekan by remember { mutableStateOf(TextFieldValue("")) }
    var dbRepo= DiscoveryBoxRepository()
    var anaKarakter by remember { mutableStateOf(TextFieldValue("")) }
    var anaKarakterOzellik by remember { mutableStateOf(TextFieldValue("")) }
    val (yanKarakterler, setTextFields) = remember { mutableStateOf(listOf("")) }
    val (selectedChip, setSelectedChip) = remember { mutableStateOf("") }
    val chipOptions = listOf("Macera", "Sevgi", "Dostluk", "Aile", "Aksiyon")
    val(secilenChip , ayarSecilenChip) = remember { mutableStateOf("") }
    var chipAyar = listOf("Kısa","Orta","Uzun")
    var generatedStory by remember { mutableStateOf("") }
    var imageGenerate by remember { mutableStateOf("") }
    val context = LocalContext.current





    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color.Black,
            Color(0xFF4B0082)

        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Hikaye", fontSize = 35.sp) },
                modifier = Modifier.background(gradientBrush),
                colors = TopAppBarColors(Color.Black,Color.DarkGray,Color.Gray,Color.White,Color.White)// Apply gradient to top bar
            )
        },

        containerColor = Color.DarkGray,
        modifier = Modifier.background(gradientBrush)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(state = rememberScrollState())
                .background(gradientBrush), // Apply gradient background to the content
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = "Konu",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.White
            )
            TextField(
                value = konu,
                onValueChange = { konu = it },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    containerColor = Color.Black,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp),
                label = { Text(text = "Örn: Uzay Yolculuğu, Arkadaşlığın önemi", fontSize = 15.sp, color = Color.White) }
            )
            Text(
                text = "Mekan",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White
            )
            TextField(
                value = mekan,
                onValueChange = { mekan = it },
                colors = TextFieldDefaults.textFieldColors(
                   cursorColor = Color.White,
                    containerColor = Color.Black,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White


                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp),
                label = { Text(text = "Örn: Orman, Sınıf, Uzay", fontSize = 15.sp, color = Color.White) }
            )
            Text(
                text = "Ana Karakter",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White
            )
            TextField(
                value = anaKarakter,
                onValueChange = { anaKarakter = it },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    containerColor = Color.Black,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White


                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp),
                label = { Text(text = "Örn: Kaan, Pamuk Prenses, Sindirella", fontSize = 15.sp,color = Color.White) }
            )
            Text(
                text = "Yan Karakterler",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White
            )
            Column {
                yanKarakterler.forEachIndexed { index, text ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = text,
                            onValueChange = { newText ->
                                val newTextFields = yanKarakterler.toMutableList()
                                newTextFields[index] = newText
                                setTextFields(newTextFields)
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                cursorColor = Color.White,
                                containerColor = Color.Black,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White


                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp),
                            label = { Text(text = "Örn: Ayşe, Gargamel, Sindirella",color = Color.White) }
                        )

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ekle",
                            modifier = Modifier
                                .clickable { setTextFields(yanKarakterler + "") }
                                .align(Alignment.CenterVertically)
                                .padding(end = 2.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Text(
                text = "Ana Karakter Özellik",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White
            )
            TextField(
                value = anaKarakterOzellik,
                onValueChange = { anaKarakterOzellik = it },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    containerColor = Color.Black,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White


                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp),
                label = { Text(text = "Örn: Cesur, Arkadaş Canlısı", fontSize = 15.sp, color = Color.White) }
            )
            Text(
                text = "Hikaye Uzunluğu",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                chipAyar.forEach { option ->
                    FilterChip(
                        selected = secilenChip == option,
                        onClick = { ayarSecilenChip(option) },
                        label = { Text(text = option, color = Color.White) },
                        modifier = Modifier
                            .clickable {}
                            .padding(bottom = 10.dp)
                    )
                }
            }
            Text(
                text = "Tema",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                chipOptions.forEach { option ->
                    FilterChip(
                        selected = selectedChip == option,
                        onClick = { setSelectedChip(option) },
                        label = { Text(text = option, color = Color.White) },
                        modifier = Modifier
                            .clickable {}
                            .padding(bottom = 30.dp)
                    )
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(Color.DarkGray),
                modifier = Modifier.padding(bottom = 22.dp),

                onClick = {
                    MainScope().launch {

                        val yanKarakterlerText = yanKarakterler.joinToString(", ") { it }
                        val temaText = if (selectedChip.isNotEmpty()) "Tema: $selectedChip" else ""
                        val uzunlukText = if (secilenChip.isNotEmpty()) "Uzunluk: $secilenChip" else ""

                        generatedStory = "bana bir hikaye yaz. Konusu: ${konu.text}, Mekanı: ${mekan.text}, " +
                                "Ana karakteri: ${anaKarakter.text}, Ana karakter özelliği: ${anaKarakterOzellik.text}, " +
                                "Yan karakterler: $yanKarakterlerText,teması $temaText, uzunlugu $uzunlukText."
                        imageGenerate= "Draw me a picture.. let the subject be ${konu.text} and the place be ${mekan.text}. let the theme be $temaText. "

                        hikayeViewModel.generateStory(generatedStory)
                        metinViewModel.queryTextToImage(imageGenerate,context)





                        navController.navigate("metin/${konu.text}")

                    }

                },

            ) {
                Text(text = "Hikayeyi Oluştur")
            }


        }
    }
}

// modelCall fonksiyonunu suspend olarak tanımlayın
/*suspend fun modelCall(prompt: String): String? {
    val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyDqVkaCBrlFoa9h_PQOj5VsHhrph5O2Cio"
    )

    // API çağrısını yapın ve yanıtı alın
    val response = generativeModel.generateContent(prompt)
    return response.text // Yanıt metnini döndürün
}*/

