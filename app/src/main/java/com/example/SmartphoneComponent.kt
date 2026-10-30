package com.example

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.*

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.ReliableAsyncImage as AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.data.NPCEntity
import com.example.data.ChatMessage
import com.example.data.ChatConversation
import com.example.data.MailMessage
import com.example.ui.theme.*

import androidx.compose.ui.text.font.FontFamily
import com.example.data.Employee
import com.example.data.GameData
import com.example.data.WorldHistoryEntry
import com.example.ui.ArtistFaceAvatar
import com.google.accompanist.permissions.isGranted
import com.example.ui.planner.PlannerAppScreen
import com.example.ui.music.TunesScreen

import com.example.ui.social.CastSocialScreen
import com.example.ui.music.SongifyScreen
import com.example.ui.music.MegaphoneScreen
import com.example.ui.music.JamsScreen
import com.example.ui.music.SharpScreen
import com.example.ui.music.TunesScreen
import com.example.ui.browser.WebHubAppScreen
import java.util.UUID
import java.io.File
import android.content.ContentValues
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest

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
            // External Device Identification Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00E676)))
                    val phoneLabel = when (state.activePhone) {
                        "TECCNO" -> "TECCNO Spark • HiOS GO"
                        "Xiaomax" -> "Xiaomax HyperOS • Ecosystem 5G Ultra"
                        "No-Thing" -> "No-Thing GLYPH // OS"
                        "Pixxelate" -> "Pixxelate Pro • Pixel UI"
                        "Samsong" -> "Samsong Galaxy • OneOS 7.0 Ultra"
                        "hiPhone" -> "hiPhone 16 Pro • HiOS 18"
                        else -> "Smartphone"
                    }
                    Text(phoneLabel, color = Color.White.copy(alpha=0.9f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            val phoneOuterShape = when (state.activePhone) {
                "TECCNO" -> RoundedCornerShape(24.dp)
                "Xiaomax" -> RoundedCornerShape(32.dp)
                "No-Thing" -> RoundedCornerShape(16.dp)
                "Pixxelate" -> RoundedCornerShape(42.dp)
                "Samsong" -> RoundedCornerShape(20.dp)
                "hiPhone" -> RoundedCornerShape(42.dp)
                else -> RoundedCornerShape(38.dp)
            }

            val phoneBezelThickness = when (state.activePhone) {
                "TECCNO" -> 14.dp
                "Xiaomax" -> 8.dp
                "No-Thing" -> 6.dp
                "Pixxelate" -> 10.dp
                "Samsong" -> 4.dp
                "hiPhone" -> 6.dp
                else -> 6.dp
            }

            val phoneFrameColor = when (state.activePhone) {
                "TECCNO" -> Color(0xFF2B2B2B)
                "Xiaomax" -> Color(0xFF1E212A)
                "No-Thing" -> Color(0xFFEEEEEE)
                "Pixxelate" -> Color(0xFF1A1A1A)
                "Samsong" -> Color(0xFF555555)
                "hiPhone" -> Color(0xFF343B46)
                else -> Color(0xFF343B46)
            }

            val phoneBorderThickness = when (state.activePhone) {
                "No-Thing" -> 2.5.dp
                "Samsong" -> 1.dp
                else -> 1.5.dp
            }

            val preferredPhoneWidth = when (state.activePhone) {
                "TECCNO" -> 395.dp
                "Xiaomax" -> 405.dp
                "No-Thing" -> 415.dp
                "Pixxelate" -> 410.dp
                "Samsong" -> 420.dp
                "hiPhone" -> 410.dp
                else -> 410.dp
            }

            val preferredPhoneHeight = when (state.activePhone) {
                "TECCNO" -> 780.dp
                "Xiaomax" -> 800.dp
                "No-Thing" -> 810.dp
                "Pixxelate" -> 805.dp
                "Samsong" -> 830.dp
                "hiPhone" -> 820.dp
                else -> 820.dp
            }
            val screen = LocalConfiguration.current
            val phoneWidth = minOf(preferredPhoneWidth, (screen.screenWidthDp - 24).coerceAtLeast(280).dp)
            val phoneHeight = minOf(preferredPhoneHeight, (screen.screenHeightDp - 72).coerceAtLeast(560).dp)

            val phoneInnerClip = when (state.activePhone) {
                "TECCNO" -> RoundedCornerShape(12.dp)
                "Xiaomax" -> RoundedCornerShape(24.dp)
                "No-Thing" -> RoundedCornerShape(12.dp)
                "Pixxelate" -> RoundedCornerShape(34.dp)
                "Samsong" -> RoundedCornerShape(16.dp)
                "hiPhone" -> RoundedCornerShape(36.dp)
                else -> RoundedCornerShape(32.dp)
            }

            val phoneContainerColor = when (state.activePhone) {
                "TECCNO" -> Color(0xFF111111)
                "No-Thing" -> Color(0xFF222222)
                else -> Color(0xFF1A1D24)
            }

            val phoneBgImage = when (state.activePhone) {
                "Samsong" -> R.drawable.img_unnamed_14
                "TECCNO" -> R.drawable.img_unnamed_15
                "Xiaomax" -> R.drawable.img_unnamed_16
                "No-Thing" -> R.drawable.img_unnamed_17
                "Pixxelate" -> R.drawable.img_unnamed_18
                "hiPhone" -> R.drawable.img_unnamed_19
                else -> R.drawable.img_unnamed_14
            }

            // Simulated Premium Flagship Smartphone Frame
            Box(
                modifier = Modifier
                    .width(phoneWidth)
                    .height(phoneHeight)
                    .clip(phoneOuterShape)
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { /* Absorb clicks */ }
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = phoneBgImage),
                    contentDescription = "Phone Hardware",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(phoneBezelThickness) // Slimmer bezels
                        .clip(phoneInnerClip)
                ) {
                    // Wallpaper Gradient Layer
                    val selectedImg = imageWallpapers.find { it.first == state.wallpaperId }
                    if (selectedImg != null) {
                        AsyncImage(
                            model = selectedImg.third,
                            contentDescription = "Wallpaper",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val wallpaperBrush = when (state.wallpaperId) {
                            "Cosmic Nebula" -> Brush.verticalGradient(listOf(Color(0xFF120C3F), Color(0xFF4A007A), Color(0xFF0B0720)))
                            "Cyber Sunset" -> Brush.verticalGradient(listOf(Color(0xFFF07B50), Color(0xFF901C40), Color(0xFF1D0E25)))
                            "Midnight Forest" -> Brush.verticalGradient(listOf(Color(0xFF0A2E12), Color(0xFF031B08), Color(0xFF000C02)))
                            "Golden Hour" -> Brush.verticalGradient(listOf(Color(0xFFF9D423), Color(0xFFFF4E50), Color(0xFF2C1014)))
                            else -> when (state.activePhone) {
                                "hiPhone" -> Brush.verticalGradient(listOf(Color(0xFF281C22), Color(0xFF14131D)))
                                "Samsong" -> Brush.verticalGradient(listOf(Color(0xFF101B2E), Color(0xFF241528)))
                                "TECCNO" -> Brush.verticalGradient(listOf(Color(0xFF12222E), Color(0xFF380720)))
                                "No-Thing" -> Brush.verticalGradient(listOf(Color(0xFF0A0A0A), Color(0xFF030303)))
                                "Xiaomax" -> Brush.verticalGradient(listOf(Color(0xFF361811), Color(0xFF111E30)))
                                "Pixxelate" -> Brush.verticalGradient(listOf(Color(0xFF2E1914), Color(0xFF141525)))
                                else -> Brush.verticalGradient(listOf(Color(0xFF0F1115), Color(0xFF1A1D24)))
                            }
                        }
                        Box(modifier = Modifier.fillMaxSize().background(wallpaperBrush)) {
                            // High-definition atmospheric light reflections (blurred vector orbs)
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                when (state.activePhone) {
                                    "hiPhone" -> {
                                        drawCircle(color = Color(0x25FF7B54), radius = w * 0.75f, center = androidx.compose.ui.geometry.Offset(w * 0.1f, h * 0.2f))
                                        drawCircle(color = Color(0x259C27B0), radius = w * 0.6f, center = androidx.compose.ui.geometry.Offset(w * 0.9f, h * 0.8f))
                                    }
                                    "Samsong" -> {
                                        drawCircle(color = Color(0x2500BCD4), radius = w * 0.75f, center = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.3f))
                                        drawCircle(color = Color(0x25E91E63), radius = w * 0.6f, center = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.7f))
                                    }
                                    "TECCNO" -> {
                                        drawCircle(color = Color(0x2500ACC1), radius = w * 0.65f, center = androidx.compose.ui.geometry.Offset(w * 0.3f, h * 0.2f))
                                        drawCircle(color = Color(0x25D81B60), radius = w * 0.8f, center = androidx.compose.ui.geometry.Offset(w * 0.7f, h * 0.8f))
                                    }
                                    "No-Thing" -> {
                                        val dotRadius = 2.5f
                                        val spacing = 50f
                                        for (x in 0..(w / spacing).toInt()) {
                                            for (y in 0..(h / spacing).toInt()) {
                                                drawCircle(color = Color.White.copy(alpha = 0.08f), radius = dotRadius, center = androidx.compose.ui.geometry.Offset(x * spacing + 25f, y * spacing + 25f))
                                            }
                                        }
                                    }
                                    "Xiaomax" -> {
                                        drawCircle(color = Color(0x25FF5722), radius = w * 0.75f, center = androidx.compose.ui.geometry.Offset(w * 0.1f, h * 0.25f))
                                        drawCircle(color = Color(0x252196F3), radius = w * 0.55f, center = androidx.compose.ui.geometry.Offset(w * 0.9f, h * 0.75f))
                                    }
                                    "Pixxelate" -> {
                                        drawCircle(color = Color(0x25FF9800), radius = w * 0.75f, center = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.3f))
                                        drawCircle(color = Color(0x253F51B5), radius = w * 0.6f, center = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.65f))
                                    }
                                    else -> {
                                        drawCircle(color = Color(0x1B3F51B5), radius = w * 0.6f, center = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.35f))
                                    }
                                }
                            }
                        }
                    }

                                        // Phone OS Status Bar
                    Box(modifier = Modifier.fillMaxWidth().zIndex(10f)) {
                        // TECCNO Teardrop Notch
                        if (state.activePhone == "TECCNO") {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(28.dp)) {
                                val w = size.width
                                val notchWidth = 80.dp.toPx()
                                val notchHeight = 28.dp.toPx()
                                val path = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(w / 2f - notchWidth / 2f, 0f)
                                    quadraticBezierTo(w / 2f - notchWidth / 4f, 0f, w / 2f - notchWidth / 6f, notchHeight * 0.6f)
                                    quadraticBezierTo(w / 2f, notchHeight, w / 2f + notchWidth / 6f, notchHeight * 0.6f)
                                    quadraticBezierTo(w / 2f + notchWidth / 4f, 0f, w / 2f + notchWidth / 2f, 0f)
                                    close()
                                }
                                drawPath(path, Color(0xFF111111))
                                drawCircle(Color(0xFF222222), radius = 6.dp.toPx(), center = androidx.compose.ui.geometry.Offset(w / 2f, notchHeight * 0.5f))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = if (state.activePhone == "TECCNO") 8.dp else 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val timeString = remember {
                                val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
                                sdf.format(java.util.Date())
                            }

                            // LEFT SIDE
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (state.activePhone == "No-Thing") {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Red))
                                }
                                Text(
                                    text = if (state.activePhone == "TECCNO" || state.activePhone == "Xiaomax") timeString else "$timeString UTC",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    fontSize = 11.sp
                                )
                                if (state.activePhone == "TECCNO") {
                                    Box(modifier = Modifier.background(Color(0xFFE91E63), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                                        Text("TURBO ON", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (state.activePhone == "Xiaomax") {
                                    Text("↓ 1.2 MB/s", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // CENTER ISLAND / NOTCH
                            when (state.activePhone) {
                                "hiPhone" -> {
                                    Box(
                                        modifier = Modifier.height(28.dp).clip(RoundedCornerShape(14.dp)).background(Color.Black).padding(horizontal = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00E676)))
                                            Text("Ecosystem 5G", color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Icon(imageVector = Icons.Default.CellTower, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                        }
                                    }
                                }
                                "Samsong", "Xiaomax" -> {
                                    Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(Color.Black).border(1.dp, Color.DarkGray, CircleShape))
                                }
                                "No-Thing" -> {
                                    Box(
                                        modifier = Modifier.height(24.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha=0.5f)).border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00E676)))
                                            Text("GLYPH // OS", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                "Pixxelate" -> {
                                    Box(
                                        modifier = Modifier.height(28.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFF1E1E1E)).padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF00E676)))
                                            Text("Ecosystem 5G", color = Color.White.copy(alpha = 0.9f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).border(1.dp, Color.Gray, CircleShape))
                                        }
                                    }
                                }
                                else -> { Spacer(modifier = Modifier.width(40.dp)) }
                            }

                            // RIGHT SIDE
                            val batteryLevel = state.energy.coerceIn(0, 100)
                            val batteryColor = when {
                                batteryLevel > 50 -> Color(0xFF4CAF50)
                                batteryLevel > 20 -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (!state.isPhoneLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock Phone",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp).clickable { viewModel.setPhoneLocked(true) }.padding(end = 4.dp)
                                    )
                                }
                                if (state.activePhone == "TECCNO") {
                                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(12.dp))
                                }
                                Text(if (state.activePhone == "hiPhone" || state.activePhone == "No-Thing") "5G" else "5G+", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Icon(imageVector = Icons.Default.SignalCellular4Bar, contentDescription = "Signal", tint = Color.White, modifier = Modifier.size(11.dp))
                                if (state.activePhone != "hiPhone") {
                                    Icon(imageVector = Icons.Default.Wifi, contentDescription = "Wi-Fi", tint = Color.White, modifier = Modifier.size(11.dp))
                                }
                                if (state.activePhone == "Samsong") {
                                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Volume", tint = Color.White, modifier = Modifier.size(11.dp))
                                }
                                if (state.activePhone == "Xiaomax") {
                                    Box(modifier = Modifier.background(Color(0xFFFF9800), RoundedCornerShape(2.dp)).padding(horizontal = 2.dp)) {
                                        Text("120W", color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = "$batteryLevel%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = "Battery", tint = batteryColor, modifier = Modifier.size(12.dp))
                            }
                        }
                    }

                    // State for quick launching apps from lockscreen notifications
                    var activeAppId by remember { mutableStateOf<String?>(null) }

                    val isStolen = state.spiritualJournal.contains("active_phone_theft")

                    // Content Area (Separated by Lock Screen)
                    if (isStolen) {
                        Box(modifier = Modifier.fillMaxSize().padding(top = 28.dp, bottom = 12.dp)) {
                            TechStoreScreen(
                                state = state,
                                viewModel = viewModel,
                                onPhoneBought = {
                                    // Remove theft tag
                                }
                            )
                        }
                    } else {
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
                                LockScreenView(
                                    state = state,
                                    viewModel = viewModel,
                                    onQuickLaunchApp = { appId ->
                                        activeAppId = appId
                                        viewModel.setPhoneLocked(false)
                                    }
                                )
                            } else {
                                SmartphoneHomeView(
                                    state = state,
                                    viewModel = viewModel,
                                    activeAppId = activeAppId,
                                    onActiveAppChange = { activeAppId = it },
                                    onDismiss = onDismiss
                                )
                            }
                    }

                    // Floating Edge Panels overlay (Top layer, outside of AnimatedContent but inside phone screen)
                    if (state.activePhone == "Samsong") {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 2.dp)
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                                    .background(Color.White.copy(alpha = 0.4f))
                            )
                        }
                    } else if (state.activePhone == "TECCNO") {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                                    .background(Color(0xFFE91E63).copy(alpha=0.9f))
                                    .padding(vertical = 12.dp, horizontal = 4.dp)
                            ) {
                                Text("BOOST", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold, modifier = Modifier.rotate(-90f))
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
fun LockScreenView(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onQuickLaunchApp: (String) -> Unit = {}
) {
    val timeString = remember {
        val sdf = java.text.SimpleDateFormat("h:mm", java.util.Locale.US)
        sdf.format(java.util.Date())
    }

    var swipeOffsetY by remember { mutableFloatStateOf(0f) }
    val animatedOffsetY by androidx.compose.animation.core.animateFloatAsState(targetValue = swipeOffsetY)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset { androidx.compose.ui.unit.IntOffset(0, animatedOffsetY.toInt()) }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (swipeOffsetY < -300f) {
                            viewModel.setPhoneLocked(false)
                        } else {
                            swipeOffsetY = 0f
                        }
                    }
                ) { change, dragAmount ->
                    change.consumePositionChange()
                    swipeOffsetY = (swipeOffsetY + dragAmount).coerceAtMost(0f)
                }
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Lock & Time Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Device Locked",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1).sp
                ),
                fontSize = 62.sp,
                color = Color.White
            )
            Text(
                text = "Week ${state.currentWeek} • Year ${state.currentYear}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Artist Status Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (state.stress > 70f) Color(0xFFFF5252) else Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${state.artistName} • ${String.format("%,d", state.followers)} Followers",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        // Lockscreen Actionable Notification Stack
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NOTIFICATION CENTER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White.copy(alpha = 0.6f)
                )
                if (state.notificationLogs.isNotEmpty()) {
                    Text(
                        text = "${state.notificationLogs.size} New",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = NeonTeal
                    )
                }
            }

            val unreadChatCount = state.chatConversations.sumOf { it.unreadCount }
            val notifications = buildList {
                if (unreadChatCount > 0) {
                    add(
                        LockNotification(
                            appId = "chat",
                            icon = Icons.Default.ChatBubble,
                            badgeColor = NeonTeal,
                            appName = "SeeChat",
                            title = "$unreadChatCount Unread Messages",
                            body = "Key industry contacts and collaborators are waiting for your response."
                        )
                    )
                }
                val unreadContracts = state.mailbox.count { !it.isRead && (it.type == "LABEL_OFFER" || it.type == "RECORD_DEAL_OFFER" || it.type == "CONTRACT_PROPOSAL" || it.reqSingles > 0 || it.reqAlbumsLP > 0) }
                val unreadRegularMail = state.mailbox.count { !it.isRead && !(it.type == "LABEL_OFFER" || it.type == "RECORD_DEAL_OFFER" || it.type == "CONTRACT_PROPOSAL" || it.reqSingles > 0 || it.reqAlbumsLP > 0) }
                if (unreadContracts > 0) {
                    add(
                        LockNotification(
                            appId = "email",
                            icon = Icons.Default.Mail,
                            badgeColor = Color(0xFFFFD700),
                            appName = "Apex Mail",
                            title = "$unreadContracts Record Deal Proposal(s)",
                            body = "Major and independent label contract agreements awaiting your review."
                        )
                    )
                } else if (unreadRegularMail > 0) {
                    add(
                        LockNotification(
                            appId = "email",
                            icon = Icons.Default.Mail,
                            badgeColor = Color(0xFF2979FF),
                            appName = "Apex Mail",
                            title = "$unreadRegularMail Unread Email(s)",
                            body = "Inquiries and proposals have arrived in your inbox."
                        )
                    )
                }
                if (state.taxDebt > 0.0) {
                    add(
                        LockNotification(
                            appId = "bank",
                            icon = Icons.Default.AccountBalance,
                            badgeColor = Color(0xFFFF5252),
                            appName = "Apex Bank",
                            title = "Tax Balance Pending",
                            body = "Outstanding tax balance of $${String.format("%,.0f", state.taxDebt)} requires settlement."
                        )
                    )
                }
                if (state.pendingStockOrders.isNotEmpty()) {
                    add(
                        LockNotification(
                            appId = "weshare",
                            icon = Icons.Default.TrendingUp,
                            badgeColor = Color(0xFF00E676),
                            appName = "WESHARE",
                            title = "${state.pendingStockOrders.size} Orders Filled",
                            body = "Your equity limit orders were executed at market prices."
                        )
                    )
                }
                state.notificationLogs.take(2).forEach { log ->
                    add(
                        LockNotification(
                            appId = "capital_club",
                            icon = Icons.Default.Notifications,
                            badgeColor = NeonPink,
                            appName = "PulseWire",
                            title = "Industry Update",
                            body = log
                        )
                    )
                }
            }

            if (notifications.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pending alerts. All systems running smooth.",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                notifications.take(3).forEach { notif ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.55f)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setPhoneLocked(false)
                                onQuickLaunchApp(notif.appId)
                            }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(notif.badgeColor.copy(alpha = 0.2f))
                                    .border(1.dp, notif.badgeColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = notif.icon,
                                    contentDescription = notif.appName,
                                    tint = notif.badgeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = notif.appName.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = notif.badgeColor,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Now",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.4f),
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = notif.body,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Controls: Quick Action Buttons & Slide to Unlock
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera Action
                IconButton(
                    onClick = {
                        viewModel.setPhoneLocked(false)
                        onQuickLaunchApp("camera")
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Unlock Bar Text Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp).clickable { viewModel.setPhoneLocked(false) }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Swipe Up",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "SWIPE UP TO UNLOCK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

private data class LockNotification(
    val appId: String,
    val icon: ImageVector,
    val badgeColor: Color,
    val appName: String,
    val title: String,
    val body: String
)

@Composable
fun SmartphoneHomeView(
    state: GameStateEntity,
    viewModel: GameViewModel,
    activeAppId: String? = null,
    onActiveAppChange: (String?) -> Unit = {},
    onDismiss: () -> Unit
) {
    var targetBrowserUrl by remember { mutableStateOf<String?>(null) }

    // Artificial lag simulation for lower-end phones
    var isOpeningApp by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val handleLaunchApp: (String) -> Unit = { appId ->
        if (state.activePhone == "TECCNO" || state.activePhone == "Xiaomax") {
            isOpeningApp = appId
            scope.launch {
                val delayTime = if (state.activePhone == "TECCNO") 1200L else 700L
                kotlinx.coroutines.delay(delayTime)
                isOpeningApp = null
                onActiveAppChange(appId)
            }
        } else {
            onActiveAppChange(appId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                SmartphoneHomeScreenGrid(state = state, viewModel = viewModel, onLaunchApp = handleLaunchApp)
            } else {
                SmartphoneAppShell(
                    appId = app,
                    state = state,
                    viewModel = viewModel,
                    onCloseApp = {
                        targetBrowserUrl = null
                        onActiveAppChange(null)
                    },
                    onOpenApp = { targetApp ->
                        onActiveAppChange(targetApp)
                    },
                    initialBrowserUrl = targetBrowserUrl,
                    onOpenUrl = { url ->
                        targetBrowserUrl = url
                        onActiveAppChange("browser")
                    }
                )
            }
        }

        // Overlay for low RAM simulation
        if (isOpeningApp != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {}, // Intercept clicks while loading
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator(color = Color.White)
                    Text(
                        text = if (state.activePhone == "TECCNO") "Loading App... (Low RAM)" else "Opening...",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SmartphoneHomeScreenGrid(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onLaunchApp: (String) -> Unit
) {
    var selectedAppCategory by remember { mutableStateOf("All") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Living Interactive Top Widgets
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Phone Specific Top Banners
            if (state.activePhone == "Samsong") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Text("Neo City 72°F • Partly Cloudy", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
            }

            // Widget 1: Now Playing / Trending Release
            val latestSong = state.songs.lastOrNull { it.isReleased } ?: state.songs.lastOrNull()
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLaunchApp("music_app") }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonTeal, NeonPurple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Music",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (latestSong != null) latestSong.title else "No Master Tracks Yet",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (latestSong != null) "${String.format("%,d", latestSong.totalStreams)} Total Streams • ${latestSong.genreId.replaceFirstChar { it.uppercase() }}" else "Record songs in Studio to stream",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.65f),
                            maxLines = 1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonTeal.copy(alpha = 0.18f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AIRPLAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = NeonTeal,
                            fontSize = 9.sp
                        )
                    }
                }
            }


        }

            // Phone Specific Mid Banners
            if (state.activePhone == "Xiaomax") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black.copy(alpha=0.4f)).padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(12.dp))
                        Text("GAME TURBO 6.0", color = Color.White.copy(alpha=0.9f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("RAM", color = Color.Gray, fontSize = 8.sp)
                        Text("16GB+8GB", color = Color(0xFF2196F3), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("•", color = Color.Gray, fontSize = 9.sp)
                        Text("54 FPS", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

        // 4-Column High-Craft Fictional Ecosystem App Grid
        val unreadChatCount = state.chatConversations.sumOf { it.unreadCount }
        val unreadMailCount = state.mailbox.count { !it.isRead }
        val breakingNewsCount = if (state.activeGlobalEvent != null) 1 else 0
        val bankAlertCount = if (state.taxDebt > 0.0) 1 else 0
        val wellnessAlertCount = if (state.stress >= 75f || state.energy <= 20) 1 else 0

        val gridApps = when (state.activePhone) {
            "hiPhone" -> listOf(
                AppConfig("chat", "SeeChat", Icons.Default.Forum, Color(0xFF34C759), unreadChatCount),
                AppConfig("cast", "Cast", Icons.Default.Tag, Color(0xFFFF2D55), if (state.trendingScore > 40f) 1 else 0),
                AppConfig("metube", "MeTube", Icons.Default.OndemandVideo, Color(0xFFFF3B30), 0),
                AppConfig("songify", "Songify", Icons.Default.LibraryMusic, Color(0xFF1DB954), 0),
                AppConfig("megaphone", "Megaphone", Icons.Default.QueueMusic, Color(0xFFFC3C44), 0),
                AppConfig("jams", "Jams", Icons.Default.Headset, Color(0xFF32ADE6), 0),
                AppConfig("sharp", "Sharp", Icons.Default.Backup, Color(0xFFFF9500), 0),
                AppConfig("tunes", "Tunes", Icons.Default.Audiotrack, Color(0xFFFFCC00), 0),
                AppConfig("weshare", "WESHARE", Icons.Default.TrendingUp, Color(0xFF34C759), state.pendingStockOrders.size),
                AppConfig("genesis", "Genesis", Icons.Default.MonetizationOn, Color(0xFFFF9500), state.pendingCryptoOrders.size),
                AppConfig("ventures", "VENTURES", Icons.Default.BusinessCenter, Color(0xFF6200EA), 0),
                AppConfig("politics", "Civic Hub", Icons.Default.HowToVote, Color(0xFF0284C7), if (state.isElectionSeason && !state.hasVotedInCurrentElection) 1 else 0),
                AppConfig("bank", "Apex Bank", Icons.Default.Museum, Color(0xFF5856D6), bankAlertCount),
                AppConfig("recruit", "Staff Hub", Icons.Default.AssignmentInd, Color(0xFF32ADE6), 0),
                AppConfig("spiritual", "Soul Care", Icons.Default.SelfImprovement, Color(0xFFFFCC00), wellnessAlertCount),
                AppConfig("calendar", "Planner", Icons.Default.Event, Color(0xFFFF2D55), 0),
                AppConfig("email", "Mail", Icons.Default.MarkEmailUnread, Color(0xFF007AFF), unreadMailCount),
                AppConfig("capital_club", "Crush", Icons.Default.FavoriteBorder, Color(0xFFFF2D55), 0),
                AppConfig("promo", "Ad Company", Icons.Default.RecordVoiceOver, Color(0xFF34C759), 0),
                AppConfig("merch", "Merch", Icons.Default.ShoppingBag, Color(0xFFFF9500), 0),
                AppConfig("security_app", "Shield", Icons.Default.Security, Color(0xFF32ADE6), 0),
                AppConfig("notes", "Notes", Icons.Default.Note, Color(0xFFFFCC00), 0),
                AppConfig("ebook", "eBook", Icons.Default.MenuBook, Color(0xFF8B5CF6), 0),
                AppConfig("camera", "Camera", Icons.Default.CameraAlt, Color(0xFF34C759), 0),
                AppConfig("browser", "Web Hub", Icons.Default.TravelExplore, Color(0xFF007AFF), 0),
                AppConfig("legacy", "Legacy", Icons.Default.WorkspacePremium, Color(0xFFFFCC00), 0),
                AppConfig("os_upgrade", "OS Upgrade", Icons.Default.BrowserUpdated, Color(0xFF5856D6), 0),
                AppConfig("settings", "Settings", Icons.Default.ManageAccounts, Color(0xFF8E8E93), 0)
            )
            "Xiaomax", "TECCNO" -> listOf(
                AppConfig("chat", "SeeChat", Icons.Default.Chat, Color(0xFF00C853), unreadChatCount),
                AppConfig("cast", "Cast", Icons.Default.FlutterDash, Color(0xFFFF3D00), if (state.trendingScore > 40f) 1 else 0),
                AppConfig("metube", "MeTube", Icons.Default.SlowMotionVideo, Color(0xFFD50000), 0),
                AppConfig("songify", "Songify", Icons.Default.Equalizer, Color(0xFF00E676), 0),
                AppConfig("megaphone", "Megaphone", Icons.Default.SpeakerGroup, Color(0xFFFF1744), 0),
                AppConfig("jams", "Jams", Icons.Default.SurroundSound, Color(0xFF00B0FF), 0),
                AppConfig("sharp", "Sharp", Icons.Default.SettingsSystemDaydream, Color(0xFFFF6D00), 0),
                AppConfig("tunes", "Tunes", Icons.Default.Radio, Color(0xFFFFD600), 0),
                AppConfig("weshare", "WESHARE", Icons.Default.TrendingUp, Color(0xFF00E676), state.pendingStockOrders.size),
                AppConfig("genesis", "Genesis", Icons.Default.MonetizationOn, Color(0xFFFF6D00), state.pendingCryptoOrders.size),
                AppConfig("ventures", "VENTURES", Icons.Default.BusinessCenter, Color(0xFF6200EA), 0),
                AppConfig("politics", "Civic Hub", Icons.Default.HowToVote, Color(0xFF0284C7), if (state.isElectionSeason && !state.hasVotedInCurrentElection) 1 else 0),
                AppConfig("bank", "Apex Bank", Icons.Default.AccountBalance, Color(0xFF6200EA), bankAlertCount),
                AppConfig("recruit", "Staff Hub", Icons.Default.SupervisedUserCircle, Color(0xFF00B0FF), 0),
                AppConfig("spiritual", "Soul Care", Icons.Default.AcUnit, Color(0xFFFFEA00), wellnessAlertCount),
                AppConfig("calendar", "Planner", Icons.Default.EditCalendar, Color(0xFFF50057), 0),
                AppConfig("email", "Mail", Icons.Default.Email, Color(0xFF2962FF), unreadMailCount),
                AppConfig("capital_club", "Crush", Icons.Default.Loyalty, Color(0xFFF50057), 0),
                AppConfig("promo", "Ad Company", Icons.Default.Podcasts, Color(0xFF00E676), 0),
                AppConfig("merch", "Merch", Icons.Default.LocalMall, Color(0xFFFF6D00), 0),
                AppConfig("security_app", "Shield", Icons.Default.GppGood, Color(0xFF00B0FF), 0),
                AppConfig("notes", "Notes", Icons.Default.SpeakerNotes, Color(0xFFFFEA00), 0),
                AppConfig("ebook", "eBook", Icons.Default.MenuBook, Color(0xFF8B5CF6), 0),
                AppConfig("camera", "Camera", Icons.Default.Camera, Color(0xFF00E676), 0),
                AppConfig("browser", "Web Hub", Icons.Default.Explore, Color(0xFF2962FF), 0),
                AppConfig("legacy", "Legacy", Icons.Default.Stars, Color(0xFFFFEA00), 0),
                AppConfig("os_upgrade", "OS Upgrade", Icons.Default.SystemUpdateAlt, Color(0xFF6200EA), 0),
                AppConfig("settings", "Settings", Icons.Default.DisplaySettings, Color(0xFF757575), 0)
            )
            else -> listOf(
                AppConfig("chat", "SeeChat", Icons.Default.ChatBubble, Color(0xFF00A884), unreadChatCount),
                AppConfig("cast", "Cast", Icons.Default.AlternateEmail, Color(0xFFE91E63), if (state.trendingScore > 40f) 1 else 0),
                AppConfig("metube", "MeTube", Icons.Default.PlayArrow, Color(0xFFFF0033), 0),
                AppConfig("songify", "Songify", Icons.Default.GraphicEq, Color(0xFF1DB954), 0),
                AppConfig("megaphone", "Megaphone", Icons.Default.MusicNote, Color(0xFFFC3C44), 0),
                AppConfig("jams", "Jams", Icons.Default.Headphones, Color(0xFF00E5FF), 0),
                AppConfig("sharp", "Sharp", Icons.Default.CloudUpload, Color(0xFFFF5500), 0),
                AppConfig("tunes", "Tunes", Icons.Default.Album, Color(0xFFFFAA00), 0),
                AppConfig("weshare", "WESHARE", Icons.Default.TrendingUp, Color(0xFF00C853), state.pendingStockOrders.size),
                AppConfig("genesis", "Genesis", Icons.Default.MonetizationOn, Color(0xFFFF9100), state.pendingCryptoOrders.size),
                AppConfig("ventures", "VENTURES", Icons.Default.BusinessCenter, Color(0xFF6200EA), 0),
                AppConfig("politics", "Civic Hub", Icons.Default.HowToVote, Color(0xFF0284C7), if (state.isElectionSeason && !state.hasVotedInCurrentElection) 1 else 0),
                AppConfig("bank", "Apex Bank", Icons.Default.AccountBalance, Color(0xFF7C4DFF), bankAlertCount),
                AppConfig("recruit", "Staff Hub", Icons.Default.Badge, Color(0xFF00E5FF), 0),
                AppConfig("spiritual", "Soul Care", Icons.Default.Spa, Color(0xFFFFD600), wellnessAlertCount),
                AppConfig("calendar", "Planner", Icons.Default.CalendarMonth, Color(0xFFFF4081), 0),
                AppConfig("email", "Mail", Icons.Default.Mail, Color(0xFF2979FF), unreadMailCount),
                AppConfig("capital_club", "Crush", Icons.Default.Favorite, Color(0xFFE91E63), 0),
                AppConfig("promo", "Ad Company", Icons.Default.Campaign, Color(0xFF69F0AE), 0),
                AppConfig("merch", "Merch", Icons.Default.Storefront, Color(0xFFFF6E40), 0),
                AppConfig("security_app", "Shield", Icons.Default.Shield, Color(0xFF40C4FF), 0),
                AppConfig("notes", "Notes", Icons.Default.Description, Color(0xFFFFAB00), 0),
                AppConfig("ebook", "eBook", Icons.Default.MenuBook, Color(0xFF8B5CF6), 0),
                AppConfig("camera", "Camera", Icons.Default.PhotoCamera, Color(0xFF64DD17), 0),
                AppConfig("browser", "Web Hub", Icons.Default.Public, Color(0xFF536DFE), 0),
                AppConfig("legacy", "Legacy", Icons.Default.EmojiEvents, Color(0xFFFFD700), 0),
                AppConfig("os_upgrade", "OS Upgrade", Icons.Default.SystemUpdate, Color(0xFF6200EA), 0),
                AppConfig("settings", "Settings", Icons.Default.Settings, Color(0xFF9E9E9E), 0)
            )
        }

        val categorizedApps = gridApps.filter { app ->
            when (selectedAppCategory) {
                "Music" -> app.id in setOf("songify", "megaphone", "jams", "sharp", "tunes", "metube", "merch", "music_app")
                "Social" -> app.id in setOf("chat", "cast", "capital_club", "email", "recruit")
                "Business" -> app.id in setOf("ventures", "weshare", "genesis", "bank", "politics", "promo", "security_app", "underworld")
                "Life" -> app.id in setOf("calendar", "spiritual", "ebook", "camera", "browser", "notes", "legacy", "os_upgrade", "settings", "chronicle")
                else -> true
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 4.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            listOf("All", "Music", "Social", "Business", "Life").forEach { category ->
                val selected = selectedAppCategory == category
                Surface(
                    onClick = { selectedAppCategory = category },
                    shape = RoundedCornerShape(18.dp),
                    color = if (selected) PrimaryBrandColor.copy(alpha = .85f) else Color.Black.copy(alpha = .35f),
                    border = BorderStroke(1.dp, if (selected) PrimaryBrandLight.copy(alpha = .7f) else Color.White.copy(alpha = .16f))
                ) {
                    Text(category, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp))
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categorizedApps) { app ->
                AppIconWidget(app = app, onClick = { onLaunchApp(app.id) })
            }
        }

        // Pixxelate Bottom Search Bar
        if (state.activePhone == "Pixxelate") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFF1E1E1E)).padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Ask Ezra or Search...", color = Color.White.copy(alpha=0.6f), fontSize = 13.sp, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(16.dp))
            }
        }

        // Frosted Glass Dock with 4 Primary Quick-Launch Apps
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dockApps = listOf(
                    AppConfig("chat", "SeeChat", Icons.Default.ChatBubble, Color(0xFF00A884), unreadChatCount),
                    AppConfig("cast", "Cast", Icons.Default.AlternateEmail, Color(0xFFE91E63), 0),
                    AppConfig("songify", "Songify", Icons.Default.GraphicEq, Color(0xFF1DB954), 0),
                    AppConfig("bank", "Bank", Icons.Default.AccountBalance, Color(0xFF7C4DFF), bankAlertCount)
                )

                dockApps.forEach { app ->
                    AppIconWidget(
                        app = app,
                        isDock = true,
                        onClick = { onLaunchApp(app.id) }
                    )
                }
            }
        }

        // Home Indicator Pill Bar (Updated to be more visible)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { viewModel.setPhoneLocked(true) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Lock Screen", tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lock", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
