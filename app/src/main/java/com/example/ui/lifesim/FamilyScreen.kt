package com.example.ui.lifesim

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun FamilyScreen(
    state: GameStateEntity,
    viewModel: GameViewModel
) {
    var selectedCategory by remember { mutableStateOf("Parents/Siblings") }
    var selectedMemberForMoney by remember { mutableStateOf<FamilyMember?>(null) }
    var requestAmountInput by remember { mutableStateOf("1000") }

    val categories = listOf("Parents/Siblings", "Spouse", "Children", "Friends")

    val filteredMembers = state.familyMembers.filter { member ->
        when (selectedCategory) {
            "Parents/Siblings" -> member.relationshipCategory == "Parents/Siblings"
            "Spouse" -> member.relationshipCategory == "Spouse" || member.role in listOf("Wife", "Husband", "Girlfriend", "Boyfriend")
            "Children" -> member.relationshipCategory == "Children" || member.role in listOf("Son", "Daughter")
            "Friends" -> member.relationshipCategory == "Friends"
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Family & Social Circle",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "The ${state.playerLastName} Household • Age ${state.playerAge}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentGold
                    )
                }

                // Quick ad reward button
                OutlinedButton(
                    onClick = { viewModel.claimRewardedAdEnergy() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentGold),
                    border = BorderStroke(1.dp, AccentGold)
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+30% Energy (Ad)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Living Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isHomeless) DangerColor.copy(alpha = 0.15f) else CardBg
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (state.isHomeless) DangerColor else BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (state.isHomeless) DangerColor.copy(alpha = 0.2f) else PrimaryBrandColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.isHomeless) Icons.Default.Warning else Icons.Default.Home,
                            contentDescription = null,
                            tint = if (state.isHomeless) DangerColor else PrimaryBrandColor
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when {
                                state.isHomeless -> "⚠️ Currently Homeless!"
                                state.isLivingWithParents -> "Living with Parents (Free Lodging)"
                                state.currentRentalHouseId != null -> "Renting Independent Residence"
                                state.ownedHouses.isNotEmpty() -> "Homeowner (${state.ownedHouses.first().name})"
                                else -> "Living Independently"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isHomeless) DangerColor else TextPrimary
                        )

                        Text(
                            text = when {
                                state.isHomeless -> "Stress is rising weekly! Rent or buy a house in the More menu."
                                state.isLivingWithParents -> "You can live at home until age 21. Parents push you out at 22."
                                else -> "Independent adult living."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    if (!state.isLivingWithParents && state.playerAge < 22) {
                        Button(
                            onClick = { viewModel.moveBackWithParents() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Move Home", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Category Filter Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Tab(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        text = {
                            Text(
                                text = cat,
                                color = if (isSelected) PrimaryBrandColor else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // Members List
        if (filteredMembers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No characters in this category yet.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            items(filteredMembers) { member ->
                FamilyMemberCard(
                    member = member,
                    onAskForMoney = { selectedMemberForMoney = member }
                )
            }
        }
    }

    // Ask for Money Dialog
    if (selectedMemberForMoney != null) {
        val member = selectedMemberForMoney!!
        AlertDialog(
            onDismissRequest = { selectedMemberForMoney = null },
            containerColor = CardBg,
            title = {
                Text(
                    text = "Ask ${member.firstName} for Financial Help",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "${member.role} • Wealth: \$${String.format("%,.0f", member.wealth)} • Relationship: ${member.relationshipScore}/100",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Warning: Asking for money reduces relationship score (-8 to -20 points depending on role). Maximum amount an NPC family member can give is \$10,000.",
                        color = DangerColor,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = requestAmountInput,
                        onValueChange = { requestAmountInput = it },
                        label = { Text("Amount ($)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBrandColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = requestAmountInput.toDoubleOrNull() ?: 1000.0
                        viewModel.askFamilyForMoney(member.id, amt)
                        selectedMemberForMoney = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor)
                ) {
                    Text("Ask for Money")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMemberForMoney = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun FamilyMemberCard(
    member: FamilyMember,
    onAskForMoney: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (member.gender) {
                                "Female" -> Icons.Default.Face
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = PrimaryBrandColor
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${member.firstName} ${member.lastName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${member.role} • Age ${member.age} • ${member.occupation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                // Pregnancy Indicator
                if (member.isPregnant) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "👶 ${member.pregnancyWeeksLeft}w left",
                            color = AccentGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Relationship bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Relationship:", color = TextSecondary, fontSize = 12.sp)
                Text("${member.relationshipScore}/100", color = if (member.relationshipScore >= 70) AccentGreen else AccentGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            LinearProgressIndicator(
                progress = { (member.relationshipScore / 100f).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = if (member.relationshipScore >= 70) AccentGreen else AccentGold,
                trackColor = SurfaceColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (member.age >= 18 && member.wealth > 0) {
                    OutlinedButton(
                        onClick = onAskForMoney,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ask for Money", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
