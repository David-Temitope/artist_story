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
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val gameViewModel: GameViewModel = viewModel()
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

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBg, Color(0xFF180A2B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Stylized Logo Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(NeonPink, NeonPurple)))
                    .border(2.dp, NeonTeal, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music Icon",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Artist Story",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 2.sp
                ),
                color = TextPrimary
            )

            Text(
                text = "Live your dream in the music industry",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF332E41)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Artist Profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Your Email") },
                        placeholder = { Text("e.g. artist@story.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0xFF444052),
                            focusedLabelColor = NeonPurple
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Stage Name / Artist Name") },
                        placeholder = { Text("e.g. Lil Rhythm") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = Color(0xFF444052),
                            focusedLabelColor = NeonPurple
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("artist_name_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (emailInput.isNotBlank() && nameInput.isNotBlank()) {
                                onLogin(emailInput, nameInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPurple,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        Text(
                            text = "START CAREER",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Demo notice info
            Text(
                text = "Use email davemcthunder@gmail.com to test the admin panel",
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navigationItems = listOf(
        Triple("Dashboard", Icons.Default.Home, "Home"),
        Triple("Studio", Icons.Default.MusicNote, "Studio"),
        Triple("Tours", Icons.Default.DirectionsBus, "Tours"),
        Triple("Trading", Icons.Default.TrendingUp, "Market"),
        Triple("Gigs (Jobs)", Icons.Default.Work, "Jobs"),
        Triple("Social & Practice", Icons.Default.Share, "Grow"),
        Triple("Marketplace", Icons.Default.ShoppingCart, "Assets"),
        Triple("Record Label", Icons.Default.Domain, "Company"),
        Triple("Mailbox", Icons.Default.Email, "Messages"),
        Triple("Shop & Profile", Icons.Default.Person, "Profile")
    )

    // Admin tab is added dynamically
    val finalNavItems = if (state.isAdmin) {
        navigationItems + Triple("Admin Panel", Icons.Default.Settings, "Admin")
    } else {
        navigationItems
    }

    Scaffold(
        floatingActionButton = {
            val hasUnreads = state.chatConversations.any { it.unreadCount > 0 }
            FloatingActionButton(
                onClick = { isPhoneOpen = true },
                containerColor = NeonPurple,
                contentColor = Color.White,
                modifier = Modifier.testTag("floating_smartphone_button")
            ) {
                Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.padding(4.dp)) {
                    Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = "Open Phone")
                    if (hasUnreads) {
                        Box(
                            modifier = Modifier
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }
                }
            }
        },
        topBar = {
            Column {
                // Main Header bar with edge-to-edge status layout padding
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // User Avatar + Label Name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(NeonPurple, NeonPink))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.artistName.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = state.artistName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Fans: ${state.fans}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NeonTeal
                                )
                            }
                        }

                        // Cash balance
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF262335)),
                            border = BorderStroke(1.dp, Color(0xFF3B3654))
                        ) {
                            Text(
                                text = "$${String.format("%,.0f", state.cash)}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                // Mini stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF13111C))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Week ${state.currentWeek}/52 (Year ${state.currentYear})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Energy Icon",
                            tint = Color(0xFFFFEB3B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Energy: ${state.energy}/${state.maxEnergy}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFFEB3B)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Slots Icon",
                            tint = NeonTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        // Used slots formula
                        val sideJobSlots = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
                        val tourSlots = state.tours.filter { it.status == "ACTIVE" }.sumOf { it.weeklyTimeCost }
                        val activeSlots = sideJobSlots + tourSlots

                        Text(
                            text = "Slots: $activeSlots/${state.maxTimeSlots}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonTeal
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Standard Navigation Bar with active pill selectors
            NavigationBar(
                containerColor = CardBg,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                // Display 5 major tabs here, and let the rest be accessed via a Bottom Drawer or scrolling row
                val barItems = finalNavItems.take(5)
                val moreItems = finalNavItems.drop(5)

                barItems.forEach { (tabId, icon, label) ->
                    val isSelected = selectedTab == tabId
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tabId },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(text = label, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonTeal,
                            selectedTextColor = NeonTeal,
                            indicatorColor = NeonPurple.copy(alpha = 0.3f),
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }

                // Custom "More" item if overflow tabs exist
                NavigationBarItem(
                    selected = moreItems.any { it.first == selectedTab },
                    onClick = {
                        // Cycles or pops the rest of the navigation
                        val currentIdx = moreItems.indexOfFirst { it.first == selectedTab }
                        if (currentIdx == -1) {
                            selectedTab = moreItems.first().first
                        } else {
                            val nextIdx = (currentIdx + 1) % moreItems.size
                            selectedTab = moreItems[nextIdx].first
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = "More") },
                    label = { Text(text = "More...", maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonTeal,
                        selectedTextColor = NeonTeal,
                        indicatorColor = NeonPurple.copy(alpha = 0.3f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(innerPadding)
        ) {
            // Quick tab-switcher row for ALL navigation items so the user can see everything easily!
            ScrollableTabRow(
                selectedTabIndex = finalNavItems.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0),
                containerColor = Color(0xFF13111C),
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    val index = finalNavItems.indexOfFirst { it.first == selectedTab }.coerceAtLeast(0)
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = NeonTeal
                    )
                }
            ) {
                finalNavItems.forEachIndexed { index, (tabId, icon, label) ->
                    val isSelected = selectedTab == tabId
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tabId },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) NeonTeal else TextSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) NeonTeal else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    )
                }
            }

            // Central page contents
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                    },
                    label = "TabContent"
                ) { targetTab ->
                    when (targetTab) {
                        "Dashboard" -> DashboardPage(state, weeklyReport, viewModel)
                        "Studio" -> StudioPage(state, viewModel)
                        "Tours" -> ToursPage(state, viewModel)
                        "Trading" -> TradingPage(state, viewModel)
                        "Gigs (Jobs)" -> SideJobsPage(state, viewModel)
                        "Social & Practice" -> SocialPracticePage(state, viewModel)
                        "Marketplace" -> MarketplacePage(state, viewModel)
                        "Record Label" -> RecordLabelPage(state, viewModel)
                        "Mailbox" -> MailboxPage(state, viewModel)
                        "Shop & Profile" -> ShopProfilePage(state, viewModel)
                        "Admin Panel" -> AdminPage(allPlayers, viewModel)
                    }
                }
            }
        }
    }

    if (isPhoneOpen) {
        SmartphoneOverlay(
            state = state,
            viewModel = viewModel,
            onDismiss = { isPhoneOpen = false }
        )
    }
}

