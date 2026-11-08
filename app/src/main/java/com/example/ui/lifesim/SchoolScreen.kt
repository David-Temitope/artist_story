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
fun SchoolScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val edu = state.education
    var selectedUniId by remember { mutableStateOf(LifeSimData.UNIVERSITIES.first().id) }
    var selectedCourseName by remember { mutableStateOf(LifeSimData.COURSES.first().name) }

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
                    Text("Higher Education Academy", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Cash: \$${String.format("%,.0f", state.cash)} • Energy: ${state.energy}%", style = MaterialTheme.typography.bodyMedium, color = AccentGold)
                }
            }
        }

        // Active Enrollment Card
        if (edu.isEnrolled) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PrimaryBrandColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (edu.isGraduated) "🎓 Graduated Alumni" else "📚 Active Student",
                                color = if (edu.isGraduated) AccentGreen else PrimaryBrandColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "CGPA: ${String.format("%.2f", edu.cgpa)} / 5.0",
                                color = AccentGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(edu.universityName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Major: ${edu.courseName}", style = MaterialTheme.typography.titleMedium, color = TextSecondary)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress
                        val progress = if (edu.totalCourseWeeks > 0) (edu.currentWeekInCourse.toFloat() / edu.totalCourseWeeks.toFloat()).coerceIn(0f, 1f) else 0f
                        Text("Curriculum Progress: Week ${edu.currentWeekInCourse} / ${edu.totalCourseWeeks}", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = PrimaryBrandColor,
                            trackColor = SurfaceColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when {
                                    edu.hasScholarship -> "Tuition: Fully Paid by Scholarship"
                                    edu.parentsPaying -> "Tuition: Paid by Parents"
                                    else -> "Tuition: \$${String.format("%,.0f", edu.weeklyTuition)}/week (You pay)"
                                },
                                color = if (edu.hasScholarship || edu.parentsPaying) AccentGreen else TextSecondary,
                                fontSize = 12.sp
                            )
                            Text("⚡ -10% Energy/wk", color = AccentGold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!edu.parentsPaying && !edu.hasScholarship && !edu.isGraduated) {
                                OutlinedButton(
                                    onClick = { viewModel.askParentsForTuition() },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, BorderColor)
                                ) {
                                    Text("Ask Parents to Pay", fontSize = 11.sp, color = TextPrimary)
                                }
                            }

                            if (!edu.isGraduated) {
                                Button(
                                    onClick = { viewModel.claimRewardedAdStudyCram() },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Study Cram (+0.05 CGPA Ad)", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // University Selection
            item {
                Text("Select University:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            items(LifeSimData.UNIVERSITIES) { uni ->
                val isSelected = selectedUniId == uni.id
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PrimaryBrandColor.copy(alpha = 0.15f) else CardBg
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) PrimaryBrandColor else BorderColor),
                    modifier = Modifier.fillMaxWidth().clickable { selectedUniId = uni.id }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(uni.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("\$${String.format("%,.0f", uni.weeklyFee)}/wk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AccentGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(uni.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            // Course Selection
            item {
                Text("Select Degree Course:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            }

            items(LifeSimData.COURSES) { course ->
                val isSelected = selectedCourseName == course.name
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AccentGreen.copy(alpha = 0.15f) else CardBg
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) AccentGreen else BorderColor),
                    modifier = Modifier.fillMaxWidth().clickable { selectedCourseName = course.name }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(course.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${course.totalWeeks} Weeks", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AccentGold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("CGPA Climb: +${course.weeklyCgpaGain}/wk • Breaks: Wk ${course.break1Start}-${course.break1End} & Wk ${course.break2Start}-${course.break2End}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.enrollInUniversity(selectedUniId, selectedCourseName) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("ENROLL IN UNIVERSITY", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
