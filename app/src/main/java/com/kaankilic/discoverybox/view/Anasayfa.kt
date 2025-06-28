package com.kaankilic.discoverybox.view


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.navigation.NavController
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
    val konular by anasayfaViewModel.konular.observeAsState(emptyList()) // Boş bir liste ile başlatıyoruz
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDialogPay by remember { mutableStateOf(false) }
    val message = stringResource(R.string.logOutMessage)
    val actionLabel = stringResource(R.string.Yes)
    val exitedApp = stringResource(R.string.exitMessage)
    val wrongPassword = stringResource(R.string.WrongPassword)
    val delbold= FontFamily(Font(R.font.delbold))
    val context = LocalContext.current
    var showMembershipChoiceDialog by remember { mutableStateOf(false) }
    val andikabody= FontFamily(Font(R.font.andikabody))
    val sandtitle= FontFamily(Font(R.font.sandtitle))






    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFd5e0fe),
            Color(0xFFfbdceb), // Sarı

             // Açık Mavi
        )
    )



    val gradientBrush2 = Brush.linearGradient(
        colors = listOf(
            Color(0xFFfbdceb), // Sarı
            Color(0xFFd5e0fe),
             // Açık Mavi

        )
    )
    LaunchedEffect(Unit) {
        anasayfaViewModel.checkUserAccess { hasTrial, isPremium ,usedFreeTrial ->
            if (!hasTrial && !isPremium) {
                showDialogPay = true
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.DiscoveryBox),
                        fontSize = 34.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = sandtitle
                    )
                },
                colors = TopAppBarColors( Color(0xFFE3F2FD), Color(0xFF81D4FA), Color(0xFF81D4FA), Color(0xFF353BA4),  Color(0xFF353BA4)),

                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val sb = snackbarHostState
                                .showSnackbar(
                                    message = /*"Do you want to log out?"*/message,
                                    actionLabel = /*"Yes"*/actionLabel
                                )
                            if (sb == SnackbarResult.ActionPerformed) {
                                snackbarHostState.showSnackbar(message = /*"exited the application"*/exitedApp)
                                anasayfaViewModel.signOut(context){
                                    navController.navigate("girisSayfa"){
                                        popUpTo("anasayfa"){inclusive=true}
                                        launchSingleTop = true

                                    }
                                }


                            }
                        }


                    }, modifier = Modifier.padding(end = 10.dp)) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint =Color(0xFF353BA4) ,
                            modifier = Modifier.size(60.dp),

                        )

                    }
                }
            )


        },

    ) { paddingValues ->

        if (konular.isEmpty()) {
            Text(
                text = stringResource(R.string.NoStoryMessage),
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        } else {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Color(0xFFE3F2FD)
                    //background(Color(0xFFE2EFFC)
                ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {

                if (showDialogPay) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text(stringResource(R.string.HakkınızTükendi)) },
                        text = { Text(
                            stringResource(R.string.haktükendiuzun)
                        ) },
                        confirmButton = {
                            Button(onClick = {
                                showDialogPay = false
                                showMembershipChoiceDialog = true // üyelik seçim dialogunu aç
                                // Üyelik alma ekranına yönlendirme veya işlemi
                                //navController.navigate("uyelikSayfasi")
                            }) {
                                Text(stringResource(R.string.ÜyelikAl))
                            }
                        },
                        dismissButton = {
                            OutlinedButton(onClick = {
                                showDialogPay = false
                                // Ücretsiz sürümle devam et
                            }) {
                                Text(stringResource(R.string.ÜcretsizDevamEt))
                            }
                        }
                    )
                }
                if (showMembershipChoiceDialog) {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("Üyelik Seçin") },
                        text = {
                            Column {
                                Text("Bir üyelik tipi seçin:")

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        showMembershipChoiceDialog = false
                                        updatePremiumForTest(7, 14, context) // Haftalık (7 gün, 14 hak)
                                    }
                                ) {
                                    Text("Haftalık (7 gün, 14 hak)")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        showMembershipChoiceDialog = false
                                        updatePremiumForTest(30, 60, context) // Aylık (30 gün, 60 hak)
                                    }
                                ) {
                                    Text("Aylık (30 gün, 60 hak)")
                                }
                            }
                        },
                        confirmButton = {},
                        dismissButton = {
                            OutlinedButton(onClick = {
                                showMembershipChoiceDialog = false
                            }) {
                                Text("İptal")
                            }
                        }
                    )
                }




                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(17.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(gradientBrush)
                        .wrapContentSize()
                        .clickable {
                            navController.navigate("gameMain")
                        }

                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), // Tüm genişliği kaplasın ki TextAlign.Left etkili olsun
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start // Sola hizalama sağlandı
                    ) {
                        Text(
                            stringResource(R.string.EducationalGames),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            fontSize = 26.sp
                            , fontFamily = sandtitle,

                            color =  Color(0xFFF454a94),
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Text(

                            stringResource(R.string.Learnwhileplaying),
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp, fontFamily = andikabody,
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Image(
                            painter = painterResource(R.drawable.child),
                            contentDescription = "Kaan Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                    shape = RoundedCornerShape(10.dp)
                                    clip = true
                                }
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        color = Color.Black.copy(alpha = 0.1f),
                                        size = size
                                    )
                                }
                        )

                    }
                }



                Image(
                    painter = painterResource(R.drawable.robot),
                    contentDescription = "Kaan Image",
                    contentScale = ContentScale.Crop,

                    )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(17.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(gradientBrush2)
                        .wrapContentSize()
                        .clickable {
                            //showDialog = true
                            navController.navigate("hikayeGecis")
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(), // Tüm genişliği kaplasın ki TextAlign.Left etkili olsun
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.Start // Sola hizalama sağlandı
                    ) {
                        Text(
                           stringResource(R.string.CreateStorieswithAI),
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp,
                            textAlign = TextAlign.Left,
                            fontSize = 26.sp, fontFamily = sandtitle,
                            color =  Color(0xFFF454a94),
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Text(
                            stringResource(R.string.Letyourimaginationfly),
                            textAlign = TextAlign.Left,
                            fontSize = 20.sp, fontFamily = andikabody,
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Image(
                            painter = painterResource(R.drawable.parent),
                            contentDescription = "Kaan Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .graphicsLayer {
                                    shadowElevation = 8.dp.toPx()
                                    shape = RoundedCornerShape(10.dp)
                                    clip = true
                                }
                                .drawWithContent {
                                    drawContent()
                                    drawRect(
                                        color = Color.Black.copy(alpha = 0.1f),
                                        size = size
                                    )
                                }
                        )

                    }
                }


                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Center)
                {
                    Image(
                        painter = painterResource(R.drawable.img),
                        contentDescription = "Kaan Image",
                        contentScale = ContentScale.Crop,

                        )
                    Spacer(Modifier.width(3.dp))
                    Text("Safe for Kids", fontFamily = sandtitle, fontSize = 18.sp)
                }


            }
        }

    }
}

fun updatePremiumForTest(durationDays: Int, totalUses: Int, context: Context) {
    val userId = Firebase.auth.currentUser?.uid ?: return
    val userRef = Firebase.firestore.collection("users").document(userId)

    userRef.update(
        mapOf(
            "premium" to true,
            "premiumStartDate" to Timestamp.now(),
            "premiumDurationDays" to durationDays,
            "remainingChatgptUses" to totalUses
        )
    ).addOnSuccessListener {
        Toast.makeText(context, "Premium üyelik başlatıldı: $durationDays gün, $totalUses hak", Toast.LENGTH_LONG).show()
        Log.d("PremiumUpdate", "Premium güncellendi: $durationDays gün, $totalUses hak")
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Premium güncelleme hatası: ${e.message}", Toast.LENGTH_LONG).show()
        Log.e("PremiumUpdate", "Premium güncelleme hatası: ${e.message}")
    }
}

data class NavItem(
    val label:String,
    val icon:ImageVector
)







