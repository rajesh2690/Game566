package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.viewmodel.GameViewModel
import java.text.NumberFormat
import java.util.Locale

// Custom Cinema Colors (Immersive UI Design Tokens)
val ColorDeepSlate = Color(0xFF1C1B1F)     // #1C1B1F (Deep Slate-Charcoal Background)
val ColorSurfaceDark = Color(0xFF2B2930)    // #2B2930 (Card Background)
val ColorCinemaGold = Color(0xFFD0BCFF)     // #D0BCFF (M3 Lilac/Lavender Accent)
val ColorCinemaRed = Color(0xFFF2B8B5)      // #F2B8B5 (Soft Error/Accent Pinkish-Red)
val ColorNeonGreen = Color(0xFF81C784)      // #81C784 (Subtle trending green)
val ColorNeonCyan = Color(0xFFCCC2DC)       // #CCC2DC (Light purple/grey secondary)
val ColorMutedText = Color(0xFFCAC4D0)      // #CAC4D0 (Subtitle/Border contrast)
val ColorBorder = Color(0xFF49454F)         // #49454F (Border color)
val ColorMoneyPink = Color(0xFFFFD8E4)      // #FFD8E4 (Money / Funds color highlight)
val ColorActivePill = Color(0xFF4F378B)     // #4F378B (Active Nav indicator background)
val ColorLabelLilac = Color(0xFFEADDFF)     // #EADDFF (Active element details label text)
val ColorTextOnLilac = Color(0xFF381E72)    // #381E72 (Contrast dark color on filled lilac elements)

