package com.kaankilic.discoverybox.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameMain(navController: NavController, anasayfaViewModel: AnasayfaViewModel) {
    val konular by anasayfaViewModel.konular.observeAsState(emptyList()) // Boş bir liste ile başlatıyoruz
    val delbold= FontFamily(Font(R.font.delbold))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text =stringResource(R.string.GameScreen), fontSize = 34.sp, fontFamily = delbold, textAlign = TextAlign.Center) })
        }
    ) { paddingValues ->

        Column( modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally ) {

            Spacer(modifier = Modifier.height(12.dp)) // Üstten biraz boşluk
            Text(
                stringResource(R.string.play), fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = delbold)
            Spacer(modifier = Modifier.height(15.dp)) // Üstten biraz boşluk
            Text(stringResource(R.string.andmore), fontSize = 19.sp)
            Spacer(modifier = Modifier.height(25.dp)) // Üstten biraz boşluk
            if (konular.isEmpty()){
                Text(text = stringResource(R.string.NoStoryMessage), fontFamily = delbold)
            } else{
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // ← Ekrandaki kalan alanı kapsar
                        .padding(horizontal = 12.dp, vertical = 20.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    // Her bir Story item'ını grid içinde göstermek için items fonksiyonunu kullanıyoruz
                    items(konular) { story ->
                        GameItem(story = story,navController)
                    }
                }
            }
            ShortBar()

        }



    }
}

@Composable
fun GameItem(story: Story, navController: NavController) {
    val delbold= FontFamily(Font(R.font.delbold))
    // Hikaye öğesinin görünümü
    Column(
        modifier = Modifier
            .padding(vertical = 20.dp, horizontal = 14.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                when (story.category) {
                    "wordGame" -> navController.navigate("wordGame")
                    "matchingGame" -> navController.navigate("matchingGame")
                    "colorGame" -> navController.navigate("colorGame")
                    "numberGame" -> navController.navigate("numberGame")
                    "wordGame" -> navController.navigate("wordGame")
                    else -> {} // Diğer kategoriler için bir şey yapılmaz
                }
            },

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = story.imageRes), contentDescription = null,
            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(25.dp)), contentScale = ContentScale.Crop)
        Spacer(modifier = Modifier.height(6.dp)) // Üstten biraz boşluk
        Text(story.title, fontWeight = FontWeight.Bold,
            fontFamily = delbold, fontSize = 16.sp)

    }
}

