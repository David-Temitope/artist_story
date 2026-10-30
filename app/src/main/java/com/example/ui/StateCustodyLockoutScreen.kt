package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.data.GameStateEntity
import com.example.data.GameViewModel

private val JailBg = Color(0xFF0D0808)
private val JailCardBg = Color(0xFF1A0F0F)
private val JailBorder = Color(0xFF3D1A1A)
private val AlertRed = Color(0xFFEF4444)
private val WarningGold = Color(0xFFF59E0B)

@Composable
fun StateCustodyLockoutScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    featureName: String,
    onOpenScc: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JailBg)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(JailCardBg)
                .border(1.5.dp, JailBorder, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // High Security Seal
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(AlertRed.copy(alpha = 0.3f), Color.Transparent)))
                    .border(2.dp, AlertRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = "Judicial Injunction",
                    tint = AlertRed,
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = AlertRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed)
                ) {
                    Text(
                        text = "STATE JUDICIAL REMAND ORDER",
                        color = AlertRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ACCESS DENIED: $featureName",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "By order of the State Supreme Court, all personal privileges, commercial contracts, recording studios, travel permits, and entertainment operations for inmate ${state.artistName.uppercase()} have been seized under federal injunction.",
                    color = Color(0xFFD1D5DB),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Indictment Dossier Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF120808))
                    .border(1.dp, Color(0xFF2E1212), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("CHARGES:", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("BOOKING ID: SCC-9941", color = AlertRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = state.arrestReason.ifBlank { "Election Campaign Fraud & Embezzlement" },
                    color = Color(0xFFFCA5A5),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Divider(color = Color(0xFF2E1212))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bail Bond Required", color = Color.Gray, fontSize = 10.sp)
                        Text(
                            "$${String.format("%,.0f", state.arrestBailAmount)}",
                            color = WarningGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Sentence Remaining", color = Color.Gray, fontSize = 10.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = AlertRed, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${state.arrestSentenceWeeksRemaining} Weeks",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenScc,
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("open_scc_from_lockout_btn")
                ) {
                    Icon(Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OPEN STATE CRIME COMMAND (SCC.GOV)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                if (state.arrestSentenceWeeksRemaining > 0) {
                    OutlinedButton(
                        onClick = { viewModel.serveDetentionWeek() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4B2323)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("serve_detention_week_lockout_btn")
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Serve 1 Week in Custody (${state.arrestSentenceWeeksRemaining} left)",
                            color = Color(0xFFE5E7EB),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
