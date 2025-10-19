package com.kaankilic.discoverybox


import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.kaankilic.discoverybox.ui.theme.DiscoveryBoxTheme
import com.kaankilic.discoverybox.util.InterstitialAdHelper
import com.kaankilic.discoverybox.view.SayfaGecisleri
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import com.kaankilic.discoverybox.viewmodel.DilViewModel
import com.kaankilic.discoverybox.viewmodel.GirisSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.KayitSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import com.kaankilic.discoverybox.viewmodel.SaveSayfaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val anasayfaViewModel : AnasayfaViewModel by viewModels()
    val hikayeViewModel : HikayeViewModel by viewModels()
    val dilViewModel : DilViewModel by viewModels()
    val metinViewModel : MetinViewModel by viewModels()
    val girisSayfaViewModel : GirisSayfaViewModel by viewModels()
    val kayitSayfaViewModel : KayitSayfaViewModel by viewModels()
    val saveSayfaViewModel : SaveSayfaViewModel by viewModels()


    private lateinit var auth: FirebaseAuth
    val isEnglish = true




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this){}
        InterstitialAdHelper.loadAd(this)
        enableEdgeToEdge()
        setContent {
           DiscoveryBoxTheme {
                //val navController = rememberNavController()
             auth = FirebaseAuth.getInstance()
                val navController = rememberNavController()

               LaunchedEffect(Unit) {
                    if (auth.currentUser != null) {
                        // Kullanıcı giriş yapmışsa, anasayfaya yönlendir
                        //navController.navigate("anasayfa")
                        navController.navigate("loginSplash") {
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
                   saveSayfaViewModel
                )
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val langCode = prefs.getString("language_code", "tr") ?: "tr"

        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        val updatedContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(updatedContext)
    }


}


