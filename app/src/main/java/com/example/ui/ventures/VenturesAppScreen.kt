package com.example.ui.ventures

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*

private val VentureBg = Color(0xFF0F121C)
private val VentureCardBg = Color(0xFF171B28)
private val VentureCardBorder = Color(0xFF262C3F)
private val VentureGreen = Color(0xFF00E676)
private val VenturePurple = Color(0xFF7C4DFF)
private val VentureGold = Color(0xFFFFB74D)
private val VentureRed = Color(0xFFFF5252)
private val TextWhite = Color(0xFFFFFFFF)
private val TextMuted = Color(0xFF8F9BB3)

@Composable
fun VenturesAppScreen(state: GameStateEntity, viewModel: GameViewModel) {
    var selectedTab by remember { mutableStateOf("My Enterprises") }
    var selectedVentureId by remember { mutableStateOf<String?>(null) }
    var launchTargetType by remember { mutableStateOf<String?>(null) }
    var ipoTargetVenture by remember { mutableStateOf<BusinessVenture?>(null) }
    var manageFundsVenture by remember { mutableStateOf<BusinessVenture?>(null) }

    val ownedVentures = state.businessVentures.filter { it.isOwned && !it.isShutDownByRegulators }

    Column(modifier = Modifier.fillMaxSize().background(VentureBg)) {
        // Executive Top Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF1A1F36), Color(0xFF141829))))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = VentureGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    Text("Business empire", color = TextWhite, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Build it. Run it. Protect it.", color = TextMuted, fontSize = 12.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0A0D15))
                        .border(1.dp, VentureGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = VentureGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$${String.format("%,.0f", state.cash)}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Navigation Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF141724))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple("My Enterprises", "My Enterprises (${ownedVentures.size})", Icons.Default.Storefront),
                Triple("Launch", "Launch Enterprise", Icons.Default.AddBusiness),
                Triple("WeShare IPOs", "WeShare IPOs", Icons.Default.Public)
            ).forEach { (tabKey, label, icon) ->
                val isSelected = selectedTab == tabKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) VenturePurple.copy(alpha = 0.25f) else Color.Transparent)
                        .border(1.dp, if (isSelected) VenturePurple else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { selectedTab = tabKey }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                "My Enterprises" -> MyEnterprisesTab(
                    ownedVentures = ownedVentures,
                    state = state,
                    viewModel = viewModel,
                    selectedVentureId = selectedVentureId,
                    onSelectVenture = { selectedVentureId = it },
                    onClearVenture = { selectedVentureId = null },
                    onOpenLaunchTab = { selectedTab = "Launch" },
                    onOpenIpoDialog = { ipoTargetVenture = it },
                    onOpenManageFunds = { manageFundsVenture = it }
                )
                "Launch" -> LaunchEnterprisesTab(
                    state = state,
                    onSelectType = { launchTargetType = it }
                )
                "WeShare IPOs" -> WeShareIpoOverviewTab(
                    ownedVentures = ownedVentures,
                    state = state,
                    onOpenIpoDialog = { ipoTargetVenture = it }
                )
            }
        }
    }

    // Launch Business Dialog
    launchTargetType?.let { type ->
        LaunchBusinessDialog(
            type = type,
            playerCash = state.cash,
            onDismiss = { launchTargetType = null },
            onConfirm = { customName ->
                viewModel.startBusinessVenture(type, customName.ifBlank { null })
                launchTargetType = null
                selectedTab = "My Enterprises"
            }
        )
    }

    // WeShare IPO Dialog
    ipoTargetVenture?.let { venture ->
        WeShareIpoDialog(
            venture = venture,
            state = state,
            onDismiss = { ipoTargetVenture = null },
            onConfirmListing = { percent ->
                viewModel.listBusinessOnWeShare(venture.id, percent)
                ipoTargetVenture = null
            }
        )
    }

    // Manage Funds Dialog (Deposit/Withdraw)
    manageFundsVenture?.let { venture ->
        ManageFundsDialog(
            venture = venture,
            playerCash = state.cash,
            onDismiss = { manageFundsVenture = null },
            onDeposit = { amt ->
                viewModel.investBusinessCapital(venture.id, amt)
                manageFundsVenture = null
            },
            onWithdraw = { amt ->
                viewModel.withdrawBusinessFunds(venture.id, amt)
                manageFundsVenture = null
            }
        )
    }
}

