package com.kaankilic.discoverybox.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import com.kaankilic.discoverybox.viewmodel.SaveSayfaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveSayfa(navController: NavController, saveSayfaViewModel: SaveSayfaViewModel,hikayeViewModel: HikayeViewModel) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val stories by saveSayfaViewModel.stories.observeAsState(emptyList())

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color.Black,
            Color(0xFF2A3E52), // Dark Green
            Color(0xFF03F6079)
            // Color.White
        )
    )



    LaunchedEffect(userId) {
        if (userId != null) {
            saveSayfaViewModel.getUserStories(userId)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "KAYDEDİLENLER", fontSize = 35.sp)}
            ,)

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

@Composable
fun StoryItem(hikaye: Hikaye, navController: NavController, saveSayfaViewModel: SaveSayfaViewModel,hikayeViewModel: HikayeViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {

                navController.navigate("metin/${hikaye.id}")
            }, colors = CardDefaults.cardColors(Color(0xFFcfcfcf))

    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = hikaye.imageUrl,
                contentDescription = "Hikaye Resmi",
                modifier = Modifier
                    .size(120.dp)
                   // .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = hikaye.title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text(text = hikaye.content.take(100) + "...")
            }
        }
    }
}


