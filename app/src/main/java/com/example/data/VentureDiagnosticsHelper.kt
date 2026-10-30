package com.example.data

import java.util.UUID
import kotlin.math.roundToInt
import kotlin.random.Random

data class OperationalBottleneck(
    val title: String,
    val description: String,
    val severity: String, // "CRITICAL", "WARNING", "OPPORTUNITY", "HEALTHY"
    val actionRequired: String,
    val specificStaffNeeded: Map<String, Int> = emptyMap(),
    val premisesUpgradeRecommended: Boolean = false,
    val recommendedPremisesTier: String = ""
)

data class VentureDiagnosticsResult(
    val summaryTitle: String,
    val overallHealth: String, // "GOOD", "NEEDS_ATTENTION", "CRITICAL_BOTTLENECK"
    val bottlenecks: List<OperationalBottleneck>,
    val managerName: String,
    val managerRole: String,
    val quickRecommendations: List<String>
)

object VentureDiagnosticsHelper {

    fun getManagerInfo(venture: BusinessVenture): Pair<String, String> {
        return when (venture.type) {
            "Restaurant" -> Pair("Helena Rossi", "Executive General Manager")
            "Taxi", "Taxi Fleet" -> Pair("Marcus Vance", "Fleet Operations Director")
            "Farming" -> Pair("Ezekiel Miller", "Chief Agronomist & Farm Superintendent")
            "Hotel", "Hotel Chain" -> Pair("Victoria Sterling", "Resident Managing Director")
            "Crypto Token" -> Pair("Alex Chen", "Head of DAO & Core Contributor Operations")
            else -> Pair("Julian Croft", "Enterprise Operations Director")
        }
    }