@Composable
fun MovieSimulatorApp(viewModel: GameViewModel = viewModel()) {
    val activeSave by viewModel.studioState.collectAsStateWithLifecycle()
    val completedMovies by viewModel.releasedMovies.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    // Root styling
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ColorDeepSlate
    ) {
        val state = activeSave
        if (state == null) {
            NewStudioScreen(onStart = { name -> viewModel.startNewStudio(name) })
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing),
                bottomBar = {
                    NavigationBar(
                        containerColor = Color(0xFF211F26), // #211F26 (Elegantly dark status/nav bar background)
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .border(BorderStroke(1.dp, ColorBorder).copy(width = 0.5.dp)) // border-t border-[#49454F]
                    ) {
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Studio") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ColorLabelLilac,
                                selectedTextColor = ColorLabelLilac,
                                indicatorColor = ColorActivePill, // #4F378B active indicator pill
                                unselectedIconColor = ColorMutedText.copy(alpha = 0.6f),
                                unselectedTextColor = ColorMutedText.copy(alpha = 0.6f)
                            )
                        )
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = {
                                BadgedBox(badge = {
                                    if (state.currentStage != ActiveProjectStage.NONE) {
                                        Badge(containerColor = ColorCinemaRed) { Text("!") }
                                    }
                                }) {
                                    Icon(Icons.Default.Movie, contentDescription = "Studio Desk")
                                }
                            },
                            label = { Text("Desk") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ColorLabelLilac,
                                selectedTextColor = ColorLabelLilac,
                                indicatorColor = ColorActivePill,
                                unselectedIconColor = ColorMutedText.copy(alpha = 0.6f),
                                unselectedTextColor = ColorMutedText.copy(alpha = 0.6f)
                            )
                        )
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = { Icon(Icons.Default.HistoryEdu, contentDescription = "Film Library") },
                            label = { Text("Films") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ColorLabelLilac,
                                selectedTextColor = ColorLabelLilac,
                                indicatorColor = ColorActivePill,
                                unselectedIconColor = ColorMutedText.copy(alpha = 0.6f),
                                unselectedTextColor = ColorMutedText.copy(alpha = 0.6f)
                            )
                        )
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Achievements") },
                            label = { Text("Awards") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ColorLabelLilac,
                                selectedTextColor = ColorLabelLilac,
                                indicatorColor = ColorActivePill,
                                unselectedIconColor = ColorMutedText.copy(alpha = 0.6f),
                                unselectedTextColor = ColorMutedText.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        0 -> DashboardScreen(
                            save = state,
                            completedCount = completedMovies.size,
                            onStartNewMovie = { selectedTab = 1 }
                        )
                        1 -> ProductionDeskScreen(
                            save = state,
                            viewModel = viewModel
                        )
                        2 -> FilmographyScreen(
                            movies = completedMovies
                        )
                        3 -> TrophyScreen(
                            save = state,
                            movies = completedMovies
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewStudioScreen(onStart: (String) -> Unit) {
    var studioName by remember { mutableStateOf("") }
    val sampleNames = listOf("Sunset Boulevard Pictures", "CineMagnate Prods", "Starlight Syndicate", "Metropolis Movies", "IndieSphere Studios")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App decorative header
        Icon(
            imageVector = Icons.Default.MovieFilter,
            contentDescription = "Logo",
            tint = ColorCinemaGold,
            modifier = Modifier
                .size(96.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "MOVIE STUDIO TYCOON",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "From hire writer to box office collections simulator",
            fontSize = 14.sp,
            color = ColorMutedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Establish Your Studio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ColorCinemaGold
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = studioName,
                    onValueChange = { studioName = it },
                    label = { Text("Studio Name") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ColorCinemaGold,
                        focusedLabelColor = ColorCinemaGold,
                        unfocusedBorderColor = ColorBorder,
                        unfocusedLabelColor = ColorMutedText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("studio_name_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Suggestions row
                Text(
                    text = "Suggested names:",
                    fontSize = 12.sp,
                    color = ColorMutedText,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sampleNames.forEach { name ->
                        AssistChip(
                            onClick = { studioName = name },
                            label = { Text(name) },
                            shape = RoundedCornerShape(50.dp),
                            border = AssistChipDefaults.assistChipBorder(borderColor = ColorBorder, enabled = true),
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = Color.White,
                                containerColor = ColorSurfaceDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { if (studioName.isNotBlank()) onStart(studioName) },
                    enabled = studioName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorCinemaGold,
                        disabledContainerColor = ColorBorder.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_studio_button")
                ) {
                    Text(
                        text = "GREENLIGHT MY LABELS",
                        fontWeight = FontWeight.Bold,
                        color = ColorTextOnLilac
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    save: StudioSave,
    completedCount: Int,
    onStartNewMovie: () -> Unit
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // App top header block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = save.name.uppercase(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Calendar Year: Week ${save.timelineWeek}".uppercase(),
                    fontSize = 11.sp,
                    color = ColorCinemaGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.0.sp
                )
            }
            // Reputation Tag
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Stars, contentDescription = "Stars", tint = ColorCinemaGold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REP: ${(save.reputation * 10).toInt()}/10",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main KPIs cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Money KPI (Using MoneyPink font highlight)
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, contentDescription = "Funds", tint = ColorMoneyPink, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("BUDGET", fontSize = 11.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = formatter.format(save.money),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = ColorMoneyPink, // Styled in premium Pink!
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Fans KPI
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, contentDescription = "Fans", tint = ColorCinemaGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("STUDIO FANS", fontSize = 11.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = NumberFormat.getNumberInstance(Locale.US).format(save.fans),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large CTA banner / Movie Poster Active Card
        if (save.currentStage == ActiveProjectStage.NONE) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, ColorBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Idea",
                        tint = ColorCinemaGold,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "STUDIO DESK VACANT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Ready to build your next blockbuster screenplay? Hire a screenwriter to script your visions.",
                        color = ColorMutedText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = onStartNewMovie,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("commission_new_button")
                    ) {
                        Text("⚙ COMMISSION NEW SCRIPT", color = ColorTextOnLilac, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Immersive Active Project gradient poster card mirroring HTML layout
            val gradientBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF4F378B), Color(0xFF21005D)),
                startY = 0.0f
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .border(BorderStroke(1.dp, ColorCinemaGold.copy(alpha = 0.2f)), RoundedCornerShape(32.dp))
                    .shadow(16.dp, RoundedCornerShape(32.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(gradientBrush)
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .background(ColorLabelLilac, RoundedCornerShape(50.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = save.activeGenre.displayName.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTextOnLilac
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = save.activeTitle.uppercase(),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    letterSpacing = (-1).sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF81C784))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Production: ${save.currentStage.name.replace("_", " ")}",
                                        color = ColorNeonCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "MARKETING HYPE",
                                    color = ColorCinemaGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${save.accumulatedHype.toInt()}",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val progressValue = when (save.currentStage) {
                            ActiveProjectStage.WRITING -> save.scriptWriteProgress / 100f
                            ActiveProjectStage.SHOOTING -> (save.shootProgressWeek + 1).toFloat() / save.shootTotalWeeks.toFloat()
                            ActiveProjectStage.PRE_PRODUCTION -> 0.25f
                            ActiveProjectStage.POST_PRODUCTION -> 0.75f
                            ActiveProjectStage.REVIEWS -> 0.9f
                            ActiveProjectStage.RELEASE_RUN -> save.boxOfficeRunWeek.toFloat() / save.boxOfficeTotalWeeks.toFloat()
                            else -> 0f
                        }.coerceIn(0f, 1f)

                        // Glowing Progress Bar
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                                    .background(ColorBorder)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progressValue)
                                        .fillMaxHeight()
                                        .background(ColorCinemaGold)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onStartNewMovie,
                                colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "GO TO PRODUCTION DESK ⚡",
                                    color = ColorTextOnLilac,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Scrolling Hollywood Gossip/Feed News
        Text(
            text = "HOLLYWOOD BEAT NEWS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = ColorCinemaGold,
            letterSpacing = 1.0.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val reports = listOf(
                    "📰 Quentin Tarantin-no seen throwing salt shakers during a script argument.",
                    "📈 Theatrical market sees 15% increase in Sci-Fi attendance this quarter.",
                    "🔥 Fans are demanding more Drama releases - is the action-fever dying out?",
                    "🎟 Steven Spielbug's latest picture breaks a 4-week weekend record.",
                    "🤔 Are AI scriptwriters the future? Indie McScreen says 'We have nothing to fear.'"
                )
                reports.forEach { report ->
                    Text(
                        text = report,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    HorizontalDivider(color = ColorBorder.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun ProductionDeskScreen(
    save: StudioSave,
    viewModel: GameViewModel
) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    
    when (save.currentStage) {
        ActiveProjectStage.NONE -> {
            Step1CreateScriptFlow(save, viewModel)
        }
        ActiveProjectStage.WRITING -> {
            Step2ScriptWritingFlow(save, viewModel)
        }
        ActiveProjectStage.PRE_PRODUCTION -> {
            Step3CastingAndBudgetFlow(save, viewModel)
        }
        ActiveProjectStage.SHOOTING -> {
            Step4ShootingStageFlow(save, viewModel)
        }
        ActiveProjectStage.POST_PRODUCTION -> {
            Step5PostReleaseFlow(save, viewModel)
        }
        ActiveProjectStage.REVIEWS -> {
            Step6ReviewFlow(save, viewModel)
        }
        ActiveProjectStage.RELEASE_RUN -> {
            Step7TheatricalBoxOfficeRunFlow(save, viewModel)
        }
    }
}

@Composable
fun Step1CreateScriptFlow(save: StudioSave, viewModel: GameViewModel) {
    var title by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf(MovieGenre.ACTION) }
    var hiredWriterId by remember { mutableStateOf("") }

    val adjectives = listOf("Redundant", "Silent", "Symphony of", "Legend of", "Tomb of", "The Electric", "Lost Files of", "Cyberpunk", "Haunted", "Chasing")
    val nouns = listOf("Wolverine", "Secret", "Moonlight", "Slayer", "Chronicles", "Galaxy", "Revenge", "Love", "Shadows", "Highway")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "COMMISSION SCREENPLAY",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorCinemaGold
            )
            Text(
                text = "Pick Genre, Title & Hired Screenplay Writer",
                fontSize = 12.sp,
                color = ColorMutedText
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Movie Title Setup", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Movie Title") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ColorCinemaGold,
                            unfocusedBorderColor = ColorBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { title = "${adjectives.random()} ${nouns.random()}" },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorSurfaceDark.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, ColorCinemaGold)
                    ) {
                        Text("🎲 GENERATE TITLE", fontSize = 11.sp, color = ColorCinemaGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Choose Genre Focus", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MovieGenre.entries.forEach { genre ->
                            FilterChip(
                                selected = selectedGenre == genre,
                                onClick = { selectedGenre = genre },
                                label = { Text(genre.displayName) },
                                shape = RoundedCornerShape(50.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ColorCinemaGold,
                                    selectedLabelColor = ColorTextOnLilac,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Text("Hire a Screenplay Writer", fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(Screenwriter.ALL) { writer ->
            val formatter = NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }
            val isSelected = hiredWriterId == writer.id
            val isAffordable = save.money >= writer.cost

            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) ColorCinemaGold else ColorBorder),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) ColorSurfaceDark.copy(alpha = 0.8f) else ColorSurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { hiredWriterId = writer.id }
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1.0f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(writer.name, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ColorCinemaGold.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "SKILL: ${(writer.skill * 10).toInt()}/10",
                                    fontSize = 10.sp,
                                    color = ColorCinemaGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(writer.bio, fontSize = 11.sp, color = ColorMutedText, modifier = Modifier.padding(top = 4.dp))
                        Text(
                            "Specialty: ${writer.favoriteGenre.displayName} (Writing Speed: x${writer.speed})",
                            fontSize = 11.sp,
                            color = ColorNeonCyan,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatter.format(writer.cost),
                            color = if (isAffordable) ColorMoneyPink else ColorCinemaRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (!isAffordable) {
                            Text("Too costly", fontSize = 9.sp, color = ColorCinemaRed)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.selectWriterAndNewMovie(title, selectedGenre, hiredWriterId) },
                enabled = title.isNotBlank() && hiredWriterId.isNotBlank() && (save.money >= (Screenwriter.ALL.firstOrNull { it.id == hiredWriterId }?.cost ?: 0.0)),
                colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaGold, disabledContainerColor = ColorBorder.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("commission_writing_step_button")
            ) {
                Text(
                    text = "DRAFT CONTRACT & PAY COMMISSION",
                    color = ColorTextOnLilac,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Step2ScriptWritingFlow(save: StudioSave, viewModel: GameViewModel) {
    val writer = Screenwriter.ALL.firstOrNull { it.id == save.activeWriterId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.BorderColor, contentDescription = "Writing", tint = ColorCinemaGold, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "DRAFTING SCREENPLAY",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White
        )
        Text(
            text = "'${save.activeTitle}'",
            fontSize = 14.sp,
            color = ColorCinemaGold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Writer Assigned: ${writer.name}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { save.scriptWriteProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = ColorCinemaGold,
                    trackColor = ColorBorder
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "${save.scriptWriteProgress.toInt()}% Finished",
                    color = ColorCinemaGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.advanceScriptWriting() },
            colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaGold),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("advance_script_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timeline, contentDescription = "Clapper", tint = ColorTextOnLilac)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "COMPILE WRITING ADVANCEMENT 🎬",
                    fontWeight = FontWeight.Bold,
                    color = ColorTextOnLilac
                )
            }
        }
    }
}

@Composable
fun Step3CastingAndBudgetFlow(save: StudioSave, viewModel: GameViewModel) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    
    var selectedDirectorId by remember { mutableStateOf("") }
    var selectedLeadId by remember { mutableStateOf("") }
    var selectedSupportId by remember { mutableStateOf("") }

    // Budget sliders
    var vfxBudget by remember { mutableDoubleStateOf(200_000.0) }
    var prodBudget by remember { mutableDoubleStateOf(500_000.0) }
    var mktBudget by remember { mutableDoubleStateOf(300_000.0) }

    val directorCost = Director.ALL.firstOrNull { it.id == selectedDirectorId }?.cost ?: 0.0
    val leadCost = Actor.ALL.firstOrNull { it.id == selectedLeadId }?.cost ?: 0.0
    val supportCost = Actor.ALL.firstOrNull { it.id == selectedSupportId }?.cost ?: 0.0
    val totalDirectSpend = directorCost + leadCost + supportCost + vfxBudget + prodBudget + mktBudget
    val isAffordable = save.money >= totalDirectSpend

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "PRE-PRODUCTION GREENLIGHT",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorCinemaGold
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Film Screenplay Quality: ${save.screenplayQuality.toInt()}/100",
                    fontSize = 12.sp,
                    color = ColorNeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Studio Cash: ${formatter.format(save.money)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorNeonGreen
                )
            }
        }

        // 1. Director Block
        item {
            Text("Assign Movie Director", fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(Director.ALL) { dir ->
            val isSelected = selectedDirectorId == dir.id
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isSelected) ColorCinemaGold.copy(alpha = 0.1f) else ColorSurfaceDark),
                border = BorderStroke(if (isSelected) 1.5.dp else 0.dp, ColorCinemaGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedDirectorId = dir.id }
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dir.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Style: ${dir.style} | Fav: ${dir.favoriteGenre.displayName}", fontSize = 11.sp, color = ColorMutedText)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatter.format(dir.cost), fontWeight = FontWeight.Bold, color = ColorCinemaGold, fontSize = 12.sp)
                        Text("SKILL: ${(dir.skill * 10).toInt()}/10", fontSize = 10.sp, color = ColorNeonCyan)
                    }
                }
            }
        }

        // 2. Lead Actor Block
        item {
            Text("Cast Lead Star", fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(Actor.ALL) { act ->
            val isSelected = selectedLeadId == act.id
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isSelected) ColorCinemaGold.copy(alpha = 0.1f) else ColorSurfaceDark),
                border = BorderStroke(if (isSelected) 1.5.dp else 0.dp, ColorCinemaGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedLeadId = act.id }
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(act.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(act.roleType, fontSize = 11.sp, color = ColorMutedText)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatter.format(act.cost), fontWeight = FontWeight.Bold, color = ColorCinemaGold, fontSize = 12.sp)
                        Text("POWER: ${(act.starPower * 10).toInt()}/10", fontSize = 10.sp, color = ColorNeonCyan)
                    }
                }
            }
        }

        // 3. Supporting Actor Block
        item {
            Text("Cast Supporting Star", fontWeight = FontWeight.Bold, color = Color.White)
        }

        items(Actor.ALL) { act ->
            val isSelected = selectedSupportId == act.id
            Card(
                colors = CardDefaults.cardColors(containerColor = if (isSelected) ColorCinemaGold.copy(alpha = 0.1f) else ColorSurfaceDark),
                border = BorderStroke(if (isSelected) 1.5.dp else 0.dp, ColorCinemaGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedSupportId = act.id }
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(act.name, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(act.roleType, fontSize = 11.sp, color = ColorMutedText)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(formatter.format(act.cost), fontWeight = FontWeight.Bold, color = ColorCinemaGold, fontSize = 12.sp)
                        Text("SKILL: ${(act.skill * 10).toInt()}/10", fontSize = 10.sp, color = ColorNeonCyan)
                    }
                }
            }
        }

        // 4. Production Resource slider allocations
        item {
            Text("Production Resource Slider investments", fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CGI & Visual Effects: ${formatter.format(vfxBudget)}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    Slider(
                        value = vfxBudget.toFloat(),
                        onValueChange = { vfxBudget = it.toDouble() },
                        valueRange = 50_000f..4_000_000f,
                        colors = SliderDefaults.colors(thumbColor = ColorCinemaGold, activeTrackColor = ColorCinemaGold)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("On-Set Materials & Gear: ${formatter.format(prodBudget)}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    Slider(
                        value = prodBudget.toFloat(),
                        onValueChange = { prodBudget = it.toDouble() },
                        valueRange = 100_000f..5_000_000f,
                        colors = SliderDefaults.colors(thumbColor = ColorCinemaGold, activeTrackColor = ColorCinemaGold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Initial Hype Marketing Campaign: ${formatter.format(mktBudget)}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    Slider(
                        value = mktBudget.toFloat(),
                        onValueChange = { mktBudget = it.toDouble() },
                        valueRange = 50_000f..3_000_000f,
                        colors = SliderDefaults.colors(thumbColor = ColorCinemaGold, activeTrackColor = ColorCinemaGold)
                    )
                }
            }
        }

        // 5. Greenlight Bottom Block
        item {
            Card(colors = CardDefaults.cardColors(containerColor = if (isAffordable) ColorSurfaceDark else ColorCinemaRed.copy(alpha = 0.1f))) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL ESTIMATED DUES:", fontSize = 11.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                        Text(
                            text = formatter.format(totalDirectSpend),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isAffordable) ColorNeonGreen else ColorCinemaRed
                        )
                    }
                    if (!isAffordable) {
                        Text(
                            text = "Insufficient studio reserves. Reduce salaries or variable sliders.",
                            color = ColorCinemaRed,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.finalizeCastingAndBudget(
                        selectedDirectorId,
                        selectedLeadId,
                        selectedSupportId,
                        vfxBudget,
                        prodBudget,
                        mktBudget
                    )
                },
                enabled = selectedDirectorId.isNotBlank() && selectedLeadId.isNotBlank() && selectedSupportId.isNotBlank() && isAffordable,
                colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaGold, disabledContainerColor = ColorMutedText.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("greenlight_production_button")
            ) {
                Text("🎬 ROLL CAMERA & START PRODUCTION", color = ColorDeepSlate, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Step4ShootingStageFlow(save: StudioSave, viewModel: GameViewModel) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    
    // Retrieve actors
    val director = Director.ALL.firstOrNull { it.id == save.activeDirectorId }
    val lead = Actor.ALL.firstOrNull { it.id == save.activeLeadActorId }

    // Hardcoded week scenarios descriptions matching ViewModel events index
    val scenarios = listOf(
        Pair(
            "Star Tantrum Escalation!",
            "Your lead actor, ${lead?.name ?: "Top Star"}, is completely refusing to come out of their luxury trailer. They demand a personalized gold coffee machine."
        ),
        Pair(
            "Stunt Safety Coordinator Dilemma",
            "The director, ${director?.name ?: "Director"}, wants to run a risky high-wire explosion sequence. Insurance is complaining about safety precautions."
        ),
        Pair(
            "Visual Materials Leaked",
            "An executive producer accidentally posted private scene stills of your film on social media. It has gone completely viral!"
        ),
        Pair(
            "Post-Hype Revision Brawl",
            "The screenwriters and editing staff are having a loud shouting match in the studios concerning the tone of the final ending sequence."
        )
    )

    val currentScenario = scenarios.getOrNull(save.shootProgressWeek) ?: Pair("On the Set", "Everything is moving steadily forwards on the filming lot.")

    val choiceLabels = when (save.shootProgressWeek) {
        0 -> Pair("👑 Pamper the actor with a Gold Coffee Machine (-$75k, +10 Hype, +5 Quality)", "⚖ Crack the whip & threat contract penalties (Free, -10 Quality, +5 Hype)")
        1 -> Pair("🛡 Safety Rewrite tone down the stunt (Free, -5 Quality)", "🚀 Hire special safety guards & double stunt cost (-$120k, +15 Quality, +15 Hype)")
        2 -> Pair("🕸 Ride the viral wave (Free, +25 Hype, -5 Quality)", "🛡 Enforce copyright takedown (-$50k, +10 Quality)")
        3 -> Pair("🎬 Hand over complete Creative Control (Free, +12 Quality, -5 Hype)", "🎞 Force studio ending & polish CGI sound effects (-$40k, +5 Quality, +15 Hype)")
        else -> Pair("Continue", "Skip option")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FILMING IN PROGRESS",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorCinemaGold
        )
        Text(
            text = "'${save.activeTitle}'",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Progress bar
        Card(colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Shoot Week: ${save.shootProgressWeek + 1} / ${save.shootTotalWeeks}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Qual Boost: ${save.accumulatedFilmQuality.toInt()}",
                        fontSize = 11.sp,
                        color = ColorNeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (save.shootProgressWeek + 1).toFloat() / save.shootTotalWeeks.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = ColorCinemaGold,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }

        // Active production set decorative drawing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.radialGradient(listOf(ColorSurfaceDark, ColorDeepSlate)))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CameraRoll,
                    contentDescription = "Tape",
                    tint = ColorCinemaGold.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "ROLLING! CAMERA SETS ARE LIVE",
                    fontSize = 10.sp,
                    color = ColorCinemaGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Current Scenario Card
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Announcement, contentDescription = "Event", tint = ColorCinemaRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentScenario.first,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = ColorCinemaRed
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = currentScenario.second,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("DUE ACTIONS:", fontSize = 11.sp, color = ColorMutedText, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))

        // Option Button 1
        Button(
            onClick = { viewModel.handleShootingWeekOptionSelected(0) },
            colors = ButtonDefaults.buttonColors(containerColor = ColorSurfaceDark),
            border = BorderStroke(1.dp, ColorCinemaGold.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shoot_option_1_button")
        ) {
            Text(
                text = choiceLabels.first,
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Option Button 2
        Button(
            onClick = { viewModel.handleShootingWeekOptionSelected(1) },
            colors = ButtonDefaults.buttonColors(containerColor = ColorSurfaceDark),
            border = BorderStroke(1.dp, ColorCinemaGold.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shoot_option_2_button")
        ) {
            Text(
                text = choiceLabels.second,
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun Step5PostReleaseFlow(save: StudioSave, viewModel: GameViewModel) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    
    var selectedSeason by remember { mutableStateOf("Summer") }
    var selectedTier by remember { mutableStateOf("Wide") }
    var extraPolishSelection by remember { mutableIntStateOf(0) } // 0: None, 1: Deluxe, 2: IMAX

    val polishCost = when (extraPolishSelection) {
        1 -> 250_000.0
        2 -> 750_000.0
        else -> 0.0
    }

    val isAffordable = save.money >= polishCost

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "POST PRODUCTION & DISTRIBUTION",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorCinemaGold
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Film Cut Quality: ${save.accumulatedFilmQuality.toInt()}/100",
                    fontSize = 12.sp,
                    color = ColorNeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hype Gauge: ${save.accumulatedHype.toInt()}/100",
                    fontSize = 12.sp,
                    color = ColorCinemaGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 1. Post Polish Options
        item {
            Text("Invest in Post-Production Polish", fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Option 0
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (extraPolishSelection == 0) ColorCinemaGold.copy(alpha = 0.1f) else ColorSurfaceDark),
                    border = BorderStroke(if (extraPolishSelection == 0) 1.5.dp else 0.dp, ColorCinemaGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { extraPolishSelection = 0 }
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = extraPolishSelection == 0, onClick = { extraPolishSelection = 0 })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text("Standard Cut (No Extra Cost)", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("No quality boost, proceed to printing.", fontSize = 11.sp, color = ColorMutedText)
                        }
                    }
                }

                // Option 1
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (extraPolishSelection == 1) ColorCinemaGold.copy(alpha = 0.1f) else ColorSurfaceDark),
                    border = BorderStroke(if (extraPolishSelection == 1) 1.5.dp else 0.dp, ColorCinemaGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { extraPolishSelection = 1 }
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = extraPolishSelection == 1, onClick = { extraPolishSelection = 1 })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Deluxe VFX & CGI Polish", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Polish sound design and visual rendering.", fontSize = 11.sp, color = ColorMutedText)
                        }
                        Text("-$250k\n+6 Qual", fontSize = 11.sp, color = ColorNeonCyan, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    }
                }

                // Option 2
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (extraPolishSelection == 2) ColorCinemaGold.copy(alpha = 0.1f) else ColorSurfaceDark),
                    border = BorderStroke(if (extraPolishSelection == 2) 1.5.dp else 0.dp, ColorCinemaGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { extraPolishSelection = 2 }
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = extraPolishSelection == 2, onClick = { extraPolishSelection = 2 })
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Ultimate IMAX Conversion", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Add an OST from a live orchestra.", fontSize = 11.sp, color = ColorMutedText)
                        }
                        Text("-$750k\n+15 Qual", fontSize = 11.sp, color = ColorNeonCyan, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    }
                }
            }
        }

        // 2) Release seasons options
        item {
            Text("Select Release Window Season", fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val seasons = listOf("Summer" to "☀️", "Holiday" to "🎄", "Oscar" to "🏆", "Dump" to "🍂")
                seasons.forEach { (codename, emoji) ->
                    val isSelected = selectedSeason == codename
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) ColorCinemaGold else ColorSurfaceDark),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSeason = codename }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(emoji, fontSize = 20.sp)
                            Text(codename, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) ColorDeepSlate else Color.White)
                        }
                    }
                }
            }
        }

        // Seasonal Details Card
        item {
            val description = when (selectedSeason) {
                "Summer" -> "🚨 MASSIVE BLOCKBUSTER WINDOW: High traffic, extreme competition penalty! Successful films gross double (x2.0), flops decay instantly."
                "Holiday" -> "🎅 HARVEST PLAY WINDOW: Strong family/holiday audiences. solid reliable multipliers (x1.4) across all general release tiers."
                "Oscar" -> "🍿 ARTISTIC ACCLAIM: Perfect for critically acclaimed movies (IMDb / critic bias +10%). Multipliers x1.0, high awards Sweep odds!"
                else -> "🍂 DUST MONTHS (Jan/Sep): Minimal competition. Very low ticket sales ceilings (x0.6), but virtually zero danger of record-breaking flops."
            }
            Card(colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark)) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = ColorCinemaGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(description, fontSize = 11.sp, color = Color.White, lineHeight = 16.sp)
                }
            }
        }

        // 3) Distribution Tiers
        item {
            Text("Select Theatrical Distribution Tier", fontWeight = FontWeight.Bold, color = Color.White)
        }

        item {
            val tiers = listOf(
                Pair("Wide", "🔥 Big Wide release (3000 screens). Max hype capacity, high fees."),
                Pair("Limited", "🎟 Indie theaters (500 screens). Safe, lower overhead costs."),
                Pair("Festival", "📽 Film Circuit (50 screens). High reputation and critic visibility."),
                Pair("Streaming", "🖥 Direct-to-Streaming. Flat distribution returns, absolute safe margin.")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tiers.forEach { (name, label) ->
                    val isSelected = selectedTier == name
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) ColorCinemaGold.copy(alpha = 0.1f) else ColorSurfaceDark),
                        border = BorderStroke(if (isSelected) 1.5.dp else 0.dp, ColorCinemaGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTier = name }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(name, fontWeight = FontWeight.Bold, color = if (isSelected) ColorCinemaGold else Color.White)
                            Text(label, fontSize = 11.sp, color = ColorMutedText, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }

        item {
            if (!isAffordable) {
                Text(
                    text = "Insufficient funds for chosen post-production polish.",
                    color = ColorCinemaRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.deployMovieToTheaters(selectedSeason, selectedTier, extraPolishSelection) },
                enabled = isAffordable,
                colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaGold, disabledContainerColor = ColorMutedText.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("deploy_movie_button")
            ) {
                Text(
                    text = "🚀 PUBLISH FILM & ROLL OUT COPIES",
                    color = ColorDeepSlate,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Step6ReviewFlow(save: StudioSave, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎬 OPENING PREMIERE NIGHT",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorCinemaGold
        )

        Text(
            text = "'${save.activeTitle}'",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        // Rotten Tomatoes circular gauge comparison
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quality score card
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                modifier = Modifier.size(110.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("FILM QUALITY", fontSize = 10.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                    Text(
                        "${save.finalMovieQuality}%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (save.finalMovieQuality >= 65) ColorNeonGreen else ColorCinemaRed
                    )
                }
            }

            // Rotten tomatoes style critic index card
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                modifier = Modifier.size(110.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("CRITIC REVIEW", fontSize = 10.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                    Text(
                        "${save.finalMovieReviewScore}%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (save.finalMovieReviewScore >= 60) ColorNeonGreen else ColorCinemaRed
                    )
                }
            }
        }

        // IMDb rating badge
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.StarRate, contentDescription = "Star", tint = ColorCinemaGold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "IMDb RATING: ${save.finalMovieImdb} / 10.0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        // Critic Comments Bubble
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, ColorCinemaGold.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "THE DAILY REEL REVIEW:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ColorCinemaGold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${save.criticCommentSummary}\"",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.proceedToTheatricalRun() },
            colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("init_box_office_button")
        ) {
            Text(
                "LAUNCH THEATRICAL DISTRIBUTIONS 🎟",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun Step7TheatricalBoxOfficeRunFlow(save: StudioSave, viewModel: GameViewModel) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    
    val weekIndex = save.boxOfficeRunWeek
    val totalPlayWeeks = save.boxOfficeTotalWeeks
    val isFinished = weekIndex >= totalPlayWeeks

    val weeklyGrossList = remember(save.boxOfficeWeeklyGrossHistoryJson) {
        try {
            save.boxOfficeWeeklyGrossHistoryJson.split(",").map { it.toDoubleOrNull() ?: 0.0 }
        } catch (e: Exception) {
            emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🎟 THEATRICAL BOX OFFICE RUN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorCinemaGold
        )

        // Film Title Panel
        Card(colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CURRENT PLAYING:", fontSize = 10.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                Text(save.activeTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Genre: ${save.activeGenre.displayName}", fontSize = 11.sp, color = ColorCinemaGold)
                    Text("Quality: ${save.finalMovieQuality}%", fontSize = 11.sp, color = ColorNeonCyan)
                }
            }
        }

        // Large running cumulative collector score
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CUMULATIVE BOX OFFICE RECEIPTS", fontSize = 11.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formatter.format(save.boxOfficeRunningCumulative),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorNeonGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Theater Screen count: ${save.boxOfficeScreens}", fontSize = 11.sp, color = Color.White)
                    Text("Theatrical Play: Week ${weekIndex} / $totalPlayWeeks", fontSize = 11.sp, color = ColorCinemaGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Custom Canvas Visualizer Line Chart representing weekly collection trend
        Text("Weekly collection trend", fontSize = 13.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
        
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    // Draw grid baseline
                    drawLine(
                        color = Color.White.copy(alpha = 0.15f),
                        start = Offset(0f, height),
                        end = Offset(width, height),
                        strokeWidth = 2f
                    )

                    if (weeklyGrossList.isNotEmpty()) {
                        val maxGross = weeklyGrossList.maxOrNull()?.coerceAtLeast(10_000.0) ?: 10_000.0
                        val points = weeklyGrossList.mapIndexed { idx, value ->
                            val x = if (totalPlayWeeks > 1) {
                                idx.toFloat() / (totalPlayWeeks - 1) * width
                            } else {
                                0f
                            }
                            // scale to height with 10% margins padding
                            val y = height - (value.toFloat() / maxGross.toFloat() * height * 0.85f)
                            Offset(x, y)
                        }

                        // Draw path line
                        val linePath = Path().apply {
                            if (points.isNotEmpty()) {
                                moveTo(points[0].x, points[0].y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }
                        }

                        drawPath(
                            path = linePath,
                            color = ColorCinemaGold,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )

                        // Draw circle nodes on indices
                        points.forEachIndexed { i, offset ->
                            if (weeklyGrossList.getOrNull(i) ?: 0.0 > 0.0) {
                                drawCircle(
                                    color = ColorCinemaRed,
                                    radius = 6f,
                                    center = offset
                                )
                            }
                        }
                    }
                }
            }
        }

        // List of past weeks results
        Card(colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark.copy(alpha = 0.5f))) {
            Column(modifier = Modifier.padding(16.dp)) {
                weeklyGrossList.forEachIndexed { idx, grossVal ->
                    if (idx < weekIndex || grossVal > 0.0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Theatrical Week #${idx + 1}:", fontSize = 12.sp, color = Color.White)
                            Text(
                                if (grossVal == 0.0) "Running..." else formatter.format(grossVal),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (grossVal == 0.0) ColorCinemaGold else Color.White
                            )
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                    }
                }
            }
        }

        // Live Action during Box Office Play Run
        if (!isFinished) {
            Card(colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "💥 ACTIVE CAMPAIGN BOOST",
                        fontSize = 11.sp,
                        color = ColorCinemaGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Purchase recursive commercial television trend clips to boost current weekly returns by +35%.",
                        fontSize = 11.sp,
                        color = Color.White,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.runMiniReleaseMarketingHypePush() },
                        enabled = save.money >= 75_000.0,
                        colors = ButtonDefaults.buttonColors(containerColor = ColorCinemaGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🚀 INJECT HYPE (-$75,000)", color = ColorDeepSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Progression buttons
        Button(
            onClick = { viewModel.advanceTheatricalWeeklyPlay() },
            colors = ButtonDefaults.buttonColors(containerColor = if (isFinished) ColorNeonGreen else ColorCinemaRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("advance_box_office_week_button")
        ) {
            Text(
                text = if (isFinished) "🎉 CONCLUDE RUN & COUNT PROFITS" else "🎟 SIMULATE WEEK PLAYING ►",
                fontWeight = FontWeight.Bold,
                color = if (isFinished) ColorDeepSlate else Color.White
            )
        }
    }
}

@Composable
fun FilmographyScreen(movies: List<ReleasedMovie>) {
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🎬 STUDIO ARCHIVES & FILMOLOGY",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorCinemaGold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (movies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.HistoryEdu, contentDescription = "None", tint = ColorMutedText, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No films records in library.", color = ColorMutedText, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, ColorBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = "IMDb", tint = ColorCinemaGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        "${item.imdbRating}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Text(
                                "Genre: ${item.genre} | Director: ${item.directorName}",
                                fontSize = 11.sp,
                                color = ColorCinemaGold,
                                modifier = Modifier.padding(top = 2.dp)
                              )

                              HorizontalDivider(
                                  color = ColorBorder.copy(alpha = 0.5f),
                                  modifier = Modifier.padding(vertical = 10.dp)
                              )

                              Row(
                                  modifier = Modifier.fillMaxWidth(),
                                  horizontalArrangement = Arrangement.SpaceBetween
                              ) {
                                  Column {
                                      Text("PROD BUDGET:", fontSize = 9.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                                      Text(formatter.format(item.totalBudget), fontSize = 12.sp, color = Color.White)
                                  }
                                  Column {
                                      Text("BOX OFFICE TOTAL:", fontSize = 9.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                                      Text(formatter.format(item.finalBoxOffice), fontSize = 12.sp, color = Color.White)
                                  }
                                  Column(horizontalAlignment = Alignment.End) {
                                      Text("STUDIO NET ROI:", fontSize = 9.sp, color = ColorMutedText, fontWeight = FontWeight.Bold)
                                      Text(
                                          text = formatter.format(item.totalProfit),
                                          fontSize = 12.sp,
                                          fontWeight = FontWeight.Bold,
                                          color = if (item.totalProfit >= 0) ColorNeonGreen else ColorCinemaRed
                                      )
                                  }
                              }

                              // Shows cute badges if won awards
                              if (item.awardNominated || item.awardWon) {
                                  Spacer(modifier = Modifier.height(10.dp))
                                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                      if (item.awardWon) {
                                          Card(
                                              colors = CardDefaults.cardColors(containerColor = ColorCinemaGold),
                                              shape = RoundedCornerShape(8.dp)
                                          ) {
                                              Text(
                                                  "🏆 AWARD WINNER",
                                                  fontSize = 9.sp,
                                                  fontWeight = FontWeight.Bold,
                                                  color = ColorDeepSlate,
                                                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                              )
                                          }
                                      } else {
                                          Card(
                                              colors = CardDefaults.cardColors(containerColor = ColorSurfaceDark.copy(alpha = 0.5f)),
                                              shape = RoundedCornerShape(8.dp),
                                              border = BorderStroke(1.dp, ColorBorder)
                                          ) {
                                              Text(
                                                  "🎗 AWARD NOMINEE",
                                                  fontSize = 9.sp,
                                                  fontWeight = FontWeight.Bold,
                                                  color = ColorCinemaGold,
                                                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                              )
                                          }
                                      }
                                  }
                              }
                          }
                      }
                  }
              }
          }
      }
  }

  @Composable
  fun TrophyScreen(save: StudioSave, movies: List<ReleasedMovie>) {
      val trophies = listOf(
          TrophyItem(
              title = "Indie Producer Dreamer",
              description = "Complete your first theatrical release in theaters.",
              icon = Icons.Default.FilterFrames,
              isUnlocked = movies.isNotEmpty()
          ),
          TrophyItem(
              title = "Golden Blockbuster Jackpot",
              description = "Release a hit movie grossing over $10,000,000 cumulative.",
              icon = Icons.Default.MonetizationOn,
              isUnlocked = movies.any { it.finalBoxOffice >= 10_000_000.0 }
          ),
          TrophyItem(
              title = "Academy Heavyweight Sweep",
              description = "Win a verified grand film festival top award.",
              icon = Icons.Default.EmojiEvents,
              isUnlocked = movies.any { it.awardWon }
          ),
          TrophyItem(
              title = "Critical Darling Milestone",
              description = "Record a film quality score rating of 85% or higher.",
              icon = Icons.Default.ThumbUp,
              isUnlocked = movies.any { it.filmQuality >= 85 }
          ),
          TrophyItem(
              title = "Hollywood Mogul Dynasty",
              description = "Maintain a stellar 8/10 active studio reputation.",
              icon = Icons.Default.MilitaryTech,
              isUnlocked = save.reputation >= 0.8f
          )
      )

      Column(
          modifier = Modifier
              .fillMaxSize()
              .padding(16.dp)
      ) {
          Text(
              text = "🏆 STUDIO TROPHY HALL",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = ColorCinemaGold,
              modifier = Modifier.padding(bottom = 12.dp)
          )

          LazyColumn(
              verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
              items(trophies) { trophy ->
                  Card(
                      colors = CardDefaults.cardColors(
                          containerColor = if (trophy.isUnlocked) ColorSurfaceDark else ColorSurfaceDark.copy(alpha = 0.4f)
                      ),
                      shape = RoundedCornerShape(24.dp),
                      border = BorderStroke(1.dp, ColorBorder)
                  ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (trophy.isUnlocked) ColorCinemaGold.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = trophy.icon,
                                contentDescription = "",
                                tint = if (trophy.isUnlocked) ColorCinemaGold else Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trophy.title,
                                fontWeight = FontWeight.Bold,
                                color = if (trophy.isUnlocked) Color.White else Color.White.copy(alpha = 0.4f),
                                fontSize = 15.sp
                            )
                            Text(
                                text = trophy.description,
                                color = if (trophy.isUnlocked) ColorMutedText else ColorMutedText.copy(alpha = 0.4f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        if (trophy.isUnlocked) {
                            Text(
                                "UNLOCKED",
                                fontSize = 10.sp,
                                color = ColorNeonGreen,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "LOCKED",
                                fontSize = 10.sp,
                                color = ColorCinemaRed.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

data class TrophyItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean
)
