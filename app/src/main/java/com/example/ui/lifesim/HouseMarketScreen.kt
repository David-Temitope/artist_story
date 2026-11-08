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
fun HouseMarketScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    var mode by remember { mutableStateOf("Rent") } // "Rent" or "Buy"

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
                    Text("Residential Real Estate", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Current Cash: \$${String.format("%,.0f", state.cash)}", style = MaterialTheme.typography.bodyMedium, color = AccentGreen)
                }
            }
        }

        // Current Residence Status
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isHomeless) DangerColor.copy(alpha = 0.15f) else CardBg
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (state.isHomeless) DangerColor else BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Residence", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            state.isHomeless -> "⚠️ Homeless (Stressed, Fatigued)"
                            state.isLivingWithParents -> "Living with Parents (Age ${state.playerAge} / Eviction at 22)"
                            state.currentRentalHouseId != null -> {
                                val h = LifeSimData.HOUSES.find { it.id == state.currentRentalHouseId }
                                "Renting ${h?.name ?: "Apartment"} (\$${String.format("%,.0f", (h?.weeklyRent ?: 0.0) + (h?.weeklyExpenses ?: 0.0))}/wk)"
                            }
                            state.ownedHouses.isNotEmpty() -> "Owner of ${state.ownedHouses.first().name}"
                            else -> "None"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isHomeless) DangerColor else TextPrimary
                    )

                    if (state.currentRentalHouseId != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.terminateRental() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerColor),
                            border = BorderStroke(1.dp, DangerColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Terminate Lease", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Rent vs Buy Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { mode = "Rent" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "Rent") PrimaryBrandColor else CardBg,
                        contentColor = if (mode == "Rent") Color.White else TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("Rent Properties", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { mode = "Buy" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "Buy") PrimaryBrandColor else CardBg,
                        contentColor = if (mode == "Buy") Color.White else TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(42.dp)
                ) {
                    Text("Buy Real Estate", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Properties List
        items(LifeSimData.HOUSES) { property ->
            val isCurrentlyRenting = state.currentRentalHouseId == property.id
            val isOwned = state.ownedHouses.any { it.id == property.id }

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
                        Text(property.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = if (mode == "Rent") "\$${String.format("%,.0f", property.weeklyRent)}/wk" else "\$${String.format("%,.0f", property.buyPrice)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(property.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Weekly Expenses & Taxes: \$${String.format("%,.0f", property.weeklyExpenses)}/wk", color = TextSecondary, fontSize = 11.sp)
                        Text("⚡ Energy Bonus: +${property.energyBonus}", color = AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isCurrentlyRenting) {
                            Button(
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = SurfaceColor)
                            ) {
                                Text("Current Lease ✓", color = AccentGreen)
                            }
                        } else if (isOwned) {
                            Button(
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = SurfaceColor)
                            ) {
                                Text("Owned ✓", color = AccentGold)
                            }
                        } else if (mode == "Rent") {
                            Button(
                                onClick = { viewModel.rentHouse(property.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Rent (\$${String.format("%,.0f", property.weeklyRent + property.weeklyExpenses)}/wk)")
                            }
                        } else {
                            Button(
                                onClick = { viewModel.buyHouse(property.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Buy Outright")
                            }
                        }
                    }
                }
            }
        }
    }
}
