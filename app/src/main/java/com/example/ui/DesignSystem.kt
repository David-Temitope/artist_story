package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ActionFeedback
import com.example.data.CareerNewsFeedItem
import com.example.data.GameStateEntity
import com.example.data.StatFeedbackItem
import com.example.ui.theme.*

/**
 * Standard layered Game Card with subtle border and elevation
 */
@Composable
fun GameCard(
    modifier: Modifier = Modifier,
    containerColor: Color = CardBg,
    borderColor: Color = BorderColor,
    borderWidth: Dp = 1.dp,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

/**
 * Clean Section Header with optional action button
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color = PrimaryBrandLight,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                if (badgeText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusChip(text = badgeText, color = badgeColor)
                }
            }
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryBrandLight
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = PrimaryBrandLight,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Metric Progress Bar with label, percentage, and semantic accent
 */
@Composable
fun StatProgressBar(
    label: String,
    value: Float,
    maxValue: Float = 100f,
    unit: String = "%",
    color: Color = PrimaryBrandLight,
    trendText: String? = null,
    modifier: Modifier = Modifier
) {
    val progress = (value / maxValue).coerceIn(0f, 1f)
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = TextSecondary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (trendText != null) {
                    Text(
                        text = trendText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (trendText.startsWith("+")) SuccessColor else DangerColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = "${String.format("%.1f", value)}$unit",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = MonospaceFontFamily),
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = SurfaceColor
        )
    }
}

/**
 * Status Chip with semantic tint and border
 */
@Composable
fun StatusChip(
    text: String,
    color: Color = PrimaryBrandLight,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.75.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color,
            maxLines = 1
        )
    }
}

/**
 * Stat Metric Tile for Artist Command Center
 */
