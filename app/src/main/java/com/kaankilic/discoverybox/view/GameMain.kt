package com.kaankilic.discoverybox.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameMain(navController: NavController, anasayfaViewModel: AnasayfaViewModel) {
    val konular by anasayfaViewModel.konular.observeAsState(emptyList()) // Boş bir liste ile başlatıyoruz
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Game Screen", fontSize = 35.sp) })
        }
    ) { paddingValues ->
        if (konular.isEmpty()){
            Text(text = "Gösterilecek hilkaye yok")
        } else{
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                columns = GridCells.Fixed(2)
            ) {
                // Her bir Story item'ını grid içinde göstermek için items fonksiyonunu kullanıyoruz
                items(konular) { story ->
                    GameItem(story = story,navController)
                }
            }

        }

    }
}







@Composable
fun GameItem(story: Story, navController: NavController) {
    // Hikaye öğesinin görünümü
    Column(
        modifier = Modifier
            .padding(6.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                when (story.category) {
                    "wordGame" -> navController.navigate("wordGame")
                    "matchingGame" -> navController.navigate("matchingGame")
                    "colorGame" -> navController.navigate("colorGame")
                    "wordGame" -> navController.navigate("wordGame")
                    "wordGame" -> navController.navigate("wordGame")
                    else -> {} // Diğer kategoriler için bir şey yapılmaz
                }
            },
            //.padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = story.imageRes), contentDescription = null,
            modifier = Modifier.size(250.dp).clip(RoundedCornerShape(25.dp)), contentScale = ContentScale.Crop)
       /* Button(onClick = { navController.navigate("saveSayfa") }) {
            Text(text = "SaveGit")

        }*/
        //Spacer(modifier = Modifier.height(7.dp))
        //Text(text = story.title, fontSize = 25.sp)
    }
}

