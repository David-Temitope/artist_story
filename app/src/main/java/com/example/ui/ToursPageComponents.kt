package com.example.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ReliableAsyncImage as AsyncImage
import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.data.NPCEntity
import com.example.data.Tour
import com.example.data.playerLevel
import com.example.data.parseTourMarkets
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBg
import com.example.ui.theme.DangerColor
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonTeal
import com.example.ui.theme.PrimaryBrandColor
import com.example.ui.theme.PrimaryBrandLight
import com.example.ui.theme.SurfaceColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.io.File

/**
 * High-fidelity Tours and Live Performance UI.
 * Communicates: location, venue, capacity, ticket sales, expected income,
 * expenses, reputation, audience size, fatigue, and opportunities.
 */
@Composable
fun EnhancedToursPage(
    state: GameStateEntity,
    viewModel: GameViewModel
) {
    if (state.playerLevel < 6) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth().testTag("tours_locked_card")
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Tours Locked",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "LIVE TOURING CIRCUIT CLOSED",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )

                        Text(
                            text = "Live touring operations remain closed until Level 6 of the game. Organizing cross-city concerts, hiring touring crews, and filling venues requires an established fanbase, catalog depth, and proven musicianship.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )

                        // Current Level vs Level 6 Progress
                        Surface(
                            color = SurfaceColor,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Artist Mastery Level:", fontSize = 12.sp, color = TextTertiary)
                                    Text("Level ${state.playerLevel} / Level 6", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                                }
                                LinearProgressIndicator(
                                    progress = { (state.playerLevel / 6f).coerceIn(0.1f, 1f) },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = AccentGold,
                                    trackColor = Color(0xFF232B42)
                                )
                                Text(
                                    "💡 How to Reach Level 6: Rehearse vocals & songwriting, write and release new singles/albums, grow your fans & followers, and expand your industry reputation!",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        return
    }

    var selectedTab by remember { mutableStateOf("Active") } // "Active", "BookTour", "Venues"

    val activeTours = state.tours.filter { it.status == "ACTIVE" }
    val bookedTours = state.tours.filter { it.status == "BOOKED" || it.status == "DRAFT" }
    val completedTours = state.tours.filter { it.status == "COMPLETED" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP HERO BANNER: World Tour Opportunity Hub
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, PrimaryBrandColor.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth().testTag("tours_hero_banner")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        PrimaryBrandColor.copy(alpha = 0.6f),
                                        NeonPurple.copy(alpha = 0.3f),
                                        Color(0xFF0F0B1E)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, PrimaryBrandLight.copy(alpha = 0.6f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Public,
                                            contentDescription = null,
                                            tint = PrimaryBrandLight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "LIVE CIRCUIT",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = TextPrimary
                                        )
                                    }
                                }

                                Text(
                                    text = "Week ${state.currentWeek}, Year ${state.currentYear}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column {
                                Text(
                                    text = "TOURS & LIVE ENGAGEMENTS",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Command the world stage • Craft setlists, hire elite crew & sell out arenas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // Quick Stats strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceColor)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ACTIVE TOURS", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                            Text("${activeTours.size}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBrandLight)
                        }
                        Column {
                            Text("SCHEDULED", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                            Text("${bookedTours.size}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AccentGold)
                        }
                        Column {
                            Text("COMPLETED RUNS", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                            Text("${completedTours.size}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = AccentGreen)
                        }
                        Column {
                            Text("CAREER TICKETS SOLD", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                            val totalTickets = state.tours.sumOf { it.totalTicketsSold }
                            Text(if (totalTickets > 0) String.format("%,d", totalTickets) else "--", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = NeonTeal)
                        }
                    }
                }
            }
        }

        // Navigation Tabs: Active & Booked, Plan New Tour, Venue Directory
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Triple("Active", "Tour Schedule", Icons.Default.DirectionsBus),
                    Triple("BookTour", "Plan New Tour", Icons.Default.Add),
                    Triple("Venues", "Venues Hub", Icons.Default.LocationOn)
                )

                tabs.forEach { (tabId, label, icon) ->
                    val isSelected = selectedTab == tabId
                    Button(
                        onClick = { selectedTab = tabId },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PrimaryBrandColor else Color.Transparent,
                            contentColor = if (isSelected) Color.White else TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("tour_tab_$tabId")
                    ) {
                        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        when (selectedTab) {
            "Active" -> {
                if (state.tours.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBrandLight.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AirplaneTicket,
                                        contentDescription = null,
                                        tint = PrimaryBrandLight,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Text(
                                    text = "No Active Tours on Schedule",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Every tour is a defining moment to monetize your catalog, build legendary stage reputation, and establish yourself as an unforgettable headliner.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { selectedTab = "BookTour" },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Plan a Realistic Tour", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Active Tours List
                    if (activeTours.isNotEmpty()) {
                        item {
                            Text(
                                "CURRENTLY ON THE ROAD",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentGreen
                            )
                        }
                        items(activeTours) { tour ->
                            TourOpportunityCard(
                                tour = tour,
                                state = state,
                                isActive = true,
                                onPromote = { viewModel.promoteTour(tour.id, 1000.0) },
                                onLaunch = { viewModel.launchTour(tour.id) }
                            )
                        }
                    }

                    // Booked / Scheduled Tours
                    if (bookedTours.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "SCHEDULED / PRE-LAUNCH TOURS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentGold
                            )
                        }
                        items(bookedTours) { tour ->
                            TourOpportunityCard(
                                tour = tour,
                                state = state,
                                isActive = false,
                                onPromote = { viewModel.promoteTour(tour.id, 1000.0) },
                                onLaunch = { viewModel.launchTour(tour.id) }
                            )
                        }
                    }

                    // Completed Tours
                    if (completedTours.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "TOUR ARCHIVE & HISTORY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextTertiary
                            )
                        }
                        items(completedTours) { tour ->
                            TourOpportunityCard(
                                tour = tour,
                                state = state,
                                isActive = false,
                                onPromote = { },
                                onLaunch = { }
                            )
                        }
                    }
                }
            }

            "BookTour" -> {
                item {
                    TourPlanningModule(
                        state = state,
                        viewModel = viewModel,
                        onTourCreated = {
                            selectedTab = "Active"
                        }
                    )
                }
            }

            "Venues" -> {
                item {
                    VenueDirectoryView(
                        state = state,
                        onSelectVenue = { _ ->
                            selectedTab = "BookTour"
                        }
                    )
                }
            }
        }
    }
}