// --- TAB PAGES ---

@Composable
fun DashboardPage(state: GameStateEntity, weeklyReport: String, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Global World Event Active Banner
        if (state.activeGlobalEvent != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (state.globalEventEffect) {
                            "LOCKDOWN" -> Color(0xFFC62828).copy(alpha = 0.15f)
                            "RECESSION" -> Color(0xFFEF6C00).copy(alpha = 0.15f)
                            else -> NeonTeal.copy(alpha = 0.15f)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.5.dp, 
                        when (state.globalEventEffect) {
                            "LOCKDOWN" -> Color(0xFFE53935)
                            "RECESSION" -> Color(0xFFFB8C00)
                            else -> NeonTeal
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Global Event",
                            tint = if (state.globalEventEffect == "LOCKDOWN") Color(0xFFE53935) else NeonTeal,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "GLOBAL EVENT: ${state.activeGlobalEvent}",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            val description = when (state.globalEventEffect) {
                                "LOCKDOWN" -> "Pandemic lockdown active! Tours generate 0 revenue. Streaming platform royalties increased by +30%."
                                "RECESSION" -> "Global recession active! Live gig pay and tour revenue reduced by -30%. Merch sales dropped by -40%."
                                "FESTIVAL" -> "Summer Festival Boom is live! Tour ticket sales and attendance boosted by +40%."
                                "AI_DEBATE" -> "Heated AI songwriting debates! Creative inspiration drained. Unrefined artists receive review penalties."
                                "VIRAL_CHALLENGE" -> "Viral TikTok/Social challenge trending! Social media promotions are twice as effective."
                                else -> "Industry environment variables are shifting. Watch your step!"
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

        item {
            // Big Proceed Next Week trigger card
            Card(
                colors = CardDefaults.cardColors(containerColor = NeonPurple.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, NeonPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "READY FOR NEXT WEEK?",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Advance state, receive salaries, grow streaming listener accounts, and update markets.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.progressToNextWeek() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .testTag("next_week_button")
                    ) {
                        Text(
                            text = "ADVANCE TO WEEK ${state.currentWeek + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        item {
            // Stats & Multiple Reputations Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Artist Statistics & Reputations",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("CORE SKILLS", style = MaterialTheme.typography.bodySmall, color = NeonPurple, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    StatRow(label = "Voice Skill", percent = state.voiceSkill, color = NeonTeal)
                    StatRow(label = "Writing Skill", percent = state.writingSkill, color = Color(0xFFFF9800))
                    StatRow(label = "Business Skill", percent = state.businessSkill, color = Color(0xFF4CAF50))

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("REPUTATIONS", style = MaterialTheme.typography.bodySmall, color = NeonPink, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    StatRow(label = "Popularity", percent = state.popularity, color = NeonPink)
                    StatRow(label = "Influence", percent = state.influence, color = NeonPurple)
                    StatRow(label = "Industry Respect", percent = state.industryRespect, color = Color(0xFF00E676))
                    StatRow(label = "Public Image", percent = state.publicImage, color = Color(0xFF29B6F6))
                    StatRow(label = "Critical Acclaim", percent = state.criticalAcclaim, color = Color(0xFFFFEE58))

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Legacy Points:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(text = "👑 ${state.legacyPoints} PTS", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Fan Segmentation Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Audience Segmentation (Total: ${String.format("%,d", state.fans)})",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val segments = listOf(
                        Triple("Casual Listeners", state.casualFans, "Stream singles regularly"),
                        Triple("Hardcore Devotees", state.hardcoreFans, "Buy physical releases and merch"),
                        Triple("Elite Collectors", state.collectors, "Snatch limited special editions"),
                        Triple("Concert Goers", state.concertGoers, "High attendance rate on tours"),
                        Triple("International Base", state.internationalFans, "Active overseas markets"),
                        Triple("Online Community", state.onlineCommunity, "Boost social media metrics")
                    )

                    segments.forEach { (name, count, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                    }
                }
            }
        }

        item {
            // Mental Health & Self-Care Actions Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mental Health & Stress State",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.burnout > 0) {
                            Text(
                                text = "🔥 BURNING OUT (${state.burnout.toInt()}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    StatRow(label = "Stress Level", percent = state.stress, color = if (state.stress > 60f) Color(0xFFE53935) else NeonTeal)
                    StatRow(label = "Creative Inspiration", percent = state.creativeInspiration, color = NeonPurple)
                    StatRow(label = "General Happiness", percent = state.happiness, color = Color(0xFF00E676))
                    StatRow(label = "Work-Life Balance", percent = state.workLifeBalance, color = Color(0xFFFB8C00))

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "SELF-CARE ACTIONS (REDUCE STRESS & RESTORE INSPIRATION)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.hobbiesAndRest() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282535)),
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Rest & Hobbies", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Cost: 40 Energy", fontSize = 8.sp, color = TextSecondary)
                            }
                        }

                        Button(
                            onClick = { viewModel.seekTherapy() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282535)),
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Seek Therapy", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Cost: $1,000 Cash", fontSize = 8.sp, color = TextSecondary)
                            }
                        }

                        Button(
                            onClick = { viewModel.takeVacation() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282535)),
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Hawaii Trip", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Cost: $5,000 Cash", fontSize = 8.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        item {
            // Weekly Summary Log Report Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Career News & Reports",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F0E17))
                            .border(1.dp, Color(0xFF282535), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = weeklyReport.ifEmpty { "No active reports. Start by practicing skills or going on side jobs!" },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, percent: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            Text(text = "${String.format("%.1f", percent)}%", style = MaterialTheme.typography.bodyMedium, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = percent / 100f,
            modifier = Modifier.fillMaxWidth(),
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
    var selectedGenre by remember { mutableStateOf(GameData.GENRES[0].id) }
    var selectedProducer by remember { mutableStateOf<String?>(null) }

    // Video creation section states
    var selectedVideoSongId by remember { mutableStateOf<String?>(null) }
    var selectedVideoTier by remember { mutableStateOf("Standard") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TabRow(
                selectedTabIndex = if (selectedStudioTab == "Record Song") 0 else 1,
                containerColor = CardBg
            ) {
                Tab(
                    selected = selectedStudioTab == "Record Song",
                    onClick = { selectedStudioTab = "Record Song" },
                    text = { Text("Record Song") }
                )
                Tab(
                    selected = selectedStudioTab == "Create Music Video",
                    onClick = { selectedStudioTab = "Create Music Video" },
                    text = { Text("Music Video") }
                )
            }
        }

        if (selectedStudioTab == "Record Song") {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Record a New Track",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = songTitle,
                            onValueChange = { songTitle = it },
                            label = { Text("Song Title") },
                            placeholder = { Text("e.g. Moonlight Bass") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

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
                            label = { Text("Studio Production Budget ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Producer", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
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
                                label = { Text("No Producer") }
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

                        Button(
                            onClick = {
                                val budgetVal = studioBudget.toDoubleOrNull() ?: 500.0
                                viewModel.createSong(songTitle, budgetVal, selectedGenre, selectedProducer)
                                songTitle = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("RECORD TRACK", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Create Music Video screen
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Production Tier Budget:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
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
                                        .background(if (selectedVideoTier == tier) Color(0xFF322A45) else Color.Transparent)
                                        .clickable { selectedVideoTier = tier }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "$tier Video", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "Cost: $${String.format("%,.0f", cost)}", color = Color(0xFF4CAF50))
                                    Text(text = effects, color = NeonTeal, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                selectedVideoSongId?.let { songId ->
                                    viewModel.createMusicVideo(songId, selectedVideoTier)
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

        item {
            Text(
                text = "Recorded Catalog",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.songs.isEmpty()) {
            item {
                Text(
                    text = "No recorded songs in your archive yet.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(state.songs) { song ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = song.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = "Rating: ${String.format("%.1f", song.rating)} / 5.0 ⭐", color = Color(0xFFFFEB3B))
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
                                    Text(text = "${String.format("%,d", streams)} streams", color = TextPrimary, fontSize = 12.sp)
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

@Composable
fun ToursPage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedLevel by remember { mutableStateOf("Simple") }
    var promoBudget by remember { mutableStateOf("1000") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Book a New Tour",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Tour Tier:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))

                    val tiers = listOf(
                        Triple("Simple", "Cost: $2000, 2 wks, slots: 2/wk, Pay: $5/att", "Simple Level"),
                        Triple("Pro", "Cost: $5000, 6 wks, slots: 3/wk, Pay: $15/att", "Pro Level"),
                        Triple("Premium", "Cost: $10000, 15 wks, slots: 5/wk, Pay: $15/att", "Premium Level")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        tiers.forEach { (level, desc, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (selectedLevel == level) Color(0xFF231E35) else Color.Transparent)
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

                    OutlinedTextField(
                        value = promoBudget,
                        onValueChange = { promoBudget = it },
                        label = { Text("Marketing/Promotion Budget ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val budgetVal = promoBudget.toDoubleOrNull() ?: 1000.0
                            viewModel.bookTour(selectedLevel, budgetVal)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("BOOK TOUR DATE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "My Scheduled / Active Tours",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.tours.isEmpty()) {
            item {
                Text(
                    text = "No active or scheduled tours. Set up your touring season above!",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(state.tours) { tour ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${tour.level} Tour", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when (tour.status) {
                                        "ACTIVE" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                        "COMPLETED" -> Color.Gray.copy(alpha = 0.2f)
                                        else -> Color(0xFFFFEB3B).copy(alpha = 0.2f)
                                    }
                                )
                            ) {
                                Text(
                                    text = tour.status,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (tour.status) {
                                        "ACTIVE" -> Color(0xFF4CAF50)
                                        "COMPLETED" -> Color.Gray
                                        else -> Color(0xFFFFEB3B)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Weeks Played: ${tour.currentWeek} / ${tour.totalWeeks}", color = TextSecondary)
                        Text(text = "Weekly Energy: ${tour.weeklyEnergyCost}%  |  Weekly Time: ${tour.weeklyTimeCost} slots", color = TextSecondary)
                        Text(text = "Booked Year: ${tour.bookedYear} Week: ${tour.bookedWeek}", color = TextSecondary)
                        Text(text = "Max Attendance Reached: ${tour.baseAttendance} people", color = NeonTeal, fontWeight = FontWeight.Bold)

                        if (tour.status == "ACTIVE" || tour.status == "BOOKED") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.promoteTour(tour.id, 1000.0) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("PROMOTE (+$1,000)")
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TabRow(
                selectedTabIndex = if (selectedTradeTab == "Stocks") 0 else 1,
                containerColor = CardBg
            ) {
                Tab(
                    selected = selectedTradeTab == "Stocks",
                    onClick = { selectedTradeTab = "Stocks" },
                    text = { Text("Stocks") }
                )
                Tab(
                    selected = selectedTradeTab == "Crypto",
                    onClick = { selectedTradeTab = "Crypto" },
                    text = { Text("Crypto Currency") }
                )
            }
        }

        if (selectedTradeTab == "Stocks") {
            items(state.stocks) { stock ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = stock.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                if (stock.connectedTo != null) {
                                    Text(text = "Connected to Tours", color = NeonTeal, fontSize = 11.sp)
                                }
                            }
                            Text(
                                text = "$${String.format("%.2f", stock.currentPrice)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Shares Owned: ${stock.quantity}", color = TextSecondary)
                        Text(text = "Weekly Dividend: ${(stock.weeklyDividendPercent * 100)}% of stock price", color = TextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.buyStock(stock.id, 10) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("BUY 10")
                            }

                            OutlinedButton(
                                onClick = { viewModel.sellStock(stock.id, 10) },
                                modifier = Modifier.weight(1f),
                                enabled = stock.quantity >= 10
                            ) {
                                Text("SELL 10")
                            }
                        }
                    }
                }
            }
        } else {
            // Cryptocurrency tab
            items(state.cryptos) { crypto ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = crypto.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = "$${String.format("%,.2f", crypto.currentPrice)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                                color = Color(0xFFFF9800)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = "Holdings Owned: ${String.format("%.4f", crypto.quantity)}", color = TextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.buyCrypto(crypto.id, 500.0) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("BUY $500")
                            }

                            OutlinedButton(
                                onClick = { viewModel.sellCrypto(crypto.id, crypto.quantity.coerceAtMost(0.1)) },
                                modifier = Modifier.weight(1f),
                                enabled = crypto.quantity > 0.0
                            ) {
                                Text("SELL PART")
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
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Side Jobs Gig Board",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, if (job.isActive) NeonTeal else Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = job.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Weekly Pay: $${String.format("%,.0f", job.weeklyPay)}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }

                        Switch(
                            checked = job.isActive,
                            onCheckedChange = { viewModel.toggleSideJob(job.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonTeal,
                                checkedTrackColor = NeonTeal.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Energy cost: ${job.energyCost}% / wk", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Time slot cost: ${job.timeCost} slots", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SocialPracticePage(state: GameStateEntity, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Social Media Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Followers", color = TextSecondary, fontSize = 11.sp)
                            Text(String.format("%,d", state.followers), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Industry Followers", color = TextSecondary, fontSize = 11.sp)
                            Text(String.format("%,d", state.industryFollowers), color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Trending Score", color = TextSecondary, fontSize = 11.sp)
                            Text(String.format("%.1f%%", state.trendingScore), color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Conversation Volume", color = TextSecondary, fontSize = 11.sp)
                            Text(String.format("%,d posts/wk", state.conversationVolume), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Public Sentiment Analysis", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    // Simple progress row for sentiments
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Color(0xFF2C2A3A), RoundedCornerShape(5.dp))
                    ) {
                        val posWeight = (state.positiveSentiment / 100f).coerceIn(0.01f, 1f)
                        val neuWeight = (state.neutralSentiment / 100f).coerceIn(0.01f, 1f)
                        val negWeight = (state.negativeSentiment / 100f).coerceIn(0.01f, 1f)
                        val totalWeight = posWeight + neuWeight + negWeight

                        Box(modifier = Modifier.weight(posWeight / totalWeight).fillMaxHeight().background(Color(0xFF4CAF50)))
                        Box(modifier = Modifier.weight(neuWeight / totalWeight).fillMaxHeight().background(Color(0xFF9E9E9E)))
                        Box(modifier = Modifier.weight(negWeight / totalWeight).fillMaxHeight().background(Color(0xFFF44336)))
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Positive: ${String.format("%.0f%%", state.positiveSentiment)}", color = Color(0xFF4CAF50), fontSize = 11.sp)
                        Text("Neutral: ${String.format("%.0f%%", state.neutralSentiment)}", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                        Text("Negative: ${String.format("%.0f%%", state.negativeSentiment)}", color = Color(0xFFF44336), fontSize = 11.sp)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Stewardship & Character Traits",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // 6 key attributes as structured progress sliders
                    val traits = listOf(
                        Triple("Business Trust", state.businessReputation, Color(0xFF29B6F6)),
                        Triple("Discipline", state.discipline, NeonPurple),
                        Triple("Integrity", state.integrity, Color(0xFF81C784)),
                        Triple("Generosity", state.generosity, NeonPink),
                        Triple("Humility", state.humility, Color(0xFFFFB300)),
                        Triple("Reliability", state.reliability, NeonTeal)
                    )

                    traits.forEach { (name, value, color) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(90.dp))
                            LinearProgressIndicator(
                                progress = value / 100f,
                                color = color,
                                trackColor = Color(0xFF1E1C2B),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .background(Color.Transparent, RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = String.format("%.0f", value), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            // Study card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Wealth Education & Self-Study", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Invest in learning. Study financial principles or consult professionals to make sound business decisions.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.readBusinessBook() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Read Fin Book\n(Energy -25 • $100)", fontSize = 11.sp, textAlign = TextAlign.Center)
                        }

                        Button(
                            onClick = { viewModel.hireBusinessConsultant() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Consult Advisor\n(Energy -15 • $1,500)", fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        item {
            // Training Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Practice & Master Skills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.practiceVoice() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Voice Training\n(Energy -20)", fontSize = 11.sp, textAlign = TextAlign.Center)
                        }

                        Button(
                            onClick = { viewModel.practiceWriting() },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Write Songs\n(Energy -20)", fontSize = 11.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }

        item {
            // Social posting card
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Engage on Social Media", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Posting regularly keeps your brand active, gets fans, and lets you promote your music.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.postOnSocialMedia() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("POST UPDATE (Energy -15)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Title for Simulated NPC connections
            Text(
                text = "Simulated Industry Connections",
                style = MaterialTheme.typography.titleLarge,
                color = NeonTeal,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(state.npcs) { npc ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, if (npc.relationshipWithPlayer < 35f) Color(0xFFC62828) else Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                            npc.isRetired -> Color(0xFFFFB300)
                            npc.relationshipWithPlayer > 75f -> Color(0xFF00E676)
                            npc.relationshipWithPlayer < 35f -> Color(0xFFE53935)
                            else -> Color(0xFF29B6F6)
                        }

                        Box(
                            modifier = Modifier
                                .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, badgeColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = relationText,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    StatRow(label = "Popularity Competition", percent = npc.popularity, color = NeonPink)
                    StatRow(label = "Relationship with You", percent = npc.relationshipWithPlayer, color = NeonPurple)

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "INTERACTION PANEL",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.strengthenFriendship(npc.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282535)),
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Grab Dinner", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Cost: $500", fontSize = 7.sp, color = TextSecondary)
                            }
                        }

                        Button(
                            onClick = { viewModel.startRivalry(npc.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282535)),
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Start Rivalry", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Cost: 15 Energy", fontSize = 7.sp, color = TextSecondary)
                            }
                        }

                        Button(
                            onClick = { viewModel.produceForNPC(npc.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282535)),
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Produce Track", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Earn: Up to $15k", fontSize = 7.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplacePage(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedCategory by remember { mutableStateOf("Cars") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TabRow(
                selectedTabIndex = when (selectedCategory) {
                    "Cars" -> 0
                    "Houses" -> 1
                    else -> 2
                },
                containerColor = CardBg
            ) {
                Tab(selected = selectedCategory == "Cars", onClick = { selectedCategory = "Cars" }, text = { Text("Cars") })
                Tab(selected = selectedCategory == "Houses", onClick = { selectedCategory = "Houses" }, text = { Text("Houses") })
                Tab(selected = selectedCategory == "Instruments", onClick = { selectedCategory = "Instruments" }, text = { Text("Instruments") })
            }
        }

        if (selectedCategory == "Cars") {
            items(GameData.CARS) { car ->
                val owned = state.ownedCars.any { it.id == car.id }
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, if (owned) NeonTeal else Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = car.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Reduces Tour travel costs by ${(car.costReductionPercent * 100).toInt()}%", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Price: $${String.format("%,.0f", car.cost)}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.buyCar(car.id) },
                            enabled = !owned && state.cash >= car.cost,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text(if (owned) "OWNED" else "BUY")
                        }
                    }
                }
            }
        } else if (selectedCategory == "Houses") {
            items(GameData.HOUSES) { house ->
                val owned = state.ownedHouses.any { it.id == house.id }
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, if (owned) NeonTeal else Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = house.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Increases max energy by +${house.energyBonus}", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Price: $${String.format("%,.0f", house.cost)}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.buyHouse(house.id) },
                            enabled = !owned && state.cash >= house.cost,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text(if (owned) "OWNED" else "BUY")
                        }
                    }
                }
            }
        } else {
            items(GameData.INSTRUMENTS) { inst ->
                val owned = state.ownedInstruments.any { it.id == inst.id }
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, if (owned) NeonTeal else Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = inst.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Boosts song studio ratings by +${inst.ratingBonus}⭐", fontSize = 12.sp, color = TextSecondary)
                            Text(text = "Price: $${String.format("%,.0f", inst.cost)}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.buyInstrument(inst.id) },
                            enabled = !owned && state.cash >= inst.cost,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Text(if (owned) "OWNED" else "BUY")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecordLabelPage(state: GameStateEntity, viewModel: GameViewModel) {
    var labelNameInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.signedLabelId != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Signed With Def Jam Records",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Label is taking ${(state.signedLabelCut * 100).toInt()}% cut of your songs and tours, but you get +30% popularity/influence boost and tour setup costs are covered by 50%!", color = TextPrimary)
                    }
                }
            }
        } else if (state.ownLabel != null) {
            // Own label manager
            val label = state.ownLabel
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "My Record Label: ${label.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = NeonTeal,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Accrued Weekly Debts: $${String.format("%.2f", label.accruedDebts)}", color = NeonPink)
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
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Promotion Team Active:", style = MaterialTheme.typography.titleMedium)
                        if (label.promoTeams.isEmpty()) {
                            Text(text = "No promotion team hired yet. Costly weekly but boosts label renown!", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(onClick = { viewModel.hirePromoTeam("promo_team_1", "Vanguard PR", 500.0, 1.5f) }) {
                                Text("Hire Vanguard PR ($500/wk)")
                            }
                        } else {
                            label.promoTeams.forEach { team ->
                                Text(text = "✔️ ${team.name} ($${team.weeklyCost}/wk) +${team.popularityBoost}% boost", color = TextPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "Label Manager Active:", style = MaterialTheme.typography.titleMedium)
                        if (label.managers.isEmpty()) {
                            Text(text = "No manager hired yet! Hire one to run standard operations.", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(onClick = { viewModel.hireManager("mgr_1", "Marcus Pierce", 1200.0, 1.2f) }) {
                                Text("Hire Marcus Pierce ($1200/wk)")
                            }
                        } else {
                            label.managers.forEach { mgr ->
                                Text(text = "✔️ ${mgr.name} ($${mgr.weeklyCost}/wk) Efficiency: ${mgr.efficiency}", color = TextPrimary)
                            }
                        }
                    }
                }
            }
        } else {
            // Create Label Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
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
                            text = "Requirements:\n• $5,000,000 Cash\n• 70% Popularity\n• 80% Influence\n• Cost to establish: $2,000,000",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = labelNameInput,
                            onValueChange = { labelNameInput = it },
                            label = { Text("Label Name") },
                            placeholder = { Text("e.g. Apex Records") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val meetsReqs = state.cash >= 5000000.0 && state.popularity >= 70f && state.influence >= 80f
                        Button(
                            onClick = { viewModel.createOwnRecordLabel(labelNameInput) },
                            enabled = labelNameInput.isNotBlank() && meetsReqs,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CREATE OWN RECORD LABEL")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MailboxPage(state: GameStateEntity, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mailbox & Contracts",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (state.mailbox.isEmpty()) {
            item {
                Text(
                    text = "No mail in your inbox yet.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(state.mailbox) { mail ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, if (mail.isAccepted) Color.Gray else NeonTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "From: ${mail.sender}", fontWeight = FontWeight.Bold, color = NeonTeal)
                            if (mail.isAccepted) {
                                Text(text = "ACCEPTED", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(text = mail.subject, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = mail.body, style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                        if (!mail.isAccepted && (mail.type == "FEATURE" || mail.type == "LABEL_OFFER")) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.acceptMail(mail.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("ACCEPT CONTRACT")
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
    var reqType by remember { mutableStateOf("CASH") }
    var reqAmount by remember { mutableStateOf("10000") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(NeonPurple, NeonPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.artistName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.artistName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = state.email, color = TextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.restartGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("RESET GAME & RESTART CAREER")
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Store - Buy Limit Boosters",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Submit a request to grant additional capacity. Requires mock bank verification from Game Admin.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Select item to boost:", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(selected = reqType == "CASH", onClick = { reqType = "CASH"; reqAmount = "10000" }, label = { Text("+$10,000 cash ($1.00)") })
                        FilterChip(selected = reqType == "TIME_SLOT", onClick = { reqType = "TIME_SLOT"; reqAmount = "7" }, label = { Text("+7 Time slots ($0.50)") })
                        FilterChip(selected = reqType == "ENERGY", onClick = { reqType = "ENERGY"; reqAmount = "60" }, label = { Text("+60 Max Energy ($0.50)") })
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Mock bank details info
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F0E17))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "GAME BANK MOCK INFO:\nBank: Music Studio Bank\nAccount: 011-ARTIST-STORY\nCode: 442211",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.submitPurchaseRequest(reqType, reqAmount.toIntOrNull() ?: 10)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SUBMIT PURCHASE REQUEST")
                    }
                }
            }
        }

        item {
            // Entertainment Empire Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏢 Entertainment Empire & Ventures",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Acquire high-yield business assets to generate passive weekly streams of income. Builds your ultimate industrial legacy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val businessOffers = listOf(
                        Quadruple("Indie Recording Studio", "STUDIO", 50000.0, 1200.0),
                        Quadruple("Boutique Record Label", "LABEL", 120000.0, 3500.0),
                        Quadruple("Public Relations Agency", "PR_AGENCY", 250000.0, 8500.0),
                        Quadruple("Global Live Booking Agency", "LIVE_AGENCY", 500000.0, 20000.0)
                    )

                    businessOffers.forEach { (name, type, cost, profit) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFF0F0E17), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF282535), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = name, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
                                Text(text = "Cost: $${String.format("%,.0f", cost)} • Profit: +$${String.format("%,.0f", profit)}/wk", style = MaterialTheme.typography.bodySmall, color = NeonPink)
                            }
                            Button(
                                onClick = { viewModel.buyBusinessAsset(name, type, cost, profit) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("Buy Asset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (state.ownedBusinesses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "YOUR PORTFOLIO VENTURES",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        state.ownedBusinesses.forEach { biz ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "🏢 ${biz.name} (${biz.type})", style = MaterialTheme.typography.bodySmall, color = Color.White)
                                Text(text = "+$${String.format("%,.0f", biz.weeklyProfit)}/wk", style = MaterialTheme.typography.bodySmall, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "My Purchase Request Statuses",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.purchaseRequests.isEmpty()) {
            item {
                Text(text = "No purchase requests submitted yet.", color = TextSecondary)
            }
        } else {
            items(state.purchaseRequests) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Request: +${req.amount} ${req.type}", fontWeight = FontWeight.Bold)
                            Text(text = "Status: ${req.status}", color = when (req.status) {
                                "APPROVED" -> Color(0xFF4CAF50)
                                "REJECTED" -> NeonPink
                                else -> Color(0xFFFFEB3B)
                            })
                        }
                    }
                }
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun AdminPage(allPlayers: List<GameStateEntity>, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Game Administrator Console",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonTeal,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Logged in as davemcthunder/oluwasegundave. See all player accounts and approve purchase requests.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        item {
            Text(text = "Player Profiles & Requests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (allPlayers.isEmpty()) {
            item {
                Text(text = "No registered players yet.")
            }
        } else {
            items(allPlayers) { player ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, Color(0xFF2C2A3A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "${player.artistName} (${player.email})", fontWeight = FontWeight.Bold, color = NeonTeal)
                        Text(text = "Cash: $${String.format("%,.0f", player.cash)} | Fans: ${player.fans}", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Max Slots: ${player.maxTimeSlots} | Max Energy: ${player.maxEnergy}", fontSize = 12.sp, color = TextSecondary)

                        if (player.purchaseRequests.any { it.status == "PENDING" }) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Pending Requests:", fontWeight = FontWeight.Bold, color = NeonPink, fontSize = 12.sp)

                            player.purchaseRequests.filter { it.status == "PENDING" }.forEach { req ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F0E17))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "+${req.amount} ${req.type}", style = MaterialTheme.typography.bodySmall)
                                    Row {
                                        Button(
                                            onClick = { viewModel.adminApproveRequest(player.email, req.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Text("Approve", fontSize = 11.sp)
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Button(
                                            onClick = { viewModel.adminRejectRequest(player.email, req.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                            modifier = Modifier.height(32.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp)
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
    }
}
