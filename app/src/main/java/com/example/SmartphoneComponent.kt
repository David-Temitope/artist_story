package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.theme.*
import java.util.UUID
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SmartphoneOverlay(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            // Simulated Smartphone Outer Bezel Frame
            Card(
                shape = RoundedCornerShape(36.dp),
                border = BorderStroke(3.dp, Brush.verticalGradient(listOf(Color(0xFF5A5475), Color(0xFF13111C)))),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0E17)),
                modifier = Modifier
                    .width(410.dp)
                    .height(820.dp)
                    .clickable(enabled = false) {}
                    .padding(12.dp)
                    .testTag("smartphone_bezel_frame")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Wallpaper Gradient Layer
                    val wallpaperBrush = when (state.wallpaperId) {
                        "sunset" -> Brush.verticalGradient(listOf(Color(0xFFF07B50), Color(0xFF901C40), Color(0xFF1D0E25)))
                        "cyber" -> Brush.verticalGradient(listOf(Color(0xFF00F0FF), Color(0xFF7000FF), Color(0xFF1B0035)))
                        "nebula" -> Brush.verticalGradient(listOf(Color(0xFF120C3F), Color(0xFF4A007A), Color(0xFF0B0720)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF1E1E2F), Color(0xFF13111C)))
                    }
                    Box(modifier = Modifier.fillMaxSize().background(wallpaperBrush))

                    // Phone OS Status Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .zIndex(10f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Year ${state.currentYear}, W${state.currentWeek}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            fontSize = 11.sp
                        )

                        // Punch Hole Camera Notch
                        Box(
                            modifier = Modifier
                                .size(width = 64.dp, height = 16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                        )

                        // Signal & Battery icons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SignalCellular4Bar, contentDescription = "Signal", tint = Color.White, modifier = Modifier.size(11.dp))
                            Icon(imageVector = Icons.Default.Wifi, contentDescription = "Wi-Fi", tint = Color.White, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "92%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = "Battery", tint = Color(0xFF4CAF50), modifier = Modifier.size(12.dp))
                        }
                    }

                    // Content Area (Separated by Lock Screen)
                    AnimatedContent(
                        targetState = state.isPhoneLocked,
                        transitionSpec = {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        },
                        label = "LockScreenState",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 28.dp, bottom = 12.dp)
                    ) { isLocked ->
                        if (isLocked) {
                            LockScreenView(state = state, viewModel = viewModel)
                        } else {
                            SmartphoneHomeView(state = state, viewModel = viewModel, onDismiss = onDismiss)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LockScreenView(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Clock Section
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 40.dp)) {
            Text(
                text = "10:42",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Light, color = Color.White),
                fontSize = 64.sp
            )
            Text(
                text = "Sunday, Week ${state.currentWeek}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        // Lockscreen Notifications Widget
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "NOTIFICATIONS",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp)
            )

            val logs = state.notificationLogs.take(3)
            if (logs.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No pending notifications.", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }
            } else {
                logs.forEach { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NeonPink)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = log,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Slide to Unlock Button
        Button(
            onClick = { viewModel.setPhoneLocked(false) },
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("unlock_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LockOpen, contentDescription = "Unlock", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "TAP TO UNLOCK", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun SmartphoneHomeView(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    var activeAppId by remember { mutableStateOf<String?>(null) }

    AnimatedContent(
        targetState = activeAppId,
        transitionSpec = {
            if (targetState == null) {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            } else {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            }
        },
        label = "ActiveAppContainer"
    ) { app ->
        if (app == null) {
            SmartphoneHomeScreenGrid(state = state, viewModel = viewModel, onLaunchApp = { activeAppId = it })
        } else {
            SmartphoneAppShell(
                appId = app,
                state = state,
                viewModel = viewModel,
                onCloseApp = { activeAppId = null }
            )
        }
    }
}

@Composable
fun SmartphoneHomeScreenGrid(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onLaunchApp: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header Date & Widget Panel
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Artist Ecosystem OS",
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Artist Brand Value", color = NeonTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = state.artistName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Followers", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        Text(text = "${state.followers}", color = NeonPink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Custom App Icons Grid (15 Apps)
        val apps = listOf(
            AppConfig("chat", "SeeChat", Icons.Default.ChatBubble, NeonTeal, state.chatConversations.count { it.unreadCount > 0 }),
            AppConfig("cast", "Cast Social", Icons.Default.AlternateEmail, NeonPink, 0),
            AppConfig("metube", "MeTube", Icons.Default.PlayArrow, Color(0xFFFF0000), 0),
            AppConfig("weshare", "WESHARE", Icons.Default.TrendingUp, Color(0xFF4CAF50), state.pendingStockOrders.size),
            AppConfig("genesis", "Genesis", Icons.Default.AccountBalanceWallet, Color(0xFFFF9800), state.pendingCryptoOrders.size),
            AppConfig("bank", "Apex Bank", Icons.Default.AccountBalance, Color(0xFF673AB7), if (state.taxDebt > 0.0) 1 else 0),
            AppConfig("music_app", "SoundStream", Icons.Default.MusicNote, Color(0xFF00BCD4), 0),
            AppConfig("recruit", "Staff Hub", Icons.Default.Group, Color(0xFF00E5FF), 0),
            AppConfig("spiritual", "Soul Care", Icons.Default.Favorite, Color(0xFFFFEB3B), 0),
            AppConfig("calendar", "Planner", Icons.Default.DateRange, Color(0xFFE91E63), 0),
            AppConfig("browser", "Web Hub", Icons.Default.Language, Color(0xFF3F51B5), 0),
            AppConfig("camera", "Camera", Icons.Default.PhotoCamera, Color(0xFF4CAF50), 0),
            AppConfig("notes", "Notes", Icons.Default.Edit, Color(0xFFFFC107), 0),
            AppConfig("email", "Email", Icons.Default.Email, Color(0xFF2196F3), 0),
            AppConfig("settings", "Settings", Icons.Default.Settings, Color.Gray, 0)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f).padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(apps) { app ->
                AppIconWidget(app = app, onClick = { onLaunchApp(app.id) })
            }
        }

        // Bottom Navigation Bar Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = { viewModel.setPhoneLocked(true) },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", tint = Color.White)
            }

            IconButton(
                onClick = { viewModel.clearNotifications() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            ) {
                Icon(imageVector = Icons.Default.NotificationsOff, contentDescription = "Clear Logs", tint = Color.White)
            }
        }
    }
}

data class AppConfig(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val badgeCount: Int = 0
)