/**
 * Visual Tour Opportunity Card displaying realistic metrics:
 * Banner, Tour Type, Crew, Acts, Setlist, Merch Stand, 3-tier ticket pricing.
 */
@Composable
fun TourOpportunityCard(
    tour: Tour,
    state: GameStateEntity,
    isActive: Boolean,
    onPromote: () -> Unit,
    onLaunch: () -> Unit
) {
    val isDraft = tour.status == "DRAFT"
    val isBooked = tour.status == "BOOKED"
    val isCompleted = tour.status == "COMPLETED"

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDraft) Color(0xFF151824) else CardBg
        ),
        border = BorderStroke(
            1.2.dp,
            if (isActive) PrimaryBrandLight else if (isBooked) AccentGold.copy(alpha = 0.7f) else BorderColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tour_card_${tour.id}")
    ) {
        Column {
            // Tour Banner Header (if custom banner picked)
            if (!tour.bannerUri.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val bannerFile = File(tour.bannerUri)
                    val imageModel: Any = if (bannerFile.exists()) bannerFile else tour.bannerUri
                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Tour Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    // Overlay Title
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryBrandColor.copy(alpha = 0.85f)
                        ) {
                            Text(
                                text = tour.tourType.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = tour.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // If no banner, regular header
                if (tour.bannerUri.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (tour.tourType) {
                                        "Global Tour" -> NeonPurple.copy(alpha = 0.2f)
                                        "National Tour" -> PrimaryBrandLight.copy(alpha = 0.2f)
                                        "City Tour" -> NeonTeal.copy(alpha = 0.2f)
                                        else -> Color(0xFF22283A)
                                    }
                                ) {
                                    Text(
                                        text = tour.tourType.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = when (tour.tourType) {
                                            "Global Tour" -> NeonPurple
                                            "National Tour" -> PrimaryBrandLight
                                            "City Tour" -> NeonTeal
                                            else -> TextSecondary
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (tour.status) {
                                        "ACTIVE" -> "● LIVE ON TOUR"
                                        "BOOKED" -> "SCHEDULED (Wk ${tour.bookedWeek})"
                                        "DRAFT" -> "PROPOSED DRAFT"
                                        else -> "COMPLETED"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (tour.status) {
                                        "ACTIVE" -> AccentGreen
                                        "BOOKED" -> AccentGold
                                        "DRAFT" -> AccentGold
                                        else -> TextTertiary
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tour.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = TextPrimary
                            )
                            if (tour.routeMarkets.isNotEmpty()) {
                                Text(
                                    text = "Markets: ${tour.routeMarkets.joinToString(" → ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (isDraft) {
                            Button(
                                onClick = onLaunch,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("LAUNCH", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3-Tier Ticket Pricing Pill & Details
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TICKET PRICING TIERS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBrandLight, fontSize = 10.sp)
                            Text("Blended Avg: $${String.format("%.0f", tour.weightedAvgTicketPrice)}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = AccentGold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("General (70%)", fontSize = 10.sp, color = TextTertiary)
                                Text("$${tour.generalTicketPrice.toInt()}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                            }
                            Column {
                                Text("VIP Pass (25%)", fontSize = 10.sp, color = TextTertiary)
                                Text("$${tour.vipTicketPrice.toInt()}", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 13.sp)
                            }
                            Column {
                                Text("Platinum (5%)", fontSize = 10.sp, color = TextTertiary)
                                Text("$${tour.platinumTicketPrice.toInt()}", fontWeight = FontWeight.Bold, color = NeonPurple, fontSize = 13.sp)
                            }
                            Column {
                                Text("Stops", fontSize = 10.sp, color = TextTertiary)
                                Text("${tour.locationsCount} Cities", fontWeight = FontWeight.Bold, color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Guest Artists & Talent Lineup
                if (!tour.openingActName.isNullOrEmpty() || !tour.closingActName.isNullOrEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!tour.openingActName.isNullOrEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E2436),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text("OPENING ACT", fontSize = 8.sp, color = TextTertiary, fontWeight = FontWeight.Bold)
                                        Text(tour.openingActName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                        if (!tour.closingActName.isNullOrEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E2436),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AccentGold, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text("CLOSING ACT", fontSize = 8.sp, color = TextTertiary, fontWeight = FontWeight.Bold)
                                        Text(tour.closingActName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Crew & Production Highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1A1F2C),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = PrimaryBrandLight, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${tour.selectedCrewRoles.size} Crew Roles",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }
                    }

                    if (tour.setlistSongTitles.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1A1F2C),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = NeonTeal, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${tour.setlistSongTitles.size} Setlist Songs",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    if (tour.selectedMerchIds.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1A1F2C),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = AccentGold, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${tour.selectedMerchIds.size} Merch Items",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar if tour is in progress
                if (isActive) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tour Progress: Stop ${tour.currentWeek} of ${tour.totalWeeks}",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "${((tour.currentWeek.toFloat() / tour.totalWeeks.coerceAtLeast(1)) * 100).toInt()}% Complete",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBrandLight
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (tour.currentWeek.toFloat() / tour.totalWeeks.coerceAtLeast(1)).coerceIn(0f, 1f) },
                            color = PrimaryBrandLight,
                            trackColor = Color(0xFF1A1F30),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onPromote,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBrandColor.copy(alpha = 0.2f),
                            contentColor = PrimaryBrandLight
                        ),
                        border = BorderStroke(1.dp, PrimaryBrandLight),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BOOST TOUR PROMO (+$1,000 Hype Campaign)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isCompleted && tour.totalTicketsSold > 0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF152219),
                        border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TOTAL BOX OFFICE", fontSize = 9.sp, color = TextTertiary)
                                Text("$${String.format("%,.0f", tour.totalGrossRevenue)}", fontWeight = FontWeight.Bold, color = AccentGreen, fontSize = 13.sp)
                            }
                            Column {
                                Text("MERCH BOOTH SALES", fontSize = 9.sp, color = TextTertiary)
                                Text("$${String.format("%,.0f", tour.totalMerchRevenueEarned)}", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 13.sp)
                            }
                            Column {
                                Text("TOTAL ATTENDEES", fontSize = 9.sp, color = TextTertiary)
                                Text("${String.format("%,d", tour.totalTicketsSold)} fans", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Realistic Tour Planning & Production Studio.
 * Fulfills all 9 requirements:
 * 1. Tour banner (camera photos / gallery)
 * 2. Tour name
 * 3. Crew selection (with unique impacts)
 * 4. Opening & closing act hiring
 * 5. Setlist creation from recorded/released songs & albums
 * 6. Tour type selection (Club Run, City Tour, National Tour, Global Tour)
 * 7. Merch stand from player's catalog
 * 8. 3-tier ticket pricing (70% General, 25% VIP, 5% Platinum)
 * 9. Launch date selection
 */
@Composable
fun TourPlanningModule(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onTourCreated: () -> Unit
) {
    // 1. Tour Banner
    var selectedBannerUri by remember { mutableStateOf<String?>(state.savedPhotos.firstOrNull()) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedBannerUri = uri.toString()
        }
    }

    // 2. Tour Name
    var tourTitle by remember { mutableStateOf("${state.artistName} Live Across Continents") }
    var tourMarketsInput by remember { mutableStateOf("") }
    val routeMarkets = remember(tourMarketsInput) { parseTourMarkets(tourMarketsInput) }
    val tourCityCatalog = remember {
        linkedMapOf(
            "Nigeria" to listOf("Lagos", "Abuja", "Port Harcourt", "Ibadan", "Enugu"),
            "Ghana" to listOf("Accra", "Kumasi", "Tamale"),
            "Kenya" to listOf("Nairobi", "Mombasa", "Kisumu"),
            "South Africa" to listOf("Johannesburg", "Cape Town", "Durban"),
            "United Kingdom" to listOf("London", "Manchester", "Birmingham", "Glasgow"),
            "United States" to listOf("New York", "Los Angeles", "Atlanta", "Chicago", "Houston"),
            "Canada" to listOf("Toronto", "Montreal", "Vancouver"),
            "France" to listOf("Paris", "Lyon", "Marseille"),
            "Germany" to listOf("Berlin", "Hamburg", "Munich"),
            "India" to listOf("Mumbai", "Delhi", "Bengaluru"),
            "Japan" to listOf("Tokyo", "Osaka", "Kyoto"),
            "Australia" to listOf("Sydney", "Melbourne", "Brisbane"),
            "Brazil" to listOf("São Paulo", "Rio de Janeiro", "Brasília"),
            "Mexico" to listOf("Mexico City", "Guadalajara", "Monterrey"),
            "United Arab Emirates" to listOf("Dubai", "Abu Dhabi"),
            "Netherlands" to listOf("Amsterdam", "Rotterdam"),
            "Spain" to listOf("Madrid", "Barcelona"),
            "Italy" to listOf("Rome", "Milan"),
            "China" to listOf("Shanghai", "Beijing"),
            "South Korea" to listOf("Seoul", "Busan")
        )
    }
    var selectedTourCountry by remember { mutableStateOf("Nigeria") }
    var selectedTourCity by remember { mutableStateOf("Lagos") }
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var cityMenuExpanded by remember { mutableStateOf(false) }

    // 3. Tour Type
    var selectedTourType by remember { mutableStateOf("City Tour") } // "Club Run", "City Tour", "National Tour", "Global Tour"

    // 4. Crew Selection
    val allCrewRoles = listOf(
        CrewRoleDefinition("Tour Manager", 1200.0, Icons.Default.SupervisorAccount, "Boosts revenue retention (+15%), prevents venue disputes & cuts time overhead"),
        CrewRoleDefinition("Head of Security", 950.0, Icons.Default.Security, "Backstage defense, eliminates stage crasher incidents & prevents crowd lawsuits"),
        CrewRoleDefinition("Audio Engineer", 850.0, Icons.Default.GraphicEq, "Master acoustic EQ, elevates show ratings & word-of-mouth attendance (+18%)"),
        CrewRoleDefinition("Wardrobe Stylist", 700.0, Icons.Default.Checkroom, "Curates viral tour outfits, enhances public image (+2.5/wk) and fan adoration"),
        CrewRoleDefinition("Instrumentalist", 800.0, Icons.Default.MusicNote, "Live band authenticity, elevates critical acclaim & setlist song stream surge (+25%)"),
        CrewRoleDefinition("Dancers", 1100.0, Icons.Default.AccessibilityNew, "High-energy choreography & stage presence, massive visual spectacle (+15% hype)"),
        CrewRoleDefinition("Photographer & Videographer", 650.0, Icons.Default.CameraAlt, "Captures 4K recaps, generates viral reels on MeTube & organic social reach (+20%)"),
        CrewRoleDefinition("Medics", 750.0, Icons.Default.MedicalServices, "Vocal and physical health care, cuts performer energy drain by 35%"),
        CrewRoleDefinition("Driver", 600.0, Icons.Default.DirectionsBus, "Dedicated long-haul highway transit, eliminates delay risks and travel stress (-20%)")
    )
    val selectedCrew = remember { mutableStateListOf<String>("Tour Manager", "Head of Security", "Audio Engineer") }

    // 5. Opening and Closing Acts
    val availableArtists = remember(state.npcs) {
        state.npcs.filter { !it.isRetired && !it.id.startsWith("npc_family_") && !it.id.startsWith("npc_sen_") && !it.id.startsWith("npc_gov_") }
    }
    var selectedOpeningActNpc by remember { mutableStateOf<NPCEntity?>(null) }
    var selectedClosingActNpc by remember { mutableStateOf<NPCEntity?>(null) }

    // 6. Setlist Selection
    val catalogSongs = remember(state.songs, state.albums) {
        val releasedSingles = state.songs.filter { it.isReleased }
        val albumSongIds = state.albums.filter { it.isReleased }.flatMap { it.songIds }
        val albumSongs = state.songs.filter { it.id in albumSongIds }
        (releasedSingles + albumSongs).distinctBy { it.id }
    }
    val selectedSetlistSongIds = remember {
        mutableStateListOf<String>().apply {
            addAll(catalogSongs.take(4).map { it.id })
        }
    }

    // 7. Merch Stand Selection
    val selectedMerchIds = remember {
        mutableStateListOf<String>().apply {
            addAll(state.merchStyles.map { it.id })
        }
    }

    // 8. 3-Tier Ticket Pricing (Range-aware per tour type)
    var generalTicketPriceInput by remember { mutableStateOf("7") }
    var vipTicketPriceInput by remember { mutableStateOf("12") }
    var platinumTicketPriceInput by remember { mutableStateOf("18") }

    val switchTourType: (String) -> Unit = { type ->
        selectedTourType = type
        when (type) {
            "Club Run" -> {
                generalTicketPriceInput = "2"
                vipTicketPriceInput = "4"
                platinumTicketPriceInput = "6"
            }
            "City Tour" -> {
                generalTicketPriceInput = "7"
                vipTicketPriceInput = "12"
                platinumTicketPriceInput = "18"
            }
            "National Tour" -> {
                generalTicketPriceInput = "25"
                vipTicketPriceInput = "38"
                platinumTicketPriceInput = "50"
            }
            "Global Tour" -> {
                generalTicketPriceInput = "60"
                vipTicketPriceInput = "100"
                platinumTicketPriceInput = "150"
            }
        }
    }

    // 9. Launch Date Selection
    var launchDateSelectionWeeksAhead by remember { mutableStateOf(2) } // 1 (immediate), 2, 4, 8

    // Calculations
    val generalPrice = generalTicketPriceInput.toDoubleOrNull() ?: 7.0
    val vipPrice = vipTicketPriceInput.toDoubleOrNull() ?: 12.0
    val platinumPrice = platinumTicketPriceInput.toDoubleOrNull() ?: 18.0
    val weightedAvgTicket = (generalPrice * 0.70) + (vipPrice * 0.25) + (platinumPrice * 0.05)

    val (locsCount, totalWeeks, venueGuaranteeCost, baseVenueCap) = when (selectedTourType) {
        "Club Run" -> Quadruple(4, 3, 5000.0, 600)
        "City Tour" -> Quadruple(8, 5, 24000.0, 2800)
        "National Tour" -> Quadruple(14, 8, 80000.0, 12500)
        "Global Tour" -> Quadruple(22, 14, 260000.0, 50000)
        else -> Quadruple(4, 3, 5000.0, 600)
    }

    val weeklyCrewSalary = selectedCrew.sumOf { roleName ->
        allCrewRoles.find { it.roleName == roleName }?.weeklySalary ?: 500.0
    }
    val totalCrewCost = weeklyCrewSalary * totalWeeks

    val openingActFee = selectedOpeningActNpc?.let { (1000.0 + (it.popularity * 150.0)) } ?: 0.0
    val closingActFee = selectedClosingActNpc?.let { (2000.0 + (it.popularity * 250.0)) } ?: 0.0
    val totalTalentCost = openingActFee + closingActFee

    val promoBudget = 2500.0
    val productionCost = 10000.0
    val weeklyTransportCost = 3500.0
    val totalTransportCost = weeklyTransportCost * totalWeeks

    val rawSetupCost = venueGuaranteeCost + productionCost + totalTransportCost + totalCrewCost + totalTalentCost + promoBudget
    val carReduction = state.ownedCars.maxByOrNull { it.costReductionPercent }?.costReductionPercent ?: 0f
    val labelReduction = if (state.signedLabelId != null) 0.50f else 0f
    val finalSetupCost = rawSetupCost * (1f - carReduction - labelReduction).coerceAtLeast(0.1f)
    val canAfford = state.cash >= finalSetupCost

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, PrimaryBrandLight.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PLAN & BOOK A TOUR",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = TextPrimary
                    )
                    Text(
                        text = "Customize every operational detail before hitting the road",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimaryBrandLight.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "STEP-BY-STEP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBrandLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // ==========================================
            // 1. TOUR BANNER SELECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. TOUR BANNER",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBrandLight
                )

                // Banner Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141724))
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                ) {
                    if (selectedBannerUri != null) {
                        val bannerFile = File(selectedBannerUri!!)
                        val imageModel: Any = if (bannerFile.exists()) bannerFile else selectedBannerUri!!
                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Tour Banner Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                    )
                                )
                        )
                        Text(
                            text = tourTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Select a photo from Camera or Gallery for your tour poster", fontSize = 11.sp, color = TextTertiary)
                        }
                    }
                }

                // Camera photos picker row
                if (state.savedPhotos.isNotEmpty()) {
                    Text("From Camera Snapshots:", fontSize = 11.sp, color = TextSecondary)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.savedPhotos) { photoPath ->
                            val isSel = selectedBannerUri == photoPath
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        2.dp,
                                        if (isSel) PrimaryBrandLight else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedBannerUri = photoPath }
                            ) {
                                AsyncImage(
                                    model = File(photoPath),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (isSel) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = PrimaryBrandLight,
                                        modifier = Modifier.size(16.dp).align(Alignment.TopEnd).padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Gallery Button
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    border = BorderStroke(1.dp, PrimaryBrandLight.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBrandLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose from Device Gallery", fontSize = 12.sp, color = PrimaryBrandLight, fontWeight = FontWeight.Bold)
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 2. TOUR NAME
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "2. TOUR NAME",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBrandLight
                )

                OutlinedTextField(
                    value = tourTitle,
                    onValueChange = { tourTitle = it },
                    label = { Text("Tour Branding / Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBrandLight,
                        unfocusedBorderColor = BorderColor
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("tour_name_input")
                )

                // Quick suggestions
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Resonance World Run", "Neon Eclipse Tour", "Midnight Echoes Live", "The Uncut Circuit").forEach { suggestion ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceColor,
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.clickable { tourTitle = "${state.artistName}: $suggestion" }
                        ) {
                            Text(
                                text = "+ $suggestion",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().testTag("tour_markets_input")) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(onClick = { countryMenuExpanded = true }, modifier = Modifier.fillMaxWidth().testTag("tour_country_picker")) {
                            Text(selectedTourCountry, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose country")
                        }
                        DropdownMenu(expanded = countryMenuExpanded, onDismissRequest = { countryMenuExpanded = false }) {
                            tourCityCatalog.keys.forEach { country ->
                                DropdownMenuItem(text = { Text(country) }, onClick = {
                                    selectedTourCountry = country
                                    selectedTourCity = tourCityCatalog[country]?.firstOrNull().orEmpty()
                                    countryMenuExpanded = false
                                })
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(onClick = { cityMenuExpanded = true }, modifier = Modifier.fillMaxWidth().testTag("tour_city_picker")) {
                            Text(selectedTourCity, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose city")
                        }
                        DropdownMenu(expanded = cityMenuExpanded, onDismissRequest = { cityMenuExpanded = false }) {
                            tourCityCatalog[selectedTourCountry].orEmpty().forEach { city ->
                                DropdownMenuItem(text = { Text(city) }, onClick = {
                                    selectedTourCity = city
                                    cityMenuExpanded = false
                                })
                            }
                        }
                    }
                    Button(onClick = {
                        val market = "$selectedTourCity, $selectedTourCountry"
                        if (parseTourMarkets("$tourMarketsInput; $market").size > routeMarkets.size) {
                            tourMarketsInput = listOf(tourMarketsInput, market).filter { it.isNotBlank() }.joinToString("; ")
                        }
                    }, modifier = Modifier.testTag("add_tour_market")) { Text("Add") }
                }
                Text("Choose a country and city for each stop. Same-country tours compete for local demand.", color = TextSecondary, fontSize = 11.sp)
                if (routeMarkets.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(routeMarkets) { market ->
                            FilterChip(
                                selected = true,
                                onClick = { tourMarketsInput = routeMarkets.filterNot { it == market }.joinToString("; ") },
                                label = { Text("$market  ×") }
                            )
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 3. TOUR TYPE SELECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "3. TOUR TYPE & SCALE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBrandLight
                )

                val tourTypes = listOf(
                    Triple("Club Run", "4 Cities • 3 Wks", "600 Cap • Grassroots ($5k Guarantee) • Max Tickets: $1 – $6"),
                    Triple("City Tour", "8 Cities • 5 Wks", "2,800 Cap • Theatre Circuit ($24k Guarantee) • Max Tickets: $5 – $18"),
                    Triple("National Tour", "14 Cities • 8 Wks", "12,500 Cap • Arena Circuit ($80k Guarantee) • Max Tickets: $20 – $50"),
                    Triple("Global Tour", "22 Cities • 14 Wks", "50,000 Cap • World Stadiums ($260k Guarantee) • Max Tickets: $50 – $150")
                )

                tourTypes.forEach { (type, duration, desc) ->
                    val isSelected = selectedTourType == type
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PrimaryBrandLight.copy(alpha = 0.15f) else SurfaceColor,
                        border = BorderStroke(1.dp, if (isSelected) PrimaryBrandLight else BorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { switchTourType(type) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(type, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryBrandLight else TextPrimary, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color.Black.copy(alpha = 0.4f)) {
                                        Text(duration, fontSize = 9.sp, color = AccentGold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text(desc, fontSize = 11.sp, color = TextSecondary)
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { switchTourType(type) },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBrandLight)
                            )
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 4. CREW SELECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "4. CREW SELECTION",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBrandLight
                        )
                        Text(
                            text = "Hire roles you can afford. Each crew member has a unique gameplay impact.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "$${String.format("%,.0f", weeklyCrewSalary)}/wk",
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        fontSize = 12.sp
                    )
                }

                allCrewRoles.forEach { role ->
                    val isChecked = selectedCrew.contains(role.roleName)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isChecked) PrimaryBrandLight.copy(alpha = 0.10f) else SurfaceColor,
                        border = BorderStroke(1.dp, if (isChecked) PrimaryBrandLight.copy(alpha = 0.6f) else BorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedCrew.remove(role.roleName) else selectedCrew.add(role.roleName)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isChecked) PrimaryBrandLight.copy(alpha = 0.2f) else Color(0xFF1E2333)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(role.icon, contentDescription = null, tint = if (isChecked) PrimaryBrandLight else TextTertiary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(role.roleName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Text("$${role.weeklySalary.toInt()}/wk", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = AccentGold)
                                }
                                Text(role.perkDescription, fontSize = 10.sp, color = TextSecondary)
                            }
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked) selectedCrew.add(role.roleName) else selectedCrew.remove(role.roleName)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = PrimaryBrandLight)
                            )
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 5. OPENING & CLOSING ACTS
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "5. OPENING & CLOSING ACTS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBrandLight
                )
                Text(
                    text = "Invite in-game artists to tour with you. Opening acts boost attendance (+20%); closing act guest headliners drive VIP ticket sell-outs (+25%).",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                // Opening Act Selector
                Text("Opening Act:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedOpeningActNpc = null },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOpeningActNpc == null,
                                onClick = { selectedOpeningActNpc = null },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBrandLight)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("None (Solo Opening)", fontSize = 12.sp, color = TextSecondary)
                        }

                        availableArtists.take(5).forEach { npc ->
                            val fee = 1000.0 + (npc.popularity * 150.0)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedOpeningActNpc = npc },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedOpeningActNpc?.id == npc.id,
                                        onClick = { selectedOpeningActNpc = npc },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryBrandLight)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(npc.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("${npc.genrePreference} • Pop: ${npc.popularity.toInt()}%", fontSize = 10.sp, color = TextTertiary)
                                    }
                                }
                                Text("Fee: $${String.format("%,.0f", fee)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                            }
                        }
                    }
                }

                // Closing Act Selector
                Text("Closing Act / Co-Headliner:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedClosingActNpc = null },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedClosingActNpc == null,
                                onClick = { selectedClosingActNpc = null },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBrandLight)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("None (Solo Finale)", fontSize = 12.sp, color = TextSecondary)
                        }

                        availableArtists.take(5).forEach { npc ->
                            val fee = 2000.0 + (npc.popularity * 250.0)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedClosingActNpc = npc },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedClosingActNpc?.id == npc.id,
                                        onClick = { selectedClosingActNpc = npc },
                                        colors = RadioButtonDefaults.colors(selectedColor = PrimaryBrandLight)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(npc.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("${npc.genrePreference} • Pop: ${npc.popularity.toInt()}%", fontSize = 10.sp, color = TextTertiary)
                                    }
                                }
                                Text("Fee: $${String.format("%,.0f", fee)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                            }
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 6. CREATION OF SETLIST
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "6. CONCERT SETLIST",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBrandLight
                        )
                        Text(
                            text = "Select released songs & album cuts. Setlist tracks receive a stream surge during the tour!",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${selectedSetlistSongIds.size} Selected",
                        fontWeight = FontWeight.Bold,
                        color = NeonTeal,
                        fontSize = 12.sp
                    )
                }

                if (catalogSongs.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceColor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No released songs found in your catalogue. Record and release songs in the Studio to feature them on your setlist!",
                            fontSize = 11.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        catalogSongs.take(8).forEachIndexed { idx, song ->
                            val isSel = selectedSetlistSongIds.contains(song.id)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) NeonTeal.copy(alpha = 0.12f) else SurfaceColor,
                                border = BorderStroke(1.dp, if (isSel) NeonTeal.copy(alpha = 0.5f) else BorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSel) selectedSetlistSongIds.remove(song.id) else selectedSetlistSongIds.add(song.id)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("#${idx + 1}", fontSize = 11.sp, color = TextTertiary, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(song.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                            Text("${song.genreId.replaceFirstChar { it.uppercase() }} • Rating: ${song.rating}/5", fontSize = 10.sp, color = TextSecondary)
                                        }
                                    }
                                    Checkbox(
                                        checked = isSel,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedSetlistSongIds.add(song.id) else selectedSetlistSongIds.remove(song.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = NeonTeal)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 7. MERCH STAND
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "7. TOUR MERCH STAND",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBrandLight
                        )
                        Text(
                            text = "Select catalog items to stock at the arena merch booth.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "${selectedMerchIds.size} Stocked",
                        fontWeight = FontWeight.Bold,
                        color = AccentGold,
                        fontSize = 12.sp
                    )
                }

                if (state.merchStyles.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceColor,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No custom merchandise in your catalog yet. Visit the Merch app to create hoodies, vinyl, and tees to sell on tour!",
                            fontSize = 11.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.merchStyles.forEach { style ->
                            val isSel = selectedMerchIds.contains(style.id)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) AccentGold.copy(alpha = 0.12f) else SurfaceColor,
                                border = BorderStroke(1.dp, if (isSel) AccentGold.copy(alpha = 0.5f) else BorderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSel) selectedMerchIds.remove(style.id) else selectedMerchIds.add(style.id)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(style.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                        Text("Price: $${style.sellPrice.toInt()} • In Stock: ${style.stockRemaining} units", fontSize = 10.sp, color = TextSecondary)
                                    }
                                    Checkbox(
                                        checked = isSel,
                                        onCheckedChange = { checked ->
                                            if (checked) selectedMerchIds.add(style.id) else selectedMerchIds.remove(style.id)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = AccentGold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 8. 3-TIER TICKET PRICING
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val (minCap, maxCap) = when (selectedTourType) {
                    "Club Run" -> 1.0 to 6.0
                    "City Tour" -> 5.0 to 18.0
                    "National Tour" -> 20.0 to 50.0
                    "Global Tour" -> 50.0 to 150.0
                    else -> 1.0 to 50.0
                }
                val isOverpriced = generalPrice > maxCap || vipPrice > maxCap || platinumPrice > maxCap

                Text(
                    text = "8. TICKET PRICING STRUCTURE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBrandLight
                )
                Text(
                    text = "Tiered seat allocation: General (70%), VIP (25%), Platinum (5%). Recommended range for $selectedTourType: $${minCap.toInt()} – $${maxCap.toInt()}.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                if (isOverpriced) {
                    Surface(
                        color = Color(0xFF7F1D1D).copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Text(
                                "⚠️ Overpriced Ticket Warning! $selectedTourType ticket prices should remain between $${minCap.toInt()} – $${maxCap.toInt()}. Pricing above $${maxCap.toInt()} will cause severe fan boycotts and catastrophic low audience turnout (<10%)!",
                                color = Color(0xFFFCA5A5),
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = generalTicketPriceInput,
                        onValueChange = { generalTicketPriceInput = it },
                        label = { Text("General (70%)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBrandLight,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.weight(1f).testTag("general_ticket_price_input")
                    )

                    OutlinedTextField(
                        value = vipTicketPriceInput,
                        onValueChange = { vipTicketPriceInput = it },
                        label = { Text("VIP (25%)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.weight(1f).testTag("vip_ticket_price_input")
                    )

                    OutlinedTextField(
                        value = platinumTicketPriceInput,
                        onValueChange = { platinumTicketPriceInput = it },
                        label = { Text("Platinum (5%)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = BorderColor
                        ),
                        modifier = Modifier.weight(1f).testTag("platinum_ticket_price_input")
                    )
                }

                // Price analytics box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("BLENDED AVG TICKET", fontSize = 9.sp, color = TextTertiary)
                            Text("$${String.format("%.2f", weightedAvgTicket)}", fontWeight = FontWeight.Bold, color = AccentGold, fontSize = 14.sp)
                        }
                        Column {
                            Text("SEATS PER SHOW", fontSize = 9.sp, color = TextTertiary)
                            Text("${String.format("%,d", baseVenueCap)} seats", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                        }
                        Column {
                            Text("POTENTIAL BOX OFFICE", fontSize = 9.sp, color = TextTertiary)
                            val potPerShow = baseVenueCap * weightedAvgTicket
                            Text("$${String.format("%,.0f", potPerShow * locsCount)}", fontWeight = FontWeight.Bold, color = AccentGreen, fontSize = 12.sp)
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // 9. LAUNCH DATE SELECTION
            // ==========================================
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "9. LAUNCH DATE TIMELINE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = PrimaryBrandLight
                )
                Text(
                    text = "Choose when your tour officially departs. Longer presale campaigns build higher anticipation and sell-through rates!",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                val launchOptions = listOf(
                    Pair(1, "Next Week (Week ${state.currentWeek + 1}, Year ${state.currentYear}) - Immediate departure"),
                    Pair(2, "In 2 Weeks (Week ${state.currentWeek + 2}, Year ${state.currentYear}) - Short presale window"),
                    Pair(4, "In 4 Weeks (Week ${state.currentWeek + 4}, Year ${state.currentYear}) - Optimal presale campaign (+15% hype)"),
                    Pair(8, "In 8 Weeks (Week ${state.currentWeek + 8}, Year ${state.currentYear}) - Global headline rollout (+30% hype)")
                )

                launchOptions.forEach { (weeksAhead, desc) ->
                    val isSel = launchDateSelectionWeeksAhead == weeksAhead
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) PrimaryBrandLight.copy(alpha = 0.12f) else SurfaceColor,
                        border = BorderStroke(1.dp, if (isSel) PrimaryBrandLight else BorderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { launchDateSelectionWeeksAhead = weeksAhead }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSel,
                                onClick = { launchDateSelectionWeeksAhead = weeksAhead },
                                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBrandLight)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(desc, fontSize = 11.sp, color = if (isSel) PrimaryBrandLight else TextSecondary, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }

            Divider(color = BorderColor.copy(alpha = 0.5f))

            // ==========================================
            // FINANCIAL RECEIPT & CONFIRMATION
            // ==========================================
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceColor,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("TOTAL TOUR SETUP BUDGET", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBrandLight)
                    Text("• Venue Guarantees ($locsCount Cities): $${String.format("%,.0f", venueGuaranteeCost)}", fontSize = 11.sp, color = TextSecondary)
                    Text("• Stage FX & Audio Rig: $${String.format("%,.0f", productionCost)}", fontSize = 11.sp, color = TextSecondary)
                    Text("• Interstate Transit ($totalWeeks wks): $${String.format("%,.0f", totalTransportCost)}", fontSize = 11.sp, color = TextSecondary)
                    Text("• Crew Salaries (${selectedCrew.size} roles × $totalWeeks wks): $${String.format("%,.0f", totalCrewCost)}", fontSize = 11.sp, color = TextSecondary)
                    if (totalTalentCost > 0) {
                        Text("• Guest Artists Talent Fees: $${String.format("%,.0f", totalTalentCost)}", fontSize = 11.sp, color = TextSecondary)
                    }
                    Text("• Presale & Digital Marketing: $${String.format("%,.0f", promoBudget)}", fontSize = 11.sp, color = TextSecondary)
                    if (labelReduction > 0) {
                        Text("• Label 50% Tour Financing Subsidy: -$${String.format("%,.0f", rawSetupCost * 0.50)}", fontSize = 11.sp, color = AccentGreen)
                    }

                    Divider(color = BorderColor, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("UPFRONT CASH REQUIRED", fontSize = 9.sp, color = TextTertiary)
                            Text("$${String.format("%,.0f", finalSetupCost)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black), color = if (canAfford) AccentGold else DangerColor)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("YOUR CASH BALANCE", fontSize = 9.sp, color = TextTertiary)
                            Text("$${String.format("%,.0f", state.cash)}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    val computedLaunchWeek = state.currentWeek + launchDateSelectionWeeksAhead
                    val computedLaunchYear = state.currentYear + (computedLaunchWeek / 53)
                    val normalizedLaunchWeek = (computedLaunchWeek % 52).coerceAtLeast(1)

                    viewModel.createRealisticTour(
                        title = tourTitle,
                        tourType = selectedTourType,
                        bannerUri = selectedBannerUri,
                        selectedCrewRoles = selectedCrew.toList(),
                        openingActNpcId = selectedOpeningActNpc?.id,
                        openingActName = selectedOpeningActNpc?.name,
                        openingActCost = openingActFee,
                        closingActNpcId = selectedClosingActNpc?.id,
                        closingActName = selectedClosingActNpc?.name,
                        closingActCost = closingActFee,
                        setlistSongIds = selectedSetlistSongIds.toList(),
                        setlistSongTitles = catalogSongs.filter { it.id in selectedSetlistSongIds }.map { it.title },
                        selectedMerchIds = selectedMerchIds.toList(),
                        generalTicketPrice = generalPrice,
                        vipTicketPrice = vipPrice,
                        platinumTicketPrice = platinumPrice,
                        launchWeek = normalizedLaunchWeek,
                        launchYear = computedLaunchYear,
                        routeMarkets = routeMarkets
                    )
                    onTourCreated()
                },
                enabled = canAfford && routeMarkets.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_tour_contract_button")
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        routeMarkets.isEmpty() -> "ADD AT LEAST ONE CITY & COUNTRY"
                        canAfford -> "SIGN TOUR CONTRACT & LOCK SCHEDULE"
                        else -> "INSUFFICIENT FUNDS ($${String.format("%,.0f", finalSetupCost)} NEEDED)"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private data class CrewRoleDefinition(
    val roleName: String,
    val weeklySalary: Double,
    val icon: ImageVector,
    val perkDescription: String
)

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

/**
 * Curated list of world-famous venues for visual immersion.
 */
@Composable
fun VenueDirectoryView(
    state: GameStateEntity,
    onSelectVenue: (String) -> Unit
) {
    val venues = listOf(
        Triple("The Roxy Theatre", "West Hollywood, CA", "500 Cap • Indie Club"),
        Triple("Bowery Ballroom", "New York, NY", "600 Cap • Historic Club"),
        Triple("Red Rocks Amphitheatre", "Morrison, CO", "9,500 Cap • Open Air"),
        Triple("O2 Arena", "London, UK", "20,000 Cap • Megastadium"),
        Triple("Madison Square Garden", "New York, NY", "20,789 Cap • Iconic Arena"),
        Triple("Tokyo Dome", "Tokyo, Japan", "55,000 Cap • Colosseum")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "ICONIC VENUES & OPPORTUNITIES",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimaryBrandLight
        )

        venues.forEach { (venueName, loc, stats) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(venueName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        Text(loc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(stats, style = MaterialTheme.typography.labelSmall, color = AccentGold)
                    }

                    Button(
                        onClick = { onSelectVenue(venueName) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor.copy(alpha = 0.2f), contentColor = PrimaryBrandLight),
                        border = BorderStroke(1.dp, PrimaryBrandLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("TARGET", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
