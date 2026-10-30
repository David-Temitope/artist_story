package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.NPCEntity
import com.example.data.NPCMemory
import com.example.ui.theme.*

@Composable
fun NpcPsychologyDialog(
    npc: NPCEntity,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Mind", "Values", "Opinion", "Networks", "Memories")
    val tabIcons = listOf(
        Icons.Default.Info,
        Icons.Default.Face,
        Icons.Default.Favorite,
        Icons.Default.Share,
        Icons.Default.MenuBook
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            color = DarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        com.example.ui.ArtistFaceAvatar(
                            artistIdentifier = npc.id,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = npc.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Psychological Profile • ${npc.genrePreference}",
                                fontSize = 11.sp,
                                color = NeonTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                HorizontalDivider(color = BorderColor, thickness = 1.dp)

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = CardBg,
                    contentColor = NeonTeal,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NeonTeal
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = tabIcons[index],
                                    contentDescription = title,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selectedContentColor = NeonTeal,
                            unselectedContentColor = TextSecondary
                        )
                    }
                }

                // Scrollable Content Pane
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> MindAndEmotionTab(npc)
                        1 -> ValuesAndTraitsTab(npc)
                        2 -> OpinionOfYouTab(npc)
                        3 -> NetworksAndTrustTab(npc)
                        4 -> MemoriesTab(npc)
                    }
                }
            }
        }
    }
}