fun AppIconWidget(
    app: AppConfig,
    isDock: Boolean = false,
    onClick: () -> Unit
) {
    val iconSize = if (isDock) 50.dp else 52.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("app_icon_${app.id}")
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            // Icon Squircle Container
            Box(
                modifier = Modifier
                    .size(iconSize)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                app.color.copy(alpha = 0.95f),
                                app.color,
                                app.color.copy(alpha = 0.75f)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Vector Gloss Surface Highlight
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w, 0f)
                        lineTo(w, h * 0.40f)
                        quadraticTo(w * 0.5f, h * 0.52f, 0f, h * 0.40f)
                        close()
                    }
                    drawPath(path = path, color = Color.White.copy(alpha = 0.22f))
                }

                // App Glyph Icon
                Icon(
                    imageVector = app.icon,
                    contentDescription = app.label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // High-Contrast Red Notification Badge
            if (app.badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3B30))
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (app.badgeCount > 99) "99+" else app.badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        if (!isDock) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SmartphoneAppShell(
    appId: String,
    state: GameStateEntity,
    viewModel: GameViewModel,
    onCloseApp: () -> Unit,
    onOpenApp: (String) -> Unit = {},
    initialBrowserUrl: String? = null,
    onOpenUrl: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top App Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg)
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
                        "ventures" -> "Business Ventures"
                        "weshare" -> "WESHARE Brokerage"
                        "genesis" -> "Genesis Crypto Markets"
                        "bank" -> "Apex Bank & Trust"
                        "songify" -> "Songify Music"
                        "megaphone" -> "Megaphone Lossless"
                        "jams" -> "JAMS Music Africa"
                        "sharp" -> "SHARP_MUSIC"
                        "tunes" -> "TUNES Street Mixtapes"
                        "music_app" -> "Songify Music"
                        "recruit" -> "Staff Hub"
                        "spiritual" -> "Soul Care Sanctuary"
                        "email" -> "Professional Email"
                        "capital_club" -> "Crush Dating"
                        "promo" -> "Ad Company Promotion"
                        "merch" -> "Merchandise Studio"
                        "legacy" -> "Legacy Hall of Fame"
                        "os_upgrade" -> "OS Upgrade Store"
                        "security_app" -> "Cyber Shield Defense"
                        "ebook" -> "eBook Store & Library"
                        "underworld" -> "Underworld Dark Syndicate"
                        "chronicle" -> "Chronicle World Archives"
                        "politics" -> "Civic Hub & Electoral Intelligence"
                        "browser" -> "Web Hub Browser"
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
                    .background(SuccessColor)
            )
        }

        // Active App Screen Injection
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            when (appId) {
                "chat" -> SeeChatScreen(state, viewModel)
                "cast" -> CastSocialScreen(state, viewModel, onOpenBrowser = onOpenUrl)
                "metube" -> MeTubeVideoScreen(state, viewModel)
                "ventures" -> {
                    if (state.isUnderArrest) {
                        com.example.ui.StateCustodyLockoutScreen(
                            state = state,
                            viewModel = viewModel,
                            featureName = "Commercial Ventures & Enterprises",
                            onOpenScc = { onOpenUrl("statecrimecommand.gov") }
                        )
                    } else {
                        com.example.ui.ventures.VenturesAppScreen(state, viewModel)
                    }
                }
                "politics" -> com.example.ui.PoliticsAppScreen(state, viewModel, onOpenBrowser = onOpenUrl)
                "weshare" -> WeShareExchangeScreen(state, viewModel)
                "genesis" -> GenesisExchangeScreen(state, viewModel)
                "bank" -> ApexBankingScreen(state, viewModel)
                "songify" -> SongifyScreen(state, viewModel, onOpenOtherPlatform = onOpenApp)
                "megaphone" -> MegaphoneScreen(state, viewModel, onOpenOtherPlatform = onOpenApp)
                "jams" -> JamsScreen(state, viewModel, onOpenOtherPlatform = onOpenApp)
                "sharp" -> SharpScreen(state, viewModel, onOpenOtherPlatform = onOpenApp)
                "tunes" -> TunesScreen(state, viewModel, onOpenOtherPlatform = onOpenApp)
                "music_app" -> SongifyScreen(state, viewModel, onOpenOtherPlatform = onOpenApp)
                "recruit" -> RecruitmentHallScreen(state, viewModel)
                "spiritual" -> SpiritualSanctuaryScreen(state, viewModel)
                "calendar" -> PlannerAppScreen(state, viewModel)
                "browser" -> WebHubAppScreen(state, viewModel, initialUrl = initialBrowserUrl)
                "camera" -> CameraAppScreen(state, viewModel)
                "notes" -> NotesAppScreen(state, viewModel)
                "email" -> ProfessionalEmailScreen(state, viewModel)
                "capital_club" -> com.example.ui.social.CapitalClubScreen(state, viewModel)
                "promo" -> AdCompanyPromotionScreen(state, viewModel)
                "merch" -> MerchStudioAppScreen(state, viewModel)
                "legacy" -> LegacyAppScreen(state, viewModel)
                "os_upgrade" -> OSUpgradeAppScreen(state, viewModel)
                "security_app" -> SecurityAppScreen(state, viewModel)
                "ebook" -> com.example.ui.EbookAppScreen(state, viewModel)
                "underworld" -> UnderworldAppScreen(state, viewModel)
                "chronicle" -> ChronicleAppScreen(state, viewModel)
                "settings" -> WallpaperSettingsScreen(state, viewModel)
            }
        }
    }
}

