package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CareerDilemma
import com.example.data.DilemmaChoice
import com.example.data.isAvailableFor
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

/**
 * High-fidelity Decision Screen Modal for major artist career crossroads.
 * Structured around Situation (Narrative), Context (Stakes/Stakeholders), and Choices.
 */
@Composable
fun ArtistDecisionModal(
    dilemma: CareerDilemma,
    playerBusinessSkill: Float = 50f,
    playerIntegrity: Float = 50f,
    onSelectChoice: (DilemmaChoice) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedChoice by remember { mutableStateOf<DilemmaChoice?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.5.dp, PrimaryBrandColor.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("decision_screen_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Tag: Decision Type & Stakes
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeonPurple.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = NeonPink,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = dilemma.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = NeonPink
                                    )
                                }
                            }

                            Text(
                                text = "Crossroads Moment",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Dilemma Headline
                    item {
                        Text(
                            text = dilemma.headline,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.3.sp
                            ),
                            color = TextPrimary
                        )
                    }

                    // SECTION 1: THE SITUATION (Narrative storytelling)
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceColor.copy(alpha = 0.8f)
                            ),
                            border = BorderStroke(1.dp, PrimaryBrandLight.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = PrimaryBrandLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "THE SITUATION",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = PrimaryBrandLight
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = dilemma.situationNarrative,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 22.sp
                                    ),
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // SECTION 2: CONTEXT & STAKES (Who is involved? What is at stake?)
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceColor,
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Stakeholders
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(AccentGold.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = AccentGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "WHO IS INVOLVED",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.8.sp
                                            ),
                                            color = AccentGold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dilemma.stakeholders,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)

                                // Stakes
                                Row(verticalAlignment = Alignment.Top) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(NeonTeal.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = NeonTeal,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "WHAT IS AT STAKE",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.8.sp
                                            ),
                                            color = NeonTeal
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = dilemma.stakes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 3: THE CHOICES (Roleplaying, Personality-driven Options)
                    item {
                        Text(
                            text = "SELECT YOUR PATH",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = PrimaryBrandLight,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(dilemma.choices.size) { index ->
                        val choice = dilemma.choices[index]
                        val isSelected = selectedChoice == choice
                        val isAvailable = choice.isAvailableFor(playerBusinessSkill, playerIntegrity)

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SurfaceColor else Color(0xFF161925)
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) PrimaryBrandLight else BorderColor
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = isAvailable) { selectedChoice = choice }
                                .testTag("decision_choice_$index")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = choice.actionText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = when {
                                            isSelected -> PrimaryBrandLight
                                            !isAvailable -> TextSecondary
                                            else -> TextPrimary
                                        }
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SurfaceColor,
                                        border = BorderStroke(0.8.dp, BorderColor)
                                    ) {
                                        Text(
                                            text = choice.shortTitle,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = TextSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                                if (!isAvailable || choice.requirementText != null) {
                                    Text(
                                        text = choice.requirementText ?: buildString {
                                            if (playerBusinessSkill < choice.requiredBusinessSkill) {
                                                append("Requires Business Skill ${choice.requiredBusinessSkill.toInt()}% ")
                                            }
                                            if (playerIntegrity < choice.requiredIntegrity) {
                                                append("Requires Integrity ${choice.requiredIntegrity.toInt()}% ")
                                            }
                                        }.trim(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // BOTTOM ACTION BAR: CONFIRM SELECTION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            selectedChoice?.let { onSelectChoice(it) }
                        },
                        enabled = selectedChoice?.isAvailableFor(playerBusinessSkill, playerIntegrity) == true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBrandColor,
                            disabledContainerColor = Color(0xFF262C3E)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_decision_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedChoice != null) "COMMIT TO DECISION" else "SELECT A CHOICE TO PROCEED",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Decisions permanently shape your reputation, relationships, and opportunities.",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
