package com.example.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class LegacyMilestones(
    val hasWikipediaPage: Boolean = false,
    val hasHallOfFame: Boolean = false,
    val hasMuseum: Boolean = false,
    val hasDocumentary: Boolean = false,
    val hasBiography: Boolean = false,
    val hasNetflixSeries: Boolean = false,
    val hasScholarships: Boolean = false,
    val artistsInspiredCount: Int = 0,
    val receivedGovernorPitch: Boolean = false,
    val receivedSenatorPitch: Boolean = false
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class SecurityCyberState(
    val hiredSecurityCompany: String = "None", // "None", "Vanguard Tactical", "Aegis Elite Command"
    val hasCyberInsurance: Boolean = false,
    val hasStudioInsurance: Boolean = false,
    val isCastAccountHijacked: Boolean = false,
    val isPhoneCompromised: Boolean = false,
    val lastHackIncident: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class FanSegmentsState(
    val casualFans: Long = 0,
    val hardcoreFans: Long = 0,
    val collectors: Long = 0,
    val concertGoers: Long = 0,
    val internationalFans: Long = 0,
    val onlineCommunity: Long = 0,
    val fansPersonalityLovers: Long = 100,
    val fansLyricsLovers: Long = 100,
    val fansVoicePurists: Long = 100,
    val fansFashionistas: Long = 100,
    val fansControversySeekers: Long = 100,
    val fansBusinessAdmirers: Long = 100,
    val fansCharityBelievers: Long = 100
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class WealthEngineState(
    val emergencyFundBalance: Double = 0.0,
    val payrollReserve: Double = 0.0,
    val businessReserve: Double = 0.0,
    val bondsBalance: Double = 0.0,
    val bondsInterestRate: Float = 0.10f, // 10% monthly yield
    val hasBondLicense: Boolean = false,
    val wasFormerBondMember: Boolean = false,
    val financialReviewLog: List<String> = emptyList(),
    val lastReviewWeek: Int = 0,
    val lastReviewYear: Int = 0,
    val financialHistoryCashFlow: List<String> = emptyList(), // Weekly cash flow logs (stored as strings)
    val smmPostCategory: String = "Music & Art",
    val smmAutoPost: Boolean = true
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class WebHubState(
    val wgtApplied: Boolean = false,
    val wgtAuditionWeek: Int = 0,
    val wgtAuditionYear: Int = 0,
    val wgtCompletedCount: Int = 0,
    val wgtWon: Boolean = false,
    val isSccWanted: Boolean = false,
    val sccCrimeReason: String = "",
    val sccFineAmount: Double = 45000.0,
    val blackHatPurchasesCount: Int = 0,
    val electionVotedCandidateId: String? = null,
    val electionVotedWeek: Int = 0,
    val electionVotedYear: Int = 0,
    val grandAwardNominated: Boolean = false,
    val grandAwardWon: Boolean = false,
    val grandAwardVipAccess: Boolean = false,
    val lastDemoSubmissionWeek: Int = 0,
    val labelContractObligations: String = "",
    val wgtSeason: Int = 1,
    val wgtSeasonYear: Int = 1,
    val wgtSemiFinalsWeek: Int = 14,
    val wgtGrandFinaleWeek: Int = 20,
    val wgtRegistrationStatus: String = "NONE",
    val wgtLastWinner: String = "",
    val wgtContestants: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class AvatarState(
    val avatarBaseLook: String = "Androgynous",
    val avatarSkinTone: String = "#F5C396",
    val avatarHairStyleId: String = "hair_slick",
    val avatarClothingId: String = "cloth_casual_tee",
    val avatarJewelryId: String = "jewel_none",
    val avatarOwnedItemIds: List<String> = listOf("hair_slick", "cloth_casual_tee", "jewel_none")
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class SpiritualState(
    val spiritualPrayerSetting: Boolean = true,
    val spiritualTitheSetting: Boolean = true,
    val spiritualTotalTithesPaid: Double = 0.0,
    val spiritualTitheReminderActive: Boolean = false,
    val spiritualJournal: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class BankingState(
    val savingsBalance: Double = 0.0,
    val loanAmount: Double = 0.0,
    val accumulatedLoanInterest: Double = 0.0,
    val taxDebt: Double = 0.0,
    val titheDebt: Double = 0.0,
    val savingsInterestRate: Float = 0.03f, // 2.5% weekly interest // 1.2% weekly interest
    val loanInterestRate: Float = 0.02f, // 2.0% weekly interest
    val mortgageAmount: Double = 0.0,
    val consecutiveUnpaidTaxWeeks: Int = 0,
    val consecutiveUnpaidTitheWeeks: Int = 0,
    val taxRate: Float = 0.12f,
    val personalLoanEligibility: Double = 10000.0,
    val businessLoanEligibility: Double = 20000.0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class SocialSentimentState(
    val positiveSentiment: Float = 50f,
    val negativeSentiment: Float = 20f,
    val neutralSentiment: Float = 30f,
    val conversationVolume: Long = 50,
    val trendingScore: Float = 10f,
    val mediaAttention: Float = 10f,
    val industryFollowers: Long = 10,
    val followers: Long = 100
)

@Entity(tableName = "game_states")
@androidx.annotation.Keep
data class GameStateEntity(
    @PrimaryKey val email: String,
    val artistName: String,
    val isAdmin: Boolean = false,
    val isDarkMode: Boolean = true,
    val cash: Double = 10000.0,
    val fans: Long = 0,
    val popularity: Float = 5f, // percentage 0-100
    val influence: Float = 5f, // percentage 0-100
    val voiceSkill: Float = 10f, // percentage 10-100
    val writingSkill: Float = 10f, // percentage 10-100
    val businessSkill: Float = 10f, // percentage 10-100
    val energy: Int = 100,
    val maxEnergy: Int = 100,
    val stressUntreatedWeeks: Int = 0,
    val stressTreatedConsecutiveWeeks: Int = 0,
    val stressTreatedThisWeek: Boolean = false,
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
    val businessVentures: List<BusinessVenture> = emptyList(),
    val stocks: List<Stock> = emptyList(),
    val cryptos: List<Crypto> = emptyList(),
    val ownLabel: OwnRecordLabel? = null,
    val signedLabelId: String? = null,
    val signedLabelCut: Float = 0.0f,
    val signedLabelRole: String? = null,
    val signedLabelLevel: Int = 1,
    val signedLabelStartWeek: Int = 0,
    val signedLabelStartYear: Int = 0,
    val signedLabelEndWeek: Int = 0,
    val signedLabelEndYear: Int = 0,
    val signedLabelReqSingles: Int = 0,
    val signedLabelReqAlbumsLP: Int = 0,
    val signedLabelReqAlbumsEP: Int = 0,
    val signedLabelReqMeTubeVideos: Int = 0,
    val signedLabelDeliveredSingles: Int = 0,
    val signedLabelDeliveredAlbumsLP: Int = 0,
    val signedLabelDeliveredAlbumsEP: Int = 0,
    val signedLabelDeliveredMeTubeVideos: Int = 0,
    val mailbox: List<MailMessage> = emptyList(),
    val npcs: List<NPCEntity> = emptyList(),
    val purchaseRequests: List<PurchaseRequest> = emptyList(),
    val activeLoanContracts: List<BankLoanContract> = emptyList(),
    val purchasedEbooks: List<String> = emptyList(),

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

    val stress: Float = 20f,
    val burnout: Float = 0f,
    val creativeInspiration: Float = 100f,
    val motivation: Float = 80f,
    val happiness: Float = 75f,
    val workLifeBalance: Float = 50f,

    val genreTrends: Map<String, Float> = emptyMap(),
    val genreTrendWeeksRemaining: Int = 0,
    val genreTrendDurationWeeks: Int = 0,
    val timelineEvents: List<String> = emptyList(),
    val ownedBusinesses: List<BusinessAsset> = emptyList(),
    val activeGlobalEvent: String? = null,
    val globalEventEffect: String? = null,

    // Digital Smartphone Ecosystem variables
    val meTubeVideos: List<MeTubeVideo> = emptyList(),
    val meTubeSubscribers: Long = 120,
    val meTubeWatchTime: Double = 45.0,
    val meTubeRevenue: Double = 0.0,
    val activeBrandDealName: String? = null,
    val brandDealWeeksRemaining: Int = 0,
    val brandDealWeeklyPayout: Double = 0.0,
    val brandDealRequiredEngagement: Long = 0,
    val brandDealPostedThisWeek: Boolean = false,
    val chatConversations: List<ChatConversation> = emptyList(),
    val pendingChatReplies: List<PendingChatReply> = emptyList(),
    val castPosts: List<CastPost> = emptyList(),
    val castTrendingHashtags: List<String> = emptyList(),
    val castTimeSpentThisWeek: Int = 0,
    val castPostsCountThisWeek: Int = 0,
    val castNicheFocus: String = "Music & Art",
    val hiredEmployees: List<Employee> = emptyList(),
    val candidateRoster: List<Employee> = emptyList(),
    val pendingStockOrders: List<StockOrder> = emptyList(),
    val pendingCryptoOrders: List<CryptoOrder> = emptyList(),
    val wallpaperId: String = "sunset",
    val isPhoneLocked: Boolean = true,
    val activePhone: String = "TECCNO",
    val ownedPhones: List<String> = listOf("TECCNO"),

    val notificationLogs: List<String> = emptyList(),
    val insuredCars: List<String> = emptyList(),
    val insuredHouses: List<String> = emptyList(),
    val adCompanyBudget: Double = 0.0,
    val activeAdCampaigns: List<AdCampaign> = emptyList(),
    val employeeEvents: List<String> = emptyList(),
    val activeBrandDeals: List<BrandDeal> = emptyList(),
    val newsArticles: List<NewsArticle> = emptyList(),
    val weatherCondition: String = "Sunny",
    val activeNoteText: String = "",
    val savedPhotos: List<String> = emptyList(),
    val scheduledActivities: List<String> = emptyList(),
    val economyCycle: String = "Stable Growth", // "Boom", "Stable Growth", "Inflation Peak", "Recession", "Depression"
    val inflationRate: Float = 0.02f, // 2% basic weekly rate
    val fuelPrice: Float = 1.25f, // fuel price per liter
    val touristIndex: Int = 100, // percentage indicator of tourist influx
    val isElectionSeason: Boolean = false,
    val residenceCountry: String = "United States",
    val hasGlobalStreamingLicense: Boolean = false,
    val accountantAutoInvestPercent: Float = 0.10f,
    val accountantAutoSellProfitMargin: Float = 0.25f,
    val cryptoAutoInvestPercent: Float = 0.10f,
    val cryptoAutoSellProfitMargin: Float = 0.25f,
    val merchStyles: List<MerchStyle> = emptyList(),
    val endorsedCandidateId: String? = null,
    val politicalTaxModifier: Float = 0.0f,
    val isElectionWeek: Boolean = false,
    val hasVotedInCurrentElection: Boolean = false,
    val isVoterRegistered: Boolean = false,
    val voterIdNumber: String = "",
    val currentElectionPosition: String = "State Governor",
    val currentElectionWinnerId: String? = null,
    val currentElectionWinnerName: String? = null,
    val endorsedPoliticianId: String? = null,
    val endorsedPoliticianName: String? = null,
    val tookEndorsementMoney: Boolean = false,
    val endorsementMoneyAmount: Double = 0.0,
    val isUnderArrest: Boolean = false,
    val arrestReason: String = "",
    val arrestBailAmount: Double = 75000.0,
    val arrestSentenceWeeksRemaining: Int = 0,
    val arrestAttorneyHired: String? = null,
    val studioCostModifier: Float = 0.0f,
    val streamingRoyaltyModifier: Float = 0.0f,
    val tourAttendanceModifier: Float = 0.0f,
    val cityProjectWeeksRemaining: Int = 0,
    val soundstreamMonthlyListeners: Long = 0,
    val soundstreamWorldRank: Int = 150,
    val soundstreamWeeklyStreams: Long = 0,
    val soundstreamSongs: List<ChartSong> = emptyList(),
    val activeFamilyReqNpcId: String? = null,
    val activeFamilyReqAmount: Double = 0.0,
    val activeFamilyReqType: String? = null,
    val activeFamilyReqDetail: String? = null,
    val activeFamilyReqStatus: String? = null,
    val lastFamilyReqWeek: Int = 0,

    // REAL-WORLD MUSIC DEALS & CONTRACTS
    val activeDistributionDeal: String = "DIY", // "DIY", "EliteVibe", "EmpireDistro"
    val activePublishingDeal: String = "None", // "None", "DIY", "CoPublishing", "AdminOnly"
    val recoupableAdvanceBalance: Double = 0.0,
    val signedDistributionWeek: Int = 0,
    val signedPublishingWeek: Int = 0,
    val albums: List<Album> = emptyList(),
    val worldHistory: List<WorldHistoryEntry> = emptyList(),

    // Granular Staff Delegations & Risk Safeguards
    val accountantStopLossMargin: Float = 0.15f,
    val cryptoStopLossMargin: Float = 0.20f,
    val accountantAutoInvestEnabled: Boolean = false,
    val cryptoAutoInvestEnabled: Boolean = false,

    // Modular Embedded State Components
    @Embedded val legacy: LegacyMilestones = LegacyMilestones(),
    @Embedded val security: SecurityCyberState = SecurityCyberState(),
    @Embedded val fanSegments: FanSegmentsState = FanSegmentsState(),
    @Embedded val wealth: WealthEngineState = WealthEngineState(),
    @Embedded val webHub: WebHubState = WebHubState(),
    val upgradedPlatforms: Set<String> = emptySet(),
    @Embedded val avatar: AvatarState = AvatarState(),
    @Embedded val spiritual: SpiritualState = SpiritualState(),
    @Embedded val banking: BankingState = BankingState(),
    @Embedded val sentiment: SocialSentimentState = SocialSentimentState(),
    val capitalClubMembers: List<CapitalClubMember> = emptyList()
) {
    // Backwards compatibility property getters
    val hasWikipediaPage: Boolean get() = legacy.hasWikipediaPage
    val hasHallOfFame: Boolean get() = legacy.hasHallOfFame
    val hasMuseum: Boolean get() = legacy.hasMuseum
    val hasDocumentary: Boolean get() = legacy.hasDocumentary
    val hasBiography: Boolean get() = legacy.hasBiography
    val hasNetflixSeries: Boolean get() = legacy.hasNetflixSeries
    val hasScholarships: Boolean get() = legacy.hasScholarships
    val artistsInspiredCount: Int get() = legacy.artistsInspiredCount
    val receivedGovernorPitch: Boolean get() = legacy.receivedGovernorPitch
    val receivedSenatorPitch: Boolean get() = legacy.receivedSenatorPitch

    val hiredSecurityCompany: String get() = security.hiredSecurityCompany
    val hasCyberInsurance: Boolean get() = security.hasCyberInsurance
    val hasStudioInsurance: Boolean get() = security.hasStudioInsurance
    val isCastAccountHijacked: Boolean get() = security.isCastAccountHijacked
    val isPhoneCompromised: Boolean get() = security.isPhoneCompromised
    val lastHackIncident: String get() = security.lastHackIncident

    val casualFans: Long get() = fanSegments.casualFans
    val hardcoreFans: Long get() = fanSegments.hardcoreFans
    val collectors: Long get() = fanSegments.collectors
    val concertGoers: Long get() = fanSegments.concertGoers
    val internationalFans: Long get() = fanSegments.internationalFans
    val onlineCommunity: Long get() = fanSegments.onlineCommunity
    val fansPersonalityLovers: Long get() = fanSegments.fansPersonalityLovers
    val fansLyricsLovers: Long get() = fanSegments.fansLyricsLovers
    val fansVoicePurists: Long get() = fanSegments.fansVoicePurists
    val fansFashionistas: Long get() = fanSegments.fansFashionistas
    val fansControversySeekers: Long get() = fanSegments.fansControversySeekers
    val fansBusinessAdmirers: Long get() = fanSegments.fansBusinessAdmirers
    val fansCharityBelievers: Long get() = fanSegments.fansCharityBelievers

    val emergencyFundBalance: Double get() = wealth.emergencyFundBalance
    val payrollReserve: Double get() = wealth.payrollReserve
    val businessReserve: Double get() = wealth.businessReserve
    val bondsBalance: Double get() = wealth.bondsBalance
    val bondsInterestRate: Float get() = wealth.bondsInterestRate
    val hasBondLicense: Boolean get() = wealth.hasBondLicense
    val wasFormerBondMember: Boolean get() = wealth.wasFormerBondMember
    val financialReviewLog: List<String> get() = wealth.financialReviewLog
    val lastReviewWeek: Int get() = wealth.lastReviewWeek
    val lastReviewYear: Int get() = wealth.lastReviewYear
    val financialHistoryCashFlow: List<String> get() = wealth.financialHistoryCashFlow
    val smmPostCategory: String get() = wealth.smmPostCategory
    val smmAutoPost: Boolean get() = wealth.smmAutoPost

    val wgtApplied: Boolean get() = webHub.wgtApplied
    val wgtAuditionWeek: Int get() = webHub.wgtAuditionWeek
    val wgtAuditionYear: Int get() = webHub.wgtAuditionYear
    val wgtCompletedCount: Int get() = webHub.wgtCompletedCount
    val wgtWon: Boolean get() = webHub.wgtWon
    val wgtSeason: Int get() = webHub.wgtSeason
    val wgtSeasonYear: Int get() = webHub.wgtSeasonYear
    val wgtSemiFinalsWeek: Int get() = webHub.wgtSemiFinalsWeek
    val wgtGrandFinaleWeek: Int get() = webHub.wgtGrandFinaleWeek
    val wgtRegistrationStatus: String get() = webHub.wgtRegistrationStatus
    val wgtLastWinner: String get() = webHub.wgtLastWinner
    val isSccWanted: Boolean get() = webHub.isSccWanted
    val sccCrimeReason: String get() = webHub.sccCrimeReason
    val sccFineAmount: Double get() = webHub.sccFineAmount
    val blackHatPurchasesCount: Int get() = webHub.blackHatPurchasesCount
    val electionVotedCandidateId: String? get() = webHub.electionVotedCandidateId
    val electionVotedWeek: Int get() = webHub.electionVotedWeek
    val electionVotedYear: Int get() = webHub.electionVotedYear
    val grandAwardNominated: Boolean get() = webHub.grandAwardNominated
    val grandAwardWon: Boolean get() = webHub.grandAwardWon
    val grandAwardVipAccess: Boolean get() = webHub.grandAwardVipAccess
    val lastDemoSubmissionWeek: Int get() = webHub.lastDemoSubmissionWeek
    val labelContractObligations: String get() = webHub.labelContractObligations

    val avatarBaseLook: String get() = avatar.avatarBaseLook
    val avatarSkinTone: String get() = avatar.avatarSkinTone
    val avatarHairStyleId: String get() = avatar.avatarHairStyleId
    val avatarClothingId: String get() = avatar.avatarClothingId
    val avatarJewelryId: String get() = avatar.avatarJewelryId
    val avatarOwnedItemIds: List<String> get() = avatar.avatarOwnedItemIds

    val artistLevel: Int
        get() {
            val totalSkills = (voiceSkill + writingSkill + businessSkill).coerceIn(0f, 300f)
            val skillRatio = totalSkills / 300f
            val reachRatio = ((fans + followers).toFloat() / 1000000f).coerceIn(0f, 1.0f)
            val catalogRatio = (songs.count { it.isReleased }.toFloat() / 15f).coerceIn(0f, 1.0f)
            val avgNpcRel = if (npcs.isNotEmpty()) npcs.map { it.relationshipWithPlayer }.average().toFloat() / 100f else 0.5f
            val industryRatio = ((industryFollowers.toFloat() / 50000f).coerceIn(0f, 1.0f) * 0.5f + avgNpcRel * 0.5f).coerceIn(0f, 1.0f)
            val overallMastery = (skillRatio * 0.35f + reachRatio * 0.30f + catalogRatio * 0.15f + industryRatio * 0.20f).coerceIn(0.0f, 1.0f)
            return (overallMastery * 9f).toInt() + 1
        }

    val spiritualPrayerSetting: Boolean get() = spiritual.spiritualPrayerSetting
    val spiritualTitheSetting: Boolean get() = spiritual.spiritualTitheSetting
    val spiritualTotalTithesPaid: Double get() = spiritual.spiritualTotalTithesPaid
    val spiritualTitheReminderActive: Boolean get() = spiritual.spiritualTitheReminderActive
    val spiritualJournal: List<String> get() = spiritual.spiritualJournal

    val savingsBalance: Double get() = banking.savingsBalance
    val loanAmount: Double get() = banking.loanAmount
    val accumulatedLoanInterest: Double get() = banking.accumulatedLoanInterest
    val taxDebt: Double get() = banking.taxDebt
    val titheDebt: Double get() = banking.titheDebt
    val savingsInterestRate: Float get() = banking.savingsInterestRate
    val loanInterestRate: Float get() = banking.loanInterestRate
    val mortgageAmount: Double get() = banking.mortgageAmount
    val consecutiveUnpaidTaxWeeks: Int get() = banking.consecutiveUnpaidTaxWeeks
    val consecutiveUnpaidTitheWeeks: Int get() = banking.consecutiveUnpaidTitheWeeks
    val taxRate: Float get() = banking.taxRate
    val personalLoanEligibility: Double get() = banking.personalLoanEligibility
    val businessLoanEligibility: Double get() = banking.businessLoanEligibility

    val positiveSentiment: Float get() = sentiment.positiveSentiment
    val negativeSentiment: Float get() = sentiment.negativeSentiment
    val neutralSentiment: Float get() = sentiment.neutralSentiment
    val conversationVolume: Long get() = sentiment.conversationVolume
    val trendingScore: Float get() = sentiment.trendingScore
    val mediaAttention: Float get() = sentiment.mediaAttention
    val industryFollowers: Long get() = sentiment.industryFollowers
    val followers: Long get() = sentiment.followers
}

class Converters {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromBusinessVentureList(value: List<BusinessVenture>?): String = try {
        moshi.adapter<List<BusinessVenture>>(Types.newParameterizedType(List::class.java, BusinessVenture::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toBusinessVentureList(value: String?): List<BusinessVenture> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<BusinessVenture>>(Types.newParameterizedType(List::class.java, BusinessVenture::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromCapitalClubMemberList(value: List<CapitalClubMember>?): String = try {
        moshi.adapter<List<CapitalClubMember>>(Types.newParameterizedType(List::class.java, CapitalClubMember::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toCapitalClubMemberList(value: String?): List<CapitalClubMember> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<CapitalClubMember>>(Types.newParameterizedType(List::class.java, CapitalClubMember::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromWorldHistoryEntryList(value: List<WorldHistoryEntry>?): String = try {
        moshi.adapter<List<WorldHistoryEntry>>(Types.newParameterizedType(List::class.java, WorldHistoryEntry::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toWorldHistoryEntryList(value: String?): List<WorldHistoryEntry> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<WorldHistoryEntry>>(Types.newParameterizedType(List::class.java, WorldHistoryEntry::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromAlbumList(value: List<Album>?): String = try {
        moshi.adapter<List<Album>>(Types.newParameterizedType(List::class.java, Album::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toAlbumList(value: String?): List<Album> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Album>>(Types.newParameterizedType(List::class.java, Album::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromChartSongList(value: List<ChartSong>?): String = try {
        moshi.adapter<List<ChartSong>>(Types.newParameterizedType(List::class.java, ChartSong::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toChartSongList(value: String?): List<ChartSong> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<ChartSong>>(Types.newParameterizedType(List::class.java, ChartSong::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromMerchStyleList(value: List<MerchStyle>?): String = try {
        moshi.adapter<List<MerchStyle>>(Types.newParameterizedType(List::class.java, MerchStyle::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toMerchStyleList(value: String?): List<MerchStyle> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<MerchStyle>>(Types.newParameterizedType(List::class.java, MerchStyle::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromCarList(value: List<Car>?): String = try {
        moshi.adapter<List<Car>>(Types.newParameterizedType(List::class.java, Car::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toCarList(value: String?): List<Car> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Car>>(Types.newParameterizedType(List::class.java, Car::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromHouseList(value: List<House>?): String = try {
        moshi.adapter<List<House>>(Types.newParameterizedType(List::class.java, House::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toHouseList(value: String?): List<House> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<House>>(Types.newParameterizedType(List::class.java, House::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromInstrumentList(value: List<Instrument>?): String = try {
        moshi.adapter<List<Instrument>>(Types.newParameterizedType(List::class.java, Instrument::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toInstrumentList(value: String?): List<Instrument> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Instrument>>(Types.newParameterizedType(List::class.java, Instrument::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromSongList(value: List<Song>?): String = try {
        moshi.adapter<List<Song>>(Types.newParameterizedType(List::class.java, Song::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toSongList(value: String?): List<Song> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Song>>(Types.newParameterizedType(List::class.java, Song::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromTourList(value: List<Tour>?): String = try {
        moshi.adapter<List<Tour>>(Types.newParameterizedType(List::class.java, Tour::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toTourList(value: String?): List<Tour> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Tour>>(Types.newParameterizedType(List::class.java, Tour::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromSideJobList(value: List<SideJob>?): String = try {
        moshi.adapter<List<SideJob>>(Types.newParameterizedType(List::class.java, SideJob::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toSideJobList(value: String?): List<SideJob> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<SideJob>>(Types.newParameterizedType(List::class.java, SideJob::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromStockList(value: List<Stock>?): String = try {
        moshi.adapter<List<Stock>>(Types.newParameterizedType(List::class.java, Stock::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toStockList(value: String?): List<Stock> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Stock>>(Types.newParameterizedType(List::class.java, Stock::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromCryptoList(value: List<Crypto>?): String = try {
        moshi.adapter<List<Crypto>>(Types.newParameterizedType(List::class.java, Crypto::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toCryptoList(value: String?): List<Crypto> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Crypto>>(Types.newParameterizedType(List::class.java, Crypto::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromOwnRecordLabel(value: OwnRecordLabel?): String = try {
        moshi.adapter(OwnRecordLabel::class.java).toJson(value)
    } catch (e: Throwable) { "null" }

    @TypeConverter
    fun toOwnRecordLabel(value: String?): OwnRecordLabel? = try {
        if (value != null && value != "null" && value.isNotBlank()) moshi.adapter(OwnRecordLabel::class.java).fromJson(value) else null
    } catch (e: Throwable) { null }

    @TypeConverter
    fun fromMailMessageList(value: List<MailMessage>?): String = try {
        moshi.adapter<List<MailMessage>>(Types.newParameterizedType(List::class.java, MailMessage::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toMailMessageList(value: String?): List<MailMessage> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<MailMessage>>(Types.newParameterizedType(List::class.java, MailMessage::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromNPCEntityList(value: List<NPCEntity>?): String = try {
        moshi.adapter<List<NPCEntity>>(Types.newParameterizedType(List::class.java, NPCEntity::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toNPCEntityList(value: String?): List<NPCEntity> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<NPCEntity>>(Types.newParameterizedType(List::class.java, NPCEntity::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromPurchaseRequestList(value: List<PurchaseRequest>?): String = try {
        moshi.adapter<List<PurchaseRequest>>(Types.newParameterizedType(List::class.java, PurchaseRequest::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toPurchaseRequestList(value: String?): List<PurchaseRequest> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<PurchaseRequest>>(Types.newParameterizedType(List::class.java, PurchaseRequest::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromBusinessAssetList(value: List<BusinessAsset>?): String = try {
        moshi.adapter<List<BusinessAsset>>(Types.newParameterizedType(List::class.java, BusinessAsset::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toBusinessAssetList(value: String?): List<BusinessAsset> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<BusinessAsset>>(Types.newParameterizedType(List::class.java, BusinessAsset::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromStringMap(value: Map<String, Float>?): String = try {
        moshi.adapter<Map<String, Float>>(Types.newParameterizedType(Map::class.java, String::class.java, Float::class.javaObjectType)).toJson(value ?: emptyMap())
    } catch (e: Throwable) { "{}" }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, Float> = try {
        if (value.isNullOrBlank() || value == "null") emptyMap()
        else moshi.adapter<Map<String, Float>>(Types.newParameterizedType(Map::class.java, String::class.java, Float::class.javaObjectType)).fromJson(value) ?: emptyMap()
    } catch (e: Throwable) { emptyMap() }

    @TypeConverter
    fun fromStringList(value: List<String>?): String = try {
        moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toStringList(value: String?): List<String> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromMeTubeVideoList(value: List<MeTubeVideo>?): String = try {
        moshi.adapter<List<MeTubeVideo>>(Types.newParameterizedType(List::class.java, MeTubeVideo::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toMeTubeVideoList(value: String?): List<MeTubeVideo> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<MeTubeVideo>>(Types.newParameterizedType(List::class.java, MeTubeVideo::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromCastPostList(value: List<CastPost>?): String = try {
        moshi.adapter<List<CastPost>>(Types.newParameterizedType(List::class.java, CastPost::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toCastPostList(value: String?): List<CastPost> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<CastPost>>(Types.newParameterizedType(List::class.java, CastPost::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromChatConversationList(value: List<ChatConversation>?): String = try {
        moshi.adapter<List<ChatConversation>>(Types.newParameterizedType(List::class.java, ChatConversation::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toChatConversationList(value: String?): List<ChatConversation> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<ChatConversation>>(Types.newParameterizedType(List::class.java, ChatConversation::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromPendingChatReplyList(value: List<PendingChatReply>?): String = try {
        moshi.adapter<List<PendingChatReply>>(Types.newParameterizedType(List::class.java, PendingChatReply::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toPendingChatReplyList(value: String?): List<PendingChatReply> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<PendingChatReply>>(Types.newParameterizedType(List::class.java, PendingChatReply::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromEmployeeList(value: List<Employee>?): String = try {
        moshi.adapter<List<Employee>>(Types.newParameterizedType(List::class.java, Employee::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toEmployeeList(value: String?): List<Employee> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<Employee>>(Types.newParameterizedType(List::class.java, Employee::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromStockOrderList(value: List<StockOrder>?): String = try {
        moshi.adapter<List<StockOrder>>(Types.newParameterizedType(List::class.java, StockOrder::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toStockOrderList(value: String?): List<StockOrder> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<StockOrder>>(Types.newParameterizedType(List::class.java, StockOrder::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromCryptoOrderList(value: List<CryptoOrder>?): String = try {
        moshi.adapter<List<CryptoOrder>>(Types.newParameterizedType(List::class.java, CryptoOrder::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toCryptoOrderList(value: String?): List<CryptoOrder> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<CryptoOrder>>(Types.newParameterizedType(List::class.java, CryptoOrder::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromAdCampaignList(value: List<AdCampaign>?): String = try {
        moshi.adapter<List<AdCampaign>>(Types.newParameterizedType(List::class.java, AdCampaign::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toAdCampaignList(value: String?): List<AdCampaign> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<AdCampaign>>(Types.newParameterizedType(List::class.java, AdCampaign::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromBrandDealList(value: List<BrandDeal>?): String = try {
        moshi.adapter<List<BrandDeal>>(Types.newParameterizedType(List::class.java, BrandDeal::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toBrandDealList(value: String?): List<BrandDeal> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<BrandDeal>>(Types.newParameterizedType(List::class.java, BrandDeal::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromNewsArticleList(value: List<NewsArticle>?): String = try {
        moshi.adapter<List<NewsArticle>>(Types.newParameterizedType(List::class.java, NewsArticle::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toNewsArticleList(value: String?): List<NewsArticle> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<NewsArticle>>(Types.newParameterizedType(List::class.java, NewsArticle::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }

    @TypeConverter
    fun fromStringSet(value: Set<String>?): String = try {
        moshi.adapter<Set<String>>(Types.newParameterizedType(Set::class.java, String::class.java)).toJson(value ?: emptySet())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toStringSet(value: String?): Set<String> = try {
        if (value.isNullOrBlank() || value == "null") emptySet()
        else moshi.adapter<Set<String>>(Types.newParameterizedType(Set::class.java, String::class.java)).fromJson(value) ?: emptySet()
    } catch (e: Throwable) { emptySet() }

    @TypeConverter
    fun fromBankLoanContractList(value: List<BankLoanContract>?): String = try {
        moshi.adapter<List<BankLoanContract>>(Types.newParameterizedType(List::class.java, BankLoanContract::class.java)).toJson(value ?: emptyList())
    } catch (e: Throwable) { "[]" }

    @TypeConverter
    fun toBankLoanContractList(value: String?): List<BankLoanContract> = try {
        if (value.isNullOrBlank() || value == "null") emptyList()
        else moshi.adapter<List<BankLoanContract>>(Types.newParameterizedType(List::class.java, BankLoanContract::class.java)).fromJson(value) ?: emptyList()
    } catch (e: Throwable) { emptyList() }
}