@Composable
fun StatMetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    trend: String? = null,
    subtext: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        shape = RoundedCornerShape(14.dp),
        color = CardBg,
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonospaceFontFamily
                ),
                color = TextPrimary
            )
            if (trend != null || subtext != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (trend != null) {
                        Text(
                            text = trend,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (trend.startsWith("-")) DangerColor else SuccessColor
                        )
                        if (subtext != null) Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (subtext != null) {
                        Text(
                            text = subtext,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * Career Progression Level Banner
 */
@Composable
fun CareerLevelBanner(
    currentLevel: Int,
    levelTitle: String,
    progress: Float,
    nextUnlock: String,
    modifier: Modifier = Modifier
) {
    GameCard(
        modifier = modifier,
        containerColor = CardBg,
        borderColor = PrimaryBrandLight.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LEVEL $currentLevel",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = PrimaryBrandLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusChip(
                        text = levelTitle,
                        color = PremiumGold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Next: $nextUnlock",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonospaceFontFamily
                ),
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = PrimaryBrandLight,
            trackColor = SurfaceColor
        )
    }
}

/**
 * Quick Action Button for Career Actions
 */
@Composable
fun QuickActionButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = CardBg,
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Industry News & World Event Item
 */
@Composable
fun IndustryNewsItem(
    category: String,
    categoryColor: Color,
    timestamp: String,
    headline: String,
    details: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CardBg,
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(text = category.uppercase(), color = categoryColor)
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = headline,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
            if (details != null && details.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Clean Empty State Widget
 */
@Composable
fun EmptyStateWidget(
    title: String,
    subtitle: String,
    icon: ImageVector = Icons.Default.Inbox,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(SurfaceColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = actionText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/**
 * Locked Feature State Widget
 */
@Composable
fun LockedFeatureCard(
    featureTitle: String,
    unlockRequirement: String,
    flavorText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CardBg.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = featureTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "Requires $unlockRequirement",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = WarningColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = flavorText,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        }
    }
}

/**
 * Career Objective data representation
 */
data class CareerObjective(
    val title: String,
    val description: String,
    val destinationTab: String,
    val icon: ImageVector
)

/**
 * Storytelling Stat Item Data
 */
data class StorytellingStat(
    val name: String,
    val value: Float,
    val delta: Float,
    val category: String,
    val consequence: String,
    val influencedBy: String,
    val color: Color
)

/**
 * Living Career Progression Card
 */
@Composable
fun LivingCareerProgressionCard(
    fans: Long,
    modifier: Modifier = Modifier,
    onViewRoadmap: () -> Unit = {}
) {
    val tierInfo = remember(fans) {
        when {
            fans < 15000L -> TierConfig(
                level = 1,
                title = "Aspiring Musician",
                nextTitle = "Indie Sensation",
                currentBase = 0L,
                nextTarget = 15000L,
                unlocks = listOf(
                    "Local Club & Coffeehouse Gigs",
                    "Direct Streaming Distribution",
                    "Basic Rehearsal Studio Room"
                )
            )
            fans < 100000L -> TierConfig(
                level = 2,
                title = "Indie Sensation",
                nextTitle = "Breakout Artist",
                currentBase = 15000L,
                nextTarget = 100000L,
                unlocks = listOf(
                    "Regional Concert Tour Booking",
                    "Professional Music Video Shoots",
                    "Indie Record Label Submissions"
                )
            )
            fans < 500000L -> TierConfig(
                level = 3,
                title = "Breakout Artist",
                nextTitle = "Headlining Hitmaker",
                currentBase = 100000L,
                nextTarget = 500000L,
                unlocks = listOf(
                    "National Theater Tour Circuits",
                    "Major Brand Sponsorship Deals",
                    "Top-Tier Studio Producers & Mixers"
                )
            )
            fans < 2500000L -> TierConfig(
                level = 4,
                title = "Headlining Hitmaker",
                nextTitle = "Platinum Chart-Topper",
                currentBase = 500000L,
                nextTarget = 2500000L,
                unlocks = listOf(
                    "Arena & Stadium Tour Headliner",
                    "Custom Merchandise Empire",
                    "National Radio Airplay Saturation"
                )
            )
            fans < 10000000L -> TierConfig(
                level = 5,
                title = "Platinum Chart-Topper",
                nextTitle = "Global Megastar",
                currentBase = 2500000L,
                nextTarget = 10000000L,
                unlocks = listOf(
                    "World Festival Headliner",
                    "Establish Own Imprint Record Label",
                    "Global Luxury Brand Ambassadorship"
                )
            )
            else -> TierConfig(
                level = 6,
                title = "Global Megastar",
                nextTitle = "Hall of Fame Icon",
                currentBase = 10000000L,
                nextTarget = 50000000L,
                unlocks = listOf(
                    "Hall of Fame Lifetime Induction",
                    "Master Rights Catalog Buyback",
                    "Private Equity & Venture Fund"
                )
            )
        }
    }

    val progressPercent = if (tierInfo.nextTarget > tierInfo.currentBase) {
        ((fans - tierInfo.currentBase).toFloat() / (tierInfo.nextTarget - tierInfo.currentBase).toFloat()).coerceIn(0.02f, 1.0f)
    } else 1.0f

    val fansRemaining = (tierInfo.nextTarget - fans).coerceAtLeast(0L)
    val isCloseToLevelUp = progressPercent >= 0.75f

    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            if (isCloseToLevelUp) 1.5.dp else 1.dp,
            if (isCloseToLevelUp) WarningColor else BorderColor
        ),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Level Badge & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryBrandColor.copy(alpha = 0.2f))
                            .border(1.dp, PrimaryBrandLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "LEVEL ${tierInfo.level}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = PrimaryBrandLight
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = tierInfo.title.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${(progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (isCloseToLevelUp) WarningColor else PrimaryBrandLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = progressPercent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isCloseToLevelUp) WarningColor else PrimaryBrandLight,
                trackColor = SurfaceColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%,d", fans)} fans",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = TextSecondary
                )
                Text(
                    text = "Goal: ${String.format("%,d", tierInfo.nextTarget)} fans",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = TextTertiary
                )
            }

            // Close to Level Up Alert Banner
            if (isCloseToLevelUp) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarningColor.copy(alpha = 0.12f))
                        .border(1.dp, WarningColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = WarningColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⚡ Near Breakthrough! Only ${String.format("%,d", fansRemaining)} fans until Level ${tierInfo.level + 1}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = WarningColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = BorderColor.copy(alpha = 0.6f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Next Level Unlocks
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEXT UNLOCKS (LVL ${tierInfo.level + 1} — ${tierInfo.nextTitle.uppercase()})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    ),
                    color = CreativePurple
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            tierInfo.unlocks.forEach { unlock ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(CreativePurple)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = unlock,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

private data class TierConfig(
    val level: Int,
    val title: String,
    val nextTitle: String,
    val currentBase: Long,
    val nextTarget: Long,
    val unlocks: List<String>
)

/**
 * Storytelling Statistics Interactive Panel
 */
@Composable
fun StorytellingStatsCard(
    stats: List<StorytellingStat>,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = remember(stats) {
        listOf("All") + stats.map { it.category }.distinct()
    }

    val filteredStats = remember(stats, selectedCategory) {
        if (selectedCategory == "All") stats else stats.filter { it.category == selectedCategory }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, BorderColor),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Artist Core Attributes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Real-time proficiencies, career impacts & growth drivers",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) PrimaryBrandColor else SurfaceColor,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryBrandLight else BorderColor
                        )
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Storytelling Stats List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredStats.forEach { stat ->
                    StorytellingStatRow(stat = stat)
                }
            }
        }
    }
}

