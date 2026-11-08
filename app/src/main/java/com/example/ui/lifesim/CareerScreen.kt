package com.example.ui.lifesim

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

private val DarkBg = Color(0xFF0F172A)
private val CardBg = Color(0xFF1E293B)
private val SurfaceColor = Color(0xFF334155)
private val PrimaryBrandColor = Color(0xFF6C5CE7)
private val AccentGreen = Color(0xFF00B894)
private val AccentGold = Color(0xFFFFB800)
private val DangerColor = Color(0xFFFF7675)
private val TextPrimary = Color(0xFFF8FAFC)
private val TextSecondary = Color(0xFF94A3B8)
private val BorderColor = Color(0xFF475569)

@Composable
fun CareerScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onOpenStudio: () -> Unit,
    onBack: () -> Unit
) {
    val chosenCareer = state.chosenCareer
    val careers = listOf("Musician", "Actor", "Content Creator", "Police", "Army")

    if (chosenCareer == "None") {
        // Career Selection Screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Choose Your Career Path", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Pick a specialized vocation to manage and build your legacy.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }

            items(careers) { career ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (career) {
                                "Musician" -> Icons.Default.MusicNote
                                "Actor" -> Icons.Default.Movie
                                "Content Creator" -> Icons.Default.Videocam
                                "Police" -> Icons.Default.Security
                                "Army" -> Icons.Default.Shield
                                else -> Icons.Default.Work
                            },
                            contentDescription = null,
                            tint = PrimaryBrandColor,
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(career, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = when (career) {
                                    "Musician" -> "Produce singles, cut albums, sign record deals, and tour worldwide."
                                    "Actor" -> "Audition for movie contracts, act scenes, gain aura, and hit box office gold."
                                    "Content Creator" -> "Produce viral digital content, grow subscribers, and sign brand sponsorships."
                                    "Police" -> "Uphold public safety, investigate organized crime syndicates, and rise through ranks."
                                    "Army" -> "Serve military commands, lead tactical deployments, and defend state interests."
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.chooseCareer(career)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Select")
                        }
                    }
                }
            }
        }
    } else if (chosenCareer == "Musician") {
        // Musician career management
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Music Career Hub", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${state.artistName} • Fans: ${String.format("%,d", state.fans)}", style = MaterialTheme.typography.bodyMedium, color = AccentGold)
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PrimaryBrandColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recording Studio Portal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Access your recording studio, soundboard, lyric writer, mastering suite, and album compilation lab.", color = TextSecondary, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onOpenStudio,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ENTER RECORDING STUDIO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    } else if (chosenCareer == "Actor") {
        // Full Actor Dashboard
        ActorDashboard(state = state, viewModel = viewModel, onBack = onBack)
    } else {
        // Other Careers (Content Creator, Police, Army)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("$chosenCareer Career", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Career division in active simulation.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Operations Desk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("You are stationed in the $chosenCareer division. Complete side jobs and higher education to advance your qualifications.", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ActorDashboard(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val actor = state.actorCareer

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Theatrical Film & Acting", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Tier: ${actor.tier} Actor • Aura: ${actor.aura}/100", style = MaterialTheme.typography.bodyMedium, color = AccentGold)
                }
            }
        }

        // Stats card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Actor Aura Meter", color = TextSecondary, fontSize = 12.sp)
                            Text("${actor.aura} / 100", color = AccentGold, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Completed Films", color = TextSecondary, fontSize = 12.sp)
                            Text("${actor.completedMoviesCount} Movies", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (actor.aura / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = AccentGold,
                        trackColor = SurfaceColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Directors evaluate your aura when offering roles without auditions. Higher aura commands bigger box office splits.", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Active Shoots (Max 2)
        item {
            Text("Active Movie Shoots (${actor.activeContracts.size}/2):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        if (actor.activeContracts.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No active movie contracts. Audition below to book a role!", color = TextSecondary)
                    }
                }
            }
        } else {
            items(actor.activeContracts) { contract ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (contract.hasActedThisWeek) AccentGreen else PrimaryBrandColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(contract.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${contract.playerProfitSharePercent}% Profit Share", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Text("${contract.studioCompany} • Genre: ${contract.genre} • ${contract.roleType}", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Weeks Remaining: ${contract.weeksRemaining} / ${contract.totalDurationWeeks}", color = TextSecondary, fontSize = 11.sp)
                            Text("Shoot Cost: -${contract.requiredEnergyPercent}% Energy", color = AccentGold, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Ad Double Box Office
                            OutlinedButton(
                                onClick = { viewModel.claimRewardedAdBoxOfficeMultiplier(contract.id) },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, AccentGold),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("2x Box Office (Ad)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // ACT Button
                            if (contract.hasActedThisWeek) {
                                Button(
                                    onClick = {},
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(disabledContainerColor = SurfaceColor)
                                ) {
                                    Text("Acted This Week ✓", color = AccentGreen, fontSize = 11.sp)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.performActOnMovie(contract.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ACT SCENES (+1 Aura)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Auditions Board
        item {
            Text("Open Audition Board:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        items(actor.availableAuditions) { audition ->
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(audition.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${audition.roleType}", color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Text("${audition.studioCompany} • ${audition.genre} • Duration: ${audition.totalDurationWeeks} weeks", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Box Office Split: ${audition.playerProfitSharePercent}% • Slots Needed: 3", color = AccentGreen, fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { viewModel.applyForMovieRole(audition.id) },
                            enabled = actor.activeContracts.size < 2,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sign Contract")
                        }
                    }
                }
            }
        }
    }
}
