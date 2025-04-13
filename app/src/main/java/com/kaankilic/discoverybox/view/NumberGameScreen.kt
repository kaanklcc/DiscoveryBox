@file:OptIn(ExperimentalMaterial3Api::class)

package com.kaankilic.discoverybox.view

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.NumberGameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NumberGameScreen(navController: NavController, numberGameViewModel: NumberGameViewModel) {

    val items by remember { derivedStateOf { numberGameViewModel.items } }
    val draggingState = remember { mutableStateOf<String?>(null) }
    val dropPosition = remember { mutableStateOf<Offset?>(null) }  // Bırakılma pozisyonu
    val delbold= FontFamily(Font(R.font.delbold))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text ="Drag And Drop", fontSize = 24.sp, textAlign = TextAlign.Center,fontFamily = delbold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF2D81), // Dinamik renk burada
                    titleContentColor = Color.White, // Başlık rengi
                    actionIconContentColor = Color.Transparent // İkon rengi (varsa)
                )
            )

        }
    ){paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            GradientBackgroundd(listOf(Color(0xFFEF2D81), Color(0xFF24D0EA)))
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {


                Box(
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                ){

                    Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {


                        Image(
                            painter = painterResource(id = R.drawable.pars),
                            contentDescription = "pars",
                            modifier = Modifier.size(150.dp).offset(y=(-25).dp, x = (-20).dp)
                                .align(Alignment.CenterVertically)

                        )

                        Box(
                            modifier = Modifier.offset(y=(-65).dp, x = (-50).dp)


                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(16.dp)
                                .align(Alignment.CenterVertically)
                        )
                        {
                            Text(
                                text = "Doğru numaraları renklerle eşleştir",
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 12.sp,fontFamily = delbold
                                )
                            )
                        }
                    }

                }

                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceAround
                    , horizontalAlignment = Alignment.CenterHorizontally) {
                    items.forEach { item ->
                        DroppableImage(
                            imageUrl = item.gorsel_Url,
                            expectedNumber = item.numara,
                            draggingState = draggingState,
                            numberGameViewModel = numberGameViewModel,
                            dropPosition = dropPosition,  // dropPosition'ı geçiyoruz
                            onDrop = {
                                numberGameViewModel.removeItem(item.numara) // Doğru bırakıldığında sil
                            }
                        )
                    }
                }



                Spacer(modifier = Modifier.height(20.dp))
               Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    items.forEach { item ->
                        DraggableNumber(
                            number = item.numara,
                            draggingState = draggingState,
                            numberGameViewModel = numberGameViewModel,
                            dropPosition = dropPosition  // dropPosition'ı geçiyoruz
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun DraggableNumber(
    number: String,
    draggingState: MutableState<String?>,
    numberGameViewModel: NumberGameViewModel,
    dropPosition: MutableState<Offset?>
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var globalPosition by remember { mutableStateOf(Offset.Zero) }

    if (!numberGameViewModel.isNumberVisible(number)) return

    Box(
        modifier = Modifier
            .size(70.dp)
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .background(Color.LightGray.copy(alpha = 0.55f), shape = RoundedCornerShape(30.dp))
            .onGloballyPositioned { coordinates ->
                globalPosition = coordinates.positionInRoot() // **En güncel konumu al**
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        draggingState.value = number
                        Log.d("DragDrop", "🟢 Sürükleme başladı! draggingState: ${draggingState.value}")
                    },
                    onDrag = { change, dragAmount ->
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        // **Yüksekliği (y) yanlış hesaplamamak için offsetY'yi kaldırdık!**
                        val finalDropPosition = Offset(globalPosition.x + offsetX, globalPosition.y)
                        dropPosition.value = finalDropPosition

                        offsetX = 0f
                        offsetY = 0f

                        Log.d("DragDrop", "🔴 Sürükleme bitti! Yeni Drop Pozisyonu: $finalDropPosition")
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = number, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun DroppableImage(
    imageUrl: String,
    expectedNumber: String,
    draggingState: MutableState<String?>,
    numberGameViewModel: NumberGameViewModel,
    dropPosition: MutableState<Offset?>,
    onDrop: () -> Unit
) {
    val painter = rememberAsyncImagePainter(imageUrl)
    if (!numberGameViewModel.isNumberVisible(expectedNumber)) return

    var boxPosition by remember { mutableStateOf(Rect.Zero) }
    val currentDropPosition = rememberUpdatedState(dropPosition.value)  // En güncel değeri al

    // UI değiştiğinde pozisyonları sıfırla ve tekrar hesapla
    LaunchedEffect(imageUrl, expectedNumber) {
        delay(50) // UI tamamen değişsin diye bekleyelim
        //boxPosition = Rect.Zero
        Log.d("DragDrop", "🔄 UI değişti, Box Pozisyonları sıfırlandı!")
    }


    LaunchedEffect(currentDropPosition.value, draggingState.value) {
        if (draggingState.value == expectedNumber) {
            currentDropPosition.value?.let { dropped ->
                val tolerance = 80f
                val adjustedBoxPosition = boxPosition.inflate(tolerance)

                if (adjustedBoxPosition.contains(dropped)) {
                    Log.d("DragDrop", "✅ Doğru eşleşme: $expectedNumber")
                    numberGameViewModel.setDroppedCorrectly(expectedNumber)
                    draggingState.value = null
                    dropPosition.value = null // Drop pozisyonunu sıfırla
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth() // Ekranın tamamını kaplar
            .padding(horizontal = 30.dp, vertical = 10.dp) // Sadece sağ-sol padding bırakır
            .height(90.dp) // Yüksekliği belirli tut
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(30.dp))
            .background(Color.LightGray.copy(alpha = 0.55f), shape = RoundedCornerShape(30.dp))
            .onGloballyPositioned { layoutCoordinates ->
                val newBoxPosition = layoutCoordinates.boundsInRoot()
                Log.d("DragDrop", "📌 onGloballyPositioned ÇALIŞTI! Yeni Box Pozisyon: $newBoxPosition")
                boxPosition = newBoxPosition
            },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painter, contentDescription = null, modifier = Modifier.size(80.dp))
    }

}