@Composable
fun MindAndEmotionTab(npc: NPCEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Vibe / Emotion Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CURRENT EMOTIONAL STATE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val emotionColor = when (npc.emotionalState) {
                    "Happy", "Excited" -> AccentGreen
                    "Inspired", "Motivated" -> NeonTeal
                    "Anxious", "Fearful" -> AccentGold
                    "Burned Out", "Heartbroken", "Angry" -> NeonPink
                    else -> Color.White
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(emotionColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, emotionColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = npc.emotionalState.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = emotionColor
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Vitals: ${npc.personality} • Age ${npc.age}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("STRESS", fontSize = 9.sp, color = TextSecondary)
                        Text("${npc.stress.toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (npc.stress > 65f) NeonPink else Color.White)
                    }
                    Column {
                        Text("MENTAL HEALTH", fontSize = 9.sp, color = TextSecondary)
                        Text("${npc.mentalHealth.toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (npc.mentalHealth < 40f) NeonPink else AccentGreen)
                    }
                    Column {
                        Text("HAPPINESS", fontSize = 9.sp, color = TextSecondary)
                        Text("${npc.happiness.toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (npc.happiness > 70f) AccentGreen else Color.White)
                    }
                }
            }
        }

        // Self-Reflection Diary
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "INTERNAL SELF-REFLECTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBg, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "\"${npc.latestSelfReflection.ifEmpty { "I need to focus on writing my next big hit." }}\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Timeline of Life Events
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LIFE EVENTS HISTORY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (npc.lifeEvents.isEmpty()) {
                    Text(
                        text = "No major life events have shook their timeline yet.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        npc.lifeEvents.reversed().forEach { event ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(NeonTeal)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = event,
                                    fontSize = 12.sp,
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

@Composable
fun ValuesAndTraitsTab(npc: NPCEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Strengths and weaknesses
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PSYCHOLOGICAL ALIGNMENTS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("💡 STRENGTH", fontSize = 9.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AccentGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .border(1.dp, AccentGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(npc.strength, fontSize = 12.sp, color = AccentGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("🚨 WEAKNESS", fontSize = 9.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NeonPink.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .border(1.dp, NeonPink.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(npc.weakness, fontSize = 12.sp, color = NeonPink, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("💀 CORE FEAR", fontSize = 9.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(npc.coreFear, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("🌟 CORE DREAM", fontSize = 9.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AccentGold.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .border(1.dp, AccentGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(npc.coreDream, fontSize = 12.sp, color = AccentGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Core Values sliders
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CORE MORAL VALUES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                ValueBar(label = "Integrity (Professional Honesty)", value = npc.integrity, color = NeonTeal)
                ValueBar(label = "Generosity (Selfless Support)", value = npc.generosity, color = AccentGreen)
                ValueBar(label = "Pride (Ego / Reputation Concern)", value = npc.pride, color = NeonPurple)
                ValueBar(label = "Humility (Self Awareness)", value = npc.humility, color = Color.White)
                ValueBar(label = "Loyalty (Friendship Defense)", value = npc.loyalty, color = AccentGold)
                ValueBar(label = "Jealousy (Rival Competition)", value = npc.jealousy, color = NeonPink)
                ValueBar(label = "Greed (Financial Aggression)", value = npc.greed, color = Color(0xFFFFA726))
            }
        }
    }
}

@Composable
fun OpinionOfYouTab(npc: NPCEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Relationship Vibe
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RELATIONSHIP STATUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val rel = npc.relationshipWithPlayer
                val relText = when {
                    rel >= 80f -> "TRUSTED ALLY"
                    rel >= 60f -> "FRIEND"
                    rel >= 40f -> "NEUTRAL COMPETITOR"
                    rel >= 20f -> "COLD RIVAL"
                    else -> "BITTER RIVAL"
                }
                val relColor = when {
                    rel >= 60f -> AccentGreen
                    rel >= 40f -> Color.White
                    else -> NeonPink
                }

                Text(
                    text = relText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = relColor
                )
                Text(
                    text = "Relationship score with player: ${rel.toInt()}%",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Multi-dimensional opinions list
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OPINION SUB-FACTORS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                ValueBar(label = "Professional Respect", value = npc.professionalRespect, color = NeonTeal)
                ValueBar(label = "Personal Trust", value = npc.personalTrust, color = AccentGreen)
                ValueBar(label = "Creative Respect", value = npc.creativeRespect, color = NeonPurple)
                ValueBar(label = "Leadership Respect", value = npc.leadershipRespect, color = AccentGold)
                ValueBar(label = "Opinion of Your Competence", value = npc.competenceOpinion, color = Color.White)
            }
        }
    }
}

@Composable
fun MemoriesTab(npc: NPCEntity) {
    if (npc.memories.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "Empty Memoirs",
                tint = TextSecondary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Their mind is a blank slate.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "They don't remember any significant interactions with you yet. Collab, dine, or rivalize to register memory cells!",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "ACTIVE INTERACTION MEMORIES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(npc.memories) { memory ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(NeonTeal.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                    .border(1.dp, NeonTeal, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = memory.memoryType,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonTeal
                                )
                            }

                            Text(
                                text = "Week ${memory.weekRecorded}, Yr ${memory.yearRecorded}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = memory.description,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Impact row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val sign = if (memory.emotionalImpact > 0) "+" else ""
                            val impactColor = if (memory.emotionalImpact > 0) AccentGreen else NeonPink
                            Row {
                                Text("EMOTIONAL IMPACT: ", fontSize = 10.sp, color = TextSecondary)
                                Text("$sign${memory.emotionalImpact.toInt()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = impactColor)
                            }

                            Row {
                                Text("DECAY RATE: ", fontSize = 10.sp, color = TextSecondary)
                                Text("-${memory.decayRate.toInt()}/wk", fontSize = 10.sp, color = NeonPink)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NetworksAndTrustTab(npc: NPCEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overlapping Networks Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "OVERLAPPING SOCIAL & PROFESSIONAL NETWORKS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                val networksList = npc.networks.ifEmpty { listOf("Friend Network", "Music Industry Network") }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    networksList.forEach { network ->
                        val netColor = when (network) {
                            "Family Network" -> Color(0xFF4CAF50)
                            "Friend Network" -> Color(0xFF2196F3)
                            "Music Industry Network" -> NeonTeal
                            "Business Network" -> Color(0xFFFF9800)
                            "Investment Network" -> AccentGold
                            "Government Network" -> NeonPurple
                            "Media Network" -> NeonPink
                            else -> Color(0xFF9E9E9E)
                        }

                        Box(
                            modifier = Modifier
                                .background(netColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .border(1.dp, netColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = netColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = network,
                                    color = netColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Connection strength
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "NETWORK CONNECTION STRENGTH",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val strengthText = when {
                    npc.relationshipWithPlayer >= 90f -> "Mentor / Closest Ally"
                    npc.relationshipWithPlayer >= 75f -> "Trusted Advisor / Close Friend"
                    npc.relationshipWithPlayer >= 60f -> "Business Partner / Friend"
                    npc.relationshipWithPlayer >= 45f -> "Professional Contact / Acquaintance"
                    npc.relationshipWithPlayer >= 20f -> "Occasional Contact / Cold Rival"
                    else -> "Bitter Enemy / Bitter Rival"
                }

                val strengthColor = when {
                    npc.relationshipWithPlayer >= 75f -> AccentGreen
                    npc.relationshipWithPlayer >= 45f -> NeonTeal
                    npc.relationshipWithPlayer >= 20f -> Color(0xFFFF9800)
                    else -> NeonPink
                }

                Text(
                    text = strengthText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = strengthColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Connection level influences recommendation likelihood, secret-keeping, and the speed at which opportunities diffuse to you.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        // Distinct Trust Categories
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TRUST & RELATIONSHIP SUB-SYSTEMS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))

                ValueBar(label = "Business Trust (Professional execution)", value = npc.businessTrust, color = NeonTeal)
                ValueBar(label = "Financial Trust (Money management & loans)", value = npc.financialTrust, color = AccentGreen)
                ValueBar(label = "Leadership Trust (Teamwork & respect)", value = npc.leadershipTrust, color = AccentGold)
                ValueBar(label = "Confidentiality (Ability to keep secrets)", value = npc.confidentiality, color = NeonPurple)
                ValueBar(label = "Reliability (Consistent execution)", value = npc.reliability, color = Color.White)
                ValueBar(label = "Public Reputation (How the network views them)", value = npc.publicReputation, color = NeonPink)
            }
        }
    }
}

@Composable
fun ValueBar(label: String, value: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text("${value.toInt()}/100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = BorderColor
        )
    }
}
