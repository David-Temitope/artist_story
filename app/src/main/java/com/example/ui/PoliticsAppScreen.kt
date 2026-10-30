package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@Composable
fun PoliticsAppScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenBrowser: (String) -> Unit = {}
) {
    val race = remember(state.currentWeek, state.currentYear) {
        PoliticsSimulationHelper.getActiveElectionRace(state.currentWeek, state.currentYear)
    }
    val racePosition = if (state.currentWeek <= 20) "State Governor" else "City Mayor & Council"
    val isVotingSeason = state.isElectionWeek || state.currentWeek == 14 || state.currentWeek == 38
    val liveResults = remember(
        state.currentWeek,
        state.currentYear,
        state.webHub.electionVotedCandidateId,
        state.electionVotedCandidateId,
        state.endorsedPoliticianId,
        state.endorsedCandidateId,
        isVotingSeason
    ) {
        PoliticsSimulationHelper.calculateLiveResults(
            contestants = race.contestants,
            position = racePosition,
            playerVotedCandidateId = state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId,
            playerEndorsedCandidateId = state.endorsedPoliticianId ?: state.endorsedCandidateId,
            playerPopularity = state.popularity,
            currentWeek = state.currentWeek,
            currentYear = state.currentYear,
            isVotingSeason = isVotingSeason
        )
    }

    var selectedTab by remember { mutableStateOf(if (state.hasVotedInCurrentElection) 1 else 0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B132B))
            .verticalScroll(rememberScrollState())
    ) {
        // Top Civic Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1C2541), Color(0xFF0B132B))
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF38BDF8), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HowToVote, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "CIVIC HUB",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Electoral System & Public Policy",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Surface(
                        color = if (state.isElectionWeek) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, if (state.isElectionWeek) Color(0xFFEF4444) else Color(0xFF10B981))
                    ) {
                        Text(
                            text = if (state.isElectionWeek) "● ELECTION WEEK" else "● YEAR ${state.currentYear} CYCLE",
                            color = if (state.isElectionWeek) Color(0xFFEF4444) else Color(0xFF10B981),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (state.isElectionWeek && !state.hasVotedInCurrentElection) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF260D0D)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MANDATORY ACTION: You cannot progress to next week without casting your ballot in the official election!",
                                color = Color(0xFFFCA5A5),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Official Voter Registration Status Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("VOTER REGISTRATION", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = if (state.isVoterRegistered) "Verified Citizen (ID: ${state.voterIdNumber.ifEmpty { "OEC-VTR-8841" }})" else "Status: Unregistered",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!state.isVoterRegistered) {
                            Button(
                                onClick = { viewModel.registerAsVoter() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp).testTag("register_voter_app_btn")
                            ) {
                                Text("REGISTER FREE", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        } else {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (state.hasVotedInCurrentElection) "BALLOT CAST ✓" else "ELIGIBLE TO VOTE",
                                    color = Color(0xFF10B981),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Quick Link to Omega Web Hub
                OutlinedButton(
                    onClick = { onOpenBrowser("oec.gov") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OPEN OMEGA ELECTORAL COMMISSION PORTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tab Selector: Candidates vs Live Results
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedTab = 0 },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 0) Color(0xFF38BDF8) else Color(0xFF1E293B)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(36.dp)
            ) {
                Text("CANDIDATES & REFORMS", color = if (selectedTab == 0) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { selectedTab = 1 },
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 1) Color(0xFF38BDF8) else Color(0xFF1E293B)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.weight(1f).height(36.dp)
            ) {
                Text(if (isVotingSeason) "LIVE ELECTION RETURNS" else "POLL & MOMENTUM", color = if (selectedTab == 1) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Active Endorsement Warning / Info
        if (state.tookEndorsementMoney || state.endorsedPoliticianId != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF231B08)),
                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ACTIVE ENDORSEMENT AGREEMENT", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "You accepted $${String.format("%,.0f", state.endorsementMoneyAmount.takeIf { it > 0.0 } ?: 40000.0)} to endorse ${state.endorsedPoliticianName ?: "your candidate"}. If you vote for an opponent and they win, State Crime Command will issue an immediate ARREST warrant for political corruption!",
                        color = Color(0xFFFEF3C7),
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Candidates Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "RUNNING CONTESTANTS FOR ${race.position.uppercase()}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                for (contestant in race.contestants) {
                    val politician = contestant.politician
                    val isVoted = (state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId) == politician.id
                    val isEndorsed = (state.endorsedPoliticianId ?: state.endorsedCandidateId) == politician.id

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.2.dp, if (isVoted) Color(0xFF10B981) else if (isEndorsed) Color(0xFF38BDF8) else Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(politician.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("${politician.title} • ${politician.party}", color = Color(0xFF38BDF8), fontSize = 11.sp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (isEndorsed) {
                                        Surface(color = Color(0xFF38BDF8).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                            Text("ENDORSED", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    if (isVoted) {
                                        Surface(color = Color(0xFF10B981).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                            Text("BALLOT CAST ✓", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Policy Platform & Concrete Economic Promises:",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            for (promise in contestant.promises) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F172A), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text("• ${promise.title}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(promise.description, color = Color(0xFF94A3B8), fontSize = 10.sp, lineHeight = 13.sp)
                                    Text("Impact: ${promise.mechanicalEffect}", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            if (!state.hasVotedInCurrentElection) {
                                if (isVotingSeason) {
                                    Button(
                                        onClick = {
                                            if (!state.isVoterRegistered) {
                                                viewModel.registerAsVoter()
                                            }
                                            viewModel.voteInOmegaElection(politician.id, politician.name, politician.party)
                                            selectedTab = 1
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("app_vote_btn_${politician.id}")
                                    ) {
                                        Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("CAST OFFICIAL BALLOT FOR ${politician.name.uppercase()}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Surface(
                                        color = Color(0xFF1E293B),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, Color(0xFF334155)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("BALLOTS OPEN IN VOTING SEASON (WEEKS 14 & 38)", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else if (isVoted) {
                                Surface(
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp),
                                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("VOTE RECORDED IN STATE ELECTION DATABASE", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Live Returns Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isVotingSeason) "LIVE ELECTION RETURNS (${race.position})" else "OPINION POLL & MOMENTUM (${race.position})",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isVotingSeason) "Turnout: ${String.format("%.1f", liveResults.turnoutPercentage)}%" else "Survey Sample: 1.8M",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isVotingSeason) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Weekly Polling Momentum: Voter preferences evolve dynamically every week. A candidate leading today can fall behind next week.",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (isVotingSeason) "Total Certified Ballots Counted: ${String.format("%,d", liveResults.totalBallotsCounted)}" else "Weekly Projected Poll Share & Trajectory",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        for (candidateShare in liveResults.candidateShares) {
                            val isPlayerVote = (state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId) == candidateShare.candidateId
                            val isLeader = candidateShare.candidateId == liveResults.leadingCandidateId

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(candidateShare.candidateName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        if (isLeader) {
                                            Surface(color = Color(0xFFF59E0B), shape = RoundedCornerShape(3.dp)) {
                                                Text("LEADER", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                        if (isPlayerVote) {
                                            Surface(color = Color(0xFF10B981), shape = RoundedCornerShape(3.dp)) {
                                                Text("YOUR VOTE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            color = when (candidateShare.weeklyTrendDirection) {
                                                "RISING" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                "FALLING" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                                else -> Color(0xFF64748B).copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(3.dp)
                                        ) {
                                            Text(
                                                text = candidateShare.weeklyDeltaText,
                                                color = when (candidateShare.weeklyTrendDirection) {
                                                    "RISING" -> Color(0xFF10B981)
                                                    "FALLING" -> Color(0xFFEF4444)
                                                    else -> Color(0xFF94A3B8)
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        Text(
                                            text = "${String.format("%.1f", candidateShare.percentage)}%",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { (candidateShare.percentage / 100f).toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                    color = if (isLeader) Color(0xFF38BDF8) else Color(0xFF64748B),
                                    trackColor = Color(0xFF0F172A),
                                )
                            }
                        }

                        Divider(color = Color(0xFF334155), thickness = 0.8.dp)

                        Text("DISTRICT RETURNS BREAKDOWN", color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        for (district in liveResults.districtReturns) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(district.districtName, color = Color.White, fontSize = 10.sp)
                                Text("Leading: ${district.leadingParty} (${String.format("%.0f", district.turnout)}% Turnout)", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
