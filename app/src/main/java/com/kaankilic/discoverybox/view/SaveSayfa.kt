package com.kaankilic.discoverybox.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.SaveSayfaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveSayfa(navController: NavController, saveSayfaViewModel: SaveSayfaViewModel,hikayeViewModel: HikayeViewModel) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val stories by saveSayfaViewModel.stories.observeAsState(emptyList())
    val sandtitle= FontFamily(Font(R.font.sandtitle))



   /* val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color.Black,
            Color(0xFF2A3E52), // Dark Green
            Color(0xFF03F6079)
        )
    )*/
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFd5e0fe),
            Color(0xFFfbdceb), // Sarı

            // Açık Mavi
        )
    )



    LaunchedEffect(userId) {
        if (userId != null) {
            saveSayfaViewModel.getUserStories(userId)
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text =stringResource(R.string.YOURSTORY), fontSize = 40.sp, textAlign = TextAlign.Center,fontFamily = sandtitle)},
                colors = TopAppBarColors( Color(0xFFd5e0fe), Color(0xFF2A3E52), Color(0xFF2A3E52), Color(0xFF353BA4), Color(0xFF353BA4))
            )

        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush),



        ) {
            items(stories) { story ->
                StoryItem(
                    hikaye = story,
                    navController = navController,
                    saveSayfaViewModel = saveSayfaViewModel,
                    hikayeViewModel = hikayeViewModel
                )
            }

        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun StoryItem(hikaye: Hikaye, navController: NavController, saveSayfaViewModel: SaveSayfaViewModel,hikayeViewModel: HikayeViewModel) {
    var deletemessage= stringResource(R.string.HikayeSİlindi)
    val context = LocalContext.current
    val sandtitle= FontFamily(Font(R.font.sandtitle))
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFd5e0fe),
            Color(0xFFfbdceb), // Sarı

            // Açık Mavi
        )
    )

    Card(
            modifier = Modifier
                .fillMaxWidth()

                .padding(8.dp)
                .clickable {
                    navController.navigate("metin/${hikaye.id}")
                },//colors = CardDefaults.cardColors(Color(0xFFcfcfcf)) //colors = CardDefaults.cardColors(Color(0xFFcfcfcf))

        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFcfcfcf))
                .padding(16.dp) // İçerik için boşluk
        ) {
            Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f)) // Solda boşluk

                    Text(
                        text = hikaye.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        fontFamily = sandtitle,
                        modifier = Modifier.weight(2f) // Ortada yer kaplasın
                    )

                    IconButton(
                        onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null) {
                                saveSayfaViewModel.deleteStory(userId, hikaye.id)
                                Toast.makeText(
                                    context,
                                    deletemessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        modifier = Modifier.weight(1f) // Sağda kalsın
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hikayeyi Sil",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }


                AsyncImage(
                    model = hikaye.imageUrl,
                    contentDescription = "Hikaye Resmi",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp)),

                    contentScale = ContentScale.Crop // Orijinal oranı koruyarak sığdırır

                )
                //Spacer(modifier = Modifier.width(16.dp))


            }

        }


        }


    }









