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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row {
                            Image(
                                painterResource(R.drawable.pencil),"pencil",


                            )
                            Text(
                                "Create Your Story",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = sandtitle,
                                color = Color.White
                            )

                        }

                        Text(
                            "Let your imagination come to life!",
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
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6B46C1)
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF6B46C1))
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section (Accordion)
            AccordionCard(
                title = "Theme",
                subtitle = if (selectedTheme.isNotEmpty()) selectedTheme else "Select a theme",
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
                title = "Story Length",
                subtitle = if (selectedLength.isNotEmpty()) selectedLength else "Select story length",
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
                title = "Konu",
                icon = R.drawable.topic,
                placeholder = "A magical kingdom in the clouds...",
                value = konu,
                onValueChange = { konu = it }
            )

            // Main Character Section
            InputCard(
                title = "Main Character",
                icon = R.drawable.main_cha,
                placeholder = "Enter your hero's name",
                value = anaKarakter,
                onValueChange = { anaKarakter = it }
            )

            // Supporting Characters Section (Accordion)
            AccordionCard(
                title = "Supporting Characters",
                subtitle = "Add characters",
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
                                placeholder = { Text("Add a character...", fontSize = 14.sp, color = Color(0xFF9CA3AF)) },
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
                                        .background(Color(0xFF10B981))
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add",
                                        tint = Color.White
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
                title = "Location",
                icon = R.drawable.location,
                placeholder = "The Enchanted Forest",
                value = mekan,
                onValueChange = { mekan = it }
            )

            // Character Trait Section
            InputCard(
                title = "Main Character Trait",
                icon = R.drawable.trait,
                placeholder = stringResource(R.string.MainCharacterCharacteristicExp),
                value = anaKarakterOzellik,
                onValueChange = { anaKarakterOzellik = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Generate Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        val yanKarakterlerText = yanKarakterler.filter { it.isNotBlank() }.joinToString(", ")
                        val temaText = if (selectedTheme.isNotEmpty()) "Tema: $selectedTheme" else ""
                        val uzunlukText = if (selectedLength.isNotEmpty()) "Uzunluk: $selectedLength" else ""

                        val generatedStory = "Bana bir hikaye yaz. " +
                                "Konu: ${konu.text}, " +
                                "Mekan: ${mekan.text}, " +
                                "Ana karakter: ${anaKarakter.text}, " +
                                "Ana karakter özelliği: ${anaKarakterOzellik.text}, " +
                                "Yardımcı karakterler: $yanKarakterlerText, " +
                                "$temaText, " +
                                "$uzunlukText. " +
                                "Hikaye doğrudan başlasın."

                        val imageGenerate = "A magical scene of ${konu.text} in ${mekan.text}"

                        hikayeViewModel.generateStory(generatedStory)
                        metinViewModel.queryTextToImage(imageGenerate, isPro = false, context = context)
                        navController.navigate("metin/${konu.text}")
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
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) color else color.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text,
                fontSize = 13.sp,
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
