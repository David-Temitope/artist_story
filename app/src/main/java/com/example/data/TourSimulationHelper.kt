package com.example.data

import kotlin.random.Random

data class TourSimulationResult(
    val updatedTours: List<Tour>,
    val grossTourPayout: Double,
    val netTourPayout: Double,
    val completedToursCount: Int
)

object TourSimulationHelper {

    fun processWeeklyTours(
        state: GameStateEntity,
        economyCycle: String,
        eventEffect: String?,
        weatherCondition: String,
        reportBuilder: StringBuilder
    ): TourSimulationResult {
        var tourPayout = 0.0
        var completedToursCount = 0

        val updatedTours = state.tours.map { tour ->
            if (tour.status == "DRAFT") {
                tour
            } else {
                val nextStatus = when {
                    tour.status == "BOOKED" && (state.currentYear > tour.bookedYear || (state.currentYear == tour.bookedYear && state.currentWeek >= tour.bookedWeek)) -> "ACTIVE"
                    tour.status == "ACTIVE" && tour.currentWeek >= tour.totalWeeks -> "COMPLETED"
                    else -> tour.status
                }

                if (nextStatus == "ACTIVE") {
                    val currentWeekPlus = tour.currentWeek + 1
                    val baseMaxAtt = when (tour.tourType) {
                        "Global Tour" -> 50000
                        "National Tour" -> 12500
                        "City Tour" -> 2800
                        "Club Run" -> 600
                        else -> when (tour.level) {
                            "Simple" -> 1000
                            "Pro" -> 10000
                            "Premium" -> 100000
                            else -> 1000
                        }
                    }

                    // Tour Type Ticket Price Ranges:
                    // Club Run: $1 - $6 (fair: $3.50)
                    // City Tour: $5 - $18 (fair: $11.0)
                    // National Tour: $20 - $50 (fair: $32.0)
                    // Global Tour: $50 - $150 (fair: $85.0)
                    val fairPrice = when (tour.tourType) {
                        "Global Tour" -> 85.0 * (1.0 + state.inflationRate * 2.0)
                        "National Tour" -> 32.0 * (1.0 + state.inflationRate * 2.0)
                        "City Tour" -> 11.0 * (1.0 + state.inflationRate * 2.0)
                        "Club Run" -> 3.5 * (1.0 + state.inflationRate * 2.0)
                        else -> when (tour.level) {
                            "Simple" -> 3.5 * (1.0 + state.inflationRate * 2.0)
                            "Pro" -> 32.0 * (1.0 + state.inflationRate * 2.0)
                            "Premium" -> 85.0 * (1.0 + state.inflationRate * 2.0)
                            else -> 11.0
                        }
                    }

                    val maxPriceCap = when (tour.tourType) {
                        "Global Tour" -> 150.0
                        "National Tour" -> 50.0
                        "City Tour" -> 18.0
                        "Club Run" -> 6.0
                        else -> 50.0
                    }

                    val effectiveAvgPrice = tour.weightedAvgTicketPrice.coerceAtLeast(1.0)
                    val priceRatio = effectiveAvgPrice / fairPrice

                    // Higher than maxPriceCap leads to very low audience turnout!
                    val pricePenalty = if (effectiveAvgPrice > maxPriceCap) {
                        val excessRatio = effectiveAvgPrice / maxPriceCap
                        (1.0 / (excessRatio * excessRatio * excessRatio)).coerceIn(0.04, 0.25).toFloat()
                    } else 1.0f

                    val highestTier = maxOf(tour.generalTicketPrice, tour.vipTicketPrice, tour.platinumTicketPrice)
                    val tierOverpricedPenalty = if (highestTier > maxPriceCap) {
                        val excess = highestTier / maxPriceCap
                        (1.0 / (excess * excess)).coerceIn(0.10, 0.50).toFloat()
                    } else 1.0f

                    if (effectiveAvgPrice > maxPriceCap || highestTier > maxPriceCap) {
                        reportBuilder.append("⚠ OVERPRICED TICKETS: Average/Tier ticket prices ($${effectiveAvgPrice.toInt()}/$${highestTier.toInt()}) exceeded the max acceptable cap of $${maxPriceCap.toInt()} for ${tour.tourType}! High prices triggered severe fan boycotts and collapsed attendance.\n")
                    }

                    val basePriceMultiplier = if (priceRatio > 1.0) {
                        (1.0 / (priceRatio * priceRatio)).toFloat()
                    } else {
                        (1.0f + (1.0f - priceRatio.toFloat()) * 0.3f)
                    }
                    val priceMultiplier = basePriceMultiplier * pricePenalty * tierOverpricedPenalty

                    // Regional audience pool constraint: To fill a 12.5k or 50k venue, an artist needs substantial total reach
                    val regionalAudiencePool = when (tour.tourType) {
                        "Club Run" -> (state.fans * 0.15).toInt().coerceAtLeast(180)
                        "City Tour" -> (state.fans * 0.08).toInt().coerceAtLeast(350)
                        "National Tour" -> (state.fans * 0.035).toInt().coerceAtLeast(500)
                        "Global Tour" -> (state.fans * 0.018).toInt().coerceAtLeast(800)
                        else -> (state.fans * 0.05).toInt().coerceAtLeast(250)
                    }
                    val effectiveVenueCap = minOf(baseMaxAtt, regionalAudiencePool)

                    // Energy impact on performance
                    val energyPerformanceBonus = when {
                        state.energy >= 70 -> 1.15f
                        state.energy >= 40 -> 1.0f
                        else -> 0.75f
                    }

                    // Season multiplier
                    val seasonMult = when (state.currentWeek) {
                        in 20..35 -> { // Summer
                            if (tour.level == "Premium" || tour.tourType == "Global Tour") 1.25f else 1.0f
                        }
                        in 48..52, in 1..5 -> { // Winter Holiday
                            if (tour.level == "Premium" || tour.tourType == "Global Tour") 0.75f else if (tour.tourType == "City Tour") 1.15f else 0.85f
                        }
                        else -> 1.0f
                    }

                    // Tour Hype multiplier & Crew Impacts
                    val crewHypeBonus = (if (tour.selectedCrewRoles.contains("Dancers")) 10f else 0f) +
                            (if (tour.selectedCrewRoles.contains("Instrumentalist")) 8f else 0f)
                    val hypeMult = 0.5f + ((tour.showHype + crewHypeBonus) / 50f)

                    val audioEngineerBonus = if (tour.selectedCrewRoles.contains("Audio Engineer")) 1.18f else 1.0f
                    val openingActBonus = if (!tour.openingActName.isNullOrEmpty()) 1.20f else 1.0f
                    val closingActBonus = if (!tour.closingActName.isNullOrEmpty()) 1.25f else 1.0f

                    val promoEffect = (tour.promotionBudget / 1000.0).coerceAtMost(2.0)
                    val socialPromoBonus = if (tour.isPromotedOnSocial) 1.25f else 1.0f
                    val newsPromoBonus = if (tour.isPromotedOnNews) 1.5f else 1.0f

                    val popularityFactor = state.popularity / 100f
                    val influenceFactor = state.influence / 100f

                    // Global event & economic cycle modifiers for tours
                    val economyTourMult = when (economyCycle) {
                        "Boom" -> 1.4f
                        "Recession" -> 0.65f
                        "Depression" -> 0.45f
                        else -> 1.0f
                    }

                    val eventTourMult = when (eventEffect) {
                        "LOCKDOWN" -> 0.0f // Tours canceled/suspended! Zero attendance.
                        "RECESSION" -> 0.7f // People have less disposable income.
                        "FESTIVAL" -> 1.4f // Concert hype is sky-high!
                        else -> 1.0f
                    }

                    // Weather modifiers
                    val weatherTourMult = when (weatherCondition) {
                        "Rainy" -> 0.85f
                        "Heavy Storm" -> 0.55f
                        else -> 1.0f
                    }

                    // Superstar Event Collision System
                    val collisionRand = Random.nextFloat()
                    val hasCollision = collisionRand < 0.12f // 12% weekly chance
                    val collisionMult = if (hasCollision && eventEffect != "LOCKDOWN") {
                        reportBuilder.append("⚠ CONCERT COLLISION: A rival superstar held an unannounced festival in your tour market this week! Divided local media coverage.\n")
                        0.65f
                    } else 1.0f

                    // Public opinion / reputation impact on tour attendance
                    val reputationTourMult = when {
                        state.publicImage < 40f -> 0.55f
                        state.publicImage > 75f -> 1.35f
                        else -> 1.0f
                    }

                    val attVal = (effectiveVenueCap * (popularityFactor * 0.5 + influenceFactor * 0.3 + promoEffect * 0.2) *
                            socialPromoBonus * newsPromoBonus * eventTourMult * economyTourMult * weatherTourMult *
                            collisionMult * reputationTourMult * priceMultiplier * seasonMult * hypeMult *
                            audioEngineerBonus * openingActBonus * closingActBonus * energyPerformanceBonus).toInt()
                    val currentMarket = tour.routeMarkets
                        .takeIf { it.isNotEmpty() }
                        ?.getOrNull(tour.currentWeek.coerceAtLeast(0) % tour.routeMarkets.size)
                    val competingArtist = currentMarket?.let { market ->
                        state.npcs
                            .filter { npc -> !npc.isRetired && npc.isOnTour && npc.tourMarkets.any { tourMarketsOverlap(market, it) } }
                            .maxByOrNull { it.fans }
                    }
                    val playerAudienceShare = competingArtist?.let {
                        playerTourAudienceShare(state.fans, it.fans)
                    } ?: 1f
                    val finalAttendance = (attVal * playerAudienceShare).toInt().coerceIn(0, baseMaxAtt)
                    if (competingArtist != null) {
                        val playerSharePercent = (playerAudienceShare * 100).toInt()
                        val rivalSharePercent = 100 - playerSharePercent
                        val competitionSummary = when {
                            playerSharePercent > rivalSharePercent -> "Your larger audience claims $playerSharePercent% of local demand."
                            playerSharePercent < rivalSharePercent -> "${competingArtist.name} captures $rivalSharePercent% of local demand; your show gets $playerSharePercent%."
                            else -> "The market is evenly split at $playerSharePercent% each."
                        }
                        reportBuilder.append(
                            "⚠ TOUR MARKET COMPETITION: ${competingArtist.name} is touring in $currentMarket with ${String.format("%,d", competingArtist.fans)} fans. " +
                                "$competitionSummary Your attendance is $finalAttendance.\n"
                        )
                    }

                    // 3-Tier Ticket Breakdown (70% General, 25% VIP, 5% Platinum)
                    val genTickets = (finalAttendance * 0.70).toInt()
                    val vipTickets = (finalAttendance * 0.25).toInt()
                    val platTickets = finalAttendance - genTickets - vipTickets

                    val effGenPrice = if (tour.generalTicketPrice > 0.0) tour.generalTicketPrice else tour.ticketPrice * 0.8
                    val effVipPrice = if (tour.vipTicketPrice > 0.0) tour.vipTicketPrice else tour.ticketPrice * 1.8
                    val effPlatPrice = if (tour.platinumTicketPrice > 0.0) tour.platinumTicketPrice else tour.ticketPrice * 3.5

                    val grossTourRevenue = (genTickets * effGenPrice) + (vipTickets * effVipPrice) + (platTickets * effPlatPrice)

                    // Realistic Economics: Venue & Promoter Cuts (Tour Manager negotiates down from 35% to 20%!)
                    val hasTourManager = tour.selectedCrewRoles.contains("Tour Manager")
                    val promoterCutRate = if (hasTourManager) 0.20f else (Random.nextFloat() * 0.12f + 0.28f)
                    val venueAndPromoterCut = grossTourRevenue * promoterCutRate

                    // Realistic Economics: Touring Expenses (Travel, Crew, Production, Hotels, Gear)
                    val fixedTourCosts = when (tour.tourType) {
                        "Global Tour" -> 60000.0
                        "National Tour" -> 18000.0
                        "City Tour" -> 5000.0
                        "Club Run" -> 1200.0
                        else -> when (tour.level) {
                            "Simple" -> 1500.0
                            "Pro" -> 15000.0
                            "Premium" -> 150000.0
                            else -> 1000.0
                        }
                    }
                    val variableExpenses = finalAttendance * (effectiveAvgPrice * 0.28)
                    val totalTourExpenses = venueAndPromoterCut + fixedTourCosts + variableExpenses

                    var netTourEarnings = grossTourRevenue - totalTourExpenses
                    if (netTourEarnings < 0) {
                        netTourEarnings = 0.0 // Minimum guarantee protects artist from negative box office settlement
                    }

                    // Tour Merch Stand Sales
                    var weeklyTourMerchRevenue = 0.0
                    var tourMerchUnitsSold = 0
                    if (tour.selectedMerchIds.isNotEmpty() && state.merchStyles.isNotEmpty()) {
                        val buyersCount = (finalAttendance * 0.22).toInt() // 22% of venue attendees buy merch
                        if (buyersCount > 0) {
                            val tourMerchItems = state.merchStyles.filter { it.id in tour.selectedMerchIds && it.stockRemaining > 0 }
                            if (tourMerchItems.isNotEmpty()) {
                                val unitsPerItem = (buyersCount / tourMerchItems.size).coerceAtLeast(1)
                                tourMerchItems.forEach { style ->
                                    val sold = unitsPerItem.coerceAtMost(style.stockRemaining)
                                    weeklyTourMerchRevenue += sold * style.sellPrice
                                    tourMerchUnitsSold += sold
                                }
                            }
                        }
                    }

                    val labelTouringMult = if (state.ownLabel != null) 1.0 + (state.ownLabel.touringLevel * 0.05) else 1.0
                    val finalEarnings = (netTourEarnings + weeklyTourMerchRevenue) * labelTouringMult
                    tourPayout += finalEarnings

                    // Crew Live Perks & Events
                    if (tour.selectedCrewRoles.contains("Photographer & Videographer")) {
                        reportBuilder.append("📸 Tour Media: Photographer's 4K concert reels went viral on MeTube! Gained +300 new fans.\n")
                    }
                    if (tour.selectedCrewRoles.contains("Wardrobe Stylist")) {
                        reportBuilder.append("✨ Tour Styling: Stylist's stage wardrobe spotlighted in major fashion magazines! (+Public Image)\n")
                    }
                    if (!tour.selectedCrewRoles.contains("Head of Security") && Random.nextFloat() < 0.08f) {
                        reportBuilder.append("⚠ Backstage Security Issue: Stage crasher delayed encore; $650 security fine incurred.\n")
                    }

                    if (eventEffect == "LOCKDOWN") {
                        reportBuilder.append("🎤 Tour (${tour.title} - Week $currentWeekPlus/${tour.totalWeeks}): Suspended due to Pandemic Lockdown! 0 attendees.\n")
                    } else {
                        reportBuilder.append("🎤 Tour (${tour.title} - Stop $currentWeekPlus/${tour.totalWeeks}): $finalAttendance fans packed the venue! [Gen: $genTickets, VIP: $vipTickets, Plat: $platTickets]\n")
                        if (finalAttendance > 0) {
                            reportBuilder.append("   Gross Box Office: $${String.format("%,.2f", grossTourRevenue)}\n")
                            reportBuilder.append("   Venue/Promoter Cut: $${String.format("%,.2f", venueAndPromoterCut)} | Operational Expenses: $${String.format("%,.2f", fixedTourCosts + variableExpenses)}\n")
                            if (weeklyTourMerchRevenue > 0.0) {
                                reportBuilder.append("   👕 Concert Merch Booth: Sold $tourMerchUnitsSold items at venue stands for $${String.format("%,.2f", weeklyTourMerchRevenue)}!\n")
                            }
                            reportBuilder.append("   Net Tour Profit: $${String.format("%,.2f", finalEarnings)}.\n")
                        }
                    }

                    tour.copy(
                        currentWeek = currentWeekPlus,
                        baseAttendance = finalAttendance.coerceAtLeast(tour.baseAttendance),
                        totalTicketsSold = tour.totalTicketsSold + finalAttendance,
                        totalGrossRevenue = tour.totalGrossRevenue + grossTourRevenue,
                        totalMerchRevenueEarned = tour.totalMerchRevenueEarned + weeklyTourMerchRevenue,
                        status = if (currentWeekPlus >= tour.totalWeeks) "COMPLETED" else "ACTIVE"
                    )
                } else if (tour.status == "BOOKED") {
                    tour
                } else {
                    if (tour.status == "ACTIVE" && nextStatus == "COMPLETED") completedToursCount++
                    tour.copy(status = nextStatus)
                }
            }
        }

        val labelCut = if (state.signedLabelId != null && tourPayout > 0.0) tourPayout * state.signedLabelCut else 0.0
        val netTourPayout = tourPayout - labelCut
        if (labelCut > 0.0) {
            reportBuilder.append("📋 Label took $${String.format("%,.2f", labelCut)} cut from tour earnings.\n")
        }

        return TourSimulationResult(
            updatedTours = updatedTours,
            grossTourPayout = tourPayout,
            netTourPayout = netTourPayout,
            completedToursCount = completedToursCount
        )
    }
}
