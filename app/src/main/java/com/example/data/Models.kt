package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Car(
    val id: String,
    val name: String,
    val cost: Double,
    val costReductionPercent: Float // e.g. 0.2f for 20%
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class House(
    val id: String,
    val name: String,
    val cost: Double,
    val energyBonus: Int
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Instrument(
    val id: String,
    val name: String,
    val cost: Double,
    val ratingBonus: Float
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class LuxuryItem(
    val id: String,
    val name: String,
    val description: String,
    val cost: Double,
    val perks: String,
    val unlockRequirement: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Genre(
    val id: String,
    val name: String,
    val productionCostMultiplier: Float,
    val listenerAppeal: Float
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Producer(
    val id: String,
    val name: String,
    val level: String, // Beginner, Experienced, Legendary
    val cost: Double,
    val ratingBonus: Float,
    val specialtyGenre: String? = null
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Song(
    val id: String,
    val title: String,
    val genreId: String,
    val budget: Double,
    val producerId: String?,
    val rating: Float, // 1 to 5
    val songQuality: Float = 50f, // 0 to 100 craft & production quality
    val isUploadedMap: Map<String, Long> = emptyMap(), // platformId to streamCount
    val totalStreams: Long = 0,
    val videoTier: String? = null, // Low, Standard, High, Premium, Cinematic
    val weekCreated: Int,
    val yearCreated: Int,
    val licenseType: String = "Full", // "Full", "Half", "None"
    val promoWeeksLeft: Int = 0,
    val promoWeeklyBoost: Float = 0f,
    val playlistsCount: Int = 0,
    val isGlobal: Boolean = false,
    val weeksSinceRelease: Int = 0,
    val peakStreams: Long = 0,
    val isViral: Boolean = false,
    val isReleased: Boolean = true,
    val hype: Float = 1f,
    val weeksUntilRelease: Int = 0,
    val initialHypeWeeksSet: Int = 0,
    val preReleaseCampaignPower: Float = 0f,
    val preReleaseCampaignReach: Long = 0L,
    val soundEngineerQuality: String = "Amateur",
    val mixingStyle: String = "Standard",
    val backingVocalists: String = "None",
    val studioDecor: String = "Basement",
    val producerTier: String = "None",
    val coverArt: String? = null,
    val isCopyrightRegistered: Boolean = false,
    val hasSampleClearance: Boolean = true,
    val audienceFeedback: List<String> = emptyList(),
    val featuredArtistId: String? = null,
    val featuredArtistName: String? = null,
    val isPlayerFeatured: Boolean = false,
    val primaryArtistName: String? = null,
    val videoTitle: String? = null,
    val videoThumbnail: String? = null
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Tour(
    val id: String,
    val level: String, // Simple, Pro, Premium
    val totalWeeks: Int,
    val currentWeek: Int,
    val weeklyEnergyCost: Int,
    val weeklyTimeCost: Int,
    val setupCost: Double,
    val payPerAttendee: Double,
    val promotionBudget: Double = 0.0,
    val isPromotedOnSocial: Boolean = false,
    val isPromotedOnNews: Boolean = false,
    val baseAttendance: Int = 0,
    val bookedWeek: Int,
    val bookedYear: Int,
    val status: String, // "BOOKED", "ACTIVE", "COMPLETED", "DRAFT"
    val title: String = "Unnamed Tour",
    val locationsCount: Int = 5,
    val ticketPrice: Double = 45.0,
    val productionTier: String = "Basic",
    val transportTier: String = "Sleeper Bus",
    val backupDancersCount: Int = 0,
    val isLaunched: Boolean = true,
    val showHype: Float = 20f,
    // Realistic Touring Expansions
    val bannerUri: String? = null,
    val tourType: String = "Club Run", // "Club Run", "City Tour", "National Tour", "Global Tour"
    val routeMarkets: List<String> = emptyList(), // Ordered "City, Country" markets used for local competition
    val selectedCrewRoles: List<String> = emptyList(), // Tour Manager, Head of Security, Audio Engineer, Wardrobe Stylist, Instrumentalist, Dancers, Photographer & Videographer, Medics, Driver
    val openingActNpcId: String? = null,
    val openingActName: String? = null,
    val openingActCost: Double = 0.0,
    val closingActNpcId: String? = null,
    val closingActName: String? = null,
    val closingActCost: Double = 0.0,
    val setlistSongIds: List<String> = emptyList(),
    val setlistSongTitles: List<String> = emptyList(),
    val selectedMerchIds: List<String> = emptyList(),
    val generalTicketPrice: Double = 35.0,
    val vipTicketPrice: Double = 85.0,
    val platinumTicketPrice: Double = 180.0,
    val launchWeek: Int = 1,
    val launchYear: Int = 2026,
    val totalMerchRevenueEarned: Double = 0.0,
    val totalTicketsSold: Long = 0,
    val totalGrossRevenue: Double = 0.0
) {
    val weightedAvgTicketPrice: Double
        get() = (generalTicketPrice * 0.70) + (vipTicketPrice * 0.25) + (platinumTicketPrice * 0.05)
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class SideJob(
    val id: String,
    val name: String,
    val weeklyPay: Double,
    val energyCost: Int,
    val timeCost: Int,
    val isActive: Boolean = false
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Stock(
    val id: String,
    val name: String,
    val currentPrice: Double,
    val quantity: Int = 0,
    val weeklyDividendPercent: Float, // e.g. 0.02f for 2%
    val connectedTo: String? = null, // e.g. "MediaGiant" (connected to tours)
    val history: List<Double> = emptyList(), // historical prices
    val sector: String = "Technology",
    val quarterlyEarnings: Double = 1500000.0,
    val PE_Ratio: Double = 14.5,
    val dividendYieldPercent: Float = 2.4f,
    val marketCap: Double = 45000000.0,
    val financialHealth: String = "Stable", // "Strong", "Stable", "Risky"
    val averagePurchasePrice: Double = 0.0,
    val initialPrice: Double = 0.0,
    val allTimeHigh: Double = 0.0,
    val analystPrediction: String = "", // "RISE" or "FALL" (from 80%+ rated accountants)
    val predictionConfidence: Int = 0,
    val predictionReason: String = "",
    val isCheapOpportunity: Boolean = false, // High conviction dislocated value entry
    val isPlayerCompany: Boolean = false,
    val playerVentureId: String? = null,
    val totalIpoShares: Long = 0L,
    val ipoSharesSold: Long = 0L,
    val ipoCompleted: Boolean = false,
    val founderEquityPercent: Double = 1.0
) {
    fun recentChangePercent(): Double {
        if (history.size < 2 || history.first() <= 0.0) return 0.0
        return ((currentPrice - history.first()) / history.first()) * 100.0
    }

    fun valuationStatus(): String {
        return when {
            PE_Ratio <= 13.0 -> "Undervalued (Value Buy)"
            PE_Ratio >= 28.0 -> "Premium (High Growth)"
            else -> "Fair Value"
        }
    }
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Crypto(
    val id: String,
    val name: String,
    val currentPrice: Double,
    val quantity: Double = 0.0,
    val history: List<Double> = emptyList(),
    val marketCap: Double = 8500000.0,
    val volume24h: Double = 250000.0,
    val supportLevel: Double = 0.0,
    val resistanceLevel: Double = 0.0,
    val developerActivity: String = "High", // "High", "Medium", "Low"
    val averagePurchasePrice: Double = 0.0,
    val initialPrice: Double = 0.0,
    val allTimeHigh: Double = 0.0,
    val cycleFloor: Double = 0.0,
    val analystPrediction: String = "", // "RISE" or "FALL" (from 80%+ rated crypto analysts)
    val predictionConfidence: Int = 0,
    val predictionReason: String = "",
    val isCheapOpportunity: Boolean = false, // High conviction dislocated value entry
    val circulatingSupply: Double = 0.0
) {
    fun recentChangePercent(): Double {
        if (history.size < 2 || history.first() <= 0.0) return 0.0
        return ((currentPrice - history.first()) / history.first()) * 100.0
    }

    fun technicalZone(): String {
        if (supportLevel > 0.0 && currentPrice <= supportLevel * 1.10) {
            return "At Major Support (Accumulation Zone)"
        }
        if (resistanceLevel > 0.0 && currentPrice >= resistanceLevel * 0.90) {
            return "At Resistance (Take-Profit Zone)"
        }
        return "Consolidating in Range"
    }

    fun volatilityCategory(): String {
        val change = kotlin.math.abs(recentChangePercent())
        return when {
            change >= 25.0 -> "Big Shift"
            change >= 7.0 -> "Medium Wave"
            else -> "Little Shift"
        }
    }
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class MarketSentimentInfo(
    val score: Int = 50,
    val label: String = "Neutral",
    val summary: String = "",
    val actionableTip: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class LabelPromotionTeam(
    val id: String,
    val name: String,
    val weeklyCost: Double,
    val popularityBoost: Float
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class LabelManager(
    val id: String,
    val name: String,
    val weeklyCost: Double,
    val efficiency: Float
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class OwnRecordLabel(
    val name: String,
    val managers: List<LabelManager> = emptyList(),
    val promoTeams: List<LabelPromotionTeam> = emptyList(),
    val ownedPropertiesCount: Int = 0, // Reduces travel and studio costs for signed artists
    val signedNPCIds: List<String> = emptyList(),
    val accruedDebts: Double = 0.0,
    val companyCash: Double = 100000.0,
    val equityHeldByPlayer: Float = 100f,
    val valuation: Double = 2500000.0,
    val weeklyRoyaltiesEarned: Double = 0.0,
    val productionLevel: Int = 1,
    val marketingLevel: Int = 1,
    val touringLevel: Int = 1,
    val legalLevel: Int = 1,
    val arLevel: Int = 1,
    val coFounders: List<CoFounder> = emptyList(),
    val shareholders: List<Shareholder> = emptyList(),
    val boardMembers: List<BoardMember> = emptyList(),
    val pendingInvestorOffers: List<InvestorOffer> = emptyList(),
    val pendingSigningOffers: List<ArtistSigningOffer> = emptyList(),
    val dividendPayoutRate: Float = 10.0f, // % of profit distributed as dividend
    val ceoSalary: Double = 1500.0, // player's weekly salary from company
    val totalSharesIssued: Long = 1000000,
    val boardSatisfaction: Float = 80f,
    val marketShare: Float = 0.5f, // percentage of industry market share
    val labelReputation: Float = 70f
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class MailMessage(
    val id: String,
    val sender: String,
    val subject: String,
    val body: String,
    val cashOffer: Double = 0.0,
    val type: String, // "COLLAB", "FEATURE", "LABEL_OFFER", "NORMAL"
    val isRead: Boolean = false,
    val isAccepted: Boolean = false,
    val expiresWeek: Int,
    val expiresYear: Int,
    val labelCut: Float = 0.0f,
    val labelId: String? = null,
    val labelRole: String? = null,
    val labelLevel: Int = 1,
    val contractDurationWeeks: Int = 52,
    val startWeek: Int = 0,
    val startYear: Int = 0,
    val endWeek: Int = 0,
    val endYear: Int = 0,
    val reqSingles: Int = 0,
    val reqAlbumsLP: Int = 0,
    val reqAlbumsEP: Int = 0,
    val reqMeTubeVideos: Int = 0,
    val contractTemplate: String = "VERTEX",
    val labelLevelText: String = "major",
    val ventureId: String? = null,
    val ventureOfferType: String? = null, // "EQUITY" or "BUYOUT"
    val ventureEquityPercent: Double = 0.0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class NPCMemory(
    val id: String,
    val description: String,
    val emotionalImpact: Float, // negative for bad, positive for good (-100 to 100)
    val memoryType: String, // HELPED, INSULTED, REJECTED, PROMOTED, COLLABORATED, SLAMMED
    val weekRecorded: Int,
    val yearRecorded: Int,
    val isCoreMemory: Boolean = false,
    val decayRate: Float = 4.0f
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class NPCEntity(
    val id: String,
    val name: String,
    val popularity: Float,
    val influence: Float,
    val fans: Long,
    val isOnTour: Boolean = false,
    val tourMarkets: List<String> = emptyList(),
    val weeklySales: Double = 0.0,
    val bestSongTitle: String = "Unknown",
    val bestSongRating: Float = 3.0f,
    val bestSongStreams: Long = 0,
    val bestSongGenre: String = "",
    val bestSongHype: Float = 0f,
    val bestSongReleaseWeek: Int = 0,
    val bestSongReleaseYear: Int = 0,

    // Dynamic simulated career fields
    val age: Int = 23,
    val personality: String = "Charismatic", // Charismatic, Ambitious, Creative, Rebel, Hustler, Diva
    val confidence: Float = 75f,
    val creativity: Float = 70f,
    val businessIntel: Float = 60f,
    val genrePreference: String = "Pop",
    val wealth: Double = 35000.0,
    val stress: Float = 25f,
    val mentalHealth: Float = 85f,
    val happiness: Float = 80f,
    val relationshipWithPlayer: Float = 50f, // 0 = Bitter Rival, 50 = Neutral, 100 = Best Friend / Ally
    val isRetired: Boolean = false,
    val currentRecordLabel: String? = "Independent",
    val managerName: String? = "None",
    val careerGoal: String = "Superstardom",

    // --- HUMAN PSYCHOLOGY ENGINE ADDITIONS ---
    // Core Values (0 - 100)
    val integrity: Float = 50f,
    val generosity: Float = 50f,
    val pride: Float = 50f,
    val humility: Float = 50f,
    val loyalty: Float = 50f,
    val trust: Float = 50f,
    val jealousy: Float = 30f,
    val greed: Float = 40f,

    // Psychological Profile Metrics (0 - 100)
    val ego: Float = 50f,
    val patience: Float = 50f,
    val emotionalIntel: Float = 50f,
    val discipline: Float = 50f,
    val curiosity: Float = 50f,
    val leadership: Float = 50f,
    val wisdom: Float = 50f,
    val compassion: Float = 50f,
    val forgiveness: Float = 50f,
    val gratitude: Float = 50f,
    val courage: Float = 50f,
    val stressTolerance: Float = 50f,
    val riskTolerance: Float = 50f,
    val workEthic: Float = 50f,
    val adaptability: Float = 50f,
    val selfAwareness: Float = 50f,

    // Strengths, Weaknesses, Fears & Dreams
    val strength: String = "Highly Creative",
    val weakness: String = "Egoistic",
    val coreFear: String = "Obscurity",
    val coreDream: String = "Grammy Achievement",

    // Emotional State & Communication
    val emotionalState: String = "Calm", // Happy, Excited, Inspired, Burned Out, Heartbroken, Lonely, Jealous, Angry, Calm, etc.
    val communicationStyle: String = "Diplomatic", // Direct, Diplomatic, Reserved, Aggressive, Emotional, Calm, Funny
    val decisionStyle: String = "Balanced", // Analytical, Emotional, Impulsive, Strategic

    // Memory System
    val memories: List<NPCMemory> = emptyList(),

    // Detailed Opinions about the Player (0 - 100)
    val professionalRespect: Float = 50f,
    val personalTrust: Float = 50f,
    val creativeRespect: Float = 50f,
    val leadershipRespect: Float = 50f,
    val competenceOpinion: Float = 50f,
    val businessTrust: Float = 50f,
    val financialTrust: Float = 50f,
    val leadershipTrust: Float = 50f,
    val confidentiality: Float = 50f,
    val reliability: Float = 50f,
    val publicReputation: Float = 50f,
    val networks: List<String> = emptyList(),

    // Life Events Log
    val lifeEvents: List<String> = emptyList(),

    // Self Reflection Notes
    val latestSelfReflection: String = "",
    val isFollowedByPlayer: Boolean = false,
    val lastInteractionWeek: Int = 0,
    val lastInteractionYear: Int = 0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class BusinessAsset(
    val id: String,
    val name: String,
    val type: String, // "Recording Studio", "Talent Agency", "Fashion Brand", "Real Estate", "Streaming platform shares"
    val purchaseCost: Double,
    val weeklyProfit: Double,
    val energyBonus: Int = 0,
    val ratingBonus: Float = 0f
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class PurchaseRequest(
    val id: String,
    val playerEmail: String,
    val playerName: String,
    val type: String, // "TIME_SLOT", "ENERGY", "CASH"
    val amount: Int,
    val status: String, // "PENDING", "APPROVED", "REJECTED"
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class MeTubeVideo(
    val id: String,
    val title: String,
    val type: String, // "MUSIC_VIDEO", "BEHIND_THE_SCENES", "LIVE_PERFORMANCE", "INTERVIEW", "VLOG"
    val views: Long,
    val likes: Long,
    val comments: List<String> = emptyList(),
    val revenueEarned: Double = 0.0,
    val weekPublished: Int,
    val yearPublished: Int,
    val thumbnailId: String = "ic_video",
    val artistName: String? = null,
    val customThumbnail: String? = null
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class CastPost(
    val id: String,
    val authorName: String,
    val authorHandle: String,
    val content: String,
    val likes: Long = 0,
    val reposts: Long = 0,
    val commentsCount: Long = 0,
    val week: Int,
    val year: Int,
    val isByPlayer: Boolean = false,
    val isLikedByPlayer: Boolean = false,
    val imageRes: String? = null
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ChatMessage(
    val id: String,
    val senderId: String, // NPC ID or "PLAYER"
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ChatConversation(
    val npcId: String,
    val npcName: String,
    val messages: List<ChatMessage> = emptyList(),
    val unreadCount: Int = 0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class PendingChatReply(
    val id: String,
    val npcId: String,
    val replyText: String,
    val scheduledYear: Int,
    val scheduledWeek: Int,
    val relationChange: Float = 0f,
    val addedCash: Double = 0.0,
    val toastMessage: String? = null
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Employee(
    val id: String,
    val name: String,
    val role: String, // "Accountant", "Financial Advisor", "Crypto Analyst", "Talent Agent", "PR Manager", "Business Manager", "Lawyer", "Tour Manager", "Music Producer", "Personal Assistant", "Security"
    val weeklySalary: Double,
    val experience: Int, // 1 to 100
    val reliability: Float, // 10 to 100
    val honesty: Float, // 10 to 100
    val greed: Float, // 10 to 100
    val intelligence: Float, // 10 to 100
    val leadership: Float, // 10 to 100
    val loyalty: Float = 60f, // starts at 60
    val isRetired: Boolean = false,
    val traitDescription: String = "Experienced",
    val age: Int = 30,
    val contractWeeks: Int = 12, // Duration of contract
    val integrity: Float = 60f,
    val communication: Float = 60f,
    val efficiency: Float = 60f,
    val negotiation: Float = 60f,
    val ambition: Float = 60f,
    val stressLevel: Float = 10f,
    val happiness: Float = 80f,
    val workload: Float = 20f,
    val reputation: Float = 70f,
    val careerHistory: List<String> = emptyList(),
    val permissions: Map<String, Boolean> = emptyMap(),
    val rating: Float = 4.5f, // unique rating (1.0 to 5.0 stars)
    val investorStyle: String = "Balanced" // for Accountant / Crypto Analyst ("Long-Term", "Daily Trader", "Balanced")
) {
    val skillRatingPercent: Int
        get() {
            val calculated = (experience * 0.40f) + (intelligence * 0.30f) + (reliability * 0.20f) + (leadership * 0.10f)
            return calculated.toInt().coerceIn(20, 99)
        }

    val experienceYears: Int
        get() = (experience / 3.5f).toInt().coerceIn(1, 35)

    val hasStubbornTrait: Boolean
        get() = (role.contains("Crypto", ignoreCase = true) || role.contains("Accountant", ignoreCase = true) || role.contains("Financial", ignoreCase = true)) && (skillRatingPercent >= 80 || rating >= 4.0f)
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class StockOrder(
    val id: String,
    val stockId: String,
    val type: String, // "LIMIT_BUY", "LIMIT_SELL"
    val targetPrice: Double,
    val quantity: Int
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class CryptoOrder(
    val id: String,
    val cryptoId: String,
    val type: String, // "LIMIT_BUY", "LIMIT_SELL", "STOP_LOSS", "TAKE_PROFIT"
    val targetPrice: Double,
    val quantity: Double
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class BrandDeal(
    val id: String,
    val brandName: String,
    val tier: String, // "LUXURY", "PREMIUM", "STANDARD"
    val weeklyPayment: Double,
    val durationWeeks: Int,
    val weeksRemaining: Int,
    val conditions: List<String> = emptyList(), // e.g. "No public controversies", "Post twice monthly"
    val signedWeek: Int,
    val signedYear: Int
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class NewsArticle(
    val id: String,
    val headline: String,
    val subtitle: String,
    val body: String, // 3–5 sentences generated from simulation events
    val author: String,
    val outlet: String, // "The Music Wire", "Billboard Daily", "Industry Insider", "Street Report"
    val category: String, // "Music", "Business", "Scandal", "Market", "Entertainment"
    val coverImageId: String, // drawable resource ID or placeholder
    val week: Int,
    val year: Int,
    val relatedArtistName: String? = null
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class AdAgency(
    val id: String,
    val name: String,
    val specialty: String, // "Social Media", "Radio", "Streaming", "Billboards", "TV"
    val audienceReach: Long,
    val conversionRate: Float, // 0.01 to 0.15
    val pricePerWeek: Double,
    val credibility: Float // affects actual results vs promised
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class AdCampaign(
    val id: String,
    val agencyId: String,
    val agencyName: String,
    val targetType: String, // "SONG", "TOUR", "BRAND"
    val targetId: String,
    val weeklyBudget: Double,
    val durationWeeks: Int,
    val weeksRemaining: Int,
    val objective: String, // "STREAMS", "FOLLOWERS", "TOUR_ATTENDANCE", "BRAND_AWARENESS"
    val estimatedReachPerWeek: Long
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class MerchStyle(
    val id: String,
    val name: String, // e.g. "Space Odyssey Hoodie"
    val type: String, // Caps, Hoodies, Shirts, Vinyls
    val unitCost: Double,
    val sellPrice: Double,
    val stockRemaining: Int,
    val totalSold: Int = 0,
    val totalRevenue: Double = 0.0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class CoFounder(
    val id: String,
    val name: String,
    val trait: String, // "Creative", "Aggressive", "Conservative", "Greedy", "Pragmatic"
    val equityShare: Float, // e.g. 15.0 for 15%
    val relationship: Float, // 0 to 100
    val salaryExpectation: Double,
    val monthlyDemands: String = "None"
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class InvestorOffer(
    val id: String,
    val investorName: String,
    val investorType: String, // "Angel", "Venture Capital", "Private Equity"
    val cashOffer: Double,
    val equityRequested: Float, // e.g. 10.0 for 10%
    val minimumValuation: Double,
    val durationWeeksExpires: Int
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Shareholder(
    val name: String,
    val equityShare: Float, // e.g. 50.0 for 50%
    val type: String, // "Founder" (Player), "Co-Founder", "Angel Investor", "Venture Capital", "Retail Public"
    val boardSeat: Boolean = false,
    val satisfaction: Float = 75f // 0 to 100
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class BoardMember(
    val id: String,
    val name: String,
    val type: String, // "Player", "Co-Founder", "Investor Representative"
    val powerWeight: Float, // voting power, usually proportional to equity or flat
    val satisfaction: Float = 75f, // 0 to 100
    val alignment: String = "Neutral" // "Growth", "Dividends", "Safety", "Neutral"
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ArtistSigningOffer(
    val id: String,
    val artistId: String,
    val artistName: String,
    val requestedRevenueShare: Float, // e.g. 50.0 for 50% cut to artist
    val requestedSigningBonus: Double,
    val contractDurationWeeks: Int,
    val weeklyExpenseLimit: Double,
    val preferredGenre: String
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ChartSong(
    val id: String,
    val title: String,
    val artistName: String,
    val artistId: String, // "player" or npcId
    val genreName: String,
    val rating: Float,
    val weeklyStreams: Long,
    val totalStreams: Long,
    val weeksOnChart: Int,
    val peakRank: Int,
    val currentRank: Int = 0,
    val isPlayer: Boolean = false,
    val releasedWeek: Int,
    val releasedYear: Int,
    val hype: Float = 50f,
    val lastGenreTrendMultiplier: Float = 1f
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class WardrobeItem(
    val id: String,
    val name: String,
    val category: String, // "HAIR", "CLOTHES", "JEWELRY"
    val cost: Double,
    val desc: String = ""
) {
    val description: String
        get() = desc
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class Album(
    val id: String,
    val title: String,
    val isEP: Boolean, // true = EP, false = Full Album (LP)
    val songIds: List<String>,
    val weekCreated: Int,
    val yearCreated: Int,
    val rating: Float, // Rated 1.0 to 5.0 based on songs composition, synergy, flow, production
    val criticalAcclaim: Float, // 10 to 100
    val totalStreams: Long = 0,
    val sales: Long = 0,
    val price: Double = 9.99,
    val isReleased: Boolean = false,
    val isUploadedMap: Map<String, Long> = emptyMap(), // platformId to streamCount
    val hype: Float = 10f,
    val weeksSinceRelease: Int = 0,
    val albumCutEarned: Double = 0.0,
    val marketingTier: String = "None", // None, Budget, Standard, Cinematic, Global Campaign
    val promoWeeksLeft: Int = 0,
    val promoWeeklyBoost: Float = 0f,
    val coverArt: String? = null,
    val reviews: List<String> = emptyList()
)

fun NPCEntity.withPsychology(random: kotlin.random.Random): NPCEntity {
    val pers = this.personality

    // Core values
    var pIntegrity = random.nextFloat() * 40f + 30f
    var pGenerosity = random.nextFloat() * 40f + 30f
    var pPride = random.nextFloat() * 40f + 30f
    var pHumility = random.nextFloat() * 40f + 30f
    var pLoyalty = random.nextFloat() * 40f + 30f
    var pTrust = random.nextFloat() * 40f + 30f
    var pJealousy = random.nextFloat() * 30f + 15f
    var pGreed = random.nextFloat() * 30f + 20f

    // Psychological Profile Metrics
    var pEgo = random.nextFloat() * 40f + 30f
    var pPatience = random.nextFloat() * 40f + 30f
    var pEmotionalIntel = random.nextFloat() * 40f + 30f
    var pDiscipline = random.nextFloat() * 40f + 30f
    var pCuriosity = random.nextFloat() * 40f + 30f
    var pLeadership = random.nextFloat() * 40f + 30f
    var pWisdom = random.nextFloat() * 40f + 30f
    var pCompassion = random.nextFloat() * 40f + 30f
    var pForgiveness = random.nextFloat() * 40f + 30f
    var pGratitude = random.nextFloat() * 40f + 30f
    var pCourage = random.nextFloat() * 40f + 30f
    var pStressTolerance = random.nextFloat() * 40f + 30f
    var pRiskTolerance = random.nextFloat() * 40f + 30f
    var pWorkEthic = random.nextFloat() * 40f + 30f
    var pAdaptability = random.nextFloat() * 40f + 30f
    var pSelfAwareness = random.nextFloat() * 40f + 30f

    var pStrength = "Highly Creative"
    var pWeakness = "Egoistic"
    var pCoreFear = "Obscurity"
    var pCoreDream = "Grammy Achievement"
    var pCommStyle = "Diplomatic"
    var pDecStyle = "Balanced"

    when (pers) {
        "Charismatic" -> {
            pIntegrity += 20f
            pGenerosity += 20f
            pHumility += 15f
            pJealousy -= 15f
            pEmotionalIntel += 25f
            pLeadership += 20f
            pStrength = "Charming Personality"
            pWeakness = "People Pleaser"
            pCoreFear = "Public Rejection"
            pCoreDream = "Beloved Global Icon"
            pCommStyle = listOf("Diplomatic", "Funny").random(random)
            pDecStyle = "Balanced"
        }
        "Ambitious" -> {
            pHumility -= 15f
            pPride += 20f
            pJealousy += 20f
            pGreed += 20f
            pPatience -= 15f
            pEgo += 25f
            pDiscipline += 15f
            pStrength = "Laser Focused"
            pWeakness = "Ruthless Drive"
            pCoreFear = "Failure"
            pCoreDream = "Billionaire Mogul"
            pCommStyle = listOf("Direct", "Aggressive").random(random)
            pDecStyle = "Strategic"
        }
        "Creative" -> {
            pDiscipline -= 15f
            pCuriosity += 25f
            pAdaptability += 15f
            pStressTolerance -= 15f
            pWisdom += 10f
            pStrength = "Brilliant Artistry"
            pWeakness = "Fragile Mental State"
            pCoreFear = "Creative Burnout"
            pCoreDream = "Time-tested Masterpiece"
            pCommStyle = "Emotional"
            pDecStyle = "Emotional"
        }
        "Rebel" -> {
            pPride += 20f
            pIntegrity -= 10f
            pRiskTolerance += 25f
            pPatience -= 20f
            pCourage += 20f
            pStrength = "Uncompromising Authenticity"
            pWeakness = "Self Destructive"
            pCoreFear = "Conformity"
            pCoreDream = "Cultural Revolution"
            pCommStyle = listOf("Aggressive", "Direct").random(random)
            pDecStyle = "Impulsive"
        }
        "Hustler" -> {
            pGreed += 20f
            pDiscipline += 20f
            pRiskTolerance += 15f
            pWorkEthic += 25f
            pSelfAwareness += 10f
            pStrength = "Brilliant Business Mind"
            pWeakness = "Materialistic"
            pCoreFear = "Bankruptcy"
            pCoreDream = "Financial Empire"
            pCommStyle = listOf("Direct", "Calm").random(random)
            pDecStyle = "Analytical"
        }
        "Diva" -> {
            pPride += 25f
            pEgo += 25f
            pHumility -= 20f
            pJealousy += 20f
            pStressTolerance -= 15f
            pEmotionalIntel += 10f
            pStrength = "Commanding Presence"
            pWeakness = "Diva Demands"
            pCoreFear = "Obscurity"
            pCoreDream = "Superstardom"
            pCommStyle = listOf("Emotional", "Aggressive").random(random)
            pDecStyle = "Emotional"
        }
    }

    // Specific logic for special NPCs (like Mom)
    if (this.id == "npc_family_mom") {
        pIntegrity = 95f
        pGenerosity = 98f
        pHumility = 80f
        pJealousy = 0f
        pLoyalty = 100f
        pCompassion = 95f
        pForgiveness = 95f
        pStrength = "Infinite Love"
        pWeakness = "Overprotective"
        pCoreFear = "Your Sadness"
        pCoreDream = "Your Ultimate Happiness"
        pCommStyle = "Diplomatic"
    } else if (this.id == "npc_gov_sterling") {
        pIntegrity = 40f
        pPride = 80f
        pLeadership = 85f
        pStrength = "Political Authority"
        pWeakness = "Bureaucratic Pride"
        pCoreFear = "Loss of Power"
        pCoreDream = "Electoral Dominance"
    } else if (this.id == "npc_sen_vance") {
        pIntegrity = 30f
        pGreed = 75f
        pDiscipline = 85f
        pStrength = "Strategic Alliance"
        pWeakness = "Transactional View"
        pCoreFear = "Scandal Humiliation"
        pCoreDream = "White House Presidency"
    }

    val pNetworks = when {
        this.id.startsWith("npc_family_") -> listOf("Family Network", "Friend Network", "Hometown Network")
        this.id == "npc_gov_sterling" || this.id == "npc_sen_vance" -> listOf("Government Network", "Business Network", "Media Network")
        else -> {
            val possible = mutableListOf("Music Industry Network")
            if (pers == "Ambitious" || pers == "Hustler") {
                possible.add("Business Network")
                possible.add("Investment Network")
            }
            if (pers == "Diva" || pers == "Charismatic") {
                possible.add("Social Media Network")
                possible.add("Entertainment Network")
            }
            if (pers == "Creative" || pers == "Rebel") {
                possible.add("Friend Network")
            }
            // Add a few random ones to keep it overlapping
            val otherPool = listOf("Church / Faith Community", "University Network", "Technology Network", "Film Industry", "Fashion Industry", "Sports Industry", "Luxury Brand Network", "International Network")
            possible.add(otherPool.random(random))
            possible.distinct().take(3)
        }
    }

    return this.copy(
        integrity = pIntegrity.coerceIn(0f, 100f),
        generosity = pGenerosity.coerceIn(0f, 100f),
        pride = pPride.coerceIn(0f, 100f),
        humility = pHumility.coerceIn(0f, 100f),
        loyalty = pLoyalty.coerceIn(0f, 100f),
        trust = pTrust.coerceIn(0f, 100f),
        jealousy = pJealousy.coerceIn(0f, 100f),
        greed = pGreed.coerceIn(0f, 100f),
        ego = pEgo.coerceIn(0f, 100f),
        patience = pPatience.coerceIn(0f, 100f),
        emotionalIntel = pEmotionalIntel.coerceIn(0f, 100f),
        discipline = pDiscipline.coerceIn(0f, 100f),
        curiosity = pCuriosity.coerceIn(0f, 100f),
        leadership = pLeadership.coerceIn(0f, 100f),
        wisdom = pWisdom.coerceIn(0f, 100f),
        compassion = pCompassion.coerceIn(0f, 100f),
        forgiveness = pForgiveness.coerceIn(0f, 100f),
        gratitude = pGratitude.coerceIn(0f, 100f),
        courage = pCourage.coerceIn(0f, 100f),
        stressTolerance = pStressTolerance.coerceIn(0f, 100f),
        riskTolerance = pRiskTolerance.coerceIn(0f, 100f),
        workEthic = pWorkEthic.coerceIn(0f, 100f),
        adaptability = pAdaptability.coerceIn(0f, 100f),
        selfAwareness = pSelfAwareness.coerceIn(0f, 100f),
        strength = pStrength,
        weakness = pWeakness,
        coreFear = pCoreFear,
        coreDream = pCoreDream,
        communicationStyle = pCommStyle,
        decisionStyle = pDecStyle,
        professionalRespect = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        personalTrust = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        creativeRespect = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        leadershipRespect = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        competenceOpinion = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        businessTrust = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        financialTrust = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        leadershipTrust = (random.nextFloat() * 30f + 40f).coerceIn(0f, 100f),
        confidentiality = (random.nextFloat() * 30f + 45f).coerceIn(0f, 100f),
        reliability = (random.nextFloat() * 30f + 45f).coerceIn(0f, 100f),
        publicReputation = this.popularity,
        networks = pNetworks
    )
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class WorldHistoryEntry(
    val id: String,
    val week: Int,
    val year: Int,
    val title: String,
    val description: String,
    val memoryType: String, // "PERSONAL", "RELATIONSHIP", "BUSINESS", "FINANCIAL", "MEDIA", "SPIRITUAL", "INDUSTRY", "COMMUNITY", "WORLD"
    val strength: String, // "TINY", "SMALL", "IMPORTANT", "MAJOR", "LIFE_CHANGING", "HISTORIC"
    val targetNpcId: String? = null,
    val targetNpcName: String? = null,
    val targetBusinessName: String? = null,
    val targetSongTitle: String? = null,
    val targetAlbumTitle: String? = null,
    val valueAffected: Float = 0f,
    val isPermanent: Boolean = false,
    val isNpcOnly: Boolean = false,
    val decayWeeksRemaining: Int = 1000
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class StatFeedbackItem(
    val label: String,
    val deltaText: String,
    val isPositive: Boolean = true,
    val category: String = "general" // "energy", "skill", "creative", "fans", "money", "stress", "influence", "image"
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ActionFeedback(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val deltas: List<StatFeedbackItem> = emptyList(),
    val iconType: String = "bolt",
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class CareerNewsFeedItem(
    val id: String,
    val category: String, // "BREAKING", "INDUSTRY", "SOCIAL", "FINANCE", "RUMOR"
    val headline: String,
    val detailedBody: String,
    val sourceOutlet: String,
    val week: Int,
    val year: Int,
    val severity: String = "NORMAL", // "HIGH", "NORMAL", "LOW"
    val isViral: Boolean = false,
    val actionSuggestion: String? = null
)





@com.squareup.moshi.JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class CapitalClubMember(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String, // "Male" or "Female"
    val title: String,
    val matchPct: String,
    val tag: String,
    val imageUrl: String,
    val quote: String,
    val tags: List<String>,
    val synergyText: String,
    val isDating: Boolean = false,
    val relationshipScore: Int = 0,
    val isSpouse: Boolean = false,
    val isLockedOut: Boolean = false, // If they reject the player
    val lastInteractionWeek: Int = 0,
    val lastInteractionYear: Int = 0
)


@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class BankLoanContract(
    val id: String = "",
    val borrowerType: String = "PERSONAL", // "PERSONAL" or "BUSINESS"
    val ventureId: String? = null,
    val ventureName: String? = null,
    val principal: Double = 0.0,
    val durationWeeks: Int = 4,
    val durationLabel: String = "1 Month", // "1 Month", "3 Months", "6 Months", "1 Year"
    val totalRepayable: Double = 0.0,
    val remainingBalance: Double = 0.0,
    val takenWeek: Int = 1,
    val takenYear: Int = 1,
    val dueWeek: Int = 5,
    val dueYear: Int = 1,
    val weeksRemaining: Int = 4,
    val isOverdue: Boolean = false,
    val weeklyLatePenaltyPercent: Float = 0.04f
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureInvestorShareholder(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val firm: String = "",
    val equityPercent: Double = 0.0,
    val colorHex: String = "#FFD700"
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureInvestorOffer(
    val id: String = "",
    val investorName: String = "",
    val investorFirm: String = "",
    val offerType: String = "EQUITY", // "EQUITY" or "BUYOUT"
    val cashAmount: Double = 0.0,
    val equityPercent: Double = 0.0, // e.g. 0.25 for 25%
    val expirationWeek: Int = 0,
    val expirationYear: Int = 0,
    val pitchMessage: String = "",
    val fairValue: Double = 0.0,
    val isTrickyOffer: Boolean = false,
    val valuationMultiplePercent: Double = 0.0, // e.g. -25.0 for discount, +10.0 for premium
    val companyValuationAtOffer: Double = 0.0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class RestaurantEquipment(
    val id: String,
    val name: String,
    val price: Double,
    val weeklyMaintenance: Double,
    val condition: Float = 1.0f, // 0.0f to 1.0f
    val isOwned: Boolean = false,
    val unlockTier: Int = 1
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class FarmCropBatch(
    val id: String,
    val cropName: String, // "Maize", "Rice", "Tomatoes", "Wheat", "Potatoes"
    val acres: Double = 1.0,
    val seedCost: Double = 150.0,
    val totalWeeks: Int = 12,
    val currentWeek: Int = 0,
    val isReadyToHarvest: Boolean = false,
    val expectedYieldUnits: Double = 100.0,
    val yieldUnitName: String = "bushels"
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class FarmMachine(
    val id: String,
    val name: String,
    val category: String, // "TOOL", "MACHINE", "VEHICLE", "STORAGE", "PROCESSING"
    val purchaseCost: Double,
    val weeklyRentalCost: Double,
    val isOwned: Boolean = false,
    val isRented: Boolean = false,
    val condition: Float = 1.0f,
    val bookValue: Double = 0.0,
    val weeklyMaintenance: Double = 0.0,
    val gameplayEffect: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class FarmLivestockGroup(
    val type: String, // "Chicken", "Goat", "Pig", "Cow", "Sheep"
    val count: Int = 0,
    val matureCount: Int = 0,
    val babyCount: Int = 0,
    val babyWeeksOld: List<Int> = emptyList(),
    val purchaseCostPerUnit: Double = 10.0,
    val matureSalePrice: Double = 5000.0,
    val weeksToMaturity: Int = 4,
    val weeklyFeedPerUnit: Double = 1.0,
    val weeklyVetPerUnit: Double = 0.5,
    val mainProducts: String = "Eggs & Meat",
    val weeklyProduceRevenuePerUnit: Double = 3.0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class FarmInventoryItem(
    val id: String,
    val name: String,
    val category: String, // "CROP", "FRUIT", "LIVESTOCK_PRODUCT", "PROCESSED"
    val quantity: Double = 0.0,
    val unit: String = "units",
    val marketPricePerUnit: Double = 0.0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class HotelAmenity(
    val id: String,
    val name: String,
    val purchaseCost: Double,
    val weeklyUpkeep: Double,
    val isOwned: Boolean = false,
    val occupancyBonus: Float = 0.05f,
    val adrBonus: Double = 0.0,
    val weeklyRevenueBonus: Double = 0.0,
    val unlockTier: Int = 1,
    val description: String = ""
) {
    val purchasePrice: Double get() = purchaseCost
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class HotelFurnitureItem(
    val category: String, // "Bed", "Mattress", "Wardrobe", "Desk", "Chair", "TV", "Mini fridge", "Air conditioner", "Curtains", "Lighting", "Bathroom fixtures", "Decoration", "Wi-Fi equipment"
    val name: String,
    val cost: Double,
    val qualityTier: String // "Basic", "Comfort", "Premium", "Luxury"
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class HotelRoom(
    val roomNumber: Int,
    val isUnlocked: Boolean = false,
    val condition: Float = 1.0f,
    val isOutOfOrder: Boolean = false,
    val outOfOrderReason: String = "",
    val furnishings: List<HotelFurnitureItem> = emptyList(),
    val weeklyOccupancyRate: Float = 0.65f,
    val lodgesThisWeek: Int = 5,
    val weeklyRevenue: Double = 0.0
) {
    val totalFurnishingInvestment: Double get() = furnishings.sumOf { it.cost }
    val roomTier: String get() = when {
        totalFurnishingInvestment >= 10000.0 -> "Luxury Room"
        totalFurnishingInvestment >= 5000.0 -> "Premium Room"
        totalFurnishingInvestment >= 2500.0 -> "Comfort Room"
        totalFurnishingInvestment >= 900.0 -> "Basic Room"
        else -> "Unfurnished Suite"
    }
    val potentialWeeklyRate: Double get() = when {
        totalFurnishingInvestment >= 10000.0 -> 520.0
        totalFurnishingInvestment >= 5000.0 -> 350.0
        totalFurnishingInvestment >= 2500.0 -> 250.0
        totalFurnishingInvestment >= 900.0 -> 182.0
        else -> 50.0
    }
}

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureTaxiState(
    val taxiCarCount: Int = 1,
    val taxiDriverCount: Int = 1,
    val taxiMechanicCount: Int = 0,
    val taxiDispatcherCount: Int = 0,
    val taxiCondition: Float = 1.0f, // 0.0 to 1.0
    val taxiOdometerMiles: Double = 0.0,
    val taxiWeeklyTripsCompleted: Int = 0,
    val taxiWeeklyFaresEarned: Double = 0.0,
    val taxiWeeklyFuelCost: Double = 0.0,
    val taxiWeeklyDriverSalary: Double = 0.0,
    val taxiWeeklyMechanicSalary: Double = 0.0,
    val taxiWeeklyDispatcherSalary: Double = 0.0,
    val taxiWeeklyMaintenanceCost: Double = 0.0,
    val taxiWeeklyParkingSpaceTax: Double = 0.0,
    val taxiWeeklyParkingSpaceMaint: Double = 0.0,
    val taxiDepotLevel: Int = 1,
    val taxiDepotCapacity: Int = 5
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureRestaurantState(
    val waiterCount: Int = 1,
    val chefCount: Int = 1,
    val seniorChefCount: Int = 0,
    val cleanerCount: Int = 0,
    val managerCount: Int = 0,
    val weeklyCustomersServed: Int = 0,
    val weeklyFoodSales: Double = 0.0,
    val weeklyIngredientCost: Double = 0.0,
    val weeklyBuildingMaintenanceCost: Double = 0.0,
    val weeklyStaffPayroll: Double = 0.0,
    val weeklyEquipmentMaintenanceCost: Double = 0.0,
    val equipmentList: List<RestaurantEquipment> = emptyList(),
    val serviceEfficiency: Float = 1.0f,
    val restaurantBonusCapacity: Int = 0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureFarmState(
    val farmLandTier: String = "SMALL_PLOT", // "SMALL_PLOT", "MEDIUM_FARM", "LARGE_FARM", "INDUSTRIAL_FARMLAND"
    val farmLandIsOwned: Boolean = false, // false = renting, true = owned
    val farmLandAcres: Double = 10.0,
    val farmLandValue: Double = 8000.0,
    val farmLandRentWeekly: Double = 125.0,
    val farmCropBatches: List<FarmCropBatch> = emptyList(),
    val farmLivestock: List<FarmLivestockGroup> = emptyList(),
    val farmMachinery: List<FarmMachine> = emptyList(),
    val farmHandCount: Int = 1,
    val farmTractorOperatorCount: Int = 0,
    val farmLivestockWorkerCount: Int = 0,
    val farmDriverCount: Int = 0,
    val farmVeterinarianCount: Int = 0,
    val farmManagerCount: Int = 0,
    val farmCleanliness: Float = 1.0f, // 0.0 to 1.0; low cleanliness causes crops & livestock mortality
    val farmInventory: List<FarmInventoryItem> = emptyList(),
    val farmWeeklyRawCropsRevenue: Double = 0.0,
    val farmWeeklyFruitsRevenue: Double = 0.0,
    val farmWeeklyLivestockRevenue: Double = 0.0,
    val farmWeeklyProcessedRevenue: Double = 0.0,
    val farmWeeklyFeedExpense: Double = 0.0,
    val farmWeeklyLandRentExpense: Double = 125.0,
    val farmWeeklyMachineUpkeepExpense: Double = 0.0,
    val farmWeeklyPayrollExpense: Double = 350.0,
    val farmWeeklySeedExpense: Double = 0.0,
    val farmLastWeatherEvent: String = "Optimal Sunshine (+15% Yield)"
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureHotelState(
    val hotelRoomCount: Int = 2,
    val hotelMaxRoomCapacity: Int = 20,
    val hotelNightlyRate: Double = 50.0,
    val hotelOccupancyRate: Float = 0.65f,
    val hotelCondition: Float = 0.95f,
    val hotelCleanliness: Float = 0.90f,
    val hotelReputation: Float = 0.85f,
    val hotelHousekeeperCount: Int = 1,
    val hotelFrontDeskCount: Int = 1,
    val hotelChefCount: Int = 0,
    val hotelWaiterCount: Int = 0,
    val hotelSecurityCount: Int = 0,
    val hotelMaintenanceTechCount: Int = 0,
    val hotelConciergeCount: Int = 0,
    val hotelGeneralManagerCount: Int = 0,
    val hotelAmenities: List<HotelAmenity> = emptyList(),
    val hotelRooms: List<HotelRoom> = emptyList(),
    val hotelTotalLodgesThisWeek: Int = 0,
    val hotelWeeklyRoomRevenue: Double = 0.0,
    val hotelWeeklyAmenityRevenue: Double = 0.0,
    val hotelWeeklyFoodRevenue: Double = 0.0,
    val hotelWeeklyPayrollCost: Double = 0.0,
    val hotelWeeklySuppliesCost: Double = 0.0,
    val hotelWeeklyUtilityCost: Double = 0.0,
    val hotelWeeklyMaintenanceCost: Double = 0.0,
    val hotelSeasonText: String = "Peak Spring Vacation (+10% Occupancy)",
    val hotelLastIncident: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureCryptoState(
    val cryptoTotalSupply: Double = 20_000_000.0,
    val cryptoTokenPrice: Double = 0.05,
    val cryptoLiquidityPoolUsd: Double = 10_000.0,
    val cryptoWeeklyTradingVolume: Double = 40_000.0,
    val cryptoStakedTokens: Double = 1_000_000.0,
    val cryptoCommunitySentiment: Float = 0.75f,
    val cryptoMarketCycle: String = "Bullish Accumulation",
    val cryptoModCount: Int = 1,
    val cryptoSolidityDevCount: Int = 0,
    val cryptoSecurityAuditorCount: Int = 0,
    val cryptoMarketerCount: Int = 0,
    val cryptoWeeklySwapFeesRevenue: Double = 0.0,
    val cryptoWeeklyStakingFeesRevenue: Double = 0.0,
    val cryptoWeeklyDeFiYieldRevenue: Double = 0.0,
    val cryptoWeeklyServerInfraCost: Double = 0.0,
    val cryptoWeeklyPayrollCost: Double = 0.0,
    val cryptoWeeklySecurityCost: Double = 0.0,
    val cryptoTokenTicker: String = "NOVA",
    val cryptoTokenName: String = "Nova Protocol",
    val cryptoCirculatingSupply: Double = 5_000_000.0,
    val cryptoActiveUsers: Int = 200,
    val cryptoDevelopersCount: Int = 1,
    val cryptoSecurityTier: Int = 1,
    val cryptoWebsiteAppTier: Int = 1,
    val cryptoMarketingTier: Int = 1,
    val cryptoIsMarketListed: Boolean = false,
    val cryptoMarketListingNotice: String = "",
    val cryptoLastHackLoss: Double = 0.0,
    val cryptoNewServiceLaunchBoostWeeks: Int = 0,
    val cryptoLastServiceLaunched: String = "",
    val cryptoFounderTokensOwned: Double = 2_000_000.0,
    val cryptoWeeklyProtocolFeeRevenue: Double = 0.0,
    val cryptoEducationalLesson: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureListingState(
    val isListedOnWeShare: Boolean = false,
    val weShareListedEquityPercent: Double = 0.0, // e.g. 0.20 for 20%
    val weShareTotalSharesIssued: Long = 0L,
    val weSharePublicSharesSold: Long = 0L,
    val weShareSharePrice: Double = 0.0,
    val weShareStockId: String? = null,
    val screeningStatus: String = "NONE", // "NONE", "UNDER_REVIEW", "APPROVED", "REJECTED"
    val screeningWeeksRemaining: Int = 0,
    val screeningRequestedEquityPercent: Double = 0.20,
    val screeningType: String = "WESHARE", // "WESHARE" or "GENESIS"
    val approvedSharePrice: Double = 0.0,
    val approvedTotalShares: Long = 0L,
    val approvedListedSharesUnits: Long = 0L,
    val screeningFeedback: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class VentureGovernanceState(
    val recurringCustomers: Int = 50,
    val marketingCampaign: String = "NONE", // "NONE", "LOCAL_ADS", "DIGITAL_ADS", "PR_BLITZ"
    val marketingWeeksActive: Int = 0,
    val marketingWeeklyCost: Double = 0.0,
    val hasLegalLicense: Boolean = false,
    val legalLicenseCost: Double = 15000.0,
    val sccWarningReceived: Boolean = false,
    val sccDeadlineWeeks: Int = 0,
    val isShutDownByRegulators: Boolean = false,
    val pocketedInvestorCapital: Double = 0.0,
    val investorLawsuitActive: Boolean = false,
    val investorLawsuitDamages: Double = 0.0,
    val grossEnterpriseValuation: Double = 0.0,
    val lastOwnerAction: String = "",
    val expansionCount: Int = 0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class BusinessVenture(
    val id: String,
    val name: String,
    val type: String,
    val setupCost: Double,
    val minStartingCapital: Double = 0.0,
    val isOwned: Boolean = false,
    val investedCapital: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val accumulatedProfit: Double = 0.0,
    val baseWeeklyRoi: Double = 0.05,
    val history: List<Double> = emptyList(),
    // Fixed Revenue, Operating Expenses & Upgrades
    val tierLevel: Int = 1,
    val tierName: String = "",
    val unitCount: Int = 1, // Taxi cars (1..25), Hotel furnished rooms (2..20)
    val weeklyGrossRevenue: Double = 0.0,
    val weeklyOperatingExpenses: Double = 0.0,
    val weeklyNetProfit: Double = 0.0,
    val totalGrossEarned: Double = 0.0,
    val totalExpensesPaid: Double = 0.0,
    val totalCapitalInvested: Double = 0.0,
    // Private Investor Equity Deals
    val investorEquityPercent: Double = 0.0, // Legacy fallback
    val investorName: String? = null,
    val pendingInvestorOffer: VentureInvestorOffer? = null,
    val investors: List<VentureInvestorShareholder> = emptyList(),
    // Operational History, Loan & Customer Metrics
    val weeksActive: Int = 0,
    val customerLoyalty: Float = 0.65f, // 0.0 to 1.0 (returning customer rate)
    val outstandingLoan: Double = 0.0,
    // Subsystem states
    val taxi: VentureTaxiState = VentureTaxiState(),
    val restaurant: VentureRestaurantState = VentureRestaurantState(),
    val farm: VentureFarmState = VentureFarmState(),
    val hotel: VentureHotelState = VentureHotelState(),
    val crypto: VentureCryptoState = VentureCryptoState(),
    val listing: VentureListingState = VentureListingState(),
    val governance: VentureGovernanceState = VentureGovernanceState()
) {
    // Taxi Fleet Getters
    val taxiCarCount: Int get() = taxi.taxiCarCount
    val taxiDriverCount: Int get() = taxi.taxiDriverCount
    val taxiMechanicCount: Int get() = taxi.taxiMechanicCount
    val taxiDispatcherCount: Int get() = taxi.taxiDispatcherCount
    val taxiCondition: Float get() = taxi.taxiCondition
    val taxiOdometerMiles: Double get() = taxi.taxiOdometerMiles
    val taxiWeeklyTripsCompleted: Int get() = taxi.taxiWeeklyTripsCompleted
    val taxiWeeklyFaresEarned: Double get() = taxi.taxiWeeklyFaresEarned
    val taxiWeeklyFuelCost: Double get() = taxi.taxiWeeklyFuelCost
    val taxiWeeklyDriverSalary: Double get() = taxi.taxiWeeklyDriverSalary
    val taxiWeeklyMechanicSalary: Double get() = taxi.taxiWeeklyMechanicSalary
    val taxiWeeklyDispatcherSalary: Double get() = taxi.taxiWeeklyDispatcherSalary
    val taxiWeeklyMaintenanceCost: Double get() = taxi.taxiWeeklyMaintenanceCost
    val taxiWeeklyParkingSpaceTax: Double get() = taxi.taxiWeeklyParkingSpaceTax
    val taxiWeeklyParkingSpaceMaint: Double get() = taxi.taxiWeeklyParkingSpaceMaint
    val taxiDepotLevel: Int get() = taxi.taxiDepotLevel
    val taxiDepotCapacity: Int get() = taxi.taxiDepotCapacity

    // Restaurant Getters
    val waiterCount: Int get() = restaurant.waiterCount
    val chefCount: Int get() = restaurant.chefCount
    val seniorChefCount: Int get() = restaurant.seniorChefCount
    val cleanerCount: Int get() = restaurant.cleanerCount
    val managerCount: Int get() = restaurant.managerCount
    val weeklyCustomersServed: Int get() = restaurant.weeklyCustomersServed
    val weeklyFoodSales: Double get() = restaurant.weeklyFoodSales
    val weeklyIngredientCost: Double get() = restaurant.weeklyIngredientCost
    val weeklyBuildingMaintenanceCost: Double get() = restaurant.weeklyBuildingMaintenanceCost
    val weeklyStaffPayroll: Double get() = restaurant.weeklyStaffPayroll
    val weeklyEquipmentMaintenanceCost: Double get() = restaurant.weeklyEquipmentMaintenanceCost
    val equipmentList: List<RestaurantEquipment> get() = restaurant.equipmentList
    val serviceEfficiency: Float get() = restaurant.serviceEfficiency
    val restaurantBonusCapacity: Int get() = restaurant.restaurantBonusCapacity

    // Farm Getters
    val farmLandTier: String get() = farm.farmLandTier
    val farmLandIsOwned: Boolean get() = farm.farmLandIsOwned
    val farmLandAcres: Double get() = farm.farmLandAcres
    val farmLandValue: Double get() = farm.farmLandValue
    val farmLandRentWeekly: Double get() = farm.farmLandRentWeekly
    val farmCropBatches: List<FarmCropBatch> get() = farm.farmCropBatches
    val farmLivestock: List<FarmLivestockGroup> get() = farm.farmLivestock
    val farmMachinery: List<FarmMachine> get() = farm.farmMachinery
    val farmHandCount: Int get() = farm.farmHandCount
    val farmTractorOperatorCount: Int get() = farm.farmTractorOperatorCount
    val farmLivestockWorkerCount: Int get() = farm.farmLivestockWorkerCount
    val farmDriverCount: Int get() = farm.farmDriverCount
    val farmVeterinarianCount: Int get() = farm.farmVeterinarianCount
    val farmManagerCount: Int get() = farm.farmManagerCount
    val farmCleanliness: Float get() = farm.farmCleanliness
    val farmInventory: List<FarmInventoryItem> get() = farm.farmInventory
    val farmWeeklyRawCropsRevenue: Double get() = farm.farmWeeklyRawCropsRevenue
    val farmWeeklyFruitsRevenue: Double get() = farm.farmWeeklyFruitsRevenue
    val farmWeeklyLivestockRevenue: Double get() = farm.farmWeeklyLivestockRevenue
    val farmWeeklyProcessedRevenue: Double get() = farm.farmWeeklyProcessedRevenue
    val farmWeeklyFeedExpense: Double get() = farm.farmWeeklyFeedExpense
    val farmWeeklyLandRentExpense: Double get() = farm.farmWeeklyLandRentExpense
    val farmWeeklyMachineUpkeepExpense: Double get() = farm.farmWeeklyMachineUpkeepExpense
    val farmWeeklyPayrollExpense: Double get() = farm.farmWeeklyPayrollExpense
    val farmWeeklySeedExpense: Double get() = farm.farmWeeklySeedExpense
    val farmLastWeatherEvent: String get() = farm.farmLastWeatherEvent

    // Hotel Getters
    val hotelRoomCount: Int get() = hotel.hotelRoomCount
    val hotelMaxRoomCapacity: Int get() = hotel.hotelMaxRoomCapacity
    val hotelNightlyRate: Double get() = hotel.hotelNightlyRate
    val hotelOccupancyRate: Float get() = hotel.hotelOccupancyRate
    val hotelCondition: Float get() = hotel.hotelCondition
    val hotelCleanliness: Float get() = hotel.hotelCleanliness
    val hotelReputation: Float get() = hotel.hotelReputation
    val hotelHousekeeperCount: Int get() = hotel.hotelHousekeeperCount
    val hotelFrontDeskCount: Int get() = hotel.hotelFrontDeskCount
    val hotelChefCount: Int get() = hotel.hotelChefCount
    val hotelWaiterCount: Int get() = hotel.hotelWaiterCount
    val hotelSecurityCount: Int get() = hotel.hotelSecurityCount
    val hotelMaintenanceTechCount: Int get() = hotel.hotelMaintenanceTechCount
    val hotelConciergeCount: Int get() = hotel.hotelConciergeCount
    val hotelGeneralManagerCount: Int get() = hotel.hotelGeneralManagerCount
    val hotelAmenities: List<HotelAmenity> get() = hotel.hotelAmenities
    val hotelRooms: List<HotelRoom> get() = hotel.hotelRooms
    val hotelTotalLodgesThisWeek: Int get() = hotel.hotelTotalLodgesThisWeek
    val hotelWeeklyRoomRevenue: Double get() = hotel.hotelWeeklyRoomRevenue
    val hotelWeeklyAmenityRevenue: Double get() = hotel.hotelWeeklyAmenityRevenue
    val hotelWeeklyFoodRevenue: Double get() = hotel.hotelWeeklyFoodRevenue
    val hotelWeeklyPayrollCost: Double get() = hotel.hotelWeeklyPayrollCost
    val hotelWeeklySuppliesCost: Double get() = hotel.hotelWeeklySuppliesCost
    val hotelWeeklyUtilityCost: Double get() = hotel.hotelWeeklyUtilityCost
    val hotelWeeklyMaintenanceCost: Double get() = hotel.hotelWeeklyMaintenanceCost
    val hotelSeasonText: String get() = hotel.hotelSeasonText
    val hotelLastIncident: String get() = hotel.hotelLastIncident

    // Crypto Getters
    val cryptoTotalSupply: Double get() = crypto.cryptoTotalSupply
    val cryptoTokenPrice: Double get() = crypto.cryptoTokenPrice
    val cryptoLiquidityPoolUsd: Double get() = crypto.cryptoLiquidityPoolUsd
    val cryptoWeeklyTradingVolume: Double get() = crypto.cryptoWeeklyTradingVolume
    val cryptoStakedTokens: Double get() = crypto.cryptoStakedTokens
    val cryptoCommunitySentiment: Float get() = crypto.cryptoCommunitySentiment
    val cryptoMarketCycle: String get() = crypto.cryptoMarketCycle
    val cryptoModCount: Int get() = crypto.cryptoModCount
    val cryptoSolidityDevCount: Int get() = crypto.cryptoSolidityDevCount
    val cryptoSecurityAuditorCount: Int get() = crypto.cryptoSecurityAuditorCount
    val cryptoMarketerCount: Int get() = crypto.cryptoMarketerCount
    val cryptoWeeklySwapFeesRevenue: Double get() = crypto.cryptoWeeklySwapFeesRevenue
    val cryptoWeeklyStakingFeesRevenue: Double get() = crypto.cryptoWeeklyStakingFeesRevenue
    val cryptoWeeklyDeFiYieldRevenue: Double get() = crypto.cryptoWeeklyDeFiYieldRevenue
    val cryptoWeeklyServerInfraCost: Double get() = crypto.cryptoWeeklyServerInfraCost
    val cryptoWeeklyPayrollCost: Double get() = crypto.cryptoWeeklyPayrollCost
    val cryptoWeeklySecurityCost: Double get() = crypto.cryptoWeeklySecurityCost
    val cryptoTokenTicker: String get() = crypto.cryptoTokenTicker
    val cryptoTokenName: String get() = crypto.cryptoTokenName
    val cryptoCirculatingSupply: Double get() = crypto.cryptoCirculatingSupply
    val cryptoActiveUsers: Int get() = crypto.cryptoActiveUsers
    val cryptoDevelopersCount: Int get() = crypto.cryptoDevelopersCount
    val cryptoSecurityTier: Int get() = crypto.cryptoSecurityTier
    val cryptoWebsiteAppTier: Int get() = crypto.cryptoWebsiteAppTier
    val cryptoMarketingTier: Int get() = crypto.cryptoMarketingTier
    val cryptoIsMarketListed: Boolean get() = crypto.cryptoIsMarketListed
    val cryptoMarketListingNotice: String get() = crypto.cryptoMarketListingNotice
    val cryptoLastHackLoss: Double get() = crypto.cryptoLastHackLoss
    val cryptoNewServiceLaunchBoostWeeks: Int get() = crypto.cryptoNewServiceLaunchBoostWeeks
    val cryptoLastServiceLaunched: String get() = crypto.cryptoLastServiceLaunched
    val cryptoFounderTokensOwned: Double get() = crypto.cryptoFounderTokensOwned
    val cryptoWeeklyProtocolFeeRevenue: Double get() = crypto.cryptoWeeklyProtocolFeeRevenue
    val cryptoEducationalLesson: String get() = crypto.cryptoEducationalLesson

    // Listing & IPO Getters
    val isListedOnWeShare: Boolean get() = listing.isListedOnWeShare
    val weShareListedEquityPercent: Double get() = listing.weShareListedEquityPercent
    val weShareTotalSharesIssued: Long get() = listing.weShareTotalSharesIssued
    val weSharePublicSharesSold: Long get() = listing.weSharePublicSharesSold
    val weShareSharePrice: Double get() = listing.weShareSharePrice
    val weShareStockId: String? get() = listing.weShareStockId
    val screeningStatus: String get() = listing.screeningStatus
    val screeningWeeksRemaining: Int get() = listing.screeningWeeksRemaining
    val screeningRequestedEquityPercent: Double get() = listing.screeningRequestedEquityPercent
    val screeningType: String get() = listing.screeningType
    val approvedSharePrice: Double get() = listing.approvedSharePrice
    val approvedTotalShares: Long get() = listing.approvedTotalShares
    val approvedListedSharesUnits: Long get() = listing.approvedListedSharesUnits
    val screeningFeedback: String get() = listing.screeningFeedback

    // Governance & Compliance Getters
    val recurringCustomers: Int get() = governance.recurringCustomers
    val marketingCampaign: String get() = governance.marketingCampaign
    val marketingWeeksActive: Int get() = governance.marketingWeeksActive
    val marketingWeeklyCost: Double get() = governance.marketingWeeklyCost
    val hasLegalLicense: Boolean get() = governance.hasLegalLicense
    val legalLicenseCost: Double get() = governance.legalLicenseCost
    val sccWarningReceived: Boolean get() = governance.sccWarningReceived
    val sccDeadlineWeeks: Int get() = governance.sccDeadlineWeeks
    val isShutDownByRegulators: Boolean get() = governance.isShutDownByRegulators
    val pocketedInvestorCapital: Double get() = governance.pocketedInvestorCapital
    val investorLawsuitActive: Boolean get() = governance.investorLawsuitActive
    val investorLawsuitDamages: Double get() = governance.investorLawsuitDamages
    val grossEnterpriseValuation: Double get() = governance.grossEnterpriseValuation
    val lastOwnerAction: String get() = governance.lastOwnerAction
    val expansionCount: Int get() = governance.expansionCount

    // Subsystem Domain Copy Helpers
    fun copyTaxi(
        taxiCarCount: Int = taxi.taxiCarCount,
        taxiDriverCount: Int = taxi.taxiDriverCount,
        taxiMechanicCount: Int = taxi.taxiMechanicCount,
        taxiDispatcherCount: Int = taxi.taxiDispatcherCount,
        taxiCondition: Float = taxi.taxiCondition,
        taxiOdometerMiles: Double = taxi.taxiOdometerMiles,
        taxiWeeklyTripsCompleted: Int = taxi.taxiWeeklyTripsCompleted,
        taxiWeeklyFaresEarned: Double = taxi.taxiWeeklyFaresEarned,
        taxiWeeklyFuelCost: Double = taxi.taxiWeeklyFuelCost,
        taxiWeeklyDriverSalary: Double = taxi.taxiWeeklyDriverSalary,
        taxiWeeklyMechanicSalary: Double = taxi.taxiWeeklyMechanicSalary,
        taxiWeeklyDispatcherSalary: Double = taxi.taxiWeeklyDispatcherSalary,
        taxiWeeklyMaintenanceCost: Double = taxi.taxiWeeklyMaintenanceCost,
        taxiWeeklyParkingSpaceTax: Double = taxi.taxiWeeklyParkingSpaceTax,
        taxiWeeklyParkingSpaceMaint: Double = taxi.taxiWeeklyParkingSpaceMaint,
        taxiDepotLevel: Int = taxi.taxiDepotLevel,
        taxiDepotCapacity: Int = taxi.taxiDepotCapacity
    ): BusinessVenture = copy(taxi = taxi.copy(
        taxiCarCount = taxiCarCount,
        taxiDriverCount = taxiDriverCount,
        taxiMechanicCount = taxiMechanicCount,
        taxiDispatcherCount = taxiDispatcherCount,
        taxiCondition = taxiCondition,
        taxiOdometerMiles = taxiOdometerMiles,
        taxiWeeklyTripsCompleted = taxiWeeklyTripsCompleted,
        taxiWeeklyFaresEarned = taxiWeeklyFaresEarned,
        taxiWeeklyFuelCost = taxiWeeklyFuelCost,
        taxiWeeklyDriverSalary = taxiWeeklyDriverSalary,
        taxiWeeklyMechanicSalary = taxiWeeklyMechanicSalary,
        taxiWeeklyDispatcherSalary = taxiWeeklyDispatcherSalary,
        taxiWeeklyMaintenanceCost = taxiWeeklyMaintenanceCost,
        taxiWeeklyParkingSpaceTax = taxiWeeklyParkingSpaceTax,
        taxiWeeklyParkingSpaceMaint = taxiWeeklyParkingSpaceMaint,
        taxiDepotLevel = taxiDepotLevel,
        taxiDepotCapacity = taxiDepotCapacity
    ))

    fun copyRestaurant(
        waiterCount: Int = restaurant.waiterCount,
        chefCount: Int = restaurant.chefCount,
        seniorChefCount: Int = restaurant.seniorChefCount,
        cleanerCount: Int = restaurant.cleanerCount,
        managerCount: Int = restaurant.managerCount,
        weeklyCustomersServed: Int = restaurant.weeklyCustomersServed,
        weeklyFoodSales: Double = restaurant.weeklyFoodSales,
        weeklyIngredientCost: Double = restaurant.weeklyIngredientCost,
        weeklyBuildingMaintenanceCost: Double = restaurant.weeklyBuildingMaintenanceCost,
        weeklyStaffPayroll: Double = restaurant.weeklyStaffPayroll,
        weeklyEquipmentMaintenanceCost: Double = restaurant.weeklyEquipmentMaintenanceCost,
        equipmentList: List<RestaurantEquipment> = restaurant.equipmentList,
        serviceEfficiency: Float = restaurant.serviceEfficiency,
        restaurantBonusCapacity: Int = restaurant.restaurantBonusCapacity
    ): BusinessVenture = copy(restaurant = restaurant.copy(
        waiterCount = waiterCount,
        chefCount = chefCount,
        seniorChefCount = seniorChefCount,
        cleanerCount = cleanerCount,
        managerCount = managerCount,
        weeklyCustomersServed = weeklyCustomersServed,
        weeklyFoodSales = weeklyFoodSales,
        weeklyIngredientCost = weeklyIngredientCost,
        weeklyBuildingMaintenanceCost = weeklyBuildingMaintenanceCost,
        weeklyStaffPayroll = weeklyStaffPayroll,
        weeklyEquipmentMaintenanceCost = weeklyEquipmentMaintenanceCost,
        equipmentList = equipmentList,
        serviceEfficiency = serviceEfficiency,
        restaurantBonusCapacity = restaurantBonusCapacity
    ))

    fun copyFarm(
        farmLandTier: String = farm.farmLandTier,
        farmLandIsOwned: Boolean = farm.farmLandIsOwned,
        farmLandAcres: Double = farm.farmLandAcres,
        farmLandValue: Double = farm.farmLandValue,
        farmLandRentWeekly: Double = farm.farmLandRentWeekly,
        farmCropBatches: List<FarmCropBatch> = farm.farmCropBatches,
        farmLivestock: List<FarmLivestockGroup> = farm.farmLivestock,
        farmMachinery: List<FarmMachine> = farm.farmMachinery,
        farmHandCount: Int = farm.farmHandCount,
        farmTractorOperatorCount: Int = farm.farmTractorOperatorCount,
        farmLivestockWorkerCount: Int = farm.farmLivestockWorkerCount,
        farmDriverCount: Int = farm.farmDriverCount,
        farmVeterinarianCount: Int = farm.farmVeterinarianCount,
        farmManagerCount: Int = farm.farmManagerCount,
        farmCleanliness: Float = farm.farmCleanliness,
        farmInventory: List<FarmInventoryItem> = farm.farmInventory,
        farmWeeklyRawCropsRevenue: Double = farm.farmWeeklyRawCropsRevenue,
        farmWeeklyFruitsRevenue: Double = farm.farmWeeklyFruitsRevenue,
        farmWeeklyLivestockRevenue: Double = farm.farmWeeklyLivestockRevenue,
        farmWeeklyProcessedRevenue: Double = farm.farmWeeklyProcessedRevenue,
        farmWeeklyFeedExpense: Double = farm.farmWeeklyFeedExpense,
        farmWeeklyLandRentExpense: Double = farm.farmWeeklyLandRentExpense,
        farmWeeklyMachineUpkeepExpense: Double = farm.farmWeeklyMachineUpkeepExpense,
        farmWeeklyPayrollExpense: Double = farm.farmWeeklyPayrollExpense,
        farmWeeklySeedExpense: Double = farm.farmWeeklySeedExpense,
        farmLastWeatherEvent: String = farm.farmLastWeatherEvent
    ): BusinessVenture = copy(farm = farm.copy(
        farmLandTier = farmLandTier,
        farmLandIsOwned = farmLandIsOwned,
        farmLandAcres = farmLandAcres,
        farmLandValue = farmLandValue,
        farmLandRentWeekly = farmLandRentWeekly,
        farmCropBatches = farmCropBatches,
        farmLivestock = farmLivestock,
        farmMachinery = farmMachinery,
        farmHandCount = farmHandCount,
        farmTractorOperatorCount = farmTractorOperatorCount,
        farmLivestockWorkerCount = farmLivestockWorkerCount,
        farmDriverCount = farmDriverCount,
        farmVeterinarianCount = farmVeterinarianCount,
        farmManagerCount = farmManagerCount,
        farmCleanliness = farmCleanliness,
        farmInventory = farmInventory,
        farmWeeklyRawCropsRevenue = farmWeeklyRawCropsRevenue,
        farmWeeklyFruitsRevenue = farmWeeklyFruitsRevenue,
        farmWeeklyLivestockRevenue = farmWeeklyLivestockRevenue,
        farmWeeklyProcessedRevenue = farmWeeklyProcessedRevenue,
        farmWeeklyFeedExpense = farmWeeklyFeedExpense,
        farmWeeklyLandRentExpense = farmWeeklyLandRentExpense,
        farmWeeklyMachineUpkeepExpense = farmWeeklyMachineUpkeepExpense,
        farmWeeklyPayrollExpense = farmWeeklyPayrollExpense,
        farmWeeklySeedExpense = farmWeeklySeedExpense,
        farmLastWeatherEvent = farmLastWeatherEvent
    ))

    fun copyHotel(
        hotelRoomCount: Int = hotel.hotelRoomCount,
        hotelMaxRoomCapacity: Int = hotel.hotelMaxRoomCapacity,
        hotelNightlyRate: Double = hotel.hotelNightlyRate,
        hotelOccupancyRate: Float = hotel.hotelOccupancyRate,
        hotelCondition: Float = hotel.hotelCondition,
        hotelCleanliness: Float = hotel.hotelCleanliness,
        hotelReputation: Float = hotel.hotelReputation,
        hotelHousekeeperCount: Int = hotel.hotelHousekeeperCount,
        hotelFrontDeskCount: Int = hotel.hotelFrontDeskCount,
        hotelChefCount: Int = hotel.hotelChefCount,
        hotelWaiterCount: Int = hotel.hotelWaiterCount,
        hotelSecurityCount: Int = hotel.hotelSecurityCount,
        hotelMaintenanceTechCount: Int = hotel.hotelMaintenanceTechCount,
        hotelConciergeCount: Int = hotel.hotelConciergeCount,
        hotelGeneralManagerCount: Int = hotel.hotelGeneralManagerCount,
        hotelAmenities: List<HotelAmenity> = hotel.hotelAmenities,
        hotelRooms: List<HotelRoom> = hotel.hotelRooms,
        hotelTotalLodgesThisWeek: Int = hotel.hotelTotalLodgesThisWeek,
        hotelWeeklyRoomRevenue: Double = hotel.hotelWeeklyRoomRevenue,
        hotelWeeklyAmenityRevenue: Double = hotel.hotelWeeklyAmenityRevenue,
        hotelWeeklyFoodRevenue: Double = hotel.hotelWeeklyFoodRevenue,
        hotelWeeklyPayrollCost: Double = hotel.hotelWeeklyPayrollCost,
        hotelWeeklySuppliesCost: Double = hotel.hotelWeeklySuppliesCost,
        hotelWeeklyUtilityCost: Double = hotel.hotelWeeklyUtilityCost,
        hotelWeeklyMaintenanceCost: Double = hotel.hotelWeeklyMaintenanceCost,
        hotelSeasonText: String = hotel.hotelSeasonText,
        hotelLastIncident: String = hotel.hotelLastIncident
    ): BusinessVenture = copy(hotel = hotel.copy(
        hotelRoomCount = hotelRoomCount,
        hotelMaxRoomCapacity = hotelMaxRoomCapacity,
        hotelNightlyRate = hotelNightlyRate,
        hotelOccupancyRate = hotelOccupancyRate,
        hotelCondition = hotelCondition,
        hotelCleanliness = hotelCleanliness,
        hotelReputation = hotelReputation,
        hotelHousekeeperCount = hotelHousekeeperCount,
        hotelFrontDeskCount = hotelFrontDeskCount,
        hotelChefCount = hotelChefCount,
        hotelWaiterCount = hotelWaiterCount,
        hotelSecurityCount = hotelSecurityCount,
        hotelMaintenanceTechCount = hotelMaintenanceTechCount,
        hotelConciergeCount = hotelConciergeCount,
        hotelGeneralManagerCount = hotelGeneralManagerCount,
        hotelAmenities = hotelAmenities,
        hotelRooms = hotelRooms,
        hotelTotalLodgesThisWeek = hotelTotalLodgesThisWeek,
        hotelWeeklyRoomRevenue = hotelWeeklyRoomRevenue,
        hotelWeeklyAmenityRevenue = hotelWeeklyAmenityRevenue,
        hotelWeeklyFoodRevenue = hotelWeeklyFoodRevenue,
        hotelWeeklyPayrollCost = hotelWeeklyPayrollCost,
        hotelWeeklySuppliesCost = hotelWeeklySuppliesCost,
        hotelWeeklyUtilityCost = hotelWeeklyUtilityCost,
        hotelWeeklyMaintenanceCost = hotelWeeklyMaintenanceCost,
        hotelSeasonText = hotelSeasonText,
        hotelLastIncident = hotelLastIncident
    ))

    fun copyCrypto(
        cryptoTotalSupply: Double = crypto.cryptoTotalSupply,
        cryptoTokenPrice: Double = crypto.cryptoTokenPrice,
        cryptoLiquidityPoolUsd: Double = crypto.cryptoLiquidityPoolUsd,
        cryptoWeeklyTradingVolume: Double = crypto.cryptoWeeklyTradingVolume,
        cryptoStakedTokens: Double = crypto.cryptoStakedTokens,
        cryptoCommunitySentiment: Float = crypto.cryptoCommunitySentiment,
        cryptoMarketCycle: String = crypto.cryptoMarketCycle,
        cryptoModCount: Int = crypto.cryptoModCount,
        cryptoSolidityDevCount: Int = crypto.cryptoSolidityDevCount,
        cryptoSecurityAuditorCount: Int = crypto.cryptoSecurityAuditorCount,
        cryptoMarketerCount: Int = crypto.cryptoMarketerCount,
        cryptoWeeklySwapFeesRevenue: Double = crypto.cryptoWeeklySwapFeesRevenue,
        cryptoWeeklyStakingFeesRevenue: Double = crypto.cryptoWeeklyStakingFeesRevenue,
        cryptoWeeklyDeFiYieldRevenue: Double = crypto.cryptoWeeklyDeFiYieldRevenue,
        cryptoWeeklyServerInfraCost: Double = crypto.cryptoWeeklyServerInfraCost,
        cryptoWeeklyPayrollCost: Double = crypto.cryptoWeeklyPayrollCost,
        cryptoWeeklySecurityCost: Double = crypto.cryptoWeeklySecurityCost,
        cryptoTokenTicker: String = crypto.cryptoTokenTicker,
        cryptoTokenName: String = crypto.cryptoTokenName,
        cryptoCirculatingSupply: Double = crypto.cryptoCirculatingSupply,
        cryptoActiveUsers: Int = crypto.cryptoActiveUsers,
        cryptoDevelopersCount: Int = crypto.cryptoDevelopersCount,
        cryptoSecurityTier: Int = crypto.cryptoSecurityTier,
        cryptoWebsiteAppTier: Int = crypto.cryptoWebsiteAppTier,
        cryptoMarketingTier: Int = crypto.cryptoMarketingTier,
        cryptoIsMarketListed: Boolean = crypto.cryptoIsMarketListed,
        cryptoMarketListingNotice: String = crypto.cryptoMarketListingNotice,
        cryptoLastHackLoss: Double = crypto.cryptoLastHackLoss,
        cryptoNewServiceLaunchBoostWeeks: Int = crypto.cryptoNewServiceLaunchBoostWeeks,
        cryptoLastServiceLaunched: String = crypto.cryptoLastServiceLaunched,
        cryptoFounderTokensOwned: Double = crypto.cryptoFounderTokensOwned,
        cryptoWeeklyProtocolFeeRevenue: Double = crypto.cryptoWeeklyProtocolFeeRevenue,
        cryptoEducationalLesson: String = crypto.cryptoEducationalLesson
    ): BusinessVenture = copy(crypto = crypto.copy(
        cryptoTotalSupply = cryptoTotalSupply,
        cryptoTokenPrice = cryptoTokenPrice,
        cryptoLiquidityPoolUsd = cryptoLiquidityPoolUsd,
        cryptoWeeklyTradingVolume = cryptoWeeklyTradingVolume,
        cryptoStakedTokens = cryptoStakedTokens,
        cryptoCommunitySentiment = cryptoCommunitySentiment,
        cryptoMarketCycle = cryptoMarketCycle,
        cryptoModCount = cryptoModCount,
        cryptoSolidityDevCount = cryptoSolidityDevCount,
        cryptoSecurityAuditorCount = cryptoSecurityAuditorCount,
        cryptoMarketerCount = cryptoMarketerCount,
        cryptoWeeklySwapFeesRevenue = cryptoWeeklySwapFeesRevenue,
        cryptoWeeklyStakingFeesRevenue = cryptoWeeklyStakingFeesRevenue,
        cryptoWeeklyDeFiYieldRevenue = cryptoWeeklyDeFiYieldRevenue,
        cryptoWeeklyServerInfraCost = cryptoWeeklyServerInfraCost,
        cryptoWeeklyPayrollCost = cryptoWeeklyPayrollCost,
        cryptoWeeklySecurityCost = cryptoWeeklySecurityCost,
        cryptoTokenTicker = cryptoTokenTicker,
        cryptoTokenName = cryptoTokenName,
        cryptoCirculatingSupply = cryptoCirculatingSupply,
        cryptoActiveUsers = cryptoActiveUsers,
        cryptoDevelopersCount = cryptoDevelopersCount,
        cryptoSecurityTier = cryptoSecurityTier,
        cryptoWebsiteAppTier = cryptoWebsiteAppTier,
        cryptoMarketingTier = cryptoMarketingTier,
        cryptoIsMarketListed = cryptoIsMarketListed,
        cryptoMarketListingNotice = cryptoMarketListingNotice,
        cryptoLastHackLoss = cryptoLastHackLoss,
        cryptoNewServiceLaunchBoostWeeks = cryptoNewServiceLaunchBoostWeeks,
        cryptoLastServiceLaunched = cryptoLastServiceLaunched,
        cryptoFounderTokensOwned = cryptoFounderTokensOwned,
        cryptoWeeklyProtocolFeeRevenue = cryptoWeeklyProtocolFeeRevenue,
        cryptoEducationalLesson = cryptoEducationalLesson
    ))

    fun copyListing(
        isListedOnWeShare: Boolean = listing.isListedOnWeShare,
        weShareListedEquityPercent: Double = listing.weShareListedEquityPercent,
        weShareTotalSharesIssued: Long = listing.weShareTotalSharesIssued,
        weSharePublicSharesSold: Long = listing.weSharePublicSharesSold,
        weShareSharePrice: Double = listing.weShareSharePrice,
        weShareStockId: String? = listing.weShareStockId,
        screeningStatus: String = listing.screeningStatus,
        screeningWeeksRemaining: Int = listing.screeningWeeksRemaining,
        screeningRequestedEquityPercent: Double = listing.screeningRequestedEquityPercent,
        screeningType: String = listing.screeningType,
        approvedSharePrice: Double = listing.approvedSharePrice,
        approvedTotalShares: Long = listing.approvedTotalShares,
        approvedListedSharesUnits: Long = listing.approvedListedSharesUnits,
        screeningFeedback: String = listing.screeningFeedback
    ): BusinessVenture = copy(listing = listing.copy(
        isListedOnWeShare = isListedOnWeShare,
        weShareListedEquityPercent = weShareListedEquityPercent,
        weShareTotalSharesIssued = weShareTotalSharesIssued,
        weSharePublicSharesSold = weSharePublicSharesSold,
        weShareSharePrice = weShareSharePrice,
        weShareStockId = weShareStockId,
        screeningStatus = screeningStatus,
        screeningWeeksRemaining = screeningWeeksRemaining,
        screeningRequestedEquityPercent = screeningRequestedEquityPercent,
        screeningType = screeningType,
        approvedSharePrice = approvedSharePrice,
        approvedTotalShares = approvedTotalShares,
        approvedListedSharesUnits = approvedListedSharesUnits,
        screeningFeedback = screeningFeedback
    ))

    fun copyGovernance(
        recurringCustomers: Int = governance.recurringCustomers,
        marketingCampaign: String = governance.marketingCampaign,
        marketingWeeksActive: Int = governance.marketingWeeksActive,
        marketingWeeklyCost: Double = governance.marketingWeeklyCost,
        hasLegalLicense: Boolean = governance.hasLegalLicense,
        legalLicenseCost: Double = governance.legalLicenseCost,
        sccWarningReceived: Boolean = governance.sccWarningReceived,
        sccDeadlineWeeks: Int = governance.sccDeadlineWeeks,
        isShutDownByRegulators: Boolean = governance.isShutDownByRegulators,
        pocketedInvestorCapital: Double = governance.pocketedInvestorCapital,
        investorLawsuitActive: Boolean = governance.investorLawsuitActive,
        investorLawsuitDamages: Double = governance.investorLawsuitDamages,
        grossEnterpriseValuation: Double = governance.grossEnterpriseValuation,
        lastOwnerAction: String = governance.lastOwnerAction,
        expansionCount: Int = governance.expansionCount
    ): BusinessVenture = copy(governance = governance.copy(
        recurringCustomers = recurringCustomers,
        marketingCampaign = marketingCampaign,
        marketingWeeksActive = marketingWeeksActive,
        marketingWeeklyCost = marketingWeeklyCost,
        hasLegalLicense = hasLegalLicense,
        legalLicenseCost = legalLicenseCost,
        sccWarningReceived = sccWarningReceived,
        sccDeadlineWeeks = sccDeadlineWeeks,
        isShutDownByRegulators = isShutDownByRegulators,
        pocketedInvestorCapital = pocketedInvestorCapital,
        investorLawsuitActive = investorLawsuitActive,
        investorLawsuitDamages = investorLawsuitDamages,
        grossEnterpriseValuation = grossEnterpriseValuation,
        lastOwnerAction = lastOwnerAction,
        expansionCount = expansionCount
    ))

    val operatingDebt: Double
        get() = (-accumulatedProfit).coerceAtLeast(0.0)

    val isInDebt: Boolean
        get() = accumulatedProfit < 0.0 || outstandingLoan > 0.0
    val totalInvestorEquityPercent: Double
        get() = (if (investors.isNotEmpty()) investors.sumOf { it.equityPercent } else investorEquityPercent).coerceIn(0.0, 1.0)

    val founderEquityPercent: Double
        get() = (1.0 - totalInvestorEquityPercent - (if (isListedOnWeShare) weShareListedEquityPercent else 0.0)).coerceIn(0.0, 1.0)

    val isPlayerInControl: Boolean
        get() = isOwned && founderEquityPercent >= 0.50 && (investors.isEmpty() || investors.none { it.equityPercent > founderEquityPercent })

    val valuation: Double
        get() {
            val allTimeProfits = (totalWithdrawn + accumulatedProfit).coerceAtLeast(0.0)
            val allExpenses = totalExpensesPaid.coerceAtLeast(0.0)
            val allPenniesSpent = (setupCost + totalCapitalInvested).coerceAtLeast(0.0)
            val computed = allTimeProfits + allExpenses + allPenniesSpent
            return if (computed > 0.0) computed else setupCost
        }
}

fun formatCompactMoney(amount: Double): String {
    val sign = if (amount < 0) "-" else ""
    val absVal = kotlin.math.abs(amount)
    return when {
        absVal >= 1_000_000_000.0 -> {
            val v = absVal / 1_000_000_000.0
            if (v >= 100) "$sign$${String.format("%.0f", v)}B" else "$sign$${String.format("%.1f", v)}B"
        }
        absVal >= 1_000_000.0 -> {
            val v = absVal / 1_000_000.0
            if (v >= 100) "$sign$${String.format("%.0f", v)}M" else "$sign$${String.format("%.1f", v)}M"
        }
        absVal >= 10_000.0 -> {
            val v = absVal / 1_000.0
            if (v >= 100) "$sign$${String.format("%.0f", v)}K" else "$sign$${String.format("%.1f", v)}K"
        }
        absVal >= 1_000.0 -> {
            val v = absVal / 1_000.0
            "$sign$${String.format("%.1f", v)}K"
        }
        else -> "$sign$${String.format("%,.0f", absVal)}"
    }
}

val GameStateEntity.playerLevel: Int
    get() {
        val totalSkills = (voiceSkill + writingSkill + businessSkill).coerceIn(0f, 300f)
        val skillRatio = totalSkills / 300f
        val reachRatio = ((fans + followers).toFloat() / 1000000f).coerceIn(0f, 1.0f)
        val catalogRatio = (songs.count { it.isReleased }.toFloat() / 15f).coerceIn(0f, 1.0f)
        val avgNpcRel = if (npcs.isNotEmpty()) npcs.map { it.relationshipWithPlayer }.average().toFloat() / 100f else 0.5f
        val industryRatio = ((industryFollowers.toFloat() / 50000f).coerceIn(0f, 1.0f) * 0.5f + avgNpcRel * 0.5f).coerceIn(0f, 1.0f)
        val overallMastery = (skillRatio * 0.35f + reachRatio * 0.30f + catalogRatio * 0.15f + industryRatio * 0.20f).coerceIn(0.0f, 1.0f)
        return ((overallMastery * 9f).toInt() + 1).coerceIn(1, 10)
    }
