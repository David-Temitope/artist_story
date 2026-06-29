package com.example.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Car(
    val id: String,
    val name: String,
    val cost: Double,
    val costReductionPercent: Float // e.g. 0.2f for 20%
)

@JsonClass(generateAdapter = true)
data class House(
    val id: String,
    val name: String,
    val cost: Double,
    val energyBonus: Int
)

@JsonClass(generateAdapter = true)
data class Instrument(
    val id: String,
    val name: String,
    val cost: Double,
    val ratingBonus: Float
)

@JsonClass(generateAdapter = true)
data class Genre(
    val id: String,
    val name: String,
    val productionCostMultiplier: Float,
    val listenerAppeal: Float
)

@JsonClass(generateAdapter = true)
data class Producer(
    val id: String,
    val name: String,
    val level: String, // Beginner, Experienced, Legendary
    val cost: Double,
    val ratingBonus: Float,
    val specialtyGenre: String? = null
)

@JsonClass(generateAdapter = true)
data class Song(
    val id: String,
    val title: String,
    val genreId: String,
    val budget: Double,
    val producerId: String?,
    val rating: Float, // 1 to 5
    val isUploadedMap: Map<String, Long> = emptyMap(), // platformId to streamCount
    val totalStreams: Long = 0,
    val videoTier: String? = null, // Low, Standard, High, Premium, Cinematic
    val weekCreated: Int,
    val yearCreated: Int
)

@JsonClass(generateAdapter = true)
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
    val status: String // "BOOKED", "ACTIVE", "COMPLETED"
)

@JsonClass(generateAdapter = true)
data class SideJob(
    val id: String,
    val name: String,
    val weeklyPay: Double,
    val energyCost: Int,
    val timeCost: Int,
    val isActive: Boolean = false
)

@JsonClass(generateAdapter = true)
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
    val financialHealth: String = "Stable" // "Strong", "Stable", "Risky"
)

@JsonClass(generateAdapter = true)
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
    val developerActivity: String = "High" // "High", "Medium", "Low"
)

@JsonClass(generateAdapter = true)
data class LabelPromotionTeam(
    val id: String,
    val name: String,
    val weeklyCost: Double,
    val popularityBoost: Float
)

@JsonClass(generateAdapter = true)
data class LabelManager(
    val id: String,
    val name: String,
    val weeklyCost: Double,
    val efficiency: Float
)

@JsonClass(generateAdapter = true)
data class OwnRecordLabel(
    val name: String,
    val managers: List<LabelManager> = emptyList(),
    val promoTeams: List<LabelPromotionTeam> = emptyList(),
    val ownedPropertiesCount: Int = 0, // Reduces travel and studio costs for signed artists
    val signedNPCIds: List<String> = emptyList(),
    val accruedDebts: Double = 0.0
)

@JsonClass(generateAdapter = true)
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
    val labelId: String? = null
)

@JsonClass(generateAdapter = true)
data class NPCEntity(
    val id: String,
    val name: String,
    val popularity: Float,
    val influence: Float,
    val fans: Long,
    val isOnTour: Boolean = false,
    val weeklySales: Double = 0.0,
    val bestSongTitle: String = "Unknown",
    val bestSongRating: Float = 3.0f,
    val bestSongStreams: Long = 0,
    
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
    val careerGoal: String = "Superstardom"
)

@JsonClass(generateAdapter = true)
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
    val thumbnailId: String = "ic_video"
)

@JsonClass(generateAdapter = true)
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
    val isLikedByPlayer: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val id: String,
    val senderId: String, // NPC ID or "PLAYER"
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class ChatConversation(
    val npcId: String,
    val npcName: String,
    val messages: List<ChatMessage> = emptyList(),
    val unreadCount: Int = 0
)

@JsonClass(generateAdapter = true)
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
    val permissions: Map<String, Boolean> = emptyMap()
)

@JsonClass(generateAdapter = true)
data class StockOrder(
    val id: String,
    val stockId: String,
    val type: String, // "LIMIT_BUY", "LIMIT_SELL"
    val targetPrice: Double,
    val quantity: Int
)

@JsonClass(generateAdapter = true)
data class CryptoOrder(
    val id: String,
    val cryptoId: String,
    val type: String, // "LIMIT_BUY", "LIMIT_SELL", "STOP_LOSS", "TAKE_PROFIT"
    val targetPrice: Double,
    val quantity: Double
)
