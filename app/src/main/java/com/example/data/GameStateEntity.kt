package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "game_states")
data class GameStateEntity(
    @PrimaryKey val email: String,
    val artistName: String,
    val isAdmin: Boolean = false,
    val cash: Double = 10000.0,
    val fans: Long = 0,
    val popularity: Float = 5f, // percentage 0-100
    val influence: Float = 5f, // percentage 0-100
    val voiceSkill: Float = 10f, // percentage 10-100
    val writingSkill: Float = 10f, // percentage 10-100
    val businessSkill: Float = 10f, // percentage 10-100
    val energy: Int = 100,
    val maxEnergy: Int = 100,
    val timeSlots: Int = 10,
    val maxTimeSlots: Int = 10,
    val currentWeek: Int = 1,
    val currentYear: Int = 1,
    val practiceMissedWeeks: Int = 0,
    val activeToursCount: Int = 0,
    
    // Serialized collections
    val ownedCars: List<Car> = emptyList(),
    val ownedHouses: List<House> = emptyList(),
    val ownedInstruments: List<Instrument> = emptyList(),
    val songs: List<Song> = emptyList(),
    val tours: List<Tour> = emptyList(),
    val sideJobs: List<SideJob> = emptyList(),
    val stocks: List<Stock> = emptyList(),
    val cryptos: List<Crypto> = emptyList(),
    val ownLabel: OwnRecordLabel? = null,
    val signedLabelId: String? = null,
    val signedLabelCut: Float = 0.0f,
    val mailbox: List<MailMessage> = emptyList(),
    val npcs: List<NPCEntity> = emptyList(),
    val purchaseRequests: List<PurchaseRequest> = emptyList(),

    // Extra dynamic features columns
    val casualFans: Long = 0,
    val hardcoreFans: Long = 0,
    val collectors: Long = 0,
    val concertGoers: Long = 0,
    val internationalFans: Long = 0,
    val onlineCommunity: Long = 0,

    val industryRespect: Float = 10f,
    val publicImage: Float = 50f,
    val criticalAcclaim: Float = 10f,
    val trustworthiness: Float = 50f,
    val legacyPoints: Long = 0,

    val businessReputation: Float = 50f,
    val discipline: Float = 50f,
    val integrity: Float = 50f,
    val generosity: Float = 50f,
    val humility: Float = 50f,
    val reliability: Float = 50f,
    
    val followers: Long = 100,
    val industryFollowers: Long = 10,
    val mediaAttention: Float = 10f,
    val trendingScore: Float = 10f,
    val conversationVolume: Long = 50,
    val positiveSentiment: Float = 50f,
    val negativeSentiment: Float = 20f,
    val neutralSentiment: Float = 30f,

    val stress: Float = 20f,
    val burnout: Float = 0f,
    val creativeInspiration: Float = 100f,
    val motivation: Float = 80f,
    val happiness: Float = 75f,
    val workLifeBalance: Float = 50f,

    val genreTrends: Map<String, Float> = emptyMap(),
    val timelineEvents: List<String> = emptyList(),
    val ownedBusinesses: List<BusinessAsset> = emptyList(),
    val activeGlobalEvent: String? = null,
    val globalEventEffect: String? = null,

    // Digital Smartphone Ecosystem variables
    val meTubeVideos: List<MeTubeVideo> = emptyList(),
    val meTubeSubscribers: Long = 120,
    val meTubeWatchTime: Double = 45.0,
    val meTubeRevenue: Double = 0.0,
    val chatConversations: List<ChatConversation> = emptyList(),
    val castPosts: List<CastPost> = emptyList(),
    val castTrendingHashtags: List<String> = emptyList(),
    val hiredEmployees: List<Employee> = emptyList(),
    val pendingStockOrders: List<StockOrder> = emptyList(),
    val pendingCryptoOrders: List<CryptoOrder> = emptyList(),
    val wallpaperId: String = "sunset",
    val isPhoneLocked: Boolean = true,
    val spiritualPrayerSetting: Boolean = true,
    val spiritualTitheSetting: Boolean = true,
    val spiritualTotalTithesPaid: Double = 0.0,
    val spiritualTitheReminderActive: Boolean = false,
    val notificationLogs: List<String> = emptyList(),
    // Banking System variables
    val savingsBalance: Double = 0.0,
    val loanAmount: Double = 0.0,
    val taxDebt: Double = 0.0,
    val savingsInterestRate: Float = 0.012f, // 1.2% weekly interest
    val loanInterestRate: Float = 0.02f, // 2.0% weekly interest
    val mortgageAmount: Double = 0.0,
    val insuredCars: List<String> = emptyList(),
    val insuredHouses: List<String> = emptyList(),
    val weatherCondition: String = "Sunny",
    val activeNoteText: String = "",
    val savedPhotos: List<String> = emptyList(),
    val scheduledActivities: List<String> = emptyList(),
    val spiritualJournal: List<String> = emptyList(),
    val economyCycle: String = "Stable Growth", // "Boom", "Stable Growth", "Inflation Peak", "Recession", "Depression"
    val inflationRate: Float = 0.02f, // 2% basic weekly rate
    val fuelPrice: Float = 1.25f, // fuel price per liter
    val touristIndex: Int = 100, // percentage indicator of tourist influx
    val isElectionSeason: Boolean = false
)

