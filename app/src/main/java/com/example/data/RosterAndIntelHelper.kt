package com.example.data

import java.util.UUID
import kotlin.random.Random

data class RosterAndIntelResult(
    val updatedCandidateRoster: List<Employee>,
    val finalStocks: List<Stock>,
    val finalCryptos: List<Crypto>
)

object RosterAndIntelHelper {

    fun processRosterAndMarketIntel(
        state: GameStateEntity,
        currentStocks: List<Stock>,
        currentCryptos: List<Crypto>,
        updatedMail: MutableList<MailMessage>
    ): RosterAndIntelResult {
        // Update Candidate Roster weekly (ratings and salaries fluctuate smoothly with the market)
        val baseCandidates = if (state.candidateRoster.isEmpty() || state.candidateRoster.any { it.weeklySalary < 600.0 }) {
            GameData.generateRealisticCandidateRoster()
        } else {
            state.candidateRoster
        }
        val updatedCandidateRoster = baseCandidates.map { emp ->
            val ratingDelta = (Random.nextFloat() * 0.1f - 0.05f)
            val newRating = (emp.rating + ratingDelta).coerceIn(1.5f, 5.0f)
            val priceScale = newRating / emp.rating
            val isFinancialRole = emp.role.contains("Crypto", ignoreCase = true) || emp.role.contains("Accountant", ignoreCase = true)
            val newSalary = if (isFinancialRole) {
                if (newRating >= 4.0f) (emp.weeklySalary * priceScale).coerceIn(18500.0, 50000.0)
                else (emp.weeklySalary * priceScale).coerceIn(1100.0, 5800.0)
            } else {
                (emp.weeklySalary * priceScale).coerceIn(800.0, 35000.0)
            }
            emp.copy(rating = newRating, weeklySalary = newSalary)
        }

        var finalStocks = currentStocks
        var finalCryptos = currentCryptos

        // Crypto Analyst Predictive Intelligence & Opportunistic Cheap Detection
        // The best way to invest in crypto is with a professional analyst with high skills!
        val cryptoAnalyst = state.hiredEmployees.find {
            it.role.equals("Crypto Analyst", ignoreCase = true) ||
            (it.role.contains("Crypto", ignoreCase = true) && it.role.contains("Analyst", ignoreCase = true)) ||
            (it.role.contains("Analyst", ignoreCase = true) && !it.role.contains("Financial", ignoreCase = true))
        }

        if (cryptoAnalyst != null) {
            val analystSkill = cryptoAnalyst.skillRatingPercent
            val isHighSkill = analystSkill >= 80
            val accuracyThreshold = when {
                analystSkill >= 90 -> 0.86f // Elite Master (90-99%): 86% directional accuracy
                analystSkill >= 80 -> 0.80f // High Skill (80-89%): 80% directional accuracy
                analystSkill >= 60 -> 0.68f // Mid-Level Specialist (60-79%): 68% accuracy
                else -> 0.52f               // Junior / Apprentice (<60%): 52% (near random)
            }

            val eligibleCheap = finalCryptos.mapNotNull { c ->
                val recentPeak = (c.history.takeLast(6) + c.currentPrice).maxOrNull() ?: c.currentPrice
                val pullback = if (recentPeak > 0.0) (recentPeak - c.currentPrice) / recentPeak else 0.0
                if (pullback in 0.18..0.42 && c.developerActivity == "High" && c.currentPrice >= c.supportLevel * 0.90) {
                    c to pullback
                } else null
            }
            val cheapCoinId = eligibleCheap.maxByOrNull { it.second }?.first?.id

            finalCryptos = finalCryptos.map { c ->
                val isCheap = (c.id == cheapCoinId)
                val trueBullish = (c.currentPrice <= c.supportLevel * 1.10) || isCheap || (state.economyCycle in listOf("Boom", "Stable Growth") && Random.nextFloat() < 0.62f)
                val isAccurate = Random.nextFloat() < accuracyThreshold
                val predictedDir = if (isAccurate) {
                    if (trueBullish) "RISE" else "FALL"
                } else {
                    if (trueBullish) "FALL" else "RISE"
                }
                val conf = when {
                    analystSkill >= 80 -> if (isAccurate) Random.nextInt(80, 95) else Random.nextInt(70, 82)
                    analystSkill >= 60 -> if (isAccurate) Random.nextInt(68, 80) else Random.nextInt(60, 72)
                    else -> Random.nextInt(52, 65)
                }
                val reason = when {
                    isCheap -> "Substantial ${(eligibleCheap.find { it.first.id == c.id }?.second?.times(100))?.toInt() ?: 24}% pullback from local peak · High developer commits indicate prime accumulation"
                    predictedDir == "RISE" && c.currentPrice <= c.supportLevel * 1.10 -> "Defending key $${String.format("%,.2f", c.supportLevel)} support shelf · On-chain whale accumulation detected"
                    predictedDir == "RISE" -> "Bullish on-chain liquidity divergence · Net exchange outflows project upcoming upward breakout"
                    c.currentPrice >= c.resistanceLevel * 0.90 -> "Approaching overhead $${String.format("%,.2f", c.resistanceLevel)} resistance · Overbought indicators favor sudden downward liquidation"
                    else -> "Macro cycle headwinds (${state.economyCycle}) · High likelihood of sharp downward volatility"
                }
                c.copy(analystPrediction = predictedDir, predictionConfidence = conf, predictionReason = reason, isCheapOpportunity = isCheap)
            }

            val forecastLines = finalCryptos.take(4).map { c ->
                val icon = if (c.analystPrediction == "RISE") "▲" else "▼"
                val cheapTag = if (c.isCheapOpportunity) " [🎯 RARE CHEAP ENTRY]" else ""
                "• **${c.name}**: $icon **${c.analystPrediction}** (${c.predictionConfidence}% confidence)$cheapTag — ${c.predictionReason}"
            }
            val cheapStatusNote = if (cheapCoinId != null) {
                val cObj = finalCryptos.find { it.id == cheapCoinId }
                "🎯 **High-Conviction Value Opportunity Identified**: ${cObj?.name} at $${String.format("%,.4f", cObj?.currentPrice ?: 0.0)}. Downside risk is hedged via on-chain smart derivatives."
            } else {
                "🛡️ **Risk Discipline Note**: No coins met our strict 'Cheap Opportunity' criteria this week. Crypto exhibits massive unpredictable shifts (+85% / -60%); we prioritize capital preservation and downside hedging."
            }
            val skillTierLabel = when {
                analystSkill >= 90 -> "Elite Master ($analystSkill% Skill • ${(accuracyThreshold * 100).toInt()}% Win Rate)"
                analystSkill >= 80 -> "High Skill Professional ($analystSkill% Skill • ${(accuracyThreshold * 100).toInt()}% Win Rate)"
                analystSkill >= 60 -> "Specialist ($analystSkill% Skill • ${(accuracyThreshold * 100).toInt()}% Win Rate)"
                else -> "Junior Analyst ($analystSkill% Skill • ${(accuracyThreshold * 100).toInt()}% Win Rate)"
            }
            val cryptoIntelEmail = MailMessage(
                id = "crypto_forecast_" + UUID.randomUUID().toString().take(6),
                sender = "${cryptoAnalyst.name} (Crypto Analyst)",
                subject = "🧠 Crypto Market Intel & Volatility Projections",
                body = "Boss,\n\nI have finalized our on-chain market projections for the upcoming shifts. Operating as your $skillTierLabel, our models detect key liquidity flows:\n\n${forecastLines.joinToString("\n\n")}\n\n$cheapStatusNote\n\n*Downside Hedging Active: If any of our holdings experience a crash (-12% or worse), I will deploy tactical on-chain hedging to recover 50%–75% of the loss.*\n\nBest,\n${cryptoAnalyst.name}",
                type = "NORMAL",
                expiresWeek = state.currentWeek + 3,
                expiresYear = state.currentYear
            )
            updatedMail.add(cryptoIntelEmail)
        } else {
            // No analyst hired: set unassisted notice on coins
            finalCryptos = finalCryptos.map { c ->
                c.copy(
                    analystPrediction = "UNASSISTED",
                    predictionConfidence = 0,
                    predictionReason = "⚠️ No Crypto Analyst hired. Crypto has huge unpredictable shifts (+85% / -60%). Hire a professional analyst with high skills to unlock directional forecasts and crash hedging.",
                    isCheapOpportunity = false
                )
            }
        }

        // Stock 70/30 Trend Predictions and Dislocated Value Detection
        val eliteAccountant = state.hiredEmployees.find {
            (it.role.contains("Accountant", ignoreCase = true) || it.role.contains("Financial", ignoreCase = true)) &&
            it.skillRatingPercent >= 80
        } ?: state.hiredEmployees.find { it.role.contains("Accountant", ignoreCase = true) || it.role.contains("Financial", ignoreCase = true) }

        if (eliteAccountant != null) {
            val eligibleCheapStocks = finalStocks.mapNotNull { s ->
                val recentPeak = (s.history.takeLast(6) + s.currentPrice).maxOrNull() ?: s.currentPrice
                val pullback = if (recentPeak > 0.0) (recentPeak - s.currentPrice) / recentPeak else 0.0
                if (pullback in 0.12..0.30 && s.financialHealth in listOf("Rock Solid", "Strong") && s.PE_Ratio <= 17.0) {
                    s to pullback
                } else null
            }
            val cheapStockId = eligibleCheapStocks.maxByOrNull { it.second }?.first?.id

            finalStocks = finalStocks.map { s ->
                val isCheap = (s.id == cheapStockId)
                val trueBullish = (s.PE_Ratio <= 14.0) || isCheap || (state.economyCycle in listOf("Boom", "Stable Growth") && Random.nextFloat() < 0.65f)
                val isAccurate = Random.nextFloat() < 0.70f
                val predictedDir = if (isAccurate) (if (trueBullish) "RISE" else "FALL") else (if (trueBullish) "FALL" else "RISE")
                val conf = if (isAccurate) Random.nextInt(72, 88) else Random.nextInt(66, 78)
                val reason = when {
                    isCheap -> "Earnings overreaction pullback (${(eligibleCheapStocks.find { it.first.id == s.id }?.second?.times(100))?.toInt() ?: 18}%) · PE compressed to ${String.format("%.1f", s.PE_Ratio)} creates deep margin of safety"
                    predictedDir == "RISE" -> "Strong balance sheet · Institutional dividend reinvestment projected over next 1–2 shifts"
                    else -> "Valuation expansion reached upper band (${String.format("%.1f", s.PE_Ratio)} PE) · Expecting minor price retracement"
                }
                s.copy(analystPrediction = predictedDir, predictionConfidence = conf, predictionReason = reason, isCheapOpportunity = isCheap)
            }

            val forecastLines = finalStocks.take(4).map { s ->
                val icon = if (s.analystPrediction == "RISE") "▲" else "▼"
                val cheapTag = if (s.isCheapOpportunity) " [🎯 VALUE BUY]" else ""
                "• **${s.name}**: $icon **${s.analystPrediction}** (${s.predictionConfidence}% confidence)$cheapTag — ${s.predictionReason}"
            }
            val forecastEmail = MailMessage(
                id = "market_forecast_" + UUID.randomUUID().toString().take(6),
                sender = "${eliteAccountant.name} (Accountant & Equity Analyst)",
                subject = "📊 Professional 4-Week Stock Market Forecast (70/30 Edge)",
                body = "Hi Boss,\n\nHere is our econometric forecast for the next 1–2 shifts based on fundamental balance sheets and cycle momentum (${state.economyCycle}):\n\n${forecastLines.joinToString("\n\n")}\n\n*With 80%+ rating and Stubborn Trait enabled, I monitor our stop-loss positions closely and will refuse bottom liquidations when a fast rebound is statistically favored.*\n\nBest,\n${eliteAccountant.name}",
                type = "NORMAL",
                expiresWeek = state.currentWeek + 3,
                expiresYear = state.currentYear
            )
            updatedMail.add(forecastEmail)
        }

        return RosterAndIntelResult(
            updatedCandidateRoster = updatedCandidateRoster,
            finalStocks = finalStocks,
            finalCryptos = finalCryptos
        )
    }
}