@Composable
private fun MyEnterprisesTab(
    ownedVentures: List<BusinessVenture>,
    state: GameStateEntity,
    viewModel: GameViewModel,
    selectedVentureId: String?,
    onSelectVenture: (String) -> Unit,
    onClearVenture: () -> Unit,
    onOpenLaunchTab: () -> Unit,
    onOpenIpoDialog: (BusinessVenture) -> Unit,
    onOpenManageFunds: (BusinessVenture) -> Unit
) {
    if (selectedVentureId != null) {
        val selectedVenture = ownedVentures.find { it.id == selectedVentureId }
        if (selectedVenture != null) {
            BusinessDedicatedScreen(
                venture = selectedVenture,
                state = state,
                viewModel = viewModel,
                onBack = onClearVenture,
                onOpenIpoDialog = { onOpenIpoDialog(selectedVenture) },
                onOpenManageFunds = { onOpenManageFunds(selectedVenture) }
            )
            return
        }
    }

    if (ownedVentures.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(VentureCardBg)
                        .border(1.dp, VentureCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("No Active Business Ventures", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Launch cash-generating enterprises like a Taxi Fleet, Restaurant, Farm, Hotel, or Web3 Token to create dependable weekly recurring income.",
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onOpenLaunchTab,
                    colors = ButtonDefaults.buttonColors(containerColor = VenturePurple),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(46.dp)
                ) {
                    Icon(Icons.Default.AddBusiness, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Explore Enterprise Opportunities", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portfolio Summary Card
            item {
                val totalGross = ownedVentures.sumOf { it.weeklyGrossRevenue }
                val totalExp = ownedVentures.sumOf { it.weeklyOperatingExpenses }
                val totalNet = ownedVentures.sumOf { it.weeklyNetProfit }
                val totalCap = ownedVentures.sumOf { it.totalCapitalInvested }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(VentureCardBg)
                        .border(1.dp, VentureCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Insights, contentDescription = null, tint = VentureGold, modifier = Modifier.size(18.dp))
                        Text("Your portfolio", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("NET EACH WEEK", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("${if (totalNet >= 0) "+" else ""}$${String.format("%,.0f", totalNet)}", color = if (totalNet >= 0) VentureGreen else VentureRed, fontSize = 27.sp, fontWeight = FontWeight.Black)
                    Text("after operating costs", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Weekly Gross Inflow", color = TextMuted, fontSize = 11.sp)
                            Text("+$${String.format("%,.0f", totalGross)}/wk", color = VentureGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Weekly Operating Costs", color = TextMuted, fontSize = 11.sp)
                            Text("-$${String.format("%,.0f", totalExp)}/wk", color = VentureRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = VentureCardBorder)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Capital Invested: $${String.format("%,.0f", totalCap)}", color = TextMuted, fontSize = 11.sp)
                        Text("${ownedVentures.size} Active Enterprises", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("YOUR ENTERPRISES", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("Tap to open business dashboard", color = TextMuted, fontSize = 11.sp)
                }
            }

            // List of Businesses as uncluttered overview cards
            items(ownedVentures, key = { it.id }) { venture ->
                EnterpriseOverviewCard(
                    venture = venture,
                    onClick = { onSelectVenture(venture.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun EnterpriseOverviewCard(
    venture: BusinessVenture,
    onClick: () -> Unit
) {
    val icon = when (venture.type) {
        "Taxi Fleet" -> Icons.Default.LocalTaxi
        "Restaurant" -> Icons.Default.Restaurant
        "Farming" -> Icons.Default.Agriculture
        "Hotel", "Hotel Chain" -> Icons.Default.Hotel
        "Crypto Token" -> Icons.Default.Token
        else -> Icons.Default.Business
    }

    val isPos = venture.weeklyNetProfit >= 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VentureCardBg)
            .border(1.dp, VentureCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Icon, Name, Type, and Net Cashflow Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VenturePurple.copy(alpha = 0.2f))
                        .border(1.dp, VenturePurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = VenturePurple, modifier = Modifier.size(24.dp))
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(venture.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(VenturePurple.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Tier ${venture.tierLevel}", color = VenturePurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(venture.type, color = TextMuted, fontSize = 12.sp)
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPos) VentureGreen.copy(alpha = 0.15f) else VentureRed.copy(alpha = 0.15f))
                    .border(1.dp, if (isPos) VentureGreen.copy(alpha = 0.4f) else VentureRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    "${if (isPos) "+" else ""}${formatCompactMoney(venture.weeklyNetProfit)}/wk",
                    color = if (isPos) VentureGreen else VentureRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Divider(color = VentureCardBorder)

        // Row 2: Metrics grid (Gross, Operating Costs, Scale / Capacity)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Gross Revenue", color = TextMuted, fontSize = 10.sp)
                Text("+${formatCompactMoney(venture.weeklyGrossRevenue)}/wk", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Column {
                Text("Operating Costs", color = TextMuted, fontSize = 10.sp)
                Text("-${formatCompactMoney(venture.weeklyOperatingExpenses)}/wk", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Enterprise Value", color = TextMuted, fontSize = 10.sp)
                Text(formatCompactMoney(venture.valuation), color = Color(0xFF67E8F9), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Bottleneck / Churn alerts if any
        if (venture.type == "Taxi Fleet" && venture.taxiCarCount > 0 && venture.taxiDriverCount < venture.taxiCarCount) {
            val shortage = venture.taxiCarCount - venture.taxiDriverCount
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(VentureGold.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
                Text(
                    "Driver Shortage: $shortage car${if (shortage > 1) "s" else ""} idle without drivers! Trip fulfillment dropping.",
                    color = VentureGold,
                    fontSize = 11.sp
                )
            }
        } else if (venture.type == "Hotel" && venture.hotelRooms.isNotEmpty() && venture.hotelHousekeeperCount * 8 < venture.hotelRooms.size) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(VentureGold.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
                Text(
                    "Housekeeping Deficit: Rooms cannot be cleaned properly! Guest churn active.",
                    color = VentureGold,
                    fontSize = 11.sp
                )
            }
        }

        // Action CTA
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(42.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VentureCardBorder),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Open Business Management", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun BusinessDedicatedScreen(
    venture: BusinessVenture,
    state: GameStateEntity,
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onOpenIpoDialog: () -> Unit,
    onOpenManageFunds: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VentureBg)
    ) {
        // Dedicated Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VentureCardBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(VentureCardBorder)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Portfolio",
                    tint = TextWhite
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        venture.name,
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VenturePurple.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "Tier ${venture.tierLevel}",
                            color = VenturePurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    "${venture.type} Enterprise Operations",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            // Quick cashflow pill
            val isPos = venture.weeklyNetProfit >= 0
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPos) VentureGreen.copy(alpha = 0.15f) else VentureRed.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "${if (isPos) "+" else ""}$${String.format("%,.0f", venture.weeklyNetProfit)}/wk",
                    color = if (isPos) VentureGreen else VentureRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Dedicated business operations body
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                EnterpriseCard(
                    venture = venture,
                    playerCash = state.cash,
                    viewModel = viewModel,
                    onUpgrade = { viewModel.upgradeBusinessVenture(venture.id) },
                    onAcceptOffer = { viewModel.acceptVentureInvestorOffer(venture.id) },
                    onDeclineOffer = { viewModel.declineVentureInvestorOffer(venture.id) },
                    onOpenIpoDialog = onOpenIpoDialog,
                    onOpenManageFunds = onOpenManageFunds
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun EnterpriseCard(
    venture: BusinessVenture,
    playerCash: Double,
    viewModel: GameViewModel,
    onUpgrade: () -> Unit,
    onAcceptOffer: () -> Unit,
    onDeclineOffer: () -> Unit,
    onOpenIpoDialog: () -> Unit,
    onOpenManageFunds: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()
    var showEquityDetails by remember { mutableStateOf(false) }
    val upgradeInfo = VentureSimulationHelper.getUpgradeInfo(venture)
    val icon = when (venture.type) {
        "Taxi Fleet" -> Icons.Default.LocalTaxi
        "Restaurant" -> Icons.Default.Restaurant
        "Farming" -> Icons.Default.Agriculture
        "Hotel", "Hotel Chain" -> Icons.Default.Hotel
        "Crypto Token" -> Icons.Default.Token
        else -> Icons.Default.Business
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VentureCardBg)
            .border(1.dp, VentureCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header with Brand, Type, and Net Cashflow Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VenturePurple.copy(alpha = 0.2f))
                        .border(1.dp, VenturePurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(venture.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        if (venture.isListedOnWeShare) {
                            Surface(
                                color = Color(0xFF0F261B),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VentureGreen)
                            ) {
                                Text("IPO", color = VentureGreen, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                    }
                    Text(venture.tierName.ifBlank { venture.type }, color = Color(0xFF93C5FD), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Net Weekly Cashflow Badge
            Surface(
                color = if (venture.weeklyNetProfit >= 0) VentureGreen.copy(alpha = 0.15f) else VentureRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (venture.weeklyNetProfit >= 0) VentureGreen.copy(alpha = 0.5f) else VentureRed.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "${if (venture.weeklyNetProfit >= 0) "+" else ""}$${String.format("%,.0f", venture.weeklyNetProfit)}/wk",
                        color = if (venture.weeklyNetProfit >= 0) VentureGreen else VentureRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("NET CASHFLOW", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 1b. Majority Shareholder Autonomous Governance Banner (When Player Has Lost Control)
        if (!venture.isPlayerInControl) {
            val founderPct = (venture.founderEquityPercent * 100).toInt()
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF351212)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF87171)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                        Text("👔 MAJORITY SHAREHOLDER CONTROLLED ($founderPct% Passive Stake)", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Text(
                        "Controlled by majority shareholders. You cannot make operational changes (hiring/firing staff, upgrading facilities, marketing campaigns, or purchasing equipment). The majority owner and appointed management direct all operations. You collect your weekly dividend profit/loss and can sell your remaining shares at any time.",
                        color = Color(0xFFFCA5A5),
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                    if (venture.lastOwnerAction.isNotBlank()) {
                        Surface(
                            color = Color(0xFF200B0B),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Recent Owner Action: ${venture.lastOwnerAction}",
                                color = Color(0xFFFDE047),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 1c. Operational Diagnostics & Manager Recommendations Panel (Detailed Needs For Every Business)
        val diagnostics = remember(venture) { VentureDiagnosticsHelper.analyzeVenture(venture) }
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131826)),
            border = androidx.compose.foundation.BorderStroke(1.dp, when (diagnostics.overallHealth) {
                "CRITICAL_BOTTLENECK" -> Color(0xFFEF4444).copy(alpha = 0.6f)
                "NEEDS_ATTENTION" -> Color(0xFFF59E0B).copy(alpha = 0.6f)
                else -> Color(0xFF10B981).copy(alpha = 0.5f)
            }),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = when (diagnostics.overallHealth) {
                                "CRITICAL_BOTTLENECK" -> Icons.Default.Warning
                                "NEEDS_ATTENTION" -> Icons.Default.Info
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when (diagnostics.overallHealth) {
                                "CRITICAL_BOTTLENECK" -> Color(0xFFEF4444)
                                "NEEDS_ATTENTION" -> Color(0xFFF59E0B)
                                else -> Color(0xFF10B981)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Text("📋 OPERATIONAL DIAGNOSTIC", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Text(
                        "${diagnostics.managerName} (${diagnostics.managerRole.take(18)}...)",
                        color = Color(0xFF94A3B8),
                        fontSize = 9.sp
                    )
                }

                if (diagnostics.bottlenecks.isNotEmpty()) {
                    diagnostics.bottlenecks.forEach { b ->
                        Surface(
                            color = when (b.severity) {
                                "CRITICAL" -> Color(0xFF3B1212)
                                "WARNING" -> Color(0xFF331E05)
                                "OPPORTUNITY" -> Color(0xFF0F2D1F)
                                else -> Color(0xFF172033)
                            },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, when (b.severity) {
                                "CRITICAL" -> Color(0xFFEF4444).copy(alpha = 0.4f)
                                "WARNING" -> Color(0xFFF59E0B).copy(alpha = 0.4f)
                                "OPPORTUNITY" -> Color(0xFF10B981).copy(alpha = 0.4f)
                                else -> Color(0xFF38BDF8).copy(alpha = 0.3f)
                            }),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(b.title, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(b.description, color = Color(0xFFCBD5E1), fontSize = 9.sp, lineHeight = 12.sp)
                                Text("💡 Action: ${b.actionRequired}", color = Color(0xFFFDE047), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Text("✓ All operational metrics, staffing ratios, and premises capacity are currently well balanced.", color = Color(0xFF86EFAC), fontSize = 10.sp)
                }
            }
        }

        // 2. Compact 3-Column Financial Snapshot
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0F1322))
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("GROSS INFLOW", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("+$${String.format("%,.0f", venture.weeklyGrossRevenue)}", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column {
                Text("OP. EXPENSES", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("-$${String.format("%,.0f", venture.weeklyOperatingExpenses)}", color = VentureRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("TREASURY CAPITAL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("$${String.format("%,.0f", venture.investedCapital)}", color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // 2a. Enterprise Valuation & Commercial Debt Status
        val grossVal = VentureSimulationHelper.getGrossEnterpriseValuation(venture)
        val netVal = VentureSimulationHelper.getEnterpriseValuation(venture)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("ENTERPRISE VALUATION", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Text("$${String.format("%,.0f", netVal)}", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
                if (venture.outstandingLoan > 0.0) {
                    Text(
                        "📉 Undervalued by $${String.format("%,.0f", venture.outstandingLoan)} due to outstanding Apex Bank loan (Gross: $${String.format("%,.0f", grossVal)}). Repaying the commercial loan will immediately restore enterprise valuation!",
                        color = Color(0xFFFBBF24),
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }
            }
        }

        // 2b. Commercial Licensing & Regulatory Compliance
        if (venture.isShutDownByRegulators) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3F0B0B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, VentureRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = VentureRed)
                    Text("⛔ SHUT DOWN BY STATE COMMERCE COMMISSION. Forfeited for unfulfilled licensing.", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        } else if (venture.sccWarningReceived) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF371B04)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEA580C)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🚨 SCC CEASE & DESIST NOTICE: ${venture.sccDeadlineWeeks} weeks remaining to acquire Commercial Operating License before permanent government shutdown!", color = Color(0xFFFB923C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { viewModel.acquireVentureLegalLicense(venture.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(32.dp)
                    ) {
                        Text("Acquire Commercial License ($${String.format("%,.0f", venture.legalLicenseCost)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (!venture.hasLegalLicense) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151A29)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3856)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("STATE COMMERCIAL LICENSE: UNREGISTERED", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("Required before listing shares on WeShare & avoids SCC shutdown as clientele expands.", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.acquireVentureLegalLicense(venture.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("License ($${String.format("%,.0f", venture.legalLicenseCost)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0D2318)),
                border = androidx.compose.foundation.BorderStroke(1.dp, VentureGreen.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = VentureGreen, modifier = Modifier.size(16.dp))
                    Text("State Commercial Operating & Enterprise License Active (SCC Compliant)", color = VentureGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2c. Shareholder Lawsuit Alert (if active)
        if (venture.investorLawsuitActive) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3F0B1A)),
                border = androidx.compose.foundation.BorderStroke(1.dp, VentureRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("⚖️ SHAREHOLDER LAWSUIT ACTIVE: Sued for misappropriation of $${String.format("%,.0f", venture.pocketedInvestorCapital)} corporate funds outside the business entity!", color = Color(0xFFFDA4AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { viewModel.settleInvestorLawsuit(venture.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = VentureRed),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(32.dp)
                    ) {
                        Text("Settle Lawsuit ($${String.format("%,.0f", venture.investorLawsuitDamages)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2d. Marketing Campaigns & Recurring Clientele Growth
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101929)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                        Text("MARKETING & RECURRING CLIENTELE", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("${String.format("%,d", venture.recurringCustomers)} Recurring Clients", color = VentureGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    "Running campaigns creates brand exposure and turns one-time customers into recurring clients who repeatedly book/purchase and refer new business.",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
                if (venture.isPlayerInControl) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val economyCycle = state?.economyCycle ?: "STABLE"
                        val taxModifier = state?.politicalTaxModifier ?: 0f
                        val localCost = VentureSimulationHelper.getMarketingCampaignCost(venture.type, "LOCAL_ADS", economyCycle, taxModifier)
                        val digitalCost = VentureSimulationHelper.getMarketingCampaignCost(venture.type, "DIGITAL_ADS", economyCycle, taxModifier)
                        val prCost = VentureSimulationHelper.getMarketingCampaignCost(venture.type, "PR_BLITZ", economyCycle, taxModifier)

                        val campaigns = listOf(
                            Triple("NONE", "None", "$0/wk"),
                            Triple("LOCAL_ADS", "Local Ads", "$${localCost.toInt()}/wk"),
                            Triple("DIGITAL_ADS", "Digital Ads", "$${digitalCost.toInt()}/wk"),
                            Triple("PR_BLITZ", "PR Blitz", if (prCost >= 1000.0) "$${String.format("%.1fk", prCost / 1000.0)}/wk" else "$${prCost.toInt()}/wk")
                        )
                        campaigns.forEach { (key, label, cost) ->
                            val isSelected = venture.marketingCampaign == key
                            Button(
                                onClick = { viewModel.setVentureMarketingCampaign(venture.id, key) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).height(32.dp),
                                contentPadding = PaddingValues(horizontal = 2.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color(0xFFCBD5E1))
                                    Text(cost, fontSize = 7.sp, color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF64748B))
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        color = Color(0xFF1E293B).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                            Text(
                                "Marketing Strategy: ${if (venture.marketingCampaign == "NONE") "No Active Campaign" else venture.marketingCampaign} • Managed autonomously by majority shareholder",
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 2e. Realistic Industry Operations Panel (Controlled vs Non-Controlled)
        if (venture.isPlayerInControl) {
            if (venture.type == "Taxi Fleet") {
                TaxiFleetOperationsPanel(venture = venture, playerCash = playerCash, viewModel = viewModel)
            } else if (venture.type == "Restaurant") {
                RestaurantOperationsPanel(venture = venture, playerCash = playerCash, viewModel = viewModel)
            } else if (venture.type == "Farming") {
                FarmingOperationsPanel(venture = venture, playerCash = playerCash, viewModel = viewModel)
            } else if (venture.type == "Hotel" || venture.type == "Hotel Chain") {
                HotelOperationsPanel(venture = venture, playerCash = playerCash, viewModel = viewModel)
            } else if (venture.type == "Crypto Token") {
                CryptoTokenOperationsPanel(venture = venture, playerCash = playerCash, viewModel = viewModel)
            }
        } else {
            NonControllingOperationsSummary(venture = venture)
        }

        // 3. Inbound Investor Deal Banner (if pending)
        venture.pendingInvestorOffer?.let { offer ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261D0C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, VentureGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
                            Text("INBOUND INVESTOR OFFER", color = VentureGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                        Surface(
                            color = if (offer.isTrickyOffer) Color(0xFF3E1F0A) else Color(0xFF0F2D1F),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (offer.isTrickyOffer) Color(0xFFF97316) else VentureGreen)
                        ) {
                            Text(
                                text = if (offer.isTrickyOffer) "Tricky Deal (-${(100 - offer.valuationMultiplePercent).toInt()}%)" else "Premium Valuation (+${(offer.valuationMultiplePercent - 100).toInt()}%)",
                                color = if (offer.isTrickyOffer) Color(0xFFFB923C) else VentureGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = if (offer.offerType == "BUYOUT") "$${String.format("%,.0f", offer.cashAmount)} Buyout" else "$${String.format("%,.0f", offer.cashAmount)} for ${(offer.equityPercent * 100).toInt()}% Stake",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (offer.fairValue > 0.0) {
                            Text(
                                "Fair Valuation: $${String.format("%,.0f", offer.fairValue)}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(offer.pitchMessage, color = Color(0xFFCBD5E1), fontSize = 11.sp, lineHeight = 15.sp)

                    Surface(
                        color = Color(0xFF1B1914),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "💡 Corporate Equity Rule: Capital goes directly into ${venture.name}'s Treasury to fund growth needs (marketing, licensing, infrastructure). Diverting it to personal spending without business reinvestment can trigger shareholder lawsuits if the business declines.",
                            color = Color(0xFFD4D4D8),
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAcceptOffer,
                            colors = ButtonDefaults.buttonColors(containerColor = VentureGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text(if (offer.offerType == "BUYOUT") "Accept Buyout" else "Accept Growth Capital", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = onDeclineOffer,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text("Decline", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 4. Upgrade Enterprise Facility Action (Single Clean Component - In-Control Only)
        if (venture.isPlayerInControl && upgradeInfo != null) {
            if (!upgradeInfo.isMaxed) {
                val canAfford = playerCash >= upgradeInfo.cost
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1530)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VenturePurple.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("NEXT SCALE UPGRADE", color = VenturePurple, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text(upgradeInfo.title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                "+$${String.format("%,.0f", upgradeInfo.nextNetProfit)}/wk cashflow",
                                color = VentureGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(upgradeInfo.description, color = TextMuted, fontSize = 11.sp, lineHeight = 14.sp)

                        Button(
                            onClick = onUpgrade,
                            enabled = canAfford,
                            colors = ButtonDefaults.buttonColors(containerColor = VenturePurple),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(38.dp)
                        ) {
                            Icon(Icons.Default.ArrowCircleUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (canAfford) "Upgrade Facility ($${String.format("%,.0f", upgradeInfo.cost)})" else "Insufficient Funds ($${String.format("%,.0f", upgradeInfo.cost)} needed)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                Surface(
                    color = Color(0xFF161E14),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VentureGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
                        Text("Maximum Operational Scale Achieved", color = VentureGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 5. Expandable Equity Details (Keeps Card Compact & Uncluttered)
        val founderEquity = venture.founderEquityPercent
        val totalInvestorEquity = venture.totalInvestorEquityPercent
        val inControl = venture.isPlayerInControl
        var showSellConfirmDialog by remember { mutableStateOf(false) }

        if (showSellConfirmDialog) {
            val grossVal = VentureSimulationHelper.getGrossEnterpriseValuation(venture)
            val playerPayout = grossVal * founderEquity
            val loanPayoff = minOf(venture.outstandingLoan, playerPayout)
            val netProceeds = (playerPayout - loanPayoff).coerceAtLeast(0.0)

            AlertDialog(
                onDismissRequest = { showSellConfirmDialog = false },
                containerColor = Color(0xFF1E1C29),
                title = {
                    Text("Sell ${venture.name}?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Are you sure you want to sell your stake in this venture? You will receive proceeds based on the percentage of equity you hold.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Surface(color = Color(0xFF141220), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Gross Enterprise Value:", color = Color.Gray, fontSize = 11.sp)
                                    Text("$${String.format("%,.0f", grossVal)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Your Equity Percentage:", color = Color.Gray, fontSize = 11.sp)
                                    Text("${(founderEquity * 100).toInt()}%", color = VenturePurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (venture.outstandingLoan > 0.0) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Commercial Loan Settled:", color = Color(0xFFF87171), fontSize = 11.sp)
                                        Text("-$${String.format("%,.0f", loanPayoff)}", color = Color(0xFFF87171), fontSize = 11.sp)
                                    }
                                }
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 2.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Net Payout to Cash:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("+$${String.format("%,.0f", netProceeds)}", color = Color(0xFF34D399), fontSize = 13.sp, fontWeight = FontWeight.Black)
                                }
                                if (inControl) {
                                    Text("✓ 10 Time Slots returned to your schedule", color = Color(0xFF60A5FA), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSellConfirmDialog = false
                            viewModel.sellVenture(venture.id)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Confirm Sell", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showSellConfirmDialog = false },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showEquityDetails = !showEquityDetails }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Founder: ${(founderEquity * 100).toInt()}% • Investors: ${(totalInvestorEquity * 100).toInt()}%",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                if (!inControl) {
                    Surface(color = Color(0xFFEF4444).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("Passive Stake", color = Color(0xFFF87171), fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (showEquityDetails) "Hide" else "Details", color = Color(0xFF93C5FD), fontSize = 11.sp)
                Icon(
                    if (showEquityDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF93C5FD),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (showEquityDetails) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1322))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Colored equity bar segments
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF262C3D))
                ) {
                    if (founderEquity > 0.0) {
                        Box(modifier = Modifier.weight(founderEquity.toFloat().coerceAtLeast(0.005f)).fillMaxHeight().background(VenturePurple))
                    }
                    if (venture.investors.isNotEmpty()) {
                        venture.investors.forEach { inv ->
                            val color = try { Color(android.graphics.Color.parseColor(inv.colorHex)) } catch (e: Exception) { VentureGold }
                            Box(modifier = Modifier.weight(inv.equityPercent.toFloat().coerceAtLeast(0.005f)).fillMaxHeight().background(color))
                        }
                    } else if (venture.investorEquityPercent > 0.0) {
                        Box(modifier = Modifier.weight(venture.investorEquityPercent.toFloat().coerceAtLeast(0.005f)).fillMaxHeight().background(VentureGold))
                    }
                    if (venture.isListedOnWeShare && venture.weShareListedEquityPercent > 0.0) {
                        Box(modifier = Modifier.weight(venture.weShareListedEquityPercent.toFloat().coerceAtLeast(0.005f)).fillMaxHeight().background(VentureGreen))
                    }
                }

                // Color dot indicators (avoids name overlap!)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(VenturePurple))
                        Text("You: ${(founderEquity * 100).toInt()}%", color = VenturePurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    if (venture.investors.isNotEmpty()) {
                        venture.investors.forEachIndexed { idx, inv ->
                            val color = try { Color(android.graphics.Color.parseColor(inv.colorHex)) } catch (e: Exception) { VentureGold }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(color))
                                Text("Inv ${idx + 1}: ${(inv.equityPercent * 100).toInt()}%", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (venture.investorEquityPercent > 0.0) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(VentureGold))
                            Text("Investor: ${(venture.investorEquityPercent * 100).toInt()}%", color = VentureGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (venture.isListedOnWeShare) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(VentureGreen))
                            Text("Float: ${(venture.weShareListedEquityPercent * 100).toInt()}%", color = VentureGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (!inControl) {
                    Text(
                        "ℹ Non-controlling interest: Lead investors direct operations. You earn passive weekly profits on your ${(founderEquity * 100).toInt()}% stake, and this business requires 0 of your weekly time slots.",
                        color = Color(0xFFFBBF24),
                        fontSize = 9.sp,
                        lineHeight = 12.sp
                    )
                }
            }
        }

        // 6. Action Controls: Manage Funds & WeShare IPO & Sell Business Stake
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onOpenManageFunds,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, VentureCardBorder),
                modifier = Modifier.weight(1f).height(38.dp)
            ) {
                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Funds", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }

            if (!venture.isListedOnWeShare) {
                Button(
                    onClick = onOpenIpoDialog,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981).copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VentureGreen),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = VentureGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("IPO", color = VentureGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Surface(
                    color = Color(0xFF0F261B),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VentureGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Listed ✓", color = VentureGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedButton(
                onClick = { showSellConfirmDialog = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f)),
                modifier = Modifier.weight(1f).height(38.dp)
            ) {
                Icon(Icons.Default.Sell, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sell", color = Color(0xFFF87171), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NonControllingOperationsSummary(venture: BusinessVenture) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161A26)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    Text("OPERATIONS DIRECTED BY MAJORITY OWNER", color = Color(0xFFE2E8F0), fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("READ-ONLY", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Text(
                "Because you hold a minority non-controlling stake (${(venture.founderEquityPercent * 100).toInt()}%), all staffing, equipment maintenance, and facility scaling are executed by the controlling shareholder. You receive your weekly profit/loss distribution and can sell your shares at any time.",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                lineHeight = 13.sp
            )

            Surface(
                color = Color(0xFF0F131D),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    when (venture.type) {
                        "Taxi Fleet" -> {
                            Text("🚗 Active Taxi Fleet: ${venture.taxiCarCount} Vehicles (${venture.taxiDriverCount} Drivers, ${venture.taxiMechanicCount} Mechanics)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("🔧 Fleet Condition: ${(venture.taxiCondition * 100).toInt()}% • Weekly Trips: ${venture.taxiWeeklyTripsCompleted}", color = Color(0xFF93C5FD), fontSize = 10.sp)
                        }
                        "Restaurant" -> {
                            Text("🍽️ Kitchen & Floor Staff: ${venture.chefCount} Chefs, ${venture.waiterCount} Waiters, ${venture.cleanerCount} Cleaners", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("👥 Recurring Diners: ${String.format("%,d", venture.recurringCustomers)} • Weekly Served: ${String.format("%,d", venture.weeklyCustomersServed)}", color = Color(0xFF93C5FD), fontSize = 10.sp)
                        }
                        "Farming" -> {
                            Text("🚜 Agri-Farm Acreage: ${venture.farmLandAcres.toInt()} Acres (${if (venture.farmLandIsOwned) "Owned" else "Leased"})", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("🌾 Planted Batches: ${venture.farmCropBatches.size} • Farm Workforce: ${venture.farmHandCount} Farmhands", color = Color(0xFF93C5FD), fontSize = 10.sp)
                        }
                        "Hotel", "Hotel Chain" -> {
                            Text("🏨 Hospitality Suites: ${venture.hotelRooms.count { it.isUnlocked }} Rooms (${(venture.hotelOccupancyRate * 100).toInt()}% Occupancy)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("🛎️ Service Staff: ${venture.hotelHousekeeperCount} Housekeepers, ${venture.hotelFrontDeskCount} Front Desk", color = Color(0xFF93C5FD), fontSize = 10.sp)
                        }
                        "Crypto Token" -> {
                            Text("🪙 Smart Protocol: ${venture.cryptoActiveUsers} Active Wallets • TVL: $${String.format("%,.0f", venture.cryptoLiquidityPoolUsd)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("💻 Dev Core: ${venture.cryptoSolidityDevCount} Engineers • Security Tier ${venture.cryptoSecurityTier}", color = Color(0xFF93C5FD), fontSize = 10.sp)
                        }
                        else -> {
                            Text("🏢 Enterprise Scale: ${venture.tierName} • Active Recurring Clients: ${venture.recurringCustomers}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxiFleetOperationsPanel(
    venture: BusinessVenture,
    playerCash: Double,
    viewModel: GameViewModel
) {
    val serviceCost = venture.taxiCarCount.coerceAtLeast(1) * VentureSimulationHelper.TAXI_SERVICE_BASE_COST
    val canService = playerCash >= serviceCost && venture.taxiCondition < 0.99f
    val maxTaxiCapacity = venture.taxiDepotCapacity.takeIf { it > 0 } ?: 5
    val isDepotFull = venture.taxiCarCount >= maxTaxiCapacity
    val canBuyCar = playerCash >= VentureSimulationHelper.TAXI_CAR_COST && !isDepotFull
    val conditionColor = when {
        venture.taxiCondition >= 0.70f -> VentureGreen
        venture.taxiCondition >= 0.40f -> VentureGold
        else -> VentureRed
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131724))
            .border(1.dp, Color(0xFF262E45), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.LocalTaxi, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
                Text("FLEET OPERATIONS & REALISM", color = VentureGold, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
            }
            Surface(
                color = conditionColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, conditionColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "${(venture.taxiCondition * 100).toInt()}% Health",
                    color = conditionColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Fleet Metrics Grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("VEHICLES", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${venture.taxiCarCount} Cabs", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("Lot: $${String.format("%,.0f", venture.taxiCarCount * 230.0)}/mo", color = Color(0xFF93C5FD), fontSize = 9.sp)
            }
            Column {
                Text("DRIVERS ON PAYROLL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${venture.taxiDriverCount} Drivers", color = if (venture.taxiDriverCount >= venture.taxiCarCount) VentureGreen else VentureGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("ODOMETER", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${String.format("%,.0f", venture.taxiOdometerMiles)} mi", color = Color(0xFF93C5FD), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Mechanical condition progress bar
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fleet Mechanical Condition", color = TextMuted, fontSize = 10.sp)
                Text(
                    if (venture.taxiCondition < 0.35f) "CRITICAL WEAR (Fares Dropping!)" else if (venture.taxiCondition < 0.70f) "Moderate Wear" else "Optimal Running Order",
                    color = conditionColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { venture.taxiCondition.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = conditionColor,
                trackColor = Color(0xFF1E2433)
            )
        }

        // Realistic weekly expenses breakdown
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0B0E17))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("LAST WEEK'S OPERATIONAL DATA", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fares (${venture.taxiWeeklyTripsCompleted} trips):", color = TextWhite, fontSize = 11.sp)
                Text("+$${String.format("%,.0f", venture.taxiWeeklyFaresEarned)}", color = VentureGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Driver Payroll ($${String.format("%,.0f", VentureSimulationHelper.TAXI_DRIVER_SALARY)}/wk × ${venture.taxiDriverCount}):", color = TextMuted, fontSize = 11.sp)
                Text("-$${String.format("%,.0f", venture.taxiWeeklyDriverSalary)}", color = VentureRed, fontSize = 11.sp)
            }
            if (venture.taxiMechanicCount > 0 || venture.taxiWeeklyMechanicSalary > 0.0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Mechanic Payroll ($${String.format("%,.0f", VentureSimulationHelper.TAXI_MECHANIC_SALARY)}/wk × ${venture.taxiMechanicCount}):", color = TextMuted, fontSize = 11.sp)
                    Text("-$${String.format("%,.0f", venture.taxiWeeklyMechanicSalary.takeIf { it > 0.0 } ?: (venture.taxiMechanicCount * VentureSimulationHelper.TAXI_MECHANIC_SALARY))}", color = VentureRed, fontSize = 11.sp)
                }
            }
            if (venture.taxiDispatcherCount > 0 || venture.taxiWeeklyDispatcherSalary > 0.0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Dispatcher Payroll ($${String.format("%,.0f", VentureSimulationHelper.TAXI_DISPATCHER_SALARY)}/wk × ${venture.taxiDispatcherCount}):", color = TextMuted, fontSize = 11.sp)
                    Text("-$${String.format("%,.0f", venture.taxiWeeklyDispatcherSalary.takeIf { it > 0.0 } ?: (venture.taxiDispatcherCount * VentureSimulationHelper.TAXI_DISPATCHER_SALARY))}", color = VentureRed, fontSize = 11.sp)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Fuel Consumption (State Gas Price):", color = TextMuted, fontSize = 11.sp)
                Text("-$${String.format("%,.0f", venture.taxiWeeklyFuelCost)}", color = VentureRed, fontSize = 11.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Routine Wear & Upkeep:", color = TextMuted, fontSize = 11.sp)
                Text("-$${String.format("%,.0f", venture.taxiWeeklyMaintenanceCost)}", color = VentureRed, fontSize = 11.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Parking Lot Space Maint & Municipal Tax:", color = TextMuted, fontSize = 11.sp)
                val spaceCosts = (venture.taxiWeeklyParkingSpaceMaint + venture.taxiWeeklyParkingSpaceTax).takeIf { it > 0.0 } ?: (venture.taxiCarCount * 53.10)
                Text("-$${String.format("%,.0f", spaceCosts)}", color = VentureRed, fontSize = 11.sp)
            }
        }

        // Staff Specialization Roles (Drivers, Mechanics, Dispatchers)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F1420))
                .border(1.dp, Color(0xFF26324D), RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("STAFF SPECIALIZATION & ROLES", color = Color(0xFF93C5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)

            // Drivers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Taxi Drivers (${venture.taxiDriverCount} on staff)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Operates cabs on the street ($500/wk/driver)", color = TextMuted, fontSize = 9.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { viewModel.hireTaxiDriver(venture.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("+Hire", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.fireTaxiDriver(venture.id) },
                        enabled = venture.taxiDriverCount > 0,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("-Fire", fontSize = 10.sp)
                    }
                }
            }

            // Mechanics
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fleet Mechanics (${venture.taxiMechanicCount} on staff)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Prevents vehicle wear & repairs engine damage ($550/wk)", color = Color(0xFFCBD5E1), fontSize = 9.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { viewModel.hireTaxiMechanic(venture.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("+Hire", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.fireTaxiMechanic(venture.id) },
                        enabled = venture.taxiMechanicCount > 0,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("-Fire", fontSize = 10.sp)
                    }
                }
            }

            // Dispatchers
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Radio Dispatchers (${venture.taxiDispatcherCount} on staff)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Optimizes ride routing & drives recurring client bookings ($450/wk)", color = Color(0xFFCBD5E1), fontSize = 9.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = { viewModel.hireTaxiDispatcher(venture.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("+Hire", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { viewModel.fireTaxiDispatcher(venture.id) },
                        enabled = venture.taxiDispatcherCount > 0,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(28.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("-Fire", fontSize = 10.sp)
                    }
                }
            }
        }

        // Fleet Assets & Service Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.buyTaxiCar(venture.id) },
                enabled = canBuyCar,
                colors = ButtonDefaults.buttonColors(containerColor = VenturePurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(34.dp)
            ) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (isDepotFull) "Depot Full ($maxTaxiCapacity/$maxTaxiCapacity max)" else "+1 Car ($${formatCompactMoney(VentureSimulationHelper.TAXI_CAR_COST)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.serviceTaxiFleet(venture.id) },
                enabled = canService,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(34.dp)
            ) {
                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Service Fleet ($${formatCompactMoney(serviceCost)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RestaurantOperationsPanel(
    venture: BusinessVenture,
    playerCash: Double,
    viewModel: GameViewModel
) {
    var showStaffManagement by remember { mutableStateOf(false) }
    var showEquipmentShop by remember { mutableStateOf(false) }

    val tierDetail = VentureSimulationHelper.RESTAURANT_TIER_DETAILS.find { it.tierLevel == venture.tierLevel } ?: VentureSimulationHelper.RESTAURANT_TIER_DETAILS.first()
    val equipmentList = if (venture.equipmentList.isEmpty()) VentureSimulationHelper.getDefaultRestaurantEquipment() else venture.equipmentList

    val effectiveChefs = venture.chefCount * 1.0 + venture.seniorChefCount * 1.5
    val effectiveWaiters = venture.waiterCount * 1.0 + venture.managerCount * 0.75
    val isUnderstaffed = effectiveChefs < tierDetail.minChefs || effectiveWaiters < tierDetail.minWaiters

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131724))
            .border(1.dp, Color(0xFF262E45), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = Color(0xFFFF8A65), modifier = Modifier.size(16.dp))
                Text("KITCHEN & DINING OPERATIONS", color = Color(0xFFFF8A65), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
            }
            Surface(
                color = if (!isUnderstaffed) VentureGreen.copy(alpha = 0.15f) else VentureRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (!isUnderstaffed) VentureGreen.copy(alpha = 0.5f) else VentureRed.copy(alpha = 0.5f))
            ) {
                Text(
                    text = if (isUnderstaffed) "Understaffed!" else "Staffing Adequate",
                    color = if (!isUnderstaffed) VentureGreen else VentureRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Restaurant metrics summary
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("DAILY CAPACITY", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${tierDetail.minDailyCapacity}–${tierDetail.maxDailyCapacity} guests", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column {
                Text("PATRONS LAST WK", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${venture.weeklyCustomersServed} served", color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("EFFICIENCY", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text("${(venture.serviceEfficiency * 100).toInt()}%", color = if (venture.serviceEfficiency >= 0.90f) VentureGreen else VentureGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Realistic expenses box (30% ingredient rule, building maintenance %, payroll, equipment)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0B0E17))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("FINANCIAL REALISM BREAKDOWN", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Food & Beverage Sales:", color = TextWhite, fontSize = 11.sp)
                Text("+$${String.format("%,.0f", venture.weeklyFoodSales)}", color = VentureGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Ingredients (30% of sales):", color = TextMuted, fontSize = 11.sp)
                Text("-$${String.format("%,.0f", venture.weeklyIngredientCost)}", color = VentureRed, fontSize = 11.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Staff Payroll (${venture.waiterCount + venture.chefCount + venture.seniorChefCount + venture.cleanerCount + venture.managerCount} staff):", color = TextMuted, fontSize = 11.sp)
                Text("-$${String.format("%,.0f", venture.weeklyStaffPayroll)}", color = VentureRed, fontSize = 11.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Building Upkeep (${(tierDetail.buildingMaintenanceMonthlyPercent * 100 * 10).toInt() / 10.0}%/mo):", color = TextMuted, fontSize = 11.sp)
                Text("-$${String.format("%,.0f", venture.weeklyBuildingMaintenanceCost)}", color = VentureRed, fontSize = 11.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Appliance Upkeep & Maintenance:", color = TextMuted, fontSize = 11.sp)
                Text("-$${String.format("%,.0f", venture.weeklyEquipmentMaintenanceCost)}", color = VentureRed, fontSize = 11.sp)
            }
        }

        // Toggles for Staffing and Equipment
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { showStaffManagement = !showStaffManagement },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (showStaffManagement) Color(0xFF1E293B) else Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnderstaffed) VentureRed else Color(0xFF38BDF8)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(36.dp)
            ) {
                Icon(Icons.Default.People, contentDescription = null, tint = if (isUnderstaffed) VentureRed else Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showStaffManagement) "Close Staff" else "Staff (${tierDetail.minChefs}C/${tierDetail.minWaiters}W req)", fontSize = 10.sp, color = TextWhite)
            }

            OutlinedButton(
                onClick = { showEquipmentShop = !showEquipmentShop },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (showEquipmentShop) Color(0xFF1E293B) else Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, VenturePurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(36.dp)
            ) {
                Icon(Icons.Default.Kitchen, contentDescription = null, tint = VenturePurple, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (showEquipmentShop) "Close Equip" else "Appliances (${equipmentList.count { it.isOwned }}/${equipmentList.size})", fontSize = 10.sp, color = TextWhite)
            }
        }

        // Staffing Management Panel
        if (showStaffManagement) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1420))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "STAFF ROSTER (Expected: ${tierDetail.minChefs} chefs, ${tierDetail.minWaiters} waiters)",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                RestaurantStaffRow("Waiter", "$300/wk", venture.waiterCount, { viewModel.hireRestaurantStaff(venture.id, "Waiter") }, { viewModel.fireRestaurantStaff(venture.id, "Waiter") })
                RestaurantStaffRow("Chef", "$600/wk", venture.chefCount, { viewModel.hireRestaurantStaff(venture.id, "Chef") }, { viewModel.fireRestaurantStaff(venture.id, "Chef") })
                RestaurantStaffRow("Senior Chef", "$850/wk", venture.seniorChefCount, { viewModel.hireRestaurantStaff(venture.id, "Senior Chef") }, { viewModel.fireRestaurantStaff(venture.id, "Senior Chef") })
                RestaurantStaffRow("Cleaner", "$250/wk", venture.cleanerCount, { viewModel.hireRestaurantStaff(venture.id, "Cleaner") }, { viewModel.fireRestaurantStaff(venture.id, "Cleaner") })
                RestaurantStaffRow("Manager", "$750/wk", venture.managerCount, { viewModel.hireRestaurantStaff(venture.id, "Manager") }, { viewModel.fireRestaurantStaff(venture.id, "Manager") })
            }
        }

        // Equipment Panel
        if (showEquipmentShop) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F1420))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("APPLIANCES & COMMERCIAL EQUIPMENT", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)

                equipmentList.forEach { eq ->
                    RestaurantEquipmentRow(
                        eq = eq,
                        currentTier = venture.tierLevel,
                        playerCash = playerCash,
                        onBuy = { viewModel.buyRestaurantEquipment(venture.id, eq.id) },
                        onService = { viewModel.serviceRestaurantEquipment(venture.id, eq.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantStaffRow(
    title: String,
    salary: String,
    count: Int,
    onHire: () -> Unit,
    onFire: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(salary, color = TextMuted, fontSize = 9.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledIconButton(
                onClick = onFire,
                enabled = count > 0,
                modifier = Modifier.size(26.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF334155))
            ) {
                Text("-", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Text("$count", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            FilledIconButton(
                onClick = onHire,
                modifier = Modifier.size(26.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = VenturePurple)
            ) {
                Text("+", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RestaurantEquipmentRow(
    eq: RestaurantEquipment,
    currentTier: Int,
    playerCash: Double,
    onBuy: () -> Unit,
    onService: () -> Unit
) {
    val isLocked = currentTier < eq.unlockTier
    val serviceCost = (eq.price * 0.15).coerceAtLeast(50.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF161B29))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(eq.name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                if (eq.isOwned) {
                    val condColor = if (eq.condition > 0.65f) VentureGreen else if (eq.condition > 0.35f) VentureGold else VentureRed
                    Text("${(eq.condition * 100).toInt()}% Cond", color = condColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                if (eq.isOwned) "Upkeep: $${String.format("%,.0f", eq.weeklyMaintenance)}/wk"
                else if (isLocked) "Unlocks at Tier ${eq.unlockTier}"
                else "$${String.format("%,.0f", eq.price)} (Upkeep: $${String.format("%,.0f", eq.weeklyMaintenance)}/wk)",
                color = TextMuted,
                fontSize = 9.sp
            )
        }

        if (!eq.isOwned) {
            Button(
                onClick = onBuy,
                enabled = !isLocked && playerCash >= eq.price,
                colors = ButtonDefaults.buttonColors(containerColor = VentureGreen),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(28.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(if (isLocked) "Locked" else "Buy $${String.format("%,.0f", eq.price)}", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        } else {
            Button(
                onClick = onService,
                enabled = eq.condition < 0.98f && playerCash >= serviceCost,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(28.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(if (eq.condition >= 0.98f) "Tuned ✓" else "Service $${String.format("%,.0f", serviceCost)}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FarmingOperationsPanel(
    venture: BusinessVenture,
    playerCash: Double,
    viewModel: GameViewModel
) {
    var farmingSubTab by remember { mutableStateOf("Overview") }
    val currentTier = venture.tierLevel
    val tierInfo = VentureSimulationHelper.FARMING_TIERS.find { it.tierLevel == currentTier } ?: VentureSimulationHelper.FARMING_TIERS.first()

    val totalPlantedAcres = venture.farmCropBatches.sumOf { it.acres }
    val freeAcres = (venture.farmLandAcres - totalPlantedAcres).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131724))
            .border(1.dp, Color(0xFF262E45), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Farm Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
                Text("AGRI-FARM REALISM & ECOSYSTEM", color = Color(0xFF4ADE80), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
            }
            Surface(
                color = Color(0xFF4ADE80).copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.5f))
            ) {
                Text(
                    text = tierInfo.tierName,
                    color = Color(0xFF4ADE80),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Seasonal Weather & Growth Stage Banner
        val hasPlantedCrops = venture.farmCropBatches.any { it.currentWeek < it.totalWeeks }
        val hasHarvestReady = venture.farmCropBatches.any { it.isReadyToHarvest || it.currentWeek >= it.totalWeeks } || venture.farmInventory.any { it.quantity > 0 }
        val seasonTitle = when {
            hasPlantedCrops && hasHarvestReady -> "PLANT & HARVEST SEASON"
            hasHarvestReady -> "HARVEST SEASON"
            hasPlantedCrops -> "PLANT SEASON"
            else -> "PLANT SEASON (NEW OPERATIONS)"
        }
        val seasonDesc = when {
            hasPlantedCrops && hasHarvestReady -> "Crops growing in fields & harvested silo inventory ready for market sale or processing."
            hasHarvestReady -> "Crops are 100% mature and silo stock is ready for market sale or industrial processing."
            hasPlantedCrops -> "Crops are actively growing in the ground. No immediate sales until harvest maturity."
            else -> "Land prepped. Start in plant season by sowing seeds in the Crops tab to begin your first growth cycle!"
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.WbSunny, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(seasonTitle, color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Text("•", color = TextMuted, fontSize = 8.sp)
                    Text(venture.farmLastWeatherEvent, color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Medium)
                }
                Text(seasonDesc, color = TextMuted, fontSize = 8.5.sp)
            }
        }

        // 🌾 Simplified 3-Step Farming Quickstart Guide
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(15.dp))
                    Text("🌾 FARMING SIMPLIFIED: 3-STEP PROFIT GUIDE", color = Color(0xFF34D399), fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
                Text("1. Land & Seeds: Go to 'Crops' tab and plant seeds on your open acres (begins in Plant Season).", color = Color(0xFFD1FAE5), fontSize = 9.sp)
                Text("2. Staff & Care: Hire Farmhands in 'Staff' tab to clean land, and Tractor Operators to operate machines.", color = Color(0xFFD1FAE5), fontSize = 9.sp)
                Text("3. Harvest & Profit: When crops reach 100% maturity, harvest them into Silo to sell or process into value-added goods!", color = Color(0xFFD1FAE5), fontSize = 9.sp)

                val readyCount = venture.farmCropBatches.count { it.isReadyToHarvest || it.currentWeek >= it.totalWeeks }
                val currentAction = when {
                    readyCount > 0 -> "🌟 ACTION NOW: $readyCount crop batch(es) are 100% MATURE! Open 'Crops' tab and tap 'Harvest All' to collect into Silo!"
                    venture.farmCropBatches.isEmpty() -> "👉 ACTION NOW: No crops planted! Open 'Crops' tab and plant seeds on your ${freeAcres.toInt()} free acres."
                    venture.farmHandCount == 0 -> "⚠️ ACTION NOW: You have 0 Farmhands! Open 'Staff' tab to hire farmhands to clean and maintain hygiene."
                    venture.farmCleanliness < 0.40f -> "🧹 ACTION NOW: Land cleanliness is low (${(venture.farmCleanliness * 100).toInt()}%). Tap 'Clean Farm' to protect yields."
                    else -> "🌱 STATUS: Crops are currently growing smoothly in plant season. Each week brings them closer to harvest!"
                }
                Surface(
                    color = Color(0xFF064E3B),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentAction,
                        color = Color(0xFFFDE047),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }

        // Sub-tabs row
        val farmSubTabs = if (currentTier >= 4) {
            listOf("Overview", "Land", "Crops", "Machines", "Livestock", "Processing", "Staff")
        } else {
            listOf("Overview", "Land", "Crops", "Machines", "Livestock", "Staff")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            farmSubTabs.forEach { tab ->
                val isSelected = farmingSubTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) Color(0xFF10B981) else Color(0xFF1A2234))
                        .clickable { farmingSubTab = tab }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tab,
                        color = if (isSelected) Color.Black else TextWhite,
                        fontSize = if (farmSubTabs.size > 6) 8.5.sp else 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        when (farmingSubTab) {
            "Overview" -> {
                // Overview metrics
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("TOTAL LAND", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("${venture.farmLandAcres.toInt()} Acres (${if (venture.farmLandIsOwned) "Owned" else "Leased"})", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column {
                        Text("LAND HYGIENE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val cleanVal = (venture.farmCleanliness * 100).toInt()
                        val cleanColor = if (cleanVal >= 75) VentureGreen else if (cleanVal >= 45) VentureGold else VentureRed
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("$cleanVal% Clean", color = cleanColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            if (cleanVal < 90) {
                                OutlinedButton(
                                    onClick = { viewModel.cleanFarm(venture.id) },
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.height(22.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.7f))
                                ) {
                                    Text("Clean ($250)", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("WORKFORCE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        val totalWorkers = venture.farmHandCount + venture.farmTractorOperatorCount + venture.farmLivestockWorkerCount + venture.farmDriverCount + venture.farmVeterinarianCount + venture.farmManagerCount
                        Text("$totalWorkers Staff", color = VentureGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // 4 Revenue Categories Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B0E17))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("REVENUE STREAMS (4 CATEGORIES)", color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1. Raw Crops (Grains & Produce):", color = TextWhite, fontSize = 10.sp)
                        Text("+$${String.format("%,.0f", venture.farmWeeklyRawCropsRevenue)}", color = VentureGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("2. Fruit Orchards (Mango, Citrus):", color = if (currentTier >= 2) TextWhite else TextMuted, fontSize = 10.sp)
                        Text(if (currentTier >= 2) "+$${String.format("%,.0f", venture.farmWeeklyFruitsRevenue)}" else "Unlocks Tier 2", color = if (currentTier >= 2) VentureGreen else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("3. Livestock (Dairy, Eggs, Meat):", color = if (currentTier >= 3) TextWhite else TextMuted, fontSize = 10.sp)
                        Text(if (currentTier >= 3) "+$${String.format("%,.0f", venture.farmWeeklyLivestockRevenue)}" else "Unlocks Tier 3", color = if (currentTier >= 3) VentureGreen else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("4. Agri-Industrial Processed Goods:", color = if (currentTier >= 4) TextWhite else TextMuted, fontSize = 10.sp)
                        Text(if (currentTier >= 4) "+$${String.format("%,.0f", venture.farmWeeklyProcessedRevenue)}" else "Unlocks Tier 4", color = if (currentTier >= 4) VentureGreen else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Operating Costs Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B0E17))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("OPERATING EXPENSES BREAKDOWN", color = Color(0xFFF87171), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Land Rent / Lease:", color = TextMuted, fontSize = 10.sp)
                        Text(if (venture.farmLandIsOwned) "$0 (Owned Land)" else "-$${String.format("%,.0f", venture.farmWeeklyLandRentExpense)}", color = if (venture.farmLandIsOwned) VentureGreen else VentureRed, fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Machinery Upkeep & Rentals:", color = TextMuted, fontSize = 10.sp)
                        Text("-$${String.format("%,.0f", venture.farmWeeklyMachineUpkeepExpense)}", color = VentureRed, fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Staff Payroll:", color = TextMuted, fontSize = 10.sp)
                        Text("-$${String.format("%,.0f", venture.farmWeeklyPayrollExpense)}", color = VentureRed, fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Livestock Feed & Veterinary:", color = TextMuted, fontSize = 10.sp)
                        Text("-$${String.format("%,.0f", venture.farmWeeklyFeedExpense)}", color = VentureRed, fontSize = 10.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Seed & Fertilizer Restocking:", color = TextMuted, fontSize = 10.sp)
                        Text("-$${String.format("%,.0f", venture.farmWeeklySeedExpense)}", color = VentureRed, fontSize = 10.sp)
                    }
                }
            }

            "Land" -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("MANAGE ACREAGE (RENT VS. BUY)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Renting requires minimal upfront capital with weekly rent. Buying land makes it a permanent, appreciating business asset with $0 rent!", color = TextMuted, fontSize = 10.sp)

                    VentureSimulationHelper.FARM_LAND_OPTIONS.forEach { opt ->
                        val isCurrent = venture.farmLandTier == opt.tierKey
                        val isOwned = isCurrent && venture.farmLandIsOwned
                        val isRented = isCurrent && !venture.farmLandIsOwned

                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isCurrent) Color(0xFF1E293B) else Color(0xFF0F1420)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isOwned) Color(0xFF10B981) else if (isRented) Color(0xFF38BDF8) else Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("${opt.name} (${opt.acres.toInt()} Acres)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    if (isOwned) {
                                        Text("Owned Asset ✓", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    } else if (isRented) {
                                        Text("Active Lease", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Rent: $${String.format("%,.0f", opt.monthlyRent)}/mo • Purchase: $${String.format("%,.0f", opt.purchasePrice)}", color = TextMuted, fontSize = 10.sp)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.acquireFarmLand(venture.id, opt.tierKey, isBuying = false) },
                                        enabled = !isRented,
                                        modifier = Modifier.weight(1f).height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                                    ) {
                                        Text(if (isRented) "Leased" else "Lease ($${String.format("%,.0f", opt.monthlyRent)}/mo)", fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.acquireFarmLand(venture.id, opt.tierKey, isBuying = true) },
                                        enabled = !isOwned && playerCash >= opt.purchasePrice,
                                        modifier = Modifier.weight(1f).height(28.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                    ) {
                                        Text(if (isOwned) "Owned" else "Buy ($${String.format("%,.0f", opt.purchasePrice)})", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Crops" -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val readyBatchesCount = venture.farmCropBatches.count { it.isReadyToHarvest || it.currentWeek >= it.totalWeeks }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACTIVE CROP PLANTINGS & HARVESTS", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        if (readyBatchesCount > 0) {
                            Button(
                                onClick = { viewModel.harvestAllFarmCrops(venture.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier.height(26.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Harvest All ($readyBatchesCount)", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (venture.farmCropBatches.isEmpty()) {
                        Text("No active crops planted. Plant seeds below to start a growth cycle!", color = TextMuted, fontSize = 10.sp)
                    } else {
                        venture.farmCropBatches.forEach { b ->
                            val progress = (b.currentWeek.toFloat() / b.totalWeeks).coerceIn(0f, 1f)
                            val isReady = b.currentWeek >= b.totalWeeks

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isReady) Color(0xFF10B981) else Color(0xFF334155)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${b.cropName} (${b.acres.toInt()} acres)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(if (isReady) "READY TO HARVEST! 🌾" else "Week ${b.currentWeek} of ${b.totalWeeks}", color = if (isReady) Color(0xFF10B981) else VentureGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                        color = if (isReady) Color(0xFF10B981) else VentureGold,
                                        trackColor = Color(0xFF1E293B)
                                    )
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Est. Yield: ${b.expectedYieldUnits.toInt()} ${b.yieldUnitName}", color = TextMuted, fontSize = 9.sp)
                                        if (isReady) {
                                            Button(
                                                onClick = { viewModel.harvestFarmCrop(venture.id, b.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                modifier = Modifier.height(26.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Text("Harvest into Silo", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("PLANT NEW CROPS (Free Acres: ${freeAcres.toInt()} ac)", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                    VentureSimulationHelper.CROPS_CATALOG.forEach { c ->
                        val plantAcres = 5.0.coerceAtMost(freeAcres)
                        val cost = c.seedCostPerAcre * plantAcres
                        val canPlant = freeAcres >= 1.0 && playerCash >= (c.seedCostPerAcre * 1.0)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F1420))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(c.name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Seeds: $${String.format("%,.0f", c.seedCostPerAcre)}/ac • Growth: ${c.growthWeeks} wks • Price: $${String.format("%.1f", c.marketPricePerUnit)}/${c.yieldUnitName}", color = TextMuted, fontSize = 9.sp)
                            }
                            Button(
                                onClick = { viewModel.plantFarmCrop(venture.id, c.name, plantAcres.coerceAtLeast(1.0)) },
                                enabled = canPlant,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("Plant ${plantAcres.toInt()} ac ($${String.format("%,.0f", cost)})", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Silo Storage & Sales
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("FARM SILO & COMMODITY INVENTORY", color = VentureGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    if (venture.farmInventory.isEmpty()) {
                        Text("Silo storage is empty. Harvest crops to sell at market or process!", color = TextMuted, fontSize = 9.sp)
                    } else {
                        venture.farmInventory.forEach { inv ->
                            val totalVal = inv.quantity * inv.marketPricePerUnit
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${inv.quantity.toInt()} ${inv.unit} of ${inv.name}", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("Market Value: $${String.format("%,.2f", totalVal)} ($${String.format("%.2f", inv.marketPricePerUnit)}/unit)", color = Color(0xFF4ADE80), fontSize = 9.sp)
                                }
                                Button(
                                    onClick = { viewModel.sellFarmInventory(venture.id, inv.id, inv.quantity) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    modifier = Modifier.height(26.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Sell All ($${String.format("%,.0f", totalVal)})", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            "Machines" -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("MACHINERY & IMPLEMENTS FLEET", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Machines boost planting speed, prevent crop failure, eliminate pests, and multiply harvest yields. Choose between Buying (permanent asset) or Renting per harvest!", color = TextMuted, fontSize = 10.sp)

                    val machineryList = if (venture.farmMachinery.isEmpty()) VentureSimulationHelper.getDefaultFarmMachinery() else venture.farmMachinery
                    machineryList.forEach { m ->
                        val serviceCost = (m.purchaseCost * 0.08).coerceAtLeast(40.0)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (m.isOwned) Color(0xFF10B981) else if (m.isRented) Color(0xFF38BDF8) else Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(m.name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    if (m.isOwned) {
                                        Text("${(m.condition * 100).toInt()}% Health", color = if (m.condition > 0.60f) Color(0xFF10B981) else VentureGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    } else if (m.isRented) {
                                        Text("Rented", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("Purpose: ${m.gameplayEffect}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                Text("Purchase: $${String.format("%,.0f", m.purchaseCost)} (Upkeep: $${String.format("%,.0f", m.weeklyMaintenance)}/wk) • Rent: $${String.format("%,.0f", m.weeklyRentalCost)}/wk", color = TextMuted, fontSize = 9.sp)

                                if (!m.isOwned && !m.isRented) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.buyFarmMachinery(venture.id, m.id, isRent = true) },
                                            modifier = Modifier.weight(1f).height(28.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
                                        ) {
                                            Text("Rent ($${String.format("%,.0f", m.weeklyRentalCost)}/wk)", fontSize = 9.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.buyFarmMachinery(venture.id, m.id, isRent = false) },
                                            enabled = playerCash >= m.purchaseCost,
                                            modifier = Modifier.weight(1f).height(28.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("Buy ($${String.format("%,.0f", m.purchaseCost)})", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else if (m.isOwned) {
                                    Button(
                                        onClick = { viewModel.serviceFarmMachinery(venture.id, m.id) },
                                        enabled = m.condition < 0.95f && playerCash >= serviceCost,
                                        modifier = Modifier.fillMaxWidth().height(26.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                                    ) {
                                        Text(if (m.condition >= 0.95f) "Condition Optimal ✓" else "Overhaul / Service ($${String.format("%,.0f", serviceCost)})", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Livestock" -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("LIVESTOCK & HERD MANAGEMENT", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    if (currentTier < 3) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🔒 LOCKED UNTIL TIER 3", color = VentureGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Upgrade farm to Tier 3 (Crops, Fruits & Livestock for $50,000) to raise chickens, goats, pigs, cows, and sheep, producing weekly milk, wool, eggs, and meat!", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    } else {
                        val livestockList = if (venture.farmLivestock.isEmpty()) VentureSimulationHelper.getDefaultFarmLivestock() else venture.farmLivestock
                        val hasVet = venture.farmVeterinarianCount > 0
                        val hasWorker = venture.farmLivestockWorkerCount > 0

                        livestockList.forEach { grp ->
                            val pairs = grp.matureCount / 2
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (grp.count > 0) Color(0xFF10B981) else Color(0xFF334155)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(grp.type, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Surface(
                                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "${grp.matureCount} Mature",
                                                    color = Color(0xFF10B981),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                            Surface(
                                                color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    "${grp.babyCount} Kids",
                                                    color = Color(0xFF38BDF8),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Text("Produces: ${grp.mainProducts} (Mature only) • Kid Cost: $${String.format("%,.0f", grp.purchaseCostPerUnit)} • Mature Value: $${String.format("%,.0f", grp.matureSalePrice)}", color = TextMuted, fontSize = 9.sp)
                                    Text("Weekly Produce: +$${String.format("%,.0f", grp.weeklyProduceRevenuePerUnit)}/mature • Feed: -$${String.format("%,.0f", grp.weeklyFeedPerUnit)}/head", color = Color(0xFF94A3B8), fontSize = 8.5.sp)

                                    // Reproduction & Maturation Status
                                    val pairDesc = if (pairs > 0) "$pairs mating pair(s) active (+${pairs} newborns/mo)" else "Needs even count (min. 2 mature) to breed"
                                    val survivalNotice = if (hasVet && hasWorker) "✓ Vet & Livestock Worker present (100% infant survival)" else "⚠️ Need Vet & Worker to prevent infant mortality!"
                                    Text("🐣 Matures in ${grp.weeksToMaturity} wks • $pairDesc\n$survivalNotice", color = if (hasVet && hasWorker) Color(0xFF4ADE80) else Color(0xFFFBBF24), fontSize = 8.sp, lineHeight = 10.sp)

                                    // Buy Young Kids
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { viewModel.buyFarmLivestock(venture.id, grp.type, 1) },
                                            enabled = playerCash >= grp.purchaseCostPerUnit,
                                            modifier = Modifier.weight(1f).height(26.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("+1 Kid ($${String.format("%,.0f", grp.purchaseCostPerUnit)})", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { viewModel.buyFarmLivestock(venture.id, grp.type, 5) },
                                            enabled = playerCash >= (grp.purchaseCostPerUnit * 5),
                                            modifier = Modifier.weight(1f).height(26.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                                        ) {
                                            Text("+5 Kids ($${String.format("%,.0f", grp.purchaseCostPerUnit * 5)})", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Sell Mature Livestock (strictly limited to matureCount)
                                    if (grp.matureCount > 0) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedButton(
                                                onClick = { viewModel.sellFarmLivestock(venture.id, grp.type, 1) },
                                                modifier = Modifier.weight(1f).height(26.dp),
                                                contentPadding = PaddingValues(horizontal = 4.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B))
                                            ) {
                                                Text("Sell 1 Mature ($${String.format("%,.0f", grp.matureSalePrice)})", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                            if (grp.matureCount > 1) {
                                                Button(
                                                    onClick = { viewModel.sellFarmLivestock(venture.id, grp.type, grp.matureCount) },
                                                    modifier = Modifier.weight(1f).height(26.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                                                ) {
                                                    Text("Sell All ${grp.matureCount} ($${String.format("%,.0f", grp.matureCount * grp.matureSalePrice)})", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    } else if (grp.babyCount > 0) {
                                        Text("⏳ ${grp.babyCount} kid(s) growing — sales unlock once matured!", color = TextMuted, fontSize = 8.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Processing" -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("AGRI-INDUSTRIAL VALUE-ADD PROCESSING", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Convert raw farm harvests & dairy into higher-value packaged goods (+20% value over raw commodities). Requires the designated industrial machine and a Tractor Operator.", color = TextMuted, fontSize = 10.sp)

                    Surface(
                        color = Color(0xFF1E293B).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "ℹ️ Direct Sale Only: Rice, chicken eggs, and pigs cannot be processed and are sold directly raw or mature.",
                            color = Color(0xFF94A3B8),
                            fontSize = 8.5.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    val hasOperator = venture.farmTractorOperatorCount > 0

                    VentureSimulationHelper.FARM_PROCESSING_RECIPES.forEach { recipe ->
                        val hasMachine = venture.farmMachinery.any { it.id == recipe.requiredMachineId && (it.isOwned || it.isRented) }
                        val rawItem = venture.farmInventory.find { it.name.equals(recipe.rawIngredientName, ignoreCase = true) }
                        val availableRaw = rawItem?.quantity ?: 0.0
                        val canProcess = hasMachine && hasOperator && availableRaw >= recipe.rawQuantityPerBatch
                        val maxBatches = (availableRaw / recipe.rawQuantityPerBatch).toInt()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (canProcess) Color(0xFF10B981) else Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("${recipe.rawIngredientName} ➔ ${recipe.outputName}", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Surface(
                                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text("+20% Value", color = Color(0xFF10B981), fontSize = 8.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }

                                Text(recipe.description, color = TextMuted, fontSize = 9.sp)
                                Text("Market Price: $${String.format("%,.0f", recipe.processedPricePerUnit)} / ${recipe.outputUnit}", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Machine: ${recipe.requiredMachineName} (${if (hasMachine) "✓" else "✗"})", color = if (hasMachine) Color(0xFF4ADE80) else Color(0xFFF87171), fontSize = 8.5.sp)
                                    Text("Staff: Tractor Operator (${if (hasOperator) "${venture.farmTractorOperatorCount} ✓" else "0 ✗"})", color = if (hasOperator) Color(0xFF4ADE80) else Color(0xFFF87171), fontSize = 8.5.sp)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Silo Stock: ${availableRaw.toInt()} / ${recipe.rawQuantityPerBatch.toInt()} ${recipe.rawUnit}", color = if (availableRaw >= recipe.rawQuantityPerBatch) TextWhite else TextMuted, fontSize = 9.sp)

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Button(
                                            onClick = { viewModel.processFarmProduct(venture.id, recipe.id, 1) },
                                            enabled = canProcess,
                                            modifier = Modifier.height(26.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("Process 1", fontSize = 8.5.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                        if (maxBatches > 1) {
                                            Button(
                                                onClick = { viewModel.processFarmProduct(venture.id, recipe.id, maxBatches) },
                                                enabled = canProcess,
                                                modifier = Modifier.height(26.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                                            ) {
                                                Text("Process All ($maxBatches)", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Staff" -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("AGRICULTURAL WORKFORCE PAYROLL", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Staff maintain land cleanliness, operate machinery, breed livestock, haul wholesale harvests to market, and ensure sales quotas are met.", color = TextMuted, fontSize = 10.sp)

                    data class FarmStaffRoleItem(val role: String, val count: Int, val salary: Double, val desc: String)
                    listOf(
                        FarmStaffRoleItem("Farmhand", venture.farmHandCount, VentureSimulationHelper.FARMHAND_WEEKLY_SALARY, "Cleans land & pastures (+6% hygiene/wk each). Without hygiene, crops & livestock suffer mortality!"),
                        FarmStaffRoleItem("Tractor Operator", venture.farmTractorOperatorCount, VentureSimulationHelper.TRACTOR_OPERATOR_WEEKLY_SALARY, "Operates heavy machines & industrial processors. 1 needed per machine."),
                        FarmStaffRoleItem("Livestock Worker", venture.farmLivestockWorkerCount, VentureSimulationHelper.LIVESTOCK_WORKER_WEEKLY_SALARY, "Cares for livestock, oversees monthly mating (pairs produce 2 babies), and harvests milk/eggs/wool/pork."),
                        FarmStaffRoleItem("Driver", venture.farmDriverCount, VentureSimulationHelper.DRIVER_WEEKLY_SALARY, "Transports accumulated wholesale harvests to market, depositing profits directly to business account."),
                        FarmStaffRoleItem("Veterinarian", venture.farmVeterinarianCount, VentureSimulationHelper.VETERINARIAN_WEEKLY_SALARY, "Treats livestock sickness, prevents animal deaths, and preserves healthy breeding rates."),
                        FarmStaffRoleItem("Farm Manager", venture.farmManagerCount, VentureSimulationHelper.FARM_MANAGER_WEEKLY_SALARY, "1 needed for every 10 staff. Coordinates logistics to meet weekly wholesale revenue goals.")
                    ).forEach { (role, count, salary, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F1420))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(role, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Salary: $${String.format("%,.0f", salary)}/wk • $desc", color = TextMuted, fontSize = 8.5.sp, lineHeight = 11.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilledIconButton(
                                    onClick = { viewModel.fireFarmStaff(venture.id, role) },
                                    enabled = count > 0,
                                    modifier = Modifier.size(24.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF334155))
                                ) {
                                    Text("-", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("$count", color = if (count > 0) Color(0xFF10B981) else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                FilledIconButton(
                                    onClick = { viewModel.hireFarmStaff(venture.id, role) },
                                    modifier = Modifier.size(24.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("+", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HotelOperationsPanel(
    venture: BusinessVenture,
    playerCash: Double,
    viewModel: GameViewModel
) {
    var selectedSubTab by remember { mutableStateOf("Rooms") }
    var furnishingRoomNumber by remember { mutableStateOf<Int?>(null) }

    val rooms = if (venture.hotelRooms.isEmpty()) VentureSimulationHelper.getDefaultHotelRooms() else venture.hotelRooms
    val unlockedRooms = rooms.filter { it.isUnlocked }
    val effectiveRoomCount = unlockedRooms.size.coerceIn(2, 20)

    val cleanCapacity = (venture.hotelHousekeeperCount * 5.5).toInt().coerceAtLeast(1)
    val understaffedClean = cleanCapacity < effectiveRoomCount
    val requiredFrontDesk = kotlin.math.ceil(effectiveRoomCount / 7.0).toInt().coerceAtLeast(1)
    val understaffedFrontDesk = venture.hotelFrontDeskCount < requiredFrontDesk
    val requiredMaintenance = kotlin.math.ceil((effectiveRoomCount + venture.hotelAmenities.count { it.isOwned }) / 7.0).toInt().coerceAtLeast(1)
    val understaffedMaintenance = venture.hotelMaintenanceTechCount < requiredMaintenance
    val needsSecurity = effectiveRoomCount > 6 && venture.hotelSecurityCount == 0

    // Furnish Dialog if open
    val targetFurnishRoom = rooms.find { it.roomNumber == furnishingRoomNumber }
    if (targetFurnishRoom != null) {
        FurnishHotelRoomDialog(
            venture = venture,
            room = targetFurnishRoom,
            playerCash = playerCash,
            onDismiss = { furnishingRoomNumber = null },
            viewModel = viewModel
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF253350)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Hotel, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                    Text("HOTEL OPERATIONS & SUITES", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }
                Text(
                    "${unlockedRooms.size}/20 Rooms Active",
                    color = VentureGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quick Metrics Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Occupancy Box
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F1420)).padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("OCCUPANCY", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    val occPct = (venture.hotelOccupancyRate * 100).toInt()
                    val occColor = when {
                        occPct >= 70 -> Color(0xFF10B981)
                        occPct >= 50 -> Color(0xFFFBBF24)
                        else -> Color(0xFFEF4444)
                    }
                    Text("$occPct%", color = occColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${venture.hotelTotalLodgesThisWeek} lodges/wk", color = TextMuted, fontSize = 8.sp)
                }

                // Cleanliness Box
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F1420)).padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("CLEANLINESS", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    val cleanPct = (venture.hotelCleanliness * 100).toInt()
                    val cleanColor = when {
                        cleanPct >= 80 -> Color(0xFF10B981)
                        cleanPct >= 50 -> Color(0xFFFBBF24)
                        else -> Color(0xFFEF4444)
                    }
                    Text("$cleanPct%", color = cleanColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (understaffedClean) "Understaffed!" else "Cleaned ✓",
                        color = if (understaffedClean) Color(0xFFEF4444) else TextMuted,
                        fontSize = 8.sp
                    )
                }

                // Condition Box
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F1420)).padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("BUILDING", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    val condPct = (venture.hotelCondition * 100).toInt()
                    val condColor = when {
                        condPct >= 80 -> Color(0xFF10B981)
                        condPct >= 50 -> Color(0xFFFBBF24)
                        else -> Color(0xFFEF4444)
                    }
                    Text("$condPct%", color = condColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (understaffedMaintenance) "Needs Maint!" else "Maintained ✓",
                        color = if (understaffedMaintenance) Color(0xFFEF4444) else TextMuted,
                        fontSize = 8.sp
                    )
                }

                // Reputation Box
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F1420)).padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("REPUTATION", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    val repPct = (venture.hotelReputation * 100).toInt()
                    val repColor = when {
                        repPct >= 80 -> Color(0xFF10B981)
                        repPct >= 50 -> Color(0xFFFBBF24)
                        else -> Color(0xFFEF4444)
                    }
                    Text("$repPct%", color = repColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (needsSecurity) "Security Alert" else "Prestige ★",
                        color = if (needsSecurity) Color(0xFFEF4444) else TextMuted,
                        fontSize = 8.sp
                    )
                }
            }

            // Sub Tab Row
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF0B0F19)).padding(2.dp)) {
                listOf("Rooms", "Amenities", "Staff", "Finances").forEach { tab ->
                    val isSel = selectedSubTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) Color(0xFF253350) else Color.Transparent)
                            .clickable { selectedSubTab = tab }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tab,
                            color = if (isSel) TextWhite else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            when (selectedSubTab) {
                "Rooms" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Seasonality & Event Badge
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
                                Column {
                                    Text("TOURISM SEASON & MARKET PULSE", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text(venture.hotelSeasonText, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // Unlock Next Room Action if under 20
                        val isMaxedRooms = unlockedRooms.size >= 20
                        val roomCost = VentureSimulationHelper.HOTEL_ROOM_COST
                        if (!isMaxedRooms) {
                            val nextRoomNum = (unlockedRooms.maxOfOrNull { it.roomNumber } ?: 2) + 1
                            Button(
                                onClick = { viewModel.unlockHotelRoom(venture.id, nextRoomNum) },
                                enabled = playerCash >= roomCost,
                                modifier = Modifier.fillMaxWidth().height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text(
                                    "🚪 Unlock Room #$nextRoomNum ($${String.format("%,.0f", roomCost)})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Rooms List (1 to 20)
                        Text("ALL 20 HOTEL ROOMS (TAP TO FURNISH)", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        rooms.forEach { room ->
                            val tierColor = when (room.roomTier) {
                                "Luxury Room" -> Color(0xFFFFB74D)
                                "Premium Room" -> Color(0xFFC084FC)
                                "Comfort Room" -> Color(0xFF60A5FA)
                                "Basic Room" -> Color(0xFF94A3B8)
                                else -> Color(0xFF64748B)
                            }

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (room.isUnlocked) Color(0xFF0F1420) else Color(0xFF0A0D15)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (room.isOutOfOrder) Color(0xFFEF4444)
                                    else if (room.isUnlocked) tierColor.copy(alpha = 0.5f)
                                    else Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                "Room ${room.roomNumber}",
                                                color = if (room.isUnlocked) TextWhite else TextMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (room.isUnlocked) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(tierColor.copy(alpha = 0.2f))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text(room.roomTier, color = tierColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Text("(Locked)", color = TextMuted, fontSize = 9.sp)
                                            }
                                        }

                                        if (room.isUnlocked) {
                                            Text(
                                                "Potential: $${room.potentialWeeklyRate.toInt()}/wk",
                                                color = VentureGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        } else {
                                            Button(
                                                onClick = { viewModel.unlockHotelRoom(venture.id, room.roomNumber) },
                                                enabled = playerCash >= roomCost,
                                                modifier = Modifier.height(26.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                                            ) {
                                                Text("Unlock ($6k)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    if (room.isUnlocked) {
                                        if (room.isOutOfOrder) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("⚠️ OUT OF ORDER: ${room.outOfOrderReason}", color = Color(0xFFEF4444), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                Button(
                                                    onClick = { viewModel.repairHotelRoom(venture.id, room.roomNumber) },
                                                    enabled = playerCash >= 250.0,
                                                    modifier = Modifier.height(24.dp),
                                                    contentPadding = PaddingValues(horizontal = 6.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                                ) {
                                                    Text("Repair ($250)", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "This week: ${room.lodgesThisWeek}/7 lodges • +$${room.weeklyRevenue.toInt()}/wk • Furnished: $${room.totalFurnishingInvestment.toInt()}",
                                                    color = TextMuted,
                                                    fontSize = 8.sp
                                                )
                                                Button(
                                                    onClick = { furnishingRoomNumber = room.roomNumber },
                                                    modifier = Modifier.height(24.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                                                ) {
                                                    Text("Furnish Room", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Amenities" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("HOTEL AMENITIES (SEPARATE REVENUE BUSINESSES)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Amenities generate standalone cashflow while driving higher hotel guest occupancy.", color = TextMuted, fontSize = 9.sp)

                        val amenities = if (venture.hotelAmenities.isEmpty()) VentureSimulationHelper.getDefaultHotelAmenities() else venture.hotelAmenities

                        amenities.forEach { amenity ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (amenity.isOwned) Color(0xFF10B981) else Color(0xFF334155)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(amenity.name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            if (amenity.isOwned) "OPERATING ✓" else "$${String.format("%,.0f", amenity.purchasePrice)}",
                                            color = if (amenity.isOwned) Color(0xFF10B981) else VentureGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(amenity.description, color = TextMuted, fontSize = 9.sp)
                                    Text(
                                        "Impact: +${(amenity.occupancyBonus * 100).toInt()}% Occupancy • +$${String.format("%,.0f", amenity.weeklyRevenueBonus)}/wk Direct Rev • Upkeep: -$${String.format("%,.0f", amenity.weeklyUpkeep)}/wk",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 8.sp
                                    )

                                    if (!amenity.isOwned) {
                                        Button(
                                            onClick = { viewModel.buyHotelAmenity(venture.id, amenity.id) },
                                            enabled = playerCash >= amenity.purchasePrice,
                                            modifier = Modifier.fillMaxWidth().height(26.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("Build & Launch ($${String.format("%,.0f", amenity.purchasePrice)})", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Staff" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("HOTEL STAFFING (EVERY ROLE HAS A PURPOSE)", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Neglecting staffing reduces cleanliness, causes equipment breakdowns, and triggers guest cancellations.", color = TextMuted, fontSize = 9.sp)

                        listOf(
                            Triple("Receptionist", venture.hotelFrontDeskCount, VentureSimulationHelper.HOTEL_FRONT_DESK_SALARY),
                            Triple("Housekeeper", venture.hotelHousekeeperCount, VentureSimulationHelper.HOTEL_HOUSEKEEPER_SALARY),
                            Triple("Chef", venture.hotelChefCount, VentureSimulationHelper.HOTEL_CHEF_SALARY),
                            Triple("Waiter", venture.hotelWaiterCount, VentureSimulationHelper.HOTEL_WAITER_SALARY),
                            Triple("Security Guard", venture.hotelSecurityCount, VentureSimulationHelper.HOTEL_SECURITY_SALARY),
                            Triple("Maintenance Worker", venture.hotelMaintenanceTechCount, VentureSimulationHelper.HOTEL_MAINT_TECH_SALARY),
                            Triple("Hotel Manager", venture.hotelGeneralManagerCount, VentureSimulationHelper.HOTEL_MANAGER_SALARY)
                        ).forEach { (role, count, salary) ->
                            val warning = when (role) {
                                "Housekeeper" -> if (understaffedClean) "⚠️ Deficit: Needs ${kotlin.math.ceil(effectiveRoomCount / 5.5).toInt()} to clean $effectiveRoomCount rooms!" else null
                                "Receptionist" -> if (understaffedFrontDesk) "⚠️ Deficit: Needs $requiredFrontDesk for $effectiveRoomCount rooms!" else null
                                "Maintenance Worker" -> if (understaffedMaintenance) "⚠️ Deficit: Needs $requiredMaintenance to prevent room outages!" else null
                                "Security Guard" -> if (needsSecurity) "⚠️ Alert: No security guards for large hotel property!" else null
                                "Chef" -> if (venture.hotelAmenities.any { it.id == "amen_restaurant" && it.isOwned } && count == 0) "⚠️ Restaurant idle: Needs Head Chef!" else null
                                else -> null
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF0F1420))
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(role, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    val desc = when (role) {
                                        "Receptionist" -> "Checks guests in/out, required beyond 7 rooms • $${salary.toInt()}/wk"
                                        "Housekeeper" -> "Cleans rooms (~5.5 rooms each), prevents dirty suites • $${salary.toInt()}/wk"
                                        "Chef" -> "Cooks for restaurant & room service, unlocks food revenue • $${salary.toInt()}/wk"
                                        "Waiter" -> "Serves dining & room service (+35% food capacity) • $${salary.toInt()}/wk"
                                        "Security Guard" -> "Prevents theft and damage incidents • $${salary.toInt()}/wk"
                                        "Maintenance Worker" -> "Repairs rooms/equipment, reduces downtime • $${salary.toInt()}/wk"
                                        "Hotel Manager" -> "Unlocks higher operational efficiency (+6% occ) • $${salary.toInt()}/wk"
                                        else -> "$${salary.toInt()}/wk"
                                    }
                                    Text(desc, color = TextMuted, fontSize = 8.sp)
                                    if (warning != null) {
                                        Text(warning, color = Color(0xFFEF4444), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilledIconButton(
                                        onClick = { viewModel.fireHotelStaff(venture.id, role) },
                                        enabled = count > 0,
                                        modifier = Modifier.size(24.dp),
                                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF334155))
                                    ) {
                                        Text("-", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("$count", color = if (count > 0) Color(0xFF10B981) else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    FilledIconButton(
                                        onClick = { viewModel.hireHotelStaff(venture.id, role) },
                                        modifier = Modifier.size(24.dp),
                                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF10B981))
                                    ) {
                                        Text("+", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                "Finances" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Deep Clean Button
                        val cleanCost = (effectiveRoomCount * 150.0).coerceAtLeast(500.0)
                        Button(
                            onClick = { viewModel.deepCleanHotel(venture.id) },
                            enabled = (venture.hotelCleanliness < 0.95f || venture.hotelCondition < 0.95f) && playerCash >= cleanCost,
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                        ) {
                            Text(
                                if (venture.hotelCleanliness >= 0.95f && venture.hotelCondition >= 0.95f) "Suites & Facilities in Pristine Cleanliness ✓"
                                else "Deep Sanitation & Facility Cleaning ($${String.format("%,.0f", cleanCost)})",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Financial Breakdown Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("WEEKLY FINANCIAL PERFORMANCE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Room Lodging Revenue (${venture.hotelTotalLodgesThisWeek} lodges)", color = TextWhite, fontSize = 10.sp)
                                    Text("+$${String.format("%,.0f", venture.hotelWeeklyRoomRevenue)}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Restaurant Food Sales", color = TextWhite, fontSize = 10.sp)
                                    Text("+$${String.format("%,.0f", venture.hotelWeeklyFoodRevenue)}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Amenities Direct Revenue (Pool/Cinema/Spa/etc.)", color = TextWhite, fontSize = 10.sp)
                                    Text("+$${String.format("%,.0f", (venture.hotelWeeklyAmenityRevenue - venture.hotelWeeklyFoodRevenue).coerceAtLeast(0.0))}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Staff Payroll", color = TextMuted, fontSize = 10.sp)
                                    Text("-$${String.format("%,.0f", venture.hotelWeeklyPayrollCost)}", color = Color(0xFFEF4444), fontSize = 10.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Room & Amenity Supplies", color = TextMuted, fontSize = 10.sp)
                                    Text("-$${String.format("%,.0f", venture.hotelWeeklySuppliesCost)}", color = Color(0xFFEF4444), fontSize = 10.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Utilities ($35/room)", color = TextMuted, fontSize = 10.sp)
                                    Text("-$${String.format("%,.0f", venture.hotelWeeklyUtilityCost)}", color = Color(0xFFEF4444), fontSize = 10.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Facility & Upkeep Maintenance", color = TextMuted, fontSize = 10.sp)
                                    Text("-$${String.format("%,.0f", venture.hotelWeeklyMaintenanceCost)}", color = Color(0xFFEF4444), fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FurnishHotelRoomDialog(
    venture: BusinessVenture,
    room: HotelRoom,
    playerCash: Double,
    onDismiss: () -> Unit,
    viewModel: GameViewModel
) {
    var selectedCategory by remember { mutableStateOf("Bed") }

    val categories = listOf(
        "Bed", "Mattress", "Wardrobe", "Desk", "Chair",
        "TV", "Mini fridge", "Air conditioner", "Curtains",
        "Lighting", "Bathroom fixtures", "Decoration", "Wi-Fi equipment"
    )

    val currentInstalled = room.furnishings.find { it.category == selectedCategory }
    val catalogItems = VentureSimulationHelper.HOTEL_FURNITURE_CATALOG.filter { it.category == selectedCategory }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("FURNISH ROOM #${room.roomNumber}", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(room.roomTier, color = VentureGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    "Invested: $${room.totalFurnishingInvestment.toInt()} • Potential: $${room.potentialWeeklyRate.toInt()}/wk",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // One-Click Packages Section
                Text("ONE-CLICK COMPLETE PACKAGES", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        Triple("Basic", 1240.0, 182),
                        Triple("Comfort", 3190.0, 250),
                        Triple("Premium", 6860.0, 350),
                        Triple("Luxury", 15050.0, 500)
                    ).forEach { (tier, cost, pot) ->
                        Button(
                            onClick = { viewModel.furnishHotelRoomPackage(venture.id, room.roomNumber, tier) },
                            enabled = playerCash >= cost,
                            modifier = Modifier.weight(1f).height(44.dp),
                            contentPadding = PaddingValues(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (tier) {
                                    "Luxury" -> Color(0xFFB45309)
                                    "Premium" -> Color(0xFF7E22CE)
                                    "Comfort" -> Color(0xFF1D4ED8)
                                    else -> Color(0xFF334155)
                                }
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(tier, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("$${cost.toInt()}", fontSize = 8.sp, color = VentureGold)
                                Text("~$pot/wk", fontSize = 7.sp, color = TextWhite.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                Divider(color = Color(0xFF253350), modifier = Modifier.padding(vertical = 2.dp))

                // Custom 13 Categories Selector
                Text("CUSTOM FURNITURE (13 CATEGORIES)", color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                // Scrollable category chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F1420))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(categories) { cat ->
                            val isSel = selectedCategory == cat
                            val hasItem = room.furnishings.any { it.category == cat }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) Color(0xFF3B82F6) else if (hasItem) Color(0xFF1E293B) else Color(0xFF131A2B))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    cat + if (hasItem) " ✓" else "",
                                    color = if (isSel) TextWhite else if (hasItem) Color(0xFF10B981) else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Currently installed item status
                Text(
                    if (currentInstalled != null) "Current: ${currentInstalled.name} (${currentInstalled.qualityTier} - $${currentInstalled.cost.toInt()})"
                    else "Current: None installed for $selectedCategory",
                    color = if (currentInstalled != null) Color(0xFF10B981) else TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // 4 Quality Tiers for selected category
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f, fill = false), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(catalogItems) { item ->
                        val isEquipped = currentInstalled?.name == item.name
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1420)),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isEquipped) Color(0xFF10B981) else Color(0xFF253350)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(6.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(item.name, color = TextWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(
                                                    when (item.qualityTier) {
                                                        "Luxury" -> Color(0xFFFFB74D)
                                                        "Premium" -> Color(0xFFC084FC)
                                                        "Comfort" -> Color(0xFF60A5FA)
                                                        else -> Color(0xFF94A3B8)
                                                    }.copy(alpha = 0.2f)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                item.qualityTier,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (item.qualityTier) {
                                                    "Luxury" -> Color(0xFFFFB74D)
                                                    "Premium" -> Color(0xFFC084FC)
                                                    "Comfort" -> Color(0xFF60A5FA)
                                                    else -> Color(0xFF94A3B8)
                                                }
                                            )
                                        }
                                    }
                                    Text(item.description, color = TextMuted, fontSize = 8.sp)
                                }

                                if (isEquipped) {
                                    Text("INSTALLED ✓", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Button(
                                        onClick = {
                                            viewModel.furnishHotelRoomItem(
                                                venture.id,
                                                room.roomNumber,
                                                HotelFurnitureItem(item.category, item.name, item.cost, item.qualityTier)
                                            )
                                        },
                                        enabled = playerCash >= item.cost,
                                        modifier = Modifier.height(24.dp),
                                        contentPadding = PaddingValues(horizontal = 6.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                                    ) {
                                        Text("Buy $${item.cost.toInt()}", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF131A2B)
    )
}

@Composable
private fun CryptoTokenOperationsPanel(
    venture: BusinessVenture,
    playerCash: Double,
    viewModel: GameViewModel
) {
    var selectedSubTab by remember { mutableStateOf("5 Core Areas") }
    val listingStatus = remember(venture) {
        VentureSimulationHelper.checkCryptoListingEligibility(venture)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161129)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3A2B66)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Token, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(18.dp))
                    Column {
                        Text(
                            "${venture.cryptoTokenName.ifBlank { "Crypto Token" }} (${venture.cryptoTokenTicker})",
                            color = Color(0xFFA855F7),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (venture.cryptoIsMarketListed) "🟢 TRADABLE ON CRYPTO MARKET" else "🟡 PRIVATE STARTUP (UNLISTED)",
                            color = if (venture.cryptoIsMarketListed) Color(0xFF10B981) else Color(0xFFFBBF24),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$${String.format(if (venture.cryptoTokenPrice < 0.01) "%.6f" else "%.4f", venture.cryptoTokenPrice)}",
                        color = Color(0xFF10B981),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "MCap: $${String.format("%,.0f", venture.cryptoCirculatingSupply * venture.cryptoTokenPrice)}",
                        color = TextMuted,
                        fontSize = 8.sp
                    )
                }
            }

            // Quick Metrics Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Active Users Box
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F0B1E)).padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ACTIVE USERS", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(
                        String.format("%,d", venture.cryptoActiveUsers),
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Web3 Protocol", color = TextMuted, fontSize = 8.sp)
                }

                // Volume Box
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F0B1E)).padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("WEEKLY VOLUME", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("$${String.format("%,.0f", venture.cryptoWeeklyTradingVolume)}", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("0.30% Fee Cut", color = Color(0xFF10B981), fontSize = 8.sp)
                }

                // Liquidity Box
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF0F0B1E)).padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AMM LIQUIDITY", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("$${String.format("%,.0f", venture.cryptoLiquidityPoolUsd)}", color = Color(0xFFA855F7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(if (venture.cryptoLiquidityPoolUsd >= 15000.0) "Deep Pool" else "Thin Pool", color = if (venture.cryptoLiquidityPoolUsd >= 15000.0) Color(0xFF10B981) else Color(0xFFFBBF24), fontSize = 8.sp)
                }
            }

            // Educational / System Insight Banner
            if (venture.cryptoEducationalLesson.isNotBlank()) {
                Surface(
                    color = Color(0xFF2E1065).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                        Text(
                            venture.cryptoEducationalLesson,
                            color = Color(0xFFE9D5FF),
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            // Exploit Warning Banner if any hack loss occurred
            if (venture.cryptoLastHackLoss > 0.0) {
                Surface(
                    color = Color(0xFF7F1D1D).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                        Text(
                            "⚠️ Recent Exploit: Lost $${String.format("%,.0f", venture.cryptoLastHackLoss)} to smart contract flash-loan attack! Upgrade Security immediately to reduce hack risks.",
                            color = Color(0xFFFEE2E2),
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Cryptocurrency Market Listing Status Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (venture.cryptoIsMarketListed) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFF3A2B66)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (venture.cryptoIsMarketListed) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Text(
                                "LISTED ON CRYPTOCURRENCY MARKET",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "${venture.cryptoTokenTicker} is actively traded by NPCs and traders on the Cryptocurrency Market. You hold ${String.format("%,.0f", venture.cryptoFounderTokensOwned)} founder tokens. Open Financial Markets to view live order flow & trade!",
                            color = TextMuted,
                            fontSize = 9.sp,
                            lineHeight = 12.sp
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "MARKET LISTING REQUIREMENTS",
                                color = Color(0xFFFBBF24),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (listingStatus.isEligible) "✅ ELIGIBLE FOR LISTING" else "IN PROGRESS",
                                color = if (listingStatus.isEligible) Color(0xFF10B981) else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Checklist items in compact row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "1+ Devs: ${if (listingStatus.hasDev) "✅" else "❌"}",
                                color = if (listingStatus.hasDev) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontSize = 9.sp
                            )
                            Text(
                                "Security Tier 2+: ${if (listingStatus.hasSecurity) "✅" else "❌"}",
                                color = if (listingStatus.hasSecurity) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontSize = 9.sp
                            )
                            Text(
                                "500+ Users: ${if (listingStatus.hasUsers) "✅ (${venture.cryptoActiveUsers})" else "❌ (${venture.cryptoActiveUsers})"}",
                                color = if (listingStatus.hasUsers) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontSize = 9.sp
                            )
                            Text(
                                "$15k+ LP: ${if (listingStatus.hasLiquidity) "✅" else "❌"}",
                                color = if (listingStatus.hasLiquidity) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontSize = 9.sp
                            )
                        }

                        if (listingStatus.isEligible) {
                            Button(
                                onClick = { viewModel.listCryptoOnExchange(venture.id) },
                                modifier = Modifier.fillMaxWidth().height(34.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("LIST TOKEN ON CRYPTOCURRENCY MARKET NOW", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                "Upgrade Developers, Security, and Liquidity above to qualify for exchange listing.",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            // Sub Tab Row
            Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFF0C0917)).padding(2.dp)) {
                listOf("5 Core Areas", "Protocol Services", "Tokenomics").forEach { tab ->
                    val isSel = selectedSubTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) Color(0xFF3A2B66) else Color.Transparent)
                            .clickable { selectedSubTab = tab }
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tab,
                            color = if (isSel) TextWhite else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            when (selectedSubTab) {
                "5 Core Areas" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("5 CORE BUSINESS ELEMENTS", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Manage and upgrade these 5 foundational areas to scale active users, reduce hacks, and drive token value.", color = TextMuted, fontSize = 9.sp)

                        // 1. DEVELOPERS
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                        Text("1. Developers: ${venture.cryptoDevelopersCount} Active", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("$${venture.cryptoDevelopersCount * 500}/wk", color = Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }
                                Text(
                                    "Builds and maintains smart contracts, patches bugs, increases user capacity headroom, and enables deploying protocol services.",
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { viewModel.hireCryptoDeveloper(venture.id) },
                                        enabled = playerCash >= VentureSimulationHelper.CRYPTO_DEV_HIRE_COST,
                                        modifier = Modifier.weight(1f).height(30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                                    ) {
                                        Text("+ Hire Developer ($1,500)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    if (venture.cryptoDevelopersCount > 0) {
                                        OutlinedButton(
                                            onClick = { viewModel.fireCryptoDeveloper(venture.id) },
                                            modifier = Modifier.height(30.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444))
                                        ) {
                                            Text("Release (-$500/wk)", color = Color(0xFFEF4444), fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // 2. SECURITY
                        val secTier = VentureSimulationHelper.CRYPTO_SECURITY_TIERS.find { it.tier == venture.cryptoSecurityTier } ?: VentureSimulationHelper.CRYPTO_SECURITY_TIERS.first()
                        val nextSecTier = VentureSimulationHelper.CRYPTO_SECURITY_TIERS.find { it.tier == venture.cryptoSecurityTier + 1 }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                                        Text("2. Security: T${secTier.tier} (${secTier.name})", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("${secTier.vulnerabilityPercent}% risk", color = if (secTier.vulnerabilityPercent <= 1.0) Color(0xFF10B981) else Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }
                                Text(secTier.description, color = TextMuted, fontSize = 9.sp)
                                Text("Weekly upkeep: $${secTier.weeklyCost.toInt()}/wk", color = TextMuted, fontSize = 8.sp)
                                if (nextSecTier != null) {
                                    Button(
                                        onClick = { viewModel.upgradeCryptoSecurity(venture.id, nextSecTier.tier) },
                                        enabled = playerCash >= nextSecTier.upgradeCost,
                                        modifier = Modifier.fillMaxWidth().height(30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                                    ) {
                                        Text("Upgrade to ${nextSecTier.name} ($${String.format("%,.0f", nextSecTier.upgradeCost)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text("🛡️ Maximum ZK & Bug Bounty Fortress protection active.", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // 3. WEBSITE / APP
                        val webTier = VentureSimulationHelper.CRYPTO_WEBSITE_APP_TIERS.find { it.tier == venture.cryptoWebsiteAppTier } ?: VentureSimulationHelper.CRYPTO_WEBSITE_APP_TIERS.first()
                        val nextWebTier = VentureSimulationHelper.CRYPTO_WEBSITE_APP_TIERS.find { it.tier == venture.cryptoWebsiteAppTier + 1 }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(16.dp))
                                        Text("3. Website & App: T${webTier.tier} (${webTier.name})", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("Cap: ${String.format("%,d", webTier.userCapacity)}", color = Color(0xFFA855F7), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }
                                Text(webTier.description, color = TextMuted, fontSize = 9.sp)
                                val usagePct = (venture.cryptoActiveUsers.toFloat() / webTier.userCapacity.toFloat()).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { usagePct },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = if (usagePct > 0.85f) Color(0xFFEF4444) else Color(0xFFA855F7),
                                    trackColor = Color(0xFF3A2B66)
                                )
                                Text("Active Users: ${String.format("%,d", venture.cryptoActiveUsers)} / ${String.format("%,d", webTier.userCapacity)} (${(usagePct * 100).toInt()}% load) • Server: $${webTier.weeklyCost.toInt()}/wk", color = TextMuted, fontSize = 8.sp)
                                if (nextWebTier != null) {
                                    Button(
                                        onClick = { viewModel.upgradeCryptoWebsiteApp(venture.id, nextWebTier.tier) },
                                        enabled = playerCash >= nextWebTier.upgradeCost,
                                        modifier = Modifier.fillMaxWidth().height(30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                                    ) {
                                        Text("Upgrade to ${nextWebTier.name} ($${String.format("%,.0f", nextWebTier.upgradeCost)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // 4. MARKETING
                        val mktTier = VentureSimulationHelper.CRYPTO_MARKETING_TIERS.find { it.tier == venture.cryptoMarketingTier } ?: VentureSimulationHelper.CRYPTO_MARKETING_TIERS.first()
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                        Text("4. Marketing Strategy", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("$${mktTier.weeklyCost.toInt()}/wk", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }
                                Text("Current: ${mktTier.name} — ${mktTier.description}", color = TextMuted, fontSize = 9.sp)

                                // Tier selection pills
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    VentureSimulationHelper.CRYPTO_MARKETING_TIERS.forEach { tier ->
                                        val isSel = venture.cryptoMarketingTier == tier.tier
                                        OutlinedButton(
                                            onClick = { viewModel.setCryptoMarketingTier(venture.id, tier.tier) },
                                            modifier = Modifier.weight(1f).height(30.dp),
                                            contentPadding = PaddingValues(0.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color(0xFFF59E0B) else Color(0xFF3A2B66)),
                                            colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isSel) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color.Transparent)
                                        ) {
                                            Text("T${tier.tier} ($${tier.weeklyCost.toInt()})", color = if (isSel) Color(0xFFF59E0B) else TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // 5. LIQUIDITY
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f, fill = false).padding(end = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF06B6D4), modifier = Modifier.size(16.dp))
                                        Text("5. AMM Liquidity Pool", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Text("$${String.format("%,.0f", venture.cryptoLiquidityPoolUsd)} Pool", color = Color(0xFF06B6D4), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                                }
                                Text("Why does liquidity matter? Allows the coin to trade properly without severe slippage. Deeper liquidity attracts institutional volume and qualifies for market listing ($15,000+).", color = TextMuted, fontSize = 9.sp)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { viewModel.injectCryptoLiquidity(venture.id, 5000.0) },
                                        enabled = playerCash >= 5000.0,
                                        modifier = Modifier.weight(1f).height(30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2))
                                    ) {
                                        Text("+ $5k LP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.injectCryptoLiquidity(venture.id, 25000.0) },
                                        enabled = playerCash >= 25000.0,
                                        modifier = Modifier.weight(1f).height(30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490))
                                    ) {
                                        Text("+ $25k LP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.executeTokenBuybackBurn(venture.id, 5000.0) },
                                        enabled = playerCash >= 5000.0,
                                        modifier = Modifier.weight(1.2f).height(30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                                    ) {
                                        Text("🔥 Burn ($5k)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                "Protocol Services" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("DEPLOY PROTOCOL SERVICES", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Launching new on-chain services surges active utility and sparks sudden token price jumps as more people gain reasons to hold and use the token.", color = TextMuted, fontSize = 9.sp)

                        VentureSimulationHelper.CRYPTO_AVAILABLE_SERVICES.forEach { service ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(service.name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            "+${(service.priceSurgePercent * 100).toInt()}% Token Jump",
                                            color = Color(0xFF10B981),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(service.description, color = TextMuted, fontSize = 9.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Req: ${service.minDevsRequired} Devs • $${String.format("%,.0f", service.devBudgetCost)} Dev Budget",
                                            color = TextMuted,
                                            fontSize = 8.sp
                                        )
                                        val hasDevs = venture.cryptoDevelopersCount >= service.minDevsRequired
                                        val canAfford = playerCash >= service.devBudgetCost
                                        Button(
                                            onClick = { viewModel.launchCryptoNewService(venture.id, service.id) },
                                            enabled = hasDevs && canAfford,
                                            modifier = Modifier.height(28.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7))
                                        ) {
                                            Text(
                                                if (!hasDevs) "Need ${service.minDevsRequired} Devs" else if (!canAfford) "Need $${String.format("%,.0f", service.devBudgetCost)}" else "Deploy Service",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "Tokenomics" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Supply & Ledger Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("ON-CHAIN LEDGER METRICS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Circulating Supply", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("${String.format("%,.0f", venture.cryptoCirculatingSupply)} tokens", color = Color(0xFFA855F7), fontSize = 10.sp, fontWeight = FontWeight.Bold, softWrap = false)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Founder Tokens Held", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("${String.format("%,.0f", venture.cryptoFounderTokensOwned)} tokens", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, softWrap = false)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Staked in Protocol", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("${String.format("%,.0f", venture.cryptoStakedTokens)} tokens", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, softWrap = false)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Market Capitalization", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    val mcap = venture.cryptoCirculatingSupply * venture.cryptoTokenPrice
                                    Text("$${String.format("%,.0f", mcap)}", color = VentureGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, softWrap = false)
                                }
                            }
                        }

                        // Financial Breakdown Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0B1E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("WEEKLY PROTOCOL CASHFLOW", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("AMM DEX Swap Fees (0.3%)", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("+$${String.format("%,.0f", venture.cryptoWeeklySwapFeesRevenue)}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, softWrap = false)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Protocol Utility Fees", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("+$${String.format("%,.0f", venture.cryptoWeeklyProtocolFeeRevenue)}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, softWrap = false)
                                }
                                if (venture.tierLevel >= 2) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text("Staking Protocol Cut", color = TextWhite, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                        Text("+$${String.format("%,.0f", venture.cryptoWeeklyStakingFeesRevenue)}", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold, softWrap = false)
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Core Developers (${venture.cryptoDevelopersCount})", color = TextMuted, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("-$${String.format("%,.0f", venture.cryptoWeeklyPayrollCost)}", color = Color(0xFFEF4444), fontSize = 10.sp, softWrap = false)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Server & RPC Infrastructure", color = TextMuted, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("-$${String.format("%,.0f", venture.cryptoWeeklyServerInfraCost)}", color = Color(0xFFEF4444), fontSize = 10.sp, softWrap = false)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Security Upkeep & Audits", color = TextMuted, fontSize = 10.sp, modifier = Modifier.weight(1f))
                                    Text("-$${String.format("%,.0f", venture.cryptoWeeklySecurityCost)}", color = Color(0xFFEF4444), fontSize = 10.sp, softWrap = false)
                                }
                                Divider(color = Color(0xFF3A2B66), thickness = 0.5.dp)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Weekly Net Profit", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        "$${String.format("%,.0f", venture.weeklyNetProfit)}",
                                        color = if (venture.weeklyNetProfit >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
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

@Composable
private fun LaunchEnterprisesTab(
    state: GameStateEntity,
    onSelectType: (String) -> Unit
) {
    val enterpriseBlueprints = listOf(
        EnterpriseBlueprint(
            type = "Taxi Fleet",
            icon = Icons.Default.LocalTaxi,
            category = "Logistics & Transport",
            setupCost = 5000.0,
            baseGross = 955.0,
            baseExpenses = 680.0,
            netProfit = 275.0,
            description = "Initial Investment: $5,000 | Business Value at Launch: $6,500 | Weekly Gross: ~$955 | Driver salary: $400, Fuel: ~$160, Maintenance: ~$120 | Weekly Profit: ~$200–350 (~$800–1,400/mo). Payback in ~4 months. Scale up to 25 vehicles, hiring 1 driver per vehicle.",
            scalingRoadmap = "Buy vehicles ($4,500 each) and hire drivers ($400/wk) to scale up to 25 cars."
        ),
        EnterpriseBlueprint(
            type = "Restaurant",
            icon = Icons.Default.Restaurant,
            category = "Hospitality & Dining",
            setupCost = 5000.0,
            baseGross = 1400.0,
            baseExpenses = 970.0,
            netProfit = 430.0,
            description = "Start with a Roadside Stand ($5,000). Progression: Roadside → Food Truck ($12k) → Corner Shop ($35k) → Dining Lounge ($85k) → Banquet Hall ($220k). Hire Chefs, Waiters, Cleaners, and Managers. Ingredient costs scale realistically at 30% of sales, plus equipment upkeep and building maintenance.",
            scalingRoadmap = "Roadside ($5k) → Food Truck ($12k) → Corner Shop ($35k) → Dining Lounge ($85k) → Banquet Hall ($220k)."
        ),
        EnterpriseBlueprint(
            type = "Farming",
            icon = Icons.Default.Agriculture,
            category = "Agriculture & Food",
            setupCost = 15000.0,
            baseGross = 2500.0,
            baseExpenses = 1500.0,
            netProfit = 1000.0,
            description = "Acquire rural acreage growing staple grain. Progress through 4 tiers: Crop Plantation → Crops & Fruit Orchards → Crops, Fruits & Livestock → Agri-Industrial Processing & Dairy ($12,300/wk net).",
            scalingRoadmap = "4 Expansion Tiers: Crops → Orchards → Livestock → Industrial Dairy/Packaging."
        ),
        EnterpriseBlueprint(
            type = "Hotel",
            icon = Icons.Default.Hotel,
            category = "Real Estate & Hospitality",
            setupCost = 500000.0,
            baseGross = 364.0,
            baseExpenses = 1500.0,
            netProfit = -1136.0,
            description = "Launch cost $500,000 (2 initial suites). Unlock up to 20 rooms ($6,000/room), furnish individual rooms across 13 furniture categories to reach Basic, Comfort, Premium, or Luxury tiers, hire vital staff (Housekeepers, Receptionists, Chefs, Waiters, Security, Maintenance, Managers), and build amenities (Cinema, Pool, Event Hall, Spa) to drive weekly lodging occupancy.",
            scalingRoadmap = "Expand up to 20 rooms ($6k/room), furnish rooms (Basic, Comfort, Premium, Luxury), build 8 amenities (Cinema, Pool, Spa, Event Hall), and list on stock market."
        ),
        EnterpriseBlueprint(
            type = "Crypto Token",
            icon = Icons.Default.Token,
            category = "Decentralized Web3",
            setupCost = 1000000.0,
            baseGross = 1000.0,
            baseExpenses = 750.0,
            netProfit = 250.0,
            description = "High-Capital Crypto Protocol — $1,000,000 startup capital. Requires smart contract audit reserves, decentralized node hosting, initial $75,000 liquidity pool, core developers, and security tiers. Qualify your token for listing on Genesis!",
            scalingRoadmap = "5 Major Areas: Developers ($1,500 recruit), Security (Tiers 1–4), Website/App (Tiers 1–4), Marketing (Tiers 1–4), Liquidity ($75k+ to list). Once listed, NPCs and global traders actively buy/sell your coin in the Cryptocurrency Market!"
        )
    )

    val sideJobSlots = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
    val tourSlots = state.tours.filter { it.status == "ACTIVE" }.sumOf { it.weeklyTimeCost }
    val ventureSlots = state.businessVentures.count { it.isOwned && it.isPlayerInControl } * 10
    val totalUsedSlots = sideJobSlots + tourSlots + ventureSlots
    val freeSlots = state.maxTimeSlots - totalUsedSlots
    val hasTimeSlots = freeSlots >= 10

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("AVAILABLE ENTERPRISE OPPORTUNITIES", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select an industry to build a recurring weekly income engine. Each venture requires 10 time slots to oversee. Available slots: $freeSlots / ${state.maxTimeSlots}", color = TextWhite, fontSize = 13.sp)
            }
        }

        items(enterpriseBlueprints) { bp ->
            EnterpriseBlueprintCard(
                bp = bp,
                playerCash = state.cash,
                hasTimeSlots = hasTimeSlots,
                freeSlots = freeSlots,
                onLaunch = { onSelectType(bp.type) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

private data class EnterpriseBlueprint(
    val type: String,
    val icon: ImageVector,
    val category: String,
    val setupCost: Double,
    val baseGross: Double,
    val baseExpenses: Double,
    val netProfit: Double,
    val description: String,
    val scalingRoadmap: String
)

@Composable
private fun EnterpriseBlueprintCard(
    bp: EnterpriseBlueprint,
    playerCash: Double,
    hasTimeSlots: Boolean,
    freeSlots: Int,
    onLaunch: () -> Unit
) {
    val canAfford = playerCash >= bp.setupCost
    val canLaunch = canAfford && hasTimeSlots

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(VentureCardBg)
            .border(1.2.dp, VentureCardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(VenturePurple.copy(alpha = 0.2f))
                        .border(1.dp, VenturePurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(bp.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text(bp.type, color = TextWhite, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(bp.category, color = TextMuted, fontSize = 12.sp)
                }
            }

            Surface(
                color = if (canAfford) VentureGreen.copy(alpha = 0.15f) else VentureRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (canAfford) VentureGreen else VentureRed)
            ) {
                Text(
                    text = "Capital: $${formatCompactMoney(bp.setupCost)}",
                    color = if (canAfford) VentureGreen else VentureRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Text(bp.description, color = Color(0xFFD1D5DB), fontSize = 13.sp, lineHeight = 18.sp)

        // Financial Economics Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF101422))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Initial Gross", color = TextMuted, fontSize = 11.sp)
                    Text("+$${formatCompactMoney(bp.baseGross)}/wk", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("Operating Expenses", color = TextMuted, fontSize = 11.sp)
                    Text("-$${formatCompactMoney(bp.baseExpenses)}/wk", color = VentureRed, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Net Cashflow", color = TextMuted, fontSize = 11.sp)
                    Text("+$${formatCompactMoney(bp.netProfit)}/wk", color = VentureGreen, fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Surface(
            color = Color(0xFF1F1A10),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = VentureGold, modifier = Modifier.size(16.dp))
                Text(bp.scalingRoadmap, color = VentureGold, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        Button(
            onClick = onLaunch,
            enabled = canLaunch,
            colors = ButtonDefaults.buttonColors(containerColor = VenturePurple),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            val btnLabel = when {
                !canAfford -> "Insufficient Funds ($${formatCompactMoney(bp.setupCost)} Needed)"
                !hasTimeSlots -> "Requires 10 Free Time Slots ($freeSlots Available)"
                else -> "Launch ${bp.type} ($${formatCompactMoney(bp.setupCost)} • 10 Time Slots)"
            }
            Text(
                btnLabel,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun WeShareIpoOverviewTab(
    ownedVentures: List<BusinessVenture>,
    state: GameStateEntity,
    onOpenIpoDialog: (BusinessVenture) -> Unit
) {
    val listedVentures = ownedVentures.filter { it.isListedOnWeShare }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(VentureCardBg)
                    .border(1.dp, VentureCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Public, contentDescription = null, tint = VentureGreen, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("WESHARE EQUITY LISTINGS & IPOS", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("List 10% to 50% of your business on the stock exchange", color = TextMuted, fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "• Sell shares to raise non-dilutive public capital based on all-time business earnings.\n" +
                    "• Public investors purchase shares weekly depending on business profitability until 100% of the listed tranche is sold.\n" +
                    "• Once 100% sold, shares trade freely on secondary exchange order books, paying quarterly dividends to public shareholders.\n" +
                    "• Under WeShare regulations, founders cannot buy or trade shares of their own listed company.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        if (listedVentures.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF141826))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No Publicly Listed Companies Yet", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ensure your enterprise is operating profitably, maintain high integrity (≥45%) and trustworthiness (≥35%), then file for IPO.", color = TextMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        } else {
            item {
                Text("YOUR PUBLIC COMPANIES ON WESHARE", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            items(listedVentures) { venture ->
                val ipoRatio = if (venture.weShareTotalSharesIssued > 0) (venture.weSharePublicSharesSold.toFloat() / venture.weShareTotalSharesIssued.toFloat()).coerceIn(0f, 1f) else 1f
                val capitalRaised = venture.weSharePublicSharesSold * venture.weShareSharePrice

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(VentureCardBg)
                        .border(1.dp, VentureGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(venture.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Listed ${(venture.weShareListedEquityPercent * 100).toInt()}% Float · Share Price: $${String.format("%.2f", venture.weShareSharePrice)}", color = VentureGreen, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F261B))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(if (venture.weSharePublicSharesSold >= venture.weShareTotalSharesIssued) "100% Sold" else "Active IPO", color = VentureGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Public Tranche Progress: ${venture.weSharePublicSharesSold} / ${venture.weShareTotalSharesIssued} shares sold (${(ipoRatio * 100).toInt()}%)", color = TextMuted, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = ipoRatio,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = VentureGreen,
                        trackColor = Color(0xFF262C3D)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Capital Raised to Date:", color = TextMuted, fontSize = 11.sp)
                        Text("$${String.format("%,.0f", capitalRaised)}", color = VentureGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        val unlistedVentures = ownedVentures.filter { !it.isListedOnWeShare }
        if (unlistedVentures.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("ENTERPRISES READY FOR PUBLIC LISTING", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            items(unlistedVentures) { venture ->
                val (eligible, reason) = VentureSimulationHelper.checkWeShareEligibility(venture, state.integrity.toInt(), state.trustworthiness.toInt())
                val valuation = VentureSimulationHelper.calculateWeShareValuation(venture)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(VentureCardBg)
                        .border(1.dp, VentureCardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(venture.name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(venture.tierName.ifBlank { venture.type }, color = Color(0xFF93C5FD), fontSize = 12.sp)
                        }
                        Surface(
                            color = if (eligible) VentureGreen.copy(alpha = 0.15f) else Color(0xFF332020),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (eligible) VentureGreen.copy(alpha = 0.5f) else VentureRed.copy(alpha = 0.5f))
                        ) {
                            Text(
                                if (eligible) "Eligible for IPO" else "Requirements Pending",
                                color = if (eligible) VentureGreen else VentureRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Estimated Valuation", color = TextMuted, fontSize = 10.sp)
                            Text("$${String.format("%,.0f", valuation.totalValuation)}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Net Cashflow", color = TextMuted, fontSize = 10.sp)
                            Text("+$${String.format("%,.0f", venture.weeklyNetProfit)}/wk", color = VentureGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onOpenIpoDialog(venture) },
                            enabled = eligible,
                            colors = ButtonDefaults.buttonColors(containerColor = VentureGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("File IPO", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    if (!eligible) {
                        Text(reason, color = VentureRed, fontSize = 10.sp, lineHeight = 13.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun LaunchBusinessDialog(
    type: String,
    playerCash: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var businessName by remember { mutableStateOf("") }
    val defaultName = when (type) {
        "Taxi Fleet" -> "Metro City Taxis"
        "Restaurant" -> "The Artisan Bistro"
        "Farming" -> "Green Pastures Farm"
        "Hotel" -> "The Grand Palms Hotel"
        "Crypto Token" -> "SoundChain Protocol"
        else -> "Alpha Enterprises"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Launch $type", color = TextWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter a unique enterprise name for your new venture:", color = TextMuted, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    placeholder = { Text(defaultName, color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = VenturePurple,
                        unfocusedBorderColor = VentureCardBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(businessName.ifBlank { defaultName }) },
                colors = ButtonDefaults.buttonColors(containerColor = VenturePurple)
            ) {
                Text("Launch Enterprise", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        },
        containerColor = VentureCardBg
    )
}

@Composable
private fun WeShareIpoDialog(
    venture: BusinessVenture,
    state: GameStateEntity,
    onDismiss: () -> Unit,
    onConfirmListing: (Double) -> Unit
) {
    var listedPercent by remember { mutableStateOf(20.0) }
    val (eligible, reason) = VentureSimulationHelper.checkWeShareEligibility(venture, state.integrity.toInt(), state.trustworthiness.toInt())
    val valResult = VentureSimulationHelper.calculateSmartSccValuation(venture, listedPercent / 100.0)
    val trancheShares = valResult.targetSharesToSell
    val estCapital = trancheShares * valResult.sharePrice

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Public, contentDescription = null, tint = VentureGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("WeShare IPO Application", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column {
                Text("Company: ${venture.name}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                val isApproved = venture.screeningStatus == "APPROVED"
                val isUnderReview = venture.screeningStatus == "UNDER_REVIEW"

                if (isApproved) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F261B))
                            .border(1.dp, VentureGreen, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text("✅ Regulatory Audit Passed! Officially approved for public listing on WeShare at $${String.format("%.2f", venture.approvedSharePrice)}/share.", color = VentureGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (isUnderReview) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E2333))
                            .border(1.dp, Color(0xFF64B5F6), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text("📑 Regulatory Audit In Progress: ${venture.screeningWeeksRemaining} week(s) remaining. You will receive an official determination email upon completion.", color = Color(0xFF90CAF9), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (!eligible) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2E1515))
                            .border(1.dp, VentureRed, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(reason, color = VentureRed, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F261B))
                            .border(1.dp, VentureGreen, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text("✅ Verified & Pre-Screened for WeShare IPO filing. A 2-week regulatory compliance audit ($20,000 filing fee) is required prior to public listing.", color = VentureGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Valuation:", color = TextMuted, fontSize = 12.sp)
                    Text("$${String.format("%,.0f", valResult.totalValuation)}", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("IPO Share Price:", color = TextMuted, fontSize = 12.sp)
                    Text("$${String.format("%.2f", valResult.sharePrice)} / share", color = VentureGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Select Percentage of Equity to List (10% - 50%):", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(10.0, 20.0, 30.0, 40.0, 50.0).forEach { p ->
                        val isSel = listedPercent == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) VentureGreen else Color(0xFF1E2333))
                                .clickable(enabled = !isUnderReview) { listedPercent = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${p.toInt()}%", color = if (isSel) Color.Black else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF141724))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Public Shares Issued:", color = TextMuted, fontSize = 11.sp)
                            Text("$trancheShares shares", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Projected Capital to Raise:", color = TextMuted, fontSize = 11.sp)
                            Text("$${String.format("%,.0f", estCapital)}", color = VentureGreen, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        if (!isApproved && !isUnderReview) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Regulatory Audit Filing Fee:", color = Color(0xFFFFA000), fontSize = 11.sp)
                                Text("$20,000", color = Color(0xFFFFA000), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            val isApproved = venture.screeningStatus == "APPROVED"
            val isUnderReview = venture.screeningStatus == "UNDER_REVIEW"
            val totalFunds = state.cash + state.banking.savingsBalance
            val canAffordFee = totalFunds >= 20000.0
            val btnEnabled = (isApproved || (eligible && canAffordFee)) && !isUnderReview

            val buttonLabel = when {
                isApproved -> "Confirm & Launch IPO"
                isUnderReview -> "Audit in Progress (${venture.screeningWeeksRemaining} wks)"
                !canAffordFee -> "Need $20,000 Audit Fee"
                else -> "Confirm & Submit Audit ($20,000 Fee)"
            }

            Button(
                onClick = { onConfirmListing(listedPercent) },
                enabled = btnEnabled,
                colors = ButtonDefaults.buttonColors(containerColor = VentureGreen)
            ) {
                Text(buttonLabel, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) }
        },
        containerColor = VentureCardBg
    )
}

@Composable
private fun ManageFundsDialog(
    venture: BusinessVenture,
    playerCash: Double,
    onDismiss: () -> Unit,
    onDeposit: (Double) -> Unit,
    onWithdraw: (Double) -> Unit
) {
    var mode by remember { mutableStateOf("Withdraw") } // "Deposit" or "Withdraw"
    val availableProfit = venture.accumulatedProfit.coerceAtLeast(0.0)
    val availableCapital = venture.investedCapital.coerceAtLeast(0.0)
    val totalAvailable = availableProfit

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Funds • ${venture.name}", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF141724)).padding(4.dp)) {
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (mode == "Withdraw") VentureGreen else Color.Transparent).clickable { mode = "Withdraw" }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text("Withdraw Profits", color = if (mode == "Withdraw") Color.Black else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (mode == "Deposit") VenturePurple else Color.Transparent).clickable { mode = "Deposit" }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text("Deposit Reserves", color = if (mode == "Deposit") Color.White else TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                if (mode == "Withdraw") {
                    Text("Undrawn Net Profit: $${String.format("%,.0f", availableProfit)}", color = VentureGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Treasury Capital: $${String.format("%,.0f", availableCapital)}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text("Available to Withdraw: $${String.format("%,.0f", totalAvailable)}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Note: Only net operating profits can be withdrawn. Capital spent on the business belongs to operations.", color = Color(0xFF64748B), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(1000.0, 5000.0, 10000.0).forEach { amt ->
                            val canWith = totalAvailable >= amt
                            Button(
                                onClick = { onWithdraw(amt) },
                                enabled = canWith,
                                colors = ButtonDefaults.buttonColors(containerColor = VentureCardBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("$${amt.toInt()}", fontSize = 11.sp, color = if (canWith) TextWhite else TextMuted)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onWithdraw(totalAvailable) },
                        enabled = totalAvailable > 0.0,
                        colors = ButtonDefaults.buttonColors(containerColor = VentureGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Withdraw All Profits ($${String.format("%,.0f", totalAvailable)})", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Player Available Cash: $${String.format("%,.0f", playerCash)}", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(2000.0, 5000.0, 10000.0).forEach { amt ->
                            val canDep = playerCash >= amt
                            Button(
                                onClick = { onDeposit(amt) },
                                enabled = canDep,
                                colors = ButtonDefaults.buttonColors(containerColor = VentureCardBorder),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("$${amt.toInt()}", fontSize = 11.sp, color = if (canDep) TextWhite else TextMuted)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close", color = TextMuted) }
        },
        containerColor = VentureCardBg
    )
}
