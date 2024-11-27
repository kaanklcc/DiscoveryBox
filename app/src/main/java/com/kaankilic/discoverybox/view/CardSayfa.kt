package com.kaankilic.discoverybox.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.kaankilic.discoverybox.entitiy.Word
import com.kaankilic.discoverybox.viewmodel.CardSayfaViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchGameScreen(cardSayfaViewModel: CardSayfaViewModel, isEnglish: Boolean) {
    val words = cardSayfaViewModel.words
    val shuffledWords = cardSayfaViewModel.shuffledWords
    val shuffledImages = cardSayfaViewModel.shuffledImages
    val selectedWord = remember { mutableStateOf("") }
    val selectedImage = remember { mutableStateOf("") }
    val isMatch = remember { mutableStateOf(false) }
    val currentGroupIndex = remember { mutableStateOf(0) }
    val matchedItem = remember { mutableStateOf<String?>(null) }
    val showCelebration = remember { mutableStateOf(false) }

    // Animasyon ayarları
    val infiniteTransition = rememberInfiniteTransition()
    val animatedAlpha = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val gradientBackground = Brush.linearGradient(
        colors = listOf(

            Color(0xFF9575CD),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
            Color(0xFFFFFFFF),
        ),
        start = Offset(0f, 0f),
        end = Offset.Infinite
    )

    // Grup boyutu
    val groupSize = 4
    val currentWords = shuffledWords.drop(currentGroupIndex.value * groupSize).take(groupSize)
    val currentImages = shuffledImages.drop(currentGroupIndex.value * groupSize).take(groupSize)

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text(text = "Matching Game", fontSize = 35.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
                modifier = Modifier.background(gradientBackground),
                colors = TopAppBarColors(
                    Color(0xFF9575CD),
                    Color(0xFF9575CD),
                    Color(0xFF9575CD),
                    Color.White,
                    Color.White
                )
            )
        },

        modifier = Modifier.background(gradientBackground)
    ) {paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)
            .background(gradientBackground)
            ) {
            if (showCelebration.value) {
                CelebrationAnimation {
                    showCelebration.value = false
                    currentGroupIndex.value++ // Sonraki gruba geç
                }
            } else {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Kelimeleri Göster
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 2.dp, top = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(currentWords) { word ->
                            if (word.isVisible) {
                                Text(
                                    text = if (isEnglish) word.nameEn else word.nameTr,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .background(
                                            color = if (selectedWord.value == word.nameEn || selectedWord.value == word.nameTr)
                                                Color(0xFF9575CD) else Color.Transparent
                                        )
                                        .clickable {
                                            selectedWord.value =
                                                if (isEnglish) word.nameEn else word.nameTr
                                            checkMatch(
                                                selectedWord.value,
                                                selectedImage.value,
                                                words,
                                                isMatch,
                                                matchedItem,
                                                cardSayfaViewModel,
                                                currentGroupIndex,
                                                showCelebration
                                            )
                                        },
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedWord.value == word.nameEn || selectedWord.value == word.nameTr)
                                        Color.White else Color.Black
                                )
                            }
                        }
                    }

                    // Görselleri Göster
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 25.dp, top = 60.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        items(currentImages) { word ->
                            if (word.isVisible) {
                                Image(
                                    painter = rememberAsyncImagePainter(word.imageUrl),
                                    contentDescription = word.nameTr,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .size(100.dp)
                                        .clickable {
                                            selectedImage.value = word.imageUrl
                                            checkMatch(
                                                selectedWord.value,
                                                selectedImage.value,
                                                words,
                                                isMatch,
                                                matchedItem,
                                                cardSayfaViewModel,
                                                currentGroupIndex,
                                                showCelebration
                                            )
                                        }
                                        .let {
                                            if (matchedItem.value == word.nameTr || matchedItem.value == word.nameEn) {
                                                it.alpha(animatedAlpha.value)
                                            } else it
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }




}


// Kutlama Animasyonu için Composable
@Composable
fun CelebrationAnimation(onAnimationEnd: () -> Unit) {
    // Basit bir kutlama animasyonu, burada animasyonun tamamlanması sonrası `onAnimationEnd` çağrılır
    Text(
        text = "Tebrikler! Grup Tamamlandı!",
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Green,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )

    // Animasyon süresi boyunca gösterim
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onAnimationEnd()
    }
}

// Doğru eşleşme durumunda kutlama animasyonunu tetikleyen `checkMatch` fonksiyonu
fun checkMatch(
    selectedWord: String, selectedImage: String, words: List<Word>,
    isMatch: MutableState<Boolean>, matchedItem: MutableState<String?>,
    cardSayfaViewModel: CardSayfaViewModel, currentGroupIndex: MutableState<Int>,
    showCelebration: MutableState<Boolean>
) {
    val matchedWord = words.find { it.imageUrl == selectedImage && (it.nameTr == selectedWord || it.nameEn == selectedWord) }
    isMatch.value = matchedWord != null

    if (isMatch.value) {
        matchedItem.value = selectedWord
        matchedWord?.let { cardSayfaViewModel.removeWord(it) }
        if (cardSayfaViewModel.shuffledWords.none { it.isVisible && it in words.drop(currentGroupIndex.value * 4).take(4) }) {
            // Grup tamamlandı, kutlama animasyonu göster
            showCelebration.value = true
        }
    }
}