@Composable
fun AppIconWidget(app: AppConfig, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("app_icon_${app.id}")
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(app.color, app.color.copy(alpha = 0.6f))))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = app.icon, contentDescription = app.label, tint = Color.White, modifier = Modifier.size(28.dp))
            }

            if (app.badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-4).dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SmartphoneAppShell(
    appId: String,
    state: GameStateEntity,
    viewModel: GameViewModel,
    onCloseApp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
    ) {
        // Top App Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B1A24))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCloseApp, modifier = Modifier.testTag("app_back_button")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonTeal)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (appId) {
                        "chat" -> "SeeChat Messaging"
                        "cast" -> "Cast Social Feed"
                        "metube" -> "MeTube Video Hub"
                        "weshare" -> "WESHARE Brokerage"
                        "genesis" -> "Genesis Exchange"
                        "bank" -> "Apex Bank & Trust"
                        "music_app" -> "SoundStream Music"
                        "recruit" -> "Staff Hub Hub"
                        "spiritual" -> "Soul Care Sanctuary"
                        "email" -> "Professional Email"
                        else -> "Settings"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.Green)
            )
        }

        // Active App Screen Injection
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (appId) {
                "chat" -> SeeChatScreen(state, viewModel)
                "cast" -> CastSocialScreen(state, viewModel)
                "metube" -> MeTubeVideoScreen(state, viewModel)
                "weshare" -> WeShareExchangeScreen(state, viewModel)
                "genesis" -> GenesisExchangeScreen(state, viewModel)
                "bank" -> ApexBankingScreen(state, viewModel)
                "music_app" -> SoundStreamMusicScreen(state, viewModel)
                "recruit" -> RecruitmentHallScreen(state, viewModel)
                "spiritual" -> SpiritualSanctuaryScreen(state, viewModel)
                "calendar" -> PlannerAppScreen(state, viewModel)
                "browser" -> WebHubAppScreen(state, viewModel)
                "camera" -> CameraAppScreen(state, viewModel)
                "notes" -> NotesAppScreen(state, viewModel)
                "email" -> ProfessionalEmailScreen(state, viewModel)
                "settings" -> WallpaperSettingsScreen(state, viewModel)
            }
        }
    }
}