// =================== 1. SEEsCHAT MESSAGING SCREEN ===================
@Composable
fun SeeChatScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var activeChatNpcId by remember { mutableStateOf<String?>(null) }
    var showPsychologyDialogNpc by remember { mutableStateOf<NPCEntity?>(null) }
    var selectedTab by remember { mutableStateOf("CHATS") } // "CHATS" or "CONTACTS"
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.ensureInitialChats()
    }

    // All available contacts in game: NPCs + Dating Capital Club members
    val allContacts = remember(state.npcs, state.capitalClubMembers) {
        val npcList = state.npcs
        val ccDatingList = state.capitalClubMembers.filter { it.isDating || it.isSpouse }.map { cc ->
            NPCEntity(
                id = cc.id,
                name = cc.name,
                popularity = 60f,
                influence = 60f,
                fans = 100000L,
                relationshipWithPlayer = cc.relationshipScore.toFloat(),
                personality = cc.title
            )
        }
        (npcList + ccDatingList).distinctBy { it.id }
    }

    if (activeChatNpcId == null) {
        // WhatsApp Contacts & Chats View
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF111B21))) {
            // WhatsApp Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF202C33))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSearching) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search chats or contacts...", color = Color(0xFFAEBAC1), fontSize = 13.sp) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFFE9EDEF), fontSize = 13.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00A884),
                            unfocusedBorderColor = Color(0xFF2A3942),
                            focusedContainerColor = Color(0xFF111B21),
                            unfocusedContainerColor = Color(0xFF111B21)
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                searchQuery = ""
                                isSearching = false
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Search", tint = Color(0xFFAEBAC1), modifier = Modifier.size(18.dp))
                            }
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                } else {
                    Text(
                        text = "SeeChat",
                        color = Color(0xFFE9EDEF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isSearching = true }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFAEBAC1), modifier = Modifier.size(20.dp))
                        }
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFFAEBAC1), modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Tabs Row (CHATS, CONTACTS)
            val unreadTotal = remember(state.chatConversations) {
                state.chatConversations.sumOf { it.unreadCount }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF202C33))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // CHATS TAB
                Column(
                    modifier = Modifier
                        .clickable { selectedTab = "CHATS" }
                        .padding(bottom = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "CHATS",
                            color = if (selectedTab == "CHATS") Color(0xFF00A884) else Color(0xFFAEBAC1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (unreadTotal > 0) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00A884)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadTotal.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    if (selectedTab == "CHATS") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(44.dp).height(2.dp).background(Color(0xFF00A884)))
                    }
                }

                // CONTACTS TAB
                Column(
                    modifier = Modifier
                        .clickable { selectedTab = "CONTACTS" }
                        .padding(bottom = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "CONTACTS",
                            color = if (selectedTab == "CONTACTS") Color(0xFF00A884) else Color(0xFFAEBAC1),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${allContacts.size})",
                            color = if (selectedTab == "CONTACTS") Color(0xFF00A884) else Color(0xFFAEBAC1),
                            fontSize = 11.sp
                        )
                    }
                    if (selectedTab == "CONTACTS") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(60.dp).height(2.dp).background(Color(0xFF00A884)))
                    }
                }
            }

            if (selectedTab == "CHATS") {
                // CHATS TAB VIEW
                Box(modifier = Modifier.fillMaxSize()) {
                    val validContactIds = remember(allContacts) { allContacts.map { it.id }.toSet() }
                    val sortedChats = remember(state.chatConversations, validContactIds, searchQuery) {
                        state.chatConversations
                            .filter { conv ->
                                if (searchQuery.isBlank()) true
                                else conv.npcName.contains(searchQuery, ignoreCase = true) ||
                                        conv.messages.any { it.message.contains(searchQuery, ignoreCase = true) }
                            }
                            .sortedWith(
                                compareByDescending<ChatConversation> { conv ->
                                    conv.messages.lastOrNull()?.timestamp ?: 0L
                                }.thenByDescending { conv ->
                                    conv.unreadCount
                                }
                            )
                    }

                    if (sortedChats.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF202C33)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = "No Chats",
                                    tint = Color(0xFF00A884),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(text = "No active conversations", color = Color(0xFFE9EDEF), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Start chatting with your family, artist peers, or collaborators.",
                                color = Color(0xFFAEBAC1),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { selectedTab = "CONTACTS" },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A884)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Browse All Contacts (${allContacts.size})", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "Quick Connect:", color = Color(0xFF8696A0), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                val quickFamily = listOf(
                                    Triple("npc_family_mom", "💖 Mom", "Family"),
                                    Triple("npc_family_uncle_joe", "🕶️ Uncle Joe", "Family"),
                                    Triple("npc_family_cousin_sarah", "🎶 Sarah", "Family"),
                                    Triple("npc_rookie_ryan", "🎤 Ryan", "Indie")
                                )
                                quickFamily.forEach { (id, label, _) ->
                                    FilterChip(
                                        selected = false,
                                        onClick = {
                                            viewModel.markChatRead(id)
                                            activeChatNpcId = id
                                        },
                                        label = { Text(label, fontSize = 11.sp, color = Color(0xFFE9EDEF)) },
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFF202C33))
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            items(sortedChats) { conv ->
                                val npc = state.npcs.find { it.id == conv.npcId }
                                val ccMember = state.capitalClubMembers.find { it.id == conv.npcId }

                                val displayName = npc?.name ?: ccMember?.name ?: conv.npcName
                                val relationScore = npc?.relationshipWithPlayer?.toInt() ?: ccMember?.relationshipScore ?: 0
                                val lastMsg = conv.messages.lastOrNull()?.message ?: "Tap to message..."
                                val unread = conv.unreadCount

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF111B21))
                                        .clickable {
                                            viewModel.markChatRead(conv.npcId)
                                            activeChatNpcId = conv.npcId
                                        }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Contact Profile Picture
                                    if (ccMember != null && ccMember.imageUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = ccMember.imageUrl,
                                            contentDescription = displayName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(44.dp).clip(CircleShape)
                                        )
                                    } else {
                                        ArtistFaceAvatar(
                                            artistIdentifier = conv.npcId,
                                            modifier = Modifier.size(44.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = displayName,
                                                color = Color(0xFFE9EDEF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "${relationScore}% Rel",
                                                color = if (relationScore >= 50) Color(0xFF00A884) else Color(0xFFAEBAC1),
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DoneAll,
                                                contentDescription = "Read Status",
                                                tint = if (unread == 0) Color(0xFF53BDEB) else Color(0xFFAEBAC1),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = lastMsg,
                                                color = Color(0xFF8696A0),
                                                fontSize = 12.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    if (unread > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(18.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF25D366)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = unread.toString(),
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = Color(0xFF202C33).copy(alpha = 0.5f), modifier = Modifier.padding(start = 72.dp))
                            }
                        }
                    }

                    // Floating Action Button to initiate new chats / view contacts
                    FloatingActionButton(
                        onClick = { selectedTab = "CONTACTS" },
                        containerColor = Color(0xFF00A884),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .testTag("seechat_new_chat_fab")
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "New Chat")
                    }
                }
            } else {
                // CONTACTS DIRECTORY TAB VIEW
                val filteredContacts = remember(allContacts, searchQuery) {
                    if (searchQuery.isBlank()) allContacts
                    else allContacts.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.personality.contains(searchQuery, ignoreCase = true) ||
                        it.genrePreference.contains(searchQuery, ignoreCase = true)
                    }
                }

                // Group contacts: Family, Rookies & Indie, Stars, Civic/VIPs, Dating
                val familyContacts = filteredContacts.filter { it.id.startsWith("npc_family") }
                val rookieContacts = filteredContacts.filter { it.id.startsWith("npc_rookie") || (it.popularity < 40f && !it.id.startsWith("npc_family") && !it.id.startsWith("cc_")) }
                val starContacts = filteredContacts.filter { it.popularity >= 60f && !it.id.startsWith("npc_gov") && !it.id.startsWith("npc_sen") && !it.id.startsWith("cc_") }
                val civicContacts = filteredContacts.filter { it.id.startsWith("npc_gov") || it.id.startsWith("npc_sen") }
                val datingContacts = filteredContacts.filter { it.id.startsWith("cc_") }
                val otherContacts = filteredContacts.filter {
                    it !in familyContacts && it !in rookieContacts && it !in starContacts && it !in civicContacts && it !in datingContacts
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (filteredContacts.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                                Text("No contacts matching '$searchQuery'", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }

                    fun renderContactGroup(title: String, icon: ImageVector, list: List<NPCEntity>) {
                        if (list.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF00A884), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "$title (${list.size})",
                                        color = Color(0xFF00A884),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            items(list) { contact ->
                                val ccMember = state.capitalClubMembers.find { it.id == contact.id }
                                val relation = contact.relationshipWithPlayer.toInt()

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2C34)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.markChatRead(contact.id)
                                            activeChatNpcId = contact.id
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (ccMember != null && ccMember.imageUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = ccMember.imageUrl,
                                                contentDescription = contact.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(42.dp).clip(CircleShape)
                                            )
                                        } else {
                                            ArtistFaceAvatar(
                                                artistIdentifier = contact.id,
                                                modifier = Modifier.size(42.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = contact.name,
                                                    color = Color(0xFFE9EDEF),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (relation >= 50) Color(0xFF00A884).copy(alpha = 0.2f) else Color.DarkGray)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "$relation% Rel",
                                                        color = if (relation >= 50) Color(0xFF00A884) else Color(0xFFAEBAC1),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (ccMember != null) ccMember.title else "${contact.personality} • ${contact.genrePreference} • ${contact.age}y/o",
                                                color = Color(0xFF8696A0),
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = {
                                                viewModel.markChatRead(contact.id)
                                                activeChatNpcId = contact.id
                                            },
                                            modifier = Modifier.size(36.dp).background(Color(0xFF00A884), CircleShape)
                                        ) {
                                            Icon(imageVector = Icons.Default.Chat, contentDescription = "Message", tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    renderContactGroup("Family", Icons.Default.Favorite, familyContacts)
                    renderContactGroup("Artists & Indie Peers", Icons.Default.Mic, rookieContacts)
                    renderContactGroup("Industry Stars & Icons", Icons.Default.Stars, starContacts)
                    renderContactGroup("Civic Leaders & VIPs", Icons.Default.AccountBalance, civicContacts)
                    renderContactGroup("Dating & Matches", Icons.Default.FavoriteBorder, datingContacts)
                    renderContactGroup("Other Contacts", Icons.Default.People, otherContacts)
                }
            }
        }
    } else {
        // WhatsApp Chat Room view
        val npcId = activeChatNpcId!!
        val npc = state.npcs.find { it.id == npcId }
        val ccMember = state.capitalClubMembers.find { it.id == npcId }
        val conv = state.chatConversations.find { it.npcId == npcId }

        val chatDisplayName = npc?.name ?: ccMember?.name ?: conv?.npcName ?: "Unknown"
        val messages = conv?.messages ?: emptyList()
        var typedText by remember { mutableStateOf("") }
        val chatListState = rememberLazyListState()

        // Keep chat marked as read whenever open and auto-scroll to the latest message
        LaunchedEffect(conv?.unreadCount) {
            if ((conv?.unreadCount ?: 0) > 0) {
                viewModel.markChatRead(npcId)
            }
        }

        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                chatListState.scrollToItem(messages.size - 1)
            }
        }

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0B141A)).imePadding()) {
            // WhatsApp Chat Room Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF202C33))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeChatNpcId = null }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Arrow", tint = Color(0xFFE9EDEF))
                }
                if (ccMember != null && ccMember.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ccMember.imageUrl,
                        contentDescription = chatDisplayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(34.dp).clip(CircleShape)
                    )
                } else {
                    ArtistFaceAvatar(
                        artistIdentifier = npcId,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = chatDisplayName, color = Color(0xFFE9EDEF), fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = npc?.personality ?: ccMember?.title ?: "Contact", color = Color(0xFF8696A0), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = { if(npc != null) showPsychologyDialogNpc = npc }) {
                    Icon(imageVector = Icons.Default.Face, contentDescription = "Psychology Profile", tint = Color(0xFF00A884))
                }
            }

            // Message Bubble list (WhatsApp style) - starts at bottom, scrolls up for history
            LazyColumn(
                state = chatListState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF182229)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF1B34B), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Messages are end-to-end encrypted.",
                                        color = Color(0xFFF1B34B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Start a conversation with $chatDisplayName to discuss collaborations, seek advice, or strengthen your relationship.",
                                        color = Color(0xFF8696A0),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
                items(messages) { msg ->
                    val isPlayer = msg.senderId == "PLAYER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isPlayer) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPlayer) Color(0xFF005C4B) else Color(0xFF202C33)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = msg.message, color = Color(0xFFE9EDEF), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Quick reply templates
            val replies = remember(npcId) {
                when {
                    npcId == "npc_family_mom" -> listOf(
                        "Love you Mom! Drinking water and practicing daily!",
                        "Hey Mom! Working on new music, thanks for checking in.",
                        "Call you soon Mom! Everything is going great."
                    )
                    npcId == "npc_family_uncle_joe" -> listOf(
                        "What's good Uncle Joe! Grinding hard in the studio.",
                        "Keeping my contracts clean unc! Appreciate the wisdom.",
                        "Time to stack that bag! Catch you later unc."
                    )
                    npcId == "npc_family_cousin_sarah" -> listOf(
                        "Hey Sarah! Thanks for streaming my music!",
                        "Let's write a track together soon!",
                        "Hope the bake shop dream is going awesome!"
                    )
                    else -> listOf(
                        "Hey! Just checking in, hope the grind is going well.",
                        "Let's collab soon! We should secure a top placement.",
                        "Time is money! Let's schedule a professional gig.",
                        "Respect the hustle. I'm focusing on the next project."
                    )
                }
            }

            Column(modifier = Modifier.background(Color(0xFF1F2C34)).padding(8.dp)) {
                Text(
                    text = "Quick reply template (Tap to send):",
                    color = Color(0xFF8696A0),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    replies.forEach { text ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A3942)),
                            border = BorderStroke(1.dp, Color(0xFF00A884).copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clickable { viewModel.sendChatMessage(npcId, text, isChatCurrentlyOpen = true) }
                        ) {
                            Text(
                                text = text,
                                color = Color(0xFFE9EDEF),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                // Custom typing box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = typedText,
                        onValueChange = { typedText = it },
                        placeholder = { Text("Type a reply...", color = Color(0xFF8696A0), fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color(0xFFE9EDEF), fontSize = 12.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00A884),
                            unfocusedBorderColor = Color(0xFF2A3942),
                            focusedContainerColor = Color(0xFF2A3942),
                            unfocusedContainerColor = Color(0xFF2A3942)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (typedText.isNotBlank()) {
                                viewModel.sendChatMessage(npcId, typedText, isChatCurrentlyOpen = true)
                                typedText = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF00A884), CircleShape)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send Message",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
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

// =================== 2. CAST SOCIAL MEDIA CLONE SCREEN ===================
// =================== 3. METUBEs VIDEO SHARER SCREEN ===================
@Composable
fun MeTubeVideoScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var activeTab by remember { mutableStateOf("home") } // "home", "your_videos", "create"
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }

    // Video Player Modal state
    var activeWatchingVideo by remember { mutableStateOf<com.example.data.MeTubeVideo?>(null) }
    var likedVideoIds by remember { mutableStateOf(setOf<String>()) }
    var isSubscribedMap by remember { mutableStateOf(mapOf<String, Boolean>()) }
    var newCommentText by remember { mutableStateOf("") }
    var localVideoComments by remember { mutableStateOf(mapOf<String, List<String>>()) }

    // Upload Studio State
    var publishTitle by remember { mutableStateOf("") }
    var publishType by remember { mutableStateOf("MUSIC_VIDEO") } // MUSIC_VIDEO, VLOG, LIVE_PERFORMANCE, INTERVIEW
    var publishCategory by remember { mutableStateOf("Music & Art") }
    var selectedThumbnailUri by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Native Photo Picker for device storage
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val photosDir = File(context.filesDir, "camera_photos").apply { mkdirs() }
                val targetFile = File(photosDir, "METUBE_THUMB_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    targetFile.outputStream().use { output -> input.copyTo(output) }
                }
                savePhotoToMediaStore(context, targetFile)
                viewModel.addSavedPhoto(targetFile.absolutePath)
                selectedThumbnailUri = targetFile.absolutePath
                viewModel.showToast("Selected photo from device storage!")
            } catch (e: Exception) {
                selectedThumbnailUri = uri.toString()
            }
        }
    }

    val playerVideos = remember(state.meTubeVideos, state.artistName) {
        state.meTubeVideos.filter {
            it.artistName == state.artistName || (it.artistName.isNullOrBlank() && it.revenueEarned > 0.0)
        }
    }

    val totalLifetimeViews = remember(playerVideos) {
        playerVideos.sumOf { it.views }
    }

    val formatViews: (Long) -> String = { views ->
        when {
            views >= 1_000_000 -> String.format("%.1fM", views / 1_000_000.0)
            views >= 1_000 -> String.format("%.1fK", views / 1_000.0)
            else -> views.toString()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
        // Authentic Top YouTube App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // YouTube Red Brand Icon
                Box(
                    modifier = Modifier
                        .size(width = 30.dp, height = 22.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFF0000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "MeTube",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MeTube",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.6).sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isSearchOpen = !isSearchOpen },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                }

                Icon(
                    imageVector = Icons.Default.Cast,
                    contentDescription = "Cast",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9800))
                        .clickable { activeTab = "your_videos" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.artistName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Expandable Search Bar
        AnimatedVisibility(visible = isSearchOpen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF222222))
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search MeTube videos...", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            }
        }

        // Main Tab Content Area
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (activeTab) {
                "home" -> {
                    // ================= TAB 1: HOME FEED =================
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Category Filter Chips
                        val categories = listOf("All", "Music Videos", "Behind The Scenes", "Live", "Interviews", "Vlogs", "Trending")
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            items(categories) { cat ->
                                val isSelected = selectedCategoryFilter == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color.White else Color(0xFF272727))
                                        .clickable { selectedCategoryFilter = cat }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        val filteredVideos = remember(state.meTubeVideos, selectedCategoryFilter, searchQuery) {
                            state.meTubeVideos.filter { video ->
                                val matchesCategory = when (selectedCategoryFilter) {
                                    "All" -> true
                                    "Music Videos" -> video.type == "MUSIC_VIDEO"
                                    "Behind The Scenes" -> video.type == "BEHIND_THE_SCENES"
                                    "Live" -> video.type == "LIVE_PERFORMANCE"
                                    "Interviews" -> video.type == "INTERVIEW"
                                    "Vlogs" -> video.type == "VLOG"
                                    "Trending" -> video.views > 2000
                                    else -> true
                                }
                                val matchesSearch = searchQuery.isBlank() ||
                                        video.title.contains(searchQuery, ignoreCase = true) ||
                                        (video.artistName?.contains(searchQuery, ignoreCase = true) == true)
                                matchesCategory && matchesSearch
                            }
                        }

                        if (filteredVideos.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("No videos found", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Be the first to upload a video!", color = Color.Gray, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { activeTab = "create" },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Create Video", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(filteredVideos) { video ->
                                    val isPlayerVideo = video.artistName == state.artistName || (video.artistName.isNullOrBlank() && video.revenueEarned > 0.0)
                                    val author = if (isPlayerVideo) state.artistName else (video.artistName ?: "Independent Artist")

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeWatchingVideo = video }
                                    ) {
                                        // 16:9 Thumbnail Box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(16f / 9f)
                                                .background(Color(0xFF1E1E1E))
                                        ) {
                                            val isLocalImage = video.thumbnailId.startsWith("file://") ||
                                                    video.thumbnailId.startsWith("/") ||
                                                    video.thumbnailId.startsWith("content://") ||
                                                    video.thumbnailId.startsWith("http")

                                            if (isLocalImage) {
                                                AsyncImage(
                                                    model = video.thumbnailId,
                                                    contentDescription = video.title,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                // Authentic Dynamic YouTube Thumbnail Card
                                                val gradientStart = when (video.type) {
                                                    "MUSIC_VIDEO" -> Color(0xFF5E17EB)
                                                    "LIVE_PERFORMANCE" -> Color(0xFFD81B60)
                                                    "INTERVIEW" -> Color(0xFF00897B)
                                                    else -> Color(0xFF1E88E5)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(Brush.verticalGradient(listOf(gradientStart, Color(0xFF0A0A0C)))),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(46.dp)
                                                                .clip(CircleShape)
                                                                .background(Color.Black.copy(alpha = 0.6f)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                                                        }
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            text = video.title,
                                                            color = Color.White,
                                                            fontSize = 15.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            text = "OFFICIAL ${video.type.replace("_", " ")}",
                                                            color = Color(0xFFFFD600),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Black
                                                        )
                                                    }
                                                }
                                            }

                                            // Top Right HD Badge
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp)
                                                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("4K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }

                                            // Bottom Left Type Tag
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(8.dp)
                                                    .background(Color(0xFFFF0000), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(video.type.replace("_", " "), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }

                                            // Bottom Right Duration Pill
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(8.dp)
                                                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("3:45", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Video Metadata Row
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            // Channel Avatar
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isPlayerVideo) Color(0xFF3F51B5) else Color(0xFFE65100)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(author.take(1).uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = video.title,
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    lineHeight = 18.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "$author • ${formatViews(video.views)} views • Year ${video.yearPublished} Wk ${video.weekPublished}",
                                                    color = Color(0xFFAAAAAA),
                                                    fontSize = 11.sp
                                                )
                                            }

                                            IconButton(onClick = { /* options */ }, modifier = Modifier.size(24.dp)) {
                                                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "your_videos" -> {
                    // ================= TAB 2: YOUR PAGE (CHANNEL) =================
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            // Channel Banner & Header
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF3F51B5))
                                                .border(2.dp, Color(0xFFFFD600), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                state.artistName.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = state.artistName,
                                                color = Color.White,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "@${state.artistName.lowercase().replace(" ", "")} • MeTube Creator",
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${formatViews(state.meTubeSubscribers.toLong())} subscribers • ${playerVideos.size} videos",
                                                color = Color(0xFFFFD600),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = Color(0xFF333333))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Monetization / Creator Studio Stats
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("MeTube Partner Revenue", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text("$${String.format("%,.2f", state.meTubeRevenue)}", color = Color(0xFF4CAF50), fontSize = 18.sp, fontWeight = FontWeight.Black)
                                            val isMonetized = state.meTubeSubscribers >= 1000L
                                            Text(
                                                text = if (isMonetized) "Partner Status: Monetized ✓" else "Partner Status: Locked (${state.meTubeSubscribers}/1,000 Subs)",
                                                color = if (isMonetized) Color(0xFF4CAF50) else Color(0xFFFFA000),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text("${String.format("%.1f", state.meTubeWatchTime)} watch hours", color = Color.LightGray, fontSize = 10.sp)
                                        }

                                        Button(
                                            onClick = { viewModel.collectMeTubeRevenue() },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF)),
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = state.meTubeRevenue > 0.0
                                        ) {
                                            Text("CLAIM PAYOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Contract Video Quota Tracker (if signed)
                                    if (state.signedLabelReqMeTubeVideos > 0) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        val isLabelReqMet = state.signedLabelDeliveredMeTubeVideos >= state.signedLabelReqMeTubeVideos
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isLabelReqMet) Color(0xFF1B5E20) else Color(0xFF37474F))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "Record Label Video Requirement:",
                                                    color = Color.White,
                                                    fontSize = 11.sp
                                                )
                                                Text(
                                                    "${state.signedLabelDeliveredMeTubeVideos} / ${state.signedLabelReqMeTubeVideos} Delivered ${if (isLabelReqMet) "✓" else ""}",
                                                    color = if (isLabelReqMet) Color(0xFFFFD600) else Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Uploaded Videos (${playerVideos.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = { activeTab = "create" },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("NEW VIDEO", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (playerVideos.isEmpty()) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.VideoCall, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("No Videos Uploaded Yet", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Upload music videos, studio vlogs, and tour clips to gain subscribers and earn ad revenue.", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { activeTab = "create" },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Create Your First Video", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            items(playerVideos) { video ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().clickable { activeWatchingVideo = video }
                                ) {
                                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        // Small 16:9 Thumbnail
                                        Box(
                                            modifier = Modifier
                                                .width(110.dp)
                                                .aspectRatio(16f / 9f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF262626))
                                        ) {
                                            val isLocalImage = video.thumbnailId.startsWith("file://") ||
                                                    video.thumbnailId.startsWith("/") ||
                                                    video.thumbnailId.startsWith("content://") ||
                                                    video.thumbnailId.startsWith("http")

                                            if (isLocalImage) {
                                                AsyncImage(
                                                    model = video.thumbnailId,
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier.fillMaxSize().background(Color(0xFF283593)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = video.title,
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${formatViews(video.views)} views • 👍 ${formatViews(video.likes)}",
                                                color = Color.Gray,
                                                fontSize = 11.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFF2E7D32))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text("Monetized", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Text(
                                                    "Earned: $${String.format("%.2f", video.revenueEarned)}",
                                                    color = Color(0xFF4CAF50),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "create" -> {
                    // ================= TAB 3: CREATE & POST (DEDICATED STUDIO) =================
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Create & Publish Video",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "Choose real photos taken with your camera or imported from your phone storage as the thumbnail.",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }

                        // Live 16:9 Thumbnail Preview
                        item {
                            Column {
                                Text("Thumbnail Preview", color = Color(0xFFFFD600), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF1E1E1E))
                                        .border(1.5.dp, Color(0xFF333333), RoundedCornerShape(12.dp))
                                ) {
                                    if (selectedThumbnailUri != null) {
                                        AsyncImage(
                                            model = selectedThumbnailUri,
                                            contentDescription = "Selected Thumbnail",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Brush.verticalGradient(listOf(Color(0xFF311B92), Color(0xFF0D0819)))),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(36.dp))
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text("Select a photo below for your thumbnail", color = Color.LightGray, fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    // Overlay Tags
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                            .background(Color(0xFFFF0000), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(publishType.replace("_", " "), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(8.dp)
                                            .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("3:45", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // THUMBNAIL SELECTOR (NO HTTPS LINKS!)
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Choose Video Thumbnail", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        if (selectedThumbnailUri != null) {
                                            Text(
                                                "Clear",
                                                color = Color(0xFFFF5252),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.clickable { selectedThumbnailUri = null }
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Pick from Device Storage Button
                                    Button(
                                        onClick = {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Pick Image from Phone Storage", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Captured Photos from Camera App
                                    Text("From Camera App Snapshots:", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (state.savedPhotos.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF222222))
                                                .padding(14.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No camera photos yet. Use the smartphone Camera app to snap studio photos, or pick from storage above.",
                                                color = Color.Gray,
                                                fontSize = 11.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    } else {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            items(state.savedPhotos) { photoPath ->
                                                val isSelected = selectedThumbnailUri == photoPath
                                                Box(
                                                    modifier = Modifier
                                                        .size(width = 80.dp, height = 55.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .border(
                                                            width = if (isSelected) 2.5.dp else 1.dp,
                                                            color = if (isSelected) Color(0xFFFF0000) else Color(0xFF444444),
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .clickable { selectedThumbnailUri = photoPath }
                                                ) {
                                                    AsyncImage(
                                                        model = photoPath,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier.fillMaxSize()
                                                    )
                                                    if (isSelected) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(20.dp)
                                                                .align(Alignment.TopEnd)
                                                                .background(Color(0xFFFF0000), RoundedCornerShape(bottomStart = 8.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Video Title Field
                        item {
                            Column {
                                Text("Video Title *", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                TextField(
                                    value = publishTitle,
                                    onValueChange = { publishTitle = it },
                                    placeholder = { Text("e.g., Official Music Video, Studio Session Ep. 1", color = Color.Gray, fontSize = 13.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF1E1E1E),
                                        unfocusedContainerColor = Color(0xFF1E1E1E),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = Color(0xFFFF0000),
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        // Video Type Selector
                        item {
                            Column {
                                Text("Video Format", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                val types = listOf(
                                    "MUSIC_VIDEO" to "Music Video",
                                    "VLOG" to "Studio Vlog",
                                    "LIVE_PERFORMANCE" to "Live Show",
                                    "INTERVIEW" to "BTS / Interview"
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    types.forEach { (typeKey, label) ->
                                        val isSel = publishType == typeKey
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) Color(0xFFFF0000) else Color(0xFF1E1E1E))
                                                .clickable { publishType = typeKey }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Category Selector
                        item {
                            Column {
                                Text("Content Category", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                val cats = listOf("Music & Art", "Lifestyle & Vlogs", "Gaming & Entertainment", "Tech & Gear", "Finance & Hustle")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(cats) { cat ->
                                        val isSel = publishCategory == cat
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(if (isSel) Color(0xFFFFD600) else Color(0xFF222222))
                                                .clickable { publishCategory = cat }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = cat,
                                                color = if (isSel) Color.Black else Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Production Cost & Upload Button
                        item {
                            val cost = 150.0
                            val energyRequired = if (state.activePhone == "Xiaomax") 8 else 15
                            val hasEnoughCash = state.cash >= cost
                            val hasEnoughEnergy = state.energy >= energyRequired

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Production Budget: $150", color = if (hasEnoughCash) Color.White else Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text("Energy Required: $energyRequired", color = if (hasEnoughEnergy) Color.White else Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(
                                        "Cash: $${String.format("%,.0f", state.cash)}",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (publishTitle.isNotBlank() && hasEnoughCash && hasEnoughEnergy) {
                                        viewModel.uploadMeTubeVideo(
                                            title = publishTitle,
                                            type = publishType,
                                            customThumbnail = selectedThumbnailUri,
                                            category = publishCategory
                                        )
                                        publishTitle = ""
                                        selectedThumbnailUri = null
                                        activeTab = "your_videos"
                                    } else if (publishTitle.isBlank()) {
                                        viewModel.showToast("Please enter a video title!")
                                    }
                                },
                                enabled = publishTitle.isNotBlank() && hasEnoughCash && hasEnoughEnergy,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Publish to MeTube", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }

        // Authentic YouTube Bottom Navigation Bar
        Surface(
            color = Color(0xFF0F0F0F),
            border = BorderStroke(0.5.dp, Color(0xFF262626)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { activeTab = "home" }.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (activeTab == "home") Icons.Default.Home else Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (activeTab == "home") Color.White else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                    Text("Home", color = if (activeTab == "home") Color.White else Color.Gray, fontSize = 10.sp, fontWeight = if (activeTab == "home") FontWeight.Bold else FontWeight.Normal)
                }

                // Create Tab (Centered Add Icon)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { activeTab = "create" }.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (activeTab == "create") Color(0xFFFF0000) else Color(0xFF2B2B2B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text("Create", color = if (activeTab == "create") Color(0xFFFF0000) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                // Your Page / Videos Tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { activeTab = "your_videos" }.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = "Your Videos",
                        tint = if (activeTab == "your_videos") Color.White else Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
                    Text("Your Page", color = if (activeTab == "your_videos") Color.White else Color.Gray, fontSize = 10.sp, fontWeight = if (activeTab == "your_videos") FontWeight.Bold else FontWeight.Normal)
                }
            }
        }

        // YouTube Interactive Video Watching Modal
        activeWatchingVideo?.let { video ->
            Dialog(onDismissRequest = { activeWatchingVideo = null }) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.96f).fillMaxHeight(0.85f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF333333))
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 16:9 Video Player Viewport
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            val isLocalImage = video.thumbnailId.startsWith("file://") ||
                                    video.thumbnailId.startsWith("/") ||
                                    video.thumbnailId.startsWith("content://") ||
                                    video.thumbnailId.startsWith("http")

                            if (isLocalImage) {
                                AsyncImage(
                                    model = video.thumbnailId,
                                    contentDescription = video.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.verticalGradient(listOf(Color(0xFF283593), Color.Black))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(video.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }

                            // Center Play Button
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.65f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                            }

                            // Close Button
                            IconButton(
                                onClick = { activeWatchingVideo = null },
                                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }

                            // Bottom scrubber bar
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(Color.Red)
                            )
                        }

                        // Video Title and Stats
                        LazyColumn(modifier = Modifier.weight(1f).padding(14.dp)) {
                            item {
                                Text(video.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${formatViews(video.views)} views • Year ${video.yearPublished} Wk ${video.weekPublished}",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Channel Row with Subscribe Button
                                val author = video.artistName ?: state.artistName
                                val isSubscribed = isSubscribedMap[author] ?: false
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF3F51B5)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(author.take(1).uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(author, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Creator Channel", color = Color.Gray, fontSize = 10.sp)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            isSubscribedMap = isSubscribedMap + (author to !isSubscribed)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSubscribed) Color(0xFF333333) else Color.White
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                                    ) {
                                        Text(
                                            if (isSubscribed) "Subscribed" else "Subscribe",
                                            color = if (isSubscribed) Color.White else Color.Black,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Interactive Likes & Engagement Buttons
                                val isLiked = likedVideoIds.contains(video.id)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            likedVideoIds = if (isLiked) likedVideoIds - video.id else likedVideoIds + video.id
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isLiked) Color(0xFFFF0000) else Color(0xFF262626)
                                        ),
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("${formatViews(video.likes + (if (isLiked) 1 else 0))}", fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = { viewModel.showToast("Video shared to MeTube link!") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFF262626))
                                Spacer(modifier = Modifier.height(12.dp))

                                // Comments Section
                                val allComments = (video.comments + (localVideoComments[video.id] ?: emptyList())).distinct()
                                Text("Comments (${allComments.size})", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Add Comment input
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextField(
                                        value = newCommentText,
                                        onValueChange = { newCommentText = it },
                                        placeholder = { Text("Add a comment...", color = Color.Gray, fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF222222),
                                            unfocusedContainerColor = Color(0xFF222222),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = {
                                            if (newCommentText.isNotBlank()) {
                                                val existing = localVideoComments[video.id] ?: emptyList()
                                                localVideoComments = localVideoComments + (video.id to (existing + "${state.artistName}: $newCommentText"))
                                                newCommentText = ""
                                            }
                                        }
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFFFF0000), modifier = Modifier.size(20.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                allComments.forEach { comment ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = comment,
                                            color = Color.LightGray,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(10.dp)
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

// =================== 4. WESHARE STOCK BROKERAGE SCREEN ===================
@Composable
fun WeShareExchangeScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedStockId by remember { mutableStateOf<String?>(null) }
    var inputPrice by remember { mutableStateOf("") }
    var inputQty by remember { mutableStateOf("10") }
    var instantQtyInput by remember { mutableStateOf("10") }
    var instantSellQtyInput by remember { mutableStateOf("10") }
    var orderType by remember { mutableStateOf("LIMIT_BUY") }
    var activeTradingTab by remember { mutableStateOf("MARKET") } // "MARKET" or "LIMIT"

    val totalStockValue = state.stocks.sumOf { it.quantity * it.currentPrice }
    val totalStockCost = state.stocks.sumOf { it.quantity * (if (it.averagePurchasePrice > 0.0) it.averagePurchasePrice else it.currentPrice) }
    val totalStockPnl = totalStockValue - totalStockCost
    val totalStockPnlPercent = if (totalStockCost > 0.0) (totalStockPnl / totalStockCost) * 100.0 else 0.0

    if (selectedStockId == null) {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF040D14))) {
            // Header & Portfolio P&L Summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WeShare Stock Exchange",
                            color = Color.Gray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$${String.format("%,.2f", totalStockValue)}",
                            color = Color(0xFF00C805),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        val pnlSign = if (totalStockPnl >= 0.0) "+" else ""
                        val pnlColor = if (totalStockPnl >= 0.0) Color(0xFF00C805) else Color(0xFFFF5252)
                        Text(
                            text = "$pnlSign$${String.format("%,.2f", totalStockPnl)} ($pnlSign${String.format("%.1f", totalStockPnlPercent)}%)",
                            color = pnlColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unrealized P&L",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111C24), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Available Cash: $${String.format("%,.2f", state.cash)}", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Cost Basis: $${String.format("%,.2f", totalStockCost)}", color = Color.Gray, fontSize = 11.sp)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Equities & Market Assets",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                    )
                }

                items(state.stocks) { stock ->
                    val ownedQty = stock.quantity
                    val avgBuy = if (stock.averagePurchasePrice > 0.0) stock.averagePurchasePrice else stock.currentPrice
                    val holdingVal = ownedQty * stock.currentPrice
                    val holdingCost = ownedQty * avgBuy
                    val holdingPnl = holdingVal - holdingCost
                    val holdingPnlPercent = if (holdingCost > 0.0) (holdingPnl / holdingCost) * 100.0 else 0.0
                    val isPos = holdingPnl >= 0.0

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111C24)),
                        shape = RoundedCornerShape(12.dp),
                        border = if (ownedQty > 0) BorderStroke(1.dp, if (isPos) Color(0xFF00C805).copy(alpha = 0.4f) else Color(0xFFFF5252).copy(alpha = 0.4f)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedStockId = stock.id
                                inputPrice = String.format("%.2f", stock.currentPrice)
                                inputQty = "10"
                                instantQtyInput = "10"
                                instantSellQtyInput = if (ownedQty > 0) ownedQty.toString() else "10"
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stock.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF1E2E3D), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "Div: ${(stock.weeklyDividendPercent * 100).toInt()}%/wk",
                                                color = Color(0xFF00E676),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (stock.isCheapOpportunity) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "🎯 VALUE BUY",
                                                    color = Color(0xFFFFD700),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Health: ${stock.financialHealth} • Floor: $${String.format("%.2f", if (stock.initialPrice > 0.0) stock.initialPrice else stock.currentPrice)}",
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                    if (stock.analystPrediction.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val predColor = if (stock.analystPrediction == "RISE") Color(0xFF00E676) else Color(0xFFFF5252)
                                            val predIcon = if (stock.analystPrediction == "RISE") "▲" else "▼"
                                            Text(
                                                text = "70/30 Intel: $predIcon ${stock.analystPrediction} (${stock.predictionConfidence}%)",
                                                color = predColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%.2f", stock.currentPrice)}",
                                        color = Color(0xFF00C805),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Trade & Buy 📈",
                                        color = Color(0xFF00E676),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (ownedQty > 0) {
                                Divider(color = Color(0xFF1E2A34), thickness = 0.5.dp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Owned: $ownedQty shares (Avg $${String.format("%.2f", avgBuy)})",
                                        color = Color.LightGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    val sign = if (isPos) "+" else ""
                                    Text(
                                        text = "P&L: $sign$${String.format("%.2f", holdingPnl)} ($sign${String.format("%.1f", holdingPnlPercent)}%)",
                                        color = if (isPos) Color(0xFF00C805) else Color(0xFFFF5252),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Active Standing Orders",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp, start = 4.dp)
                    )
                }

                if (state.pendingStockOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFF111C24), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No active limit orders. Trade instantly above!", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                } else {
                    items(state.pendingStockOrders) { order ->
                        val stock = state.stocks.find { it.id == order.stockId }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111C24)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${order.type.replace('_', ' ')} • ${stock?.name ?: "Stock"}",
                                        color = if (order.type.contains("BUY")) Color(0xFF00C805) else Color(0xFFFF5252),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${order.quantity} shares @ target $${String.format("%.2f", order.targetPrice)}",
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                                IconButton(onClick = { viewModel.cancelStockOrder(order.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                                }
                    }
                }
            }
        }
    }
}
    } else {
        // Individual Stock Detail & Trade Order view
        val stockId = selectedStockId!!
        val stock = state.stocks.find { it.id == stockId } ?: state.stocks[0]
        val ownedQty = stock.quantity
        val avgBuy = if (stock.averagePurchasePrice > 0.0) stock.averagePurchasePrice else stock.currentPrice
        val holdingVal = ownedQty * stock.currentPrice
        val holdingCost = ownedQty * avgBuy
        val holdingPnl = holdingVal - holdingCost
        val holdingPnlPercent = if (holdingCost > 0.0) (holdingPnl / holdingCost) * 100.0 else 0.0
        val isPos = holdingPnl >= 0.0

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF040D14))) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedStockId = null }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stock.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Market: $${String.format("%.2f", stock.currentPrice)} • Div: ${(stock.weeklyDividendPercent * 100).toInt()}%/wk", color = Color(0xFF00C805), fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF111C24), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Cash: $${String.format("%,.0f", state.cash)}", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Position & PnL Summary Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111C24)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (ownedQty > 0 && isPos) Color(0xFF00C805).copy(alpha = 0.5f) else Color(0xFF1E2A34)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Your Investment Position", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                val status = if (ownedQty > 0) "$ownedQty Shares Owned" else "No Shares Held"
                                Text(status, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (ownedQty > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Avg Buy: $${String.format("%.2f", avgBuy)}", color = Color.LightGray, fontSize = 11.sp)
                                        Text("Total Cost: $${String.format("%,.2f", holdingCost)}", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Current Value: $${String.format("%,.2f", holdingVal)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        val sign = if (isPos) "+" else ""
                                        Text("Profit/Loss: $sign$${String.format("%.2f", holdingPnl)} ($sign${String.format("%.1f", holdingPnlPercent)}%)", color = if (isPos) Color(0xFF00C805) else Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 10-Week Candlestick Chart
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111C24)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(130.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
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
                                    val normHeight = (((price - minVal) / denom) * 75 + 15).dp
                                    val barColor = if (price >= displayList.first()) Color(0xFF00C805) else Color(0xFFFF5252)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(modifier = Modifier.width(2.dp).height(6.dp).background(barColor.copy(alpha = 0.5f)))
                                        Box(
                                            modifier = Modifier
                                                .width(16.dp)
                                                .height(normHeight)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(barColor)
                                        )
                                        Box(modifier = Modifier.width(2.dp).height(6.dp).background(barColor.copy(alpha = 0.5f)))
                                    }
                                }
                            }
                            Text(
                                text = "10-Week Simulated Price Action",
                                color = Color.Gray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.TopStart)
                            )
                        }
                    }
                }

                // Trading Modes Toggle (Instant Market vs Limit Order)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MARKET" to "⚡ Instant Market Trade", "LIMIT" to "🎯 Limit Standing Order").forEach { (tabKey, tabLabel) ->
                            val isSel = activeTradingTab == tabKey
                            Button(
                                onClick = { activeTradingTab = tabKey },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSel) Color(0xFF00C805) else Color(0xFF111C24),
                                    contentColor = if (isSel) Color.Black else Color.LightGray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text(tabLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (activeTradingTab == "MARKET") {
                    // Manual Instant Market Buy / Sell
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111C24)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("⚡ Instant Market Execution", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                // BUY SECTION
                                Text("Buy Shares @ Live Market Price ($${String.format("%.2f", stock.currentPrice)})", color = Color(0xFF00C805), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1, 5, 10, 25, 50).forEach { presetQty ->
                                        val cost = presetQty * stock.currentPrice
                                        val canAfford = state.cash >= cost
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(if (canAfford) Color(0xFF1B2A34) else Color(0xFF141C22), RoundedCornerShape(6.dp))
                                                .clickable(enabled = canAfford) { instantQtyInput = presetQty.toString() }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("+$presetQty", color = if (canAfford) Color.White else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextField(
                                        value = instantQtyInput,
                                        onValueChange = { instantQtyInput = it },
                                        label = { Text("Shares Qty") },
                                        modifier = Modifier.weight(1f),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF1B2A34),
                                            unfocusedContainerColor = Color(0xFF1B2A34),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedIndicatorColor = Color(0xFF00C805)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    val buyQty = instantQtyInput.toIntOrNull() ?: 0
                                    val estCost = buyQty * stock.currentPrice
                                    val canBuy = buyQty > 0 && state.cash >= estCost

                                    Button(
                                        onClick = {
                                            if (canBuy) {
                                                viewModel.buyStock(stock.id, buyQty)
                                            }
                                        },
                                        enabled = canBuy,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C805), contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(54.dp)
                                    ) {
                                        Text("BUY NOW\n$" + String.format("%,.0f", estCost), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }

                                // SELL SECTION (if owned)
                                if (ownedQty > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider(color = Color(0xFF1E2A34), thickness = 0.5.dp)
                                    Text("Sell Shares @ Live Market Price ($${String.format("%.2f", stock.currentPrice)})", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("25%" to 0.25, "50%" to 0.50, "100%" to 1.0).forEach { (label, ratio) ->
                                            val qtyToSet = (ownedQty * ratio).toInt().coerceAtLeast(1)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(Color(0xFF2A1C1C), RoundedCornerShape(6.dp))
                                                    .clickable { instantSellQtyInput = qtyToSet.toString() }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(label, color = Color(0xFFFF8A80), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextField(
                                            value = instantSellQtyInput,
                                            onValueChange = { instantSellQtyInput = it },
                                            label = { Text("Shares to Sell") },
                                            modifier = Modifier.weight(1f),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color(0xFF1B2A34),
                                                unfocusedContainerColor = Color(0xFF1B2A34),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedIndicatorColor = Color(0xFFFF5252)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        val sellQty = instantSellQtyInput.toIntOrNull() ?: 0
                                        val estPayout = sellQty * stock.currentPrice
                                        val canSell = sellQty in 1..ownedQty

                                        Button(
                                            onClick = {
                                                if (canSell) {
                                                    viewModel.sellStock(stock.id, sellQty)
                                                }
                                            },
                                            enabled = canSell,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252), contentColor = Color.White),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(54.dp)
                                        ) {
                                            Text("SELL NOW\n+$" + String.format("%,.0f", estPayout), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                    }
                }
            }
        }
    }
} else {
                    // Standing Limit Order Panel
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111C24)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "Place Automated Standing Limit Order", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    listOf("LIMIT_BUY", "LIMIT_SELL").forEach { type ->
                                        val isSel = orderType == type
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSel) Color(0xFF00C805) else Color(0xFF1B2A34)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { orderType = type }
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = type.replace("_", " "),
                                                    color = if (isSel) Color.Black else Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                TextField(
                                    value = inputPrice,
                                    onValueChange = { inputPrice = it },
                                    label = { Text("Target Trigger Price ($)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF1B2A34),
                                        unfocusedContainerColor = Color(0xFF1B2A34),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = Color(0xFF00C805)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                TextField(
                                    value = inputQty,
                                    onValueChange = { inputQty = it },
                                    label = { Text("Shares Quantity") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF1B2A34),
                                        unfocusedContainerColor = Color(0xFF1B2A34),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = Color(0xFF00C805)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
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
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C805), contentColor = Color.Black),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                ) {
                                    Text("CONFIRM STANDING ORDER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                    }
                }
            }
        }
    }
}
    }
}

// =================== 5. GENESIS CRYPTO SCREEN ===================
@Composable
fun GenesisExchangeScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedCryptoId by remember { mutableStateOf<String?>(null) }
    var inputPrice by remember { mutableStateOf("") }
    var inputQty by remember { mutableStateOf("0.5") }
    var instantUsdInput by remember { mutableStateOf("250") }
    var instantSellQtyInput by remember { mutableStateOf("0.1") }
    var orderType by remember { mutableStateOf("LIMIT_BUY") }
    var activeTradingTab by remember { mutableStateOf("MARKET") } // "MARKET" or "LIMIT"

    val totalCryptoValue = state.cryptos.sumOf { it.quantity * it.currentPrice }
    val totalCryptoCost = state.cryptos.sumOf { it.quantity * (if (it.averagePurchasePrice > 0.0) it.averagePurchasePrice else it.currentPrice) }
    val totalCryptoPnl = totalCryptoValue - totalCryptoCost
    val totalCryptoPnlPercent = if (totalCryptoCost > 0.0) (totalCryptoPnl / totalCryptoCost) * 100.0 else 0.0

    if (selectedCryptoId == null) {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0814))) {
            // Header & Portfolio P&L Summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Genesis Crypto Markets",
                            color = Color(0xFFB388FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$${String.format("%,.2f", totalCryptoValue)}",
                            color = Color(0xFF00E5FF),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        val pnlSign = if (totalCryptoPnl >= 0.0) "+" else ""
                        val pnlColor = if (totalCryptoPnl >= 0.0) Color(0xFF00E676) else Color(0xFFFF5252)
                        Text(
                            text = "$pnlSign$${String.format("%,.2f", totalCryptoPnl)} ($pnlSign${String.format("%.1f", totalCryptoPnlPercent)}%)",
                            color = pnlColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unrealized P&L",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF141026), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Available Cash: $${String.format("%,.2f", state.cash)}", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Cost Basis: $${String.format("%,.2f", totalCryptoCost)}", color = Color.Gray, fontSize = 11.sp)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Cryptographic Assets",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                    )
                }

                items(state.cryptos) { crypto ->
                    val ownedQty = crypto.quantity
                    val avgBuy = if (crypto.averagePurchasePrice > 0.0) crypto.averagePurchasePrice else crypto.currentPrice
                    val holdingVal = ownedQty * crypto.currentPrice
                    val holdingCost = ownedQty * avgBuy
                    val holdingPnl = holdingVal - holdingCost
                    val holdingPnlPercent = if (holdingCost > 0.0) (holdingPnl / holdingCost) * 100.0 else 0.0
                    val isPos = holdingPnl >= 0.0

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141026)),
                        shape = RoundedCornerShape(12.dp),
                        border = if (ownedQty > 0.0) BorderStroke(1.dp, if (isPos) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFFFF5252).copy(alpha = 0.5f)) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCryptoId = crypto.id
                                inputPrice = String.format("%.2f", crypto.currentPrice)
                                inputQty = "0.5"
                                instantUsdInput = "250"
                                instantSellQtyInput = if (ownedQty > 0.0) String.format("%.4f", ownedQty) else "0.1"
                            }
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = crypto.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF261947), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = crypto.volatilityCategory(),
                                                color = when (crypto.volatilityCategory()) {
                                                    "Big Shift" -> Color(0xFFFF4081)
                                                    "Medium Wave" -> Color(0xFFFFD700)
                                                    else -> Color(0xFFB388FF)
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (crypto.isCheapOpportunity) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF00E5FF).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "🎯 CHEAP ENTRY",
                                                    color = Color(0xFF00E5FF),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Cap: $" + String.format("%,.0f", crypto.marketCap),
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                    if (crypto.analystPrediction.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            val predColor = if (crypto.analystPrediction == "RISE") Color(0xFF00E676) else Color(0xFFFF5252)
                                            val predIcon = if (crypto.analystPrediction == "RISE") "▲" else "▼"
                                            Text(
                                                text = "70/30 Intel: $predIcon ${crypto.analystPrediction} (${crypto.predictionConfidence}%)",
                                                color = predColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%.2f", crypto.currentPrice)}",
                                        color = Color(0xFF00E5FF),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "Trade & Swap 🪙",
                                        color = Color(0xFF7C4DFF),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (ownedQty > 0.0) {
                                Divider(color = Color(0xFF241B42), thickness = 0.5.dp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Holding: ${String.format("%.4f", ownedQty)} (Avg $${String.format("%.2f", avgBuy)})",
                                        color = Color.LightGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    val sign = if (isPos) "+" else ""
                                    Text(
                                        text = "P&L: $sign$${String.format("%.2f", holdingPnl)} ($sign${String.format("%.1f", holdingPnlPercent)}%)",
                                        color = if (isPos) Color(0xFF00E676) else Color(0xFFFF5252),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Pending Automated Crypto Rules",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp, bottom = 2.dp, start = 4.dp)
                    )
                }

                if (state.pendingCryptoOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFF141026), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "No active automated crypto rules. Swap coins above!", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                } else {
                    items(state.pendingCryptoOrders) { order ->
                        val crypto = state.cryptos.find { it.id == order.cryptoId }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141026)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${order.type.replace('_', ' ')} • ${crypto?.name ?: "Crypto"}",
                                        color = if (order.type.contains("BUY")) Color(0xFF00E5FF) else Color(0xFFFF5252),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${String.format("%.4f", order.quantity)} coins @ trigger $${String.format("%.2f", order.targetPrice)}",
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                                IconButton(onClick = { viewModel.cancelCryptoOrder(order.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                                }
                    }
                }
            }
        }
    }
}
    } else {
        // Individual Crypto Detail & Instant Swap / Limit view
        val cryptoId = selectedCryptoId!!
        val crypto = state.cryptos.find { it.id == cryptoId } ?: state.cryptos[0]
        val ownedQty = crypto.quantity
        val avgBuy = if (crypto.averagePurchasePrice > 0.0) crypto.averagePurchasePrice else crypto.currentPrice
        val holdingVal = ownedQty * crypto.currentPrice
        val holdingCost = ownedQty * avgBuy
        val holdingPnl = holdingVal - holdingCost
        val holdingPnlPercent = if (holdingCost > 0.0) (holdingPnl / holdingCost) * 100.0 else 0.0
        val isPos = holdingPnl >= 0.0

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0814))) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedCryptoId = null }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = crypto.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Spot: $${String.format("%.2f", crypto.currentPrice)} • Activity: ${crypto.developerActivity}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF141026), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Cash: $${String.format("%,.0f", state.cash)}", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Position & PnL Summary Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141026)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (ownedQty > 0.0 && isPos) Color(0xFF00E676).copy(alpha = 0.5f) else Color(0xFF241B42)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Your Crypto Position", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                val status = if (ownedQty > 0.0) "${String.format("%.4f", ownedQty)} Coins Owned" else "No Coins Held"
                                Text(status, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (ownedQty > 0.0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Avg Buy: $${String.format("%.2f", avgBuy)}", color = Color.LightGray, fontSize = 11.sp)
                                        Text("Total Cost: $${String.format("%,.2f", holdingCost)}", color = Color.Gray, fontSize = 10.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Current Value: $${String.format("%,.2f", holdingVal)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        val sign = if (isPos) "+" else ""
                                        Text("Profit/Loss: $sign$${String.format("%.2f", holdingPnl)} ($sign${String.format("%.1f", holdingPnlPercent)}%)", color = if (isPos) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 10-Week Candlestick Visualizer
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141026)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(130.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                val hist = crypto.history.takeLast(10)
                                val displayList = if (hist.size < 5) listOf(100.0, 120.0, 110.0, 140.0, crypto.currentPrice) else hist
                                val maxVal = displayList.maxOrNull() ?: 1.0
                                val minVal = displayList.minOrNull() ?: 0.1
                                val denom = (maxVal - minVal).coerceAtLeast(1.0)

                                displayList.forEach { price ->
                                    val normHeight = (((price - minVal) / denom) * 75 + 15).dp
                                    val barColor = if (price >= displayList.first()) Color(0xFF00E5FF) else Color(0xFFFF5252)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(modifier = Modifier.width(2.dp).height(6.dp).background(barColor.copy(alpha = 0.5f)))
                                        Box(
                                            modifier = Modifier
                                                .width(16.dp)
                                                .height(normHeight)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(barColor)
                                        )
                                        Box(modifier = Modifier.width(2.dp).height(6.dp).background(barColor.copy(alpha = 0.5f)))
                                    }
                                }
                            }
                            Text(
                                text = "Genesis 10-Week Candlesticks",
                                color = Color.Gray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.align(Alignment.TopStart)
                            )
                        }
                    }
                }

                // Trading Modes Toggle (Instant Swap vs Limit Order)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MARKET" to "⚡ Instant Market Swap", "LIMIT" to "🎯 Automated Limit Rule").forEach { (tabKey, tabLabel) ->
                            val isSel = activeTradingTab == tabKey
                            Button(
                                onClick = { activeTradingTab = tabKey },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSel) Color(0xFF00E5FF) else Color(0xFF141026),
                                    contentColor = if (isSel) Color.Black else Color.LightGray
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text(tabLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (activeTradingTab == "MARKET") {
                    // Manual Instant Buy / Sell
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141026)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("⚡ Instant Crypto Swap", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                // BUY SECTION
                                Text("Swap Cash for Coins @ Spot ($${String.format("%.2f", crypto.currentPrice)})", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(50, 250, 1000, 5000).forEach { presetUsd ->
                                        val canAfford = state.cash >= presetUsd
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(if (canAfford) Color(0xFF261947) else Color(0xFF19132D), RoundedCornerShape(6.dp))
                                                .clickable(enabled = canAfford) { instantUsdInput = presetUsd.toString() }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("$$presetUsd", color = if (canAfford) Color.White else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextField(
                                        value = instantUsdInput,
                                        onValueChange = { instantUsdInput = it },
                                        label = { Text("USD Amount ($)") },
                                        modifier = Modifier.weight(1f),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFF261947),
                                            unfocusedContainerColor = Color(0xFF261947),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedIndicatorColor = Color(0xFF00E5FF)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    val usdAmount = instantUsdInput.toDoubleOrNull() ?: 0.0
                                    val canBuy = usdAmount > 0.0 && state.cash >= usdAmount
                                    val estCoins = if (crypto.currentPrice > 0.0) usdAmount / crypto.currentPrice else 0.0

                                    Button(
                                        onClick = {
                                            if (canBuy) {
                                                viewModel.buyCrypto(crypto.id, usdAmount)
                                            }
                                        },
                                        enabled = canBuy,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(54.dp)
                                    ) {
                                        Text("SWAP NOW\n≈ " + String.format("%.3f", estCoins), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }

                                // SELL SECTION (if owned)
                                if (ownedQty > 0.0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Divider(color = Color(0xFF241B42), thickness = 0.5.dp)
                                    Text("Liquidate Coins for Cash @ Spot ($${String.format("%.2f", crypto.currentPrice)})", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("25%" to 0.25, "50%" to 0.50, "100%" to 1.0).forEach { (label, ratio) ->
                                            val qtyToSet = ownedQty * ratio
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(Color(0xFF2E1524), RoundedCornerShape(6.dp))
                                                    .clickable { instantSellQtyInput = String.format(java.util.Locale.US, "%.4f", qtyToSet) }
                                                    .padding(vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(label, color = Color(0xFFFF8A80), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextField(
                                            value = instantSellQtyInput,
                                            onValueChange = { instantSellQtyInput = it },
                                            label = { Text("Coins to Sell") },
                                            modifier = Modifier.weight(1f),
                                            colors = TextFieldDefaults.colors(
                                                focusedContainerColor = Color(0xFF261947),
                                                unfocusedContainerColor = Color(0xFF261947),
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedIndicatorColor = Color(0xFFFF5252)
                                            ),
                                            shape = RoundedCornerShape(8.dp)
                                        )

                                        val sellQty = instantSellQtyInput.toDoubleOrNull() ?: 0.0
                                        val estPayout = sellQty * crypto.currentPrice
                                        val canSell = sellQty > 0.0 && sellQty <= ownedQty + 0.0001

                                        Button(
                                            onClick = {
                                                if (canSell) {
                                                    viewModel.sellCrypto(crypto.id, sellQty.coerceAtMost(ownedQty))
                                                }
                                            },
                                            enabled = canSell,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252), contentColor = Color.White),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(54.dp)
                                        ) {
                                            Text("SELL NOW\n+$" + String.format("%,.0f", estPayout), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                    }
                }
            }
        }
    }
} else {
                    // Standing Limit Order Panel
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF141026)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "Configure Automated Trigger Rule", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    listOf("LIMIT_BUY", "LIMIT_SELL").forEach { type ->
                                        val isSel = orderType == type
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSel) Color(0xFF00E5FF) else Color(0xFF261947)
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { orderType = type }
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = type.replace("_", " "),
                                                    color = if (isSel) Color.Black else Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                TextField(
                                    value = inputPrice,
                                    onValueChange = { inputPrice = it },
                                    label = { Text("Target Trigger Price ($)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF261947),
                                        unfocusedContainerColor = Color(0xFF261947),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = Color(0xFF00E5FF)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                )

                                TextField(
                                    value = inputQty,
                                    onValueChange = { inputQty = it },
                                    label = { Text("Coins Quantity") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color(0xFF261947),
                                        unfocusedContainerColor = Color(0xFF261947),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedIndicatorColor = Color(0xFF00E5FF)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
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
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black),
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                ) {
                                    Text("CONFIRM AUTOMATED RULE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                    }
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
    var selectedTab by remember { mutableStateOf("HIRE") } // "HIRE" or "ROSTER"
    var expandedEmployeeId by remember { mutableStateOf<String?>(null) }
    var selectedProfessionFilter by remember { mutableStateOf("All Roles") }
    var selectedSkillFilter by remember { mutableStateOf("All Skills") }
    var candidateToHire by remember { mutableStateOf<Employee?>(null) }

    val fullRoster = if (state.candidateRoster.isEmpty() || state.candidateRoster.any { it.weeklySalary < 500.0 }) {
        GameData.generateRealisticCandidateRoster()
    } else {
        state.candidateRoster
    }

    val allProfessions = listOf(
        "All Roles",
        "Accountant",
        "Crypto Analyst",
        "Financial Advisor",
        "Talent Agent",
        "PR Manager",
        "Business Manager",
        "Lawyer",
        "Tour Manager",
        "Music Producer",
        "Personal Assistant",
        "Security",
        "Social Media Manager",
        "Vocal Coach / Sound Engineer"
    )

    val skillFilters = listOf("All Skills", "Elite (80%+)", "Experienced (60-79%)", "Junior (<60%)")

    val filteredCandidates = fullRoster.filter { cand ->
        val matchesRole = selectedProfessionFilter == "All Roles" || cand.role == selectedProfessionFilter
        val matchesSkill = when (selectedSkillFilter) {
            "Elite (80%+)" -> cand.skillRatingPercent >= 80
            "Experienced (60-79%)" -> cand.skillRatingPercent in 60..79
            "Junior (<60%)" -> cand.skillRatingPercent < 60
            else -> true
        }
        matchesRole && matchesSkill
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F141A))) {
        // Staff Hub Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Staff Hub & Agency",
                        color = Color(0xFF1DBF73),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Realistic salaries & skill-driven performance",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1E2732), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Cash: $${String.format("%,.0f", state.cash)}",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("HIRE" to "Hire Talent (${filteredCandidates.size})", "ROSTER" to "My Roster (${state.hiredEmployees.size})").forEach { (tabKey, tabLabel) ->
                    val isSel = selectedTab == tabKey
                    Button(
                        onClick = { selectedTab = tabKey },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSel) Color(0xFF1DBF73) else Color(0xFF1E2732),
                            contentColor = if (isSel) Color.Black else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(38.dp)
                    ) {
                        Text(tabLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (selectedTab == "ROSTER") {
            // My Hired Staff Roster
            if (state.hiredEmployees.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("No staff currently hired.", color = Color.Gray, fontSize = 14.sp)
                        Text("Switch to 'Hire Talent' tab to recruit accountants, crypto analysts, and personal assistants.", color = Color.DarkGray, fontSize = 11.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Social Media Manager Automation Card (Strictly only shown if SMM or PR Manager is hired)
                    val hasSmm = state.hiredEmployees.any { it.role.equals("Social Media Manager", ignoreCase = true) || it.role.equals("PR Manager", ignoreCase = true) }
                    if (hasSmm) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF16202C)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF1D9BF0).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("📱 Social Media Manager Automation", color = Color(0xFF1D9BF0), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Select automation niche & manage auto-posts", color = Color.Gray, fontSize = 10.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF00E676).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("Staff Active", color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Text("SMM Niche Focus:", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    val smmCategories = listOf("Music & Art", "Tech & Gadgets", "Fashion & Lifestyle", "Gaming & Entertainment", "Fitness & Wellness", "Business & Finance")
                                    Row(
                                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        smmCategories.forEach { cat ->
                                            val isSel = (state.castNicheFocus == cat || state.smmPostCategory == cat)
                                            FilterChip(
                                                selected = isSel,
                                                onClick = {
                                                    viewModel.setCastNicheFocus(cat)
                                                    viewModel.setSmmPostCategory(cat)
                                                },
                                                label = { Text(cat, fontSize = 10.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF1D9BF0),
                                                    selectedLabelColor = Color.White,
                                                    containerColor = Color(0xFF1E2732),
                                                    labelColor = Color.LightGray
                                                )
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Auto-Post Weekly Social Updates", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                            Text("Fosters weekly fan engagement in chosen niche", color = Color.Gray, fontSize = 9.sp)
                                        }
                                        Switch(
                                            checked = state.smmAutoPost,
                                            onCheckedChange = { viewModel.setSmmAutoPost(!state.smmAutoPost) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = Color(0xFF1DBF73)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Crypto Analyst Granular Delegation Card
                    val hasCryptoAnalyst = state.hiredEmployees.any { it.role.equals("Crypto Analyst", ignoreCase = true) }
                    if (hasCryptoAnalyst) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFA000).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("⚡ Crypto Analyst Delegation Strategy", color = Color(0xFFFFA000), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Set bank balance allowance, profit target & stop loss", color = Color.Gray, fontSize = 10.sp)
                                        }
                                        Switch(
                                            checked = state.cryptoAutoInvestEnabled,
                                            onCheckedChange = {
                                                viewModel.setCryptoAutoInvest(it, state.cryptoAutoInvestPercent, state.cryptoAutoSellProfitMargin, state.cryptoStopLossMargin)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = Color(0xFFFFA000)
                                            )
                                        )
                                    }

                                    if (state.cryptoAutoInvestEnabled) {
                                        // Bank balance allocation percentage
                                        Text("Bank Balance Allocation: ${(state.cryptoAutoInvestPercent * 100).toInt()}%", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f).forEach { pct ->
                                                val isSel = (state.cryptoAutoInvestPercent * 100).toInt() == (pct * 100).toInt()
                                                FilterChip(
                                                    selected = isSel,
                                                    onClick = { viewModel.setCryptoAutoInvest(true, pct, state.cryptoAutoSellProfitMargin, state.cryptoStopLossMargin) },
                                                    label = { Text("${(pct * 100).toInt()}%", fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFFFFA000),
                                                        selectedLabelColor = Color.Black,
                                                        containerColor = Color(0xFF25253A),
                                                        labelColor = Color.LightGray
                                                    )
                                                )
                                            }
                                        }

                                        // Profit Target Return to sell at
                                        Text("Target Profit Margin: +${(state.cryptoAutoSellProfitMargin * 100).toInt()}%", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0.10f, 0.25f, 0.50f, 1.0f, 2.0f).forEach { margin ->
                                                val isSel = (state.cryptoAutoSellProfitMargin * 100).toInt() == (margin * 100).toInt()
                                                FilterChip(
                                                    selected = isSel,
                                                    onClick = { viewModel.setCryptoAutoInvest(true, state.cryptoAutoInvestPercent, margin, state.cryptoStopLossMargin) },
                                                    label = { Text("+${(margin * 100).toInt()}%", fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFF00E676),
                                                        selectedLabelColor = Color.Black,
                                                        containerColor = Color(0xFF25253A),
                                                        labelColor = Color.LightGray
                                                    )
                                                )
                                            }
                                        }

                                        // Stop loss limit
                                        Text("Stop Loss Safeguard: -${(state.cryptoStopLossMargin * 100).toInt()}%", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0.05f, 0.10f, 0.15f, 0.20f, 0.30f).forEach { stopLoss ->
                                                val isSel = (state.cryptoStopLossMargin * 100).toInt() == (stopLoss * 100).toInt()
                                                FilterChip(
                                                    selected = isSel,
                                                    onClick = { viewModel.setCryptoAutoInvest(true, state.cryptoAutoInvestPercent, state.cryptoAutoSellProfitMargin, stopLoss) },
                                                    label = { Text("-${(stopLoss * 100).toInt()}%", fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFFF4212E),
                                                        selectedLabelColor = Color.White,
                                                        containerColor = Color(0xFF25253A),
                                                        labelColor = Color.LightGray
                                                    )
                                                )
                                            }
                    }
                }
            }
        }
    }
}

                    // 3. Accountant & Financial Advisor Delegation Card
                    val hasAccountant = state.hiredEmployees.any { it.role.equals("Accountant", ignoreCase = true) || it.role.equals("Financial Advisor", ignoreCase = true) }
                    if (hasAccountant) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF13221B)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF1DBF73).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("💼 Accountant & Equity Strategy", color = Color(0xFF1DBF73), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Auto-invest bank reserves into value equities with risk controls", color = Color.Gray, fontSize = 10.sp)
                                        }
                                        Switch(
                                            checked = state.accountantAutoInvestEnabled,
                                            onCheckedChange = {
                                                viewModel.setAccountantAutoInvest(it, state.accountantAutoInvestPercent, state.accountantAutoSellProfitMargin, state.accountantStopLossMargin)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = Color(0xFF1DBF73)
                                            )
                                        )
                                    }

                                    if (state.accountantAutoInvestEnabled) {
                                        // Bank balance allocation percentage
                                        Text("Bank Balance Allocation: ${(state.accountantAutoInvestPercent * 100).toInt()}%", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f).forEach { pct ->
                                                val isSel = (state.accountantAutoInvestPercent * 100).toInt() == (pct * 100).toInt()
                                                FilterChip(
                                                    selected = isSel,
                                                    onClick = { viewModel.setAccountantAutoInvest(true, pct, state.accountantAutoSellProfitMargin, state.accountantStopLossMargin) },
                                                    label = { Text("${(pct * 100).toInt()}%", fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFF1DBF73),
                                                        selectedLabelColor = Color.Black,
                                                        containerColor = Color(0xFF1E2E25),
                                                        labelColor = Color.LightGray
                                                    )
                                                )
                                            }
                                        }

                                        // Profit Target Return to sell at
                                        Text("Target Dividend / Profit: +${(state.accountantAutoSellProfitMargin * 100).toInt()}%", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0.05f, 0.10f, 0.20f, 0.30f, 0.50f).forEach { margin ->
                                                val isSel = (state.accountantAutoSellProfitMargin * 100).toInt() == (margin * 100).toInt()
                                                FilterChip(
                                                    selected = isSel,
                                                    onClick = { viewModel.setAccountantAutoInvest(true, state.accountantAutoInvestPercent, margin, state.accountantStopLossMargin) },
                                                    label = { Text("+${(margin * 100).toInt()}%", fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFF00E676),
                                                        selectedLabelColor = Color.Black,
                                                        containerColor = Color(0xFF1E2E25),
                                                        labelColor = Color.LightGray
                                                    )
                                                )
                                            }
                                        }

                                        // Stop loss limit
                                        Text("Stop Loss Limit: -${(state.accountantStopLossMargin * 100).toInt()}%", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0.05f, 0.10f, 0.15f, 0.20f, 0.25f).forEach { stopLoss ->
                                                val isSel = (state.accountantStopLossMargin * 100).toInt() == (stopLoss * 100).toInt()
                                                FilterChip(
                                                    selected = isSel,
                                                    onClick = { viewModel.setAccountantAutoInvest(true, state.accountantAutoInvestPercent, state.accountantAutoSellProfitMargin, stopLoss) },
                                                    label = { Text("-${(stopLoss * 100).toInt()}%", fontSize = 10.sp) },
                                                    colors = FilterChipDefaults.filterChipColors(
                                                        selectedContainerColor = Color(0xFFF4212E),
                                                        selectedLabelColor = Color.White,
                                                        containerColor = Color(0xFF1E2E25),
                                                        labelColor = Color.LightGray
                                                    )
                                                )
                                            }
                    }
                }
            }
        }
    }
}

                    // Personal Assistant Quick Action Banner
                    val hasPA = state.hiredEmployees.find { it.role == "Personal Assistant" }
                    if (hasPA != null) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2F23)),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFF1DBF73).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("👔 Personal Assistant Active", color = Color(0xFF1DBF73), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("${hasPA.name} compiles weekly executive staff briefings & manages inbox hygiene.", color = Color.LightGray, fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.cleanReadEmails() },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DBF73), contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("🧹 Clean Read Mail", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    items(state.hiredEmployees) { emp ->
                        val isExpanded = expandedEmployeeId == emp.id
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2732)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedEmployeeId = if (isExpanded) null else emp.id }
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(emp.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${emp.role} • $${String.format("%,.0f", emp.weeklySalary)}/wk (Payroll)", color = Color(0xFF1DBF73), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        val badgeColor = when {
                                            emp.skillRatingPercent >= 80 -> Color(0xFFFFB300)
                                            emp.skillRatingPercent >= 60 -> Color(0xFF00E676)
                                            else -> Color.Gray
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("${emp.skillRatingPercent}% Skill", color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        IconButton(onClick = { expandedEmployeeId = if (isExpanded) null else emp.id }, modifier = Modifier.size(24.dp)) {
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = "Expand",
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }

                                if (isExpanded) {
                                    Divider(color = Color(0xFF2C394B), thickness = 0.5.dp)
                                    Text("Competence: Intelligence ${emp.intelligence.toInt()}%, Experience ${emp.experienceYears}y, Reliability ${emp.reliability.toInt()}%, Loyalty ${emp.loyalty.toInt()}%", color = Color.LightGray, fontSize = 10.sp)

                                    // Permissions & Actions
                                    val isFinSpecialist = emp.role.contains("Crypto", ignoreCase = true) ||
                                        emp.role.contains("Accountant", ignoreCase = true) ||
                                        emp.role.contains("Financial", ignoreCase = true)
                                    val trainCost = if (isFinSpecialist) "$8.5k" else "$4.5k"

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.trainEmployee(emp.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (emp.hasStubbornTrait) Color(0xFF4A148C) else Color(0xFF2C3E50)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(if (emp.hasStubbornTrait) "Mastery ($trainCost)" else "Train ($trainCost)", color = Color.White, fontSize = 10.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.promoteEmployee(emp.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Promote (+15%)", color = Color.White, fontSize = 10.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.fireProfessional(emp.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(32.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("Terminate", color = Color.White, fontSize = 10.sp)
                                        }
                    }
                }
            }
        }
    }
}
            }
        } else {
            // HIRE TALENT TAB with Profession & Skill Filters
            Column(modifier = Modifier.fillMaxSize()) {
                // Profession Filter Carousel
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allProfessions.forEach { prof ->
                        val isSel = selectedProfessionFilter == prof
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedProfessionFilter = prof },
                            label = { Text(prof, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF1DBF73),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF1E2732),
                                labelColor = Color.LightGray
                            )
                        )
                    }
                }

                // Skill Tier Filter Row
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    skillFilters.forEach { tier ->
                        val isSel = selectedSkillFilter == tier
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedSkillFilter = tier },
                            label = { Text(tier, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFB300),
                                selectedLabelColor = Color.Black,
                                containerColor = Color(0xFF171F28),
                                labelColor = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Candidate List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCandidates) { emp ->
                        val role = emp.role
                        val name = emp.name
                        val salary = emp.weeklySalary
                        val alreadyHired = state.hiredEmployees.any { it.role == role }
                        val skillRating = emp.skillRatingPercent
                        val badgeColor = when {
                            skillRating >= 80 -> Color(0xFFFFB300) // Gold Elite
                            skillRating >= 60 -> Color(0xFF00E676) // Green Pro
                            else -> Color.Gray // Slate Junior
                        }
                        val skillTierTitle = when {
                            skillRating >= 80 -> "Elite Master"
                            skillRating >= 60 -> "Experienced Specialist"
                            else -> "Entry / Junior"
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2732)),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (alreadyHired) Color.DarkGray else badgeColor.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "$skillRating% Skill ($skillTierTitle)",
                                                    color = badgeColor,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "$role • ${emp.experienceYears} yrs exp • Intellect: ${emp.intelligence.toInt()}% • Reliability: ${emp.reliability.toInt()}%",
                                            color = Color.LightGray,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Button(
                                        onClick = { candidateToHire = emp },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DBF73)),
                                        enabled = !alreadyHired,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(30.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                    ) {
                                        Text(
                                            text = if (alreadyHired) "Hired" else "Hire",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (alreadyHired) Color.Gray else Color.Black
                                        )
                                    }
                                }

                                if (emp.traitDescription.isNotEmpty()) {
                                    Text(
                                        text = "Specialty: ${emp.traitDescription}",
                                        color = Color(0xFF81D4FA),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val styleNote = if (emp.investorStyle.isNotEmpty()) "Strategy: ${emp.investorStyle}" else "Reputation: Professional"
                                    Text(text = styleNote, color = Color.Gray, fontSize = 10.sp)
                                    Text(
                                        text = "$${String.format("%,.0f", salary)}/wk (Payroll)",
                                        color = Color(0xFF1DBF73),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                    }
                }
            }
        }
    }
}

        // CANDIDATE HIRING & CONTRACT CONFIRMATION DIALOG
        candidateToHire?.let { cand ->
            val hasLowReserve = state.cash < (cand.weeklySalary * 3)
            AlertDialog(
                onDismissRequest = { candidateToHire = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = Color(0xFF1DBF73),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hire ${cand.role}",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Review Employment Contract & Weekly Payroll Terms:",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161F28)),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF2C394B))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = cand.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${cand.role} • ${cand.experienceYears} Years Exp • ${cand.skillRatingPercent}% Rating",
                                    color = Color(0xFF81D4FA),
                                    fontSize = 11.sp
                                )
                                if (cand.traitDescription.isNotEmpty()) {
                                    Text(
                                        text = cand.traitDescription,
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                                Divider(color = Color(0xFF2C394B), thickness = 0.5.dp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Weekly Salary:", color = Color.Gray, fontSize = 11.sp)
                                    Text(
                                        "$${String.format("%,.0f", cand.weeklySalary)} / week",
                                        color = Color(0xFF1DBF73),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Monthly Cost (~4 wks):", color = Color.Gray, fontSize = 11.sp)
                                    Text(
                                        "$${String.format("%,.0f", cand.weeklySalary * 4.0)} / mo",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Your Current Cash:", color = Color.Gray, fontSize = 11.sp)
                                    Text(
                                        "$${String.format("%,.0f", state.cash)}",
                                        color = if (hasLowReserve) Color(0xFFFF5252) else Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        if (hasLowReserve) {
                            Text(
                                text = "⚠️ Financial Risk Alert: You have less than 3 weeks of payroll for this staff member. If cash drops below zero, morale will collapse and staff may resign.",
                                color = Color(0xFFFF8A80),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        } else {
                            Text(
                                text = "ℹ️ This salary will be automatically deducted from your account each week during weekly transition.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val candidate = candidateToHire
                            candidateToHire = null
                            if (candidate != null) {
                                viewModel.recruitProfessional(candidate.role, candidate.name, candidate.weeklySalary)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DBF73)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Sign Contract ($${String.format("%,.0f", cand.weeklySalary)}/wk)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { candidateToHire = null },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White, fontSize = 11.sp)
                    }
                },
                containerColor = Color(0xFF1A222D),
                shape = RoundedCornerShape(16.dp)
            )
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
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Systemic Tithing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Give back from your earnings. Cultivates massive trustworthiness and integrity stats.", color = Color.Gray, fontSize = 10.sp)

                    if (state.titheDebt > 0.0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E241E)),
                            border = BorderStroke(1.dp, Color(0xFFFF9800)),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⚠️ Outstanding Tithe Liability", color = Color(0xFFFF9800), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "$${String.format("%,.2f", state.titheDebt)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = "Unsettled tithes are currently draining your Generosity, Integrity, and Creative Inspiration stats.",
                                    color = Color.LightGray,
                                    fontSize = 9.sp
                                )
                                Button(
                                    onClick = { viewModel.titheCash(state.titheDebt) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                    modifier = Modifier.fillMaxWidth().height(32.dp).padding(top = 4.dp)
                                ) {
                                    Text("SETTLE OUTSTANDING TITHE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    TextField(
                        value = titheInputText,
                        onValueChange = { titheInputText = it },
                        placeholder = { Text("Custom tithe amount ($)...", color = Color.Gray, fontSize = 11.sp) },
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
    var selectedMailId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("Primary") }

    fun categoryFor(mail: MailMessage): String = when (mail.type.uppercase()) {
        "COLLAB", "FEATURE" -> "Social"
        "BRAND_ENDORSEMENT", "METUBE_BRAND_DEAL", "MUSIC_VIDEO" -> "Promotions"
        else -> "Primary"
    }

    LaunchedEffect(selectedMailId) {
        if (selectedMailId != null) {
            viewModel.markMailAsRead(selectedMailId!!)
        }
    }

    val selectedMail = state.mailbox.find { it.id == selectedMailId }

    if (selectedMail != null) {
        // Full Mail Detail View
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF16151B))
                .padding(16.dp)
        ) {
            // Mail Header with Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { selectedMailId = null },
                    modifier = Modifier.testTag("email_detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Inbox",
                        tint = Color.White
                    )
                }
                Text(
                    text = "W${selectedMail.expiresWeek}, Year ${selectedMail.expiresYear}",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subject line
            Text(
                text = selectedMail.subject,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sender info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ArtistFaceAvatar(
                    artistIdentifier = selectedMail.sender,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = selectedMail.sender,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${selectedMail.sender.lowercase().replace(" ", "")}@pulsewire.net",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(16.dp))

            val isContract = selectedMail.type == "LABEL_OFFER" ||
                             selectedMail.type == "RECORD_DEAL_OFFER" ||
                             selectedMail.type == "CONTRACT_PROPOSAL" ||
                             selectedMail.reqSingles > 0 ||
                             selectedMail.reqAlbumsLP > 0

            if (isContract) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    com.example.ui.ContractProposalView(
                        mail = selectedMail,
                        playerName = state.artistName,
                        onAccept = {
                            viewModel.acceptMail(selectedMail.id)
                        },
                        onDecline = {
                            viewModel.deleteMail(selectedMail.id)
                            selectedMailId = null
                        }
                    )
                }
            } else {
                // Email Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = selectedMail.body,
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Panel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!selectedMail.isAccepted) {
                        Button(
                            onClick = {
                                viewModel.acceptMail(selectedMail.id)
                                selectedMailId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("email_detail_accept_btn")
                        ) {
                            Text("Accept Offer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.deleteMail(selectedMail.id)
                                selectedMailId = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                            border = BorderStroke(1.dp, Color.Gray),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("email_detail_decline_btn")
                        ) {
                            Text("Decline", fontSize = 11.sp)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.deleteMail(selectedMail.id)
                                selectedMailId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("email_detail_delete_btn")
                        ) {
                            Text("Delete Mail", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = { selectedMailId = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Text("Close", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1F1F1F))) {
            // Search Bar (Google Mail styled)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF2D2D2D))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search in mail",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.artistName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Category Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AssistChip(
                    onClick = { selectedCategory = "Primary" },
                    label = { Text("Primary", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedCategory == "Primary") Color(0xFF323232) else Color.Transparent,
                        labelColor = if (selectedCategory == "Primary") Color.White else Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color(0xFF424242))
                )
                AssistChip(
                    onClick = { selectedCategory = "Promotions" },
                    label = { Text("Promotions", fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedCategory == "Promotions") Color(0xFF323232) else Color.Transparent,
                        labelColor = if (selectedCategory == "Promotions") Color.White else Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2C2C2C))
                )
                AssistChip(
                    onClick = { selectedCategory = "Social" },
                    label = { Text("Social", fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selectedCategory == "Social") Color(0xFF323232) else Color.Transparent,
                        labelColor = if (selectedCategory == "Social") Color.White else Color.Gray
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2C2C2C))
                )
                AssistChip(
                    onClick = { viewModel.markAllMailAsRead() },
                    label = { Text("Mark All Read", fontSize = 11.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.Transparent,
                        labelColor = Color(0xFF64B5F6)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2C2C2C))
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val letters = state.mailbox.asReversed().filter { categoryFor(it) == selectedCategory }
                if (letters.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Inbox, contentDescription = "Empty Inbox", tint = Color.Gray, modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "You're all caught up!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Nothing in your primary inbox.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                } else {
                    items(letters) { mail ->
                        val isUnread = !mail.isRead
                        val avatarColor = when (mail.sender.take(1).uppercase()) {
                            "A", "E", "I", "O", "U" -> Color(0xFFE53935)
                            "B", "C", "D", "F", "G" -> Color(0xFF3F51B5)
                            "H", "J", "K", "L", "M" -> Color(0xFFFF9800)
                            else -> Color(0xFF009688)
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnread) Color(0xFF2B2B3D) else Color(0xFF23232D)
                            ),
                            border = BorderStroke(1.dp, if (isUnread) NeonTeal.copy(alpha = 0.6f) else Color(0xFF33333F)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMailId = mail.id }
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                // Circular Avatar
                                ArtistFaceAvatar(
                                    artistIdentifier = mail.sender,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mail.sender,
                                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isUnread) Color.White else Color.LightGray,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "W${mail.expiresWeek}",
                                            color = Color.Gray,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = mail.subject,
                                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isUnread) Color.White else Color.LightGray,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = mail.body,
                                        color = Color.Gray,
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (mail.type == "BRAND_ENDORSEMENT") {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFFB300).copy(alpha = 0.15f))
                                                .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "COMMERCIAL BRAND ENDORSEMENT",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFB300)
                                            )
                                        }
                                    } else if (mail.type == "ROYAL_PERFORMANCE") {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF9C27B0).copy(alpha = 0.15f))
                                                .border(1.dp, Color(0xFF9C27B0).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "ROYAL COMMAND GALA INVITATION",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFCE93D8)
                                            )
                                        }
                                    } else if (mail.type == "HIGH_STATUS_GIG") {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF29B6F6).copy(alpha = 0.15f))
                                                .border(1.dp, Color(0xFF29B6F6).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "OFFICIAL COMMISSION CONTRACT",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF29B6F6)
                                            )
                                        }
                                    } else if (mail.type == "FEATURE") {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF00E676).copy(alpha = 0.15f))
                                                .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "ARTIST COLLABORATION & GUEST FEATURE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00E676)
                                            )
                                        }
                                    }

                                    val isContractOffer = mail.type == "LABEL_OFFER" ||
                                                          mail.type == "RECORD_DEAL_OFFER" ||
                                                          mail.type == "CONTRACT_PROPOSAL" ||
                                                          mail.reqSingles > 0 ||
                                                          mail.reqAlbumsLP > 0

                                    if (isContractOffer) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFFD700).copy(alpha = 0.15f))
                                                .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "OFFICIAL CONTRACT PROPOSAL • TAP TO REVIEW",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFFD700)
                                            )
                                        }
                                    }

                                    if (!mail.isAccepted && mail.type != "NORMAL" && !isContractOffer) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { viewModel.acceptMail(mail.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                                modifier = Modifier.height(28.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                            ) {
                                                Text("Accept", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.deleteMail(mail.id) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                                                border = BorderStroke(1.dp, Color.Gray),
                                                modifier = Modifier.height(28.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                            ) {
                                                Text("Decline", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                // Simple Star Icon
                                Icon(
                                    imageVector = Icons.Default.StarBorder,
                                    contentDescription = "Star",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp).align(Alignment.Top)
                                )
                            }
                    }
                }
            }
        }
    }
}
}

