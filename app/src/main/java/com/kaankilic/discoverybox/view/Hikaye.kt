@file:OptIn(ExperimentalLayoutApi::class)

package com.kaankilic.discoverybox.view

import android.annotation.SuppressLint
import android.widget.Toast

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.material.icons.filled.Add
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.repo.DiscoveryBoxRepository
import com.kaankilic.discoverybox.util.isInternetAvailable

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Hikaye(navController: NavController,hikayeViewModel: HikayeViewModel,metinViewModel: MetinViewModel,anasayfaViewModel: AnasayfaViewModel) {
    var konu by remember { mutableStateOf(TextFieldValue("")) }
    var mekan by remember { mutableStateOf(TextFieldValue("")) }
    var anaKarakter by remember { mutableStateOf(TextFieldValue("")) }
    var anaKarakterOzellik by remember { mutableStateOf(TextFieldValue("")) }
    val (yanKarakterler, setTextFields) = remember { mutableStateOf(listOf("")) }
    val (selectedChip, setSelectedChip) = remember { mutableStateOf("") }
    val chipOptions = listOf(stringResource(R.string.Adventure),stringResource(R.string.Love),stringResource(R.string.Friendship),stringResource(R.string.Family),stringResource(R.string.Action))
    val(secilenChip , ayarSecilenChip) = remember { mutableStateOf("") }
    var chipAyar = listOf(stringResource(R.string.Short),stringResource(R.string.Medium),stringResource(R.string.Long))
    var generatedStory by remember { mutableStateOf("") }
    var imageGenerate by remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState= remember { SnackbarHostState() }
    var focusState by remember { mutableStateOf(false) }
    val delbold= FontFamily(Font(R.font.delbold))
    var showDialogPay by remember { mutableStateOf(false) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    //var dbRepo= DiscoveryBoxRepository()
    val dbRepo = hikayeViewModel.dbRepo




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
                text = stringResource(R.string.Subject),
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
                label = { Text(text = stringResource(R.string.SubjectExp), fontSize = 12.sp, fontFamily = delbold) }
            )
            Text(
                text = stringResource(R.string.Location),
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
                label = { Text(text =stringResource(R.string.LocationExp), fontSize = 12.sp, fontFamily = delbold) }
            )
            Text(
                text = stringResource(R.string.MainCharacter),
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
                label = { Text(text = stringResource(R.string.MainCharacterExp), fontSize = 12.sp, fontFamily = delbold) }
            )
            Text(
                text = stringResource(R.string.MinorCharacter),
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
                                .weight(1f) // fillMaxWidth yerine weight kullan
                                .padding(start = 10.dp, top = 10.dp, bottom = 25.dp)
                                .onFocusChanged { focusState = it.isFocused },
                            label = { Text(text = stringResource(R.string.MinorCharacterExp), fontSize = 12.sp, fontFamily = delbold) }
                        )

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ekle",
                            modifier = Modifier
                                .padding(end = 7.dp)
                                .size(34.dp)
                                .clickable { setTextFields(yanKarakterler + "") },
                            tint = Color.White
                        )
                    }
                }
            }

            Text(
                text =stringResource(R.string.MainCharacterCharacteristic),
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
                label = { Text(text =stringResource(R.string.MainCharacterCharacteristicExp), fontSize = 12.sp, fontFamily = delbold) }
            )
            Text(
                text = stringResource(R.string.StoryLength) ,
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
                text = stringResource(R.string.Theme),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                fontSize = 18.sp,
                color = Color.White, fontFamily = delbold
            )
            Spacer(modifier = Modifier.height(2.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 5.dp,
                crossAxisSpacing = 5.dp
            ) {
                chipOptions.forEach { option ->
                    FilterChip(
                        selected = selectedChip == option,
                        onClick = { setSelectedChip(option) },
                        label = {
                            Text(
                                text = option,
                                color = Color.White,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.Black,
                            selectedContainerColor = Color(0xFF21324A),
                            labelColor = Color.White,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }


            Button(
                colors = ButtonDefaults.buttonColors(Color.DarkGray),
                modifier = Modifier.padding(bottom = 22.dp),
                enabled = konu.text.isNotEmpty() && mekan.text.isNotEmpty(),

                onClick = {
                    if (!isInternetAvailable(context = context)){
                        Toast.makeText(context,"Internet connection error!",Toast.LENGTH_LONG).show()
                    }else{
                        anasayfaViewModel.checkUserAccess { hasTrial, isPremium, usedFreeTrial ->
                            val isPro = isPremium || hasTrial


                            MainScope().launch {

                                val yanKarakterlerText = yanKarakterler.joinToString(", ") { it }
                                val temaText = if (selectedChip.isNotEmpty()) "Tema: $selectedChip" else ""
                                val uzunlukText = if (secilenChip.isNotEmpty()) "Uzunluk: $secilenChip" else ""

                                generatedStory = "Write me a story. " +
                                        "Topic: ${konu.text}," +
                                        " Location: ${mekan.text}," +
                                        " Main character: ${anaKarakter.text}," +
                                        " Main character trait: ${anaKarakterOzellik.text}," +
                                        " Supporting characters: ${yanKarakterlerText}, " +
                                        "Theme: ${temaText}," +
                                        " Length: ${uzunlukText}" +
                                        ".but at the beginning of the story, there shouldn't be an AI-related sentence — for example, no sentences like 'here is a story for you.' It should start directly with the story"

                                imageGenerate =  """
A photorealistic digital painting in the style of a high-end children’s storybook illustration. 
Depict a vivid and emotionally rich scene of "$konu" taking place in $mekan.
The theme is $temaText.

Characters are captured mid-action with lifelike gestures and natural expressions — full of joy, curiosity, and warmth. 
Human anatomy is realistic and age-appropriate, with fine skin details, light reflections in eyes, and dynamic postures.

Use cinematic, realistic lighting with global illumination, soft shadows, subtle reflections, and ambient occlusion.
Textures such as skin, hair, fabric, and nature are richly detailed and physically accurate.
Materials reflect light based on their real properties (e.g., soft cloth, shiny eyes, moist lips, natural wood).

Camera style: shallow depth of field, slightly blurred background for realism and focus, gentle bokeh highlights.
Atmospheric perspective enhances spatial depth, with misty or sunlit air particles adding realism.
Warm natural tones dominate the palette, with pastel undertones used only subtly for charm — not cartoonish.

Avoid watercolor or flat 2D styles; emphasize rich brushstroke simulation with physically-based rendering.
Environment is immersive, filled with small details like dust particles in light, texture on walls or grass, and dynamic natural elements.

Layout: wide horizontal (storybook spread), ultra-high detail, rendered at 1024x1024 resolution or higher.
""".trimIndent()
                                if (isPro){
                                    hikayeViewModel.generateStory(generatedStory)
                                    metinViewModel.queryTextToImage(imageGenerate, isPro = true, context = context)

                                    navController.navigate("metin/${konu.text}")
                                } else {
                                    hikayeViewModel.generateStory(generatedStory)
                                    metinViewModel.queryTextToImage(imageGenerate, isPro = false, context = context)
                                    dbRepo.decrementChatGptUseIfNotPro(userId, false) { success ->
                                        if (!success) {
                                            Toast.makeText(context, "Hak güncelleme başarısız", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                navController.navigate("metin/${konu.text}") // HAK eksiltme başarılıysa yönlendir
                            }
                        }
                    }

                },
            ) {
                Text(text = stringResource(R.string.CreatetheStory))
            }
            if (showDialogPay) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Hakkınız Tükenmiş") },
                    text = { Text("Ücretli üyelik hakkınız bulunmamaktadır. Ücretsiz hikaye üretebilirsiniz. Veya " +
                            "daha kaliteli hikaye,görsel ve ses için ücretli plana geçiş yapabilirsiniz.") },
                    confirmButton = {
                        Button(onClick = {
                            showDialogPay = false
                            // Üyelik alma ekranına yönlendirme veya işlemi
                            //navController.navigate("uyelikSayfasi")
                        }) {
                            Text("Üyelik Al")
                        }
                    },
                    dismissButton = {
                        androidx.compose.material.OutlinedButton(onClick = {
                            showDialogPay = false
                            // Ücretsiz sürümle devam et
                        }) {
                            Text("Ücretsiz Sürüm ile Hikaye Üret")
                        }
                    }
                )
            }

        }
    }
}



