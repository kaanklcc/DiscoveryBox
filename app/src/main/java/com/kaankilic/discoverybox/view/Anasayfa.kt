package com.kaankilic.discoverybox.view


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Story
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import kotlinx.coroutines.launch
import java.util.Locale

/*@SuppressLint("RememberReturnType")
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
    val context = LocalContext.current

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

            CenterAlignedTopAppBar(title = { Text(text =stringResource(R.string.DiscoveryBox), fontSize = 35.sp, textAlign = TextAlign.Center) }
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
}*/

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
    val message = stringResource(R.string.logOutMessage)
    val actionLabel = stringResource(R.string.Yes)
    val exitedApp = stringResource(R.string.exitMessage)
    val wrongPassword = stringResource(R.string.WrongPassword)
    val delbold= FontFamily(Font(R.font.delbold))
    val context = LocalContext.current

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFCD7D7), // Sarı
            Color(0xFFB0D2FD), // Açık Mavi
            // Color(0xFFFF8A80)  // Pembe
        )
    )
    val gradientBrush2 = Brush.linearGradient(
        colors = listOf(
            Color(0xFFC5FCC6), // Sarı
            Color(0xFFFCC1FC), // Açık Mavi
            //Color(0xFF353BA4)  // Pembe
        )
    )


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.DiscoveryBox),
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = delbold
                    )
                },
                colors = TopAppBarColors( Color(0xFFE2EFFC), Color(0xFF81D4FA), Color(0xFF81D4FA), Color(0xFF353BA4),  Color(0xFF353BA4)),


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
                                anasayfaViewModel.signOut()
                                navController.navigate("girisSayfa")

                            }
                        }


                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint =Color(0xFF353BA4) ,
                            modifier = Modifier.size(60.dp),

                        )

                    }
                }
            )


        }
    ) { paddingValues ->

        if (konular.isEmpty()) {
            Text(
                text = stringResource(R.string.NoStoryMessage),
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()
                .padding(paddingValues).
                background(Color(0xFFE2EFFC)
            ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {

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
                            "Educational Games",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            fontSize = 24.sp
                            , fontFamily = delbold,

                            color =  Color(0xFFF454a94),
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Text(
                            "Learn while playing!",
                            textAlign = TextAlign.Left,
                            fontSize = 16.sp, fontFamily = delbold,
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
                            showDialog = true
                            //navController.navigate("hikayeGecis")
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
                            "Create Stories with AI",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Left,
                            fontSize = 24.sp, fontFamily = delbold,
                            color =  Color(0xFFF454a94),
                            modifier = Modifier.fillMaxWidth() // Text'in genişliğini doldurması için
                        )
                        Text(
                            "Let your imagination fly!",
                            textAlign = TextAlign.Left,
                            fontSize = 16.sp, fontFamily = delbold,
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
                                Text(stringResource(R.string.Verify))
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text(stringResource(R.string.Cancel))
                            }
                        }
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Center)
                {
                    Image(
                        painter = painterResource(R.drawable.img),
                        contentDescription = "Kaan Image",
                        contentScale = ContentScale.Crop,

                        )
                    Spacer(Modifier.width(3.dp))
                    Text("Safe for Kids", fontFamily = delbold)
                }


            }
        }

    }
}






