
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import java.util.*

@RequiresApi(Build.VERSION_CODES.DONUT)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Metin(navController: NavController, hikayeViewModel: HikayeViewModel,metinViewModel: MetinViewModel,hikayeId:String?) {
    val hikayeyiOlustur by hikayeViewModel.hikayeOlustur.observeAsState("")
    val context = LocalContext.current
    val hikaye by remember { mutableStateOf(Hikaye()) }
    val kaan by hikayeViewModel.hikaye.observeAsState(Hikaye())
    val generatedImage by metinViewModel.imageBitmap.observeAsState(null)
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var audioVisible by remember { mutableStateOf(false) }
    var audioVisibleStory by remember { mutableStateOf(false) }
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val itim= FontFamily(Font(R.font.itim))
    val zen= FontFamily(Font(R.font.zen))
    val delreg= FontFamily(Font(R.font.delreg))
    val delbold= FontFamily(Font(R.font.delbold))
    val saveMessage = stringResource(R.string.Storyandimagesavedsuccessfully)
    val saveFail = stringResource(R.string.UserSessionMessage)
    val language = stringResource(R.string.language)
    val country = stringResource(R.string.country)

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    LaunchedEffect(hikayeId) {
        hikayeId?.let {
            hikayeViewModel.getStoryById(it)
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6BB7C0), // Üstteki renk
            Color(0xFF21324A)  // Alttaki renk
        ),
        startY = 0f,
        endY = 1800f // Bu değeri ekran yüksekliğine göre ayarlayabilirsiniz.
    )

    DisposableEffect(key1 = context) {

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Türkçe dilini ayarla
                val result = textToSpeech?.setLanguage(Locale(/*"en"*/language, /*"US"*/country))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TextToSpeech", "Dil desteklenmiyor.")
                }
            } else {
                Log.e("TextToSpeech", "Başlatma başarısız.")
            }
        }

        onDispose {
            textToSpeech?.shutdown()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = /*"YOUR STORY"*/stringResource(R.string.YOURSTORY), fontSize = 40.sp, fontWeight = FontWeight.Bold, fontFamily = delbold, textAlign = TextAlign.Center) },
               modifier = Modifier.background(Color.Transparent),
                colors= TopAppBarColors(Color(0xFF6BB7C0),Color(0xFF6BB7C0),Color(0xFF6BB7C0),Color.White,Color.White),

            ) },
        containerColor = Color.DarkGray,
       modifier = Modifier.background(gradientBackground)


    ) { paddingValues ->

        val scrollState = rememberScrollState()
//başlangıç

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .background(gradientBackground),
                    //.background(Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

                ) {
                if (kaan.title.isNotEmpty()) {


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = kaan.imageUrl,
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .padding(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )

                            Image(
                                painter = painterResource(id = R.drawable.mavidenemeee),
                                contentDescription = "",
                                alignment = Alignment.TopEnd,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable {
                                        audioVisibleStory = true
                                    }
                                    .clip(CircleShape)
                                    .size(95.dp)
                                    .padding(end = 10.dp)
                            )
                        }
                    }

                    Text(
                    text = kaan.title,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .padding(10.dp)
                        ,
                    color = Color.White,
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp,
                        fontFamily = zen

                )
                    Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = kaan.content,
                    modifier = Modifier.padding(
                        start = 9.dp,
                        end = 4.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                    fontSize = 19.sp,
                   fontFamily = itim

                )


                Button(onClick = {
                    navController.navigate("saveSayfa")
                }, colors = ButtonDefaults.buttonColors(Color(0xFF6BB7C0)), modifier = Modifier.padding(bottom = 13.dp)) {
                    Text(text = /*"My Stories"*/stringResource(R.string.MYSTORIES),fontSize = 22.sp, fontFamily = zen)

                }
                //bitiş
            }else{
                    if (hikayeyiOlustur.isEmpty()) {
                        Text(
                            text = /*"Story is being created..."*/stringResource(R.string.Storyisbeingcreated___),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text(
                            text = /*"Image is being created..."*/stringResource(R.string.Imageisbeingcreated__),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        Text(
                            text = /*"Converted to sound..."*/stringResource(R.string.Convertedtosound___),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                        CircularProgressIndicator()
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                generatedImage?.let { bitmap ->
                                    val scaledBitmap = bitmap.scale(512, 512) // İstediğiniz boyuta göre ayarlayın
                                    Image(
                                        bitmap = scaledBitmap.asImageBitmap(),
                                        contentDescription = "Generated Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .padding(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                }

                                Image(
                                    painter = painterResource(id = R.drawable.mavidenemeee),
                                    contentDescription = "",
                                    alignment = Alignment.TopEnd,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .clickable {
                                            audioVisible = true
                                        }
                                        .clip(CircleShape)
                                        .size(95.dp)
                                        .padding(end = 10.dp)
                                )
                            }

                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.baseline_bookmark_border_24),
                        contentDescription = "Kaydet",

                        modifier = Modifier.clickable {
                            val userId = getCurrentUserId()

                            if (userId != null && generatedImage != null) {
                                metinViewModel.saveImageToStorage(generatedImage!!, userId)

                                metinViewModel.imageSaved.observe(context as LifecycleOwner) { saved ->
                                    if (saved) {
                                        // Burada, resmi kaydettikten sonra URL'yi almak için bir gözlemci ekleyin
                                        metinViewModel.imageSavedUrl.observe(context as LifecycleOwner) { imageUrl ->
                                            if (imageUrl != null) {
                                                metinViewModel.saveStoryForUser( title = hikayeId!!, story = hikayeyiOlustur, imageUrl = imageUrl, userId = userId)
                                            } else {
                                                Toast.makeText(context, "Could not get image url", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Image could not be saved", Toast.LENGTH_LONG).show()
                                    }
                                }

                                metinViewModel.storySaved.observe(context as LifecycleOwner) { success ->
                                    if (success) {
                                        Toast.makeText(context, /*"Story and image saved successfully"*/saveMessage, Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Image could not be saved", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(context, /*"User session not available or image not available!"*/saveFail, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    Text(text = hikayeyiOlustur,
                        modifier = Modifier.padding(
                        start = 9.dp,
                        end = 4.dp,
                        top = 4.dp,
                        bottom = 4.dp
                    ),
                        color = Color.White,
                        textAlign = TextAlign.Justify,
                        fontSize = 19.sp,
                        fontFamily = itim)
                    Button(onClick = {
                        val prompt = hikayeViewModel.getCurrentPrompt()
                        hikayeViewModel.generateStory(prompt)

                    },colors = ButtonDefaults.buttonColors(Color(0xFF6BB7C0)))  {
                        Text(text = /*"Rebuild"*/stringResource(R.string.Rebuild),fontSize = 22.sp, fontFamily = zen,)
                    }

                    Button(onClick = {
                        navController.navigate("saveSayfa")
                    },colors = ButtonDefaults.buttonColors(Color(0xFF6BB7C0))) {
                        Text(text = /*"My Stories"*/stringResource(R.string.MYSTORIES),fontSize = 22.sp, fontFamily = zen,)
                    }
                }

        }








        }

        if (audioVisible ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.5f))
                .clickable {
                    audioVisible = false
                },
                contentAlignment = Alignment.Center


                ){
                Audio(navController, hikayeViewModel,metinViewModel, onClose = { audioVisible = false })
            }

        }

    if (audioVisibleStory) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.5f))
            .clickable {
                audioVisibleStory = false // Doğru state'i kapatma
            },
            contentAlignment = Alignment.Center
        ) {
            AudioSave(navController, hikayeViewModel, metinViewModel, onClose = { audioVisibleStory = false }) // State'i onClose'da kapat
        }
    }





}


@Composable
fun Audio(navController: NavController,hikayeViewModel: HikayeViewModel, metinViewModel: MetinViewModel,  onClose: () -> Unit) {
    val hikayeyiOlustur by hikayeViewModel.hikayeOlustur.observeAsState("")
    val context = LocalContext.current
    val kaan by hikayeViewModel.hikaye.observeAsState(Hikaye())
    val generatedImage by metinViewModel.imageBitmap.observeAsState(null)
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var audioVisible by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    val language = stringResource(R.string.language)
    val country = stringResource(R.string.country)

    val infiniteTransition = rememberInfiniteTransition()
    val barHeights = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = if (isPlaying) (index * 10 + 10).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            targetValue = if (isPlaying) (index * 10 + 50).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 100),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val barColors = List(6) {
        infiniteTransition.animateColor(
            initialValue = Color(0xFF4CAF50),
            targetValue = Color(0xFFFFC107),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    DisposableEffect(key1 = context) {

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Türkçe dilini ayarla
                val result = textToSpeech?.setLanguage(Locale(/*"en"*/language, /*"US"*/country))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TextToSpeech", "Dil desteklenmiyor.")
                }
            } else {
                Log.e("TextToSpeech", "Başlatma başarısız.")
            }
        }

        onDispose {
            textToSpeech?.shutdown()
        }
    }


    Column(
        modifier = Modifier
            .size(250.dp, 400.dp)
            .background(Color.LightGray.copy(alpha = 0.50f), shape = RectangleShape),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.size(300.dp,180.dp)) {
            // Hikaye fotoğrafını ortalar
            generatedImage?.let { bitmap ->
                val scaledBitmap = bitmap.scale(512, 512) // İstediğiniz boyuta göre ayarlayın
                Image(
                    bitmap = scaledBitmap.asImageBitmap(),
                    contentDescription = "Generated Image",
                    modifier = Modifier
                        .size(512.dp)
                        .padding(2.dp)
                )
            }

            // Çarpı ikonunu sağ üst köşeye yerleştirir
            Image(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd) // Çarpıyı sağ üst köşeye hizalar
                    .padding(end = 10.dp)
                    .clickable {
                        // navController.navigate("metin")
                        textToSpeech?.shutdown()
                        onClose()

                    }// Sağ üst köşeye biraz boşluk ekler (isteğe bağlı)
            )
        }



        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(65.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Çubukları oluşturma
                barHeights.forEachIndexed { index, barHeight ->
                    AudioBar(height = barHeight.value, color = barColors[index].value)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))


        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            Image(
                painter = painterResource(id = R.drawable.playicon),
                contentDescription = "hizlandir icon",
                modifier = Modifier
                    .size(40.dp) // İkonun boyutunu ayarlamak için
                    .clip(CircleShape) // Yuvarlak yapmak için CircleShape kullanılır
                    .background(Color.LightGray) // İsteğe bağlı arka plan rengi
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        textToSpeech?.speak(hikayeyiOlustur, TextToSpeech.QUEUE_FLUSH, null, null)
                        isPlaying = true

                    }
            )



            Image(
                painter = painterResource(id = R.drawable.pauseb),
                contentDescription = "play icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {

                        textToSpeech?.stop()
                        isPlaying = false

                    }// İsteğe bağlı kenarlık
            )



        }

    }
}

