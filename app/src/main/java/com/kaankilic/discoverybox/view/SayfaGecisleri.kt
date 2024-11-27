package com.kaankilic.discoverybox.view

import Metin
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.kaankilic.discoverybox.entitiy.Hikaye
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import com.kaankilic.discoverybox.viewmodel.DilViewModel
import com.kaankilic.discoverybox.viewmodel.GirisSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.KayitSayfaViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
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

    ) {
   // val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "girisSayfa" ){
        composable("anasayfa"){
            Anasayfa(navController = navController, anasayfaViewModel = anasayfaViewModel)
        }
        composable("bilim"){
            Bilim(navController = navController)
        }
        composable("diger"){
            Diger(navController = navController)
        }
        composable("dil"){
            Dil(navController = navController,dilViewModel)
        }
        composable("guncelHayat"){
            GuncelHayat(navController = navController)
        }
        composable("hikaye"){
            Hikaye(navController = navController,hikayeViewModel,metinViewModel)
        }

        composable("metin/{hikayeId}") { backStackEntry ->
            val hikayeId = backStackEntry.arguments?.getString("hikayeId")
            Metin(navController = navController, hikayeViewModel, metinViewModel, hikayeId)
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

    }
    
}