class Converters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromCarList(value: List<Car>?): String = moshi.adapter<List<Car>>(Types.newParameterizedType(List::class.java, Car::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toCarList(value: String?): List<Car> = moshi.adapter<List<Car>>(Types.newParameterizedType(List::class.java, Car::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromHouseList(value: List<House>?): String = moshi.adapter<List<House>>(Types.newParameterizedType(List::class.java, House::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toHouseList(value: String?): List<House> = moshi.adapter<List<House>>(Types.newParameterizedType(List::class.java, House::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromInstrumentList(value: List<Instrument>?): String = moshi.adapter<List<Instrument>>(Types.newParameterizedType(List::class.java, Instrument::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toInstrumentList(value: String?): List<Instrument> = moshi.adapter<List<Instrument>>(Types.newParameterizedType(List::class.java, Instrument::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromSongList(value: List<Song>?): String = moshi.adapter<List<Song>>(Types.newParameterizedType(List::class.java, Song::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toSongList(value: String?): List<Song> = moshi.adapter<List<Song>>(Types.newParameterizedType(List::class.java, Song::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromTourList(value: List<Tour>?): String = moshi.adapter<List<Tour>>(Types.newParameterizedType(List::class.java, Tour::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toTourList(value: String?): List<Tour> = moshi.adapter<List<Tour>>(Types.newParameterizedType(List::class.java, Tour::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromSideJobList(value: List<SideJob>?): String = moshi.adapter<List<SideJob>>(Types.newParameterizedType(List::class.java, SideJob::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toSideJobList(value: String?): List<SideJob> = moshi.adapter<List<SideJob>>(Types.newParameterizedType(List::class.java, SideJob::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromStockList(value: List<Stock>?): String = moshi.adapter<List<Stock>>(Types.newParameterizedType(List::class.java, Stock::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toStockList(value: String?): List<Stock> = moshi.adapter<List<Stock>>(Types.newParameterizedType(List::class.java, Stock::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromCryptoList(value: List<Crypto>?): String = moshi.adapter<List<Crypto>>(Types.newParameterizedType(List::class.java, Crypto::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toCryptoList(value: String?): List<Crypto> = moshi.adapter<List<Crypto>>(Types.newParameterizedType(List::class.java, Crypto::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromOwnRecordLabel(value: OwnRecordLabel?): String = moshi.adapter(OwnRecordLabel::class.java).toJson(value)
    @TypeConverter
    fun toOwnRecordLabel(value: String?): OwnRecordLabel? = if (value != null && value != "null") moshi.adapter(OwnRecordLabel::class.java).fromJson(value) else null

    @TypeConverter
    fun fromMailMessageList(value: List<MailMessage>?): String = moshi.adapter<List<MailMessage>>(Types.newParameterizedType(List::class.java, MailMessage::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toMailMessageList(value: String?): List<MailMessage> = moshi.adapter<List<MailMessage>>(Types.newParameterizedType(List::class.java, MailMessage::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromNPCEntityList(value: List<NPCEntity>?): String = moshi.adapter<List<NPCEntity>>(Types.newParameterizedType(List::class.java, NPCEntity::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toNPCEntityList(value: String?): List<NPCEntity> = moshi.adapter<List<NPCEntity>>(Types.newParameterizedType(List::class.java, NPCEntity::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromPurchaseRequestList(value: List<PurchaseRequest>?): String = moshi.adapter<List<PurchaseRequest>>(Types.newParameterizedType(List::class.java, PurchaseRequest::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toPurchaseRequestList(value: String?): List<PurchaseRequest> = moshi.adapter<List<PurchaseRequest>>(Types.newParameterizedType(List::class.java, PurchaseRequest::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromBusinessAssetList(value: List<BusinessAsset>?): String = moshi.adapter<List<BusinessAsset>>(Types.newParameterizedType(List::class.java, BusinessAsset::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toBusinessAssetList(value: String?): List<BusinessAsset> = moshi.adapter<List<BusinessAsset>>(Types.newParameterizedType(List::class.java, BusinessAsset::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromStringMap(value: Map<String, Float>?): String = moshi.adapter<Map<String, Float>>(Types.newParameterizedType(Map::class.java, String::class.java, Float::class.javaObjectType)).toJson(value ?: emptyMap())
    @TypeConverter
    fun toStringMap(value: String?): Map<String, Float> = moshi.adapter<Map<String, Float>>(Types.newParameterizedType(Map::class.java, String::class.java, Float::class.javaObjectType)).fromJson(value ?: "{}") ?: emptyMap()

    @TypeConverter
    fun fromStringList(value: List<String>?): String = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toStringList(value: String?): List<String> = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromMeTubeVideoList(value: List<MeTubeVideo>?): String = moshi.adapter<List<MeTubeVideo>>(Types.newParameterizedType(List::class.java, MeTubeVideo::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toMeTubeVideoList(value: String?): List<MeTubeVideo> = moshi.adapter<List<MeTubeVideo>>(Types.newParameterizedType(List::class.java, MeTubeVideo::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromCastPostList(value: List<CastPost>?): String = moshi.adapter<List<CastPost>>(Types.newParameterizedType(List::class.java, CastPost::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toCastPostList(value: String?): List<CastPost> = moshi.adapter<List<CastPost>>(Types.newParameterizedType(List::class.java, CastPost::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromChatConversationList(value: List<ChatConversation>?): String = moshi.adapter<List<ChatConversation>>(Types.newParameterizedType(List::class.java, ChatConversation::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toChatConversationList(value: String?): List<ChatConversation> = moshi.adapter<List<ChatConversation>>(Types.newParameterizedType(List::class.java, ChatConversation::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromEmployeeList(value: List<Employee>?): String = moshi.adapter<List<Employee>>(Types.newParameterizedType(List::class.java, Employee::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toEmployeeList(value: String?): List<Employee> = moshi.adapter<List<Employee>>(Types.newParameterizedType(List::class.java, Employee::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromStockOrderList(value: List<StockOrder>?): String = moshi.adapter<List<StockOrder>>(Types.newParameterizedType(List::class.java, StockOrder::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toStockOrderList(value: String?): List<StockOrder> = moshi.adapter<List<StockOrder>>(Types.newParameterizedType(List::class.java, StockOrder::class.java)).fromJson(value ?: "[]") ?: emptyList()

    @TypeConverter
    fun fromCryptoOrderList(value: List<CryptoOrder>?): String = moshi.adapter<List<CryptoOrder>>(Types.newParameterizedType(List::class.java, CryptoOrder::class.java)).toJson(value ?: emptyList())
    @TypeConverter
    fun toCryptoOrderList(value: String?): List<CryptoOrder> = moshi.adapter<List<CryptoOrder>>(Types.newParameterizedType(List::class.java, CryptoOrder::class.java)).fromJson(value ?: "[]") ?: emptyList()
}
