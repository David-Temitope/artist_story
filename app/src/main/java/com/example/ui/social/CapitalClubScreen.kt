package com.example.ui.social

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.ReliableAsyncImage
import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.data.CapitalClubMember

val ccGold = Color(0xFFE91E63)
val ccGoldMuted = Color(0xFFAD1457)
val ccDarkBg = Color(0xFF0C0C0E)
val ccCardBg = Color(0xFF141418)
val ccGreyText = Color(0xFF8E8E93)

@Composable
fun CapitalClubScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTab by remember { mutableStateOf("Discover") }
    var bottomTab by remember { mutableStateOf("MATCH") }

    // Select the first non-dating member for discover if possible
    val discoverMembers = state.capitalClubMembers.filter { !it.isDating && !it.isSpouse && !it.isLockedOut }
    var currentDiscoverIndex by remember { mutableIntStateOf(0) }

    val currentMember = if (discoverMembers.isNotEmpty()) discoverMembers[currentDiscoverIndex % discoverMembers.size] else null

    val partnerMembers = state.capitalClubMembers.filter { it.isDating || it.isSpouse }

    Scaffold(
        containerColor = ccDarkBg,
        bottomBar = {
            CapitalClubBottomNav(
                selectedTab = bottomTab,
                onTabSelected = { bottomTab = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CapitalClubHeader()
            Spacer(modifier = Modifier.height(16.dp))

            if (bottomTab == "MATCH") {
                CapitalClubTabs(selectedTab, onTabSelected = { selectedTab = it })
                Spacer(modifier = Modifier.height(16.dp))

                if (currentMember != null) {
                    MainProfileCard(
                        member = currentMember,
                        onPass = { currentDiscoverIndex++ },
                        onLike = {
                            viewModel.askOutCapitalClubMember(currentMember.id)
                            currentDiscoverIndex++
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp), contentAlignment = Alignment.Center) {
                        Text("No more profiles to discover.", color = ccGreyText)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                CuratedTodaySection(state.capitalClubMembers)
                Spacer(modifier = Modifier.height(24.dp))
            } else if (bottomTab == "DATE") {
                // Dating Tab
                Spacer(modifier = Modifier.height(16.dp))
                if (partnerMembers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("You do not have a partner yet. Match with someone and build a relationship first.", color = ccGreyText, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                } else {
                    val partner = partnerMembers.first()
                    DatingProfile(partner, viewModel)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Section Under Construction", color = ccGreyText)
                }
            }
        }
    }
}

@Composable
fun DatingProfile(member: CapitalClubMember, viewModel: GameViewModel) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Your Partner", color = ccGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, ccGoldMuted, RoundedCornerShape(20.dp))
        ) {
            ReliableAsyncImage(
                model = member.imageUrl,
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                fallbackCategory = "club",
                fallbackText = member.name,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            ) {
                Text(member.name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                if (member.isSpouse) {
                    Text("Spouse", color = ccGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("Partner", color = Color.White, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = ccGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Relationship: ${member.relationshipScore}/100", color = ccGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Text("Interactions", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.interactWithCapitalClubPartner(member.id, "go_on_date") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E28))
        ) {
            Text("Go on a Date (-$1,000)", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.interactWithCapitalClubPartner(member.id, "buy_gift") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E28))
        ) {
            Text("Buy Luxury Gift (-$5,000)", color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (member.gender == "Male") {
            Button(
                onClick = { viewModel.interactWithCapitalClubPartner(member.id, "ask_for_gift") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E28))
            ) {
                Text("Ask for Money/Shopping", color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!member.isSpouse) {
            Button(
                onClick = { viewModel.interactWithCapitalClubPartner(member.id, "propose") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ccGold)
            ) {
                Text("Propose", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Note: Building a strong relationship takes time. Continue chatting with them in SeeChat and going on dates here to increase your bond.",
            color = ccGreyText,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CapitalClubHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ccGold),
                contentAlignment = Alignment.Center
            ) {
                Text("CC", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "CAPITAL CLUB",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, ccGoldMuted, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("PRIVATE TIER", color = ccGoldMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    "FOUNDER & ALLIANCE SALON",
                    color = ccGreyText,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CapitalClubTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF16161A),
        border = BorderStroke(1.dp, Color(0xFF2A2A30))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("Discover", "Invites", "Chats")
            tabs.forEach { tab ->
                val isSelected = tab == selectedTab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF1E212A) else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else ccGreyText,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MainProfileCard(member: CapitalClubMember, onPass: () -> Unit, onLike: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ccCardBg)
            .border(1.dp, Color(0xFF2A2A30), RoundedCornerShape(20.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            ) {
                ReliableAsyncImage(
                    model = member.imageUrl,
                    contentDescription = member.name,
                    contentScale = ContentScale.Crop,
                    fallbackCategory = "club",
                    fallbackText = member.name,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 600f
                            )
                        )
                )
                // Top tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ccGold))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("NEW MEMBER", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Name and Title
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${member.name}, ${member.age}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = ccGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(member.title, color = Color.LightGray, fontSize = 14.sp)
                }
            }

            // Details Section
            Column(modifier = Modifier.padding(16.dp)) {
                // Vision Alignment
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2A2A30), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = ccGoldMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Vision Alignment", color = ccGreyText, fontSize = 13.sp)
                            }
                            Text(member.matchPct, color = ccGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Co-Founding Synergy
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF2A2A30), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFF2A2A30), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("SYNERGY", color = ccGoldMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(member.synergyText, color = Color.White, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quote
                Text(
                    "\"${member.quote}\"",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tags
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    member.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFF2A2A30), RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E1E24), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(tag, color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, Color(0xFF2A2A30), CircleShape)
                            .background(Color(0xFF16161A), CircleShape)
                            .clickable { onPass() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Pass", tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(20.dp))

                    // Star button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(1.dp, Color(0xFF2A2A30), CircleShape)
                            .background(Color(0xFF16161A), CircleShape)
                            .clickable { onLike() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Super Like", tint = ccGoldMuted, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(20.dp))

                    // Heart button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(ccGold, Color(0xFFAD1457)),
                                ),
                                CircleShape
                            )
                            .clickable { onLike() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Like", tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CuratedTodaySection(members: List<CapitalClubMember>) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("CURATED TODAY", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1E1E24), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Salon Pool", color = ccGreyText, fontSize = 10.sp)
                }
            }
            Text("View All →", color = ccGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(members) { m ->
                CuratedCard(m)
            }
        }
    }
}

@Composable
fun CuratedCard(m: CapitalClubMember) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ccCardBg)
            .border(1.dp, Color(0xFF2A2A30), RoundedCornerShape(16.dp))
    ) {
        ReliableAsyncImage(
            model = m.imageUrl,
            contentDescription = m.name,
            contentScale = ContentScale.Crop,
            fallbackCategory = "club",
            fallbackText = m.name,
            modifier = Modifier.fillMaxWidth().height(140.dp)
        )
        // Fade at the bottom of the image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, ccCardBg),
                        startY = 150f,
                        endY = 400f
                    )
                )
        )

        // Tag
        Box(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(m.tag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text("${m.name}, ${m.age}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(m.title, color = ccGreyText, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(m.matchPct, color = ccGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CapitalClubBottomNav(selectedTab: String, onTabSelected: (String) -> Unit) {
    Surface(
        color = Color(0xFF0C0C0E),
        border = BorderStroke(1.dp, Color(0xFF2A2A30).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem("DASH", Icons.Default.Home, selectedTab == "DASH", onClick = { onTabSelected("DASH") })
            BottomNavItem("MATCH", Icons.Default.Favorite, selectedTab == "MATCH", onClick = { onTabSelected("MATCH") })
            BottomNavItem("DATE", Icons.Default.LocalBar, selectedTab == "DATE", onClick = { onTabSelected("DATE") })
            BottomNavItem("EMPIRE", Icons.Default.Domain, selectedTab == "EMPIRE", onClick = { onTabSelected("EMPIRE") })
            BottomNavItem("BRIEF", Icons.Default.Info, selectedTab == "BRIEF", onClick = { onTabSelected("BRIEF") })
        }
    }
}

@Composable
fun BottomNavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ccGold else ccGreyText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            color = if (isSelected) ccGold else ccGreyText,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
