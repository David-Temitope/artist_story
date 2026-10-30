package com.example.data

import java.util.UUID
import kotlin.random.Random

data class MarketSimulationResult(
    val updatedStocks: List<Stock>,
    val updatedCryptos: List<Crypto>,
    val remainingStockOrders: List<StockOrder>,
    val remainingCryptoOrders: List<CryptoOrder>,
    val addedCash: Double,
    val addedSalary: Double
)

data class StockConfig(
    val anchorPrice: Double,
    val sector: String,
    val beta: Double,
    val basePE: Double,
    val health: String
)

data class CryptoSpec(
    val anchor: Double,
    val support: Double,
    val resistance: Double,
    val tier: Int,
    val dev: String
)

object MarketSimulationHelper {

    val stockConfigs = mapOf(
        "stock_media" to StockConfig(50.0, "Broadcasting & Media", 1.00, 16.5, "Stable"),
        "stock_apex" to StockConfig(120.0, "Music Publishing & Distribution", 0.65, 13.8, "Strong"),
        "stock_space" to StockConfig(35.0, "Creative Real Estate", 0.85, 12.2, "Stable"),
        "stock_stream" to StockConfig(80.0, "Digital Streaming Tech", 1.35, 27.5, "Strong"),
        "stock_acoustic" to StockConfig(45.0, "Audio Equipment & Guitars", 0.80, 11.5, "Stable"),
        "stock_label" to StockConfig(65.0, "Independent Labels", 0.90, 14.0, "Stable"),
        "stock_auto" to StockConfig(210.0, "EV & Clean Tech", 1.45, 36.5, "Strong"),
        "stock_micro" to StockConfig(185.0, "Audio DSP & Semiconductors", 1.35, 23.0, "Strong"),
        "stock_realestate" to StockConfig(140.0, "Commercial Real Estate & REIT", 0.60, 10.8, "Strong")
    )

    val cryptoSpecs = mapOf(
        "crypto_bit" to CryptoSpec(45000.0, 35000.0, 68000.0, 1, "High"),
        "crypto_ether" to CryptoSpec(3200.0, 2400.0, 4800.0, 2, "High"),
        "crypto_sol" to CryptoSpec(150.0, 105.0, 260.0, 2, "High"),
        "crypto_ripple" to CryptoSpec(1.20, 0.85, 2.20, 3, "Medium"),
        "crypto_cardano" to CryptoSpec(0.65, 0.45, 1.20, 3, "Medium"),
        "crypto_polka" to CryptoSpec(6.20, 4.40, 12.00, 3, "Medium"),
        "crypto_doge" to CryptoSpec(0.15, 0.09, 0.40, 4, "Low"),
        "crypto_shib" to CryptoSpec(0.000025, 0.000015, 0.000080, 4, "Low")
    )

