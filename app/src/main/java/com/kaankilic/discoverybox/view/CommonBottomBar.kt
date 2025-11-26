package com.kaankilic.discoverybox.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kaankilic.discoverybox.R

@Composable
fun CommonBottomBar(
    navController: NavController,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF003366)
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = {
                onTabSelected(0)
                navController.navigate("anasayfa") {
                    popUpTo("anasayfa") { inclusive = true }
                }
            },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == 0) Color(0xFFFCD34D) else Color.White
                )
            },
            label = { Text(stringResource(R.string.home), fontSize = 10.sp, color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFCD34D),
                unselectedIconColor = Color.White,
                indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = {
                onTabSelected(1)
                navController.navigate("hikaye")
            },
            icon = {
                Icon(
                    Icons.Default.Create,
                    contentDescription = "Create",
                    tint = if (selectedTab == 1) Color(0xFFFCD34D) else Color.White
                )
            },
            label = { Text(stringResource(R.string.create), fontSize = 10.sp, color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFCD34D),
                unselectedIconColor = Color.White,
                indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = {
                onTabSelected(2)
                navController.navigate("saveSayfa")
            },
            icon = {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Saved",
                    tint = if (selectedTab == 2) Color(0xFFFCD34D) else Color.White
                )
            },
            label = { Text(stringResource(R.string.saved), fontSize = 10.sp, color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFCD34D),
                unselectedIconColor = Color.White,
                indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = {
                onTabSelected(3)
                navController.navigate("profil")
            },
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (selectedTab == 3) Color(0xFF22D3EE) else Color.White
                )
            },
            label = { Text(stringResource(R.string.profile), fontSize = 10.sp, color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF22D3EE),
                unselectedIconColor = Color.White,
                indicatorColor = Color(0xFF06B6D4).copy(alpha = 0.2f)
            )
        )
    }
}