    fun analyzeVenture(venture: BusinessVenture): VentureDiagnosticsResult {
        val (mgrName, mgrRole) = getManagerInfo(venture)
        val bottlenecks = mutableListOf<OperationalBottleneck>()
        val recommendations = mutableListOf<String>()

        when (venture.type) {
            "Restaurant" -> {
                val tierDetail = VentureSimulationHelper.RESTAURANT_TIER_DETAILS.find { it.tierLevel == venture.tierLevel }
                    ?: VentureSimulationHelper.RESTAURANT_TIER_DETAILS.first()

                val recurring = venture.recurringCustomers
                val wCount = venture.waiterCount
                val cCount = venture.chefCount
                val scCount = venture.seniorChefCount
                val clCount = venture.cleanerCount
                val mCount = venture.managerCount

                // 1. Physical Seating vs Recurring Customers
                val seatingCapacity = tierDetail.maxDailyCapacity
                if (venture.tierLevel == 1 && recurring >= 60) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "🚨 Severe Seating Shortage: Food Truck vs $recurring Recurring Diners",
                            description = "Your Food Truck has zero physical seating! With $recurring recurring patrons arriving weekly, customers have nowhere to sit and eat. Many walk away hungry and angry, abandoning your restaurant.",
                            severity = "CRITICAL",
                            actionRequired = "Upgrade premises tier to Casual Bistro or Diner to unlock indoor seating tables!",
                            premisesUpgradeRecommended = true,
                            recommendedPremisesTier = "Casual Bistro / Diner"
                        )
                    )
                    recommendations.add("Premises Overcrowding: Upgrade tier from Food Truck to provide seating for $recurring diners.")
                } else if (recurring > seatingCapacity * 2) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "⚠️ Dining Room Overcrowded: $recurring Diners vs $seatingCapacity Capacity",
                            description = "Customer demand is double your current floor space. Lineups out the door cause patrons to seek alternatives.",
                            severity = "WARNING",
                            actionRequired = "Upgrade premises tier to increase dining room square footage and seat count.",
                            premisesUpgradeRecommended = true,
                            recommendedPremisesTier = "Next Restaurant Tier"
                        )
                    )
                    recommendations.add("Upgrade premises tier to expand dining room capacity beyond current $seatingCapacity seats.")
                }

                // 2. Staffing Needed by Role based on Recurring Diners
                val neededChefs = ((recurring + 24) / 25).coerceAtLeast(1)
                val neededSeniorChefs = if (cCount >= 4) (cCount / 4) else 0
                val neededWaiters = ((recurring + 19) / 20).coerceAtLeast(1)
                val neededCleaners = ((recurring + 19) / 20).coerceAtLeast(1)
                val totalStaff = wCount + cCount + scCount + clCount
                val neededManagers = if (totalStaff >= 8) ((totalStaff + 7) / 8) else 0

                val missingStaffMap = mutableMapOf<String, Int>()

                if (cCount < neededChefs) {
                    val diff = neededChefs - cCount
                    missingStaffMap["Chefs"] = diff
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Kitchen Speed Deficit: Missing $diff Chef(s)",
                            description = "Current kitchen staffing ($cCount Chefs) cannot prepare meals fast enough for $recurring recurring diners.",
                            severity = "WARNING",
                            actionRequired = "Hire $diff more Chef(s) in Staff Management.",
                            specificStaffNeeded = mapOf("Chefs" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Chef(s) to meet kitchen order volume.")
                }

                if (scCount < neededSeniorChefs) {
                    val diff = neededSeniorChefs - scCount
                    missingStaffMap["Senior Chefs"] = diff
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Kitchen Leadership Void: Missing $diff Senior Chef(s)",
                            description = "With $cCount chefs on line, lack of a Senior Chef causes station disorganization and dish timing failures.",
                            severity = "WARNING",
                            actionRequired = "Promote or hire $diff Senior Chef(s).",
                            specificStaffNeeded = mapOf("Senior Chefs" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Senior Chef(s) to coordinate prep line.")
                }

                if (wCount < neededWaiters) {
                    val diff = neededWaiters - wCount
                    missingStaffMap["Waiters"] = diff
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Food Spoiling at Pass: Missing $diff Waiter(s)",
                            description = "With only $wCount Waiter(s) for $recurring diners, prepared hot dishes sit at the kitchen pass unserved and spoil. Ingredients are wasted without collecting revenue!",
                            severity = "CRITICAL",
                            actionRequired = "Hire $diff more Waiter(s) immediately to deliver orders to tables.",
                            specificStaffNeeded = mapOf("Waiters" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Waiter(s) immediately to stop prepared food from spoiling unserved.")
                }

                if (clCount < neededCleaners) {
                    val diff = neededCleaners - clCount
                    missingStaffMap["Cleaners"] = diff
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Sanitation & Table Turnover: Missing $diff Cleaner(s)",
                            description = "Dirty tables and unwashed dishes slow dining turnover. $clCount cleaner(s) is insufficient for $recurring patrons, causing patron churn.",
                            severity = "WARNING",
                            actionRequired = "Hire $diff more Cleaner(s) to keep tables sanitized.",
                            specificStaffNeeded = mapOf("Cleaners" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Cleaner(s) to prevent hygiene penalties and customer loss.")
                }

                if (mCount < neededManagers) {
                    val diff = neededManagers - mCount
                    missingStaffMap["Managers"] = diff
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Shift Supervision Void: Missing $diff Floor Manager(s)",
                            description = "Floor staff ($totalStaff crew) lacks floor coordination, causing service confusion.",
                            severity = "INFO",
                            actionRequired = "Hire $diff Floor Manager(s) to streamline operations.",
                            specificStaffNeeded = mapOf("Managers" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Manager(s) to coordinate floor and kitchen shifts.")
                }

                // 3. Equipment Check
                val eqList = if (venture.equipmentList.isEmpty()) VentureSimulationHelper.getDefaultRestaurantEquipment() else venture.equipmentList
                val brokenEq = eqList.filter { it.isOwned && it.condition < 0.40f }
                if (brokenEq.isNotEmpty()) {
                    val names = brokenEq.joinToString(", ") { it.name }
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Appliance Breakdown: $names",
                            description = "Kitchen appliances are worn down (<40% condition), creating service delays during peak meal hours.",
                            severity = "WARNING",
                            actionRequired = "Service equipment in Appliances Tab."
                        )
                    )
                    recommendations.add("Service deteriorating appliances: $names.")
                }
            }

            "Taxi", "Taxi Fleet" -> {
                val cars = venture.taxiCarCount
                val drivers = venture.taxiDriverCount
                val mechanics = venture.taxiMechanicCount
                val dispatchers = venture.taxiDispatcherCount
                val recMechanics = ((cars + 3) / 4).coerceAtLeast(1)
                val recDispatchers = if (cars >= 5) ((cars + 4) / 5) else 0

                if (drivers < cars) {
                    val idle = cars - drivers
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Idle Taxis: $idle Cab(s) Have No Drivers",
                            description = "$idle taxi cabs are parked collecting dust while parking fees and insurance continue. You are missing out on weekly ride fares.",
                            severity = "CRITICAL",
                            actionRequired = "Hire $idle more Taxi Driver(s) in Fleet Staffing.",
                            specificStaffNeeded = mapOf("Taxi Drivers" to idle)
                        )
                    )
                    recommendations.add("Hire +$idle Driver(s) to get all $cars taxis on the road generating fares.")
                }

                if (mechanics < recMechanics) {
                    val diff = recMechanics - mechanics
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "🚨 Catastrophic Breakdown Risk: Missing $diff Fleet Mechanic(s)",
                            description = "Your fleet of $cars vehicles requires at least $recMechanics dedicated mechanic(s) (1 per 4 cabs). Without mechanics, vehicles lose 3% condition every week until they are TOTALED and destroyed.",
                            severity = "CRITICAL",
                            actionRequired = "Hire $diff Mechanic(s) immediately to maintain vehicle roadworthiness.",
                            specificStaffNeeded = mapOf("Mechanics" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Fleet Mechanic(s) (requires 1 mechanic per 4 cars) to avoid vehicle write-offs.")
                }

                if (dispatchers < recDispatchers) {
                    val diff = recDispatchers - dispatchers
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Dispatch Routing Congestion: Missing $diff Dispatcher(s)",
                            description = "Drivers are waiting excessively for dispatched passenger calls without central routing.",
                            severity = "INFO",
                            actionRequired = "Hire $diff Radio Dispatcher(s).",
                            specificStaffNeeded = mapOf("Dispatchers" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Dispatcher(s) for optimized trip coordination.")
                }

                if (venture.taxiCondition < 0.65f) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Worn Vehicle Fleet: Condition ${(venture.taxiCondition * 100).toInt()}%",
                            description = "Fleet condition is dropping. Passengers complain of rattling engines and squeaking brakes.",
                            severity = "WARNING",
                            actionRequired = "Tap 'Service Fleet' to restore cabs to 100% roadworthiness."
                        )
                    )
                    recommendations.add("Service vehicle fleet to prevent unexpected road breakdowns.")
                }

                if (cars >= venture.taxiDepotCapacity) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Depot Parking Limit Reached (${cars}/${venture.taxiDepotCapacity} Cabs)",
                            description = "All depot parking stalls are occupied. Municipal overflow parking taxes apply.",
                            severity = "WARNING",
                            actionRequired = "Upgrade Depot Tier in Upgrades Tab to expand fleet parking stalls.",
                            premisesUpgradeRecommended = true,
                            recommendedPremisesTier = "Next Depot Facility Tier"
                        )
                    )
                    recommendations.add("Upgrade Taxi Depot to expand parking capacity for more cabs.")
                }
            }

            "Farming" -> {
                val totalAcres = venture.farmLandAcres
                val plantedAcres = venture.farmCropBatches.sumOf { it.acres }
                val freeAcres = (totalAcres - plantedAcres).coerceAtLeast(0.0)
                val hands = venture.farmHandCount
                val neededHands = ((plantedAcres.toInt() + 9) / 10).coerceAtLeast(1)

                if (plantedAcres == 0.0) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Empty Fields: 0 Acres Planted",
                            description = "You are paying land rent/taxes on ${totalAcres.toInt()} acres, but nothing is in the ground! Fields are idle.",
                            severity = "CRITICAL",
                            actionRequired = "Go to Crops tab and click 'Plant' to start seed cultivation."
                        )
                    )
                    recommendations.add("Plant seeds on free acres (${freeAcres.toInt()} ac) to start generating crop revenue.")
                } else if (hands < neededHands) {
                    val diff = neededHands - hands
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Field Understaffing: Missing $diff Farmhand(s)",
                            description = "You have ${plantedAcres.toInt()} planted acres, but only $hands Farmhand(s). Standard agriculture requires 1 worker per 10 acres. Crops suffer from weeds, pests, and 30% yield loss.",
                            severity = "CRITICAL",
                            actionRequired = "Hire $diff more Farmhand(s) in Staff tab.",
                            specificStaffNeeded = mapOf("Farmhands" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Farmhand(s) (1 worker per 10 cultivated acres needed).")
                }

                // Check ready harvests
                val readyBatches = venture.farmCropBatches.filter { it.currentWeek >= it.totalWeeks }
                if (readyBatches.isNotEmpty()) {
                    val batchNames = readyBatches.joinToString(", ") { it.cropName }
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "🌾 Harvest Ready: $batchNames",
                            description = "Your crops have completed their growth cycle and are golden in the fields! Harvest now to avoid rot.",
                            severity = "OPPORTUNITY",
                            actionRequired = "Go to Crops tab and click 'Harvest into Silo', then sell to market for instant cash!"
                        )
                    )
                    recommendations.add("Harvest ready crop batches ($batchNames) into silo and sell to market.")
                }

                // Missing Weather Protection Equipment
                val hasIrrigation = venture.farmMachinery.any { it.id == "mach_irrigation" && (it.isOwned || it.isRented) }
                val hasSprayer = venture.farmMachinery.any { it.id == "mach_sprayer" && (it.isOwned || it.isRented) }
                val hasHarvester = venture.farmMachinery.any { it.id == "mach_harvester" && (it.isOwned || it.isRented) }

                if (!hasIrrigation) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Drought Vulnerability: No Automated Irrigation",
                            description = "Dry summer spells will scorch crops without irrigation, causing a 30% yield penalty.",
                            severity = "WARNING",
                            actionRequired = "Lease or buy an Automated Irrigation System in Machinery Tab."
                        )
                    )
                    recommendations.add("Acquire Irrigation System to safeguard against seasonal droughts.")
                }

                if (!hasSprayer) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Pest Vulnerability: No Crop Sprayer",
                            description = "Locust and blight infestations will destroy 25% of crops without chemical protection.",
                            severity = "INFO",
                            actionRequired = "Acquire a Crop Sprayer in Machinery Tab."
                        )
                    )
                    recommendations.add("Acquire Crop Sprayer to defend against insect swarms.")
                }
            }

            "Hotel", "Hotel Chain" -> {
                val rooms = venture.hotelRoomCount
                val housekeepers = venture.hotelHousekeeperCount
                val neededHousekeepers = ((rooms + 3) / 4).coerceAtLeast(1)

                if (housekeepers < neededHousekeepers) {
                    val diff = neededHousekeepers - housekeepers
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Room Turnover Deficit: Missing $diff Housekeeper(s)",
                            description = "Dirty sheets and uncleaned suites cause room turnover delays and plummeting cleanliness ratings.",
                            severity = "CRITICAL",
                            actionRequired = "Hire $diff more Housekeeper(s) in Hotel Staffing.",
                            specificStaffNeeded = mapOf("Housekeepers" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Housekeeper(s) (1 per 4 rooms required for sanitation).")
                }

                if (venture.hotelCleanliness < 0.70f) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Hygiene Warning: Hotel Cleanliness at ${(venture.hotelCleanliness * 100).toInt()}%",
                            description = "Guest complaints regarding dust and bathroom sanitation are reducing occupancy rates.",
                            severity = "WARNING",
                            actionRequired = "Increase housekeeping staff and perform deep sanitation."
                        )
                    )
                    recommendations.add("Deep clean suites to improve guest review scores.")
                }
            }

            "Crypto Token" -> {
                if (venture.cryptoDevelopersCount < 2 && venture.cryptoActiveUsers >= 300) {
                    val diff = 2 - venture.cryptoDevelopersCount
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "Core Dev Bandwidth Cap: Missing $diff Solidity Dev(s)",
                            description = "Network congestion and smart contract updates require dedicated core developers.",
                            severity = "WARNING",
                            actionRequired = "Hire $diff more Developer(s) in Team Management.",
                            specificStaffNeeded = mapOf("Developers" to diff)
                        )
                    )
                    recommendations.add("Hire +$diff Solidity Developer(s) to manage smart contract throughput.")
                }

                if (venture.cryptoSecurityTier == 1 && venture.cryptoLiquidityPoolUsd >= 50_000.0) {
                    bottlenecks.add(
                        OperationalBottleneck(
                            title = "🚨 Exploit Vulnerability: Tier 1 Security on $50K+ Pool",
                            description = "Your smart contracts have not had a full formal audit. Flash loan exploits can drain user liquidity.",
                            severity = "CRITICAL",
                            actionRequired = "Upgrade Security Tier in Operations Panel immediately."
                        )
                    )
                    recommendations.add("Upgrade Security Tier to protect liquidity pools from hacker drain attacks.")
                }
            }
        }

        // Generic checks: License & Legal
        if (!venture.hasLegalLicense && venture.recurringCustomers >= 50) {
            bottlenecks.add(
                OperationalBottleneck(
                    title = "⚖️ Unlicensed Operations: SCC Warning Imminent",
                    description = "State Commerce Commission regulations mandate an Operating License for commercial enterprises with active customer volume.",
                    severity = "WARNING",
                    actionRequired = "Purchase Commercial Operating License ($15,000) in Ventures Portal."
                )
            )
            recommendations.add("Acquire official State Commercial Operating License to prevent forced closure.")
        }

        val health = when {
            bottlenecks.any { it.severity == "CRITICAL" } -> "CRITICAL_BOTTLENECK"
            bottlenecks.any { it.severity == "WARNING" } -> "NEEDS_ATTENTION"
            else -> "GOOD"
        }

        val summary = when (health) {
            "CRITICAL_BOTTLENECK" -> "Action Required: Operational Bottlenecks Detected"
            "NEEDS_ATTENTION" -> "Room for Optimization: Staffing & Upgrades Needed"
            else -> "Operating Smoothly: Capacity & Staffing Well Balanced"
        }

        return VentureDiagnosticsResult(
            summaryTitle = summary,
            overallHealth = health,
            bottlenecks = bottlenecks,
            managerName = mgrName,
            managerRole = mgrRole,
            quickRecommendations = recommendations
        )
    }

    fun generateManagerWeeklyEmail(
        venture: BusinessVenture,
        currentWeek: Int,
        currentYear: Int
    ): MailMessage? {
        val diag = analyzeVenture(venture)
        if (diag.bottlenecks.isEmpty()) return null

        val (mgrName, mgrRole) = getManagerInfo(venture)
        val bulletPoints = StringBuilder()

        diag.bottlenecks.take(4).forEach { b ->
            bulletPoints.append("• [${b.severity}] ${b.title}\n  - Issue: ${b.description}\n  - Recommendation: ${b.actionRequired}\n\n")
        }

        val emailBody = """
Dear Boss,

I am writing with our weekly operational diagnostic for ${venture.name}.

CURRENT ENTERPRISE PULSE:
• Weekly Customers / Users: ${String.format("%,d", venture.recurringCustomers)}
• Gross Revenue: $${String.format("%,.0f", venture.weeklyGrossRevenue)}
• Operating Expenses: $${String.format("%,.0f", venture.weeklyOperatingExpenses)}
• Net Margin: $${String.format("%,.0f", venture.weeklyNetProfit)}

CRITICAL BOTTLENECK ANALYSIS:
$bulletPoints
${if (diag.quickRecommendations.isNotEmpty()) "RECOMMENDED IMMEDIATE ACTION:\n" + diag.quickRecommendations.take(3).joinToString("\n") { "✓ $it" } else ""}

Please review these recommendations in the Ventures Management Portal. We can significantly increase our weekly profits once these bottlenecks are resolved.

Best regards,
$mgrName
$mgrRole, ${venture.name}
""".trimIndent()

        return MailMessage(
            id = "biz_mgr_" + UUID.randomUUID().toString().take(6),
            sender = "$mgrName ($mgrRole, ${venture.name})",
            subject = "📋 Operational Assessment: ${venture.name} Status & Staffing Needs",
            body = emailBody,
            type = "BUSINESS_MANAGER_REPORT",
            ventureId = venture.id,
            expiresWeek = currentWeek + 4,
            expiresYear = currentYear
        )
    }

    fun simulateMajorityOwnerDecision(venture: BusinessVenture): Pair<BusinessVenture, String> {
        val diag = analyzeVenture(venture)
        val roll = Random.nextDouble()
        var updated = venture
        var actionNote = ""

        when {
            // 65% of the time, the majority owner is sensible or value-oriented
            roll < 0.65 -> {
                if (diag.bottlenecks.any { it.specificStaffNeeded.isNotEmpty() }) {
                    // Hire a needed staff member
                    val targetStaff = diag.bottlenecks.firstOrNull { it.specificStaffNeeded.isNotEmpty() }?.specificStaffNeeded?.keys?.firstOrNull()
                    when (targetStaff) {
                        "Waiters" -> {
                            updated = updated.copyRestaurant(waiterCount = updated.waiterCount + 1)
                            actionNote = "Majority Owner hired 1 Waiter to handle dining room rush."
                        }
                        "Chefs" -> {
                            updated = updated.copyRestaurant(chefCount = updated.chefCount + 1)
                            actionNote = "Majority Owner hired 1 Line Cook to accelerate kitchen prep."
                        }
                        "Cleaners" -> {
                            updated = updated.copyRestaurant(cleanerCount = updated.cleanerCount + 1)
                            actionNote = "Majority Owner hired 1 Cleaner to restore restaurant hygiene."
                        }
                        "Taxi Drivers" -> {
                            updated = updated.copyTaxi(taxiDriverCount = (updated.taxiDriverCount + 1).coerceAtMost(updated.taxiCarCount))
                            actionNote = "Majority Owner hired 1 Driver to put an idle taxi on the road."
                        }
                        "Mechanics" -> {
                            updated = updated.copyTaxi(taxiMechanicCount = updated.taxiMechanicCount + 1)
                            actionNote = "Majority Owner hired 1 Mechanic to maintain vehicle fleet safety."
                        }
                        "Farmhands" -> {
                            updated = updated.copyFarm(farmHandCount = updated.farmHandCount + 1)
                            actionNote = "Majority Owner hired 1 Farmhand to increase crop cultivation capacity."
                        }
                        "Housekeepers" -> {
                            updated = updated.copyHotel(hotelHousekeeperCount = updated.hotelHousekeeperCount + 1)
                            actionNote = "Majority Owner hired 1 Housekeeper to speed up room turnover."
                        }
                        else -> {
                            actionNote = "Majority Owner optimized weekly inventory schedules."
                        }
                    }
                } else if (venture.type == "Taxi" && venture.taxiCondition < 0.80f) {
                    updated = updated.copyTaxi(taxiCondition = 1.0f)
                    actionNote = "Majority Owner authorized full mechanical overhaul of the taxi fleet."
                } else if (venture.marketingCampaign == "NONE") {
                    updated = updated.copyGovernance(marketingCampaign = "LOCAL_ADS", marketingWeeksActive = 1, marketingWeeklyCost = 250.0)
                    actionNote = "Majority Owner launched a local community advertising campaign."
                } else {
                    actionNote = "Majority Owner audited supplier contracts, stabilizing operational margins."
                }
            }

            // 35% of the time, the majority owner cuts corners or makes short-sighted moves
            else -> {
                val cutRoll = Random.nextInt(3)
                when (cutRoll) {
                    0 -> {
                        // Slashed marketing to reduce weekly costs
                        if (updated.marketingCampaign != "NONE") {
                            updated = updated.copyGovernance(marketingCampaign = "NONE", marketingWeeklyCost = 0.0)
                            actionNote = "Majority Owner terminated marketing campaigns to cut overhead."
                        } else {
                            actionNote = "Majority Owner deferred quarterly facility maintenance to conserve cash."
                        }
                    }
                    1 -> {
                        // Layoff to cut payroll
                        if (updated.type == "Restaurant" && updated.cleanerCount > 0) {
                            updated = updated.copyRestaurant(cleanerCount = (updated.cleanerCount - 1).coerceAtLeast(0))
                            actionNote = "Majority Owner laid off 1 Cleaner to reduce weekly payroll."
                        } else if (updated.type == "Taxi" && updated.taxiMechanicCount > 1) {
                            updated = updated.copyTaxi(taxiMechanicCount = updated.taxiMechanicCount - 1)
                            actionNote = "Majority Owner laid off 1 Mechanic to cut fleet maintenance expenses."
                        } else {
                            actionNote = "Majority Owner renegotiated ingredient contracts for cheaper budget alternatives."
                        }
                    }
                    else -> {
                        // Neglected maintenance
                        if (updated.type == "Taxi") {
                            updated = updated.copyTaxi(taxiCondition = (updated.taxiCondition - 0.05f).coerceAtLeast(0.20f))
                            actionNote = "Majority Owner skipped scheduled vehicle maintenance to trim costs."
                        } else {
                            actionNote = "Majority Owner postponed equipment upgrades, running machinery at max capacity."
                        }
                    }
                }
            }
        }

        updated = updated.copyGovernance(lastOwnerAction = actionNote)
        return Pair(updated, actionNote)
    }
}
