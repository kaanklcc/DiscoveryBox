package com.kaankilic.discoverybox.view

import Metin
import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import com.kaankilic.discoverybox.viewmodel.CardSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.DilViewModel
import com.kaankilic.discoverybox.viewmodel.GirisSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.KayitSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import com.kaankilic.discoverybox.viewmodel.NumberGameViewModel
import com.kaankilic.discoverybox.viewmodel.SaveSayfaViewModel

@Composable
fun SayfaGecisleri(
    navController: NavHostController,
    anasayfaViewModel: AnasayfaViewModel,
    hikayeViewModel: HikayeViewModel,
    dilViewModel: DilViewModel,
    metinViewModel: MetinViewModel,
    girisSayfaViewModel: GirisSayfaViewModel,
    kayitSayfaViewModel: KayitSayfaViewModel,
    saveSayfaViewModel: SaveSayfaViewModel,
    cardSayfaViewModel: CardSayfaViewModel,
    numberGameViewModel: NumberGameViewModel


    ) {
   // val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splashScreen1" ){
        composable("anasayfa"){
            Anasayfa(navController = navController, anasayfaViewModel = anasayfaViewModel)
        }
        composable("gameMain"){
            GameMain(navController,anasayfaViewModel)
        }
        composable("wordGame"){
            MeyveKartSirali(cardSayfaViewModel)
        }
        composable("matchingGame"){
            MatchGameScreen(cardSayfaViewModel,false)
        }
        composable("colorGame"){
            GameApp()

        }
        composable("hikayeGecis"){
            HikayeGecis(navController)
        }
        composable("hikaye"){
            Hikaye(navController = navController,hikayeViewModel,metinViewModel,anasayfaViewModel)
        }

        composable("metin/{hikayeId}") { backStackEntry ->
            val hikayeId = backStackEntry.arguments?.getString("hikayeId")
            Metin(navController = navController, hikayeViewModel, metinViewModel, hikayeId,anasayfaViewModel)
        }
        composable("girisSayfa"){
            GirisSayfa(navController = navController,girisSayfaViewModel)
        }
        composable("kayitSayfa"){
            KayitSayfa(navController = navController,kayitSayfaViewModel)
        }
        composable("saveSayfa"){
            SaveSayfa(navController = navController,saveSayfaViewModel,hikayeViewModel)
        }
        composable("splashScreen1"){
            SplashScreen1(navController)

        }
        composable("splashScreen2"){
            SplashScreen2(navController)

        }
        composable("splashScreen3"){
            SplashScreen3(navController)

        }
        composable("loginSplash"){
            LoginSplashScreen(navController)

        }
        composable("numberGame"){
        NumberGameScreen(navController,numberGameViewModel)

    }

    }
    
}