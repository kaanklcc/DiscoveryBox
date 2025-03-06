package com.kaankilic.discoverybox.view


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import kotlinx.coroutines.launch

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Anasayfa(navController: NavController, anasayfaViewModel: AnasayfaViewModel) {
    val konular by anasayfaViewModel.konular.observeAsState(emptyList()) // Boş bir liste ile başlatıyoruz
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {SnackbarHostState()}
    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val message = stringResource(R.string.logOutMessage)
    val actionLabel = stringResource(R.string.Yes)
    val exitedApp = stringResource(R.string.exitMessage)
    val wrongPassword = stringResource(R.string.WrongPassword)

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFC107), // Sarı
            Color(0xFF64B5F6), // Açık Mavi
            Color(0xFFFF8A80)  // Pembe
        )
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {

            CenterAlignedTopAppBar(title = { Text(text = /*"DISCOVERY BOX"*/ stringResource(R.string.DiscoveryBox), fontSize = 35.sp, textAlign = TextAlign.Center) }
            ,colors = TopAppBarColors( Color(0xFF64B5F6), Color(0xFF64B5F6), Color(0xFF64B5F6), Color.White, Color.White),

                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val sb = snackbarHostState
                                .showSnackbar(message = /*"Do you want to log out?"*/message, actionLabel = /*"Yes"*/actionLabel)
                            if (sb == SnackbarResult.ActionPerformed){
                                snackbarHostState.showSnackbar(message = /*"exited the application"*/exitedApp)
                                anasayfaViewModel.signOut()
                                navController.navigate("girisSayfa")

                            }
                        }



                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
            )


        }
    ) { paddingValues ->
        if (konular.isEmpty()) {
            Text(
                text = /*"There is no story to show"*/stringResource(R.string.NoStoryMessage),
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                textAlign = TextAlign.Center
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(gradientBrush)
            ) {
                Box (modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        showDialog = true
                        //navController.navigate("hikayeGecis")
                    }
                    .padding(9.dp)){



                    Image(
                        painter = painterResource(R.drawable.parent),
                        contentDescription = "parent picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                           // .weight(1f)
                            .padding(9.dp)
                            .fillMaxHeight()
                            .alpha(0.8f) // Görünürlüğü azaltır
                            //.clip(RoundedCornerShape(16.dp)) // Köşeleri yuvarlatır
                            .border(
                                width = 7.dp, // Kenarlık kalınlığı
                                color = Color.Black, // Kenarlık rengi
                                //shape = RoundedCornerShape(16.dp) // Kenarlığın şekli
                            )
                            .padding(8.dp) // Görsele iç boşluk ekler
                           /* .clickable {
                                navController.navigate("hikayeGecis")
                            }*/
                    )

                    Text(
                        text = /*"Story"*/stringResource(R.string.Story),
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter) // Ortaya hizalama
                           // .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)) // Arka planı yarı şeffaf yap
                            .padding(8.dp)
                            .offset(y = (10).dp)
                    )
                    Text(
                        text = /*"Mode"*/stringResource(R.string.Mode),
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter) // Ortaya hizalama
                            // .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)) // Arka planı yarı şeffaf yap
                            .padding(8.dp)
                            .offset(y = (30).dp)
                    )

                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(/*"Enter Password"*/stringResource(R.string.EnterPassword)) },
                        text = {
                            Column {
                                TextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text(/*"Password"*/stringResource(R.string.Password)) },
                                    visualTransformation = PasswordVisualTransformation()
                                )
                                errorMessage?.let { Text(it, color = Color.Red) }
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                anasayfaViewModel.reauthenticateUser(password,
                                    onSuccess = {
                                        showDialog = false
                                       // onSuccess()
                                        navController.navigate("hikayeGecis")
                                    },
                                    onFailure = {
                                        errorMessage = /*"Wrong Password"*/wrongPassword
                                    }
                                )
                            }) {
                                Text(/*"Verify"*/stringResource(R.string.Verify))
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text(/*"Cancel"*/stringResource(R.string.Cancel))
                            }
                        }
                    )
                }

                Box (modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        navController.navigate("gameMain")
                    }
                    .padding(9.dp)){
                    Image(
                        painter = painterResource(R.drawable.child),
                        contentDescription = "child picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                           // .weight(1f)
                            .fillMaxHeight()
                            /*.clickable {
                                navController.navigate("gameMain")
                            }*/
                            .padding(9.dp)
                            .alpha(0.8f) // Görünürlüğü azaltır
                            //.clip(RoundedCornerShape(16.dp)) // Köşeleri yuvarlatır
                            .border(
                                width = 7.dp, // Kenarlık kalınlığı
                                color = Color.Black, // Kenarlık rengi
                                //shape = RoundedCornerShape(16.dp) // Kenarlığın şekli
                            )
                            .padding(8.dp) // Görsele iç boşluk ekler

                    )

                    Text(
                        text = /*"Game"*/stringResource(R.string.Game),
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter) // Ortaya hizalama
                            //.background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)) // Arka planı yarı şeffaf yap
                            .padding(8.dp)
                            .offset(y = (10).dp)
                    )
                    Text(
                        text = /*"Mode"*/stringResource(R.string.Mode),
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter) // Ortaya hizalama
                            // .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp)) // Arka planı yarı şeffaf yap
                            .padding(8.dp)
                            .offset(y = (30).dp)
                    )

                }



            }
        }
    }
}

/*@Composable
fun StoryItem(story: Story, navController: NavController) {
    // Hikaye öğesinin görünümü
    Column(
        modifier = Modifier
            .padding(6.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                when (story.category) {
                    "bilim" -> navController.navigate("bilim")
                    "diger" -> navController.navigate("diger")
                    "dil" -> navController.navigate("dil")
                    "guncelHayat" -> navController.navigate("guncelHayat")
                    "hikaye" -> navController.navigate("hikaye")
                    "saveSayfa" -> navController.navigate("saveSayfa")// Eğer hikaye sayfası var ise
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
}*/