// =================== 9. WALLPAPER SETTINGS SCREEN & HELP GUIDE ===================
val imageWallpapers = listOf(
    Triple("porsche", "Porsche 911", "https://images.unsplash.com/photo-1503376713356-1a6d902cb25c?q=80&w=1440&h=2560&fit=crop"),
    Triple("spiderman", "Hero", "https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?q=80&w=1440&h=2560&fit=crop"),
    Triple("mountain", "Mountain Sunset", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?q=80&w=1440&h=2560&fit=crop"),
    Triple("mustang", "Mustang GT", "https://images.unsplash.com/photo-1584345604476-8ec5e12e42a5?q=80&w=1440&h=2560&fit=crop"),
    Triple("hyper_blue", "Hyper Blue", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=1440&h=2560&fit=crop"),
    Triple("hyper_red", "Hyper Red", "https://images.unsplash.com/photo-1618005192384-a83a8bd57fbe?q=80&w=1440&h=2560&fit=crop"),
    Triple("cyberpunk", "Neon City", "https://images.unsplash.com/photo-1515462277126-2dd0c162007a?q=80&w=1440&h=2560&fit=crop"),
    Triple("nature", "Forest Mist", "https://images.unsplash.com/photo-1448375240586-882707db888b?q=80&w=1440&h=2560&fit=crop"),
    Triple("space", "Deep Space", "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?q=80&w=1440&h=2560&fit=crop"),
    Triple("minimal", "Dark Minimal", "https://images.unsplash.com/photo-1494438639946-1ebd1d20bf85?q=80&w=1440&h=2560&fit=crop")
)

@Composable
fun WallpaperSettingsScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val options = listOf(
        Triple("Cosmic Nebula", "Cosmic Nebula", Brush.verticalGradient(listOf(Color(0xFF120C3F), Color(0xFF4A007A), Color(0xFF0B0720)))),
        Triple("Cyber Sunset", "Cyber Sunset", Brush.verticalGradient(listOf(Color(0xFFF07B50), Color(0xFF901C40), Color(0xFF1D0E25)))),
        Triple("Midnight Forest", "Midnight Forest", Brush.verticalGradient(listOf(Color(0xFF0A2E12), Color(0xFF031B08), Color(0xFF000C02)))),
        Triple("Golden Hour", "Golden Hour", Brush.verticalGradient(listOf(Color(0xFFF9D423), Color(0xFFFF4E50), Color(0xFF2C1014))))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Ecosystem Wallpaper Theme", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)

        imageWallpapers.forEach { (id, name, url) ->
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
                    AsyncImage(
                        model = url,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }


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

        Spacer(modifier = Modifier.height(8.dp))

        // CAREER GUIDEBOOK & HELP MANUAL
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161424)),
            border = BorderStroke(1.dp, Color(0xFF2C2844)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Career Guide & Help Manual", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Understanding the dynamic systems operating in the music industry simulation will aid your strategic career development:",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                val guideTopics = listOf(
                    "Staff Hub & Professional Team" to "Each specialist brings distinct intelligence, reliability, and loyalty. Fair salaries and skill training retain valuable personnel. Unreasonable cuts increase burnout and resignation risks.",
                    "Copyrights & Intellectual Property" to "Registering copyrights safeguards your catalog against unauthorized sampling and infringement. Holding your master rights secures compounding catalog royalties.",
                    "Cast Social Media & Engagement" to "Maintaining a focused category niche, spending active time connecting, and posting consistently develops organic reach. Following other artists builds peer affinity.",
                    "Spirituality & Well-Being" to "Balancing your schedule through prayer, meditation, and adequate rest relieves career stress, boosts inspiration, and protects mental focus during heavy release cycles.",
                    "Professional Mailbox & Inquiries" to "Review all collaboration requests, endorsement inquiries, and business offers carefully. Early-stage offers require diligence before accepting binding terms.",
                    "Banking & Reserve Management" to "Living within your means and maintaining cash in savings safeguards your career during economic downturns and dry release spells.",
                    "Crypto & Stock Volatility" to "Financial markets fluctuate with global sentiment. High potential reward comes with market volatility and requires measured risk tolerance.",
                    "Industry Networking & Collaborations" to "Building genuine relationships with fellow artists and industry stakeholders unlocks co-release opportunities and long-term goodwill.",
                    "Merchandise Studio & Product Lines" to "Launching customized apparel and physical collectibles generates direct listener revenue and deepens fan attachment to your brand.",
                    "Studio Production & World Tours" to "Writing quality material and rehearsing your stagecraft elevates live performance ratings and expands global attendance."
                )

                guideTopics.forEachIndexed { i, (title, desc) ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text(text = "${i + 1}. $title", color = NeonTeal, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = desc, color = Color.LightGray, fontSize = 10.sp, lineHeight = 15.sp)
                    }
                    if (i < guideTopics.size - 1) {
                        Divider(color = Color(0xFF2C2844), thickness = 0.5.dp)
                    }
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
    var isBusinessLoan by remember { mutableStateOf(false) }
    var selectedVentureId by remember { mutableStateOf(state.businessVentures.firstOrNull()?.id ?: "") }
    var selectedDurationWeeks by remember { mutableStateOf(4) } // 4 (1 mo), 12 (3 mo), 26 (6 mo), 52 (1 yr)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Financial Overview card (ApexPay Blue Gradient styled)
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .background(Brush.verticalGradient(listOf(Color(0xFF0052FF), Color(0xFF0035A3))))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Apex Total Wallet Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = "Bank Balance", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Text(
                        text = if (state.cash < 0) "-$${String.format("%,.2f", -state.cash)}" else "$${String.format("%,.2f", state.cash)}",
                        color = if (state.cash < 0) Color(0xFFFF5252) else Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Apex Wealth Savings (2.50% wk)", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text(
                                "$${String.format("%,.2f", state.savingsBalance)}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Active Loan Balance", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            Text(
                                "$${String.format("%,.2f", state.loanAmount)}",
                                color = Color(0xFFFFCDD2),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.taxDebt > 0.0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Outstanding Tax Bill:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "$${String.format("%,.2f", state.taxDebt)}",
                                    color = Color(0xFFFFCDD2),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("savings" to "Savings", "loan" to "Loans", "tax" to "Taxes", "wealth" to "Wealth").forEach { (tabId, label) ->
                val isSelected = activeTab == tabId
                Button(
                    onClick = {
                        activeTab = tabId
                        inputAmount = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color(0xFF0052FF) else Color(0xFF23232F),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                            "Earn a premium weekly compounding interest rate of 3.00% (465% APY equivalent) on all active savings deposits. Perfect for protecting your wealth from inflation hazards and life emergency events.",
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Loan Application Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Apex Structured Credit Facilities", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                            // Loan Type Toggle: Personal vs Commercial Business Loan
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { isBusinessLoan = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (!isBusinessLoan) Color(0xFF0052FF) else Color(0xFF2B283E)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Text("Personal Loan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { isBusinessLoan = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isBusinessLoan) Color(0xFF0052FF) else Color(0xFF2B283E)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Text("Business Loan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (isBusinessLoan) {
                                val eligibleVentures = state.businessVentures.filter { it.isOwned && it.isPlayerInControl }
                                if (eligibleVentures.isEmpty()) {
                                    Text(
                                        "⚠ Commercial loans are exclusively available for businesses you own and control (≥50% equity). You currently have no eligible businesses under your operational control.",
                                        color = Color(0xFFF87171),
                                        fontSize = 11.sp
                                    )
                                } else {
                                    Text("Select Commercial Enterprise:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        eligibleVentures.forEach { venture ->
                                            val isSelected = selectedVentureId == venture.id || (selectedVentureId.isBlank() && venture == eligibleVentures.first())
                                            Button(
                                                onClick = { selectedVentureId = venture.id },
                                                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Color(0xFF2563EB) else Color(0xFF161424)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF60A5FA) else Color(0xFF334155)),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp)
                                            ) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                    Text(venture.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    Text("Cap: $${String.format("%,.0f", venture.investedCapital)}", fontSize = 10.sp, color = Color(0xFF93C5FD))
                                                }
                                            }
                                        }
                                    }
                                    Text("Commercial Credit Eligibility: $${String.format("%,.0f", state.businessLoanEligibility)}", color = NeonTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "💡 Commercial Loan Rule: Initial limit is $20,000. Settle on time to expand next line up to $35,000! Borrowed capital funds Withdrawable Profits.",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            } else {
                                Text("Personal Credit Eligibility: $${String.format("%,.0f", state.personalLoanEligibility)}", color = NeonTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "💡 Personal Loan Rule: Initial limit is $10,000. Paying fast expands next limit between $11,000 and $20,000!",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }

                            // Duration Selector (1 Month, 3 Months, 6 Months, 1 Year)
                            Text("Select Repayment Term Duration:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                val durations = listOf(
                                    Pair(4, "1 Mo (4w)"),
                                    Pair(12, "3 Mo (12w)"),
                                    Pair(26, "6 Mo (26w)"),
                                    Pair(52, "1 Yr (52w)")
                                )
                                durations.forEach { (weeks, label) ->
                                    val isSelected = selectedDurationWeeks == weeks
                                    Button(
                                        onClick = { selectedDurationWeeks = weeks },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Color(0xFF0052FF) else Color(0xFF23232F)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 2.dp)
                                    ) {
                                        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color(0xFFCBD5E1))
                                    }
                                }
                            }

                            // Dynamic Calculation Summary
                            val borrowAmt = inputAmount.toDoubleOrNull() ?: 50000.0
                            val interestPct = when (selectedDurationWeeks) {
                                4 -> 0.134
                                12 -> 0.44
                                26 -> 0.75
                                else -> 1.20
                            }
                            val interestFee = borrowAmt * interestPct
                            val maturityRepayable = borrowAmt + interestFee

                            Surface(
                                color = Color(0xFF141220),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Requested Principal:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                        Text("$${String.format("%,.0f", borrowAmt)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Fixed Term Interest (${(interestPct * 100).toInt()}%):", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                        Text("+$${String.format("%,.0f", interestFee)}", color = Color(0xFFFCA5A5), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 2.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Maturity Repayable:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("$${String.format("%,.0f", maturityRepayable)}", color = Color(0xFF67E8F9), fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }
                                    Text(
                                        "⚠ Maturity Deadline: $selectedDurationWeeks weeks. You can settle early at any time. Once the deadline passes, an additional 4% compounding penalty is applied weekly until fully paid off.",
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 9.sp,
                                        lineHeight = 12.sp
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val amt = inputAmount.toDoubleOrNull()
                                    if (amt != null && amt > 0.0) {
                                        val eligible = state.businessVentures.filter { it.isOwned && it.isPlayerInControl }
                                        val vId = if (isBusinessLoan) {
                                            selectedVentureId.ifBlank { eligible.firstOrNull()?.id }
                                        } else null
                                        if (isBusinessLoan && (vId == null || eligible.none { it.id == vId })) {
                                            viewModel.showToast("Commercial credit denied! You must have controlling interest in an active, owned business.")
                                        } else {
                                            viewModel.applyForStructuredLoan(amt, selectedDurationWeeks, if (isBusinessLoan) "COMMERCIAL" else "PERSONAL", vId)
                                            inputAmount = ""
                                        }
                                    } else {
                                        viewModel.showToast("Please enter a valid positive borrow amount!")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Borrow $${String.format("%,.0f", borrowAmt)} (${if (isBusinessLoan) "Business" else "Personal"})", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Active Loans / Contracts Section
                    if (state.activeLoanContracts.isNotEmpty()) {
                        Text("Active Loan Contracts (${state.activeLoanContracts.size})", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        state.activeLoanContracts.forEach { loan ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF221F35)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (loan.isOverdue) Color(0xFFEF4444) else Color(0xFF3B82F6)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(
                                                imageVector = if (loan.borrowerType == "COMMERCIAL") Icons.Default.Business else Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (loan.isOverdue) Color(0xFFEF4444) else Color(0xFF60A5FA),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = if (loan.borrowerType == "COMMERCIAL") "${loan.ventureName ?: "Enterprise"} Credit" else "Personal Credit",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Surface(
                                            color = if (loan.isOverdue) Color(0xFF450A0A) else Color(0xFF0F261B),
                                            shape = RoundedCornerShape(4.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (loan.isOverdue) Color(0xFFEF4444) else Color(0xFF10B981))
                                        ) {
                                            Text(
                                                text = if (loan.isOverdue) "OVERDUE (+4%/wk penalty)" else "${loan.weeksRemaining} wks remaining",
                                                color = if (loan.isOverdue) Color(0xFFFCA5A5) else Color(0xFF6EE7B7),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Remaining Balance:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                        Text("$${String.format("%,.0f", loan.remainingBalance)}", color = Color(0xFFFCA5A5), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Original Facility:", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                        Text("$${String.format("%,.0f", loan.principal)} (${loan.durationLabel})", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                    }

                                    // Quick Repay buttons
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { viewModel.repayStructuredLoan(loan.id, 10000.0) },
                                            enabled = state.cash >= 1000.0,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp)
                                        ) {
                                            Text("Pay $10k", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { viewModel.repayStructuredLoan(loan.id, loan.remainingBalance) },
                                            enabled = state.cash >= 1.0,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp)
                                        ) {
                                            Text("Pay Full", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Legacy Loan Payoff Button (if player had old loan)
                    if (state.loanAmount > 0.0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF381515)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Legacy Credit Line Active: $${String.format("%,.2f", state.loanAmount)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = {
                                        val amt = inputAmount.toDoubleOrNull() ?: state.loanAmount
                                        viewModel.repayLoan(amt)
                                        inputAmount = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text("Repay Legacy Loan", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
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

            "wealth" -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Net Worth Card
                    val netWorth = viewModel.calculateNetWorth(state)
                    val totalAssets = viewModel.calculateTotalAssets(state)
                    val totalLiabilities = viewModel.calculateTotalLiabilities(state)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Apex Wealth Intelligence Dashboard", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF23232F), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("True Net Worth", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                        Text(
                                            "$${String.format("%,.2f", netWorth)}",
                                            color = if (netWorth >= 0) NeonTeal else NeonPink,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Assets / Liabilities", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                        Text(
                                            "$${String.format("%,.0f", totalAssets)} / $${String.format("%,.0f", totalLiabilities)}",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                            // Reserves Grid
                            Text("Your Wealth Reserves", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val reserves = listOf(
                                    Triple("Emergency Fund", state.emergencyFundBalance, "Protects from life hazard incidents"),
                                    Triple("Payroll Reserve", state.payrollReserve, "Guarantees employee weekly salaries"),
                                    Triple("Business Reserve", state.businessReserve, "Buffers overhead and corporate costs"),
                                    Triple("Gov. Bonds (10% mo)", state.bondsBalance, if (state.hasBondLicense) "Active Sovereign Member (10% monthly yield)" else "License Required ($5M initial / $2.5M renew)")
                                )
                                reserves.forEach { (name, bal, desc) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF252335), RoundedCornerShape(6.dp))
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                            Text(desc, color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                                        }
                                        Text(
                                            "$${String.format("%,.2f", bal)}",
                                            color = if (bal > 0.0) NeonTeal else Color.White.copy(alpha = 0.5f),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (!state.hasBondLicense) {
                                    val cost = if (state.wasFormerBondMember) 2500000.0 else 5000000.0
                                    val feeLabel = if (state.wasFormerBondMember) "Renew Sovereign License ($2.5M)" else "Buy Sovereign License ($5.0M)"
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2338)),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAB308)),
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                                Text("🏛️ Sovereign Bond Membership", color = Color(0xFFFDE047), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Surface(color = Color(0xFFEAB308).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                                    Text(if (state.wasFormerBondMember) "FORMER MEMBER" else "UNREGISTERED", color = Color(0xFFFDE047), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                }
                                            }
                                            Text("Sovereign bond investing requires a membership license ($5M initial, $2.5M renewal). 10% monthly yield (every 4 weeks). Min deposit: $500,000. Full withdrawal expires membership.", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp, lineHeight = 12.sp)
                                            Button(
                                                onClick = { viewModel.purchaseBondMembershipLicense() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEAB308)),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(feeLabel, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Fund Transfer Panel
                    var fromAccount by remember { mutableStateOf("CASH") }
                    var toAccount by remember { mutableStateOf("SAVINGS") }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Secure Wealth Fund Transfers", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Move money frictionlessly between your specialized capital accounts:", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)

                            val accounts = listOf("CASH", "SAVINGS", "EMERGENCY", "PAYROLL", "BUSINESS", "BONDS")

                            // From Selector
                            Column {
                                Text("FROM SOURCE ACCOUNT", color = NeonTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    accounts.forEach { acc ->
                                        val isSel = fromAccount == acc
                                        Button(
                                            onClick = { fromAccount = acc },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSel) NeonTeal else Color(0xFF252335),
                                                contentColor = if (isSel) Color.Black else Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(acc, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // To Selector
                            Column {
                                Text("TO DESTINATION ACCOUNT", color = NeonPink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    accounts.forEach { acc ->
                                        val isSel = toAccount == acc
                                        Button(
                                            onClick = { toAccount = acc },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isSel) NeonPink else Color(0xFF252335),
                                                contentColor = if (isSel) Color.Black else Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Text(acc, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    val amt = inputAmount.toDoubleOrNull()
                                    if (amt == null || amt <= 0.0) {
                                        viewModel.showToast("Please enter a valid positive transfer amount!")
                                    } else if (fromAccount == toAccount) {
                                        viewModel.showToast("Source and destination accounts must be different!")
                                    } else {
                                        // Check balance
                                        val bal = when (fromAccount) {
                                            "CASH" -> state.cash
                                            "SAVINGS" -> state.savingsBalance
                                            "EMERGENCY" -> state.emergencyFundBalance
                                            "PAYROLL" -> state.payrollReserve
                                            "BUSINESS" -> state.businessReserve
                                            "BONDS" -> state.bondsBalance
                                            else -> 0.0
                                        }
                                        if (bal < amt) {
                                            viewModel.showToast("Insufficient funds in $fromAccount (Available: $${String.format("%,.2f", bal)})!")
                                        } else {
                                            viewModel.transferWealthFunds(fromAccount, toAccount, amt)
                                            inputAmount = ""
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonTeal),
                                modifier = Modifier.fillMaxWidth().height(44.dp)
                            ) {
                                Text("Confirm Internal Wealth Transfer", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    // Wealth Coaching Log Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C29)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Apex Wealth Coaching Reviews", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            if (state.financialReviewLog.isEmpty()) {
                                Text(
                                    "Your first Monthly Wealth Intelligence Review will be generated on Week 4. It will comprehensively analyze your capital allocations, emergency fund cushions, debt-to-asset ratios, portfolio risks, and stewardship traits to provide professional financial coaching.",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            } else {
                                state.financialReviewLog.reversed().forEachIndexed { index, logText ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF252335)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("Review Month ${state.financialReviewLog.size - index}", color = NeonTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = logText,
                                                color = Color.White.copy(alpha = 0.85f),
                                                fontSize = 10.sp,
                                                lineHeight = 14.sp,
                                                fontFamily = FontFamily.Monospace
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
fun GenreCoverArt(title: String, genreId: String, sizeDp: Int = 40) {
    val gradientColors = when (genreId.lowercase()) {
        "pop" -> listOf(Color(0xFFFF4081), Color(0xFFE040FB))
        "rock" -> listOf(Color(0xFFD50000), Color(0xFF37474F))
        "hip_hop", "hiphop" -> listOf(Color(0xFFFFAB00), Color(0xFF00C853))
        "electronic", "synthwave" -> listOf(Color(0xFF3D5AFE), Color(0xFF00E5FF))
        "jazz" -> listOf(Color(0xFF4E342E), Color(0xFFFFD54F))
        "classical" -> listOf(Color(0xFFECEFF1), Color(0xFF880E4F))
        else -> listOf(Color(0xFF00E676), Color(0xFF006064))
    }
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.take(2).uppercase(),
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = (sizeDp / 3.2).sp
        )
    }
}

// =================== SOUNDSTREAM MUSIC APP SCREEN ===================
// =================== 10. GLOBAL MARKET INTELLIGENCE ===================
@Composable
fun MarketIntelligenceWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("GLOBAL MARKET INTELLIGENCE", color = Color(0xFF38BDF8), fontSize = 15.sp, fontWeight = FontWeight.Black)
                Text("omnisearch.com/market • Macroeconomic Feed", color = Color.Gray, fontSize = 10.sp)
            }
            Surface(color = Color(0xFF38BDF8).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                Text(state.economyCycle, color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Macro Indicators", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Economic Cycle:", color = Color.Gray, fontSize = 11.sp)
                            Text(state.economyCycle, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Inflation Rate:", color = Color.Gray, fontSize = 11.sp)
                            Text("${String.format("%.1f", state.inflationRate * 100)}%", color = Color(0xFFF43F5E), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Fuel Price:", color = Color.Gray, fontSize = 11.sp)
                            Text("$${state.fuelPrice}/L", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =================== 11. WIKIPEDIA ARTIST REGISTRY ===================
@Composable
fun WikipediaArtistsWebsitePage(state: GameStateEntity, viewModel: GameViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF181A1B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF23272A))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("WIKIPEDIA", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("wikipedia.org • City Music Artist Registry", color = Color.Gray, fontSize = 10.sp)
            }
            Surface(color = Color(0xFF3B82F6).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                Text("${state.npcs.size} Entries", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.npcs) { npc ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF23272A))) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(npc.name, color = Color(0xFF93C5FD), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Genre: ${npc.genrePreference} • Fans: ${String.format("%,d", npc.fans)} • Influence: ${npc.influence.toInt()}/100", color = Color.LightGray, fontSize = 10.sp)
                        Text(
                            text = "A prominent musician based in the city. Relationship affinity with you is ${npc.relationshipWithPlayer.toInt()}%.",
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

// =================== 13. CAMERA & PHOTO GALLERY SCREEN ===================
private fun savePhotoToMediaStore(context: android.content.Context, file: File) {
    try {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/ArtistStory")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                file.inputStream().use { inp -> inp.copyTo(out) }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun createRealisticSnapshotBitmap(file: File, artistName: String, mode: String) {
    try {
        val width = 1080
        val height = 1080
        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        val gradient = when (mode) {
            "PORTRAIT" -> android.graphics.LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                android.graphics.Color.rgb(65, 25, 50), android.graphics.Color.rgb(18, 10, 22),
                android.graphics.Shader.TileMode.CLAMP
            )
            "CINEMATIC" -> android.graphics.LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                android.graphics.Color.rgb(20, 45, 75), android.graphics.Color.rgb(8, 12, 24),
                android.graphics.Shader.TileMode.CLAMP
            )
            else -> android.graphics.LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                android.graphics.Color.rgb(38, 38, 48), android.graphics.Color.rgb(14, 14, 18),
                android.graphics.Shader.TileMode.CLAMP
            )
        }
        val bgPaint = android.graphics.Paint().apply { shader = gradient }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        val ringPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(80, 255, 215, 0)
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        canvas.drawCircle(width / 2f, height / 2f, 320f, ringPaint)

        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 58f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        canvas.drawText("📸 $artistName", width / 2f, height / 2f - 40f, textPaint)

        val subPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(220, 220, 220)
            textSize = 34f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        canvas.drawText("STUDIO CAM • ${mode.uppercase()}", width / 2f, height / 2f + 30f, subPaint)

        val timePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(255, 215, 0)
            textSize = 28f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        canvas.drawText("STAMP: $dateStr", width / 2f, height / 2f + 90f, timePaint)

        file.outputStream().use { out ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, out)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
@Composable
fun CameraAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val permissions = remember {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            listOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            listOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    val permissionsState = com.google.accompanist.permissions.rememberMultiplePermissionsState(permissions)
    val cameraGranted = permissionsState.permissions.find { it.permission == android.Manifest.permission.CAMERA }?.status?.isGranted ?: false

    if (cameraGranted) {
        RealCameraAppUI(state, viewModel)
    } else {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF1E1E1E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(44.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Camera & Storage Access Required", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Please grant camera access to use the viewfinder and storage access to save pictures to your device photo gallery and MeTube video library.",
                color = Color.LightGray,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { permissionsState.launchMultiplePermissionRequest() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Allow Camera & Storage", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RealCameraAppUI(state: GameStateEntity, viewModel: GameViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var lensFacing by remember { mutableStateOf(androidx.camera.core.CameraSelector.LENS_FACING_BACK) }
    var currentMode by remember { mutableStateOf("PHOTO") }
    var flashMode by remember { mutableStateOf("AUTO") }
    val modes = listOf("CINEMATIC", "VIDEO", "PHOTO", "PORTRAIT", "PANO")
    var showFlash by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableStateOf("1x") }
    var showGalleryModal by remember { mutableStateOf(false) }
    var viewingPhotoPath by remember { mutableStateOf<String?>(null) }

    val imageCapture = remember {
        androidx.camera.core.ImageCapture.Builder()
            .setCaptureMode(androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val photosDir = File(context.filesDir, "camera_photos").apply { mkdirs() }
                val importedFile = File(photosDir, "IMPORT_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    importedFile.outputStream().use { output -> input.copyTo(output) }
                }
                savePhotoToMediaStore(context, importedFile)
                viewModel.addSavedPhoto(importedFile.absolutePath)
                viewModel.showToast("Imported photo from phone storage!")
            } catch (e: Exception) {
                viewModel.addSavedPhoto(uri.toString())
            }
        }
    }

    LaunchedEffect(showFlash) {
        if (showFlash) {
            kotlinx.coroutines.delay(100)
            showFlash = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Viewfinder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 160.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
        ) {
            androidx.compose.ui.viewinterop.AndroidView(
                factory = { ctx ->
                    val previewView = androidx.camera.view.PreviewView(ctx).apply {
                        scaleType = androidx.camera.view.PreviewView.ScaleType.FILL_CENTER
                    }
                    val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = androidx.camera.core.Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = androidx.camera.core.CameraSelector.Builder().requireLensFacing(lensFacing).build()
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, androidx.core.content.ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                update = { previewView ->
                    val cameraProviderFuture = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = androidx.camera.core.Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = androidx.camera.core.CameraSelector.Builder().requireLensFacing(lensFacing).build()
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, androidx.core.content.ContextCompat.getMainExecutor(context))
                },
                modifier = Modifier.fillMaxSize()
            )

            // Grid Overlay
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 0.5.dp)
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 0.5.dp)
            }
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Box(modifier = Modifier.fillMaxHeight().width(0.5.dp).background(Color.White.copy(alpha = 0.2f)))
                Box(modifier = Modifier.fillMaxHeight().width(0.5.dp).background(Color.White.copy(alpha = 0.2f)))
            }

            // Focus indicator (center)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            ) {
                Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFD700).copy(alpha = 0.8f), modifier = Modifier.size(16.dp).align(Alignment.CenterEnd).offset(x = 24.dp))
            }

            // Zoom controls overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(".5", "1x", "2x", "3x").forEach { z ->
                    Text(
                        text = z,
                        color = if (zoomRatio == z) Color(0xFFFFD700) else Color.White,
                        fontSize = if (zoomRatio == z) 14.sp else 12.sp,
                        fontWeight = if (zoomRatio == z) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.clickable { zoomRatio = z }
                    )
                }
            }
        }

        // Flash Screen Overlay
        if (showFlash) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White))
        }

        // Top Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                flashMode = when (flashMode) {
                    "AUTO" -> "ON"
                    "ON" -> "OFF"
                    else -> "AUTO"
                }
            }) {
                val icon = when (flashMode) {
                    "ON" -> Icons.Default.FlashOn
                    "OFF" -> Icons.Default.FlashOff
                    else -> Icons.Default.FlashAuto
                }
                val color = if (flashMode == "ON") Color(0xFFFFD700) else Color.White
                Icon(imageVector = icon, contentDescription = "Flash", tint = color, modifier = Modifier.size(24.dp))
            }

            // Storage Import Button
            IconButton(onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Import from storage", tint = Color.White, modifier = Modifier.size(24.dp))
            }

            IconButton(onClick = {
                lensFacing = if (lensFacing == androidx.camera.core.CameraSelector.LENS_FACING_BACK) {
                    androidx.camera.core.CameraSelector.LENS_FACING_FRONT
                } else {
                    androidx.camera.core.CameraSelector.LENS_FACING_BACK
                }
            }) {
                Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Flip Camera", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }

        // Bottom Controls Area
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Mode Selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(horizontal = 40.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(modes) { mode ->
                    Text(
                        text = mode,
                        color = if (mode == currentMode) Color(0xFFFFD700) else Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (mode == currentMode) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.clickable { currentMode = mode }
                    )
                }
            }

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Thumbnail Button
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .background(Color(0xFF222222))
                        .clickable { showGalleryModal = true },
                    contentAlignment = Alignment.Center
                ) {
                    val latestPhoto = state.savedPhotos.firstOrNull()
                    if (latestPhoto != null) {
                        AsyncImage(
                            model = latestPhoto,
                            contentDescription = "Saved Photos Gallery",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Photo Count Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD600))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text("${state.savedPhotos.size}", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }

                // Shutter Button
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .border(3.5.dp, Color.White, CircleShape)
                        .padding(5.dp)
                        .clickable {
                            showFlash = true
                            val photosDir = File(context.filesDir, "camera_photos").apply { mkdirs() }
                            val photoFile = File(photosDir, "PHOTO_${System.currentTimeMillis()}.jpg")

                            val saveAndNotify = { fileToSave: File ->
                                savePhotoToMediaStore(context, fileToSave)
                                viewModel.addSavedPhoto(fileToSave.absolutePath)
                                viewModel.updateState {
                                    it.copy(
                                        fans = it.fans + 250,
                                        energy = kotlin.math.max(0, it.energy - 5),
                                        cash = kotlin.math.max(0.0, it.cash - 25.0)
                                    )
                                }
                            }

                            val outputOptions = androidx.camera.core.ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCapture.takePicture(
                                outputOptions,
                                androidx.core.content.ContextCompat.getMainExecutor(context),
                                object : androidx.camera.core.ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: androidx.camera.core.ImageCapture.OutputFileResults) {
                                        saveAndNotify(photoFile)
                                    }

                                    override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                                        // Virtual emulator fallback: create realistic JPEG photo
                                        createRealisticSnapshotBitmap(photoFile, state.artistName, currentMode)
                                        saveAndNotify(photoFile)
                                    }
                                }
                            )
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(if (currentMode == "VIDEO" || currentMode == "CINEMATIC") Color.Red else Color.White))
                }

                // Switch Camera Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF222222)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        lensFacing = if (lensFacing == androidx.camera.core.CameraSelector.LENS_FACING_BACK) {
                            androidx.camera.core.CameraSelector.LENS_FACING_FRONT
                        } else {
                            androidx.camera.core.CameraSelector.LENS_FACING_BACK
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Cached, contentDescription = "Switch Camera", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Gallery Modal Sheet
        if (showGalleryModal) {
            Dialog(onDismissRequest = { showGalleryModal = false; viewingPhotoPath = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .fillMaxHeight(0.85f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFF333333))
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Captured Photos (${state.savedPhotos.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { showGalleryModal = false; viewingPhotoPath = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        // Pick from device storage CTA
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import Photo from Phone Storage", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        if (viewingPhotoPath != null) {
                            // Full-screen viewer for selected photo
                            Column(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color.Black)
                                ) {
                                    AsyncImage(
                                        model = viewingPhotoPath,
                                        contentDescription = "Full photo preview",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.removeSavedPhoto(viewingPhotoPath!!)
                                            viewingPhotoPath = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Delete", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewingPhotoPath = null },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Back to Grid", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        } else if (state.savedPhotos.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("No photos captured yet", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text("Snap photos with the shutter button or import from phone storage.", color = Color.DarkGray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
                                }
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 4.dp)
                            ) {
                                items(state.savedPhotos) { photo ->
                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF2B2B2B))
                                            .border(1.dp, Color(0xFF3E3E3E), RoundedCornerShape(8.dp))
                                            .clickable { viewingPhotoPath = photo }
                                    ) {
                                        AsyncImage(
                                            model = photo,
                                            contentDescription = "Photo thumbnail",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
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

// =================== PULSEWIRE NEWS ECOSYSTEM SCREEN ===================
fun getNewsImageUrl(tag: String): String {
    return when {
        tag.contains("TREND", ignoreCase = true) || tag.contains("MUSIC", ignoreCase = true) || tag.contains("FOR YOU", ignoreCase = true) -> {
            "https://images.unsplash.com/photo-1501386761578-eac5c94b800a?w=600&auto=format&fit=crop&q=60" // Concert stage and crowd
        }
        tag.contains("BUSINESS", ignoreCase = true) || tag.contains("ECONOMY", ignoreCase = true) || tag.contains("TAX", ignoreCase = true) || tag.contains("MARKET", ignoreCase = true) || tag.contains("ALERT", ignoreCase = true) -> {
            "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?w=600&auto=format&fit=crop&q=60" // Financial charts
        }
        tag.contains("STEWARDSHIP", ignoreCase = true) || tag.contains("STAFF", ignoreCase = true) || tag.contains("ECOSYSTEM", ignoreCase = true) || tag.contains("FAITH", ignoreCase = true) || tag.contains("MIND", ignoreCase = true) -> {
            "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=600&auto=format&fit=crop&q=60" // Business desk/marketing
        }
        tag.contains("POLITICS", ignoreCase = true) || tag.contains("WORLD", ignoreCase = true) || tag.contains("GLOBAL", ignoreCase = true) -> {
            "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=60" // Globe / network
        }
        else -> {
            "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=60" // General music
        }
    }
}

data class NewsArticleItem(
    val tag: String,
    val headline: String,
    val subtitle: String,
    val time: String,
    val author: String,
    val imageUrl: String
)

@Composable
fun NewsArticleCard(item: NewsArticleItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16151D)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Real Image instead of placeholders
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.headline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.tag,
                        color = Color(0xFFE57373),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                    Text(
                        text = item.time,
                        color = Color.Gray,
                        fontSize = 8.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.headline,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.subtitle,
                    color = Color.LightGray,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "By ${item.author}",
                        color = Color.Gray,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}
// =================== AD COMPANY PROMOTION APP SCREEN ===================
@Composable
fun AdCompanyPromotionScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTab by remember { mutableStateOf("Campaigns") }
    val tabs = listOf("Campaigns", "Advertisers", "Analytics")
    var showSongSelectorForAgency by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C100D))
            .padding(12.dp)
    ) {
        // App Promotion Header Info
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131E15)),
            border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("PROMOTIONS ENGINE", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Campaign Budget", color = Color.LightGray, fontSize = 9.sp)
                        Text("$${String.format("%,.2f", state.adCompanyBudget)}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = { viewModel.addAdCompanyBudget(5000.0) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("add_promo_budget_btn")
                    ) {
                        Text("ADD $5K", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Action Tabs
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tabName ->
                Button(
                    onClick = { selectedTab = tabName },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == tabName) Color(0xFF1D3220) else Color(0xFF141A15),
                        contentColor = if (selectedTab == tabName) Color(0xFF81C784) else Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (selectedTab == tabName) Color(0xFF81C784) else Color.Transparent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(32.dp)
                ) {
                    Text(tabName, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showSongSelectorForAgency != null) {
            AlertDialog(
                onDismissRequest = { showSongSelectorForAgency = null },
                containerColor = Color(0xFF131E15),
                title = { Text("Select Target Song", color = Color.White) },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(state.songs.filter { it.isUploadedMap.isNotEmpty() }) { song ->
                            TextButton(
                                onClick = {
                                    viewModel.launchAdCampaign(showSongSelectorForAgency!!, song.id)
                                    showSongSelectorForAgency = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(song.title, color = Color(0xFF81C784))
                            }
                        }
                        if (state.songs.filter { it.isUploadedMap.isNotEmpty() }.isEmpty()) {
                            item { Text("No released songs available.", color = Color.Gray) }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSongSelectorForAgency = null }) {
                        Text("Cancel", color = Color.Red)
                    }
                }
            )
        }

        when (selectedTab) {
            "Campaigns" -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    items(state.activeAdCampaigns) { camp ->
                        val targetSong = state.songs.filter { it.isUploadedMap.isNotEmpty() }.find { it.id == camp.targetId }
                        val targetName = targetSong?.title ?: "Unknown"
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131814)),
                            border = BorderStroke(1.dp, Color(0xFF81C784)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(camp.agencyName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("$$${String.format("%,.0f", camp.weeklyBudget)}/wk", color = Color(0xFF81C784), fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Target: $targetName", color = Color.LightGray, fontSize = 10.sp)
                                Text("Remaining: ${camp.weeksRemaining} weeks", color = Color.Gray, fontSize = 9.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.haltAdCampaign(camp.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha=0.3f), contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(30.dp)
                                ) {
                                    Text("HALT CAMPAIGN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    if (state.activeAdCampaigns.isEmpty()) {
                        item {
                            Text("No active campaigns. Go to 'Advertisers' to launch one.", color = Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(20.dp))
                        }
                    }
                }
            }
            "Advertisers" -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    val advertisers = listOf(
                        AdvertiserConfig(
                            id = "beatwave",
                            title = "BeatWave Media (Social/Digital Ads)",
                            cost = 8000.0,
                            desc = "Hyper-targeted visual loops on SeeChat & Cast. Boosts weekly followers (+3K to +6K) & increases Trending score (+1.8).",
                            tag = "SOCIAL FOCUS"
                        ),
                        AdvertiserConfig(
                            id = "starvision",
                            title = "StarVision TV Network (Television Ads)",
                            cost = 22000.0,
                            desc = "Nationwide promotional broadcast spins. Delivers explosive mainstream fan volume (+11K to +24K) and boosts Public Image (+3.5).",
                            tag = "MAINSTREAM REACH"
                        ),
                        AdvertiserConfig(
                            id = "urban",
                            title = "UrbanBillboards (Transit Hub Outdoor)",
                            cost = 6500.0,
                            desc = "High-definition static placements in dense city centers. Expands brand familiarity, boosting respect (+1.2) & Public Image (+1.8).",
                            tag = "VISUAL PLACEMENT"
                        ),
                        AdvertiserConfig(
                            id = "soundspot",
                            title = "SoundSpot Radio (Podcast & Spins)",
                            cost = 4200.0,
                            desc = "Daily rotational loops on top channels. Enhances vocal training exposure (+1.0), Trending (+2.2) and hardcore fans.",
                            tag = "VOCAL ROTATIONS"
                        )
                    )
                    items(advertisers) { adv ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131814)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color(0xFF222222))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = adv.tag,
                                            color = Color.LightGray,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "$$${String.format("%,.0f", adv.cost)}/wk",
                                        color = Color(0xFF81C784),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(adv.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(adv.desc, color = Color.LightGray, fontSize = 9.sp, lineHeight = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showSongSelectorForAgency = adv.id },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF81C784),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().height(30.dp)
                                ) {
                                    Text("LAUNCH CAMPAIGN", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            "Analytics" -> {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131814)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ACTIVE CAMPAIGN SUMMARY", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            val activeCount = state.activeAdCampaigns.size
                            val totalCost = state.activeAdCampaigns.sumOf { it.weeklyBudget }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Running Campaigns:", color = Color.Gray, fontSize = 11.sp)
                                Text("$activeCount", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Weekly Ad Spend Rate:", color = Color.Gray, fontSize = 11.sp)
                                Text("$${String.format("%,.2f", totalCost)}/wk", color = Color(0xFF81C784), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Estimated Weekly Conversion:", color = Color.Gray, fontSize = 11.sp)
                                val conversion = if (activeCount > 0) "${activeCount * 3.4f}%" else "0.0%"
                                Text(conversion, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131814)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Growth", tint = Color(0xFF81C784), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Conversion & Engagement Pulse", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Running social campaigns builds deep visual brand loyalty, converting temporary listeners into hardcore fans over time.",
                                color = Color.LightGray,
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
            else -> {
                // Campaigns Tab (Manage existing active ones)
                if (state.activeAdCampaigns.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131814)),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp).fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "No Ads", tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Active Ad Campaigns", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Explore the Advertisers tab to launch targeted promotions and gain thousands of fans weekly.", color = Color.LightGray, fontSize = 9.sp, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        items(state.activeAdCampaigns.toList()) { campaign ->
                            val title = campaign.agencyName
                            val cost = campaign.weeklyBudget

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B241C)),
                                border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Cost: $${String.format("%,.0f", cost)}/week (Deducted from Campaign budget)", color = Color.LightGray, fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.haltAdCampaign(campaign.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f), contentColor = Color.Red),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("STOP", fontSize = 8.sp, fontWeight = FontWeight.Bold)
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

data class AdvertiserConfig(
    val id: String,
    val title: String,
    val cost: Double,
    val desc: String,
    val tag: String
)

@Composable
fun MerchStudioAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTab by remember { mutableStateOf("Catalog") }
    val tabs = listOf("Catalog", "Design Studio")

    // Form states for Design Studio
    var merchName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("T-Shirts") }

    val typeUnitCosts = mapOf(
        "Caps" to 4.0,
        "T-Shirts" to 6.0,
        "Hoodies" to 12.0,
        "Vinyls" to 15.0
    )
    val typeDesignFees = mapOf(
        "Caps" to 1000.0,
        "T-Shirts" to 2500.0,
        "Hoodies" to 5000.0,
        "Vinyls" to 8000.0
    )

    val unitCost = typeUnitCosts[selectedType] ?: 5.0
    val designFee = typeDesignFees[selectedType] ?: 2000.0

    var sellPrice by remember(selectedType) { mutableStateOf(unitCost * 3.0) }
    var initialQuantity by remember { mutableStateOf(100) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E13)) // Deep violet-black stylish theme
            .padding(12.dp)
    ) {
        // App Header Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16141D)),
            border = BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("MERCHANDISE & STYLE STUDIO", color = Color(0xFFFF5722), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Merch Earnings", color = Color.Gray, fontSize = 9.sp)
                        val totalRev = state.merchStyles.sumOf { it.totalRevenue }
                        Text("$${String.format("%,.2f", totalRev)}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pocket Cash", color = Color.Gray, fontSize = 9.sp)
                        Text("$${String.format("%,.2f", state.cash)}", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Warehouse modifier status display
                val hasWarehouse = state.ownedHouses.any { it.name.contains("Warehouse", ignoreCase = true) }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (hasWarehouse) Color(0xFF1B5E20).copy(alpha = 0.15f) else Color(0xFF3E2723).copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (hasWarehouse) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = "Warehouse status",
                        tint = if (hasWarehouse) Color(0xFF4CAF50) else Color(0xFFFF9800),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (hasWarehouse) "Warehouse Logistics Boost Active (+25% selling rate!)" else "Standard Storage (Acquire a Warehouse to boost sales)",
                        color = if (hasWarehouse) Color(0xFF81C784) else Color(0xFFFFB74D),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Action Tabs
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tabName ->
                Button(
                    onClick = { selectedTab = tabName },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == tabName) Color(0xFF2C1E3F) else Color(0xFF141318),
                        contentColor = if (selectedTab == tabName) Color(0xFFFFD54F) else Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (selectedTab == tabName) Color(0xFFFFD54F).copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(32.dp).testTag("merch_tab_${tabName.lowercase().replace(" ", "_")}")
                ) {
                    Text(tabName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedTab == "Catalog") {
            if (state.merchStyles.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141318)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f)),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "No Merch", tint = Color.Gray, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Merch Launched Yet", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Head to the Design Studio tab to manufacture customized Hats, T-Shirts, Hoodies, or collectible Vinyl records. Target standard profit margins of 3x unit cost for maximum fan hype!",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(state.merchStyles) { style ->
                        var localPrice by remember(style.id) { mutableStateOf(style.sellPrice) }
                        var restockQty by remember { mutableStateOf(100) }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16141D)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Header: Title & Type Badge
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(style.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        val typeIcon = when (style.type) {
                                            "Caps" -> "🧢"
                                            "T-Shirts" -> "👕"
                                            "Hoodies" -> "🧥"
                                            "Vinyls" -> "💿"
                                            else -> "👕"
                                        }
                                        Text("$typeIcon ${style.type} (Unit Cost: $${String.format("%.2f", style.unitCost)})", color = Color.Gray, fontSize = 10.sp)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (style.stockRemaining > 0) Color(0xFF1B5E20).copy(alpha = 0.2f) else Color(0xFFB71C1C).copy(alpha = 0.2f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (style.stockRemaining > 0) "In Stock: ${style.stockRemaining}" else "OUT OF STOCK",
                                            color = if (style.stockRemaining > 0) Color(0xFF81C784) else Color(0xFFE57373),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Sales details
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Total Sold", color = Color.Gray, fontSize = 9.sp)
                                        Text("${style.totalSold} items", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Total Revenue", color = Color.Gray, fontSize = 9.sp)
                                        Text("$${String.format("%,.2f", style.totalRevenue)}", color = Color(0xFFFFD54F), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Current Price", color = Color.Gray, fontSize = 9.sp)
                                        Text("$${String.format("%.2f", style.sellPrice)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Pricing & Restock Panels
                                Text("MANAGE STYLING & LOGISTICS", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                // Price Adjustment slider
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Selling Price", color = Color.Gray, fontSize = 10.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("$${String.format("%.2f", localPrice)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            if (localPrice != style.sellPrice) {
                                                Button(
                                                    onClick = { viewModel.updateMerchPrice(style.id, localPrice) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                                    shape = RoundedCornerShape(4.dp),
                                                    modifier = Modifier.height(20.dp)
                                                ) {
                                                    Text("Apply", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                    Slider(
                                        value = localPrice.toFloat(),
                                        onValueChange = { localPrice = it.toDouble() },
                                        valueRange = (style.unitCost).toFloat()..(style.unitCost * 8.0).toFloat(),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFFFF5722),
                                            activeTrackColor = Color(0xFFFF5722),
                                            inactiveTrackColor = Color.Gray.copy(alpha = 0.2f)
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Restock panel
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Select Restock Volume", color = Color.Gray, fontSize = 9.sp)
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            listOf(50, 100, 250, 500).forEach { qty ->
                                                OutlinedButton(
                                                    onClick = { restockQty = qty },
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        containerColor = if (restockQty == qty) Color(0xFF2C1E3F) else Color.Transparent,
                                                        contentColor = if (restockQty == qty) Color(0xFFFFD54F) else Color.LightGray
                                                    ),
                                                    border = BorderStroke(1.dp, if (restockQty == qty) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.1f)),
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                                    modifier = Modifier.height(24.dp)
                                                ) {
                                                    Text("+$qty", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    val restockCost = style.unitCost * restockQty
                                    Button(
                                        onClick = { viewModel.restockMerchStyle(style.id, restockQty) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp).testTag("restock_btn_${style.id}")
                                    ) {
                                        Text("Restock ($${String.format("%.0f", restockCost)})", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                    }
                }
            }
        }
    }
}
        } else {
            // Design Studio Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF141318), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("CREATE A NEW PRODUCT DESIGN", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                // Name Input Field
                OutlinedTextField(
                    value = merchName,
                    onValueChange = { merchName = it },
                    label = { Text("Product Name (e.g., Midnight Hoodie)", fontSize = 10.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFF5722),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedLabelColor = Color(0xFFFF5722),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("merch_name_input")
                )

                // Select Type buttons
                Column {
                    Text("Select Product Category", color = Color.Gray, fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Caps", "T-Shirts", "Hoodies", "Vinyls").forEach { type ->
                            OutlinedButton(
                                onClick = { selectedType = type },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedType == type) Color(0xFF2C1E3F) else Color.Transparent,
                                    contentColor = if (selectedType == type) Color(0xFFFFD54F) else Color.LightGray
                                ),
                                border = BorderStroke(1.dp, if (selectedType == type) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.1f)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.weight(1f).height(28.dp).testTag("select_merch_type_$type")
                            ) {
                                val icon = when (type) {
                                    "Caps" -> "🧢"
                                    "T-Shirts" -> "👕"
                                    "Hoodies" -> "🧥"
                                    "Vinyls" -> "💿"
                                    else -> "👕"
                                }
                                Text("$icon $type", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Preset Fees & Unit costs
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1B22)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Upfront Setup Fee", color = Color.Gray, fontSize = 9.sp)
                            Text("$${String.format("%,.0f", designFee)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Unit Materials Cost", color = Color.Gray, fontSize = 9.sp)
                            Text("$${String.format("%.2f", unitCost)}/unit", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Suggested Price", color = Color.Gray, fontSize = 9.sp)
                            Text("$${String.format("%.2f", unitCost * 3.0)} (3x margin)", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Custom Selling Price Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Custom Retail Price", color = Color.Gray, fontSize = 10.sp)
                        Text("$${String.format("%.2f", sellPrice)}/item", color = Color(0xFFFF5722), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = sellPrice.toFloat(),
                        onValueChange = { sellPrice = it.toDouble() },
                        valueRange = (unitCost).toFloat()..(unitCost * 8.0).toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF5722),
                            activeTrackColor = Color(0xFFFF5722)
                        )
                    )
                }

                // Initial Stock order volume
                Column {
                    Text("Initial Manufacturing Quantity", color = Color.Gray, fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(50, 100, 250, 500, 1000).forEach { qty ->
                            OutlinedButton(
                                onClick = { initialQuantity = qty },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (initialQuantity == qty) Color(0xFF2C1E3F) else Color.Transparent,
                                    contentColor = if (initialQuantity == qty) Color(0xFFFFD54F) else Color.LightGray
                                ),
                                border = BorderStroke(1.dp, if (initialQuantity == qty) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.1f)),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier.weight(1f).height(24.dp).testTag("select_initial_qty_$qty")
                            ) {
                                Text("$qty pcs", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                Spacer(modifier = Modifier.height(4.dp))

                // Launch Total Estimator & Run button
                val initialStockCost = unitCost * initialQuantity
                val totalCost = designFee + initialStockCost

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Launch Upfront Cost", color = Color.Gray, fontSize = 9.sp)
                        Text("$${String.format("%,.2f", totalCost)}", color = if (state.cash >= totalCost) Color.White else Color.Red, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Button(
                        onClick = {
                            if (merchName.isBlank()) {
                                viewModel.showToast("Please enter a style name!")
                            } else {
                                viewModel.designMerchStyle(merchName, selectedType, unitCost, sellPrice, initialQuantity)
                                merchName = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = state.cash >= totalCost,
                        modifier = Modifier.height(36.dp).testTag("launch_merch_style_btn")
                    ) {
                        Text("LAUNCH BRAND LINE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }
            }
        }
    }
}

// =================== 21. LEGACY APP SCREEN ===================
@Composable
fun LegacyAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Gold Title Section
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Legacy Logo", tint = Color(0xFFFFD700), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Career Legacy Hub", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Immortalize your name across generations. Establish foundations, publish memoirs, and influence new generations of musical minds.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            // Legacy Stats Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "IMPACT METRICS", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Artists Inspired", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(text = "${state.artistsInspiredCount}", color = Color(0xFFFFD700), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Scholarships Foundation", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(
                                text = if (state.hasScholarships) "ACTIVE (-$7.5K/wk)" else "INACTIVE",
                                color = if (state.hasScholarships) Color.Green else Color.Red,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Wikipedia Card
        item {
            LegacyMilestoneItem(
                title = "Wikipedia Biography Page",
                description = "Publish an official peer-reviewed entry documenting your achievements.",
                requirement = "Requires 100,000+ Fans & $10,000 cash.",
                isUnlocked = state.hasWikipediaPage,
                canUnlock = state.fans >= 100000 && state.cash >= 10000.0,
                onUnlock = { viewModel.buyWikipediaPage() },
                buttonTag = "unlock_wiki_btn"
            )
        }

        // Autobiography Card
        item {
            LegacyMilestoneItem(
                title = "National Biography Memoir",
                description = "Author an intimate autobiography detail of your struggles and rise. Yields $1,200/wk legacy royalties.",
                requirement = "Requires 250,000+ Fans & $25,000 cash.",
                isUnlocked = state.hasBiography,
                canUnlock = state.fans >= 250000 && state.cash >= 25000.0,
                onUnlock = { viewModel.buyBiography() },
                buttonTag = "unlock_biography_btn"
            )
        }

        // Documentary Card
        item {
            LegacyMilestoneItem(
                title = "Worldwide Career Documentary",
                description = "Release a cinematic behind-the-scenes documentary highlighting your tour life. Yields $4,500/wk legacy royalties.",
                requirement = "Requires 500,000+ Fans & $75,000 cash.",
                isUnlocked = state.hasDocumentary,
                canUnlock = state.fans >= 500000 && state.cash >= 75000.0,
                onUnlock = { viewModel.buyDocumentary() },
                buttonTag = "unlock_doc_btn"
            )
        }

        // Netflix Series Card
        item {
            LegacyMilestoneItem(
                title = "Netflix Episodic Series Deal",
                description = "Partner with Netflix for a high-budget 6-part biopic series. Yields $15,000/wk legacy royalties.",
                requirement = "Requires 1,000,000+ Fans & $250,000 cash.",
                isUnlocked = state.hasNetflixSeries,
                canUnlock = state.fans >= 1000000 && state.cash >= 250000.0,
                onUnlock = { viewModel.buyNetflixSeries() },
                buttonTag = "unlock_netflix_btn"
            )
        }

        // Scholarships Toggle
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Scholarships Foundation", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Contribute $7,500 weekly to train aspiring underprivileged artists. Earns a 3% tax write-off and boosts your charity fan multiplier to 2.5x!",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = state.hasScholarships,
                        onCheckedChange = { viewModel.toggleScholarships() },
                        modifier = Modifier.testTag("scholarships_switch")
                    )
                }
            }
        }

        // Museum Card
        item {
            LegacyMilestoneItem(
                title = "Permanent Career Museum",
                description = "Erect a state-of-the-art archives museum filled with your instruments, stage outfits, and lyrics papers.",
                requirement = "Requires 5,000,000+ Fans & $1,000,000 cash.",
                isUnlocked = state.hasMuseum,
                canUnlock = state.fans >= 5000000 && state.cash >= 1000000.0,
                onUnlock = { viewModel.buyMuseum() },
                buttonTag = "unlock_museum_btn"
            )
        }

        // Hall of Fame Card
        item {
            LegacyMilestoneItem(
                title = "National Music Hall of Fame",
                description = "Secure your plaque in the sacred music hall of fame. Solidify eternal legendary status.",
                requirement = "Requires 10,000,000+ Fans & $500,000 lobbying cash.",
                isUnlocked = state.hasHallOfFame,
                canUnlock = state.fans >= 10000000 && state.cash >= 500000.0,
                onUnlock = { viewModel.buyHallOfFame() },
                buttonTag = "unlock_halloffame_btn"
            )
        }
    }
}

@Composable
fun LegacyMilestoneItem(
    title: String,
    description: String,
    requirement: String,
    isUnlocked: Boolean,
    canUnlock: Boolean,
    onUnlock: () -> Unit,
    buttonTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (isUnlocked) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "UNLOCKED", color = Color(0xFFFFD700), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(text = "LOCKED", color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = requirement, color = if (canUnlock || isUnlocked) Color(0xFFFFD700) else Color.Red.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)

            if (!isUnlocked) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onUnlock,
                    enabled = canUnlock,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp).testTag(buttonTag)
                ) {
                    Text(text = "ESTABLISH MILESTONE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// =================== 22. SECURITY APP SCREEN ===================
@Composable
fun SecurityAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Security Logo", tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Aegis Tactical Security", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Protect your assets from hackers, phone thieves, studio robberies, and data leaks. Retain elite corporate defense and cyber-shields.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "CURRENT THREAT LEVEL", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val threatDesc = if (state.hiredSecurityCompany == "Aegis Elite Command") "MINIMAL (Protected by Aegis)"
                                     else if (state.hiredSecurityCompany == "Vanguard Tactical") "MODERATE (Protected by Vanguard)"
                                     else "HIGH (No contracted Security Agency)"
                    val threatColor = if (state.hiredSecurityCompany == "Aegis Elite Command") Color.Green
                                      else if (state.hiredSecurityCompany == "Vanguard Tactical") Color.Yellow
                                      else Color.Red

                    Text(text = threatDesc, color = threatColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Retained Firm: ${state.hiredSecurityCompany.ifEmpty { "None (Solo Security)" }}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Security Options Selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "HIRE SECURITY CONTRACTORS", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                // Tier 1: Vanguard Tactical
                SecurityRetainerItem(
                    name = "Vanguard Tactical Security",
                    cost = "$5,000/week",
                    perks = "Shields studio from basic burglaries, reduces phone theft damages by 60%, and decreases house maintenance bills.",
                    isSelected = state.hiredSecurityCompany == "Vanguard Tactical",
                    onSelect = { viewModel.hireSecurityCompany("Vanguard Tactical") },
                    buttonTag = "hire_vanguard_btn"
                )

                // Tier 2: Aegis Elite
                SecurityRetainerItem(
                    name = "Aegis Elite Command Force",
                    cost = "$15,000/week",
                    perks = "Full immunity from studio robberies, phone snatchers, and data blackmail. Provides premium protection and massive house upkeep discounts.",
                    isSelected = state.hiredSecurityCompany == "Aegis Elite Command",
                    onSelect = { viewModel.hireSecurityCompany("Aegis Elite Command") },
                    buttonTag = "hire_aegis_btn"
                )

                // Cancel Retainer
                if (state.hiredSecurityCompany.isNotEmpty()) {
                    Button(
                        onClick = { viewModel.hireSecurityCompany("") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f), contentColor = Color.Red),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("cancel_security_btn")
                    ) {
                        Text("CANCEL ALL SECURITY CONTRACTS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Insurances Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "LIABILITY & LOSS INSURANCE COVERS", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                // Cyber Insurance
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Cyber-Shield Security Cover", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Premium: $850/week. Reimburses 100% of crypto-wallet or bank account hacking losses automatically.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = state.hasCyberInsurance,
                            onCheckedChange = { viewModel.toggleCyberInsurance() },
                            modifier = Modifier.testTag("cyber_insurance_switch")
                        )
                    }
                }

                // Studio Insurance
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Studio-Guard Instrument Cover", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Premium: $1,200/week. Reimburses 100% of studio hardware losses and vintage guitar theft damages.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = state.hasStudioInsurance,
                            onCheckedChange = { viewModel.toggleStudioInsurance() },
                            modifier = Modifier.testTag("studio_insurance_switch")
                        )
                    }
                }
            }
        }

        // Account Recovery Section
        item {
            val lostEntry = state.spiritualJournal.find { it.startsWith("phone_stolen_followers_lost:") }
            val hasLostFollowers = lostEntry != null
            val lostAmount = lostEntry?.substringAfter("phone_stolen_followers_lost:")?.toLongOrNull() ?: 0L

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "SOCIAL ACCOUNT RECOVERY MODULE", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Card(
                    colors = CardDefaults.cardColors(containerColor = if (hasLostFollowers) Color(0xFF3E121E) else Color(0xFF1B1A24)),
                    shape = RoundedCornerShape(12.dp),
                    border = if (hasLostFollowers) BorderStroke(1.dp, Color.Red) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasLostFollowers) Icons.Default.Warning else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (hasLostFollowers) Color.Red else Color.Green,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasLostFollowers) "⚠️ RECOVERY ATTENTION REQUIRED" else "🛡️ ACCOUNTS SECURE",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (hasLostFollowers) {
                            Text(
                                text = "Your smartphone was stolen, resulting in unauthorized access. Cryptographic authentication is required to recover ${String.format("%,d", lostAmount)} followers.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Verification Fee: ${if (state.hasCyberInsurance) "FREE (Cyber Cover Active)" else "$1,500"}",
                                color = if (state.hasCyberInsurance) Color.Green else Color.Yellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.recoverStolenAccount() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.fillMaxWidth().testTag("recover_social_btn")
                            ) {
                                Text("RECLAIM STOLEN ACCOUNTS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }
                        } else {
                            Text(
                                text = "Two-factor authentication is active on all linked accounts. Cryptographic key signatures match and no rogue access is detected on CastSocial or MeTube.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Genesis Crypto Asset Recovery Section
        item {
            val stolenCryptoEntry = state.spiritualJournal.find { it.startsWith("genesis_crypto_stolen:") }
            val hasStolenCrypto = stolenCryptoEntry != null
            val stolenUsd = stolenCryptoEntry?.substringAfter("genesis_crypto_stolen:")?.toDoubleOrNull() ?: 0.0
            val securityStaff = state.hiredEmployees.find { it.role == "Security" && it.loyalty > 25f }
            val hasProtector = state.hiredSecurityCompany.isNotEmpty() || state.hasCyberInsurance || securityStaff != null

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "GENESIS CRYPTO RECOVERY & FORENSICS MODULE", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Card(
                    colors = CardDefaults.cardColors(containerColor = if (hasStolenCrypto) Color(0xFF3E121E) else Color(0xFF1B1A24)),
                    shape = RoundedCornerShape(12.dp),
                    border = if (hasStolenCrypto) BorderStroke(1.dp, Color.Red) else null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasStolenCrypto) Icons.Default.Warning else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (hasStolenCrypto) Color.Red else Color.Green,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (hasStolenCrypto) "🚨 GENESIS WALLET COMPROMISED" else "🛡️ CRYPTO WALLET SECURE",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (hasStolenCrypto) {
                            Text(
                                text = "On-chain exploit detected! $${String.format("%,.2f", stolenUsd)} in crypto assets were redirected to an unauthorized address. Forensics tracing is required to recover funds.",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Recovery Status: ${if (hasProtector) "Shield Active (${state.hiredSecurityCompany.ifEmpty { securityStaff?.name ?: "Cyber-Shield" }})" else "NO ACTIVE SHIELD / SECURITY (Recovery Locked)"}",
                                color = if (hasProtector) Color.Green else Color.Yellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.recoverStolenCrypto() },
                                colors = ButtonDefaults.buttonColors(containerColor = if (hasProtector) Color(0xFF00E5FF) else Color.DarkGray),
                                enabled = hasProtector,
                                modifier = Modifier.fillMaxWidth().testTag("recover_crypto_btn")
                            ) {
                                Text(
                                    "RECOVER STOLEN CRYPTO COINS",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (hasProtector) Color.Black else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            Text(
                                text = "Genesis multi-signature cold storage is intact. No rogue smart contract approvals or unconfirmed mempool drainers detected on the ledger.",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityRetainerItem(
    name: String,
    cost: String,
    perks: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    buttonTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.08f) else Color(0xFF1B1A24)),
        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color.Transparent),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = cost, color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = perks, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color.Green.copy(alpha = 0.2f) else Color(0xFF00E5FF),
                    contentColor = if (isSelected) Color.Green else Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp).testTag(buttonTag)
            ) {
                Text(text = if (isSelected) "RETAINED AGENCY" else "SIGN RETAINER CONTRACT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// =================== 23. UNDERWORLD APP SCREEN ===================
@Composable
fun UnderworldAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0505))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C).copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFB71C1C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Syndicate Logo", tint = Color(0xFFB71C1C), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Underworld Syndicate", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RISK WARNING: These illegal shortcuts offer rapid wealth and metric inflation but carry massive penalties. IRS audits, whistleblowers, and platform bans await the reckless.",
                        color = Color(0xFFFFB3B3),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "SYNDICATE REPUTATION", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Active Tax Debt", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(text = "$${String.format("%,.0f", state.taxDebt)}", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Integrity", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(text = "${String.format("%.1f", state.integrity)}%", color = if (state.integrity > 50f) Color.Green else Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Crime Contracts Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "ACTIVE SYNDICATE CONTRACTS", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

                // 1. Money Laundering
                UnderworldContractItem(
                    title = "Launder Dirty Capital",
                    description = "Clean dirty money to gain clean cash, but increase tax audit risks.",
                    cost = "Costs $5,000 upfront. Cleans and adds $50,000 cash.",
                    consequence = "20% risk of IRS Audit: adds $25,000 Tax Debt & drops Public Image by -15.",
                    buttonText = "LAUNDER $5,000",
                    onAction = { viewModel.runUnderworldAction("MONEY_LAUNDERING") },
                    buttonTag = "launder_cash_btn"
                )

                // 2. Click Farm Streams
                UnderworldContractItem(
                    title = "Deploy Russian Click Farms",
                    description = "Inflate your streaming charts with millions of robotic song streams.",
                    cost = "Costs $10,000 upfront. Drastically boosts streaming algorithms.",
                    consequence = "30% risk of platform ban: exposure deletes streams, wipes -15% fans, and drops public image.",
                    buttonText = "BUY FAKE STREAMS ($10K)",
                    onAction = { viewModel.runUnderworldAction("FRAUD_STREAMS") },
                    buttonTag = "fake_streams_btn"
                )

                // 3. Fake Followers
                UnderworldContractItem(
                    title = "Purchase Bot Followers Farm",
                    description = "Deploy 150,000 bot accounts to follow your Cast Social page instantly.",
                    cost = "Costs $3,000 upfront. Gained +150,000 followers.",
                    consequence = "25% risk of Bot Sweep: purges bots and adds +15 Negative Sentiment.",
                    buttonText = "BUY BOTS ($3K)",
                    onAction = { viewModel.runUnderworldAction("FAKE_FOLLOWERS") },
                    buttonTag = "fake_followers_btn"
                )

                // 4. Bribery
                UnderworldContractItem(
                    title = "Bribe Award Committee Voter",
                    description = "Pay an insider judge to secure your next awards nominations.",
                    cost = "Costs $35,000 upfront. Secures +25 Critical Acclaim & +15 Industry Respect.",
                    consequence = "15% risk of Whistleblower Exposure: drops public image by -50 and integrity by -40.",
                    buttonText = "BRIBE JUDGE ($35K)",
                    onAction = { viewModel.runUnderworldAction("BRIBERY") },
                    buttonTag = "bribe_judge_btn"
                )

                // 5. Tax Evasion
                UnderworldContractItem(
                    title = "Offshore Tax Shield Shelter",
                    description = "Dodge the tax authorities by hiding 50% of your total tax liabilities.",
                    cost = "Costs $12,000 upfront. Erases 50% of active Tax Debt.",
                    consequence = "25% risk of exposure: drops integrity by -25 and doubles remaining Tax Debt.",
                    buttonText = "SHIELD ASSETS ($12K)",
                    onAction = { viewModel.runUnderworldAction("TAX_EVASION") },
                    buttonTag = "tax_evasion_btn"
                )

                // 6. Counterfeit Merch
                UnderworldContractItem(
                    title = "Bootleg Counterfeit Merch Line",
                    description = "Siphon fans by selling low-grade bootleg hoodies in parking lots.",
                    cost = "Costs $5,000 setup. Instantly produces $20,000 clean cash.",
                    consequence = "Guaranteed drop of -15 Trustworthiness due to poor quality fabric complaints.",
                    buttonText = "BOOTLEG SHIRTS ($5K)",
                    onAction = { viewModel.runUnderworldAction("COUNTERFEIT") },
                    buttonTag = "counterfeit_merch_btn"
                )
            }
        }
    }
}

@Composable
fun UnderworldContractItem(
    title: String,
    description: String,
    cost: String,
    consequence: String,
    buttonText: String,
    onAction: () -> Unit,
    buttonTag: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF160E0E)),
        border = BorderStroke(1.dp, Color(0xFFB71C1C).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "IMMEDIATE RETURN:", color = Color.Green, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = cost, color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "RISK / CONSEQUENCE:", color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = consequence, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C), contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp).testTag(buttonTag)
            ) {
                Text(text = buttonText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

// =================== 21. CHRONICLE WORLD ARCHIVES SCREEN ===================
@Composable
fun ChronicleAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedStrength by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("ALL", "WORLD", "INDUSTRY", "MEDIA", "BUSINESS", "FINANCIAL", "RELATIONSHIP", "PERSONAL", "COMMUNITY")
    val strengths = listOf("ALL", "TINY", "SMALL", "IMPORTANT", "MAJOR", "LIFE_CHANGING", "HISTORIC")

    // Filtered Memories
    val filteredMemories = state.worldHistory.filter { entry ->
        val matchesCategory = selectedCategory == "ALL" || entry.memoryType.equals(selectedCategory, ignoreCase = true)
        val matchesStrength = selectedStrength == "ALL" || entry.strength.equals(selectedStrength, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() ||
                entry.title.contains(searchQuery, ignoreCase = true) ||
                entry.description.contains(searchQuery, ignoreCase = true) ||
                (entry.targetNpcName != null && entry.targetNpcName.contains(searchQuery, ignoreCase = true)) ||
                (entry.targetBusinessName != null && entry.targetBusinessName.contains(searchQuery, ignoreCase = true)) ||
                (entry.targetSongTitle != null && entry.targetSongTitle.contains(searchQuery, ignoreCase = true))

        matchesCategory && matchesStrength && matchesSearch
    }.sortedWith(compareByDescending<WorldHistoryEntry> { it.year }.thenByDescending { it.week })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E17))
            .padding(12.dp)
    ) {
        // Stats Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
            border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CHRONICLE ECOSYSTEM",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonPurple,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Total Footprints: ${state.worldHistory.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Active Influence: ${state.worldHistory.count { it.decayWeeksRemaining > 0 || it.isPermanent }} events",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Small iconic status of current economy/trends
                Column(horizontalAlignment = Alignment.End) {
                    val activeEco = state.worldHistory.firstOrNull { it.memoryType == "WORLD" && it.title.contains("Economic", ignoreCase = true) && it.decayWeeksRemaining > 0 }
                    if (activeEco != null) {
                        Text(
                            text = if (activeEco.valueAffected > 0) "📈 BOOMING" else "📉 RECESSION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeEco.valueAffected > 0) Color.Green else Color.Red
                        )
                    } else {
                        Text(
                            text = "⚖️ STABLE ECON",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val activeGenre = state.worldHistory.firstOrNull { it.memoryType == "INDUSTRY" && it.title.contains("Genre", ignoreCase = true) && it.decayWeeksRemaining > 0 }
                    if (activeGenre != null) {
                        Text(
                            text = activeGenre.title.substringAfter("Surge: "),
                            fontSize = 10.sp,
                            color = NeonPink,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text = "Standard Trends",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search historical records...", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("chronicle_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedContainerColor = Color(0xFF1B1A24),
                unfocusedContainerColor = Color(0xFF1B1A24)
            ),
            shape = RoundedCornerShape(8.dp),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color.White.copy(alpha = 0.4f))
            }
        )

        // Category Horizontal Selector
        Text(
            text = "FILTER CATEGORY",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) NeonPurple else Color(0xFF1B1A24))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("filter_cat_$cat")
                ) {
                    Text(
                        text = cat,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Strength Horizontal Selector
        Text(
            text = "FILTER IMPACT STRENGTH",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(strengths) { str ->
                val isSelected = selectedStrength == str
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) NeonPurple.copy(alpha = 0.8f) else Color(0xFF1B1A24))
                        .clickable { selectedStrength = str }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("filter_str_$str")
                ) {
                    Text(
                        text = str,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Timeline Records List
        Text(
            text = "ARCHIVAL RECORDS",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (filteredMemories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Empty",
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching archives found",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "History generates dynamically as actions occur.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMemories) { memory ->
                    ChronicleMemoryCard(memory)
                }
            }
        }
    }
}

@Composable
fun ChronicleMemoryCard(memory: WorldHistoryEntry) {
    // Styling colors based on category
    val catColor = when (memory.memoryType.uppercase()) {
        "WORLD" -> Color(0xFF9C27B0) // NeonPurple
        "INDUSTRY" -> Color(0xFFE91E63) // NeonPink
        "MEDIA" -> Color(0xFF2196F3) // NeonBlue
        "BUSINESS" -> Color(0xFFFF9800) // Gold/Orange
        "FINANCIAL" -> Color(0xFF4CAF50) // NeonGreen
        "RELATIONSHIP" -> Color(0xFF00E5FF) // NeonTeal
        "PERSONAL" -> Color(0xFFFFEB3B) // Yellow
        "COMMUNITY" -> Color(0xFFFF5722) // Coral
        else -> Color.Gray
    }

    val catIcon = when (memory.memoryType.uppercase()) {
        "WORLD" -> Icons.Default.Public
        "INDUSTRY" -> Icons.Default.TrendingUp
        "MEDIA" -> Icons.Default.PhotoCamera
        "BUSINESS" -> Icons.Default.AccountBalance
        "FINANCIAL" -> Icons.Default.AccountBalanceWallet
        "RELATIONSHIP" -> Icons.Default.Group
        "PERSONAL" -> Icons.Default.Favorite
        "COMMUNITY" -> Icons.Default.Star
        else -> Icons.Default.Info
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1A24)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().testTag("chronicle_card_${memory.id}")
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Category color bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(catColor)
                    .align(Alignment.CenterVertically)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = catIcon,
                            contentDescription = memory.memoryType,
                            tint = catColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = memory.memoryType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = catColor
                        )
                    }

                    Text(
                        text = "Y${memory.year} W${memory.week}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Title
                Text(
                    text = memory.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description
                Text(
                    text = memory.description,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Impact badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(catColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "STRENGTH: ${memory.strength}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = catColor
                        )
                    }

                    // Decay Status badge
                    val isPermanent = memory.isPermanent || memory.strength == "HISTORIC"
                    val isExpired = memory.decayWeeksRemaining <= 0 && !isPermanent
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isPermanent) Color(0xFF4CAF50).copy(alpha = 0.15f)
                                else if (isExpired) Color.Red.copy(alpha = 0.15f)
                                else Color(0xFFFF9800).copy(alpha = 0.15f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isPermanent) "PERMANENT ARCHIVE"
                            else if (isExpired) "SYSTEM INFLUENCE: FADED"
                            else "ACTIVE INFLUENCE: ${memory.decayWeeksRemaining}W REMAINING",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPermanent) Color(0xFF4CAF50)
                            else if (isExpired) Color.Red
                            else Color(0xFFFF9800)
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun TechStoreScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onPhoneBought: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF11141A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(48.dp).padding(top = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "DEVICE REPORTED STOLEN",
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You cannot access your digital life. Purchase a replacement smartphone to regain access.",
            color = Color.LightGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Tech Store Devices",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Option 1: hiPhone
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232E)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().clickable {
                viewModel.buyPhone("hiPhone", 1700.0)
            }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("hiPhone 15 Pro", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Premium OS • High Security", color = Color.Gray, fontSize = 12.sp)
                }
                Text("$1,700", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Option 2: TECNOO
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232E)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().clickable {
                viewModel.buyPhone("TECNOO", 500.0)
            }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("TECNOO Spark", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Basic OS • Standard Capabilities", color = Color.Gray, fontSize = 12.sp)
                }
                Text("$500", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun OSUpgradeAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    val scrollState = rememberScrollState()

    data class PhoneOption(val id: String, val name: String, val price: Double, val perkDesc: String)
    val phones = listOf(
        PhoneOption("TECCNO", "TECCNO Spark", 500.0, "Basic smartphone. No special perks."),
        PhoneOption("Xiaomax", "Xiaomax HyperOS", 1500.0, "Faster Processing. Energy costs for social media & MeTube reduced by up to 50%."),
        PhoneOption("No-Thing", "No-Thing GLYPH", 3000.0, "Zen Interface. Passive stress reduction every week (-1 stress)."),
        PhoneOption("Pixxelate", "Pixxelate Pro", 6000.0, "Pro Camera. Social media posts gain 20% more fans."),
        PhoneOption("Samsong", "Samsong Galaxy", 10000.0, "Multitasking Beast & Unhackable Enclave. Digital streaming revenue +10%, completely unhackable hardware security."),
        PhoneOption("hiPhone", "hiPhone 16 Pro", 15000.0, "Ultimate Status & Unhackable Enclave. Capital club gains +15%, completely unhackable biometric security.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SystemUpdate,
            contentDescription = null,
            tint = Color(0xFF6200EA),
            modifier = Modifier.size(48.dp).padding(top = 8.dp, bottom = 16.dp)
        )
        Text(
            text = "OS UPGRADE STORE",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
        Text(
            text = "Upgrade your device for unique perks & OS experiences.",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        phones.forEach { phone ->
            val isOwned = state.ownedPhones.contains(phone.id)
            val isActive = state.activePhone == phone.id

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .border(
                        width = if (isActive) 2.dp else 0.dp,
                        color = if (isActive) Color(0xFF6200EA) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = phone.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (!isOwned) {
                            Text(
                                text = "$${java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(phone.price)}",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        } else if (isActive) {
                            Text(
                                text = "ACTIVE",
                                color = Color(0xFF6200EA),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        } else {
                            Text(
                                text = "OWNED",
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = phone.perkDesc,
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (!isOwned) {
                                viewModel.buyPhone(phone.id, phone.price)
                            } else if (!isActive) {
                                viewModel.equipPhone(phone.id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) Color.DarkGray else if (isOwned) Color(0xFF6200EA) else Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isActive
                    ) {
                        Text(
                            text = if (isActive) "Currently Equipped" else if (isOwned) "Equip Device" else "Purchase Device",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
