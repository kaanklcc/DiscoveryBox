@file:OptIn(ExperimentalMaterial3Api::class)

package com.kaankilic.discoverybox.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.AnasayfaViewModel
import com.kaankilic.discoverybox.viewmodel.HikayeViewModel
import com.kaankilic.discoverybox.viewmodel.MetinViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Hikaye(
    navController: NavController,
    hikayeViewModel: HikayeViewModel,
    metinViewModel: MetinViewModel,
    anasayfaViewModel: AnasayfaViewModel
) {
    val context = LocalContext.current
    val sandtitle = FontFamily(Font(R.font.sandtitle))
    val andikabody = FontFamily(Font(R.font.andikabody))
    val coroutineScope = rememberCoroutineScope()

    var konu by remember { mutableStateOf(TextFieldValue("")) }
    var mekan by remember { mutableStateOf(TextFieldValue("")) }
    var anaKarakter by remember { mutableStateOf(TextFieldValue("")) }
    var anaKarakterOzellik by remember { mutableStateOf(TextFieldValue("")) }
    var yanKarakterler by remember { mutableStateOf(listOf("")) }
    var selectedTheme by remember { mutableStateOf("") }
    var selectedLength by remember { mutableStateOf("") }

    var themeExpanded by remember { mutableStateOf(true) }
    var lengthExpanded by remember { mutableStateOf(false) }
    var supportingExpanded by remember { mutableStateOf(false) }

    val themes = listOf(
        stringResource(R.string.Adventure),
        stringResource(R.string.Love),
        stringResource(R.string.Friendship),
        stringResource(R.string.Family),
        stringResource(R.string.Action)
    )
    val lengths = listOf(
        stringResource(R.string.Short),
        stringResource(R.string.Medium),
        stringResource(R.string.Long)
    )

    var selectedTab by remember { mutableStateOf(1) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row {
                            Image(
                                painterResource(R.drawable.pencil),"pencil"
                            )
                            Text(
                                stringResource(R.string.create_your_story),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = sandtitle,
                                color = Color.White
                            )
                        }
                        Text(
                            stringResource(R.string.let_imagination_live),
                            fontSize = 12.sp,
                            fontFamily = andikabody,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF4C1D95)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1B4B),
                modifier = Modifier.height(90.dp)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { 
                        selectedTab = 0
                        navController.navigate("anasayfa")
                    },
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (selectedTab == 0) Color(0xFFC084FC) else Color(0xFFE9D5FF)
                        )
                    },
                    label = { Text(stringResource(R.string.home), fontSize = 10.sp, color = Color(0xFFE9D5FF)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFC084FC),
                        unselectedIconColor = Color(0xFFE9D5FF),
                        indicatorColor = Color(0xFF7C3AED).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = "Create",
                            tint = if (selectedTab == 1) Color(0xFFF472B6) else Color(0xFFFCE7F3)
                        )
                    },
                    label = { Text(stringResource(R.string.create), fontSize = 10.sp, color = Color(0xFFFCE7F3)) },
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
                    label = { Text(stringResource(R.string.saved), fontSize = 10.sp, color = Color(0xFFFEF3C7)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFBBF24),
                        unselectedIconColor = Color(0xFFFEF3C7),
                        indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        Firebase.auth.signOut()
                        navController.navigate("girisSayfa") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = if (selectedTab == 3) Color(0xFF22D3EE) else Color(0xFFCFFAFE)
                        )
                    },
                    label = { Text(stringResource(R.string.logout), fontSize = 10.sp, color = Color(0xFFCFFAFE)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF22D3EE),
                        unselectedIconColor = Color(0xFFCFFAFE),
                        indicatorColor = Color(0xFF06B6D4).copy(alpha = 0.2f)
                    )
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF4C1D95),
                            Color(0xFF6B21A8),
                            Color(0xFF7E22CE)
                        )
                    )
                )
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section (Accordion)
            AccordionCard(
                title = stringResource(R.string.theme),
                subtitle = if (selectedTheme.isNotEmpty()) selectedTheme else stringResource(R.string.select_theme),
                icon = R.drawable.theme,
                expanded = themeExpanded,
                onExpandChange = { themeExpanded = !themeExpanded }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeButton(
                        text = themes[0],
                        icon = "🏰",
                        color = Color(0xFFEC4899),
                        selected = selectedTheme == themes[0],
                        modifier = Modifier.weight(1f)
                    ) { selectedTheme = themes[0] }
                    ThemeButton(
                        text = themes[1],
                        icon = "🚀",
                        color = Color(0xFF06B6D4),
                        selected = selectedTheme == themes[1],
                        modifier = Modifier.weight(1f)
                    ) { selectedTheme = themes[1] }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeButton(
                        text = themes[2],
                        icon = "🌲",
                        color = Color(0xFF10B981),
                        selected = selectedTheme == themes[2],
                        modifier = Modifier.weight(1f)
                    ) { selectedTheme = themes[2] }
                    ThemeButton(
                        text = themes[3],
                        icon = "👻",
                        color = Color(0xFF8B5CF6),
                        selected = selectedTheme == themes[3],
                        modifier = Modifier.weight(1f)
                    ) { selectedTheme = themes[3] }
                }
                if (themes.size > 4) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ThemeButton(
                            text = themes[4],
                            icon = "⚡",
                            color = Color(0xFFF59E0B),
                            selected = selectedTheme == themes[4],
                            modifier = Modifier.weight(0.5f)
                        ) { selectedTheme = themes[4] }
                    }
                }
            }

            // Story Length Section (Accordion)
            AccordionCard(
                title = stringResource(R.string.story_length),
                subtitle = if (selectedLength.isNotEmpty()) selectedLength else stringResource(R.string.select_story_length),
                icon = R.drawable.book,
                expanded = lengthExpanded,
                onExpandChange = { lengthExpanded = !lengthExpanded }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    lengths.forEachIndexed { index, length ->
                        val colors = listOf(
                            Color(0xFFEC4899),
                            Color(0xFF8B5CF6),
                            Color(0xFF06B6D4)
                        )
                        val icons = listOf("📖", "📚", "📕")
                        ThemeButton(
                            text = length,
                            icon = icons.getOrElse(index) { "📖" },
                            color = colors.getOrElse(index) { Color(0xFF8B5CF6) },
                            selected = selectedLength == length,
                            modifier = Modifier.weight(1f)
                        ) { selectedLength = length }
                    }
                }
            }

            // Setting Section
            InputCard(
                title = stringResource(R.string.topic),
                icon = R.drawable.topic,
                placeholder = "A magical kingdom in the clouds...",
                value = konu,
                onValueChange = { konu = it }
            )

            // Main Character Section
            InputCard(
                title = stringResource(R.string.main_character),
                icon = R.drawable.main_cha,
                placeholder = stringResource(R.string.enter_hero_name),
                value = anaKarakter,
                onValueChange = { anaKarakter = it }
            )

            // Supporting Characters Section (Accordion)
            AccordionCard(
                title = stringResource(R.string.supporting_characters),
                subtitle = stringResource(R.string.add_characters),
                icon =R.drawable.sup_cha,
                expanded = supportingExpanded,
                onExpandChange = { supportingExpanded = !supportingExpanded }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    yanKarakterler.forEachIndexed { index, character ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = character,
                                onValueChange = { newValue ->
                                    yanKarakterler = yanKarakterler.toMutableList().apply {
                                        this[index] = newValue
                                    }
                                },
                                placeholder = { Text(stringResource(R.string.add_character), fontSize = 14.sp, color = Color(0xFF9CA3AF)) },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF1F2937),
                                    unfocusedTextColor = Color(0xFF1F2937),
                                    cursorColor = Color(0xFF6B46C1),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            )
                            if (index == yanKarakterler.size - 1) {
                                IconButton(
                                    onClick = {
                                        yanKarakterler = yanKarakterler + ""
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(Color(0xFFFCD34D))
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color(0xFF6B46C1)
                                    )
                                }
                            }
                            if (yanKarakterler.size > 1) {
                                IconButton(
                                    onClick = {
                                        yanKarakterler = yanKarakterler.toMutableList().apply {
                                            removeAt(index)
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = Color(0xFFEF4444)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Location Section
            InputCard(
                title = stringResource(R.string.location),
                icon = R.drawable.location,
                placeholder = "The Enchanted Forest",
                value = mekan,
                onValueChange = { mekan = it }
            )

            // Character Trait Section
            InputCard(
                title = stringResource(R.string.main_character_trait),
                icon = R.drawable.trait,
                placeholder = stringResource(R.string.MainCharacterCharacteristicExp),
                value = anaKarakterOzellik,
                onValueChange = { anaKarakterOzellik = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Generate Button
            Button(
                onClick = {
                    val yanKarakterlerText = yanKarakterler.filter { it.isNotBlank() }.joinToString(", ")
                    val temaText = if (selectedTheme.isNotEmpty()) "Tema: $selectedTheme" else ""
                    val uzunlukText = if (selectedLength.isNotEmpty()) "Uzunluk: $selectedLength" else ""

                    val characterDescription = when {
                        anaKarakter.text.contains("shrek", ignoreCase = true) -> "${anaKarakter.text} (yeşil dev, büyük kulaklar)"
                        anaKarakter.text.contains("sindirella", ignoreCase = true) || anaKarakter.text.contains("cinderella", ignoreCase = true) -> "${anaKarakter.text} (sarı saçlı prenses, mavi elbise)"
                        anaKarakter.text.contains("pamuk prenses", ignoreCase = true) || anaKarakter.text.contains("snow white", ignoreCase = true) -> "${anaKarakter.text} (siyah saçlı prenses, kırmızı kurdele)"
                        anaKarakter.text.contains("rapunzel", ignoreCase = true) -> "${anaKarakter.text} (çok uzun sarı saçlı prenses)"
                        anaKarakter.text.contains("elsa", ignoreCase = true) -> "${anaKarakter.text} (platin sarısı saçlı buz kraliçesi)"
                        anaKarakter.text.contains("anna", ignoreCase = true) -> "${anaKarakter.text} (kızıl saçlı prenses)"
                        else -> "${anaKarakter.text} (${anaKarakterOzellik.text})"
                    }

                    val generatedStory = "Bana bir çocuk hikayesi yaz. " +
                            "Konu: ${konu.text}, " +
                            "Mekan: ${mekan.text}, " +
                            "Ana karakter: $characterDescription, " +
                            "Yardımcı karakterler: $yanKarakterlerText, " +
                            "$temaText, " +
                            "$uzunlukText. " +
                            "ÖNEMLİ: Karakterlerin fiziksel görünümünü her sayfada tutarlı tut. Hikaye doğrudan başlasın."

                    hikayeViewModel.setStoryContext(characterDescription, mekan.text)
                    navController.navigate("metin/${konu.text}")
                    
                    anasayfaViewModel.checkUserAccess { hasTrial, isPremiumStatus, _ ->
                        hikayeViewModel.generateStoryWithImages(generatedStory, selectedLength, context, isPremiumStatus || hasTrial)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFCD34D)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(painter = painterResource(R.drawable.pencil), contentDescription = null, tint = Color(0xFF6B46C1))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.CreatetheStory),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = sandtitle,
                    color = Color(0xFF6B46C1)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AccordionCard(
    title: String,
    subtitle: String,
    icon: Int,
    expanded: Boolean,
    onExpandChange: () -> Unit,
    content: @Composable (() -> Unit)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6).copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandChange() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = Color(0xFFFCD34D),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            subtitle,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFFCD34D)
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ThemeButton(
    text: String,
    icon: String,
    color: Color,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color else color.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun LengthOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFFEDE9FE) else Color.Transparent)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            fontSize = 14.sp,
            color = if (selected) Color(0xFF7C3AED) else Color(0xFF6B7280),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF7C3AED),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun InputCard(
    title: String,
    icon: Int,
    placeholder: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8B5CF6).copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = Color(0xFFFCD34D),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, fontSize = 14.sp, color = Color(0xFF9CA3AF)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = Color(0xFF6B46C1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            )
        }
    }
}
