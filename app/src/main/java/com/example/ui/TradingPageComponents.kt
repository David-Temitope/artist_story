package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Crypto
import com.example.data.Employee
import com.example.data.GameData
import com.example.data.GameStateEntity
import com.example.data.GameViewModel
import com.example.data.MarketSentimentInfo
import com.example.data.Stock
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
import kotlin.math.abs

/**
 * Enhanced Artist Wealth & Investment Management Screen.
 * Part of the artist's financial life: clearly distinguishes Opportunities vs Holdings,
 * Gains/Losses, Risk profiles, Expected returns, and Time horizons.
 */
@Composable
fun EnhancedTradingPage(
    state: GameStateEntity,
    viewModel: GameViewModel
) {
    var activeTab by remember { mutableStateOf("Holdings") } // "Holdings", "Opportunities", "Crypto"
    var filterRisk by remember { mutableStateOf("ALL") }

    val ownedStocks = state.stocks.filter { it.quantity > 0 }
    val availableStocks = state.stocks
    val ownedCryptos = state.cryptos.filter { it.quantity > 0 }
    val availableCryptos = state.cryptos

    val totalStockValue = state.stocks.sumOf { it.quantity * it.currentPrice }
    val totalStockCost = state.stocks.sumOf { it.quantity * it.averagePurchasePrice }
    val stockUnrealizedPnL = totalStockValue - totalStockCost

    val totalCryptoValue = state.cryptos.sumOf { it.quantity * it.currentPrice }
    val totalCryptoCost = state.cryptos.sumOf { it.quantity * it.averagePurchasePrice }
    val cryptoUnrealizedPnL = totalCryptoValue - totalCryptoCost

    val totalPortfolioValue = totalStockValue + totalCryptoValue
    val totalPortfolioPnL = stockUnrealizedPnL + cryptoUnrealizedPnL

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP ARTIST WEALTH & WEALTH ALLOCATION CARD
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.5f)),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth().testTag("wealth_hero_card")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2C2412),
                                        Color(0xFF1E1A29),
                                        Color(0xFF0F0B1E)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.Black.copy(alpha = 0.4f),
                                    border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.6f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Savings,
                                            contentDescription = null,
                                            tint = AccentGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ARTIST WEALTH MANAGEMENT",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = TextPrimary
                                        )
                                    }
                                }

                                Text(
                                    text = "Liquid Cash: $${String.format("%,.0f", state.cash)}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = AccentGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column {
                                Text(
                                    text = "$${String.format("%,.2f", totalPortfolioValue)}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (totalPortfolioPnL >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                        contentDescription = null,
                                        tint = if (totalPortfolioPnL >= 0) AccentGreen else DangerColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (totalPortfolioPnL >= 0) "+$${String.format("%,.2f", totalPortfolioPnL)} total gain" else "-$${String.format("%,.2f", abs(totalPortfolioPnL))} total loss",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (totalPortfolioPnL >= 0) AccentGreen else DangerColor
                                    )
                                }
                            }
                        }
                    }

                    // Strategic Asset Allocation Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF10131E))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("EQUITIES ALLOCATION", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                            Text("$${String.format("%,.0f", totalStockValue)}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PrimaryBrandLight)
                        }
                        Column {
                            Text("CRYPTO ASSETS", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                            Text("$${String.format("%,.0f", totalCryptoValue)}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = NeonPurple)
                        }
                        Column {
                            Text("WEEKLY DIVIDENDS", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                            val estDividends = state.stocks.sumOf { it.quantity * it.currentPrice * it.weeklyDividendPercent }
                            Text("+$${String.format("%,.0f", estDividends)}/wk", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = AccentGold)
                        }
                    }
                }
            }
        }

        // NAVIGATION TABS: Holdings vs Opportunities vs Crypto
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Triple("Holdings", "My Holdings (${ownedStocks.size + ownedCryptos.size})", Icons.Default.PieChart),
                    Triple("Opportunities", "Stock Opportunities", Icons.Default.ShowChart),
                    Triple("Crypto", "Crypto Opportunities", Icons.Default.CurrencyBitcoin)
                ).forEach { (tabId, label, icon) ->
                    val isSelected = activeTab == tabId
                    Button(
                        onClick = { activeTab = tabId },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PrimaryBrandLight else Color.Transparent,
                            contentColor = if (isSelected) Color.White else TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("trading_tab_$tabId")
                    ) {
                        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        val sentimentInfo = viewModel.getMarketSentimentInfo(state)
        item {
            MarketPulseHeaderCard(
                sentimentInfo = sentimentInfo,
                economyCycle = state.economyCycle,
                inflationRate = state.inflationRate
            )
        }

        when (activeTab) {
            "Holdings" -> {
                if (ownedStocks.isEmpty() && ownedCryptos.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            border = BorderStroke(1.dp, BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(AccentGold.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = AccentGold,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Text(
                                    text = "No Current Investment Holdings",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Investing streaming royalties into equities and growth assets hedges against career downturns and builds generational artist wealth.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { activeTab = "Opportunities" },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Explore Stock Opportunities", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    if (ownedStocks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Equities & Stocks Portfolio",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBrandLight
                            )
                        }
                        items(ownedStocks) { stock ->
                            HoldingStockCard(
                                stock = stock,
                                onBuyMore = { viewModel.buyStock(stock.id, 10) },
                                onSell = { viewModel.sellStock(stock.id, 10) }
                            )
                        }
                    }

                    if (ownedCryptos.isNotEmpty()) {
                        item {
                            Text(
                                text = "Digital & Crypto Holdings",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonPurple
                            )
                        }
                        items(ownedCryptos) { crypto ->
                            HoldingCryptoCard(
                                crypto = crypto,
                                onBuyMore = { viewModel.buyCrypto(crypto.id, 500.0) },
                                onSell = { viewModel.sellCrypto(crypto.id, crypto.quantity / 2.0) }
                            )
                        }
                    }
                }
            }

            "Opportunities" -> {
                item {
                    Text(
                        text = "AVAILABLE MARKET OPPORTUNITIES",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                }
                items(availableStocks) { stock ->
                    OpportunityStockCard(
                        stock = stock,
                        playerCash = state.cash,
                        onBuy = { qty -> viewModel.buyStock(stock.id, qty) }
                    )
                }
            }

            "Crypto" -> {
                item {
                    CryptoAnalystDeskCard(state = state, viewModel = viewModel)
                }
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "DIGITAL ASSETS & CRYPTO MARKET",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary
                    )
                }
                items(availableCryptos) { crypto ->
                    OpportunityCryptoCard(
                        crypto = crypto,
                        playerCash = state.cash,
                        onBuy = { amount -> viewModel.buyCrypto(crypto.id, amount) }
                    )
                }
            }
        }
    }
}

