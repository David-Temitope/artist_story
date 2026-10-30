package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.os.Build
import android.Manifest
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("ArtistStoryApp", "Fatal uncaught exception on thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
        AppImageLoader.init(this)
        enableEdgeToEdge()
        setContent {
            val gameViewModel: GameViewModel = viewModel()
            val gameState by gameViewModel.gameState.collectAsStateWithLifecycle()
            val isDark = gameState?.isDarkMode ?: true

            MyApplicationTheme(darkTheme = isDark) {
                val context = LocalContext.current

                // Show toasts reactively
                LaunchedEffect(Unit) {
                    gameViewModel.toastMessage.collectLatest { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                GameApp(viewModel = gameViewModel)
            }
        }
    }
}

@Composable
fun GameApp(viewModel: GameViewModel) {
    val email by viewModel.currentUserEmail.collectAsStateWithLifecycle()
    val state by viewModel.gameState.collectAsStateWithLifecycle()

    var showSplash by remember { mutableStateOf(true) }

    Crossfade(
        targetState = showSplash,
        animationSpec = tween(durationMillis = 1000),
        label = "SplashTransition"
    ) { splashActive ->
        if (splashActive) {
            SplashScreen(onSplashFinished = { showSplash = false })
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (email == null || state == null) {
                    LoginScreen(
                        onLogin = { userEmail, artistName ->
                            viewModel.loginOrCreateAccount(userEmail, artistName)
                        }
                    )
                } else {
                    val playerState = state!!
                    val weeklyReport by viewModel.weeklyReport.collectAsStateWithLifecycle()
                    val allPlayers by viewModel.allGameStates.collectAsStateWithLifecycle()

                    MainGameLayout(
                        state = playerState,
                        allPlayers = allPlayers,
                        weeklyReport = weeklyReport,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val logoAlpha = remember { Animatable(0f) }
    val logoOffsetY = remember { Animatable(20f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            logoOffsetY.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }
        logoAlpha.animateTo(1f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        taglineAlpha.animateTo(1.0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        delay(500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115))
            .clickable { onSplashFinished() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // Centered Logo & Tagline Container
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo Card
                Box(
                    modifier = Modifier
                        .offset(y = logoOffsetY.value.dp)
                        .graphicsLayer(alpha = logoAlpha.value)
                        .size(140.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .border(1.dp, Color(0xFF343B46).copy(alpha = logoAlpha.value), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.img_app_icon_1783749117204),
                        contentDescription = "Genesis icon",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Tagline
                Text(
                    text = "BUILD YOUR STORY. SHAPE YOUR LEGACY.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFA7AFBD),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.4.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .graphicsLayer(alpha = taglineAlpha.value)
                        .padding(horizontal = 24.dp)
                )
            }

            // Step 5b: Publisher / Studio Credit at the bottom
            Text(
                text = "BY ALPHADOMINITY",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .graphicsLayer(alpha = taglineAlpha.value)
            )
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium Stylized Logo Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.img_app_icon_1783749117204),
                    contentDescription = "Genesis icon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Genesis",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary
            )

            Text(
                text = "Your choices shape the life you build",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Premium Form card (16px corner radius)
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create a life",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryBrandColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Local save ID (stays on this phone)") },
                        placeholder = { Text("e.g. my-first-life") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp), // 12px input fields
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBrandColor,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = PrimaryBrandColor,
                            focusedContainerColor = SurfaceColor,
                            unfocusedContainerColor = SurfaceColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedPlaceholderColor = TextSecondary.copy(alpha = 0.5f),
                            unfocusedPlaceholderColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    Text(
                        text = "This is not an account login. Nothing is sent to a server.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Character name") },
                        placeholder = { Text("What should people call you?") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp), // 12px input fields
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBrandColor,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = PrimaryBrandColor,
                            focusedContainerColor = SurfaceColor,
                            unfocusedContainerColor = SurfaceColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedPlaceholderColor = TextSecondary.copy(alpha = 0.5f),
                            unfocusedPlaceholderColor = TextSecondary.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("artist_name_input")
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank() && nameInput.isNotBlank()) {
                                onLogin(emailInput, nameInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBrandColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp), // 14px buttons
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_button")
                    ) {
                        Text(
                            text = "START CAREER",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Establish your legacy in the music charts.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MainGameLayout(
    state: GameStateEntity,
    allPlayers: List<GameStateEntity>,
    weeklyReport: String,
    viewModel: GameViewModel
) {
    var selectedTab by remember { mutableStateOf("Dashboard") }
    var isPhoneOpen by remember { mutableStateOf(false) }
    var showEnergyDialog by remember { mutableStateOf(false) }
    val actionFeedback by viewModel.actionFeedback.collectAsStateWithLifecycle()

    val secondaryTabs = setOf(
        "More",
        "Gigs (Jobs)",
        "Social & Practice",
        "Marketplace",
        "Record Label",
        "Mailbox",
        "Shop & Profile"
    )

    Scaffold(
        floatingActionButton = {
            val hasUnreads = state.chatConversations.any { it.unreadCount > 0 }
            FloatingActionButton(
                onClick = {
                    viewModel.consumeEnergy(1)
                    isPhoneOpen = true
                },
                containerColor = PrimaryBrandColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("floating_smartphone_button")
            ) {
                Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = "Open Phone")
                    if (hasUnreads) {
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(DangerColor)
                                .border(1.5.dp, CardBg, CircleShape)
                        )
                    }
                }
            }
        },
        topBar = {
            Column {
                // Permanent Top Artist HUD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Artist Identity & Stage Tag
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryBrandColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.artistName.take(1).uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = state.artistName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${state.fans} fans",
                                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(TextTertiary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Wk ${state.currentWeek} · Y${state.currentYear}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                }
                            }
                        }

                        // Resource Indicators: Cash & Energy
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                                border = BorderStroke(1.dp, BorderColor),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        tint = if (state.cash < 0) Color(0xFFFF5252) else SuccessColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (state.cash < 0) "-$${String.format("%,.0f", -state.cash)}" else String.format("%,.0f", state.cash),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = if (state.cash < 0) Color(0xFFFF5252) else SuccessColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Secondary Resource Bar: Energy & Used Slots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceColor.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Energy HUD (Clickable Vitality Dashboard)
                    val energyTint = when {
                        state.energy >= 70 -> AccentGreen
                        state.energy >= 35 -> WarningColor
                        else -> DangerColor
                    }
                    val vitalityBadge = when {
                        state.energy >= 75 -> " 🔥"
                        state.energy < 25 -> " 😴"
                        else -> ""
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { showEnergyDialog = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Energy Icon",
                            tint = energyTint,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Energy: ${state.energy}/${state.maxEnergy}%$vitalityBadge",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                            color = energyTint
                        )
                    }

                    // Time Slots HUD
                    val sideJobSlots = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
                    val tourSlots = state.tours.filter { it.status == "ACTIVE" }.sumOf { it.weeklyTimeCost }
                    val ventureSlots = state.businessVentures.count { it.isOwned && it.isPlayerInControl } * 10
                    val activeSlots = sideJobSlots + tourSlots + ventureSlots

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Slots Icon",
                            tint = PrimaryBrandLight,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Slots: $activeSlots/${state.maxTimeSlots}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                            color = PrimaryBrandLight
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Permanent Single Bottom Control Center (No dual navigation bars)
            NavigationBar(
                containerColor = CardBg,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                val primaryNavItems = listOf(
                    Triple("Dashboard", Icons.Default.Home, "Home"),
                    Triple("Studio", Icons.Default.MusicNote, "Studio"),
                    Triple("Tours", Icons.Default.DirectionsBus, "Tours"),
                    Triple("Trading", Icons.Default.TrendingUp, "Market"),
                    Triple("More", Icons.Default.GridView, "More")
                )

                primaryNavItems.forEach { (tabId, icon, label) ->
                    val isSelected = if (tabId == "More") {
                        selectedTab in secondaryTabs
                    } else {
                        selectedTab == tabId
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tabId },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBrandLight,
                            selectedTextColor = PrimaryBrandLight,
                            indicatorColor = PrimaryBrandColor.copy(alpha = 0.18f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(innerPadding)
        ) {
            // Central Page Router
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                    },
                    label = "TabContent"
                ) { targetTab ->
                    when (targetTab) {
                        "Dashboard" -> DashboardPage(state, weeklyReport, viewModel, onNavigate = { selectedTab = it })
                        "Studio" -> {
                            if (state.isUnderArrest) {
                                com.example.ui.StateCustodyLockoutScreen(
                                    state = state,
                                    viewModel = viewModel,
                                    featureName = "Recording Studio Facilities",
                                    onOpenScc = {
                                        viewModel.consumeEnergy(1)
                                        isPhoneOpen = true
                                    }
                                )
                            } else {
                                StudioPage(state, viewModel)
                            }
                        }
                        "Tours" -> {
                            if (state.isUnderArrest) {
                                com.example.ui.StateCustodyLockoutScreen(
                                    state = state,
                                    viewModel = viewModel,
                                    featureName = "Concert Touring Operations",
                                    onOpenScc = {
                                        viewModel.consumeEnergy(1)
                                        isPhoneOpen = true
                                    }
                                )
                            } else {
                                EnhancedToursPage(state, viewModel)
                            }
                        }
                        "Trading" -> EnhancedTradingPage(state, viewModel)
                        "More" -> CareerHubPage(state, viewModel, onNavigate = { selectedTab = it })
                        "Gigs (Jobs)" -> Column(modifier = Modifier.fillMaxSize()) {
                            SecondaryScreenHeader(
                                title = "Gigs & Day Jobs",
                                subtitle = "Perform live shows or take day jobs to fund your career",
                                icon = Icons.Default.Work,
                                onBack = { selectedTab = "More" }
                            )
                            SideJobsPage(state, viewModel)
                        }
                        "Social & Practice" -> Column(modifier = Modifier.fillMaxSize()) {
                            SecondaryScreenHeader(
                                title = "Skills & Social Growth",
                                subtitle = "Vocal coaching, songwriting practice, and social media networking",
                                icon = Icons.Default.Share,
                                onBack = { selectedTab = "More" }
                            )
                            SocialPracticePage(state, viewModel)
                        }
                        "Marketplace" -> Column(modifier = Modifier.fillMaxSize()) {
                            SecondaryScreenHeader(
                                title = "Asset Marketplace",
                                subtitle = "Acquire luxury vehicles, real estate, instruments, and studio gear",
                                icon = Icons.Default.ShoppingCart,
                                onBack = { selectedTab = "More" }
                            )
                            MarketplacePage(state, viewModel)
                        }
                        "Record Label" -> Column(modifier = Modifier.fillMaxSize()) {
                            SecondaryScreenHeader(
                                title = "Record Label Operations",
                                subtitle = "Negotiate contracts, review royalties, and manage label relations",
                                icon = Icons.Default.Domain,
                                onBack = { selectedTab = "More" }
                            )
                            RecordLabelPage(state, viewModel)
                        }
                        "Mailbox" -> Column(modifier = Modifier.fillMaxSize()) {
                            SecondaryScreenHeader(
                                title = "Fan Mailbox",
                                subtitle = "Read fan mail, claim promotional gifts, and handle event invites",
                                icon = Icons.Default.Email,
                                onBack = { selectedTab = "More" }
                            )
                            MailboxPage(state, viewModel)
                        }
                        "Shop & Profile" -> Column(modifier = Modifier.fillMaxSize()) {
                            SecondaryScreenHeader(
                                title = "Artist Profile & Wardrobe",
                                subtitle = "Manage cosmetic styles, stage outfits, achievements, and identity",
                                icon = Icons.Default.Person,
                                onBack = { selectedTab = "More" }
                            )
                            ShopProfilePage(state, viewModel)
                        }
                        else -> DashboardPage(state, weeklyReport, viewModel, onNavigate = { selectedTab = it })
                    }
                }

                ActionFeedbackHUD(
                    feedback = actionFeedback,
                    onDismiss = { viewModel.clearActionFeedback() },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                )
            }
        }
    }

    if (isPhoneOpen) {
        SmartphoneOverlay(
            state = state,
            viewModel = viewModel,
            onDismiss = {
            isPhoneOpen = false
            if (state.activePhone == "hiPhone" || state.activePhone == "Samsong") {
                viewModel.setPhoneLocked(true)
            }
        }
        )
    }

    if (showEnergyDialog) {
        val energyPct = (state.energy.toFloat() / state.maxEnergy.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
        val statusTitle = when {
            state.energy >= 75 -> "🔥 Peak Vitality & Creative Flow"
            state.energy >= 40 -> "⚡ Normal Operating Vitality"
            state.energy >= 20 -> "😴 Sluggish / Minor Fatigue"
            else -> "⚠️ Severe Exhaustion & Vocal Strain"
        }
        val statusColor = when {
            state.energy >= 75 -> AccentGreen
            state.energy >= 40 -> WarningColor
            else -> DangerColor
        }
        AlertDialog(
            onDismissRequest = { showEnergyDialog = false },
            containerColor = Color(0xFF131826),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Bolt, null, tint = statusColor)
                    Text("Energy & Vitality", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(statusTitle, color = statusColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    LinearProgressIndicator(
                        progress = { energyPct },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = statusColor,
                        trackColor = Color(0xFF1E2638)
                    )

                    Surface(
                        color = Color(0xFF1A2234),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Current Vitality: ${state.energy} / ${state.maxEnergy}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Active Gameplay Multipliers:", color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            if (state.energy >= 70) {
                                Text("• +35% Songwriting & Vocal practice XP boost", color = AccentGreen, fontSize = 11.sp)
                                Text("• +15% Live concert crowd excitement & retention", color = AccentGreen, fontSize = 11.sp)
                                Text("• Studio song craftsmanship score bonus", color = AccentGreen, fontSize = 11.sp)
                            } else if (state.energy < 35) {
                                Text("• -35% Practice XP penalty due to fatigue", color = DangerColor, fontSize = 11.sp)
                                Text("• -25% Live concert energy & turnout penalty", color = DangerColor, fontSize = 11.sp)
                                Text("• Impaired studio recording quality & vocal strain", color = DangerColor, fontSize = 11.sp)
                            } else {
                                Text("• Standard performance across all career actions", color = TextSecondary, fontSize = 11.sp)
                            }
                            Text("• Residual Energy: Ending the week well-rested starts next Monday with peak vitality!", color = AccentGold, fontSize = 10.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restAndRecharge()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Text("Deep Rest (+Vitality)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnergyDialog = false }) {
                    Text("Close", color = Color.LightGray)
                }
            }
        )
    }

    val activeDilemma by CareerDilemmaController.activeDilemma.collectAsStateWithLifecycle()
    activeDilemma?.let { dilemma ->
        ArtistDecisionModal(
            dilemma = dilemma,
            playerBusinessSkill = state.businessSkill,
            playerIntegrity = state.integrity,
            onSelectChoice = { choice ->
                CareerDilemmaController.resolveDilemma(choice, viewModel)
            },
            onDismiss = {
                CareerDilemmaController.dismissDilemma()
            }
        )
    }
}

/**
 * Secondary Navigation Header for auxiliary career modules
 */
@Composable
fun SecondaryScreenHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onBack: () -> Unit
) {
    Surface(
        color = CardBg,
        border = BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceColor)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to More Hub",
                    tint = PrimaryBrandLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryBrandLight,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Career Hub Page presenting organized secondary systems
 */
@Composable
fun CareerHubPage(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onNavigate: (String) -> Unit
) {
    data class HubEntry(val title: String, val hint: String, val icon: ImageVector, val color: Color, val route: String)
    val entries = listOf(
        HubEntry("Gigs", "Earn from live work", Icons.Default.Work, SuccessColor, "Gigs (Jobs)"),
        HubEntry("Skills & people", "Train and network", Icons.Default.Share, CreativePurple, "Social & Practice"),
        HubEntry("Record label", "Contracts and royalties", Icons.Default.Domain, WarningColor, "Record Label"),
        HubEntry("Marketplace", "Buy useful assets", Icons.Default.ShoppingCart, PrimaryBrandLight, "Marketplace"),
        HubEntry("Fan mail", "Requests and invitations", Icons.Default.Email, InfoColor, "Mailbox"),
        HubEntry("Artist identity", "Profile and wardrobe", Icons.Default.Person, PremiumGold, "Shop & Profile")
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(PrimaryBrandColor.copy(alpha = .22f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = PrimaryBrandLight, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text("Your world", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                    Text("Choose where to spend your time", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }
        items(entries.chunked(2).size) { rowIndex ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                entries.chunked(2)[rowIndex].forEach { entry ->
                    Card(
                        modifier = Modifier.weight(1f).heightIn(min = 132.dp).clickable { onNavigate(entry.route) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.size(38.dp).clip(RoundedCornerShape(13.dp)).background(entry.color.copy(alpha = .16f)), contentAlignment = Alignment.Center) {
                                Icon(entry.icon, contentDescription = null, tint = entry.color, modifier = Modifier.size(21.dp))
                            }
                            Text(entry.title, color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(entry.hint, color = TextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                        }
                    }
                }
                if (entries.chunked(2)[rowIndex].size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item { Text("The choices you make here can affect your money, career, and relationships.", color = TextTertiary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 2.dp)) }
    }
}

// --- TAB PAGES ---

@Composable
fun DashboardPage(
    state: GameStateEntity,
    weeklyReport: String,
    viewModel: GameViewModel,
    onNavigate: (String) -> Unit = {}
) {
    var selectedDashboardTab by remember { mutableStateOf("Overview") }
    var showWeeklyTransitionDialog by remember { mutableStateOf(false) }
    val overviewListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Use actual saved weekly cash movement. Lifetime streams and current venture projections
    // cannot reliably predict the next simulated week, which includes variable income and costs.
    val recentCashFlowSummary = remember(state.financialHistoryCashFlow) {
        RecentCashFlowSummaryCalculator.summarize(state.financialHistoryCashFlow)
    }
    val calculatedWeeklyFans = remember(state) {
        val releasedSongs = state.songs.filter { it.isReleased }
        if (releasedSongs.isEmpty()) 0L else (state.popularity * 18).toLong().coerceAtLeast(0L)
    }
    val calculatedWeeklyListeners = remember(state) {
        val releasedSongs = state.songs.filter { it.isReleased }
        if (releasedSongs.isEmpty()) {
            0L
        } else {
            val totalStreams = releasedSongs.sumOf { it.totalStreams }
            val avgRating = releasedSongs.map { it.rating }.average().toFloat()
            val qualityMultiplier = when {
                avgRating < 2.0f -> 0.02f
                avgRating < 2.8f -> 0.08f
                avgRating < 3.5f -> 0.25f
                avgRating < 4.2f -> 0.65f
                avgRating < 4.8f -> 1.05f
                else -> 1.40f
            }
            val rawTarget = (totalStreams * 0.08 + state.fans * 0.4) * qualityMultiplier
            val expectedDelta = (rawTarget.toLong() - state.soundstreamMonthlyListeners) * 0.30f
            expectedDelta.toLong().coerceAtLeast(0L)
        }
    }

    if (showWeeklyTransitionDialog) {
        WeeklyAdvanceSummaryDialog(
            currentWeek = state.currentWeek,
            currentYear = state.currentYear,
            fansDelta = calculatedWeeklyFans,
            cashDelta = recentCashFlowSummary.average,
            cashDeltaLabel = if (recentCashFlowSummary.sampleCount == 0) "Cash flow history" else "Recent net cash flow (4 wk avg)",
            cashDeltaNote = if (recentCashFlowSummary.sampleCount == 0) {
                "Your first completed week will establish a cash-flow baseline. Next week's result can vary."
            } else {
                "Based on your last ${recentCashFlowSummary.sampleCount} completed week(s); this is a guide, not a guaranteed payout."
            },
            listenersDelta = calculatedWeeklyListeners,
            stressDelta = if (state.stress > 60f) 5.5f else 2.0f,
            headline = state.activeGlobalEvent?.let { "Global Trend: ${it.uppercase()}" } ?: weeklyReport.lines().firstOrNull() ?: "Industry executives are monitoring your releases.",
            onConfirmAdvance = {
                showWeeklyTransitionDialog = false
                viewModel.progressToNextWeek()
                // Chance of triggering a living career dilemma upon advancing
                CareerDilemmaController.maybeTriggerRandomDilemma(state)
                coroutineScope.launch {
                    overviewListState.animateScrollToItem(0)
                }
            },
            onDismiss = {
                showWeeklyTransitionDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabRow(
            selectedTabIndex = if (selectedDashboardTab == "Overview") 0 else 1,
            containerColor = CardBg,
            contentColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedDashboardTab == "Overview",
                onClick = { selectedDashboardTab = "Overview" },
                text = { Text("Overview", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedDashboardTab == "Career",
                onClick = { selectedDashboardTab = "Career" },
                text = { Text("🏆 Career Dashboard", fontWeight = FontWeight.Bold, color = if (selectedDashboardTab == "Career") NeonPink else Color.White) }
            )
        }

        if (selectedDashboardTab == "Overview") {
            LazyColumn(
                state = overviewListState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
        // GLOBAL WORLD EVENT ACTIVE BANNER
        if (state.activeGlobalEvent != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (state.globalEventEffect) {
                            "LOCKDOWN" -> DangerColor.copy(alpha = 0.12f)
                            "RECESSION" -> WarningColor.copy(alpha = 0.12f)
                            else -> PrimaryBrandColor.copy(alpha = 0.12f)
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        when (state.globalEventEffect) {
                            "LOCKDOWN" -> DangerColor.copy(alpha = 0.6f)
                            "RECESSION" -> WarningColor.copy(alpha = 0.6f)
                            else -> PrimaryBrandLight.copy(alpha = 0.6f)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when (state.globalEventEffect) {
                                        "LOCKDOWN" -> DangerColor.copy(alpha = 0.2f)
                                        "RECESSION" -> WarningColor.copy(alpha = 0.2f)
                                        else -> PrimaryBrandColor.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Global Event",
                                tint = when (state.globalEventEffect) {
                                    "LOCKDOWN" -> DangerColor
                                    "RECESSION" -> WarningColor
                                    else -> PrimaryBrandLight
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "WORLD EVENT: ${state.activeGlobalEvent}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val description = when (state.globalEventEffect) {
                                "LOCKDOWN" -> "Pandemic lockdown active! Live tours halted. Streaming royalties boosted (+30%)."
                                "RECESSION" -> "Global recession in effect. Gig pay & tour tickets down (-30%). Merch sales slow."
                                "FESTIVAL" -> "Summer Festival Season! Live tour attendance & ticket demand up (+40%)."
                                "AI_DEBATE" -> "AI Songwriting debate trending. Creative inspiration drains faster."
                                "VIRAL_CHALLENGE" -> "Viral Social Trend! Social media promotions & singles gain double reach."
                                else -> "Industry market forces are shifting. Plan your releases accordingly."
                            }
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // 1. ARTIST COMMAND CENTER PROFILE HERO (Identity, Level, Momentum, Resources)
        item {
            // Dynamic multi-pillar Career Mastery (Skills, Reach, Catalog & Industry Standing)
            val totalSkills = (state.voiceSkill + state.writingSkill + state.businessSkill).coerceIn(0f, 300f)
            val skillRatio = totalSkills / 300f
            val reachRatio = ((state.fans + state.followers).toFloat() / 1000000f).coerceIn(0f, 1.0f)
            val catalogRatio = (state.songs.count { it.isReleased }.toFloat() / 15f).coerceIn(0f, 1.0f)
            val avgNpcRel = if (state.npcs.isNotEmpty()) state.npcs.map { it.relationshipWithPlayer }.average().toFloat() / 100f else 0.5f
            val industryRatio = ((state.industryFollowers.toFloat() / 50000f).coerceIn(0f, 1.0f) * 0.5f + avgNpcRel * 0.5f).coerceIn(0f, 1.0f)
            val overallMastery = (skillRatio * 0.35f + reachRatio * 0.30f + catalogRatio * 0.15f + industryRatio * 0.20f).coerceIn(0.0f, 1.0f)
            val calculatedLevel = (overallMastery * 9f).toInt() + 1
            val levelFloor = (calculatedLevel - 1) / 9f
            val levelCeil = calculatedLevel / 9f
            val progressPercent = if (calculatedLevel >= 10) 1.0f else ((overallMastery - levelFloor) / (levelCeil - levelFloor)).coerceIn(0.05f, 0.98f)

            val careerTitle = when {
                state.fans > 10000000L -> "Hall of Fame Icon"
                state.fans > 5000000L -> "Global Megastar"
                state.fans > 1000000L -> "Platinum Chart-Topper"
                state.fans > 250000L -> "Headlining Hitmaker"
                state.fans > 50000L -> "Breakout Artist"
                state.fans > 5000L -> "Indie Sensation"
                else -> "Aspiring Musician"
            }

            // Career momentum calculation based on popularity, energy, and stress
            val momentumScore = (state.popularity * 0.5f + (100f - state.stress) * 0.3f + state.energy * 0.2f).coerceIn(0f, 100f)
            val momentumLabel = when {
                momentumScore > 75f -> "Viral Surge 🚀"
                momentumScore > 50f -> "Strong Momentum 📈"
                momentumScore > 30f -> "Steady Pace ⚡"
                else -> "Cooling Off ⚠️"
            }
            val momentumColor = when {
                momentumScore > 75f -> CreativePurple
                momentumScore > 50f -> SuccessColor
                momentumScore > 30f -> WarningColor
                else -> DangerColor
            }

            val totalEmpireVal = state.cash + state.businessVentures.filter { it.isOwned }.sumOf { it.valuation } + state.ownedHouses.sumOf { it.cost } + state.ownedCars.sumOf { it.cost }

            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Top identity row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AvatarWidget(
                            baseLook = state.avatarBaseLook,
                            skinTone = state.avatarSkinTone,
                            hairStyleId = state.avatarHairStyleId,
                            clothingId = state.avatarClothingId,
                            jewelryId = state.avatarJewelryId,
                            modifier = Modifier
                                .size(60.dp)
                                .border(1.5.dp, PrimaryBrandLight.copy(alpha = 0.4f), CircleShape)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = state.artistName,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                StatusChip(text = "LVL $calculatedLevel", color = PrimaryBrandLight)
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = careerTitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = PrimaryBrandLight
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Level progress bar
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Level Progress",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "${(progressPercent * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = PrimaryBrandLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = progressPercent,
                                color = PrimaryBrandLight,
                                trackColor = SurfaceColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showWeeklyTransitionDialog = true },
                                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("advance_week_arrow_btn"),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor)
                            ) {
                                Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(17.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Advance to week ${state.currentWeek + 1}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BorderColor.copy(alpha = 0.6f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Key metric tiles row (Fans, Net Worth, Momentum, Energy)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Fans
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "TOTAL FANS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (state.fans > 1000000) String.format("%.2fM", state.fans / 1000000.0) else String.format("%,d", state.fans),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextPrimary
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(BorderColor))

                        // Net Worth
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "NET WORTH",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = if (totalEmpireVal > 1000000) String.format("$%.2fM", totalEmpireVal / 1000000.0) else String.format("$%,.0f", totalEmpireVal),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = SuccessColor
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(BorderColor))

                        // Career Momentum
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "MOMENTUM",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = momentumLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = momentumColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 2. Authentic Career News & Reports (Summary of previous week: expenses, royalties, staff, marriage, gossip, etc.)
        item {
            CareerNewsReportSection(
                weeklyReport = weeklyReport,
                currentWeek = state.currentWeek,
                currentYear = state.currentYear
            )
        }


        // 4. WEEK & TIMELINE BAR
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryBrandLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WEEK ${state.currentWeek} OF 52",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(TextTertiary))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Year ${state.currentYear}",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${52 - state.currentWeek} wks to Year End",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }
        }


        // Two-Column Grid: Top Song & Top Country (Dynamic across Global Music Industry)
        item {
            val playerBestSong = state.songs.filter { it.isReleased }.maxByOrNull { it.totalStreams }
            val npcBestSong = state.npcs.filter { !it.isRetired }.maxByOrNull { it.bestSongStreams }

            val topTrackTitle: String
            val topTrackArtist: String
            val topTrackStreams: Long
            val isPlayerTrack: Boolean

            if (playerBestSong != null && (npcBestSong == null || playerBestSong.totalStreams >= npcBestSong.bestSongStreams)) {
                topTrackTitle = playerBestSong.title
                topTrackArtist = state.artistName
                topTrackStreams = playerBestSong.totalStreams
                isPlayerTrack = true
            } else if (npcBestSong != null) {
                topTrackTitle = npcBestSong.bestSongTitle
                topTrackArtist = npcBestSong.name
                topTrackStreams = npcBestSong.bestSongStreams
                isPlayerTrack = false
            } else {
                topTrackTitle = "Midnight Echoes"
                topTrackArtist = "Elena Vance"
                topTrackStreams = 4500000L
                isPlayerTrack = false
            }

            val countries = listOf("United States", "United Kingdom", "Japan", "Germany", "Canada", "Brazil", "Australia", "France", "South Korea")
            val countryIndex = ((topTrackStreams / 100000L) + state.currentWeek).toInt() % countries.size
            val topCountryName = countries[countryIndex.coerceAtLeast(0)]
            val countryListeners = (topTrackStreams * 0.38).toLong().coerceAtLeast(1200L)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Track Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, if (isPlayerTrack) NeonTeal.copy(alpha = 0.5f) else BorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Top Track", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = if (isPlayerTrack) NeonTeal else PrimaryBrandLight, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = topTrackTitle,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "by $topTrackArtist",
                            color = if (isPlayerTrack) NeonTeal else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%,d", topTrackStreams)} streams",
                            color = AccentGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Top Country Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Top Market", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = AccentGold, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = topCountryName,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Global Hub",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${String.format("%,d", countryListeners)} listeners",
                            color = AccentGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }


        // Quick Actions Navigation Shortcuts
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val actions = listOf(
                            Triple("New Song", Icons.Default.MusicNote, "Studio"),
                            Triple("Invest", Icons.Default.TrendingUp, "Trading"),
                            Triple("Assets", Icons.Default.ShoppingCart, "Marketplace"),
                            Triple("Mailbox", Icons.Default.Email, "Mailbox"),
                            Triple("Grow", Icons.Default.Share, "Social & Practice")
                        )

                        actions.forEach { (label, icon, tabId) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onNavigate(tabId) }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF1B1826))
                                        .border(1.dp, BorderColor, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = label, tint = NeonTeal, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }


        // Artist Storytelling Statistics Panel
        item {
            val storytellingStatsList = listOf(
                // Core Creative Artistry
                StorytellingStat(
                    name = "Vocal Performance",
                    value = state.voiceSkill,
                    delta = 2.4f,
                    category = "Artistry",
                    consequence = "Directly improves recorded vocal take rating & live concert vocal stamina.",
                    influencedBy = "Vocal coaching masterclasses, live gig singing, recording takes.",
                    color = PrimaryBrandLight
                ),
                StorytellingStat(
                    name = "Lyric Writing",
                    value = state.writingSkill,
                    delta = 1.8f,
                    category = "Artistry",
                    consequence = "Elevates song emotional resonance, replay longevity & critical review scores.",
                    influencedBy = "Songwriting workshops, daily journaling, life milestones.",
                    color = CreativePurple
                ),
                StorytellingStat(
                    name = "Creative Inspiration",
                    value = state.creativeInspiration,
                    delta = if (state.creativeInspiration > 60) 3.5f else -1.2f,
                    category = "Artistry",
                    consequence = "Powers hit track creation. High inspiration unlocks breakthrough viral tracks.",
                    influencedBy = "Art galleries, Hawaii vacations, resting, listening to vinyl records.",
                    color = WarningColor
                ),

                // Industry & Business Savvy
                StorytellingStat(
                    name = "Business Savvy",
                    value = state.businessSkill,
                    delta = 1.2f,
                    category = "Industry",
                    consequence = "Secures higher royalty percentages, lowers tour overhead & unlocks investments.",
                    influencedBy = "Business management study, running commercial ventures, contract deals.",
                    color = SuccessColor
                ),
                StorytellingStat(
                    name = "Industry Influence",
                    value = state.influence,
                    delta = 0.8f,
                    category = "Industry",
                    consequence = "Attracts legendary producers, festival headliner invitations & playlisting.",
                    influencedBy = "Chart success, industry networking events, red carpet appearances.",
                    color = PrimaryBrandColor
                ),
                StorytellingStat(
                    name = "Peer Respect",
                    value = state.industryRespect,
                    delta = 0.5f,
                    category = "Industry",
                    consequence = "Top musicians accept collaboration requests and give public co-signs.",
                    influencedBy = "High-quality release discography, artistic integrity, fair deals.",
                    color = SuccessColor
                ),

                // Public Persona & Acclaim
                StorytellingStat(
                    name = "Public Popularity",
                    value = state.popularity,
                    delta = if (state.songs.isNotEmpty()) 4.2f else 0.0f,
                    category = "Public",
                    consequence = "Drives organic streaming algorithm exposure and ticket sales velocity.",
                    influencedBy = "Social media posts, hit singles, tour performances, media buzz.",
                    color = WarningColor
                ),
                StorytellingStat(
                    name = "Public Image",
                    value = state.publicImage,
                    delta = 0.2f,
                    category = "Public",
                    consequence = "Attracts premium corporate sponsorship contracts and family-friendly brands.",
                    influencedBy = "Charity philanthropy, clean press interviews, avoiding scandals.",
                    color = SuccessColor
                ),
                StorytellingStat(
                    name = "Critical Acclaim",
                    value = state.criticalAcclaim,
                    delta = 1.5f,
                    category = "Public",
                    consequence = "Boosts end-of-year award nominations and evergreen streaming royalties.",
                    influencedBy = "Concept albums, high production standards, genre innovation.",
                    color = CreativePurple
                ),

                // Stamina & Well-being
                StorytellingStat(
                    name = "Physical Energy",
                    value = state.energy.toFloat(),
                    delta = -2.0f,
                    category = "Vitality",
                    consequence = "Required to execute weekly actions. Drained stamina risks severe burnout.",
                    influencedBy = "Full night sleep, nutritious diet, taking recovery weeks.",
                    color = if (state.energy < 30) DangerColor else WarningColor
                ),
                StorytellingStat(
                    name = "Stress Level",
                    value = state.stress,
                    delta = if (state.stress > 50) 2.5f else -1.0f,
                    category = "Vitality",
                    consequence = "Elevated stress reduces creative output and increases studio error rates.",
                    influencedBy = "Heavy touring, debt, paparazzi pressure. Relieved by hobbies & therapy.",
                    color = if (state.stress > 60) DangerColor else PrimaryBrandLight
                ),
                StorytellingStat(
                    name = "Work-Life Balance",
                    value = state.workLifeBalance,
                    delta = 0.0f,
                    category = "Vitality",
                    consequence = "Preserves long-term career fulfillment and prevents sudden hiatuses.",
                    influencedBy = "Taking time off between album cycles, personal passions, vacations.",
                    color = SuccessColor
                )
            )

            StorytellingStatsCard(stats = storytellingStatsList)
        }


        // Mental Health & Self-Care Actions Panel (Living Well-being Dashboard)
        item {
            ArtistWellbeingDashboard(
                stress = state.stress,
                creativeInspiration = state.creativeInspiration,
                happiness = state.happiness,
                workLifeBalance = state.workLifeBalance,
                energy = state.energy,
                burnout = state.burnout,
                cash = state.cash,
                onRestAndHobby = {
                    viewModel.hobbiesAndRest()
                    viewModel.postActionFeedback(
                        title = "Creative Hobby & Relaxation",
                        subtitle = "Restored stamina · Mental clarity rejuvenated",
                        deltas = listOf(
                            StatFeedbackItem("Energy", "+40", true, "energy"),
                            StatFeedbackItem("Stress", "-15%", true, "stress"),
                            StatFeedbackItem("Inspiration", "+12%", true, "creative")
                        ),
                        iconType = "rest"
                    )
                },
                onSeekTherapy = {
                    if (state.cash >= 1000.0) {
                        viewModel.seekTherapy()
                        viewModel.postActionFeedback(
                            title = "Therapy & Mental Resilience",
                            subtitle = "Cognitive decompression & anxiety reduction",
                            deltas = listOf(
                                StatFeedbackItem("Stress", "-30%", true, "stress"),
                                StatFeedbackItem("Happiness", "+15%", true, "general"),
                                StatFeedbackItem("Cash", "-$1,000", false, "money")
                            ),
                            iconType = "therapy"
                        )
                    } else {
                        viewModel.seekTherapy()
                    }
                },
                onTakeVacation = {
                    if (state.cash >= 5000.0) {
                        viewModel.takeVacation()
                        viewModel.postActionFeedback(
                            title = "Island Vacation Getaway",
                            subtitle = "Full sensory detox & creative recharge",
                            deltas = listOf(
                                StatFeedbackItem("Burnout", "-100%", true, "stress"),
                                StatFeedbackItem("Energy", "+100", true, "energy"),
                                StatFeedbackItem("Inspiration", "+50%", true, "creative"),
                                StatFeedbackItem("Cash", "-$5,000", false, "money")
                            ),
                            iconType = "rest"
                        )
                    } else {
                        viewModel.takeVacation()
                    }
                }
            )
        }


    }
} else {
    // CAREER DASHBOARD PAGE LAYOUT
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tier Status Header with Progress Badge
                item {
                    val totalEmpireVal = state.cash + state.businessVentures.filter { it.isOwned }.sumOf { it.valuation } + state.ownedHouses.sumOf { it.cost } + state.ownedCars.sumOf { it.cost }
                    val currentTier = when {
                        state.fans > 10000000L -> "Hall of Fame Legend"
                        state.fans > 1000000L -> "Global Superstar"
                        state.fans > 200000L -> "National Hitmaker"
                        state.fans > 20000L -> "Rising Sensation"
                        else -> "Underground Artist"
                    }
                    val tierColor = when (currentTier) {
                        "Hall of Fame Legend" -> AccentGold
                        "Global Superstar" -> NeonPink
                        "National Hitmaker" -> NeonPurple
                        "Rising Sensation" -> NeonTeal
                        else -> TextSecondary
                    }

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.5.dp, tierColor),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "CURRENT STANDING",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentTier,
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = tierColor,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(tierColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "LEVEL ${(state.fans / 200000L).toInt().coerceAtLeast(1)}",
                                        color = tierColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = BorderColor.copy(alpha = 0.4f), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "LIFETIME CAREER STATISTICS",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonPurple,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val lifetimeStats = listOf(
                                "Total Songs Recorded" to "${state.songs.size} tracks",
                                "All-Time Music Streams" to (state.songs.sumOf { it.totalStreams.toLong() }.let {
                                    if (it > 1000000) String.format("%.2fM", it / 1000000.0) else String.format("%,d", it)
                                }),
                                "Best Release Rating" to (state.songs.maxByOrNull { it.rating }?.let { String.format("%.1f ⭐", it.rating) } ?: "None yet"),
                                "Label Contract Status" to (state.ownLabel?.let { "CEO of ${it.name}" } ?: state.signedLabelId?.let { "Signed with $it" } ?: "Independent Artist"),
                                "Estimated Asset Net Worth" to (if (totalEmpireVal > 1000000) String.format("$%.2fM", totalEmpireVal / 1000000.0) else String.format("$%,.0f", totalEmpireVal))
                            )

                            lifetimeStats.forEach { (label, value) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = label, color = TextSecondary, fontSize = 12.sp)
                                    Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }

                // Interactive Career Milestone Checklist
                item {
                    val milestones = listOf(
                        Quad(
                            "Record a 4.5+ Star Hit Song",
                            "Use premium studio setups, hire top producers, and stay highly inspired.",
                            state.songs.any { it.rating >= 4.5f },
                            NeonTeal
                        ),
                        Quad(
                            "Own or Form a Record Label",
                            "Earn enough capital and establish your custom imprint label.",
                            state.ownLabel != null,
                            NeonPurple
                        ),
                        Quad(
                            "Shoot standard or higher Music Video",
                            "Sponsor a professional cinematic video shoot for any recorded track.",
                            state.songs.any { it.videoTier != null },
                            NeonPink
                        ),
                        Quad(
                            "Reach $500,000+ Total Net Worth",
                            "Acquire properties, secure automatic assets, and scale investments.",
                            (state.cash + state.businessVentures.filter { it.isOwned }.sumOf { it.valuation } + state.ownedHouses.sumOf { it.cost } + state.ownedCars.sumOf { it.cost }) >= 500000.0,
                            AccentGreen
                        ),
                        Quad(
                            "Book and Complete a Concert Tour",
                            "Schedule live tours to monetize your fan engagement globally.",
                            state.tours.any { it.status == "COMPLETED" },
                            AccentGold
                        )
                    )

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "CAREER ROADMAP OBJECTIVES",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonPink,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            milestones.forEachIndexed { index, (title, desc, isDone, color) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Milestone State",
                                        tint = if (isDone) AccentGreen else Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isDone) Color.White else TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                if (index < milestones.size - 1) {
                                    Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // Historical Timeline Events Journey
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "LIFETIME JOURNEY LOGS",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            if (state.timelineEvents.isEmpty()) {
                                Text(
                                    text = "Your journey has just begun! Milestones and key releases will populate this timeline as you progress.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    state.timelineEvents.reversed().take(15).forEach { event ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "⚡",
                                                color = NeonTeal,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(end = 12.dp)
                                            )
                                            Text(
                                                text = event,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
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

data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun StatRow(label: String, percent: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text(text = "${String.format("%.1f", percent)}%", style = MaterialTheme.typography.bodyMedium, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = percent / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF2C2A3A)
        )
    }
}

@Composable
fun StudioPage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedStudioTab by remember { mutableStateOf("Record Song") }
    var songTitle by remember { mutableStateOf("") }
    var studioBudget by remember { mutableStateOf("1000") }
    var selectedStudioName by remember { mutableStateOf("Jipsee Sound") }
    var selectedGenre by remember { mutableStateOf(GameData.GENRES[0].id) }
    var selectedProducer by remember { mutableStateOf<String?>(null) }
    var lyricsFocus by remember { mutableStateOf(0.5f) }
    var mainstreamAppeal by remember { mutableStateOf(0.5f) }
    var vocalFocus by remember { mutableStateOf(0.5f) }
    var customSubgenreName by remember { mutableStateOf("") }

    // Hyper-realistic customizations
    var weeksUntilReleaseOpt by remember { mutableStateOf(0) }
    var soundEngineerOpt by remember { mutableStateOf("Amateur") }
    var mixingStyleOpt by remember { mutableStateOf("Standard") }
    var backingVocalistsOpt by remember { mutableStateOf("None") }
    var studioDecorOpt by remember { mutableStateOf("Basement") }
    var producerTierOpt by remember { mutableStateOf("None") }
    var registerCopyrightOpt by remember { mutableStateOf(false) }
    var selectedCoverArtOpt by remember { mutableStateOf<String?>(null) }
    var selectedFeaturedArtistId by remember { mutableStateOf<String?>(null) }

    // Video creation section states
    var selectedVideoSongId by remember { mutableStateOf<String?>(null) }
    var selectedVideoTier by remember { mutableStateOf("Standard") }
    var videoTitleInput by remember { mutableStateOf("") }
    var videoThumbnailOpt by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    var isPickingForVideo by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val uriStr = uri.toString()
            if (isPickingForVideo) {
                videoThumbnailOpt = uriStr
                Toast.makeText(context, "Video thumbnail selected from gallery!", Toast.LENGTH_SHORT).show()
            } else {
                selectedCoverArtOpt = uriStr
                Toast.makeText(context, "Cover artwork selected from gallery!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(context, "Storage / Media permission is required to access your device gallery.", Toast.LENGTH_LONG).show()
        }
    }

    val pickImageWithPermission = { forVideo: Boolean ->
        isPickingForVideo = forVideo
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            permissionLauncher.launch(perm)
        }
    }

    var catalogFilter by remember { mutableStateOf("All") }
    var hypeTierIndex by remember { mutableStateOf(0) }

    // EP & Album compilation states
    var albumTitle by remember { mutableStateOf("") }
    var compileIsEP by remember { mutableStateOf(true) }
    var selectedCompileSongIds by remember { mutableStateOf(setOf<String>()) }
    var albumMarketingTier by remember { mutableStateOf("DIY") }

    var isSessionFormExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Studio Showcase Card (Platinum Sound Studios Mockup)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Visual Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(NeonPurple.copy(alpha = 0.6f), Color(0xFF131124))
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star",
                                        tint = AccentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "★ 4.8 (128 reviews)",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Platinum Sound Studios",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Los Angeles, USA  •  Premium Tier Studio",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    // Characteristic metrics grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Quality", color = TextSecondary, fontSize = 11.sp)
                            Text("95/100", color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Hourly Rate", color = TextSecondary, fontSize = 11.sp)
                            Text("$450 /hr", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Availability", color = TextSecondary, fontSize = 11.sp)
                            Text("Available Now", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    // Studio Services & Boost columns
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left services
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "Studio Services",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val services = listOf(
                                "Recording" to "$450/hr",
                                "Mixing & Mastering" to "$650/hr",
                                "Vocal Editing" to "$350/track",
                                "Beat Production" to "$300/hr"
                            )
                            services.forEach { (srv, cost) ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(NeonTeal)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(srv, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                    Text(cost, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Right boosts
                        Column(modifier = Modifier.weight(0.8f)) {
                            Text(
                                text = "Studio Boosts",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val boosts = listOf(
                                "Song Quality" to "+25%",
                                "Fan Perception" to "+15%",
                                "Chart Appeal" to "+10%"
                            )
                            boosts.forEach { (bst, rate) ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AccentGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(bst, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                    Text(rate, color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Book Studio Session button
                    Button(
                        onClick = { isSessionFormExpanded = !isSessionFormExpanded },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (isSessionFormExpanded) "CLOSE SESSION BUILDER" else "BOOK STUDIO SESSION",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Expandable Session Booking Panel
        if (isSessionFormExpanded) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Configure Booking Session",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = songTitle,
                            onValueChange = { songTitle = it },
                            label = { Text("Song Title") },
                            placeholder = { Text("e.g. Midnight Bass") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Studio Partner", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Jipsee Sound", "Audination", "BigSound Man").forEach { studioName ->
                                FilterChip(
                                    selected = selectedStudioName == studioName,
                                    onClick = {
                                        selectedStudioName = studioName
                                        // Update default budgets based on choice
                                        studioBudget = when (studioName) {
                                            "Jipsee Sound" -> "1500"
                                            "Audination" -> "18000"
                                            "BigSound Man" -> "85000"
                                            else -> "1000"
                                        }
                                    },
                                    label = { Text(studioName) }
                                )
                            }
                        }

                        // Studio Specifications details Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                when (selectedStudioName) {
                                    "Jipsee Sound" -> {
                                        Text("Jipsee Sound Details (Budgets: $1,000 - $5,000)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("• Tailored for upcoming artists. Sells full copyrights.", color = Color.Gray, fontSize = 10.sp)
                                        Text("• Song quality is based directly on paid budget.", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    "Audination" -> {
                                        Text("Audination Details (Budgets: $15,000 - $25,000)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("• Half copyright license. Excellent distribution support.", color = Color.Gray, fontSize = 10.sp)
                                        Text("• Guarantees 4 weeks promo of +3% weekly stream boosts.", color = Color.Gray, fontSize = 10.sp)
                                        Text("• Automatic inclusion into 1 major platform playlist.", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    "BigSound Man" -> {
                                        Text("BigSound Man Details (Budgets: $70,000 - $100,000)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("• Premium service. Gives you full master license copyright.", color = Color.Gray, fontSize = 10.sp)
                                        Text("• Heavy promotion for 12 weeks with +7% weekly stream boosts.", color = Color.Gray, fontSize = 10.sp)
                                        Text("• Inclusion in 5 popular playlists. Boosts global international stream reach.", color = Color.Gray, fontSize = 10.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Select Genre", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GameData.GENRES.forEach { genre ->
                                FilterChip(
                                    selected = selectedGenre == genre.id,
                                    onClick = { selectedGenre = genre.id },
                                    label = { Text("${genre.name} (${(genre.listenerAppeal * 100).toInt()}% appeal)") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = studioBudget,
                            onValueChange = { studioBudget = it },
                            label = { Text("Base Studio Session Budget ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Campaign & Release Strategy", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(0 to "Drop Instantly", 2 to "2-Wk Hype", 4 to "4-Wk Campaign", 8 to "8-Wk Blockbuster").forEach { (weeks, label) ->
                                FilterChip(
                                    selected = weeksUntilReleaseOpt == weeks,
                                    onClick = { weeksUntilReleaseOpt = weeks },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Workforce & Aesthetics", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Studio Decor Aesthetic
                        Text("Studio Decor Theme", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Basement" to "Basement (Free)", "Indie Loft" to "Cosmic Indie Loft ($1.5k)", "Retro Analog Oasis" to "Retro Oasis ($5k)", "High-Tech Audio Fortress" to "Audio Fortress ($12.5k)").forEach { (key, label) ->
                                FilterChip(
                                    selected = studioDecorOpt == key,
                                    onClick = { studioDecorOpt = key },
                                    label = { Text(label) }
                                )
                            }
                        }

                        // Producer Tier
                        Text("Session Producer Tier", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("None" to "None (Free)", "Indie Beatmaker" to "Indie Beatmaker ($2.5k)", "Hitmaker Producer" to "Hitmaker ($10k)", "Legendary Sonic Guru" to "Sonic Guru ($30k)").forEach { (key, label) ->
                                FilterChip(
                                    selected = producerTierOpt == key,
                                    onClick = { producerTierOpt = key },
                                    label = { Text(label) }
                                )
                            }
                        }

                        // Sound Engineer Choice
                        Text("Sound Engineer Quality", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Amateur" to "Amateur (Free)", "Professional" to "Professional ($1.8k)", "Grammy-winning" to "Grammy-Winning ($7.5k)").forEach { (key, label) ->
                                FilterChip(
                                    selected = soundEngineerOpt == key,
                                    onClick = { soundEngineerOpt = key },
                                    label = { Text(label) }
                                )
                            }
                        }

                        // Mixing Style
                        Text("Mixing & Mastering Polish", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Standard" to "Standard (Free)", "Warm Analog Tape" to "Warm Analog ($1.2k)", "Spatial Dolby Atmos Master" to "Spatial Atmos ($3.5k)").forEach { (key, label) ->
                                FilterChip(
                                    selected = mixingStyleOpt == key,
                                    onClick = { mixingStyleOpt = key },
                                    label = { Text(label) }
                                )
                            }
                        }

                        // Backing Vocalists
                        Text("Arrangement Support", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("None" to "None (Free)", "Backing Vocalists" to "Backing Vocals ($800)", "Live Brass & String Section" to "Live Brass/Strings ($3k)").forEach { (key, label) ->
                                FilterChip(
                                    selected = backingVocalistsOpt == key,
                                    onClick = { backingVocalistsOpt = key },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Additional Freelance Producer Addon", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedProducer == null,
                                onClick = { selectedProducer = null },
                                label = { Text("No Producer Addon") }
                            )
                            GameData.PRODUCERS.forEach { prod ->
                                FilterChip(
                                    selected = selectedProducer == prod.id,
                                    onClick = { selectedProducer = prod.id },
                                    label = { Text("${prod.name} (+${prod.ratingBonus}⭐ - $${prod.cost})") }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val baseSessionVal = studioBudget.toDoubleOrNull() ?: 1000.0
                        val decorAddon = when (studioDecorOpt) {
                            "Indie Loft" -> 1500.0
                            "Retro Analog Oasis" -> 5000.0
                            "High-Tech Audio Fortress" -> 12500.0
                            else -> 0.0
                        }
                        val producerAddon = when (producerTierOpt) {
                            "Indie Beatmaker" -> 2500.0
                            "Hitmaker Producer" -> 10000.0
                            "Legendary Sonic Guru" -> 30000.0
                            else -> 0.0
                        }
                        val engineerAddon = when (soundEngineerOpt) {
                            "Professional" -> 1800.0
                            "Grammy-winning" -> 7500.0
                            else -> 0.0
                        }
                        val mixingAddon = when (mixingStyleOpt) {
                            "Warm Analog Tape" -> 1200.0
                            "Spatial Dolby Atmos Master" -> 3500.0
                            else -> 0.0
                        }
                        val backingAddon = when (backingVocalistsOpt) {
                            "Backing Vocalists" -> 800.0
                            "Live Brass & String Section" -> 3000.0
                            else -> 0.0
                        }
                        val selectedProducerObj = GameData.PRODUCERS.find { it.id == selectedProducer }
                        val prodBaseCost = selectedProducerObj?.cost ?: 0.0

                        // Featured Artist Collaboration ($20,000 - $500,000, requires >= 30% relationship)
                        val featuredNpc = if (!selectedFeaturedArtistId.isNullOrBlank()) state.npcs.find { it.id == selectedFeaturedArtistId } else null
                        val featureAddonCost: Double = if (featuredNpc != null) {
                            val popFactor = (featuredNpc.popularity / 100.0).coerceIn(0.0, 1.0)
                            val calc = 20000.0 + (popFactor * popFactor) * 480000.0
                            (Math.round(calc / 500.0) * 500.0).coerceIn(20000.0, 500000.0)
                        } else 0.0

                        val sessionSubtotal = baseSessionVal + decorAddon + producerAddon + engineerAddon + mixingAddon + backingAddon + prodBaseCost + featureAddonCost
                        val copyrightAddon = if (registerCopyrightOpt) (sessionSubtotal * 0.15).coerceAtLeast(150.0) else 0.0
                        val estimatedTotalCost = sessionSubtotal + copyrightAddon

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Feature an Artist (Collaboration)", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            if (featuredNpc != null) {
                                Text("Feature Fee: $${String.format("%,.0f", featureAddonCost)}", color = AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("Top artists demand $20k - $500k. Expensive stars boost song hype and chance of viral blowup! (Requires ≥30% relationship)", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        var featuredDropdownExpanded by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                onClick = { featuredDropdownExpanded = true },
                                shape = RoundedCornerShape(12.dp),
                                color = CardBg,
                                border = BorderStroke(1.dp, if (featuredNpc != null) AccentGold else BorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (featuredNpc != null) featuredNpc.name else "Solo Track (No Feature)",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (featuredNpc != null) AccentGold else TextPrimary
                                        )
                                        Text(
                                            text = if (featuredNpc != null) {
                                                "${featuredNpc.popularity.toInt()}★ Pop • ${featuredNpc.relationshipWithPlayer.toInt()}% Rel • Fee: $${String.format("%,.0f", featureAddonCost)}"
                                            } else {
                                                "Tap to select an artist from your contact roster"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Artist",
                                        tint = if (featuredNpc != null) AccentGold else TextSecondary
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = featuredDropdownExpanded,
                                onDismissRequest = { featuredDropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f).background(CardBg)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Solo Track (No Feature)",
                                            color = if (selectedFeaturedArtistId == null) AccentGreen else TextPrimary,
                                            fontWeight = if (selectedFeaturedArtistId == null) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedFeaturedArtistId = null
                                        featuredDropdownExpanded = false
                                    }
                                )
                                HorizontalDivider(color = BorderColor)
                                state.npcs.forEach { npc ->
                                    val popFactor = (npc.popularity / 100.0).coerceIn(0.0, 1.0)
                                    val calc = 20000.0 + (popFactor * popFactor) * 480000.0
                                    val roundedNpcCost = (Math.round(calc / 500.0) * 500.0).coerceIn(20000.0, 500000.0)
                                    val rel = npc.relationshipWithPlayer
                                    val canAfford = state.cash >= roundedNpcCost
                                    val meetsRel = rel >= 30f
                                    val isEligible = canAfford && meetsRel
                                    val isSelected = selectedFeaturedArtistId == npc.id

                                    DropdownMenuItem(
                                        text = {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "${npc.name} (${npc.popularity.toInt()}★ Pop)",
                                                        color = if (isSelected) AccentGold else if (isEligible) TextPrimary else TextSecondary,
                                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "$${String.format("%,.0f", roundedNpcCost)}",
                                                        color = if (isEligible) AccentGold else TextSecondary,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                                val statusNote = when {
                                                    !meetsRel -> "⚠️ Requires ≥30% relationship (currently ${rel.toInt()}%)"
                                                    !canAfford -> "⚠️ Insufficient funds (Fee: $${String.format("%,.0f", roundedNpcCost)})"
                                                    else -> "✓ Available to feature (Rel: ${rel.toInt()}%) • ${npc.genrePreference}"
                                                }
                                                Text(
                                                    text = statusNote,
                                                    color = if (isEligible) AccentGreen else DangerColor,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        },
                                        enabled = isEligible,
                                        onClick = {
                                            selectedFeaturedArtistId = npc.id
                                            featuredDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Intellectual Property & Copyright Protection", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !registerCopyrightOpt,
                                onClick = { registerCopyrightOpt = false },
                                label = { Text("Unregistered Master ($0)") }
                            )
                            FilterChip(
                                selected = registerCopyrightOpt,
                                onClick = { registerCopyrightOpt = true },
                                label = { Text("Federal Copyright (+15% fee: $${String.format("%,.0f", (sessionSubtotal * 0.15).coerceAtLeast(150.0))})") }
                            )
                        }

                        // Custom Cover Art Selector with Personal Gallery & Saved Photos
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Music Cover Art", style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Button(
                                onClick = { pickImageWithPermission(false) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2248)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(14.dp), tint = NeonTeal)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pick from Personal Gallery", fontSize = 11.sp, color = Color.White)
                            }
                        }
                        if (!selectedCoverArtOpt.isNullOrBlank()) {
                            Text("Selected: ${selectedCoverArtOpt?.take(45)}...", color = NeonTeal, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }
                        if (state.savedPhotos.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedCoverArtOpt == null,
                                    onClick = { selectedCoverArtOpt = null },
                                    label = { Text("Automatic Cover Art ✨") }
                                )
                                state.savedPhotos.forEach { photo ->
                                    val shortPhotoName = photo.substringBefore(" (").take(22) + "..."
                                    FilterChip(
                                        selected = selectedCoverArtOpt == photo,
                                        onClick = { selectedCoverArtOpt = photo },
                                        label = { Text(shortPhotoName) }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val budgetVal = studioBudget.toDoubleOrNull() ?: 1000.0
                                val recordedTitle = songTitle.ifBlank { "Untitled Master" }
                                viewModel.createSong(
                                    title = songTitle,
                                    genreId = selectedGenre,
                                    producerId = selectedProducer,
                                    studioName = selectedStudioName,
                                    sessionCost = budgetVal,
                                    weeksUntilRelease = weeksUntilReleaseOpt,
                                    soundEngineerQuality = soundEngineerOpt,
                                    mixingStyle = mixingStyleOpt,
                                    backingVocalists = backingVocalistsOpt,
                                    studioDecor = studioDecorOpt,
                                    producerTier = producerTierOpt,
                                    coverArt = selectedCoverArtOpt,
                                    registerCopyright = registerCopyrightOpt,
                                    featuredArtistId = selectedFeaturedArtistId
                                )
                                viewModel.postActionFeedback(
                                    title = "Master Track Recorded: \"$recordedTitle\"",
                                    subtitle = "Studio session complete · Acoustic master produced",
                                    deltas = listOf(
                                        StatFeedbackItem("Energy", "-20", false, "energy"),
                                        StatFeedbackItem("Lyric Skill", "+2.4", true, "skill"),
                                        StatFeedbackItem("Creativity", "+1.8", true, "creative"),
                                        StatFeedbackItem("Cash", "-$${String.format("%,.0f", estimatedTotalCost)}", false, "money")
                                    ),
                                    iconType = "music"
                                )
                                songTitle = ""
                                selectedCoverArtOpt = null
                                selectedFeaturedArtistId = null
                                isSessionFormExpanded = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CONFIRM & RECORD SONG (Est: $${String.format("%,.0f", estimatedTotalCost)})", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Subtabs for Video, Compilation & Catalog Management
        item {
            TabRow(
                selectedTabIndex = when (selectedStudioTab) {
                    "Record Song" -> 0
                    "Compile Album" -> 1
                    else -> 2
                },
                containerColor = CardBg,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedStudioTab == "Record Song",
                    onClick = { selectedStudioTab = "Record Song" },
                    text = { Text("Catalog & Release") }
                )
                Tab(
                    selected = selectedStudioTab == "Compile Album",
                    onClick = { selectedStudioTab = "Compile Album" },
                    text = { Text("Compile EP/LP") }
                )
                Tab(
                    selected = selectedStudioTab == "Create Music Video",
                    onClick = { selectedStudioTab = "Create Music Video" },
                    text = { Text("Shoot Video") }
                )
            }
        }

        if (selectedStudioTab == "Create Music Video") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Shoot a Music Video",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonPink,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select recorded song:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        if (state.songs.isEmpty()) {
                            Text("No recorded songs yet. Record some music first!", color = TextSecondary)
                        } else {
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.songs.forEach { song ->
                                    FilterChip(
                                        selected = selectedVideoSongId == song.id,
                                        onClick = { selectedVideoSongId = song.id },
                                        label = { Text("${song.title} (${String.format("%.1f", song.rating)}⭐)") }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Video Title (Optional):", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = videoTitleInput,
                            onValueChange = { videoTitleInput = it },
                            placeholder = { Text("e.g. Official Music Video / Acoustic Live Performance", color = TextSecondary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Video Thumbnail Artwork:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            Button(
                                onClick = { pickImageWithPermission(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2248)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(14.dp), tint = NeonPink)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Upload from Gallery", fontSize = 11.sp, color = Color.White)
                            }
                        }
                        if (!videoThumbnailOpt.isNullOrBlank()) {
                            Text("Selected Thumbnail: ${videoThumbnailOpt?.take(45)}...", color = NeonPink, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Select Production Tier Budget:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Text("Higher subscriber counts and views multiply initial hype surge and streams!", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(6.dp))

                        val videoTiers = listOf(
                            Triple("Low", 2000.0, "1.2x listeners, +5 pop"),
                            Triple("Standard", 5000.0, "1.5x listeners, +15 pop"),
                            Triple("High", 10000.0, "2.0x listeners, +30 pop"),
                            Triple("Premium", 20000.0, "2.5x listeners, +50 pop"),
                            Triple("Cinematic", 50000.0, "3.0x listeners, +100 pop")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            videoTiers.forEach { (tier, cost, effects) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (selectedVideoTier == tier) Color(0xFF1E1430) else Color.Transparent)
                                        .clickable { selectedVideoTier = tier }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "$tier Video", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "Cost: $${String.format("%,.0f", cost)}", color = AccentGreen)
                                    Text(text = effects, color = NeonTeal, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                selectedVideoSongId?.let { songId ->
                                    viewModel.createMusicVideo(
                                        songId = songId,
                                        tier = selectedVideoTier,
                                        customTitle = videoTitleInput.takeIf { it.isNotBlank() },
                                        customThumbnail = videoThumbnailOpt
                                    )
                                    viewModel.postActionFeedback(
                                        title = "Music Video Production Started",
                                        subtitle = "$selectedVideoTier Tier visuals · Set design & camera crew engaged",
                                        deltas = listOf(
                                            StatFeedbackItem("Hype", "+25%", true, "general"),
                                            StatFeedbackItem("Streams Boost", "+35%", true, "streams"),
                                            StatFeedbackItem("Energy", "-30", false, "energy")
                                        ),
                                        iconType = "video"
                                    )
                                    videoTitleInput = ""
                                    videoThumbnailOpt = null
                                } ?: viewModel.showToast("Select a song first!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("LAUNCH MUSIC VIDEO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (selectedStudioTab == "Compile Album") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Compile & Launch EP or Full LP",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Group your unreleased or released singles into a complete release. Cohesive genres boost your score; incompatible genres turn off critics.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Type selector: EP vs LP
                        Text(text = "Release Type:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilterChip(
                                selected = compileIsEP,
                                onClick = { compileIsEP = true },
                                label = { Text("Extended Play (EP) • 3-5 Songs") }
                            )
                            FilterChip(
                                selected = !compileIsEP,
                                onClick = { compileIsEP = false },
                                label = { Text("Full-Length Album (LP) • 8-14 Songs") }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Album Title
                        OutlinedTextField(
                            value = albumTitle,
                            onValueChange = { albumTitle = it },
                            label = { Text("EP or Album Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonTeal,
                                unfocusedBorderColor = BorderColor,
                                focusedLabelColor = NeonTeal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("album_title_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Song checkboxes
                        Text(
                            text = "Select Songs for Tracklist (${selectedCompileSongIds.size} selected):",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (state.songs.isEmpty()) {
                            Text(text = "No songs in your archive yet. Record songs first!", color = Color.Red, fontSize = 12.sp)
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1430)),
                                modifier = Modifier.fillMaxWidth().heightIn(max = 240.dp)
                            ) {
                                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(8.dp)) {
                                    state.songs.forEach { song ->
                                        val isChecked = selectedCompileSongIds.contains(song.id)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedCompileSongIds = if (isChecked) {
                                                        selectedCompileSongIds - song.id
                                                    } else {
                                                        selectedCompileSongIds + song.id
                                                    }
                                                }
                                                .padding(vertical = 6.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    selectedCompileSongIds = if (checked == true) {
                                                        selectedCompileSongIds + song.id
                                                    } else {
                                                        selectedCompileSongIds - song.id
                                                    }
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = NeonTeal)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(text = song.title, color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                                Text(text = "${song.genreId} • ${String.format("%.1f", song.rating)}⭐ Rating", color = TextSecondary, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Marketing selection
                        Text(text = "Select Marketing Campaign:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        val campaigns = listOf(
                            Triple("DIY", 0.0, "Organic release, +10 Hype, base sales"),
                            Triple("Budget", 1500.0, "Local Street Promo, +25 Hype, 1.3x sales"),
                            Triple("Standard", 5000.0, "Digital Ad Campaign, +60 Hype, 1.8x sales"),
                            Triple("Cinematic", 15000.0, "Cinematic Teasers, +150 Hype, 2.6x sales"),
                            Triple("Global Campaign", 40000.0, "Global Billboard Blitz, +350 Hype, 4.2x sales")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            campaigns.forEach { (tier, cost, effects) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (albumMarketingTier == tier) Color(0xFF1E1430) else Color.Transparent)
                                        .clickable { albumMarketingTier = tier }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = tier, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = if (cost == 0.0) "Free" else "Cost: $${String.format("%,.0f", cost)}", color = AccentGreen)
                                    Text(text = effects, color = NeonTeal, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val requiredSongCount = if (compileIsEP) 3..5 else 8..14
                                if (albumTitle.isBlank()) {
                                    viewModel.showToast("Please enter a title!")
                                } else if (selectedCompileSongIds.size !in requiredSongCount) {
                                    viewModel.showToast("An ${if (compileIsEP) "EP" else "Album"} requires ${requiredSongCount.first} to ${requiredSongCount.last} songs! You selected ${selectedCompileSongIds.size}.")
                                } else {
                                    val releaseType = if (compileIsEP) "EP" else "Studio LP"
                                    viewModel.compileAndReleaseAlbum(
                                        title = albumTitle,
                                        isEP = compileIsEP,
                                        songIds = selectedCompileSongIds.toList(),
                                        marketingTier = albumMarketingTier
                                    )
                                    viewModel.postActionFeedback(
                                        title = "New $releaseType Released: \"$albumTitle\"",
                                        subtitle = "Catalog compilation live · Distributed to digital platforms",
                                        deltas = listOf(
                                            StatFeedbackItem("Streams", "+15.2K", true, "streams"),
                                            StatFeedbackItem("Fans", "+480", true, "fans"),
                                            StatFeedbackItem("Image", "+2.5", true, "image"),
                                            StatFeedbackItem("Energy", "-40", false, "energy")
                                        ),
                                        iconType = "music"
                                    )
                                    // Reset fields on success
                                    albumTitle = ""
                                    selectedCompileSongIds = emptySet()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            modifier = Modifier.fillMaxWidth().testTag("compile_launch_btn")
                        ) {
                            Text("COMPILE & LAUNCH RELEASE (Est: -40 Energy)", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            // List Compiled Releases
            if (state.albums.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Released EPs & Albums",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(state.albums) { album ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = album.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${if (album.isEP) "Extended Play (EP)" else "Full LP"} • Released Week ${album.weekCreated}, Year ${album.yearCreated}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1430)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${String.format("%.1f", album.rating)}⭐ / 5.0",
                                        color = AccentGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Sales: ${String.format("%,d", album.sales)} copies",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Royalties Earned: $${String.format("%,.2f", album.albumCutEarned)}",
                                    color = AccentGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Album Hype: ${album.hype.toInt()}%",
                                    color = NeonTeal,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (album.promoWeeksLeft > 0) {
                                    Text(
                                        text = "🔥 Promo: ${album.promoWeeksLeft} wks left",
                                        color = AccentGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { album.hype / 100f },
                                color = NeonTeal,
                                trackColor = Color(0xFF2C2A3A),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )

                            if (album.promoWeeksLeft <= 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Launch Active Album Promo (holds hype & boosts weekly catalog streams):",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.startActiveAlbumPromo(album.id, "dsp") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("DSP ($3.5k)", fontSize = 10.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { viewModel.startActiveAlbumPromo(album.id, "radio") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("Radio ($12k)", fontSize = 10.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { viewModel.startActiveAlbumPromo(album.id, "global") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("Global ($35k)", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }

                            if (album.reviews.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Critical Reviews:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeonTeal,
                                    fontWeight = FontWeight.Bold
                                )
                                album.reviews.forEach { review ->
                                    Text(
                                        text = review,
                                        color = TextPrimary.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedStudioTab == "Record Song") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recorded Catalog",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.songs.size} tracks",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Solo", "Featured & Collabs").forEach { filter ->
                        FilterChip(
                            selected = catalogFilter == filter,
                            onClick = { catalogFilter = filter },
                            label = { Text(filter, fontSize = 11.sp) }
                        )
                    }
                }
            }

            val displayedSongs = state.songs.filterNot { song ->
                song.title.contains("Brand Endorsement", ignoreCase = true) ||
                song.title.contains("Royal Command", ignoreCase = true) ||
                song.title.contains("Apex Bank", ignoreCase = true) ||
                song.title.contains("Titan Capital", ignoreCase = true) ||
                song.title.contains("Aura Tech", ignoreCase = true) ||
                song.title.contains("Valerius Fashion", ignoreCase = true) ||
                song.title.contains("Paramount Studios", ignoreCase = true) ||
                song.title.contains("The Royal Secretariat", ignoreCase = true)
            }.filter { song ->
                when (catalogFilter) {
                    "Solo" -> song.featuredArtistName.isNullOrBlank() && !song.title.contains("feat.", ignoreCase = true)
                    "Featured & Collabs" -> !song.featuredArtistName.isNullOrBlank() || song.title.contains("feat.", ignoreCase = true)
                    else -> true
                }
            }

            if (displayedSongs.isEmpty()) {
                item {
                    Text(
                        text = if (state.songs.isEmpty()) "No recorded songs in your archive yet." else "No songs found for this filter.",
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                }
            } else {
                items(displayedSongs) { song ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = song.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(text = "${String.format("%.1f", song.rating)} / 5.0", color = TextSecondary, fontSize = 12.sp)
                                        }
                                        if (!song.featuredArtistName.isNullOrBlank() || song.title.contains("feat.", ignoreCase = true)) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFF311B92).copy(alpha = 0.6f)),
                                                shape = RoundedCornerShape(4.dp),
                                                border = BorderStroke(0.5.dp, NeonPurple.copy(alpha = 0.8f))
                                            ) {
                                                Text(
                                                    text = "🤝 Featured Collab",
                                                    color = Color(0xFFE1BEE7),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Upload platforms popup/options
                                Row {
                                    val platforms = listOf(
                                        Triple("tunes", "Tunes (Free)", "tunes"),
                                        Triple("sharp", "Sharp ($500)", "sharp"),
                                        Triple("jams", "Jams ($700)", "jams"),
                                        Triple("megaphone", "Megaphone ($1000)", "megaphone"),
                                        Triple("songify", "Songify ($1500)", "songify")
                                    )

                                    var showPlatformsMenu by remember { mutableStateOf(false) }

                                    Box {
                                        IconButton(onClick = { showPlatformsMenu = true }) {
                                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = "Upload", tint = NeonTeal)
                                        }

                                        DropdownMenu(
                                            expanded = showPlatformsMenu,
                                            onDismissRequest = { showPlatformsMenu = false }
                                        ) {
                                            platforms.forEach { (pId, name, icon) ->
                                                val uploaded = song.isUploadedMap.containsKey(pId)
                                                DropdownMenuItem(
                                                    text = { Text(text = if (uploaded) "$name (UPLOADED)" else name) },
                                                    onClick = {
                                                        showPlatformsMenu = false
                                                        viewModel.uploadSong(song.id, pId)
                                                    },
                                                    enabled = !uploaded
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (!song.isReleased) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⏳ UNRELEASED (Drops in ${song.weeksUntilRelease} weeks)",
                                        color = AccentGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Hype: ${song.hype.toInt()}%",
                                        color = NeonTeal,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                LinearProgressIndicator(
                                    progress = { song.hype / 100f },
                                    color = NeonTeal,
                                    trackColor = Color(0xFF2C2A3A),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Pre-Release Hype Campaign:",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        listOf(0 to "Budget", 1 to "Growth", 2 to "Viral Blitz").forEach { (idx, lbl) ->
                                            FilterChip(
                                                selected = hypeTierIndex == idx,
                                                onClick = { hypeTierIndex = idx },
                                                label = { Text(lbl, fontSize = 9.sp) },
                                                modifier = Modifier.height(24.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))

                                val (socialLbl, videoLbl, adLbl, radioLbl) = when(hypeTierIndex) {
                                    1 -> listOf("Reels ($1.5k)", "Visualizer ($3k)", "DSP Ads ($6k)", "Regional ($4.5k)")
                                    2 -> listOf("Viral ($5k)", "CGI Blitz ($10k)", "Global Ads ($20k)", "National ($16k)")
                                    else -> listOf("Social ($250)", "Snippet ($500)", "Indie ($1k)", "College ($800)")
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.promoteUnreleasedSong(song.id, "social", hypeTierIndex) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF231E3D)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text(socialLbl, fontSize = 9.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { viewModel.promoteUnreleasedSong(song.id, "video", hypeTierIndex) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF231E3D)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text(videoLbl, fontSize = 9.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { viewModel.promoteUnreleasedSong(song.id, "ad", hypeTierIndex) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF231E3D)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text(adLbl, fontSize = 9.sp, color = Color.White)
                                    }
                                    Button(
                                        onClick = { viewModel.promoteUnreleasedSong(song.id, "radio", hypeTierIndex) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF231E3D)),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text(radioLbl, fontSize = 9.sp, color = Color.White)
                                    }
                                }
                            } else {
                                // Released Song: Current Hype & Active Promotion
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Current Hype: ${song.hype.toInt()}%",
                                        color = NeonTeal,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (song.promoWeeksLeft > 0) {
                                        Text(
                                            text = "🔥 Active Promo: ${song.promoWeeksLeft} wks left",
                                            color = AccentGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { song.hype / 100f },
                                    color = NeonTeal,
                                    trackColor = Color(0xFF2C2A3A),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                )

                                if (song.promoWeeksLeft <= 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Launch Active Promotion (holds hype & boosts weekly streams):",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.startActiveSongPromo(song.id, "dsp") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(30.dp)
                                        ) {
                                            Text("DSP ($2.5k)", fontSize = 10.sp, color = Color.White)
                                        }
                                        Button(
                                            onClick = { viewModel.startActiveSongPromo(song.id, "radio") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(30.dp)
                                        ) {
                                            Text("Radio ($8.5k)", fontSize = 10.sp, color = Color.White)
                                        }
                                        Button(
                                            onClick = { viewModel.startActiveSongPromo(song.id, "global") },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838)),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(30.dp)
                                        ) {
                                            Text("Global ($25k)", fontSize = 10.sp, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                // Stats on platforms
                                if (song.isUploadedMap.isEmpty()) {
                                    Text(text = "Not uploaded to any streaming platforms yet. Click cloud upload icon above!", color = TextSecondary, fontSize = 12.sp)
                                } else {
                                    Text(text = "Released Platforms Streams:", color = TextSecondary, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    song.isUploadedMap.forEach { (platform, streams) ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = platform.uppercase(), color = NeonTeal, fontSize = 12.sp)
                                            Text(text = "${String.format("%,d", streams)} streams", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Copyright Status & Purchase
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (song.isCopyrightRegistered) Color(0xFF132B1F) else Color(0xFF2B1F14))
                                    .border(0.5.dp, if (song.isCopyrightRegistered) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFFFFB300).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Copyright",
                                        tint = if (song.isCopyrightRegistered) Color(0xFF00E676) else AccentGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (song.isCopyrightRegistered) "Federal Copyright Registered" else "Unregistered Master Rights",
                                        color = if (song.isCopyrightRegistered) Color(0xFF00E676) else AccentGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (!song.isCopyrightRegistered) {
                                    val copyrightFee = (song.budget * 0.15).coerceAtLeast(150.0)
                                    Button(
                                        onClick = { viewModel.registerSongCopyright(song.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Register ($${String.format("%,.0f", copyrightFee)} · 15%)", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (song.videoTier != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video", tint = NeonPink, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${song.videoTier} Music Video Active", color = NeonPink, fontSize = 12.sp)
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
fun ToursPage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedLevel by remember { mutableStateOf("Simple") }
    var promoBudget by remember { mutableStateOf("1000") }
    var isBookingFormExpanded by remember { mutableStateOf(false) }

    // Hyper-realistic Tour configurations
    var tourTitle by remember { mutableStateOf("") }
    var locationsCountInput by remember { mutableStateOf("5") }
    var ticketPriceInput by remember { mutableStateOf("50") }
    var tourPromoBudgetInput by remember { mutableStateOf("1500") }
    var productionTierOpt by remember { mutableStateOf("Indie / Raw Acoustic") }
    var transportTierOpt by remember { mutableStateOf("Local Transit") }
    var backupDancersOpt by remember { mutableStateOf("None") }

    val totalRevenue = state.tours.sumOf { it.totalGrossRevenue + it.totalMerchRevenueEarned }
    val totalMerchRevenue = state.tours.sumOf { it.totalMerchRevenueEarned }
    val totalAttendance = state.tours.sumOf { it.totalTicketsSold }
    val showcaseTour = state.tours.firstOrNull { it.status == "ACTIVE" }
        ?: state.tours.firstOrNull { it.status == "BOOKED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tour overview using saved tour data
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Massive Crowds Concert Graphic Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(NeonPink.copy(alpha = 0.5f), Color(0xFF0F0B1E))
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (showcaseTour?.status == "ACTIVE") "LIVE TOUR SESSION" else "TOUR OVERVIEW",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = showcaseTour?.title?.takeIf { it.isNotBlank() } ?: "Your tour starts here",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = showcaseTour?.let { "${it.level} Tour  •  ${it.locationsCount} cities  •  ${it.totalWeeks} weeks" }
                                    ?: "No tours booked yet  •  Plan your first tour below",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    // Financial metrics row (recorded revenue, merch, attendance)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Total Revenue", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = if (totalRevenue >= 1_000_000.0) "$${String.format("%,.2fM", totalRevenue / 1_000_000.0)}" else "$${String.format("%,.0f", totalRevenue)}",
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Merch Revenue", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = if (totalMerchRevenue >= 1_000_000.0) "$${String.format("%,.2fM", totalMerchRevenue / 1_000_000.0)}" else "$${String.format("%,.0f", totalMerchRevenue)}",
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Attendance", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = String.format("%,d", totalAttendance),
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Divider(color = BorderColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    // Next Show info block
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = showcaseTour?.status?.let {
                                    if (it == "ACTIVE") "CURRENT TOUR" else "UPCOMING TOUR"
                                } ?: "NO UPCOMING TOUR",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (showcaseTour == null) CardBg else NeonTeal.copy(alpha = 0.2f)),
                                border = BorderStroke(1.dp, if (showcaseTour == null) BorderColor else NeonTeal)
                            ) {
                                Text(
                                    text = showcaseTour?.status ?: "NOT BOOKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showcaseTour == null) TextSecondary else NeonTeal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = showcaseTour?.let { it.title.ifBlank { "${it.level} Tour" } } ?: "Book a tour to schedule your first stop",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = showcaseTour?.let {
                                if (it.status == "ACTIVE") "Current stop: ${it.currentWeek.coerceAtLeast(1)} of ${it.totalWeeks}  •  Tickets sold: ${String.format("%,d", it.totalTicketsSold)}"
                                else "Booked for Year ${it.bookedYear}, Week ${it.bookedWeek}  •  ${it.locationsCount} cities"
                            } ?: "Your real tour dates and attendance will appear here after booking.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    // Book/Manage Tour button
                    Button(
                        onClick = { isBookingFormExpanded = !isBookingFormExpanded },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (isBookingFormExpanded) "CLOSE TOUR BOOKING FORM" else "BOOK A NEW TOUR",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Expandable Tour booking form
        if (isBookingFormExpanded) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Tour Booker Terminal",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = tourTitle,
                            onValueChange = { tourTitle = it },
                            label = { Text("Tour Name / Title") },
                            placeholder = { Text("e.g. Neon Horizon Tour") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Tour Level / Tier Capacity:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))

                        val tiers = listOf(
                            Triple("Simple", "Cost: $2,000 base, 2 wks, Max 1,000 capacity/show", "Simple Level"),
                            Triple("Pro", "Cost: $5,000 base, 6 wks, Max 10,000 capacity/show", "Pro Level"),
                            Triple("Premium", "Cost: $10,000 base, 15 wks, Max 100,000 capacity/show", "Premium Level")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            tiers.forEach { (level, desc, label) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (selectedLevel == level) Color(0xFF1E192E) else Color.Transparent)
                                        .clickable { selectedLevel = level }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = label, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(text = desc, fontSize = 11.sp, color = TextSecondary)
                                    }
                                    RadioButton(
                                        selected = selectedLevel == level,
                                        onClick = { selectedLevel = level }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = locationsCountInput,
                                onValueChange = { locationsCountInput = it },
                                label = { Text("No. of Locations") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ticketPriceInput,
                                onValueChange = { ticketPriceInput = it },
                                label = { Text("Ticket Price ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = tourPromoBudgetInput,
                            onValueChange = { tourPromoBudgetInput = it },
                            label = { Text("Promotion/Marketing Budget ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Stage Concert Production Tier:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Basic Show" to "Basic Show (Free)", "Neon Laser Light Show" to "Laser Light Show ($10k)", "Hyper-Realistic Pyro & Hologram FX" to "Pyro & Hologram FX ($35k)").forEach { (key, label) ->
                                FilterChip(
                                    selected = productionTierOpt == key,
                                    onClick = { productionTierOpt = key },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Text("Transit Fleet Logistics Tier:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Standard Vans" to "Standard Vans (Free)", "Luxury Sleeper Bus" to "Luxury Sleeper Bus ($4k/wk)", "Chartered Private Jet" to "Private Jet ($20k/wk)").forEach { (key, label) ->
                                FilterChip(
                                    selected = transportTierOpt == key,
                                    onClick = { transportTierOpt = key },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Text("Stage Choreographers & Backup Dancers:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(0 to "No Dancers", 4 to "4 Dancers ($1.5k/wk)", 12 to "12 Elite Dancers ($4.5k/wk)").forEach { (count, label) ->
                                FilterChip(
                                    selected = backupDancersOpt == count.toString(),
                                    onClick = { backupDancersOpt = count.toString() },
                                    label = { Text(label) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dynamic live estimate calculations
                        val baseVal = when (selectedLevel) {
                            "Simple" -> 2000.0
                            "Pro" -> 5000.0
                            "Premium" -> 10000.0
                            else -> 2000.0
                        }
                        val weeksDuration = when (selectedLevel) {
                            "Simple" -> 2
                            "Pro" -> 6
                            "Premium" -> 15
                            else -> 2
                        }
                        val numLocs = locationsCountInput.toIntOrNull() ?: 5
                        val venueGuarCost = numLocs * when (selectedLevel) {
                            "Simple" -> 1500.0
                            "Pro" -> 8000.0
                            "Premium" -> 40000.0
                            else -> 1000.0
                        }
                        val prodCost = when (productionTierOpt) {
                            "Neon Laser Light Show" -> 10000.0
                            "Hyper-Realistic Pyro & Hologram FX" -> 35000.0
                            else -> 2000.0
                        }
                        val weeklyTransCost = when (transportTierOpt) {
                            "Luxury Sleeper Bus" -> 4000.0
                            "Chartered Private Jet" -> 20000.0
                            else -> 1000.0
                        }
                        val dancersCountVal = backupDancersOpt.toIntOrNull() ?: 0
                        val weeklyDancersCost = (dancersCountVal / 4) * 1500.0
                        val totalSetupCostEst = baseVal + venueGuarCost + prodCost + (weeklyTransCost + weeklyDancersCost) * weeksDuration + (tourPromoBudgetInput.toDoubleOrNull() ?: 1500.0)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B182B)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("ESTIMATED BOOKING RECEIPT SUMMARY", fontSize = 11.sp, color = NeonTeal, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Venue Guarantees ($numLocs shows): $${String.format("%,.0f", venueGuarCost)}", fontSize = 11.sp, color = TextSecondary)
                                Text("• Stage FX & Production: $${String.format("%,.0f", prodCost)}", fontSize = 11.sp, color = TextSecondary)
                                Text("• Operations ($weeksDuration wks transit/crew): $${String.format("%,.0f", (weeklyTransCost + weeklyDancersCost) * weeksDuration)}", fontSize = 11.sp, color = TextSecondary)
                                Text("• Marketing Campaign Budget: $${String.format("%,.0f", (tourPromoBudgetInput.toDoubleOrNull() ?: 1500.0))}", fontSize = 11.sp, color = TextSecondary)
                                Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))
                                Text("TOTAL SETUP COST: $${String.format("%,.0f", totalSetupCostEst)}", fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                val locsVal = locationsCountInput.toIntOrNull() ?: 5
                                val ticketPriceVal = ticketPriceInput.toDoubleOrNull() ?: 50.0
                                val promoVal = tourPromoBudgetInput.toDoubleOrNull() ?: 1500.0
                                val dancersCount = backupDancersOpt.toIntOrNull() ?: 0
                                val weeksLength = when (selectedLevel) {
                                    "Simple" -> 2
                                    "Pro" -> 6
                                    "Premium" -> 15
                                    else -> 2
                                }
                                viewModel.createDraftTour(
                                    title = tourTitle,
                                    level = selectedLevel,
                                    totalWeeks = weeksLength,
                                    locationsCount = locsVal,
                                    ticketPrice = ticketPriceVal,
                                    promoBudget = promoVal,
                                    productionTier = productionTierOpt,
                                    transportTier = transportTierOpt,
                                    backupDancersCount = dancersCount
                                )
                                isBookingFormExpanded = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("BOOK & GENERATE DRAFT TOUR RECEIPT", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Active/Booked tours title
        item {
            Text(
                text = "My Active Tours & Schedule",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.tours.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tours booked yet",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Plan your first tour using the setup form above. Once booked, its real dates, venues, attendance, and earnings will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(state.tours) { tour ->
                val isDraft = tour.status == "DRAFT"
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDraft) Color(0xFF151224) else CardBg
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (isDraft) NeonTeal.copy(alpha = 0.6f) else BorderColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = tour.title.ifEmpty { "${tour.level} Tour" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDraft) NeonTeal else TextPrimary
                                )
                                Text(
                                    text = "Tier: ${tour.level} Class",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (tour.status) {
                                        "ACTIVE" -> AccentGreen.copy(alpha = 0.2f)
                                        "COMPLETED" -> Color.Gray.copy(alpha = 0.2f)
                                        "DRAFT" -> NeonTeal.copy(alpha = 0.2f)
                                        else -> AccentGold.copy(alpha = 0.2f)
                                    }
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    when (tour.status) {
                                        "ACTIVE" -> AccentGreen
                                        "COMPLETED" -> Color.Gray
                                        "DRAFT" -> NeonTeal
                                        else -> AccentGold
                                    }
                                )
                            ) {
                                Text(
                                    text = tour.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (tour.status) {
                                        "ACTIVE" -> AccentGreen
                                        "COMPLETED" -> Color.Gray
                                        "DRAFT" -> NeonTeal
                                        else -> AccentGold
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isDraft) {
                            Text(
                                text = "OFFICIAL BOOKING RECEIPT DETAILS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "• Route Locations: ${tour.locationsCount} cities", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• Set Ticket Price: $${String.format("%,.2f", tour.ticketPrice)} per ticket", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• Stage Production: ${tour.productionTier}", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• Logistics Fleet: ${tour.transportTier}", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• Backup Dancers: ${tour.backupDancersCount} cast", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "• Marketing Guarantee: $${String.format("%,.0f", tour.promotionBudget)}", color = TextSecondary, fontSize = 12.sp)

                            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Est. Launch Costs", color = TextSecondary, fontSize = 11.sp)
                                    Text(
                                        text = "$${String.format("%,.0f", tour.setupCost)}",
                                        color = AccentGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Button(
                                    onClick = { viewModel.launchTour(tour.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("🚀 LAUNCH TOUR NOW", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Text(text = "Weeks Played: ${tour.currentWeek} / ${tour.totalWeeks}", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "Weekly Energy Cost: ${tour.weeklyEnergyCost}%  |  Weekly Time Cost: ${tour.weeklyTimeCost} slots", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "Booked Calendar: Year ${tour.bookedYear}, Week ${tour.bookedWeek}", color = TextSecondary, fontSize = 12.sp)
                            Text(text = "Max Attendance Reached: ${String.format("%,d", tour.baseAttendance)} fans", color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tour Hype & Traction", color = TextSecondary, fontSize = 11.sp)
                                Text("${tour.showHype.toInt()}%", color = NeonTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (tour.showHype / 150f).coerceAtMost(1f) },
                                color = NeonTeal,
                                trackColor = Color(0xFF1E1C2A),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )

                            if (tour.status == "ACTIVE" || tour.status == "BOOKED") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.promoteTour(tour.id, 1000.0) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonTeal),
                                        border = BorderStroke(1.dp, NeonTeal),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("RUN CAMPAIGN (+$1,000 Hype)")
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
fun TradingPage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTradeTab by remember { mutableStateOf("Stocks") }

    val stockValue = state.stocks.sumOf { it.quantity * it.currentPrice }
    val cryptoValue = state.cryptos.sumOf { it.quantity * it.currentPrice }
    val totalPortfolioValue = stockValue + cryptoValue

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-fidelity Portfolio Summary Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TRADING PORTFOLIO NET WORTH",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$${String.format("%,.2f", totalPortfolioValue)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BorderColor)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("STOCK HOLDINGS", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${String.format("%,.2f", stockValue)}",
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(30.dp)
                                .background(BorderColor)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("CRYPTO HOLDINGS", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$${String.format("%,.2f", cryptoValue)}",
                                color = NeonPink,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        item {
            TabRow(
                selectedTabIndex = if (selectedTradeTab == "Stocks") 0 else 1,
                containerColor = CardBg,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTradeTab == "Stocks",
                    onClick = { selectedTradeTab = "Stocks" },
                    text = { Text("Equity Stocks") }
                )
                Tab(
                    selected = selectedTradeTab == "Crypto",
                    onClick = { selectedTradeTab = "Crypto" },
                    text = { Text("Crypto Assets") }
                )
            }
        }

        if (selectedTradeTab == "Stocks") {
            items(state.stocks) { stock ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stock.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (stock.connectedTo != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonTeal.copy(alpha = 0.12f))
                                            .border(0.5.dp, NeonTeal, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Connected to Live Tours",
                                            color = NeonTeal,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "$${String.format("%.2f", stock.currentPrice)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                                   Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Shares Owned:", color = TextSecondary, fontSize = 13.sp)
                            Text(text = "${stock.quantity} shares", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        if (stock.quantity > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Average Cost:", color = TextSecondary, fontSize = 13.sp)
                                Text(text = "$${String.format("%.2f", stock.averagePurchasePrice)}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            val stockCost = stock.quantity * stock.averagePurchasePrice
                            val stockCurrentValue = stock.quantity * stock.currentPrice
                            val stockPnl = stockCurrentValue - stockCost
                            val stockPnlPercent = if (stockCost > 0.0) (stockPnl / stockCost) * 100.0 else 0.0
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Unrealized P&L:", color = TextSecondary, fontSize = 13.sp)
                                val pnlText = if (stockPnl >= 0.0) {
                                    "+$${String.format("%,.2f", stockPnl)} (+${String.format("%.2f", stockPnlPercent)}%)"
                                } else {
                                    "-$${String.format("%,.2f", kotlin.math.abs(stockPnl))} (${String.format("%.2f", stockPnlPercent)}%)"
                                }
                                Text(
                                    text = pnlText,
                                    color = if (stockPnl >= 0.0) AccentGreen else NeonPink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Weekly Dividend Yield:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = "${String.format("%.1f%%", stock.weeklyDividendPercent * 100)} / wk",
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.buyStock(stock.id, 10) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("BUY 10", fontWeight = FontWeight.Bold)
                            }

                            val canSell = stock.quantity >= 10
                            OutlinedButton(
                                onClick = { viewModel.sellStock(stock.id, 10) },
                                border = if (canSell) BorderStroke(1.dp, NeonPink) else BorderStroke(1.dp, BorderColor),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (canSell) NeonPink else TextSecondary.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.weight(1f),
                                enabled = canSell
                            ) {
                                Text("SELL 10", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Cryptocurrency tab
            items(state.cryptos) { crypto ->
                val prevPrice = if (crypto.history.size >= 2) crypto.history[crypto.history.size - 2] else crypto.currentPrice
                val change24h = if (prevPrice > 0.0) ((crypto.currentPrice - prevPrice) / prevPrice) * 100.0 else 0.0
                val isPositive24h = change24h >= 0.0
                val formattedMcap = when {
                    crypto.marketCap >= 1_000_000_000.0 -> "$${String.format("%.2fB", crypto.marketCap / 1_000_000_000.0)}"
                    crypto.marketCap >= 1_000_000.0 -> "$${String.format("%.1fM", crypto.marketCap / 1_000_000.0)}"
                    crypto.marketCap >= 1_000.0 -> "$${String.format("%.1fK", crypto.marketCap / 1_000.0)}"
                    else -> "$${String.format("%,.0f", crypto.marketCap)}"
                }
                val formattedVol = when {
                    crypto.volume24h >= 1_000_000_000.0 -> "$${String.format("%.2fB", crypto.volume24h / 1_000_000_000.0)}"
                    crypto.volume24h >= 1_000_000.0 -> "$${String.format("%.1fM", crypto.volume24h / 1_000_000.0)}"
                    crypto.volume24h >= 1_000.0 -> "$${String.format("%.0fK", crypto.volume24h / 1_000.0)}"
                    else -> "$${String.format("%,.0f", crypto.volume24h)}"
                }
                val formattedSupply = when {
                    crypto.circulatingSupply >= 1_000_000_000.0 -> "${String.format("%.1fB", crypto.circulatingSupply / 1_000_000_000.0)}"
                    crypto.circulatingSupply >= 1_000_000.0 -> "${String.format("%.1fM", crypto.circulatingSupply / 1_000_000.0)}"
                    crypto.circulatingSupply >= 1_000.0 -> "${String.format("%.0fK", crypto.circulatingSupply / 1_000.0)}"
                    crypto.circulatingSupply > 0.0 -> "${String.format("%,.0f", crypto.circulatingSupply)}"
                    else -> "N/A"
                }
                val npcSentiment = when {
                    change24h >= 15.0 -> "🚀 Frenzy NPC Buy Volume"
                    change24h >= 4.0 -> "🟢 Heavy NPC Accumulation"
                    change24h <= -15.0 -> "💥 Panic NPC Capitulation"
                    change24h <= -4.0 -> "🔴 NPC Profit Taking"
                    else -> "🟡 Steady Organic Trading"
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, if (crypto.id.startsWith("crypto_token_")) NeonPurple.copy(alpha = 0.8f) else BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header: Name, Ticker badge, Price & 24h change
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = crypto.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (crypto.id.startsWith("crypto_token_")) {
                                        Surface(
                                            color = NeonPurple.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(0.8.dp, NeonPurple)
                                        ) {
                                            Text(
                                                "YOUR TOKEN",
                                                color = NeonPurple,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = npcSentiment,
                                    fontSize = 11.sp,
                                    color = if (isPositive24h) AccentGreen else if (change24h < 0.0) NeonPink else TextSecondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val priceFormat = if (crypto.currentPrice < 0.01) "%.6f" else if (crypto.currentPrice < 1.0) "%.4f" else "%,.2f"
                                Text(
                                    text = "$${String.format(priceFormat, crypto.currentPrice)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                                    color = AccentGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = if (isPositive24h) AccentGreen.copy(alpha = 0.15f) else NeonPink.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "24h: ${if (isPositive24h) "+" else ""}${String.format("%.1f", change24h)}%",
                                        color = if (isPositive24h) AccentGreen else NeonPink,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Market Stats Row: Market Cap, 24h Volume, Circulating Supply
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBg.copy(alpha = 0.6f))
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text("Market Cap", color = TextSecondary, fontSize = 10.sp)
                                Text(formattedMcap, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("24h Volume", color = TextSecondary, fontSize = 10.sp)
                                Text(formattedVol, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Circ. Supply", color = TextSecondary, fontSize = 10.sp)
                                Text(formattedSupply, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        // Mini Sparkline Price Movement
                        if (crypto.history.size >= 2) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val minP = crypto.history.minOrNull() ?: 1.0
                            val maxP = (crypto.history.maxOrNull() ?: 1.0).coerceAtLeast(minP + 0.000001)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF0F0F1A))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                crypto.history.takeLast(10).forEach { p ->
                                    val heightFraction = ((p - minP) / (maxP - minP)).toFloat().coerceIn(0.15f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 2.dp)
                                            .fillMaxHeight(heightFraction)
                                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                                            .background(if (isPositive24h) AccentGreen.copy(alpha = 0.8f) else NeonPink.copy(alpha = 0.8f))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Holdings Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Holdings Owned:", color = TextSecondary, fontSize = 13.sp)
                            val tokenDisplay = if (crypto.quantity >= 1000.0) String.format("%,.0f", crypto.quantity) else String.format("%.4f", crypto.quantity)
                            Text(
                                text = "$tokenDisplay tokens",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (crypto.quantity > 0.0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Average Cost:", color = TextSecondary, fontSize = 13.sp)
                                val avgPriceFormat = if (crypto.averagePurchasePrice < 0.01) "%.6f" else if (crypto.averagePurchasePrice < 1.0) "%.4f" else "%,.2f"
                                Text(text = "$${String.format(avgPriceFormat, crypto.averagePurchasePrice)}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                            }
                            val cryptoCost = crypto.quantity * crypto.averagePurchasePrice
                            val cryptoCurrentValue = crypto.quantity * crypto.currentPrice
                            val cryptoPnl = cryptoCurrentValue - cryptoCost
                            val cryptoPnlPercent = if (cryptoCost > 0.0) (cryptoPnl / cryptoCost) * 100.0 else 0.0
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Unrealized P&L:", color = TextSecondary, fontSize = 13.sp)
                                val pnlText = if (cryptoPnl >= 0.0) {
                                    "+$${String.format("%,.2f", cryptoPnl)} (+${String.format("%.2f", cryptoPnlPercent)}%)"
                                } else {
                                    "-$${String.format("%,.2f", kotlin.math.abs(cryptoPnl))} (${String.format("%.2f", cryptoPnlPercent)}%)"
                                }
                                Text(
                                    text = pnlText,
                                    color = if (cryptoPnl >= 0.0) AccentGreen else NeonPink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Buy / Sell Action Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.buyCrypto(crypto.id, 500.0) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("BUY $500", fontWeight = FontWeight.Bold)
                            }

                            val canSell = crypto.quantity > 0.0
                            val sellAmt = when {
                                crypto.quantity <= 1.0 -> crypto.quantity
                                crypto.currentPrice < 1.0 -> (crypto.quantity * 0.25).coerceAtLeast(10.0).coerceAtMost(crypto.quantity)
                                else -> (crypto.quantity * 0.25).coerceAtLeast(0.01).coerceAtMost(crypto.quantity)
                            }
                            OutlinedButton(
                                onClick = { viewModel.sellCrypto(crypto.id, sellAmt) },
                                border = if (canSell) BorderStroke(1.dp, NeonPink) else BorderStroke(1.dp, BorderColor),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (canSell) NeonPink else TextSecondary.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.weight(1f),
                                enabled = canSell
                            ) {
                                val label = if (crypto.quantity > 1000.0) "SELL 25%" else "SELL PART"
                                Text(label, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideJobsPage(state: GameStateEntity, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Side Jobs Gig Board",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Perform side gigs to survive and earn cash weekly. Gigs require weekly energy and time slots.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        items(state.sideJobs) { job ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.2.dp, if (job.isActive) NeonTeal else BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = job.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (job.isActive) NeonTeal else TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Weekly Pay: $${String.format("%,.0f", job.weeklyPay)}",
                                color = AccentGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Switch(
                            checked = job.isActive,
                            onCheckedChange = {
                                viewModel.toggleSideJob(job.id)
                                val actionTitle = if (!job.isActive) "Started Gig: ${job.name}" else "Left Gig: ${job.name}"
                                val actionSub = if (!job.isActive) "Contract signed · Weekly shift scheduled" else "Shift cancelled · Energy reclaimed"
                                viewModel.postActionFeedback(
                                    title = actionTitle,
                                    subtitle = actionSub,
                                    deltas = if (!job.isActive) listOf(
                                        StatFeedbackItem("Weekly Pay", "+$${String.format("%,.0f", job.weeklyPay)}", true, "money"),
                                        StatFeedbackItem("Energy Drain", "-${job.energyCost}%/wk", false, "energy")
                                    ) else listOf(
                                        StatFeedbackItem("Energy Restored", "+${job.energyCost}%/wk", true, "energy")
                                    ),
                                    iconType = "work"
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonTeal,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = Color(0xFF1B1826)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonPurple.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Energy cost: ${job.energyCost}% / wk",
                                color = Purple80,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonPink.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Time slot cost: ${job.timeCost} slots",
                                color = Pink80,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialPracticePage(state: GameStateEntity, viewModel: GameViewModel) {
    var showPsychologyDialogNpc by remember { mutableStateOf<NPCEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-fidelity Social Analytics Dashboard Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Social Media Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Total Followers", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%,d", state.followers),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Industry Followers", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%,d", state.industryFollowers),
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Trending Score", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.1f%%", state.trendingScore),
                                color = NeonPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Conversation Vol.", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%,d/wk", state.conversationVolume),
                                color = AccentGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Public Sentiment Analysis",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Segmented sentiment gauge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color(0xFF2C2A3A))
                    ) {
                        val posWeight = (state.positiveSentiment / 100f).coerceIn(0.01f, 1f)
                        val neuWeight = (state.neutralSentiment / 100f).coerceIn(0.01f, 1f)
                        val negWeight = (state.negativeSentiment / 100f).coerceIn(0.01f, 1f)
                        val totalWeight = posWeight + neuWeight + negWeight

                        Box(modifier = Modifier.weight(posWeight / totalWeight).fillMaxHeight().background(AccentGreen))
                        Box(modifier = Modifier.weight(neuWeight / totalWeight).fillMaxHeight().background(Color(0xFF707383)))
                        Box(modifier = Modifier.weight(negWeight / totalWeight).fillMaxHeight().background(NeonPink))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Positive: ${String.format("%.0f%%", state.positiveSentiment)}", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Neutral: ${String.format("%.0f%%", state.neutralSentiment)}", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Negative: ${String.format("%.0f%%", state.negativeSentiment)}", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Character Traits Progress Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Stewardship & Character Traits",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val traits = listOf(
                        Triple("Business Trust", state.businessReputation, Color(0xFF29B6F6)),
                        Triple("Discipline", state.discipline, NeonPurple),
                        Triple("Integrity", state.integrity, AccentGreen),
                        Triple("Generosity", state.generosity, NeonPink),
                        Triple("Humility", state.humility, AccentGold),
                        Triple("Reliability", state.reliability, NeonTeal)
                    )

                    traits.forEachIndexed { index, (name, value, color) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.width(100.dp))
                            LinearProgressIndicator(
                                progress = value / 100f,
                                color = color,
                                trackColor = Color(0xFF1B1826),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = String.format("%.0f", value),
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                        }
                        if (index < traits.size - 1) {
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }

        // Financial Education Self-Study Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Wealth Education & Self-Study",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Invest in learning. Study financial principles or consult professionals to make sound business decisions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.readBusinessBook()
                                viewModel.postActionFeedback(
                                    title = "Studied Financial Economics",
                                    subtitle = "Mastered cashflow management & asset stewardship",
                                    deltas = listOf(
                                        StatFeedbackItem("Business Reputation", "+2.5", true, "skill"),
                                        StatFeedbackItem("Energy", "-25", false, "energy"),
                                        StatFeedbackItem("Cash", "-$100", false, "money")
                                    ),
                                    iconType = "skill"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1826)),
                            border = BorderStroke(1.dp, Color(0xFF2C283F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Read Fin Book", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Energy -25 • $100", fontSize = 8.sp, color = NeonPurple)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.hireBusinessConsultant()
                                viewModel.postActionFeedback(
                                    title = "Executive Advisory Session",
                                    subtitle = "Contract valuation & royalty strategy consulting",
                                    deltas = listOf(
                                        StatFeedbackItem("Business Reputation", "+6.0", true, "skill"),
                                        StatFeedbackItem("Energy", "-15", false, "energy"),
                                        StatFeedbackItem("Cash", "-$1,500", false, "money")
                                    ),
                                    iconType = "skill"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1826)),
                            border = BorderStroke(1.dp, Color(0xFF2C283F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Consult Advisor", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Energy -15 • $1.5k", fontSize = 8.sp, color = NeonTeal)
                            }
                        }
                    }
                }
            }
        }

        // Training Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Practice & Master Skills",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.practiceVoice()
                                viewModel.postActionFeedback(
                                    title = "Vocal Range & Pitch Practice",
                                    subtitle = "Tonal accuracy & breath control drills completed",
                                    deltas = listOf(
                                        StatFeedbackItem("Vocal Skill", "+2.5", true, "skill"),
                                        StatFeedbackItem("Energy", "-20", false, "energy")
                                    ),
                                    iconType = "skill"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1826)),
                            border = BorderStroke(1.dp, Color(0xFF2C283F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Voice Training", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Energy -20", fontSize = 8.sp, color = NeonPurple)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.practiceWriting()
                                viewModel.postActionFeedback(
                                    title = "Lyricism & Composition Writing",
                                    subtitle = "Melodic rhythm & wordplay refined",
                                    deltas = listOf(
                                        StatFeedbackItem("Lyric Skill", "+2.5", true, "skill"),
                                        StatFeedbackItem("Creativity", "+1.8", true, "creative"),
                                        StatFeedbackItem("Energy", "-20", false, "energy")
                                    ),
                                    iconType = "skill"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1826)),
                            border = BorderStroke(1.dp, Color(0xFF2C283F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Write Songs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Energy -20", fontSize = 8.sp, color = NeonTeal)
                            }
                        }
                    }
                }
            }
        }

        // Social posting card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Engage on Social Media",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Posting regularly keeps your brand active, gets fans, and lets you promote your music.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.postOnSocialMedia()
                            viewModel.postActionFeedback(
                                title = "New Social Post Broadcasted",
                                subtitle = "Cast timeline algorithm engagement boosted",
                                deltas = listOf(
                                    StatFeedbackItem("Fans", "+38", true, "fans"),
                                    StatFeedbackItem("Public Image", "+0.7", true, "image"),
                                    StatFeedbackItem("Energy", "-15", false, "energy")
                                ),
                                iconType = "social"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("POST UPDATE (Energy -15)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        item {
            // Title for Simulated NPC connections
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Simulated Industry Connections",
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonTeal,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Interact with producers, artists, and advisors. Establish alliances or trigger hot rivalries.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        items(state.npcs) { npc ->
            val relationshipColor = when {
                npc.relationshipWithPlayer < 35f -> NeonPink
                npc.relationshipWithPlayer > 75f -> AccentGreen
                else -> BorderColor
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.2.dp, relationshipColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = npc.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${npc.genrePreference} • Age ${npc.age})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Personality: ${npc.personality} • Net Worth: $${String.format("%,.0f", npc.wealth)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        // Status Badge
                        val relationText = when {
                            npc.isRetired -> "Retired Mentor"
                            npc.relationshipWithPlayer > 75f -> "Close Ally"
                            npc.relationshipWithPlayer < 35f -> "Bitter Rival"
                            else -> "Neutral Competitor"
                        }
                        val badgeColor = when {
                            npc.isRetired -> AccentGold
                            npc.relationshipWithPlayer > 75f -> AccentGreen
                            npc.relationshipWithPlayer < 35f -> NeonPink
                            else -> Color(0xFF29B6F6)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(badgeColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .border(1.dp, badgeColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = relationText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = badgeColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { showPsychologyDialogNpc = npc },
                                modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Psychology Profile",
                                    tint = NeonTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    StatRow(label = "Popularity Competition", percent = npc.popularity, color = NeonPink)
                    Spacer(modifier = Modifier.height(4.dp))
                    StatRow(label = "Relationship with You", percent = npc.relationshipWithPlayer, color = NeonPurple)

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "INTERACTION PROTOCOLS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.strengthenFriendship(npc.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1826)),
                            border = BorderStroke(1.dp, Color(0xFF2C283F)),
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Grab Dinner", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Cost: $500", fontSize = 8.sp, color = AccentGreen)
                            }
                        }

                        Button(
                            onClick = { viewModel.startRivalry(npc.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1826)),
                            border = BorderStroke(1.dp, Color(0xFF2C283F)),
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Start Rivalry", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Cost: 15 Energy", fontSize = 8.sp, color = NeonPink)
                            }
                        }

                        Button(
                            onClick = { viewModel.produceForNPC(npc.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1826)),
                            border = BorderStroke(1.dp, Color(0xFF2C283F)),
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Produce Track", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Up to $15k", fontSize = 8.sp, color = NeonTeal)
                            }
                        }
                    }
                }
            }
        }
    }

    showPsychologyDialogNpc?.let { targetNpc ->
        val latestNpcState = state.npcs.find { it.id == targetNpc.id } ?: targetNpc
        NpcPsychologyDialog(npc = latestNpcState, onClose = { showPsychologyDialogNpc = null })
    }
}

@Composable
fun MarketplacePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Migration Announcement Card
        Card(
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131F17))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF00E676), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("COMMERCE MOVED TO WEBHUB", color = Color(0xFF00E676), fontSize = 13.sp, fontWeight = FontWeight.Black)
                        Text("WebHub Smartphone Browser", color = Color.Gray, fontSize = 10.sp)
                    }
                }

                Text(
                    text = "All asset acquisition and ecommerce have migrated into the official WebHub internet browser on your smartphone. Browse dedicated sites with real-time auctions, custom listings, and master audio catalogs.",
                    color = Color(0xFFD1D5DB),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Destination 1: Alphadom.online (Instruments & Luxury)
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C15)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFD4AF37), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text("Alphadom Audio & Luxury", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("alphadom.online", color = Color(0xFFD4AF37), fontSize = 10.sp)
                        }
                    }
                    Surface(color = Color(0xFFD4AF37).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("${state.ownedInstruments.size} OWNED", color = Color(0xFFD4AF37), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Text(
                    text = "Shop professional polyphonic synthesizers (Moog, Prophet, Jupiter), vintage guitars, Neve preamps, Sony tube microphones, and prestige luxury timepieces.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Button(
                    onClick = {
                                                viewModel.showToast("Opening Alphadom on WebHub...")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("OPEN ALPHADOM STORE", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Destination 2: Sold.com (Real Estate & Vehicles)
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141D26)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF38BDF8), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text("Sold.com Real Estate & Assets", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("sold.com", color = Color(0xFF38BDF8), fontSize = 10.sp)
                        }
                    }
                    Surface(color = Color(0xFF38BDF8).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("${state.ownedHouses.size + state.ownedCars.size} ASSETS", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Text(
                    text = "Acquire luxury skyline penthouses, coastal recording villas, and exotic supercars (Ferrari SF90, Rolls-Royce Phantom) with weekly energy and prestige bonuses.",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )

                Button(
                    onClick = {
                                                viewModel.showToast("Opening Sold.com on WebHub...")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("OPEN SOLD.COM AUCTIONS", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}


@Composable
fun RecordLabelPage(state: GameStateEntity, viewModel: GameViewModel) {
    var labelNameInput by remember { mutableStateOf("") }
    var selectedLabelTab by remember { mutableStateOf("Overview") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Brand Header Section
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val companyInitials = if (state.ownLabel != null) {
                        state.ownLabel.name.take(2).uppercase()
                    } else if (state.signedLabelId != null) {
                        state.signedLabelId.take(2).uppercase()
                    } else {
                        state.artistName.take(2).uppercase()
                    }

                    val companyName = if (state.ownLabel != null) {
                        state.ownLabel.name
                    } else if (state.signedLabelId != null) {
                        "Signed with ${state.signedLabelId}"
                    } else {
                        "${state.artistName} Entertainment"
                    }

                    // Brand Initial Logo Circular Gradient
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(AccentGold, NeonPurple)
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = companyInitials,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = companyName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    val companyStatus = if (state.ownLabel != null) "Independent Label" else if (state.signedLabelId != null) "Under Label Contract" else "Self-Managed Entity"
                    val labelValText = if (state.ownLabel != null) {
                        "$${String.format("%,.2f", state.ownLabel.valuation)}"
                    } else {
                        "$14.25M"
                    }
                    Text(
                        text = "$companyStatus  •  Since Jan 1, 2024  •  Valuation: $labelValText",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Horizontal Category Tabs (Overview, Departments, Projects, Finances)
        item {
            val tabsList = if (state.ownLabel != null) {
                listOf("Overview", "Corporate", "Boardroom", "Departments")
            } else {
                listOf("Overview", "Departments")
            }
            TabRow(
                selectedTabIndex = tabsList.indexOf(selectedLabelTab).coerceAtLeast(0),
                containerColor = CardBg,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabsList.forEach { tabName ->
                    Tab(
                        selected = selectedLabelTab == tabName,
                        onClick = { selectedLabelTab = tabName },
                        text = { Text(tabName, fontSize = 11.sp, maxLines = 1) }
                    )
                }
            }
        }

        if (selectedLabelTab == "Overview") {
            // Stats grid card (Image 3)
            if (state.ownLabel != null) {
                // Stats grid card for OWN label
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BorderColor),
                            colors = CardDefaults.cardColors(containerColor = CardBg)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Monthly Revenue", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$${String.format("%,.0f", state.ownLabel.companyCash * 0.12)}", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("▲ Stable", color = AccentGreen, fontSize = 9.sp)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BorderColor),
                            colors = CardDefaults.cardColors(containerColor = CardBg)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Available Cash", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("$${String.format("%,.0f", state.ownLabel.companyCash)}", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            } else if (state.signedLabelId != null) {
                // Dashboard stats for SIGNED label
                item {
                    val labelVal = (state.popularity * 0.4f) + (state.influence * 0.3f) + (state.industryRespect * 0.3f)
                    val valueLevel = when {
                        labelVal > 85 -> "Superstar (Priority)"
                        labelVal > 60 -> "Valuable Asset"
                        labelVal > 35 -> "Developing Artist"
                        else -> "Bottom Roster"
                    }
                    val royaltyBonus = when {
                        labelVal > 85 -> "+3% Royalty"
                        labelVal > 60 -> "+1% Royalty"
                        else -> "Standard Rate"
                    }
                    val perks = when {
                        labelVal > 85 -> "Promo & Studio Bill"
                        labelVal > 60 -> "Partial Promo Support"
                        else -> "No special perks"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BorderColor),
                            colors = CardDefaults.cardColors(containerColor = CardBg)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Your Label Value", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(valueLevel, color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BorderColor),
                            colors = CardDefaults.cardColors(containerColor = CardBg)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Current Perks", color = TextSecondary, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(perks, color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(royaltyBonus, color = NeonTeal, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            if (state.signedLabelId != null) {
                item {
                    val labelId = state.signedLabelId ?: ""
                    val labelNameFormatted = labelId.replace("_", " ").split(" ")
                        .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
                    val isExpired = state.signedLabelEndYear > 0 && (
                        state.currentYear > state.signedLabelEndYear ||
                        (state.currentYear == state.signedLabelEndYear && state.currentWeek >= state.signedLabelEndWeek)
                    )
                    val weeksRemaining = if (state.signedLabelEndYear > 0) {
                        ((state.signedLabelEndYear - state.currentYear) * 52 + (state.signedLabelEndWeek - state.currentWeek)).coerceAtLeast(0)
                    } else 0
                    val fineAmount = if (isExpired) 0.0 else (25000.0 + (state.popularity * 750.0)).coerceAtLeast(15000.0)
                    val lawyer = state.hiredEmployees.find { it.role == "Lawyer" }
                    val lawyerBaseCost = if (lawyer != null) 5000.0 else 20000.0
                    val totalLawyerCost = lawyerBaseCost + state.recoupableAdvanceBalance

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header: Label Name & Status Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = labelNameFormatted,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = NeonTeal,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = "${state.signedLabelRole ?: "Recording Artist"} • Tier ${state.signedLabelLevel}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                                Surface(
                                    color = if (isExpired) AccentGreen.copy(alpha = 0.2f) else AccentGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, if (isExpired) AccentGreen else AccentGold)
                                ) {
                                    Text(
                                        text = if (isExpired) "Term Expired" else "Active Term",
                                        color = if (isExpired) AccentGreen else AccentGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderColor)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Contract Term and Royalty Split Metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                                    border = BorderStroke(0.5.dp, BorderColor),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Label Cut", color = TextSecondary, fontSize = 11.sp)
                                        Text(
                                            "${(state.signedLabelCut * 100).toInt()}%",
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text("Artist keeps ${100 - (state.signedLabelCut * 100).toInt()}%", color = TextSecondary, fontSize = 9.sp)
                                    }
                                }
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                                    border = BorderStroke(0.5.dp, BorderColor),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Term Length", color = TextSecondary, fontSize = 11.sp)
                                        Text(
                                            if (isExpired) "Completed" else "$weeksRemaining wks left",
                                            color = if (isExpired) AccentGreen else TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            if (state.signedLabelEndYear > 0) "Thru Y${state.signedLabelEndYear} W${state.signedLabelEndWeek}" else "Indefinite",
                                            color = TextSecondary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Deliverables Tracking
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Contract Deliverables", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val singlesDone = state.signedLabelDeliveredSingles >= state.signedLabelReqSingles
                                val lpDone = state.signedLabelDeliveredAlbumsLP >= state.signedLabelReqAlbumsLP
                                val epDone = state.signedLabelDeliveredAlbumsEP >= state.signedLabelReqAlbumsEP
                                val vidDone = state.signedLabelDeliveredMeTubeVideos >= state.signedLabelReqMeTubeVideos

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Singles", fontSize = 10.sp, color = TextSecondary)
                                    Text("${state.signedLabelDeliveredSingles} / ${state.signedLabelReqSingles}", color = if (singlesDone) AccentGreen else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("LP Albums", fontSize = 10.sp, color = TextSecondary)
                                    Text("${state.signedLabelDeliveredAlbumsLP} / ${state.signedLabelReqAlbumsLP}", color = if (lpDone) AccentGreen else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("EP Albums", fontSize = 10.sp, color = TextSecondary)
                                    Text("${state.signedLabelDeliveredAlbumsEP} / ${state.signedLabelReqAlbumsEP}", color = if (epDone) AccentGreen else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Videos", fontSize = 10.sp, color = TextSecondary)
                                    Text("${state.signedLabelDeliveredMeTubeVideos} / ${state.signedLabelReqMeTubeVideos}", color = if (vidDone) AccentGreen else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            // Recoupable Advance Balance & Repayments
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                                border = BorderStroke(0.5.dp, BorderColor),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Recoupable Advance Balance", color = TextSecondary, fontSize = 11.sp)
                                            Text(
                                                text = if (state.recoupableAdvanceBalance > 0.0) "$${String.format("%,.2f", state.recoupableAdvanceBalance)}" else "$0.00 (Fully Recouped)",
                                                color = if (state.recoupableAdvanceBalance > 0.0) AccentGold else AccentGreen,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }
                                        if (state.recoupableAdvanceBalance > 0.0) {
                                            Surface(
                                                color = AccentGold.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    "Recouped from royalties",
                                                    color = AccentGold,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (state.recoupableAdvanceBalance > 0.0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Pay down advance balance with personal cash reserves:",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            if (state.recoupableAdvanceBalance >= 5000.0) {
                                                Button(
                                                    onClick = { viewModel.payOffRecordLabelAdvance(5000.0) },
                                                    enabled = state.cash >= 5000.0,
                                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceColor),
                                                    border = BorderStroke(1.dp, NeonTeal),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.weight(1f),
                                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                                                ) {
                                                    Text("Pay $5K", fontSize = 11.sp, color = NeonTeal, maxLines = 1)
                                                }
                                            }
                                            if (state.recoupableAdvanceBalance >= 25000.0) {
                                                Button(
                                                    onClick = { viewModel.payOffRecordLabelAdvance(25000.0) },
                                                    enabled = state.cash >= 25000.0,
                                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceColor),
                                                    border = BorderStroke(1.dp, NeonTeal),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.weight(1f),
                                                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                                                ) {
                                                    Text("Pay $25K", fontSize = 11.sp, color = NeonTeal, maxLines = 1)
                                                }
                                            }
                                            Button(
                                                onClick = { viewModel.payOffRecordLabelAdvance(state.recoupableAdvanceBalance) },
                                                enabled = state.cash >= state.recoupableAdvanceBalance,
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal.copy(alpha = 0.2f)),
                                                border = BorderStroke(1.dp, NeonTeal),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.weight(1.2f),
                                                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                                            ) {
                                                Text(
                                                    "Pay Off All",
                                                    fontSize = 11.sp,
                                                    color = NeonTeal,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Deliverables Progress
                            if (state.signedLabelReqSingles > 0 || state.signedLabelReqAlbumsLP > 0 || state.signedLabelReqAlbumsEP > 0 || state.signedLabelReqMeTubeVideos > 0) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Contract Deliverables:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                if (state.signedLabelReqSingles > 0) {
                                    val isMet = state.signedLabelDeliveredSingles >= state.signedLabelReqSingles
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Commercial Singles", color = TextPrimary, fontSize = 12.sp)
                                        Text(
                                            "${state.signedLabelDeliveredSingles} / ${state.signedLabelReqSingles} ${if (isMet) "✓" else ""}",
                                            color = if (isMet) AccentGreen else AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                if (state.signedLabelReqAlbumsLP > 0) {
                                    val isMet = state.signedLabelDeliveredAlbumsLP >= state.signedLabelReqAlbumsLP
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Studio LP Albums", color = TextPrimary, fontSize = 12.sp)
                                        Text(
                                            "${state.signedLabelDeliveredAlbumsLP} / ${state.signedLabelReqAlbumsLP} ${if (isMet) "✓" else ""}",
                                            color = if (isMet) AccentGreen else AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                if (state.signedLabelReqAlbumsEP > 0) {
                                    val isMet = state.signedLabelDeliveredAlbumsEP >= state.signedLabelReqAlbumsEP
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Studio EPs", color = TextPrimary, fontSize = 12.sp)
                                        Text(
                                            "${state.signedLabelDeliveredAlbumsEP} / ${state.signedLabelReqAlbumsEP} ${if (isMet) "✓" else ""}",
                                            color = if (isMet) AccentGreen else AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                if (state.signedLabelReqMeTubeVideos > 0) {
                                    val isMet = state.signedLabelDeliveredMeTubeVideos >= state.signedLabelReqMeTubeVideos
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("MeTube Music Videos", color = TextPrimary, fontSize = 12.sp)
                                        Text(
                                            "${state.signedLabelDeliveredMeTubeVideos} / ${state.signedLabelReqMeTubeVideos} ${if (isMet) "✓" else ""}",
                                            color = if (isMet) AccentGreen else AccentGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action buttons: Standard Termination vs Lawyer Dispute
                            if (isExpired && state.recoupableAdvanceBalance <= 0.0) {
                                Button(
                                    onClick = { viewModel.terminateLabelContract() },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Conclude Fulfilled Contract (Free - $0)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.terminateLabelContract() },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isExpired) "Conclude Contract (Settle Advance: $${String.format("%,.0f", state.recoupableAdvanceBalance)})"
                                            else "Early Termination (Breach Fine: $${String.format("%,.0f", fineAmount)})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { viewModel.forceTerminateWithLawyer() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = BorderStroke(1.dp, NeonTeal),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Legal Force-Out via Lawyer ($${String.format("%,.0f", totalLawyerCost)})",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = NeonTeal
                                        )
                                        Text(
                                            text = if (lawyer != null) "Staff Lawyer ${lawyer.name} ($5K fee + advance)" else "External legal counsel ($20K fee + advance)",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (state.ownLabel != null) {
                val label = state.ownLabel

                // 1. Label Financial Vault
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "💼 Company Vault & Treasury",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Company Capital", color = TextSecondary, fontSize = 11.sp)
                                    Text("$${String.format("%,.2f", label.companyCash)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Equity Stake Held", color = TextSecondary, fontSize = 11.sp)
                                    Text("${String.format("%.1f", label.equityHeldByPlayer)}%", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Weekly Artist Royalties: +$${String.format("%,.2f", label.weeklyRoyaltiesEarned)}",
                                color = AccentGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Accrued Weekly Debts: $${String.format("%,.2f", label.accruedDebts)}",
                                color = if (label.accruedDebts > 0.0) NeonPink else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (label.accruedDebts > 0.0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.payLabelDebt(1000.0) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Pay $1,000")
                                    }
                                    Button(
                                        onClick = { viewModel.payLabelDebt(label.accruedDebts) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Pay All Debt")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = BorderColor)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Capital Investment Operations", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.depositCompanyCash(10000.0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("Deposit $10K", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.depositCompanyCash(100000.0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676), contentColor = Color.Black),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("Deposit $100K", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.withdrawCompanyCash(50000.0) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("Withdraw $50K", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // 2. Signed Artists Management
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎤 Signed Artist Catalog",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val signedNPCs = state.npcs.filter { label.signedNPCIds.contains(it.id) }
                            if (signedNPCs.isEmpty()) {
                                Text(
                                    text = "No artists signed to your company yet. Scout talent below to build your roster!",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            } else {
                                signedNPCs.forEach { npc ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                                        border = BorderStroke(1.dp, BorderColor),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(npc.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                                    Text("${npc.genrePreference} Genre  •  ${String.format("%,d", npc.fans)} Fans", color = TextSecondary, fontSize = 11.sp)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .background(NeonPurple.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text("15% Cut", color = NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = { viewModel.sponsorArtistRelease(npc.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Sponsor Album Campaign ($30,000 Company Cash)", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Talent Scouting (Sign New Artists)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📡 Talent Scout Network",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Independent artists currently looking for label backing:", color = TextSecondary, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            val unsignedNPCs = state.npcs.filter {
                                !label.signedNPCIds.contains(it.id) &&
                                (it.id.startsWith("npc_star_") || it.id.startsWith("npc_rookie_")) &&
                                it.currentRecordLabel == "Independent" &&
                                !it.isRetired
                            }
                            if (unsignedNPCs.isEmpty()) {
                                Text("No independent artists scouting for contracts right now.", color = TextSecondary, fontSize = 12.sp)
                            } else {
                                unsignedNPCs.take(4).forEach { npc ->
                                    val signingBonus = (npc.popularity * 2500.0).coerceAtLeast(15000.0)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(npc.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                            Text("${npc.genrePreference} • Pop: ${(npc.popularity).toInt()}% • Sign: $${String.format("%,.0f", signingBonus)}", color = TextSecondary, fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.signArtistToCompany(npc.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Color.Black),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(28.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                        ) {
                                            Text("SIGN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Team Staff Management (managers & promotion teams)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "👥 Professional Agency Staff",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(text = "Promotion Team Active:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            if (label.promoTeams.isEmpty()) {
                                Text(
                                    text = "No promotion team hired yet. Costly weekly but boosts label renown!",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.hirePromoTeam("promo_team_1", "Vanguard PR", 500.0, 1.5f) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Color.Black)
                                ) {
                                    Text("Hire Vanguard PR ($500/wk)")
                                }
                            } else {
                                label.promoTeams.forEach { team ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${team.name} ($${team.weeklyCost}/wk) +${team.popularityBoost}% boost",
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Label Manager Active:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            if (label.managers.isEmpty()) {
                                Text(
                                    text = "No manager hired yet! Hire one to run standard operations.",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.hireManager("mgr_1", "Marcus Pierce", 1200.0, 1.2f) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                                ) {
                                    Text("Hire Marcus Pierce ($1,200/wk)")
                                }
                            } else {
                                label.managers.forEach { mgr ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${mgr.name} ($${mgr.weeklyCost}/wk) Efficiency: ${mgr.efficiency}",
                                            color = TextPrimary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Pre-launch company dashboard setup state (Image 3 requirements panel)
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Establish Your Own Record Label",
                                style = MaterialTheme.typography.titleMedium,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Pre-requisite Qualifications Required:\n• Cash Reserve: $5,000,000\n• Popularity Level: 70%\n• Industry Influence: 80%\n\nLaunch Cost: $2,000,000",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = labelNameInput,
                                onValueChange = { labelNameInput = it },
                                label = { Text("Label/Company Name") },
                                placeholder = { Text("e.g. Apex Records") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val totalSkills = (state.voiceSkill + state.writingSkill + state.businessSkill).coerceIn(0f, 300f)
                            val skillRatio = totalSkills / 300f
                            val reachRatio = ((state.fans + state.followers).toFloat() / 1000000f).coerceIn(0f, 1.0f)
                            val catalogRatio = (state.songs.count { it.isReleased }.toFloat() / 15f).coerceIn(0f, 1.0f)
                            val avgNpcRel = if (state.npcs.isNotEmpty()) state.npcs.map { it.relationshipWithPlayer }.average().toFloat() / 100f else 0.5f
                            val industryRatio = ((state.industryFollowers.toFloat() / 50000f).coerceIn(0f, 1.0f) * 0.5f + avgNpcRel * 0.5f).coerceIn(0f, 1.0f)
                            val overallMastery = (skillRatio * 0.35f + reachRatio * 0.30f + catalogRatio * 0.15f + industryRatio * 0.20f).coerceIn(0.0f, 1.0f)
                            val calculatedLevel = (overallMastery * 9f).toInt() + 1

                            val meetsReqs = calculatedLevel >= 10 && state.cash >= 5000000.0 && state.popularity >= 70f && state.influence >= 80f
                            Button(
                                onClick = { viewModel.createOwnRecordLabel(labelNameInput) },
                                enabled = labelNameInput.isNotBlank() && meetsReqs,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (calculatedLevel < 10) "LOCKED (REQUIRES LEVEL 10 MASTER)" else "ESTABLISH ENTERTAINMENT COMPANY")
                            }
                        }
                    }
                }
            }
        } else if (selectedLabelTab == "Corporate" && state.ownLabel != null) {
            val label = state.ownLabel!!
            // Financial Health Overview Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📊 Corporate Equity & Valuation",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Label Valuation", color = TextSecondary, fontSize = 11.sp)
                                Text("$${String.format("%,.0f", label.valuation)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Corporate Reserves", color = TextSecondary, fontSize = 11.sp)
                                Text("$${String.format("%,.2f", label.companyCash)}", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Market Share", color = TextSecondary, fontSize = 11.sp)
                                Text("${String.format("%.2f", label.marketShare)}%", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Equity Control", color = TextSecondary, fontSize = 11.sp)
                                Text("${String.format("%.1f", label.equityHeldByPlayer)}% (CEO)", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // Sliders / Controls for CEO Salary and Dividend policy
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "⚙️ Executive Policies",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // CEO Salary Slider
                        Text(
                            text = "CEO Weekly Salary: $${String.format("%,.0f", label.ceoSalary)}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Paid from corporate reserves directly into your personal wallet weekly.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.adjustCeoSalary((label.ceoSalary - 500.0).coerceAtLeast(1000.0)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("- $500", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.adjustCeoSalary((label.ceoSalary + 500.0).coerceAtMost(15000.0)) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("+ $500", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderColor.copy(0.3f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Dividend rate slider
                        Text(
                            text = "Corporate Dividend Payout: ${String.format("%.0f", label.dividendPayoutRate)}%",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Distributes this percentage of weekly net profits to equity holders. High payouts please board members & investors, boosting satisfaction!",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.adjustDividendRate((label.dividendPayoutRate - 5f).coerceAtLeast(0f)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("- 5%", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { viewModel.adjustDividendRate((label.dividendPayoutRate + 5f).coerceAtMost(100f)) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("+ 5%", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Shareholder buyback card
            if (label.equityHeldByPlayer < 100f) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🛒 Equity Buyback Program",
                                style = MaterialTheme.typography.titleSmall,
                                color = AccentGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Use your personal bank savings to buy back shares and regain absolute control of your company.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val costFor5Percent = label.valuation * 0.05
                            val costFor10Percent = label.valuation * 0.10

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.buyBackEquity(5f) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Buy 5% ($${String.format("%,.0f", costFor5Percent)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.buyBackEquity(10f) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold, contentColor = Color.Black),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Buy 10% ($${String.format("%,.0f", costFor10Percent)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // List of active shareholders
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "👥 Company Shareholders Ledger",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        label.shareholders.forEach { sh ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(sh.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text(sh.type, color = TextSecondary, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${String.format("%.1f", sh.equityShare)}% Equity", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 13.sp)
                                    Text("Satisfaction: ${sh.satisfaction.toInt()}%", color = if (sh.satisfaction >= 50f) AccentGreen else NeonPink, fontSize = 10.sp)
                                }
                            }
                            HorizontalDivider(color = BorderColor.copy(0.3f))
                        }
                    }
                }
            }
        } else if (selectedLabelTab == "Boardroom" && state.ownLabel != null) {
            val label = state.ownLabel!!

            // Boardroom Header & satisfaction indicator
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🏛️ Executive Board of Directors",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Board Satisfaction", color = TextSecondary, fontSize = 11.sp)
                                Text("${label.boardSatisfaction.toInt()}%", color = if (label.boardSatisfaction >= 60f) AccentGreen else if (label.boardSatisfaction >= 40f) AccentGold else NeonPink, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (label.boardSatisfaction >= 40f) AccentGreen.copy(0.15f) else NeonPink.copy(0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (label.boardSatisfaction >= 70f) "Highly Backed" else if (label.boardSatisfaction >= 40f) "Satisfactory" else "Coup Danger!",
                                    color = if (label.boardSatisfaction >= 70f) AccentGreen else if (label.boardSatisfaction >= 40f) AccentGold else NeonPink,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "If you own less than 50% of your company's equity and the Board's satisfaction stays below 35%, they can vote you out of office as CEO! Lobby them to remain in power.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Co-Founders Section
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🤝 Active Co-Founders",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (label.coFounders.isEmpty()) {
                            Text("No active co-founders registered in your company.", color = TextSecondary, fontSize = 12.sp)
                        } else {
                            label.coFounders.forEach { cf ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                                    border = BorderStroke(1.dp, BorderColor.copy(0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(cf.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                                Text("Trait: ${cf.trait}  •  Salary: $${String.format("%,.0f", cf.salaryExpectation)}/wk", color = TextSecondary, fontSize = 11.sp)
                                            }
                                            Text("${cf.equityShare.toInt()}% Equity", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text("Relationship with you: ${cf.relationship.toInt()}%", color = if (cf.relationship >= 50f) AccentGreen else NeonPink, fontSize = 11.sp)
                                        if (cf.monthlyDemands != "None") {
                                            Text("Demand: ${cf.monthlyDemands}", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Button(
                                            onClick = { viewModel.socializeWithCoFounder(cf.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Socialize / Luxury Dinner (Costs $500, -15 Energy)", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Board Members List
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🏛️ Board Seats & Representatives",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        label.boardMembers.forEach { bm ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(bm.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                        Text("Role: ${bm.type}  •  Vote Weight: ${bm.powerWeight.toInt()}%", color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Alignment: ${bm.alignment}", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 12.sp)
                                        Text("Approval: ${bm.satisfaction.toInt()}%", color = if (bm.satisfaction >= 50f) AccentGreen else NeonPink, fontSize = 11.sp)
                                    }
                                }

                                if (bm.type != "Player") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.socializeWithBoardMember(bm.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(28.dp).fillMaxWidth(),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Host Executive Golf Outing (Costs $1,200, -20 Energy)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = BorderColor.copy(0.3f))
                            }
                        }
                    }
                }
            }

            // Strategic Investor Proposals Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💼 Incoming Investment Offers",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Exchange some company equity for instant corporate capital to invest in upgrades and signing talent.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (label.pendingInvestorOffers.isEmpty()) {
                            Text("No investor groups currently making offers. Establish catalog royalties first!", color = TextSecondary, fontSize = 11.sp)
                        } else {
                            label.pendingInvestorOffers.forEach { offer ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                                    border = BorderStroke(1.dp, BorderColor.copy(0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(offer.investorName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                                Text(offer.investorType + " Representative", color = TextSecondary, fontSize = 11.sp)
                                            }
                                            Text("${offer.equityRequested.toInt()}% Equity", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 13.sp)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("Funding Offer: $${String.format("%,.2f", offer.cashOffer)} company cash", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Implied Valuation: $${String.format("%,.2f", offer.minimumValuation)}", color = TextSecondary, fontSize = 11.sp)

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.acceptInvestorOffer(offer.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = Color.Black),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Accept Funding", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = { viewModel.rejectInvestorOffer(offer.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Decline", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Artists Wanting to be Signed Proposals
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📡 Artists Auditions & Signing Offers",
                            style = MaterialTheme.typography.titleSmall,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Independent artists seeking custom contracts with your record label. Review their split preferences & required signing bonus.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (label.pendingSigningOffers.isEmpty()) {
                            Text("No independent artists scouting for contracts right now. Scout talent inside 'Overview' tab!", color = TextSecondary, fontSize = 11.sp)
                        } else {
                            label.pendingSigningOffers.forEach { offer ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.04f)),
                                    border = BorderStroke(1.dp, BorderColor.copy(0.5f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(offer.artistName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                                Text("Genre: ${offer.preferredGenre}  •  Term: ${offer.contractDurationWeeks} wks", color = TextSecondary, fontSize = 11.sp)
                                            }
                                            Text("${offer.requestedRevenueShare.toInt()}% Split Cut", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 13.sp)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("Signing Bonus: $${String.format("%,.0f", offer.requestedSigningBonus)}", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Weekly Overhead Limit: $${String.format("%,.0f", offer.weeklyExpenseLimit)}", color = TextSecondary, fontSize = 11.sp)

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.acceptArtistSigningOffer(offer.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal, contentColor = Color.Black),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Sign Artist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = { viewModel.rejectArtistSigningOffer(offer.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.1f)),
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Reject", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedLabelTab == "Departments") {
            // Departments tab showing progress bars mimicking Image 3
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Department Operations & Efficiency",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val depts = listOf(
                            Triple("Music Production", "8 Employees  •  Studios", 0.92f),
                            Triple("Marketing & Public Relations", "6 Employees  •  Campaigns", 0.88f),
                            Triple("Touring Operations", "5 Employees  •  Logistics", 0.91f),
                            Triple("Corporate Legal & Royalties", "3 Employees  •  Audits", 0.94f),
                            Triple("A&R Discovery", "2 Employees  •  Talent Scouting", 0.85f)
                        )

                        depts.forEach { (dept, staff, progress) ->
                            Column(modifier = Modifier.padding(vertical = 10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = dept, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                                        Text(text = staff, color = TextSecondary, fontSize = 11.sp)
                                    }
                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        fontWeight = FontWeight.Bold,
                                        color = NeonTeal,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = progress,
                                    color = NeonPurple,
                                    trackColor = Color(0xFF1E172E),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MailboxPage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTab by remember { mutableStateOf("primary") }

    val promotionsMail = state.mailbox.filter { mail ->
        mail.type == "BRAND_ENDORSEMENT" || mail.type == "METUBE_BRAND_DEAL" ||
        mail.subject.contains("Brand", ignoreCase = true) || mail.subject.contains("Sponsor", ignoreCase = true) ||
        mail.subject.contains("Ambassador", ignoreCase = true) || mail.sender.contains("Alphify", ignoreCase = true) ||
        mail.sender.contains("Brand", ignoreCase = true) || mail.sender.contains("Obasa", ignoreCase = true)
    }

    val socialMail = state.mailbox.filter { mail ->
        !promotionsMail.contains(mail) && (
            mail.sender.contains("Accountant", ignoreCase = true) ||
            mail.sender.contains("Crypto Analyst", ignoreCase = true) ||
            mail.sender.contains("Financial Advisor", ignoreCase = true) ||
            mail.sender.contains("Security", ignoreCase = true) ||
            mail.sender.contains("Doctor Martins", ignoreCase = true) ||
            mail.sender.contains("Dr. Martins", ignoreCase = true) ||
            mail.sender.contains("Lawyer", ignoreCase = true) ||
            mail.sender.contains("Staff", ignoreCase = true) ||
            mail.type.contains("STAFF", ignoreCase = true) ||
            mail.type == "SECURITY_ALERT" ||
            mail.type == "TAX_ADVISORY" ||
            mail.type == "FINANCIAL_REPORT"
        )
    }

    val primaryMail = state.mailbox.filter { mail ->
        !promotionsMail.contains(mail) && !socialMail.contains(mail)
    }

    val currentTabMails = when (selectedTab) {
        "social" -> socialMail
        "promotions" -> promotionsMail
        else -> primaryMail
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Official Communications & Mailbox",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Receive industry offers, collaboration deals, staff reports, and brand partnerships directly in your verified mailbox.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Email Navigation Tabs (Primary, Social, Promotions)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF13111C))
                    .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Primary Tab
                val isPrimary = selectedTab == "primary"
                Button(
                    onClick = { selectedTab = "primary" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPrimary) NeonTeal else Color.Transparent,
                        contentColor = if (isPrimary) Color.Black else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Primary", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (primaryMail.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isPrimary) Color.Black else NeonTeal)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "${primaryMail.size}",
                                    color = if (isPrimary) NeonTeal else Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Social Tab (Staffs: Crypto analyst, accountant, financial advisor, security)
                val isSocial = selectedTab == "social"
                Button(
                    onClick = { selectedTab = "social" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSocial) NeonPurple else Color.Transparent,
                        contentColor = if (isSocial) Color.White else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Social", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (socialMail.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSocial) Color.White else NeonPurple)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "${socialMail.size}",
                                    color = if (isSocial) NeonPurple else Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Promotions Tab (Brand deals, Alphify, endorsements)
                val isPromotions = selectedTab == "promotions"
                Button(
                    onClick = { selectedTab = "promotions" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPromotions) Color(0xFFD4AF37) else Color.Transparent,
                        contentColor = if (isPromotions) Color.Black else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Promotions", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        if (promotionsMail.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isPromotions) Color.Black else Color(0xFFD4AF37))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    "${promotionsMail.size}",
                                    color = if (isPromotions) Color(0xFFD4AF37) else Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        if (currentTabMails.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (selectedTab) {
                                "social" -> "👥"
                                "promotions" -> "🏷️"
                                else -> "📨"
                            },
                            fontSize = 44.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            text = when (selectedTab) {
                                "social" -> "No staff messages"
                                "promotions" -> "No promotional offers"
                                else -> "No primary messages"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (selectedTab) {
                                "social" -> "Reports from your accountant, crypto analyst, and advisors will appear here."
                                "promotions" -> "Commercial sponsorships, brand ambassador deals, and endorsements arrive here."
                                else -> "Collaborations, label offers, and music proposals will arrive as your career advances."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(currentTabMails) { mail ->
                val cardBorderColor = if (mail.isAccepted) BorderColor else when (selectedTab) {
                    "social" -> NeonPurple
                    "promotions" -> Color(0xFFD4AF37)
                    else -> NeonTeal
                }
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.2.dp, cardBorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(cardBorderColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "From: ${mail.sender}",
                                    fontWeight = FontWeight.Bold,
                                    color = cardBorderColor,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (mail.isAccepted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BorderColor)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                    Text(
                                        text = "ACCEPTED",
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (mail.type == "LABEL_OFFER") {
                            com.example.ui.ContractProposalView(
                                mail = mail,
                                playerName = state.artistName,
                                onAccept = { viewModel.acceptMail(mail.id) },
                                onDecline = { viewModel.deleteMail(mail.id) }
                            )
                        } else {
                            Text(
                                text = mail.subject,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = mail.body,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )

                            if (!mail.isAccepted) {
                                Spacer(modifier = Modifier.height(16.dp))
                                if (mail.type == "FEATURE" || mail.type == "COLLAB" || mail.type == "BRAND_ENDORSEMENT" || mail.type == "METUBE_BRAND_DEAL" || mail.cashOffer > 0.0) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.acceptMail(mail.id) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (mail.type == "BRAND_ENDORSEMENT" || mail.type == "METUBE_BRAND_DEAL") Color(0xFFD4AF37) else NeonTeal
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Text(
                                                text = if (mail.type == "BRAND_ENDORSEMENT" || mail.type == "METUBE_BRAND_DEAL") "ACCEPT PARTNERSHIP" else "ACCEPT OFFER",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.deleteMail(mail.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Text("DECLINE", color = TextSecondary)
                                        }
                                    }
                                } else if (mail.type == "VENTURE_EQUITY_OFFER" || mail.type == "VENTURE_BUYOUT_OFFER") {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.acceptMail(mail.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Text(
                                                text = if (mail.type == "VENTURE_BUYOUT_OFFER") "ACCEPT BUYOUT" else "ACCEPT INVESTMENT",
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.deleteMail(mail.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Text("DECLINE DEAL", color = TextSecondary)
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
fun ShopProfilePage(state: GameStateEntity, viewModel: GameViewModel) {
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            containerColor = Color(0xFF1E1C29),
            title = {
                Text(
                    text = "Reset Career & Restart Game?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to reset your career? All your progress, songs, releases, fans, bank accounts, and relationships will be permanently erased.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        viewModel.restartGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Text("Yes, Reset Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetConfirmDialog = false },
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    var selectedWardrobeCategory by remember { mutableStateOf("HAIR") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Glowing Premium Profile Header Card (Image 5)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar with double gradient border
                    AvatarWidget(
                        baseLook = state.avatarBaseLook,
                        skinTone = state.avatarSkinTone,
                        hairStyleId = state.avatarHairStyleId,
                        clothingId = state.avatarClothingId,
                        jewelryId = state.avatarJewelryId,
                        modifier = Modifier
                            .size(90.dp)
                            .border(3.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.artistName.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = state.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C1929)),
                        border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("reset_career_btn")
                    ) {
                        Text("RESET CAREER & RESTART GAME", color = NeonPink, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stage Wardrobe & Outfits Catalog
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STAGE WARDROBE & STYLING",
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryBrandLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cash: $${String.format("%,.0f", state.cash)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Wardrobe category tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F111A))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("HAIR", "Hairstyle", Icons.Default.Face),
                            Triple("CLOTHES", "Outfits", Icons.Default.Checkroom),
                            Triple("JEWELRY", "Jewelry", Icons.Default.Diamond)
                        ).forEach { (catKey, label, icon) ->
                            val isSel = selectedWardrobeCategory == catKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PrimaryBrandColor else Color.Transparent)
                                    .clickable { selectedWardrobeCategory = catKey }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(icon, contentDescription = null, tint = if (isSel) Color.White else TextSecondary, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSel) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Item cards for selected category
                    val catalogItems = GameData.WARDROBE_ITEMS.filter { it.category == selectedWardrobeCategory }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        catalogItems.forEach { item ->
                            val isOwned = state.avatarOwnedItemIds.contains(item.id) || item.cost == 0.0
                            val isEquipped = when (item.category) {
                                "HAIR" -> state.avatarHairStyleId == item.id
                                "CLOTHES" -> state.avatarClothingId == item.id
                                "JEWELRY" -> state.avatarJewelryId == item.id
                                else -> false
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isEquipped) Color(0xFF1E2638) else Color(0xFF171A26)),
                                border = BorderStroke(1.dp, if (isEquipped) PrimaryBrandLight else BorderColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(text = item.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            if (isEquipped) {
                                                Surface(
                                                    color = PrimaryBrandLight.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text("EQUIPPED", color = PrimaryBrandLight, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                        }
                                        Text(text = item.desc.ifEmpty { item.name }, color = TextSecondary, fontSize = 10.sp)
                                        if (!isOwned) {
                                            Text(text = "Price: $${String.format("%,.0f", item.cost)}", color = WarningColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (isEquipped) {
                                        Text("Active", color = PrimaryBrandLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else if (isOwned) {
                                        Button(
                                            onClick = { viewModel.equipWardrobeItem(item.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandLight),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("EQUIP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.buyWardrobeItem(item.id) },
                                            enabled = state.cash >= item.cost,
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("BUY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Audience Segments Panel (Total Audience Demographics)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Audience Segments (Total: ${String.format("%,d", state.fans)})",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val segments = listOf(
                        Triple("Casual Listeners", state.casualFans, "Regular streams & playlist views"),
                        Triple("Hardcore Devotees", state.hardcoreFans, "Buys physical merch & tour passes"),
                        Triple("Elite Collectors", state.collectors, "Rare editions & physical box sets"),
                        Triple("Concert Goers", state.concertGoers, "High attendance rate on world tours"),
                        Triple("International Base", state.internationalFans, "Overseas streaming listener hub"),
                        Triple("Online Supporters", state.onlineCommunity, "Boost social media metrics & trends")
                    )

                    segments.forEachIndexed { index, (name, count, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = name, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.SemiBold)
                                Text(text = desc, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Text(
                                text = String.format("%,d", count),
                                style = MaterialTheme.typography.bodyMedium,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (index < segments.size - 1) {
                            Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

data class NpcAvatarAppearance(
    val baseLook: String,
    val skinTone: String,
    val hairId: String,
    val clothesId: String,
    val jewelryId: String
)

fun getNpcAvatar(npcId: String): NpcAvatarAppearance {
    return when (npcId) {
        "npc_family_mom" -> NpcAvatarAppearance("Female", "#F5C396", "hair_bob", "cloth_casual_tee", "jewel_pearls")
        "npc_family_uncle_joe" -> NpcAvatarAppearance("Male", "#E0AC69", "hair_slick", "cloth_casual_tee", "jewel_silver_chain")
        "npc_family_cousin_sarah" -> NpcAvatarAppearance("Female", "#FFDBAC", "hair_braids", "cloth_street_jacket", "jewel_none")
        "npc_gov_sterling", "npc_sen_vance" -> NpcAvatarAppearance("Male", "#F5C396", "hair_slick", "cloth_suit", "jewel_none")
        "npc_star_1", "npc_rival_1" -> NpcAvatarAppearance("Female", "#FFDBAC", "hair_dreads", "cloth_gold_blazer", "jewel_grillz") // Aria Nova
        "npc_star_2", "npc_rival_2" -> NpcAvatarAppearance("Male", "#8D5524", "hair_spiky", "cloth_retro_hoodie", "jewel_silver_chain") // DJ Pulse
        "npc_star_3" -> NpcAvatarAppearance("Androgynous", "#E0AC69", "hair_mohawk", "cloth_bomber", "jewel_none")
        "npc_star_4" -> NpcAvatarAppearance("Male", "#F5C396", "hair_waves", "cloth_street_jacket", "jewel_none")
        "npc_star_5" -> NpcAvatarAppearance("Female", "#FFDBAC", "hair_bob", "cloth_casual_tee", "jewel_pearls")
        else -> {
            val hash = npcId.hashCode()
            val baseLook = if (hash % 3 == 0) "Female" else if (hash % 3 == 1) "Male" else "Androgynous"
            val skinTone = if (hash % 4 == 0) "#F5C396" else if (hash % 4 == 1) "#FFDBAC" else if (hash % 4 == 2) "#E0AC69" else "#8D5524"
            val hair = if (hash % 5 == 0) "hair_slick" else if (hash % 5 == 1) "hair_waves" else if (hash % 5 == 2) "hair_mohawk" else if (hash % 5 == 3) "hair_afro" else "hair_bob"
            val clothes = if (hash % 4 == 0) "cloth_casual_tee" else if (hash % 4 == 1) "cloth_retro_hoodie" else if (hash % 4 == 2) "cloth_street_jacket" else "cloth_bomber"
            val jewel = if (hash % 5 == 0) "jewel_none" else if (hash % 5 == 1) "jewel_silver_chain" else "jewel_pearls"
            NpcAvatarAppearance(baseLook, skinTone, hair, clothes, jewel)
        }
    }
}

@Composable
fun AvatarWidget(
    baseLook: String,
    skinTone: String,
    hairStyleId: String,
    clothingId: String,
    jewelryId: String,
    modifier: Modifier = Modifier
) {
    val parsedSkinColor = remember(skinTone) {
        try {
            Color(android.graphics.Color.parseColor(skinTone))
        } catch (e: Exception) {
            Color(0xFFF5C396)
        }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF1E2638), Color(0xFF0F131E)))),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // NECK
            val neckWidth = width * 0.18f
            val neckHeight = height * 0.22f
            drawRect(
                color = parsedSkinColor.copy(alpha = 0.85f),
                topLeft = androidx.compose.ui.geometry.Offset(centerX - neckWidth / 2f, centerY + height * 0.05f),
                size = androidx.compose.ui.geometry.Size(neckWidth, neckHeight)
            )

            // HEAD
            val faceRadiusX = width * 0.26f
            val faceRadiusY = when (baseLook) {
                "Female" -> height * 0.30f
                "Male" -> height * 0.32f
                else -> height * 0.31f
            }
            drawOval(
                color = parsedSkinColor,
                topLeft = androidx.compose.ui.geometry.Offset(centerX - faceRadiusX, centerY - faceRadiusY),
                size = androidx.compose.ui.geometry.Size(faceRadiusX * 2, faceRadiusY * 2)
            )

            // EYES
            val eyeOffsetY = centerY - faceRadiusY * 0.15f
            val eyeSpacing = faceRadiusX * 0.45f
            val eyeRadius = width * 0.035f
            drawCircle(
                color = Color(0xFF0E1320),
                radius = eyeRadius,
                center = androidx.compose.ui.geometry.Offset(centerX - eyeSpacing, eyeOffsetY)
            )
            drawCircle(
                color = Color(0xFF0E1320),
                radius = eyeRadius,
                center = androidx.compose.ui.geometry.Offset(centerX + eyeSpacing, eyeOffsetY)
            )

            // Sparks
            drawCircle(
                color = Color.White,
                radius = eyeRadius * 0.3f,
                center = androidx.compose.ui.geometry.Offset(centerX - eyeSpacing - eyeRadius * 0.2f, eyeOffsetY - eyeRadius * 0.2f)
            )
            drawCircle(
                color = Color.White,
                radius = eyeRadius * 0.3f,
                center = androidx.compose.ui.geometry.Offset(centerX + eyeSpacing - eyeRadius * 0.2f, eyeOffsetY - eyeRadius * 0.2f)
            )

            // CHEEKS
            if (baseLook == "Female" || baseLook == "Androgynous") {
                drawCircle(
                    color = Color(0xFFFF8595).copy(alpha = 0.3f),
                    radius = faceRadiusX * 0.25f,
                    center = androidx.compose.ui.geometry.Offset(centerX - faceRadiusX * 0.55f, centerY + faceRadiusY * 0.1f)
                )
                drawCircle(
                    color = Color(0xFFFF8595).copy(alpha = 0.3f),
                    radius = faceRadiusX * 0.25f,
                    center = androidx.compose.ui.geometry.Offset(centerX + faceRadiusX * 0.55f, centerY + faceRadiusY * 0.1f)
                )
            }

            // NOSE
            drawOval(
                color = parsedSkinColor.copy(alpha = 0.75f),
                topLeft = androidx.compose.ui.geometry.Offset(centerX - width * 0.02f, centerY + faceRadiusY * 0.05f),
                size = androidx.compose.ui.geometry.Size(width * 0.04f, height * 0.06f)
            )

            // MOUTH
            val mouthY = centerY + faceRadiusY * 0.35f
            val mouthWidth = faceRadiusX * 0.5f
            val mouthHeight = height * 0.03f
            drawArc(
                color = when (baseLook) {
                    "Female" -> Color(0xFFE91E63)
                    else -> Color(0xFFC26F65)
                },
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = androidx.compose.ui.geometry.Offset(centerX - mouthWidth / 2f, mouthY),
                size = androidx.compose.ui.geometry.Size(mouthWidth, mouthHeight)
            )

            // HAIR
            when (hairStyleId) {
                "hair_slick" -> {
                    drawArc(
                        color = Color(0xFF2C221E),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - faceRadiusX * 1.08f, centerY - faceRadiusY * 1.15f),
                        size = androidx.compose.ui.geometry.Size(faceRadiusX * 2.16f, faceRadiusY * 1.1f)
                    )
                }
                "hair_afro" -> {
                    drawCircle(
                        color = Color(0xFF130E0C),
                        radius = faceRadiusX * 1.25f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY - faceRadiusY * 0.4f)
                    )
                }
                "hair_braids" -> {
                    drawCircle(
                        color = Color(0xFF1E1C20),
                        radius = faceRadiusX * 1.05f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY - faceRadiusY * 0.35f)
                    )
                    drawRoundRect(
                        color = Color(0xFF26212B),
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - faceRadiusX * 1.15f, centerY - faceRadiusY * 0.1f),
                        size = androidx.compose.ui.geometry.Size(width * 0.1f, height * 0.5f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                    )
                    drawRoundRect(
                        color = Color(0xFF26212B),
                        topLeft = androidx.compose.ui.geometry.Offset(centerX + faceRadiusX * 0.85f, centerY - faceRadiusY * 0.1f),
                        size = androidx.compose.ui.geometry.Size(width * 0.1f, height * 0.5f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                    )
                }
                "hair_spiky" -> {
                    val spikeColor = Color(0xFF39FF14)
                    drawCircle(
                        color = spikeColor,
                        radius = faceRadiusX * 1.05f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY - faceRadiusY * 0.3f)
                    )
                    val pathLeft = androidx.compose.ui.graphics.Path().apply {
                        moveTo(centerX - faceRadiusX, centerY - faceRadiusY * 0.2f)
                        lineTo(centerX - faceRadiusX * 1.3f, centerY - faceRadiusY * 0.9f)
                        lineTo(centerX - faceRadiusX * 0.6f, centerY - faceRadiusY * 0.8f)

                        moveTo(centerX - faceRadiusX * 0.7f, centerY - faceRadiusY * 0.8f)
                        lineTo(centerX - faceRadiusX * 0.8f, centerY - faceRadiusY * 1.4f)
                        lineTo(centerX - faceRadiusX * 0.2f, centerY - faceRadiusY * 0.9f)

                        moveTo(centerX, centerY - faceRadiusY)
                        lineTo(centerX, centerY - faceRadiusY * 1.5f)
                        lineTo(centerX + faceRadiusX * 0.4f, centerY - faceRadiusY * 0.9f)

                        moveTo(centerX + faceRadiusX * 0.7f, centerY - faceRadiusY * 0.8f)
                        lineTo(centerX + faceRadiusX * 0.8f, centerY - faceRadiusY * 1.4f)
                        lineTo(centerX + faceRadiusX * 0.2f, centerY - faceRadiusY * 0.9f)

                        moveTo(centerX + faceRadiusX, centerY - faceRadiusY * 0.2f)
                        lineTo(centerX + faceRadiusX * 1.3f, centerY - faceRadiusY * 0.9f)
                        lineTo(centerX + faceRadiusX * 0.6f, centerY - faceRadiusY * 0.8f)
                    }
                    drawPath(pathLeft, color = spikeColor)
                }
                "hair_waves" -> {
                    drawCircle(
                        color = Color(0xFF1F120C),
                        radius = faceRadiusX * 1.05f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY - faceRadiusY * 0.35f)
                    )
                    for (i in 1..4) {
                        drawArc(
                            color = Color.White.copy(alpha = 0.15f),
                            startAngle = 200f,
                            sweepAngle = 140f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(centerX - faceRadiusX, centerY - faceRadiusY * (0.35f + i * 0.15f)),
                            size = androidx.compose.ui.geometry.Size(faceRadiusX * 2f, faceRadiusY * 0.5f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                        )
                    }
                }
                "hair_mohawk" -> {
                    val mohawkColor = Color(0xFFFF5722)
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(centerX - faceRadiusX * 0.2f, centerY - faceRadiusY * 0.2f)
                        lineTo(centerX - faceRadiusX * 0.3f, centerY - faceRadiusY * 1.5f)
                        lineTo(centerX, centerY - faceRadiusY * 1.7f)
                        lineTo(centerX + faceRadiusX * 0.3f, centerY - faceRadiusY * 1.5f)
                        lineTo(centerX + faceRadiusX * 0.2f, centerY - faceRadiusY * 0.2f)
                    }
                    drawPath(path, color = mohawkColor)
                }
                "hair_bob" -> {
                    val hairColor = Color(0xFFCFD8DC)
                    drawCircle(
                        color = hairColor,
                        radius = faceRadiusX * 1.1f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY - faceRadiusY * 0.4f)
                    )
                    drawRoundRect(
                        color = hairColor,
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - faceRadiusX * 1.1f, centerY - faceRadiusY * 0.2f),
                        size = androidx.compose.ui.geometry.Size(faceRadiusX * 0.35f, faceRadiusY * 1.2f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f, 15f)
                    )
                    drawRoundRect(
                        color = hairColor,
                        topLeft = androidx.compose.ui.geometry.Offset(centerX + faceRadiusX * 0.75f, centerY - faceRadiusY * 0.2f),
                        size = androidx.compose.ui.geometry.Size(faceRadiusX * 0.35f, faceRadiusY * 1.2f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f, 15f)
                    )
                }
                "hair_dreads" -> {
                    val dreadColor = Color(0xFF2E221E)
                    val goldColor = Color(0xFFFFD700)
                    drawCircle(
                        color = dreadColor,
                        radius = faceRadiusX * 1.1f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY - faceRadiusY * 0.4f)
                    )
                    val positions = listOf(-0.9f, -0.6f, -0.3f, 0.3f, 0.6f, 0.9f)
                    positions.forEach { offsetMult ->
                        drawRoundRect(
                            color = dreadColor,
                            topLeft = androidx.compose.ui.geometry.Offset(centerX + faceRadiusX * offsetMult - width * 0.04f, centerY - faceRadiusY * 0.2f),
                            size = androidx.compose.ui.geometry.Size(width * 0.08f, height * 0.4f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                        )
                        drawRect(
                            color = goldColor,
                            topLeft = androidx.compose.ui.geometry.Offset(centerX + faceRadiusX * offsetMult - width * 0.04f, centerY - faceRadiusY * 0.2f + height * 0.36f),
                            size = androidx.compose.ui.geometry.Size(width * 0.08f, height * 0.04f)
                        )
                    }
                }
                else -> {
                    drawArc(
                        color = Color(0xFF1A1A1A),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - faceRadiusX * 1.05f, centerY - faceRadiusY * 1.05f),
                        size = androidx.compose.ui.geometry.Size(faceRadiusX * 2.1f, faceRadiusY * 1.1f)
                    )
                }
            }

            // CLOTHING
            val clothingColor = when (clothingId) {
                "cloth_casual_tee" -> Color.White
                "cloth_retro_hoodie" -> Color(0xFFFF007F)
                "cloth_street_jacket" -> Color(0xFF1E88E5)
                "cloth_bomber" -> Color(0xFF263238)
                "cloth_leather" -> Color(0xFF1A1A1A)
                "cloth_suit" -> Color(0xFF111E38)
                "cloth_gold_blazer" -> Color(0xFFFFD700)
                "cloth_space_suit" -> Color(0xFFECEFF1)
                else -> Color.White
            }

            val shoulderY = centerY + faceRadiusY * 0.8f
            val shoulderWidth = width * 0.8f

            val shoulderPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(centerX - shoulderWidth / 2f, height)
                quadraticTo(
                    centerX - shoulderWidth * 0.4f, shoulderY,
                    centerX, shoulderY + height * 0.05f
                )
                quadraticTo(
                    centerX + shoulderWidth * 0.4f, shoulderY,
                    centerX + shoulderWidth / 2f, height
                )
                close()
            }
            drawPath(shoulderPath, color = clothingColor)

            if (clothingId == "cloth_suit" || clothingId == "cloth_gold_blazer" || clothingId == "cloth_street_jacket" || clothingId == "cloth_leather") {
                val lapelPathLeft = androidx.compose.ui.graphics.Path().apply {
                    moveTo(centerX, shoulderY + height * 0.05f)
                    lineTo(centerX - shoulderWidth * 0.2f, shoulderY + height * 0.05f)
                    lineTo(centerX - shoulderWidth * 0.35f, height)
                    lineTo(centerX, height)
                    close()
                }
                drawPath(lapelPathLeft, color = clothingColor.copy(alpha = 0.85f))

                val lapelPathRight = androidx.compose.ui.graphics.Path().apply {
                    moveTo(centerX, shoulderY + height * 0.05f)
                    lineTo(centerX + shoulderWidth * 0.2f, shoulderY + height * 0.05f)
                    lineTo(centerX + shoulderWidth * 0.35f, height)
                    lineTo(centerX, height)
                    close()
                }
                drawPath(lapelPathRight, color = clothingColor.copy(alpha = 0.85f))

                val shirtPath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(centerX - neckWidth / 2f, shoulderY)
                    lineTo(centerX + neckWidth / 2f, shoulderY)
                    lineTo(centerX, shoulderY + height * 0.15f)
                    close()
                }
                drawPath(shirtPath, color = if (clothingId == "cloth_suit") Color.White else Color(0xFF1E242B))
            }

            if (clothingId == "cloth_space_suit") {
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                    radius = width * 0.03f,
                    center = androidx.compose.ui.geometry.Offset(centerX - shoulderWidth * 0.18f, shoulderY + height * 0.12f)
                )
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                    radius = width * 0.03f,
                    center = androidx.compose.ui.geometry.Offset(centerX + shoulderWidth * 0.18f, shoulderY + height * 0.12f)
                )
                drawArc(
                    color = Color(0xFF00E5FF),
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(centerX - neckWidth * 1.5f, shoulderY + height * 0.02f),
                    size = androidx.compose.ui.geometry.Size(neckWidth * 3f, height * 0.1f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )
            }

            // JEWELRY
            when (jewelryId) {
                "jewel_silver_chain" -> {
                    drawArc(
                        color = Color(0xFFCFD8DC),
                        startAngle = 15f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - neckWidth * 0.6f, centerY + faceRadiusY * 0.65f),
                        size = androidx.compose.ui.geometry.Size(neckWidth * 1.2f, height * 0.12f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                    )
                }
                "jewel_pearls" -> {
                    val pearlRadius = width * 0.015f
                    for (i in 0..8) {
                        val angle = 180f + (i * 22.5f)
                        val angleRad = Math.toRadians(angle.toDouble())
                        val px = centerX + (neckWidth * 0.65f * Math.cos(angleRad)).toFloat()
                        val py = centerY + faceRadiusY * 0.75f + (height * 0.06f * Math.sin(angleRad)).toFloat()
                        drawCircle(
                            color = Color(0xFFF1F8E9),
                            radius = pearlRadius,
                            center = androidx.compose.ui.geometry.Offset(px, py)
                        )
                    }
                }
                "jewel_gold_medallion" -> {
                    drawArc(
                        color = Color(0xFFFFA000),
                        startAngle = 15f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - neckWidth * 0.65f, centerY + faceRadiusY * 0.62f),
                        size = androidx.compose.ui.geometry.Size(neckWidth * 1.3f, height * 0.14f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                    )
                    drawCircle(
                        color = Color(0xFFFFD54F),
                        radius = width * 0.06f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY + faceRadiusY * 0.85f)
                    )
                    drawCircle(
                        color = Color(0xFFFFA000),
                        radius = width * 0.04f,
                        center = androidx.compose.ui.geometry.Offset(centerX, centerY + faceRadiusY * 0.85f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }
                "jewel_grillz" -> {
                    val grillY = centerY + faceRadiusY * 0.35f
                    val grillW = faceRadiusX * 0.35f
                    val grillH = height * 0.015f
                    drawRect(
                        color = Color(0xFFE0F7FA),
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - grillW / 2f, grillY),
                        size = androidx.compose.ui.geometry.Size(grillW, grillH)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = androidx.compose.ui.geometry.Offset(centerX - grillW * 0.3f, grillY + grillH * 0.5f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = androidx.compose.ui.geometry.Offset(centerX + grillW * 0.3f, grillY + grillH * 0.5f)
                    )
                }
                "jewel_crown" -> {
                    val crownW = faceRadiusX * 1.2f
                    val crownH = height * 0.15f
                    val crownY = centerY - faceRadiusY * 1.25f
                    drawRoundRect(
                        color = Color(0xFFFFC107),
                        topLeft = androidx.compose.ui.geometry.Offset(centerX - crownW / 2f, crownY),
                        size = androidx.compose.ui.geometry.Size(crownW, height * 0.02f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                    val pointsPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(centerX - crownW / 2f, crownY)
                        lineTo(centerX - crownW * 0.35f, crownY - crownH)
                        lineTo(centerX - crownW * 0.2f, crownY)
                        lineTo(centerX, crownY - crownH * 1.2f)
                        lineTo(centerX + crownW * 0.2f, crownY)
                        lineTo(centerX + crownW * 0.35f, crownY - crownH)
                        lineTo(centerX + crownW / 2f, crownY)
                    }
                    drawPath(pointsPath, color = Color(0xFFFFC107))
                    drawCircle(
                        color = Color(0xFFE0F7FA),
                        radius = 5f,
                        center = androidx.compose.ui.geometry.Offset(centerX, crownY - crownH * 1.2f)
                    )
                    drawCircle(
                        color = Color(0xFFE0F7FA),
                        radius = 4f,
                        center = androidx.compose.ui.geometry.Offset(centerX - crownW * 0.35f, crownY - crownH)
                    )
                    drawCircle(
                        color = Color(0xFFE0F7FA),
                        radius = 4f,
                        center = androidx.compose.ui.geometry.Offset(centerX + crownW * 0.35f, crownY - crownH)
                    )
                }
            }
        }
    }
}
