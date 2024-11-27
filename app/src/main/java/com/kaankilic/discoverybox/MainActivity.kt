package com.kaankilic.discoverybox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.ui.theme.DiscoveryBoxTheme
import com.kaankilic.discoverybox.view.Anasayfa
import com.kaankilic.discoverybox.view.Bilim
import com.kaankilic.discoverybox.view.Diger
import com.kaankilic.discoverybox.view.Dil
import com.kaankilic.discoverybox.view.GirisSayfa
import com.kaankilic.discoverybox.view.GuncelHayat
import com.kaankilic.discoverybox.view.Hikaye
import com.kaankilic.discoverybox.view.KayitSayfa
import com.kaankilic.discoverybox.view.MatchGameScreen
import com.kaankilic.discoverybox.view.SaveSayfa
import com.kaankilic.discoverybox.view.SayfaGecisleri
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import com.kaankilic.discoverybox.viewmodel.CardSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.DilViewModel
import com.kaankilic.discoverybox.viewmodel.GirisSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.KayitSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import com.kaankilic.discoverybox.viewmodel.SaveSayfaViewModel

class MainActivity : ComponentActivity() {

    val anasayfaViewModel : AnasayfaViewModel by viewModels()
    val hikayeViewModel : HikayeViewModel by viewModels()
    val dilViewModel : DilViewModel by viewModels()
    val metinViewModel : MetinViewModel by viewModels()
    val girisSayfaViewModel : GirisSayfaViewModel by viewModels()
    val kayitSayfaViewModel : KayitSayfaViewModel by viewModels()
    val saveSayfaViewModel : SaveSayfaViewModel by viewModels()
    val cardSayfaViewModel:CardSayfaViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    val isEnglish = true




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiscoveryBoxTheme {
                //val navController = rememberNavController()
                auth = FirebaseAuth.getInstance()
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    if (auth.currentUser != null) {
                        // Kullanıcı giriş yapmışsa, anasayfaya yönlendir
                        navController.navigate("anasayfa") {
                            popUpTo("girisSayfa") { inclusive = true }
                        }
                    }
                }
               SayfaGecisleri(
                   navController=navController,
                    anasayfaViewModel = anasayfaViewModel,
                    hikayeViewModel = hikayeViewModel,
                    dilViewModel,
                    metinViewModel = metinViewModel,
                    girisSayfaViewModel,
                    kayitSayfaViewModel,
                   saveSayfaViewModel,
                )
               // SaveSayfa(navController = rememberNavController() , saveSayfaViewModel =saveSayfaViewModel )
                //GirisSayfa()
                //KayitSayfa()
               // Anasayfa(anasayfaViewModel = anasayfaViewModel)
               // Hikaye()
               // Dil()
                //GuncelHayat()
                //Bilim()
                //Diger()*/
                //MatchGameScreen(cardSayfaViewModel = cardSayfaViewModel, isEnglish = isEnglish )

            }
        }
    }
}