@Composable
private fun StorytellingStatRow(stat: StorytellingStat) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceColor.copy(alpha = 0.6f))
            .border(1.dp, BorderColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(14.dp)
    ) {
        Column {
            // Main Top Stat Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stat.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Trend Chip
                        val deltaText = if (stat.delta > 0) "+${String.format("%.1f", stat.delta)}%" else if (stat.delta < 0) "${String.format("%.1f", stat.delta)}%" else "Steady"
                        val deltaColor = if (stat.delta > 0) SuccessColor else if (stat.delta < 0) DangerColor else TextTertiary
                        Text(
                            text = deltaText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = deltaColor
                        )
                    }
                    Text(
                        text = stat.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                // Numeric Percentage Display
                Text(
                    text = "${String.format("%.1f", stat.value)}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = stat.color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = (stat.value / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = stat.color,
                trackColor = CardBg
            )

            // Consequence & Influence Details (expandable or default view)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "⚡ Consequence: ${stat.consequence}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 6.dp)) {
                    Text(
                        text = "🎯 Growth Drivers: ${stat.influencedBy}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBrandLight
                    )
                }
            }
        }
    }
}

/**
 * Artist Well-being and Mental Health Condition Dashboard
 */
@Composable
fun ArtistWellbeingDashboard(
    stress: Float,
    creativeInspiration: Float,
    happiness: Float,
    workLifeBalance: Float,
    energy: Int,
    burnout: Float,
    cash: Double,
    onRestAndHobby: () -> Unit,
    onSeekTherapy: () -> Unit,
    onTakeVacation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val conditionStatus = when {
        burnout > 30f || stress >= 80f -> "CRITICAL BURNOUT ⚠️"
        stress >= 60f || energy <= 20 -> "EXHAUSTED & STRAINED ⚡"
        creativeInspiration >= 70f && happiness >= 70f -> "INSPIRED & THRIVING ✨"
        else -> "BALANCED & STEADY 🧘"
    }

    val conditionColor = when {
        burnout > 30f || stress >= 80f -> DangerColor
        stress >= 60f || energy <= 20 -> WarningColor
        creativeInspiration >= 70f && happiness >= 70f -> CreativePurple
        else -> SuccessColor
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, if (burnout > 20f || stress >= 75f) DangerColor.copy(alpha = 0.6f) else BorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top Section Header & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ARTIST CONDITION & VITALITY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = conditionStatus,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = conditionColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(conditionColor.copy(alpha = 0.15f))
                        .border(1.dp, conditionColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Energy: $energy / 100",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = conditionColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Vital Metrics Stack
            ConditionMetricBar(
                name = "Mental Stress",
                value = stress,
                max = 100f,
                color = if (stress > 60f) DangerColor else PrimaryBrandLight,
                stateNote = if (stress > 75f) "Severe Risk" else if (stress > 45f) "Elevated" else "Healthy"
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConditionMetricBar(
                name = "Creative Inspiration",
                value = creativeInspiration,
                max = 100f,
                color = CreativePurple,
                stateNote = if (creativeInspiration > 75f) "Flow State" else if (creativeInspiration > 40f) "Active" else "Dry Spell"
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConditionMetricBar(
                name = "General Happiness",
                value = happiness,
                max = 100f,
                color = SuccessColor,
                stateNote = if (happiness > 70f) "Joyful" else if (happiness > 40f) "Content" else "Melancholic"
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConditionMetricBar(
                name = "Work-Life Balance",
                value = workLifeBalance,
                max = 100f,
                color = WarningColor,
                stateNote = if (workLifeBalance > 60f) "Sustainable" else "Overworked"
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = BorderColor.copy(alpha = 0.6f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CONTEXTUAL SELF-CARE PROTOCOLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                ),
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Protocol Action Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Rest Protocol
                Surface(
                    onClick = onRestAndHobby,
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = WarningColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rest & Hobby",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "+Energy, -Stress",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessColor
                        )
                        Text(
                            text = "Free / 40 Stamina",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }

                // Therapy Protocol
                Surface(
                    onClick = onSeekTherapy,
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = DangerColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Seek Therapy",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "-30 Stress",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessColor
                        )
                        Text(
                            text = "$1,000 Cash",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (cash >= 1000) TextTertiary else DangerColor
                            )
                        )
                    }
                }

                // Vacation Protocol
                Surface(
                    onClick = onTakeVacation,
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceColor,
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = CreativePurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Hawaii Trip",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "-35 Stress • +30 Inspiration",
                            style = MaterialTheme.typography.labelSmall,
                            color = CreativePurple
                        )
                        Text(
                            text = "$5,000 Cash",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (cash >= 5000) TextTertiary else DangerColor
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConditionMetricBar(
    name: String,
    value: Float,
    max: Float,
    color: Color,
    stateNote: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stateNote,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${value.toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (value / max).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = SurfaceColor
        )
    }
}

/**
 * Polished Weekly Transition Dialog
 */
@Composable
fun WeeklyAdvanceSummaryDialog(
    currentWeek: Int,
    currentYear: Int,
    fansDelta: Long,
    cashDelta: Double,
    cashDeltaLabel: String,
    cashDeltaNote: String,
    listenersDelta: Long,
    stressDelta: Float,
    headline: String,
    onConfirmAdvance: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryBrandColor.copy(alpha = 0.2f))
                        .border(1.5.dp, PrimaryBrandLight, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = PrimaryBrandLight,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "WEEK $currentWeek COMPLETE",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Year $currentYear Simulation Cycle Summary",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Key Delta Metric Rows Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        WeeklyDeltaRow(
                            label = cashDeltaLabel,
                            deltaText = if (cashDelta >= 0) "+$${String.format("%,.0f", cashDelta)}" else "-$${String.format("%,.0f", -cashDelta)}",
                            isPositive = cashDelta >= 0,
                            icon = Icons.Default.AttachMoney
                        )
                        WeeklyDeltaRow(
                            label = "Audience Fan Growth",
                            deltaText = if (fansDelta >= 0) "+${String.format("%,d", fansDelta)}" else "${String.format("%,d", fansDelta)}",
                            isPositive = fansDelta >= 0,
                            icon = Icons.Default.People
                        )
                        WeeklyDeltaRow(
                            label = "Monthly Listeners",
                            deltaText = if (listenersDelta >= 0) "+${String.format("%,d", listenersDelta)}" else "${String.format("%,d", listenersDelta)}",
                            isPositive = listenersDelta >= 0,
                            icon = Icons.Default.Headphones
                        )
                        WeeklyDeltaRow(
                            label = "Stress Trajectory",
                            deltaText = if (stressDelta > 0) "+${String.format("%.1f", stressDelta)}%" else "${String.format("%.1f", stressDelta)}%",
                            isPositive = stressDelta <= 0,
                            icon = Icons.Default.Favorite
                        )
                        Text(
                            text = cashDeltaNote,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // World Headline
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceColor)
                        .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "📰 CAREER & INDUSTRY HEADLINE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryBrandLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = headline.ifEmpty { "Producers and tour promoters are monitoring your career trajectory closely." },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmAdvance,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "START WEEK ${currentWeek + 1}",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Review Decisions First",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
            }
        }
    )
}

