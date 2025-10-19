package com.kaankilic.discoverybox.view


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.offset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Anasayfa(navController: NavController, anasayfaViewModel: AnasayfaViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val sandtitle = FontFamily(Font(R.font.sandtitle))
    val andikabody = FontFamily(Font(R.font.andikabody))
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1B4B),
                modifier = Modifier.height(90.dp)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (selectedTab == 0) Color(0xFFC084FC) else Color(0xFFE9D5FF)
                        )
                    },
                    label = { Text("Home", fontSize = 10.sp, color = Color(0xFFE9D5FF)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFC084FC),
                        unselectedIconColor = Color(0xFFE9D5FF),
                        indicatorColor = Color(0xFF7C3AED).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate("hikaye")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = "Create",
                            tint = if (selectedTab == 1) Color(0xFFF472B6) else Color(0xFFFCE7F3)
                        )
                    },
                    label = { Text("Create", fontSize = 10.sp, color = Color(0xFFFCE7F3)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF472B6),
                        unselectedIconColor = Color(0xFFFCE7F3),
                        indicatorColor = Color(0xFFEC4899).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        navController.navigate("saveSayfa")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Saved",
                            tint = if (selectedTab == 2) Color(0xFFFBBF24) else Color(0xFFFEF3C7)
                        )
                    },
                    label = { Text("Saved", fontSize = 10.sp, color = Color(0xFFFEF3C7)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFBBF24),
                        unselectedIconColor = Color(0xFFFEF3C7),
                        indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Profile",
                            tint = if (selectedTab == 3) Color(0xFF22D3EE) else Color(0xFFCFFAFE)
                        )
                    },
                    label = { Text("Profile", fontSize = 10.sp, color = Color(0xFFCFFAFE)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF22D3EE),
                        unselectedIconColor = Color(0xFFCFFAFE),
                        indicatorColor = Color(0xFF06B6D4).copy(alpha = 0.2f)
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4C1D95),
                            Color(0xFF6B21A8),
                            Color(0xFF7E22CE)
                        )
                    )
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {

                    Text(
                        "TaleTeller",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontFamily = sandtitle
                    )
                    Text(
                        "Yapay zeka hikaye arkadaşın",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            // Mascot Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFFFBBF24))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.pars),
                            contentDescription = "Mascot",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFBBF24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 14.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp))
                        .background(Color.White.copy(alpha = 0.95f))
                        .padding(14.dp)
                ) {
                    Text(
                        "Hoşgeldin, küçük hikaye kurdu!",
                        color = Color(0xFF5B21B6),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = andikabody
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Creating Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFA855F7),
                                Color(0xFF8B5CF6),
                                Color(0xFF7C3AED)
                            )
                        )
                    )
                    .clickable { navController.navigate("hikaye") }
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.book),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text("✨", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Hikayeni Oluşturmaya Başla",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle
                    )
                    Text(
                        "Hikayenin nasıl olacağına karar ver",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontFamily = andikabody
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFBBF24))
                            .clickable { navController.navigate("hikaye") }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✨ Sihirli Hikaye Oluştur ✨",
                            color = Color(0xFF5B21B6),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = sandtitle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Featured Stories
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Öne çıkan hikayeler",
                        color = Color(0xFFE9D5FF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sandtitle
                    )
                    Text(
                        "View All Stories",
                        color = Color(0xFFE9D5FF),
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { navController.navigate("saveSayfa") }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                val stories = listOf(
                    Pair(R.drawable.parent, "The Magical Forest"),
                    Pair(R.drawable.robot, "Space Adventure"),
                    Pair(R.drawable.parent, "Ocean Tales"),
                    Pair(R.drawable.robot, "Dream World")
                )
                
                stories.chunked(2).forEach { rowStories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowStories.forEach { (image, title) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.75f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF1E293B))
                                    .clickable { navController.navigate("saveSayfa") }
                            ) {
                                Image(
                                    painter = painterResource(image),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                )
                                Text(
                                    title,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

    }
}