/**
 * Stock Opportunity Card clearly distinguishing:
 * Risk, Expected Return, Time Horizon, Price, Valuation (P/E), Financial Health, and Dividend yields.
 */
@Composable
fun OpportunityStockCard(
    stock: Stock,
    playerCash: Double,
    onBuy: (Int) -> Unit
) {
    val riskProfile = when {
        stock.weeklyDividendPercent >= 0.03 -> "Defensive / Dividend"
        stock.currentPrice > 160 -> "Blue Chip / Low Risk"
        stock.currentPrice > 65 -> "Growth / Moderate Risk"
        else -> "Speculative / High Risk"
    }
    val horizon = when {
        stock.weeklyDividendPercent >= 0.03 -> "Long-Term (Generational)"
        stock.currentPrice > 160 -> "Medium to Long-Term"
        else -> "Short to Medium-Term"
    }
    val trend = stock.recentChangePercent()

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().testTag("stock_opp_${stock.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name, Sector, & Current Price + Trend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = TextPrimary
                    )
                    Text(
                        text = stock.sector,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%,.2f", stock.currentPrice)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AccentGreen
                    )
                    Text(
                        text = if (trend >= 0) "▲ +${String.format("%.1f", trend)}%" else "▼ ${String.format("%.1f", trend)}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (trend >= 0) AccentGreen else DangerColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Valuation & Health Metrics Pill + Protected Base
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceColor,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "P/E: ${String.format("%.1f", stock.PE_Ratio)} • ${stock.valuationStatus()}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (stock.PE_Ratio < 14) AccentGreen else if (stock.PE_Ratio > 30) DangerColor else AccentGold
                        )
                        Text(
                            text = "Health: ${stock.financialHealth}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    val basePrice = if (stock.initialPrice > 0.0) stock.initialPrice else stock.currentPrice
                    Text(
                        text = "🛡️ Base Floor: $${String.format("%,.2f", basePrice)} (Protected from dropping below inception)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AccentGreen,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Investment Traits: Risk, Return, Time Horizon
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceColor,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PROFILE", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = AccentGold, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(riskProfile, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("TIME HORIZON", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timelapse, contentDescription = null, tint = PrimaryBrandLight, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(horizon, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dividend Yield & Cash Affordability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Dividend: ${String.format("%.1f%%", stock.weeklyDividendPercent * 100)} / wk",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = AccentGold
                    )
                    Text(
                        text = "Favors large capital with passive payouts",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = TextSecondary
                    )
                }

                val cost10 = stock.currentPrice * 10
                val canAfford10 = playerCash >= cost10

                Button(
                    onClick = { onBuy(10) },
                    enabled = canAfford10,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "BUY 10 ($${String.format("%,.0f", cost10)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

/**
 * Crypto Opportunity Card with distinct Support/Resistance and Technical State.
 */
@Composable
fun OpportunityCryptoCard(
    crypto: Crypto,
    playerCash: Double,
    onBuy: (Double) -> Unit
) {
    val trend = crypto.recentChangePercent()
    val techZone = crypto.technicalZone()
    val shiftType = crypto.volatilityCategory()
    val athMultiplier = if (crypto.initialPrice > 0.0) (crypto.allTimeHigh / crypto.initialPrice) else 1.0

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier.fillMaxWidth().testTag("crypto_opp_${crypto.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = crypto.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (shiftType) {
                                "Big Shift" -> NeonPink.copy(alpha = 0.2f)
                                "Medium Wave" -> AccentGold.copy(alpha = 0.2f)
                                else -> TextSecondary.copy(alpha = 0.15f)
                            },
                            border = BorderStroke(1.dp, when (shiftType) {
                                "Big Shift" -> NeonPink.copy(alpha = 0.6f)
                                "Medium Wave" -> AccentGold.copy(alpha = 0.6f)
                                else -> BorderColor
                            })
                        ) {
                            Text(
                                text = shiftType,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = when (shiftType) {
                                    "Big Shift" -> NeonPink
                                    "Medium Wave" -> AccentGold
                                    else -> TextSecondary
                                },
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Dev: ${crypto.developerActivity} • 24h Vol: $${String.format("%,.0f", crypto.volume24h)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%,.2f", crypto.currentPrice)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = AccentGold
                    )
                    Text(
                        text = if (trend >= 0) "▲ +${String.format("%.1f", trend)}%" else "▼ ${String.format("%.1f", trend)}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (trend >= 0) AccentGreen else DangerColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technical Support & Resistance & Ratchet Info
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceColor,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Support: $${String.format("%,.2f", crypto.supportLevel)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentGreen
                        )
                        Text(
                            text = "Resistance: $${String.format("%,.2f", crypto.resistanceLevel)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = DangerColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "📊 State: $techZone",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (techZone.contains("Support")) AccentGreen else if (techZone.contains("Resistance")) DangerColor else TextSecondary
                    )

                    if (athMultiplier >= 3.0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🚀 3x-4x Surge Achieved: Protected floor ratcheted near base ($${String.format("%,.2f", crypto.cycleFloor)}). Tough to drop back to initial price!",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                            color = NeonPink
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceColor,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RISK PROFILE", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                        Text("High Volatility / Asymmetric", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = NeonPink)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("STRATEGY", style = MaterialTheme.typography.labelSmall, color = TextTertiary, fontSize = 9.sp)
                        Text("Favors Risk-Takers", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBrandLight)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Professional Analyst Forecast & Risk Assessment
            val analystPred = crypto.analystPrediction
            val hasAnalystIntel = analystPred.isNotEmpty() && analystPred != "UNASSISTED"
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (hasAnalystIntel) {
                    if (analystPred == "RISE") AccentGreen.copy(alpha = 0.12f) else DangerColor.copy(alpha = 0.12f)
                } else SurfaceColor,
                border = BorderStroke(1.dp, if (hasAnalystIntel) {
                    if (analystPred == "RISE") AccentGreen.copy(alpha = 0.45f) else DangerColor.copy(alpha = 0.45f)
                } else BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasAnalystIntel) {
                                    if (analystPred == "RISE") Icons.Default.TrendingUp else Icons.Default.TrendingDown
                                } else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (hasAnalystIntel) {
                                    if (analystPred == "RISE") AccentGreen else DangerColor
                                } else AccentGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasAnalystIntel) {
                                    "ANALYST FORECAST: ${analystPred} (${crypto.predictionConfidence}% CONF)"
                                } else {
                                    "ANALYST INTEL: UNASSISTED"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (hasAnalystIntel) {
                                    if (analystPred == "RISE") AccentGreen else DangerColor
                                } else AccentGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = crypto.predictionReason.ifEmpty {
                            "Crypto has huge unpredictable shifts (+85% / -60%). Hire a high-skilled Crypto Analyst to forecast breakout direction and hedge crash losses."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onBuy(250.0) },
                    enabled = playerCash >= 250.0,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("BUY $250", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = { onBuy(1000.0) },
                    enabled = playerCash >= 1000.0,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("BUY $1,000", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

/**
 * Professional Crypto Analyst Desk & Market Realism Briefing.
 * Highlighting the reality that crypto investments carry huge, unpredictable shifts,
 * and that retaining a high-skilled analyst is the best way to invest and mitigate downside crashes.
 */
@Composable
fun CryptoAnalystDeskCard(
    state: GameStateEntity,
    viewModel: GameViewModel
) {
    val cryptoAnalyst = state.hiredEmployees.find {
        it.role.equals("Crypto Analyst", ignoreCase = true) ||
        (it.role.contains("Analyst", ignoreCase = true) && !it.role.contains("Financial", ignoreCase = true))
    }
    val analystSkill = cryptoAnalyst?.skillRatingPercent ?: 0
    var showHiringCandidates by remember { mutableStateOf(false) }

    val candidates = remember(state.candidateRoster) {
        val fromState = state.candidateRoster.filter { it.role.equals("Crypto Analyst", ignoreCase = true) }
        if (fromState.isNotEmpty()) fromState else GameData.generateRealisticCandidateRoster().filter { it.role.equals("Crypto Analyst", ignoreCase = true) }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, if (cryptoAnalyst != null) AccentGold.copy(alpha = 0.5f) else DangerColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("crypto_analyst_desk_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CurrencyBitcoin,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CRYPTO VOLATILITY & ANALYST DESK",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = TextPrimary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (cryptoAnalyst != null) AccentGreen.copy(alpha = 0.15f) else DangerColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (cryptoAnalyst != null) AccentGreen.copy(alpha = 0.5f) else DangerColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (cryptoAnalyst != null) "HEDGED & PROTECTED" else "HIGH RISK / UNASSISTED",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = if (cryptoAnalyst != null) AccentGreen else DangerColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reality Comparison Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceColor,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "📊 Market Dynamics: Shares vs Crypto",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryBrandLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Shares (Equities): Move little by little (-1.5% to +1.8%). Rare big shifts (~4.5% chance) occur on corporate earnings.\n• Crypto (Coins): Shift is huge (+85% / -60%) balanced with quiet sideways periods. Hard to predict without an elite analyst.\n• Best Strategy: Retain a high-skilled Crypto Analyst to forecast breakout direction and hedge 50%–75% of crash losses!",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Analyst or Alert
            if (cryptoAnalyst != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentGold.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = cryptoAnalyst.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentGold.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (analystSkill >= 80) "★ HIGH SKILL ($analystSkill%)" else "SKILL: $analystSkill%",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                                            color = AccentGold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Salary: $${String.format("%,.0f", cryptoAnalyst.weeklySalary)}/wk • ${cryptoAnalyst.traitDescription.ifEmpty { "On-Chain Risk Manager" }}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextSecondary
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { showHiringCandidates = !showHiringCandidates },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor.copy(alpha = 0.2f)),
                                    border = BorderStroke(1.dp, PrimaryBrandLight),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (showHiringCandidates) "Hide" else "Upgrade",
                                        fontSize = 11.sp,
                                        color = PrimaryBrandLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { viewModel.fireProfessional(cryptoAnalyst.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DangerColor.copy(alpha = 0.15f)),
                                    border = BorderStroke(1.dp, DangerColor.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Fire",
                                        fontSize = 11.sp,
                                        color = DangerColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        val hedgePct = when {
                            analystSkill >= 80 -> (50 + ((analystSkill - 80) * 1.25)).toInt().coerceIn(50, 75)
                            analystSkill >= 60 -> (25 + ((analystSkill - 60) * 1.0)).toInt().coerceIn(25, 45)
                            else -> 10
                        }
                        val winRate = when {
                            analystSkill >= 90 -> 86
                            analystSkill >= 80 -> 80
                            analystSkill >= 60 -> 68
                            else -> 52
                        }
                        Text(
                            text = "🛡️ Active Crash Hedging: Recovers up to $hedgePct% of portfolio losses during huge downward crashes (-12% or worse). On-chain forecasting win rate: $winRate%.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                            color = AccentGreen
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DangerColor.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, DangerColor.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = DangerColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "⚠️ NO CRYPTO ANALYST ON RETAINER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = DangerColor
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You are currently absorbing 100% of unpredictable market dumps unassisted. Hire a high-skilled analyst to unlock on-chain predictive intelligence and automated downside hedge protection.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showHiringCandidates = !showHiringCandidates },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (showHiringCandidates) "Close Candidates" else "Hire a Professional Crypto Analyst",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Candidate Roster Quick Hire Section
            if (showHiringCandidates) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AVAILABLE HIGH-SKILLED CRYPTO ANALYSTS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = AccentGold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    candidates.sortedByDescending { it.skillRatingPercent }.forEach { candidate ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceColor,
                            border = BorderStroke(1.dp, if (candidate.skillRatingPercent >= 80) AccentGold.copy(alpha = 0.4f) else BorderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = candidate.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (candidate.skillRatingPercent >= 80) AccentGold.copy(alpha = 0.2f) else TextSecondary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${candidate.skillRatingPercent}% Skill",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                color = if (candidate.skillRatingPercent >= 80) AccentGold else TextSecondary,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "$${String.format("%,.0f", candidate.weeklySalary)}/wk • ${candidate.traitDescription.ifEmpty { "On-chain quant analyst" }}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.recruitProfessional(candidate.role, candidate.name, candidate.weeklySalary)
                                        showHiringCandidates = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (candidate.skillRatingPercent >= 80) AccentGold else PrimaryBrandColor
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Hire",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (candidate.skillRatingPercent >= 80) Color.Black else Color.White
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

/**
 * Market Pulse & Sentiment Header Card clearly presenting macro cycle, fear/greed meter, and strategic guidance.
 */
@Composable
fun MarketPulseHeaderCard(
    sentimentInfo: MarketSentimentInfo,
    economyCycle: String,
    inflationRate: Float
) {
    val sentimentColor = when {
        sentimentInfo.score >= 76 -> AccentGreen
        sentimentInfo.score >= 56 -> PrimaryBrandLight
        sentimentInfo.score >= 45 -> AccentGold
        sentimentInfo.score >= 25 -> Color(0xFFFF9800)
        else -> DangerColor
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, sentimentColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth().testTag("market_pulse_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Macro Cycle & Sentiment Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = PrimaryBrandLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CYCLE: $economyCycle".uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = sentimentColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, sentimentColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${sentimentInfo.label} (${sentimentInfo.score}/100)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = sentimentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gauge / Progress Meter
            Column(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { (sentimentInfo.score / 100f).coerceIn(0.05f, 0.95f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = sentimentColor,
                    trackColor = BorderColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Extreme Fear (0)", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = DangerColor)
                    Text("Neutral (50)", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = AccentGold)
                    Text("Extreme Greed (100)", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = AccentGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Strategy Insight Pill
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceColor,
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "💡",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = sentimentInfo.actionableTip,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Holding Stock Card with clear Unrealized P&L, Cost Basis, and Sell/Buy controls.
 */
@Composable
fun HoldingStockCard(
    stock: Stock,
    onBuyMore: () -> Unit,
    onSell: () -> Unit
) {
    val totalCost = stock.quantity * stock.averagePurchasePrice
    val currentValue = stock.quantity * stock.currentPrice
    val pnl = currentValue - totalCost
    val pnlPercent = if (totalCost > 0.0) (pnl / totalCost) * 100.0 else 0.0

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
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
                    Text(stock.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Text("${stock.quantity} shares @ avg $${String.format("%.2f", stock.averagePurchasePrice)}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%,.2f", currentValue)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = if (pnl >= 0) "+$${String.format("%,.2f", pnl)} (+${String.format("%.1f", pnlPercent)}%)" else "-$${String.format("%,.2f", abs(pnl))} (${String.format("%.1f", pnlPercent)}%)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (pnl >= 0) AccentGreen else DangerColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSell,
                    border = BorderStroke(1.dp, if (stock.quantity >= 10) DangerColor else BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (stock.quantity >= 10) DangerColor else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f),
                    enabled = stock.quantity >= 10
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SELL 10", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onBuyMore,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrandColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("BUY 10 MORE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

/**
 * Holding Crypto Card with clear P&L and Liquidation controls.
 */
@Composable
fun HoldingCryptoCard(
    crypto: Crypto,
    onBuyMore: () -> Unit,
    onSell: () -> Unit
) {
    val totalCost = crypto.quantity * crypto.averagePurchasePrice
    val currentValue = crypto.quantity * crypto.currentPrice
    val pnl = currentValue - totalCost
    val pnlPercent = if (totalCost > 0.0) (pnl / totalCost) * 100.0 else 0.0

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
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
                    Text(crypto.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Text("${String.format("%.4f", crypto.quantity)} tokens @ $${String.format("%.2f", crypto.currentPrice)}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%,.2f", currentValue)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = if (pnl >= 0) "+$${String.format("%,.2f", pnl)} (+${String.format("%.1f", pnlPercent)}%)" else "-$${String.format("%,.2f", abs(pnl))} (${String.format("%.1f", pnlPercent)}%)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (pnl >= 0) AccentGreen else DangerColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSell,
                    border = BorderStroke(1.dp, DangerColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SELL 50%", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onBuyMore,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("BUY $500", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
