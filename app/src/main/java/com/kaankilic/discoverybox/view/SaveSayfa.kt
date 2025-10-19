package com.kaankilic.discoverybox.view

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.util.InterstitialAdHelper
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.SaveSayfaViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveSayfa(navController: NavController, saveSayfaViewModel: SaveSayfaViewModel, hikayeViewModel: HikayeViewModel) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val stories by saveSayfaViewModel.stories.observeAsState(emptyList())
    val sandtitle = FontFamily(Font(R.font.sandtitle))

    LaunchedEffect(userId) {
        if (userId != null) {
            saveSayfaViewModel.getUserStories(userId)
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF7C3AED),
                                Color(0xFF9333EA),
                                Color(0xFFA855F7)
                            )
                        )
                    )
                    .padding(top = 40.dp, bottom = 20.dp, start = 16.dp, end = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "My Story Collection",
                            fontSize = 20.sp,
                            fontFamily = sandtitle,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${stories.size} magical stories saved",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(start = 56.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF3E8FF),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            items(stories, key = { it.id }) { story ->
                SwipeToDeleteStoryItem(
                    hikaye = story,
                    navController = navController,
                    saveSayfaViewModel = saveSayfaViewModel,
                    hikayeViewModel = hikayeViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteStoryItem(
    hikaye: Hikaye,
    navController: NavController,
    saveSayfaViewModel: SaveSayfaViewModel,
    hikayeViewModel: HikayeViewModel
) {
    val context = LocalContext.current
    val sandtitle = FontFamily(Font(R.font.sandtitle))
    val deletemessage = stringResource(R.string.HikayeSİlindi)
    val coroutineScope = rememberCoroutineScope()
    
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val sizePx = with(LocalDensity.current) { 80.dp.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1)
    var showDeleteDialog by remember { mutableStateOf(false) }

    val userId = Firebase.auth.currentUser?.uid ?: ""
    var isPro by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val premium = document.getBoolean("premium") ?: false
                val hasTrial = document.getBoolean("usedFreeTrial") == false
                isPro = premium || hasTrial
            }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
    ) {
        // Delete background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFEF4444)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(32.dp)
            )
        }

        // Story card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .clickable {
                    val activity = context as? Activity
                    if (isPro) {
                        navController.navigate("metin/${hikaye.id}")
                    } else {
                        if (activity != null) {
                            InterstitialAdHelper.showAd(activity) {
                                navController.navigate("metin/${hikaye.id}")
                            }
                        } else {
                            navController.navigate("metin/${hikaye.id}")
                        }
                    }
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = hikaye.imageUrl,
                    contentDescription = "Story Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEC4899)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = hikaye.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sandtitle,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Created 3 days ago",
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue == 1) {
            showDeleteDialog = true
            swipeableState.snapTo(0)
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Story", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this story?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            if (userId != null) {
                                saveSayfaViewModel.deleteStory(userId, hikaye.id)
                                Toast.makeText(context, deletemessage, Toast.LENGTH_SHORT).show()
                            }
                            showDeleteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
