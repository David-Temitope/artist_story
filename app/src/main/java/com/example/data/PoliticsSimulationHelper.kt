package com.example.data

import androidx.compose.ui.graphics.Color
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class PoliticianProfile(
    val id: String,
    val name: String,
    val title: String,
    val party: String,
    val partyAbbr: String,
    val alignment: String, // "Progressive", "Conservative", "Tech-Libertarian", "Labor", "Centrist"
    val avatarInitial: String,
    val themeColorHex: String,
    val biography: String,
    val basePopularity: Float,
    val endorsementOfferAmount: Double,
    val corruptionTolerance: Float // 0.0 (high integrity) to 1.0 (vengeful/ruthless)
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class CampaignPromise(
    val title: String,
    val description: String,
    val mechanicalEffect: String // Summary of actual in-game world benefit
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ElectionContestant(
    val politician: PoliticianProfile,
    val promises: List<CampaignPromise>,
    val projectedVoteShare: Float,
    val campaignCatchphrase: String
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ActiveElectionRace(
    val electionYear: Int,
    val electionWeek: Int,
    val positionTitle: String, // e.g. "State Governor", "City Mayor", "Attorney General", "State Comptroller"
    val positionDescription: String,
    val contestants: List<ElectionContestant>,
    val isConcluded: Boolean = false,
    val winnerPoliticianId: String? = null
) {
    val position: String get() = positionTitle
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class DistrictVoteTally(
    val districtName: String,
    val registeredVoters: Int,
    val votesCast: Int,
    val reportingPercentage: Float,
    val contestantVotes: Map<String, Int>
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class DistrictReturnDisplay(
    val districtName: String,
    val leadingParty: String,
    val turnout: Float
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class LiveElectionResults(
    val racePosition: String,
    val precinctsReportingPercent: Float,
    val totalBallotsCounted: Int,
    val overallTurnoutPercent: Float,
    val candidateShares: List<CandidateResultShare>,
    val districtTallies: List<DistrictVoteTally>,
    val isCertified: Boolean,
    val leadingCandidateId: String,
    val isVotingSeason: Boolean = false,
    val forecastStageName: String = "Public Opinion Poll & Momentum Tracking"
) {
    val turnoutPercentage: Float get() = overallTurnoutPercent
    val districtReturns: List<DistrictReturnDisplay>
        get() = districtTallies.mapIndexed { idx, d ->
            val partyName = when (idx % 3) {
                0 -> "Progressive Alliance"
                1 -> "Conservative Front"
                else -> "Innovation Party"
            }
            DistrictReturnDisplay(d.districtName, partyName, d.reportingPercentage)
        }
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class CandidateResultShare(
    val candidateId: String,
    val candidateName: String,
    val party: String,
    val themeColorHex: String,
    val totalVotes: Int,
    val percentage: Float,
    val isProjectedWinner: Boolean,
    val weeklyDeltaText: String = "+0.0%",
    val weeklyTrendDirection: String = "STABLE"
) {
    val voteCount: Int get() = totalVotes
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class DefenseAttorneyOption(
    val id: String,
    val name: String,
    val firm: String,
    val retainerCost: Double,
    val bailDiscountPercent: Float,
    val sentenceReductionWeeks: Int,
    val successRate: Float,
    val pitch: String
) {
    val cost: Double get() = retainerCost
    val bailDiscount: Float get() = bailDiscountPercent
}

object PoliticsSimulationHelper {

    val ROSTER_OF_POLITICIANS = listOf(
        PoliticianProfile(
            id = "cand_sterling",
            name = "Marcus Sterling",
            title = "Governor",
            party = "Fiscal Conservative Front",
            partyAbbr = "FCF",
            alignment = "Conservative",
            avatarInitial = "MS",
            themeColorHex = "#3B82F6",
            biography = "Two-term executive focusing on corporate tax breaks, private enterprise deregulation, and commercial studio tax credits.",
            basePopularity = 52f,
            endorsementOfferAmount = 50000.0,
            corruptionTolerance = 0.85f
        ),
        PoliticianProfile(
            id = "cand_vance",
            name = "Elena Vance",
            title = "Mayor",
            party = "Civic Progressive Alliance",
            partyAbbr = "CPA",
            alignment = "Progressive",
            avatarInitial = "EV",
            themeColorHex = "#10B981",
            biography = "Former grassroots activist and cultural minister fighting for public arts funding, creator healthcare, and live music deregulation.",
            basePopularity = 49f,
            endorsementOfferAmount = 45000.0,
            corruptionTolerance = 0.40f
        ),
        PoliticianProfile(
            id = "cand_chen",
            name = "Aria Chen",
            title = "Commissioner",
            party = "Digital Rights & Innovation Party",
            partyAbbr = "DRIP",
            alignment = "Tech-Libertarian",
            avatarInitial = "AC",
            themeColorHex = "#8B5CF6",
            biography = "Former Silicon Valley engineer championing strict AI vocal copyright shields, streaming royalty floors, and studio fiber grants.",
            basePopularity = 46f,
            endorsementOfferAmount = 60000.0,
            corruptionTolerance = 0.60f
        ),
        PoliticianProfile(
            id = "cand_cross",
            name = "Raymond Cross",
            title = "Attorney General",
            party = "Federal Law & Order Coalition",
            partyAbbr = "FLOC",
            alignment = "Centrist",
            avatarInitial = "RC",
            themeColorHex = "#EF4444",
            biography = "Tough-on-crime prosecutor targeting music piracy syndicates, white-collar defalcation, and fraudulent promoters.",
            basePopularity = 48f,
            endorsementOfferAmount = 55000.0,
            corruptionTolerance = 0.90f
        ),
        PoliticianProfile(
            id = "cand_adebayo",
            name = "Malik Adebayo",
            title = "Council Member",
            party = "Youth & Creative Labor Movement",
            partyAbbr = "YCLM",
            alignment = "Labor",
            avatarInitial = "MA",
            themeColorHex = "#F59E0B",
            biography = "Independent organizer pushing for busker protections, street festival subsidies, and fair splits for backing vocalists and dancers.",
            basePopularity = 47f,
            endorsementOfferAmount = 40000.0,
            corruptionTolerance = 0.30f
        ),
        PoliticianProfile(
            id = "cand_vic_sterling",
            name = "Victoria Sterling",
            title = "Comptroller",
            party = "Enterprise & Commerce Board",
            partyAbbr = "ECB",
            alignment = "Conservative",
            avatarInitial = "VS",
            themeColorHex = "#06B6D4",
            biography = "High-finance analyst advocating for zero capital gains on WeShare IPO listings and low corporate taxes for business ventures.",
            basePopularity = 50f,
            endorsementOfferAmount = 70000.0,
            corruptionTolerance = 0.75f
        ),
        PoliticianProfile(
            id = "cand_thorne",
            name = "Lucas Thorne",
            title = "Alderman",
            party = "Heritage & Nightlife Guild",
            partyAbbr = "HNG",
            alignment = "Centrist",
            avatarInitial = "LT",
            themeColorHex = "#EC4899",
            biography = "Prominent venue owner fighting against early concert curfews, sound ordinance penalties, and heavy licensing red tape.",
            basePopularity = 45f,
            endorsementOfferAmount = 35000.0,
            corruptionTolerance = 0.50f
        ),
        PoliticianProfile(
            id = "cand_ross",
            name = "Evelyn Ross",
            title = "Senator",
            party = "Green Arts & Sustainable Future",
            partyAbbr = "GASF",
            alignment = "Progressive",
            avatarInitial = "ER",
            themeColorHex = "#84CC16",
            biography = "Pioneering eco-friendly tour subsidies, solar studio grants, and carbon-neutral concert incentives.",
            basePopularity = 44f,
            endorsementOfferAmount = 40000.0,
            corruptionTolerance = 0.35f
        ),
        PoliticianProfile(
            id = "cand_drake",
            name = "Damian Drake",
            title = "Industrial Director",
            party = "National Industrial Syndicate",
            partyAbbr = "NIS",
            alignment = "Conservative",
            avatarInitial = "DD",
            themeColorHex = "#64748B",
            biography = "Billionaire investor promising heavy capital injection into stadium arenas, private jets, and international luxury hospitality.",
            basePopularity = 51f,
            endorsementOfferAmount = 85000.0,
            corruptionTolerance = 0.88f
        )
    )

    val DEFENSE_ATTORNEYS = listOf(
        DefenseAttorneyOption(
            id = "atty_public",
            name = "State Public Defender's Office",
            firm = "Department of Legal Aid",
            retainerCost = 0.0,
            bailDiscountPercent = 0.0f,
            sentenceReductionWeeks = 1,
            successRate = 0.30f,
            pitch = "Assigned standard legal counsel provided by the state at zero retainer cost."
        ),
        DefenseAttorneyOption(
            id = "atty_thorne",
            name = "Rachel Thorne, Esq.",
            firm = "Thorne & Associates Criminal Defense",
            retainerCost = 15000.0,
            bailDiscountPercent = 0.35f,
            sentenceReductionWeeks = 2,
            successRate = 0.75f,
            pitch = "Former assistant prosecutor who negotiates 35% bail reduction and cuts 2 weeks off prison sentences."
        ),
        DefenseAttorneyOption(
            id = "atty_blackwood",
            name = "Marcus Blackwood, K.C.",
            firm = "Blackwood White-Collar Litigators",
            retainerCost = 35000.0,
            bailDiscountPercent = 0.55f,
            sentenceReductionWeeks = 3,
            successRate = 0.95f,
            pitch = "Top-tier constitutional defense titan. Slashes bail by 55%, accelerates sentence discharges, and quashes media leaks."
        )
    )

    fun getActiveElectionRace(week: Int, year: Int): ActiveElectionRace {
        val (position, description) = getElectionPositionForSeason(year, week)
        val contestants = generateContestantsForRace(position, year)
        return ActiveElectionRace(
            electionYear = year,
            electionWeek = week,
            positionTitle = position,
            positionDescription = description,
            contestants = contestants
        )
    }

    /**
     * Determines which political position is up for election based on the week and year.
     */
    fun getElectionPositionForSeason(year: Int, week: Int): Pair<String, String> {
        return when {
            week in 12..15 -> Pair(
                "State Governor",
                "Chief Executive of the State. Controls luxury entertainment taxes, state creative grants, and judicial appointment oversight."
            )
            week in 36..39 -> Pair(
                if (year % 2 == 1) "City Mayor" else "Attorney General",
                if (year % 2 == 1)
                    "Municipal Mayor. Governs venue curfew hours, street busking permits, studio zoning, and local live performance subsidies."
                else
                    "State Attorney General & Chief Legal Officer. Enforces copyright piracy laws, white-collar fraud probes, and State Crime Command enforcement."
            )
            else -> Pair(
                "State Comptroller",
                "Chief Financial Regulator. Sets corporate venture tax rates, WeShare IPO capital gains rules, and commercial banking interest caps."
            )
        }
    }

    /**
     * Generates 3 distinct candidates contesting for the selected position,
     * each with unique, believable promises tailored to this specific election.
     */
    fun generateContestantsForRace(position: String, year: Int): List<ElectionContestant> {
        val pool = when (position) {
            "State Governor" -> listOf(
                ROSTER_OF_POLITICIANS.first { it.id == "cand_sterling" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_vance" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_chen" }
            )
            "City Mayor" -> listOf(
                ROSTER_OF_POLITICIANS.first { it.id == "cand_adebayo" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_thorne" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_vance" }
            )
            "Attorney General" -> listOf(
                ROSTER_OF_POLITICIANS.first { it.id == "cand_cross" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_chen" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_drake" }
            )
            else -> listOf(
                ROSTER_OF_POLITICIANS.first { it.id == "cand_vic_sterling" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_ross" },
                ROSTER_OF_POLITICIANS.first { it.id == "cand_sterling" }
            )
        }

        return pool.map { politician ->
            val promises = generateDynamicPromises(politician, position, year)
            val catchphrase = when (politician.id) {
                "cand_sterling" -> "Strengthening Prosperity Through Free Enterprise."
                "cand_vance" -> "Culture, Community, and Creator Empowerment."
                "cand_chen" -> "Securing Intellectual Property in the Digital Era."
                "cand_cross" -> "Uncompromising Justice & Anti-Corruption."
                "cand_adebayo" -> "A Voice for Independent Artists & Street Culture."
                "cand_vic_sterling" -> "Responsible Capital, Stronger Enterprises."
                "cand_thorne" -> "Protecting Live Music, Venues, and Nightlife."
                "cand_ross" -> "Sustainable Growth & Green Creative Hubs."
                else -> "Unmatched Enterprise Growth & Modern Infrastructure."
            }
            ElectionContestant(
                politician = politician,
                promises = promises,
                projectedVoteShare = 0.33f,
                campaignCatchphrase = catchphrase
            )
        }
    }

    private fun generateDynamicPromises(politician: PoliticianProfile, position: String, year: Int): List<CampaignPromise> {
        return when (politician.id) {
            "cand_sterling" -> listOf(
                CampaignPromise(
                    title = "Slashing Luxury Entertainment Tax",
                    description = "Reduce state tax on high-earning entertainers from 12% to an unprecedented 6%.",
                    mechanicalEffect = "-6% Weekly State Tax Relief on all earnings"
                ),
                CampaignPromise(
                    title = "Commercial Venture Tax Abatements",
                    description = "Provide 15% operational expense write-offs for private enterprise owners.",
                    mechanicalEffect = "+15% Business Venture Net Profit Margin"
                ),
                CampaignPromise(
                    title = "Private Studio Security Credits",
                    description = "Tax write-offs for audio recording facilities investing in armed private security.",
                    mechanicalEffect = "+20% Protection against studio theft & leaks"
                )
            )
            "cand_vance" -> listOf(
                CampaignPromise(
                    title = "$50,000 State Creative Arts Subsidies",
                    description = "Direct cash grants distributed to active, high-integrity recording artists.",
                    mechanicalEffect = "Periodic $50,000 State Arts Grant payouts"
                ),
                CampaignPromise(
                    title = "Nightlife & Venue Curfew Deregulation",
                    description = "Eliminate sound ordinance restrictions for live concert venues and club runs.",
                    mechanicalEffect = "+25% Concert Tour Attendance and Ticket Gross"
                ),
                CampaignPromise(
                    title = "Independent Musician Healthcare Shield",
                    description = "State-backed mental wellness and vocal therapy clinics for creative performers.",
                    mechanicalEffect = "-20% Burnout and Stress accumulation on the road"
                )
            )
            "cand_chen" -> listOf(
                CampaignPromise(
                    title = "Strict AI Voice Cloning Copyright Enforcement",
                    description = "Criminalize unauthorized AI vocal replication and protect artist biometric intellectual property.",
                    mechanicalEffect = "+25% Weekly Streaming Royalty Revenue"
                ),
                CampaignPromise(
                    title = "High-Speed Studio Fiber Optic Backbone",
                    description = "Subsidize municipal ultra-fast fiber connectivity to recording studios.",
                    mechanicalEffect = "-35% Studio Session Costs across all tiers"
                ),
                CampaignPromise(
                    title = "Zero-Tax Digital Royalties Exemption",
                    description = "Complete tax-free status on streaming micro-royalties and on-chain fan passes.",
                    mechanicalEffect = "100% Tax Exemption on digital streaming payouts"
                )
            )
            "cand_cross" -> listOf(
                CampaignPromise(
                    title = "Zero-Tolerance Music Bootleg Taskforce",
                    description = "Federal raids on black-market vinyl, unauthorized merchandise, and streaming bots.",
                    mechanicalEffect = "+30% Official Merchandise Sales & Brand Protection"
                ),
                CampaignPromise(
                    title = "Anti-Corruption Audits on Predatory Record Labels",
                    description = "Mandate transparent accounting audits for major labels withholding creator royalties.",
                    mechanicalEffect = "-5% Label Contract Take from unsigned/signed artists"
                ),
                CampaignPromise(
                    title = "Fast-Track Court Expungement",
                    description = "Allow reformed citizens with high integrity to cleanly clear administrative citations.",
                    mechanicalEffect = "50% Discount on State Crime Command compliance fees"
                )
            )
            "cand_adebayo" -> listOf(
                CampaignPromise(
                    title = "Independent Busker & Street Artist Protection",
                    description = "Legalize amplified acoustic busking in municipal transit hubs and public plazas.",
                    mechanicalEffect = "+50% Tips and Cash from Street Busking gigs"
                ),
                CampaignPromise(
                    title = "Backstage Labor & Crew Fair Wage Floor",
                    description = "Subsidize audio engineers, wardrobe stylists, and backup dancers on touring circuits.",
                    mechanicalEffect = "-30% Weekly Tour Crew Personnel Costs"
                ),
                CampaignPromise(
                    title = "Youth Cultural Center Grants",
                    description = "State funding for youth music workshops and community masterclasses.",
                    mechanicalEffect = "+15% Industry Respect and Generosity score"
                )
            )
            "cand_vic_sterling" -> listOf(
                CampaignPromise(
                    title = "WeShare Public IPO Capital Gains Exemption",
                    description = "Zero state capital gains tax when selling shares of private companies on the stock exchange.",
                    mechanicalEffect = "+20% Cash Inflow from WeShare IPO share sales"
                ),
                CampaignPromise(
                    title = "Enterprise Low-Interest Credit Lines",
                    description = "Subsidized corporate financing for expanding Taxi Fleets, Hotels, and Restaurants.",
                    mechanicalEffect = "-25% Setup & Upgrade Costs for all Business Ventures"
                ),
                CampaignPromise(
                    title = "Commercial Banking Reserve Subsidies",
                    description = "Higher interest yields on high-balance business checking and payroll reserves.",
                    mechanicalEffect = "+5% Annual Yield on Bank Savings & Emergency Funds"
                )
            )
            else -> listOf(
                CampaignPromise(
                    title = "Live Entertainment Zone Licensing",
                    description = "Streamlined 24-hour permits for nightclubs, music lounges, and festival grounds.",
                    mechanicalEffect = "+20% Concert & Nightclub Gig Payouts"
                ),
                CampaignPromise(
                    title = "Acoustic Noise Waiver Protection",
                    description = "Shield venues from residential noise complaints during sanctioned weekend performances.",
                    mechanicalEffect = "Zero fines or cancellations during concert tours"
                ),
                CampaignPromise(
                    title = "Regional Music Heritage Grants",
                    description = "Historic preservation funding dedicated to vintage recording studios and vinyl shops.",
                    mechanicalEffect = "+10% Critical Acclaim on classic studio releases"
                )
            )
        }
    }

    /**
     * Calculates realistic, dynamic election results and public opinion tracking.
     * Candidate momentum and percentages evolve dynamically week-over-week.
     */
    fun calculateLiveResults(
        contestants: List<ElectionContestant>,
        position: String,
        playerVotedCandidateId: String?,
        playerEndorsedCandidateId: String?,
        playerPopularity: Float,
        currentWeek: Int = 1,
        currentYear: Int = 2024,
        isVotingSeason: Boolean = false
    ): LiveElectionResults {
        val districts = listOf(
            "District 1 - Metro Downtown Core",
            "District 2 - Arts & Cultural Basin",
            "District 3 - North Hills & University Heights",
            "District 4 - Waterfront & Marina District",
            "District 5 - Industrial Port & Freight Corridor",
            "District 6 - West End & Commercial Hub",
            "District 7 - South Basin & Residential Suburbs"
        )

        // Dynamic week-over-week campaign momentum and voter swings
        val baseShares = mutableMapOf<String, Float>()
        val weeklyDeltaMap = mutableMapOf<String, Float>()

        contestants.forEachIndexed { index, c ->
            val candHash = (c.politician.id.hashCode() and 0x7FFFFFFF) % 100
            val curWave = (kotlin.math.sin((currentWeek.toDouble() * 0.48) + (candHash * 0.17)) * 7.0).toFloat() +
                          (kotlin.math.cos((currentWeek.toDouble() * 0.24) + (candHash * 0.31)) * 4.5).toFloat()
            val prevWave = (kotlin.math.sin(((currentWeek - 1).toDouble() * 0.48) + (candHash * 0.17)) * 7.0).toFloat() +
                           (kotlin.math.cos(((currentWeek - 1).toDouble() * 0.24) + (candHash * 0.31)) * 4.5).toFloat()
            val weeklySwing = curWave - prevWave

            var weight = 32.0f + curWave + (if (index == 0) 3.0f else if (index == 1) 1.5f else 0.0f)
            // Endorsement and vote impact
            if (c.politician.id == playerEndorsedCandidateId) {
                weight += (playerPopularity / 100f) * 7.5f
            }
            if (c.politician.id == playerVotedCandidateId) {
                weight += 3.0f
            }
            baseShares[c.politician.id] = weight.coerceAtLeast(10.0f)
            weeklyDeltaMap[c.politician.id] = weeklySwing
        }

        val totalWeight = baseShares.values.sum()
        val normalizedShares = baseShares.mapValues { it.value / totalWeight }

        val totalRegistered = 1850000
        val turnoutPercent = if (isVotingSeason) 76.4f else 71.8f
        val totalVotesCast = (totalRegistered * (turnoutPercent / 100f)).toInt()
        val precinctsReporting = if (isVotingSeason) 88.6f else 0.0f

        val candidateVoteCounts = mutableMapOf<String, Int>()
        normalizedShares.forEach { (candId, share) ->
            candidateVoteCounts[candId] = (totalVotesCast * share).toInt()
        }

        val sortedContestants = contestants.sortedByDescending { candidateVoteCounts[it.politician.id] ?: 0 }
        val projectedLeaderId = sortedContestants.firstOrNull()?.politician?.id ?: ""

        val candidateResultShares = sortedContestants.map { c ->
            val votes = candidateVoteCounts[c.politician.id] ?: 0
            val pct = (votes.toFloat() / totalVotesCast.toFloat()) * 100f
            val swing = weeklyDeltaMap[c.politician.id] ?: 0.0f
            val deltaStr = String.format("%+.1f%%", swing)
            val trendDir = if (swing > 0.4f) "RISING" else if (swing < -0.4f) "FALLING" else "STABLE"

            CandidateResultShare(
                candidateId = c.politician.id,
                candidateName = c.politician.name,
                party = c.politician.party,
                themeColorHex = c.politician.themeColorHex,
                totalVotes = votes,
                percentage = pct,
                isProjectedWinner = c.politician.id == projectedLeaderId,
                weeklyDeltaText = deltaStr,
                weeklyTrendDirection = trendDir
            )
        }

        val districtTallies = districts.mapIndexed { idx, dName ->
            val dRegistered = totalRegistered / districts.size
            val dVotes = (dRegistered * (turnoutPercent / 100f)).toInt()
            val dMap = mutableMapOf<String, Int>()
            contestants.forEach { c ->
                val base = normalizedShares[c.politician.id] ?: 0.33f
                val jitter = if (idx % 2 == 0) 0.03f else -0.03f
                dMap[c.politician.id] = (dVotes * (base + jitter).coerceIn(0.1f, 0.9f)).toInt()
            }
            DistrictVoteTally(
                districtName = dName,
                registeredVoters = dRegistered,
                votesCast = dVotes,
                reportingPercentage = if (isVotingSeason) 85f + (idx * 2f).coerceAtMost(15f) else 0f,
                contestantVotes = dMap
            )
        }

        return LiveElectionResults(
            racePosition = position,
            precinctsReportingPercent = precinctsReporting,
            totalBallotsCounted = totalVotesCast,
            overallTurnoutPercent = turnoutPercent,
            candidateShares = candidateResultShares,
            districtTallies = districtTallies,
            isCertified = false,
            leadingCandidateId = projectedLeaderId,
            isVotingSeason = isVotingSeason,
            forecastStageName = if (isVotingSeason) "CERTIFIED LIVE BALLOT RETURNS" else "PUBLIC OPINION POLL & MOMENTUM FORECAST"
        )
    }
}
