package com.kaankilic.discoverybox.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.kaankilic.discoverybox.entitiy.NumberItem
import com.kaankilic.discoverybox.viewmodel.NumberGameViewModel
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import kotlin.math.roundToInt


@Composable
fun DragAndDropGameScreen(viewModel: NumberGameViewModel) {
    val numberItems by viewModel.numberItems
    val matchedPairs = remember { mutableStateListOf<Pair<NumberItem, String>>() }
    val currentNumber = remember { mutableStateOf<String?>(null) }

    if (numberItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val images = numberItems.filter { !it.isMatched }
        val numbers = images.map { it.numara }.shuffled().take(1)

        if (matchedPairs.size == images.size) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tebrikler! Tüm eşleşmeleri doğru yaptınız.",
                    color = Color.Green,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Lütfen şu rakama tıklayın: ${numbers.first()}",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        images.forEach { image ->
                            ClickableImage(
                                item = image,
                                currentNumber = currentNumber,
                                matchedPairs = matchedPairs,
                                onMatchSuccess = { matchedItem ->
                                    matchedPairs.add(matchedItem to matchedItem.numara)
                                    viewModel.markItemAsMatched(matchedItem)
                                    viewModel.loadNextQuestion()
                                    currentNumber.value = viewModel.currentQuestion.value?.numara
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(32.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        numbers.forEach { number ->
                            images.forEach { image ->
                                DroppableTarget(
                                    number = number,
                                    currentNumber = currentNumber,
                                    matchedPairs = matchedPairs,  // matchedPairs'ı burada geçiyoruz
                                    item = image,  // item parametresini geçiyoruz
                                    onDropSuccess = { matchedItem ->
                                        matchedPairs.add(matchedItem to number)
                                        viewModel.markItemAsMatched(matchedItem)
                                        viewModel.loadNextQuestion()
                                        currentNumber.value = viewModel.currentQuestion.value?.numara
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Eşleşmeler: ${matchedPairs.size}/${images.size}",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun ClickableImage(
    item: NumberItem,
    currentNumber: MutableState<String?>,
    matchedPairs: MutableList<Pair<NumberItem, String>>,
    onMatchSuccess: (NumberItem) -> Unit
) {
    if (item.isMatched) return  // Eşleşmiş öğeleri göstermemek

    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable {
                // Seçilen rakamla karşılaştır
                currentNumber.value?.let { number ->
                    if (item.numara == number) {
                        // Eğer eşleşme doğruysa, eşleşmeyi kaydet
                        onMatchSuccess(item)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberImagePainter(data = item.gorsel_Url),
            contentDescription = item.isim_tr,
            modifier = Modifier.size(80.dp)
        )
    }
}


@Composable
fun DroppableTarget(
    number: String,
    currentNumber: MutableState<String?>,
    matchedPairs: MutableList<Pair<NumberItem, String>>,  // matchedPairs'ı parametre olarak ekliyoruz
    item: NumberItem,
    onDropSuccess: (NumberItem) -> Unit
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(
                if (currentNumber.value == number) Color.Green else Color.Gray
            )
            .clickable {
                // currentNumber'a göre eşleşme kontrolü yapılacak
                currentNumber.value?.let { num ->
                    if (num == number) {
                        // Eğer eşleşme doğruysa işlemi tamamla
                        onDropSuccess(item)  // Eşleşmeyi kaydet ve işlemi bitir
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = number, fontSize = 24.sp, color = Color.White)
    }
}












