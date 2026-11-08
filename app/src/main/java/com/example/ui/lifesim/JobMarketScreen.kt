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
fun JobMarketScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val edu = state.education
    val currentJob = state.currentJob
    var selectedField by remember { mutableStateOf("All") }
    val fields = listOf("All", "Computer Science", "Law", "Engineering", "Medicine")

    val filteredJobs = if (selectedField == "All") LifeSimData.JOB_POSTINGS else LifeSimData.JOB_POSTINGS.filter { it.field == selectedField }

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
                    Text("Professional Job Market", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = if (edu.isGraduated) "Degree: ${edu.courseName} (CGPA: ${String.format("%.2f", edu.cgpa)})" else "No Degree Yet (Graduate to qualify)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (edu.isGraduated) AccentGreen else AccentGold
                    )
                }
            }
        }

        // Current Job Card
        if (currentJob != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AccentGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Current Employment", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Salary: \$${String.format("%,.0f", currentJob.weeklySalary)}/wk", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(currentJob.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(currentJob.companyName + " • Field: " + currentJob.field, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Energy Cost: -${currentJob.energyCostPercent}% per week • Tenure: ${currentJob.weeksEmployed} weeks", color = TextSecondary, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { viewModel.quitJob() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerColor),
                            border = BorderStroke(1.dp, DangerColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Resign from Job", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Field filter
        item {
            ScrollableTabRow(
                selectedTabIndex = fields.indexOf(selectedField).coerceAtLeast(0),
                containerColor = Color.Transparent,
                edgePadding = 0.dp,
                divider = {}
            ) {
                fields.forEach { f ->
                    val isSelected = selectedField == f
                    Tab(
                        selected = isSelected,
                        onClick = { selectedField = f },
                        text = {
                            Text(
                                text = f,
                                color = if (isSelected) PrimaryBrandColor else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // Postings
        items(filteredJobs) { posting ->
            val matchesDegree = edu.isGraduated && edu.courseName == posting.field
            val meetsGrade = edu.isGraduated && edu.cgpa >= posting.minCgpaRequired
            val isQualified = matchesDegree && meetsGrade
            val isCurrentRole = currentJob?.id == posting.id

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
                        Column {
                            Text(posting.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${posting.companyName} • ${posting.companyTier}", color = TextSecondary, fontSize = 12.sp)
                        }
                        Text(
                            text = "\$${String.format("%,.0f", posting.weeklySalaryMin)}-\$${String.format("%,.0f", posting.weeklySalaryMax)}/wk",
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Requirement: Degree in ${posting.field}", color = if (matchesDegree) AccentGreen else TextSecondary, fontSize = 11.sp)
                        Text("Min CGPA: ${posting.minCgpaRequired}", color = if (meetsGrade) AccentGreen else AccentGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isCurrentRole) {
                            Button(
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(disabledContainerColor = SurfaceColor)
                            ) {
                                Text("Current Job ✓", color = AccentGreen)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.applyForJob(posting.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isQualified) PrimaryBrandColor else SurfaceColor
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (isQualified) "Apply for Job" else "Requirements Not Met")
                            }
                        }
                    }
                }
            }
        }
    }
}