// =================== 1. SEEsCHAT MESSAGING SCREEN ===================
@Composable
fun SeeChatScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var activeChatNpcId by remember { mutableStateOf<String?>(null) }

    if (activeChatNpcId == null) {
        // Conversations List view
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "NPC Conversations",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            val npcsList = state.npcs
            if (npcsList.isEmpty()) {
                item {
                    Text(text = "No contacts available. Release music to get noticed!", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(npcsList) { npc ->
                    val conv = state.chatConversations.find { it.npcId == npc.id }
                    val lastMsg = conv?.messages?.lastOrNull()?.message ?: "Tap to start conversation..."
                    val unread = conv?.unreadCount ?: 0

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        border = BorderStroke(1.dp, if (unread > 0) NeonTeal else Color(0xFF2E2C39)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.markChatRead(npc.id)
                                activeChatNpcId = npc.id
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(NeonTeal, NeonPurple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = npc.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = npc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${npc.relationshipWithPlayer.toInt()}% Rel", color = NeonTeal, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = lastMsg,
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (unread > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(NeonTeal)
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Individual Chat Room view
        val npcId = activeChatNpcId!!
        val npc = state.npcs.find { it.id == npcId } ?: return
        val conv = state.chatConversations.find { it.npcId == npcId }
        val messages = conv?.messages ?: emptyList()

        Column(modifier = Modifier.fillMaxSize()) {
            // Chat Room Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF15141C))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeChatNpcId = null }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Arrow", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(text = npc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Personality: ${npc.personality} | Status: Online", color = NeonTeal, fontSize = 10.sp)
                }
            }

            // Message Bubble list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isPlayer = msg.senderId == "PLAYER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isPlayer) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPlayer) NeonPurple else Color(0xFF2C2A3A)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = msg.message, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Suggested Player Reply chips
            val replies = listOf(
                "Hey! Just checking in, hope the grind is going well.",
                "Let's collab soon! We should secure a top placement.",
                "Time is money! Let's schedule a professional gig.",
                "Respect the hustle. I'm focusing on the next project."
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .background(Color(0xFF13111C))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                replies.forEach { text ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF262335)),
                        border = BorderStroke(1.dp, NeonTeal),
                        modifier = Modifier
                            .clickable { viewModel.sendChatMessage(npcId, text) }
                    ) {
                        Text(
                            text = text,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

// =================== 2. CAST SOCIAL MEDIA CLONE SCREEN ===================
@Composable
fun CastSocialScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var newPostContent by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Draft Cast Post card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "What's happening in the industry?", color = NeonPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = newPostContent,
                    onValueChange = { newPostContent = it },
                    placeholder = { Text("Draft official cast post...", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Energy cost: 10", color = Color.Yellow, fontSize = 10.sp)
                    Button(
                        onClick = {
                            if (newPostContent.isNotBlank()) {
                                viewModel.createCastPost(newPostContent)
                                newPostContent = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Post", fontSize = 11.sp)
                    }
                }
            }
        }

        // Timeline of Posts
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Cast Timeline Feed", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Trends: ${state.castTrendingHashtags.joinToString(", ")}",
                        color = NeonTeal,
                        fontSize = 9.sp
                    )
                }
            }

            if (state.castPosts.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15141C)), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Timeline is quiet today. Create a post to start a trending chain!", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                items(state.castPosts) { post ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(NeonPink),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = post.authorName.take(1), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(text = post.authorName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = post.authorHandle, color = Color.Gray, fontSize = 9.sp)
                                    }
                                }

                                if (post.isByPlayer) {
                                    IconButton(
                                        onClick = { viewModel.deleteCastPost(post.id) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = post.content, color = Color.White, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Interactive metrics panel
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { viewModel.likeCastPost(post.id) }
                                ) {
                                    Icon(
                                        imageVector = if (post.isLikedByPlayer) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Likes",
                                        tint = if (post.isLikedByPlayer) Color.Red else Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${post.likes}", color = Color.White, fontSize = 10.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Repeat, contentDescription = "Repost", tint = Color.White, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${post.reposts}", color = Color.White, fontSize = 10.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Comment, contentDescription = "Comments", tint = Color.White, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${post.commentsCount}", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== 3. METUBEs VIDEO SHARER SCREEN ===================
@Composable
fun MeTubeVideoScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var publishTitle by remember { mutableStateOf("") }
    var publishType by remember { mutableStateOf("VLOG") }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Stats header block
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF261014)),
            border = BorderStroke(1.dp, Color(0xFFFF0000)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "MeTube Channel Dashboard", color = Color(0xFFFF0000), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${String.format("%,d", state.meTubeSubscribers)} subscribers", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${String.format("%.1f", state.meTubeWatchTime)} total watch hours", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Claimable Ad Cash", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text(text = "$${String.format("%,.2f", state.meTubeRevenue)}", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { viewModel.collectMeTubeRevenue() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.height(24.dp).padding(top = 4.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text("CLAIM", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Upload Video controller card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Publish a New Video", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = publishTitle,
                    onValueChange = { publishTitle = it },
                    placeholder = { Text("Video Title...", color = Color.Gray, fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("VLOG", "MUSIC_VIDEO", "INTERVIEW").forEach { type ->
                            val isSel = publishType == type
                            Card(
                                colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0xFFFF0000) else Color(0xFF262335)),
                                modifier = Modifier.clickable { publishType = type }
                            ) {
                                Text(
                                    text = type.replace("_", " "),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (publishTitle.isNotBlank()) {
                                viewModel.uploadMeTubeVideo(publishTitle, publishType)
                                publishTitle = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Upload", fontSize = 10.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Cost: $150 & 15 Energy", color = Color.Yellow, fontSize = 9.sp)
            }
        }

        // Uploaded videos listing
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(text = "Published Videos", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            if (state.meTubeVideos.isEmpty()) {
                item {
                    Text(text = "No videos published. Tap draft controllers above!", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(state.meTubeVideos) { video ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Red)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = video.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                                Text(text = "Type: ${video.type}", color = Color.Gray, fontSize = 9.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "👁️ ${String.format("%,d", video.views)}", color = Color.White, fontSize = 10.sp)
                                    Text(text = "👍 ${String.format("%,d", video.likes)}", color = Color.White, fontSize = 10.sp)
                                    Text(text = "💰 $${String.format("%.2f", video.revenueEarned)}", color = Color(0xFF4CAF50), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== 4. WESHARE STOCK BROKERAGE SCREEN ===================
@Composable
fun WeShareExchangeScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedStockId by remember { mutableStateOf<String?>(null) }
    var inputPrice by remember { mutableStateOf("") }
    var inputQty by remember { mutableStateOf("") }
    var orderType by remember { mutableStateOf("LIMIT_BUY") }

    if (selectedStockId == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(text = "Available Corporate Stocks", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }

            items(state.stocks) { stock ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                    modifier = Modifier.fillMaxWidth().clickable {
                        selectedStockId = stock.id
                        inputPrice = String.format("%.2f", stock.currentPrice)
                        inputQty = "10"
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stock.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Dividend: ${(stock.weeklyDividendPercent * 100)}% | Owned: ${stock.quantity}", color = Color.Gray, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${String.format("%.2f", stock.currentPrice)}",
                                color = Color(0xFF4CAF50),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Tap to trade / Chart", color = NeonTeal, fontSize = 9.sp)
                        }
                    }
                }
            }

            item {
                Text(text = "Your Pending Limit Orders", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            }

            if (state.pendingStockOrders.isEmpty()) {
                item {
                    Text(text = "No pending limit orders.", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                }
            } else {
                items(state.pendingStockOrders) { order ->
                    val stock = state.stocks.find { it.id == order.stockId }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2A3A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "${order.type} - ${stock?.name ?: "Unknown"}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = "Qty: ${order.quantity} | Target Price: $${String.format("%.2f", order.targetPrice)}", color = Color.Gray, fontSize = 10.sp)
                            }
                            IconButton(onClick = { viewModel.cancelStockOrder(order.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Individual Stock Detail & Trade Order view
        val stockId = selectedStockId!!
        val stock = state.stocks.find { it.id == stockId } ?: return

        LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                IconButton(onClick = { selectedStockId = null }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonTeal)
                }
            }

            item {
                Text(text = stock.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Ticker sector: Entertainment | Div: ${(stock.weeklyDividendPercent * 100)}%", color = Color.Gray, fontSize = 11.sp)
            }

            // High fidelity simulated Candlestick/History Chart Widget
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val hist = stock.history.takeLast(10)
                            val displayList = if (hist.size < 5) listOf(10.0, 12.0, 11.0, 14.0, stock.currentPrice) else hist
                            val maxVal = displayList.maxOrNull() ?: 1.0
                            val minVal = displayList.minOrNull() ?: 0.1
                            val denom = (maxVal - minVal).coerceAtLeast(1.0)

                            displayList.forEach { price ->
                                val normHeight = (((price - minVal) / denom) * 80 + 10).dp
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height(normHeight)
                                        .background(if (price >= displayList.first()) Color(0xFF4CAF50) else Color.Red)
                                )
                            }
                        }
                        Text(text = "Simulated 10W Candlestick Chart", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp, modifier = Modifier.align(Alignment.TopStart))
                    }
                }
            }

            // Order Placement Panel
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Place Limit Order Setup", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("LIMIT_BUY", "LIMIT_SELL").forEach { type ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (orderType == type) NeonPurple else Color(0xFF2C2A3A)),
                                    modifier = Modifier.clickable { orderType = type }
                                ) {
                                    Text(
                                        text = type,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        TextField(
                            value = inputPrice,
                            onValueChange = { inputPrice = it },
                            label = { Text("Target Trigger Price ($)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = inputQty,
                            onValueChange = { inputQty = it },
                            label = { Text("Quantity (Shares)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val price = inputPrice.toDoubleOrNull() ?: 0.0
                                val qty = inputQty.toIntOrNull() ?: 0
                                if (price > 0.0 && qty > 0) {
                                    viewModel.placeStockOrder(stock.id, orderType, price, qty)
                                    selectedStockId = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SUBMIT ORDER SETUP", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =================== 5. GENESIS CRYPTO EXCHANGE SCREEN ===================
@Composable
fun GenesisExchangeScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedCryptoId by remember { mutableStateOf<String?>(null) }
    var inputPrice by remember { mutableStateOf("") }
    var inputQty by remember { mutableStateOf("") }
    var orderType by remember { mutableStateOf("LIMIT_BUY") }

    if (selectedCryptoId == null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Genesis Crypto Markets", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.15f))) {
                        Text(text = "Fear & Greed Index: 64 (Greed)", color = Color(0xFFFF9800), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }

            items(state.cryptos) { crypto ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                    modifier = Modifier.fillMaxWidth().clickable {
                        selectedCryptoId = crypto.id
                        inputPrice = String.format("%.4f", crypto.currentPrice)
                        inputQty = "1.0"
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = crypto.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Dev activity: Stable | Held: ${String.format("%.4f", crypto.quantity)}", color = Color.Gray, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$${String.format("%.4f", crypto.currentPrice)}",
                                color = Color(0xFFFF9800),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Limit, Stop, Take profit options", color = NeonTeal, fontSize = 8.sp)
                        }
                    }
                }
            }

            item {
                Text(text = "Pending Crypto Rules", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            }

            if (state.pendingCryptoOrders.isEmpty()) {
                item {
                    Text(text = "No pending crypto orders.", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(8.dp))
                }
            } else {
                items(state.pendingCryptoOrders) { order ->
                    val crypto = state.cryptos.find { it.id == order.cryptoId }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2A3A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "${order.type} - ${crypto?.name ?: "Unknown"}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(text = "Qty: ${String.format("%.4f", order.quantity)} | Trigger: $${String.format("%.4f", order.targetPrice)}", color = Color.Gray, fontSize = 10.sp)
                            }
                            IconButton(onClick = { viewModel.cancelCryptoOrder(order.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    } else {
        val cryptoId = selectedCryptoId!!
        val crypto = state.cryptos.find { it.id == cryptoId } ?: return

        LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                IconButton(onClick = { selectedCryptoId = null }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeonTeal)
                }
            }

            item {
                Text(text = crypto.name, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "High volatility cryptographic asset class", color = Color.Gray, fontSize = 11.sp)
            }

            // Cryptographic chart
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val hist = crypto.history.takeLast(10)
                            val displayList = if (hist.size < 5) listOf(0.12, 0.15, 0.11, 0.18, crypto.currentPrice) else hist
                            val maxVal = displayList.maxOrNull() ?: 1.0
                            val minVal = displayList.minOrNull() ?: 0.01
                            val denom = (maxVal - minVal).coerceAtLeast(0.01)

                            displayList.forEach { price ->
                                val normHeight = (((price - minVal) / denom) * 80 + 10).dp
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height(normHeight)
                                        .background(if (price >= displayList.first()) Color(0xFFFF9800) else Color.Red)
                                )
                            }
                        }
                        Text(text = "Simulated Crypto Trend Chart", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp, modifier = Modifier.align(Alignment.TopStart))
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Genesis Advanced Trader Setup", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("LIMIT_BUY", "LIMIT_SELL", "STOP_LOSS", "TAKE_PROFIT").forEach { type ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = if (orderType == type) Color(0xFFFF9800) else Color(0xFF2C2A3A)),
                                    modifier = Modifier.clickable { orderType = type }
                                ) {
                                    Text(
                                        text = type.replace("_", " "),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        TextField(
                            value = inputPrice,
                            onValueChange = { inputPrice = it },
                            label = { Text("Target Trigger Price ($)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = inputQty,
                            onValueChange = { inputQty = it },
                            label = { Text("Quantity (Coins)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val price = inputPrice.toDoubleOrNull() ?: 0.0
                                val qty = inputQty.toDoubleOrNull() ?: 0.0
                                if (price > 0.0 && qty > 0.0) {
                                    viewModel.placeCryptoOrder(crypto.id, orderType, price, qty)
                                    selectedCryptoId = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("EXECUTE TRADE STRATEGY", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =================== 6. RECRUITMENT HALL HIRING SCREEN ===================
@Composable
fun RecruitmentHallScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedCategory by remember { mutableStateOf("HIRED") }
    var expandedEmployeeId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        TabRow(
            selectedTabIndex = if (selectedCategory == "HIRED") 0 else 1,
            containerColor = Color(0xFF1E1C29),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Tab(selected = selectedCategory == "HIRED", onClick = { selectedCategory = "HIRED" }, text = { Text("Hired Staff (${state.hiredEmployees.size})", fontSize = 11.sp, color = Color.White) })
            Tab(selected = selectedCategory == "MARKET", onClick = { selectedCategory = "MARKET" }, text = { Text("Hiring Pool", fontSize = 11.sp, color = Color.White) })
        }

        if (selectedCategory == "HIRED") {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.hiredEmployees.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Your business is currently unstaffed. To automate tasks, protect your assets, and maximize studio ratings, hire professionals from the Hiring Pool tab!",
                                    color = Color.LightGray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    items(state.hiredEmployees) { emp ->
                        val isExpanded = expandedEmployeeId == emp.id
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151221)),
                            border = BorderStroke(1.dp, if (isExpanded) NeonPurple else Color(0xFF3B3654)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Title Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { expandedEmployeeId = if (isExpanded) null else emp.id }
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(text = emp.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = "Lvl ${emp.experience / 10}", color = NeonTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color(0xFF0F2C2C), shape = RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                        Text(text = emp.role, color = NeonTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(text = "$${String.format("%,.0f", emp.weeklySalary)}/wk", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { expandedEmployeeId = if (isExpanded) null else emp.id }) {
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Expand",
                                                tint = Color.LightGray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = { viewModel.fireProfessional(emp.id) }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Fire", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                // Quick Status Indicator
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "Loyalty: ${emp.loyalty.toInt()}%", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                    Text(text = "Happiness: ${emp.happiness.toInt()}%", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                    Text(text = "Weeks Left: ${emp.contractWeeks}", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                }

                                if (isExpanded) {
                                    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF3B3654)).padding(vertical = 8.dp))

                                    // Detailed Metrics Panel
                                    Text("PROFESSIONAL ATTRIBUTES", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        val stats = listOf(
                                            Triple("Loyalty & Retention", emp.loyalty / 100f, Color(0xFF2196F3)),
                                            Triple("Intelligence & Competence", emp.intelligence / 100f, Color(0xFF4CAF50)),
                                            Triple("Negotiation Skill", emp.negotiation / 100f, Color(0xFFFF9800)),
                                            Triple("Integrity & Honesty", emp.integrity / 100f, Color(0xFF9C27B0)),
                                            Triple("Efficiency & Speed", emp.efficiency / 100f, Color(0xFFE91E63)),
                                            Triple("Communication Flow", emp.communication / 100f, Color(0xFF00BCD4))
                                        )
                                        stats.forEach { (label, value, color) ->
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                                Text(text = label, color = Color.Gray, fontSize = 9.sp, modifier = Modifier.weight(1.5f))
                                                LinearProgressIndicator(
                                                    progress = { value },
                                                    color = color,
                                                    trackColor = Color(0xFF1A1A24),
                                                    modifier = Modifier.weight(2f).height(4.dp)
                                                )
                                                Text(text = "${(value * 100).toInt()}%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f), textAlign = TextAlign.End)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(text = "Age: ${emp.age} yrs", color = Color.LightGray, fontSize = 10.sp)
                                        Text(text = "Reputation: ${emp.reputation.toInt()}/100", color = Color.LightGray, fontSize = 10.sp)
                                        Text(text = "Trait: ${emp.traitDescription}", color = NeonTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Role Specific Permissions Panel
                                    if (emp.permissions.isNotEmpty()) {
                                        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF3B3654)).padding(vertical = 8.dp))
                                        Text("AUTOMATION PERMISSIONS", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            emp.permissions.forEach { (permKey, enabled) ->
                                                val displayName = when (permKey) {
                                                    "auto_tax" -> "Automatic Tax Reserve (Saves 15% profits)"
                                                    "auto_savings" -> "Automatic Savings Allocation (Saves 10% profits)"
                                                    "auto_stocks" -> "Strategic Stock Purchasing (Limit $2,000/wk)"
                                                    "auto_crypto" -> "Autonomous Crypto Trading & Alerts"
                                                    "auto_contracts" -> "Autonomous Brand Contract Filter"
                                                    "auto_social" -> "Autonomous Reputation Maintenance Campaigns"
                                                    else -> permKey
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(text = displayName, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, modifier = Modifier.weight(1f))
                                                    Checkbox(
                                                        checked = enabled,
                                                        onCheckedChange = { viewModel.toggleEmployeePermission(emp.id, permKey, it) },
                                                        colors = CheckboxDefaults.colors(checkedColor = NeonTeal, uncheckedColor = Color.Gray)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Action Buttons Panel
                                    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF3B3654)).padding(vertical = 8.dp))
                                    Text("PERFORMANCE MANAGEMENT", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.rewardProfessionalBonus(emp.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2442)),
                                            modifier = Modifier.weight(1f).height(28.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Bonus ($500)", fontSize = 9.sp, color = Color.White)
                                        }
                                        Button(
                                            onClick = { viewModel.trainEmployee(emp.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A2F)),
                                            modifier = Modifier.weight(1f).height(28.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Train ($1K)", fontSize = 9.sp, color = Color.White)
                                        }
                                        Button(
                                            onClick = { viewModel.promoteEmployee(emp.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D251D)),
                                            modifier = Modifier.weight(1f).height(28.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Promote", fontSize = 9.sp, color = Color.White)
                                        }
                                        Button(
                                            onClick = { viewModel.demoteEmployee(emp.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF421D1D)),
                                            modifier = Modifier.weight(1f).height(28.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Demote", fontSize = 9.sp, color = Color.White)
                                        }
                                    }

                                    // Career History Logs Section
                                    if (emp.careerHistory.isNotEmpty()) {
                                        Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF3B3654)).padding(vertical = 8.dp))
                                        Text("CAREER HISTORY & PERFORMANCE JOURNAL", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF100E19)),
                                            modifier = Modifier.fillMaxWidth().heightIn(max = 100.dp)
                                        ) {
                                            LazyColumn(
                                                modifier = Modifier.padding(6.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                items(emp.careerHistory.reversed()) { logEntry ->
                                                    Text(text = logEntry, color = Color.LightGray, fontSize = 9.sp, lineHeight = 12.sp)
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
        } else {
            // Marketplace Roster with ALL roles & multiple options per role!
            val roster = listOf(
                Triple("Accountant", "Franklin Sterns", 320.0),
                Triple("Accountant", "Amelia Sterling", 390.0),
                Triple("Accountant", "Arthur Pendelton", 280.0),
                
                Triple("Financial Advisor", "Marcus Brody", 360.0),
                Triple("Financial Advisor", "Devon Vance", 420.0),
                Triple("Financial Advisor", "Clara Oswald", 310.0),
                
                Triple("Crypto Analyst", "Vitalik Nakamoto", 280.0),
                Triple("Crypto Analyst", "Satoshi Finney", 350.0),
                Triple("Crypto Analyst", "Zooko Wilcox", 240.0),
                
                Triple("Talent Agent", "Ari Gold", 550.0),
                Triple("Talent Agent", "Scooter Braunster", 650.0),
                Triple("Talent Agent", "Harvey Specter", 500.0),
                
                Triple("PR Manager", "Damian Chase", 400.0),
                Triple("PR Manager", "Olivia Pope", 480.0),
                Triple("PR Manager", "Taylor Swiftwater", 350.0),
                
                Triple("Business Manager", "Gail Dunaway", 450.0),
                Triple("Business Manager", "Christian Grey", 520.0),
                Triple("Business Manager", "Sheryl Sandberg", 390.0),
                
                Triple("Lawyer", "Eileen Vance", 480.0),
                Triple("Lawyer", "Saul Goodman", 420.0),
                Triple("Lawyer", "Ally McBeal", 550.0),
                
                Triple("Tour Manager", "Logistics Bill", 380.0),
                Triple("Tour Manager", "Roadie Ronnie", 310.0),
                Triple("Tour Manager", "Aria Montgomery", 410.0),
                
                Triple("Music Producer", "Rick Rubin", 600.0),
                Triple("Music Producer", "Quincy Jonesy", 750.0),
                Triple("Music Producer", "Max Martinsson", 550.0),
                
                Triple("Personal Assistant", "Penny Pink", 180.0),
                Triple("Personal Assistant", "Alfred Pennyworth", 250.0),
                Triple("Personal Assistant", "Lorna Shore", 150.0),
                
                Triple("Security", "Marcus 'Brick' Stone", 250.0),
                Triple("Security", "John 'Herc' Shepard", 300.0),
                Triple("Security", "Sarah Connor", 220.0)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(roster) { (role, name, salary) ->
                    val alreadyHired = state.hiredEmployees.any { it.role == role }
                    val desc = when (role) {
                        "Accountant" -> "Prepares weekly statements, automates tax withholding, and invests savings."
                        "Financial Advisor" -> "Performs stock market research and provides high-growth advisory alerts."
                        "Crypto Analyst" -> "Monitors digital token fluctuations and executes auto-trades."
                        "Talent Agent" -> "Sources corporate endorsement deals, sponsor partnerships, and collaborations."
                        "PR Manager" -> "Defuses public scandals and performs image repair sweeps."
                        "Business Manager" -> "Cuts corporate cost structures and renegotiates employee payroll contracts."
                        "Lawyer" -> "Defends writing disputes and regulatory filing failures, saving thousands."
                        "Tour Manager" -> "Optimizes concert tour logistic planning and minimizes energy exhaustion."
                        "Music Producer" -> "Polishes melodies in-studio, boosting star-ratings of recorded songs."
                        "Personal Assistant" -> "Filters spam from mailbox and streamlines scheduling to restore energy."
                        "Security" -> "Formidable wall of protection mitigating stress factors."
                        else -> "Diligent professional staff."
                    }
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        border = BorderStroke(1.dp, if (alreadyHired) Color.Gray else NeonTeal)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = role, color = NeonTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.recruitProfessional(role, name, salary) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                    enabled = !alreadyHired,
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(text = if (alreadyHired) "Hired" else "Hire", fontSize = 10.sp, color = Color.White)
                                }
                            }
                            Text(text = desc, color = Color.LightGray, fontSize = 10.sp, lineHeight = 13.sp)
                            Text(text = "Suggested salary: $${String.format("%,.0f", salary)}/wk", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =================== 7. SPIRITUAL SANCTUARY SCREEN ===================
@Composable
fun SpiritualSanctuaryScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var titheInputText by remember { mutableStateOf("") }
    var charityInputText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF211E10)),
                border = BorderStroke(1.dp, Color(0xFFFFEB3B))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Soul Sanctuary & Spiritual Life", color = Color(0xFFFFEB3B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Stewardship is not about buffs or optimization. It represents the alignment of character, generosity, and longevity.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Action 1: Silent Prayer
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Silent Contemplation & Prayer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Spends 10 Energy. Relieves 12 Stress and cultivates internal discipline & humility.", color = Color.Gray, fontSize = 10.sp)
                    Button(
                        onClick = { viewModel.pray() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text("SPEND TIME IN PRAYER", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Action 2: Tithing
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Systemic Tithing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Give back from your earnings. Cultivates massive trustworthiness and integrity stats.", color = Color.Gray, fontSize = 10.sp)
                    
                    TextField(
                        value = titheInputText,
                        onValueChange = { titheInputText = it },
                        placeholder = { Text("Tithe amount ($)...", color = Color.Gray, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total paid: $${String.format("%,.0f", state.spiritualTotalTithesPaid)}",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )

                        Button(
                            onClick = {
                                val amt = titheInputText.toDoubleOrNull() ?: 0.0
                                if (amt > 0.0) {
                                    viewModel.titheCash(amt)
                                    titheInputText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("TITHE", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Tithing reminders setting toggles
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Enable tithe warning notification", color = Color.White, fontSize = 10.sp)
                        Switch(
                            checked = state.spiritualTitheSetting,
                            onCheckedChange = { viewModel.toggleTitheReminder(it) },
                            modifier = Modifier.scale(0.7f)
                        )
                    }
                }
            }
        }

        // Action 3: Charitable Donation
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Acts of Charity & Service", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Donate to community development. Directly elevates public image and generosity levels.", color = Color.Gray, fontSize = 10.sp)

                    TextField(
                        value = charityInputText,
                        onValueChange = { charityInputText = it },
                        placeholder = { Text("Donation amount ($)...", color = Color.Gray, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    )

                    Button(
                        onClick = {
                            val amt = charityInputText.toDoubleOrNull() ?: 0.0
                            if (amt > 0.0) {
                                viewModel.donateToCharity(amt)
                                charityInputText = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text("DONATE TO CHARITY & SERVICE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// =================== 8. PROFESSIONAL EMAIL SCREEN ===================
@Composable
fun ProfessionalEmailScreen(state: GameStateEntity, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(text = "Inbox (Official Matters)", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }

        val letters = state.mailbox
        if (letters.isEmpty()) {
            item {
                Text(text = "Corporate Inbox empty.", color = Color.Gray, modifier = Modifier.padding(16.dp), fontSize = 12.sp)
            }
        } else {
            items(letters) { mail ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                    border = BorderStroke(1.dp, if (mail.isAccepted) Color.Gray else NeonTeal)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = mail.sender, fontWeight = FontWeight.Bold, color = NeonTeal, fontSize = 12.sp)
                            Text(text = "Expires: Y${mail.expiresYear} W${mail.expiresWeek}", color = Color.Gray, fontSize = 9.sp)
                        }
                        Text(text = mail.subject, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text(text = mail.body, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)

                        if (!mail.isAccepted && mail.type != "NORMAL") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.acceptMail(mail.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text("Accept Offer", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { viewModel.deleteMail(mail.id) },
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text("Decline", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== 9. WALLPAPER SETTINGS SCREEN ===================
@Composable
fun WallpaperSettingsScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val options = listOf(
        Triple("slate", "Slate Theme Blue", Brush.verticalGradient(listOf(Color(0xFF1E1E2F), Color(0xFF13111C)))),
        Triple("sunset", "Cosmic Sunset Orange", Brush.verticalGradient(listOf(Color(0xFFF07B50), Color(0xFF901C40), Color(0xFF1D0E25)))),
        Triple("cyber", "Cyberpunk Neon Purple", Brush.verticalGradient(listOf(Color(0xFF00F0FF), Color(0xFF7000FF), Color(0xFF1B0035)))),
        Triple("nebula", "Aura Deep Space Nebula", Brush.verticalGradient(listOf(Color(0xFF120C3F), Color(0xFF4A007A), Color(0xFF0B0720))))
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Ecosystem Wallpaper Settings", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)

        options.forEach { (id, name, brush) ->
            val isCurrent = state.wallpaperId == id
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                border = BorderStroke(2.dp, if (isCurrent) NeonTeal else Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.updateWallpaper(id) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(brush)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

// =================== BANKING APP SCREEN ===================
@Composable
fun ApexBankingScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var activeTab by remember { mutableStateOf("savings") } // "savings", "loan", "tax"
    var inputAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Financial Overview card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A30)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Working Cash", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Bank Balance", tint = NeonTeal, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = "$${String.format("%,.2f", state.cash)}",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Savings Account", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        Text(
                            "$${String.format("%,.2f", state.savingsBalance)}",
                            color = NeonTeal,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Active Debt Balance", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        Text(
                            "$${String.format("%,.2f", state.loanAmount)}",
                            color = NeonPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (state.taxDebt > 0.0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Red.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Unpaid Tax Bill:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "$${String.format("%,.2f", state.taxDebt)}",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("savings" to "Savings", "loan" to "Loans & Credit", "tax" to "Tax Settle").forEach { (tabId, label) ->
                val isSelected = activeTab == tabId
                Button(
                    onClick = {
                        activeTab = tabId
                        inputAmount = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF673AB7) else Color(0xFF1E1C29),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Amount Input Field
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1824)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter Transaction Amount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = inputAmount,
                    onValueChange = { inputAmount = it },
                    label = { Text("Amount ($)", color = Color.White.copy(alpha = 0.5f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Suggestion Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val suggestions = when (activeTab) {
                        "savings" -> listOf(1000.0, 5000.0, 25000.0)
                        "loan" -> listOf(10000.0, 50000.0, 200000.0)
                        else -> listOf(5000.0, 20000.0, state.taxDebt)
                    }

                    suggestions.filter { it > 0.0 }.forEach { value ->
                        val text = if (value == state.taxDebt) "Full Due" else "$${String.format("%,.0f", value)}"
                        AssistChip(
                            onClick = { inputAmount = String.format("%.2f", value) },
                            label = { Text(text, color = Color.White, fontSize = 10.sp) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF2B283E))
                        )
                    }
                }
            }
        }

        // Dynamic Tab Panels
        when (activeTab) {
            "savings" -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Apex High-Yield Savings Account", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Earn a premium weekly compounding interest rate of 1.20% (62.4% APY equivalent) on all active savings deposits. Perfect for protecting your wealth from inflation hazards and life emergency events.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val amt = inputAmount.toDoubleOrNull()
                                    if (amt != null && amt > 0.0) {
                                        viewModel.depositToSavings(amt)
                                        inputAmount = ""
                                    } else {
                                        viewModel.showToast("Please enter a valid positive deposit amount!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Deposit Cash", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val amt = inputAmount.toDoubleOrNull()
                                    if (amt != null && amt > 0.0) {
                                        viewModel.withdrawFromSavings(amt)
                                        inputAmount = ""
                                    } else {
                                        viewModel.showToast("Please enter a valid positive withdrawal amount!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Withdraw", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "loan" -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Flexible Creative Credit Lines", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Secure immediate liquid funding up to 2.5x your net worth to finance luxury cars, houses, recording setups or world marketing tours. Charges a standard interest rate of 2.00% compounding weekly.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )

                        val assetValue = state.ownedCars.sumOf { it.cost } + state.ownedHouses.sumOf { it.cost } + state.ownedInstruments.sumOf { it.cost }
                        val capacity = ((state.cash + assetValue) * 2.5 + 50000.0).coerceIn(50000.0, 5000000.0)
                        Text(
                            "Your Personal Borrowing Cap: $${String.format("%,.2f", capacity)}",
                            color = NeonTeal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val amt = inputAmount.toDoubleOrNull()
                                    if (amt != null && amt > 0.0) {
                                        viewModel.applyForLoan(amt)
                                        inputAmount = ""
                                    } else {
                                        viewModel.showToast("Please enter a valid positive borrow amount!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Borrow Loan", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val amt = inputAmount.toDoubleOrNull()
                                    if (amt != null && amt > 0.0) {
                                        viewModel.repayLoan(amt)
                                        inputAmount = ""
                                    } else {
                                        viewModel.showToast("Please enter a valid positive repayment amount!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                modifier = Modifier.weight(1f).height(48.dp)
                            ) {
                                Text("Repay Debt", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            "tax" -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Government Tax Settlement Panel", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Progressive wealth and asset value taxes are assessed annually. Delinquent tax bills trigger a punishing 3% surcharge penalty compounded every single week. Settle your accounts to remain fully compliant with the IRS.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val amt = inputAmount.toDoubleOrNull()
                                if (amt != null && amt > 0.0) {
                                    viewModel.payTaxDebt(amt)
                                    inputAmount = ""
                                } else {
                                    viewModel.showToast("Please enter a valid positive payment amount!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Settle Outstanding Taxes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =================== SOUNDSTREAM MUSIC APP SCREEN ===================
@Composable
fun SoundStreamMusicScreen(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // High-level catalog streaming indicators
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131F2E)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Platform Streams", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    val aggregateStreams = state.songs.sumOf { it.totalStreams }
                    Text(
                        "${String.format("%,d", aggregateStreams)} plays",
                        color = NeonTeal,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Weekly Listener Appeal", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Text(
                        "${String.format("%.1f", state.popularity)}%",
                        color = NeonPink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "Your Recorded Songs & Uploads",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
        )

        if (state.songs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No songs recorded in your catalog yet. Head to the studio to create and record tracks first!",
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.songs) { song ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    val genreText = GameData.GENRES.find { it.id == song.genreId }?.name ?: song.genreId
                                    Text("Genre: $genreText | Quality: ${String.format("%.1f", song.rating)} ★", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Streams", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
                                    Text("${String.format("%,d", song.totalStreams)}", color = NeonTeal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                            Text("Streaming Platforms Deployment:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                            val platforms = listOf(
                                Triple("tunes", "iTunes OS", 0.0),
                                Triple("sharp", "Sharp Music", 500.0),
                                Triple("jams", "JamSphere", 700.0),
                                Triple("megaphone", "MegaPhone", 1000.0),
                                Triple("songify", "Songify Plus", 1500.0)
                            )

                            platforms.forEach { (pId, pName, cost) ->
                                val streamsOnPlatform = song.isUploadedMap[pId]
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(pName, color = Color.White, fontSize = 12.sp)

                                    if (streamsOnPlatform != null) {
                                        // Already uploaded
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF2E7D32).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                "${String.format("%,d", streamsOnPlatform)} plays",
                                                color = Color(0xFF4CAF50),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        // Upload button
                                        Button(
                                            onClick = { viewModel.uploadSong(song.id, pId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            val costText = if (cost == 0.0) "Free" else "$${String.format("%.0f", cost)}"
                                            Text("Upload ($costText)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

// =================== 11. PLANNER APP SCREEN (CALENDAR, WEATHER, MAPS) ===================
@Composable
fun PlannerAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Calendar, 1 = Weather, 2 = Travel Map

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0C0B0E))) {
        // App Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF15141B),
            contentColor = NeonPink
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Planner", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) NeonPink else Color.Gray)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Weather", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) NeonPink else Color.Gray)
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("Maps", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 2) NeonPink else Color.Gray)
            }
        }

        Box(modifier = Modifier.weight(1f).padding(12.dp)) {
            when (selectedTab) {
                0 -> {
                    // Planner Screen
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Text("Weekly Strategy Planner", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Schedule up to 3 activities for the next week. Bonuses are credited at week progression.", color = Color.Gray, fontSize = 10.sp)
                        }

                        // Scheduled List
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Upcoming Schedule", color = NeonTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    if (state.scheduledActivities.isEmpty()) {
                                        Text("No activities scheduled. Tap options below to queue your strategy.", color = Color.Gray, fontSize = 11.sp)
                                    } else {
                                        state.scheduledActivities.forEach { act ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Box(modifier = Modifier.size(6.dp).background(NeonPink, CircleShape))
                                                    Text(act, color = Color.White, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.clearScheduledActivities() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.4f)),
                                            modifier = Modifier.fillMaxWidth().height(28.dp)
                                        ) {
                                            Text("Clear Schedule", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Add options
                        val activities = listOf(
                            "Rest & Meditation" to "Restores +25 Energy, Relieves 15 Stress",
                            "Vocal Practice" to "+1.2 Vocal Skill boost",
                            "Creative Writing" to "+1.2 Songwriting Skill boost",
                            "Social PR Campaign" to "+4% Public Image & Inspiration",
                            "Spiritual Retreat" to "+3 Generosity, +4 Humility, -20 Stress"
                        )

                        items(activities) { (actName, actDesc) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF15141B)),
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.scheduleActivity(actName) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(actName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(actDesc, color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = "Schedule", tint = NeonTeal, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Weather Screen
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Dynamic Weather Forecast", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                val (icon, color, desc) = when (state.weatherCondition) {
                                    "Sunny" -> Triple(Icons.Default.WbSunny, Color(0xFFFFEB3B), "Clear skies. Optimal concert turnout and normal street activities.")
                                    "Rainy" -> Triple(Icons.Default.Cloud, Color(0xFF90CAF9), "Light showers. Outdoor gig turnouts slightly reduced (-15%).")
                                    "Overcast" -> Triple(Icons.Default.CloudQueue, Color(0xFFB0BEC5), "Gray overcast. Standard travel rates and normal streaming activity.")
                                    "Heavy Storm" -> Triple(Icons.Default.FlashOn, Color(0xFFFF5722), "⚡ Violent thunderstorms! High road delays, outdoor gig turnout dropped by 45%, but indoor digital streaming viewtimes spiked (+25%!)")
                                    "Heatwave" -> Triple(Icons.Default.WbSunny, Color(0xFFFF9800), "Extreme high temperatures. Double fatigue rate during active physical tasks.")
                                    else -> Triple(Icons.Default.WbSunny, Color(0xFFFFEB3B), "Perfect spring day.")
                                }

                                Icon(imageVector = icon, contentDescription = "Weather", tint = color, modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(state.weatherCondition, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = desc,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15141B))) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Environmental Impact Log", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Rainy weather boosts passive MeTube watchtime by +10%", color = Color.Gray, fontSize = 10.sp)
                                Text("• Heavy Storms cause structural delays but trigger a +25% MeTube streaming views multiplier", color = Color.Gray, fontSize = 10.sp)
                                Text("• Heatwaves reduce vocal practice efficiency by 15%", color = Color.Gray, fontSize = 10.sp)
                            }
                        }
                    }
                }
                2 -> {
                    // Travel Map Screen
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Text("Ecosystem Regional Travel Map", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Current fuel index:", color = Color.Gray, fontSize = 10.sp)
                                Text("$${state.fuelPrice}/L", color = NeonTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        val cities = listOf(
                            Triple("Lagos, Nigeria", "15M Potential Fans • Afrobeats Capital", 180.0),
                            Triple("London, United Kingdom", "9M Potential Fans • Indie Synth Hub", 450.0),
                            Triple("New York City, USA", "8.5M Potential Fans • HipHop & Pop Center", 620.0),
                            Triple("Tokyo, Japan", "14M Potential Fans • Neon J-Pop Paradise", 880.0)
                        )

                        items(cities) { (name, desc, baseCost) ->
                            val travelCost = baseCost * (state.fuelPrice / 1.25)
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15141B))) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(desc, color = Color.Gray, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Estimated flight setup: $${String.format("%,.1f", travelCost)} (Adjusted dynamically for fuel price)",
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 9.sp
                                        )
                                    }
                                    Icon(imageVector = Icons.Default.Navigation, contentDescription = "Travel", tint = NeonPink, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== 12. WEB HUB APP SCREEN (BROWSER, SHOP, AUCTION) ===================
@Composable
fun WebHubAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Browser, 1 = Equipment Shop, 2 = Auction House

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0C0B0E))) {
        // App Tab Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF15141B),
            contentColor = NeonTeal
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Search", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) NeonTeal else Color.Gray)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Equip Shop", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) NeonTeal else Color.Gray)
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("Auctions", modifier = Modifier.padding(12.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 2) NeonTeal else Color.Gray)
            }
        }

        Box(modifier = Modifier.weight(1f).padding(12.dp)) {
            when (selectedTab) {
                0 -> {
                    // Browser Web view
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Global Market Analysis & Intelligence", color = NeonTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Economy Cycle:", color = Color.Gray, fontSize = 10.sp)
                                        Text(state.economyCycle, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Current Inflation:", color = Color.Gray, fontSize = 10.sp)
                                        Text("${String.format("%.1f", state.inflationRate * 100)}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Fuel Price index:", color = Color.Gray, fontSize = 10.sp)
                                        Text("$${state.fuelPrice}/L", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Election Hype Active:", color = Color.Gray, fontSize = 10.sp)
                                        Text(if (state.isElectionSeason) "YES" else "NO", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Text("NPC Artist Register Directory", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        items(state.npcs) { npc ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15141B))) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(npc.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Box(modifier = Modifier.background(NeonPink.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                            Text(npc.genrePreference, color = NeonPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Ecosystem Fans: ${String.format("%,d", npc.fans)}", color = Color.Gray, fontSize = 10.sp)
                                        Text("Rep: ${String.format("%.0f", npc.influence)}/100", color = Color.Gray, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Equipment Shop
                    val items = listOf(
                        Triple("Premium Studio Microphone", "Reduces skill decay velocity during heavy side-job strain.", 12000.0),
                        Triple("Vintage Polyphonic Synthesizer", "Triggers high creative inspiration, boosting writing efficiency.", 25000.0),
                        Triple("Pro Acoustical Foam Panels", "Improves overall studio recording fidelity, boosting stream plays.", 8500.0)
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(items) { (name, desc, cost) ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15141B))) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(desc, color = Color.Gray, fontSize = 10.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("$${String.format("%,.0f", cost)}", color = NeonTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Button(
                                            onClick = {
                                                if (state.cash >= cost) {
                                                    viewModel.showToast("Purchased $name!")
                                                    viewModel.updateState { s -> s.copy(cash = s.cash - cost) }
                                                } else {
                                                    viewModel.showToast("Insufficient cash reserves!")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("BUY NOW", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Auction House
                    var auctionBid by remember { mutableStateOf(22000.0) }
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29))) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier.background(NeonPink.copy(alpha = 0.2f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ACTIVE AUCTION", color = NeonPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("Historic Copyright: 'Sunset Grooves (1994)'", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Acquiring this master license guarantees a flat $450/week passive royalty payout in perpetuity.", color = Color.Gray, fontSize = 10.sp)
                                Divider(color = Color.Gray.copy(alpha = 0.2f))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Current Highest Bid:", color = Color.Gray, fontSize = 11.sp)
                                    Text("$${String.format("%,.0f", auctionBid)}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Current Bidder:", color = Color.Gray, fontSize = 11.sp)
                                    Text("Burna Boy (NPC)", color = NeonTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (state.cash > auctionBid + 2500) {
                                    auctionBid += 2500.0
                                    viewModel.showToast("Placed Bid: $${String.format("%,.0f", auctionBid)}")
                                    // 35% chance NPC counters instantly
                                    if (Random.nextFloat() < 0.35f) {
                                        auctionBid += 3000.0
                                        viewModel.showToast("Counter Bid: Wizkid bid $${String.format("%,.0f", auctionBid)}")
                                    }
                                } else {
                                    viewModel.showToast("Insufficient cash to place a bidding increment!")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Text("PLACE INCREMENT BID (+$2,500)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// =================== 13. CAMERA & PHOTO GALLERY SCREEN ===================
@Composable
fun CameraAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var chosenLens by remember { mutableStateOf("Fisheye") }
    val lensOptions = listOf("Fisheye", "Cinematic 35mm", "Retro Polaroid")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0C0B0E)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Camera & Promo Capturing", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, Color.Gray),
            modifier = Modifier.fillMaxWidth().height(160.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Lens View", tint = NeonTeal, modifier = Modifier.size(48.dp))
                    Text("Promo Live Viewfinder", color = Color.White, fontSize = 11.sp)
                    Text("Lens Active: $chosenLens", color = Color.Gray, fontSize = 9.sp)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            lensOptions.forEach { lens ->
                Button(
                    onClick = { chosenLens = lens },
                    colors = ButtonDefaults.buttonColors(containerColor = if (chosenLens == lens) NeonTeal else Color(0xFF1E1C29)),
                    modifier = Modifier.weight(1f).height(32.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(lens, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (chosenLens == lens) Color.Black else Color.White)
                }
            }
        }

        Button(
            onClick = {
                val week = state.currentWeek
                val year = state.currentYear
                val captions = listOf(
                    "Live in Concert Year $year Week $week: Epic $chosenLens photo capturing our roaring stadium crowd!",
                    "Behind the Scenes: Deep studio mixing session with high-end instruments capturing our signature frequency.",
                    "Studio Promo: Visually stunning aesthetic photoshoot with $chosenLens lens capturing raw emotion."
                )
                viewModel.takeConcertPhoto(captions.random())
                viewModel.showToast("Captured new memory photo!")
            },
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
            modifier = Modifier.fillMaxWidth().height(36.dp)
        ) {
            Text("SNAP PROMO MEMORY PHOTO", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
        }

        Text("Saved Memories Roll (${state.savedPhotos.size})", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            if (state.savedPhotos.isEmpty()) {
                item {
                    Text("No photos saved yet. Snap a viewfinder memory above!", color = Color.Gray, fontSize = 11.sp)
                }
            } else {
                items(state.savedPhotos) { photo ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF15141B))) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(photo, color = Color.White, fontSize = 10.sp)
                            Button(
                                onClick = {
                                    viewModel.createCastPost(photo)
                                    viewModel.updateState { s -> s.copy(publicImage = (s.publicImage + 3f).coerceAtMost(100f)) }
                                    viewModel.showToast("Posted to Cast! Public image +3%!")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                modifier = Modifier.fillMaxWidth().height(26.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("POST MEMORY TO CAST SOCIAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =================== 14. NOTES PAD SCREEN ===================
@Composable
fun NotesAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var noteInputText by remember { mutableStateOf(state.activeNoteText) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A10)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Songwriting Pad & Notebook", color = Color(0xFFFFEB3B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = {
                    viewModel.saveActiveNoteText(noteInputText)
                    viewModel.showToast("Saved notes to persistent ledger.")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEB3B)),
                modifier = Modifier.height(28.dp)
            ) {
                Text("SAVE", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFEF0)),
            border = BorderStroke(1.dp, Color(0xFFFFEB3B)),
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) {
            TextField(
                value = noteInputText,
                onValueChange = { noteInputText = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("Jot down song ideas, lyrics, budget goals, or tax strategies here...", color = Color.Gray, fontSize = 12.sp) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