@Composable
private fun WeeklyDeltaRow(
    label: String,
    deltaText: String,
    isPositive: Boolean,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isPositive) SuccessColor else DangerColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Text(
            text = deltaText,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = if (isPositive) SuccessColor else DangerColor
        )
    }
}

/**
 * Immediate, Satisfying Action Feedback Toast / HUD
 */
@Composable
fun ActionFeedbackHUD(
    feedback: ActionFeedback?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = feedback != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(animationSpec = tween(250)),
        modifier = modifier
    ) {
        if (feedback != null) {
            LaunchedEffect(feedback.id) {
                kotlinx.coroutines.delay(4000)
                onDismiss()
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onDismiss() },
                shape = RoundedCornerShape(16.dp),
                color = CardBg,
                shadowElevation = 10.dp,
                border = BorderStroke(1.5.dp, PrimaryBrandColor.copy(alpha = 0.8f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryBrandColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (feedback.iconType) {
                                        "music" -> Icons.Default.MusicNote
                                        "share" -> Icons.Default.Share
                                        "mic" -> Icons.Default.Mic
                                        "money" -> Icons.Default.AttachMoney
                                        "rest" -> Icons.Default.Bedtime
                                        "therapy" -> Icons.Default.Favorite
                                        else -> Icons.Default.Bolt
                                    },
                                    contentDescription = null,
                                    tint = PrimaryBrandLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = feedback.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                if (feedback.subtitle.isNotEmpty()) {
                                    Text(
                                        text = feedback.subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Feedback",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (feedback.deltas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            feedback.deltas.forEach { delta ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (delta.isPositive) SuccessColor.copy(alpha = 0.12f) else DangerColor.copy(alpha = 0.12f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (delta.isPositive) SuccessColor.copy(alpha = 0.35f) else DangerColor.copy(alpha = 0.35f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = delta.label.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = delta.deltaText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            color = if (delta.isPositive) SuccessColor else DangerColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic Career News & Reports Section displaying previous week summary, living expenses,
 * staff actions, royalties, MeTube gains, relationships, marriage, gossip, and state updates.
 */
@Composable
fun CareerNewsReportSection(
    weeklyReport: String,
    currentWeek: Int,
    currentYear: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Career News & Reports",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFFF5252),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F0E17),
                border = BorderStroke(1.dp, Color(0xFF282538)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val displayText = if (weeklyReport.isNotBlank()) {
                        weeklyReport
                    } else {
                        "=== WEEK $currentWeek SUMMARY (YEAR $currentYear) ===\n\n" +
                        "🏠 Weekly Expenses: Standard living expenses and studio overhead tracked.\n" +
                        "🎵 Music Income: Royalties and catalog streams calculated upon week advance.\n" +
                        "👥 Staff & Operations: All active team departments ready for this cycle.\n" +
                        "Advance to next week to generate detailed weekly financial, staff, and relationship reports."
                    }

                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            lineHeight = 22.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}
