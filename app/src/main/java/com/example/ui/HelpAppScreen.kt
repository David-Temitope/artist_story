package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameStateEntity
import com.example.data.GameViewModel

private val BgDark = Color(0xFF0F0F14)
private val CardBgDark = Color(0xFF181722)
private val PrimaryEmerald = Color(0xFF00E676)
private val AccentTeal = Color(0xFF00BFA5)
private val AccentCyan = Color(0xFF00E5FF)
private val AccentGold = Color(0xFFFFD54F)
private val AccentPink = Color(0xFFFF4081)
private val TextMain = Color(0xFFF0F0F5)
private val TextMuted = Color(0xFF9E9EA7)
private val BorderSubtle = Color(0xFF282738)

data class HelpTopic(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val icon: ImageVector,
    val details: List<String>,
    val proTips: List<String> = emptyList()
)

@Composable
fun HelpAppScreen(
    state: GameStateEntity,
    viewModel: GameViewModel,
    onClose: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedTopicId by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "All",
        "Music & Studio",
        "Staff & Agency",
        "Social & SMM",
        "Brand Deals",
        "Banking & Wealth",
        "Label & Empire",
        "Wellness"
    )

    val helpTopics = remember {
        listOf(
            // Music & Studio
            HelpTopic(
                id = "studio_recording",
                title = "Studio Recording & Acoustic Quality",
                summary = "How song composition, producers, mixing styles, and acoustic decor drive hit ratings.",
                category = "Music & Studio",
                icon = Icons.Default.MusicNote,
                details = listOf(
                    "Song ratings range from 1.0 to 5.0 ⭐ based on your Lyric Writing, Vocal Skill, Producer quality, and Studio acoustics.",
                    "Choosing premium sound engineers ('Professional' or 'Grammy-winning') and mixing styles ('Dolby Atmos') significantly boosts initial song hype and DSP playlisting chances.",
                    "Unreleased songs generate hype over their scheduled weeks through pre-release marketing campaigns on Cast, MeTube, and Radio."
                ),
                proTips = listOf(
                    "Keep your Energy > 40 when recording to avoid vocal fatigue penalties.",
                    "High Industry Respect grants up to a 20% discount when hiring elite producers."
                )
            ),
            HelpTopic(
                id = "copyright_licensing",
                title = "Copyright Licensing & Master Rights",
                summary = "Protecting your songs from plagiarism, lawsuits, and boosting streaming royalties.",
                category = "Music & Studio",
                icon = Icons.Default.Copyright,
                details = listOf(
                    "You can purchase Federal Copyright Protection for $250 per song during studio recording or anytime in your Studio Discography archive.",
                    "Registered songs are 100% legally protected against plagiarism claims, sample clearance lawsuits, and unauthorized online bootlegs.",
                    "Clean master rights provide a +20% streaming revenue multiplier across all DSP streaming platforms and unlock high-paying commercial sync licensing offers in your inbox."
                ),
                proTips = listOf(
                    "Always register copyright before launching aggressive marketing campaigns or signing major distribution deals."
                )
            ),
            HelpTopic(
                id = "dsp_streaming",
                title = "Streaming Platforms (DSP) & SoundStream",
                summary = "Uploading singles to Tunes, Sharp, Jams, Megaphone, Songify, and climbing the Global Top 100.",
                category = "Music & Studio",
                icon = Icons.Default.GraphicEq,
                details = listOf(
                    "Each streaming platform has its own audience demographic and revenue rate per stream. Premium platforms (Megaphone, Songify) require upfront distribution fees but pay higher royalties.",
                    "SoundStream tracks your real-time Monthly Active Listeners and global chart position. Reaching the Top 10 unlocks massive viral reach and prestigious festival bookings."
                ),
                proTips = listOf(
                    "Upload high-rated songs (>4.0⭐) to all platforms simultaneously to maximize compound weekly royalty payouts."
                )
            ),

            // Staff & Agency
            HelpTopic(
                id = "accountant_role",
                title = "Accountant (CPA & Royalty Auditor)",
                summary = "Tax optimization, expense write-offs, forensic royalty recovery, and cash-flow management.",
                category = "Staff & Agency",
                icon = Icons.Default.Badge,
                details = listOf(
                    "Tax Optimization: Audits all studio equipment, touring travel, and business overhead to legally slash weekly tax liability by 15% to 35%.",
                    "Forensic Royalty Audits: Periodically audits streaming platforms and record labels to locate unpaid digital/mechanical royalties, recovering $400 to $12,000 checks directly to your balance.",
                    "Prudent Dip-Buying: Unlike aggressive traders, an Accountant only buys shares if stock prices plunge to deep-discount bargain levels and your savings account has healthy liquidity.",
                    "Cash Flow Statements: Generates transparent weekly financial reports in your mailbox summarizing all revenue streams and expense burn."
                ),
                proTips = listOf(
                    "Hiring an Accountant with 70+ Experience protects you from IRS tax audits and ensures flawless compliance."
                )
            ),
            HelpTopic(
                id = "financial_advisor_role",
                title = "Financial Advisor (Wealth Manager)",
                summary = "Portfolio asset allocation, active profit taking (+25%), fixed-income bonds, and market research.",
                category = "Staff & Agency",
                icon = Icons.Default.TrendingUp,
                details = listOf(
                    "Strategic Wealth Planning: Manages capital across WESHARE Stocks, Genesis Crypto, and High-Yield Government Bonds.",
                    "Active Profit Taking: Automatically sells winning stock positions when they reach target profit margins (+25% to +50%), locking in gains into safe cash reserves.",
                    "Passive Income Yield: Allocates surplus savings into bonds generating guaranteed 0.8% to 1.2% weekly yield.",
                    "Market Alpha: Delivers in-depth fundamental stock research directly to your inbox highlighting undervalued market opportunities."
                ),
                proTips = listOf(
                    "Use a Financial Advisor when your net worth exceeds $50,000 to automate high-yield compound wealth generation."
                )
            ),
            HelpTopic(
                id = "other_staff_roles",
                title = "Manager, Lawyer, PR & Security",
                summary = "Full breakdown of Talent Agents, Business Managers, Legal Counsel, PR Spin, and Aegis Command.",
                category = "Staff & Agency",
                icon = Icons.Default.People,
                details = listOf(
                    "Talent Agent / Business Manager: Boosts incoming brand deal frequency and negotiates up to +40% higher contract payouts.",
                    "Lawyer: Audits incoming contracts, reduces label 360 cuts, defends against rogue employees, and wins lawsuit judgments.",
                    "PR Manager: Fixes damaged public reputation (+1.5% weekly), spins scandals into publicity, and boosts viral social traction.",
                    "Security: Protects your residence, prevents stalker break-ins, thwarts rogue employee embezzlement, and safeguards studio equipment."
                )
            ),

            // Social & SMM
            HelpTopic(
                id = "smm_management",
                title = "Social Media Manager (SMM) & Cast Automation",
                summary = "Auto-posting on Cast, assigning specialized content categories, and brand sponsorship fulfillment.",
                category = "Social & SMM",
                icon = Icons.Default.AlternateEmail,
                details = listOf(
                    "Cast Auto-Posting: Your hired SMM creates authentic weekly Cast posts on your behalf, steadily growing followers, trending score, and public image.",
                    "Category Specialization: Assign your SMM to specific niches (Music & Art, Tech & Gadgets, Fashion & Lifestyle, Gaming, Fitness, Finance).",
                    "Brand Sponsorship Fulfillment: When you have an active brand deal, your SMM automatically publishes sponsored promotional posts with sponsor hashtags, fulfilling contract requirements so you get paid every week!",
                    "Niche Consistency: Consistently posting in a category attracts high-tier corporate brand deals tailored to your specialized audience."
                ),
                proTips = listOf(
                    "Configure your SMM Category in Staff Hub or Settings to match the brands you want to attract."
                )
            ),
            HelpTopic(
                id = "metube_growth",
                title = "MeTube Video Ecosystem & Ad Revenue",
                summary = "Publishing music videos, studio vlogs, interviews, and claiming weekly ad revenue payouts.",
                category = "Social & SMM",
                icon = Icons.Default.PlayArrow,
                details = listOf(
                    "Publish Music Videos, Studio Vlogs, Performance Clips, and Interviews to build a subscriber empire.",
                    "Claimable Revenue: Your channel generates weekly ad revenue based on video views and subscriber count. Tap 'CLAIM' in the MeTube app anytime to transfer earnings to your bank.",
                    "Rival Artists: Explore community videos from other artists to discover trending industry genres and rival chart competitors."
                )
            ),

            // Brand Deals
            HelpTopic(
                id = "brand_sponsorships",
                title = "Brand Deals, Contracts & Endorsements",
                summary = "Negotiating sponsor contracts, weekly engagement rules, and maintaining sponsor trust.",
                category = "Brand Deals",
                icon = Icons.Default.BusinessCenter,
                details = listOf(
                    "Brand proposals arrive in your Mailbox based on your subscriber count, follower count, and category consistency.",
                    "Weekly Requirement: You (or your SMM) must publish at least one promotional post or video each week to receive that week's payout.",
                    "Morality & Reputation: High-tier brands require a Public Image > 40%. Getting involved in public scandals may void active contracts."
                ),
                proTips = listOf(
                    "Hire a Talent Agent to unlock six-figure luxury brand ambassador contracts."
                )
            ),

            // Banking & Wealth
            HelpTopic(
                id = "apex_bank",
                title = "Apex Bank: Savings, Loans & Tax Compliance",
                summary = "Earning compound weekly interest, refinancing debt, and avoiding tax penalties.",
                category = "Banking & Wealth",
                icon = Icons.Default.AccountBalance,
                details = listOf(
                    "High-Yield Savings: Apex Bank pays 1.2% weekly compound interest on all deposited savings.",
                    "Loan Refinancing: Borrow capital for studio sessions, tours, or real estate. Make prompt weekly payments to prevent interest compounding.",
                    "Tax Compliance: Unpaid tax debt over 3 consecutive weeks leads to bank asset freezes and public reputation damage. Keep an emergency tax buffer!"
                )
            ),
            HelpTopic(
                id = "investments_weshare",
                title = "WESHARE Stocks & Genesis Crypto",
                summary = "Trading equities, dividend aristocrats, swing trading, and cryptocurrency volatility.",
                category = "Banking & Wealth",
                icon = Icons.Default.TrendingUp,
                details = listOf(
                    "WESHARE lists real music industry equities (streaming DSPs, record labels, instrument manufacturers).",
                    "Dividend Stocks pay cash directly to your bank account every week based on their dividend yield %.",
                    "Genesis Crypto offers massive exponential growth with high volatility. Only allocate capital you can afford to hold during bear markets."
                )
            ),

            // Label & Empire
            HelpTopic(
                id = "found_record_label",
                title = "Founding Your Own Record Label",
                summary = "Establishing an independent imprint, upgrading departments, and signing roster talent.",
                category = "Label & Empire",
                icon = Icons.Default.Storefront,
                details = listOf(
                    "Found your independent label in the Business Empire tab once you reach 200,000 fans and $50,000 cash.",
                    "Upgrade Production, Marketing, Legal, and A&R departments to slash studio costs and sign talented NPC artists.",
                    "Artist Contracts: Earn 20% to 50% executive cuts on all streams, merchandise, and tours completed by your signed roster."
                )
            ),
            HelpTopic(
                id = "legacy_milestones",
                title = "Career Legacy & Hall of Fame Milestones",
                summary = "Wikipedia pages, Netflix documentaries, museum exhibits, and lifetime achievement.",
                category = "Label & Empire",
                icon = Icons.Default.EmojiEvents,
                details = listOf(
                    "Achieve historical career milestones by amassing millions of streams, winning awards, and building business conglomerates.",
                    "Unlock Wikipedia pages, published memoirs, streaming documentaries, and permanent induction into the Music Hall of Fame."
                )
            ),

            // Wellness
            HelpTopic(
                id = "wellness_burnout",
                title = "Energy, Stress & Burnout Management",
                summary = "Balancing tour schedules, vacations, mental wellness, and spiritual rejuvenation.",
                category = "Wellness",
                icon = Icons.Default.Spa,
                details = listOf(
                    "Energy regenerates each week based on your home luxury tier and sleep quality.",
                    "High stress (>75%) triggers creative burnout, lowering songwriting ratings and causing missed tour dates.",
                    "Use Soul Care app, schedule spa retreats, attend church/prayer, or take luxury vacations to rapidly relieve stress."
                )
            )
        )
    }

    val filteredTopics = remember(searchQuery, selectedCategory, helpTopics) {
        helpTopics.filter { topic ->
            val matchesCategory = selectedCategory == "All" || topic.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                topic.title.contains(searchQuery, ignoreCase = true) ||
                topic.summary.contains(searchQuery, ignoreCase = true) ||
                topic.details.any { it.contains(searchQuery, ignoreCase = true) } ||
                topic.proTips.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .testTag("help_app_screen")
    ) {
        // App Top Bar
        Surface(
            color = Color(0xFF14131C),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(0.5.dp, BorderSubtle)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(PrimaryEmerald, AccentTeal))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help Guide",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Artist Guide & Academy",
                            color = TextMain,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Comprehensive Career Strategy & Systems",
                            color = TextMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("help_app_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Help",
                        tint = TextMuted
                    )
                }
            }
        }

        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search mechanics, staff roles, copyright, taxes...",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PrimaryEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("help_search_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryEmerald,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = CardBgDark,
                    unfocusedContainerColor = CardBgDark,
                    focusedTextColor = TextMain,
                    unfocusedTextColor = TextMain
                )
            )
        }

        // Horizontal Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryEmerald.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryEmerald,
                        containerColor = CardBgDark,
                        labelColor = TextMuted
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) PrimaryEmerald else BorderSubtle
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Topic Listing
        if (filteredTopics.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching guides found",
                        color = TextMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Try searching with different keywords like 'Accountant', 'Copyright', or 'SMM'",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp, top = 6.dp)
            ) {
                items(filteredTopics, key = { it.id }) { topic ->
                    val isExpanded = expandedTopicId == topic.id

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBgDark),
                        border = BorderStroke(
                            1.dp,
                            if (isExpanded) PrimaryEmerald.copy(alpha = 0.6f) else BorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedTopicId = if (isExpanded) null else topic.id
                            }
                            .testTag("help_card_${topic.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PrimaryEmerald.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = topic.icon,
                                        contentDescription = null,
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = topic.title,
                                        color = TextMain,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = topic.category.uppercase(),
                                        color = AccentTeal,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = topic.summary,
                                color = TextMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            // Expanded Detailed Breakdown
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(0.5.dp)
                                            .background(BorderSubtle)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "CORE MECHANICS & RULES",
                                        color = PrimaryEmerald,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    topic.details.forEach { detail ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "•",
                                                color = PrimaryEmerald,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(end = 6.dp)
                                            )
                                            Text(
                                                text = detail,
                                                color = TextMain.copy(alpha = 0.9f),
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    if (topic.proTips.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(AccentGold.copy(alpha = 0.1f))
                                                .border(
                                                    0.5.dp,
                                                    AccentGold.copy(alpha = 0.3f),
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.Top) {
                                                Icon(
                                                    imageVector = Icons.Default.Lightbulb,
                                                    contentDescription = "Pro Tip",
                                                    tint = AccentGold,
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .padding(top = 1.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "STRATEGIC PRO TIP",
                                                        color = AccentGold,
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    topic.proTips.forEach { tip ->
                                                        Text(
                                                            text = tip,
                                                            color = Color.White.copy(alpha = 0.9f),
                                                            fontSize = 11.sp,
                                                            lineHeight = 15.sp,
                                                            modifier = Modifier.padding(top = 2.dp)
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
            }
        }
    }
}
