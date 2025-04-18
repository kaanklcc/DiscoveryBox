package com.kaankilic.discoverybox.view

import android.annotation.SuppressLint
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.R
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

@SuppressLint("SuspiciousIndentation")
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
    val chipOptions = listOf(/*"Adventure", "Love", "Friendship", "Family", "Action"*/stringResource(R.string.Adventure),stringResource(R.string.Love),stringResource(R.string.Friendship),stringResource(R.string.Family),stringResource(R.string.Action))
    val(secilenChip , ayarSecilenChip) = remember { mutableStateOf("") }
    var chipAyar = listOf(/*"Short","Medium","Long"*/stringResource(R.string.Short),stringResource(R.string.Medium),stringResource(R.string.Long))
    var generatedStory by remember { mutableStateOf("") }
    var imageGenerate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState= remember { SnackbarHostState() }
    var focusState by remember { mutableStateOf(false) }
    val makeStory = stringResource(R.string.MakeStory,)
    val makeImage = stringResource(R.string.makeImage)
    val delbold= FontFamily(Font(R.font.delbold))


    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF21324A),
            Color(0xFF6BB7C0), // Üstteki renk
            // Alttaki renk
        ),
        startY = 0f,
        endY = 3500f// eğeri ekran yüksekliğine göre ayarlayabilirsiniz.
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = /*"Story"*/stringResource(R.string.Story), fontSize = 42.sp, textAlign = TextAlign.Center, fontFamily = delbold) },
                modifier = Modifier.background(gradientBrush),
                colors = TopAppBarColors(Color(0xFF21324A),Color.DarkGray,Color.Gray,Color.White,Color.White)// Apply gradient to top bar
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                text = /*"Subject"*/stringResource(R.string.Subject),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp, start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
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
                    unfocusedTextColor = Color.White,
                    focusedLabelColor =  Color(0xFF21324A),
                    unfocusedLabelColor = Color.White



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
                    .onFocusChanged { focusState = it.isFocused },
                label = { Text(text = /*"Exp: Space Travel, importance of friendship"*/stringResource(R.string.SubjectExp), fontSize = 15.sp, fontFamily = delbold) }
            )
            Text(
                text = /*"Location"*/stringResource(R.string.Location),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
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
                    unfocusedTextColor = Color.White,
                    focusedLabelColor =  Color(0xFF21324A),
                    unfocusedLabelColor = Color.White,



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
                    .onFocusChanged { focusState = it.isFocused },
                label = { Text(text = /*"Exp: Desert, School, Forest"*/stringResource(R.string.LocationExp), fontSize = 15.sp, fontFamily = delbold) }
            )
            Text(
                text = /*"Main Character"*/stringResource(R.string.MainCharacter),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
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
                    unfocusedTextColor = Color.White,
                    focusedLabelColor =  Color(0xFF21324A),
                    unfocusedLabelColor = Color.White,



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
                    .onFocusChanged { focusState = it.isFocused },
                label = { Text(text = /*"Exp: Cınderella, James , Emily"*/stringResource(R.string.MainCharacterExp), fontSize = 15.sp, fontFamily = delbold) }
            )
            Text(
                text = /*"Minor Character"*/stringResource(R.string.MinorCharacter),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
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
                                unfocusedTextColor = Color.White,
                                focusedLabelColor =  Color(0xFF21324A),
                                unfocusedLabelColor = Color.White



                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
                                .onFocusChanged { focusState = it.isFocused },
                            label = { Text(text = /*"Exp: Shrek, Liam, Kylie"*/stringResource(R.string.MinorCharacterExp), fontSize = 15.sp, fontFamily = delbold) }
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
                text = /*"Main Character Characteristic"*/stringResource(R.string.MainCharacterCharacteristic),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
            )
            TextField(
                value = anaKarakterOzellik,
                onValueChange = { anaKarakterOzellik = it },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    containerColor = Color.Black,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor =  Color(0xFF21324A),
                    unfocusedLabelColor = Color.White



                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 25.dp)
                    .onFocusChanged { focusState = it.isFocused },
                label = { Text(text = /*"Exp: Naughty, Lovable, Honest"*/stringResource(R.string.MainCharacterCharacteristicExp), fontSize = 15.sp, fontFamily = delbold) }
            )
            Text(
                text = /*"Story Length"*/stringResource(R.string.StoryLength) ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
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
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Black, // Varsayılan arka plan rengi
                            selectedContainerColor = Color(0xFF21324A), // Seçili olduğunda arka plan rengi
                            labelColor = Color.Black, // Varsayılan yazı rengi
                            selectedLabelColor = Color.Black // Seçili olduğunda yazı rengi
                        ),
                        modifier = Modifier
                            .clickable {}
                            .padding(bottom = 10.dp)
                    )
                }
            }
            Text(
                text = /*"Theme"*/stringResource(R.string.Theme),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
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
                        label = { Text(text = option, color = Color.White, fontSize = 10.sp, textAlign = TextAlign.Center) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Black, // Varsayılan arka plan rengi
                            selectedContainerColor = Color(0xFF21324A),
                            labelColor = Color.Black, // Varsayılan yazı rengi
                            selectedLabelColor = Color.Black // Seçili olduğunda yazı rengi
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {}
                            .padding(bottom = 30.dp)
                    )
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(Color.DarkGray),
                modifier = Modifier.padding(bottom = 22.dp),
                enabled = konu.text.isNotEmpty() && mekan.text.isNotEmpty(),

                onClick = {
                    MainScope().launch {

                        val yanKarakterlerText = yanKarakterler.joinToString(", ") { it }
                        val temaText = if (selectedChip.isNotEmpty()) "Tema: $selectedChip" else ""
                        val uzunlukText = if (secilenChip.isNotEmpty()) "Uzunluk: $secilenChip" else ""

                       /* generatedStory = "bana bir hikaye yaz. Konusu: ${konu.text}, Mekanı: ${mekan.text}, " +
                                "Ana karakteri: ${anaKarakter.text}, Ana karakter özelliği: ${anaKarakterOzellik.text}, " +
                                "Yan karakterler: $yanKarakterlerText,teması $temaText, uzunlugu $uzunlukText."*/
                        generatedStory = "Write me a story. " +
                                "Topic: ${konu.text}," +
                                " Location: ${mekan.text}," +
                                " Main character: ${anaKarakter.text}," +
                                " Main character trait: ${anaKarakterOzellik.text}," +
                                " Supporting characters: ${yanKarakterlerText}, " +
                                "Theme: ${temaText}," +
                                " Length: ${uzunlukText}."
                        //generatedStory= makeStory

                        imageGenerate= "Draw me a picture.. let the subject be ${konu.text} and the place be ${mekan.text}. let the theme be $temaText. "
                        //imageGenerate = makeImage


                            hikayeViewModel.generateStory(generatedStory)
                            metinViewModel.queryTextToImage(imageGenerate,context)
                            navController.navigate("metin/${konu.text}")

                    }

                },

            ) {
                Text(text = /*"Create the Story"*/stringResource(R.string.CreatetheStory))
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