@Composable
fun AudioSave(navController: NavController,hikayeViewModel: HikayeViewModel, metinViewModel: MetinViewModel,  onClose: () -> Unit) {
    val hikayeyiOlustur by hikayeViewModel.hikayeOlustur.observeAsState("")
    val context = LocalContext.current
    val kaan by hikayeViewModel.hikaye.observeAsState(Hikaye())
    val generatedImage by metinViewModel.imageBitmap.observeAsState(null)
    var textToSpeech: TextToSpeech? by remember { mutableStateOf(null) }
    var audioVisible by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    val language = stringResource(R.string.language)
    val country = stringResource(R.string.country)

    val infiniteTransition = rememberInfiniteTransition()
    val barHeights = List(6) { index ->
        infiniteTransition.animateFloat(
            initialValue = if (isPlaying) (index * 10 + 10).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            targetValue = if (isPlaying) (index * 10 + 50).toFloat() else 0f, // Oynatmada değilse animasyon başlamasın
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 300 + (index * 100),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    val barColors = List(6) {
        infiniteTransition.animateColor(
            initialValue = Color(0xFF4CAF50),
            targetValue = Color(0xFFFFC107),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    DisposableEffect(key1 = context) {

        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Türkçe dilini ayarla
                val result = textToSpeech?.setLanguage(Locale(/*"en"*/language, /*"US"*/country))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TextToSpeech", "Dil desteklenmiyor.")
                }
            } else {
                Log.e("TextToSpeech", "Başlatma başarısız.")
            }
        }

        onDispose {
            textToSpeech?.shutdown()
        }
    }


    Column(
        modifier = Modifier
            .size(250.dp, 400.dp)
            .background(Color.LightGray.copy(alpha = 0.50f), shape = RectangleShape),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(modifier = Modifier.size(300.dp,180.dp)) {
            AsyncImage(model = kaan.imageUrl, contentDescription = "", modifier = Modifier.size(512.dp,512.dp))


            Image(
                painter = painterResource(id = R.drawable.baseline_close_24),
                contentDescription = "",
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd)
                    .padding(end = 10.dp)
                    .clickable {
                        textToSpeech?.shutdown()
                        onClose() // onClose fonksiyonunu çağırın
                    }
            )

        }



        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(65.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {

                barHeights.forEachIndexed { index, barHeight ->
                    AudioBar(height = barHeight.value, color = barColors[index].value)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))


        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            Image(
                painter = painterResource(id = R.drawable.playicon),
                contentDescription = "hizlandir icon",
                modifier = Modifier
                    .size(40.dp) // İkonun boyutunu ayarlamak için
                    .clip(CircleShape) // Yuvarlak yapmak için CircleShape kullanılır
                    .background(Color.LightGray) // İsteğe bağlı arka plan rengi
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {
                        textToSpeech?.speak(kaan.content, TextToSpeech.QUEUE_FLUSH, null, null)
                        isPlaying = true

                    }
            )



            Image(
                painter = painterResource(id = R.drawable.pauseb),
                contentDescription = "play icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .border(2.dp, Color.Gray, CircleShape)
                    .clickable {

                        textToSpeech?.stop()
                        isPlaying = false

                    }
            )



        }

    }
}


@Composable
fun AudioBar(height: Float, color: Color) {
    Canvas(
        modifier = Modifier
            .size(10.dp, height.dp)
            .background(color)
    ) {

    }
}