    fun simulateStocksAndCryptos(
        state: GameStateEntity,
        economyCycle: String,
        isElectionSeason: Boolean,
        hackerCryptoLossRatio: Double,
        reportBuilder: StringBuilder
    ): MarketSimulationResult {
        val electionVolatility = if (isElectionSeason) 1.15 else 1.00
        var addedCash = 0.0
        var addedSalary = 0.0

        // Check for active Crypto Analyst
        val cryptoAnalyst = state.hiredEmployees.find {
            it.role.equals("Crypto Analyst", ignoreCase = true) ||
            (it.role.contains("Crypto", ignoreCase = true) && it.role.contains("Analyst", ignoreCase = true)) ||
            (it.role.contains("Analyst", ignoreCase = true) && !it.role.contains("Financial", ignoreCase = true))
        }
        val analystSkill = cryptoAnalyst?.skillRatingPercent ?: 0

        // 1. SIMULATE STOCKS (SHARES):
        // Shift "little by little", rare big shifts (+/-), stable corporate equities
        val updatedStocks = state.stocks.map { stock ->
            val cfg = stockConfigs[stock.id] ?: StockConfig(stock.currentPrice.coerceAtLeast(20.0), stock.sector, 1.0, 15.0, "Stable")
            val initialBase = if (stock.initialPrice > 0.0) stock.initialPrice else cfg.anchorPrice
            val currentATH = maxOf(stock.allTimeHigh, stock.currentPrice, initialBase)

            // Health drift: subtle, gradual change
            val healthDrift = when (cfg.health) {
                "Strong" -> Random.nextDouble(0.1, 0.5)
                "Stable" -> Random.nextDouble(-0.2, 0.3)
                else -> Random.nextDouble(-0.5, 0.3)
            }

            // Macro percentage scaled by sector beta: gentle economic influence
            var macroPct = when (economyCycle) {
                "Boom" -> Random.nextDouble(0.2, 0.7) * cfg.beta
                "Stable Growth" -> Random.nextDouble(0.0, 0.4) * cfg.beta
                "Inflation Peak" -> Random.nextDouble(-0.5, 0.2) * cfg.beta
                "Recession" -> Random.nextDouble(-0.8, -0.1) * cfg.beta
                "Depression" -> Random.nextDouble(-1.2, -0.3) * cfg.beta
                else -> 0.0
            }

            // Living world impulses: player & cultural moments
            var playerInfluence = 0.0
            var narrativeEventStr = ""

            if (stock.id == "stock_stream" && state.soundstreamMonthlyListeners > 10_000_000) {
                playerInfluence += 0.4
                if (Random.nextDouble() < 0.08) narrativeEventStr = "SoundStream stock ticked upward following record-breaking platform streaming numbers."
            }
            if (stock.id == "stock_space" && state.tours.any { it.status == "ACTIVE" && it.level == "Premium" }) {
                playerInfluence += 0.3
                if (Random.nextDouble() < 0.08) narrativeEventStr = "Creative Real Estate saw modest steady gains supported by national concert tours."
            }
            if (stock.quantity > 5000 && Random.nextDouble() < 0.04) {
                playerInfluence += 0.5
                narrativeEventStr = "Institutional analysts noted long-term institutional accumulation in ${stock.name}."
            }

            // Sector baseline noise (small, gradual fluctuations)
            val sectorTrend = when (stock.sector) {
                "Tech" -> Random.nextDouble(-0.4, 0.5)
                "Finance" -> Random.nextDouble(-0.2, 0.3)
                "Entertainment" -> Random.nextDouble(-0.3, 0.4)
                "Retail" -> Random.nextDouble(-0.3, 0.3)
                else -> 0.0
            }

            // Standard low-volatility operational noise for shares (shift little by little)
            val operationalNoise = Random.nextDouble(-0.6, 0.6)

            // Valuation gravity: gentle pull back toward fair valuation
            val priceRatio = stock.currentPrice / initialBase
            val valuationPull = when {
                priceRatio >= 2.8 -> -Random.nextDouble(0.6, 1.4)
                priceRatio >= 2.0 -> -Random.nextDouble(0.3, 0.8)
                priceRatio < 0.80 -> Random.nextDouble(0.4, 1.2)
                else -> 0.0
            }

            // RARE BIG SHIFTS (~4.5% chance per week per stock, can be upward or downward)
            val isRareBigShift = Random.nextFloat() < 0.045f
            val rareBigShiftPct = if (isRareBigShift) {
                val biasBullish = priceRatio < 1.10 || economyCycle == "Boom"
                val isUpward = if (biasBullish) Random.nextFloat() < 0.60f else Random.nextFloat() < 0.42f
                if (isUpward) {
                    val shift = Random.nextDouble(6.0, 14.0)
                    val corporateBeats = listOf(
                        "${stock.name} reported stellar quarterly earnings beating Wall Street expectations, stock rose +${String.format("%.1f", shift)}%.",
                        "${stock.name} secured a multi-year commercial enterprise contract, boosting shares +${String.format("%.1f", shift)}%.",
                        "${stock.name} announced a major share repurchase program and raised guidance, lifting shares +${String.format("%.1f", shift)}%."
                    )
                    narrativeEventStr = corporateBeats.random()
                    shift
                } else {
                    val shift = -Random.nextDouble(5.0, 13.0)
                    val corporateMisses = listOf(
                        "${stock.name} slipped ${String.format("%.1f", shift)}% after missing consensus revenue estimates amid supply chain friction.",
                        "${stock.name} dropped ${String.format("%.1f", shift)}% following regulatory scrutiny and executive leadership turnover.",
                        "${stock.name} shares declined ${String.format("%.1f", shift)}% as institutional downgrades cited conservative consumer spending."
                    )
                    narrativeEventStr = corporateMisses.random()
                    shift
                }
            } else {
                0.0
            }

            if (narrativeEventStr.isNotEmpty() && !reportBuilder.contains(narrativeEventStr)) {
                reportBuilder.append("📈 Corporate News: $narrativeEventStr\n")
            }

            // Total stock percentage shift: normal weeks are between -1.5% and +1.8%
            val totalStockPct = (healthDrift + macroPct + playerInfluence + sectorTrend + operationalNoise + valuationPull + rareBigShiftPct) * electionVolatility
            val rawNewPrice = stock.currentPrice * (1.0 + (totalStockPct / 100.0))

            val absoluteSolvencyFloor = maxOf(3.0, initialBase * 0.20)
            val newPrice = maxOf(rawNewPrice, absoluteSolvencyFloor)
            val updatedATH = maxOf(currentATH, newPrice)
            val newHistory = (stock.history + newPrice).takeLast(10)

            val newPE = (cfg.basePE * (newPrice / initialBase)).coerceIn(8.0, 55.0)
            val macroEarningsMult = when (economyCycle) {
                "Boom" -> 1.05
                "Inflation Peak" -> 0.98
                "Recession" -> 0.94
                "Depression" -> 0.90
                else -> 1.0
            }
            val newEarnings = stock.quarterlyEarnings * macroEarningsMult

            if (stock.quantity > 0) {
                val divPayment = stock.quantity * newPrice * stock.weeklyDividendPercent
                val holdingVal = stock.quantity * newPrice
                reportBuilder.append("📈 Stock Dividend: Collected $${String.format("%,.2f", divPayment)} cash dividend from ${stock.quantity} shares of ${stock.name} (Holdings: $${String.format("%,.0f", holdingVal)}).\n")
                addedSalary += divPayment
            }

            stock.copy(
                currentPrice = newPrice,
                history = newHistory,
                PE_Ratio = newPE,
                quarterlyEarnings = newEarnings,
                marketCap = newPrice * 2500000.0,
                sector = cfg.sector,
                financialHealth = cfg.health,
                initialPrice = initialBase,
                allTimeHigh = updatedATH
            )
        }

        // 2. SIMULATE CRYPTO (COINS):
        // Highly volatile, huge shifts (+/-) balanced with sideways periods, hard to predict
        // Best navigated with a high-skilled Crypto Analyst!
        val updatedCryptos = state.cryptos.map { crypto ->
            val spec = cryptoSpecs[crypto.id] ?: CryptoSpec(crypto.currentPrice, crypto.supportLevel, crypto.resistanceLevel, 2, crypto.developerActivity)
            val initialBase = if (crypto.initialPrice > 0.0) crypto.initialPrice else spec.anchor
            val currentATH = maxOf(crypto.allTimeHigh, crypto.currentPrice, initialBase)
            val athMultiplier = currentATH / initialBase

            val hasSurged3x = athMultiplier >= 3.0
            val dynamicCycleFloor = if (hasSurged3x) initialBase * 1.02 else initialBase * 0.50

            val macroBullChance = when (economyCycle) {
                "Boom" -> 0.65f
                "Stable Growth" -> 0.54f
                "Inflation Peak" -> 0.44f
                "Recession" -> 0.38f
                "Depression" -> 0.30f
                else -> 0.50f
            }

            // "little shift happens too but its balances with the huge shift, it can be upward or downward and hard to predict"
            val isLittleShiftWeek = Random.nextFloat() < 0.35f // 35% consolidation / sideways, 65% huge shifts

            val cryptoPctChange: Double = if (isLittleShiftWeek) {
                // Little shift: sideways chop and consolidation
                when (spec.tier) {
                    1 -> Random.nextDouble(-2.5, 3.5)
                    2 -> Random.nextDouble(-3.5, 4.5)
                    else -> Random.nextDouble(-5.0, 6.0)
                }
            } else {
                // Huge shift: violent moves, upward or downward, hard to predict!
                val isUpward = Random.nextFloat() < macroBullChance
                if (isUpward) {
                    when (spec.tier) {
                        1 -> Random.nextDouble(16.0, 40.0)       // BTC: +16% to +40%
                        2 -> Random.nextDouble(25.0, 62.0)       // ETH/SOL: +25% to +62%
                        3 -> Random.nextDouble(32.0, 85.0)       // XRP/ADA/DOT: +32% to +85%
                        else -> Random.nextDouble(45.0, 140.0)   // DOGE/SHIB: +45% to +140%
                    }
                } else {
                    when (spec.tier) {
                        1 -> -Random.nextDouble(14.0, 34.0)      // BTC: -14% to -34%
                        2 -> -Random.nextDouble(20.0, 50.0)      // ETH/SOL: -20% to -50%
                        3 -> -Random.nextDouble(26.0, 60.0)      // XRP/ADA/DOT: -26% to -60%
                        else -> -Random.nextDouble(35.0, 72.0)   // DOGE/SHIB: -35% to -72%
                    }
                }
            }

            var finalCryptoPctChange = cryptoPctChange
            val (softLow, softHigh) = when(crypto.id) {
                "crypto_bit" -> Pair(20000.0, 150000.0)
                "crypto_ether" -> Pair(1500.0, 9000.0)
                "crypto_sol" -> Pair(50.0, 550.0)
                "crypto_ripple" -> Pair(0.40, 3.00)
                "crypto_cardano" -> Pair(0.30, 2.50)
                "crypto_polka" -> Pair(3.0, 50.0)
                "crypto_doge" -> Pair(0.05, 0.65)
                "crypto_shib" -> Pair(0.000005, 0.00009)
                else -> Pair(initialBase * 0.4, initialBase * 6.0)
            }
            if (crypto.currentPrice > softHigh * (Random.nextDouble(1.0, 1.15))) {
                finalCryptoPctChange = -Random.nextDouble(15.0, 45.0)
            } else if (crypto.currentPrice < softLow * (Random.nextDouble(0.85, 1.0))) {
                finalCryptoPctChange = Random.nextDouble(15.0, 50.0)
            }

            val linkedVenture = state.businessVentures.find { it.type == "Crypto Token" && ("crypto_token_" + it.id) == crypto.id }
            val ventureDemandBoost = if (linkedVenture != null) {
                var boost = 0.0
                if (linkedVenture.cryptoNewServiceLaunchBoostWeeks > 0) boost += 6.0
                if (linkedVenture.cryptoActiveUsers > 1000) boost += (linkedVenture.cryptoActiveUsers / 1500.0).coerceAtMost(5.0)
                boost
            } else 0.0

            val effectivePctChange = finalCryptoPctChange + ventureDemandBoost
            val finalVolatility = (effectivePctChange * electionVolatility) / 100.0
            val rawNewPrice = crypto.currentPrice * (1.0 + finalVolatility)
            val absoluteCryptoFloor = maxOf(0.000001, initialBase * 0.12)
            val newPrice = maxOf(rawNewPrice, absoluteCryptoFloor)
            val updatedATH = maxOf(currentATH, newPrice)
            val newHistory = (crypto.history + newPrice).takeLast(10)
            val adjustedQty = if (hackerCryptoLossRatio < 1.0) (crypto.quantity * hackerCryptoLossRatio).coerceAtLeast(0.0) else crypto.quantity

            val newSupport = if (updatedATH >= initialBase * 3.0) initialBase * 1.08 else spec.support
            val newResistance = maxOf(updatedATH * 1.06, newPrice * 1.25)

            // --- PROFESSIONAL CRYPTO ANALYST IMPACT ON USER PORTFOLIO ---
            if (crypto.quantity > 0.0) {
                val singleWeekChange = ((newPrice - crypto.currentPrice) / crypto.currentPrice) * 100.0
                val holdingVal = crypto.quantity * newPrice

                if (singleWeekChange <= -12.0) {
                    // HUGE DOWNWARD SHIFT: Risk mitigation logic
                    if (cryptoAnalyst != null) {
                        // High Skill (>=80%) Analyst protects 50% to 75% of the drop!
                        // Mid Skill (60-79%) protects 25% to 45%
                        // Junior (<60%) protects 8% to 15%
                        val hedgeProtectionRate = when {
                            analystSkill >= 80 -> (0.50 + ((analystSkill - 80) / 20.0) * 0.25).coerceIn(0.50, 0.75)
                            analystSkill >= 60 -> (0.25 + ((analystSkill - 60) / 20.0) * 0.20).coerceIn(0.25, 0.45)
                            else -> 0.10
                        }
                        val nominalLoss = (crypto.currentPrice - newPrice) * crypto.quantity
                        val cashSaved = nominalLoss * hedgeProtectionRate
                        addedCash += cashSaved
                        val skillTierLabel = if (analystSkill >= 80) "Elite High-Skill" else if (analystSkill >= 60) "Professional" else "Junior"
                        reportBuilder.append("🛡️ Crypto Analyst Alpha: ${cryptoAnalyst.name} ($skillTierLabel • $analystSkill% Skill) executed smart on-chain hedging on ${crypto.name}, recovering $${String.format("%,.2f", cashSaved)} from a brutal ${String.format("%.1f", singleWeekChange)}% crash!\n")
                    } else {
                        // No analyst: Player takes the full catastrophic hit unassisted
                        reportBuilder.append("⚡ Crypto Market Crash: ${crypto.name} dumped ${String.format("%.1f", singleWeekChange)}% (Holdings down to $${String.format("%,.2f", holdingVal)}). Trading unassisted without a skilled Crypto Analyst exposed you to 100% of this crash.\n")
                    }
                } else if (singleWeekChange >= 20.0) {
                    // HUGE UPWARD BREAKOUT
                    if (cryptoAnalyst != null && analystSkill >= 80) {
                        // Elite analyst captures extra staking yields & optimal routing
                        val alphaBonus = crypto.quantity * newPrice * (0.04 + ((analystSkill - 80) / 20.0) * 0.04)
                        addedSalary += alphaBonus
                        reportBuilder.append("🚀 Crypto Analyst Alpha: ${cryptoAnalyst.name} (${analystSkill}% Skill) maximized execution on ${crypto.name}'s +${String.format("%.1f", singleWeekChange)}% surge, harvesting $${String.format("%,.2f", alphaBonus)} in bonus yield!\n")
                    } else {
                        reportBuilder.append("🚀 Crypto Breakout: ${crypto.name} skyrocketed +${String.format("%.1f", singleWeekChange)}% to $${String.format("%,.2f", newPrice)}! Holdings: $${String.format("%,.2f", holdingVal)}.\n")
                    }
                }
            }

            val effectiveCirculatingSupply = if (crypto.circulatingSupply > 0.0) crypto.circulatingSupply else if (linkedVenture != null) linkedVenture.cryptoCirculatingSupply else (if (spec.tier == 1) 19500000.0 else 120000000.0)
            val effectiveMcap = newPrice * effectiveCirculatingSupply
            val effectiveVolume24h = if (linkedVenture != null) (linkedVenture.cryptoWeeklyTradingVolume / 7.0).coerceAtLeast(15000.0) else (if (crypto.volume24h > 0.0) crypto.volume24h * Random.nextDouble(0.9, 1.15) else (effectiveMcap * 0.05))
            val effectiveDevActivity = if (linkedVenture != null) {
                if (linkedVenture.cryptoDevelopersCount >= 2) "High" else "Medium"
            } else spec.dev

            crypto.copy(
                quantity = adjustedQty,
                currentPrice = newPrice,
                history = newHistory,
                supportLevel = newSupport,
                resistanceLevel = newResistance,
                developerActivity = effectiveDevActivity,
                marketCap = effectiveMcap,
                volume24h = effectiveVolume24h,
                circulatingSupply = effectiveCirculatingSupply,
                initialPrice = initialBase,
                allTimeHigh = updatedATH,
                cycleFloor = dynamicCycleFloor
            )
        }

        // Exchange Limit & S&L Orders Execution
        val remainingStockOrders = mutableListOf<StockOrder>()
        val stocksModifiedMap = updatedStocks.associateBy { it.id }.toMutableMap()
        var currentWorkingCash = state.cash

        state.pendingStockOrders.forEach { order ->
            val stock = stocksModifiedMap[order.stockId]
            if (stock != null) {
                var executed = false
                if (order.type == "LIMIT_BUY" && stock.currentPrice <= order.targetPrice) {
                    val cost = stock.currentPrice * order.quantity
                    if (currentWorkingCash >= cost) {
                        currentWorkingCash -= cost
                        stocksModifiedMap[order.stockId] = stock.copy(quantity = stock.quantity + order.quantity)
                        reportBuilder.append("🔔 Stock Exchange: LIMIT BUY triggered! Bought ${order.quantity} shares of ${stock.name} at $${String.format("%.2f", stock.currentPrice)}/share.\n")
                        executed = true
                    }
                } else if (order.type == "LIMIT_SELL" && stock.currentPrice >= order.targetPrice) {
                    if (stock.quantity >= order.quantity) {
                        val revenue = stock.currentPrice * order.quantity
                        addedSalary += revenue
                        stocksModifiedMap[order.stockId] = stock.copy(quantity = stock.quantity - order.quantity)
                        reportBuilder.append("🔔 Stock Exchange: LIMIT SELL triggered! Sold ${order.quantity} shares of ${stock.name} at $${String.format("%.2f", stock.currentPrice)}/share.\n")
                        executed = true
                    }
                }
                if (!executed) remainingStockOrders.add(order)
            }
        }
        val finalStocks = updatedStocks.map { stocksModifiedMap[it.id] ?: it }

        val remainingCryptoOrders = mutableListOf<CryptoOrder>()
        val cryptosModifiedMap = updatedCryptos.associateBy { it.id }.toMutableMap()

        state.pendingCryptoOrders.forEach { order ->
            val crypto = cryptosModifiedMap[order.cryptoId]
            if (crypto != null) {
                var executed = false
                val price = crypto.currentPrice
                if (order.type == "LIMIT_BUY" && price <= order.targetPrice) {
                    val cost = price * order.quantity
                    if (currentWorkingCash >= cost) {
                        currentWorkingCash -= cost
                        cryptosModifiedMap[order.cryptoId] = crypto.copy(quantity = crypto.quantity + order.quantity)
                        reportBuilder.append("🔔 Genesis Exchange: LIMIT BUY triggered! Bought ${String.format("%.4f", order.quantity)} of ${crypto.name} at $${String.format("%.2f", price)}.\n")
                        executed = true
                    }
                } else if (order.type == "LIMIT_SELL" && price >= order.targetPrice) {
                    if (crypto.quantity >= order.quantity) {
                        val revenue = price * order.quantity
                        addedSalary += revenue
                        cryptosModifiedMap[order.cryptoId] = crypto.copy(quantity = crypto.quantity - order.quantity)
                        reportBuilder.append("🔔 Genesis Exchange: LIMIT SELL triggered! Sold ${String.format("%.4f", order.quantity)} of ${crypto.name} at $${String.format("%.2f", price)}.\n")
                        executed = true
                    }
                } else if (order.type == "STOP_LOSS" && price <= order.targetPrice) {
                    if (crypto.quantity >= order.quantity) {
                        val revenue = price * order.quantity
                        addedSalary += revenue
                        cryptosModifiedMap[order.cryptoId] = crypto.copy(quantity = crypto.quantity - order.quantity)
                        reportBuilder.append("🚨 Genesis Exchange: STOP LOSS triggered! Sold ${String.format("%.4f", order.quantity)} of ${crypto.name} at $${String.format("%.2f", price)} to limit loss.\n")
                        executed = true
                    }
                } else if (order.type == "TAKE_PROFIT" && price >= order.targetPrice) {
                    if (crypto.quantity >= order.quantity) {
                        val revenue = price * order.quantity
                        addedSalary += revenue
                        cryptosModifiedMap[order.cryptoId] = crypto.copy(quantity = crypto.quantity - order.quantity)
                        reportBuilder.append("💰 Genesis Exchange: TAKE PROFIT triggered! Sold ${String.format("%.4f", order.quantity)} of ${crypto.name} at $${String.format("%.2f", price)}.\n")
                        executed = true
                    }
                }
                if (!executed) remainingCryptoOrders.add(order)
            }
        }
        val finalCryptos = updatedCryptos.map { cryptosModifiedMap[it.id] ?: it }
        val netCashDifference = currentWorkingCash - state.cash
        addedCash += netCashDifference

        return MarketSimulationResult(
            updatedStocks = finalStocks,
            updatedCryptos = finalCryptos,
            remainingStockOrders = remainingStockOrders,
            remainingCryptoOrders = remainingCryptoOrders,
            addedCash = addedCash,
            addedSalary = addedSalary
        )
    }
}
