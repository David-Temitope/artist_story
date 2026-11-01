package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.random.Random
import kotlin.math.roundToInt

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.gameStateDao()
    private val stateWriteMutex = Mutex()
    private val prefs = application.getSharedPreferences("artist_story_prefs", android.content.Context.MODE_PRIVATE)

    // Login/Auth State
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _currentArtistName = MutableStateFlow<String?>(null)
    val currentArtistName: StateFlow<String?> = _currentArtistName.asStateFlow()

    init {
        val savedEmail = prefs.getString("saved_email", null)
        val savedName = prefs.getString("saved_name", null)
        if (savedEmail != null && savedName != null) {
            _currentUserEmail.value = savedEmail
            _currentArtistName.value = savedName
            viewModelScope.launch {
                try {
                    val existing = dao.getGameStateDirect(savedEmail)
                    if (existing == null) {
                        loginOrCreateAccount(savedEmail, savedName)
                    }
                } catch (e: Throwable) {
                    android.util.Log.e("GameViewModel", "Auto-init recovery check error: ${e.message}", e)
                }
            }
        }
    }

    // Observed Game State
    val gameState: StateFlow<GameStateEntity?> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) {
                dao.getGameState(email).map { state ->
                    if (state != null && state.capitalClubMembers.isEmpty()) {
                        state.copy(
                            capitalClubMembers = getInitialCapitalClubMembers()
                        )
                    } else state
                }
            } else kotlinx.coroutines.flow.flowOf(null)
        }
        .catch { e ->
            android.util.Log.e("GameViewModel", "Error observing game state: ${e.message}", e)
            emit(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin State: all users
    val allGameStates: StateFlow<List<GameStateEntity>> = dao.getAllGameStates()
        .catch { e ->
            android.util.Log.e("GameViewModel", "Error observing all states: ${e.message}", e)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly summary report log
    private val _weeklyReport = MutableStateFlow<String>("")
    val weeklyReport: StateFlow<String> = _weeklyReport.asStateFlow()

    // Immediate Action Feedback
    private val _actionFeedback = MutableStateFlow<ActionFeedback?>(null)
    val actionFeedback: StateFlow<ActionFeedback?> = _actionFeedback.asStateFlow()

    fun postActionFeedback(
        title: String,
        subtitle: String = "",
        deltas: List<StatFeedbackItem>,
        iconType: String = "bolt"
    ) {
        viewModelScope.launch {
            _actionFeedback.value = ActionFeedback(
                id = UUID.randomUUID().toString(),
                title = title,
                subtitle = subtitle,
                deltas = deltas,
                iconType = iconType,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    fun clearActionFeedback() {
        _actionFeedback.value = null
    }

    // Custom alerts
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.emit(msg)
        }
    }

    fun loginOrCreateAccount(email: String, artistName: String, country: String = "United States") {
        viewModelScope.launch {
            val trimmedEmail = email.trim().lowercase()
            val trimmedName = artistName.trim()
            if (trimmedEmail.isEmpty() || trimmedName.isEmpty()) {
                showToast("Email and Name cannot be empty!")
                return@launch
            }

            _currentUserEmail.value = trimmedEmail
            _currentArtistName.value = trimmedName

            // Save credentials for auto-login
            prefs.edit()
                .putString("saved_email", trimmedEmail)
                .putString("saved_name", trimmedName)
                .apply()

            // Check if user exists
            val existing = dao.getGameStateDirect(trimmedEmail)
            if (existing == null) {
                // Initialize default game state
                // A local save ID is not authentication and must never grant elevated privileges.
                val isAdmin = false

                // Proceduralize NPCEntities
                val random = Random(System.currentTimeMillis())
                val personalities = listOf("Charismatic", "Ambitious", "Creative", "Rebel", "Hustler", "Diva")
                val goals = listOf("Superstardom", "Critical Acclaim", "Financial Empire", "Genre Pioneer", "Underground Legend")
                val genres = listOf("Pop", "Hip Hop", "Rock", "R&B", "Electronic", "Country", "Jazz", "Indie")

                val proceduralNPCs = GameData.DEFAULT_NPCS.map { npc ->
                    npc.copy(
                        age = random.nextInt(18, 35),
                        personality = personalities.random(random),
                        confidence = random.nextFloat() * 40f + 50f,
                        creativity = random.nextFloat() * 40f + 50f,
                        businessIntel = random.nextFloat() * 45f + 40f,
                        genrePreference = genres.random(random),
                        wealth = random.nextDouble(15000.0, 250000.0),
                        stress = random.nextFloat() * 30f + 10f,
                        mentalHealth = random.nextFloat() * 30f + 65f,
                        happiness = random.nextFloat() * 30f + 65f,
                        relationshipWithPlayer = 50f,
                        currentRecordLabel = if (random.nextFloat() > 0.5f) listOf("Def Jam Records", "Apex Records", "Sub Pop", "Sony Music").random(random) else "Independent",
                        managerName = listOf("Alfie Miller", "Sophia Carter", "Marcus Vance", "Olivia Sterling", "None").random(random),
                        careerGoal = goals.random(random)
                    ).withPsychology(random)
                }

                val customNPCs = listOf(
                    NPCEntity(
                        id = "npc_gov_sterling",
                        name = "Governor Sterling",
                        popularity = 75f,
                        influence = 80f,
                        fans = 10000000L,
                        bestSongTitle = "Political Pulse",
                        bestSongRating = 1.0f,
                        bestSongStreams = 0L,
                        age = 52,
                        personality = "Ambitious",
                        relationshipWithPlayer = 45f,
                        careerGoal = "Political Power"
                    ),
                    NPCEntity(
                        id = "npc_sen_vance",
                        name = "Senator Vance",
                        popularity = 85f,
                        influence = 90f,
                        fans = 50000000L,
                        bestSongTitle = "The Freedom Anthem",
                        bestSongRating = 1.0f,
                        bestSongStreams = 0L,
                        age = 48,
                        personality = "Hustler",
                        relationshipWithPlayer = 40f,
                        careerGoal = "Presidency"
                    ),
                    NPCEntity(
                        id = "npc_family_uncle_joe",
                        name = "Uncle Joe",
                        popularity = 15f,
                        influence = 10f,
                        fans = 100L,
                        bestSongTitle = "Grill Master Blues",
                        bestSongRating = 3.0f,
                        bestSongStreams = 1000L,
                        age = 45,
                        personality = "Hustler",
                        relationshipWithPlayer = 70f,
                        careerGoal = "Get Rich Quick"
                    ),
                    NPCEntity(
                        id = "npc_family_cousin_sarah",
                        name = "Cousin Sarah",
                        popularity = 25f,
                        influence = 15f,
                        fans = 1200L,
                        bestSongTitle = "Guitar Strings",
                        bestSongRating = 4.0f,
                        bestSongStreams = 5000L,
                        age = 22,
                        personality = "Creative",
                        relationshipWithPlayer = 75f,
                        careerGoal = "Bake Shop Dream"
                    ),
                    NPCEntity(
                        id = "npc_family_mom",
                        name = "Mom",
                        popularity = 5f,
                        influence = 5f,
                        fans = 50L,
                        bestSongTitle = "Lullaby for You",
                        bestSongRating = 5.0f,
                        bestSongStreams = 10L,
                        age = 55,
                        personality = "Charismatic",
                        relationshipWithPlayer = 90f,
                        careerGoal = "Your Success"
                    )
                )

                                val initialDefaultCastPosts = listOf(
                    CastPost(
                        id = "init_cast_alpha",
                        authorName = "Alphadom Official",
                        authorHandle = "@alphadom",
                        content = "Build, scale, and automate your independent music label on the cloud. Visit alphadom.online to deploy your next studio workstation today.",
                        likes = 1240,
                        reposts = 340,
                        commentsCount = 42,
                        week = 1,
                        year = 1,
                        isByPlayer = false
                    ),
                    CastPost(
                        id = "init_cast_apex",
                        authorName = "Apex Records & Publishing",
                        authorHandle = "@apexrecords",
                        content = "Looking for raw talent and demo submissions. Check out our global contract roster at apexrecord.org and get heard by A&R executives.",
                        likes = 3120,
                        reposts = 890,
                        commentsCount = 115,
                        week = 1,
                        year = 1,
                        isByPlayer = false
                    ),
                    CastPost(
                        id = "init_cast_wgt",
                        authorName = "Who Is Got Talent",
                        authorHandle = "@wgt_show",
                        content = "Auditions are now open worldwide! Compete on the main stage and secure your grand finale spot at wgt.show.",
                        likes = 5400,
                        reposts = 1220,
                        commentsCount = 310,
                        week = 1,
                        year = 1,
                        isByPlayer = false
                    ),
                    CastPost(
                        id = "init_cast_pulse",
                        authorName = "PulseWire News Network",
                        authorHandle = "@pulsewire",
                        content = "Global charts update: Streaming volumes reach an all-time high as independent artists take over the airwaves. Read breaking stories at pulsewire.com.",
                        likes = 2450,
                        reposts = 630,
                        commentsCount = 78,
                        week = 1,
                        year = 1,
                        isByPlayer = false
                    ),
                    CastPost(
                        id = "init_cast_sold",
                        authorName = "SOLD Real Estate Exchange",
                        authorHandle = "@sold_properties",
                        content = "Explore luxury recording compounds, high-rise penthouses, and private estates on sold.com.",
                        likes = 1890,
                        reposts = 410,
                        commentsCount = 52,
                        week = 1,
                        year = 1,
                        isByPlayer = false
                    )
                )
                val defaultState = GameStateEntity(
                castPosts = initialDefaultCastPosts,
                email = trimmedEmail,
                artistName = trimmedName,
                isAdmin = isAdmin,
                capitalClubMembers = getInitialCapitalClubMembers(),
                chatConversations = getInitialChatConversations(),
                songs = emptyList(),
                sideJobs = GameData.SIDE_JOBS,
                stocks = GameData.STOCKS,
                cryptos = GameData.CRYPTOS,
                npcs = proceduralNPCs + customNPCs.map { it.withPsychology(random) },
                industryRespect = 10f,
                publicImage = 50f,
                criticalAcclaim = 10f,
                trustworthiness = 50f,
                legacyPoints = 0,
                businessReputation = 50f,
                discipline = 50f,
                integrity = 50f,
                generosity = 50f,
                humility = 50f,
                reliability = 50f,
                stress = 20f,
                burnout = 0f,
                creativeInspiration = 100f,
                motivation = 80f,
                happiness = 75f,
                workLifeBalance = 50f,
                genreTrends = mapOf(
                        "genre_pop" to 1.0f,
                        "genre_hiphop" to GenreTrendSimulator.activeMultiplier,
                        "genre_rock" to GenreTrendSimulator.coolingMultiplier,
                        "genre_rnb" to 1.0f,
                        "genre_electronic" to 1.0f,
                        "genre_country" to 0.8f,
                        "genre_jazz" to 0.7f,
                        "genre_indie" to 1.0f
                    ),
                genreTrendWeeksRemaining = 4,
                genreTrendDurationWeeks = 4,
                timelineEvents = listOf("Year 1, Week 1: Started your music career as an aspiring independent artist in $country!"),
                mailbox = listOf(
                        MailMessage(
                            id = "welcome_msg",
                            sender = "Music Council",
                            subject = "Welcome to Genesis!",
                            body = "Welcome $trimmedName! Start by doing some Side Jobs in the Gigs section to earn cash, then rent a Studio to write your first hit song! Reach 45% influence on social media and 60% popularity to get Record Label deals.",
                            type = "NORMAL",
                            expiresWeek = 5,
                            expiresYear = 1
                        )
                    ),
                residenceCountry = country,
                hasGlobalStreamingLicense = false,
                fanSegments = FanSegmentsState(casualFans = 200, hardcoreFans = 10, collectors = 2, concertGoers = 5, internationalFans = 0, onlineCommunity = 50),
                sentiment = SocialSentimentState(followers = 1500, industryFollowers = 50, mediaAttention = 10f, trendingScore = 5f, conversationVolume = 120, positiveSentiment = 60f, negativeSentiment = 10f, neutralSentiment = 30f)
            )
                dao.insertGameState(defaultState)
                _weeklyReport.value = "Welcome to your musical journey, $trimmedName!"
            } else {
                var loaded = existing
                if (loaded.cash < 0.0) {
                    loaded = loaded.copy(cash = 10000.0)
                }
                if (loaded.chatConversations.isEmpty()) {
                    loaded = loaded.copy(chatConversations = getInitialChatConversations())
                }
                if (loaded != existing) {
                    dao.insertGameState(loaded)
                }
                _weeklyReport.value = "Loaded saved game for ${existing.artistName}."
            }
        }
    }

    fun getInitialChatConversations(): List<ChatConversation> {
        val now = System.currentTimeMillis()
        return listOf(
            ChatConversation(
                npcId = "npc_family_mom",
                npcName = "Mom",
                messages = listOf(
                    ChatMessage(
                        id = "init_mom_1",
                        senderId = "npc_family_mom",
                        senderName = "Mom",
                        message = "Hey sweetheart! 💖 I'm so proud of you taking the leap into the music world. Remember to drink water, practice your vocals, and stay humble. Call me whenever you can!",
                        timestamp = now - 7200000L
                    )
                ),
                unreadCount = 1
            ),
            ChatConversation(
                npcId = "npc_family_uncle_joe",
                npcName = "Uncle Joe",
                messages = listOf(
                    ChatMessage(
                        id = "init_joe_1",
                        senderId = "npc_family_uncle_joe",
                        senderName = "Uncle Joe",
                        message = "What's good superstar! 🕶️ Heard through the family grapevine you're chasing that music bag! Get those streams up and don't sign any crazy contracts without asking your uncle first!",
                        timestamp = now - 3600000L
                    )
                ),
                unreadCount = 1
            ),
            ChatConversation(
                npcId = "npc_family_cousin_sarah",
                npcName = "Cousin Sarah",
                messages = listOf(
                    ChatMessage(
                        id = "init_sarah_1",
                        senderId = "npc_family_cousin_sarah",
                        senderName = "Cousin Sarah",
                        message = "Hey! 🎶 I told all my friends you're recording your first track. Can't wait to play it on repeat! Hit me up when you need some acoustic inspiration!",
                        timestamp = now - 1800000L
                    )
                ),
                unreadCount = 1
            ),
            ChatConversation(
                npcId = "npc_rookie_ryan",
                npcName = "Ryan Sparks",
                messages = listOf(
                    ChatMessage(
                        id = "init_ryan_1",
                        senderId = "npc_rookie_ryan",
                        senderName = "Ryan Sparks",
                        message = "Hey there! Saw your new artist profile in the indie directory. Good to see another rookie on the grind. Let's stay in touch and maybe trade feedback on beats!",
                        timestamp = now - 900000L
                    )
                ),
                unreadCount = 1
            )
        )
    }

    fun ensureInitialChats() {
        updateState { state ->
            if (state.chatConversations.isNotEmpty()) return@updateState state
            state.copy(chatConversations = getInitialChatConversations())
        }
    }

    fun logout() {
        _currentUserEmail.value = null
        _currentArtistName.value = null
        prefs.edit().clear().apply()
    }

    // Helper to edit current state and save it
    fun updateState(block: (GameStateEntity) -> GameStateEntity) {
        val email = _currentUserEmail.value ?: return
        viewModelScope.launch {
            stateWriteMutex.withLock {
                val currentFromDb = dao.getGameStateDirect(email) ?: return@withLock
                val current = if (currentFromDb.capitalClubMembers.isEmpty()) {
                    currentFromDb.copy(
                        capitalClubMembers = getInitialCapitalClubMembers()
                    )
                } else {
                    val hasEmptyImage = currentFromDb.capitalClubMembers.any { it.imageUrl.isEmpty() }
                    if (hasEmptyImage) {
                        val initialMembers = getInitialCapitalClubMembers()
                        val mergedMembers = currentFromDb.capitalClubMembers.map { member ->
                            if (member.imageUrl.isEmpty()) {
                                val initial = initialMembers.find { it.id == member.id }
                                if (initial != null) member.copy(imageUrl = initial.imageUrl) else member
                            } else {
                                member
                            }
                        }
                        val missingMembers = initialMembers.filter { init -> currentFromDb.capitalClubMembers.none { it.id == init.id } }
                        currentFromDb.copy(capitalClubMembers = mergedMembers + missingMembers)
                    } else currentFromDb
                }
                val currentWithChats = if (current.chatConversations.isEmpty()) {
                    current.copy(chatConversations = getInitialChatConversations())
                } else current
                val updated = block(currentWithChats)
                dao.insertGameState(updated)
            }
        }
    }

    // Helper to update specific player's state (for admin)
    fun adminUpdateState(playerEmail: String, block: (GameStateEntity) -> GameStateEntity) {
        viewModelScope.launch {
            stateWriteMutex.withLock {
                val current = dao.getGameStateDirect(playerEmail) ?: return@withLock
                val updated = block(current)
                dao.insertGameState(updated)
            }
        }
    }

    // --- GAME ACTIONS ---

    // 1. Practice & Skills (Impacted by Energy Vitality)
    fun practiceVoice() {
        updateState { state ->
            if (state.energy < 6) {
                showToast("Not enough energy (6 required)!")
                state
            } else {
                val energyMult = when {
                    state.energy >= 70 -> 1.35f // Peak Focus
                    state.energy >= 40 -> 1.00f
                    else -> 0.65f // Fatigue
                }
                val bonus = (Random.nextFloat() * 1.8f + 0.8f) * energyMult
                val newVoice = (state.voiceSkill + bonus).coerceAtMost(100f)
                val statusMsg = if (energyMult > 1f) " (🔥 Peak Vitality +35% bonus!)" else if (energyMult < 1f) " (😴 Fatigue penalty -35%)" else ""
                showToast("Practiced singing! Voice skill increased by +${String.format("%.1f", bonus)}%$statusMsg")
                state.copy(
                    energy = state.energy - 6,
                    voiceSkill = newVoice,
                    practiceMissedWeeks = 0
                )
            }
        }
    }

    fun practiceWriting() {
        updateState { state ->
            if (state.energy < 6) {
                showToast("Not enough energy (6 required)!")
                state
            } else {
                val energyMult = when {
                    state.energy >= 70 -> 1.35f // Peak Focus
                    state.energy >= 40 -> 1.00f
                    else -> 0.65f // Fatigue
                }
                val bonus = (Random.nextFloat() * 1.8f + 0.8f) * energyMult
                val newWriting = (state.writingSkill + bonus).coerceAtMost(100f)
                val statusMsg = if (energyMult > 1f) " (🔥 Peak Vitality +35% bonus!)" else if (energyMult < 1f) " (😴 Fatigue penalty -35%)" else ""
                showToast("Practiced songwriting! Writing skill increased by +${String.format("%.1f", bonus)}%$statusMsg")
                state.copy(
                    energy = state.energy - 6,
                    writingSkill = newWriting,
                    practiceMissedWeeks = 0
                )
            }
        }
    }

    fun restAndRecharge() {
        updateState { state ->
            if (state.energy >= state.maxEnergy) {
                showToast("You are already at peak vitality (${state.maxEnergy}/${state.maxEnergy})!")
                return@updateState state
            }
            val restored = (25 + (state.maxEnergy * 0.10f).toInt()).coerceAtMost(state.maxEnergy - state.energy)
            val newStress = (state.stress - 15f).coerceAtLeast(0f)
            showToast("🛌 Rested & Recharged: Gained +$restored Vitality and relieved stress (-15%)!")
            state.copy(
                energy = (state.energy + restored).coerceAtMost(state.maxEnergy),
                stress = newStress
            )
        }
    }

    // 2. Social Media Interactions
    fun postOnSocialMedia(promoSongId: String? = null) {
        updateState { state ->
            if (state.energy < 15) {
                showToast("Not enough energy (15 required)!")
                state
            } else {
                val rand = Random.nextFloat()
                var followerGain = 0L
                var influenceGain = 0f
                var publicImageChange = 0f
                var stressChange = 0f
                var msg = ""

                // Segments to boost
                var casualGain = 0L
                var hardcoreGain = 0L
                var onlineGain = 0L
                var collectorsGain = 0L

                // Spam Saturation Penalty: if energy is already low, posting represents low-effort spam
                val isSpamming = state.energy < 40

                if (state.stress > 70f && rand < 0.15f) {
                    // Stressed erratic post
                    influenceGain = Random.nextFloat() * 8.0f + 4.0f
                    publicImageChange = -15f
                    stressChange = 10f
                    followerGain = Random.nextLong(200, 800)
                    onlineGain = (followerGain * 0.8).toLong()
                    casualGain = (followerGain * 0.2).toLong()
                    msg = "🚨 PR CONTROVERSY: Overwhelmed by stress, you posted an unhinged midnight rant! Gained +${String.format("%.1f", influenceGain)}% influence from the viral drama, but Public Image plummeted by -15%!"
                } else {
                    // Procedurally select an authentic post type
                    val postTypeRoll = Random.nextInt(4)
                    val baseMin = 10
                    val baseMax = if (state.songs.any { it.rating >= 4.0f }) 220 else 110
                    val penaltyMult = if (isSpamming) 0.15f else 1.0f
                    val skillMultiplier = (1f + (state.businessSkill + state.writingSkill) / 100f) * penaltyMult

                    val calculatedGains = (Random.nextInt(baseMin, baseMax + 1) * skillMultiplier).toLong()

                    when (postTypeRoll) {
                        0 -> { // The Behind-the-Scenes Studio Update (Authentic & Professional)
                            followerGain = calculatedGains * 2
                            hardcoreGain = (followerGain * 0.6).toLong()
                            onlineGain = (followerGain * 0.4).toLong()
                            influenceGain = Random.nextFloat() * 3.5f + 1.5f
                            publicImageChange = 2f
                            stressChange = 2f
                            msg = "🎨 Behind-the-Scenes: Shared a photo of your acoustic tracking session. Your core fans loved the authentic peek into your craft! Gained +${followerGain} fans (+${hardcoreGain} hardcore, +${onlineGain} online community)."
                        }
                        1 -> { // The Self-Deprecating Shitpost / Meme (Viral Potential)
                            val viralRoll = Random.nextFloat()
                            if (viralRoll > 0.88f && !isSpamming) { // Viral Hit!
                                followerGain = (state.fans * 0.08).toLong().coerceIn(1000, 45000)
                                casualGain = (followerGain * 0.7).toLong()
                                onlineGain = (followerGain * 0.3).toLong()
                                influenceGain = Random.nextFloat() * 10f + 5f
                                publicImageChange = 5f
                                stressChange = -5f
                                msg = "🔥 MEME GOES VIRAL: Your ridiculous self-deprecating joke was shared by major community pages! Gained +${String.format("%,d", followerGain)} fans and exploded your online reach!"
                            } else {
                                followerGain = calculatedGains
                                onlineGain = (followerGain * 0.9).toLong()
                                influenceGain = Random.nextFloat() * 2f + 0.5f
                                stressChange = -3f
                                msg = "😂 Meme Shared: Posted a lighthearted industry meme. It earned decent laughs from the online community."
                            }
                        }
                        2 -> { // Commercial Song Promo (Reach vs Fatigue)
                            followerGain = (calculatedGains * 1.5).toLong()
                            casualGain = (followerGain * 0.6).toLong()
                            collectorsGain = (followerGain * 0.4).toLong()
                            influenceGain = Random.nextFloat() * 1.5f + 0.5f
                            publicImageChange = if (isSpamming) -3f else 1f
                            stressChange = 4f

                            // Boost newest song streams directly
                            val latestSong = state.songs.maxByOrNull { it.yearCreated * 100 + it.weekCreated }
                            if (latestSong != null && latestSong.isUploadedMap.isNotEmpty()) {
                                val updatedMap = latestSong.isUploadedMap.mapValues { (_, streams) ->
                                    val boostVal = if (isSpamming) Random.nextLong(20, 80) else Random.nextLong(150, 600)
                                    streams + boostVal
                                }
                                state.songs.map { s -> if (s.id == latestSong.id) s.copy(isUploadedMap = updatedMap, totalStreams = updatedMap.values.sum()) else s }
                            }

                            msg = if (isSpamming) {
                                "📢 Promo Spam: Shilled your latest release. Followers are getting annoyed by the low-effort self-promotion (Public Image dropped)."
                            } else {
                                "🎵 Song Spotlight: Posted an elegant promo clip of your latest track. Driven new clicks & interest! Gained +${followerGain} fans."
                            }
                        }
                        else -> { // Vulnerable Diary Entry (Super-Authentic)
                            followerGain = calculatedGains / 2
                            hardcoreGain = (followerGain * 0.8).toLong()
                            onlineGain = (followerGain * 0.2).toLong()
                            influenceGain = Random.nextFloat() * 4.0f + 2.0f
                            publicImageChange = 4f
                            stressChange = -12f // Clears considerable stress
                            msg = "🌸 Vulnerable Post: Opened up about your creative struggles and mental health blockages. Core followers deeply appreciated your honesty! Gained +${followerGain} fans, Stress decreased by -12."
                        }
                    }
                }

                val newFans = (state.fans + followerGain).coerceAtLeast(0)
                val newInfluence = (state.influence + influenceGain).coerceIn(0f, 100f)
                val newPublicImage = (state.publicImage + publicImageChange).coerceIn(0f, 100f)
                val newStress = (state.stress + stressChange).coerceIn(0f, 100f)

                // Promoting a specific song if passed as promoSongId
                val updatedSongs = if (promoSongId != null) {
                    state.songs.map { song ->
                        if (song.id == promoSongId) {
                            val boost = if (isSpamming) Random.nextInt(10, 50) else Random.nextInt(80, 250)
                            song.copy(isUploadedMap = song.isUploadedMap.mapValues { it.value + boost })
                        } else song
                    }
                } else state.songs

                showToast(msg)
                state.copy(
                energy = state.energy - (if (state.activePhone == "Xiaomax") 8 else 15),
                fans = newFans,
                influence = newInfluence,
                publicImage = newPublicImage,
                stress = newStress,
                songs = updatedSongs,
                practiceMissedWeeks = 0,
                fanSegments = state.fanSegments.copy(
                    casualFans = (state.casualFans + casualGain).coerceAtLeast(0),
                    hardcoreFans = (state.hardcoreFans + hardcoreGain).coerceAtLeast(0),
                    onlineCommunity = (state.onlineCommunity + onlineGain).coerceAtLeast(0),
                    collectors = (state.collectors + collectorsGain).coerceAtLeast(0)
                )
            )
        }
    }
}

    // 3. Side Jobs Manager
    fun toggleSideJob(jobId: String) {
        updateState { state ->
            val jobs = state.sideJobs.map { job ->
                if (job.id == jobId) {
                    if (job.isActive) {
                        job.copy(isActive = false)
                    } else {
                        // Check remaining time slots including active ventures (10 slots each)
                        val sideJobSlots = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
                        val tourSlots = state.tours.filter { it.status == "ACTIVE" }.sumOf { it.weeklyTimeCost }
                        val ventureSlots = state.businessVentures.count { it.isOwned && it.isPlayerInControl } * 10
                        val currentUsed = sideJobSlots + tourSlots + ventureSlots
                        if (currentUsed + job.timeCost > state.maxTimeSlots) {
                            showToast("Not enough time slots available! Used: $currentUsed/${state.maxTimeSlots} (Ventures take 10 slots each). Quit jobs or expand time slots.")
                            job
                        } else {
                            job.copy(isActive = true)
                        }
                    }
                } else job
            }
            state.copy(sideJobs = jobs)
        }
    }

    private fun trendSubgenreForGenre(genreId: String): String = when (genreId) {
        "genre_pop" -> "Lo-Fi Dreamwave"
        "genre_hiphop" -> "Jazz-Drill Fusion"
        "genre_rock" -> "Post-Grunge Rebel Wave"
        "genre_rnb" -> "Neo-Soul Satin Beats"
        "genre_electronic" -> "Synthwave Retro-Glow"
        "genre_indie" -> "Acoustic Forest Indie"
        "genre_jazz" -> "Neo-Bossa Lounge Chill"
        "genre_country" -> "Modern Roots Revival"
        "genre_afro" -> "Afro-Fusion Groove"
        else -> "Fresh Sounds"
    }

    // Reports and release planning read the same active market trend as the simulator.
    @Suppress("UNUSED_PARAMETER")
    fun getWeeklyTrendSubgenre(week: Int): Pair<String, String> {
        val currentTrends = gameState.value?.genreTrends.orEmpty()
        val activeGenreId = currentTrends.maxByOrNull { it.value }?.key ?: "genre_pop"
        return activeGenreId to trendSubgenreForGenre(activeGenreId)
    }

    fun updateAccountantInvestmentSettings(percent: Float, sellMargin: Float) {
        updateState { state ->
            showToast("Updated Accountant Auto-Investment parameters: ${(percent * 100).toInt()}% cap, ${(sellMargin * 100).toInt()}% margin target!")
            state.copy(
                accountantAutoInvestPercent = percent,
                accountantAutoSellProfitMargin = sellMargin
            )
        }
    }

    fun setSmmPostCategory(category: String) {
        updateState { state ->
            showToast("Assigned SMM content category to '$category'!")
            state.copy(wealth = state.wealth.copy(smmPostCategory = category))
        }
    }

    fun setSmmAutoPost(enabled: Boolean) {
        updateState { state ->
            showToast(if (enabled) "SMM auto-posting enabled!" else "SMM auto-posting paused.")
            state.copy(wealth = state.wealth.copy(smmAutoPost = enabled))
        }
    }

    fun updateCryptoInvestmentSettings(percent: Float, sellMargin: Float) {
        updateState { state ->
            showToast("Updated Crypto Analyst Auto-Investment parameters: ${(percent * 100).toInt()}% cap, ${(sellMargin * 100).toInt()}% margin target!")
            state.copy(
                cryptoAutoInvestPercent = percent,
                cryptoAutoSellProfitMargin = sellMargin
            )
        }
    }

    // 4. Studio - Creating Songs & Music Videos
    fun createSong(
        title: String,
        genreId: String,
        producerId: String?,
        studioName: String,
        sessionCost: Double,
        lyricsFocus: Float = 0.5f,
        mainstreamAppeal: Float = 0.5f,
        vocalFocus: Float = 0.5f,
        subgenreName: String = "",
        weeksUntilRelease: Int = 0,
        soundEngineerQuality: String = "Amateur",
        mixingStyle: String = "Standard",
        backingVocalists: String = "None",
        studioDecor: String = "Basement",
        producerTier: String = "None",
        coverArt: String? = null,
        registerCopyright: Boolean = false,
        clearSamples: Boolean = false,
        featuredArtistId: String? = null
    ) {
        updateState { state ->
            val cleanTitle = title.trim()
            if (cleanTitle.isBlank()) {
                showToast("Please enter a valid song title!")
                return@updateState state
            }
            if (state.songs.any { it.title.equals(cleanTitle, ignoreCase = true) } ||
                state.albums.any { it.title.equals(cleanTitle, ignoreCase = true) }) {
                showToast("A track or album titled '$cleanTitle' already exists! Titles must be unique.")
                return@updateState state
            }
            if (state.energy < 10) {
                showToast("Not enough energy (10 required) to record in the studio!")
                return@updateState state
            }

            // Featured artist validation and worth calculation ($5,000 - $350,000)
            val featuredNpc = if (!featuredArtistId.isNullOrBlank()) state.npcs.find { it.id == featuredArtistId } else null
            val featureCost: Double = if (featuredNpc != null) {
                val popFactor = (featuredNpc.popularity / 100.0).coerceIn(0.0, 1.0)
                val calc = 5000.0 + (popFactor * popFactor) * 345000.0
                (Math.round(calc / 500.0) * 500.0).coerceIn(5000.0, 350000.0)
            } else 0.0

            if (featuredNpc != null) {
                if (featuredNpc.relationshipWithPlayer < 30f) {
                    showToast("${featuredNpc.name} declined the feature! Relationship is only ${featuredNpc.relationshipWithPlayer.toInt()}%, but at least 30% is required.")
                    return@updateState state
                }
            }

            // Production Level cost discount from company management simulation
            val labelDiscount = if (state.ownLabel != null) state.ownLabel.productionLevel * 0.04f else 0f
            val sessionCostWithDiscount = sessionCost * (1f - labelDiscount).coerceAtLeast(0.6f)

            val decorCost = when (studioDecor) {
                "Indie Loft" -> 1500.0
                "Retro Analog Oasis" -> 5000.0
                "High-Tech Audio Fortress" -> 12500.0
                else -> 0.0
            }
            val producerTierCost = when (producerTier) {
                "Indie Beatmaker" -> 2500.0
                "Hitmaker Producer" -> 10000.0
                "Legendary Sonic Guru" -> 30000.0
                else -> 0.0
            }
            val engineerCost = when (soundEngineerQuality) {
                "Professional" -> 1800.0
                "Grammy-winning" -> 7500.0
                else -> 0.0
            }
            val mixingCost = when (mixingStyle) {
                "Warm Analog Tape" -> 1200.0
                "Spatial Dolby Atmos Master" -> 3500.0
                else -> 0.0
            }
            val backingCost = when (backingVocalists) {
                "Backing Vocalists" -> 800.0
                "Live Brass & String Section" -> 3000.0
                else -> 0.0
            }

            val clearanceCost = if (clearSamples) 1500.0 else 0.0

            if (state.cash < sessionCostWithDiscount) {
                showToast("Insufficient cash for this studio session (costs $${String.format("%,.0f", sessionCostWithDiscount)} with discount)!")
                return@updateState state
            }
            val producer = GameData.PRODUCERS.find { it.id == producerId }
            var prodCost = producer?.cost ?: 0.0

            // Respect Discount: High industry respect makes producers eager to work with you!
            if (state.industryRespect > 70f && prodCost > 0.0) {
                prodCost *= 0.80 // 20% discount
            }

            val studioRecordingSubtotal = sessionCostWithDiscount + prodCost + decorCost + producerTierCost + engineerCost + mixingCost + backingCost + clearanceCost + featureCost
            val copyrightCost = if (registerCopyright) (studioRecordingSubtotal * 0.15).coerceAtLeast(150.0) else 0.0
            val totalCost = studioRecordingSubtotal + copyrightCost

            if (state.cash < totalCost) {
                val feeMsg = if (featuredNpc != null) " (includes $${String.format("%,.0f", featureCost)} feature fee for ${featuredNpc.name})" else ""
                showToast("Insufficient cash! Total required: $${String.format("%,.0f", totalCost)}$feeMsg")
                return@updateState state
            }

            // Dynamic check for album delivery constraints
            val reqSingles = state.signedLabelReqSingles
            val delSingles = state.signedLabelDeliveredSingles
            if (state.signedLabelId != null && delSingles >= reqSingles) {
                // If they delivered singles but still owe an album, recording another single doesn't count toward deliverables, but is allowed.
            }

            val genre = GameData.GENRES.find { it.id == genreId } ?: GameData.GENRES[0]

            // Calculate song rating with mental health and experience modifiers
            val instrumentBonus = state.ownedInstruments.sumOf { it.ratingBonus.toDouble() }.toFloat()
            val prodBonus = producer?.ratingBonus ?: 0f
            val skillBase = (state.voiceSkill + state.writingSkill) / 2f

            // Studio-specific boosts
            var studioRatingBonus = 0f
            var licType = "Full"
            var promoWks = 0
            var promoBst = 0f
            var playlists = 0
            var globalStream = false

            when (studioName) {
                "Jipsee Sound" -> {
                    val clampedCost = sessionCost.coerceIn(1000.0, 5000.0)
                    studioRatingBonus = ((clampedCost / 5000.0) * 0.8f).toFloat()
                    licType = "Full"
                    promoWks = 0
                    promoBst = 0f
                    playlists = 0
                    globalStream = false
                }
                "Audination" -> {
                    val clampedCost = sessionCost.coerceIn(15000.0, 25000.0)
                    studioRatingBonus = ((clampedCost / 25000.0) * 1.5f + 0.3f).toFloat()
                    licType = "Half"
                    promoWks = 4
                    promoBst = 0.03f
                    playlists = 1
                    globalStream = false
                }
                "BigSound Man" -> {
                    val clampedCost = sessionCost.coerceIn(70000.0, 100000.0)
                    studioRatingBonus = ((clampedCost / 100000.0) * 2.2f + 0.8f).toFloat()
                    licType = "Full"
                    promoWks = 12
                    promoBst = 0.07f
                    playlists = 5
                    globalStream = true
                }
                else -> {
                    val budgetEffect = (sessionCost / 5000.0).coerceAtMost(1.8).toFloat()
                    studioRatingBonus = budgetEffect
                }
            }

            val decorRatingBonus = when (studioDecor) {
                "Indie Loft" -> 0.3f
                "Retro Analog Oasis" -> 0.7f
                "High-Tech Audio Fortress" -> 1.2f
                else -> 0f
            }
            val producerTierBonus = when (producerTier) {
                "Indie Beatmaker" -> 0.6f
                "Hitmaker Producer" -> 1.5f
                "Legendary Sonic Guru" -> 2.8f
                else -> 0f
            }
            val engineerRatingBonus = when (soundEngineerQuality) {
                "Professional" -> 0.4f
                "Grammy-winning" -> 0.9f
                else -> 0f
            }
            val mixingRatingBonus = when (mixingStyle) {
                "Warm Analog Tape" -> 0.2f
                "Spatial Dolby Atmos Master" -> 0.5f
                else -> 0f
            }
            val backingRatingBonus = when (backingVocalists) {
                "Backing Vocalists" -> 0.15f
                "Live Brass & String Section" -> 0.45f
                else -> 0f
            }

            // Experience and Mental multipliers
            val expBonus = (state.songs.size * 0.05f).coerceAtMost(0.6f)
            val mentalMultiplier = (state.creativeInspiration / 100f) * (1f - (state.burnout / 100f) * 0.5f)

            // Hired Music Producer workforce boost
            val hiredProducer = state.hiredEmployees.find { it.role == "Music Producer" }
            val hiredProducerBonus = if (hiredProducer != null && hiredProducer.loyalty > 25f) (0.25f + (hiredProducer.experience / 300f)) else 0f

            // Company Production Department level rating boost
            val labelProductionBonus = if (state.ownLabel != null) state.ownLabel.productionLevel * 0.05f else 0f

            // Advanced Interactive Sliders logic
            val writingBonus = if (lyricsFocus > 0.5f) {
                ((state.writingSkill - 40f) / 60f) * (lyricsFocus - 0.5f) * 0.4f
            } else {
                ((instrumentBonus - 1.5f) / 10f).coerceAtLeast(-0.2f) * (0.5f - lyricsFocus) * 0.3f
            }

            val voiceBonus = if (vocalFocus > 0.5f) {
                ((state.voiceSkill - 40f) / 60f) * (vocalFocus - 0.5f) * 0.4f
            } else {
                (prodBonus / 5f) * (0.5f - vocalFocus) * 0.3f
            }

            val experimentalBonus = (mainstreamAppeal - 0.5f) * -0.15f

            // ----------------------------------------------------
            // SONG QUALITY & CRAFTSMANSHIP ENGINE (0 - 100)
            // ----------------------------------------------------
            // 1. Raw musical foundation (voice skill, songwriting skill, inspiration, vitality): up to 32 pts
            val energyCraftBonus = when {
                state.energy >= 75 -> 3.5f // Peak vitality flow
                state.energy >= 40 -> 0.0f
                else -> -4.0f // Vocal & cognitive fatigue
            }
            val rawCraftPoints = ((state.voiceSkill * 0.45f + state.writingSkill * 0.45f + state.creativeInspiration * 0.10f) * 0.32f + energyCraftBonus).coerceAtLeast(4.0f)

            // 2. Alphadom Instruments & Studio Outboard Gear: up to 16 pts
            val alphadomGearPoints = (instrumentBonus * 3.6f + prodBonus * 0.8f).coerceAtMost(16.0f)

            // 3. Production Workforce (Producer tier, Sound Engineer, Mixing style, Arrangement support): up to 30 pts
            val pTierPoints = when (producerTier) {
                "Indie Beatmaker" -> 4f
                "Hitmaker Producer" -> 8f
                "Legendary Sonic Guru" -> 14f
                else -> 0f
            }
            val sEngineerPoints = when (soundEngineerQuality) {
                "Professional" -> 4f
                "Grammy-winning" -> 8f
                else -> 0f
            }
            val mixPoints = when (mixingStyle) {
                "Warm Analog Tape" -> 3f
                "Spatial Dolby Atmos Master" -> 5f
                else -> 1f
            }
            val arrangementPoints = when (backingVocalists) {
                "Backing Vocalists" -> 2f
                "Live Brass & String Section" -> 4f
                else -> 0f
            }

            // 4. Additional Producers / Label Production Dept & Studio Acoustics: up to 14 pts
            val studioFacilityPoints = when (studioName) {
                "Jipsee Sound" -> 1.5f
                "Audination" -> 3.5f
                "BigSound Man" -> 5.5f
                else -> 1f
            }
            val decorPoints = when (studioDecor) {
                "Indie Loft" -> 1.5f
                "Retro Analog Oasis" -> 3.0f
                "High-Tech Audio Fortress" -> 4.5f
                else -> 0f
            }
            val additionalProducerPoints = if (hiredProducer != null && hiredProducer.loyalty > 25f) {
                (2.0f + (hiredProducer.experience / 40f)).coerceAtMost(4.5f)
            } else 0f
            val labelProdPoints = labelProductionBonus * 20f

            // 5. Campaign Strategy & Rollout Horizon
            val rolloutStrategyPoints = if (weeksUntilRelease >= 4) 4.0f else if (weeksUntilRelease > 0) 2.0f else 0.0f

            // 6. Experience & Sliders Polish
            val expPoints = (state.songs.size * 0.25f).coerceAtMost(4.0f)
            val sliderPolish = (writingBonus + voiceBonus + experimentalBonus) * 4.0f

            // Mental health modifier
            val burnoutDrag = (state.burnout / 100f) * 10f

            // Guest artist chemistry
            val guestSynergyPts = if (featuredNpc != null) {
                val genreMatch = featuredNpc.genrePreference.equals(genre.name, ignoreCase = true)
                val chemistry = if (genreMatch) 2.0f else -1.5f
                val talent = ((featuredNpc.bestSongRating - 3.2f) * 3.0f).coerceIn(-3f, 4f)
                chemistry + talent
            } else 0f

            val rawQualityScore = (rawCraftPoints + alphadomGearPoints + pTierPoints + sEngineerPoints + mixPoints + arrangementPoints + studioFacilityPoints + decorPoints + additionalProducerPoints + labelProdPoints + rolloutStrategyPoints + expPoints + sliderPolish + guestSynergyPts - burnoutDrag)

            // Diminishing returns curve so that reaching 100/100 quality is exceptionally tough
            val computedQuality = if (rawQualityScore <= 70f) {
                rawQualityScore.coerceIn(15f, 70f)
            } else {
                // Asymptotically scales from 70 to 100 (requires top-tier Alphadom gear + Legendary Guru + Grammy Engineer + Dolby + Top skill to approach 98-100)
                70f + (30f * (1.0f - kotlin.math.exp(-((rawQualityScore - 70f) / 22f)))).toFloat()
            }.coerceIn(10f, 100f)

            // Star Rating (1.0 to 5.0) is directly judged from song quality
            val finalRating = (computedQuality / 20.0f).coerceIn(1.0f, 5.0f)

            val displaySubgenre = subgenreName.ifEmpty { "Original Mix" }
            var baseFormattedTitle = if (title.trim().contains("(")) title.trim() else "${title.trim().ifEmpty { "Unreleased Hit" }} ($displaySubgenre)"
            val formattedTitle = if (featuredNpc != null && !baseFormattedTitle.contains("feat.", ignoreCase = true) && !baseFormattedTitle.contains("ft.", ignoreCase = true)) {
                "$baseFormattedTitle (feat. ${featuredNpc.name})"
            } else {
                baseFormattedTitle
            }

            // Hype measures audience awareness, not studio spend. A feature contributes
            // modest awareness; production quality is reflected in rating and retention.
            var startingHype = if (weeksUntilRelease > 0) 8f else 3f
            if (featuredNpc != null) startingHype += (featuredNpc.popularity * 0.08f).coerceIn(0f, 8f)
            startingHype = startingHype.coerceIn(1f, 20f)

            val assignedCover = if (coverArt.isNullOrBlank()) {
                "🎨 Auto-Art: Stylized modern ${genre.name} abstract design themed around '${formattedTitle}'"
            } else {
                coverArt
            }

            // Generate realistic listener reactions / critic / A&R feedback
            val feedbackList = mutableListOf<String>()
            val genreName = genre.name

            // 1. Mixing Quality
            if (soundEngineerQuality == "Amateur" && mixingStyle == "Standard") {
                feedbackList.add("⚠ Mixing Critique: The low end sounds muddy, and the vocals sound buried in the mix. Needs a professional engineer.")
                feedbackList.add("💬 Fan Forum: 'A decent melody, but the sound quality holds it back. Sounds like it was recorded on a phone.'")
            } else if (mixingStyle == "Warm Analog Tape") {
                feedbackList.add("📈 Audiophile Report: The warm analog tape master adds excellent harmonic saturation and vintage weight.")
                feedbackList.add("💬 Industry Review: 'The stereo field is beautifully saturated. The analogue tape compression handles the peaks wonderfully.'")
            } else if (mixingStyle == "Spatial Dolby Atmos Master") {
                feedbackList.add("🔥 Spatial Audio Spotlight: Immersive 3D audio. The vocal placement floats cleanly above the synth layers.")
                feedbackList.add("💬 Tweet: 'Listening to this track in Dolby Atmos on quality headphones is an absolute dream!'")
            } else {
                feedbackList.add("✅ Technical Check: A solid, commercial mix with standard high-frequency clarity.")
            }

            // 2. Performance Feedback
            if (lyricsFocus > 0.65f) {
                if (state.writingSkill >= 70f) {
                    feedbackList.add("🔥 Lyricism: Genuinely profound and poetic storytelling. It's rare to hear writing this vulnerable.")
                } else if (state.writingSkill < 40f) {
                    feedbackList.add("⚠ Lyrist Backlash: The song emphasizes deep lyrics, but they end up feeling juvenile and poorly structured.")
                } else {
                    feedbackList.add("✅ Writing: Solid, relatable lyricism that resonates well with casual listeners.")
                }
            } else {
                if (state.voiceSkill >= 75f && vocalFocus > 0.6f) {
                    feedbackList.add("🔥 Vocal Mastery: Absolute showstopper! The high notes and technical pitch control are flawless.")
                } else {
                    feedbackList.add("💬 Fan Note: 'Love the upbeat vibe, it's really catchy and easy to loop.'")
                }
            }

            // 3. Mainstream vs Experimental
            if (mainstreamAppeal > 0.75f) {
                if (finalRating >= 3.8f) {
                    feedbackList.add("🚀 Radio DJ: 'An absolute earworm! Pure mainstream pop magic built for the charts.'")
                } else {
                    feedbackList.add("⚠ Critic: This track is cookie-cutter radio bait. Lacks unique artistic identity, aiming for generic algorithms.")
                }
            } else if (mainstreamAppeal < 0.35f) {
                feedbackList.add("🎨 Underground Blog: Defies commercial structures. It is experimental, edgy, and refreshing.")
            }

            // 4. Sample status
            val sampleClearedState = if (clearSamples || registerCopyright) {
                true
            } else {
                // If they did not clear samples or register copyright, 30% chance of a potential lawsuit/clearance issue
                kotlin.random.Random.nextFloat() > 0.30f
            }

            if (!sampleClearedState) {
                feedbackList.add("🚨 Industry Rumor: Music bloggers note an uncleared classic synth sample loop. This could face copyright claims.")
            }

            // 5. A&R Trend Alignment
            val weeklyTrend = getWeeklyTrendSubgenre(state.currentWeek)
            val trendGenreId = weeklyTrend.first
            val trendGenreName = GameData.GENRES.find { it.id == trendGenreId }?.name ?: "Pop"
            if (genreId == trendGenreId) {
                feedbackList.add("📈 A&R Trend Alignment: Perfect timing! Releasing a $genreName track now capitalizes on the current market craze for $trendGenreName.")
            } else {
                feedbackList.add("💬 A&R Market Note: $genreName is currently overshadowed by the major trend in $trendGenreName. It will require extra promotion.")
            }

            // 6. Marketing Blitz vs Silent Drop
            if (weeksUntilRelease > 0) {
                feedbackList.add("📣 Release plan set: starting awareness is $startingHype%. Promotion and audience response will shape anticipation over the countdown.")
            } else {
                feedbackList.add("⚠ Marketing Critique: Silent drop! Moving straight to release with 0 weeks of build-up has lowered reach. Pre-release scheduling is safer.")
            }

            // 7. Copyright Registration
            if (registerCopyright) {
                feedbackList.add("🔒 Intellectual Property: Federal copyright registered. Master and publishing rights are legally protected.")
            } else {
                feedbackList.add("⚠ Intellectual Property Warning: Unregistered copyright! Shady competitors might release unauthorized covers or claim royalties.")
            }

            val newSong = Song(
                id = UUID.randomUUID().toString(),
                title = formattedTitle,
                genreId = genreId,
                budget = totalCost,
                producerId = producerId,
                rating = finalRating,
                songQuality = computedQuality,
                weekCreated = state.currentWeek,
                yearCreated = state.currentYear,
                licenseType = licType,
                promoWeeksLeft = promoWks,
                promoWeeklyBoost = promoBst,
                playlistsCount = playlists,
                isGlobal = globalStream,
                isReleased = weeksUntilRelease <= 0,
                hype = startingHype.coerceIn(1f, 100f),
                weeksUntilRelease = weeksUntilRelease,
                initialHypeWeeksSet = weeksUntilRelease,
                soundEngineerQuality = soundEngineerQuality,
                mixingStyle = mixingStyle,
                backingVocalists = backingVocalists,
                studioDecor = studioDecor,
                producerTier = producerTier,
                coverArt = assignedCover,
                isCopyrightRegistered = registerCopyright,
                hasSampleClearance = sampleClearedState,
                audienceFeedback = feedbackList,
                featuredArtistId = featuredNpc?.id,
                featuredArtistName = featuredNpc?.name,
                isPlayerFeatured = false,
                primaryArtistName = state.artistName
            )

            // Timeline Event & Reputation Boost
            val isHit = finalRating >= 4.2f
            val milestoneMsg = "Year ${state.currentYear}, Week ${state.currentWeek}: Recorded the song '${newSong.title}' at $studioName (Cost: $${String.format("%,.0f", totalCost)}) - rated ${String.format("%.1f", finalRating)}⭐!"
            val updatedTimeline = state.timelineEvents + milestoneMsg + listOfNotNull(
                if (isHit) "Year ${state.currentYear}, Week ${state.currentWeek}: 🔥 hit single alert! '${newSong.title}' recorded at $studioName was highly acclaimed!" else null
            )

            val newInspiration = (state.creativeInspiration - 20f).coerceAtLeast(10f)
            val newStress = (state.stress + 8f).coerceAtMost(100f)

            val updatedNpcs = if (featuredNpc != null) {
                state.npcs.map { n ->
                    if (n.id == featuredNpc.id) n.copy(relationshipWithPlayer = (n.relationshipWithPlayer + 8f).coerceAtMost(100f))
                    else n
                }
            } else state.npcs

            val currentWeeklyTrend = getWeeklyTrendSubgenre(state.currentWeek)
            if (genreId == currentWeeklyTrend.first) {
                showToast("🔥 ${genre.name} is trending for ${state.genreTrendWeeksRemaining.coerceAtLeast(1)} more week(s). Songs get a temporary discovery boost while listener interest lasts.")
            } else {
                showToast("Recorded song '${newSong.title}' with rating ${String.format("%.1f", finalRating)}⭐ at $studioName!")
            }
            state.copy(
                energy = state.energy - 10,
                cash = state.cash - totalCost,
                songs = state.songs + newSong,
                npcs = updatedNpcs,
                signedLabelDeliveredSingles = if (state.signedLabelId != null) state.signedLabelDeliveredSingles + 1 else state.signedLabelDeliveredSingles,
                popularity = (state.popularity + finalRating * 1.5f).coerceAtMost(100f),
                industryRespect = (state.industryRespect + (if (isHit) 6f else 1.5f)).coerceAtMost(100f),
                criticalAcclaim = (state.criticalAcclaim + (if (isHit) 8f else 1.5f)).coerceAtMost(100f),
                creativeInspiration = newInspiration,
                stress = newStress,
                timelineEvents = updatedTimeline
            )
        }
    }

    // SIGN DISTRIBUTION DEAL (Symphony / EliteVibe / EmpireDistro)
    fun signDistributionDeal(dealType: String) {
        updateState { state ->
            var advanceAmount = 0.0
            val currentWeek = state.currentWeek

            when (dealType) {
                "DIY" -> {
                    advanceAmount = 0.0
                }
                "EliteVibe" -> {
                    advanceAmount = 5000.0
                }
                "EmpireDistro" -> {
                    if (state.popularity < 40f && state.industryRespect < 45f) {
                        showToast("EmpireDistro requires at least 40% Popularity or 45% Industry Respect to sign!")
                        return@updateState state
                    }
                    advanceAmount = 25000.0
                }
                else -> {
                    showToast("Invalid distribution deal option!")
                    return@updateState state
                }
            }

            val logMsg = "Signed Distribution Deal: $dealType with upfront recoupable advance of $${String.format("%,.0f", advanceAmount)}!"
            val updatedTimeline = state.timelineEvents + "Year ${state.currentYear}, Week $currentWeek: $logMsg"

            showToast("Successfully signed with $dealType distribution! Advance: $${String.format("%,.0f", advanceAmount)}")

            state.copy(
                activeDistributionDeal = dealType,
                recoupableAdvanceBalance = state.recoupableAdvanceBalance + advanceAmount,
                cash = state.cash + advanceAmount,
                signedDistributionWeek = currentWeek,
                timelineEvents = updatedTimeline
            )
        }
    }

    // SIGN PUBLISHING DEAL (None / DIY / CoPublishing / AdminOnly)
    fun signPublishingDeal(dealType: String) {
        updateState { state ->
            var advanceAmount = 0.0
            var adminSetupFee = 0.0
            val currentWeek = state.currentWeek

            when (dealType) {
                "None", "DIY" -> {
                    advanceAmount = 0.0
                }
                "CoPublishing" -> {
                    advanceAmount = 15000.0
                }
                "AdminOnly" -> {
                    adminSetupFee = 8000.0
                    if (state.cash < adminSetupFee) {
                        showToast("Administrative Setup Deal requires a one-time retainer fee of $8,000!")
                        return@updateState state
                    }
                    advanceAmount = 0.0
                }
                else -> {
                    showToast("Invalid publishing deal option!")
                    return@updateState state
                }
            }

            val logMsg = if (adminSetupFee > 0.0) {
                "Signed Publishing Administration with Symphony Admin. Paid setup retainer fee of $8,000."
            } else {
                "Signed Publishing Co-Publishing Deal with Luminate Pubs. Upfront recoupable advance: $${String.format("%,.0f", advanceAmount)}."
            }
            val updatedTimeline = state.timelineEvents + "Year ${state.currentYear}, Week $currentWeek: $logMsg"

            showToast("Successfully signed publishing deal with ${if (dealType == "CoPublishing") "Luminate Pubs" else "Symphony Admin"}")

            state.copy(
                activePublishingDeal = dealType,
                recoupableAdvanceBalance = state.recoupableAdvanceBalance + advanceAmount,
                cash = state.cash + advanceAmount - adminSetupFee,
                signedPublishingWeek = currentWeek,
                timelineEvents = updatedTimeline
            )
        }
    }

    // REGISTER RETROACTIVE COPYRIGHT
    // Clean read & processed emails using Personal Assistant
    fun cleanReadEmails() {
        updateState { state ->
            val pa = state.hiredEmployees.find { it.role == "Personal Assistant" }
            val readOrAccepted = state.mailbox.filter { it.isAccepted || it.subject.startsWith("✅") || it.subject.startsWith("📊") || it.subject.startsWith("👔") }
            if (readOrAccepted.isEmpty()) {
                showToast("No read or processed emails to clean!")
                return@updateState state
            }
            val maxClean = if (pa != null) (pa.skillRatingPercent / 5).coerceIn(5, 50) else 12
            val toRemoveIds = readOrAccepted.take(maxClean).map { it.id }.toSet()
            val remaining = state.mailbox.filter { it.id !in toRemoveIds }
            val paName = pa?.name ?: "System"
            showToast("🧹  purged ${toRemoveIds.size} read/processed emails from your inbox!")
            state.copy(mailbox = remaining)
        }
    }

    fun registerSongCopyright(songId: String) {
        updateState { state ->
            if (state.cash < 250.0) {
                showToast("Insufficient cash ($250 required) to register copyright protection!")
                return@updateState state
            }
            val song = state.songs.find { it.id == songId }
            if (song == null) {
                showToast("Song not found!")
                return@updateState state
            }
            if (song.isCopyrightRegistered) {
                showToast("Copyright is already registered for '${song.title}'!")
                return@updateState state
            }

            val updatedSongs = state.songs.map { s ->
                if (s.id == songId) {
                    val feedback = s.audienceFeedback.filter { !it.contains("Copyright") && !it.contains("unregistered") } +
                        "✅ Copyright Protection: Registered and protected by the National Copyright Administration."
                    s.copy(isCopyrightRegistered = true, hasSampleClearance = true, audienceFeedback = feedback)
                } else s
            }

            showToast("Successfully registered federal copyright for '${song.title}'!")
            state.copy(
                cash = state.cash - 250.0,
                songs = updatedSongs,
                timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Registered copyright for '${song.title}'."
            )
        }
    }

    // RETROACTIVE SAMPLE CLEARANCE
    fun clearSongSamples(songId: String) {
        updateState { state ->
            if (state.cash < 1500.0) {
                showToast("Insufficient cash ($1,500 required) to clear sample rights!")
                return@updateState state
            }
            val song = state.songs.find { it.id == songId }
            if (song == null) {
                showToast("Song not found!")
                return@updateState state
            }
            if (song.hasSampleClearance) {
                showToast("Samples are already cleared for '${song.title}'!")
                return@updateState state
            }

            val updatedSongs = state.songs.map { s ->
                if (s.id == songId) {
                    val feedback = s.audienceFeedback.filter { !it.contains("sampling") && !it.contains("uncleared") && !it.contains("🚨") } +
                        "✅ Sampling Status: All samples and loops fully cleared for digital distribution."
                    s.copy(hasSampleClearance = true, audienceFeedback = feedback)
                } else s
            }

            showToast("Successfully paid sample license clearing fees for '${song.title}'!")
            state.copy(
                cash = state.cash - 1500.0,
                songs = updatedSongs,
                timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Cleared sampling licenses for '${song.title}'."
            )
        }
    }

    fun createMusicVideo(songId: String, tier: String, customTitle: String? = null, customThumbnail: String? = null) {
        val cost = when (tier) {
            "Low" -> 2000.0
            "Standard" -> 5000.0
            "High" -> 10000.0
            "Premium" -> 20000.0
            "Cinematic" -> 50000.0
            else -> 2000.0
        }

        val popularityBonus = when (tier) {
            "Low" -> 5f
            "Standard" -> 15f
            "High" -> 30f
            "Premium" -> 50f
            "Cinematic" -> 100f
            else -> 5f
        }

        val videoEnergyCost = when (tier) {
            "Low" -> 6
            "Standard" -> 7
            "High" -> 8
            "Premium" -> 9
            "Cinematic" -> 10
            else -> 6
        }

        updateState { state ->
            if (state.energy < videoEnergyCost) {
                showToast("Not enough energy to shoot a $tier tier music video (needs $videoEnergyCost energy)!")
                return@updateState state
            }
            if (state.cash < cost) {
                showToast("Insufficient cash for this music video tier!")
                return@updateState state
            }

            val song = state.songs.find { it.id == songId }
            if (song == null) {
                showToast("Song not found!")
                return@updateState state
            }

            val finalVidTitle = customTitle?.trim()?.ifBlank { null } ?: "${song.title} (Official Music Video)"
            val finalThumbnail = customThumbnail?.ifBlank { null } ?: song.coverArt

            // Music videos also boosts hype depending on subscribers count and amount of views
            val baseHypeByTier = when (tier) {
                "Low" -> 14f
                "Standard" -> 24f
                "High" -> 34f
                "Premium" -> 46f
                "Cinematic" -> 60f
                else -> 14f
            }
            // Subscribers count multiplier (scales realistically from 1.0x up to 2.8x)
            val subMultiplier = 1.0f + (kotlin.math.log10((state.meTubeSubscribers + 50).toDouble()) - 1.5).coerceIn(0.0, 3.5).toFloat() * 0.5f
            val totalHypeBoost = (baseHypeByTier * subMultiplier).coerceIn(15f, 85f)

            // Initial view surge on release based on subscribers and tier
            val initialViews = (state.meTubeSubscribers * 0.30 * subMultiplier + when(tier) {
                "Low" -> 1500.0
                "Standard" -> 6000.0
                "High" -> 18000.0
                "Premium" -> 50000.0
                "Cinematic" -> 150000.0
                else -> 1500.0
            }).toLong()

            val newVideo = MeTubeVideo(
                id = UUID.randomUUID().toString(),
                title = finalVidTitle,
                type = "MUSIC_VIDEO",
                views = initialViews,
                likes = (initialViews * 0.08).toLong(),
                comments = listOf(
                    "This music video is pure art! 🔥",
                    "The visuals on this are insane!",
                    "On repeat all week long!"
                ),
                revenueEarned = if (state.meTubeSubscribers >= 1000L) initialViews * 0.0035 else 0.0,
                weekPublished = state.currentWeek,
                yearPublished = state.currentYear,
                thumbnailId = finalThumbnail ?: "ic_video",
                artistName = state.artistName,
                customThumbnail = finalThumbnail
            )

            val updatedSongs = state.songs.map { s ->
                if (s.id == songId) {
                    s.copy(
                        videoTier = tier,
                        videoTitle = finalVidTitle,
                        videoThumbnail = finalThumbnail,
                        hype = (s.hype + totalHypeBoost).coerceAtMost(100f)
                    )
                } else s
            }

            showToast("Created '$finalVidTitle'! Hype boosted (+${totalHypeBoost.toInt()}%) and published to MeTube with ${String.format("%,d", initialViews)} views! (-$videoEnergyCost Energy)")
            state.copy(
                energy = (state.energy - videoEnergyCost).coerceAtLeast(0),
                cash = state.cash - cost,
                songs = updatedSongs,
                meTubeVideos = listOf(newVideo) + state.meTubeVideos,
                signedLabelDeliveredMeTubeVideos = if (state.signedLabelId != null) state.signedLabelDeliveredMeTubeVideos + 1 else state.signedLabelDeliveredMeTubeVideos,
                popularity = (state.popularity + popularityBonus).coerceAtMost(100f)
            )
        }
    }

    fun compileAndReleaseAlbum(title: String, isEP: Boolean, songIds: List<String>, marketingTier: String) {
        val marketingCost = when (marketingTier) {
            "Budget" -> 1500.0
            "Standard" -> 5000.0
            "Cinematic" -> 15000.0
            "Global Campaign" -> 40000.0
            else -> 0.0
        }

        updateState { state ->
            if (state.energy < 40) {
                showToast("Not enough energy (40 required) to compile and launch an album!")
                return@updateState state
            }
            if (state.cash < marketingCost) {
                showToast("Insufficient cash for the selected marketing tier!")
                return@updateState state
            }

            val requiredSongCount = if (isEP) 3..5 else 8..14
            if (songIds.size !in requiredSongCount) {
                val typeStr = if (isEP) "EP" else "Album"
                showToast("An ${typeStr} requires ${requiredSongCount.first} to ${requiredSongCount.last} songs! You selected ${songIds.size}.")
                return@updateState state
            }

            val selectedSongs = state.songs.filter { it.id in songIds }
            if (selectedSongs.size != songIds.size) {
                showToast("Error finding selected songs!")
                return@updateState state
            }

            var totalPriorStreams = 0L
            selectedSongs.forEach { song ->
                if (song.isUploadedMap.isNotEmpty()) {
                    totalPriorStreams += song.isUploadedMap.values.sum()
                }
            }

            val streamBonusHype = (totalPriorStreams / 100_000f).coerceIn(0f, 30f)
            val initialAvgRating = selectedSongs.map { it.rating }.average().toFloat()

            val genreGroups = selectedSongs.groupBy { it.genreId }
            val primaryGenreCount = genreGroups.values.maxOfOrNull { it.size } ?: 0
            val isCohesive = primaryGenreCount.toFloat() / selectedSongs.size >= 0.75f
            val cohesionBonus = if (isCohesive) 0.4f else -0.3f
            val highQualityBonus = selectedSongs.count { it.soundEngineerQuality == "Expert" || it.soundEngineerQuality == "Legendary" } * 0.05f
            val finalRating = (initialAvgRating + cohesionBonus + highQualityBonus).coerceIn(1.0f, 5.0f)
            val acclaimBase = finalRating * 20f
            val acclaimNoise = kotlin.random.Random.nextInt(-6, 6)
            val finalAcclaim = (acclaimBase + acclaimNoise).coerceIn(10f, 100f)

            val reviewList = mutableListOf<String>()
            val artistName = state.artistName
            if (finalRating >= 4.2f) {
                reviewList.add("★ A masterpiece! @$artistName has woven a rich, coherent tapestry of sound with unmatched flow.")
                reviewList.add("★ The synergy on '${title}' is absolute. A standout release that will define this year.")
            } else if (finalRating >= 3.0f) {
                reviewList.add("★ A solid effort. While some tracks feel like filler, standout singles anchor the tracklist nicely.")
                reviewList.add("★ Enjoyable and structurally sound, showing clear artistic maturity from @$artistName.")
            } else {
                reviewList.add("★ A disjointed compilation. The track progression feels chaotic, and several tracks lack sonic polish.")
                reviewList.add("★ Highly erratic. We expected more cohesive storytelling, but the execution falls flat.")
            }

            val tierHype = when (marketingTier) {
                "Budget" -> 15f
                "Standard" -> 30f
                "Cinematic" -> 50f
                "Global Campaign" -> 70f
                else -> 10f
            }
            val finalHype = (tierHype + (state.popularity * 0.25f) + streamBonusHype * 0.5f).coerceIn(10f, 100f)
            val albumIsGood = initialAvgRating > 3.5f

            val newAlbum = Album(
                id = java.util.UUID.randomUUID().toString().take(6),
                title = title,
                isEP = isEP,
                songIds = songIds,
                weekCreated = state.currentWeek,
                yearCreated = state.currentYear,
                rating = finalRating,
                criticalAcclaim = finalAcclaim,
                isReleased = true,
                hype = finalHype,
                price = if (isEP) 4.99 else 9.99,
                marketingTier = marketingTier,
                reviews = reviewList
            )

            val albumUpdatedSongs = state.songs.map { song ->
                if (song.id in songIds) {
                    var newIsUploadedMap = if (song.isUploadedMap.isEmpty()) mapOf("tunes" to 100L) else song.isUploadedMap
                    if (albumIsGood && song.isUploadedMap.isNotEmpty()) {
                        newIsUploadedMap = newIsUploadedMap.mapValues { (_, streams) -> (streams * 1.15).toLong() }
                    }
                    song.copy(
                        isReleased = true,
                        weeksUntilRelease = 0,
                        isUploadedMap = newIsUploadedMap,
                        totalStreams = newIsUploadedMap.values.sum()
                    )
                } else song
            }

            val typeStr = if (isEP) "EP" else "Album"
            val eventStr = "Year ${state.currentYear}, Week ${state.currentWeek}: Released ${typeStr} '${title}' (${String.format("%.1f", finalRating)}⭐) to critical acclaim!"
            var labelBonus = 0.0
            if (state.signedLabelId != null) {
                labelBonus = if (isEP) 8000.0 else 25000.0
            }

            showToast("Successfully compiled and launched ${typeStr} '${title}'!")

            state.copy(
                energy = state.energy - 40,
                cash = state.cash - marketingCost + (labelBonus * (1f - state.signedLabelCut)),
                songs = albumUpdatedSongs,
                albums = state.albums + newAlbum,
                signedLabelDeliveredAlbumsLP = if (state.signedLabelId != null && !isEP) state.signedLabelDeliveredAlbumsLP + 1 else state.signedLabelDeliveredAlbumsLP,
                signedLabelDeliveredAlbumsEP = if (state.signedLabelId != null && isEP) state.signedLabelDeliveredAlbumsEP + 1 else state.signedLabelDeliveredAlbumsEP,
                timelineEvents = state.timelineEvents + eventStr,
                criticalAcclaim = (state.criticalAcclaim + (finalRating * 1.5f)).coerceAtMost(100f)
            )
        }
    }
    // --- Hyper-Realistic Tour Booker App ---
    fun createDraftTour(
        title: String,
        level: String, // "Simple", "Pro", "Premium"
        totalWeeks: Int,
        locationsCount: Int,
        ticketPrice: Double,
        promoBudget: Double,
        productionTier: String,
        transportTier: String,
        backupDancersCount: Int
    ) {
        updateState { state ->
            if (state.playerLevel < 6) {
                showToast("Tour circuit locked! You must reach Level 6 before booking tours (Current: Level ${state.playerLevel}).")
                return@updateState state
            }
            val baseVenueCost = when (level) {
                "Simple" -> 1500.0 * locationsCount
                "Pro" -> 8000.0 * locationsCount
                "Premium" -> 40000.0 * locationsCount
                else -> 1000.0 * locationsCount
            }
            val productionCost = when (productionTier) {
                "Neon Laser Light Show" -> 10000.0
                "Hyper-Realistic Pyro & Hologram FX" -> 35000.0
                else -> 2000.0
            }
            val weeklyTransportCost = when (transportTier) {
                "Luxury Sleeper Bus" -> 4000.0
                "Chartered Private Jet" -> 20000.0
                else -> 1000.0
            }
            val weeklyDancersCost = (backupDancersCount / 4) * 1500.0

            val totalWeeklyOperating = weeklyTransportCost + weeklyDancersCost
            val totalOperatingSetup = totalWeeklyOperating * totalWeeks
            val totalHypePromo = promoBudget

            val initialSetupCost = baseVenueCost + productionCost + totalOperatingSetup + totalHypePromo

            // Apply car reduction & signed label reduction to initial venue booking
            val carReduction = state.ownedCars.maxByOrNull { it.costReductionPercent }?.costReductionPercent ?: 0f
            val labelReduction = if (state.signedLabelId != null) 0.50f else 0f
            val finalSetupCost = initialSetupCost * (1f - carReduction - labelReduction).coerceAtLeast(0.1f)

            // Energy & Time costs
            val energyCost = when (level) {
                "Simple" -> 15
                "Pro" -> 25
                "Premium" -> 40
                else -> 10
            }
            val actualEnergyCost = if (transportTier == "Chartered Private Jet") (energyCost / 2) else energyCost

            val timeCost = when (level) {
                "Simple" -> 2
                "Pro" -> 3
                "Premium" -> 5
                else -> 1
            }

            val payPerAttendee = ticketPrice

            // ---- TOUR MANAGER PROPOSAL FEASIBILITY & ROI PROJECTIONS ----
            val baseMaxAtt = when (level) {
                "Simple" -> 1000
                "Pro" -> 10000
                "Premium" -> 100000
                else -> 1000
            }
            val popularityFactor = state.popularity / 100f
            val influenceFactor = state.influence / 100f
            val promoEffect = (promoBudget / 1000.0).coerceAtMost(2.0)

            val fairPrice = when (level) {
                "Simple" -> 30.0
                "Pro" -> 90.0
                "Premium" -> 220.0
                else -> 30.0
            }
            val priceRatio = ticketPrice / fairPrice
            val priceMultiplier = if (priceRatio > 1.0) {
                (1.0 / (priceRatio * priceRatio)).toFloat()
            } else {
                (1.0f + (1.0f - priceRatio.toFloat()) * 0.3f)
            }
            val attVal = (baseMaxAtt * (popularityFactor * 0.5 + influenceFactor * 0.3 + promoEffect * 0.2) * priceMultiplier).toInt()
            val projectedReach = attVal.coerceIn(0, baseMaxAtt)

            val labelTouringMult = if (state.ownLabel != null) 1.0 + (state.ownLabel.touringLevel * 0.05) else 1.0
            val projectedWeeklyEarnings = projectedReach * ticketPrice * labelTouringMult
            val projectedTotalEarnings = projectedWeeklyEarnings * totalWeeks

            val projectedNetProfit = projectedTotalEarnings - finalSetupCost
            val roiPercent = if (finalSetupCost > 0.0) (projectedNetProfit / finalSetupCost) * 100.0 else 0.0

            val (verdict, analysis) = when {
                roiPercent < -15.0 -> {
                    "⚠ RISKY / POOR ROI" to "I cannot recommend this setup. Your ticket price of $${String.format("%.2f", ticketPrice)} is either too high for your current fan base, or your transport/production overhead ($${String.format("%,.2f", finalSetupCost)}) is inflated. We project a net loss of $${String.format("%,.2f", -projectedNetProfit)} (ROI: ${String.format("%.1f", roiPercent)}%). I suggest cutting costs like backup dancers or lavish jets, or reducing ticket prices to attract more attendees."
                }
                roiPercent in -15.0..15.0 -> {
                    "⚖ MARGINAL / BREAK-EVEN" to "This is a borderline plan. We expect to just barely break even with a projected net profit of $${String.format("%,.2f", projectedNetProfit)} (ROI: ${String.format("%.1f", roiPercent)}%). It's safe to run if you want popularity and exposure, but don't expect major financial growth. Proceed with caution."
                }
                roiPercent in 15.0..100.0 -> {
                    "✅ APPROVED / SUSTAINABLE" to "This is a highly professional proposal. We project a healthy net profit of $${String.format("%,.2f", projectedNetProfit)} (ROI: ${String.format("%.1f", roiPercent)}%). The ticket pricing and marketing budget are perfectly balanced with your stardom. I strongly recommend launching this tour!"
                }
                else -> {
                    "💎 EXCELLENT / GOLDMINE" to "This is an absolute masterpiece of planning! We expect a massive net profit of $${String.format("%,.2f", projectedNetProfit)} (ROI: ${String.format("%.1f", roiPercent)}%). Our fans are starving for this tour, and your margins are incredible. Let's sign the papers and hit the road immediately!"
                }
            }

            val managerMail = MailMessage(
                id = UUID.randomUUID().toString(),
                sender = "Tour Manager",
                subject = "Tour Proposal Review: ${title.ifEmpty { "The Grand Road Tour" }} ($level)",
                body = """
                    Hello!

                    I have completed the feasibility study and financial review for your proposed tour: "${title.ifEmpty { "The Grand Road Tour" }}".

                    --- PROPOSAL DETAILS ---
                    • Tour Category: $level Tour ($locationsCount Locations, $totalWeeks Weeks)
                    • Ticket Price: $${String.format("%.2f", ticketPrice)} per attendee
                    • Promotion Budget: $${String.format("%,.2f", promoBudget)}
                    • Production Tier: $productionTier
                    • Transport Tier: $transportTier
                    • Backup Dancers: $backupDancersCount

                    --- TOUR MANAGER FINANCIAL PROJECTIONS ---
                    • Total Setup & Venue Fees: $${String.format("%,.2f", finalSetupCost)}
                    • Projected Weekly Attendance: ${String.format("%,d", projectedReach)} fans per show
                    • Projected Total Attendance: ${String.format("%,d", projectedReach * totalWeeks)} fans
                    • Projected Gross Revenue: $${String.format("%,.2f", projectedTotalEarnings)}
                    • Projected Net Income: $${String.format("%,.2f", projectedNetProfit)}

                    --- FEASIBILITY VERDICT ---
                    • Status: $verdict
                    • Analysis: $analysis

                    To proceed with this tour, open your Tour Booker dashboard and click 'Approve and Launch'!
                """.trimIndent(),
                type = "NORMAL",
                expiresWeek = state.currentWeek + 4,
                expiresYear = state.currentYear
            )

            // Delete any existing draft tour to avoid cluttering
            val filteredTours = state.tours.filter { it.status != "DRAFT" }

            val draftTour = Tour(
                id = UUID.randomUUID().toString(),
                level = level,
                totalWeeks = totalWeeks,
                currentWeek = 0,
                weeklyEnergyCost = actualEnergyCost,
                weeklyTimeCost = timeCost,
                setupCost = finalSetupCost,
                payPerAttendee = payPerAttendee,
                promotionBudget = promoBudget,
                status = "DRAFT",
                bookedWeek = state.currentWeek + 1,
                bookedYear = state.currentYear,
                title = title.ifEmpty { "The Grand Road Tour" },
                locationsCount = locationsCount,
                ticketPrice = ticketPrice,
                productionTier = productionTier,
                transportTier = transportTier,
                backupDancersCount = backupDancersCount,
                isLaunched = false,
                showHype = 20f + (promoBudget / 1000.0).toFloat() + (if (productionTier != "Basic") 15f else 0f)
            )

            showToast("📋 TOUR PROPOSAL UNDER REVIEW: Report delivered to your inbox!")
            state.copy(
                tours = filteredTours + draftTour,
                mailbox = state.mailbox + managerMail
            )
        }
    }

    fun createRealisticTour(
        title: String,
        tourType: String, // "Club Run", "City Tour", "National Tour", "Global Tour"
        bannerUri: String?,
        selectedCrewRoles: List<String>,
        openingActNpcId: String?,
        openingActName: String?,
        openingActCost: Double,
        closingActNpcId: String?,
        closingActName: String?,
        closingActCost: Double,
        setlistSongIds: List<String>,
        setlistSongTitles: List<String>,
        selectedMerchIds: List<String>,
        generalTicketPrice: Double,
        vipTicketPrice: Double,
        platinumTicketPrice: Double,
        launchWeek: Int,
        launchYear: Int,
        productionTier: String = "Neon Laser Light Show",
        transportTier: String = "Luxury Sleeper Bus",
        promoBudget: Double = 2500.0,
        routeMarkets: List<String> = emptyList()
    ) {
        updateState { state ->
            val normalizedRouteMarkets = parseTourMarkets(routeMarkets.joinToString(";"))
            if (normalizedRouteMarkets.isEmpty()) {
                showToast("Add at least one tour market as City, Country before booking.")
                return@updateState state
            }
            if (state.playerLevel < 6) {
                showToast("Tour circuit locked! You must reach Level 6 before booking tours (Current: Level ${state.playerLevel}).")
                return@updateState state
            }
            val level = when (tourType) {
                "Global Tour" -> "Premium"
                "National Tour", "City Tour" -> "Pro"
                else -> "Simple"
            }
            val locationsCount = when (tourType) {
                "Global Tour" -> 22
                "National Tour" -> 14
                "City Tour" -> 8
                else -> 4
            }
            val totalWeeks = when (tourType) {
                "Global Tour" -> 14
                "National Tour" -> 8
                "City Tour" -> 5
                else -> 3
            }
            val venueGuaranteeCost = when (tourType) {
                "Global Tour" -> 260000.0
                "National Tour" -> 80000.0
                "City Tour" -> 24000.0
                else -> 5000.0
            }
            val baseVenueCap = when (tourType) {
                "Global Tour" -> 50000
                "National Tour" -> 12500
                "City Tour" -> 2800
                else -> 600
            }
            val baseEnergy = when (tourType) {
                "Global Tour" -> 10
                "National Tour" -> 8
                "City Tour" -> 6
                else -> 4
            }

            val crewCostPerWeekMap = mapOf(
                "Tour Manager" to 1200.0,
                "Head of Security" to 950.0,
                "Audio Engineer" to 850.0,
                "Wardrobe Stylist" to 700.0,
                "Instrumentalist" to 800.0,
                "Dancers" to 1100.0,
                "Photographer & Videographer" to 650.0,
                "Medics" to 750.0,
                "Driver" to 600.0
            )
            val weeklyCrewCost = selectedCrewRoles.sumOf { crewCostPerWeekMap[it] ?: 500.0 }
            val totalCrewCost = weeklyCrewCost * totalWeeks

            val productionCost = when (productionTier) {
                "Hyper-Realistic Pyro & Hologram FX" -> 35000.0
                "Neon Laser Light Show" -> 10000.0
                else -> 2500.0
            }
            val weeklyTransportCost = when (transportTier) {
                "Chartered Private Jet" -> 18000.0
                "Luxury Sleeper Bus" -> 3500.0
                else -> 800.0
            }
            val totalTransportCost = weeklyTransportCost * totalWeeks
            val talentCost = openingActCost + closingActCost
            val rawSetupCost = venueGuaranteeCost + productionCost + totalTransportCost + totalCrewCost + talentCost + promoBudget

            val carReduction = state.ownedCars.maxByOrNull { it.costReductionPercent }?.costReductionPercent ?: 0f
            val labelReduction = if (state.signedLabelId != null) 0.50f else 0f
            val finalSetupCost = rawSetupCost * (1f - carReduction - labelReduction).coerceAtLeast(0.1f)

            if (state.cash < finalSetupCost) {
                showToast("Insufficient funds! You need $${String.format("%,.2f", finalSetupCost)} to launch this tour setup.")
                return@updateState state
            }

            var actualEnergy = baseEnergy.toFloat()
            if (selectedCrewRoles.contains("Medics")) actualEnergy *= 0.65f
            if (selectedCrewRoles.contains("Driver")) actualEnergy *= 0.80f
            if (transportTier == "Chartered Private Jet") actualEnergy *= 0.70f
            val finalEnergyCost = actualEnergy.toInt().coerceIn(3, 10)

            var calcHype = 25f
            if (selectedCrewRoles.contains("Photographer & Videographer")) calcHype += 12f
            if (selectedCrewRoles.contains("Dancers")) calcHype += 15f
            if (selectedCrewRoles.contains("Wardrobe Stylist")) calcHype += 10f
            if (selectedCrewRoles.contains("Instrumentalist")) calcHype += 10f
            if (openingActNpcId != null) calcHype += 15f
            if (closingActNpcId != null) calcHype += 20f
            calcHype += (promoBudget / 1000.0).toFloat()

            val weightedAvgPrice = (generalTicketPrice * 0.70) + (vipTicketPrice * 0.25) + (platinumTicketPrice * 0.05)

            // Update NPC relationship if hired
            val updatedNpcs = state.npcs.map { npc ->
                when {
                    npc.id == openingActNpcId -> npc.copy(
                        relationshipWithPlayer = (npc.relationshipWithPlayer + 15f).coerceAtMost(100f),
                        wealth = npc.wealth + openingActCost
                    )
                    npc.id == closingActNpcId -> npc.copy(
                        relationshipWithPlayer = (npc.relationshipWithPlayer + 18f).coerceAtMost(100f),
                        wealth = npc.wealth + closingActCost
                    )
                    else -> npc
                }
            }

            val newTour = Tour(
                id = UUID.randomUUID().toString(),
                level = level,
                totalWeeks = totalWeeks,
                currentWeek = 0,
                weeklyEnergyCost = finalEnergyCost,
                weeklyTimeCost = if (selectedCrewRoles.contains("Tour Manager")) 2 else 3,
                setupCost = finalSetupCost,
                payPerAttendee = weightedAvgPrice,
                promotionBudget = promoBudget,
                status = "BOOKED",
                bookedWeek = launchWeek,
                bookedYear = launchYear,
                title = title.ifEmpty { "${state.artistName} $tourType" },
                locationsCount = locationsCount,
                ticketPrice = weightedAvgPrice,
                productionTier = productionTier,
                transportTier = transportTier,
                backupDancersCount = if (selectedCrewRoles.contains("Dancers")) 6 else 0,
                isLaunched = true,
                showHype = calcHype,
                bannerUri = bannerUri,
                tourType = tourType,
                routeMarkets = normalizedRouteMarkets,
                selectedCrewRoles = selectedCrewRoles,
                openingActNpcId = openingActNpcId,
                openingActName = openingActName,
                openingActCost = openingActCost,
                closingActNpcId = closingActNpcId,
                closingActName = closingActName,
                closingActCost = closingActCost,
                setlistSongIds = setlistSongIds,
                setlistSongTitles = setlistSongTitles,
                selectedMerchIds = selectedMerchIds,
                generalTicketPrice = generalTicketPrice,
                vipTicketPrice = vipTicketPrice,
                platinumTicketPrice = platinumTicketPrice,
                launchWeek = launchWeek,
                launchYear = launchYear
            )

            val managerMail = MailMessage(
                id = UUID.randomUUID().toString(),
                sender = if (selectedCrewRoles.contains("Tour Manager")) "Tour Manager" else "Booking Agency",
                subject = "Tour Confirmation: ${newTour.title} ($tourType)",
                body = """
                    Tour contract signed and routing locked in!

                    --- TOUR SUMMARY ---
                    • Tour Name: ${newTour.title}
                    • Format: $tourType ($locationsCount Cities, $totalWeeks Weeks)
                    • Tour Markets: ${normalizedRouteMarkets.joinToString(" → ")}
                    • Scheduled Launch: Week $launchWeek, Year $launchYear
                    • Total Setup Financed: $${String.format("%,.2f", finalSetupCost)}

                    --- PRICING & INVENTORY ---
                    • General Admission (70%): $${String.format("%.2f", generalTicketPrice)}
                    • VIP Experience (25%): $${String.format("%.2f", vipTicketPrice)}
                    • Platinum Access (5%): $${String.format("%.2f", platinumTicketPrice)}
                    • Blended Average Ticket: $${String.format("%.2f", weightedAvgPrice)}

                    --- TALENT & CREW ---
                    • Opening Act: ${openingActName ?: "None (Solo Opening)"}
                    • Closing Act: ${closingActName ?: "None"}
                    • Active Crew: ${if (selectedCrewRoles.isEmpty()) "Minimal (Self-Managed)" else selectedCrewRoles.joinToString(", ")}
                    • Setlist Length: ${setlistSongTitles.size} songs
                    • Merch Stand: ${selectedMerchIds.size} catalog styles stocked

                    We are launching presale campaigns and preparing tour logistics!
                """.trimIndent(),
                type = "NORMAL",
                expiresWeek = launchWeek + totalWeeks + 4,
                expiresYear = launchYear
            )

            showToast("🚀 TOUR OFFICIALLY BOOKED! Road departure set for Week $launchWeek, Year $launchYear!")
            state.copy(
                cash = state.cash - finalSetupCost,
                tours = state.tours + newTour,
                npcs = updatedNpcs,
                mailbox = state.mailbox + managerMail
            )
        }
    }

    fun launchTour(tourId: String) {
        updateState { state ->
            val tour = state.tours.find { it.id == tourId } ?: return@updateState state
            if (state.cash < tour.setupCost) {
                showToast("Insufficient cash ($${String.format("%,.2f", tour.setupCost)} required) to launch this tour!")
                return@updateState state
            }

            // Check if player has time slots available (including 10 slots per venture)
            val sideJobSlots = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
            val tourSlots = state.tours.filter { it.status == "ACTIVE" }.sumOf { it.weeklyTimeCost }
            val ventureSlots = state.businessVentures.count { it.isOwned && it.isPlayerInControl } * 10
            val activeSlots = sideJobSlots + tourSlots + ventureSlots
            if (activeSlots + tour.weeklyTimeCost > state.maxTimeSlots) {
                showToast("Insufficient free time slots! Current used: $activeSlots / ${state.maxTimeSlots} (Ventures take 10 slots each). Requires ${tour.weeklyTimeCost} more.")
                return@updateState state
            }

            val updatedTours = state.tours.map {
                if (it.id == tourId) {
                    it.copy(status = "BOOKED", isLaunched = true)
                } else it
            }

            showToast("🚀 TOUR OFFICIALLY LAUNCHED! Enjoy the road!")
            state.copy(
                cash = state.cash - tour.setupCost,
                tours = updatedTours
            )
        }
    }

    // 7. Trading
    fun buyStock(stockId: String, qty: Int) {
        updateState { state ->
            val stock = state.stocks.find { it.id == stockId } ?: GameData.STOCKS.find { it.id == stockId } ?: return@updateState state
            if (stock.isPlayerCompany && stock.founderEquityPercent > 0.0) {
                showToast("Active founders cannot trade shares on the open exchange. Use the Founder Buyback feature!")
                return@updateState state
            }
            val cost = stock.currentPrice * qty
            if (state.cash < cost) {
                showToast("Insufficient cash to purchase stocks!")
                return@updateState state
            }

            val updatedStocks = state.stocks.map { s ->
                if (s.id == stockId) {
                    val oldTotalCost = s.averagePurchasePrice * s.quantity
                    val newCost = stock.currentPrice * qty
                    val newQty = s.quantity + qty
                    val newAvgPrice = if (newQty > 0) (oldTotalCost + newCost) / newQty else 0.0
                    s.copy(quantity = newQty, averagePurchasePrice = newAvgPrice)
                } else s
            }

            val lockKey = "traded_stock_${stockId}_week_${state.currentWeek}_year_${state.currentYear}"
            val updatedJournal = if (!state.spiritualJournal.contains(lockKey)) {
                state.spiritualJournal + lockKey
            } else state.spiritualJournal

            showToast("Bought $qty shares of ${stock.name}! Same-week liquidation is locked.")
            state.copy(
                cash = state.cash - cost,
                stocks = updatedStocks,
                businessSkill = (state.businessSkill + 0.5f).coerceAtMost(100f),
                spiritual = state.spiritual.copy(spiritualJournal = updatedJournal)
            )
        }
    }

    fun sellStock(stockId: String, qty: Int) {
        updateState { state ->
            val stock = state.stocks.find { it.id == stockId }
            if (stock == null || stock.quantity < qty) {
                showToast("Insufficient shares to sell!")
                return@updateState state
            }

            val lockKey = "traded_stock_${stockId}_week_${state.currentWeek}_year_${state.currentYear}"
            if (state.spiritualJournal.contains(lockKey)) {
                showToast("❌ REGULATORY TRADING LOCK: You purchased ${stock.name} this week! Regulations restrict same-week liquidations to prevent exploits.")
                return@updateState state
            }

            val payout = stock.currentPrice * qty
            val updatedStocks = state.stocks.map { s ->
                if (s.id == stockId) s.copy(quantity = s.quantity - qty) else s
            }

            // Check if profit made
            val basePrice = if (stock.averagePurchasePrice > 0.0) stock.averagePurchasePrice else (GameData.STOCKS.find { it.id == stockId }?.currentPrice ?: stock.currentPrice)
            val profitPerShare = stock.currentPrice - basePrice
            val totalProfit = (profitPerShare * qty).coerceAtLeast(0.0)
            val isProfit = stock.currentPrice >= basePrice
            val skillChange = if (isProfit) 1.5f else -1.0f

            val currentTaxRate = (0.12f + state.politicalTaxModifier).coerceIn(0.01f, 0.35f)
            val stockTax = totalProfit * currentTaxRate
            val stockTithe = totalProfit * 0.10

            val toastMsg = if (totalProfit > 0.0) {
                "Sold $qty shares of ${stock.name} for $${String.format("%.2f", payout)} (+$${String.format("%,.0f", totalProfit)} profit! Accrued $${String.format("%,.0f", stockTax)} tax & $${String.format("%,.0f", stockTithe)} tithe)."
            } else {
                "Sold $qty shares of ${stock.name} for $${String.format("%.2f", payout)}!"
            }
            showToast(toastMsg)
            state.copy(
                cash = state.cash + payout,
                stocks = updatedStocks,
                businessSkill = (state.businessSkill + skillChange).coerceIn(10f, 100f),
                banking = state.banking.copy(
                    taxDebt = state.banking.taxDebt + stockTax,
                    titheDebt = state.banking.titheDebt + stockTithe
                )
            )
        }
    }

    fun buyCrypto(cryptoId: String, amountUsd: Double) {
        updateState { state ->
            if (state.cash < amountUsd) {
                showToast("Insufficient cash!")
                return@updateState state
            }
            val crypto = state.cryptos.find { it.id == cryptoId } ?: GameData.CRYPTOS.find { it.id == cryptoId } ?: return@updateState state
            val buyQty = amountUsd / crypto.currentPrice

            val updatedCryptos = state.cryptos.map { c ->
                if (c.id == cryptoId) {
                    val oldTotalCost = c.averagePurchasePrice * c.quantity
                    val newCost = amountUsd
                    val newQty = c.quantity + buyQty
                    val newAvgPrice = if (newQty > 0.0) (oldTotalCost + newCost) / newQty else 0.0
                    c.copy(quantity = newQty, averagePurchasePrice = newAvgPrice)
                } else c
            }

            val lockKey = "traded_crypto_${cryptoId}_week_${state.currentWeek}_year_${state.currentYear}"
            val updatedJournal = if (!state.spiritualJournal.contains(lockKey)) {
                state.spiritualJournal + lockKey
            } else state.spiritualJournal

            showToast("Bought ${String.format("%.4f", buyQty)} of ${crypto.name}! Same-week liquidation is restricted.")
            state.copy(
                cash = state.cash - amountUsd,
                cryptos = updatedCryptos,
                businessSkill = (state.businessSkill + 0.5f).coerceAtMost(100f),
                spiritual = state.spiritual.copy(spiritualJournal = updatedJournal)
            )
        }
    }

    fun sellCrypto(cryptoId: String, qtyToSell: Double) {
        updateState { state ->
            val crypto = state.cryptos.find { it.id == cryptoId }
            if (crypto == null || crypto.quantity < qtyToSell) {
                showToast("Insufficient crypto holding to sell!")
                return@updateState state
            }

            val lockKey = "traded_crypto_${cryptoId}_week_${state.currentWeek}_year_${state.currentYear}"
            if (state.spiritualJournal.contains(lockKey)) {
                showToast("❌ EXCHANGE LOCK: You bought ${crypto.name} this week! Liquidating same-week purchases is restricted.")
                return@updateState state
            }

            val payout = crypto.currentPrice * qtyToSell
            val updatedCryptos = state.cryptos.map { c ->
                if (c.id == cryptoId) c.copy(quantity = c.quantity - qtyToSell) else c
            }

            // Real profit calculation based on average purchase price
            val basePrice = if (crypto.averagePurchasePrice > 0.0) crypto.averagePurchasePrice else (GameData.CRYPTOS.find { it.id == cryptoId }?.currentPrice ?: crypto.currentPrice)
            val profitPerUnit = crypto.currentPrice - basePrice
            val totalProfit = (profitPerUnit * qtyToSell).coerceAtLeast(0.0)
            val isProfit = crypto.currentPrice >= basePrice
            val skillChange = if (isProfit) 2.0f else -1.5f

            val currentTaxRate = (0.12f + state.politicalTaxModifier).coerceIn(0.01f, 0.35f)
            val cryptoTax = totalProfit * currentTaxRate
            val cryptoTithe = totalProfit * 0.10

            val toastMsg = if (totalProfit > 0.0) {
                "Sold ${String.format("%.4f", qtyToSell)} of ${crypto.name} for $${String.format("%.2f", payout)} (+$${String.format("%,.0f", totalProfit)} profit! Accrued $${String.format("%,.0f", cryptoTax)} tax & $${String.format("%,.0f", cryptoTithe)} tithe)."
            } else {
                "Sold ${String.format("%.4f", qtyToSell)} of ${crypto.name} for $${String.format("%.2f", payout)}!"
            }
            showToast(toastMsg)
            state.copy(
                cash = state.cash + payout,
                cryptos = updatedCryptos,
                businessSkill = (state.businessSkill + skillChange).coerceIn(10f, 100f),
                banking = state.banking.copy(
                    taxDebt = state.banking.taxDebt + cryptoTax,
                    titheDebt = state.banking.titheDebt + cryptoTithe
                )
            )
        }
    }

    // 8. Record Label System
    fun createOwnRecordLabel(name: String, coFounderId: String? = null, coFounderPercent: Int = 0) {
        updateState { state ->
            val hasCoFounder = coFounderId != null && coFounderId != "none"
            val cfCandidate = if (hasCoFounder) GameData.CO_FOUNDER_CANDIDATES.find { it.id == coFounderId } else null

            val baseCost = 2000000.0
            val coFounderFunding = when (coFounderPercent) {
                20 -> 500000.0
                30 -> 1000000.0
                50 -> 2000000.0
                else -> 0.0
            }

            // Player's net cash cost out of pocket
            val netPlayerCost = (baseCost - coFounderFunding).coerceAtLeast(0.0)

            // Single founder requires $5M, with co-founder the financial load is shared so they can launch with less personal capital!
            val minimumCashRequired = if (hasCoFounder) (5000000.0 - coFounderFunding).coerceAtLeast(2000000.0) else 5000000.0

            if (state.cash < minimumCashRequired || state.popularity < 70f || state.influence < 80f) {
                val reqMsg = if (hasCoFounder) "with Co-Founder needs $${String.format("%,.0f", minimumCashRequired)} cash" else "Single founder needs $5M cash"
                showToast("Requirements not met! $reqMsg, 70% pop, 80% inf.")
                return@updateState state
            }

            val coFounderSal = when (coFounderPercent) {
                20 -> 2000.0
                30 -> 4000.0
                50 -> 8000.0
                else -> 0.0
            }

            val coFoundersList = if (cfCandidate != null) {
                listOf(cfCandidate.copy(equityShare = coFounderPercent.toFloat(), salaryExpectation = coFounderSal, relationship = 90f))
            } else emptyList()

            val playerEquity = 100f - coFounderPercent.toFloat()
            val shareholders = mutableListOf<Shareholder>()
            shareholders.add(Shareholder(name = "You (${state.artistName})", equityShare = playerEquity, type = "Founder", boardSeat = true, satisfaction = 100f))
            if (cfCandidate != null) {
                shareholders.add(Shareholder(name = cfCandidate.name, equityShare = coFounderPercent.toFloat(), type = "Co-Founder", boardSeat = true, satisfaction = 90f))
            }

            val boardMembers = mutableListOf<BoardMember>()
            boardMembers.add(BoardMember(id = "player_board", name = "You (${state.artistName})", type = "Player", powerWeight = playerEquity, satisfaction = 100f, alignment = "Growth"))
            if (cfCandidate != null) {
                boardMembers.add(BoardMember(id = "cofounder_board_" + cfCandidate.id, name = cfCandidate.name, type = "Co-Founder", powerWeight = coFounderPercent.toFloat(), satisfaction = 90f, alignment = "Growth"))
            }

            val initialCorporateCash = 500000.0 + coFounderFunding

            val newLabel = OwnRecordLabel(
                name = name,
                companyCash = initialCorporateCash,
                equityHeldByPlayer = playerEquity,
                coFounders = coFoundersList,
                shareholders = shareholders,
                boardMembers = boardMembers,
                valuation = 2500000.0 + (coFounderFunding * 1.5),
                dividendPayoutRate = 15f,
                ceoSalary = 2000.0,
                boardSatisfaction = 90f,
                labelReputation = 80f
            )

            showToast("Successfully launched '$name' Record Label! Corporate reserves funded.")
            state.copy(
                cash = state.cash - netPlayerCost,
                ownLabel = newLabel
            )
        }
    }

    fun hireManager(id: String, name: String, cost: Double, efficiency: Float) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val updatedLabel = label.copy(managers = label.managers + LabelManager(id, name, cost, efficiency))
            showToast("Hired Manager $name!")
            state.copy(ownLabel = updatedLabel)
        }
    }

    fun hirePromoTeam(id: String, name: String, cost: Double, boost: Float) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val updatedLabel = label.copy(promoTeams = label.promoTeams + LabelPromotionTeam(id, name, cost, boost))
            showToast("Hired Promotion Team $name!")
            state.copy(ownLabel = updatedLabel)
        }
    }

    fun depositCompanyCash(amount: Double) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            if (state.cash < amount) {
                showToast("Insufficient personal cash!")
                return@updateState state
            }
            val updatedLabel = label.copy(companyCash = label.companyCash + amount)
            showToast("Deposited $${String.format("%,.0f", amount)} into company account!")
            state.copy(
                cash = state.cash - amount,
                ownLabel = updatedLabel
            )
        }
    }

    fun withdrawCompanyCash(amount: Double) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            if (label.companyCash < amount) {
                showToast("Insufficient company cash!")
                return@updateState state
            }
            val updatedLabel = label.copy(companyCash = label.companyCash - amount)
            showToast("Withdrew $${String.format("%,.0f", amount)} from company account!")
            state.copy(
                cash = state.cash + amount,
                ownLabel = updatedLabel
            )
        }
    }

    fun signArtistToCompany(npcId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val npc = state.npcs.find { it.id == npcId } ?: return@updateState state
            if (!npcId.startsWith("npc_star_") && !npcId.startsWith("npc_rookie_")) {
                showToast("Only professional artists can be signed to your record label!")
                return@updateState state
            }
            if (label.signedNPCIds.contains(npcId)) {
                showToast("${npc.name} is already signed!")
                return@updateState state
            }
            val signingBonus = (npc.popularity * 2500.0).coerceAtLeast(15000.0)
            if (label.companyCash < signingBonus) {
                showToast("Signing bonus requires $${String.format("%,.0f", signingBonus)} of company cash!")
                return@updateState state
            }
            val updatedLabel = label.copy(
                companyCash = label.companyCash - signingBonus,
                signedNPCIds = label.signedNPCIds + npcId,
                valuation = label.valuation + (npc.fans * 0.1)
            )
            val updatedNPCs = state.npcs.map { n ->
                if (n.id == npcId) n.copy(currentRecordLabel = label.name, relationshipWithPlayer = (n.relationshipWithPlayer + 15f).coerceAtMost(100f)) else n
            }
            showToast("Signed ${npc.name} to ${label.name}! Paid signing bonus of $${String.format("%,.0f", signingBonus)}.")
            state.copy(
                ownLabel = updatedLabel,
                npcs = updatedNPCs
            )
        }
    }

    fun sponsorArtistRelease(npcId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val npc = state.npcs.find { it.id == npcId } ?: return@updateState state
            val cost = 30000.0
            if (label.companyCash < cost) {
                showToast("Sponsorship requires $30,000 company cash!")
                return@updateState state
            }
            val updatedLabel = label.copy(companyCash = label.companyCash - cost)
            val updatedNPCs = state.npcs.map { n ->
                if (n.id == npcId) {
                    val popularityBoost = Random.nextFloat() * 8f + 5f
                    val fanBoost = (n.fans * 0.10).toLong().coerceAtLeast(10000)
                    n.copy(
                        popularity = (n.popularity + popularityBoost).coerceAtMost(100f),
                        fans = n.fans + fanBoost,
                        bestSongStreams = n.bestSongStreams + (fanBoost * 3)
                    )
                } else n
            }
            showToast("Sponsored a release campaign for ${npc.name}! Chart hype boosted.")
            state.copy(
                ownLabel = updatedLabel,
                npcs = updatedNPCs
            )
        }
    }

    fun upgradeLabelDepartment(deptName: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val currentLevel = when (deptName) {
                "Music Production" -> label.productionLevel
                "Marketing & Public Relations" -> label.marketingLevel
                "Touring Operations" -> label.touringLevel
                "Corporate Legal & Royalties" -> label.legalLevel
                "A&R Discovery" -> label.arLevel
                else -> 1
            }
            if (currentLevel >= 10) {
                showToast("$deptName is already at the maximum level of 10!")
                return@updateState state
            }
            val cost = currentLevel * 150000.0
            if (label.companyCash < cost) {
                showToast("Insufficient company capital! Upgrade requires $${String.format("%,.0f", cost)} company cash.")
                return@updateState state
            }
            val updatedLabel = when (deptName) {
                "Music Production" -> label.copy(companyCash = label.companyCash - cost, productionLevel = currentLevel + 1)
                "Marketing & Public Relations" -> label.copy(companyCash = label.companyCash - cost, marketingLevel = currentLevel + 1)
                "Touring Operations" -> label.copy(companyCash = label.companyCash - cost, touringLevel = currentLevel + 1)
                "Corporate Legal & Royalties" -> label.copy(companyCash = label.companyCash - cost, legalLevel = currentLevel + 1)
                "A&R Discovery" -> label.copy(companyCash = label.companyCash - cost, arLevel = currentLevel + 1)
                else -> label
            }
            showToast("Upgraded $deptName department to Level ${currentLevel + 1}!")
            state.copy(ownLabel = updatedLabel)
        }
    }

    fun buyWardrobeItem(itemId: String) {
        updateState { state ->
            val item = GameData.WARDROBE_ITEMS.find { it.id == itemId } ?: return@updateState state
            if (state.avatarOwnedItemIds.contains(itemId)) {
                showToast("You already own this item!")
                return@updateState state
            }
            if (state.cash < item.cost) {
                showToast("Insufficient personal cash to purchase ${item.name}!")
                return@updateState state
            }
            showToast("Successfully purchased ${item.name}!")
            state.copy(
                cash = state.cash - item.cost,
                avatar = state.avatar.copy(avatarOwnedItemIds = state.avatarOwnedItemIds + itemId)
            )
        }
    }

    fun equipWardrobeItem(itemId: String) {
        updateState { state ->
            val item = GameData.WARDROBE_ITEMS.find { it.id == itemId } ?: return@updateState state
            if (!state.avatarOwnedItemIds.contains(itemId)) {
                showToast("You must buy this item first!")
                return@updateState state
            }
            val newState = when (item.category) {
                "HAIR" -> state.copy(avatar = state.avatar.copy(avatarHairStyleId = itemId))
                "CLOTHES" -> state.copy(avatar = state.avatar.copy(avatarClothingId = itemId))
                "JEWELRY" -> state.copy(avatar = state.avatar.copy(avatarJewelryId = itemId))
                else -> state
            }
            showToast("Equipped ${item.name}!")
            newState
        }
    }

    fun setAvatarBaseLookAndSkin(baseLook: String, skinTone: String) {
        updateState { state ->
            state.copy(
                avatar = state.avatar.copy(avatarBaseLook = baseLook, avatarSkinTone = skinTone)
            )
        }
    }

    fun toggleDarkMode() {
        updateState { state ->
            val next = !state.isDarkMode
            showToast(if (next) "Switched to Dark Mode 🌙" else "Switched to Light Mode ☀️")
            state.copy(isDarkMode = next)
        }
    }

    fun sellOwnCompanyShares(qtyShares: Long) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val sharePrice = label.valuation / label.totalSharesIssued
            val playerMaxShares = ((label.equityHeldByPlayer / 100f) * label.totalSharesIssued).toLong()
            if (qtyShares > playerMaxShares) {
                showToast("You cannot sell more shares than you hold!")
                return@updateState state
            }

            val payout = qtyShares * sharePrice
            val soldPercent = (qtyShares.toFloat() / label.totalSharesIssued.toFloat()) * 100f
            val newPlayerEquity = (label.equityHeldByPlayer - soldPercent).coerceAtLeast(0f)

            // Update shareholders
            var publicFound = false
            val updatedShareholders = label.shareholders.map { sh ->
                if (sh.type == "Founder") {
                    sh.copy(equityShare = newPlayerEquity)
                } else if (sh.name == "Retail Public") {
                    publicFound = true
                    sh.copy(equityShare = sh.equityShare + soldPercent)
                } else sh
            }.toMutableList()

            if (!publicFound) {
                updatedShareholders.add(
                    Shareholder(name = "Retail Public", equityShare = soldPercent, type = "Retail Investors", boardSeat = false, satisfaction = 100f)
                )
            }

            val updatedLabel = label.copy(
                equityHeldByPlayer = newPlayerEquity,
                shareholders = updatedShareholders.filter { it.equityShare > 0.01f }
            )

            showToast("Successfully sold $qtyShares shares of ${label.name} for $${String.format("%,.2f", payout)}!")
            state.copy(
                cash = state.cash + payout,
                ownLabel = updatedLabel
            )
        }
    }

    fun adjustDividendRate(rate: Float) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val clampedRate = rate.coerceIn(0f, 100f)
            showToast("Dividend payout rate adjusted to ${String.format("%.1f", clampedRate)}% of profits!")
            state.copy(ownLabel = label.copy(dividendPayoutRate = clampedRate))
        }
    }

    fun adjustCeoSalary(salary: Double) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val maxAllowed = (label.companyCash * 0.05).coerceAtLeast(1000.0)
            if (salary > maxAllowed && label.boardSatisfaction < 50f) {
                showToast("The Board of Directors rejected your salary increase! Boost Board Satisfaction first.")
                return@updateState state
            }

            val updatedLabel = label.copy(
                ceoSalary = salary,
                boardSatisfaction = if (salary > maxAllowed) (label.boardSatisfaction - 8f).coerceIn(0f, 100f) else label.boardSatisfaction
            )
            showToast("CEO Salary set to $${String.format("%,.0f", salary)}/week!")
            state.copy(ownLabel = updatedLabel)
        }
    }

    fun payOffRecordLabelAdvance(amount: Double) {
        updateState { state ->
            if (state.signedLabelId == null) {
                showToast("You are not currently signed with any record label!")
                return@updateState state
            }
            if (state.recoupableAdvanceBalance <= 0.0) {
                showToast("Your advance balance is already fully paid off!")
                return@updateState state
            }
            val amountToPay = amount.coerceAtMost(state.recoupableAdvanceBalance)
            if (state.cash < amountToPay) {
                showToast("Insufficient cash to pay $${String.format("%,.0f", amountToPay)} towards advance!")
                return@updateState state
            }
            val newBalance = (state.recoupableAdvanceBalance - amountToPay).coerceAtLeast(0.0)
            showToast("Paid $${String.format("%,.0f", amountToPay)} towards your record label advance! Remaining advance: $${String.format("%,.0f", newBalance)}.")
            state.copy(
                cash = state.cash - amountToPay,
                recoupableAdvanceBalance = newBalance
            )
        }
    }

    fun terminateLabelContract() {
        updateState { state ->
            val labelId = state.signedLabelId
            if (labelId == null) {
                showToast("You are not currently signed with any record label!")
                return@updateState state
            }
            if (state.recoupableAdvanceBalance > 0.0) {
                showToast("Cannot terminate contract! You have an unrecouped advance of $${String.format("%,.0f", state.recoupableAdvanceBalance)}. You must pay back the advance cash or force your way out with a lawyer.")
                return@updateState state
            }
            val isContractExpired = if (state.signedLabelEndYear > 0) {
                state.currentYear > state.signedLabelEndYear ||
                    (state.currentYear == state.signedLabelEndYear && state.currentWeek >= state.signedLabelEndWeek)
            } else true

            val fine = if (isContractExpired) 0.0 else (25000.0 + (state.popularity * 750.0)).coerceAtLeast(15000.0)
            if (state.cash < fine) {
                showToast("Cannot afford early termination! You need $${String.format("%,.0f", fine)} to pay the breach of contract fine.")
                return@updateState state
            }

            if (fine > 0.0) {
                showToast("Terminated contract with $labelId! Paid $${String.format("%,.0f", fine)} breach fine.")
            } else {
                showToast("Successfully concluded contract with $labelId! You are now an independent artist.")
            }
            state.copy(
                cash = state.cash - fine,
                signedLabelId = null,
                signedLabelCut = 0f,
                signedLabelRole = null,
                signedLabelLevel = 1,
                signedLabelStartWeek = 0,
                signedLabelStartYear = 0,
                signedLabelEndWeek = 0,
                signedLabelEndYear = 0,
                signedLabelReqSingles = 0,
                signedLabelReqAlbumsLP = 0,
                signedLabelReqAlbumsEP = 0,
                signedLabelReqMeTubeVideos = 0,
                signedLabelDeliveredSingles = 0,
                signedLabelDeliveredAlbumsLP = 0,
                signedLabelDeliveredAlbumsEP = 0,
                signedLabelDeliveredMeTubeVideos = 0,
                popularity = if (fine > 0) (state.popularity - 5f).coerceAtLeast(0f) else state.popularity,
                influence = if (fine > 0) (state.influence - 5f).coerceAtLeast(0f) else state.influence
            )
        }
    }

    fun forceTerminateWithLawyer() {
        updateState { state ->
            val labelId = state.signedLabelId
            if (labelId == null) {
                showToast("You are not currently signed with any record label!")
                return@updateState state
            }
            val lawyer = state.hiredEmployees.find { it.role == "Lawyer" }
            val baseLawyerFee = if (lawyer != null) 5000.0 else 20000.0
            val unrecoupedAdvanceFine = state.recoupableAdvanceBalance
            val totalRequired = baseLawyerFee + unrecoupedAdvanceFine

            if (state.cash < totalRequired) {
                showToast("Cannot afford legal force-out! You need $${String.format("%,.0f", totalRequired)} ($${String.format("%,.0f", baseLawyerFee)} legal fees + $${String.format("%,.0f", unrecoupedAdvanceFine)} advance fine).")
                return@updateState state
            }

            val lawyerMsg = if (lawyer != null) "retained lawyer ${lawyer.name}" else "hired external legal counsel"
            showToast("Force-terminated contract via $lawyerMsg! Paid $${String.format("%,.0f", baseLawyerFee)} legal fee and fined $${String.format("%,.0f", unrecoupedAdvanceFine)} from personal money to settle advance.")
            state.copy(
                cash = state.cash - totalRequired,
                recoupableAdvanceBalance = 0.0,
                signedLabelId = null,
                signedLabelCut = 0f,
                signedLabelRole = null,
                signedLabelLevel = 1,
                signedLabelStartWeek = 0,
                signedLabelStartYear = 0,
                signedLabelEndWeek = 0,
                signedLabelEndYear = 0,
                signedLabelReqSingles = 0,
                signedLabelReqAlbumsLP = 0,
                signedLabelReqAlbumsEP = 0,
                signedLabelReqMeTubeVideos = 0,
                signedLabelDeliveredSingles = 0,
                signedLabelDeliveredAlbumsLP = 0,
                signedLabelDeliveredAlbumsEP = 0,
                signedLabelDeliveredMeTubeVideos = 0,
                industryRespect = (state.industryRespect - 8f).coerceAtLeast(5f),
                trustworthiness = (state.trustworthiness - 10f).coerceAtLeast(5f)
            )
        }
    }

    fun buyBackEquity(amountPercent: Float) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            if (label.equityHeldByPlayer >= 100f) {
                showToast("You already own 100% of the company equity!")
                return@updateState state
            }
            val buybackCost = label.valuation * (amountPercent / 100f)
            if (state.cash < buybackCost) {
                showToast("Insufficient personal cash! Need $${String.format("%,.2f", buybackCost)} to buy back ${amountPercent}% equity.")
                return@updateState state
            }

            val newPlayerEquity = (label.equityHeldByPlayer + amountPercent).coerceAtMost(100f)
            val updatedShareholders = label.shareholders.map { sh ->
                if (sh.type == "Founder") {
                    sh.copy(equityShare = newPlayerEquity)
                } else {
                    val proportion = (100f - newPlayerEquity) / (100f - label.equityHeldByPlayer)
                    sh.copy(equityShare = sh.equityShare * proportion)
                }
            }.filter { it.equityShare > 0.01f }

            val updatedLabel = label.copy(
                equityHeldByPlayer = newPlayerEquity,
                shareholders = updatedShareholders,
                boardSatisfaction = (label.boardSatisfaction + 5f).coerceAtMost(100f)
            )
            showToast("Successfully bought back ${amountPercent}% equity for $${String.format("%,.2f", buybackCost)}!")
            state.copy(
                cash = state.cash - buybackCost,
                ownLabel = updatedLabel
            )
        }
    }

    fun acceptInvestorOffer(offerId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val offer = label.pendingInvestorOffers.find { it.id == offerId } ?: return@updateState state
            if (label.equityHeldByPlayer <= offer.equityRequested) {
                showToast("Insufficient equity to complete this sale!")
                return@updateState state
            }

            val newPlayerEquity = label.equityHeldByPlayer - offer.equityRequested
            val newInvestorShareholder = Shareholder(
                name = offer.investorName,
                equityShare = offer.equityRequested,
                type = offer.investorType + " Investor",
                boardSeat = offer.equityRequested >= 10f,
                satisfaction = 85f
            )

            val updatedShareholders = label.shareholders.map { sh ->
                if (sh.type == "Founder") {
                    sh.copy(equityShare = newPlayerEquity)
                } else sh
            } + newInvestorShareholder

            val newBoardMember = if (newInvestorShareholder.boardSeat) {
                BoardMember(
                    id = "board_" + offer.id,
                    name = offer.investorName + " Rep",
                    type = "Investor Representative",
                    powerWeight = offer.equityRequested,
                    satisfaction = 85f,
                    alignment = if (offer.investorType == "Private Equity") "Dividends" else "Growth"
                )
            } else null

            val updatedBoardMembers = if (newBoardMember != null) label.boardMembers + newBoardMember else label.boardMembers

            val updatedLabel = label.copy(
                companyCash = label.companyCash + offer.cashOffer,
                equityHeldByPlayer = newPlayerEquity,
                shareholders = updatedShareholders,
                boardMembers = updatedBoardMembers,
                pendingInvestorOffers = label.pendingInvestorOffers.filter { it.id != offerId },
                boardSatisfaction = (label.boardSatisfaction + 10f).coerceAtMost(100f)
            )
            showToast("Accepted investment from ${offer.investorName}! +$${String.format("%,.2f", offer.cashOffer)} company capital.")
            state.copy(ownLabel = updatedLabel)
        }
    }

    fun rejectInvestorOffer(offerId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val updatedLabel = label.copy(
                pendingInvestorOffers = label.pendingInvestorOffers.filter { it.id != offerId }
            )
            showToast("Rejected investment proposal.")
            state.copy(ownLabel = updatedLabel)
        }
    }

    fun acceptArtistSigningOffer(offerId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val offer = label.pendingSigningOffers.find { it.id == offerId } ?: return@updateState state
            if (label.companyCash < offer.requestedSigningBonus) {
                showToast("Insufficient corporate cash to pay signing bonus of $${String.format("%,.0f", offer.requestedSigningBonus)}!")
                return@updateState state
            }
            if (label.signedNPCIds.contains(offer.artistId)) {
                showToast("Artist is already signed!")
                return@updateState state
            }

            val updatedLabel = label.copy(
                companyCash = label.companyCash - offer.requestedSigningBonus,
                signedNPCIds = label.signedNPCIds + offer.artistId,
                pendingSigningOffers = label.pendingSigningOffers.filter { it.id != offerId }
            )
            val updatedNPCs = state.npcs.map { n ->
                if (n.id == offer.artistId) {
                    n.copy(
                        currentRecordLabel = label.name,
                        relationshipWithPlayer = (n.relationshipWithPlayer + 20f).coerceAtMost(100f)
                    )
                } else n
            }
            showToast("Signed ${offer.artistName} to your label roster!")
            state.copy(ownLabel = updatedLabel, npcs = updatedNPCs)
        }
    }

    fun rejectArtistSigningOffer(offerId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val updatedLabel = label.copy(
                pendingSigningOffers = label.pendingSigningOffers.filter { it.id != offerId }
            )
            showToast("Signing proposal declined.")
            state.copy(ownLabel = updatedLabel)
        }
    }

    fun socializeWithCoFounder(coFounderId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val cf = label.coFounders.find { it.id == coFounderId } ?: return@updateState state

            val energyCost = 15
            val cashCost = 500.0
            if (state.energy < energyCost) {
                showToast("Too exhausted to socialize!")
                return@updateState state
            }
            if (state.cash < cashCost) {
                showToast("Insufficient personal funds to host dinner!")
                return@updateState state
            }

            val updatedCoFounders = label.coFounders.map { item ->
                if (item.id == coFounderId) {
                    item.copy(relationship = (item.relationship + 12f).coerceAtMost(100f))
                } else item
            }
            showToast("Took co-founder ${cf.name} out for a luxury business dinner. Relationship improved!")
            state.copy(
                energy = state.energy - energyCost,
                cash = state.cash - cashCost,
                ownLabel = label.copy(coFounders = updatedCoFounders)
            )
        }
    }

    fun socializeWithBoardMember(boardId: String) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            val bm = label.boardMembers.find { it.id == boardId } ?: return@updateState state

            val energyCost = 20
            val cashCost = 1200.0
            if (state.energy < energyCost) {
                showToast("Too exhausted to socialize!")
                return@updateState state
            }
            if (state.cash < cashCost) {
                showToast("Insufficient personal cash to host golf event!")
                return@updateState state
            }

            val updatedBoard = label.boardMembers.map { item ->
                if (item.id == boardId) {
                    item.copy(satisfaction = (item.satisfaction + 15f).coerceAtMost(100f))
                } else item
            }
            showToast("Hosted executive golf retreat for board member ${bm.name}. Satisfaction surged!")
            state.copy(
                energy = state.energy - energyCost,
                cash = state.cash - cashCost,
                ownLabel = label.copy(
                    boardMembers = updatedBoard,
                    boardSatisfaction = (label.boardSatisfaction + 3f).coerceAtMost(100f)
                )
            )
        }
    }

    // 9. Marketplace Purchases
    fun buyCar(carId: String) {
        updateState { state ->
            val car = GameData.CARS.find { it.id == carId } ?: return@updateState state
            if (state.cash < car.cost) {
                showToast("Insufficient cash!")
                return@updateState state
            }
            if (state.ownedCars.any { it.id == carId }) {
                showToast("You already own this car!")
                return@updateState state
            }

            showToast("Purchased ${car.name}!")
            state.copy(
                cash = state.cash - car.cost,
                ownedCars = state.ownedCars + car
            )
        }
    }

    fun buyHouse(houseId: String) {
        updateState { state ->
            val house = GameData.HOUSES.find { it.id == houseId } ?: return@updateState state
            if (state.cash < house.cost) {
                showToast("Insufficient cash!")
                return@updateState state
            }
            if (state.ownedHouses.any { it.id == houseId }) {
                showToast("You already own this house!")
                return@updateState state
            }

            val newMaxEnergy = state.maxEnergy + house.energyBonus

            showToast("Purchased ${house.name}! Max energy increased by +${house.energyBonus}!")
            state.copy(
                cash = state.cash - house.cost,
                ownedHouses = state.ownedHouses + house,
                maxEnergy = newMaxEnergy
            )
        }
    }

    fun buyInstrument(instrumentId: String) {
        updateState { state ->
            val instrument = GameData.INSTRUMENTS.find { it.id == instrumentId } ?: return@updateState state
            if (state.cash < instrument.cost) {
                showToast("Insufficient cash!")
                return@updateState state
            }
            if (state.ownedInstruments.any { it.id == instrumentId }) {
                showToast("You already own this instrument!")
                return@updateState state
            }

            showToast("Purchased ${instrument.name}! Production ratings boosted by +${instrument.ratingBonus}⭐!")
            state.copy(
                cash = state.cash - instrument.cost,
                ownedInstruments = state.ownedInstruments + instrument
            )
        }
    }

    fun buyStreamingLicense() {
        updateState { state ->
            if (state.cash < 25000.0) {
                showToast("Insufficient cash!")
                return@updateState state
            }
            if (state.hasGlobalStreamingLicense) {
                showToast("You already own the copyright license!")
                return@updateState state
            }

            showToast("Purchased Copyright Sync License! Brand promotion deals will now begin reaching out in your Mailbox!")
            state.copy(
                cash = state.cash - 25000.0,
                hasGlobalStreamingLicense = true
            )
        }
    }

    fun buyLuxuryItem(itemId: String) {
        updateState { state ->
            val item = GameData.LUXURY_ITEMS.find { it.id == itemId } ?: return@updateState state
            val journalKey = "luxury_item_$itemId"
            if (state.spiritualJournal.contains(journalKey)) {
                showToast("You have already purchased this elite upgrade!")
                return@updateState state
            }
            if (state.cash < item.cost) {
                showToast("Insufficient cash to acquire this elite upgrade!")
                return@updateState state
            }

            var newVoice = state.voiceSkill
            var newWriting = state.writingSkill
            var newMaxEnergy = state.maxEnergy
            var newBurnout = state.burnout
            var newStress = state.stress
            var newPublicImage = state.publicImage
            var newNegativeSentiment = state.negativeSentiment
            var newIndustryRespect = state.industryRespect
            var newPopularity = state.popularity
            var newHumility = state.humility

            val toastMsg = when (itemId) {
                "luxury_vocal_coach" -> {
                    newVoice = (newVoice + 15f).coerceAtMost(100f)
                    newStress = (newStress - 40f).coerceAtLeast(0f)
                    "Recruited elite vocal coach! Vocal Skill +15, Stress -40%."
                }
                "luxury_writing_camp" -> {
                    newWriting = (newWriting + 20f).coerceAtMost(100f)
                    "Joined Grammy-winning songwriting camp! Writing Skill +20."
                }
                "luxury_pr_defense" -> {
                    newPublicImage = (newPublicImage + 45f).coerceAtMost(100f)
                    newNegativeSentiment = (newNegativeSentiment - 30f).coerceAtLeast(0f)
                    "Launched VIP PR Defense! Public Image +45, Negative Sentiment -30%."
                }
                "luxury_media_syndicate" -> {
                    newIndustryRespect = (newIndustryRespect + 25f).coerceAtMost(100f)
                    "Acquired media syndicate shares! Industry Respect +25, listener growth boosted permanently."
                }
                "luxury_wellness_bio" -> {
                    newMaxEnergy = state.maxEnergy + 25
                    newBurnout = 0f
                    "Installed Wellness Biohacking Chamber! Max Energy permanently increased by +25, all Burnout cleared!"
                }
                "luxury_superyacht" -> {
                    newPopularity = (newPopularity + 25f).coerceAtMost(100f)
                    newHumility = (newHumility - 25f).coerceAtLeast(0f)
                    "Purchased Bespoke Superyacht! Popularity +25%, Humility reduced by 25% (Prestige surge!)."
                }
                "luxury_festival_owner" -> {
                    newIndustryRespect = (newIndustryRespect + 40f).coerceAtMost(100f)
                    "Co-Bought Global Music Festival! Industry Respect +40, and you will now receive a weekly $85,000 dividend payout."
                }
                "luxury_security_detail" -> {
                    "Recruited elite protective security detail! Negative PR scandal impacts reduced by 80%."
                }
                "luxury_web3_metaverse" -> {
                    "Launched NFT & Web3 Virtual Studio! +$18,000 weekly dividend established."
                }
                "luxury_catalog_trust" -> {
                    "Established Catalog Preservation Trust! Global mechanical and publishing collections doubled permanently."
                }
                "luxury_charity_foundation" -> {
                    newIndustryRespect = (newIndustryRespect + 60f).coerceAtMost(100f)
                    newPublicImage = (newPublicImage + 50f).coerceAtMost(100f)
                    "Endowed Charitable Arts & Education Foundation! Respect +60, Public Image +50."
                }
                "luxury_private_jet" -> {
                    newPopularity = (newPopularity + 50f).coerceAtMost(100f)
                    "Acquired Gulfstream G800 Elite Private Jet! Popularity +50, travel stress eliminated."
                }
                "luxury_netflix_biopic" -> {
                    "Produced cinematic Sundance & Netflix biopic! Millions of new fans streaming your historic catalog."
                }
                else -> "Acquired luxury upgrade!"
            }

            val addedFans = if (itemId == "luxury_netflix_biopic") 1000000L else 0L

            showToast("✨ ELITE PURCHASE SUCCESSFUL: $toastMsg")
            state.copy(
                cash = state.cash - item.cost,
                fans = state.fans + addedFans,
                voiceSkill = newVoice,
                writingSkill = newWriting,
                maxEnergy = newMaxEnergy,
                burnout = newBurnout,
                stress = newStress,
                publicImage = newPublicImage,
                industryRespect = newIndustryRespect,
                popularity = newPopularity,
                humility = newHumility,
                sentiment = state.sentiment.copy(negativeSentiment = newNegativeSentiment),
                spiritual = state.spiritual.copy(spiritualJournal = state.spiritualJournal + journalKey)
            )
        }
    }

    // 10. Admin Purchase Request System
    fun submitPurchaseRequest(type: String, amount: Int) {
        updateState { state ->
            val newRequest = PurchaseRequest(
                id = UUID.randomUUID().toString(),
                playerEmail = state.email,
                playerName = state.artistName,
                type = type,
                amount = amount,
                status = "PENDING"
            )

            showToast("Request submitted for Admin approval!")
            state.copy(
                purchaseRequests = state.purchaseRequests + newRequest
            )
        }
    }

    // Admin function
    fun adminApproveRequest(playerEmail: String, requestId: String) {
        viewModelScope.launch {
            stateWriteMutex.withLock {
                val playerState = dao.getGameStateDirect(playerEmail) ?: return@withLock
                val req = playerState.purchaseRequests.find { it.id == requestId } ?: return@withLock
                if (req.status == "APPROVED") return@withLock

                val updatedRequests = playerState.purchaseRequests.map { request ->
                    if (request.id == requestId) request.copy(status = "APPROVED") else request
                }
                val (newCash, newMaxSlots, newMaxEnergy) = when (req.type) {
                    "CASH" -> Triple(playerState.cash + req.amount, playerState.maxTimeSlots, playerState.maxEnergy)
                    "TIME_SLOT" -> Triple(playerState.cash, playerState.maxTimeSlots + req.amount, playerState.maxEnergy)
                    "ENERGY" -> Triple(playerState.cash, playerState.maxTimeSlots, playerState.maxEnergy + req.amount)
                    else -> Triple(playerState.cash, playerState.maxTimeSlots, playerState.maxEnergy)
                }
                dao.insertGameState(
                    playerState.copy(
                        cash = newCash,
                        maxTimeSlots = newMaxSlots,
                        maxEnergy = newMaxEnergy,
                        purchaseRequests = updatedRequests
                    )
                )
                showToast("Approved request for $playerEmail!")
            }
        }
    }

    fun adminRejectRequest(playerEmail: String, requestId: String) {
        viewModelScope.launch {
            stateWriteMutex.withLock {
                val playerState = dao.getGameStateDirect(playerEmail) ?: return@withLock
                val req = playerState.purchaseRequests.find { it.id == requestId } ?: return@withLock
                if (req.status == "APPROVED" || req.status == "REJECTED") return@withLock
                val updatedRequests = playerState.purchaseRequests.map { request ->
                    if (request.id == requestId) request.copy(status = "REJECTED") else request
                }
                dao.insertGameState(playerState.copy(purchaseRequests = updatedRequests))
                showToast("Rejected request for $playerEmail!")
            }
        }
    }

    // Restart game
    fun restartGame() {
        updateState { state ->
            val cleanNpcs = GameData.DEFAULT_NPCS.map { npc ->
                npc.copy(
                    relationshipWithPlayer = 50f,
                    isFollowedByPlayer = false
                )
            }
            state.copy(
                cash = 10000.0,
                fans = 0L,
                popularity = 5f,
                influence = 5f,
                voiceSkill = 10f,
                writingSkill = 10f,
                businessSkill = 10f,
                energy = 100,
                maxEnergy = 100,
                timeSlots = 10,
                maxTimeSlots = 10,
                currentWeek = 1,
                currentYear = 1,
                practiceMissedWeeks = 0,
                endorsedCandidateId = null,
                politicalTaxModifier = 0.0f,
                cityProjectWeeksRemaining = 0,
                ownedCars = emptyList(),
                ownedHouses = emptyList(),
                ownedInstruments = emptyList(),
                capitalClubMembers = getInitialCapitalClubMembers(),
                songs = emptyList(),
                tours = emptyList(),
                sideJobs = GameData.SIDE_JOBS,
                stocks = GameData.STOCKS,
                cryptos = GameData.CRYPTOS,
                ownLabel = null,
                signedLabelId = null,
                signedLabelCut = 0.0f,
                industryRespect = 10f,
                publicImage = 50f,
                criticalAcclaim = 10f,
                trustworthiness = 50f,
                legacyPoints = 0L,
                businessReputation = 50f,
                discipline = 50f,
                integrity = 50f,
                generosity = 50f,
                humility = 50f,
                reliability = 50f,
                stress = 20f,
                burnout = 0f,
                creativeInspiration = 100f,
                motivation = 80f,
                happiness = 75f,
                workLifeBalance = 50f,
                genreTrends = GameData.GENRES.associate { genre ->
                    genre.id to when (genre.id) {
                        "genre_hiphop" -> GenreTrendSimulator.activeMultiplier
                        "genre_rock" -> GenreTrendSimulator.coolingMultiplier
                        else -> 1.0f
                    }
                },
                genreTrendWeeksRemaining = 4,
                genreTrendDurationWeeks = 4,
                timelineEvents = emptyList(),
                ownedBusinesses = emptyList(),
                activeGlobalEvent = null,
                globalEventEffect = null,
                meTubeVideos = emptyList(),
                meTubeSubscribers = 120L,
                meTubeWatchTime = 45.0,
                meTubeRevenue = 0.0,
                activeBrandDealName = null,
                brandDealWeeksRemaining = 0,
                brandDealWeeklyPayout = 0.0,
                brandDealRequiredEngagement = 0L,
                brandDealPostedThisWeek = false,
                npcs = cleanNpcs,
                chatConversations = getInitialChatConversations(),
                activeFamilyReqNpcId = null,
                castPosts = GameData.INITIAL_CAST_POSTS,
                castTrendingHashtags = listOf("#NewMusicFriday", "#StudioGrind", "#IndieWave", "#SummerVibes"),
                castTimeSpentThisWeek = 0,
                castPostsCountThisWeek = 0,
                castNicheFocus = "Music & Art",
                hiredEmployees = emptyList(),
                candidateRoster = emptyList(),
                pendingStockOrders = emptyList(),
                pendingCryptoOrders = emptyList(),
                notificationLogs = emptyList(),
                insuredCars = emptyList(),
                insuredHouses = emptyList(),
                adCompanyBudget = 0.0,
                activeAdCampaigns = emptyList(),
                employeeEvents = emptyList(),
                activeBrandDeals = emptyList(),
                newsArticles = emptyList(),
                weatherCondition = "Sunny",
                activeNoteText = "",
                savedPhotos = emptyList(),
                worldHistory = emptyList(),
                cryptoAutoInvestEnabled = false,
                cryptoAutoInvestPercent = 0.10f,
                cryptoAutoSellProfitMargin = 0.25f,
                cryptoStopLossMargin = 0.20f,
                accountantAutoInvestEnabled = false,
                accountantAutoInvestPercent = 0.10f,
                accountantAutoSellProfitMargin = 0.25f,
                accountantStopLossMargin = 0.20f,
                webHub = WebHubState(
                    wgtApplied = false,
                    wgtAuditionWeek = 0,
                    wgtAuditionYear = 0,
                    wgtCompletedCount = 0,
                    wgtWon = false,
                    isSccWanted = false,
                    sccCrimeReason = "",
                    sccFineAmount = 45000.0,
                    blackHatPurchasesCount = 0,
                    electionVotedCandidateId = null,
                    electionVotedWeek = 0,
                    electionVotedYear = 0,
                    grandAwardNominated = false,
                    grandAwardWon = false,
                    grandAwardVipAccess = false,
                    lastDemoSubmissionWeek = 0,
                    labelContractObligations = ""
                ),
                mailbox = listOf(
                    MailMessage(
                        id = "restart_msg",
                        sender = "Artist Guide",
                        subject = "Welcome to Your Fresh Career",
                        body = "Your career has been fully reset. Start creating songs, releasing music, networking with peers, and managing your financial future!",
                        type = "NORMAL",
                        expiresWeek = 5,
                        expiresYear = 1
                    )
                ),
                banking = BankingState(consecutiveUnpaidTaxWeeks = 0, consecutiveUnpaidTitheWeeks = 0, taxRate = 0.12f, savingsBalance = 0.0, loanAmount = 0.0, accumulatedLoanInterest = 0.0, taxDebt = 0.0, titheDebt = 0.0, mortgageAmount = 0.0),
                fanSegments = FanSegmentsState(casualFans = 0L, hardcoreFans = 0L, collectors = 0L, concertGoers = 0L, internationalFans = 0L, onlineCommunity = 0L, fansPersonalityLovers = 100L, fansLyricsLovers = 100L, fansVoicePurists = 100L, fansFashionistas = 100L, fansControversySeekers = 100L, fansBusinessAdmirers = 100L, fansCharityBelievers = 100L),
                sentiment = SocialSentimentState(followers = 100L, industryFollowers = 10L, mediaAttention = 10f, trendingScore = 10f, conversationVolume = 50L, positiveSentiment = 50f, negativeSentiment = 20f, neutralSentiment = 30f),
                spiritual = SpiritualState(spiritualPrayerSetting = true, spiritualTitheSetting = true, spiritualTotalTithesPaid = 0.0, spiritualTitheReminderActive = false),
                legacy = LegacyMilestones(hasWikipediaPage = false, hasHallOfFame = false, hasMuseum = false, hasDocumentary = false, hasBiography = false, hasNetflixSeries = false, hasScholarships = false, artistsInspiredCount = 0, receivedGovernorPitch = false, receivedSenatorPitch = false),
                security = SecurityCyberState(hiredSecurityCompany = "None", hasCyberInsurance = false, hasStudioInsurance = false)
            )
        }
        _weeklyReport.value = "Your life has been restarted."
    }

    data class WeeklyToursSimResult(
        val updatedTours: List<Tour>,
        val tourPayout: Double,
        val completedToursCount: Int
    )

    private fun simulateWeeklyTours(
        state: GameStateEntity,
        economyCycle: String,
        eventEffect: String,
        weatherCondition: String,
        reportBuilder: StringBuilder
    ): WeeklyToursSimResult {
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

                    val regionalAudiencePool = when (tour.tourType) {
                        "Club Run" -> (state.fans * 0.15).toInt().coerceAtLeast(180)
                        "City Tour" -> (state.fans * 0.08).toInt().coerceAtLeast(350)
                        "National Tour" -> (state.fans * 0.035).toInt().coerceAtLeast(500)
                        "Global Tour" -> (state.fans * 0.018).toInt().coerceAtLeast(800)
                        else -> (state.fans * 0.05).toInt().coerceAtLeast(250)
                    }
                    val effectiveVenueCap = minOf(baseMaxAtt, regionalAudiencePool)

                    val energyPerformanceBonus = when {
                        state.energy >= 70 -> 1.15f
                        state.energy >= 40 -> 1.0f
                        else -> 0.75f
                    }

                    val seasonMult = when (state.currentWeek) {
                        in 20..35 -> if (tour.level == "Premium" || tour.tourType == "Global Tour") 1.25f else 1.0f
                        in 48..52, in 1..5 -> if (tour.level == "Premium" || tour.tourType == "Global Tour") 0.75f else if (tour.tourType == "City Tour") 1.15f else 0.85f
                        else -> 1.0f
                    }

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

                    val economyTourMult = when (economyCycle) {
                        "Boom" -> 1.4f
                        "Recession" -> 0.65f
                        "Depression" -> 0.45f
                        else -> 1.0f
                    }

                    val eventTourMult = when (eventEffect) {
                        "LOCKDOWN" -> 0.0f
                        "RECESSION" -> 0.7f
                        "FESTIVAL" -> 1.4f
                        else -> 1.0f
                    }

                    val weatherTourMult = when (weatherCondition) {
                        "Rainy" -> 0.85f
                        "Heavy Storm" -> 0.55f
                        else -> 1.0f
                    }

                    val collisionRand = Random.nextFloat()
                    val hasCollision = collisionRand < 0.12f
                    val collisionMult = if (hasCollision && eventEffect != "LOCKDOWN") {
                        reportBuilder.append("⚠ CONCERT COLLISION: A rival superstar held an unannounced festival in your tour market this week! Divided local media coverage.\n")
                        0.65f
                    } else 1.0f

                    val reputationTourMult = when {
                        state.publicImage < 40f -> 0.55f
                        state.publicImage > 75f -> 1.35f
                        else -> 1.0f
                    }

                    val attVal = (effectiveVenueCap * (popularityFactor * 0.5 + influenceFactor * 0.3 + promoEffect * 0.2) *
                            socialPromoBonus * newsPromoBonus * eventTourMult * economyTourMult * weatherTourMult *
                            collisionMult * reputationTourMult * priceMultiplier * seasonMult * hypeMult *
                            audioEngineerBonus * openingActBonus * closingActBonus * energyPerformanceBonus).toInt()
                    val finalAttendance = attVal.coerceIn(0, baseMaxAtt)

                    val genTickets = (finalAttendance * 0.70).toInt()
                    val vipTickets = (finalAttendance * 0.25).toInt()
                    val platTickets = finalAttendance - genTickets - vipTickets

                    val effGenPrice = if (tour.generalTicketPrice > 0.0) tour.generalTicketPrice else tour.ticketPrice * 0.8
                    val effVipPrice = if (tour.vipTicketPrice > 0.0) tour.vipTicketPrice else tour.ticketPrice * 1.8
                    val effPlatPrice = if (tour.platinumTicketPrice > 0.0) tour.platinumTicketPrice else tour.ticketPrice * 3.5

                    val grossTourRevenue = (genTickets * effGenPrice) + (vipTickets * effVipPrice) + (platTickets * effPlatPrice)

                    val hasTourManager = tour.selectedCrewRoles.contains("Tour Manager")
                    val promoterCutRate = if (hasTourManager) 0.20f else (Random.nextFloat() * 0.12f + 0.28f)
                    val venueAndPromoterCut = grossTourRevenue * promoterCutRate

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
                        netTourEarnings = 0.0
                    }

                    var weeklyTourMerchRevenue = 0.0
                    var tourMerchUnitsSold = 0
                    if (tour.selectedMerchIds.isNotEmpty() && state.merchStyles.isNotEmpty()) {
                        val buyersCount = (finalAttendance * 0.22).toInt()
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
        return WeeklyToursSimResult(updatedTours, tourPayout, completedToursCount)
    }


    // --- WEEKLY TICK ENGINE ("NEXT WEEK") ---
    fun progressToNextWeek() {
        val currentState = gameState.value ?: return
        val isMandatoryElectionWeek = (currentState.currentWeek == 14 || currentState.currentWeek == 38 || currentState.isElectionWeek)
        if (isMandatoryElectionWeek && !currentState.hasVotedInCurrentElection) {
            showToast("🗳 MANDATORY VOTING: It is Election Week! You cannot advance to next week until you have voted. Visit omegaelectoralcommission.gov to register and vote.")
            return
        }

        updateState { state ->
            val reportBuilder = java.lang.StringBuilder()
            var newSavingsBalance = state.savingsBalance
            var updatedActivePhone = state.activePhone
            var updatedOwnedPhones = state.ownedPhones
            reportBuilder.append("=== WEEK ${state.currentWeek} SUMMARY (YEAR ${state.currentYear}) ===\n")
            val playerSongsWeeklyStreamsMap = mutableMapOf<String, Long>()

            // Initialize all mutable stats at the top for unified system integration
            var updatedCash = state.cash
            var totalStreamsPayout = 0.0
            var currentStress = state.stress
            var currentBurnout = state.burnout
            var currentInspiration = state.creativeInspiration
            var currentMotivation = state.motivation
            var currentHappiness = state.happiness
            var updatedInspiredCount = state.artistsInspiredCount
            var currentWorkLifeBalance = state.workLifeBalance

            var currentIndustryRespect = state.industryRespect
            var currentPublicImage = state.publicImage
            var currentCriticalAcclaim = state.criticalAcclaim
            var currentTrustworthiness = state.trustworthiness
            var currentLegacyPoints = state.legacyPoints
            var newTitheDebt = state.titheDebt

            var currentBusinessReputation = state.businessReputation
            var currentDiscipline = state.discipline
            var currentIntegrity = state.integrity
            var currentGenerosity = state.generosity
            var currentHumility = state.humility
            var currentReliability = state.reliability

            var updatedIsUnderArrest = state.isUnderArrest
            var updatedArrestSentenceWeeksRemaining = state.arrestSentenceWeeksRemaining
            var updatedArrestReason = state.arrestReason
            var updatedArrestBailAmount = state.arrestBailAmount
            var updatedArrestAttorneyHired = state.arrestAttorneyHired

            var nextIsElectionWeek = false
            var nextHasVotedInCurrentElection = false
            var nextTookEndorsementMoney = state.tookEndorsementMoney
            var nextEndorsementMoneyAmount = state.endorsementMoneyAmount
            var nextEndorsedPoliticianId = state.endorsedPoliticianId
            var nextEndorsedPoliticianName = state.endorsedPoliticianName
            var nextElectionWinnerId = state.currentElectionWinnerId
            var nextElectionWinnerName = state.currentElectionWinnerName

            // Serve detention sentence if currently under arrest
            if (state.isUnderArrest) {
                updatedArrestSentenceWeeksRemaining = (state.arrestSentenceWeeksRemaining - 1).coerceAtLeast(0)
                if (updatedArrestSentenceWeeksRemaining <= 0) {
                    updatedIsUnderArrest = false
                    updatedArrestReason = ""
                    updatedArrestBailAmount = 0.0
                    updatedArrestAttorneyHired = null
                    reportBuilder.append("⚖️ CORRECTIONAL RELEASE: Inmate ${state.artistName} has finished serving state detention. All civil injunctions vacated, all studio, tour, venture, and broadcast access restored!\n")
                } else {
                    currentInspiration = (currentInspiration + 3f).coerceAtMost(100f)
                    currentDiscipline = (currentDiscipline + 2f).coerceAtMost(100f)
                    reportBuilder.append("⚖️ SERVING DETENTION: You served 1 week in state correctional custody. $updatedArrestSentenceWeeksRemaining weeks remaining in your judicial sentence.\n")
                }
            }

            var updatedVoice = state.voiceSkill
            var updatedWriting = state.writingSkill
            var updatedInfluence = state.influence

            // Financial Intelligence & Wealth Engine local variables
            var updatedEmergencyFund = state.emergencyFundBalance
            var updatedPayrollReserve = state.payrollReserve
            var updatedBusinessReserve = state.businessReserve
            var updatedBondsBalance = state.bondsBalance

            // Ad Campaign promotional variables
            var updatedAdBudget = state.adCompanyBudget
            var updatedCampaignsList = state.activeAdCampaigns.toMutableList()
            var gainedFansFromPromo = 0L
            var updatedFollowers = state.followers
            var updatedNegativeSentiment = state.negativeSentiment
            val updatedMail = state.mailbox.toMutableList()
            var updatedTimelineEvents = state.timelineEvents
            val updatedSpiritualJournal = state.spiritualJournal.toMutableList()

            var mutCastPosts = state.castPosts.toMutableList()
            var trendingPromoBoost = 0f
            var publicImagePromoBoost = 0f
            var industryRespectPromoBoost = 0f
            var voicePromoBoost = 0f

            // 1. Check & increment week

            // OS Perks
            if (state.activePhone == "No-Thing") {
                updatedNegativeSentiment = (updatedNegativeSentiment - 1f).coerceAtLeast(0f)
            }

            var nextWeek = state.currentWeek + 1
            var nextYear = state.currentYear
            var triggerAwardsShow = false

            if (nextWeek > 52) {
                nextWeek = 1
                nextYear++
                triggerAwardsShow = true
            }

            // 1b. Manage Dynamic Economy & Weather
            var economyCycle = state.economyCycle
            var inflationRate = state.inflationRate
            var fuelPrice = state.fuelPrice
            var touristIndex = state.touristIndex
            var isElectionSeason = state.isElectionSeason

            // Organic Macro-Economic Cycle Progression (5% chance per week with natural phase progression)
            if (Random.nextFloat() < 0.05f) {
                val nextCycle = when (economyCycle) {
                    "Stable Growth" -> if (Random.nextFloat() < 0.65f) "Boom" else "Recession"
                    "Boom" -> if (Random.nextFloat() < 0.65f) "Inflation Peak" else "Stable Growth"
                    "Inflation Peak" -> if (Random.nextFloat() < 0.70f) "Recession" else "Stable Growth"
                    "Recession" -> if (Random.nextFloat() < 0.15f) "Depression" else "Stable Growth"
                    "Depression" -> if (Random.nextFloat() < 0.85f) "Recession" else "Stable Growth"
                    else -> "Stable Growth"
                }
                if (nextCycle != economyCycle) {
                    economyCycle = nextCycle
                    when (economyCycle) {
                        "Boom" -> {
                            inflationRate = 0.045f
                            fuelPrice = 1.65f
                            touristIndex = 135
                            reportBuilder.append("📈 ECONOMIC BOOM: Markets are expanding! Consumer optimism is high, corporate earnings are up, and fuel is $1.65/L.\n")
                        }
                        "Stable Growth" -> {
                            inflationRate = 0.02f
                            fuelPrice = 1.25f
                            touristIndex = 100
                            reportBuilder.append("🌍 ECONOMIC STABILITY: The economy is growing at a healthy, sustainable pace. Inflation is mild at 2.0%.\n")
                        }
                        "Inflation Peak" -> {
                            inflationRate = 0.075f
                            fuelPrice = 1.95f
                            touristIndex = 90
                            reportBuilder.append("🚨 INFLATION PEAK: High demand and supply constraints have elevated inflation to 7.5%. Central banks are tightening credit.\n")
                        }
                        "Recession" -> {
                            inflationRate = 0.01f
                            fuelPrice = 1.10f
                            touristIndex = 70
                            reportBuilder.append("📉 RECESSION: Economic activity is cooling. Valuations have compressed, creating value opportunities for patient investors. Gas is $1.10/L.\n")
                        }
                        "Depression" -> {
                            inflationRate = -0.005f
                            fuelPrice = 0.90f
                            touristIndex = 45
                            reportBuilder.append("💀 PROLONGED DOWNTURN: Severe economic cooling. Asset prices are heavily discounted near historical floors. Gas is $0.90/L.\n")
                        }
                    }
                }
            }

            // Election season is managed by the scheduled campaigns system below.

            // Update Weather Condition (Sunny, Rainy, Overcast, Heavy Storm, Heatwave)
            val weatherRand = Random.nextFloat()
            val weatherCondition = when {
                weatherRand < 0.50f -> "Sunny"
                weatherRand < 0.70f -> "Rainy"
                weatherRand < 0.85f -> "Overcast"
                weatherRand < 0.95f -> "Heavy Storm"
                else -> "Heatwave"
            }
            when (weatherCondition) {
                "Rainy" -> reportBuilder.append("🌧 WEATHER REPORT: Light rain throughout the region. Outdoor gig turnouts could face a slight -15% drag.\n")
                "Heavy Storm" -> reportBuilder.append("⚡ WEATHER REPORT: Torrential storms! Road travel is slow, outdoor gigs see -45% turnout, but people stayed home streaming (+25% MeTube views)!\n")
                "Heatwave" -> reportBuilder.append("☀ WEATHER REPORT: Extreme Heatwave alert! Higher exhaustion risk. Conserving energy is recommended.\n")
                else -> { /* Sunny / normal, no severe impact */ }
            }

            // Planner Activity Application (Calendar App Integration)
            var calendarBonusEnergy = 0
            var calendarBonusStress = 0.0f
            state.scheduledActivities.forEach { act ->
                when (act) {
                    "Rest & Meditation" -> {
                        calendarBonusEnergy += 25
                        calendarBonusStress -= 15.0f
                        reportBuilder.append("📅 Planner: Completed 'Rest & Meditation' session. Reclaimed +25 Energy, -15 Stress!\n")
                    }
                    "Vocal Practice" -> {
                        updatedVoice = (updatedVoice + 1.2f).coerceAtMost(100f)
                        reportBuilder.append("📅 Planner: Practice makes perfect! Vocal Practice added +1.2 Voice Skill.\n")
                    }
                    "Creative Writing" -> {
                        updatedWriting = (updatedWriting + 1.2f).coerceAtMost(100f)
                        reportBuilder.append("📅 Planner: Spent hours in notebook. Creative Writing added +1.2 Songwriting Skill.\n")
                    }
                    "Social PR Campaign" -> {
                        currentPublicImage = (currentPublicImage + 4f).coerceAtMost(100f)
                        currentInspiration = (currentInspiration + 5f).coerceAtMost(100f)
                        reportBuilder.append("📅 Planner: Orchestrated an online PR campaign. Raised Public Image +4%.\n")
                    }
                    "Spiritual Retreat" -> {
                        currentGenerosity = (currentGenerosity + 3f).coerceAtMost(100f)
                        currentHumility = (currentHumility + 4f).coerceAtMost(100f)
                        calendarBonusStress -= 20.0f
                        reportBuilder.append("📅 Planner: Attended a spiritual retreat. Lifted Generosity & Humility, melted stress away.\n")
                    }
                }
            }

            // 1b. Manage Global World Events
            var activeEvent = state.activeGlobalEvent
            var eventEffect = state.globalEventEffect

            if (activeEvent != null) {
                // 15% chance event resolves
                if (Random.nextFloat() > 0.85f) {
                    reportBuilder.append("🌍 Global Event Resolved: '${activeEvent}' has concluded. Market conditions normalized.\n")
                    activeEvent = null
                    eventEffect = null
                } else {
                    reportBuilder.append("🌍 Active Global Event: '${activeEvent}' continues to affect the music industry.\n")
                }
            } else {
                // 8% chance a new global event triggers
                if (Random.nextFloat() > 0.92f) {
                    val events = listOf(
                        "Economic Recession" to "RECESSION",
                        "Global Music Festival Boom" to "FESTIVAL",
                        "AI Songwriting Controversy" to "AI_DEBATE",
                        "Viral Social Challenge" to "VIRAL_CHALLENGE",
                        "Pandemic Lockdown" to "LOCKDOWN"
                    )
                    val selected = events.random()
                    activeEvent = selected.first
                    eventEffect = selected.second
                    reportBuilder.append("🌍 GLOBAL EVENT INITIATED: '${activeEvent}'! This will heavily impact market rates, stress levels, and income streams.\n")
                }
            }

            // 1c. Rotate the market-wide genre trend on a saved 3, 4, or 6 week schedule.
            val currentTrends = state.genreTrends.toMutableMap()
            GameData.GENRES.forEach { currentTrends.putIfAbsent(it.id, 1.0f) }
            val priorActiveGenreId = currentTrends.maxByOrNull { it.value }?.key
            val shouldRotateTrend = state.genreTrendWeeksRemaining <= 1 || state.genreTrendDurationWeeks <= 0
            val trendSeed = state.currentYear.toLong() * 10_000L + state.currentWeek * 31L + (priorActiveGenreId?.hashCode() ?: 0)
            val trendRandom = kotlin.random.Random(trendSeed)
            val nextTrendGenreId = if (shouldRotateTrend) {
                GameData.GENRES.map { it.id }.filter { it != priorActiveGenreId }.randomOrNull(trendRandom)
                    ?: GameData.GENRES.firstOrNull()?.id.orEmpty()
            } else priorActiveGenreId.orEmpty()
            val nextTrendDuration = if (shouldRotateTrend) GenreTrendSimulator.availableDurations.random(trendRandom) else state.genreTrendDurationWeeks
            val genreTrendUpdate = GenreTrendSimulator.advance(
                multipliers = currentTrends,
                weeksRemaining = state.genreTrendWeeksRemaining,
                durationWeeks = state.genreTrendDurationWeeks,
                genreIds = GameData.GENRES.map { it.id },
                nextGenreId = nextTrendGenreId,
                nextDurationWeeks = nextTrendDuration
            )
            currentTrends.clear()
            currentTrends.putAll(genreTrendUpdate.multipliers)
            if (genreTrendUpdate.rotated) {
                val genreName = GameData.GENRES.find { it.id == genreTrendUpdate.activeGenreId }?.name ?: "Unknown"
                reportBuilder.append("📈 Genre trend shift: $genreName is now leading listener demand for ${genreTrendUpdate.durationWeeks} weeks. Its discovery boost will fade when the next genre takes over.\n")
                priorActiveGenreId?.takeIf { it != genreTrendUpdate.activeGenreId }?.let { oldGenreId ->
                    val oldGenreName = GameData.GENRES.find { it.id == oldGenreId }?.name ?: oldGenreId
                    reportBuilder.append("📉 $oldGenreName's trend has cooled; expect less carryover discovery.\n")
                }
            }

            // 2. Resource & Time Deductions
            val sideJobCost = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
            val activeTours = state.tours.filter { it.status == "ACTIVE" }
            val tourSlotsCost = activeTours.sumOf { it.weeklyTimeCost }
            val totalUsedSlots = sideJobCost + tourSlotsCost

            val stressWasTreated = state.stressTreatedThisWeek
            var updatedStressUntreatedWeeks = state.stressUntreatedWeeks
            var updatedStressTreatedConsecutiveWeeks = state.stressTreatedConsecutiveWeeks

            if (stressWasTreated) {
                updatedStressTreatedConsecutiveWeeks += 1
                if (updatedStressTreatedConsecutiveWeeks >= 10) {
                    if (updatedStressUntreatedWeeks > 0) {
                        reportBuilder.append("🩺 FULL MEDICAL RECOVERY: After 10 consecutive weeks of active stress treatment, Dr. Martins has certified your full physiological recovery! Vitality restored to 100/100!\n")
                        updatedMail.add(
                            MailMessage(
                                id = "mail_dr_martins_recovery_" + java.util.UUID.randomUUID().toString().take(6),
                                sender = "Dr. Martins (Clinical Wellness Specialist)",
                                subject = "🩺 Official Medical Clearance: Full Vitality Restored (100/100)",
                                body = "Dear ${state.artistName},\n\nI have thoroughly reviewed your biometric and stress markers following 10 consecutive weeks of dedicated mental health management.\n\nYour chronic adrenal fatigue has completely reversed. Your cellular resilience is fully stabilized, and your baseline vitality has returned to 100/100 energy.\n\nContinue this admirable self-care routine.\n\nWarm regards,\nDr. Martins, M.D.\nSpecialist in Performing Arts Medicine",
                                type = "NORMAL",
                                expiresWeek = state.currentWeek + 4,
                                expiresYear = state.currentYear
                            )
                        )
                    }
                    updatedStressUntreatedWeeks = 0
                }
            } else {
                updatedStressTreatedConsecutiveWeeks = 0
                if (state.stress >= 30f) {
                    updatedStressUntreatedWeeks += 1
                }
            }

            // Energy reduction if untreated for weeks:
            // "if stress is not treated for like 6 month, the 24th week the energy will be 95/100 instead of 100/100, after one month if still untreated it becomes 90/100, so on, to get back the 100/100, player has to treat their stress every week for 10 weeks (email will be sent by Doctor Martins)."
            val energyPenalty = if (updatedStressUntreatedWeeks >= 24) {
                val monthsBeyond24 = (updatedStressUntreatedWeeks - 24) / 4
                (5 + monthsBeyond24 * 5).coerceAtMost(60)
            } else 0

            val baseCapacity = (100 - energyPenalty).coerceAtLeast(40)
            val houseEnergyBonus = state.ownedHouses.sumOf { it.energyBonus }
            val calculatedMaxEnergy = baseCapacity + houseEnergyBonus

            if (updatedStressUntreatedWeeks == 24 || (updatedStressUntreatedWeeks > 24 && (updatedStressUntreatedWeeks - 24) % 4 == 0 && energyPenalty <= 60)) {
                reportBuilder.append("⚠️ CHRONIC STRESS IMPACT: You have left your stress untreated for $updatedStressUntreatedWeeks consecutive weeks! Adrenal exhaustion has degraded your maximum weekly energy to $calculatedMaxEnergy/100.\n")
                updatedMail.add(
                    MailMessage(
                        id = "mail_dr_martins_warning_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = "Dr. Martins (Clinical Wellness Specialist)",
                        subject = "🩺 Urgent Health Notice: Chronic Stress Induced Fatigue ($calculatedMaxEnergy/100)",
                        body = "Dear ${state.artistName},\n\nOur clinical health monitoring indicates you have left your stress levels untreated for $updatedStressUntreatedWeeks consecutive weeks.\n\nChronic untreated stress has impaired your adrenal system and physiological vitality. Your maximum weekly energy has dropped to $calculatedMaxEnergy/100 and will continue to decay by 5 points every month if left unchecked.\n\nTo reverse this systemic fatigue and restore your 100/100 vitality, you must actively treat your stress every single week for 10 consecutive weeks.\n\nPlease take immediate action via the Wellness Dashboard.\n\nSincerely,\nDr. Martins, M.D.",
                        type = "NORMAL",
                        expiresWeek = state.currentWeek + 4,
                        expiresYear = state.currentYear
                    )
                )
            }

            val jobEnergyCost = state.sideJobs.filter { it.isActive }.sumOf { it.energyCost }

            // Tour Manager reduces energy costs of touring
            val hasTourManager = state.hiredEmployees.any { it.role == "Tour Manager" && it.loyalty > 25f }
            val tourEnergyDiscount = if (hasTourManager) 0.65f else 1.0f
            val tourEnergyCost = (activeTours.sumOf { it.weeklyEnergyCost } * tourEnergyDiscount).toInt()

            val totalEnergyDeducted = (jobEnergyCost + tourEnergyCost - calendarBonusEnergy).coerceAtLeast(0)

            val paRestBonus = if (state.hiredEmployees.any { it.role == "Personal Assistant" }) 15 else 0
            val houseRestBonus = state.ownedHouses.sumOf { (it.energyBonus * 1.5).toInt() }
            val carryoverFactor = (state.energy.toFloat() / calculatedMaxEnergy.coerceAtLeast(1).toFloat()).coerceIn(0.2f, 1.0f)
            val baseVitalityRecovery = (calculatedMaxEnergy * (0.60f + 0.40f * carryoverFactor)).toInt()
            val newEnergy = (baseVitalityRecovery + houseRestBonus + paRestBonus - totalEnergyDeducted).coerceIn(15, calculatedMaxEnergy)

            if (state.energy < 20) {
                reportBuilder.append("⚡ RESIDUAL FATIGUE: You ended last week with drained energy (<20%). You enter this week with sluggish vitality ($newEnergy/$calculatedMaxEnergy)!\n")
            } else if (state.energy >= 75) {
                reportBuilder.append("⚡ PEAK VITALITY: Great energy management last week! You enter this week feeling energized and sharp ($newEnergy/$calculatedMaxEnergy)!\n")
            }
            if (newEnergy <= 15 && totalEnergyDeducted > 0) {
                reportBuilder.append("⚠ EXHAUSTED: You worked past your limits. Extreme fatigue caused your voice and writing skills to decay by 3%!\n")
            }

            // 3. Rebuild Economy: Financial Pressure, Inflation, Maintenance and Taxes
            val economyInflationRate = 1.0f + (state.currentYear - 1) * 0.04f + inflationRate
            val baseCostOfLiving = 220.0 * economyInflationRate

            // Apply mortgage payment (1.5% of mortgage principal paid every week as payment)
            var mortgagePayment = 0.0
            var newMortgageAmount = state.mortgageAmount
            if (newMortgageAmount > 0.0) {
                mortgagePayment = (newMortgageAmount * 0.015) + (newMortgageAmount * 0.005) // Principal + interest
                newMortgageAmount = (newMortgageAmount - (newMortgageAmount * 0.015)).coerceAtLeast(0.0)
                reportBuilder.append("🏠 MORTGAGE PAYMENT: Paid $${String.format("%,.2f", mortgagePayment)} to Apex Bank (Remaining Mortgage: $${String.format("%,.2f", newMortgageAmount)})\n")
            }

            val carMaintenance = state.ownedCars.sumOf {
                val isInsured = state.insuredCars.contains(it.id)
                val rate = if (isInsured) 0.012 else 0.008 // premium covers eventualities but has slightly higher weekly admin
                it.cost * rate
            }
            val isAegis = state.hiredSecurityCompany == "Aegis Elite Command"
            val isVanguard = state.hiredSecurityCompany == "Vanguard Tactical"
            val insDiscount = if (isAegis) 0.70 else if (isVanguard) 0.85 else 1.0

            val houseMaintenance = state.ownedHouses.sumOf {
                val isInsured = state.insuredHouses.contains(it.id)
                val rate = if (isInsured) 0.009 * insDiscount else 0.005
                it.cost * rate
            }
            val totalMaintenance = carMaintenance + houseMaintenance

            // Subsystem Weekly Costs
            val securityCompanyWeeklyCost = if (isAegis) 15000.0 else if (isVanguard) 5000.0 else 0.0
            val scholarshipWeeklyCost = if (state.hasScholarships) 7500.0 else 0.0
            val mansionPropertyTax = if (state.ownedHouses.any { it.id == "house_mansion" }) 1500.0 else 0.0

            // Employee Payroll Processing
            val totalPayroll = state.hiredEmployees.sumOf { it.weeklySalary }

            val totalWeeklyOutflow = baseCostOfLiving + totalMaintenance + mortgagePayment + securityCompanyWeeklyCost + scholarshipWeeklyCost + mansionPropertyTax + totalPayroll

            updatedCash -= totalWeeklyOutflow
            reportBuilder.append("🏠 Weekly Expenses: Paid $${String.format("%,.2f", baseCostOfLiving)} basic living costs (reflecting Year ${state.currentYear} inflation).\n")
            if (totalPayroll > 0.0) {
                reportBuilder.append("💼 Payroll: Paid $${String.format("%,.2f", totalPayroll)} in weekly salaries to your ${state.hiredEmployees.size} staff members.\n")
            }
            if (totalMaintenance > 0.0) {
                reportBuilder.append("🔧 Asset Upkeep: Paid $${String.format("%,.2f", totalMaintenance)} for car/house maintenance.\n")
            }
            if (securityCompanyWeeklyCost > 0.0) {
                reportBuilder.append("🛡 Security Contract: Paid $${String.format("%,.0f", securityCompanyWeeklyCost)} for elite security company patrols.\n")
            }
            if (scholarshipWeeklyCost > 0.0) {
                reportBuilder.append("🎓 Scholarships: Contributed $${String.format("%,.0f", scholarshipWeeklyCost)} to your youth music foundation.\n")
            }
            if (mansionPropertyTax > 0.0) {
                reportBuilder.append("🏛 Property Tax: Paid $${String.format("%,.0f", mansionPropertyTax)} Sunset Hills estate property taxes.\n")
            }

            // Progressive Year-End Taxes
            var newTaxDebt = state.taxDebt
            if (nextWeek == 1) {
                val assetValue = state.ownedCars.sumOf { it.cost } + state.ownedHouses.sumOf { it.cost } + state.ownedInstruments.sumOf { it.cost }
                val baseTaxRate = (state.taxRate - (if (state.hasScholarships) 0.03f else 0.0f)).coerceAtLeast(0.01f)
                val taxRate = baseTaxRate.toDouble()
                val taxDue = (state.cash * taxRate) + (assetValue * 0.01) // Progressive tax + asset wealth tax
                newTaxDebt += taxDue
                val taxNote = if (state.hasScholarships) " (including a 3% tax write-off from your Scholarships Foundation!)" else ""
                reportBuilder.append("📉 TAX SEASON: Progressive tax assessment of Year ${state.currentYear} completed. Tax calculated: $${String.format("%,.2f", taxDue)}$taxNote. Settle this debt in the Banking app to avoid weekly 3% penalties!\n")
            }

            // 3b. Active Ad Campaign processing
            state.activeAdCampaigns.forEach { campaign ->
                val cost = campaign.weeklyBudget
                if (updatedAdBudget >= cost) {
                    updatedAdBudget -= cost
                    when (campaign.agencyId) {
                        "beatwave" -> {
                            gainedFansFromPromo += Random.nextLong(3200, 6400)
                            trendingPromoBoost += 1.8f
                            reportBuilder.append("📢 Marketing: BeatWave Media social ads campaign active (-$8,000 budget, boosting fans & trendscore)!\n")
                        }
                        "starvision" -> {
                            gainedFansFromPromo += Random.nextLong(11000, 24000)
                            publicImagePromoBoost += 3.5f
                            reportBuilder.append("📺 Marketing: StarVision TV campaign broadcast nationwide (-$22,000 budget, gained massive new fans & Public Image)!\n")
                        }
                        "urban" -> {
                            publicImagePromoBoost += 1.8f
                            industryRespectPromoBoost += 1.2f
                            reportBuilder.append("🏙 Marketing: UrbanBillboards campaign posted visual ads (-$6,500 budget, boosted brand loyalty & respect)!\n")
                        }
                        "soundspot" -> {
                            gainedFansFromPromo += Random.nextLong(1800, 4200)
                            trendingPromoBoost += 2.2f
                            voicePromoBoost += 1.0f
                            reportBuilder.append("📻 Marketing: SoundSpot Radio campaign rotated tracks (-$4,200 budget, boosted vocal exposure & fans)!\n")
                        }
                    }
                } else {
                    updatedCampaignsList.remove(campaign)
                    reportBuilder.append("⚠ Marketing Hold: Insufficient advertising budget to run '${campaign.agencyName}' campaign. Stopped.\n")
                }
            }

            // NEW SYSTEMS: Legacy Royalties Payout
            val biographyRoyalties = if (state.hasBiography) 1200.0 else 0.0
            val documentaryRoyalties = if (state.hasDocumentary) 4500.0 else 0.0
            val netflixRoyalties = if (state.hasNetflixSeries) 15000.0 else 0.0
            val totalLegacyRoyalties = biographyRoyalties + documentaryRoyalties + netflixRoyalties

            if (totalLegacyRoyalties > 0.0) {
                updatedCash += totalLegacyRoyalties
                reportBuilder.append("🎞 Legacy Royalties: Earned $${String.format("%,.2f", totalLegacyRoyalties)} from your Biography, Documentary, and Netflix series syndications!\n")
            }

            // NEW SYSTEMS: Artists Inspired by Player
            if (state.fans > 500000 && kotlin.random.Random.nextFloat() < 0.12f) {
                updatedInspiredCount++
                val newArtistName = listOf("Nova Dreamer", "Kid Indigo", "Lil Lyricist", "Luna Gold", "Yung Harmony", "Symphony Smith", "Astro Beats").random()
                reportBuilder.append("✨ Artist Inspired: Your phenomenal catalog inspired a new artist, $newArtistName, to start their career! They look up to you as an idol.\n")

                // Add an email from this inspired artist
                val inspireMailId = "inspire_${kotlin.random.Random.nextInt(10000, 99999)}"
                updatedMail.add(
                    MailMessage(
                        id = inspireMailId,
                        sender = "$newArtistName (Aspiring Artist)",
                        subject = "✨ Thank you for the inspiration!",
                        body = "Dear ${state.artistName},\n\nI just wanted to reach out and say that your music is the reason I started writing and performing. Your style, voice, and visual storytelling completely changed my perspective on art. I hope that one day I can be even half as successful and impactful as you are!\n\nDeepest respect,\n$newArtistName",
                        type = "NORMAL",
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
            }

            // NEW SYSTEMS: Security Crises & Threat Simulator
            var securityBreachedOccurred = false
            var threatPreemptedMessage = ""
            var lossRecoveredByInsurance = ""
            var hackerCryptoLossRatio = 1.0

            val isSpiritualGiant = state.spiritualPrayerSetting && state.titheDebt <= 10.0 && (currentIntegrity >= 60f || currentGenerosity >= 60f)

            val totalCryptoValue = state.cryptos.sumOf { it.quantity * it.currentPrice }
            val hasActiveCrypto = state.cryptos.any { it.quantity > 0.0 }
            val hasShieldContract = isAegis || isVanguard || state.hasCyberInsurance
            val activeSecurityStaff = state.hiredEmployees.find { it.role == "Security" && it.loyalty > 25f }

            // 1. Regular Crypto Hack Threats on Genesis:
            // "no one is trying to hack players crypto balance so that shield can defend so players won't see reason to use the app's feature"
            // "genesis is hackable but shield can help so as a security staff to recover coins"
            if (hasActiveCrypto && totalCryptoValue > 30.0 && kotlin.random.Random.nextFloat() < 0.28f) {
                if (hasShieldContract || activeSecurityStaff != null) {
                    val defender = if (isAegis) "Aegis Elite Cyber-Shield" else if (isVanguard) "Vanguard Tactical Shield" else if (activeSecurityStaff != null) "Security Officer ${activeSecurityStaff.name}" else "Cyber-Shield Defense"
                    reportBuilder.append("🛡️ SHIELD INTERCEPTION: $defender repelled a targeted cyber-attack on your Genesis Crypto Wallet! Your coins ($${String.format("%,.2f", totalCryptoValue)}) remain 100% secure.\n")
                    updatedMail.add(
                        MailMessage(
                            id = "sec_shield_" + java.util.UUID.randomUUID().toString().take(6),
                            sender = "$defender Team",
                            subject = "🛡️ Security Thwarted: Genesis Crypto Hack Intercepted",
                            body = "Your retained Shield security infrastructure intercepted a malicious zero-day drain targeting your Genesis crypto balance. Zero coins compromised.",
                            type = "SECURITY_ALERT",
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                } else {
                    val lostPercent = kotlin.random.Random.nextDouble(0.20, 0.35)
                    val lostAmount = (totalCryptoValue * lostPercent).coerceAtMost(totalCryptoValue)
                    hackerCryptoLossRatio = ((totalCryptoValue - lostAmount) / totalCryptoValue).coerceIn(0.0, 1.0)
                    updatedSpiritualJournal.add("genesis_crypto_stolen:$lostAmount")
                    reportBuilder.append("🚨 GENESIS CRYPTO HACK: Unprotected hot-wallet breached! Rogue hackers siphoned $${String.format("%,.2f", lostAmount)} in crypto tokens! Open the Shield app or engage Security staff to trace and recover your stolen coins!\n")
                    updatedMail.add(
                        MailMessage(
                            id = "hack_alert_" + java.util.UUID.randomUUID().toString().take(6),
                            sender = "Genesis Protocol Security",
                            subject = "🚨 URGENT: Unauthorized Crypto Withdrawal Detected",
                            body = "An unauthorized private key drain of $${String.format("%,.2f", lostAmount)} was detected on your Genesis wallet.\n\nYou can use the Shield app's Crypto Recovery module or engage a Security staff member to audit the ledger and recover your stolen assets.",
                            type = "SECURITY_ALERT",
                            expiresWeek = state.currentWeek + 3,
                            expiresYear = state.currentYear
                        )
                    )
                }
            }

            val eligibleThreats = mutableListOf("PHONE_THEFT", "STUDIO_ROBBERY", "DATA_LEAK")

            // Base check
            if (isSpiritualGiant && kotlin.random.Random.nextFloat() < 0.40f) {
                val threatType = eligibleThreats.random()
                threatPreemptedMessage = "🙏 Divine Shielding: A potential $threatType on your estate was completely dissolved by a miraculous turn of events! Your commitment to prayer, generous tithes, and high integrity acts as an invisible shield of grace against bad forces. You feel deeply inspired (+20 Inspiration, -15 Stress)!"
                currentInspiration = (currentInspiration + 20f).coerceAtMost(100f)
                currentStress = (currentStress - 15f).coerceAtLeast(0f)
            } else if (kotlin.random.Random.nextFloat() < 0.12f) { // 12% chance of a secondary security threat
                val threatType = eligibleThreats.random()

                // Security company defense check
                if (isAegis) {
                    threatPreemptedMessage = "🛡 Aegis Elite Command intercepted a sophisticated $threatType attempt on your assets! Elite cyber-operatives and tactical responders secured your property. Absolute safety guaranteed."
                } else if (isVanguard) {
                    if (kotlin.random.Random.nextFloat() < 0.90f) {
                        threatPreemptedMessage = "🛡 Vanguard Tactical patrols successfully thwarted a $threatType attempt on your assets! Incident logged and security tightened."
                    } else {
                        securityBreachedOccurred = true
                    }
                } else {
                    // One-man team check
                    val activeSecurity = state.hiredEmployees.find { it.role == "Security" && it.loyalty > 30f }
                    if (activeSecurity != null) {
                        val defenseChance = activeSecurity.intelligence / 120f + activeSecurity.experience / 150f
                        if (kotlin.random.Random.nextFloat() < defenseChance) {
                            threatPreemptedMessage = "🛡 Security Officer ${activeSecurity.name} intercepted a $threatType on your assets! Excellent response."
                        } else {
                            securityBreachedOccurred = true
                        }
                    } else {
                        securityBreachedOccurred = true
                    }
                }

                if (threatPreemptedMessage.isNotEmpty()) {
                    reportBuilder.append("$threatPreemptedMessage\n")
                } else if (securityBreachedOccurred) {
                // Breach consequence
                when (threatType) {
                        "PHONE_THEFT" -> {
                            val isUnhackablePhone = updatedActivePhone.equals("hiPhone", ignoreCase = true) || updatedActivePhone.equals("Samsong", ignoreCase = true)
                            if (isUnhackablePhone) {
                                reportBuilder.append("🔒 HARDWARE BIOMETRIC SECURITY: Attackers tried to remotely crack your $updatedActivePhone, but its unhackable hardware biometric enclave repelled the intrusion! Your device and accounts remain pristine.\n")
                            } else if (hasShieldContract || activeSecurityStaff != null) {
                                reportBuilder.append("🛡️ SHIELD ENDPOINT DEFENSE: Shield security protocols detected and blocked a phone breach attempt! Cryptographic keys were protected and account hijacking was prevented.\n")
                            } else {
                                updatedOwnedPhones = updatedOwnedPhones - updatedActivePhone
                                updatedActivePhone = "TECCNO"
                                if (!updatedOwnedPhones.contains("TECCNO")) updatedOwnedPhones = updatedOwnedPhones + "TECCNO"

                                currentPublicImage = (currentPublicImage - 15f).coerceIn(0f, 100f)
                                updatedFollowers = (updatedFollowers - 15000L).coerceAtLeast(1500L)
                                updatedNegativeSentiment = (updatedNegativeSentiment + 20f).coerceIn(0f, 100f)
                                updatedSpiritualJournal.add("phone_stolen_followers_lost:15000")
                                updatedSpiritualJournal.add("cast_account_hijacked:15000")
                                reportBuilder.append("📱 PHONE HACK & CAST HIJACK: Rogue actors cracked your smartphone (even though it was locked) and hijacked your Cast account! Lost -15,000 followers and rogue posts damaged your image! Your bank app was completely unhackable. Open the Shield app to recover your hijacked account!\n")
                            }
                        }
                        "STUDIO_ROBBERY" -> {
                            if (state.hasStudioInsurance) {
                                lossRecoveredByInsurance = "🎸 Studio-Guard Insurance fully reimbursed you with $15,000 for the stolen master tracks and equipment!"
                                reportBuilder.append("🎙 STUDIO ROBBERY: Thieves broke into your studio! BUT $lossRecoveredByInsurance\n")
                            } else {
                                if (state.ownedInstruments.isNotEmpty()) {
                                    val stolenInst = state.ownedInstruments.random()
                                    reportBuilder.append("🎙 STUDIO ROBBERY: Thieves broke into your studio and stole your precious '${stolenInst.name}'! (Stolen instrument removed from collection)\n")
                                    // We will remove it at the state copy phase
                                } else {
                                    updatedCash -= 8000.0
                                    reportBuilder.append("🎙 STUDIO ROBBERY: Thieves broke into your studio, stealing recording gear worth $8,000!\n")
                                }
                            }
                        }
                        "DATA_LEAK" -> {
                            currentPublicImage = (currentPublicImage - 15f).coerceIn(0f, 100f)
                            currentTrustworthiness = (currentTrustworthiness - 15f).coerceIn(0f, 100f)
                            updatedNegativeSentiment = (updatedNegativeSentiment + 20f).coerceIn(0f, 100f)
                            reportBuilder.append("📂 DATA LEAK: Confidential accounting ledgers and private chats were leaked online! Critics are questioning your professional standards.\n")
                        }
                    }
                }
            }

            // Accrue savings interest
            newSavingsBalance = newSavingsBalance // already declared above
            if (newSavingsBalance > 0.0) {
                val interestEarned = newSavingsBalance * state.savingsInterestRate
                newSavingsBalance += interestEarned
                reportBuilder.append("🏦 BANKING: Earned +$${String.format("%,.2f", interestEarned)} weekly interest on your Savings Account.\n")
            }

            // Accrue Government Bonds interest: 10% increase every month (4 weeks)
            if (updatedBondsBalance > 0.0 && nextWeek % 4 == 0) {
                val bondYield = updatedBondsBalance * 0.10
                updatedBondsBalance += bondYield
                reportBuilder.append("🏛️ SOVEREIGN BONDS: Earned +$${String.format("%,.2f", bondYield)} monthly yield (10%) on your Government Bonds deposit ($${String.format("%,.2f", updatedBondsBalance)} balance).\n")
            }

            // Structured Loan Contracts Processing (Personal & Business)
            // Rule: Percentage interest should ONLY begin climbing if the maturity deadline has been hit.
            // When not hit yet, player only owes the fixed interest rate calculated for the loan term duration.
            val updatedLoanContracts = mutableListOf<BankLoanContract>()
            var overduePersonalPenaltyAccrued = 0.0
            for (loan in state.activeLoanContracts) {
                if (loan.remainingBalance <= 0.0) continue
                var remWeeks = loan.weeksRemaining
                var isOverdue = loan.isOverdue
                var bal = loan.remainingBalance

                if (!isOverdue) {
                    remWeeks -= 1
                    if (remWeeks <= 0) {
                        isOverdue = true
                        remWeeks = 0
                        val targetLabel = loan.ventureName ?: "Personal Account"
                        reportBuilder.append("🚨 LOAN MATURITY DEADLINE REACHED: Your ${loan.durationLabel} loan for $targetLabel has reached its maturity deadline without full settlement ($${String.format("%,.0f", bal)} unpaid). Compounding 4% weekly delinquency penalty now applies!\n")
                    }
                } else {
                    // Deadline reached: compounding percentage delinquency interest now climbs on unpaid balance
                    val penalty = (bal * loan.weeklyLatePenaltyPercent).roundToInt().toDouble()
                    bal += penalty
                    if (loan.borrowerType == "PERSONAL") {
                        overduePersonalPenaltyAccrued += penalty
                    }
                    val targetLabel = loan.ventureName ?: "Personal Account"
                    reportBuilder.append("🚨 LOAN DELINQUENCY: +$${String.format("%,.0f", penalty)} late fee (4%) compounded on overdue ${loan.borrowerType} credit line for $targetLabel. Outstanding balance: $${String.format("%,.0f", bal)}.\n")
                }

                updatedLoanContracts.add(loan.copy(
                    weeksRemaining = remWeeks,
                    isOverdue = isOverdue,
                    remainingBalance = bal
                ))
            }

            // Sync personal loan amount with active contracts without pre-deadline compounding
            val activePersonalLoanTotal = updatedLoanContracts.filter { it.borrowerType == "PERSONAL" }.sumOf { it.remainingBalance }
            val newLoanAmount = if (state.activeLoanContracts.isNotEmpty()) activePersonalLoanTotal else state.loanAmount
            val newAccumulatedInterest = state.accumulatedLoanInterest + overduePersonalPenaltyAccrued

            // Unpaid tax penalties
            if (newTaxDebt > 0.0) {
                val lateFee = newTaxDebt * 0.03
                newTaxDebt += lateFee
                reportBuilder.append("⚠ TAX DELINQUENCY: Surtax penalty of 3% (+$${String.format("%,.2f", lateFee)}) added to your outstanding Tax Debt.\n")
            }

            // Life Hazards & Emergencies (12% weekly probability)
            if (Random.nextFloat() < 0.12f) {
                val lawyer = state.hiredEmployees.find { it.role == "Lawyer" }
                val hasLawyer = lawyer != null && lawyer.loyalty > 25f
                val incidents = listOf(
                    {
                        if (updatedEmergencyFund >= 1200.0) {
                            updatedEmergencyFund -= 1200.0
                            currentStress = (currentStress + 3f).coerceAtMost(100f)
                            currentHappiness = (currentHappiness - 3f).coerceAtLeast(10f)
                            reportBuilder.append("🚨 HEALTH RECOVERY: Faced a sudden medical issue. Covered $1,200 from your Emergency Fund. Having this reserve saved you from high stress!\n")
                        } else {
                            updatedCash -= 1200.0
                            currentStress = (currentStress + 18f).coerceAtMost(100f)
                            currentHappiness = (currentHappiness - 12f).coerceAtLeast(10f)
                            reportBuilder.append("🚨 EMERGENCY: Faced a sudden medical issue. Paid $1,200 in healthcare fees. Stress increased by +18% because you had no Emergency Fund buffer!\n")
                        }
                    },
                    {
                        val baseCost = 4000.0
                        val mitigation = if (hasLawyer) (0.5f + (lawyer!!.experience / 200f)).coerceIn(0.5f, 0.95f) else 0.0f
                        val actualCost = baseCost * (1f - mitigation)

                        if (updatedEmergencyFund >= actualCost) {
                            updatedEmergencyFund -= actualCost
                            currentTrustworthiness = (currentTrustworthiness - (3f * (1f - mitigation))).coerceAtLeast(10f)
                            currentPublicImage = (currentPublicImage - (2f * (1f - mitigation))).coerceAtLeast(10f)
                            currentStress = (currentStress + 3f).coerceAtMost(100f)
                            reportBuilder.append("⚖ DISPUTE RESOLVED: A musician asserted co-writing credits. Settle cost of $${String.format("%,.0f", actualCost)} paid from your Emergency Fund, protecting active cash.\n")
                        } else {
                            updatedCash -= actualCost
                            currentTrustworthiness = (currentTrustworthiness - (12f * (1f - mitigation))).coerceAtLeast(10f)
                            currentPublicImage = (currentPublicImage - (8f * (1f - mitigation))).coerceAtLeast(10f)
                            currentStress = (currentStress + 15f).coerceAtMost(100f)
                            if (hasLawyer) {
                                reportBuilder.append("⚖ DISPUTE RESOLVED: A musician asserted co-writing credits. Your Lawyer ${lawyer!!.name} intervened and settled for $${String.format("%,.0f", actualCost)} cash (Saved $${String.format("%,.0f", baseCost - actualCost)}!).\n")
                            } else {
                                reportBuilder.append("⚖ DISPUTE: A musician asserted co-writing credits. Paid $4,000 from active cash reserves. Stress increased due to lack of an Emergency Fund.\n")
                            }
                        }
                    },
                    {
                        if (updatedEmergencyFund >= 2500.0) {
                            updatedEmergencyFund -= 2500.0
                            currentInspiration = (currentInspiration - 3f).coerceAtLeast(10f)
                            reportBuilder.append("🎙 REPAIR BUFFER: A power surge damaged your studio monitors. Bought replacements for $2,500 from your Emergency Fund, avoiding active cash strain!\n")
                        } else {
                            updatedCash -= 2500.0
                            currentInspiration = (currentInspiration - 15f).coerceAtLeast(10f)
                            reportBuilder.append("🎙 STUDIO ACCIDENT: A power surge damaged your studio monitors. Bought replacements for $2,500 from active cash reserves. Inspiration took a heavy hit (-15%).\n")
                        }
                    },
                    {
                        currentPublicImage = (currentPublicImage + 15f).coerceAtMost(100f)
                        reportBuilder.append("🔥 VIRAL BOOST: A famous creator danced to your unreleased demo clip on social media! Public Image improved by +15%!\n")
                    },
                    {
                        val baseCost = 3500.0
                        val mitigation = if (hasLawyer) (0.6f + (lawyer!!.intelligence / 250f)).coerceIn(0.6f, 0.95f) else 0.0f
                        val actualCost = baseCost * (1f - mitigation)

                        if (updatedEmergencyFund >= actualCost) {
                            updatedEmergencyFund -= actualCost
                            reportBuilder.append("📋 COMPLIANCE SETTLED: Regulatory audit discovered minor business filing errors. Settled penalties of $${String.format("%,.0f", actualCost)} from your Emergency Fund.\n")
                        } else {
                            updatedCash -= actualCost
                            currentIndustryRespect = (currentIndustryRespect - (8f * (1f - mitigation))).coerceAtLeast(10f)
                            if (hasLawyer) {
                                reportBuilder.append("📋 COMPLIANCE COMPLETED: Regulatory check discovered minor filing errors. Paid $${String.format("%,.0f", actualCost)} fine from active cash (Your Lawyer ${lawyer!!.name} saved you $${String.format("%,.0f", baseCost - actualCost)}).\n")
                            } else {
                                reportBuilder.append("📋 COMPLIANCE: A regulatory check discovered minor business filing errors. Settled penalties of $3,500 from active cash.\n")
                            }
                        }
                    }
                )
                incidents.random().invoke()
            }

            // 3b. Side Job Income (Stable Contracted Pay)
            var weeklySalary = 0.0
            state.sideJobs.filter { it.isActive }.forEach { job ->
                // Contracted base pay is respected; minor tips/overtime variance (+/- 4%)
                val tipsMult = Random.nextDouble(0.97, 1.04)
                val eventImpact = if (eventEffect == "LOCKDOWN") 0.85 else 1.0 // mild venue restriction during lockdown
                val skillBonus = if (updatedVoice > 70f || updatedWriting > 70f) 1.08 else 1.00
                val adjustedPay = job.weeklyPay * tipsMult * eventImpact * skillBonus
                weeklySalary += adjustedPay
            }
            if (weeklySalary > 0.0) {
                val skillNote = if (updatedVoice > 70f || updatedWriting > 70f) " (includes performance tip & artistic bonus)" else ""
                reportBuilder.append("💼 Side Jobs: Earned $${String.format("%,.2f", weeklySalary)} from contracted gigs$skillNote.\n")
            }

            // 3b. Entertainment Empire Income (Owned Businesses)
            var businessRevenue = 0.0
            val bizCycleMult = when (economyCycle) {
                "Boom" -> 1.45
                "Stable Growth" -> 1.10
                "Inflation Peak" -> 0.80 // High raw material/energy overhead costs
                "Recession" -> 0.55
                "Depression" -> 0.30
                else -> 1.0
            }
            state.ownedBusinesses.forEach { biz ->
                businessRevenue += biz.weeklyProfit * bizCycleMult
            }
            if (businessRevenue > 0.0) {
                reportBuilder.append("🏢 Entertainment Empire: Your business portfolio generated $${String.format("%,.2f", businessRevenue)} in weekly profits (Macro multiplier: ${String.format("%.2f", bizCycleMult)}x due to $economyCycle cycle)!\n")
                weeklySalary += businessRevenue
            }

            // 3c. Luxury Dividends (Global Music Festival co-ownership payout +$85,000)
            if (state.spiritualJournal.contains("luxury_item_luxury_festival_owner")) {
                weeklySalary += 85000.0
                reportBuilder.append("🎡 Festival Co-Ownership: Received $85,000.00 in weekly profit dividends from Global Music Festival!\n")
            }

            // 3d. Legal Copyright Infringement Risk (only affects unregistered songs lacking sample clearance when integrity is low)
            val vulnerableInfringementSongs = state.songs.filter { it.isReleased && !it.isCopyrightRegistered && !it.hasSampleClearance }
            if (currentIntegrity < 40f && kotlin.random.Random.nextFloat() < 0.15f && vulnerableInfringementSongs.isNotEmpty()) {
                val offendingSong = vulnerableInfringementSongs.random()
                val hasLawyer = state.hiredEmployees.find { it.role == "Lawyer" }
                if (hasLawyer != null) {
                    val fee = 5000.0
                    updatedCash -= fee
                    reportBuilder.append("⚖ COPYRIGHT CLAIM DEFENDED: Plagiarism accusation filed against '${offendingSong.title}'! Your lawyer, ${hasLawyer.name}, handled negotiations expertly and settled out of court for a small fee of $${String.format("%,.0f", fee)} (Integrity restored +5).\n")
                    currentIntegrity = (currentIntegrity + 5f).coerceAtMost(100f)
                } else {
                    val penalty = (state.cash * 0.10).coerceIn(10000.0, 80000.0)
                    updatedCash -= penalty
                    currentPublicImage = (currentPublicImage - 20f).coerceAtLeast(0f)
                    reportBuilder.append("⚖ LAWSUIT LOST: Plagiarism lawsuit filed against '${offendingSong.title}'! With no lawyer on staff to defend you, you were found liable. Paid damages of $${String.format("%,.2f", penalty)} and lost 20 Public Image!\n")
                }
            }

            // 3e. Public Relations Crisis (based on Public Image < 30 or negativeSentiment > 50)
            if ((currentPublicImage < 30f || state.negativeSentiment > 50f) && kotlin.random.Random.nextFloat() < 0.15f) {
                if (state.spiritualJournal.contains("luxury_item_luxury_security_detail")) {
                    reportBuilder.append("🛡 SECURITY SYSTEM ACTIVE: Tabloids attempted to leak a negative scandal, but your Elite Personal Security & Cyber Defense detail intercepted the information and scrubbed it from the web before it could trend! Saved 100% of Public Image.\n")
                } else {
                    val hasPR = state.hiredEmployees.find { it.role == "PR Manager" }
                    if (hasPR != null) {
                        val prCost = 3000.0
                        updatedCash -= prCost
                        currentPublicImage = (currentPublicImage + 15f).coerceAtMost(100f)
                        reportBuilder.append("📺 PR CRISIS RESOLVED: A social media scandal exploded! Fortunately, your PR Manager, ${hasPR.name}, contained the crisis instantly. Spent $${String.format("%,.0f", prCost)} on a cleanup campaign, restoring Public Image (+15).\n")
                    } else {
                        currentPublicImage = (currentPublicImage - 15f).coerceAtLeast(0f)
                        reportBuilder.append("📺 PR CRISIS EXPLODED: A major tabloid scandal broke regarding your lifestyle! Lacking a PR Manager, the story spread unchecked. Lost 15 Public Image, and Stress increased by 20%!\n")
                        currentStress = (currentStress + 20f).coerceAtMost(100f)
                    }
                }
            }

            // 4. Advanced Music Discovery: Update Songs streams and platform payouts
            var totalMasterArtistRevenue = 0.0
            var totalPublishingArtistRevenue = 0.0
            var recoupableAdvanceBalance = state.recoupableAdvanceBalance
            var viralHitOccurred = false
            var viralHitTitle = ""

            // Distribution deal multiplier
            val distMult = when (state.activeDistributionDeal) {
                "EliteVibe" -> 1.25f
                "EmpireDistro" -> 1.60f
                else -> 1.0f
            }

            // Biopic hype multiplier
            val biopicMult = if (state.spiritualJournal.contains("luxury_item_luxury_netflix_biopic")) 1.5f else 1.0f

            // Sanitize catalog: Purge any erroneous brand endorsement or gala invitations added by past bug
            val cleanCatalogSongs = state.songs.filterNot { song ->
                song.title.contains("Brand Endorsement", ignoreCase = true) ||
                song.title.contains("Royal Command", ignoreCase = true) ||
                song.title.contains("Apex Bank", ignoreCase = true) ||
                song.title.contains("Titan Capital", ignoreCase = true) ||
                song.title.contains("Aura Tech", ignoreCase = true) ||
                song.title.contains("Valerius Fashion", ignoreCase = true) ||
                song.title.contains("Paramount Studios", ignoreCase = true) ||
                song.title.contains("The Royal Secretariat", ignoreCase = true)
            }

            val updatedSongs = cleanCatalogSongs.map { originalSong ->
                val song = if (!originalSong.isReleased) {
                    val weeklyCampaignLift = ReleaseCompetitionHelper.campaignHypeLift(originalSong.preReleaseCampaignPower, originalSong.initialHypeWeeksSet)
                    val grownHype = (originalSong.hype + weeklyCampaignLift).coerceIn(1f, 100f)
                    val releaseAdvance = SongReleaseLifecycle.advance(originalSong, nextWeek, nextYear)
                    val advancedSong = releaseAdvance.song.copy(hype = grownHype)
                    if (!releaseAdvance.releasedNow) {
                        if (weeklyCampaignLift > 0f) reportBuilder.append("📣 PRE-RELEASE: '${originalSong.title}' awareness rose by ${weeklyCampaignLift.toInt()} points from its campaign (${advancedSong.weeksUntilRelease} week(s) until release).\n")
                        return@map advancedSong
                    }

                    val campaignFans = (originalSong.preReleaseCampaignReach * 0.035).toLong().coerceAtLeast(0L)
                    gainedFansFromPromo += campaignFans
                    reportBuilder.append("🎉 RELEASE DAY: '${originalSong.title}' is out. Its ${grownHype.toInt()}% awareness reflects the pre-release campaign and audience response. ${if (campaignFans > 0) "The campaign converted about ${String.format("%,d", campaignFans)} new fans." else "Now its rating and listener response will shape its momentum."}\n")
                    advancedSong.copy(
                        preReleaseCampaignPower = 0f,
                        preReleaseCampaignReach = 0L
                    )
                } else originalSong

                if (song.isUploadedMap.isNotEmpty()) {
                    // Check for Copyright claim on Uncleared Sample (only if not registered and sample not cleared)
                    val lawsuitOccurred = !song.hasSampleClearance && !song.isCopyrightRegistered && Random.nextFloat() < 0.15f
                    if (lawsuitOccurred) {
                        updatedCash -= 5000.0
                        reportBuilder.append("🚨 COPYRIGHT LAWSUIT: A copyright infringement claim was filed against '${song.title}' due to an uncleared classic sample! Paid $5,000.00 in settlement fees, and streaming royalties for this track are frozen this week.\n")
                        song.copy(
                            weeksSinceRelease = song.weeksSinceRelease + 1
                        )
                    } else {
                        var songViralHitOccurred = false
                        var totalWeeklyStreamsForSong = 0L
                        var plagiarismReportedForSong = false

                        // Weekly Hype Evolution Engine based on rating, active promo, and release age
                        val currentHype = song.hype.coerceIn(1f, 100f)
                        val hasPromo = song.promoWeeksLeft > 0
                        val promoBoost = if (hasPromo) song.promoWeeklyBoost else 0f
                        val weeksSince = song.weeksSinceRelease + 1

                        val hypeDelta: Float = when {
                            weeksSince <= 4 -> {
                                if (song.rating >= 4.2f) {
                                    if (hasPromo) {
                                        // Outstanding quality + active promo: Hype builds up!
                                        ((song.rating - 4.0f) * 4.0f + promoBoost * 14.0f).coerceIn(1.5f, 4.5f)
                                    } else {
                                        // Outstanding quality, promo expired / no promo: natural cooling
                                        -((5.0f - song.rating) * 2.0f + 2.8f).coerceIn(2.5f, 4.5f)
                                    }
                                } else if (song.rating in 3.6f..4.19f) {
                                    if (hasPromo) {
                                        (promoBoost * 10.0f - 1.2f).coerceIn(-1.2f, 1.5f)
                                    } else {
                                        -((4.2f - song.rating) * 4.0f + 3.5f).coerceIn(3.5f, 6.5f)
                                    }
                                } else {
                                    // Mediocre / poor rating: drops steadily
                                    if (hasPromo) {
                                        -((3.6f - song.rating) * 4.0f + 3.5f).coerceIn(3.5f, 8.0f)
                                    } else {
                                        -((3.6f - song.rating) * 6.0f + 7.0f).coerceIn(7.0f, 16.0f)
                                    }
                                }
                            }
                            else -> {
                                // Natural fatigue over weeks: people gradually move on to other tracks
                                val fatigue = ((weeksSince - 4) * 0.9f).coerceAtMost(10f)
                                if (song.rating >= 4.2f) {
                                    if (hasPromo) (-1.5f - fatigue * 0.4f) else (-3.2f - fatigue * 0.8f)
                                } else {
                                    (-5.5f - fatigue * 1.2f)
                                }
                            }
                        }

                        // Music videos boost hype during their initial release window (first 4 weeks of video release)
                        val linkedMeTubeVideo = state.meTubeVideos.find { it.title.contains(song.title, ignoreCase = true) }
                        val videoAge = if (linkedMeTubeVideo != null) (state.currentYear - linkedMeTubeVideo.yearPublished) * 52 + (state.currentWeek - linkedMeTubeVideo.weekPublished) else 999
                        val videoViewsHypeBonus = if (linkedMeTubeVideo != null && linkedMeTubeVideo.views > 0 && videoAge in 0..4) {
                            (kotlin.math.log10((linkedMeTubeVideo.views + 10).toDouble()) * 0.35f).toFloat().coerceIn(0.5f, 2.0f)
                        } else 0f

                        val updatedHype = (currentHype + hypeDelta + videoViewsHypeBonus).coerceIn(1f, 100f)

                        // -------------------------------------------------------------------------
                        // BALANCED REALISTIC STREAMING & VIRAL SURGE ENGINE
                        // -------------------------------------------------------------------------
                        val featNpcObj = if (!song.featuredArtistName.isNullOrBlank()) {
                            state.npcs.find { it.name.equals(song.featuredArtistName, ignoreCase = true) || it.id == song.featuredArtistId }
                        } else null
                        val featPop = featNpcObj?.popularity ?: if (!song.featuredArtistName.isNullOrBlank()) 25f else 0f
                        val starPowerMult = if (!song.featuredArtistName.isNullOrBlank()) {
                            // Scale from guest's actual popularity: indie rookie (pop 18) = 1.16x, megastar (pop 90) = 1.90x
                            1.0 + (featPop / 100.0) * 0.90
                        } else 1.0

                        // Determine viral surge once per song per week under strict conditions
                        val eligibleForViral = song.rating >= 4.2f && currentHype >= 68f && weeksSince in 1..6
                        val viralChance = if (eligibleForViral) {
                            val base = 0.02f
                            val promoBonus = if (hasPromo) 0.02f else 0f
                            val starBonus = if (featPop > 75f) 0.015f else 0f
                            (base + promoBonus + starBonus).coerceAtMost(0.05f)
                        } else 0f

                        songViralHitOccurred = eligibleForViral && Random.nextFloat() < viralChance
                        val viralMultiplier = if (songViralHitOccurred) {
                            viralHitOccurred = true
                            viralHitTitle = song.title
                            val boostFactor = if (featPop > 75f) 2.2f else 1.8f
                            reportBuilder.append("🚀 VIRAL SURGE: '${song.title}' gained massive playlist traction across social feeds ($boostFactor x streaming surge)!\n")
                            boostFactor
                        } else {
                            1.0f
                        }

                        val trendMult = currentTrends[song.genreId] ?: 1.0f

                        val eventMult = when (eventEffect) {
                            "LOCKDOWN" -> 1.25f
                            "RECESSION" -> 0.85f
                            else -> 1.0f
                        }

                        val wordOfMouth = when {
                            song.rating >= 4.5f -> 1.4f
                            song.rating >= 4.0f -> 1.15f
                            song.rating < 2.5f -> 0.6f
                            else -> 1.0f
                        }

                        val sentimentFactor = (state.positiveSentiment - state.negativeSentiment) / 100f
                        val sentimentMult = 1f + (sentimentFactor * 0.20f)
                        val prestigeFactor = (1f + (currentIndustryRespect / 100f) * 0.20f + (currentPublicImage / 100f) * 0.15f) * sentimentMult

                        val lyricsLoversMod = 1f + (state.fansLyricsLovers / 1000000f).coerceAtMost(1.5f) * (state.writingSkill / 100f)
                        val voicePuristsMod = 1f + (state.fansVoicePurists / 1000000f).coerceAtMost(1.5f) * (state.voiceSkill / 100f)
                        val fashionistasMod = 1f + (state.fansFashionistas / 1000000f).coerceAtMost(1.5f) * (state.ownedCars.size * 0.10f + state.ownedHouses.size * 0.15f).coerceAtMost(0.8f)
                        val controversyMod = 1f + (state.fansControversySeekers / 1000000f).coerceAtMost(1.8f) * (state.negativeSentiment / 100f)
                        val businessMod = 1f + (state.fansBusinessAdmirers / 1000000f).coerceAtMost(1.5f) * (state.ownedBusinesses.size * 0.15f).coerceAtMost(0.8f)
                        val charityMod = 1f + (state.fansCharityBelievers / 1000000f).coerceAtMost(1.5f) * (if (state.hasScholarships) 0.3f else 0.1f)
                        val personalityMod = 1f + (state.fansPersonalityLovers / 1000000f).coerceAtMost(1.5f) * (state.happiness / 100f)
                        val fanPsychologyMult = (lyricsLoversMod + voicePuristsMod + fashionistasMod + controversyMod + businessMod + charityMod + personalityMod) / 7f

                        // Realistic active fan streams & casual discovery
                        val fanEngagement = (0.15 + (currentHype / 100.0) * 0.45).coerceIn(0.10, 0.65)
                        val activeFanStreams = state.fans * fanEngagement * (0.6 + (song.rating / 5.0) * 0.4)
                        val generalDiscovery = (state.popularity * 85.0 + state.influence * 45.0 + 250.0) * (currentHype / 100.0) * starPowerMult
                        val baseTotalWeeklyStreams = ((activeFanStreams * 0.40 + generalDiscovery * 0.60) * fanPsychologyMult).coerceAtLeast(15.0)

                        val updatedMap = song.isUploadedMap.mapValues { (platformId, currentStreams) ->
                            val qualityBoost = when (song.videoTier) {
                                "Low" -> 1.05f
                                "Standard" -> 1.15f
                                "High" -> 1.25f
                                "Premium" -> 1.40f
                                "Cinematic" -> 1.60f
                                else -> 1.0f
                            }

                            val promoMult = if (song.promoWeeksLeft > 0) (1f + song.promoWeeklyBoost) else 1f
                            val countryMult = if (song.isGlobal || state.hasGlobalStreamingLicense) 1.0f else 0.20f
                            val labelMarketingMult = if (state.ownLabel != null) 1f + (state.ownLabel.marketingLevel * 0.04f) else 1f

                            // Realistic market share split per platform (sums to 1.0)
                            val platformShare = when (platformId) {
                                "songify" -> 0.40f // Global leader
                                "megaphone" -> 0.25f // Major streamer
                                "jams" -> 0.20f // Urban & indie streamer
                                "sharp" -> 0.10f // Regional streamer
                                "tunes" -> 0.05f // Indie beginner upload portal
                                else -> 0.10f
                            }

                            val rawWeeklyStream = baseTotalWeeklyStreams * platformShare * qualityBoost * trendMult * eventMult * wordOfMouth * prestigeFactor * viralMultiplier * promoMult * countryMult * labelMarketingMult * distMult * biopicMult * (Random.nextFloat() * 0.15f + 0.92f)
                            val finalWeeklyStream = rawWeeklyStream.toLong().coerceAtLeast(1L)

                            totalWeeklyStreamsForSong += finalWeeklyStream
                            val newStreamsTotal = currentStreams + finalWeeklyStream

                            val isPremium = state.upgradedPlatforms.contains(platformId)
                            // Realistic streaming payouts (e.g. $0.003 to $0.008 per stream)
                            val rate = when (platformId) {
                                "tunes" -> if (isPremium) 0.003 else 0.001
                                "sharp" -> if (isPremium) 0.005 else 0.002
                                "jams" -> if (isPremium) 0.006 else 0.003
                                "megaphone" -> if (isPremium) 0.008 else 0.004
                                "songify" -> if (isPremium) 0.012 else 0.005
                                else -> 0.0
                            }

                            val isPlagiarized = !song.isCopyrightRegistered && !song.hasSampleClearance && Random.nextFloat() < 0.04f
                            val plagiarismFactor = if (isPlagiarized) 0.60 else 1.00
                            if (isPlagiarized && !plagiarismReportedForSong) {
                                plagiarismReportedForSong = true
                                reportBuilder.append("⚠ COPYRIGHT INFRINGEMENT: An unauthorized derivative of your unregistered song '${song.title}' was released on a competitor platform. You lost 40% of its royalties this week! Register copyright in the studio to protect your tracks.\n")
                            }

                            // 1. MASTER ROYALTY (Recording Payout)
                            val grossMasterRoyalty = finalWeeklyStream * rate * plagiarismFactor
                            val distCut = when (state.activeDistributionDeal) {
                                "EliteVibe" -> 0.15
                                "EmpireDistro" -> 0.30
                                else -> 0.0
                            }
                            val netMasterArtistRoyalty = grossMasterRoyalty * (1.0 - distCut)
                            totalMasterArtistRevenue += netMasterArtistRoyalty

                            // 2. PUBLISHING ROYALTY (Mechanical & Performance Payout)
                            // Realistic Publishing Mechanicals (~$0.0009 to $0.0012 per stream)
                            val baseMechanicalRate = 0.0009
                            val mechanicalMult = when (state.activePublishingDeal) {
                                "CoPublishing" -> 1.5
                                "AdminOnly" -> 1.2
                                else -> 1.0
                            }
                            var finalMechRate = baseMechanicalRate * mechanicalMult
                            if (state.spiritualJournal.contains("luxury_item_luxury_catalog_trust")) {
                                finalMechRate *= 2.0 // Double Collections Elite Perk!
                            }
                            if (state.signedLabelId != null) {
                                finalMechRate *= 0.75 // Controlled Composition Clause Penalty
                            }
                            val grossPublishingRoyalty = finalWeeklyStream * finalMechRate * plagiarismFactor
                            val pubCut = when (state.activePublishingDeal) {
                                "CoPublishing" -> 0.50
                                "AdminOnly" -> 0.15
                                "DIY" -> 0.10
                                else -> 0.30 // No publisher leaves 30% uncollected (black box pool)
                            }
                            val netPublishingArtistRoyalty = grossPublishingRoyalty * (1.0 - pubCut)
                            totalPublishingArtistRevenue += netPublishingArtistRoyalty

                            newStreamsTotal
                        }

                        playerSongsWeeklyStreamsMap[song.id] = totalWeeklyStreamsForSong
                        val decrementedWeeks = (song.promoWeeksLeft - 1).coerceAtLeast(0)
                        val updatedPromoBoost = if (decrementedWeeks > 0) song.promoWeeklyBoost else 0f
                        song.copy(
                            isUploadedMap = updatedMap,
                            totalStreams = updatedMap.values.sum(),
                            hype = updatedHype,
                            promoWeeksLeft = decrementedWeeks,
                            promoWeeklyBoost = updatedPromoBoost,
                            weeksSinceRelease = song.weeksSinceRelease + 1,
                            peakStreams = maxOf(song.peakStreams, totalWeeklyStreamsForSong),
                            isViral = song.isViral || songViralHitOccurred
                        )
                    }
                } else song
            }

            if (viralHitOccurred) {
                reportBuilder.append("🚀 ALGORITHM BREAKTHROUGH: Your song '$viralHitTitle' broke the internet and went completely viral! Gained massive streaming surge!\n")
                currentPublicImage = (currentPublicImage + 15f).coerceAtMost(100f)
                currentIndustryRespect = (currentIndustryRespect + 10f).coerceAtMost(100f)
            }

            // 4b. EP & Album Weekly Sales, Streams, and Decay
            val updatedAlbums = state.albums.map { album ->
                if (album.isReleased) {
                    val weeksSince = album.weeksSinceRelease + 1

                    // Decaying sales rate: base is 0.55, modified by artistic quality and economic climate
                    val skillInfluence = ((updatedVoice + updatedWriting) / 200f) * 0.18f // Slower decay if skills are exceptional (masterpiece quality)
                    val econDecayModifier = when (economyCycle) {
                        "Recession" -> -0.10f
                        "Depression" -> -0.18f
                        "Boom" -> 0.05f
                        else -> 0.0f
                    }
                    val finalDecayBase = (0.55f + skillInfluence + econDecayModifier).coerceIn(0.35f, 0.85f)
                    val decayPower = Math.pow(finalDecayBase.toDouble(), (weeksSince - 1).toDouble()).toFloat()

                    // Album Hype & Active Promo Engine
                    val currentAlbumHype = album.hype.coerceIn(1f, 100f)
                    val hasAlbumPromo = album.promoWeeksLeft > 0
                    val albumPromoBoost = if (hasAlbumPromo) album.promoWeeklyBoost else 0f
                    val decrementedAlbumPromo = (album.promoWeeksLeft - 1).coerceAtLeast(0)
                    val updatedAlbumPromoBoost = if (decrementedAlbumPromo > 0) album.promoWeeklyBoost else 0f

                    val albumHypeDelta: Float = when {
                        weeksSince <= 4 -> {
                            if (album.rating >= 4.2f) {
                                if (hasAlbumPromo) ((album.rating - 4.0f) * 4.0f + albumPromoBoost * 14.0f).coerceIn(1.5f, 4.5f)
                                else -((5.0f - album.rating) * 2.0f + 2.8f).coerceIn(2.5f, 4.5f)
                            } else if (album.rating in 3.6f..4.19f) {
                                if (hasAlbumPromo) (albumPromoBoost * 10.0f - 1.2f).coerceIn(-1.2f, 1.5f)
                                else -((4.2f - album.rating) * 4.0f + 3.5f).coerceIn(3.5f, 6.5f)
                            } else {
                                if (hasAlbumPromo) -((3.6f - album.rating) * 4.0f + 3.5f).coerceIn(3.5f, 8.0f)
                                else -((3.6f - album.rating) * 6.0f + 7.0f).coerceIn(7.0f, 16.0f)
                            }
                        }
                        else -> {
                            val fatigue = ((weeksSince - 4) * 0.9f).coerceAtMost(10f)
                            if (album.rating >= 4.2f) {
                                if (hasAlbumPromo) (-1.5f - fatigue * 0.4f) else (-3.2f - fatigue * 0.8f)
                            } else {
                                (-5.5f - fatigue * 1.2f)
                            }
                        }
                    }
                    val updatedAlbumHype = (currentAlbumHype + albumHypeDelta).coerceIn(1f, 100f)

                    // Core music fans purchasing copies directly (digital/physical purchases)
                    val coreAudience = (state.fans * 0.0015f + state.popularity * 4.0f).coerceAtLeast(1.0f)
                    val qualityMult = (album.rating / 5.0f).coerceIn(0.4f, 1.2f)
                    val hypeMult = (0.25f + (currentAlbumHype / 100f) * 0.75f)
                    val baseSalesVal = coreAudience * qualityMult * hypeMult
                    val marketingMult = when (album.marketingTier) {
                        "Budget" -> 1.15f
                        "Standard" -> 1.35f
                        "Cinematic" -> 1.70f
                        "Global Campaign" -> 2.20f
                        else -> 1.0f
                    }
                    val currentSales = (baseSalesVal * marketingMult * decayPower * (Random.nextFloat() * 0.3f + 0.85f)).toLong().coerceAtLeast(0L)

                    val grossRevenue = currentSales * album.price
                    // Calculate Contract Cut (DIY = 0%, Major labels = 15-30%)
                    val labelCut = if (state.signedLabelId != null) state.signedLabelCut else 0f
                    // Net earnings (65% net retail/distro margin, minus label cut)
                    val netEarnings = grossRevenue * (1.0f - labelCut) * 0.65f

                    updatedCash += netEarnings

                    if (currentSales > 0) {
                        reportBuilder.append("💿 ${if (album.isEP) "EP" else "Album"} Sales: Sold ${String.format("%,d", currentSales)} copies of '${album.title}', earning $${String.format("%,.2f", netEarnings)} in net sales royalties!\n")
                    }

                    album.copy(
                        sales = album.sales + currentSales,
                        weeksSinceRelease = weeksSince,
                        hype = updatedAlbumHype,
                        promoWeeksLeft = decrementedAlbumPromo,
                        promoWeeklyBoost = updatedAlbumPromoBoost,
                        albumCutEarned = album.albumCutEarned + netEarnings
                    )
                } else album
            }

            // 4c. Decaying Weekly Social Media Engagement for CastPosts & Social Streak Check
            val postedThisWeek = state.castPosts.any { it.isByPlayer && it.week == state.currentWeek && it.year == state.currentYear }
            val currentStreakStr = state.spiritualJournal.find { it.startsWith("social_streak:") }?.substringAfter("social_streak:")
            var currentSocialStreak = currentStreakStr?.toIntOrNull() ?: 0
            if (postedThisWeek) {
                currentSocialStreak++
            } else {
                currentSocialStreak = (currentSocialStreak - 2).coerceAtLeast(0)
            }
            updatedSpiritualJournal.removeAll { it.startsWith("social_streak:") }
            updatedSpiritualJournal.add("social_streak:$currentSocialStreak")

            val updatedCastPosts = mutCastPosts.map { post ->
                if (post.isByPlayer) {
                    val age = (nextYear - post.year) * 52 + (nextWeek - post.week)
                    if (age > 0) {
                        // A post gets most of its attention early, then leaves the feed.
                        // The finite window prevents old posts generating followers forever.
                        val engagementMultiplier = castEngagementMultiplier(age)
                        val likesGain = (post.likes * engagementMultiplier).toLong().coerceAtLeast(0L)
                        val repostsGain = (post.reposts * (engagementMultiplier * (0.15f / 0.18f))).toLong().coerceAtLeast(0L)
                        val commentsGain = (post.commentsCount * (engagementMultiplier * (0.12f / 0.18f))).toLong().coerceAtLeast(0L)

                        // Small organic fan boost
                        updatedFollowers += (likesGain * 0.05).toLong()

                        post.copy(
                            likes = post.likes + likesGain,
                            reposts = post.reposts + repostsGain,
                            commentsCount = post.commentsCount + commentsGain
                        )
                    } else post
                } else post
            }
            mutCastPosts = updatedCastPosts.toMutableList()

            // Record Label split of master royalty
            val labelCut = if (state.signedLabelId != null) totalMasterArtistRevenue * state.signedLabelCut else 0.0
            val netMasterArtistRevenue = totalMasterArtistRevenue - labelCut

            // Sync Licensing check
            if (state.activePublishingDeal == "CoPublishing" || state.activePublishingDeal == "AdminOnly") {
                val syncChance = if (state.activePublishingDeal == "CoPublishing") 0.06 else 0.03
                if (Random.nextDouble() < syncChance) {
                    val releasedSongs = state.songs.filter { it.isReleased }
                    if (releasedSongs.isNotEmpty()) {
                        val syncSong = releasedSongs.random()
                        val syncFee = if (state.activePublishingDeal == "CoPublishing") Random.nextDouble(10000.0, 30000.0) else Random.nextDouble(8000.0, 20000.0)
                        val artistSyncShare = if (state.activePublishingDeal == "CoPublishing") syncFee * 0.50 else syncFee * 0.85
                        totalPublishingArtistRevenue += artistSyncShare
                        reportBuilder.append("📺 SYNC LICENSING PLACEMENT: Your publisher secured a sync license for '${syncSong.title}' in a television commercial! Earned your share of $${String.format("%,.2f", artistSyncShare)}.\n")
                    }
                }
            }

            var netWeeklyArtistRoyalties = netMasterArtistRevenue + totalPublishingArtistRevenue

            // Samsong Phone Perk
            if (state.activePhone == "Samsong") {
                val samsongBonus = netWeeklyArtistRoyalties * 0.10
                netWeeklyArtistRoyalties += samsongBonus
                if (samsongBonus > 0) { reportBuilder.append("📱 Samsong Perk: +10% streaming & royalty boost ($"); reportBuilder.append(String.format("%,.2f", samsongBonus)); reportBuilder.append(").\n") }
            }

            totalStreamsPayout = netWeeklyArtistRoyalties
            if (netWeeklyArtistRoyalties > 0.0) {
                reportBuilder.append("🎵 Music Income: Gross Master royalties: $${String.format("%,.2f", totalMasterArtistRevenue)} | Gross Publishing royalties: $${String.format("%,.2f", totalPublishingArtistRevenue)}.\n")
                if (labelCut > 0.0) {
                    reportBuilder.append("   Record Label cut: $${String.format("%,.2f", labelCut)} taken (${String.format("%.0f", state.signedLabelCut * 100)}% Master cut).\n")
                }
                if (state.signedLabelId != null) {
                    reportBuilder.append("   Controlled Composition Rule: Your publishing mechanical royalties were reduced by 25% under your label contract.\n")
                }

                // Recoupment check
                if (state.recoupableAdvanceBalance > 0.0) {
                    val amountToRecoup = minOf(netWeeklyArtistRoyalties, state.recoupableAdvanceBalance)
                    val remainingAdvance = state.recoupableAdvanceBalance - amountToRecoup
                    val artistPayout = netWeeklyArtistRoyalties - amountToRecoup

                    // Deduct recoupment from recoupable balance in state
                    recoupableAdvanceBalance = remainingAdvance

                    reportBuilder.append("⚖ RECOUPMENT UPDATE: $${String.format("%,.2f", amountToRecoup)} of your weekly royalties was kept by the label/publisher to recoup your advance. Unrecouped advance remaining: $${String.format("%,.2f", remainingAdvance)}.\n")
                    if (artistPayout > 0.0) {
                        reportBuilder.append("   Paid you $${String.format("%,.2f", artistPayout)} in excess royalties!\n")
                        weeklySalary += artistPayout
                    }
                } else {
                    weeklySalary += netWeeklyArtistRoyalties
                    reportBuilder.append("💵 Net Royalties Paid to Artist: $${String.format("%,.2f", netWeeklyArtistRoyalties)} deposited directly.\n")
                }
            }

            // 5. Update Tours status & pay
            var tourPayout = 0.0
            var completedToursCount = 0
            val tourSimulationResult = TourSimulationHelper.processWeeklyTours(
                state = state,
                economyCycle = economyCycle,
                eventEffect = eventEffect,
                weatherCondition = weatherCondition,
                reportBuilder = reportBuilder
            )
            val updatedTours = tourSimulationResult.updatedTours
            tourPayout = tourSimulationResult.grossTourPayout
            completedToursCount = tourSimulationResult.completedToursCount
            weeklySalary += tourSimulationResult.netTourPayout

            // 6. Merch sales weekly (List-driven premium inventory-capped + legacy fallback)
            var merchRevenue = 0.0
            val updatedMerchStyles = if (state.merchStyles.isNotEmpty()) {
                var totalMerchRevenue = 0.0
                var totalUnitsSold = 0
                val mapped = state.merchStyles.map { style ->
                    if (style.stockRemaining > 0) {
                        // Base demand: casual + hardcore + popularity
                        val demandBase = (state.casualFans * 0.003 + state.hardcoreFans * 0.02 + state.popularity * 2.5).coerceAtLeast(5.0)

                        // Price competitiveness modifier (standard is 3x unitCost)
                        val priceRatio = style.sellPrice / (style.unitCost * 3.0)
                        val priceMod = when {
                            priceRatio <= 0.8 -> 1.5
                            priceRatio <= 1.2 -> 1.0
                            priceRatio <= 2.0 -> 0.4
                            else -> 0.08
                        }

                        // Warehouse modifier reduces logistics costs/boosts efficiency
                        val hasWarehouse = state.ownedHouses.any { it.name.contains("Warehouse", ignoreCase = true) }
                        val warehouseMod = if (hasWarehouse) 1.25f else 1.0f

                        // Business skill modifier (around 1.0 at 50 skill, ranges from 0.6 to 1.5)
                        val bizMod = 0.5f + (state.businessSkill / 100f)

                        // Integrity modifier (around 1.0 at 50, ranges from 0.7 to 1.3)
                        val integrityMod = 0.6f + (state.integrity / 125f)

                        // Popularity modifier
                        val popMod = 0.5f + (state.popularity / 50f)

                        val eventMerchMult = if (eventEffect == "RECESSION") 0.6 else 1.0
                        val weeklyDemand = (demandBase * priceMod * warehouseMod * eventMerchMult * bizMod * integrityMod * popMod * (Random.nextFloat() * 0.4f + 0.8f)).toInt().coerceAtLeast(1)
                        val actualSales = weeklyDemand.coerceAtMost(style.stockRemaining)

                        if (actualSales > 0) {
                            val revenue = actualSales * style.sellPrice
                            totalMerchRevenue += revenue
                            totalUnitsSold += actualSales
                            style.copy(
                                stockRemaining = style.stockRemaining - actualSales,
                                totalSold = style.totalSold + actualSales,
                                totalRevenue = style.totalRevenue + revenue
                            )
                        } else {
                            style
                        }
                    } else {
                        style
                    }
                }
                merchRevenue = totalMerchRevenue
                if (totalMerchRevenue > 0.0) {
                    val merchLabelCut = if (state.signedLabelId != null) {
                        totalMerchRevenue * (if (state.signedLabelId == "syndicate_360") 0.25 else 0.15)
                    } else 0.0
                    val netMerch = totalMerchRevenue - merchLabelCut
                    reportBuilder.append("👕 Premium Merchandise: Sold $totalUnitsSold items across styles, generating $${String.format("%,.2f", totalMerchRevenue)} in revenues.\n")
                    if (merchLabelCut > 0.0) {
                        reportBuilder.append("   Record Label 360 Cut on Merch: -$${String.format("%,.2f", merchLabelCut)} taken by the label.\n")
                    }
                    weeklySalary += netMerch
                }
                mapped
            } else {
                // Legacy Fallback
                val bizMod = 0.5f + (state.businessSkill / 100f)
                val integrityMod = 0.6f + (state.integrity / 125f)
                val popMod = 0.5f + (state.popularity / 50f)
                val merchBaseSales = (state.fans * 0.01 + state.popularity * 2.5).coerceAtLeast(0.0)
                val eventMerchMult = if (eventEffect == "RECESSION") 0.6 else 1.0
                merchRevenue = merchBaseSales * bizMod * integrityMod * popMod * (Random.nextFloat() * 1.5 + 0.5) * eventMerchMult
                if (merchRevenue > 0.0) {
                    val merchLabelCut = if (state.signedLabelId != null) {
                        merchRevenue * (if (state.signedLabelId == "syndicate_360") 0.25 else 0.15)
                    } else 0.0
                    val netMerch = merchRevenue - merchLabelCut
                    reportBuilder.append("👕 Merch: Generated $${String.format("%,.2f", merchRevenue)} in generic merchandise sales.\n")
                    if (merchLabelCut > 0.0) {
                        reportBuilder.append("   Record Label 360 Cut on Merch: -$${String.format("%,.2f", merchLabelCut)} taken by the label.\n")
                    }
                    weeklySalary += netMerch
                }
                emptyList()
            }

            // 6. Simulate Stock & Crypto Weekly Price Shifts (Realistic Slow Stock Drift + Rare Big Moves, Huge Crypto Volatility & Analyst Hedging)
            val marketSimResult = MarketSimulationHelper.simulateStocksAndCryptos(
                state = state,
                economyCycle = economyCycle,
                isElectionSeason = isElectionSeason,
                hackerCryptoLossRatio = hackerCryptoLossRatio,
                reportBuilder = reportBuilder
            )
            var updatedStocks = marketSimResult.updatedStocks
            val updatedCryptos = marketSimResult.updatedCryptos
            val remainingStockOrders = marketSimResult.remainingStockOrders
            val remainingCryptoOrders = marketSimResult.remainingCryptoOrders
            updatedCash += marketSimResult.addedCash
            weeklySalary += marketSimResult.addedSalary

            // 7. Update Business Ventures (Realistic Fixed Revenue, Expenses, Upgrades, Investors & WeShare IPO)
            val ventureSimResult = VentureSimulationHelper.processWeeklyVentures(
                state = state.copy(stocks = updatedStocks),
                economyCycle = economyCycle,
                reportBuilder = reportBuilder
            )
            val updatedVentures = ventureSimResult.updatedVentures
            updatedStocks = ventureSimResult.updatedStocks
            updatedMail.addAll(ventureSimResult.generatedMails)
            updatedCash += (ventureSimResult.totalPlayerProfitCash + ventureSimResult.totalIpoCashEarned)


            // 7c. MeTube Channel Updates & Brand Deals (Delegated to MeTubeAndBrandHelper)
            val meTubeBrandResult = MeTubeAndBrandHelper.processMeTubeAndBrandDeals(
                state = state,
                currentPublicImage = currentPublicImage,
                updatedSpiritualJournal = updatedSpiritualJournal,
                updatedMail = updatedMail,
                reportBuilder = reportBuilder
            )
            val updatedMeTubeVideos = meTubeBrandResult.updatedMeTubeVideos.toMutableList()
            val newWatchTime = meTubeBrandResult.newWatchTime
            val newSubscribers = meTubeBrandResult.newSubscribers
            val meTubeEarnings = meTubeBrandResult.meTubeEarnings
            val activeBrandDealName = meTubeBrandResult.activeBrandDealName
            val brandDealWeeklyPayout = meTubeBrandResult.brandDealWeeklyPayout
            val brandDealWeeksRemaining = meTubeBrandResult.brandDealWeeksRemaining
            var brandDealPostedThisWeek = meTubeBrandResult.brandDealPostedThisWeek
            updatedCash += meTubeBrandResult.addedCash
            weeklySalary += meTubeBrandResult.addedSalary

            // 7d. Roster Updates & 70/30 Predictive Market Intel (Delegated to RosterAndIntelHelper)
            val rosterIntelResult = RosterAndIntelHelper.processRosterAndMarketIntel(
                state = state,
                currentStocks = updatedStocks,
                currentCryptos = updatedCryptos,
                updatedMail = updatedMail
            )
            val updatedCandidateRoster = rosterIntelResult.updatedCandidateRoster
            var finalStocks = rosterIntelResult.finalStocks
            var finalCryptos = rosterIntelResult.finalCryptos

            // 7d. Upgraded Employee AI Workforce Simulator
            val workforceResult = simulateEmployeeWorkforce(
                state = state,
                initialCash = updatedCash,
                initialSavings = newSavingsBalance,
                initialStocks = finalStocks,
                initialCryptos = finalCryptos,
                initialMeTubeVideos = updatedMeTubeVideos,
                initialCastPosts = mutCastPosts,
                initialMail = updatedMail,
                reportBuilder = reportBuilder,
                activeTours = activeTours,
                totalWeeklyOutflow = totalWeeklyOutflow,
                weeklySalary = weeklySalary,
                totalStreamsPayout = totalStreamsPayout,
                tourPayout = tourPayout,
                merchRevenue = merchRevenue,
                meTubeEarnings = meTubeEarnings,
                newLoanAmount = newLoanAmount,
                initialPublicImage = currentPublicImage,
                initialNegativeSentiment = updatedNegativeSentiment,
                initialTrustworthiness = currentTrustworthiness,
                initialBusinessReputation = currentBusinessReputation,
                initialReliability = currentReliability,
                initialIndustryRespect = currentIndustryRespect,
                brandDealPostedEarlierThisWeek = brandDealPostedThisWeek
            )

            updatedCash = workforceResult.updatedCash
            newSavingsBalance = workforceResult.newSavingsBalance
            updatedPayrollReserve = workforceResult.updatedPayrollReserve
            updatedMail.clear()
            updatedMail.addAll(workforceResult.updatedMail)
            val updatedEmployeesList = workforceResult.updatedEmployeesList
            finalStocks = workforceResult.finalStocks
            finalCryptos = workforceResult.finalCryptos
            updatedMeTubeVideos.clear()
            updatedMeTubeVideos.addAll(workforceResult.updatedMeTubeVideos)
            mutCastPosts.clear()
            mutCastPosts.addAll(workforceResult.updatedCastPosts)
            if (workforceResult.smmAutoPostedBrandDeal) {
                brandDealPostedThisWeek = true
            }
            currentPublicImage = workforceResult.currentPublicImage
            updatedNegativeSentiment = workforceResult.updatedNegativeSentiment
            currentTrustworthiness = workforceResult.currentTrustworthiness
            currentBusinessReputation = workforceResult.currentBusinessReputation
            currentReliability = workforceResult.currentReliability
            val employeesUnpaid = workforceResult.employeesUnpaid

            // 7e. Cast Trending Topics Generator
            val trendingHashtags = listOf(
                "#${state.artistName}Hit",
                "#RecessionGigs",
                "#GenesisBullRun",
                "#WeShareDividends",
                "#MusicWars",
                "#RetroSynthVibe",
                "#AuraTours",
                "#HiringHallHustle",
                "#SoulCarePrayer"
            ).shuffled().take(3)

            // 7f. Spiritual Life Reminders
            if (state.spiritualTitheSetting && state.spiritualTitheReminderActive && weeklySalary > 0.0) {
                val suggestedTithe = weeklySalary * 0.10
                reportBuilder.append("🙏 Spiritual Life: Tithe reminder active. Consider tithing 10% ($${String.format("%,.2f", suggestedTithe)}) from this week's earnings.\n")
            }

            // 9. Label manager weekly cost
            var accruedDebts = state.ownLabel?.accruedDebts ?: 0.0

            // Push notifications tracking
            val newNotifications = mutableListOf<String>()
            if (viralHitOccurred) newNotifications.add("🔥 ALGORITHM BLOWUP: Song '$viralHitTitle' is trending #1 on MeTube!")
            if (totalWeeklyOutflow > 1000.0) newNotifications.add("🏠 Asset Upkeep: Paid $${String.format("%.2f", totalWeeklyOutflow)} expenses.")
            if (accruedDebts > 0.0 && Random.nextFloat() > 0.5f) newNotifications.add("⚠ Debt Warning: Your Record Label has accrued debts of $${String.format("%.2f", accruedDebts)}!")
            if (employeesUnpaid) newNotifications.add("🚨 Salary Delinquency: Professional staff salaries were unpaid!")
            if (meTubeEarnings > 10.0) newNotifications.add("📺 MeTube Alert: Channel accrued $${String.format("%.2f", meTubeEarnings)} in ad revenue!")

            // 8. Practice / Social Engagement Skill Decay
            var practiceMissed = state.practiceMissedWeeks + 1

            if (practiceMissed >= 2) {
                updatedVoice = (updatedVoice * 0.98f).coerceAtLeast(10f)
                updatedWriting = (updatedWriting * 0.98f).coerceAtLeast(10f)
                updatedInfluence = (updatedInfluence * 0.96f).coerceAtLeast(5f)
                reportBuilder.append("📉 Skill Decay: Lack of social posting/practice decayed skills & online influence.\n")
            }

            var labelPlayerDividends = 0.0
            var labelCeoPaycheck = 0.0

            val ownLabel = state.ownLabel?.let { label ->
                // Auto-initialize co-founders and shareholders for backwards-compatibility or fresh labels without them
                var currentLabel = label
                if (currentLabel.shareholders.isEmpty()) {
                    val initialCoFounder = CoFounder(
                        id = "cofounder_1",
                        name = listOf("Marcus Vance", "Elena Rostova", "Devon Cole", "Sarah Sterling").random(),
                        trait = listOf("Creative", "Aggressive", "Conservative", "Greedy", "Pragmatic").random(),
                        equityShare = 20.0f,
                        relationship = 85.0f,
                        salaryExpectation = 1800.0,
                        monthlyDemands = "None"
                    )
                    currentLabel = currentLabel.copy(
                        coFounders = listOf(initialCoFounder),
                        shareholders = listOf(
                            Shareholder(name = "You (${state.artistName})", equityShare = 80.0f, type = "Founder", boardSeat = true, satisfaction = 100f),
                            Shareholder(name = initialCoFounder.name, equityShare = 20.0f, type = "Co-Founder", boardSeat = true, satisfaction = 85f)
                        ),
                        boardMembers = listOf(
                            BoardMember(id = "player_board", name = "You (${state.artistName})", type = "Player", powerWeight = 80.0f, satisfaction = 100f, alignment = "Growth"),
                            BoardMember(id = "cofounder_board_1", name = initialCoFounder.name, type = "Co-Founder", powerWeight = 20.0f, satisfaction = 85f, alignment = "Growth")
                        ),
                        dividendPayoutRate = 15.0f,
                        ceoSalary = 2000.0,
                        boardSatisfaction = 90f,
                        labelReputation = 80f
                    )
                }

                val managersCost = currentLabel.managers.sumOf { it.weeklyCost }
                val promoCost = currentLabel.promoTeams.sumOf { it.weeklyCost }
                val coFounderSalaries = currentLabel.coFounders.sumOf { it.salaryExpectation }

                // Base cost + staff + cofounders + CEO salary
                val overheadExpenses = 1500.0 + managersCost + promoCost + coFounderSalaries + currentLabel.ceoSalary

                // Calculate royalties from signed artists!
                var weeklyRoyalties = 0.0
                currentLabel.signedNPCIds.forEach { npcId ->
                    val npc = state.npcs.find { n -> n.id == npcId }
                    if (npc != null) {
                        val npcWeeklyEarning = (npc.popularity * 180.0 + npc.fans * 0.0003)
                        weeklyRoyalties += npcWeeklyEarning * 0.15
                    }
                }

                // Process royalties and expenses in company vault
                val totalCompanyFunds = currentLabel.companyCash + weeklyRoyalties

                // Dividends distribution
                val totalProfit = weeklyRoyalties - overheadExpenses
                var distributedDividends = 0.0
                var playerDividendEarned = 0.0

                val finalCompanyCash: Double
                val updatedLabelDebt: Double

                if (totalCompanyFunds < overheadExpenses) {
                    val deficit = overheadExpenses - totalCompanyFunds
                    currentBusinessReputation = (currentBusinessReputation - 8f).coerceAtLeast(0f)
                    currentReliability = (currentReliability - 5f).coerceAtLeast(0f)
                    currentIntegrity = (currentIntegrity - 3f).coerceAtLeast(0f)
                    reportBuilder.append("⚠ COMPANY DEBT TRIGGER: '${currentLabel.name}' cash reserve is empty! Accrued $${String.format("%,.2f", deficit)} in debt to pay salaries. Business Reputation damaged!\n")
                    finalCompanyCash = 0.0
                    updatedLabelDebt = currentLabel.accruedDebts + deficit

                    // Decays board satisfaction since company is in debt
                    currentLabel = currentLabel.copy(boardSatisfaction = (currentLabel.boardSatisfaction - 15f).coerceIn(0f, 100f))
                } else {
                    currentBusinessReputation = (currentBusinessReputation + 1.2f).coerceAtMost(100f)
                    currentReliability = (currentReliability + 0.8f).coerceAtMost(100f)
                    reportBuilder.append("🏢 Company '${currentLabel.name}' Operations: Paid $${String.format("%,.2f", overheadExpenses)} overhead (including salaries & CEO paycheck) & collected $${String.format("%,.2f", weeklyRoyalties)} royalties.\n")

                    var netCash = totalCompanyFunds - overheadExpenses

                    // CEO Salary goes to player's personal wallet!
                    labelCeoPaycheck = currentLabel.ceoSalary
                    updatedCash += currentLabel.ceoSalary
                    reportBuilder.append("💼 CEO Salary: Received your weekly corporate paycheck of +$${String.format("%,.2f", currentLabel.ceoSalary)} in your personal cash!\n")

                    if (totalProfit > 0.0 && currentLabel.dividendPayoutRate > 0f) {
                        distributedDividends = totalProfit * (currentLabel.dividendPayoutRate / 100f)
                        playerDividendEarned = distributedDividends * (currentLabel.equityHeldByPlayer / 100f)
                        labelPlayerDividends = playerDividendEarned
                        updatedCash += playerDividendEarned
                        netCash -= distributedDividends
                        reportBuilder.append("💼 Corporate Dividends: Distributed $${String.format("%,.2f", distributedDividends)} to shareholders. You received your equity cut of +$${String.format("%,.2f", playerDividendEarned)} personal cash!\n")
                    }

                    finalCompanyCash = netCash
                    updatedLabelDebt = currentLabel.accruedDebts
                }

                // Tick co-founders relationships and demands
                val updatedCoFounders = currentLabel.coFounders.map { cf ->
                    var rel = cf.relationship
                    var monthlyDemands = cf.monthlyDemands

                    if (totalCompanyFunds < overheadExpenses) {
                        rel = (rel - 6f).coerceIn(0f, 100f)
                        monthlyDemands = "Needs financial stability!"
                    } else {
                        rel = (rel + 0.5f).coerceAtMost(100f)
                        if (cf.trait == "Greedy" && Random.nextFloat() < 0.15f) {
                            monthlyDemands = "Wants a salary raise to $${String.format("%.0f", cf.salaryExpectation * 1.15)}"
                        } else {
                            monthlyDemands = "None"
                        }
                    }
                    cf.copy(relationship = rel, monthlyDemands = monthlyDemands)
                }

                // Board Satisfaction calculation based on profit, dividends, and CEO reputation
                val boardSatFactor = if (totalProfit > 0) 5f else -10f
                val dividendSatFactor = if (distributedDividends > 0) 4f else -2f
                val reputationSatFactor = if (currentPublicImage > 60f) 2f else -5f

                val currentBoardSat = (currentLabel.boardSatisfaction + boardSatFactor + dividendSatFactor + reputationSatFactor).coerceIn(0f, 100f)

                // Update Shareholders satisfaction
                val updatedShareholders = currentLabel.shareholders.map { sh ->
                    if (sh.type == "Founder") {
                        sh.copy(satisfaction = 100f)
                    } else {
                        var sat = sh.satisfaction + boardSatFactor + dividendSatFactor
                        if (currentPublicImage < 40f) sat -= 5f
                        sh.copy(satisfaction = sat.coerceIn(0f, 100f))
                    }
                }

                // Update Board Members satisfaction
                val updatedBoardMembers = currentLabel.boardMembers.map { bm ->
                    if (bm.type == "Player") {
                        bm.copy(satisfaction = 100f)
                    } else {
                        var sat = bm.satisfaction + boardSatFactor + dividendSatFactor
                        if (currentPublicImage < 45f) sat -= 4f
                        bm.copy(satisfaction = sat.coerceIn(0f, 100f))
                    }
                }

                // Dynamic investor offers during progression
                val updatedPendingInvestorOffers = currentLabel.pendingInvestorOffers.toMutableList()
                // Auto-expire investor offers
                updatedPendingInvestorOffers.removeAll { it.durationWeeksExpires <= state.currentWeek }

                if (currentLabel.equityHeldByPlayer > 40f && updatedPendingInvestorOffers.size < 3 && Random.nextFloat() < 0.25f) {
                    val offerValuation = 2000000.0 + finalCompanyCash * 3.5 + (currentLabel.signedNPCIds.size * 400000.0)
                    val percentRequested = listOf(5f, 10f, 15f, 20f).random()
                    val cashOffer = offerValuation * (percentRequested / 100f)
                    val investorId = "inv_" + java.util.UUID.randomUUID().toString().take(5)
                    val investorName = listOf("Julian Vance (Titan Crest Capital)", "Zenith Capital", "Vortex Syndicate", "Alpha Blue Ventures", "Nova Peak Private Equity").random()
                    val newOffer = InvestorOffer(
                        id = investorId,
                        investorName = investorName,
                        investorType = listOf("Angel", "Venture Capital", "Private Equity").random(),
                        cashOffer = cashOffer,
                        equityRequested = percentRequested,
                        minimumValuation = offerValuation,
                        durationWeeksExpires = state.currentWeek + 3
                    )
                    updatedPendingInvestorOffers.add(newOffer)
                    reportBuilder.append("💼 Corporate Capital: Received a new strategic equity investment offer from ${investorName} in your corporate portal!\n")
                }

                // Dynamic artist signing offers from independent scouting
                val updatedPendingSigningOffers = currentLabel.pendingSigningOffers.toMutableList()
                // Auto-expire offers
                if (Random.nextFloat() < 0.20f && updatedPendingSigningOffers.isNotEmpty()) {
                    updatedPendingSigningOffers.removeAt(Random.nextInt(updatedPendingSigningOffers.size))
                }

                // Add new talent auditions weekly
                if (updatedPendingSigningOffers.size < 4 && Random.nextFloat() < 0.30f) {
                    val scoutingNPCs = state.npcs.filter { !currentLabel.signedNPCIds.contains(it.id) && it.currentRecordLabel == "Independent" && !it.isRetired }
                    if (scoutingNPCs.isNotEmpty()) {
                        val scouted = scoutingNPCs.random()
                        val bonus = (scouted.popularity * 2200.0).coerceAtLeast(10000.0)
                        val requestedRevSplit = listOf(35f, 40f, 45f, 50f, 60f).random()
                        val newOffer = ArtistSigningOffer(
                            id = "art_off_" + java.util.UUID.randomUUID().toString().take(5),
                            artistId = scouted.id,
                            artistName = scouted.name,
                            requestedRevenueShare = requestedRevSplit,
                            requestedSigningBonus = bonus,
                            contractDurationWeeks = listOf(12, 24, 48).random(),
                            weeklyExpenseLimit = 1500.0,
                            preferredGenre = scouted.genrePreference
                        )
                        if (updatedPendingSigningOffers.none { it.artistId == scouted.id }) {
                            updatedPendingSigningOffers.add(newOffer)
                            reportBuilder.append("📡 Talent Scouting: Independent artist ${scouted.name} has submitted a label signing proposal!\n")
                        }
                    }
                }

                // CRITICAL OUSTER VOTE DANGER!
                // If player owns less than 50% equity and board satisfaction falls below 35%,
                // a board coup could happen!
                val finalPlayerEquity = currentLabel.equityHeldByPlayer
                var finalCeoSalary = currentLabel.ceoSalary
                if (finalPlayerEquity < 50f && currentBoardSat < 35f && Random.nextFloat() < 0.40f) {
                    reportBuilder.append("🚨 BOARD ROOM COUP: Board members have held an emergency meeting and voted you out as CEO due to poor corporate results, scandals, and low shareholder satisfaction!\n")
                    reportBuilder.append("💼 You have been demoted to 'Chief Creative Director'. Your CEO salary was cut in half, and corporate funds distribution is locked until you buy back >50% equity control!\n")
                    finalCeoSalary *= 0.5
                }

                // Pop boost from promotion teams
                val popBoost = currentLabel.promoTeams.sumOf { it.popularityBoost.toDouble() }.toFloat()
                if (popBoost > 0f) {
                    reportBuilder.append("🏢 Label Promo: Promo teams gained +${String.format("%.2f", popBoost)}% popularity for your imprint!\n")
                }

                currentLabel.copy(
                    coFounders = updatedCoFounders,
                    shareholders = updatedShareholders,
                    boardMembers = updatedBoardMembers,
                    pendingInvestorOffers = updatedPendingInvestorOffers,
                    pendingSigningOffers = updatedPendingSigningOffers,
                    companyCash = finalCompanyCash,
                    accruedDebts = updatedLabelDebt,
                    weeklyRoyaltiesEarned = weeklyRoyalties,
                    ceoSalary = finalCeoSalary,
                    valuation = (2500000.0 + finalCompanyCash * 3.0 + (currentLabel.signedNPCIds.size * 500000.0) + (currentLabel.productionLevel + currentLabel.marketingLevel + currentLabel.touringLevel + currentLabel.legalLevel + currentLabel.arLevel) * 200000.0 + (updatedCoFounders.sumOf { it.relationship.toDouble() } * 10000.0)).coerceAtLeast(100000.0),
                    boardSatisfaction = currentBoardSat
                )
            }

            // 10. Generate Mail / Contracts / Hidden Opportunities / Rare Life-Changing Events

            // 9b. Mentorship & Shady Deals State Progress
            val activeMentorTag = updatedSpiritualJournal.find { it.startsWith("mentor_active_") }
            if (activeMentorTag != null) {
                val weeksLeft = activeMentorTag.substringAfter("mentor_active_").toInt()
                updatedSpiritualJournal.remove(activeMentorTag)
                if (weeksLeft > 1) {
                    val nextTag = "mentor_active_${weeksLeft - 1}"
                    updatedSpiritualJournal.add(nextTag)
                    currentGenerosity = (currentGenerosity + 3.0f).coerceAtMost(100f)
                    currentHumility = (currentHumility + 2.0f).coerceAtMost(100f)
                    reportBuilder.append("🎓 Mentorship Active: Spent valuable time coaching your young protégé this week. Your soul feels deeply enriched (+3% Generosity, +2% Humility, ${weeksLeft - 1} weeks remaining)!\n")
                } else {
                    // Mentorship completed! 12 weeks of active mentoring!
                    val mentorRoll = kotlin.random.Random.nextFloat()
                    if (mentorRoll < 0.35f) {
                        // Outcome A: 35% Chance breakout viral success!
                        val payout = 150000.0
                        updatedCash += payout
                        currentIndustryRespect = (currentIndustryRespect + 30f).coerceAtMost(100f)
                        currentPublicImage = (currentPublicImage + 25f).coerceAtMost(100f)
                        currentGenerosity = (currentGenerosity + 15f).coerceAtMost(100f)

                        val protegeEmail = MailMessage(
                            id = "protege_success_" + UUID.randomUUID().toString().take(6),
                            sender = "Your Protégé (Breakout Star)",
                            subject = "🚀 PROTÉGÉ SUCCESS: We hit #1 on the Indie Charts!",
                            body = "Dear ${state.artistName},\n\nI can't believe it! After 12 weeks of hard work, tireless sessions, and your incredible backing, my debut single has officially broken into the top spots and gone viral!\n\nYour \$25,000 investment paid off spectacularly. To express my endless gratitude for taking a chance on me when I was a nobody, I've wired you \$150,000 as your share of the initial royalties!\n\nYou are my lifelong mentor. Thank you!",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 3,
                            expiresYear = state.currentYear
                        )
                        updatedMail.add(protegeEmail)
                        reportBuilder.append("🚀 PROTÉGÉ BREAKOUT: Your mentored protégé achieved breakout viral success! They wired you \$150,000 in royalties (+30% Respect, +25% Public Image)!\n")
                        updatedTimelineEvents = updatedTimelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Your mentored protégé achieved spectacular breakout success, viral charting, and wired you \$150,000 in royalties."
                    } else if (mentorRoll < 0.75f) {
                        // Outcome B: 40% Chance - Still Grinding, no immediate financial return but high soul/respect gains
                        currentIndustryRespect = (currentIndustryRespect + 15f).coerceAtMost(100f)
                        currentGenerosity = (currentGenerosity + 10f).coerceAtMost(100f)
                        currentHumility = (currentHumility + 10f).coerceAtMost(100f)

                        val protegeEmail = MailMessage(
                            id = "protege_grind_" + UUID.randomUUID().toString().take(6),
                            sender = "Your Protégé (Still Grinding)",
                            subject = "✊ Mentorship Update: Keep the faith!",
                            body = "Hey ${state.artistName},\n\nWe wrapped up our 12 weeks of sessions! While our debut project didn't instantly explode on MeTube, it was highly acclaimed by indie blogs and established a beautiful base of listeners. The industry is a long, hard grind, but because of you, I have a foot in the door!\n\nI'll keep pushing and honoring your name. Thank you for your support!",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 3,
                            expiresYear = state.currentYear
                        )
                        updatedMail.add(protegeEmail)
                        reportBuilder.append("✊ PROTÉGÉ GRINDING: Your protégé finished their mentorship journey. They didn't hit stardom immediately, but gained respect & core fans (+15% Respect, +10% Generosity)!\n")
                    } else {
                        // Outcome C: 25% Chance - Gave up or pivoted, minor loss of cash but valuable soul gains
                        currentGenerosity = (currentGenerosity + 15f).coerceAtMost(100f)
                        currentHumility = (currentHumility + 15f).coerceAtMost(100f)

                        val protegeEmail = MailMessage(
                            id = "protege_pivot_" + UUID.randomUUID().toString().take(6),
                            sender = "Your Protégé (Life Pivot)",
                            subject = "🌱 Mentorship Update: A new path forward",
                            body = "Dear ${state.artistName},\n\nThank you so much for the 12 weeks of mentorship and your generous sponsorship. It was a beautiful journey, but it helped me realize that the stress and spotlight of the music industry isn't my true calling. I have decided to pivot to teaching music to underprivileged kids in our neighborhood.\n\nYour kindness taught me how to be a force for good. I will carry your lessons of generosity with me forever. Thank you for believing in me.",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 3,
                            expiresYear = state.currentYear
                        )
                        updatedMail.add(protegeEmail)
                        reportBuilder.append("🌱 PROTÉGÉ PIVOT: Your protégé decided that a public music career isn't their path and pivoted to community work. Gained deep fulfillment (+15% Generosity, +15% Humility)!\n")
                    }
                }
            }

            val activeScamTag = updatedSpiritualJournal.find { it.startsWith("scam_coin_signed_week_") }
            if (activeScamTag != null) {
                val signedWeek = activeScamTag.substringAfter("scam_coin_signed_week_").toInt()
                if (state.currentWeek >= signedWeek + 3 || (state.currentWeek < signedWeek && state.currentWeek + 52 >= signedWeek + 3)) {
                    updatedSpiritualJournal.remove(activeScamTag)

                    val hasSecurityOrLawyer = state.hiredEmployees.any { it.role == "Lawyer" && it.loyalty > 30f }
                    val lossAmount = if (hasSecurityOrLawyer) 10000.0 else 25000.0
                    updatedCash -= lossAmount
                    currentTrustworthiness = (currentTrustworthiness - 45f).coerceAtLeast(0f)
                    currentPublicImage = (currentPublicImage - 30f).coerceAtLeast(0f)

                    val rugpullEmail = MailMessage(
                        id = "token_inquiry_" + UUID.randomUUID().toString().take(6),
                        sender = "Financial Conduct & Regulatory Authority",
                        subject = "Notice of Compliance Review: Promotional Token Endorsement",
                        body = "Dear ${state.artistName},\n\nFollowing the sudden liquidity collapse of the cryptocurrency token you promoted to your audience, our regulatory office has received formal complaints regarding promotional financial endorsements.\n\nCompensatory legal filings and advisory defense expenses totaling \$${String.format("%,.0f", lossAmount)} have been deducted from your commercial account.",
                        type = "NORMAL",
                        expiresWeek = state.currentWeek + 4,
                        expiresYear = state.currentYear
                    )
                    updatedMail.add(rugpullEmail)
                    reportBuilder.append("⚠ Regulatory Notice: Inquiries initiated regarding your promotional endorsement of a defunct token project. Legal compliance costs deducted \$${String.format("%,.0f", lossAmount)}.\n")
                    updatedTimelineEvents = updatedTimelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Faced compliance review regarding promotional endorsement of a cryptocurrency venture."
                }
            }

            // Generate Early Career Predatory Contract (Realistic terms where only the other party benefits)
            val isEarlyStageArtist = updatedSongs.none { it.isReleased && it.totalStreams >= 20000L } || state.popularity < 25f
            val isLowOnCashAndUnsigned = state.signedLabelId == null && updatedCash < 15000.0
            if (isEarlyStageArtist && isLowOnCashAndUnsigned && kotlin.random.Random.nextFloat() < 0.08f && !updatedMail.any { it.id.startsWith("dev_deal_") }) {
                val contractId = "dev_deal_" + UUID.randomUUID().toString().take(6)
                updatedMail.add(
                    MailMessage(
                        id = contractId,
                        sender = "Vanguard Sound Group",
                        subject = "Artist Development & Distribution Agreement",
                        body = "Dear ${state.artistName},\n\nOur A&R team has observed your early activity on the local circuit. Vanguard Sound Group is prepared to offer you an exclusive 360 developmental recording contract.\n\nWe will provide an upfront advance of \$5,000 cash for studio production and equipment. In consideration, Vanguard Sound Group will retain 85% of all streaming, sync, and touring revenues across a 3-album commitment. All advances are recoupable from net royalties.\n\nWe look forward to executing this agreement.",
                        type = "SHADY_CONTRACT",
                        cashOffer = 5000.0,
                        expiresWeek = state.currentWeek + 4,
                        expiresYear = state.currentYear,
                        labelCut = 0.85f,
                        labelId = "vanguard_sound"
                    )
                )
                reportBuilder.append("✉ New Mail: Vanguard Sound Group sent an Artist Development & Distribution Agreement to your Mailbox.\n")
            }

            if (state.followers > 15000 && kotlin.random.Random.nextFloat() < 0.12f && !updatedMail.any { it.id.startsWith("token_deal_") }) {
                val contractId = "token_deal_" + UUID.randomUUID().toString().take(6)
                val tokenDeals = listOf(
                    Triple("Vortex Network Labs", "Promotional Ambassador Agreement: \$VTX Launch", "Dear ${state.artistName},\n\nWe represent Vortex Network Labs. We are launching a utility network token (\$VTX) designed for digital creator micro-transactions.\n\nWe are seeking creators to announce our protocol to their audience. In consideration of an official endorsement on your Cast feed, our organization will provide a promotional payment of \$25,000 cash.\n\nPromotional assets are provided. We look forward to confirming your participation."),
                    Triple("Aether Protocols", "Ecosystem Ambassador Proposal: \$AETH", "Greetings ${state.artistName},\n\nI am reaching out on behalf of the Aether Protocols launch committee. We are deploying an automated liquidity token (\$AETH) across decentralized networks.\n\nIf you agree to share a promotional announcement on your active social channels, our foundation will wire \$25,000 cash directly to your verified account.\n\nLet us know if you wish to confirm this partnership."),
                    Triple("Nexus Yield Foundation", "Digital Ambassador Proposal: \$NXY Protocol", "Hello ${state.artistName},\n\nNexus Yield Foundation is launching its decentralized yield platform next month. We would like to contract you as a launch ambassador.\n\nBy endorsing our protocol to your social network, we will transfer \$25,000 cash immediately upon execution of this agreement.\n\nWe look forward to your partnership.")
                ).random()

                updatedMail.add(
                    MailMessage(
                        id = contractId,
                        sender = tokenDeals.first,
                        subject = tokenDeals.second,
                        body = tokenDeals.third,
                        type = "SHADY_CRYPTO",
                        cashOffer = 25000.0,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("✉ New Mail: Received a promotional ambassador proposal from ${tokenDeals.first}.\n")
            }

            val canMentor = updatedCash >= 35000.0 && state.popularity >= 50f && state.fans >= 50000L
            if (canMentor && kotlin.random.Random.nextFloat() < 0.20f && !updatedMail.any { it.id.startsWith("mentor_") } && !updatedSpiritualJournal.any { it.startsWith("mentor_active_") }) {
                val contractId = "mentor_" + UUID.randomUUID().toString().take(6)
                val protegeOffers = listOf(
                    Triple("Marcus Vance (Violin Prodigy)", "🎓 Mentorship Proposal: Instrumental Fusion Project", "Dear ${state.artistName},\n\nI am Marcus, a violinist attempting to blend classical orchestral hooks with heavy modern beats. I've been grinding in underground clubs for 2 years. I have raw passion, but zero industry connections or production budget. Sponsoring my 12-week tracking sessions for \$25,000 and mentoring me would give me the boost I need. I'll share 20% of my lifetime royalties if we make it."),
                    Triple("Elena Rose (Folk-Pop Songwriter)", "🎓 Mentorship Request: Acoustic Sessions Sponsor", "Hello ${state.artistName},\n\nI write acoustic folk-pop songs with raw storytelling. I have zero studio funds and no industry backing. You've built such an incredible brand, and I need your guidance to navigate the pitfalls. If you sponsor my album's tracking (\$25,000) and mentor me, I'll legally assign you 20% of my royalties."),
                    Triple("Leo Vance (Jazz-Fusion Keyboardist)", "🎓 Mentorship Proposal: Experimental Jazz Launch", "Greetings ${state.artistName},\n\nI am Leo, an experimental jazz fusion producer trying to blend complex modal progressions with modern synth textures. Sponsoring my album production (\$25,000) and guiding me through marketing strategies will change my life. I'll share 20% of all future royalties.")
                ).random()

                updatedMail.add(
                    MailMessage(
                        id = contractId,
                        sender = protegeOffers.first,
                        subject = protegeOffers.second,
                        body = protegeOffers.third,
                        type = "MENTORSHIP_OFFER",
                        cashOffer = -25000.0,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("🎓 MENTORSHIP REQUEST: An upcoming artist is begging for your mentorship and financial support (\$25,000). Check Mailbox.\n")
            }

            // Lawyer Contract Analysis Routine
            val lawyer = state.hiredEmployees.find { it.role == "Lawyer" }
            if (lawyer != null && lawyer.loyalty > 25f) {
                val pendingDeals = updatedMail.filter {
                    (it.type == "SHADY_CONTRACT" || it.type == "SHADY_CRYPTO" || it.type == "METUBE_BRAND_DEAL" || it.type == "FEATURE" || it.type == "LABEL_OFFER")
                    && !it.isAccepted
                    && !updatedMail.any { review -> review.subject.contains("⚖ Legal Analysis") && review.subject.contains(it.id) }
                }
                for (deal in pendingDeals) {
                    val isPredatory = deal.type == "SHADY_CONTRACT" || deal.type == "SHADY_CRYPTO" || deal.labelCut > 0.60f
                    val analysisBody = if (isPredatory) {
                        "LEGAL MEMORANDUM\n\n" +
                        "To: ${state.artistName}\n" +
                        "From: ${lawyer.name}, Legal Counsel\n" +
                        "Subject: Contract Audit: '${deal.subject}' (${deal.sender})\n\n" +
                        "Summary of Core Clauses:\n" +
                        "• Financial Terms: Provides upfront consideration of \$${String.format("%,.0f", deal.cashOffer)}. Note that this advance is structured as recoupable against gross earnings.\n" +
                        "• Revenue & Royalty Allocation: The counterparty stipulates a ${(deal.labelCut * 100).toInt().coerceAtLeast(85)}% royalty cut, allocating only ${(100 - (deal.labelCut * 100).toInt()).coerceAtLeast(15)}% to you.\n" +
                        "• Commercial Encumbrance: Exclusivity covenants bind your future catalog, live performance, and brand rights under long-term option periods.\n" +
                        "• Assessment: The contractual balance heavily favors the counterparty with substantial downside risk to your long-term independence and royalty stream."
                    } else {
                        "LEGAL MEMORANDUM\n\n" +
                        "To: ${state.artistName}\n" +
                        "From: ${lawyer.name}, Legal Counsel\n" +
                        "Subject: Contract Audit: '${deal.subject}' (${deal.sender})\n\n" +
                        "Summary of Core Clauses:\n" +
                        "• Financial Terms: Outlines compensation of \$${String.format("%,.0f", deal.cashOffer)} tied to standard promotional deliverables.\n" +
                        "• Royalty & Rights Terms: Revenue splits of ${(deal.labelCut * 100).toInt()}% align with standard commercial parameters for an artist at your market scale.\n" +
                        "• Operating Covenants: Requires maintaining consistent brand representation and fulfilling scheduled deliverables during the term.\n" +
                        "• Assessment: Standard commercial agreement within typical industry parameters."
                    }
                    updatedMail.add(
                        MailMessage(
                            id = "lawyer_review_${deal.id}",
                            sender = "${lawyer.name} (Lawyer Review)",
                            subject = "⚖ Legal Analysis: Contract ${deal.id}",
                            body = analysisBody,
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 3,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("⚖ Legal Advisory: Your Lawyer ${lawyer.name} delivered a legal audit regarding '${deal.sender}' to your Mailbox.\n")
                }
            }

            // A. Low Reputation Penalties: Active sponsorship withdrawal & brand fallout
            if (currentBusinessReputation < 40f) {
                val hadFeatureOffers = updatedMail.any { (it.type == "FEATURE" || it.type == "BRAND_ENDORSEMENT") && !it.isAccepted }
                if (hadFeatureOffers) {
                    updatedMail.removeAll { (it.type == "FEATURE" || it.type == "BRAND_ENDORSEMENT") && !it.isAccepted }
                    updatedMail.add(
                        MailMessage(
                            id = "withdrawn_" + UUID.randomUUID().toString().take(6),
                            sender = "Brand Council",
                            subject = "Notice: Partnership Inquiries Withdrawn",
                            body = "Dear ${state.artistName},\n\nDue to reports of unreliability, disputed contractor accounts, and non-compliance with professional commitments, commercial partners have withdrawn pending partnership inquiries.\n\nCommercial trust is essential for brand collaborations.",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("⚠ Notice: Commercial partners withdrew pending proposals due to poor business reputation metrics.\n")
                    updatedCash -= 2000.0
                }
            }

            // B. High Reputation Sponsorships (Luxury Brands, Banks, Sponsors)
            val releasedSongs = state.songs.filter { it.isReleased }
            val totalStreams = state.songs.sumOf { it.totalStreams }
            val hasProvenCareer = releasedSongs.size >= 2 && totalStreams >= 30_000L && state.fans >= 15_000L && state.popularity >= 30f
            if (currentBusinessReputation >= 65f && (hasProvenCareer || state.popularity >= 25f) && Random.nextFloat() > 0.6f) {
                val brandName = listOf("Aura Tech Global", "Valerius Haute Horlogerie", "Titan Capital", "Apex Commercial Bank", "Vanguard Media Syndicate").random()
                val luxuryOffer = 25000.0 + (state.popularity * 800.0) + (currentBusinessReputation * 400.0)
                val brandSubject = "Brand Endorsement Proposal: $brandName"
                val brandBody = "Dear ${state.artistName},\n\nWe have been following your career trajectory and catalog growth. $brandName is seeking a brand ambassador who embodies professional excellence and integrity. We are pleased to offer a commercial sponsorship package of \$${String.format("%,.0f", luxuryOffer)}.\n\nWe look forward to confirming your participation."
                updatedMail.add(
                    MailMessage(
                        id = "brand_" + UUID.randomUUID().toString().take(6),
                        sender = brandName,
                        subject = brandSubject,
                        body = brandBody,
                        type = "BRAND_ENDORSEMENT",
                        cashOffer = luxuryOffer,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("✉ New Mail: Received a brand endorsement proposal from $brandName.\n")
            }

            // Dedicated Alphadominity Pinnacle Brand Ambassador Milestone from David Temitope
            // An exceptionally difficult, historic career milestone reserved for legendary cultural masters
            val hasEverReceivedDavidDeal = state.mailbox.any { it.sender.contains("David Temitope") } ||
                    state.spiritualJournal.contains("david_temitope_milestone_unlocked") ||
                    updatedMail.any { it.sender.contains("David Temitope") }
            val hasFiveMillionStreamHits = state.songs.count { it.isReleased && it.totalStreams >= 1_000_000L } >= 5
            val hasAchievedDavidMilestone = state.fans >= 300_000L &&
                    state.followers >= 150_000L &&
                    state.meTubeSubscribers >= 250_000L &&
                    currentIntegrity >= 85f &&
                    currentIndustryRespect >= 85f &&
                    state.playerLevel >= 8 &&
                    hasFiveMillionStreamHits

            if (!hasEverReceivedDavidDeal && hasAchievedDavidMilestone) {
                val alphadomOffer = 500_000.0 + (state.popularity * 1500.0) + (currentBusinessReputation * 800.0)
                updatedSpiritualJournal.add("david_temitope_milestone_unlocked")
                updatedMail.add(
                    MailMessage(
                        id = "brand_alphadom_milestone",
                        sender = "David Temitope (Founder & CEO, Alphadominity)",
                        subject = "🏆 PINNACLE CULTURAL FELLOWSHIP: Alphadominity Global Ambassadorship",
                        body = "Dear ${state.artistName.ifEmpty { "Artist" }},\n\nI am David Temitope, founder of Alphadominity — building visionary AI learning architectures, creative intelligence platforms, and foundational technologies.\n\nOver the past years, my research team and I have monitored the global cultural landscape. We track thousands of creators, but our investment thesis is uncompromising: we only partner with cultural leaders who demonstrate exceptional discipline, unbroken artistic integrity (≥85%), a proven catalog of multi-million stream masterpieces, and genuine community reverence.\n\nYou have achieved what few in this generation ever accomplish: genuine cultural mastery without compromising your principles.\n\nAlphadominity hereby extends an exclusive Global Cultural Ambassadorship fellowship grant of \$${String.format("%,.0f", alphadomOffer)}.\n\nThis is not a commercial product placement — it is a recognition of exceptional creative, ethical, and entrepreneurial leadership. We welcome you to our fellowship of pioneers.\n\nWith utmost respect,\nDavid Temitope\nFounder & CEO, Alphadominity",
                        type = "BRAND_ENDORSEMENT",
                        cashOffer = alphadomOffer,
                        expiresWeek = state.currentWeek + 8,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("🌟 LEGENDARY MILESTONE: David Temitope, founder of Alphadominity, recognized your cultural mastery with an exclusive \$${String.format("%,.0f", alphadomOffer)} Global Fellowship Ambassadorship! Check your Mailbox.\n")
            }

            // C. Opportunity Discovery from Exposure Networks (NPC Introductions)
            val closeFriends = state.npcs.filter { it.relationshipWithPlayer >= 70f && !it.isRetired }
            if (closeFriends.isNotEmpty() && Random.nextFloat() > 0.65f) {
                val friend = closeFriends.random()
                val hasProvenTalent = releasedSongs.isNotEmpty() && (totalStreams >= 5_000L || state.writingSkill >= 50f)
                if (hasProvenTalent) {
                    val referralPay = 1500.0 + (friend.popularity * 15.0)
                    updatedMail.add(
                        MailMessage(
                            id = "referral_" + UUID.randomUUID().toString().take(6),
                            sender = "${friend.name} (Referral)",
                            subject = "Private Songwriting Engagement",
                            body = "Hey ${state.artistName},\n\nOur colleague ${friend.name} shared your catalog with our production team. Based on your musical work, we would like to contract you for a private songwriting session. Compensation is \$${String.format("%,.0f", referralPay)} upon completion.",
                            type = "NORMAL",
                            cashOffer = referralPay,
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("✉ Referral Gig: '${friend.name}' connected you with a private songwriting engagement.\n")
                } else {
                    reportBuilder.append("🤝 Network Feedback: ${friend.name} pitched your name to an industry collaborator, but they declined: 'They haven't proved themselves with a solid catalog yet. Tell them to release more music first.'\n")
                }
            }

            // D. Standard Record label offers (based on popularity, influence, and catalog proof)
            val isLabelReady = releasedSongs.size >= 2 && totalStreams >= 50_000L && state.popularity >= 45f && state.influence >= 35f && state.signedLabelId == null
            if (isLabelReady && Random.nextFloat() > 0.65f) {
                val labelCut = (Random.nextInt(18, 32) / 100f) - (currentBusinessReputation / 1000f)
                val adjustedCut = labelCut.coerceIn(0.12f, 0.35f)

                data class LabelContractSpec(
                    val name: String,
                    val template: String,
                    val levelText: String,
                    val level: Int
                )

                val labelPool = listOf(
                    LabelContractSpec("Vertex Media Group", "VERTEX", "major", 3),
                    LabelContractSpec("Grime City Records", "GRIME_CITY", "street-certified indie", 2),
                    LabelContractSpec("Titan Records", "TITAN", "premier major", 3),
                    LabelContractSpec("Indie Sound Collective", "INDIE", "boutique indie", 1)
                )
                val chosenLabel = labelPool.random()

                val contractId = UUID.randomUUID().toString()
                val advanceOffer = (state.popularity * 800.0 + state.influence * 600.0).coerceIn(30000.0, 120000.0)
                val reqSingles = Random.nextInt(2, 5)
                val reqLP = if (state.popularity > 55f) 1 else 0
                val reqEP = if (reqLP == 0) 1 else 0
                val reqVideos = Random.nextInt(2, 4)
                val contractDurationWeeks = if (state.popularity > 65f) 104 else 52
                val durationYears = contractDurationWeeks / 52
                val endWeekCalc = if (state.currentWeek + contractDurationWeeks > 52) (state.currentWeek + contractDurationWeeks - 52) else state.currentWeek + contractDurationWeeks
                val endYearCalc = if (state.currentWeek + contractDurationWeeks > 52) state.currentYear + (contractDurationWeeks / 52) else state.currentYear

                val contractBody = when (chosenLabel.template) {
                    "VERTEX" -> "VERTEX MEDIA GROUP — Recorded Music Division — Artist Services Agreement\n\nThis Artist Services Agreement is entered into between ${chosenLabel.name}, a ${chosenLabel.levelText} recorded-music entity, and ${state.artistName}, to be engaged as Recording Artist.\n\nTerm: Week ${state.currentWeek}, Year ${state.currentYear} to Week $endWeekCalc, Year $endYearCalc.\n\nDeliverables:\n• Singles: $reqSingles\n• Albums (LP): $reqLP\n• Album (EP): $reqEP\n• MeTube videos: $reqVideos\n\nAdvance & Recoupment: \$${String.format("%,.0f", advanceOffer)} advance, ${(adjustedCut*100).toInt()}% recoupment cut."
                    "GRIME_CITY" -> "GRIME CITY RECORDS — STREET CO-SIGNED • NO SOFT HANDS\n\nYo ${state.artistName},\nWord's been going around about you and we don't co-sign just anybody. ${chosenLabel.name} is running things as a ${chosenLabel.levelText} label out here, and we're offering you a spot as Recording Artist.\n\nDeliverables:\n• $reqSingles singles\n• $reqLP albums (LP)\n• $reqEP album (EP)\n• $reqVideos MeTube videos\n\nMoney: Fronting you \$${String.format("%,.0f", advanceOffer)}, ${(adjustedCut*100).toInt()}% cut."
                    "TITAN" -> "TITAN RECORDS — The Standard the Industry Measures Itself Against\n\nDear ${state.artistName},\nIt is with great pleasure that Titan Records extends this formal offer of representation as Recording Artist under ${chosenLabel.name}.\n\nDeliverables:\n• $reqSingles singles\n• $reqLP albums (LP)\n• $reqEP album (EP)\n• $reqVideos MeTube promotional videos\n\nAdvance: \$${String.format("%,.0f", advanceOffer)}, ${(adjustedCut*100).toInt()}% recoupment deduction."
                    else -> "Indie Sound Collective — small label, big ears\n\nHey ${state.artistName},\nWe've been following what you've been putting out lately and honestly, we're into it. We'd love to have you join ${chosenLabel.name} as Recording Artist.\n\nDeliverables:\n• $reqSingles singles\n• $reqLP albums (LP)\n• $reqEP album (EP)\n• $reqVideos MeTube videos\n\nAdvance: \$${String.format("%,.0f", advanceOffer)}, ${(adjustedCut*100).toInt()}% cut."
                }

                updatedMail.add(
                    MailMessage(
                        id = contractId,
                        sender = chosenLabel.name,
                        subject = "Recording Agreement Proposal - ${chosenLabel.name}",
                        body = contractBody,
                        type = "LABEL_OFFER",
                        cashOffer = advanceOffer,
                        expiresWeek = state.currentWeek + 4,
                        expiresYear = state.currentYear,
                        labelCut = adjustedCut,
                        labelId = chosenLabel.name.lowercase().replace(" ", "_"),
                        labelRole = "Recording Artist",
                        labelLevel = chosenLabel.level,
                        contractDurationWeeks = contractDurationWeeks,
                        startWeek = state.currentWeek,
                        startYear = state.currentYear,
                        endWeek = endWeekCalc,
                        endYear = endYearCalc,
                        reqSingles = reqSingles,
                        reqAlbumsLP = reqLP,
                        reqAlbumsEP = reqEP,
                        reqMeTubeVideos = reqVideos,
                        contractTemplate = chosenLabel.template,
                        labelLevelText = chosenLabel.levelText
                    )
                )
                reportBuilder.append("✉ New Mail: ${chosenLabel.name} submitted a Recording Agreement Proposal (${chosenLabel.template}) to your Mailbox.\n")
            }

            // E. Rare high-status events (for verified superstars)
            val isSuperstarLevel = releasedSongs.size >= 3 && totalStreams >= 250_000L && state.popularity > 75f && state.publicImage > 70f
            if (isSuperstarLevel && Random.nextFloat() > 0.88f) {
                val roll = Random.nextInt(3)
                if (roll == 0) {
                    updatedMail.add(
                        MailMessage(
                            id = "world_cup_" + UUID.randomUUID().toString().take(6),
                            sender = "Global Sports Assoc.",
                            subject = "World Cup Official Anthem Selection",
                            body = "Dear ${state.artistName},\n\nYou have been selected to compose and perform the official World Cup Anthem. Upfront compensation is \$120,000 upon contract execution. Global broadcast reach will significantly expand your international audience.",
                            type = "HIGH_STATUS_GIG",
                            cashOffer = 120000.0,
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("🌟 High-Profile Invitation: Invited to create the official World Cup Anthem. Check Mailbox.\n")
                } else if (roll == 1) {
                    updatedMail.add(
                        MailMessage(
                            id = "hollywood_" + UUID.randomUUID().toString().take(6),
                            sender = "Paramount Studios",
                            subject = "Feature Film Soundtrack Theme Commission",
                            body = "Dear ${state.artistName},\n\nWe are producing an upcoming theatrical motion picture and wish to commission you for the original title track. Compensation is \$75,000 upfront plus soundtrack licensing royalties.",
                            type = "HIGH_STATUS_GIG",
                            cashOffer = 75000.0,
                            expiresWeek = state.currentWeek + 3,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("🌟 Studio Inquiry: Paramount Studios reached out to license a soundtrack theme from you. Check Mailbox.\n")
                } else {
                    updatedMail.add(
                        MailMessage(
                            id = "royal_" + UUID.randomUUID().toString().take(6),
                            sender = "The Royal Secretariat",
                            subject = "Royal Command Performance Invitation",
                            body = "Dear ${state.artistName},\n\nIt is our privilege to invite you to perform before dignitaries at the upcoming Royal Charity Gala. An honorarium of \$90,000 is provided alongside high-level cultural exposure.",
                            type = "ROYAL_PERFORMANCE",
                            cashOffer = 90000.0,
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("🌟 Formal Invitation: Invited to perform at the Royal Charity Gala. Check Mailbox.\n")
                }
            }

            // F. Regular feature requests & early-stage showcase slot offers
            val isFeatureReady = state.fans >= 20_000L && state.popularity >= 25f && releasedSongs.size >= 2 && totalStreams >= 15_000L
            if (isFeatureReady && Random.nextFloat() > 0.72f) {
                val feeOffer = Random.nextDouble(4000.0, 18000.0)
                val peerArtist = state.npcs.filter { !it.isRetired && it.popularity >= 20f }.randomOrNull()?.name ?: "Jordan Cole"
                updatedMail.add(
                    MailMessage(
                        id = UUID.randomUUID().toString(),
                        sender = peerArtist,
                        subject = "Collaboration Feature Inquiry",
                        body = "Hello ${state.artistName},\n\nI've been listening to your recent releases and respect your sound. I'm finishing a single for my next project and would like to feature you as a guest artist. We have budgeted a \$${String.format("%.2f", feeOffer)} collaboration fee. Let me know if you're interested.",
                        type = "FEATURE",
                        cashOffer = feeOffer,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("✉ New Mail: Collaboration inquiry received from $peerArtist in your Mailbox.\n")
            } else if (isEarlyStageArtist && Random.nextFloat() < 0.08f && !updatedMail.any { it.id.startsWith("early_slot_") }) {
                // At early stage, promoters try to exploit the unproven artist with pay-to-play showcase fees
                updatedMail.add(
                    MailMessage(
                        id = "early_slot_" + UUID.randomUUID().toString().take(6),
                        sender = "Apex Showcase Circuit",
                        subject = "Emerging Artist Showcase Performance Slot",
                        body = "Dear ${state.artistName},\n\nOur talent scouting team tracks unsigned acts for our upcoming indie showcase concert. We have one open slot on the stage roster. To confirm production staging, backline audio, and engineering fees, artist registration is \$1,200. Let us know if you wish to confirm your booking.",
                        type = "NORMAL",
                        cashOffer = -1200.0,
                        expiresWeek = state.currentWeek + 2,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("✉ New Mail: Apex Showcase Circuit sent a performance slot offer to your Mailbox.\n")
            }

            // 11. Fully Simulated NPC Careers (Mentorship, Rivals & Competitive Dynamics with Human Psychology)
            val npcSimResult = NpcSimulationHelper.processWeeklyNpcs(
                state = state,
                nextWeek = nextWeek,
                initialStress = currentStress,
                initialPublicImage = currentPublicImage,
                initialVoice = updatedVoice,
                initialWriting = updatedWriting,
                initialHappiness = currentHappiness,
                updatedSongs = updatedSongs,
                updatedMail = updatedMail,
                reportBuilder = reportBuilder
            )
            val updatedNPCs = npcSimResult.updatedNPCs
            val finalNPCs = updatedNPCs.toMutableList()
            currentStress = npcSimResult.finalStress
            currentPublicImage = npcSimResult.finalPublicImage
            updatedVoice = npcSimResult.finalVoice
            updatedWriting = npcSimResult.finalWriting
            currentHappiness = npcSimResult.finalHappiness

            // 12. Update Player's Mental Health Metrics

            // Side jobs & Active tours impact stress/balance
            val totalStressLoad = (state.sideJobs.filter { it.isActive }.size * 6f) + (activeTours.size * 12f)
            currentStress = (currentStress + totalStressLoad).coerceIn(0f, 100f)
            // --- EMPLOYEE LOYALTY & DEMANDS ---
            var updatedEmployees = state.hiredEmployees.map { emp ->
                var updatedLoyalty = emp.loyalty
                // Loyalty decays naturally depending on the employee's greed vs their salary/artist wealth
                val greedDecay = (emp.greed / 100f) * 1.5f
                updatedLoyalty = (updatedLoyalty - greedDecay).coerceAtLeast(0f)

                // If loyalty drops below 25, there's a chance they resign
                if (updatedLoyalty < 25f && kotlin.random.Random.nextFloat() < 0.15f) {
                    reportBuilder.append("🚪 STAFF RESIGNATION: Your ${emp.role} (${emp.name}) resigned abruptly! Their loyalty dropped too low due to a lack of raises/bonuses or high greed.\n")
                    null // Will filter out nulls
                } else if (updatedLoyalty < 40f && kotlin.random.Random.nextFloat() < 0.10f) {
                    // They demand a raise, we simulate it by sending a mail
                    val demandMailId = "raise_demand_${kotlin.random.Random.nextInt(1000, 9999)}"
                    updatedMail.add(
                        MailMessage(
                            id = demandMailId,
                            sender = "${emp.name} (${emp.role})",
                            subject = "Important: Compensation Review",
                            body = "Dear ${state.artistName},\n\nI've been working hard in my role as ${emp.role}, but market rates are higher than my current salary of $${String.format("%,.0f", emp.weeklySalary)}. If you don't grant me a raise or a bonus via the Team tab soon, I'll have to explore other opportunities.\n\nRegards,\n${emp.name}",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 4,
                            expiresYear = state.currentYear
                        )
                    )
                    emp.copy(loyalty = updatedLoyalty)
                } else {
                    emp.copy(loyalty = updatedLoyalty)
                }
            }.filterNotNull()

            currentWorkLifeBalance = (currentWorkLifeBalance - (totalStressLoad * 1.5f)).coerceIn(0f, 100f)

            // Dynamic Economic stress multipliers (struggle & real-world simulation)
            if (updatedCash < 0.0) {
                currentStress = (currentStress + 12f).coerceIn(0f, 100f)
                currentHappiness = (currentHappiness - 10f).coerceIn(5f, 100f)
                currentMotivation = (currentMotivation - 8f).coerceIn(5f, 100f)
                reportBuilder.append("🚨 FINANCIAL DISTRESS: Carrying a negative cash balance ($${String.format("%,.2f", updatedCash)}) triggers severe financial stress! Motivation and happiness collapsed.\n")
            }
            if (newTaxDebt > 8000.0) {
                currentStress = (currentStress + 5f).coerceIn(0f, 100f)
                reportBuilder.append("📈 TAX LIABILITY PANIC: Carrying a substantial tax liability of $${String.format("%,.2f", newTaxDebt)} causes chronic background anxiety.\n")
            }

            // Power of Networking: Compute average relationship score and its impact
            val activeNpcsForNetwork = finalNPCs.filter { !it.isRetired }
            val avgRelation = if (activeNpcsForNetwork.isNotEmpty()) activeNpcsForNetwork.map { it.relationshipWithPlayer }.average().toFloat() else 50f
            if (avgRelation > 72f) {
                currentIndustryRespect = (currentIndustryRespect + 1.2f).coerceAtMost(100f)
                updatedInfluence = (updatedInfluence + 1.0f).coerceAtMost(100f)
                reportBuilder.append("🤝 POWER OF NETWORKING: Your warm relationships with other artists are paying dividends. Positive gossip inside industry circles boosted your Respect (+1.2) and general Influence (+1.0)!\n")
            } else if (avgRelation < 38f) {
                currentIndustryRespect = (currentIndustryRespect - 1.5f).coerceAtLeast(0f)
                updatedInfluence = (updatedInfluence - 1.2f).coerceAtLeast(0f)
                reportBuilder.append("📉 COLD RELATIONSHIPS: Industry leaders and gatekeepers are growing cold toward you due to a weak relational network. Respect (-1.5) and general Influence (-1.2) decayed.\n")
            }

            // Rival Sabotage and Alliances System (Living Consequence Engine)
            // Relationship maintenance uses the same result for the report and saved state.
            val partnerUpdates = state.capitalClubMembers.map { member ->
                PartnerRelationshipSimulation.advanceWeek(member, state.currentWeek, state.currentYear)
            }
            partnerUpdates.filter { it.crossedNeglectThreshold }.forEach { update ->
                reportBuilder.append("💔 RELATIONSHIP ALERT: ${update.member.name} feels neglected. Spend time together to keep the relationship healthy.\n")
            }
            partnerUpdates.filter { it.brokeUp }.forEach { update ->
                reportBuilder.append("💔 BREAKUP: ${update.member.name} ended the relationship after a long period without meaningful time together.\n")
                updatedTimelineEvents = updatedTimelineEvents +
                    "Year ${state.currentYear}, Week ${state.currentWeek}: Your relationship with ${update.member.name} ended after a long period without meaningful time together."
            }
            val updatedCrushMembers = partnerUpdates.map { it.member }

            val rivalsList = finalNPCs.filter { !it.isRetired && it.relationshipWithPlayer < 25f }
            if (rivalsList.isNotEmpty() && Random.nextFloat() < 0.15f) {
                val chosenRival = rivalsList.random()
                val hasPR = state.hiredEmployees.find { it.role == "PR Manager" }
                val highValueFriend = finalNPCs.find { !it.isRetired && it.relationshipWithPlayer >= 80f && it.id != chosenRival.id }

                if (hasPR != null) {
                    reportBuilder.append("🎭 RIVAL ATTACK: Your rival, ${chosenRival.name}, launched a smear campaign accusing you of inflating numbers! BUT your PR Manager ${hasPR.name} defused the scandal instantly and turned it into viral exposure (+4% popularity).\n")
                } else if (highValueFriend != null) {
                    reportBuilder.append("🎭 RIVAL ATTACK: Your rival, ${chosenRival.name}, attacked your creative integrity publicly! BUT your powerful friend, ${highValueFriend.name}, immediately made a viral post defending you. The community rallied behind you (+2 Public Image)!\n")
                    currentPublicImage = (currentPublicImage + 2f).coerceAtMost(100f)
                } else {
                    currentPublicImage = (currentPublicImage - 8f).coerceAtLeast(0f)
                    updatedNegativeSentiment = (updatedNegativeSentiment + 12f).coerceAtMost(100f)
                    reportBuilder.append("🎭 RIVAL ATTACK: Your rival, ${chosenRival.name}, released a vicious teardown track about your style! With no PR Manager or loyal artist allies to defend you, the post went unchecked. Lost -8 Public Image and negative sentiment rose!\n")
                }
            }

            // Burnout trigger
            if (currentStress >= 70f) {
                currentBurnout = (currentBurnout + 8f).coerceAtMost(100f)
                currentInspiration = (currentInspiration - 12f).coerceAtLeast(5f)
                currentMotivation = (currentMotivation - 10f).coerceAtLeast(10f)
                currentHappiness = (currentHappiness - 8f).coerceAtLeast(10f)
                reportBuilder.append("⚠ BURNOUT WARNING: Your stress levels are extremely high! Writing skills restricted and creative inspiration choked.\n")
            } else {
                currentBurnout = (currentBurnout - 4f).coerceAtLeast(0f)
                currentInspiration = (currentInspiration + 4f).coerceAtMost(100f)
            }

            // Decay stress naturally each week if taking it easy
            if (totalStressLoad == 0f) {
                currentStress = (currentStress - 10f).coerceAtLeast(5f)
                currentHappiness = (currentHappiness + 4f).coerceAtMost(100f)
            }

            // 13. Social Media Ecosystem & Fan Segment Synchronization
            val previousFans = state.fans
            val gainedFans = (state.popularity * 2.5f + state.influence * 1.5f + (Random.nextFloat() * 15f)).toLong() + gainedFansFromPromo
            val totalFansNew = (previousFans + gainedFans).coerceAtLeast(200)

            // Dynamic distribution to psychological segments:
            val gainedPsyFans = (totalFansNew - state.fans).coerceAtLeast(0L)

            val wPersonality = 1.0f + (currentHappiness / 100f) + (currentTrustworthiness / 100f)
            val wLyrics = 1.0f + (state.writingSkill / 50f)
            val wVoice = 1.0f + (state.voiceSkill / 50f)
            val wFashion = 1.0f + (state.ownedCars.size * 0.15f) + (state.ownedHouses.size * 0.25f)
            val wControversy = 1.0f + (state.negativeSentiment / 40f)
            val wBusiness = 1.0f + (state.ownedBusinesses.size * 0.20f)
            val wCharity = 1.0f + (if (state.hasScholarships) 0.60f else 0.10f) + (state.generosity / 50f)

            val sumW = wPersonality + wLyrics + wVoice + wFashion + wControversy + wBusiness + wCharity

            val gainedPersonality = (gainedPsyFans * (wPersonality / sumW)).toLong()
            val gainedLyrics = (gainedPsyFans * (wLyrics / sumW)).toLong()
            val gainedVoice = (gainedPsyFans * (wVoice / sumW)).toLong()
            val gainedFashion = (gainedPsyFans * (wFashion / sumW)).toLong()
            val gainedControversy = (gainedPsyFans * (wControversy / sumW)).toLong()
            val gainedBusiness = (gainedPsyFans * (wBusiness / sumW)).toLong()
            val gainedCharity = (gainedPsyFans * (wCharity / sumW)).toLong()

            val updatedPersonalityLovers = state.fansPersonalityLovers + gainedPersonality
            val updatedLyricsLovers = state.fansLyricsLovers + gainedLyrics
            val updatedVoicePurists = state.fansVoicePurists + gainedVoice
            val updatedFashionistas = state.fansFashionistas + gainedFashion
            val updatedControversySeekers = state.fansControversySeekers + gainedControversy
            val updatedBusinessAdmirers = state.fansBusinessAdmirers + gainedBusiness
            val updatedCharityBelievers = state.fansCharityBelievers + gainedCharity

            // Segment distribution formulas
            val updatedCasual = (totalFansNew * 0.55).toLong()
            val updatedHardcore = (totalFansNew * 0.15).toLong() // Loyal Fans
            val updatedCollectors = (totalFansNew * 0.05).toLong()
            val updatedConcertGoers = (totalFansNew * 0.15).toLong()
            val updatedInternational = (totalFansNew * 0.06).toLong()
            val updatedOnline = (totalFansNew * 0.04).toLong()

            // Redesigned Social Media Ecosystem Updates
            updatedFollowers = (updatedFollowers + (gainedFans * 1.6).toLong()).coerceAtLeast(1500L)
            val updatedIndustryFollowers = (state.industryFollowers + (currentIndustryRespect * 0.15f + currentCriticalAcclaim * 0.05f).toLong()).coerceIn(10L, 50000L)

            val activeTourBonus = if (activeTours.isNotEmpty()) 15f else 0f
            val viralBonus = if (viralHitOccurred) 35f else 0f
            val updatedTrendingScore = ((state.trendingScore * 0.72f) + (gainedFans / 350f) + activeTourBonus + viralBonus + trendingPromoBoost).coerceIn(0f, 100f)

            currentPublicImage = (currentPublicImage + publicImagePromoBoost).coerceIn(0f, 100f)
            currentIndustryRespect = (currentIndustryRespect + industryRespectPromoBoost).coerceIn(0f, 100f)
            updatedVoice = (updatedVoice + voicePromoBoost).coerceIn(0f, 100f)

            val updatedConversationVolume = (updatedFollowers * 0.005f * (1f + updatedTrendingScore / 60f)).toLong().coerceAtLeast(50L)

            // Dynamic Public Sentiment updates
            val basePosSentiment = (currentPublicImage * 0.6f + currentTrustworthiness * 0.4f).coerceIn(5f, 100f)
            val baseNegSentiment = ((100f - currentTrustworthiness) * 0.5f + (if (currentStress > 70f) 18f else 0f) + (if (accruedDebts > 0.0) 15f else 0f)).coerceIn(0f, 100f)
            val updatedPositiveSentiment = basePosSentiment
            updatedNegativeSentiment = baseNegSentiment
            val updatedNeutralSentiment = (100f - updatedPositiveSentiment - updatedNegativeSentiment).coerceIn(0f, 100f)

            // Stewardship Character Attributes & Tax/Tithe Consequences System
            val disciplineGain = if (practiceMissed == 0) 0.6f else -1.2f
            var updatedDiscipline = (currentDiscipline + disciplineGain).coerceIn(10f, 100f)

            val reliabilityGain = if (accruedDebts <= 0.0) 0.5f else -1.5f
            var updatedReliability = (currentReliability + reliabilityGain).coerceIn(10f, 100f)

            var updatedIntegrity = currentIntegrity.coerceIn(10f, 100f)
            var updatedGenerosity = (currentGenerosity - 0.05f).coerceIn(10f, 100f)
            var updatedHumility = currentHumility.coerceIn(10f, 100f)

            // Consecutive Unpaid Weeks State Updating
            var consecutiveTaxWeeks = state.consecutiveUnpaidTaxWeeks
            if (state.taxDebt > 10.0) {
                consecutiveTaxWeeks += 1
            } else {
                consecutiveTaxWeeks = 0
            }

            var consecutiveTitheWeeks = state.consecutiveUnpaidTitheWeeks
            if (state.titheDebt > 5.0) {
                consecutiveTitheWeeks += 1
            } else {
                consecutiveTitheWeeks = 0
            }

            // Weekly expected accruals if the player earned positive revenue (Music, Business Ventures, Stock & Corporate Dividends)
            val totalBusinessIncome = ventureSimResult.totalPlayerProfitCash
            val totalDividendsIncome = marketSimResult.addedSalary + labelPlayerDividends
            val totalCombinedWeeklyIncome = weeklySalary + totalBusinessIncome + labelCeoPaycheck + labelPlayerDividends

            if (totalCombinedWeeklyIncome > 0.0) {
                val currentTaxRate = (0.12f + state.politicalTaxModifier).coerceIn(0.01f, 0.35f)
                val weeklyTaxDue = totalCombinedWeeklyIncome * currentTaxRate
                newTaxDebt += weeklyTaxDue

                val weeklyTitheDue = totalCombinedWeeklyIncome * 0.10 // 10% Expected Tithe
                newTitheDebt += weeklyTitheDue

                reportBuilder.append("📉 Weekly Tax & Tithe Accruals: Earned $${String.format("%,.2f", totalCombinedWeeklyIncome)} total (Gigs/Salary: $${String.format("%,.2f", weeklySalary)}, Business Income: $${String.format("%,.2f", totalBusinessIncome)}, Dividends: $${String.format("%,.2f", totalDividendsIncome)}). Added +$${String.format("%,.2f", weeklyTaxDue)} (${String.format("%.0f", currentTaxRate * 100)}% Tax) and +$${String.format("%,.2f", weeklyTitheDue)} (10% Tithe) to balances.\n")
            }

            // Unpaid Tax consequences (Decreases traits only after 4 consecutive weeks straight)
            if (consecutiveTaxWeeks >= 4) {
                currentBusinessReputation = (currentBusinessReputation - 1.5f).coerceAtLeast(5f)
                currentTrustworthiness = (currentTrustworthiness - 1.0f).coerceAtLeast(5f)
                reportBuilder.append("⚠ Tax Hold: Outstanding tax debt of $${String.format("%,.2f", newTaxDebt)} has been unpaid for $consecutiveTaxWeeks weeks straight, decaying your Business Reputation (-1.5%) and Trustworthiness (-1.0%)!\n")
            }
            if (consecutiveTaxWeeks >= 6) {
                currentPublicImage = (currentPublicImage - 2.5f).coerceAtLeast(5f)
                currentStress = (currentStress + 4.0f).coerceAtMost(100f)
                reportBuilder.append("🚨 IRS Warning: Continued tax delinquency of $consecutiveTaxWeeks weeks straight is causing public brand damage (-2.5% Public Image) and escalating Stress (+4.0%)!\n")
            }
            if (consecutiveTaxWeeks >= 8) {
                // Garnishment of current week's salary
                val garnishment = (weeklySalary * 0.25).coerceAtMost(newTaxDebt)
                if (garnishment > 0.0) {
                    weeklySalary -= garnishment
                    newTaxDebt -= garnishment
                    reportBuilder.append("🏛 IRS Garnishment: Your tax delinquency has exceeded 8 weeks straight! The tax authority has garnished 25% ($${String.format("%,.2f", garnishment)}) of this week's payouts directly to settle back taxes.\n")
                }
            }

            // Unpaid Tithe consequences (Spiritual Life) (Decreases traits only after 4 consecutive weeks straight)
            if (consecutiveTitheWeeks >= 4) {
                updatedGenerosity = (updatedGenerosity - 1.5f).coerceAtLeast(10f)
                updatedIntegrity = (updatedIntegrity - 1.2f).coerceAtLeast(10f)
                updatedHumility = (updatedHumility - 0.8f).coerceAtLeast(10f)
                currentInspiration = (currentInspiration - 3f).coerceAtLeast(5f)
                reportBuilder.append("🙏 Spiritual Friction: High unpaid tithes ($${String.format("%,.2f", newTitheDebt)}) have been unpaid for $consecutiveTitheWeeks weeks straight, reducing Generosity (-1.5%), Integrity (-1.2%), and Creative Inspiration (-3.0%)!\n")
            }
            if (consecutiveTitheWeeks >= 6) {
                currentStress = (currentStress + 3.5f).coerceAtMost(100f)
                currentHappiness = (currentHappiness - 4.0f).coerceAtLeast(10f)
                reportBuilder.append("🌪 Devourer Active: Unsettled tithes ($consecutiveTitheWeeks weeks straight) are causing mental pressure (+3.5% Stress) and eroding Happiness (-4.0%).\n")
            }

            // 14. Multiple Reputation Shifts & Word of Mouth Boosts
            if (state.practiceMissedWeeks >= 2) {
                currentIndustryRespect = (currentIndustryRespect - 1.5f).coerceAtLeast(5f)
            }
            if (completedToursCount > 0) {
                currentIndustryRespect = (currentIndustryRespect + 8f).coerceAtMost(100f)
                currentPublicImage = (currentPublicImage + 5f).coerceAtMost(100f)
                currentLegacyPoints += 50
            }

            // Word of Mouth marketing influence: Loyal fans * Sentiment creates organic fan drift!
            val wordOfMouthGrowth = (updatedHardcore * 0.03 * (updatedPositiveSentiment / 50f)).toLong()
            val finalFansNew = (totalFansNew + wordOfMouthGrowth).coerceAtLeast(200)

            // Dynamic Public Opinion influences weekly stats (shifts public image slightly based on sentiment)
            val publicOpinionShift = (updatedPositiveSentiment - updatedNegativeSentiment) * 0.04f
            currentPublicImage = (currentPublicImage + publicOpinionShift).coerceIn(10f, 100f)

            // 7g. Real-Time Dynamic Weekly News Articles Generator (PulseWire)
            val highestTrendGenreEntry = currentTrends.maxByOrNull { it.value }
            val highestTrendGenreId = highestTrendGenreEntry?.key ?: "genre_pop"
            val highestTrendGenreName = GameData.GENRES.find { it.id == highestTrendGenreId }?.name ?: "Synthwave"

            val trendGenreId = highestTrendGenreId
            val trendSubgenreName = trendSubgenreForGenre(trendGenreId)
            val trendGenreName = GameData.GENRES.find { it.id == trendGenreId }?.name ?: "Pop"

            val marketSentimentInfo = calculateMarketSentiment(economyCycle, updatedStocks, updatedCryptos)

            val generatedNews = generateWeeklyNewsArticles(
                nextWeek = nextWeek,
                nextYear = nextYear,
                highestTrendPercent = (((highestTrendGenreEntry?.value ?: 1.0f) - 1.0f) * 100).coerceAtLeast(0f),
                trendSubgenreName = trendSubgenreName,
                trendGenreName = trendGenreName,
                trendWeeksRemaining = genreTrendUpdate.weeksRemaining,
                economyCycle = economyCycle,
                inflationRate = inflationRate,
                fuelPrice = fuelPrice,
                isElectionSeason = isElectionSeason,
                sentimentInfo = marketSentimentInfo
            )

            val finalChatConversations = state.chatConversations.toMutableList()

            // --- SOUNDSTREAM MUSIC PLATFORM SIMULATION ---
            val soundstreamSongsList = state.soundstreamSongs.toMutableList()
            if (soundstreamSongsList.isEmpty()) {
                val preGenArtists = listOf(
                    Triple("Travis Scottie", "Hip Hop", "Astro Hyperspace"),
                    Triple("Drake Drake", "Hip Hop", "Midnight in Toronto"),
                    Triple("Kanye East", "Hip Hop", "Graduation Day 2"),
                    Triple("Eminem-y", "Hip Hop", "Rap Renegade"),
                    Triple("Aria Nova", "Pop", "Starlight Serenade"),
                    Triple("Lady Gogo", "Pop", "Electro Monster"),
                    Triple("Billie Eyelash", "Indie", "Whispers in the Dark"),
                    Triple("Post Malone-y", "Pop", "Sunflower Echoes"),
                    Triple("Vince", "Hip Hop", "No Regrets"),
                    Triple("Ademola", "Afro", "Lagos Vibe City"),
                    Triple("DJ Pulse", "Electronic", "Neon Frequency"),
                    Triple("Jax Riff", "Rock", "Overdrive Soul"),
                    Triple("Seraphina", "R&B", "Golden Dawn"),
                    Triple("Alabaster Rock", "Rock", "Iron & Velvet"),
                    Triple("Amadeus Neo", "Electronic", "Symphony 2099"),
                    Triple("Elysian Sky", "Indie", "Cloudburst"),
                    Triple("Cyrus King", "Hip Hop", "Crown of Gold"),
                    Triple("Bob Marley-esque", "Reggae", "One Love Vibration"),
                    Triple("Kid Laroi-esque", "Pop", "Stay Forever"),
                    Triple("Busta Rhymes", "Hip Hop", "Rapid Fire Flow"),
                    Triple("Mia Flow", "Hip Hop", "Flow State"),
                    Triple("Ryan Sparks", "Pop", "Ignite The Night"),
                    Triple("Ian Woods", "Indie", "Cabin Acoustics"),
                    Triple("Chloe Bloom", "Pop", "Petals in the Wind"),
                    Triple("Shadow Lyricist", "Hip Hop", "Midnight Monologue")
                )
                val initialTiers = listOf(
                    18500000L, 15200000L, 12800000L, 10400000L, 8600000L,
                    6900000L, 5400000L, 4200000L, 3300000L, 2500000L,
                    1800000L, 1300000L, 950000L, 720000L, 540000L,
                    420000L, 330000L, 260000L, 200000L, 150000L,
                    120000L, 95000L, 75000L, 60000L, 45000L
                )
                preGenArtists.forEachIndexed { idx, art ->
                    val bStreams = initialTiers.getOrElse(idx) { (45000L - (idx - 24) * 1500L).coerceAtLeast(15000L) }
                    soundstreamSongsList.add(
                        ChartSong(
                            id = "ss_init_${idx}",
                            title = art.third,
                            artistName = art.first,
                            artistId = "npc_init_${idx}",
                            genreName = art.second,
                            rating = (4.9f - idx * 0.05f).coerceIn(2.5f, 5.0f),
                            weeklyStreams = bStreams,
                            totalStreams = bStreams * (idx % 6 + 4),
                            weeksOnChart = (idx % 8 + 1),
                            peakRank = idx + 1,
                            currentRank = idx + 1,
                            isPlayer = false,
                            releasedWeek = 1,
                            releasedYear = 1
                        )
                    )
                }
            }

            // Only singles actually released by the weekly NPC decision engine enter the chart.
            npcSimResult.releasedSingles.forEach { release ->
                val releaseGenreId = GameData.GENRES.find { it.name.equals(release.genre, ignoreCase = true) }?.id
                val releaseTrendMultiplier = releaseGenreId?.let { currentTrends[it] } ?: 1f
                val launchStreams = (release.firstWeekStreams * releaseTrendMultiplier).toLong().coerceAtLeast(1L)
                soundstreamSongsList.add(
                    ChartSong(
                        id = "ss_npc_${release.artistId}_${nextWeek}_${nextYear}_${release.title.hashCode()}",
                        title = release.title,
                        artistName = release.artistName,
                        artistId = release.artistId,
                        genreName = release.genre,
                        rating = release.rating,
                        weeklyStreams = launchStreams,
                        totalStreams = launchStreams,
                        weeksOnChart = 1,
                        peakRank = 999,
                        isPlayer = false,
                        releasedWeek = nextWeek,
                        releasedYear = nextYear,
                        hype = release.hype,
                        lastGenreTrendMultiplier = releaseTrendMultiplier
                    )
                )
            }

            // Player Releases Sync & Weekly Streams Simulation
            var totalPlayerWeeklyStreams = 0L
            var simulatedSoundstreamSongsList = soundstreamSongsList.map { cs ->
                val newWeeksOnChart = cs.weeksOnChart + 1
                if (cs.isPlayer) {
                    val weeklyStreams = playerSongsWeeklyStreamsMap[cs.id] ?: (cs.totalStreams / 10L).coerceAtLeast(500L)
                    totalPlayerWeeklyStreams += weeklyStreams
                    val newTotal = cs.totalStreams + weeklyStreams
                    cs.copy(
                        weeklyStreams = weeklyStreams,
                        totalStreams = newTotal,
                        weeksOnChart = newWeeksOnChart,
                        lastGenreTrendMultiplier = GameData.GENRES.find { it.name.equals(cs.genreName, true) }?.id?.let { currentTrends[it] } ?: 1f
                    )
                } else {
                    val npc = updatedNPCs.find { it.id == cs.artistId }
                    val isMasterpiece = cs.rating >= 4.4f
                    val baseDecay = if (isMasterpiece) {
                        (0.93f - (cs.weeksOnChart * 0.008f)).coerceIn(0.70f, 0.96f)
                    } else {
                        (0.86f - (cs.weeksOnChart * 0.015f)).coerceIn(0.50f, 0.92f)
                    }
                    val playlistPush = Random.nextFloat() < 0.12f
                    val radioSurge = Random.nextFloat() < 0.08f
                    val decayMult = when {
                        playlistPush -> {
                            if (Random.nextFloat() < 0.25f) {
                                reportBuilder.append("📈 Playlist Push: '${cs.title}' by ${cs.artistName} was added to a major streaming editorial playlist!\n")
                            }
                            1.35f
                        }
                        radioSurge -> 1.20f
                        else -> baseDecay
                    }

                    val tourBoost = if (npc?.isOnTour == true) 1.25f else 1.0f
                    val chartGenreId = GameData.GENRES.find { it.name.equals(cs.genreName, ignoreCase = true) }?.id
                    val genreTrendVal = chartGenreId?.let { currentTrends[it] } ?: 1.0f
                    val trendBoost = (genreTrendVal / cs.lastGenreTrendMultiplier.coerceAtLeast(0.1f)).coerceIn(0.6f, 1.7f)

                    val newWeeklyStreams = (cs.weeklyStreams * decayMult * tourBoost * trendBoost).toLong().coerceAtLeast(300L)
                    cs.copy(
                        weeklyStreams = newWeeklyStreams,
                        totalStreams = cs.totalStreams + newWeeklyStreams,
                        weeksOnChart = newWeeksOnChart,
                        lastGenreTrendMultiplier = genreTrendVal
                    )
                }
            }.toMutableList()

            // Sync all player releases uploaded to platforms
            updatedSongs.forEach { s ->
                if (s.isUploadedMap.isNotEmpty() && !simulatedSoundstreamSongsList.any { it.id == s.id }) {
                    val genreName = GameData.GENRES.find { it.id == s.genreId }?.name ?: "Pop"
                    val weeklyStreams = playerSongsWeeklyStreamsMap[s.id] ?: 0L
                    totalPlayerWeeklyStreams += weeklyStreams
                    val newChartSong = ChartSong(
                        id = s.id,
                        title = s.title,
                        artistName = state.artistName,
                        artistId = "player",
                        genreName = genreName,
                        rating = s.rating,
                        weeklyStreams = weeklyStreams,
                        totalStreams = s.totalStreams + weeklyStreams,
                        weeksOnChart = 1,
                        peakRank = 999,
                        isPlayer = true,
                        releasedWeek = s.weekCreated,
                        releasedYear = s.yearCreated,
                        hype = s.hype,
                        lastGenreTrendMultiplier = currentTrends[s.genreId] ?: 1f
                    )
                    simulatedSoundstreamSongsList.add(newChartSong)
                }
            }

            // Shared genre audience is an offline proxy for fan overlap. A larger or
            // better received song can win attention within the overlapping segment.
            val currentWeekReleases = simulatedSoundstreamSongsList.filter { song ->
                song.releasedWeek == nextWeek && song.releasedYear == nextYear && song.weeksOnChart == 1
            }
            val newReleaseIds = currentWeekReleases.map { it.id }.toSet()
            simulatedSoundstreamSongsList = simulatedSoundstreamSongsList.map { song ->
                if (!song.isPlayer || song.id !in newReleaseIds) song else {
                    val playerDemand = ReleaseCompetitionHelper.demand(state.fans, song.rating, song.hype)
                    val rivalDemand = currentWeekReleases.filter { !it.isPlayer && it.id != song.id }.sumOf { rival ->
                        val npcFans = updatedNPCs.find { it.id == rival.artistId }?.fans?.coerceAtLeast(1L)?.toDouble() ?: 1.0
                        ReleaseCompetitionHelper.demand(npcFans.toLong(), rival.rating, rival.hype) * ReleaseCompetitionHelper.genreOverlap(song.genreName, rival.genreName)
                    }
                    val sharedAudience = currentWeekReleases.any { rival -> !rival.isPlayer && rival.genreName.equals(song.genreName, true) }
                    val retainedShare = ReleaseCompetitionHelper.retainedShare(playerDemand, rivalDemand)
                    val adjustedStreams = (song.weeklyStreams * retainedShare).toLong().coerceAtLeast(1L)
                    if (sharedAudience) {
                        val strongestRival = currentWeekReleases.filter { !it.isPlayer && it.genreName.equals(song.genreName, true) }
                            .maxByOrNull { rival ->
                                val fans = updatedNPCs.find { it.id == rival.artistId }?.fans ?: 0L
                                ReleaseCompetitionHelper.demand(fans, rival.rating, rival.hype)
                            }
                        if (strongestRival != null) {
                            reportBuilder.append("📊 RELEASE WEEK: '${song.title}' competed with ${strongestRival.artistName}'s ${song.genreName} release. Its ${song.rating}⭐ rating and ${song.hype.toInt()}% hype left it with about ${(retainedShare * 100).toInt()}% of its expected launch-week streams after audience competition.\n")
                        }
                    }
                    song.copy(weeklyStreams = adjustedStreams, totalStreams = song.totalStreams - song.weeklyStreams + adjustedStreams)
                }
            }.toMutableList()

            // Sort by weekly streams to generate leaderboard
            simulatedSoundstreamSongsList.sortByDescending { it.weeklyStreams }

            // Keep top 100 songs active on global charts
            val finalChartSongs = simulatedSoundstreamSongsList.take(100).mapIndexed { idx, cs ->
                val rank = idx + 1
                val peak = if (cs.peakRank == 999 || cs.peakRank == 0) rank else minOf(cs.peakRank, rank)

                if (cs.isPlayer && rank == 1 && cs.currentRank != 1) {
                    reportBuilder.append("🏆 CHART CHAMPION: Your song '${cs.title}' reached #1 on the Global Charts! 🏆\n")
                }

                cs.copy(
                    currentRank = rank,
                    peakRank = peak
                )
            }
            // Randomly trigger SeeChat congratulations or rivalry
            if (kotlin.random.Random.nextFloat() < 0.20f && finalChartSongs.isNotEmpty()) {
                val playerSongOnChart = finalChartSongs.find { it.isPlayer }
                if (playerSongOnChart != null) {
                    val rank = playerSongOnChart.currentRank
                    val randomNpc = updatedNPCs.filter { !it.isRetired }.randomOrNull()
                    if (randomNpc != null) {
                        val npcId = randomNpc.id
                        val npcName = randomNpc.name
                        val relation = randomNpc.relationshipWithPlayer

                        val msgText = when {
                            rank == 1 -> {
                                if (relation < 35f) {
                                    "Yo, enjoy that #1 spot on Soundstream with '${playerSongOnChart.title}' while you can. My next track is dropping soon and I'm coming to take my crown back. 🤫"
                                } else if (relation > 70f) {
                                    "OMGGGG! 😭😭 I am crying! '${playerSongOnChart.title}' is officially #1 on Soundstream! I have it on constant loop! So proud of you! 🎉🥂"
                                } else {
                                    "Congrats on hitting #1 with '${playerSongOnChart.title}' on Soundstream! That's a massive achievement."
                                }
                            }
                            rank in 2..10 -> {
                                if (relation < 35f) {
                                    "Just saw '${playerSongOnChart.title}' enter the Top 10. Mixing is kinda basic but congrats, I guess."
                                } else if (relation > 70f) {
                                    "Omg! '${playerSongOnChart.title}' is in the Top 10 on Soundstream! You're rising so fast! Let's collab soon! 💕"
                                } else {
                                    "Hey, saw '${playerSongOnChart.title}' sitting at #$rank on the Soundstream chart. Solid work!"
                                }
                            }
                            else -> {
                                if (relation > 75f && kotlin.random.Random.nextFloat() < 0.5f) {
                                    "Hey! Was streaming Soundstream and '${playerSongOnChart.title}' came up. It's such a vibe! Added to my personal playlist."
                                } else null
                            }
                        }

                        if (msgText != null) {
                            val systemMsg = ChatMessage(
                                id = java.util.UUID.randomUUID().toString().take(6),
                                senderId = npcId,
                                senderName = npcName,
                                message = msgText,
                                timestamp = System.currentTimeMillis()
                            )
                            val existingIndex = finalChatConversations.indexOfFirst { it.npcId == npcId }
                            if (existingIndex != -1) {
                                val conv = finalChatConversations[existingIndex]
                                finalChatConversations[existingIndex] = conv.copy(
                                    messages = conv.messages + systemMsg,
                                    unreadCount = conv.unreadCount + 1
                                )
                            } else {
                                finalChatConversations.add(
                                    ChatConversation(
                                        npcId = npcId,
                                        npcName = npcName,
                                        messages = listOf(systemMsg),
                                        unreadCount = 1
                                    )
                                )
                            }
                            reportBuilder.append("📱 SeeChat: Received a direct message from $npcName about your chart status!\n")
                        }
                    }
                }
            }

            // Create Cast comment on player success
            if (kotlin.random.Random.nextFloat() < 0.18f && finalChartSongs.any { it.isPlayer && it.currentRank <= 10 }) {
                val song = finalChartSongs.first { it.isPlayer && it.currentRank <= 10 }
                val posterHandle = listOf("@MusicInsider", "@PopVibes", "@RapRadar", "@BillboardBot").random()
                val posterName = listOf("Music Insider", "Pop Vibes", "Rap Radar", "Billboard Bot").random()
                val commentContent = listOf(
                    "Can we talk about how '${song.title}' is currently sitting at #${song.currentRank} on Soundstream? Absolute masterpiece! 🔥📈",
                    "Honestly, '${song.title}' by ${state.artistName} deserves that Top 10 spot on Soundstream. The production is flawless.",
                    "If you aren't streaming '${song.title}' on Soundstream right now, what are you even doing? Rising rapidly!",
                    "Soundstream charts update: ${state.artistName} climbs to #${song.currentRank} with '${song.title}'. Massive momentum!"
                ).random()

                val cPost = CastPost(
                    id = "ss_cast_" + java.util.UUID.randomUUID().toString().take(6),
                    authorName = posterName,
                    authorHandle = posterHandle,
                    content = commentContent,
                    likes = kotlin.random.Random.nextLong(150, 2500),
                    reposts = kotlin.random.Random.nextLong(40, 500),
                    commentsCount = kotlin.random.Random.nextLong(10, 150),
                    week = state.currentWeek,
                    year = state.currentYear,
                    isByPlayer = false
                )
                mutCastPosts.add(0, cPost)
                reportBuilder.append("📣 Cast: A popular industry music blogger posted about your Soundstream chart success!\n")
            }

            // 7e. LIVING WORLD AUTONOMOUS NPC ACTIONS (Social Media posts, private chats, dragging each other, emails)
            val autoResult = simulateLivingWorldAutonomousActions(
                state = state,
                updatedNPCs = updatedNPCs,
                finalNPCs = finalNPCs,
                mutCastPosts = mutCastPosts,
                finalChatConversations = finalChatConversations,
                updatedMail = updatedMail,
                updatedSongs = updatedSongs,
                reportBuilder = reportBuilder
            )
            updatedCash += autoResult.addedCash
            val remainingPendingReplies = autoResult.remainingPendingReplies.toMutableList()

            // E. Dynamic Who Is Got Talent (WGT) Bi-Annual Championship Progression
            val wgtResult = simulateWgtProgression(
                state = state,
                initialWebHub = state.webHub,
                updatedMail = updatedMail,
                generatedNews = generatedNews,
                reportBuilder = reportBuilder
            )
            var currentWebHub = wgtResult.updatedWebHub
            updatedCash += wgtResult.addedCash

            // --- DYNAMIC SYSTEM INTEGRATIONS: ELECTIONS, FAMILY, & AWARDS ---
            val currentWeekVal = state.currentWeek
            var currentPopularity = state.popularity
            var newEndorsedCandidateId = state.endorsedCandidateId
            var newPoliticalTaxModifier = state.politicalTaxModifier
            var currentCityProjectWeeks = state.cityProjectWeeksRemaining
            var currentEnergy = newEnergy
            var currentReceivedGovPitch = state.receivedGovernorPitch
            var currentReceivedSenPitch = state.receivedSenatorPitch

            // 1. ELECTIONS MECHANICS
            if (currentWeekVal in 12..14) {
                isElectionSeason = true
                if (currentWeekVal == 12) {
                    reportBuilder.append("🗳 ELECTION: The Governorship campaign has officially begun! Local advertising rates are high.\n")
                    if (state.popularity >= 45f && currentIntegrity >= 45f) {
                        currentReceivedGovPitch = true
                        val campaignEmail = MailMessage(
                            id = "gov_campaign_pitch_" + java.util.UUID.randomUUID().toString().take(6),
                            sender = "Governor Sterling",
                            subject = "🗳 Campaign Support Offer: $40,000 Support Payment",
                            body = "Dear ${state.artistName},\n\nI am running for re-election as Governor. As an artist of high popularity and integrity, your endorsement would be monumentally valuable to our campaign. We will pay you $40,000 for your support. To accept, open SeeChat and reply to my message with 'endorse' or 'yes'.\n\nBest,\nGovernor Sterling",
                            type = "NORMAL",
                            expiresWeek = 14,
                            expiresYear = state.currentYear
                        )
                        updatedMail.add(campaignEmail)

                        val govMsg = ChatMessage(
                            id = java.util.UUID.randomUUID().toString().take(6),
                            senderId = "npc_gov_sterling",
                            senderName = "Governor Sterling",
                            message = "Hello! I am running for re-election as Governor. Since you are an influential artist with strong status, I would love to get your official endorsement. I will pay you $40,000 for a campaign shoutout! Type 'endorse' or 'yes' to accept, or 'no' to decline.",
                            timestamp = System.currentTimeMillis()
                        )
                        val existingIndex = finalChatConversations.indexOfFirst { it.npcId == "npc_gov_sterling" }
                        if (existingIndex != -1) {
                            val conv = finalChatConversations.removeAt(existingIndex)
                            finalChatConversations.add(0, conv.copy(
                                messages = conv.messages + govMsg,
                                unreadCount = conv.unreadCount + 1
                            ))
                        } else {
                            finalChatConversations.add(0,
                                ChatConversation(npcId = "npc_gov_sterling", npcName = "Governor Sterling", messages = listOf(govMsg), unreadCount = 1)
                            )
                        }
                        reportBuilder.append("📱 SeeChat: Governor Sterling pitched a campaign endorsement deal to you! Check your chat.\n")
                    }
                }
            } else if (currentWeekVal in 36..39) {
                isElectionSeason = true
                if (currentWeekVal == 36) {
                    reportBuilder.append("🗳 ELECTION: The Presidential campaign has officially begun! National advertising rates are high.\n")
                    if (state.popularity >= 60f && state.followers >= 5000) {
                        currentReceivedSenPitch = true
                        val campaignEmail = MailMessage(
                            id = "pres_campaign_pitch_" + java.util.UUID.randomUUID().toString().take(6),
                            sender = "Senator Vance",
                            subject = "🗳 Presidential Endorsement Offer: $80,000 Support Payment",
                            body = "Dear ${state.artistName},\n\nI am running for President of the United States. Your massive fan base would guarantee our victory. We will pay you $80,000 for your official endorsement. To accept, open SeeChat and reply to my message with 'endorse' or 'yes'.\n\nIn Unity,\nSenator Vance",
                            type = "NORMAL",
                            expiresWeek = 39,
                            expiresYear = state.currentYear
                        )
                        updatedMail.add(campaignEmail)

                        val vanceMsg = ChatMessage(
                            id = java.util.UUID.randomUUID().toString().take(6),
                            senderId = "npc_sen_vance",
                            senderName = "Senator Vance",
                            message = "Hello! I am running for President of the United States. With your massive following, your support would guarantee our victory. I will pay you $80,000 for your endorsement. Type 'endorse' or 'yes' to accept, or 'no' to decline.",
                            timestamp = System.currentTimeMillis()
                        )
                        val existingIndex = finalChatConversations.indexOfFirst { it.npcId == "npc_sen_vance" }
                        if (existingIndex != -1) {
                            val conv = finalChatConversations.removeAt(existingIndex)
                            finalChatConversations.add(0, conv.copy(
                                messages = conv.messages + vanceMsg,
                                unreadCount = conv.unreadCount + 1
                            ))
                        } else {
                            finalChatConversations.add(0,
                                ChatConversation(npcId = "npc_sen_vance", npcName = "Senator Vance", messages = listOf(vanceMsg), unreadCount = 1)
                            )
                        }
                        reportBuilder.append("📱 SeeChat: Senator Vance pitched a Presidential endorsement deal to you! Check your chat.\n")
                    }
                }
            } else {
                isElectionSeason = false
            }

            if (currentWeekVal == 13 || currentWeekVal == 37) {
                nextIsElectionWeek = true
                nextHasVotedInCurrentElection = false
                val raceName = if (currentWeekVal == 13) "State Governor" else "City Mayor & Council"
                reportBuilder.append("🗳 UPCOMING ELECTION: Polling opens next week for $raceName! Citizen voting is mandatory via omegaelectoralcommission.gov.\n")
            }

            if (currentWeekVal == 14 || currentWeekVal == 38) {
                val racePosition = if (currentWeekVal == 14) "State Governor" else "City Mayor & Council"
                val race = PoliticsSimulationHelper.getActiveElectionRace(
                    week = currentWeekVal,
                    year = state.currentYear
                )
                val results = PoliticsSimulationHelper.calculateLiveResults(
                    contestants = race.contestants,
                    position = racePosition,
                    playerVotedCandidateId = state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId,
                    playerEndorsedCandidateId = state.endorsedPoliticianId ?: state.endorsedCandidateId,
                    playerPopularity = currentPopularity,
                    currentWeek = currentWeekVal,
                    currentYear = state.currentYear,
                    isVotingSeason = true
                )
                val winnerCandidate = race.contestants.find { it.politician.id == results.leadingCandidateId } ?: race.contestants.first()
                val winnerPolitician = winnerCandidate.politician
                nextElectionWinnerId = winnerPolitician.id
                nextElectionWinnerName = winnerPolitician.name

                reportBuilder.append("🗳 OFFICIAL ELECTION RESULTS: ${winnerPolitician.name} (${winnerPolitician.party}) has won the election for $racePosition!\n")

                val electionEmail = MailMessage(
                    id = "official_election_results_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "Omega Electoral Commission",
                    subject = "🗳 Certified Results: $racePosition Election",
                    body = "Certified election results for $racePosition are in.\n\nWinner: ${winnerPolitician.name} (${winnerPolitician.party})\nTotal Votes Counted: ${String.format("%,d", results.totalBallotsCounted)}\n\nWinning Platform:\n${winnerCandidate.promises.firstOrNull()?.title ?: "Civic Reform"}: ${winnerCandidate.promises.firstOrNull()?.mechanicalEffect ?: ""}",
                    type = "NORMAL",
                    expiresWeek = nextWeek + 4,
                    expiresYear = nextYear
                )
                updatedMail.add(0, electionEmail)

                val tookMoney = state.tookEndorsementMoney || (state.endorsedCandidateId != null)
                val endorsedId = state.endorsedPoliticianId ?: state.endorsedCandidateId
                val votedId = state.webHub.electionVotedCandidateId ?: state.electionVotedCandidateId

                if (tookMoney && endorsedId != null && votedId != null && votedId != endorsedId) {
                    if (winnerPolitician.id == endorsedId) {
                        // Endorsed candidate won after player took their money and voted for someone else!
                        updatedIsUnderArrest = true
                        updatedArrestReason = "Political Wire Fraud & Campaign Bribery by Order of ${winnerPolitician.title} ${winnerPolitician.name}"
                        updatedArrestBailAmount = 65000.0
                        updatedArrestSentenceWeeksRemaining = 4
                        currentStress = (currentStress + 25f).coerceAtMost(100f)
                        reportBuilder.append("🚨 ARREST WARRANT EXECUTED: ${winnerPolitician.title} ${winnerPolitician.name} discovered you took $${String.format("%,.0f", state.endorsementMoneyAmount.takeIf { it > 0.0 } ?: 40000.0)} campaign cash and voted against them! State Crime Command has remanded you to detention! Access to Studio, Tours, Ventures, and WGT is suspended until bail ($65,000) or sentence (4 weeks) is served at statecrimecommand.gov.\n")

                        val arrestEmail = MailMessage(
                            id = "scc_arrest_warrant_" + java.util.UUID.randomUUID().toString().take(6),
                            sender = "State Crime Command",
                            subject = "🚨 NOTICE OF REMAND & ASSET SEIZURE",
                            body = "To Defendant ${state.artistName},\n\nYou have been remanded to State Crime Command detention under Judicial Warrant issued by the administration of ${winnerPolitician.name} for Campaign Wire Fraud. Bail is set at $65,000 or a 4-week correctional sentence. Settle immediately via the SCC portal.",
                            type = "NORMAL",
                            expiresWeek = nextWeek + 8,
                            expiresYear = nextYear
                        )
                        updatedMail.add(0, arrestEmail)
                    } else {
                        // Endorsed candidate lost!
                        currentIntegrity = (currentIntegrity - 35f).coerceAtLeast(5f)
                        currentTrustworthiness = (currentTrustworthiness - 30f).coerceAtLeast(5f)
                        currentPublicImage = (currentPublicImage - 20f).coerceAtLeast(10f)
                        reportBuilder.append("📉 INTEGRITY COLLAPSE: Leaked bank records revealed you accepted campaign money from ${state.endorsedPoliticianName ?: "the candidate"} and voted against them! Industry Integrity dropped (-35%) and Trustworthiness dropped (-30%).\n")
                    }
                } else if (tookMoney && endorsedId != null && votedId == endorsedId && winnerPolitician.id == endorsedId) {
                    newPoliticalTaxModifier = -0.06f
                    currentPopularity = (currentPopularity + 12f).coerceAtMost(100f)
                    currentIndustryRespect = (currentIndustryRespect + 15f).coerceAtMost(100f)
                    reportBuilder.append("🎉 CAMPAIGN LOYALTY REWARDED: Your endorsed ally ${winnerPolitician.name} won! Weekly tax rate slashed to 6%! Popularity (+12%) and Industry Respect (+15%) boosted.\n")
                }

                // Reset cycle endorsement
                nextIsElectionWeek = false
                nextHasVotedInCurrentElection = false
                nextTookEndorsementMoney = false
                nextEndorsedPoliticianId = null
                nextEndorsedPoliticianName = null
            }

            // Supportive Politician Demand Project trigger
            if (newPoliticalTaxModifier < 0.0f && (currentWeekVal == 20 || currentWeekVal == 44)) {
                val politicianName = if (currentWeekVal == 20) "Governor Sterling" else "President Vance"
                val supportMessage = "Tell your fans that we just launched a new project to help the city, they should support by giving what they have, it will only cost you 7% of your energy for the next 4 weeks and also some dollars. Thank you."

                val chatMsg = ChatMessage(
                    id = java.util.UUID.randomUUID().toString().take(6),
                    senderId = if (currentWeekVal == 20) "npc_gov_sterling" else "npc_sen_vance",
                    senderName = politicianName,
                    message = supportMessage,
                    timestamp = System.currentTimeMillis()
                )

                val npcId = if (currentWeekVal == 20) "npc_gov_sterling" else "npc_sen_vance"
                val existingIndex = finalChatConversations.indexOfFirst { it.npcId == npcId }
                if (existingIndex != -1) {
                    val conv = finalChatConversations.removeAt(existingIndex)
                    finalChatConversations.add(0, conv.copy(
                        messages = conv.messages + chatMsg,
                        unreadCount = conv.unreadCount + 1
                    ))
                } else {
                    finalChatConversations.add(0,
                        ChatConversation(npcId = npcId, npcName = politicianName, messages = listOf(chatMsg), unreadCount = 1)
                    )
                }
                reportBuilder.append("📱 SeeChat: Received an urgent request from $politicianName about a City Renewal Project!\n")
            }

            // Weekly project support deduction
            if (currentCityProjectWeeks > 0) {
                updatedCash -= 250.0
                currentCityProjectWeeks -= 1
                currentEnergy = (currentEnergy - 7).coerceAtLeast(0)
                currentIntegrity = (currentIntegrity + 1.0f).coerceAtMost(100f)
                currentPublicImage = (currentPublicImage + 2.0f).coerceAtMost(100f)
                reportBuilder.append("🌟 City Project Support: Contributed 7% energy and $250. Gained +1% Integrity and +2% Public Image! ($currentCityProjectWeeks weeks remaining)\n")
            }

            // 2. FAMILY & FRIENDS REQUESTS MECHANICS (DYNAMIC & UNEXPECTED)
            var nextActiveFamilyReqNpcId = state.activeFamilyReqNpcId
            var nextActiveFamilyReqAmount = state.activeFamilyReqAmount
            var nextActiveFamilyReqType = state.activeFamilyReqType
            var nextActiveFamilyReqDetail = state.activeFamilyReqDetail
            var nextActiveFamilyReqStatus = state.activeFamilyReqStatus
            var nextLastFamilyReqWeek = state.lastFamilyReqWeek

            // If there's an active request that is still pending, but it's been more than 4 weeks and player hasn't replied, let's expire it!
            if (nextActiveFamilyReqStatus == "PENDING" && currentWeekVal - nextLastFamilyReqWeek > 4) {
                nextActiveFamilyReqStatus = "EXPIRED"
                nextActiveFamilyReqNpcId = null
                reportBuilder.append("📱 SeeChat: An old family or friend help request has expired because you waited too long.\n")
            }

            // Probability check to trigger a new request:
            // 12% chance every week if:
            // - No active request is PENDING
            // - At least 8 weeks have passed since the last trigger
            // - We are in week 5 or later
            if (nextActiveFamilyReqStatus != "PENDING" && (currentWeekVal - nextLastFamilyReqWeek >= 8 || nextLastFamilyReqWeek == 0) && currentWeekVal >= 5) {
                val roll = kotlin.random.Random.nextFloat()
                if (roll < 0.12f) { // 12% probability
                    // Choose an NPC
                    val eligibleNPCs = state.npcs.filter { n ->
                        n.id in listOf("npc_family_mom", "npc_family_cousin_sarah", "npc_family_uncle_joe") || n.relationshipWithPlayer >= 55f
                    }
                    val npc = if (eligibleNPCs.isNotEmpty()) eligibleNPCs.random() else state.npcs.find { it.id == "npc_family_mom" }
                    if (npc != null) {
                        // Generate dynamic request
                        val types = listOf("EMERGENCY", "BUSINESS", "TUITION", "CELEBRATION")
                        val type = types.random()

                        val minAmount = when (type) {
                            "EMERGENCY" -> 1500.0
                            "BUSINESS" -> 5000.0
                            "TUITION" -> 2000.0
                            "CELEBRATION" -> 1000.0
                            else -> 1000.0
                        }
                        val maxAmount = when (type) {
                            "EMERGENCY" -> minOf(updatedCash * 0.25, 8000.0).coerceAtLeast(1500.0)
                            "BUSINESS" -> minOf(updatedCash * 0.40, 25000.0).coerceAtLeast(5000.0)
                            "TUITION" -> minOf(updatedCash * 0.20, 6000.0).coerceAtLeast(2000.0)
                            "CELEBRATION" -> minOf(updatedCash * 0.15, 4000.0).coerceAtLeast(1000.0)
                            else -> 2000.0
                        }
                        val amountVal = ((kotlin.random.Random.nextDouble(minAmount, maxAmount) / 100).toInt() * 100).toDouble().coerceAtLeast(minAmount)

                        val details = when (type) {
                            "EMERGENCY" -> listOf(
                                Pair("vet_emergency", "pet needs emergency vet surgery"),
                                Pair("emergency_repair", "home water pipe leak flooded the basement"),
                                Pair("dental_bill", "needs urgent dental root canal and crown"),
                                Pair("lost_laptop", "smashed work laptop and lost design files")
                            )
                            "BUSINESS" -> listOf(
                                Pair("organic_crypto_lemonade", "open an organic crypto lemonade stand 🍋"),
                                Pair("food_truck", "lease a gourmet street taco food truck 🌮"),
                                Pair("vinyl_press", "press a limited run of band vinyls on retro colored wax 🎚"),
                                Pair("artisanal_bakery", "launch an artisanal sourdough bakery pop-up 🥐")
                            )
                            "TUITION" -> listOf(
                                Pair("tuition_help", "short on college tuition and rent"),
                                Pair("coding_bootcamp", "enroll in an intensive game dev coding bootcamp 💻"),
                                Pair("sound_engineering", "enroll in an advanced sound mixing masterclass 🎚")
                            )
                            "CELEBRATION" -> listOf(
                                Pair("surprise_party", "throw a milestone birthday surprise party 🎉"),
                                Pair("milestone_anniversary", "fund parent's milestone anniversary vacation package ✈")
                            )
                            else -> listOf(Pair("general", "facing an unexpected financial setback"))
                        }
                        val selectedDetail = details.random()
                        val detailKey = selectedDetail.first
                        val detailText = selectedDetail.second

                        val amountStr = String.format("%,.0f", amountVal)
                        val msgText = when (detailKey) {
                            "vet_emergency" -> "Hey! I'm in a real panic right now... my pet got extremely sick and needs emergency veterinary surgery. 🐶 The clinic says it's going to cost $$amountStr immediately. I'm completely short. Can you help me out? Type 'help' or 'yes' to support, or 'no' to decline."
                            "emergency_repair" -> "Hey! A water pipe literally burst in my house today and flooded the whole lower floor. 🌊 Mold is starting to grow and I need a professional cleanup contractor immediately. The estimate is $$amountStr. Can you help me cover this? Type 'help' or 'yes' to support, or 'no' to decline."
                            "dental_bill" -> "Hi! I am in absolute agony with an infected tooth, and the dentist says I need an emergency root canal and cap. 🦷 It is going to cost $$amountStr because my insurance doesn't cover dental. Is there any way you can help me? Type 'help' or 'yes' to support, or 'no' to decline."
                            "lost_laptop" -> "Hey! My laptop slipped from my backpack today and the screen and drive are completely destroyed. 💻 I have all my class files and freelance projects on it! A replacement costs $$amountStr. Can you help me get back on track? Type 'help' or 'yes' to support, or 'no' to decline."
                            "organic_crypto_lemonade" -> "Hey kiddo! I've got this mind-blowing business idea to launch an organic crypto lemonade stand. 🍋 We pair artisan organic cold-pressed juice with real-time Web3 utility tokens! I need $$amountStr to lease a storefront. Type 'invest' or 'yes' to partner up, or 'no' to pass!"
                            "food_truck" -> "Hey! I've designed a stellar menu for gourmet fusion street tacos 🌮 and found a vintage step-van food truck for lease! I just need $$amountStr to secure the equipment permit and stock up. Would you like to invest? Type 'invest' or 'yes' to partner, or 'no' to pass!"
                            "vinyl_press" -> "Hey! My band has been writing new tracks and we want to press a limited run of neon retro vinyls! 🎚 The pressing factory quote is $$amountStr. If you fund us, you'll get 50% of our sales! What do you say? Type 'invest' or 'yes' to back us, or 'no' to decline."
                            "artisanal_bakery" -> "Hey! I'm launching an artisanal sourdough bread bakery pop-up in the trendiest part of town! 🥐 I found a professional steam oven but it costs $$amountStr. If you help me finance it, we'll share the sweet profits! Type 'invest' or 'yes' to back me, or 'no' to pass."
                            "tuition_help" -> "Hey! I am really struggling to cover my university tuition and rent this month. 🎓 If I can't raise $$amountStr by next week, the bursar will drop me from all my classes. Can you support me? Type 'help' or 'yes' to help out, or 'no' to decline."
                            "coding_bootcamp" -> "Hi there! I just got accepted into an intensive 12-week software engineering coding bootcamp! 💻 They have a 98% job placement rate, but tuition is $$amountStr. Can you invest in my future? Type 'help' or 'yes' to help, or 'no' to decline."
                            "sound_engineering" -> "Hey! I want to enroll in an advanced analog sound engineering and mixing masterclass 🎚 to level up my production skills. The course fee and gear kit is $$amountStr. Can you support this? Type 'help' or 'yes' to support, or 'no' to decline."
                            "surprise_party" -> "Hey! We are organizing a surprise golden jubilee party for our family's elders! 🎉 It costs $$amountStr to rent the community banquet hall and caterer. Can you contribute to the family fund? Type 'help' or 'yes' to support, or 'no' to decline."
                            "milestone_anniversary" -> "Hi! I am raising money to send our parents on a dream vacation for their milestone wedding anniversary. ✈ They have worked so hard for us. The total flight and hotel package is $$amountStr. Can you help fund it? Type 'help' or 'yes' to support, or 'no' to decline."
                            else -> "Hey! I am facing an unexpected expense of $$amountStr and could really use your support. Can you help me out? Type 'help' or 'yes' to support, or 'no' to decline."
                        }

                        val chatMsg = ChatMessage(
                            id = java.util.UUID.randomUUID().toString().take(6),
                            senderId = npc.id,
                            senderName = npc.name,
                            message = msgText,
                            timestamp = System.currentTimeMillis()
                        )

                        val existingIndex = finalChatConversations.indexOfFirst { it.npcId == npc.id }
                        if (existingIndex != -1) {
                            val conv = finalChatConversations.removeAt(existingIndex)
                            finalChatConversations.add(0, conv.copy(
                                messages = conv.messages + chatMsg,
                                unreadCount = conv.unreadCount + 1
                            ))
                        } else {
                            finalChatConversations.add(0,
                                ChatConversation(npcId = npc.id, npcName = npc.name, messages = listOf(chatMsg), unreadCount = 1)
                            )
                        }

                        nextActiveFamilyReqNpcId = npc.id
                        nextActiveFamilyReqAmount = amountVal
                        nextActiveFamilyReqType = type
                        nextActiveFamilyReqDetail = detailText
                        nextActiveFamilyReqStatus = "PENDING"
                        nextLastFamilyReqWeek = currentWeekVal

                        reportBuilder.append("📱 SeeChat: Received an unexpected personal message from ${npc.name} asking for help with their ${detailText}!\n")
                    }
                }
            }

            // 3. AWARDS SHOW NOMINATION EMAIL (Week 48)
            if (currentWeekVal == 48) {
                val nominations = mutableListOf<String>()
                if (state.meTubeVideos.isNotEmpty()) nominations.add("• Content Creator of the Year")
                if (state.songs.isNotEmpty()) nominations.add("• Best Song of the Year")
                if (currentPopularity > 50f) nominations.add("• Best Artist of the Year")
                if (state.songs.any { it.totalStreams > 5000 }) nominations.add("• Largest Stream Count of the Year")
                if (state.followers > 2000) nominations.add("• Most Social Artist of the Year")
                if (currentIntegrity < 40f && state.cash > 50000.0) nominations.add("• The infamous 'Nepotism Award' (Industry backing favor)")

                if (nominations.isNotEmpty()) {
                    val nominationEmail = MailMessage(
                        id = "awards_nomination_y${state.currentYear}",
                        sender = "Global Music Awards Committee",
                        subject = "🏆 Official Nominations: Annual Global Music Awards!",
                        body = "Dear ${state.artistName},\n\nCongratulations! You have been officially nominated for the following awards in the upcoming Annual Global Music Awards show:\n\n" +
                                nominations.joinToString("\n") +
                                "\n\nBeing nominated is a high honor, but winning is not guaranteed—your achievements will be compared with the industry's top stars! The live awards broadcast will air in Week 50.\n\nWarm regards,\nAwards Committee Board",
                        type = "NORMAL",
                        expiresWeek = 51,
                        expiresYear = state.currentYear
                    )
                    updatedMail.add(nominationEmail)
                    reportBuilder.append("🏆 Awards Nomination: Received official nomination email for the Global Music Awards! Check your Mailbox.\n")
                }
            }

            if (currentWeekVal == 50) {
                triggerAwardsShow = true
            }

            // Dynamic Monthly Listeners & World Rank Calculations
            val allReleasedSongs = updatedSongs.filter { it.isReleased }
            val playerHasReleasedSongs = allReleasedSongs.isNotEmpty()
            val totalReleasedStreams = allReleasedSongs.sumOf { it.totalStreams }
            val previousListeners = if (playerHasReleasedSongs) state.soundstreamMonthlyListeners else 0L
            val finalMonthlyListeners = if (!playerHasReleasedSongs || totalReleasedStreams <= 0L) {
                0L
            } else {
                val avgRating = allReleasedSongs.map { it.rating }.average().toFloat()
                val qualityMultiplier = when {
                    avgRating < 2.0f -> 0.02f
                    avgRating < 2.8f -> 0.08f
                    avgRating < 3.5f -> 0.25f
                    avgRating < 4.2f -> 0.65f
                    avgRating < 4.8f -> 1.05f
                    else -> 1.40f
                }
                // Target monthly listeners based on actual weekly streams + catalog depth + fans, scaled by rating quality
                val rawTarget = (totalPlayerWeeklyStreams * 2.5 + totalReleasedStreams * 0.08 + state.fans * 0.4) * qualityMultiplier
                val targetListeners = rawTarget.toLong().coerceAtLeast(0L)
                val listenerDelta = (targetListeners - previousListeners) * 0.30f
                (previousListeners + listenerDelta).toLong().coerceAtLeast(0L)
            }

            val npcListeners = updatedNPCs.map { n -> n.id to (n.fans * 2.2 + n.popularity * 15000 + 10000).toLong() }
            val globalSuperstars = listOf(
                55_000_000L, 48_000_000L, 42_000_000L, 38_000_000L, 32_000_000L,
                28_000_000L, 25_000_000L, 22_000_000L, 19_000_000L, 16_000_000L,
                14_000_000L, 12_000_000L, 10_000_000L, 8_500_000L, 7_200_000L,
                6_100_000L, 5_200_000L, 4_400_000L, 3_800_000L, 3_200_000L,
                2_700_000L, 2_300_000L, 1_900_000L, 1_600_000L, 1_350_000L,
                1_150_000L, 980_000L, 840_000L, 720_000L, 610_000L,
                520_000L, 440_000L, 370_000L, 310_000L, 260_000L,
                210_000L, 175_000L, 145_000L, 120_000L, 98_000L,
                80_000L, 65_000L, 52_000L, 42_000L, 33_000L,
                25_000L, 18_000L, 12_000L, 8_000L, 5_000L
            )
            val allArtistsStreams = (npcListeners.map { it.second } + finalMonthlyListeners + globalSuperstars).sortedByDescending { it }
            val finalWorldRank = if (playerHasReleasedSongs && finalMonthlyListeners > 0L) {
                (allArtistsStreams.indexOf(finalMonthlyListeners) + 1).coerceIn(1, 150)
            } else {
                150
            }

            val currentWeekCashFlow = (updatedCash + weeklySalary) - state.cash
            val updatedHistoryCashFlow = (state.financialHistoryCashFlow + currentWeekCashFlow.toString()).takeLast(12)

            var updatedFinancialReviewLog = state.financialReviewLog
            var lastReviewWeek = state.lastReviewWeek
            var lastReviewYear = state.lastReviewYear

            if (state.currentWeek % 4 == 0 && (state.currentWeek != state.lastReviewWeek || state.currentYear != state.lastReviewYear)) {
                val reviewText = generateMonthlyFinancialReview(
                    state = state.copy(
                        cash = updatedCash + weeklySalary,
                        stocks = finalStocks,
                        businessVentures = updatedVentures,
                        cryptos = finalCryptos,
                        banking = state.banking.copy(savingsBalance = newSavingsBalance),
                        wealth = state.wealth.copy(
                            emergencyFundBalance = updatedEmergencyFund,
                            payrollReserve = updatedPayrollReserve,
                            businessReserve = updatedBusinessReserve,
                            bondsBalance = updatedBondsBalance
                        )
                    ),
                    currentCash = updatedCash + weeklySalary,
                    currentWeeklyCashFlow = updatedHistoryCashFlow.mapNotNull { it.toDoubleOrNull() }.average().takeIf { !it.isNaN() } ?: currentWeekCashFlow
                )
                updatedFinancialReviewLog = state.financialReviewLog + reviewText
                lastReviewWeek = state.currentWeek
                lastReviewYear = state.currentYear

                val coachEmail = MailMessage(
                    id = "fin_review_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "Apex Advisory",
                    subject = "📊 Monthly Wealth & Intelligence Report",
                    body = reviewText,
                    type = "NORMAL",
                    expiresWeek = nextWeek + 4,
                    expiresYear = nextYear
                )
                updatedMail.add(coachEmail)
                reportBuilder.append("📊 WEALTH COACHING: Your new Monthly Financial Review is ready! Checked in on emergency funds, liabilities, and investment allocations. View it in the smartphone banking app.\n")
            }


            // Business Manager Proactive Operational Intelligence Logic
            val bm = state.hiredEmployees.find { it.role == "Business Manager" }
            val activeControlledVentures = state.businessVentures.filter { it.isOwned && it.isPlayerInControl }
            if (bm != null && activeControlledVentures.isNotEmpty()) {
                val shouldAdvise = (state.currentWeek % 2 == 0) || (kotlin.random.Random.nextFloat() < 0.45f)
                if (shouldAdvise) {
                    val targetVenture = activeControlledVentures.random()
                    val (adviceSubject, adviceBody) = when (targetVenture.type) {
                        "RESTAURANT" -> {
                            Pair(
                                "Customer Volume & Waiter Capacity",
                                "There are many recurring customers boss and I know our business level only requires 8 waiters but the customers want orders delivered faster so add 2 more waiters is totally worth it to improve turnover and boost weekly tips."
                            )
                        }
                        "CRYPTO_EXCHANGE", "CRYPTO_TOKEN", "CRYPTO" -> {
                            Pair(
                                "Smart Contract & Developer Staffing",
                                "Sir our crypto business needs 2 more developers on the team to audit security protocols and scale our node throughput."
                            )
                        }
                        "HOTEL" -> {
                            Pair(
                                "Guest Amenities & Suite Demand",
                                "Customers are demanding amenities in the hotel sir. Adding luxury wellness facilities, premium toiletries, and rooftop dining will allow us to raise room rates by 20%."
                            )
                        }
                        "TAXI_COMPANY", "TAXI" -> {
                            Pair(
                                "Fleet Readiness & Surge Demand",
                                "Boss, ride requests during peak hours are surging. Adding 2 more cars to our taxi fleet will dramatically cut passenger wait times and boost weekly fares."
                            )
                        }
                        "RECORD_LABEL" -> {
                            Pair(
                                "A&R Scouting & Catalog Royalties",
                                "Boss, our label roster has great momentum. Hiring an additional talent scout and expanding digital PR will bring in higher-tier demo tapes."
                            )
                        }
                        "FARM" -> {
                            Pair(
                                "Storage Silos & Refrigerated Transport",
                                "Boss, crop yields are high this month. Upgrading storage silos and adding transport capacity will prevent spoilage and accelerate wholesale deliveries."
                            )
                        }
                        "NIGHTCLUB" -> {
                            Pair(
                                "VIP Lounge & Sound Engineering",
                                "Boss, VIP booths are oversubscribed every weekend. Expanding the premium seating area and upgrading the acoustic rig will maximize beverage sales."
                            )
                        }
                        else -> {
                            Pair(
                                "Operational Efficiency Review",
                                "Boss, reviewing ${targetVenture.name}: customer retention is steady, but increasing staff training and marketing allocation will unlock higher operating profit."
                            )
                        }
                    }

                    val bmEmail = MailMessage(
                        id = "bm_advice_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = "${bm.name} (Business Manager)",
                        subject = "💼 ${targetVenture.name}: $adviceSubject",
                        body = adviceBody,
                        type = "BUSINESS_MANAGER",
                        expiresWeek = nextWeek + 4,
                        expiresYear = nextYear
                    )
                    updatedMail.add(0, bmEmail)
                    reportBuilder.append("👔 BUSINESS MANAGER ADVICE: ${bm.name} reviewed ${targetVenture.name} and sent you operational recommendations in your Mailbox.\n")
                }
            }

            // Personal Assistant Proactive Logic (Never flags/archives Business Manager emails)
            val pa = state.hiredEmployees.find { it.role == "Personal Assistant" }
            if (pa != null) {
                val emailsToSummarize = updatedMail.filter { !it.isAccepted && !it.isRead && it.type == "NORMAL" && !it.sender.contains("Business Manager") }
                if (emailsToSummarize.size >= 3) {
                    updatedMail.removeAll(emailsToSummarize)
                    val summaryText = emailsToSummarize.joinToString(separator = "\n- ") { "${it.subject} (from ${it.sender})" }
                    val summaryEmail = MailMessage(
                        id = "pa_summary_${System.currentTimeMillis()}",
                        sender = pa.name + " (Personal Assistant)",
                        subject = "Weekly Email Summary",
                        body = "Boss, here is a summary of the emails you received while you were busy:\n\n- $summaryText\n\nI've archived the originals for you to keep your inbox clean.",
                        type = "NORMAL",
                        expiresWeek = nextWeek + 4,
                        expiresYear = nextYear
                    )
                    updatedMail.add(0, summaryEmail)
                }
            }

            val netWeeklyCash = updatedCash + weeklySalary
            if (netWeeklyCash < 0.0) {
                // If it's an optional service offered (e.g. ad campaigns), stop it after that week
                if (updatedCampaignsList.isNotEmpty()) {
                    updatedCampaignsList.clear()
                    reportBuilder.append("⚠️ SERVICES SUSPENDED: PR & promotional ad campaigns halted due to negative balance ($${String.format("%,.0f", netWeeklyCash)}). Clear your deficit to resume services.\n")
                }
                reportBuilder.append("🚨 ACCOUNT DEFICIT: Your personal cash balance is in the red at -$${String.format("%,.0f", -netWeeklyCash)}. Statutory taxes and debt obligations continue to accrue.\n")
            }

            _weeklyReport.value = reportBuilder.toString()


            val (stateWithEcosystem, finalNPCsWithEcosystem) = simulateWorldEcosystem(state, reportBuilder, updatedMail, finalNPCs)

            var finalState = stateWithEcosystem.copy(
                capitalClubMembers = updatedCrushMembers,
                currentWeek = nextWeek,
                currentYear = nextYear,
                cash = netWeeklyCash,
                recoupableAdvanceBalance = recoupableAdvanceBalance,
                songs = updatedSongs,
                albums = updatedAlbums,
                tours = updatedTours,
                energy = currentEnergy,
                maxEnergy = calculatedMaxEnergy,
                stressUntreatedWeeks = updatedStressUntreatedWeeks,
                stressTreatedConsecutiveWeeks = updatedStressTreatedConsecutiveWeeks,
                stressTreatedThisWeek = false,
                popularity = currentPopularity,
                endorsedCandidateId = newEndorsedCandidateId,
                politicalTaxModifier = newPoliticalTaxModifier,
                cityProjectWeeksRemaining = currentCityProjectWeeks,
                sideJobs = state.sideJobs,
                stocks = finalStocks,
                        businessVentures = updatedVentures,
                cryptos = finalCryptos,
                practiceMissedWeeks = practiceMissed,
                voiceSkill = updatedVoice,
                writingSkill = updatedWriting,
                influence = updatedInfluence,
                ownLabel = ownLabel,
                mailbox = updatedMail,
                webHub = currentWebHub,
                pendingChatReplies = remainingPendingReplies,
                npcs = finalNPCsWithEcosystem,
                castPosts = mutCastPosts,
                merchStyles = if (updatedMerchStyles.isNotEmpty()) updatedMerchStyles else state.merchStyles,
                newsArticles = generatedNews,
                soundstreamMonthlyListeners = finalMonthlyListeners,
                soundstreamWorldRank = finalWorldRank,
                soundstreamWeeklyStreams = totalPlayerWeeklyStreams,
                soundstreamSongs = finalChartSongs,
                chatConversations = finalChatConversations,
                // Living world properties
                activeGlobalEvent = activeEvent,
                globalEventEffect = eventEffect,
                genreTrends = currentTrends,
                genreTrendWeeksRemaining = genreTrendUpdate.weeksRemaining,
                genreTrendDurationWeeks = genreTrendUpdate.durationWeeks,
                // Smartphone digital ecosystem preservation
                meTubeVideos = updatedMeTubeVideos,
                meTubeSubscribers = newSubscribers,
                meTubeWatchTime = newWatchTime,
                meTubeRevenue = state.meTubeRevenue + meTubeEarnings,
                hiredEmployees = updatedEmployeesList,
                candidateRoster = updatedCandidateRoster,
                pendingStockOrders = remainingStockOrders,
                pendingCryptoOrders = remainingCryptoOrders,
                castTrendingHashtags = trendingHashtags,
                castTimeSpentThisWeek = 0,
                castPostsCountThisWeek = 0,
                notificationLogs = (newNotifications + state.notificationLogs).take(20),
                // Fan segmentation
                fans = finalFansNew,
                // Character/Stewardship Traits
                businessReputation = currentBusinessReputation,
                discipline = updatedDiscipline,
                integrity = updatedIntegrity,
                generosity = updatedGenerosity,
                humility = updatedHumility,
                reliability = updatedReliability,
                // Reputations
                industryRespect = currentIndustryRespect,
                publicImage = currentPublicImage,
                criticalAcclaim = currentCriticalAcclaim,
                trustworthiness = currentTrustworthiness,
                legacyPoints = currentLegacyPoints,
                activePhone = updatedActivePhone,
                ownedPhones = updatedOwnedPhones,
                // Mental health
                stress = currentStress,
                burnout = currentBurnout,
                creativeInspiration = currentInspiration,
                motivation = currentMotivation,
                happiness = currentHappiness,
                workLifeBalance = currentWorkLifeBalance,
                // Banking System updates
                activeLoanContracts = updatedLoanContracts,
                adCompanyBudget = updatedAdBudget,
                activeAdCampaigns = updatedCampaignsList,
                economyCycle = economyCycle,
                inflationRate = inflationRate,
                fuelPrice = fuelPrice,
                touristIndex = touristIndex,
                isElectionSeason = isElectionSeason,
                isUnderArrest = updatedIsUnderArrest,
                arrestSentenceWeeksRemaining = updatedArrestSentenceWeeksRemaining,
                arrestReason = updatedArrestReason,
                arrestBailAmount = updatedArrestBailAmount,
                arrestAttorneyHired = updatedArrestAttorneyHired,
                isElectionWeek = nextIsElectionWeek,
                hasVotedInCurrentElection = nextHasVotedInCurrentElection,
                tookEndorsementMoney = nextTookEndorsementMoney,
                endorsementMoneyAmount = nextEndorsementMoneyAmount,
                endorsedPoliticianId = nextEndorsedPoliticianId,
                endorsedPoliticianName = nextEndorsedPoliticianName,
                currentElectionWinnerId = nextElectionWinnerId,
                currentElectionWinnerName = nextElectionWinnerName,
                weatherCondition = weatherCondition,
                activeBrandDealName = activeBrandDealName,
                brandDealWeeksRemaining = brandDealWeeksRemaining,
                brandDealWeeklyPayout = brandDealWeeklyPayout,
                brandDealPostedThisWeek = brandDealPostedThisWeek,
                scheduledActivities = emptyList<String>(),
                activeFamilyReqNpcId = nextActiveFamilyReqNpcId,
                activeFamilyReqAmount = nextActiveFamilyReqAmount,
                activeFamilyReqType = nextActiveFamilyReqType,
                activeFamilyReqDetail = nextActiveFamilyReqDetail,
                activeFamilyReqStatus = nextActiveFamilyReqStatus,
                lastFamilyReqWeek = nextLastFamilyReqWeek,
                timelineEvents = updatedTimelineEvents,
                banking = stateWithEcosystem.banking.copy(
                    savingsBalance = newSavingsBalance,
                    taxRate = (0.12f + newPoliticalTaxModifier).coerceIn(0.01f, 0.35f),
                    loanAmount = newLoanAmount,
                    accumulatedLoanInterest = newAccumulatedInterest,
                    taxDebt = newTaxDebt,
                    titheDebt = newTitheDebt,
                    mortgageAmount = newMortgageAmount
                ),
                fanSegments = stateWithEcosystem.fanSegments.copy(casualFans = updatedCasual, hardcoreFans = updatedHardcore, collectors = updatedCollectors, concertGoers = updatedConcertGoers, internationalFans = updatedInternational, onlineCommunity = updatedOnline, fansPersonalityLovers = updatedPersonalityLovers, fansLyricsLovers = updatedLyricsLovers, fansVoicePurists = updatedVoicePurists, fansFashionistas = updatedFashionistas, fansControversySeekers = updatedControversySeekers, fansBusinessAdmirers = updatedBusinessAdmirers, fansCharityBelievers = updatedCharityBelievers),
                legacy = stateWithEcosystem.legacy.copy(artistsInspiredCount = updatedInspiredCount, receivedGovernorPitch = currentReceivedGovPitch, receivedSenatorPitch = currentReceivedSenPitch),
                sentiment = stateWithEcosystem.sentiment.copy(
                    followers = updatedFollowers,
                    industryFollowers = updatedIndustryFollowers,
                    trendingScore = updatedTrendingScore,
                    conversationVolume = updatedConversationVolume,
                    positiveSentiment = updatedPositiveSentiment,
                    negativeSentiment = updatedNegativeSentiment,
                    neutralSentiment = updatedNeutralSentiment
                ),
                wealth = stateWithEcosystem.wealth.copy(emergencyFundBalance = updatedEmergencyFund, payrollReserve = updatedPayrollReserve, businessReserve = updatedBusinessReserve, bondsBalance = updatedBondsBalance, financialReviewLog = updatedFinancialReviewLog, lastReviewWeek = lastReviewWeek, lastReviewYear = lastReviewYear, financialHistoryCashFlow = updatedHistoryCashFlow),
                spiritual = stateWithEcosystem.spiritual.copy(spiritualJournal = updatedSpiritualJournal)
            )

            // Trigger Annual Awards Show
            if (triggerAwardsShow) {
                finalState = runAnnualAwardsShow(finalState)
            }

            finalState
        }
    }

    fun calculateMarketSentiment(
        economyCycle: String,
        stocks: List<Stock>,
        cryptos: List<Crypto>
    ): MarketSentimentInfo {
        val base = when (economyCycle) {
            "Boom" -> 78
            "Stable Growth" -> 56
            "Inflation Peak" -> 40
            "Recession" -> 26
            "Depression" -> 14
            else -> 50
        }
        var totalPct = 0.0
        var count = 0
        stocks.forEach { s ->
            if (s.history.size >= 2 && s.history.first() > 0.0) {
                totalPct += ((s.currentPrice - s.history.first()) / s.history.first()) * 100.0
                count++
            }
        }
        cryptos.forEach { c ->
            if (c.history.size >= 2 && c.history.first() > 0.0) {
                totalPct += ((c.currentPrice - c.history.first()) / c.history.first()) * 100.0
                count++
            }
        }
        val avgTrend = if (count > 0) (totalPct / count).coerceIn(-15.0, 15.0) else 0.0
        val score = (base + avgTrend).toInt().coerceIn(5, 95)
        val (label, summary, tip) = when {
            score >= 76 -> Triple(
                "Extreme Greed",
                "Market euphoria is elevated. Speculative assets and tech equities are testing upper resistance limits with heavy retail order flow.",
                "Elevated pullback risk. Avoid FOMO-buying breakouts; consider locking in profits or maintaining liquid cash."
            )
            score >= 56 -> Triple(
                "Greed",
                "Bullish sentiment is building across digital currencies and corporate equities. Corporate earnings growth and expansion momentum are solid.",
                "Momentum is favorable, but keep watch on key resistance bands as assets move higher."
            )
            score >= 45 -> Triple(
                "Neutral",
                "Market is in balanced consolidation. Neither buyers nor sellers have decisive control, keeping equities in orderly trading ranges.",
                "Excellent environment for dividend accumulation and disciplined dollar-cost averaging into undervalued assets."
            )
            score >= 25 -> Triple(
                "Fear",
                "Risk-off caution has gripped investors. Valuations have compressed, leading to oversold conditions in speculative sectors.",
                "Favorable risk/reward for patient long-term buyers. Defensive blue-chips and REITs offer steady dividend income."
            )
            else -> Triple(
                "Extreme Fear",
                "Pervasive market pessimism. Asset prices are testing multi-month support floors with heavy institutional value bids emerging.",
                "Historical value accumulation zone. Major blue-chips and digital assets rarely remain at structural support for long."
            )
        }
        return MarketSentimentInfo(score, label, summary, tip)
    }

    fun getMarketSentimentInfo(state: GameStateEntity? = gameState.value): MarketSentimentInfo {
        if (state == null) return MarketSentimentInfo(50, "Neutral", "Consolidating in range.", "Maintain a balanced portfolio.")
        return calculateMarketSentiment(state.economyCycle, state.stocks, state.cryptos)
    }

    private fun generateWeeklyNewsArticles(
        nextWeek: Int,
        nextYear: Int,
        highestTrendPercent: Float,
        trendSubgenreName: String,
        trendGenreName: String,
        trendWeeksRemaining: Int,
        economyCycle: String,
        inflationRate: Float,
        fuelPrice: Float,
        isElectionSeason: Boolean,
        sentimentInfo: MarketSentimentInfo
    ): MutableList<NewsArticle> {
        return mutableListOf(
            // 1. Music / Trend Article
            NewsArticle(
                id = "news_trend_${nextWeek}_${nextYear}",
                headline = "📈 Listener Trend: $trendSubgenreName is gaining attention",
                subtitle = "$trendGenreName has a temporary modeled discovery advantage of about ${highestTrendPercent.toInt()}%, with $trendWeeksRemaining week(s) remaining.",
                body = "Listener interest has shifted toward $trendGenreName, especially the $trendSubgenreName sound. The current market cycle has $trendWeeksRemaining week(s) left before another genre takes the lead. Songs in the active genre receive a temporary discovery advantage, but release quality, artist reach, and promotion still determine how listeners respond. Plan your release timing around the remaining trend window; the boost is temporary and does not guarantee a hit.",
                author = "Marcus Vance",
                outlet = "Billboard Daily",
                category = "Music",
                coverImageId = "ic_music_trend",
                week = nextWeek,
                year = nextYear
            ),
            // 2. Business / Economic Article
            NewsArticle(
                id = "news_biz_${nextWeek}_${nextYear}",
                headline = "Market Pulse: Investor Sentiment Registers ${sentimentInfo.label} (${sentimentInfo.score}/100)",
                subtitle = "Macro cycle is $economyCycle with inflation at ${String.format("%.1f", inflationRate * 100)}% and fuel at $${String.format("%.2f", fuelPrice)}/L.",
                body = "Global equities and digital asset trading desks report that market sentiment is currently '${sentimentInfo.label}' with an index score of ${sentimentInfo.score}/100. ${sentimentInfo.summary} Chief financial strategists advise: '${sentimentInfo.actionableTip}' Meanwhile, disciplined investors continue anchoring portfolios with dependable weekly cash dividends from commercial real estate and established distribution firms.",
                author = "Sarah Sterling",
                outlet = "Financial Desk",
                category = "Business",
                coverImageId = "ic_financial",
                week = nextWeek,
                year = nextYear
            ),
            // 3. Ecosystem / Staff / Live Article
            NewsArticle(
                id = "news_eco_${nextWeek}_${nextYear}",
                headline = "Automated Wealth Management and Digital Security Redefined",
                subtitle = "Top digital publicists confirm that delegating online channels to automated PR tools and hiring active Security is the fastest way to build stable careers.",
                body = "In a fast-paced media climate, hiring a specialized Social Media Manager (SMM) has evolved from a luxury into an absolute career necessity. Public relations agency directories report that automatic publishing permissions allow SMMs to maintain constant audience engagement with zero manual efforts. Furthermore, forensic accounting experts warn that hiring qualified Security protects portfolios from embezzlement, recovering funds and suing rogue employees with massive interest penalties.",
                author = "Olivia Pope",
                outlet = "Industry Insider",
                category = "Ecosystem",
                coverImageId = "ic_ecosystem",
                week = nextWeek,
                year = nextYear
            ),
            // 4. Global News / Politics Article
            NewsArticle(
                id = "news_global_${nextWeek}_${nextYear}",
                headline = if (isElectionSeason) "Electoral Volatility Sweeps Ad Agencies!" else "Global Transit Costs React to Stable Oil Indexes",
                subtitle = if (isElectionSeason) "With a high-stakes election season under way, bidding competition for billboards, TV, and radio ad slots has reached a fever pitch." else "International logistics networks report standard margins on regional travel routes, supporting easier concert bookings.",
                body = if (isElectionSeason) "As national campaigns saturate media networks, ad agencies are raising their minimum bidding budgets, directly impacting independent music promo campaigns. Political analysts suggest that consumer attention span is highly fragmented, requiring smart targeting and higher promotional budgets from artists hoping to stand out." else "Standard transit pricing is providing major breathing room for national touring models. Tour managers are recommending immediate booking confirmations to lock in favorable logistics prices before any sudden market changes occur.",
                author = "Evelyn Vance",
                outlet = "Street Report",
                category = "Global",
                coverImageId = "ic_global",
                week = nextWeek,
                year = nextYear
            )
        )
    }

    private data class AutonomousActionsResult(
        val addedCash: Double,
        val remainingPendingReplies: List<PendingChatReply>
    )

    private fun simulateLivingWorldAutonomousActions(
        state: GameStateEntity,
        updatedNPCs: List<NPCEntity>,
        finalNPCs: MutableList<NPCEntity>,
        mutCastPosts: MutableList<CastPost>,
        finalChatConversations: MutableList<ChatConversation>,
        updatedMail: MutableList<MailMessage>,
        updatedSongs: List<Song>,
        reportBuilder: StringBuilder
    ): AutonomousActionsResult {
        var extraCash = 0.0

        // A. Independent CAST Posts - Multiple artists post EVERY SINGLE WEEK!
        val activeNpcs = updatedNPCs.filter { !it.isRetired }.shuffled()
        val postersThisWeek = activeNpcs.take(kotlin.random.Random.nextInt(2, 5))
        for (npc in postersThisWeek) {
            val targetName = state.artistName
            val isPolitician = npc.id.startsWith("npc_gov") || npc.id.startsWith("npc_sen") ||
                    npc.name.contains("Governor", ignoreCase = true) ||
                    npc.name.contains("Senator", ignoreCase = true) ||
                    npc.name.contains("Mayor", ignoreCase = true) ||
                    npc.name.contains("Council", ignoreCase = true) ||
                    npc.name.contains("President", ignoreCase = true)

            val postContent = if (isPolitician) {
                listOf(
                    "🏛 Signed the Live Music Venue Preservation Act today! Allocating $15M in emergency state grants to historic music clubs and independent stages. This protects our local cultural heritage and ensures independent musicians have vibrant stages to perform on! 🎸🏛",
                    "📊 Lowering state commercial tax on professional recording studio gear, instruments, and audio interfaces by 4.5% starting next quarter! This leaves more cash in the pockets of local musicians producing albums in our state. 🎙💰",
                    "⚖ Introducing the Fair Digital Streaming Transparency Bill in the legislature! Requiring DSP platforms to establish a guaranteed minimum royalty floor for independent creators. Musicians deserve fair compensation for their craft! 📱📈",
                    "🏗 Groundbreaking ceremony for the new $40M Downtown Arts & Arena Pavilion today! A 12,000-seat civic facility with world-class acoustics — reserving dedicated opening performance slots for rising local artists! 🏟✨",
                    "📜 Enacted the Anti-Ticket Bot & Scalper Prevention Law! Mandating strict 10% caps on secondary concert resale margins so genuine fans can afford tickets and touring musicians keep 100% of their gate profits! 🎟🚫",
                    "💰 Announcing the $10M Independent Creator Touring & Grant Subsidy! Registered local artists with active catalogs can apply for up to $15,000 in album production grants and tour travel stipends. 🚐🎵",
                    "🚨 Debating the proposed 2% Luxury Entertainment Surcharge on major conglomerate promoters. Small indie venues and local artists remain 100% exempt, keeping ticket prices accessible! 🏛📑",
                    "🚦 Upgraded municipal soundproofing grants for neighborhood live bars. Helping indie venues host live bands until 2:00 AM without police curfew fines or noise citations! 🎷🌃",
                    "🛣 Expanding late-night metro transit lines directly connecting downtown music corridors to outer boroughs. Safe travel for concertgoers means a 30% surge in weekday live gig attendance for our local artists! 🚇🎶",
                    "📜 Street Performance Deregulation is now official! Buskers and acoustic musicians are no longer burdened by costly licensing fees to share their music in public parks and civic plazas! 🪕🌳"
                ).random()
            } else when {
                npc.relationshipWithPlayer < 35f -> {
                    listOf(
                        "Heard @${targetName}'s latest drop... is it just me or is it totally over-hyped? Mid. 🥱",
                        "Some artists spend more time buying fake streams than practicing vocal scales. naming no names @${targetName}!",
                        "Honestly, @${targetName} is proof that you don't need raw talent to get industry play. Just marketing money.",
                        "Just saw @${targetName}'s social post. So cringe. Keep trying sweetie! ✨"
                    ).random()
                }
                npc.relationshipWithPlayer > 70f -> {
                    listOf(
                        "Stream @${targetName}'s new tracks! Literally the best songwriter in the world right now! 😭👑",
                        "Absolute genius. We need a collab ASAP @${targetName}! The studio is calling us.",
                        "So much respect for @${targetName}. True artistry in a world full of industry plants! 🙌💖",
                        "Woke up and chose to stream @${targetName} all day. Vibe is absolutely immaculate."
                    ).random()
                }
                else -> {
                    val otherNPC = updatedNPCs.filter { it.id != npc.id && !it.isRetired }.randomOrNull()
                    if (otherNPC != null && kotlin.random.Random.nextFloat() > 0.5f) {
                        listOf(
                            "Honestly, @${otherNPC.name} has zero stage presence. Change my mind. 🤭",
                            "Just heard @${otherNPC.name}'s new album. Sounds like a dial-up modem crashing in the rain. Standard.",
                            "Why is @${otherNPC.name} acting like they wrote that hit? We all know they have a team of 15 ghostwriters.",
                            "I respect the hustle but @${otherNPC.name}'s vocals sound like they are yawning in the mic."
                        ).random()
                    } else {
                        listOf(
                            "Just cooked some absolute heat in the studio today. Album of the year incoming! 🚀🔥",
                            "The grind never stops. Independent and thriving! No label suites can box me in.",
                            "Tour life is exhausting but seeing the fans makes every sleepless night worth it. ❤✈",
                            "Focusing on my peace, my health, and my frequencies. New vibrations on the way."
                        ).random()
                    }
                }
            }

            val postEngagement = castPostEngagement(
                fans = npc.fans,
                popularity = npc.popularity,
                variation = kotlin.random.Random.nextFloat()
            )
            val npcCastPost = CastPost(
                id = "npc_cast_" + java.util.UUID.randomUUID().toString().take(6),
                authorName = npc.name,
                authorHandle = "@" + npc.name.replace(" ", "").lowercase(),
                content = postContent,
                likes = postEngagement.likes,
                reposts = postEngagement.reposts,
                commentsCount = postEngagement.comments,
                week = state.currentWeek,
                year = state.currentYear,
                isByPlayer = false
            )
            mutCastPosts.add(0, npcCastPost)
        }
        if (mutCastPosts.size > 2500) {
            val trimmed = mutCastPosts.take(2500)
            mutCastPosts.clear()
            mutCastPosts.addAll(trimmed)
        }

        // Sponsor Website Ads on Cast Feed - 1 to 2 rotating ads every single week from authentic sites!
        val allSponsorAds = listOf(
            Triple("Alphadom Cloud Studio", "@alphadom", "Build, scale, and automate your independent music label on the cloud. Visit alphadom.online to deploy your next studio workstation today."),
            Triple("Apex Records & Publishing", "@apexrecords", "Looking for raw talent and demo submissions. Check out our global contract roster at apexrecord.org and get heard by A&R executives."),
            Triple("Who Is Got Talent", "@wgt_show", "Auditions are now open worldwide! Compete on the main stage and secure your grand finale spot at wgt.show."),
            Triple("SOLD Real Estate Exchange", "@sold_properties", "Premium penthouse suites, private recording studios, and countryside estates listed daily. Browse available luxury deeds at sold.com."),
            Triple("PulseWire News Network", "@pulsewire", "Breaking industry analysis, chart shifts, and exclusive interviews. Read real-time stories at pulsewire.com."),
            Triple("BlockVault Crypto Desk", "@blockvault", "Trade Bitcoin, Ethereum, and emerging digital tokens with ultra-low slippage. Deposit and withdraw instantly at blockvault.io."),
            Triple("Metro Sound Exchange", "@metrosound", "Vintage Neve consoles, Telefunken tube microphones, and analog tape machines on sale this week at metrosound.store."),
            Triple("SonicBloom Audio", "@sonicbloom", "Next-generation master limiter and stereo imaging plugins for professional bedroom producers. Try free for 14 days at sonicbloom.audio."),
            Triple("Vanguard Private Wealth", "@vanguardwealth", "Bespoke wealth management, family office trusts, and tax sheltering strategies for platinum-selling recording artists at vanguardwealth.net."),
            Triple("Velvet Room Nightclub", "@velvetroom", "VIP bottle service, celebrity guest DJ sets, and underground industry networking every Friday night. Reserve your table at velvetroom.club.")
        )
        val adIndices = (allSponsorAds.indices).shuffled().take(kotlin.random.Random.nextInt(1, 3))
        for (idx in adIndices) {
            val ad = allSponsorAds[idx]
            val adCastPost = CastPost(
                id = "ad_cast_" + java.util.UUID.randomUUID().toString().take(6),
                authorName = ad.first,
                authorHandle = ad.second,
                content = ad.third,
                likes = kotlin.random.Random.nextLong(450, 7500),
                reposts = kotlin.random.Random.nextLong(120, 1900),
                commentsCount = kotlin.random.Random.nextLong(30, 420),
                week = state.currentWeek,
                year = state.currentYear,
                isByPlayer = false
            )
            mutCastPosts.add(0, adCastPost)
        }

        // B. Spontaneous private SeeChat Messages (15% chance)
        if (kotlin.random.Random.nextFloat() < 0.15f) {
            val npc = updatedNPCs.filter { !it.isRetired }.randomOrNull()
            if (npc != null) {
                val npcId = npc.id
                val npcName = npc.name
                val relation = npc.relationshipWithPlayer

                val spontaneousText = when {
                    relation > 70f -> {
                        listOf(
                            "Hey! Just wanted to send some love. You've been killing it lately. We should hang out!",
                            "Yo, are you free this week? Let's hit the studio and swap some ideas. 🎹",
                            "Just wanted to say I really value our friendship. Keep shining, my friend! ✨",
                            "I was reading about your company. If you ever need advice or partnership, I'm here!"
                        ).random()
                    }
                    relation < 35f -> {
                        listOf(
                            "Saw your recent numbers. Is that really the best you can do? 🤭",
                            "My producers laughed at your latest mix. It's cool, keep trying though!",
                            "Just signed a massive promo deal. Get ready to be completely eclipsed on the charts.",
                            "You should probably practice more and post less. Just some friendly advice."
                        ).random()
                    }
                    else -> {
                        listOf(
                            "Hey, how is the studio treating you? Stay safe out there.",
                            "Heard there's a new wave of trends coming next week. Make sure you're ready!",
                            "Sup! Keeping my head down and grinding. Catch you around the city!"
                        ).random()
                    }
                }

                val spontaneousMsg = ChatMessage(
                    id = java.util.UUID.randomUUID().toString().take(6),
                    senderId = npcId,
                    senderName = npcName,
                    message = spontaneousText,
                    timestamp = System.currentTimeMillis()
                )

                val existingIndex = finalChatConversations.indexOfFirst { it.npcId == npcId }
                if (existingIndex != -1) {
                    val conv = finalChatConversations.removeAt(existingIndex)
                    finalChatConversations.add(0, conv.copy(
                        messages = conv.messages + spontaneousMsg,
                        unreadCount = conv.unreadCount + 1
                    ))
                } else {
                    finalChatConversations.add(0,
                        ChatConversation(
                            npcId = npcId,
                            npcName = npcName,
                            messages = listOf(spontaneousMsg),
                            unreadCount = 1
                        )
                    )
                }
                reportBuilder.append("📱 SeeChat: Received a direct private message from $npcName!\n")
            }
        }

        // C. Spontaneous Direct Emails (10% chance, requires established catalog)
        val hasEstablishedMarketProfile = updatedSongs.count { it.isReleased } >= 2 && updatedSongs.sumOf { it.totalStreams } >= 25000L && state.popularity >= 30f
        if (hasEstablishedMarketProfile && kotlin.random.Random.nextFloat() < 0.10f) {
            val brandOffer = listOf(
                Triple("HyperX Beats", "Sponsorship Collaboration Proposal", "Hello " + state.artistName + ",\n\nWe love your unique sonic aesthetic and public presence. We would love to discuss a collaborative partnership or hardware endorsement deal in the near future. Keep raising the bar!\n\nBest,\nMarketing Team\nHyperX Beats"),
                Triple("Apex Fitwear", "Custom Apparel Collaboration", "Hi " + state.artistName + ",\n\nOur design division has been tracking your rising world rank. We are interesting in sending you custom activewear and modeling a bespoke capsule collection under your name. Let us know if you have bandwidth!\n\nWarmly,\nVP of Lifestyle\nApex Fitwear"),
                Triple("Zora Cosmetics", "Press Package & Styling Partnership", "Dear " + state.artistName + ",\n\nYour visual profile is stunning. We would love to send a luxury styling package to your estate this week. Let's arrange a public feature!\n\nSincerely,\nPriscilla Sterling\nZora Cosmetics")
            ).random()

            val randomEmailId = "mail_spont_" + java.util.UUID.randomUUID().toString().take(6)
            val emailMsg = MailMessage(
                id = randomEmailId,
                sender = brandOffer.first,
                subject = brandOffer.second,
                body = brandOffer.third,
                type = "NORMAL",
                expiresWeek = state.currentWeek + 3,
                expiresYear = state.currentYear
            )
            updatedMail.add(emailMsg)
            reportBuilder.append("✉ Mail: Received a partnership query from ${brandOffer.first}.\n")
        }

        // D. Living World SeeChat Pending Delayed Replies
        val remainingPendingReplies = mutableListOf<PendingChatReply>()
        for (pending in state.pendingChatReplies) {
            val isDue = (state.currentYear > pending.scheduledYear) ||
                        (state.currentYear == pending.scheduledYear && state.currentWeek >= pending.scheduledWeek)
            if (isDue) {
                val npc = updatedNPCs.find { it.id == pending.npcId }
                val npcName = npc?.name ?: "Contact"
                val npcMsg = ChatMessage(
                    id = java.util.UUID.randomUUID().toString().take(6),
                    senderId = pending.npcId,
                    senderName = npcName,
                    message = pending.replyText,
                    timestamp = System.currentTimeMillis()
                )
                val existingIndex = finalChatConversations.indexOfFirst { it.npcId == pending.npcId }
                if (existingIndex != -1) {
                    val conv = finalChatConversations.removeAt(existingIndex)
                    finalChatConversations.add(0, conv.copy(
                        messages = conv.messages + npcMsg,
                        unreadCount = conv.unreadCount + 1
                    ))
                } else {
                    finalChatConversations.add(0,
                        ChatConversation(
                            npcId = pending.npcId,
                            npcName = npcName,
                            messages = listOf(npcMsg),
                            unreadCount = 1
                        )
                    )
                }
                if (pending.addedCash > 0) {
                    extraCash += pending.addedCash
                    reportBuilder.append("💰 SeeChat: Received $${String.format("%,.0f", pending.addedCash)} transfer from $npcName!\n")
                }
                if (pending.relationChange != 0f && npc != null) {
                    val npcIdx = finalNPCs.indexOfFirst { it.id == npc.id }
                    if (npcIdx != -1) {
                        finalNPCs[npcIdx] = finalNPCs[npcIdx].copy(
                            relationshipWithPlayer = (finalNPCs[npcIdx].relationshipWithPlayer + pending.relationChange).coerceIn(0f, 100f)
                        )
                    }
                }
                reportBuilder.append("📱 SeeChat: New reply received from $npcName!\n")
            } else {
                remainingPendingReplies.add(pending)
            }
        }

        return AutonomousActionsResult(addedCash = extraCash, remainingPendingReplies = remainingPendingReplies)
    }

    private data class WgtProgressionResult(
        val updatedWebHub: WebHubState,
        val addedCash: Double
    )

    private fun simulateWgtProgression(
        state: GameStateEntity,
        initialWebHub: WebHubState,
        updatedMail: MutableList<MailMessage>,
        generatedNews: MutableList<NewsArticle>,
        reportBuilder: StringBuilder
    ): WgtProgressionResult {
        var currentWebHub = initialWebHub
        var addedCash = 0.0

        if (currentWebHub.wgtAuditionWeek == 0) {
            val isFirstHalf = state.currentWeek <= 20
            val sNum = if (isFirstHalf) 1 else 2
            val sYear = state.currentYear
            val audW = if (isFirstHalf) kotlin.random.Random.nextInt(7, 12) else kotlin.random.Random.nextInt(28, 33)
            val semiW = audW + 4
            val finW = (semiW + 4).coerceAtMost(if (isFirstHalf) 20 else 40)
            currentWebHub = currentWebHub.copy(
                wgtSeason = sNum,
                wgtSeasonYear = sYear,
                wgtAuditionYear = sYear,
                wgtAuditionWeek = audW,
                wgtSemiFinalsWeek = semiW,
                wgtGrandFinaleWeek = finW
            )
        }

        // If player was ROLLED OVER, once the old season is over (past grand finale) and new season starts, promote status to REGISTERED_SELECTED!
        if (currentWebHub.wgtRegistrationStatus == "REGISTERED_ROLLED_OVER" &&
            (state.currentYear > currentWebHub.wgtSeasonYear || (state.currentYear == currentWebHub.wgtSeasonYear && state.currentWeek > currentWebHub.wgtGrandFinaleWeek))) {
            currentWebHub = currentWebHub.copy(wgtRegistrationStatus = "REGISTERED_SELECTED")
            val promoMail = MailMessage(
                id = "wgt_promoted_" + java.util.UUID.randomUUID().toString().take(6),
                sender = "WGT Executive Production",
                subject = "⭐ AUDITION CALL: Your Rolled-Over Ticket is Now Active!",
                body = "Dear ${state.artistName},\n\nYour rolled-over registration is now ACTIVE for the upcoming Season ${currentWebHub.wgtSeason} live auditions!\n\n• Live Audition Broadcast: Week ${currentWebHub.wgtAuditionWeek}, Year ${currentWebHub.wgtSeasonYear}\n• Prepare your performance piece. Good luck!\n\n- WGT Production Team",
                type = "NORMAL",
                expiresWeek = currentWebHub.wgtAuditionWeek + 2,
                expiresYear = currentWebHub.wgtSeasonYear
            )
            updatedMail.add(promoMail)
            reportBuilder.append("⭐ WGT: Your rolled-over audition pass is now active for Season ${currentWebHub.wgtSeason} (Audition Week ${currentWebHub.wgtAuditionWeek})!\n")
        }

        // Stage: Audition Week reminder
        if (state.currentWeek == currentWebHub.wgtAuditionWeek && state.currentYear == currentWebHub.wgtSeasonYear) {
            if (currentWebHub.wgtRegistrationStatus == "REGISTERED_SELECTED") {
                reportBuilder.append("🎤 WHO IS GOT TALENT: LIVE AUDITION BROADCAST THIS WEEK! Open WebHub to perform your audition piece on national television!\n")
            }
        }

        // Stage: Semi-Finals reminder
        if (state.currentWeek == currentWebHub.wgtSemiFinalsWeek && state.currentYear == currentWebHub.wgtSeasonYear) {
            if (currentWebHub.wgtRegistrationStatus == "ADVANCED_SEMI_FINALS") {
                reportBuilder.append("🌟 WHO IS GOT TALENT: NATIONAL SEMI-FINALS BROADCAST THIS WEEK! Open WebHub to perform your semi-finals showcase!\n")
            }
        }

        // Stage: Grand Finale Broadcast!
        if (state.currentWeek == currentWebHub.wgtGrandFinaleWeek && state.currentYear == currentWebHub.wgtSeasonYear) {
            val contestants = listOf("Marcus Vance", "Elena Rostova", "Kai Tanaka", "Daphne Thorne", "Leo Martinez", "Chloe King")
            val isPlayerInFinale = currentWebHub.wgtRegistrationStatus == "ADVANCED_FINALE"

            val winnerName: String
            val playerWon: Boolean
            if (isPlayerInFinale) {
                val vocalComp = (state.voiceSkill * 0.40f).coerceIn(0f, 40f)
                val writingComp = (state.writingSkill * 0.25f).coerceIn(0f, 25f)
                val charismaComp = ((state.popularity + state.publicImage) * 0.15f).coerceIn(0f, 20f)
                val energyMod = if (state.energy < 25) -15f else 5f
                val stressMod = (state.stress * 0.15f).coerceIn(0f, 15f)
                val luck = kotlin.random.Random.nextFloat() * 20f
                val finalScore = (vocalComp + writingComp + charismaComp + energyMod - stressMod + luck).coerceIn(20f, 100f)

                if (finalScore >= 78f) {
                    winnerName = state.artistName
                    playerWon = true
                } else {
                    winnerName = contestants.random()
                    playerWon = false
                }
            } else {
                winnerName = contestants.random()
                playerWon = false
            }

            currentWebHub = currentWebHub.copy(wgtLastWinner = winnerName)

            if (playerWon) {
                val prize = 30000.0
                val winFans = kotlin.random.Random.nextLong(30000, 50000)
                addedCash += prize
                val winMail = MailMessage(
                    id = "wgt_champ_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "WGT Broadcast Network",
                    subject = "🏆 GRAND CHAMPION: You Won Who Is Got Talent Season ${currentWebHub.wgtSeason}!",
                    body = "CONGRATULATIONS!\n\nBy nationwide vote and unanimous judges' acclaim, you have won the Who Is Got Talent Season ${currentWebHub.wgtSeason} National Championship!\n\n• Grand Championship Prize: $${String.format("%,.2f", prize)} Cash Transferred\n• Exposure Surge: +${String.format("%,d", winFans)} Fans\n• Industry Popularity: +20%\n• Industry Respect: +18%\n\nYour championship trophy and national broadcast triumph will go down in entertainment history!",
                    type = "NORMAL",
                    expiresWeek = 52,
                    expiresYear = state.currentYear + 1
                )
                updatedMail.add(winMail)
                currentWebHub = currentWebHub.copy(wgtRegistrationStatus = "WINNER", wgtWon = true, wgtCompletedCount = currentWebHub.wgtCompletedCount + 1)
                reportBuilder.append("🏆 WHO IS GOT TALENT CHAMPION: You won the Grand Finale! Received $${String.format("%,.0f", prize)} and +${String.format("%,d", winFans)} fans!\n")
            } else if (isPlayerInFinale) {
                val runnerPrize = 7500.0
                val runnerFans = kotlin.random.Random.nextLong(10000, 20000)
                addedCash += runnerPrize
                val runnerMail = MailMessage(
                    id = "wgt_runner_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "WGT Broadcast Network",
                    subject = "🥈 NATIONAL RUNNER-UP: Who Is Got Talent Finale",
                    body = "What a battle! You finished as the National Runner-Up of Who Is Got Talent Season ${currentWebHub.wgtSeason}. Winner: $winnerName.\n\n• Runner-Up Award: $${String.format("%,.2f", runnerPrize)}\n• Exposure Gained: +${String.format("%,d", runnerFans)} Fans\n• Popularity: +10%",
                    type = "NORMAL",
                    expiresWeek = 52,
                    expiresYear = state.currentYear + 1
                )
                updatedMail.add(runnerMail)
                currentWebHub = currentWebHub.copy(wgtRegistrationStatus = "RUNNER_UP", wgtCompletedCount = currentWebHub.wgtCompletedCount + 1)
                reportBuilder.append("🥈 WHO IS GOT TALENT: Finished as National Runner-Up! Awarded $${String.format("%,.0f", runnerPrize)} and +${String.format("%,d", runnerFans)} fans!\n")
            } else {
                reportBuilder.append("📺 WHO IS GOT TALENT: The Season ${currentWebHub.wgtSeason} Grand Finale has broadcast! National Champion: $winnerName.\n")
            }

            val finaleArticle = NewsArticle(
                id = "news_wgt_fin_" + java.util.UUID.randomUUID().toString().take(6),
                headline = "Who Is Got Talent Crowns Season ${currentWebHub.wgtSeason} Winner!",
                subtitle = "$winnerName takes the crown after a thrilling nationwide showdown.",
                body = "The nationwide Grand Finale of Who Is Got Talent Season ${currentWebHub.wgtSeason} concluded with breathtaking performances. $winnerName was crowned the National Champion, taking home a major cash prize and nationwide recognition.",
                author = "PulseWire Desk",
                outlet = "PulseWire Entertainment",
                category = "Entertainment",
                coverImageId = "ic_trophy",
                week = state.currentWeek,
                year = state.currentYear
            )
            generatedNews.add(0, finaleArticle)

            // Schedule NEXT show:
            val isNextFirst = currentWebHub.wgtSeason == 2
            val nextSeasonNum = if (isNextFirst) 1 else 2
            val nextSeasonYr = if (isNextFirst) state.currentYear + 1 else state.currentYear
            val nextAuditionWeek = if (isNextFirst) kotlin.random.Random.nextInt(7, 12) else kotlin.random.Random.nextInt(28, 33)
            val nextSemiFinalsWeek = nextAuditionWeek + 4
            val nextFinaleWeek = (nextSemiFinalsWeek + 4).coerceAtMost(if (isNextFirst) 20 else 40)

            val keepRegistration = currentWebHub.wgtRegistrationStatus == "REGISTERED_ROLLED_OVER"
            currentWebHub = currentWebHub.copy(
                wgtSeason = nextSeasonNum,
                wgtSeasonYear = nextSeasonYr,
                wgtAuditionWeek = nextAuditionWeek,
                wgtSemiFinalsWeek = nextSemiFinalsWeek,
                wgtGrandFinaleWeek = nextFinaleWeek,
                wgtApplied = keepRegistration,
                wgtRegistrationStatus = if (keepRegistration) "REGISTERED_SELECTED" else "NONE"
            )
        }

        return WgtProgressionResult(
            updatedWebHub = currentWebHub,
            addedCash = addedCash
        )
    }

    private fun runAnnualAwardsShow(state: GameStateEntity): GameStateEntity {
        val report = StringBuilder()
        report.append("\n🏆🏆 THE ANNUAL ARTIST STORY AWARDS CEREMONY 🏆🏆\n\n")

        val newsArticlesList = state.newsArticles.toMutableList()
        val mailboxList = state.mailbox.toMutableList()

        var wonAwards = mutableListOf<String>()
        var lostAwards = mutableListOf<String>()

        var bonusCash = 0.0
        var bonusPop = 0.0f
        var bonusRespect = 0.0f

        // --- 1. Best Artist of the Year ---
        val playerPopularity = state.popularity
        val candidate1Pop = 85f
        val candidate2Pop = 75f
        val candidate3Pop = 60f
        report.append("🌟 CATEGORY: BEST ARTIST OF THE YEAR\n")
        report.append("• Nominees: ${state.artistName} ($playerPopularity%), Aria Nova ($candidate1Pop%), DJ Pulse ($candidate2Pop%), Shadow Lyricist ($candidate3Pop%)\n")
        if (playerPopularity > candidate1Pop) {
            wonAwards.add("Best Artist of the Year")
            bonusPop += 25f
            bonusRespect += 20f
            bonusCash += 50000.0
            report.append("👉 WINNER: **${state.artistName}**!\n\n")
        } else {
            lostAwards.add("Best Artist of the Year")
            report.append("👉 WINNER: **Aria Nova**!\n\n")
        }

        // --- 2. Best Song of the Year ---
        val playerBestSongRating = state.songs.maxOfOrNull { it.rating } ?: 0f
        val candidate1Song = 4.8f
        val candidate2Song = 4.5f
        report.append("🎵 CATEGORY: BEST SONG OF THE YEAR\n")
        report.append("• Nominees: ${state.artistName} (${String.format("%.1f", playerBestSongRating)}★), Aria Nova ($candidate1Song★), DJ Pulse ($candidate2Song★)\n")
        if (playerBestSongRating > candidate1Song) {
            wonAwards.add("Best Song of the Year")
            bonusPop += 20f
            bonusRespect += 15f
            bonusCash += 40000.0
            report.append("👉 WINNER: **${state.artistName}**!\n\n")
        } else {
            lostAwards.add("Best Song of the Year")
            report.append("👉 WINNER: **Aria Nova**!\n\n")
        }

        // --- 3. Content Creator of the Year ---
        val playerSubscribers = state.meTubeSubscribers
        val candidate1Subs = 450000L
        val candidate2Subs = 200000L
        report.append("📺 CATEGORY: CONTENT CREATOR OF THE YEAR\n")
        report.append("• Nominees: ${state.artistName} (${String.format("%,d", playerSubscribers)} subs), Drake Drake (${String.format("%,d", candidate1Subs)} subs), Lady Gogo (${String.format("%,d", candidate2Subs)} subs)\n")
        if (playerSubscribers > candidate1Subs) {
            wonAwards.add("Content Creator of the Year")
            bonusPop += 15f
            bonusCash += 30000.0
            report.append("👉 WINNER: **${state.artistName}**!\n\n")
        } else {
            lostAwards.add("Content Creator of the Year")
            report.append("👉 WINNER: **Drake Drake**!\n\n")
        }

        // --- 4. Largest Stream Count of the Year ---
        val playerStreams = state.songs.sumOf { it.totalStreams }
        val candidate1Streams = 10000000L
        val candidate2Streams = 5000000L
        report.append("📈 CATEGORY: LARGEST STREAM COUNT OF THE YEAR\n")
        report.append("• Nominees: ${state.artistName} (${String.format("%,d", playerStreams)} streams), Travis Scottie (${String.format("%,d", candidate1Streams)} streams), Post Malone-y (${String.format("%,d", candidate2Streams)} streams)\n")
        if (playerStreams > candidate1Streams) {
            wonAwards.add("Largest Stream Count of the Year")
            bonusPop += 20f
            bonusRespect += 15f
            bonusCash += 40000.0
            report.append("👉 WINNER: **${state.artistName}**!\n\n")
        } else {
            lostAwards.add("Largest Stream Count of the Year")
            report.append("👉 WINNER: **Travis Scottie**!\n\n")
        }

        // --- 5. Most Social Artist of the Year ---
        val playerFollowers = state.followers
        val candidate1Followers = 50000L
        val candidate2Followers = 25000L
        report.append("💬 CATEGORY: MOST SOCIAL ARTIST OF THE YEAR\n")
        report.append("• Nominees: ${state.artistName} (${String.format("%,d", playerFollowers)} followers), Kanye East (${String.format("%,d", candidate1Followers)} followers), Dua Lip-sync (${String.format("%,d", candidate2Followers)} followers)\n")
        if (playerFollowers > candidate1Followers) {
            wonAwards.add("Most Social Artist of the Year")
            bonusPop += 15f
            bonusCash += 20000.0
            report.append("👉 WINNER: **${state.artistName}**!\n\n")
        } else {
            lostAwards.add("Most Social Artist of the Year")
            report.append("👉 WINNER: **Kanye East**!\n\n")
        }

        // --- 6. Nepotism Award ---
        val playerIntegrity = state.integrity
        val isEligibleForNepo = playerIntegrity < 40f && state.cash > 50000.0
        if (isEligibleForNepo) {
            report.append("🤡 CATEGORY: THE NEPOTISM AWARD (Industry Favor)\n")
            report.append("• Nominees: ${state.artistName}, Alabaster Rock, Seraphina\n")
            val playerWinsNepo = kotlin.random.Random.nextBoolean()
            if (playerWinsNepo) {
                wonAwards.add("Nepotism Award")
                bonusPop += 5f
                bonusCash += 15000.0
                report.append("👉 WINNER: **${state.artistName}**!\n\n")
            } else {
                lostAwards.add("Nepotism Award")
                report.append("👉 WINNER: **Alabaster Rock**!\n\n")
            }
        }

        // Apply awards impact
        if (wonAwards.isNotEmpty()) {
            report.append("🎉 Congratulations! You won ${wonAwards.size} award(s) this year! Gained +$${String.format("%,.0f", bonusCash)} cash, +${bonusPop}% popularity, +${bonusRespect}% industry respect!\n")

            // Generate headline news for each won award
            wonAwards.forEach { award ->
                val newsId = "awards_news_" + java.util.UUID.randomUUID().toString().take(6)
                val article = NewsArticle(
                    id = newsId,
                    headline = "🏆 ${state.artistName} triumphs at Global Music Awards!",
                    subtitle = "Won '$award'!",
                    body = "The music industry is absolutely buzzing following the conclusion of the Annual Global Music Awards show. In a display of talent and dominance, ${state.artistName} took home the prestigious '$award' trophy, beating out tough competitors. Critics call this a career-defining moment!",
                    author = "Billboard Beat",
                    outlet = "Billboard Daily",
                    category = "Entertainment",
                    coverImageId = "placeholder_awards",
                    week = state.currentWeek,
                    year = state.currentYear,
                    relatedArtistName = state.artistName
                )
                newsArticlesList.add(article)
            }

            // Add a special lucrative collaboration request email as a reward
            val collabEmail = MailMessage(
                id = "awards_collab_offer_" + java.util.UUID.randomUUID().toString().take(6),
                sender = "Epic Records & Collab Agency",
                subject = "🤝 Premium Collaboration Opportunity: Celebrity Feature Offer",
                body = "Dear ${state.artistName},\n\nWe were absolutely blown away by your performance and wins at the Global Music Awards! On behalf of several major label artists, we would love to offer you a prestigious feature and collaboration request. This premium project will boost your streaming royalties and global reach immediately. Sign up by accepting this email!\n\nBest,\nEpic Collabs",
                cashOffer = 25000.0,
                type = "COLLAB",
                expiresWeek = state.currentWeek + 4,
                expiresYear = state.currentYear
            )
            mailboxList.add(collabEmail)
        } else {
            report.append("😢 Work harder next year! You didn't win any trophies this time, but the nominations boosted your popularity (+3%).\n")
            bonusPop += 3f
        }

        val awardsResultEmail = MailMessage(
            id = "awards_results_y${state.currentYear}",
            sender = "Global Music Awards Committee",
            subject = "🏆 RESULTS: Annual Global Music Awards Broadcast!",
            body = "Dear ${state.artistName},\n\nThe live broadcast is complete, and we have recorded the official results:\n\n" +
                    "🏆 Awards Won:\n" + (if (wonAwards.isEmpty()) "None" else wonAwards.joinToString("\n• ", prefix = "• ")) + "\n\n" +
                    "🥈 Runner-ups / Nominated:\n" + (if (lostAwards.isEmpty()) "None" else lostAwards.joinToString("\n• ", prefix = "• ")) + "\n\n" +
                    "Your wins have generated headline entertainment news articles, unlocked high-value collaboration opportunities, and significantly boosted your popularity and respect! Keep crafting!\n\nSincerely,\nGlobal Music Awards Committee Board",
            type = "NORMAL",
            expiresWeek = state.currentWeek + 4,
            expiresYear = state.currentYear
        )
        mailboxList.add(awardsResultEmail)

        _weeklyReport.value = _weeklyReport.value + "\n" + report.toString()

        return state.copy(
                cash = state.cash + bonusCash,
                popularity = (state.popularity + bonusPop).coerceAtMost(100f),
                industryRespect = (state.industryRespect + bonusRespect).coerceAtMost(100f),
                newsArticles = newsArticlesList,
                mailbox = mailboxList
            )
    }

    // Accept Mail Message
    fun acceptMail(mailId: String) {
        updateState { state ->
            val mail = state.mailbox.find { it.id == mailId }
            if (mail == null || mail.isAccepted) {
                return@updateState state
            }

            val updatedMail = state.mailbox.map { m ->
                if (m.id == mailId) m.copy(isAccepted = true) else m
            }

            var updatedCash = state.cash
            var updatedSignedLabelId = state.signedLabelId
            var updatedSignedLabelCut = state.signedLabelCut
            var updatedPopularity = state.popularity
            var updatedInfluence = state.influence
            var updatedTrustworthiness = state.trustworthiness
            var updatedPublicImage = state.publicImage
            var updatedIndustryRespect = state.industryRespect
            var updatedStress = state.stress
            var updatedEnergy = state.energy
            var updatedNPCs = state.npcs
            var updatedIntegrity = state.integrity
            var updatedHumility = state.humility
            var updatedGenerosity = state.generosity
            var updatedSpiritualJournal = state.spiritualJournal
            var updatedTimelineEvents = state.timelineEvents
            var updatedRecoupableAdvanceBalance = state.recoupableAdvanceBalance
            var updatedSignedLabelRole = state.signedLabelRole
            var updatedSignedLabelLevel = state.signedLabelLevel
            var updatedSignedLabelStartWeek = state.signedLabelStartWeek
            var updatedSignedLabelStartYear = state.signedLabelStartYear
            var updatedSignedLabelEndWeek = state.signedLabelEndWeek
            var updatedSignedLabelEndYear = state.signedLabelEndYear
            var updatedSignedLabelReqSingles = state.signedLabelReqSingles
            var updatedSignedLabelReqAlbumsLP = state.signedLabelReqAlbumsLP
            var updatedSignedLabelReqAlbumsEP = state.signedLabelReqAlbumsEP
            var updatedSignedLabelReqMeTubeVideos = state.signedLabelReqMeTubeVideos
            var updatedSignedLabelDeliveredSingles = state.signedLabelDeliveredSingles
            var updatedSignedLabelDeliveredAlbumsLP = state.signedLabelDeliveredAlbumsLP
            var updatedSignedLabelDeliveredAlbumsEP = state.signedLabelDeliveredAlbumsEP
            var updatedSignedLabelDeliveredMeTubeVideos = state.signedLabelDeliveredMeTubeVideos
            var updatedSongs = state.songs

            if (mail.type == "NORMAL") {
                if (mail.cashOffer > 0.0) {
                    updatedCash += mail.cashOffer
                    updatedIndustryRespect = (state.industryRespect + 5f).coerceAtMost(100f)
                    showToast("Accepted and completed referral gig! Earned $${String.format("%.2f", mail.cashOffer)}!")
                } else {
                    showToast("Acknowledged email: ${mail.subject}!")
                }
            } else if (mail.type == "BRAND_ENDORSEMENT") {
                updatedCash += mail.cashOffer
                updatedPublicImage = (state.publicImage + 12f).coerceAtMost(100f)
                updatedTrustworthiness = (state.trustworthiness + 6f).coerceAtMost(100f)
                val eventMsg = "Year ${state.currentYear}, Week ${state.currentWeek}: Signed brand endorsement partnership with ${mail.sender} ($${String.format("%,.0f", mail.cashOffer)})."
                updatedTimelineEvents = updatedTimelineEvents + eventMsg
                showToast("Signed brand endorsement with ${mail.sender}! Earned $${String.format("%,.0f", mail.cashOffer)} and boosted public image.")
            } else if (mail.type == "ROYAL_PERFORMANCE") {
                updatedCash += mail.cashOffer
                updatedPublicImage = (state.publicImage + 25f).coerceAtMost(100f)
                updatedIndustryRespect = (state.industryRespect + 20f).coerceAtMost(100f)
                val eventMsg = "Year ${state.currentYear}, Week ${state.currentWeek}: Delivered a command performance at the Royal Charity Gala ($${String.format("%,.0f", mail.cashOffer)} honorarium)."
                updatedTimelineEvents = updatedTimelineEvents + eventMsg
                showToast("Honored at the Royal Command Performance Gala! Received $${String.format("%,.0f", mail.cashOffer)} honorarium and immense cultural acclaim.")
            } else if (mail.type == "HIGH_STATUS_GIG") {
                updatedCash += mail.cashOffer
                updatedPublicImage = (state.publicImage + 16f).coerceAtMost(100f)
                updatedInfluence = (state.influence + 14f).coerceAtMost(100f)
                val eventMsg = "Year ${state.currentYear}, Week ${state.currentWeek}: Contract executed with ${mail.sender}: '${mail.subject}' ($${String.format("%,.0f", mail.cashOffer)})."
                updatedTimelineEvents = updatedTimelineEvents + eventMsg
                showToast("Executed contract with ${mail.sender}! Received $${String.format("%,.0f", mail.cashOffer)}.")
            } else if (mail.type == "FEATURE") {
                updatedCash += mail.cashOffer
                updatedInfluence = (state.influence + 8f).coerceAtMost(100f)
                val peer = state.npcs.find { it.name == mail.sender || it.id == mail.labelId }
                val isRealArtist = peer != null || listOf("Aria Sterling", "Zion Kidd", "Luna Sol", "Jax Teller", "Nico Black", "Jordan Cole").contains(mail.sender)
                if (!isRealArtist) {
                    updatedPublicImage = (state.publicImage + 8f).coerceAtMost(100f)
                    showToast("Accepted commercial engagement with ${mail.sender}! Earned $${String.format("%,.0f", mail.cashOffer)}.")
                } else {
                    val peerName = peer?.name ?: mail.sender
                    val collabGenre = peer?.genrePreference ?: "Pop"
                    val genreObj = GameData.GENRES.find { it.name.equals(collabGenre, ignoreCase = true) } ?: GameData.GENRES[0]
                    val guestRating = (((peer?.popularity ?: 40f) / 100f) * 1.0f + ((state.voiceSkill + state.writingSkill) / 200f) * 1.8f + 1.2f).coerceIn(2.2f, 4.6f)
                    val realSongNames = listOf(
                        "Midnight Echoes", "Velvet Skies", "Crossroads", "City Lights", "No Regrets",
                        "Fading Shadows", "Starlight Jam", "Heart & Soul", "Gold Dust", "Summer Breeze",
                        "Lost In Sound", "Neon Reverie", "Chasing Horizons", "After Midnight", "Static Waves"
                    )
                    val pickedSongName = realSongNames.random()
                    val collabSongTitle = "${peerName} - ${pickedSongName} (feat. ${state.artistName})"
                    val featSong = Song(
                        id = UUID.randomUUID().toString(),
                        title = collabSongTitle,
                        genreId = genreObj.id,
                        budget = mail.cashOffer,
                        producerId = null,
                        rating = guestRating,
                        weekCreated = state.currentWeek,
                        yearCreated = state.currentYear,
                        isReleased = true,
                        hype = (20f + (peer?.popularity ?: 35f) * 0.35f).coerceIn(15f, 60f),
                        coverArt = "🎨 Collaboration Master: '${collabSongTitle}'",
                        isCopyrightRegistered = true,
                        hasSampleClearance = true,
                        featuredArtistId = null,
                        featuredArtistName = state.artistName,
                        isPlayerFeatured = true,
                        primaryArtistName = peerName,
                        audienceFeedback = listOf(
                            "🔥 Guest Feature: Fans love ${state.artistName}'s guest verse on '${collabSongTitle}'!",
                            "📈 Featured track distributed across streaming platforms."
                        )
                    )
                    updatedSongs = updatedSongs + featSong
                    if (peer != null) {
                        updatedNPCs = updatedNPCs.map { n ->
                            if (n.id == peer.id) n.copy(relationshipWithPlayer = (n.relationshipWithPlayer + 15f).coerceAtMost(100f))
                            else n
                        }
                        showToast("Accepted feature with ${peer.name}! Earned $${String.format("%.2f", mail.cashOffer)}, track added to your catalog, and boosted relationship!")
                    } else {
                        showToast("Accepted feature collaboration! Earned $${String.format("%.2f", mail.cashOffer)} and track added to your catalog!")
                    }
                }
            } else if (mail.type == "LABEL_OFFER") {
                val labelIdToSet = mail.labelId ?: mail.sender.lowercase().replace(" ", "_")
                updatedSignedLabelId = labelIdToSet
                updatedSignedLabelCut = mail.labelCut
                updatedSignedLabelRole = mail.labelRole ?: "Recording Artist"
                updatedSignedLabelLevel = mail.labelLevel.coerceAtLeast(1)
                updatedSignedLabelStartWeek = state.currentWeek
                updatedSignedLabelStartYear = state.currentYear
                val durationWeeks = if (mail.contractDurationWeeks > 0) mail.contractDurationWeeks else 52
                var endW = state.currentWeek + durationWeeks
                var endY = state.currentYear
                while (endW > 52) {
                    endW -= 52
                    endY += 1
                }
                updatedSignedLabelEndWeek = endW
                updatedSignedLabelEndYear = endY
                updatedSignedLabelReqSingles = if (mail.reqSingles > 0) mail.reqSingles else 3
                updatedSignedLabelReqAlbumsLP = mail.reqAlbumsLP
                updatedSignedLabelReqAlbumsEP = mail.reqAlbumsEP
                updatedSignedLabelReqMeTubeVideos = if (mail.reqMeTubeVideos > 0) mail.reqMeTubeVideos else 2
                updatedSignedLabelDeliveredSingles = 0
                updatedSignedLabelDeliveredAlbumsLP = 0
                updatedSignedLabelDeliveredAlbumsEP = 0
                updatedSignedLabelDeliveredMeTubeVideos = 0
                updatedPopularity = (state.popularity + 30f).coerceAtMost(100f)
                updatedInfluence = (state.influence + 30f).coerceAtMost(100f)
                updatedCash += mail.cashOffer
                updatedRecoupableAdvanceBalance = state.recoupableAdvanceBalance + mail.cashOffer
                showToast("Signed with ${mail.sender}! Received $${String.format("%,.0f", mail.cashOffer)} upfront advance. Check Company tab for contract deliverables!")
            } else if (mail.type == "SHADY_BRAND") {
                updatedCash += mail.cashOffer
                updatedTrustworthiness = (state.trustworthiness - 15f).coerceAtLeast(0f)
                updatedPublicImage = (state.publicImage - 10f).coerceAtLeast(0f)
                showToast("Sponsored post published to your channels. Payment received.")
            } else if (mail.type == "GHOSTWRITER") {
                updatedCash += mail.cashOffer
                updatedIndustryRespect = (state.industryRespect - 20f).coerceAtLeast(10f)
                updatedTrustworthiness = (state.trustworthiness - 15f).coerceAtLeast(0f)
                showToast("Sold ghostwritten melody! Earned $${String.format("%.2f", mail.cashOffer)}, but lost Industry Respect and Trustworthiness.")
            } else if (mail.type == "CHARITY_GIG") {
                if (state.cash < 1000.0) {
                    showToast("Not enough cash ($1,000 required for travel)!")
                    return@updateState state
                }
                if (state.energy < 35) {
                    showToast("Not enough energy (35 required)!")
                    return@updateState state
                }
                updatedCash -= 1000.0
                updatedEnergy -= 35
                updatedPublicImage = (state.publicImage + 20f).coerceAtMost(100f)
                updatedIndustryRespect = (state.industryRespect + 15f).coerceAtMost(100f)
                updatedTrustworthiness = (state.trustworthiness + 10f).coerceAtMost(100f)
                showToast("Performed at the Charity benefit! Gained massive reputation boosts!")
            } else if (mail.type == "SMEAR_CAMPAIGN") {
                if (state.cash < 2500.0) {
                    showToast("Not enough cash ($2,500 required)!")
                    return@updateState state
                }
                updatedCash -= 2500.0
                // Sabotage rival
                val targetNpcName = mail.subject.substringAfter("Sabotage Rival ").substringBefore(" ($")
                val exposed = Random.nextFloat() < 0.35f

                updatedNPCs = state.npcs.map { n ->
                    if (n.name == targetNpcName) {
                        n.copy(popularity = (n.popularity - 15f).coerceAtLeast(10f))
                    } else n
                }

                if (exposed) {
                    updatedTrustworthiness = (state.trustworthiness - 30f).coerceAtLeast(0f)
                    updatedPublicImage = (state.publicImage - 25f).coerceAtLeast(0f)
                    updatedStress = (state.stress + 15f).coerceAtMost(100f)
                    showToast("SMEAR CAMPAIGN FAILURE: Your anonymous campaign was exposed! Trustworthiness and Public Image collapsed.")
                } else {
                    showToast("SMEAR CAMPAIGN SUCCESS: Sabotaged your rival anonymously! Their popularity dropped by -15%.")
                }
            } else if (mail.type == "REPAY_LOAN_PROMPT") {
                val amtToPay = state.loanAmount.coerceAtMost(state.savingsBalance)
                if (amtToPay <= 0.0) {
                    showToast("No active loan balance or insufficient bank funds!")
                    return@updateState state
                }
                val paidInterest = amtToPay.coerceAtMost(state.accumulatedLoanInterest)
                showToast("Loan manager successfully paid off $${String.format("%,.2f", amtToPay)} of your active loan from your Bank account!")
                return@updateState state.copy(
                mailbox = updatedMail,
                banking = state.banking.copy(savingsBalance = state.savingsBalance - amtToPay, loanAmount = state.loanAmount - amtToPay, accumulatedLoanInterest = state.accumulatedLoanInterest - paidInterest)
            )
            } else if (mail.type == "COMPANY_INVESTMENT") {
                val label = state.ownLabel
                if (label == null) {
                    showToast("No active music company found!")
                    return@updateState state
                }
                val equitySold = mail.labelCut
                val investmentAmount = mail.cashOffer
                val updatedLabel = label.copy(
                    companyCash = label.companyCash + investmentAmount,
                    equityHeldByPlayer = (label.equityHeldByPlayer - (equitySold * 100f)).coerceAtLeast(0f),
                    valuation = label.valuation + investmentAmount * 3.0
                )
                showToast("Accepted investment! Company received $${String.format("%,.0f", investmentAmount)} in cash!")
                return@updateState state.copy(
                ownLabel = updatedLabel,
                mailbox = updatedMail
            )
            } else if (mail.type == "METUBE_BRAND_DEAL") {
                if (state.activeBrandDealName != null) {
                    showToast("You already have an active brand deal with ${state.activeBrandDealName}! Complete it first.")
                    return@updateState state
                }
                showToast("Signed 4-week sponsorship contract with ${mail.sender}! Weekly payout: $${String.format("%,.2f", mail.cashOffer)}.")
                return@updateState state.copy(
                activeBrandDealName = mail.sender,
                brandDealWeeklyPayout = mail.cashOffer,
                brandDealWeeksRemaining = 4,
                brandDealPostedThisWeek = false,
                mailbox = updatedMail
            )
            } else if (mail.type == "VENTURE_BUYOUT_OFFER") {
                val venture = state.businessVentures.find { it.id == mail.ventureId }
                val buyoutAmt = mail.cashOffer
                updatedCash += buyoutAmt
                showToast("Acquisition Complete! Sold 100% of ${venture?.name ?: "venture"} for $${String.format("%,.0f", buyoutAmt)}!")
                return@updateState state.copy(
                    cash = updatedCash,
                    businessVentures = state.businessVentures.filter { it.id != mail.ventureId },
                    mailbox = updatedMail
                )
            } else if (mail.type == "VENTURE_EQUITY_OFFER") {
                val venture = state.businessVentures.find { it.id == mail.ventureId }
                val cashAmt = mail.cashOffer
                val eqPct = mail.ventureEquityPercent
                updatedCash += cashAmt

                val palette = listOf("#F59E0B", "#06B6D4", "#10B981", "#F43F5E", "#8B5CF6", "#EC4899", "#3B82F6")
                val updatedBiz = state.businessVentures.map { v ->
                    if (v.id == mail.ventureId) {
                        val chosenColor = palette[v.investors.size % palette.size]
                        val newShareholder = VentureInvestorShareholder(
                            name = mail.sender,
                            firm = mail.sender,
                            equityPercent = eqPct,
                            colorHex = chosenColor
                        )
                        val newInvestors = v.investors + newShareholder
                        val newTotalInvestorEquity = newInvestors.sumOf { it.equityPercent }
                        val newFounderEquity = (1.0 - newTotalInvestorEquity - (if (v.isListedOnWeShare) v.weShareListedEquityPercent else 0.0)).coerceIn(0.0, 1.0)
                        val lostControl = v.isPlayerInControl && (newFounderEquity < 0.50 || newInvestors.any { it.equityPercent > newFounderEquity })
                        val controlMsg = if (lostControl) " Controlling stake transferred to investors! 10 time slots returned to your schedule." else ""
                        showToast("Equity Deal Closed! Received $${String.format("%,.0f", cashAmt)} for ${(eqPct * 100).toInt()}% equity in ${v.name} (credited to Withdrawable Profits).$controlMsg")
                        v.copy(
                            investors = newInvestors,
                            investorEquityPercent = newTotalInvestorEquity,
                            investorName = mail.sender,
                            accumulatedProfit = v.accumulatedProfit + cashAmt,
                            pendingInvestorOffer = null
                        )
                    } else v
                }
                return@updateState state.copy(
                    cash = state.cash,
                    businessVentures = updatedBiz,
                    mailbox = updatedMail
                )
            }

            else if (mail.type == "SHADY_CONTRACT") {
                updatedCash += mail.cashOffer
                updatedSignedLabelId = mail.labelId
                updatedSignedLabelCut = mail.labelCut
                updatedRecoupableAdvanceBalance = state.recoupableAdvanceBalance + mail.cashOffer
                updatedIntegrity = (state.integrity - 40f).coerceAtLeast(0f)
                updatedHumility = (state.humility - 30f).coerceAtLeast(0f)
                updatedTimelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Signed an exclusive 360 recording and distribution agreement."
                showToast("Signed recording contract with Vanguard Sound Group. Advance deposited into your account.")
            } else if (mail.type == "SHADY_CRYPTO") {
                updatedCash += mail.cashOffer
                updatedTrustworthiness = (state.trustworthiness - 40f).coerceAtLeast(0f)
                updatedPublicImage = (state.publicImage - 30f).coerceAtLeast(0f)
                updatedSpiritualJournal = listOf("scam_coin_signed_week_${state.currentWeek}") + state.spiritualJournal.take(19)
                updatedTimelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Endorsed \$SCAM Coin for an up-front payment of \$80,000. Risking massive fan fallout."
                showToast("Endorsed \$SCAM Coin! Received \$80,000 upfront. Watch out for the backlash!")
            } else if (mail.type == "MENTORSHIP_OFFER") {
                if (state.cash < 25000.0) {
                    showToast("Not enough cash to sponsor your protégé (\$25,000 required)!")
                    return@updateState state
                }
                updatedCash -= 25000.0
                updatedGenerosity = (state.generosity + 15f).coerceAtMost(100f)
                updatedPublicImage = (state.publicImage + 15f).coerceAtMost(100f)
                updatedIndustryRespect = (state.industryRespect + 10f).coerceAtMost(100f)
                updatedSpiritualJournal = listOf("mentor_active_12") + state.spiritualJournal.filter { !it.startsWith("mentor_active_") }.take(19)
                updatedTimelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Sponsored and took a young upcoming artist under your wing as a protégé. Invested \$25,000."
                showToast("Accepted Mentorship! Sponsoring young protégé for \$25,000. Gained immense generosity & prestige!")
            }

            state.copy(
                mailbox = updatedMail,
                cash = updatedCash,
                signedLabelId = updatedSignedLabelId,
                signedLabelCut = updatedSignedLabelCut,
                signedLabelRole = updatedSignedLabelRole,
                signedLabelLevel = updatedSignedLabelLevel,
                signedLabelStartWeek = updatedSignedLabelStartWeek,
                signedLabelStartYear = updatedSignedLabelStartYear,
                signedLabelEndWeek = updatedSignedLabelEndWeek,
                signedLabelEndYear = updatedSignedLabelEndYear,
                signedLabelReqSingles = updatedSignedLabelReqSingles,
                signedLabelReqAlbumsLP = updatedSignedLabelReqAlbumsLP,
                signedLabelReqAlbumsEP = updatedSignedLabelReqAlbumsEP,
                signedLabelReqMeTubeVideos = updatedSignedLabelReqMeTubeVideos,
                signedLabelDeliveredSingles = updatedSignedLabelDeliveredSingles,
                signedLabelDeliveredAlbumsLP = updatedSignedLabelDeliveredAlbumsLP,
                signedLabelDeliveredAlbumsEP = updatedSignedLabelDeliveredAlbumsEP,
                signedLabelDeliveredMeTubeVideos = updatedSignedLabelDeliveredMeTubeVideos,
                popularity = updatedPopularity,
                influence = updatedInfluence,
                trustworthiness = updatedTrustworthiness,
                publicImage = updatedPublicImage,
                industryRespect = updatedIndustryRespect,
                stress = updatedStress,
                energy = updatedEnergy,
                npcs = updatedNPCs,
                integrity = updatedIntegrity,
                humility = updatedHumility,
                generosity = updatedGenerosity,
                timelineEvents = updatedTimelineEvents,
                recoupableAdvanceBalance = updatedRecoupableAdvanceBalance,
                songs = updatedSongs,
                spiritual = state.spiritual.copy(spiritualJournal = updatedSpiritualJournal)
            )
        }
    }

    // --- NEW LIVING-WORLD & CAREER ACTIONS ---

    // 1. Mental Health Care Actions
    fun takeVacation() {
        updateState { state ->
            if (state.cash < 5000.0) {
                showToast("Not enough cash ($5,000 required) for a vacation!")
                state
            } else {
                showToast("Spent a week on vacation in Hawaii! Relieved stress (-35%) and boosted creative inspiration (+30)!")
                state.copy(
                    cash = state.cash - 5000.0,
                    stress = (state.stress - 35f).coerceAtLeast(0f),
                    burnout = (state.burnout - 15f).coerceAtLeast(0f),
                    creativeInspiration = (state.creativeInspiration + 30f).coerceAtMost(100f),
                    happiness = (state.happiness + 25f).coerceAtMost(100f),
                    motivation = (state.motivation + 20f).coerceAtMost(100f),
                    stressTreatedThisWeek = true,
                    timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Took a refreshing luxury vacation in Hawaii to relieve stress and reset mind."
                )
            }
        }
    }

    fun seekTherapy() {
        updateState { state ->
            if (state.cash < 1000.0) {
                showToast("Not enough cash ($1,000 required) for professional therapy!")
                state
            } else {
                showToast("Attended therapy session! Reduced stress (-30%) and decompressing.")
                state.copy(
                    cash = state.cash - 1000.0,
                    stress = (state.stress - 30f).coerceAtLeast(0f),
                    burnout = (state.burnout - 15f).coerceAtLeast(0f),
                    happiness = (state.happiness + 15f).coerceAtMost(100f),
                    motivation = (state.motivation + 15f).coerceAtMost(100f),
                    stressTreatedThisWeek = true
                )
            }
        }
    }

    fun hobbiesAndRest() {
        updateState { state ->
            showToast("Spent time on personal hobbies and relaxing at home! Stress relieved.")
            state.copy(
                stress = (state.stress - 20f).coerceAtLeast(0f),
                burnout = (state.burnout - 10f).coerceAtLeast(0f),
                creativeInspiration = (state.creativeInspiration + 20f).coerceAtMost(100f),
                happiness = (state.happiness + 12f).coerceAtMost(100f),
                energy = (state.energy + 25).coerceAtMost(state.maxEnergy),
                stressTreatedThisWeek = true
            )
        }
    }

    fun executiveWellnessAndGolf() {
        updateState { state ->
            if (state.cash < 450.0) {
                showToast("Not enough cash ($450 required) for the executive wellness session!")
                state
            } else {
                showToast("Executive wellness session completed. Stress reduced (-25%)!")
                state.copy(
                    cash = state.cash - 450.0,
                    stress = (state.stress - 25f).coerceAtLeast(0f),
                    creativeInspiration = (state.creativeInspiration + 15f).coerceAtMost(100f),
                    happiness = (state.happiness + 15f).coerceAtMost(100f),
                    energy = (state.energy + 20).coerceAtMost(state.maxEnergy),
                    stressTreatedThisWeek = true,
                    publicImage = (state.publicImage + 2f).coerceAtMost(100f)
                )
            }
        }
    }

    fun conductVentureExecutiveInspection(ventureId: String) {
        updateState { state ->
            if (state.energy < 8) {
                showToast("Not enough energy (8% required) for an executive inspection!")
                return@updateState state
            }
            val v = state.businessVentures.find { it.id == ventureId }
            if (v == null) return@updateState state

            val updatedVentures = state.businessVentures.map {
                if (it.id == ventureId) {
                    val baseUpdated = it.copy(
                        customerLoyalty = (it.customerLoyalty + 0.05f).coerceAtMost(1.0f)
                    )
                    when (baseUpdated.type) {
                        "Taxi Fleet" -> baseUpdated.copyTaxi(taxiCondition = (baseUpdated.taxiCondition + 0.05f).coerceAtMost(1.0f))
                        "Hotel", "Hotel Chain" -> baseUpdated.copyHotel(
                            hotelCleanliness = (baseUpdated.hotelCleanliness + 0.06f).coerceAtMost(1.0f),
                            hotelReputation = (baseUpdated.hotelReputation + 0.05f).coerceAtMost(1.0f)
                        )
                        else -> baseUpdated
                    }
                } else it
            }
            showToast("🏢 Conducted executive inspection & strategy meeting at ${v.name}! Operational morale and standards boosted.")
            state.copy(
                energy = state.energy - 8,
                businessVentures = updatedVentures,
                businessSkill = (state.businessSkill + 0.8f).coerceAtMost(100f),
                businessReputation = (state.businessReputation + 1.2f).coerceAtMost(100f)
            )
        }
    }

    // 1b. Financial Education & Business Stewardship Actions
    fun readBusinessBook() {
        updateState { state ->
            if (state.energy < 10) {
                showToast("Not enough energy (10 required) to read!")
                state
            } else if (state.cash < 80.0) {
                showToast("Not enough cash ($80 required) for a financial book!")
                state
            } else {
                val skillGain = Random.nextFloat() * 1.8f + 1.2f
                val lessons = listOf(
                    "📖 Budgeting: Keeping basic living expenses below your active gig income is key to early-stage survival.",
                    "📖 Emergency Fund: Maintain at least 3-6 months of expenses in cash to absorb life emergencies without debt.",
                    "📖 Compounding: Reinvesting stock dividends and business cash-flow accelerates wealth exponentially.",
                    "📖 Diversification: Spreading capital across stocks, crypto, and business assets minimizes downside risk.",
                    "📖 Debt Management: Avoid high-interest liabilities. Clean your label debts before buying luxury assets."
                )
                showToast(lessons.random())
                state.copy(
                energy = state.energy - 10,
                cash = state.cash - 80.0,
                businessSkill = (state.businessSkill + skillGain).coerceAtMost(100f),
                discipline = (state.discipline + 1.2f).coerceAtMost(100f),
                reliability = (state.reliability + 0.6f).coerceAtMost(100f),
                stress = (state.stress + 1f).coerceAtMost(100f)
            )
            }
        }
    }

    fun hireBusinessConsultant() {
        updateState { state ->
            if (state.energy < 8) {
                showToast("Not enough energy (8 required) to consult!")
                state
            } else if (state.cash < 1200.0) {
                showToast("Not enough cash ($1,200 required) to hire a financial consultant!")
                state
            } else {
                val skillGain = Random.nextFloat() * 4.5f + 3.5f
                val consultAdvice = listOf(
                    "💼 Strategic Advice: 'During an Economic Recession, high-overhead businesses underperform. Transition focus to organic streaming.'",
                    "💼 Cash Flow Strategy: 'Asset appreciation from music catalog royalties beats short-term gig pay. Focus on song quality!'",
                    "💼 Debt Advice: 'Label manager expenses can trigger compounding debt penalties if unpaid. Settle debts immediately!'",
                    "💼 Brand Advisory: 'Luxury sponsors offer higher budgets but require pristine business reputations. Keep your integrity high!'",
                    "💼 Asset Strategy: 'Studio equipment and instrument upgrades boost your song ratings. Prioritize functional assets over flash cars.'"
                )
                showToast(consultAdvice.random())
                state.copy(
                energy = state.energy - 8,
                cash = state.cash - 1200.0,
                businessSkill = (state.businessSkill + skillGain).coerceAtMost(100f),
                businessReputation = (state.businessReputation + 2.5f).coerceAtMost(100f),
                discipline = (state.discipline + 0.8f).coerceAtMost(100f),
                reliability = (state.reliability + 1.5f).coerceAtMost(100f),
                integrity = (state.integrity + 0.5f).coerceAtMost(100f)
            )
            }
        }
    }

    fun payLabelDebt(amount: Double) {
        updateState { state ->
            val label = state.ownLabel ?: return@updateState state
            if (label.accruedDebts <= 0.0) {
                showToast("Your label has no accrued debt!")
                return@updateState state
            }
            val amountToPay = amount.coerceIn(0.01, label.accruedDebts)
            if (state.cash < amountToPay) {
                showToast("Insufficient cash to pay off debt!")
                return@updateState state
            }
            val newDebt = label.accruedDebts - amountToPay
            val updatedLabel = label.copy(accruedDebts = newDebt)

            val repGain = (amountToPay / 8000.0).toFloat().coerceIn(1.0f, 15f)
            val newRep = (state.businessReputation + repGain).coerceAtMost(100f)
            val newReliability = (state.reliability + repGain * 0.8f).coerceAtMost(100f)
            val newIntegrity = (state.integrity + repGain * 0.4f).coerceAtMost(100f)

            showToast("Paid off $${String.format("%,.2f", amountToPay)} of label debt! Business reputation restored.")
            state.copy(
                cash = state.cash - amountToPay,
                ownLabel = updatedLabel,
                businessReputation = newRep,
                reliability = newReliability,
                integrity = newIntegrity
            )
        }
    }

    // 2. Entertainment Empire Actions
    fun buyBusinessAsset(name: String, type: String, cost: Double, weeklyProfit: Double) {
        updateState { state ->
            if (state.cash < cost) {
                showToast("Not enough cash to buy this business ($${String.format("%,.0f", cost)} required)!")
                state
            } else {
                val assetId = "biz_" + UUID.randomUUID().toString().take(6)
                val newAsset = BusinessAsset(
                    id = assetId,
                    name = name,
                    type = type,
                    purchaseCost = cost,
                    weeklyProfit = weeklyProfit
                )
                showToast("Acquired $name ($type)! Generates $${String.format("%,.0f", weeklyProfit)} per week.")
                val stateWithBiz = state.copy(
                cash = state.cash - cost,
                ownedBusinesses = state.ownedBusinesses + newAsset,
                legacyPoints = state.legacyPoints + (cost / 100).toLong(),
                timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Acquired the $type business '$name'!"
            )
                addHistoryEntryToState(
                    state = stateWithBiz,
                    title = "🏢 Business Acquired: '$name'",
                    description = "Acquired a new enterprise in the $type category for $${String.format("%,.0f", cost)} cash. Projected weekly profit is $${String.format("%,.0f", weeklyProfit)}.",
                    memoryType = "BUSINESS",
                    strength = "IMPORTANT",
                    targetBusinessName = name,
                    valueAffected = cost.toFloat()
                )
            }
        }
    }

    // 3. NPC Relationships Actions
    fun produceForNPC(npcId: String) {
        updateState { state ->
            val npc = state.npcs.find { it.id == npcId }
            if (npc == null) return@updateState state

            if (state.energy < 35) {
                showToast("Not enough energy (35 required) to write/produce for others!")
                return@updateState state
            }

            // Check 1: Good terms (relationship)
            if (npc.relationshipWithPlayer < 40f) {
                showToast("${npc.name} declined. Your relationship is too poor to work together.")
                return@updateState state
            }

            // Check 2: Skill matching artist status (Player needs sufficient writingSkill/popularity vs NPC's popularity)
            val playerSkillScore = state.writingSkill + (state.popularity * 0.5f) + (state.industryRespect * 0.5f)
            val npcStatusScore = npc.popularity + (npc.influence * 0.5f)

            // Allow some leniency, but high status artists will reject low skill players
            if (playerSkillScore < npcStatusScore * 0.7f) {
                showToast("${npc.name} declined. Your production skills and industry status aren't high enough for them yet.")
                return@updateState state
            }

            // Check 3: Not recently produced a track
            val recentCollab = npc.memories.find { it.memoryType == "PRODUCED_TRACK" &&
                (state.currentYear == it.yearRecorded && (state.currentWeek - it.weekRecorded) < 24 ||
                 state.currentYear > it.yearRecorded && (52 - it.weekRecorded + state.currentWeek) < 24)
            }

            if (recentCollab != null) {
                showToast("${npc.name} declined. You just recently produced a track together. They want to spread out their releases.")
                return@updateState state
            }

            // Interconnected logic: payout scales with NPC wealth and your business skill!
            // Greedy NPCs with high businessIntel try to pay less, but wealthy NPCs can afford more.
            val greedFactor = if (npc.personality == "Hustler" || npc.personality == "Diva") 0.85 else 1.05
            val scaleMultiplier = 1.0 + (state.businessSkill / 100f) + (npc.wealth / 250000.0).coerceAtMost(1.0)
            val payout = 3500.0 * scaleMultiplier * greedFactor

            showToast("Produced a track for ${npc.name}! Earned $${String.format("%,.0f", payout)}.")

            // Create a memory of this collaboration
            val collabMemory = NPCMemory(
                id = java.util.UUID.randomUUID().toString(),
                description = "Produced a track together with ${state.artistName}",
                emotionalImpact = 15f,
                memoryType = "PRODUCED_TRACK",
                weekRecorded = state.currentWeek,
                yearRecorded = state.currentYear,
                isCoreMemory = false
            )

            // Update NPC relationship and state
            val updatedNPCs = state.npcs.map { n ->
                if (n.id == npcId) {
                    n.copy(
                        relationshipWithPlayer = (n.relationshipWithPlayer + 15f).coerceAtMost(100f),
                        popularity = (n.popularity + 2f).coerceAtMost(100f),
                        creativeRespect = (n.creativeRespect + 15f).coerceAtMost(100f),
                        professionalRespect = (n.professionalRespect + 10f).coerceAtMost(100f),
                        businessTrust = (n.businessTrust + 8f).coerceAtMost(100f),
                        reliability = (n.reliability + 8f).coerceAtMost(100f),
                        memories = n.memories + collabMemory
                    )
                } else n
            }

            state.copy(
                energy = state.energy - 35,
                cash = state.cash + payout,
                industryRespect = (state.industryRespect + 3f).coerceAtMost(100f),
                npcs = updatedNPCs,
                timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Worked as a producer for ${npc.name}, solidifying industry bonds."
            )
        }
    }

    fun strengthenFriendship(npcId: String) {
        updateState { state ->
            val npc = state.npcs.find { it.id == npcId }
            if (npc == null) return@updateState state

            if (state.energy < 12) {
                showToast("Not enough energy (12 required) to socialize with colleagues!")
                state
            } else {
                // Boost varies between 0.5% and 3.0% (representing 0.5f - 3.0f on the relationship scale)
                val relBoost = Random.nextFloat() * 2.5f + 0.5f
                var toastMsg = "Met up and socialized with ${npc.name}! Relationship improved by +${String.format("%.2f", relBoost)}%."
                var energyDrain = 12
                var finalRelBoost = relBoost

                // Relationship difficulty checks:
                val isCold = npc.relationshipWithPlayer < 45f
                val diceRoll = Random.nextFloat()

                if (isCold && (npc.personality == "Diva" || npc.personality == "Ambitious" || npc.personality == "Hustler")) {
                    if (diceRoll > 0.4f) {
                        finalRelBoost = relBoost * 0.3f
                        toastMsg = "${npc.name} (${npc.personality}) was preoccupied and barely spoke to you. Relationship +${String.format("%.2f", finalRelBoost)}%."
                    } else {
                        finalRelBoost = relBoost * 0.6f
                        toastMsg = "Slight progress! Had a brief chat with ${npc.name}, but they still seem distant. Relationship +${String.format("%.2f", finalRelBoost)}%."
                    }
                } else if (npc.personality == "Charismatic" || npc.personality == "Hustler") {
                    toastMsg = "Had a good networking session with ${npc.name}! Relationship +${String.format("%.2f", finalRelBoost)}%."
                } else if (npc.personality == "Diva" && state.publicImage < 45f) {
                    finalRelBoost = 0f
                    energyDrain = 4
                    toastMsg = "Met up with Diva ${npc.name}, but they acted cold and dismissed you because your Public Image is too low. Drained 4 energy."
                } else if (npc.personality == "Rebel") {
                    toastMsg = "Hung out with Rebel ${npc.name} at a subterranean indie club. Creative inspiration boosted (+10)! Relationship +${String.format("%.2f", finalRelBoost)}%."
                }

                showToast(toastMsg)
                val updatedNPCs = state.npcs.map { n ->
                    if (n.id == npcId) {
                        n.copy(
                            relationshipWithPlayer = (n.relationshipWithPlayer + finalRelBoost).coerceIn(0f, 100f),
                            personalTrust = (n.personalTrust + finalRelBoost * 0.8f).coerceIn(0f, 100f),
                            businessTrust = (n.businessTrust + finalRelBoost * 0.5f).coerceIn(0f, 100f),
                            reliability = (n.reliability + finalRelBoost * 0.6f).coerceIn(0f, 100f)
                        )
                    } else n
                }

                val newInspiration = if (npc.personality == "Rebel") {
                    (state.creativeInspiration + 10f).coerceAtMost(100f)
                } else state.creativeInspiration

                state.copy(
                energy = state.energy - energyDrain,
                creativeInspiration = newInspiration,
                npcs = updatedNPCs
            )
            }
        }
    }

    fun startRivalry(npcId: String) {
        updateState { state ->
            val npc = state.npcs.find { it.id == npcId }
            if (npc == null) return@updateState state

            var publicImageLoss = 10f
            var influenceGain = 12f
            var stressGain = 15f
            var toastMsg = "Publicly dissed ${npc.name} on social media! Stirred up controversy!"

            if (npc.personality == "Rebel" || npc.personality == "Ambitious") {
                influenceGain = 20f
                stressGain = 25f
                publicImageLoss = 15f
                toastMsg = "🔥 CLASH OF TITANS: Dissed ${npc.name}! They immediately hit back with an aggressive reply, igniting massive internet drama!"
            }

            showToast(toastMsg)
            val updatedNPCs = state.npcs.map { n ->
                if (n.id == npcId) {
                    n.copy(
                        relationshipWithPlayer = (n.relationshipWithPlayer - 35f).coerceAtLeast(0f),
                        stress = (n.stress + 15f).coerceAtMost(100f)
                    )
                } else n
            }
            state.copy(
                npcs = updatedNPCs,
                influence = (state.influence + influenceGain).coerceAtMost(100f),
                // Controversy fuels social engagement
                publicImage = (state.publicImage - publicImageLoss).coerceAtLeast(0f),
                // But hurts public image
                stress = (state.stress + stressGain).coerceAtMost(100f),
                timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Sparked a viral organic rivalry with ${npc.name} on social media."
            )
        }
    }

    fun setPhoneLocked(locked: Boolean) {
        updateState { state ->
            state.copy(isPhoneLocked = locked)
        }
    }

    fun updateWallpaper(wallpaperId: String) {
        updateState { state ->
            showToast("Wallpaper updated successfully!")
            state.copy(wallpaperId = wallpaperId)
        }
    }

    fun togglePrayerReminder(enabled: Boolean) {
        updateState { state ->
            state.copy(spiritual = state.spiritual.copy(spiritualPrayerSetting = enabled))
        }
    }

    fun toggleTitheReminder(enabled: Boolean) {
        updateState { state ->
            state.copy(spiritual = state.spiritual.copy(spiritualTitheSetting = enabled, spiritualTitheReminderActive = enabled))
        }
    }

    fun addNotification(message: String) {
        updateState { state ->
            state.copy(notificationLogs = listOf(message) + state.notificationLogs.take(19))
        }
    }

    fun clearNotifications() {
        updateState { state ->
            state.copy(notificationLogs = emptyList())
        }
    }

    fun buyEbook(bookId: String) {
        updateState { state ->
            val book = com.example.ui.EbookCatalog.findBook(bookId) ?: return@updateState state
            if (state.purchasedEbooks.contains(bookId)) {
                showToast("You already own '${book.title}'!")
                return@updateState state
            }
            if (state.cash < book.price) {
                showToast("Insufficient funds to acquire '${book.title}' ($${String.format("%,.0f", book.price)} needed)!")
                return@updateState state
            }
            showToast("📖 Purchased '${book.title}'! Added to your eBook Library.")
            state.copy(
                cash = state.cash - book.price,
                purchasedEbooks = state.purchasedEbooks + bookId
            )
        }
    }

    // --- MeTube Platform ---
    fun uploadMeTubeVideo(title: String, type: String, customThumbnail: String? = null, category: String = "Music & Art") {
        updateState { state ->
            if (state.energy < 15) {
                showToast("Not enough energy to direct and edit a video! Requires 15 energy.")
                return@updateState state
            }
            val cost = 150.0
            if (state.cash < cost) {
                showToast("Insufficient cash to pay video production crew ($150 required).")
                return@updateState state
            }

            // Generate some random initial engagement
            val initialViews = (state.fans * 0.12 + Random.nextInt(100, 1000)).toLong()
            val initialLikes = (initialViews * 0.08).toLong()

            val autoThumbnail = if (customThumbnail.isNullOrBlank()) {
                "📺 Auto-Generated MeTube Thumbnail: Beautiful promotional artwork slide themed for your $type video titled '$title' [$category]"
            } else {
                customThumbnail
            }

            val newVideo = MeTubeVideo(
                id = UUID.randomUUID().toString().take(6),
                title = title,
                type = type,
                views = initialViews,
                likes = initialLikes,
                comments = listOf(
                    "This is absolutely fire! 🔥",
                    "Keep up the great work! So inspiring.",
                    "The production quality is next level!",
                    "Is anyone else listening to this in Year ${state.currentYear}?",
                    "Underated artist! Deserves way more subscribers."
                ).shuffled().take(2),
                revenueEarned = if (state.meTubeSubscribers >= 1000L) initialViews * 0.0035 else 0.0,
                weekPublished = state.currentWeek,
                yearPublished = state.currentYear,
                thumbnailId = autoThumbnail
            )

            val addedRevenue = if (state.meTubeSubscribers >= 1000L) initialViews * 0.0035 else 0.0
            showToast("Successfully uploaded video in '${category}' category to MeTube!")

            val updatedSpiritualJournal = state.spiritualJournal.filter { !it.startsWith("latest_metube_category_") && !it.startsWith("latest_cast_category_") }.toMutableList()
            updatedSpiritualJournal.add("latest_metube_category_$category")
            updatedSpiritualJournal.add("latest_cast_category_$category")

            state.copy(
                energy = state.energy - (if (state.activePhone == "Xiaomax") 8 else 15),
                cash = state.cash - cost,
                meTubeVideos = state.meTubeVideos + newVideo,
                signedLabelDeliveredMeTubeVideos = if (state.signedLabelId != null) state.signedLabelDeliveredMeTubeVideos + 1 else state.signedLabelDeliveredMeTubeVideos,
                meTubeSubscribers = state.meTubeSubscribers + Random.nextInt(50, 300),
                meTubeRevenue = state.meTubeRevenue + addedRevenue,
                sentiment = state.sentiment.copy(trendingScore = (state.trendingScore + 5f).coerceAtMost(100f)),
                spiritual = state.spiritual.copy(spiritualJournal = updatedSpiritualJournal)
            )
        }
    }

    fun collectMeTubeRevenue() {
        updateState { state ->
            val revenue = state.meTubeRevenue
            if (revenue <= 0.0) {
                showToast("No MeTube ad revenue to collect currently.")
                return@updateState state
            }
            showToast("Collected $${String.format("%,.2f", revenue)} in MeTube ad revenue!")
            state.copy(
                cash = state.cash + revenue,
                meTubeRevenue = 0.0
            )
        }
    }

    // --- SeeChat Messaging Platform ---
    fun sendChatMessage(npcId: String, playerMessageText: String, isChatCurrentlyOpen: Boolean = false) {
        updateState { state ->
            val npc = state.npcs.find { n -> n.id == npcId }
                ?: state.capitalClubMembers.find { it.id == npcId }?.let { cc ->
                    NPCEntity(
                        id = cc.id,
                        name = cc.name,
                        popularity = 60f,
                        influence = 60f,
                        fans = 100000L,
                        relationshipWithPlayer = cc.relationshipScore.toFloat(),
                        personality = cc.title,
                        lastInteractionWeek = cc.lastInteractionWeek,
                        lastInteractionYear = cc.lastInteractionYear
                    )
                }
                ?: return@updateState state
            // Build player message
            val normalizedMessage = playerMessageText.trim()
            if (normalizedMessage.isEmpty()) {
                showToast("Write a message before sending.")
                return@updateState state
            }
            val contactEnergyCost = PartnerRelationshipSimulation.energyCostForFirstContactThisWeek(
                lastInteractionWeek = npc.lastInteractionWeek,
                lastInteractionYear = npc.lastInteractionYear,
                currentWeek = state.currentWeek,
                currentYear = state.currentYear
            )
            if (contactEnergyCost > state.energy) {
                showToast("You're too exhausted to start another conversation this week. Rest first.")
                return@updateState state
            }
            val playerMsg = ChatMessage(
                id = UUID.randomUUID().toString().take(6),
                senderId = "PLAYER",
                senderName = state.artistName,
                message = normalizedMessage,
                timestamp = System.currentTimeMillis()
            )

            val lowerText = normalizedMessage.lowercase()
            var addedCash = 0.0
            var relationChange = 2f
            var updatedNPCs = state.npcs
            var updatedOwnedBusinesses = state.ownedBusinesses
            var newEndorsedCandidateId = state.endorsedCandidateId
            var newPoliticalTaxModifier = state.politicalTaxModifier
            var newCityProjectWeeksRemaining = state.cityProjectWeeksRemaining
            var newTaxRate = state.taxRate
            var chargeCash = 0.0
            var energyDeduction = contactEnergyCost

            var currentHappiness = state.happiness
            var currentHumility = state.humility
            var currentIntegrity = state.integrity
            var currentPublicImage = state.publicImage
            var currentTrustworthiness = state.trustworthiness
            var currentStatus = state.activeFamilyReqStatus

            val replyText = if (npcId == state.activeFamilyReqNpcId && state.activeFamilyReqStatus == "PENDING") {
                val reqAmount = state.activeFamilyReqAmount
                val reqType = state.activeFamilyReqType ?: "EMERGENCY"
                val reqDetail = state.activeFamilyReqDetail ?: "assistance"

                if (lowerText.contains("yes") || lowerText.contains("help") || lowerText.contains("accept") || lowerText.contains("invest") || lowerText.contains("donate")) {
                    if (state.cash < reqAmount) {
                        "Oh, thank you so much for wanting to support me! 💖 But it looks like you don't quite have enough cash on hand right now ($${String.format("%,.0f", reqAmount)} required). Let me know if you can help later!"
                    } else {
                        chargeCash = reqAmount
                        relationChange = if (npcId in listOf("npc_family_mom", "npc_family_cousin_sarah", "npc_family_uncle_joe")) 30f else 20f
                        currentStatus = "ACCEPTED"

                        when (reqType) {
                            "EMERGENCY" -> {
                                currentHappiness = (currentHappiness + 10f).coerceIn(0f, 100f)
                                currentPublicImage = (currentPublicImage + 8f).coerceIn(0f, 100f)
                                currentTrustworthiness = (currentTrustworthiness + 10f).coerceIn(0f, 100f)
                                "Oh my goodness! You are a literal lifesaver. 😭 I can't thank you enough for helping me with this emergency. You have such a generous heart!"
                            }
                            "BUSINESS" -> {
                                val rand = kotlin.random.Random.nextFloat()
                                when {
                                    rand < 0.40f -> {
                                        // Scam / Total loss
                                        val security = state.hiredEmployees.find { it.role == "Security" && it.loyalty > 25f }
                                        val lawyer = state.hiredEmployees.find { it.role == "Lawyer" && it.loyalty > 25f }
                                        if (security != null && lawyer != null) {
                                            addedCash = reqAmount * 1.15
                                            "Uh oh! It turns out there were some serious contractual disputes and shady operators involved in the venture... 🎰 BUT your elite Security officer ${security.name} and your Lawyer ${lawyer.name} stepped in immediately! They recovered your full investment plus 15% interest ($${String.format("%,.0f", reqAmount * 1.15)} total). Whew!"
                                        } else {
                                            relationChange = -20f
                                            "The venture hit some really shady complications, and the funds are completely lost! 📱 Without a legal and security team, there's no way to recover it. They say: 'I'm so incredibly sorry, everything went sideways...'"
                                        }
                                    }
                                    else -> {
                                        // Success: Add dynamic business asset!
                                        val weeklyProf = ((reqAmount * 0.03) / 10).toInt() * 10.0 + 50.0 // 3% weekly profit + $50
                                        val newBiz = BusinessAsset(
                                            id = "biz_dyn_" + java.util.UUID.randomUUID().toString().take(6),
                                            name = "${npc.name}'s ${reqDetail.substringBefore("🍋").substringBefore("🌮").substringBefore("🎚").substringBefore("🥐").trim()}",
                                            type = "Fashion Brand",
                                            purchaseCost = reqAmount,
                                            weeklyProfit = weeklyProf
                                        )
                                        updatedOwnedBusinesses = state.ownedBusinesses + newBiz
                                        "The venture is a massive success! 🚀 Everything is set up beautifully, and you've been registered as a primary equity partner. You will now receive $${String.format("%,.0f", weeklyProf)} in weekly business profits!"
                                    }
                                }
                            }
                            "TUITION" -> {
                                currentIntegrity = (currentIntegrity + 8f).coerceIn(0f, 100f)
                                currentHappiness = (currentHappiness + 5f).coerceIn(0f, 100f)
                                "Thank you so much! 🎓 I can finally focus on my education without the constant stress of being dropped. I won't let you down!"
                            }
                            "CELEBRATION" -> {
                                currentHappiness = (currentHappiness + 15f).coerceIn(0f, 100f)
                                currentHumility = (currentHumility + 10f).coerceIn(0f, 100f)
                                "This is amazing! 🎉 Thank you for supporting this beautiful family moment. Everyone is going to be so happy!"
                            }
                            else -> {
                                "Thank you so much for your kind support! It means the world to me."
                            }
                        }
                    }
                } else if (lowerText.contains("no") || lowerText.contains("reject") || lowerText.contains("decline")) {
                    relationChange = -10f
                    currentStatus = "DECLINED"
                    if (reqType == "EMERGENCY") {
                        currentHappiness = (currentHappiness - 6f).coerceIn(0f, 100f) // guilt
                    }
                    "I understand sweetheart. Family comes first no matter what, and we'll figure things out together. Take good care of yourself!"
                } else {
                    "Type 'yes' or 'help' if you want to support me with $${String.format("%,.0f", reqAmount)}, or 'no' to decline."
                }
            } else {
                val clubMember = state.capitalClubMembers.find { it.id == npcId }
                val isDatingOrSpouse = clubMember != null && (clubMember.isDating || clubMember.isSpouse)
                val isFamilyMember = npcId.startsWith("npc_family_") || npcId in listOf("npc_family_mom", "npc_family_dad", "npc_family_uncle_joe", "npc_family_cousin_sarah")

                when {
                    npcId == "npc_gov_sterling" -> {
                    val isGovernorshipActive = state.currentWeek in 12..14
                    if (isGovernorshipActive) {
                        if (lowerText.contains("yes") || lowerText.contains("endorse") || lowerText.contains("accept") || lowerText.contains("support")) {
                            if (state.popularity < 45f || state.integrity < 45f) {
                                "On second thought, my campaign managers say your current reputation or popularity is a bit too controversial. We'll have to pass on your endorsement. Thank you!"
                            } else {
                                addedCash = 40000.0
                                newEndorsedCandidateId = "npc_gov_sterling"
                                relationChange = 25f
                                "Fantastic! Your endorsement has been published across all local networks. My campaign was just credited with a massive surge in youth voters! Here is your $40,000 support payment. Let's win this election together! 🗳"
                            }
                        } else if (lowerText.contains("no") || lowerText.contains("reject") || lowerText.contains("decline")) {
                            relationChange = -15f
                            "A shame. We'll find support elsewhere. Don't expect any regulatory favors if we win."
                        } else {
                            "Hello! I am running for re-election as Governor. Since you are an influential artist with strong status, I would love to get your official endorsement. I will pay you $40,000 for a campaign shoutout! Type 'endorse' or 'yes' to accept, or 'no' to decline."
                        }
                    } else {
                        if (state.politicalTaxModifier < 0.0f) {
                            if (lowerText.contains("yes") || lowerText.contains("support") || lowerText.contains("accept") || lowerText.contains("help")) {
                                if (state.cash < 5000.0 || state.energy < 15) {
                                    "It seems you are too tired or low on funds to support our city project right now. Get some rest and funds!"
                                } else {
                                    chargeCash = 5000.0
                                    energyDeduction = 15
                                    newCityProjectWeeksRemaining = 4
                                    relationChange = 20f
                                    "Thank you! Your active support of our City Renewal Project has been announced. Your fans love seeing you give back! (-7% energy and some dollars active for next 4 weeks). 🌟"
                                }
                            } else {
                                "Hello! Remember, my administration reduced your tax rate to help you thrive. Please help us support the city renewal project! Type 'support' or 'yes' to help out."
                            }
                        } else {
                            "My administration is currently busy reviewing the tax and compliance audits of high-earning artists like you. Please make sure your taxes are fully settled."
                        }
                    }
                }
                npcId == "npc_sen_vance" -> {
                    val isPresidentialActive = state.currentWeek in 36..39
                    if (isPresidentialActive) {
                        if (lowerText.contains("yes") || lowerText.contains("endorse") || lowerText.contains("accept") || lowerText.contains("support")) {
                            if (state.popularity < 60f || state.followers < 5000) {
                                "Our national polling staff indicates that your follower count or popularity is not yet high enough to move the needle. Let's touch base later!"
                            } else {
                                addedCash = 80000.0
                                newEndorsedCandidateId = "npc_sen_vance"
                                relationChange = 30f
                                "Superb! Your endorsement went viral nationwide! Our campaign headquarters is buzzing. Here is your $80,000 national campaign support payment. Victory is within reach! 🗳"
                            }
                        } else if (lowerText.contains("no") || lowerText.contains("reject") || lowerText.contains("decline")) {
                            relationChange = -20f
                            "Very well. We'll remember who stood with us and who remained silent."
                        } else {
                            "Hello! I am running for President of the United States. With your massive following, your support would guarantee our victory. I will pay you $80,000 for your endorsement. Type 'endorse' or 'yes' to accept, or 'no' to decline."
                        }
                    } else {
                        if (state.politicalTaxModifier < 0.0f) {
                            if (lowerText.contains("yes") || lowerText.contains("support") || lowerText.contains("accept") || lowerText.contains("help")) {
                                if (state.cash < 8000.0 || state.energy < 15) {
                                    "We need your full strength. Please get more energy and cash first!"
                                } else {
                                    chargeCash = 8000.0
                                    energyDeduction = 15
                                    newCityProjectWeeksRemaining = 4
                                    relationChange = 25f
                                    "Wonderful! The national arts platform is thriving under our joint project. Your popularity has been boosted! (-7% energy for next 4 weeks). 🌟"
                                }
                            } else {
                                "Hello! As President, we've lowered your tax rate. We'd love your backing on our new nationwide youth initiative. Type 'support' or 'yes' to participate!"
                            }
                        } else {
                            "Our federal regulatory committee is monitoring the financial declarations of top entertainers. Please ensure complete compliance with the 20% federal tax rate."
                        }
                    }
                }
                isDatingOrSpouse -> {
                    val partnerName = clubMember?.name ?: npc.name
                    val isSpouse = clubMember?.isSpouse == true
                    when {
                        lowerText.contains("love") || lowerText.contains("adore") || lowerText.contains("miss you") ||
                        lowerText.contains("babe") || lowerText.contains("honey") || lowerText.contains("darling") ||
                        lowerText.contains("sweetheart") || lowerText.contains("beautiful") || lowerText.contains("handsome") -> {
                            relationChange = 8f
                            currentHappiness = (currentHappiness + 5f).coerceIn(0f, 100f)
                            if (isSpouse) {
                                "I love you so much more every day. Having you as my partner in life is my greatest joy. Forever with you! ❤️💍"
                            } else {
                                "Aww, you always make my heart flutter! I've been thinking about you all morning. Can't wait to see you soon! 💕✨"
                            }
                        }
                        lowerText.contains("date") || lowerText.contains("dinner") || lowerText.contains("lunch") ||
                        lowerText.contains("meet") || lowerText.contains("see you") || lowerText.contains("hang out") ||
                        lowerText.contains("vacation") || lowerText.contains("trip") || lowerText.contains("weekend") -> {
                            relationChange = 6f
                            currentHappiness = (currentHappiness + 4f).coerceIn(0f, 100f)
                            "I'd love that! Let's reserve a quiet table tonight just the two of us, away from all the public spotlight and cameras. You deserve an evening of pure relaxation with me. 🥂🕯️"
                        }
                        lowerText.contains("marry") || lowerText.contains("wedding") || lowerText.contains("future") ||
                        lowerText.contains("move in") || lowerText.contains("together") || lowerText.contains("home") -> {
                            relationChange = 7f
                            if (isSpouse) {
                                "Building our sanctuary and life together has been the sweetest part of my journey. I cherish every single day with you! 🏡❤️"
                            } else {
                                "Thinking about our future together fills me with so much peace and happiness. Taking this journey with you feels right. 💫"
                            }
                        }
                        lowerText.contains("tired") || lowerText.contains("stress") || lowerText.contains("exhausted") ||
                        lowerText.contains("how are you") || lowerText.contains("how was your day") -> {
                            relationChange = 5f
                            currentHappiness = (currentHappiness + 4f).coerceIn(0f, 100f)
                            "I was just about to check in on you! Please remember to take a breath and rest, honey. You give so much to the world, but your peace and health come first. I'm always right here for you. ☕🌸"
                        }
                        lowerText.contains("loan") || lowerText.contains("borrow") || lowerText.contains("send me money") || lowerText.contains("need cash") -> {
                            if (npc.relationshipWithPlayer >= 75f) {
                                addedCash = 3000.0
                                relationChange = 2f
                                "Of course sweetheart! If you need support right now, I transferred $3,000 from my personal savings. We're partners in this life, always. 💖"
                            } else {
                                "Sweetheart, let's keep our personal finances independent for now so money never creates pressure between us. I believe in you! 🤍"
                            }
                        }
                        lowerText.contains("trash") || lowerText.contains("loser") || lowerText.contains("sucks") ||
                        lowerText.contains("hate") || lowerText.contains("ugly") || lowerText.contains("shut up") -> {
                            relationChange = -20f
                            currentHappiness = (currentHappiness - 8f).coerceIn(0f, 100f)
                            "That really hurt... Why are you speaking to me like that? If you're stressed or angry, talk to me, don't take it out on our relationship. 💔"
                        }
                        else -> {
                            relationChange = 3f
                            val romanticPicks = listOf(
                                "Hey my love! Just took a break from work and smiled thinking about you. How has your day been so far?",
                                "Good to hear from you sweetheart! I was just admiring our recent photos together. Let's make time for a quiet evening soon.",
                                "Thinking of you today! No matter how hectic schedules get, you're always the highlight of my week. 💕",
                                "Hey darling! Make sure you stay hydrated and don't skip meals today. You mean the world to me. ✨"
                            )
                            romanticPicks.random()
                        }
                    }
                }
                isFamilyMember -> {
                    when {
                        lowerText.contains("love") || lowerText.contains("miss you") || lowerText.contains("thinking of you") ||
                        lowerText.contains("mom") || lowerText.contains("dad") || lowerText.contains("family") -> {
                            relationChange = 8f
                            currentHappiness = (currentHappiness + 6f).coerceIn(0f, 100f)
                            "Aww, we love you so much! No matter how big you get or how much success you achieve, you will always be our pride and joy. Family is forever! ❤️"
                        }
                        lowerText.contains("visit") || lowerText.contains("come over") || lowerText.contains("dinner") ||
                        lowerText.contains("food") || lowerText.contains("cook") || lowerText.contains("home") -> {
                            relationChange = 6f
                            "Please come over whenever you can! We'll make your favorite home-cooked meal and sit around the kitchen table just like old times. Take a break and visit home soon! 🍲🏡"
                        }
                        lowerText.contains("how are you") || lowerText.contains("doing well") || lowerText.contains("health") -> {
                            relationChange = 5f
                            "We are doing wonderfully! Just praying for your peace of mind, good health, and wisdom every morning. How is your heart doing these days?"
                        }
                        lowerText.contains("loan") || lowerText.contains("borrow") || lowerText.contains("money") -> {
                            "Oh sweetheart, our savings are modest, but our home will always have a warm bed and hot meal waiting for you whenever you need. Take care of yourself!"
                        }
                        lowerText.contains("trash") || lowerText.contains("hate") || lowerText.contains("shut up") -> {
                            relationChange = -15f
                            "That really hurts our feelings. We raised you with love and respect. Please remember who was there for you before everything else."
                        }
                        else -> {
                            relationChange = 2f
                            val familyPicks = listOf(
                                "Hello sweetheart! Just checking in to see how you're feeling. Don't work yourself too hard, okay?",
                                "Hi dear! Was looking at some old family photos this morning. Time flies so fast, but we're so proud of the kind person you are.",
                                "Thinking of you today! Remember to eat healthy, get some good rest, and keep your feet on the ground. We love you!"
                            )
                            familyPicks.random()
                        }
                    }
                }
                else -> {
                    when {
                        lowerText.contains("time is money") || lowerText.contains("professional gig") || lowerText.contains("schedule a professional gig") -> {
                            if (npc.relationshipWithPlayer >= 50f) {
                                relationChange = 4f
                                "I'd love to schedule a live show or collaborative gig with you! Let's have our management teams coordinate calendar dates and venue bookings. 🎤"
                            } else {
                                "My tour and studio schedule is completely booked right now. Send a formal proposal through my management agency if interested."
                            }
                        }
                        lowerText.contains("loan") || lowerText.contains("borrow") || lowerText.contains("lend") ||
                        lowerText.contains("send me money") || lowerText.contains("give me money") || lowerText.contains("can you send cash") ||
                        (lowerText.contains("money") && (lowerText.contains("need") || lowerText.contains("send") || lowerText.contains("give"))) -> {
                            val conv = state.chatConversations.find { it.npcId == npcId }
                            val recentLoanRequests = conv?.messages?.count {
                                it.senderId == "PLAYER" && (it.message.lowercase().contains("money") || it.message.lowercase().contains("loan") || it.message.lowercase().contains("borrow"))
                            } ?: 0

                            when {
                                recentLoanRequests >= 2 -> {
                                    relationChange = -20f
                                    "Stop asking me for money! I'm not an ATM. If you ask for handouts again, I will block your number."
                                }
                                npc.relationshipWithPlayer >= 75f -> {
                                    val amount = listOf(1000.0, 2500.0, 5000.0).random()
                                    addedCash = amount
                                    relationChange = -5f
                                    "Since we've got a great friendship, I can help out with $${String.format("%,.0f", amount)} this time. Make sure you manage your cash flow wisely! 🤝"
                                }
                                npc.relationshipWithPlayer >= 40f -> {
                                    relationChange = -3f
                                    "I'd love to help, but things are a bit tight right now with studio and production fees. Maybe next time!"
                                }
                                else -> {
                                    relationChange = -15f
                                    "Are you kidding me? Ask me for money? Go ask a bank. Don't message me again."
                                }
                            }
                        }
                        lowerText.contains("trash") || lowerText.contains("loser") || lowerText.contains("sucks") ||
                        lowerText.contains("hate") || lowerText.contains("mid") || lowerText.contains("garbage") -> {
                            relationChange = -25f
                            "Wow, okay. Keep talking trash on social media while your streams decline. Standard."
                        }
                        lowerText.contains("collab") || lowerText.contains("feature") || lowerText.contains("track") || lowerText.contains("song") || lowerText.contains("album") -> {
                            when {
                                state.songs.isEmpty() || state.popularity < 12f -> {
                                    if (npc.popularity > 50f) {
                                        relationChange = 1f
                                        "I only collab with artists who have a proven streaming catalog or buzzing singles. Keep dropping independent music and get your numbers up first!"
                                    } else {
                                        relationChange = 3f
                                        "I'm an up-and-comer too! Let's both keep grinding in the studio. Once we have some tracks charting, let's link up for real."
                                    }
                                }
                                npc.relationshipWithPlayer >= 60f -> {
                                    relationChange = 6f
                                    "I love your acoustic style! Let's definitely cook up a feature together. Head to the Studio or record label portal and send the demo!"
                                }
                                npc.relationshipWithPlayer >= 35f -> {
                                    relationChange = 3f
                                    "Your recent tracks sound clean! Have your management or A&R email my team with the stems and concept."
                                }
                                else -> {
                                    relationChange = -5f
                                    "We don't really have that kind of creative rapport. Focus on your own releases."
                                }
                            }
                        }
                        lowerText.contains("love") || lowerText.contains("goat") || lowerText.contains("awesome") ||
                        lowerText.contains("legend") || lowerText.contains("great") || lowerText.contains("best") -> {
                            relationChange = 8f
                            "Aww, thank you! That means the world coming from you. Let's keep supporting each other! 💕"
                        }
                        else -> {
                            when (npc.personality) {
                                "Charismatic" -> "Hey! Always awesome hearing from you. Let's keep making waves! 🌊"
                                "Ambitious" -> "What's the play? I'm focusing on dominating the charts. Let's talk business!"
                                "Rebel" -> "Sup. I'm just cooking some experimental beats. Don't let the label suit guys box you in."
                                "Hustler" -> "Time is money, friend! If you have a gig referral, hit me up. Otherwise, stay grinding."
                                "Diva" -> "My schedule is packed, but for you, I have a minute. What do you need? ✨"
                                else -> "Appreciate the message! Let's touch base soon."
                            }
                        }
                    }
                }
            }
            }

            // Relationship-based response timing:
            // Family, politicians on active deals, or high affinity (>= 65) -> Immediate!
            // Medium affinity (40..64.99) -> 45% immediate, 55% delayed (1 week later)
            // Low affinity (< 40) -> 60% ghosted/never replies, 40% delayed (2-3 weeks later)
            val isHighAffinity = npc.relationshipWithPlayer >= 65f || npcId.startsWith("npc_family_") || npcId in listOf("npc_gov_sterling", "npc_sen_vance")
            val isMediumAffinity = npc.relationshipWithPlayer in 40f..64.99f

            val responseMode = when {
                isHighAffinity -> "IMMEDIATE"
                isMediumAffinity -> if (kotlin.random.Random.nextFloat() < 0.45f) "IMMEDIATE" else "DELAYED_1W"
                else -> if (kotlin.random.Random.nextFloat() < 0.60f) "NEVER" else "DELAYED_WEEKS"
            }

            // Repeated messages in the same week cannot farm the same positive relationship gain.
            relationChange = PartnerRelationshipSimulation.relationshipDeltaForWeek(
                proposedDelta = relationChange,
                lastInteractionWeek = npc.lastInteractionWeek,
                lastInteractionYear = npc.lastInteractionYear,
                currentWeek = state.currentWeek,
                currentYear = state.currentYear
            )

            val conversations = state.chatConversations.toMutableList()
            val existingIndex = conversations.indexOfFirst { it.npcId == npcId }
            val updatedPendingReplies = state.pendingChatReplies.toMutableList()
            var immediateCashDelta = 0.0
            var immediateRelationDelta = 0f

            if (responseMode == "IMMEDIATE") {
                immediateCashDelta = addedCash
                immediateRelationDelta = relationChange
                val npcMsg = ChatMessage(
                    id = UUID.randomUUID().toString().take(6),
                    senderId = npcId,
                    senderName = npc.name,
                    message = replyText,
                    timestamp = System.currentTimeMillis() + 100
                )
                if (existingIndex != -1) {
                    val conv = conversations.removeAt(existingIndex)
                    conversations.add(0, conv.copy(
                        messages = conv.messages + playerMsg + npcMsg,
                        unreadCount = if (isChatCurrentlyOpen) 0 else conv.unreadCount + 1
                    ))
                } else {
                    conversations.add(0,
                        ChatConversation(
                            npcId = npcId,
                            npcName = npc.name,
                            messages = listOf(playerMsg, npcMsg),
                            unreadCount = if (isChatCurrentlyOpen) 0 else 1
                        )
                    )
                }
            } else if (responseMode == "DELAYED_1W") {
                if (existingIndex != -1) {
                    val conv = conversations.removeAt(existingIndex)
                    conversations.add(0, conv.copy(
                        messages = conv.messages + playerMsg,
                        unreadCount = 0
                    ))
                } else {
                    conversations.add(0,
                        ChatConversation(
                            npcId = npcId,
                            npcName = npc.name,
                            messages = listOf(playerMsg),
                            unreadCount = 0
                        )
                    )
                }
                val dueWeek = if (state.currentWeek >= 52) 1 else state.currentWeek + 1
                val dueYear = if (state.currentWeek >= 52) state.currentYear + 1 else state.currentYear
                updatedPendingReplies.add(
                    PendingChatReply(
                        id = UUID.randomUUID().toString().take(6),
                        npcId = npcId,
                        replyText = replyText,
                        scheduledWeek = dueWeek,
                        scheduledYear = dueYear,
                        relationChange = relationChange,
                        addedCash = addedCash
                    )
                )
                showToast("Message sent to ${npc.name}. They will reply when available.")
            } else if (responseMode == "DELAYED_WEEKS") {
                if (existingIndex != -1) {
                    val conv = conversations.removeAt(existingIndex)
                    conversations.add(0, conv.copy(
                        messages = conv.messages + playerMsg,
                        unreadCount = 0
                    ))
                } else {
                    conversations.add(0,
                        ChatConversation(
                            npcId = npcId,
                            npcName = npc.name,
                            messages = listOf(playerMsg),
                            unreadCount = 0
                        )
                    )
                }
                val delayWeeks = kotlin.random.Random.nextInt(2, 4)
                val targetW = state.currentWeek + delayWeeks
                val dueWeek = if (targetW > 52) targetW - 52 else targetW
                val dueYear = if (targetW > 52) state.currentYear + 1 else state.currentYear
                val delayedColdReply = listOf(
                    "Just seeing this. What's up?",
                    "Busy with my project right now. Catch you later.",
                    "Took a while to see this. Hope you're good.",
                    "Hey. A lot going on, can't talk much right now."
                ).random()
                updatedPendingReplies.add(
                    PendingChatReply(
                        id = UUID.randomUUID().toString().take(6),
                        npcId = npcId,
                        replyText = delayedColdReply,
                        scheduledWeek = dueWeek,
                        scheduledYear = dueYear,
                        relationChange = 0.5f,
                        addedCash = addedCash
                    )
                )
                showToast("Message sent to ${npc.name}.")
            } else {
                // NEVER (Left on read / ghosted)
                if (existingIndex != -1) {
                    val conv = conversations.removeAt(existingIndex)
                    conversations.add(0, conv.copy(
                        messages = conv.messages + playerMsg,
                        unreadCount = 0
                    ))
                } else {
                    conversations.add(0,
                        ChatConversation(
                            npcId = npcId,
                            npcName = npc.name,
                            messages = listOf(playerMsg),
                            unreadCount = 0
                        )
                    )
                }
                showToast("Delivered to ${npc.name}.")
            }

            // Apply relationship delta
            val finalNPCs = updatedNPCs.map { n ->
                if (n.id == npcId) {
                    n.copy(
                        relationshipWithPlayer = (n.relationshipWithPlayer + immediateRelationDelta).coerceIn(0f, 100f),
                        lastInteractionWeek = state.currentWeek,
                        lastInteractionYear = state.currentYear
                    )
                } else n
            }

            if (immediateCashDelta > 0) {
                showToast("Received $${String.format("%,.0f", immediateCashDelta)} cash transfer from ${npc.name}!")
            }
            if (chargeCash > 0) {
                showToast("Transferred $${String.format("%,.0f", chargeCash)} to ${npc.name}.")
            }

            state.copy(
                cash = state.cash + immediateCashDelta - chargeCash,
                energy = (state.energy - energyDeduction).coerceAtLeast(0),
                chatConversations = conversations,
                pendingChatReplies = updatedPendingReplies,
                npcs = finalNPCs,
                ownedBusinesses = updatedOwnedBusinesses,
                endorsedCandidateId = newEndorsedCandidateId,
                politicalTaxModifier = newPoliticalTaxModifier,
                cityProjectWeeksRemaining = newCityProjectWeeksRemaining,
                happiness = currentHappiness,
                humility = currentHumility,
                integrity = currentIntegrity,
                publicImage = currentPublicImage,
                trustworthiness = currentTrustworthiness,
                activeFamilyReqStatus = currentStatus,
                capitalClubMembers = state.capitalClubMembers.map { member ->
                    if (member.id == npcId) {
                        val relationshipDelta = PartnerRelationshipSimulation.relationshipDeltaForWeek(
                            proposedDelta = immediateRelationDelta,
                            lastInteractionWeek = member.lastInteractionWeek,
                            lastInteractionYear = member.lastInteractionYear,
                            currentWeek = state.currentWeek,
                            currentYear = state.currentYear
                        )
                        member.copy(
                            relationshipScore = (member.relationshipScore + relationshipDelta.toInt()).coerceIn(0, 100),
                            lastInteractionWeek = state.currentWeek,
                            lastInteractionYear = state.currentYear
                        )
                    } else member
                },
                banking = state.banking.copy(taxRate = newTaxRate)
            )
        }
    }

    fun markChatRead(npcId: String) {
        updateState { state ->
            val conversations = state.chatConversations.map { conv ->
                if (conv.npcId == npcId) conv.copy(unreadCount = 0) else conv
            }
            state.copy(chatConversations = conversations)
        }
    }

    // --- Cast Social Platform ---
    fun createCastPost(content: String, category: String = "Music & Art", imageRes: String? = null) {
        updateState { state ->
            val energyCost = if (state.activePhone == "Xiaomax") 1 else 3
            if (state.energy < energyCost) {
                showToast("Not enough energy to draft a social media post! Requires $energyCost energy.")
                return@updateState state
            }

            val criticismTerms = listOf("lied", "steal", "fraud", "fake", "exposed", "hate", "diss", "scandal")
            val isControversial = criticismTerms.any { content.contains(it, ignoreCase = true) }
            // Reach is limited by the existing audience; small accounts do not get
            // an arbitrary minimum of ten likes per post.
            val baseLikes = (state.followers * (0.012f + Random.nextFloat() * 0.035f))
                .toLong().coerceAtLeast(if (state.followers > 0L) 1L else 0L)
            // Repeated posts compete for the same weekly audience attention.
            val weeklyPostIndex = state.castPostsCountThisWeek.coerceAtLeast(0)
            val audienceAttention = 1f / (1f + weeklyPostIndex * 0.65f)

            // --- Realistic Algorithm Virality System ---
            // 1. Get posting streak
            val streakStr = state.spiritualJournal.find { it.startsWith("social_streak:") }?.substringAfter("social_streak:")
            val streakVal = streakStr?.toIntOrNull() ?: 0

            // 2. Category focus ratio
            val catPrefix = "social_cat_count:$category:"
            val existingCatEntry = state.spiritualJournal.find { it.startsWith(catPrefix) }
            val currentCatCount = existingCatEntry?.substringAfter(catPrefix)?.toIntOrNull() ?: 0
            val newCatCount = currentCatCount + 1

            val allCatEntries = state.spiritualJournal.filter { it.startsWith("social_cat_count:") }
            val previousTotalPosts = allCatEntries.sumOf { it.substringAfterLast(":").toIntOrNull() ?: 0 }
            val totalPostsWithCurrent = previousTotalPosts + 1
            val focusRatio = if (totalPostsWithCurrent >= 5) {
                newCatCount.toFloat() / totalPostsWithCurrent
            } else {
                1.0f // Give them a clean slate early on
            }

            // 3. Community engagement frequency (liking other posts, replying, etc)
            val engagementStr = state.spiritualJournal.find { it.startsWith("social_other_engagements:") }?.substringAfter("social_other_engagements:")
            val engagements = engagementStr?.toIntOrNull() ?: 0

            // Check if criteria for "Overnight Virality" are met:
            // Posting streak >= 5 weeks, category focus >= 60% of their catalog, and engaged other people's posts >= 8 times
            val streakCheck = streakVal >= 5
            val focusCheck = totalPostsWithCurrent < 5 || focusRatio >= 0.60f
            val engagementCheck = engagements >= 8

            var wentViral = false
            var followerGain = (baseLikes * 0.25 * audienceAttention).toLong()
            var finalLikes = (baseLikes * audienceAttention).toLong()
            var newEngagements = engagements

            if (streakCheck && focusCheck && engagementCheck) {
                // Roll for algorithm trigger (35% base chance under good conditions)
                val isPRManagerHired = state.hiredEmployees.any { it.role.equals("PR Manager", ignoreCase = true) }
                val rollChance = if (isPRManagerHired) 0.50f else 0.35f
                if (Random.nextFloat() < rollChance) {
                    wentViral = true
                    val viralMult = when {
                        state.popularity < 20f -> Random.nextInt(3, 7)
                        state.popularity < 60f -> Random.nextInt(5, 12)
                        else -> Random.nextInt(8, 18)
                    }
                    finalLikes = (baseLikes * viralMult * audienceAttention).toLong()
                    followerGain = when {
                        state.popularity < 20f -> Random.nextLong(150, 600)
                        state.popularity < 60f -> Random.nextLong(800, 3000)
                        else -> Random.nextLong(4000, 15000)
                    }.let { (it * audienceAttention).toLong() }
                    newEngagements = (engagements - 4).coerceAtLeast(0)
                }
            }

            if (!wentViral) {
                followerGain = when {
                    state.popularity < 20f -> Random.nextLong(10, 45)
                    state.popularity < 60f -> Random.nextLong(40, 200)
                    else -> Random.nextLong(150, 800)
                }.let { (it * audienceAttention).toLong() }
            }


            if (state.activePhone == "Pixxelate") {
                followerGain = (followerGain * 1.2).toLong()
            }
            // Mentions carry relationship consequences using small offline phrase rules.
            val praiseTerms = listOf("thank", "support", "proud", "love", "credit", "grateful", "respect")
            val mentionedNpcIds = state.npcs.filter { npc ->
                npc.name.length >= 3 && content.contains(npc.name, ignoreCase = true)
            }.map { it.id }.toSet()
            val npcRelationshipDelta = when {
                criticismTerms.any { content.contains(it, ignoreCase = true) } -> -5f
                praiseTerms.any { content.contains(it, ignoreCase = true) } -> 3f
                else -> 0f
            }
            val updatedNpcs = if (npcRelationshipDelta == 0f || mentionedNpcIds.isEmpty()) state.npcs else
                state.npcs.map { npc ->
                    if (npc.id in mentionedNpcIds) npc.copy(
                        relationshipWithPlayer = (npc.relationshipWithPlayer + npcRelationshipDelta).coerceIn(0f, 100f)
                    ) else npc
                }
            val finalReposts = (finalLikes * (if (wentViral) 0.35f else 0.15f)).toLong()
            val finalComments = (finalLikes * (if (wentViral) 0.25f else 0.10f)).toLong()

            val newPost = CastPost(
                id = UUID.randomUUID().toString().take(6),
                authorName = state.artistName,
                authorHandle = "@${state.artistName.lowercase().replace(" ", "")}",
                content = content,
                likes = finalLikes,
                reposts = finalReposts,
                commentsCount = finalComments,
                week = state.currentWeek,
                year = state.currentYear,
                isByPlayer = true,
                imageRes = imageRes
            )

            val imageShift = if (isControversial) -5f else +3f
            val sentimentShift = if (isControversial) -8f else +5f

            var promoPosted = state.brandDealPostedThisWeek
            if (state.activeBrandDealName != null) {
                val brandNameStr = state.activeBrandDealName
                val explicitAdTag = Regex("(?i)(^|\\s)#?ad($|\\s|[.,!?])").containsMatchIn(content)
                if (content.contains(brandNameStr, ignoreCase = true) || content.contains("promo", ignoreCase = true) || content.contains("sponsor", ignoreCase = true) || explicitAdTag) {
                    promoPosted = true
                    showToast("Published promotion on Cast platform! Marked weekly brand promotion as COMPLETED!")
                } else if (wentViral) {
                    showToast("🔥 Overnight Viral Post published on Cast!")
                } else {
                    showToast("Successfully published post in '${category}' category!")
                }
            } else if (wentViral) {
                showToast("🔥 Overnight Viral Post published on Cast!")
            } else {
                showToast("Successfully published post in '${category}' category!")
            }

            if (wentViral) {
                showToast("🔥 ALGORITHM TAKEOVER! Your consistent posts in '$category', active engagement, and posting streak triggered a massive viral wave overnight! Gained ${String.format("%,d", followerGain)} new followers!")
            }

            // Build updated spiritual journal
            val updatedSpiritualJournal = state.spiritualJournal.filter {
                !it.startsWith("latest_metube_category_") &&
                !it.startsWith("latest_cast_category_") &&
                !it.startsWith("social_cat_count:") &&
                !it.startsWith("social_other_engagements:")
            }.toMutableList()

            updatedSpiritualJournal.add("latest_metube_category_$category")
            updatedSpiritualJournal.add("latest_cast_category_$category")
            updatedSpiritualJournal.add("$catPrefix$newCatCount")
            updatedSpiritualJournal.add("social_other_engagements:$newEngagements")

            // Re-add other categories
            allCatEntries.forEach { entry ->
                if (!entry.startsWith(catPrefix)) {
                    updatedSpiritualJournal.add(entry)
                }
            }

            val timelineEventStr = if (wentViral) {
                "Year ${state.currentYear}, Week ${state.currentWeek}: Went viral overnight on CastSocial with a trending post on '${category}'!"
            } else null

            state.copy(
                energy = state.energy - (if (state.activePhone == "Xiaomax") 1 else 3),
                castPosts = listOf(newPost) + state.castPosts,
                npcs = updatedNpcs,
                castPostsCountThisWeek = state.castPostsCountThisWeek + 1,
                publicImage = (state.publicImage + imageShift).coerceIn(10f, 100f),
                brandDealPostedThisWeek = promoPosted,
                timelineEvents = if (timelineEventStr != null) state.timelineEvents + timelineEventStr else state.timelineEvents,
                sentiment = state.sentiment.copy(
                    followers = state.followers + followerGain,
                    positiveSentiment = (state.positiveSentiment + (if (wentViral) sentimentShift * 2f else sentimentShift)).coerceIn(5f, 100f),
                    trendingScore = (state.trendingScore + (if (wentViral) 35f else 6f)).coerceAtMost(100f)
                ),
                spiritual = state.spiritual.copy(spiritualJournal = updatedSpiritualJournal)
            )
        }
    }

    fun likeCastPost(postId: String) {
        updateState { state ->
            var isOtherPlayerPost = false
            var isLiking = false
            val updated = state.castPosts.map { post ->
                if (post.id == postId) {
                    val isLiked = !post.isLikedByPlayer
                    isLiking = isLiked
                    isOtherPlayerPost = !post.isByPlayer
                    post.copy(
                        isLikedByPlayer = isLiked,
                        likes = post.likes + (if (isLiked) 1 else -1)
                    )
                } else post
            }

            val updatedSpiritualJournal = state.spiritualJournal.toMutableList()
            if (isOtherPlayerPost && isLiking) {
                val existing = state.spiritualJournal.find { it.startsWith("social_other_engagements:") }
                val count = existing?.substringAfter("social_other_engagements:")?.toIntOrNull() ?: 0
                val newCount = count + 1
                updatedSpiritualJournal.removeAll { it.startsWith("social_other_engagements:") }
                updatedSpiritualJournal.add("social_other_engagements:$newCount")
            }

            state.copy(
                castPosts = updated,
                spiritual = state.spiritual.copy(spiritualJournal = updatedSpiritualJournal)
            )
        }
    }

    fun deleteCastPost(postId: String) {
        updateState { state ->
            state.copy(castPosts = state.castPosts.filter { it.id != postId })
        }
    }

    // --- Exchange Order Book Systems ---
    fun placeStockOrder(stockId: String, type: String, targetPrice: Double, quantity: Int) {
        updateState { state ->
            val stock = state.stocks.find { it.id == stockId }
            if (stock == null) {
                showToast("Stock not found.")
                return@updateState state
            }
            if (quantity <= 0) {
                showToast("Quantity must be greater than zero.")
                return@updateState state
            }
            if (type == "LIMIT_BUY") {
                val requiredCash = targetPrice * quantity
                if (state.cash < requiredCash) {
                    showToast("Insufficient reserves for this limit buy setup ($${String.format("%.2f", requiredCash)} required).")
                    return@updateState state
                }
            } else if (type == "LIMIT_SELL") {
                if (stock.quantity < quantity) {
                    showToast("You do not own enough shares of ${stock.name} for this order!")
                    return@updateState state
                }
            }

            val newOrder = StockOrder(
                id = UUID.randomUUID().toString().take(6),
                stockId = stockId,
                type = type,
                targetPrice = targetPrice,
                quantity = quantity
            )

            showToast("Placed stock limit $type order on WESHARE exchange.")
            state.copy(pendingStockOrders = state.pendingStockOrders + newOrder)
        }
    }

    fun cancelStockOrder(orderId: String) {
        updateState { state ->
            showToast("Cancelled stock order.")
            state.copy(pendingStockOrders = state.pendingStockOrders.filter { it.id != orderId })
        }
    }

    fun placeCryptoOrder(cryptoId: String, type: String, targetPrice: Double, quantity: Double) {
        updateState { state ->
            val crypto = state.cryptos.find { it.id == cryptoId }
            if (crypto == null) {
                showToast("Crypto currency not found.")
                return@updateState state
            }
            if (quantity <= 0.0) {
                showToast("Quantity must be greater than zero.")
                return@updateState state
            }
            if (type == "LIMIT_BUY") {
                val requiredCash = targetPrice * quantity
                if (state.cash < requiredCash) {
                    showToast("Insufficient reserves to support this buy configuration.")
                    return@updateState state
                }
            } else { // LIMIT_SELL, STOP_LOSS, TAKE_PROFIT
                if (crypto.quantity < quantity) {
                    showToast("You do not hold enough coins for this order!")
                    return@updateState state
                }
            }

            val newOrder = CryptoOrder(
                id = UUID.randomUUID().toString().take(6),
                cryptoId = cryptoId,
                type = type,
                targetPrice = targetPrice,
                quantity = quantity
            )

            showToast("Placed crypto order on Genesis exchange.")
            state.copy(pendingCryptoOrders = state.pendingCryptoOrders + newOrder)
        }
    }

    fun cancelCryptoOrder(orderId: String) {
        updateState { state ->
            showToast("Cancelled crypto order.")
            state.copy(pendingCryptoOrders = state.pendingCryptoOrders.filter { it.id != orderId })
        }
    }


    fun startBusinessVenture(type: String, customName: String? = null) {
        updateState { state ->
            val sideJobSlots = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
            val tourSlots = state.tours.filter { it.status == "ACTIVE" }.sumOf { it.weeklyTimeCost }
            val ventureSlots = state.businessVentures.count { it.isOwned && it.isPlayerInControl } * 10
            val totalUsedSlots = sideJobSlots + tourSlots + ventureSlots
            val freeSlots = state.maxTimeSlots - totalUsedSlots
            if (freeSlots < 10) {
                showToast("Managing a venture requires 10 free time slots! (Available: $freeSlots, Needed: 10). Upgrade time slots or finish other commitments first!")
                return@updateState state
            }

            val newVenture = VentureSimulationHelper.createVenture(type, customName)
            if (state.cash < newVenture.setupCost) {
                showToast("Not enough cash to launch ${newVenture.name}! (Need $${String.format("%,.0f", newVenture.setupCost)})")
                return@updateState state
            }
            showToast("Successfully launched ${newVenture.name}! (Committed 10 time slots) Generating $${String.format("%,.0f", newVenture.weeklyGrossRevenue)}/wk gross ($${String.format("%,.0f", newVenture.weeklyNetProfit)}/wk net).")
            state.copy(
                cash = state.cash - newVenture.setupCost,
                businessVentures = state.businessVentures + newVenture
            )
        }
    }

    fun upgradeBusinessVenture(id: String) {
        updateState { state ->
            if (state.energy < 8) {
                showToast("Not enough energy to oversee business expansion & upgrade (needs 8 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id }
            if (venture == null) {
                showToast("Business venture not found!")
                return@updateState state
            }
            val upgradeInfo = VentureSimulationHelper.getUpgradeInfo(venture)
            if (upgradeInfo == null || upgradeInfo.isMaxed) {
                showToast("${venture.name} has already reached maximum capacity!")
                return@updateState state
            }
            if (state.cash < upgradeInfo.cost) {
                showToast("Insufficient funds! Upgrade requires $${String.format("%,.0f", upgradeInfo.cost)}.")
                return@updateState state
            }
            val upgraded = VentureSimulationHelper.performUpgrade(venture)
            val updatedList = state.businessVentures.map { if (it.id == id) upgraded else it }
            showToast("Upgraded ${venture.name}! New Net Cashflow: $${String.format("%,.0f", upgraded.weeklyNetProfit)}/wk. (-8 Energy)")
            state.copy(
                energy = (state.energy - 8).coerceAtLeast(0),
                cash = state.cash - upgradeInfo.cost,
                businessVentures = updatedList
            )
        }
    }

    fun buyTaxiCar(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val maxAllowed = venture.taxiDepotCapacity.takeIf { it > 0 } ?: 5
            if (venture.taxiCarCount >= maxAllowed) {
                showToast("Depot capacity full (${venture.taxiCarCount}/$maxAllowed taxis)! You must upgrade your depot in Overview to buy more vehicles.")
                return@updateState state
            }
            val cost = VentureSimulationHelper.TAXI_CAR_COST
            if (state.cash < cost) {
                showToast("Insufficient cash! New taxi vehicle costs $${String.format("%,.0f", cost)}.")
                return@updateState state
            }
            val newCars = venture.taxiCarCount + 1
            val updated = venture.copy(
                unitCount = newCars,
                totalCapitalInvested = venture.totalCapitalInvested + cost
            ).copyTaxi(taxiCarCount = newCars)
            showToast("🚗 Acquired taxi vehicle #$newCars for $${String.format("%,.0f", cost)}! ($newCars/$maxAllowed depot capacity used). Remember to hire a driver.")
            state.copy(
                cash = state.cash - cost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun hireTaxiDriver(id: String) {
        updateState { state ->
            if (state.energy < 3) {
                showToast("Not enough energy to interview and hire a driver (needs 3 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val newDrivers = venture.taxiDriverCount + 1
            val updated = venture.copyTaxi(taxiDriverCount = newDrivers)
            showToast("👨‍✈️ Hired driver #$newDrivers ($${String.format("%,.0f", VentureSimulationHelper.TAXI_DRIVER_SALARY)}/wk salary). (-3 Energy)")
            state.copy(
                energy = (state.energy - 3).coerceAtLeast(0),
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun fireTaxiDriver(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (venture.taxiDriverCount <= 0) {
                showToast("No drivers currently on payroll!")
                return@updateState state
            }
            val newDrivers = venture.taxiDriverCount - 1
            val updated = venture.copyTaxi(taxiDriverCount = newDrivers)
            showToast("Dismissed 1 driver. Active drivers: $newDrivers.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun serviceTaxiFleet(id: String) {
        updateState { state ->
            if (state.energy < 5) {
                showToast("Not enough energy to oversee fleet servicing (needs 5 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val costPerCar = VentureSimulationHelper.TAXI_SERVICE_BASE_COST
            val totalCost = venture.taxiCarCount.coerceAtLeast(1) * costPerCar
            if (state.cash < totalCost) {
                showToast("Insufficient cash! Deep servicing ${venture.taxiCarCount} vehicle(s) costs $${String.format("%,.0f", totalCost)}.")
                return@updateState state
            }
            val updated = venture.copyTaxi(taxiCondition = 1.0f)
            showToast("🔧 Fully serviced fleet of ${venture.taxiCarCount} vehicle(s) for $${String.format("%,.0f", totalCost)}! Mechanical condition restored to 100%. (-5 Energy)")
            state.copy(
                energy = (state.energy - 5).coerceAtLeast(0),
                cash = state.cash - totalCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun hireRestaurantStaff(id: String, role: String) {
        updateState { state ->
            if (state.energy < 3) {
                showToast("Not enough energy to interview and hire staff (needs 3 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val updated = when (role) {
                "Waiter" -> venture.copyRestaurant(waiterCount = venture.waiterCount + 1)
                "Chef" -> venture.copyRestaurant(chefCount = venture.chefCount + 1)
                "Senior Chef" -> venture.copyRestaurant(seniorChefCount = venture.seniorChefCount + 1)
                "Cleaner" -> venture.copyRestaurant(cleanerCount = venture.cleanerCount + 1)
                "Manager" -> venture.copyRestaurant(managerCount = venture.managerCount + 1)
                else -> venture
            }
            showToast("Hired 1 $role for ${venture.name}! (-3 Energy)")
            state.copy(
                energy = (state.energy - 3).coerceAtLeast(0),
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun fireRestaurantStaff(id: String, role: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            var success = false
            val updated = when (role) {
                "Waiter" -> if (venture.waiterCount > 0) { success = true; venture.copyRestaurant(waiterCount = venture.waiterCount - 1) } else venture
                "Chef" -> if (venture.chefCount > 0) { success = true; venture.copyRestaurant(chefCount = venture.chefCount - 1) } else venture
                "Senior Chef" -> if (venture.seniorChefCount > 0) { success = true; venture.copyRestaurant(seniorChefCount = venture.seniorChefCount - 1) } else venture
                "Cleaner" -> if (venture.cleanerCount > 0) { success = true; venture.copyRestaurant(cleanerCount = venture.cleanerCount - 1) } else venture
                "Manager" -> if (venture.managerCount > 0) { success = true; venture.copyRestaurant(managerCount = venture.managerCount - 1) } else venture
                else -> venture
            }
            if (!success) {
                showToast("No $role currently on payroll!")
                return@updateState state
            }
            showToast("Dismissed 1 $role from ${venture.name}.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun buyRestaurantEquipment(id: String, equipmentId: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val equipmentList = if (venture.equipmentList.isEmpty()) VentureSimulationHelper.getDefaultRestaurantEquipment() else venture.equipmentList
            val eq = equipmentList.find { it.id == equipmentId }
            if (eq == null) {
                showToast("Equipment not found!")
                return@updateState state
            }
            if (eq.isOwned) {
                showToast("${eq.name} is already installed!")
                return@updateState state
            }
            if (venture.tierLevel < eq.unlockTier) {
                val tierDetail = VentureSimulationHelper.RESTAURANT_TIER_DETAILS.find { it.tierLevel == eq.unlockTier }
                showToast("${eq.name} requires ${tierDetail?.name ?: "Tier ${eq.unlockTier}"}!")
                return@updateState state
            }
            if (state.cash < eq.price) {
                showToast("Insufficient cash! ${eq.name} costs $${String.format("%,.0f", eq.price)}.")
                return@updateState state
            }
            val updatedEquip = equipmentList.map {
                if (it.id == equipmentId) it.copy(isOwned = true, condition = 1.0f) else it
            }
            val updated = venture.copy(
                totalCapitalInvested = venture.totalCapitalInvested + eq.price
            ).copyRestaurant(
                equipmentList = updatedEquip
            )
            showToast("🍳 Installed ${eq.name} for $${String.format("%,.0f", eq.price)}! Kitchen efficiency and menu capacity improved.")
            state.copy(
                cash = state.cash - eq.price,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun serviceRestaurantEquipment(id: String, equipmentId: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val equipmentList = if (venture.equipmentList.isEmpty()) VentureSimulationHelper.getDefaultRestaurantEquipment() else venture.equipmentList
            val eq = equipmentList.find { it.id == equipmentId }
            if (eq == null || !eq.isOwned) {
                showToast("Equipment not owned!")
                return@updateState state
            }
            val serviceCost = (eq.price * 0.15).coerceAtLeast(50.0)
            if (state.cash < serviceCost) {
                showToast("Insufficient cash! Servicing ${eq.name} costs $${String.format("%,.0f", serviceCost)}.")
                return@updateState state
            }
            val updatedEquip = equipmentList.map {
                if (it.id == equipmentId) it.copy(condition = 1.0f) else it
            }
            val updated = venture.copyRestaurant(equipmentList = updatedEquip)
            showToast("🔧 Serviced and tuned ${eq.name} for $${String.format("%,.0f", serviceCost)}! Appliance condition restored to 100%.")
            state.copy(
                cash = state.cash - serviceCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun acquireFarmLand(id: String, optionKey: String, isBuying: Boolean) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val landOption = VentureSimulationHelper.FARM_LAND_OPTIONS.find { it.tierKey == optionKey } ?: return@updateState state

            if (isBuying) {
                if (state.cash < landOption.purchasePrice) {
                    showToast("Insufficient cash! Purchasing ${landOption.name} costs $${String.format("%,.0f", landOption.purchasePrice)}.")
                    return@updateState state
                }
                val updatedAcres = venture.farmLandAcres + landOption.acres
                val updatedLandValue = venture.farmLandValue + landOption.purchasePrice
                val updated = venture.copy(
                    totalCapitalInvested = venture.totalCapitalInvested + landOption.purchasePrice
                ).copyFarm(
                    farmLandTier = landOption.tierKey,
                    farmLandIsOwned = true,
                    farmLandAcres = updatedAcres,
                    farmLandValue = updatedLandValue,
                    farmLandRentWeekly = venture.farmLandRentWeekly
                )
                showToast("🏡 Purchased ${landOption.name} (+${landOption.acres.toInt()} acres, Total: ${updatedAcres.toInt()} acres) for $${String.format("%,.0f", landOption.purchasePrice)}! Appreciating land asset accumulated.")
                state.copy(
                    cash = state.cash - landOption.purchasePrice,
                    businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
                )
            } else {
                val updatedAcres = venture.farmLandAcres + landOption.acres
                val updatedRent = venture.farmLandRentWeekly + landOption.weeklyRent
                val updated = venture.copyFarm(
                    farmLandTier = landOption.tierKey,
                    farmLandAcres = updatedAcres,
                    farmLandRentWeekly = updatedRent
                )
                showToast("📄 Leased ${landOption.name} (+${landOption.acres.toInt()} acres, Total: ${updatedAcres.toInt()} acres) at +$${String.format("%,.0f", landOption.monthlyRent)}/month. Land parcel added.")
                state.copy(
                    businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
                )
            }
        }
    }

    fun plantFarmCrop(id: String, cropName: String, acres: Double) {
        updateState { state ->
            if (state.energy < 4) {
                showToast("Not enough energy to prepare fields and plant crops (needs 4 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val cropInfo = VentureSimulationHelper.CROPS_CATALOG.find { it.name == cropName } ?: return@updateState state

            val currentlyPlantedAcres = venture.farmCropBatches.sumOf { it.acres }
            val availableAcres = venture.farmLandAcres - currentlyPlantedAcres
            if (acres > availableAcres) {
                showToast("Not enough land! Available: ${String.format("%.1f", availableAcres)} acres (Farm total: ${venture.farmLandAcres.toInt()} acres).")
                return@updateState state
            }

            val seedCost = cropInfo.seedCostPerAcre * acres
            if (state.cash < seedCost) {
                showToast("Insufficient cash! Seeds for ${acres.toInt()} acres of $cropName cost $${String.format("%,.0f", seedCost)}.")
                return@updateState state
            }

            val newBatch = FarmCropBatch(
                id = "batch_" + UUID.randomUUID().toString().take(6),
                cropName = cropInfo.name,
                acres = acres,
                seedCost = seedCost,
                totalWeeks = cropInfo.growthWeeks,
                currentWeek = 0,
                isReadyToHarvest = false,
                expectedYieldUnits = cropInfo.expectedYieldUnits * acres,
                yieldUnitName = cropInfo.yieldUnitName
            )

            val updatedBatches = venture.farmCropBatches + newBatch
            val updated = venture.copyFarm(farmCropBatches = updatedBatches)
            showToast("🌱 Planted ${acres.toInt()} acres of $cropName (Cost: $${String.format("%,.0f", seedCost)}, Growth: ${cropInfo.growthWeeks} wks). (-4 Energy)")
            state.copy(
                energy = (state.energy - 4).coerceAtLeast(0),
                cash = state.cash - seedCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun cleanFarm(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val hasFarmhands = venture.farmHandCount > 0
            val energyCost = if (hasFarmhands) 1 else 3
            val cleaningCost = if (hasFarmhands) 80.0 else 250.0

            if (state.energy < energyCost) {
                showToast("Not enough energy to clean and sanitize the farm (needs $energyCost energy)!")
                return@updateState state
            }
            if (state.cash < cleaningCost) {
                showToast("Insufficient funds! Cleaning supplies and sanitization crew cost $${cleaningCost.toInt()}.")
                return@updateState state
            }

            val hygieneBoost = if (hasFarmhands) 0.50f else 0.35f
            val updatedCleanliness = (venture.farmCleanliness + hygieneBoost).coerceAtMost(1.0f)
            val updated = venture.copyFarm(farmCleanliness = updatedCleanliness)
            val crewMsg = if (hasFarmhands) "Farmhands mobilized! Sanitized fields & pastures" else "Sanitized farm grounds"
            showToast("🧹 $crewMsg! Land hygiene raised to ${(updatedCleanliness * 100).toInt()}% clean. (-$energyCost Energy, -$${cleaningCost.toInt()})")
            state.copy(
                energy = (state.energy - energyCost).coerceAtLeast(0),
                cash = state.cash - cleaningCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun harvestAllFarmCrops(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val readyBatches = venture.farmCropBatches.filter { it.isReadyToHarvest || it.currentWeek >= it.totalWeeks }
            if (readyBatches.isEmpty()) {
                showToast("No crop batches ready for harvest!")
                return@updateState state
            }

            val invList = venture.farmInventory.toMutableList()
            var totalHarvestedCount = 0

            for (batch in readyBatches) {
                val cropInfo = VentureSimulationHelper.CROPS_CATALOG.find { it.name == batch.cropName } ?: VentureSimulationHelper.CROPS_CATALOG.first()
                val yieldUnits = batch.expectedYieldUnits
                val unitPrice = cropInfo.marketPricePerUnit

                val existIdx = invList.indexOfFirst { it.name == batch.cropName }
                if (existIdx >= 0) {
                    val item = invList[existIdx]
                    invList[existIdx] = item.copy(
                        quantity = item.quantity + yieldUnits,
                        marketPricePerUnit = unitPrice
                    )
                } else {
                    invList.add(
                        FarmInventoryItem(
                            id = "inv_" + UUID.randomUUID().toString().take(6),
                            name = batch.cropName,
                            category = "CROP",
                            quantity = yieldUnits,
                            unit = batch.yieldUnitName,
                            marketPricePerUnit = unitPrice
                        )
                    )
                }
                totalHarvestedCount++
            }

            val remainingBatches = venture.farmCropBatches.filter { !it.isReadyToHarvest && it.currentWeek < it.totalWeeks }
            val updated = venture.copyFarm(
                farmCropBatches = remainingBatches,
                farmInventory = invList
            )
            showToast("🌾 Harvested $totalHarvestedCount crop batch(es) into farm storage!")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun harvestFarmCrop(id: String, batchId: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val batch = venture.farmCropBatches.find { it.id == batchId } ?: return@updateState state

            val cropInfo = VentureSimulationHelper.CROPS_CATALOG.find { it.name == batch.cropName } ?: VentureSimulationHelper.CROPS_CATALOG.first()
            val yieldUnits = batch.expectedYieldUnits
            val unitPrice = cropInfo.marketPricePerUnit

            val invList = venture.farmInventory.toMutableList()
            val existIdx = invList.indexOfFirst { it.name == batch.cropName }
            if (existIdx >= 0) {
                val item = invList[existIdx]
                invList[existIdx] = item.copy(
                    quantity = item.quantity + yieldUnits,
                    marketPricePerUnit = unitPrice
                )
            } else {
                invList.add(
                    FarmInventoryItem(
                        id = "inv_" + UUID.randomUUID().toString().take(6),
                        name = batch.cropName,
                        category = "CROP",
                        quantity = yieldUnits,
                        unit = batch.yieldUnitName,
                        marketPricePerUnit = unitPrice
                    )
                )
            }

            val remainingBatches = venture.farmCropBatches.filter { it.id != batchId }
            val updated = venture.copyFarm(
                farmCropBatches = remainingBatches,
                farmInventory = invList
            )
            showToast("🚜 Harvested ${yieldUnits.toInt()} ${batch.yieldUnitName} of ${batch.cropName} into storage!")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun sellFarmInventory(id: String, itemId: String, quantityToSell: Double) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val item = venture.farmInventory.find { it.id == itemId } ?: return@updateState state

            val actualQty = quantityToSell.coerceAtMost(item.quantity)
            if (actualQty <= 0.0) {
                showToast("No quantity to sell!")
                return@updateState state
            }

            val hasDriver = venture.farmDriverCount > 0
            val freightMultiplier = if (hasDriver) 1.15 else 1.0
            val effectiveUnitPrice = item.marketPricePerUnit * freightMultiplier
            val revenue = actualQty * effectiveUnitPrice

            val updatedInv = venture.farmInventory.mapNotNull {
                if (it.id == itemId) {
                    val rem = it.quantity - actualQty
                    if (rem > 0.01) it.copy(quantity = rem) else null
                } else {
                    it
                }
            }

            val updated = venture.copy(
                accumulatedProfit = venture.accumulatedProfit + revenue
            ).copyFarm(
                farmInventory = updatedInv,
                farmWeeklyRawCropsRevenue = venture.farmWeeklyRawCropsRevenue + revenue
            )
            val driverBonusNotice = if (hasDriver) " (🚚 +15% Driver Freight Delivery Bonus)" else ""
            showToast("💰 Sold ${actualQty.toInt()} ${item.unit} of ${item.name} for $${String.format("%,.0f", revenue)}$driverBonusNotice!")
            state.copy(
                cash = state.cash + revenue,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun buyFarmMachinery(id: String, machineId: String, isRent: Boolean) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val machineryList = if (venture.farmMachinery.isEmpty()) VentureSimulationHelper.getDefaultFarmMachinery() else venture.farmMachinery
            val m = machineryList.find { it.id == machineId } ?: return@updateState state

            if (isRent) {
                val updatedMach = machineryList.map {
                    if (it.id == machineId) it.copy(isRented = true, isOwned = false) else it
                }
                val updated = venture.copyFarm(farmMachinery = updatedMach)
                showToast("🚜 Rented ${m.name} for $${String.format("%,.0f", m.weeklyRentalCost)}/wk. No large upfront capital required.")
                state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
            } else {
                if (state.cash < m.purchaseCost) {
                    showToast("Insufficient cash! Buying ${m.name} costs $${String.format("%,.0f", m.purchaseCost)}.")
                    return@updateState state
                }
                val updatedMach = machineryList.map {
                    if (it.id == machineId) it.copy(isOwned = true, isRented = false, condition = 1.0f, bookValue = m.purchaseCost) else it
                }
                val updated = venture.copy(
                    totalCapitalInvested = venture.totalCapitalInvested + m.purchaseCost
                ).copyFarm(
                    farmMachinery = updatedMach
                )
                showToast("🚜 Purchased ${m.name} for $${String.format("%,.0f", m.purchaseCost)}! Permanent farm asset acquired.")
                state.copy(
                    cash = state.cash - m.purchaseCost,
                    businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
                )
            }
        }
    }

    fun serviceFarmMachinery(id: String, machineId: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val machineryList = if (venture.farmMachinery.isEmpty()) VentureSimulationHelper.getDefaultFarmMachinery() else venture.farmMachinery
            val m = machineryList.find { it.id == machineId } ?: return@updateState state

            if (!m.isOwned) {
                showToast("Only owned machinery requires servicing!")
                return@updateState state
            }

            val serviceCost = (m.purchaseCost * 0.08).coerceAtLeast(40.0)
            if (state.cash < serviceCost) {
                showToast("Insufficient cash! Servicing ${m.name} costs $${String.format("%,.0f", serviceCost)}.")
                return@updateState state
            }

            val updatedMach = machineryList.map {
                if (it.id == machineId) it.copy(condition = 1.0f) else it
            }
            val updated = venture.copyFarm(farmMachinery = updatedMach)
            showToast("🔧 Overhauled ${m.name} for $${String.format("%,.0f", serviceCost)}! Condition restored to 100%.")
            state.copy(
                cash = state.cash - serviceCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun buyFarmLivestock(id: String, livestockType: String, count: Int) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (venture.tierLevel < 3) {
                showToast("Livestock unlocks at Tier 3 (Crops, Fruits & Livestock)!")
                return@updateState state
            }
            val livestockList = if (venture.farmLivestock.isEmpty()) VentureSimulationHelper.getDefaultFarmLivestock() else venture.farmLivestock
            val grp = livestockList.find { it.type == livestockType } ?: return@updateState state

            val totalCost = count * grp.purchaseCostPerUnit
            if (state.cash < totalCost) {
                showToast("Insufficient cash! Purchasing $count young ${grp.type} costs $${String.format("%,.0f", totalCost)}.")
                return@updateState state
            }

            val newBabies = List(count) { 0 }
            val updatedLivestock = livestockList.map {
                if (it.type == livestockType) {
                    it.copy(
                        count = it.count + count,
                        babyCount = it.babyCount + count,
                        babyWeeksOld = it.babyWeeksOld + newBabies
                    )
                } else it
            }
            val updated = venture.copy(
                totalCapitalInvested = venture.totalCapitalInvested + totalCost
            ).copyFarm(
                farmLivestock = updatedLivestock
            )
            showToast("🐣 Purchased $count young ${grp.type} (bought as kid/chick) for $${String.format("%,.0f", totalCost)}! Matures in ${grp.weeksToMaturity} weeks.")
            state.copy(
                cash = state.cash - totalCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun sellFarmLivestock(id: String, livestockType: String, count: Int) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val livestockList = if (venture.farmLivestock.isEmpty()) VentureSimulationHelper.getDefaultFarmLivestock() else venture.farmLivestock
            val grp = livestockList.find { it.type == livestockType } ?: return@updateState state

            val actualToSell = count.coerceAtMost(grp.matureCount)
            if (actualToSell <= 0) {
                showToast("No mature ${grp.type} available to sell! You have ${grp.babyCount} young ones growing.")
                return@updateState state
            }

            val totalRevenue = actualToSell * grp.matureSalePrice
            val updatedLivestock = livestockList.map {
                if (it.type == livestockType) {
                    it.copy(
                        count = (it.count - actualToSell).coerceAtLeast(0),
                        matureCount = (it.matureCount - actualToSell).coerceAtLeast(0)
                    )
                } else it
            }

            val updated = venture.copy(
                accumulatedProfit = venture.accumulatedProfit + totalRevenue
            ).copyFarm(
                farmLivestock = updatedLivestock,
                farmWeeklyLivestockRevenue = venture.farmWeeklyLivestockRevenue + totalRevenue
            )
            showToast("💰 Sold $actualToSell mature ${grp.type}(s) for $${String.format("%,.0f", totalRevenue)}!")
            state.copy(
                cash = state.cash + totalRevenue,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun processFarmProduct(id: String, recipeId: String, batchCount: Int) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (venture.tierLevel < 4) {
                showToast("Industrial Agriculture processing unlocks at Tier 4 (Agro-Industrial Commercial Farm)!")
                return@updateState state
            }

            val recipe = VentureSimulationHelper.FARM_PROCESSING_RECIPES.find { it.id == recipeId } ?: return@updateState state
            val hasMachine = venture.farmMachinery.any { it.id == recipe.requiredMachineId && (it.isOwned || it.isRented) }
            if (!hasMachine) {
                showToast("Missing machine: ${recipe.requiredMachineName}! Acquire it in the Machines tab.")
                return@updateState state
            }

            if (venture.farmTractorOperatorCount <= 0) {
                showToast("Tractor Operator required! Hire operators in the Staff tab to operate industrial processing machinery.")
                return@updateState state
            }

            val rawItem = venture.farmInventory.find { it.name.equals(recipe.rawIngredientName, ignoreCase = true) }
            val neededQty = recipe.rawQuantityPerBatch * batchCount
            if (rawItem == null || rawItem.quantity < neededQty) {
                val available = rawItem?.quantity?.toInt() ?: 0
                showToast("Insufficient ${recipe.rawIngredientName}! Need ${neededQty.toInt()} ${recipe.rawUnit}, but have $available.")
                return@updateState state
            }

            val processedUnits = recipe.outputQuantityPerBatch * batchCount
            val updatedInv = venture.farmInventory.mapNotNull {
                if (it.name.equals(recipe.rawIngredientName, ignoreCase = true)) {
                    val rem = it.quantity - neededQty
                    if (rem > 0.01) it.copy(quantity = rem) else null
                } else it
            }.toMutableList()

            val existProcIdx = updatedInv.indexOfFirst { it.name.equals(recipe.outputName, ignoreCase = true) }
            if (existProcIdx >= 0) {
                val existing = updatedInv[existProcIdx]
                updatedInv[existProcIdx] = existing.copy(
                    quantity = existing.quantity + processedUnits,
                    marketPricePerUnit = recipe.processedPricePerUnit
                )
            } else {
                updatedInv.add(
                    FarmInventoryItem(
                        id = "inv_proc_" + UUID.randomUUID().toString().take(6),
                        name = recipe.outputName,
                        category = "PROCESSED",
                        quantity = processedUnits,
                        unit = recipe.outputUnit,
                        marketPricePerUnit = recipe.processedPricePerUnit
                    )
                )
            }

            val updated = venture.copyFarm(
                farmInventory = updatedInv
            )
            showToast("🏭 Processed ${neededQty.toInt()} ${recipe.rawUnit} of ${recipe.rawIngredientName} into ${processedUnits.toInt()} ${recipe.outputUnit} of ${recipe.outputName} (+20% value over raw goods)!")
            state.copy(
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun hireFarmStaff(id: String, role: String) {
        updateState { state ->
            if (state.energy < 3) {
                showToast("Not enough energy to interview and hire farm staff (needs 3 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val updated = when (role) {
                "Farmhand" -> venture.copyFarm(farmHandCount = venture.farmHandCount + 1)
                "Tractor Operator" -> venture.copyFarm(farmTractorOperatorCount = venture.farmTractorOperatorCount + 1)
                "Livestock Worker" -> venture.copyFarm(farmLivestockWorkerCount = venture.farmLivestockWorkerCount + 1)
                "Driver" -> venture.copyFarm(farmDriverCount = venture.farmDriverCount + 1)
                "Veterinarian" -> venture.copyFarm(farmVeterinarianCount = venture.farmVeterinarianCount + 1)
                "Farm Manager" -> venture.copyFarm(farmManagerCount = venture.farmManagerCount + 1)
                else -> venture
            }
            showToast("Hired 1 $role for ${venture.name}! (-3 Energy)")
            state.copy(
                energy = (state.energy - 3).coerceAtLeast(0),
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun fireFarmStaff(id: String, role: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            var success = false
            val updated = when (role) {
                "Farmhand" -> if (venture.farmHandCount > 0) { success = true; venture.copyFarm(farmHandCount = venture.farmHandCount - 1) } else venture
                "Tractor Operator" -> if (venture.farmTractorOperatorCount > 0) { success = true; venture.copyFarm(farmTractorOperatorCount = venture.farmTractorOperatorCount - 1) } else venture
                "Livestock Worker" -> if (venture.farmLivestockWorkerCount > 0) { success = true; venture.copyFarm(farmLivestockWorkerCount = venture.farmLivestockWorkerCount - 1) } else venture
                "Driver" -> if (venture.farmDriverCount > 0) { success = true; venture.copyFarm(farmDriverCount = venture.farmDriverCount - 1) } else venture
                "Veterinarian" -> if (venture.farmVeterinarianCount > 0) { success = true; venture.copyFarm(farmVeterinarianCount = venture.farmVeterinarianCount - 1) } else venture
                "Farm Manager" -> if (venture.farmManagerCount > 0) { success = true; venture.copyFarm(farmManagerCount = venture.farmManagerCount - 1) } else venture
                else -> venture
            }
            if (!success) {
                showToast("No $role currently on payroll!")
                return@updateState state
            }
            showToast("Dismissed 1 $role from ${venture.name}.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun quickPlantAllFarmland(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (!venture.isPlayerInControl) {
                showToast("Majority shareholders control farming operations.")
                return@updateState state
            }
            val plantedAcres = venture.farmCropBatches.sumOf { it.acres }
            val freeAcres = (venture.farmLandAcres - plantedAcres).coerceAtLeast(0.0)
            if (freeAcres < 1.0) {
                showToast("All farmland is already planted (${venture.farmLandAcres.toInt()} acres)!")
                return@updateState state
            }
            val crop = VentureSimulationHelper.CROPS_CATALOG.firstOrNull { it.seedCostPerAcre * freeAcres <= state.cash }
                ?: VentureSimulationHelper.CROPS_CATALOG.minByOrNull { it.seedCostPerAcre }
                ?: return@updateState state
            val seedCost = crop.seedCostPerAcre * freeAcres
            if (state.cash < seedCost) {
                showToast("Insufficient cash! Seeds for ${freeAcres.toInt()} acres of ${crop.name} cost $${String.format("%,.0f", seedCost)}.")
                return@updateState state
            }
            val newBatch = FarmCropBatch(
                id = "batch_" + UUID.randomUUID().toString().take(6),
                cropName = crop.name,
                acres = freeAcres,
                seedCost = seedCost,
                totalWeeks = crop.growthWeeks,
                currentWeek = 0,
                isReadyToHarvest = false,
                expectedYieldUnits = crop.expectedYieldUnits * freeAcres,
                yieldUnitName = crop.yieldUnitName
            )
            val updated = venture.copyFarm(farmCropBatches = venture.farmCropBatches + newBatch)
            showToast("🌱 Quick Planted ${freeAcres.toInt()} acres of ${crop.name} for $${String.format("%,.0f", seedCost)}! Ready in ${crop.growthWeeks} weeks.")
            state.copy(
                cash = state.cash - seedCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun quickHarvestAndSellAll(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (!venture.isPlayerInControl) {
                showToast("Majority shareholders control farming operations.")
                return@updateState state
            }
            val readyBatches = venture.farmCropBatches.filter { it.currentWeek >= it.totalWeeks }
            if (readyBatches.isEmpty() && venture.farmInventory.isEmpty()) {
                showToast("No mature crops ready for harvest yet!")
                return@updateState state
            }
            var earnedCash = 0.0
            readyBatches.forEach { b ->
                val cropInfo = VentureSimulationHelper.CROPS_CATALOG.find { it.name == b.cropName } ?: VentureSimulationHelper.CROPS_CATALOG.first()
                earnedCash += b.expectedYieldUnits * cropInfo.marketPricePerUnit
            }
            venture.farmInventory.forEach { inv ->
                earnedCash += inv.quantity * inv.marketPricePerUnit
            }
            val updatedBatches = venture.farmCropBatches.filter { it.currentWeek < it.totalWeeks }
            val updated = venture.copyFarm(
                farmCropBatches = updatedBatches,
                farmInventory = emptyList(),
                farmWeeklyRawCropsRevenue = venture.farmWeeklyRawCropsRevenue + earnedCash
            )
            showToast("🌾 Quick Harvested & Sold! Earned +$${String.format("%,.0f", earnedCash)} into cash balance!")
            state.copy(
                cash = state.cash + earnedCash,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    // --- Hotel Management Methods ---
    fun unlockHotelRoom(id: String, requestedRoomNumber: Int? = null) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val currentRooms = if (venture.hotelRooms.isEmpty()) VentureSimulationHelper.getDefaultHotelRooms() else venture.hotelRooms
            val targetRoom = if (requestedRoomNumber != null) {
                currentRooms.find { it.roomNumber == requestedRoomNumber }
            } else {
                currentRooms.find { !it.isUnlocked }
            }

            if (targetRoom == null || targetRoom.isUnlocked) {
                showToast("All available rooms (up to 20) are already unlocked!")
                return@updateState state
            }

            val cost = VentureSimulationHelper.HOTEL_ROOM_COST // $6,000
            if (state.cash < cost) {
                showToast("Insufficient cash! Unlocking Room #${targetRoom.roomNumber} costs $${String.format("%,.0f", cost)}.")
                return@updateState state
            }

            val updatedRooms = currentRooms.map {
                if (it.roomNumber == targetRoom.roomNumber) {
                    it.copy(
                        isUnlocked = true,
                        condition = 1.0f,
                        isOutOfOrder = false,
                        outOfOrderReason = "",
                        furnishings = emptyList(),
                        weeklyOccupancyRate = 0.50f,
                        lodgesThisWeek = 0,
                        weeklyRevenue = 0.0
                    )
                } else it
            }
            val unlockedCount = updatedRooms.count { it.isUnlocked }
            val updated = venture.copy(
                unitCount = unlockedCount,
                totalCapitalInvested = venture.totalCapitalInvested + cost
            ).copyHotel(
                hotelRooms = updatedRooms,
                hotelRoomCount = unlockedCount
            )
            showToast("🚪 Unlocked Room #${targetRoom.roomNumber} for $${String.format("%,.0f", cost)}! Furnish it now to maximize lodging rates.")
            state.copy(
                cash = state.cash - cost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun furnishHotelRoomItem(id: String, roomNumber: Int, item: HotelFurnitureItem) {
        updateState { state ->
            if (state.energy < 3) {
                showToast("Not enough energy to install room furnishings (needs 3 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (state.cash < item.cost) {
                showToast("Insufficient cash! ${item.name} costs $${String.format("%,.0f", item.cost)}.")
                return@updateState state
            }
            val currentRooms = if (venture.hotelRooms.isEmpty()) VentureSimulationHelper.getDefaultHotelRooms() else venture.hotelRooms
            val targetRoom = currentRooms.find { it.roomNumber == roomNumber }
            if (targetRoom == null || !targetRoom.isUnlocked) {
                showToast("Room #$roomNumber must be unlocked before furnishing!")
                return@updateState state
            }

            val updatedFurnishings = targetRoom.furnishings.filter { it.category != item.category } + item
            val updatedRooms = currentRooms.map {
                if (it.roomNumber == roomNumber) {
                    it.copy(furnishings = updatedFurnishings)
                } else it
            }
            val updatedRoom = updatedRooms.find { it.roomNumber == roomNumber }
            val updated = venture.copy(
                totalCapitalInvested = venture.totalCapitalInvested + item.cost
            ).copyHotel(
                hotelRooms = updatedRooms
            )
            showToast("🛋️ Installed ${item.name} (${item.qualityTier}) in Room #$roomNumber ($${item.cost.toInt()})! Potential: $${updatedRoom?.potentialWeeklyRate?.toInt() ?: 0}/wk. (-3 Energy)")
            state.copy(
                energy = (state.energy - 3).coerceAtLeast(0),
                cash = state.cash - item.cost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun furnishHotelRoomPackage(id: String, roomNumber: Int, tierName: String) {
        updateState { state ->
            if (state.energy < 6) {
                showToast("Not enough energy to oversee complete room furnishing (needs 6 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val packageItems = VentureSimulationHelper.HOTEL_FURNITURE_CATALOG
                .filter { it.qualityTier.equals(tierName, ignoreCase = true) }
                .map { HotelFurnitureItem(it.category, it.name, it.cost, it.qualityTier) }

            if (packageItems.isEmpty()) {
                showToast("Unknown furniture tier: $tierName")
                return@updateState state
            }

            val packageCost = packageItems.sumOf { it.cost }
            if (state.cash < packageCost) {
                showToast("Insufficient cash! Full $tierName Package costs $${String.format("%,.0f", packageCost)}.")
                return@updateState state
            }

            val currentRooms = if (venture.hotelRooms.isEmpty()) VentureSimulationHelper.getDefaultHotelRooms() else venture.hotelRooms
            val targetRoom = currentRooms.find { it.roomNumber == roomNumber }
            if (targetRoom == null || !targetRoom.isUnlocked) {
                showToast("Room #$roomNumber must be unlocked before furnishing!")
                return@updateState state
            }

            val updatedRooms = currentRooms.map {
                if (it.roomNumber == roomNumber) {
                    it.copy(
                        furnishings = packageItems,
                        condition = 1.0f,
                        isOutOfOrder = false,
                        outOfOrderReason = ""
                    )
                } else it
            }
            val updatedRoom = updatedRooms.find { it.roomNumber == roomNumber }
            val updated = venture.copy(
                totalCapitalInvested = venture.totalCapitalInvested + packageCost
            ).copyHotel(
                hotelRooms = updatedRooms
            )
            showToast("✨ Fully outfitted Room #$roomNumber with $tierName Package ($${packageCost.toInt()})! Weekly potential: $${updatedRoom?.potentialWeeklyRate?.toInt() ?: 0}/wk. (-6 Energy)")
            state.copy(
                energy = (state.energy - 6).coerceAtLeast(0),
                cash = state.cash - packageCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun repairHotelRoom(id: String, roomNumber: Int) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val repairCost = 250.0
            if (state.cash < repairCost) {
                showToast("Insufficient cash! Emergency room repair costs $250.")
                return@updateState state
            }
            val currentRooms = if (venture.hotelRooms.isEmpty()) VentureSimulationHelper.getDefaultHotelRooms() else venture.hotelRooms
            val targetRoom = currentRooms.find { it.roomNumber == roomNumber }
            if (targetRoom == null) return@updateState state

            val updatedRooms = currentRooms.map {
                if (it.roomNumber == roomNumber) {
                    it.copy(isOutOfOrder = false, outOfOrderReason = "", condition = 1.0f)
                } else it
            }
            val updated = venture.copyHotel(hotelRooms = updatedRooms)
            showToast("🔧 Repaired Room #$roomNumber! It is back in guest service.")
            state.copy(
                cash = state.cash - repairCost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun setHotelNightlyRate(id: String, newRate: Double) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val rate = newRate.coerceIn(30.0, 2500.0)
            val updated = venture.copyHotel(hotelNightlyRate = rate)
            showToast("Set Average Daily Room Rate (ADR) to $${rate.toInt()}/night for ${venture.name}.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun hireHotelStaff(id: String, role: String) {
        updateState { state ->
            if (state.energy < 3) {
                showToast("Not enough energy to interview and hire staff (needs 3 energy)!")
                return@updateState state
            }
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val updated = when (role) {
                "Housekeeper" -> venture.copyHotel(hotelHousekeeperCount = venture.hotelHousekeeperCount + 1)
                "Receptionist", "Front Desk" -> venture.copyHotel(hotelFrontDeskCount = venture.hotelFrontDeskCount + 1)
                "Chef" -> venture.copyHotel(hotelChefCount = venture.hotelChefCount + 1)
                "Waiter" -> venture.copyHotel(hotelWaiterCount = venture.hotelWaiterCount + 1)
                "Security", "Security Guard" -> venture.copyHotel(hotelSecurityCount = venture.hotelSecurityCount + 1)
                "Maintenance Worker", "Maintenance Tech" -> venture.copyHotel(hotelMaintenanceTechCount = venture.hotelMaintenanceTechCount + 1)
                "Hotel Manager", "General Manager" -> venture.copyHotel(hotelGeneralManagerCount = venture.hotelGeneralManagerCount + 1)
                "Concierge" -> venture.copyHotel(hotelConciergeCount = venture.hotelConciergeCount + 1)
                else -> venture
            }
            showToast("Hired 1 $role for ${venture.name}! (-3 Energy)")
            state.copy(
                energy = (state.energy - 3).coerceAtLeast(0),
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun fireHotelStaff(id: String, role: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            var success = false
            val updated = when (role) {
                "Housekeeper" -> if (venture.hotelHousekeeperCount > 0) { success = true; venture.copyHotel(hotelHousekeeperCount = venture.hotelHousekeeperCount - 1) } else venture
                "Receptionist", "Front Desk" -> if (venture.hotelFrontDeskCount > 0) { success = true; venture.copyHotel(hotelFrontDeskCount = venture.hotelFrontDeskCount - 1) } else venture
                "Chef" -> if (venture.hotelChefCount > 0) { success = true; venture.copyHotel(hotelChefCount = venture.hotelChefCount - 1) } else venture
                "Waiter" -> if (venture.hotelWaiterCount > 0) { success = true; venture.copyHotel(hotelWaiterCount = venture.hotelWaiterCount - 1) } else venture
                "Security", "Security Guard" -> if (venture.hotelSecurityCount > 0) { success = true; venture.copyHotel(hotelSecurityCount = venture.hotelSecurityCount - 1) } else venture
                "Maintenance Worker", "Maintenance Tech" -> if (venture.hotelMaintenanceTechCount > 0) { success = true; venture.copyHotel(hotelMaintenanceTechCount = venture.hotelMaintenanceTechCount - 1) } else venture
                "Hotel Manager", "General Manager" -> if (venture.hotelGeneralManagerCount > 0) { success = true; venture.copyHotel(hotelGeneralManagerCount = venture.hotelGeneralManagerCount - 1) } else venture
                "Concierge" -> if (venture.hotelConciergeCount > 0) { success = true; venture.copyHotel(hotelConciergeCount = venture.hotelConciergeCount - 1) } else venture
                else -> venture
            }
            if (!success) {
                showToast("No $role currently on hotel staff!")
                return@updateState state
            }
            showToast("Dismissed 1 $role from ${venture.name}.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun buyHotelAmenity(id: String, amenityId: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val amenities = if (venture.hotelAmenities.isEmpty()) VentureSimulationHelper.getDefaultHotelAmenities() else venture.hotelAmenities
            val amenity = amenities.find { it.id == amenityId } ?: return@updateState state

            if (amenity.isOwned) {
                showToast("Amenity already installed!")
                return@updateState state
            }
            if (state.cash < amenity.purchasePrice) {
                showToast("Insufficient cash! Installing ${amenity.name} costs $${String.format("%,.0f", amenity.purchasePrice)}.")
                return@updateState state
            }

            val updatedAmenities = amenities.map {
                if (it.id == amenityId) it.copy(isOwned = true) else it
            }
            val updated = venture.copy(
                totalCapitalInvested = venture.totalCapitalInvested + amenity.purchasePrice
            ).copyHotel(
                hotelAmenities = updatedAmenities
            )
            showToast("✨ Installed ${amenity.name} for $${String.format("%,.0f", amenity.purchasePrice)}! Improves guest satisfaction and hotel revenue.")
            state.copy(
                cash = state.cash - amenity.purchasePrice,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun deepCleanHotel(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val cost = (venture.hotelRoomCount * 150.0).coerceAtLeast(500.0)
            if (state.cash < cost) {
                showToast("Insufficient cash! Deep cleaning costs $${String.format("%,.0f", cost)}.")
                return@updateState state
            }
            val updated = venture.copyHotel(
                hotelCleanliness = 1.0f,
                hotelCondition = (venture.hotelCondition + 0.20f).coerceAtMost(1.0f)
            )
            showToast("🧼 Deep sanitation completed for $${String.format("%,.0f", cost)}! Hotel cleanliness restored to 100%.")
            state.copy(
                cash = state.cash - cost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    // --- Crypto Token Management Methods ---
    fun hireCryptoDeveloper(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val cost = VentureSimulationHelper.CRYPTO_DEV_HIRE_COST
            if (state.cash < cost) {
                showToast("Need $${String.format("%,.0f", cost)} recruitment budget to hire a core developer!")
                return@updateState state
            }
            val newDevs = venture.cryptoDevelopersCount + 1
            val updated = venture.copyCrypto(
                cryptoDevelopersCount = newDevs,
                cryptoWeeklyPayrollCost = newDevs * VentureSimulationHelper.CRYPTO_DEV_SALARY,
                cryptoEducationalLesson = "Hired developer #$newDevs! Developer capacity increased, expanding active user headroom and unlocked protocol service development."
            )
            showToast("💻 Hired Core Protocol Developer for ${venture.name}! Team: $newDevs dev(s). Weekly payroll: $${String.format("%,.0f", updated.cryptoWeeklyPayrollCost)}/wk.")
            state.copy(
                cash = state.cash - cost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun fireCryptoDeveloper(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (venture.cryptoDevelopersCount <= 0) {
                showToast("No active developers to let go!")
                return@updateState state
            }
            val newDevs = venture.cryptoDevelopersCount - 1
            val updated = venture.copyCrypto(
                cryptoDevelopersCount = newDevs,
                cryptoWeeklyPayrollCost = newDevs * VentureSimulationHelper.CRYPTO_DEV_SALARY
            )
            showToast("Reduced core dev team to $newDevs developer(s).")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun upgradeCryptoSecurity(id: String, targetTier: Int) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val tierDetail = VentureSimulationHelper.CRYPTO_SECURITY_TIERS.find { it.tier == targetTier } ?: return@updateState state
            val cost = tierDetail.upgradeCost
            if (state.cash < cost) {
                showToast("Need $${String.format("%,.0f", cost)} for ${tierDetail.name} upgrade!")
                return@updateState state
            }
            val updated = venture.copyCrypto(
                cryptoSecurityTier = targetTier,
                cryptoWeeklySecurityCost = tierDetail.weeklyCost,
                cryptoEducationalLesson = "Why did security upgrade cost money? Smart contracts need audits and multi-sig shields to prevent devastating liquidity pool exploits."
            )
            showToast("🛡️ Upgraded Security to ${tierDetail.name}! Smart contract exploit risk dropped to ${tierDetail.vulnerabilityPercent}%.")
            state.copy(
                cash = state.cash - cost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun upgradeCryptoWebsiteApp(id: String, targetTier: Int) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val tierDetail = VentureSimulationHelper.CRYPTO_WEBSITE_APP_TIERS.find { it.tier == targetTier } ?: return@updateState state
            val cost = tierDetail.upgradeCost
            if (state.cash < cost) {
                showToast("Need $${String.format("%,.0f", cost)} for ${tierDetail.name} infrastructure!")
                return@updateState state
            }
            val updated = venture.copyCrypto(
                cryptoWebsiteAppTier = targetTier,
                cryptoWeeklyServerInfraCost = tierDetail.weeklyCost,
                cryptoEducationalLesson = "Expanded portal with ${tierDetail.name}! User capacity scaled to ${String.format("%,d", tierDetail.userCapacity)} users to prevent server overload and user churn."
            )
            showToast("🌐 Upgraded Website & App to ${tierDetail.name}! Active user capacity expanded to ${String.format("%,d", tierDetail.userCapacity)}.")
            state.copy(
                cash = state.cash - cost,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun setCryptoMarketingTier(id: String, targetTier: Int) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val tierDetail = VentureSimulationHelper.CRYPTO_MARKETING_TIERS.find { it.tier == targetTier } ?: return@updateState state
            val updated = venture.copyCrypto(
                cryptoMarketingTier = targetTier,
                cryptoEducationalLesson = "Active marketing: ${tierDetail.name}. Expanding awareness attracts new token holders and drives AMM trading volume."
            )
            showToast("📣 Marketing strategy set to ${tierDetail.name} ($${String.format("%,.0f", tierDetail.weeklyCost)}/wk).")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun launchCryptoNewService(id: String, serviceId: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val service = VentureSimulationHelper.CRYPTO_AVAILABLE_SERVICES.find { it.id == serviceId } ?: return@updateState state
            if (venture.cryptoDevelopersCount < service.minDevsRequired) {
                showToast("Requires at least ${service.minDevsRequired} core developers to build ${service.name}! (Currently have ${venture.cryptoDevelopersCount})")
                return@updateState state
            }
            if (state.cash < service.devBudgetCost) {
                showToast("Need $${String.format("%,.0f", service.devBudgetCost)} development budget for ${service.name}!")
                return@updateState state
            }
            val userSurge = (venture.cryptoActiveUsers * service.userBoostPercent).toInt().coerceAtLeast(40)
            val newPrice = kotlin.math.round(venture.cryptoTokenPrice * (1.0 + service.priceSurgePercent) * 10000.0) / 10000.0
            val updated = venture.copyCrypto(
                cryptoActiveUsers = venture.cryptoActiveUsers + userSurge,
                cryptoTokenPrice = newPrice,
                cryptoNewServiceLaunchBoostWeeks = 4,
                cryptoLastServiceLaunched = service.name,
                cryptoEducationalLesson = "Why did the token jump after our new service launched? More people now have a utility reason to hold and use the token."
            )
            val news = NewsArticle(
                id = "news_crypto_" + java.util.UUID.randomUUID().toString().take(6),
                headline = "Protocol Launch: ${venture.cryptoTokenName} unveils ${service.name}!",
                subtitle = "Active utility spikes as new on-chain service goes live",
                body = "${venture.cryptoTokenName} (${venture.cryptoTokenTicker}) has deployed its ${service.name}. Active utility spiked +$userSurge users as token surged +${(service.priceSurgePercent * 100).toInt()}% to $${String.format("%.4f", newPrice)}.",
                author = "Staff Reporter",
                outlet = "CoinDesk & CryptoDaily",
                category = "Market",
                coverImageId = "ic_crypto",
                week = state.currentWeek,
                year = state.currentYear
            )
            showToast("🚀 Launched ${service.name}! Gained +$userSurge active users, token price surged +${(service.priceSurgePercent * 100).toInt()}%.")
            state.copy(
                cash = state.cash - service.devBudgetCost,
                newsArticles = listOf(news) + state.newsArticles.take(19),
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun listCryptoOnExchange(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (venture.cryptoIsMarketListed) {
                showToast("Token is already listed and trading on the Cryptocurrency Market!")
                return@updateState state
            }
            val status = VentureSimulationHelper.checkCryptoListingEligibility(venture)
            if (!status.isEligible) {
                showToast("Listing requirements not met! Requires 1+ Devs, Security Tier 2+, 500+ Users, and $15,000+ Liquidity.")
                return@updateState state
            }
            val tokenId = "crypto_token_" + venture.id
            val existingIndex = state.cryptos.indexOfFirst { it.id == tokenId }
            val listedToken = Crypto(
                id = tokenId,
                name = "${venture.cryptoTokenName} (${venture.cryptoTokenTicker})",
                currentPrice = venture.cryptoTokenPrice,
                quantity = venture.cryptoFounderTokensOwned,
                history = listOf(venture.cryptoTokenPrice * 0.94, venture.cryptoTokenPrice),
                marketCap = venture.cryptoCirculatingSupply * venture.cryptoTokenPrice,
                volume24h = (venture.cryptoWeeklyTradingVolume / 7.0).coerceAtLeast(15000.0),
                supportLevel = venture.cryptoTokenPrice * 0.85,
                resistanceLevel = venture.cryptoTokenPrice * 1.35,
                developerActivity = if (venture.cryptoDevelopersCount >= 2) "High" else "Medium",
                averagePurchasePrice = venture.cryptoTokenPrice,
                initialPrice = venture.cryptoTokenPrice,
                allTimeHigh = venture.cryptoTokenPrice * 1.12,
                cycleFloor = venture.cryptoTokenPrice * 0.50,
                circulatingSupply = venture.cryptoCirculatingSupply
            )
            val updatedCryptos = if (existingIndex >= 0) {
                state.cryptos.toMutableList().apply { set(existingIndex, listedToken) }
            } else {
                state.cryptos + listedToken
            }
            val updatedVenture = venture.copyCrypto(
                cryptoIsMarketListed = true,
                cryptoMarketListingNotice = "🟢 Listed on Cryptocurrency Market as ${venture.cryptoTokenTicker}. Tradable against USD & BTC.",
                cryptoEducationalLesson = "Congratulations! ${venture.cryptoTokenTicker} is officially listed. NPCs and global traders are now actively trading it on the Cryptocurrency Market."
            )
            val news = NewsArticle(
                id = "news_listing_" + java.util.UUID.randomUUID().toString().take(6),
                headline = "EXCHANGE LISTING: ${venture.cryptoTokenName} (${venture.cryptoTokenTicker}) Goes Live!",
                subtitle = "On-chain token meets liquidity covenants and goes live for global trading",
                body = "Decentralized protocol ${venture.cryptoTokenName} has successfully satisfied strict security audit and liquidity covenants. ${venture.cryptoTokenTicker} is now actively trading on the global Cryptocurrency Market!",
                author = "Crypto Desk",
                outlet = "Crypto Markets Today",
                category = "Market",
                coverImageId = "ic_crypto",
                week = state.currentWeek,
                year = state.currentYear
            )
            showToast("🎉 ${venture.cryptoTokenTicker} is now listed on the Cryptocurrency Market! Open Financial Markets to view real-time trading.")
            state.copy(
                cryptos = updatedCryptos,
                newsArticles = listOf(news) + state.newsArticles.take(19),
                businessVentures = state.businessVentures.map { if (it.id == id) updatedVenture else it }
            )
        }
    }

    fun hireCryptoStaff(id: String, role: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val updated = when (role) {
                "Community Moderator" -> venture.copyCrypto(cryptoModCount = venture.cryptoModCount + 1)
                "Solidity Developer" -> venture.copyCrypto(cryptoSolidityDevCount = venture.cryptoSolidityDevCount + 1)
                "Security Auditor" -> venture.copyCrypto(cryptoSecurityAuditorCount = venture.cryptoSecurityAuditorCount + 1)
                "Growth Marketer" -> venture.copyCrypto(cryptoMarketerCount = venture.cryptoMarketerCount + 1)
                else -> venture
            }
            showToast("Onboarded 1 $role for ${venture.name}!")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun fireCryptoStaff(id: String, role: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            var success = false
            val updated = when (role) {
                "Community Moderator" -> if (venture.cryptoModCount > 0) { success = true; venture.copyCrypto(cryptoModCount = venture.cryptoModCount - 1) } else venture
                "Solidity Developer" -> if (venture.cryptoSolidityDevCount > 0) { success = true; venture.copyCrypto(cryptoSolidityDevCount = venture.cryptoSolidityDevCount - 1) } else venture
                "Security Auditor" -> if (venture.cryptoSecurityAuditorCount > 0) { success = true; venture.copyCrypto(cryptoSecurityAuditorCount = venture.cryptoSecurityAuditorCount - 1) } else venture
                "Growth Marketer" -> if (venture.cryptoMarketerCount > 0) { success = true; venture.copyCrypto(cryptoMarketerCount = venture.cryptoMarketerCount - 1) } else venture
                else -> venture
            }
            if (!success) {
                showToast("No $role currently under protocol contract!")
                return@updateState state
            }
            showToast("Terminated 1 $role contract from ${venture.name}.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun injectCryptoLiquidity(id: String, amountUsd: Double) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (state.cash < amountUsd) {
                showToast("Insufficient personal cash! Need $${String.format("%,.0f", amountUsd)} to inject liquidity.")
                return@updateState state
            }
            val updatedLp = venture.cryptoLiquidityPoolUsd + amountUsd
            val updatedSentiment = (venture.cryptoCommunitySentiment + 0.05f).coerceAtMost(1.0f)
            val updated = venture.copy(
                totalCapitalInvested = venture.totalCapitalInvested + amountUsd
            ).copyCrypto(
                cryptoLiquidityPoolUsd = updatedLp,
                cryptoCommunitySentiment = updatedSentiment
            )
            showToast("💧 Injected $${String.format("%,.0f", amountUsd)} into DEX Liquidity Pool! Pool depth: $${String.format("%,.0f", updatedLp)}.")
            state.copy(
                cash = state.cash - amountUsd,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun executeTokenBuybackBurn(id: String, burnAmountUsd: Double) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (state.cash < burnAmountUsd) {
                showToast("Insufficient personal cash! Need $${String.format("%,.0f", burnAmountUsd)} for buyback.")
                return@updateState state
            }
            val tokensBought = if (venture.cryptoTokenPrice > 0) burnAmountUsd / venture.cryptoTokenPrice else 0.0
            val newSupply = (venture.cryptoTotalSupply - tokensBought).coerceAtLeast(100_000.0)
            val newPrice = kotlin.math.round((venture.cryptoTokenPrice * 1.04) * 10000.0) / 10000.0
            val newSentiment = (venture.cryptoCommunitySentiment + 0.08f).coerceAtMost(1.0f)
            val updated = venture.copyCrypto(
                cryptoTotalSupply = newSupply,
                cryptoTokenPrice = newPrice,
                cryptoCommunitySentiment = newSentiment
            )
            showToast("🔥 Bought back and burned ${String.format("%,.0f", tokensBought)} tokens with $${String.format("%,.0f", burnAmountUsd)}! Deflationary supply reduced to ${String.format("%,.0f", newSupply)}.")
            state.copy(
                cash = state.cash - burnAmountUsd,
                businessVentures = state.businessVentures.map { if (it.id == id) updated else it }
            )
        }
    }

    fun applyForVentureScreening(id: String, requestedEquityPercent: Double) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id }
            if (venture == null) {
                showToast("Business not found!")
                return@updateState state
            }
            val fee = if (venture.type == "Crypto Token") 35000.0 else 20000.0
            if (state.cash < fee) {
                showToast("Insufficient personal funds! Regulatory screening fee is $${String.format("%,.0f", fee)}.")
                return@updateState state
            }
            val (eligible, reason) = if (venture.type == "Crypto Token") {
                VentureSimulationHelper.checkGenesisEligibility(venture, state.integrity.toInt())
            } else {
                VentureSimulationHelper.checkWeShareEligibility(venture, state.integrity.toInt(), state.trustworthiness.toInt())
            }
            if (!eligible) {
                showToast("Screening Pre-check Failed: $reason")
                return@updateState state
            }

            val updatedVenture = venture.copyListing(
                screeningStatus = "UNDER_REVIEW",
                screeningWeeksRemaining = 2,
                screeningRequestedEquityPercent = requestedEquityPercent
            )
            val updatedVentures = state.businessVentures.map { if (it.id == id) updatedVenture else it }
            showToast("📑 Submitted ${venture.name} for 2-week regulatory audit! Fee of $${String.format("%,.0f", fee)} paid.")
            state.copy(
                cash = state.cash - fee,
                businessVentures = updatedVentures
            )
        }
    }

    fun launchApprovedVentureIpo(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id }
            if (venture == null) {
                showToast("Enterprise not found!")
                return@updateState state
            }
            if (venture.screeningStatus != "APPROVED") {
                showToast("Cannot launch: Enterprise has not been officially approved by regulators.")
                return@updateState state
            }
            val stockId = "stock_biz_" + venture.id
            val trancheShares = venture.approvedListedSharesUnits.takeIf { it > 0 } ?: (venture.approvedTotalShares * venture.screeningRequestedEquityPercent).toLong()
            val sharePrice = venture.approvedSharePrice.takeIf { it > 0.0 } ?: 10.0
            val newStock = Stock(
                id = stockId,
                name = venture.name,
                currentPrice = sharePrice,
                weeklyDividendPercent = 0.02f,
                sector = when (venture.type) {
                    "Taxi Fleet" -> "Logistics & Transport"
                    "Restaurant" -> "Hospitality & Dining"
                    "Farming" -> "Agriculture & Food"
                    "Hotel", "Hotel Chain" -> "Real Estate & Lodging"
                    "Crypto Token" -> "Decentralized Finance"
                    else -> "Commercial Services"
                },
                quarterlyEarnings = (venture.weeklyNetProfit * 13.0).coerceAtLeast(15000.0),
                PE_Ratio = 8.0,
                marketCap = venture.approvedTotalShares * sharePrice,
                financialHealth = if (venture.weeklyNetProfit > 2500) "Strong" else "Stable",
                initialPrice = sharePrice,
                history = listOf(sharePrice),
                isPlayerCompany = true,
                playerVentureId = venture.id,
                totalIpoShares = trancheShares,
                ipoSharesSold = 0L,
                ipoCompleted = false,
                founderEquityPercent = (1.0 - venture.screeningRequestedEquityPercent).coerceAtLeast(0.1)
            )
            val updatedVenture = venture.copyListing(
                isListedOnWeShare = true,
                weShareListedEquityPercent = venture.screeningRequestedEquityPercent,
                weShareTotalSharesIssued = trancheShares,
                weSharePublicSharesSold = 0L,
                weShareSharePrice = sharePrice,
                weShareStockId = stockId
            )
            val updatedVentures = state.businessVentures.map { if (it.id == id) updatedVenture else it }
            showToast("🚀 Successfully listed ${venture.name} on the public market at $${String.format("%.2f", sharePrice)}/unit!")
            state.copy(
                businessVentures = updatedVentures,
                stocks = state.stocks.filter { it.playerVentureId != venture.id } + newStock
            )
        }
    }

    fun buybackPublicShares(id: String, buybackFractionOfPublic: Double) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id }
            if (venture == null) {
                showToast("Enterprise not found!")
                return@updateState state
            }
            if (!venture.isListedOnWeShare || venture.weSharePublicSharesSold <= 0L) {
                showToast("No outstanding public shares available to buy back!")
                return@updateState state
            }
            val (sharesToBuy, totalCost, equityReclaimed) = VentureSimulationHelper.calculateBuybackQuote(venture, buybackFractionOfPublic)
            if (state.cash < totalCost) {
                showToast("Insufficient personal funds! Required: $${String.format("%,.0f", totalCost)} at current share price ($${String.format("%.2f", venture.weShareSharePrice)}/unit).")
                return@updateState state
            }

            val updatedVenture = venture.copyListing(
                weSharePublicSharesSold = (venture.weSharePublicSharesSold - sharesToBuy).coerceAtLeast(0L),
                weShareTotalSharesIssued = (venture.weShareTotalSharesIssued - sharesToBuy).coerceAtLeast(1L),
                weShareListedEquityPercent = (venture.weShareListedEquityPercent - equityReclaimed).coerceAtLeast(0.0)
            )
            val updatedStocks = state.stocks.map { s ->
                if (s.playerVentureId == id) {
                    s.copy(
                        totalIpoShares = (s.totalIpoShares - sharesToBuy).coerceAtLeast(1L),
                        ipoSharesSold = (s.ipoSharesSold - sharesToBuy).coerceAtLeast(0L),
                        founderEquityPercent = updatedVenture.founderEquityPercent
                    )
                } else s
            }
            val updatedVentures = state.businessVentures.map { if (it.id == id) updatedVenture else it }
            showToast("📈 Buyback executed! Reclaimed ${String.format("%,d", sharesToBuy)} shares (+${String.format("%.1f", equityReclaimed * 100)}% equity) for $${String.format("%,.0f", totalCost)}.")
            state.copy(
                cash = state.cash - totalCost,
                businessVentures = updatedVentures,
                stocks = updatedStocks
            )
        }
    }

    fun listBusinessOnWeShare(id: String, listedPercent: Double) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id }
            if (venture == null) {
                showToast("Business not found!")
                return@updateState state
            }
            if (venture.screeningStatus == "APPROVED") {
                val stockId = "stock_biz_" + venture.id
                val trancheShares = venture.approvedListedSharesUnits.takeIf { it > 0 } ?: (venture.approvedTotalShares * venture.screeningRequestedEquityPercent).toLong()
                val sharePrice = venture.approvedSharePrice.takeIf { it > 0.0 } ?: 10.0
                val newStock = Stock(
                    id = stockId,
                    name = venture.name,
                    currentPrice = sharePrice,
                    weeklyDividendPercent = 0.02f,
                    sector = when (venture.type) {
                        "Taxi Fleet" -> "Logistics & Transport"
                        "Restaurant" -> "Hospitality & Dining"
                        "Farming" -> "Agriculture & Food"
                        "Hotel", "Hotel Chain" -> "Real Estate & Lodging"
                        "Crypto Token" -> "Decentralized Finance"
                        else -> "Commercial Services"
                    },
                    quarterlyEarnings = (venture.weeklyNetProfit * 13.0).coerceAtLeast(15000.0),
                    PE_Ratio = 8.0,
                    marketCap = venture.approvedTotalShares * sharePrice,
                    financialHealth = if (venture.weeklyNetProfit > 2500) "Strong" else "Stable",
                    initialPrice = sharePrice,
                    history = listOf(sharePrice),
                    isPlayerCompany = true,
                    playerVentureId = venture.id,
                    totalIpoShares = trancheShares,
                    ipoSharesSold = 0L,
                    ipoCompleted = false,
                    founderEquityPercent = (1.0 - venture.screeningRequestedEquityPercent).coerceAtLeast(0.1)
                )
                val updatedVenture = venture.copyListing(
                    isListedOnWeShare = true,
                    weShareListedEquityPercent = venture.screeningRequestedEquityPercent,
                    weShareTotalSharesIssued = trancheShares,
                    weSharePublicSharesSold = 0L,
                    weShareSharePrice = sharePrice,
                    weShareStockId = stockId
                )
                val updatedVentures = state.businessVentures.map { if (it.id == id) updatedVenture else it }
                showToast("🚀 Successfully listed ${venture.name} on the public market at $${String.format("%.2f", sharePrice)}/unit!")
                return@updateState state.copy(
                    businessVentures = updatedVentures,
                    stocks = state.stocks.filter { it.playerVentureId != venture.id } + newStock
                )
            }
            if (venture.screeningStatus == "UNDER_REVIEW") {
                showToast("Filing In Progress: ${venture.name} is currently undergoing 2-week regulatory review (${venture.screeningWeeksRemaining} weeks left).")
                return@updateState state
            }
            // If not yet reviewed, submit for 2-week regulatory screening audit ($20,000 fee)
            val fee = if (venture.type == "Crypto Token") 35000.0 else 20000.0
            val totalAvail = state.cash + state.banking.savingsBalance
            if (totalAvail < fee) {
                showToast("Insufficient personal funds! Regulatory screening fee is $${String.format("%,.0f", fee)}.")
                return@updateState state
            }
            val (eligible, reason) = if (venture.type == "Crypto Token") {
                VentureSimulationHelper.checkGenesisEligibility(venture, state.integrity.toInt())
            } else {
                VentureSimulationHelper.checkWeShareEligibility(venture, state.integrity.toInt(), state.trustworthiness.toInt())
            }
            if (!eligible) {
                showToast("Screening Pre-check Failed: $reason")
                return@updateState state
            }

            var deductedCash = state.cash
            var deductedSavings = state.banking.savingsBalance
            if (deductedCash >= fee) {
                deductedCash -= fee
            } else {
                val rem = fee - deductedCash
                deductedCash = 0.0
                deductedSavings = (deductedSavings - rem).coerceAtLeast(0.0)
            }

            val actualPercentRatio = if (listedPercent > 1.0) listedPercent / 100.0 else listedPercent
            val smartVal = VentureSimulationHelper.calculateSmartSccValuation(venture, actualPercentRatio)
            val updatedVenture = venture.copyListing(
                screeningStatus = "UNDER_REVIEW",
                screeningWeeksRemaining = 2,
                screeningRequestedEquityPercent = actualPercentRatio,
                approvedSharePrice = smartVal.sharePrice,
                approvedTotalShares = smartVal.totalSharesIssued,
                approvedListedSharesUnits = smartVal.targetSharesToSell
            )
            val boardName = if (venture.type == "Crypto Token") "Genesis Protocol Listing Council" else "WeShare Listing Qualifications Board"
            val submissionMail = MailMessage(
                id = "audit_sub_" + UUID.randomUUID().toString().take(6),
                sender = boardName,
                subject = "📑 IPO Application Received: ${venture.name} (2-Week Audit Commenced)",
                body = "Dear Founder,\n\nWe acknowledge receipt of your IPO application and regulatory filing fee of $${String.format("%,.0f", fee)} for ${venture.name}.\n\nThe committee has commenced our 2-week operational screening audit. We will audit:\n• Net profit margin consistency (>=15%)\n• Loyal returning customer retention (>=40%)\n• Solvency with zero unresolved commercial debt liabilities\n\nYou will receive official determination email upon conclusion of the audit in 2 weeks.\n\nRespectfully,\nListing Qualifications Board, $boardName",
                type = "NORMAL",
                expiresWeek = state.currentWeek + 6,
                expiresYear = state.currentYear
            )
            val updatedVentures = state.businessVentures.map { if (it.id == id) updatedVenture else it }
            showToast("📑 Submitted ${venture.name} for 2-week regulatory audit! Fee of $${String.format("%,.0f", fee)} paid.")
            state.copy(
                cash = deductedCash,
                banking = state.banking.copy(savingsBalance = deductedSavings),
                businessVentures = updatedVentures,
                mailbox = state.mailbox + submissionMail
            )
        }
    }

    fun acceptVentureInvestorOffer(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id }
            val offer = venture?.pendingInvestorOffer
            if (venture == null || offer == null) {
                showToast("No active investor offer!")
                return@updateState state
            }
            val currentTaxRate = (0.12f + state.politicalTaxModifier).coerceIn(0.01f, 0.35f)

            if (offer.offerType == "BUYOUT") {
                val buyoutAmt = offer.cashAmount
                val taxWithheld = (buyoutAmt * currentTaxRate).roundToInt().toDouble()
                val netCashPayout = buyoutAmt - taxWithheld

                showToast("🤝 Acquired! Sold 100% of ${venture.name} to ${offer.investorName} for $${String.format("%,.0f", buyoutAmt)} ($${String.format("%,.0f", taxWithheld)} State Tax withheld, $${String.format("%,.0f", netCashPayout)} net credited)!")
                val updatedBiz = state.businessVentures.map { v ->
                    if (v.id == id) {
                        v.copy(
                            isOwned = false,
                            investorEquityPercent = 1.0,
                            investorName = offer.investorName,
                            pendingInvestorOffer = null
                        )
                    } else v
                }
                val updatedStocks = state.stocks.map { s ->
                    if (s.playerVentureId == id) {
                        s.copy(
                            isPlayerCompany = false,
                            founderEquityPercent = 0.0
                        )
                    } else s
                }
                val updatedMail = state.mailbox.filter { it.ventureId != id }
                state.copy(
                    cash = state.cash + netCashPayout,
                    businessVentures = updatedBiz,
                    stocks = updatedStocks,
                    mailbox = updatedMail
                )
            } else {
                val cashAmt = offer.cashAmount
                val eqPct = offer.equityPercent
                val taxWithheld = (cashAmt * currentTaxRate).roundToInt().toDouble()
                val netPersonalCash = cashAmt - taxWithheld

                val palette = listOf("#F59E0B", "#06B6D4", "#10B981", "#F43F5E", "#8B5CF6", "#EC4899", "#3B82F6")
                var controlMsg = ""
                val updatedBiz = state.businessVentures.map { v ->
                    if (v.id == id) {
                        val chosenColor = palette[v.investors.size % palette.size]
                        val newShareholder = VentureInvestorShareholder(
                            name = offer.investorName,
                            firm = offer.investorFirm,
                            equityPercent = eqPct,
                            colorHex = chosenColor
                        )
                        val newInvestors = v.investors + newShareholder
                        val newTotalInvestorEquity = newInvestors.sumOf { it.equityPercent }
                        val newFounderEquity = (1.0 - newTotalInvestorEquity - (if (v.isListedOnWeShare) v.weShareListedEquityPercent else 0.0)).coerceIn(0.0, 1.0)
                        val lostControl = v.isPlayerInControl && (newFounderEquity < 0.50 || newInvestors.any { it.equityPercent > newFounderEquity })
                        if (lostControl) {
                            controlMsg = " You now hold ${(newFounderEquity * 100).toInt()}% equity. Controlling interest relinquished to lead investors! 10 time slots returned to your schedule."
                        }
                        v.copy(
                            investors = newInvestors,
                            investorEquityPercent = newTotalInvestorEquity,
                            investorName = offer.investorName,
                            accumulatedProfit = v.accumulatedProfit + cashAmt,
                            pendingInvestorOffer = null
                        )
                    } else v
                }
                showToast("🤝 Deal Closed! Sold ${(eqPct * 100).toInt()}% equity in ${venture.name} to ${offer.investorName} for $${String.format("%,.0f", cashAmt)} (credited to Withdrawable Profits).$controlMsg")
                val updatedMail = state.mailbox.filter { it.ventureId != id }
                state.copy(
                    cash = state.cash,
                    businessVentures = updatedBiz,
                    mailbox = updatedMail
                )
            }
        }
    }

    fun declineVentureInvestorOffer(id: String) {
        updateState { state ->
            val updatedBiz = state.businessVentures.map { v ->
                if (v.id == id) v.copy(pendingInvestorOffer = null) else v
            }
            val updatedMail = state.mailbox.filter { it.ventureId != id }
            showToast("Declined investor offer.")
            state.copy(businessVentures = updatedBiz, mailbox = updatedMail)
        }
    }

    fun sellVenture(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id && it.isOwned }
            if (venture == null) {
                showToast("Business venture not found or not owned!")
                return@updateState state
            }
            val grossVal = VentureSimulationHelper.getGrossEnterpriseValuation(venture)
            val playerEquity = venture.founderEquityPercent
            val grossPlayerPayout = grossVal * playerEquity

            val ventureLoans = state.activeLoanContracts.filter { it.ventureId == id }
            val totalBusinessDebt = if (ventureLoans.isNotEmpty()) {
                ventureLoans.sumOf { it.remainingBalance }
            } else {
                venture.outstandingLoan
            }

            val remainingContracts = state.activeLoanContracts.filter { it.ventureId != id }.toMutableList()
            val netPayout: Double
            val msg: String

            if (grossPlayerPayout >= totalBusinessDebt) {
                netPayout = grossPlayerPayout - totalBusinessDebt
                msg = if (totalBusinessDebt > 0.0) {
                    "🏢 Sold ${(playerEquity * 100).toInt()}% stake in ${venture.name} for $${String.format("%,.0f", grossPlayerPayout)}. Bank deducted $${String.format("%,.0f", totalBusinessDebt)} to settle business loan. You received the remaining $${String.format("%,.0f", netPayout)}!"
                } else {
                    "🏢 Sold ${(playerEquity * 100).toInt()}% stake in ${venture.name} for $${String.format("%,.0f", netPayout)}!"
                }
            } else {
                val shortfall = totalBusinessDebt - grossPlayerPayout
                netPayout = 0.0
                val rolloverContract = BankLoanContract(
                    id = "loan_per_rollover_" + UUID.randomUUID().toString().take(6),
                    borrowerType = "PERSONAL",
                    ventureId = null,
                    ventureName = "Debt Rollover from ${venture.name}",
                    principal = shortfall,
                    durationWeeks = 12,
                    durationLabel = "3 Months",
                    totalRepayable = shortfall,
                    remainingBalance = shortfall,
                    takenWeek = state.currentWeek,
                    takenYear = state.currentYear,
                    dueWeek = ((state.currentWeek + 12 - 1) % 52) + 1,
                    dueYear = state.currentYear + ((state.currentWeek + 12 - 1) / 52),
                    weeksRemaining = 12,
                    isOverdue = false,
                    weeklyLatePenaltyPercent = 0.04f
                )
                remainingContracts.add(rolloverContract)
                msg = "🏢 Sold ${(playerEquity * 100).toInt()}% stake in ${venture.name} for $${String.format("%,.0f", grossPlayerPayout)}. Bank recouped all sale proceeds toward debt ($${String.format("%,.0f", totalBusinessDebt)}). Shortfall of $${String.format("%,.0f", shortfall)} transferred to PERSONAL LOAN and must be paid!"
            }

            val updatedBiz = state.businessVentures.filter { it.id != id }
            val updatedMail = state.mailbox.filter { it.ventureId != id }

            val freedSlots = if (venture.isPlayerInControl) 10 else 0
            val slotsMsg = if (freedSlots > 0) " (+10 Time Slots returned)" else ""
            showToast("$msg$slotsMsg")

            val activePersonalLoans = remainingContracts.filter { it.borrowerType == "PERSONAL" }.sumOf { it.remainingBalance }

            state.copy(
                cash = state.cash + netPayout,
                businessVentures = updatedBiz,
                activeLoanContracts = remainingContracts,
                banking = state.banking.copy(loanAmount = activePersonalLoans),
                mailbox = updatedMail
            )
        }
    }

    fun investBusinessCapital(id: String, amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash!")
                return@updateState state
            }
            val ventures = state.businessVentures.map {
                if (it.id == id) it.copy(investedCapital = it.investedCapital + amount) else it
            }
            showToast("Deposited $${String.format("%,.0f", amount)} into business treasury.")
            state.copy(cash = state.cash - amount, businessVentures = ventures)
        }
    }

    fun withdrawBusinessFunds(id: String, amount: Double) {
        updateState { state ->
            var profitWithdrawn = 0.0
            val ventures = state.businessVentures.map {
                if (it.id == id) {
                    val available = it.accumulatedProfit.coerceAtLeast(0.0)
                    if (amount > available || available <= 0.0) {
                        showToast("Cannot withdraw that much! Only business net profits can be withdrawn, not invested treasury capital.")
                        return@updateState state
                    }
                    val withdrawAmt = amount.coerceAtMost(available)
                    profitWithdrawn = withdrawAmt
                    val newProfit = (it.accumulatedProfit - withdrawAmt).coerceAtLeast(0.0)

                    it.copy(
                        accumulatedProfit = newProfit,
                        totalWithdrawn = it.totalWithdrawn + withdrawAmt
                    )
                } else it
            }

            val currentTaxRate = (0.12f + state.politicalTaxModifier).coerceIn(0.01f, 0.35f)
            val taxDue = profitWithdrawn * currentTaxRate
            val titheDue = profitWithdrawn * 0.10

            showToast("Withdrew $${String.format("%,.0f", amount)} profit from business ($${String.format("%,.0f", taxDue)} tax & $${String.format("%,.0f", titheDue)} tithe accrued).")
            state.copy(
                cash = state.cash + amount,
                businessVentures = ventures,
                banking = state.banking.copy(
                    taxDebt = state.banking.taxDebt + taxDue,
                    titheDebt = state.banking.titheDebt + titheDue
                )
            )
        }
    }

    // --- Enterprise Staffing, Marketing & SCC Legal Operations ---
    fun hireTaxiMechanic(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val count = venture.taxiMechanicCount + 1
            val updated = venture.copyTaxi(taxiMechanicCount = count)
            showToast("🔧 Hired Fleet Mechanic #$count ($550/wk). Reduces vehicle wear and lowers maintenance costs!")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun fireTaxiMechanic(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (venture.taxiMechanicCount <= 0) {
                showToast("No mechanics currently on payroll!")
                return@updateState state
            }
            val count = venture.taxiMechanicCount - 1
            val updated = venture.copyTaxi(taxiMechanicCount = count)
            showToast("Dismissed 1 mechanic. Active mechanics: $count.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun hireTaxiDispatcher(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val count = venture.taxiDispatcherCount + 1
            val updated = venture.copyTaxi(taxiDispatcherCount = count)
            showToast("📡 Hired Fleet Dispatcher #$count ($450/wk). Maximizes ride dispatch efficiency and captures recurring ride bookings!")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun fireTaxiDispatcher(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            if (venture.taxiDispatcherCount <= 0) {
                showToast("No dispatchers currently on payroll!")
                return@updateState state
            }
            val count = venture.taxiDispatcherCount - 1
            val updated = venture.copyTaxi(taxiDispatcherCount = count)
            showToast("Dismissed 1 dispatcher. Active dispatchers: $count.")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun setVentureMarketingCampaign(id: String, campaign: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val updated = venture.copyGovernance(marketingCampaign = campaign)
            val label = when (campaign) {
                "LOCAL_ADS" -> "Local Print & Radio ($150/wk)"
                "DIGITAL_ADS" -> "Targeted Digital Ads ($450/wk)"
                "PR_BLITZ" -> "National PR Blitz ($1,200/wk)"
                else -> "None ($0/wk)"
            }
            showToast("📢 Marketing campaign updated for ${venture.name}: $label")
            state.copy(businessVentures = state.businessVentures.map { if (it.id == id) updated else it })
        }
    }

    fun acquireVentureLegalLicense(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val cost = if (venture.legalLicenseCost > 0.0) venture.legalLicenseCost else 15000.0

            // Prefer paying from business treasury if available, else from player cash
            val payFromTreasury = venture.investedCapital >= cost
            val payFromCash = !payFromTreasury && state.cash >= cost

            if (!payFromTreasury && !payFromCash) {
                showToast("Insufficient capital to acquire Commercial Operating License ($${String.format("%,.0f", cost)})! Fund the company treasury or pay with personal cash.")
                return@updateState state
            }

            val updatedVentures = state.businessVentures.map { v ->
                if (v.id == id) {
                    val newCap = if (payFromTreasury) v.investedCapital - cost else v.investedCapital
                    v.copy(
                        investedCapital = newCap
                    ).copyGovernance(
                        hasLegalLicense = true,
                        sccWarningReceived = false,
                        sccDeadlineWeeks = 0,
                        isShutDownByRegulators = false
                    )
                } else v
            }
            val newCash = if (!payFromTreasury && payFromCash) state.cash - cost else state.cash
            val updatedMail = state.mailbox.filter { it.ventureId != id || it.type != "SCC_LICENSE_WARNING" }

            showToast("📜 Acquired State Commercial Operating & Enterprise License for ${venture.name}! Full regulatory compliance secured (-3% energy).")
            state.copy(
                cash = newCash,
                energy = (state.energy - 3).coerceAtLeast(0),
                businessVentures = updatedVentures,
                mailbox = updatedMail
            )
        }
    }

    fun settleInvestorLawsuit(id: String) {
        updateState { state ->
            val venture = state.businessVentures.find { it.id == id } ?: return@updateState state
            val cost = venture.investorLawsuitDamages
            if (state.cash < cost) {
                showToast("Insufficient personal funds to settle lawsuit ($${String.format("%,.0f", cost)})!")
                return@updateState state
            }
            val updatedVentures = state.businessVentures.map { v ->
                if (v.id == id) {
                    v.copyGovernance(
                        investorLawsuitActive = false,
                        investorLawsuitDamages = 0.0,
                        pocketedInvestorCapital = 0.0
                    )
                } else v
            }
            val updatedMail = state.mailbox.filter { it.ventureId != id || it.type != "INVESTOR_LAWSUIT_NOTICE" }
            showToast("⚖️ Settled shareholder derivative lawsuit for $${String.format("%,.0f", cost)}. Full fiduciary discharge granted.")
            state.copy(
                cash = state.cash - cost,
                businessVentures = updatedVentures,
                mailbox = updatedMail
            )
        }
    }

    // --- Structured Realistic Loans (Personal & Business) ---
    fun applyForStructuredLoan(amount: Double, durationWeeks: Int, borrowerType: String, ventureId: String? = null) {
        updateState { state ->
            val multiplier = when (durationWeeks) {
                4 -> 1.134
                12 -> 1.44
                26 -> 1.85
                52 -> 2.60
                else -> 1.134
            }
            val totalRepayable = (amount * multiplier).roundToInt().toDouble()

            var dueWk = state.currentWeek + durationWeeks
            var dueYr = state.currentYear
            while (dueWk > 52) {
                dueWk -= 52
                dueYr += 1
            }

            val durationLabel = when (durationWeeks) {
                4 -> "1 Month"
                12 -> "3 Months"
                26 -> "6 Months"
                52 -> "1 Year"
                else -> "$durationWeeks Weeks"
            }

            val isBusiness = borrowerType.uppercase() in listOf("BUSINESS", "COMMERCIAL")
            if (isBusiness) {
                val targetVenture = state.businessVentures.find { it.id == ventureId && it.isOwned }
                if (targetVenture == null || !targetVenture.isPlayerInControl) {
                    showToast("Commercial credit denied! You must have controlling interest (≥50% equity) in an active business to borrow in its name.")
                    return@updateState state
                }
                val maxLoanAllowed = state.banking.businessLoanEligibility
                if (amount > maxLoanAllowed) {
                    showToast("Commercial credit denied! Max credit limit for ${targetVenture.name} is $${String.format("%,.0f", maxLoanAllowed)}.")
                    return@updateState state
                }

                val contract = BankLoanContract(
                    id = "loan_biz_" + UUID.randomUUID().toString().take(6),
                    borrowerType = "BUSINESS",
                    ventureId = targetVenture.id,
                    ventureName = targetVenture.name,
                    principal = amount,
                    durationWeeks = durationWeeks,
                    durationLabel = durationLabel,
                    totalRepayable = totalRepayable,
                    remainingBalance = totalRepayable,
                    takenWeek = state.currentWeek,
                    takenYear = state.currentYear,
                    dueWeek = dueWk,
                    dueYear = dueYr,
                    weeksRemaining = durationWeeks,
                    isOverdue = false,
                    weeklyLatePenaltyPercent = 0.04f
                )

                val updatedVentures = state.businessVentures.map { v ->
                    if (v.id == targetVenture.id) {
                        v.copy(
                            accumulatedProfit = v.accumulatedProfit + amount,
                            outstandingLoan = v.outstandingLoan + amount
                        )
                    } else v
                }

                showToast("🏦 Commercial Loan of $${String.format("%,.0f", amount)} disbursed to ${targetVenture.name} Withdrawable Profits! Total repayable: $${String.format("%,.0f", totalRepayable)} (including interest) over $durationLabel.")
                state.copy(
                    businessVentures = updatedVentures,
                    activeLoanContracts = state.activeLoanContracts + contract
                )
            } else {
                val maxPersonal = state.banking.personalLoanEligibility
                if (amount > maxPersonal) {
                    showToast("Personal credit limit reached! Maximum allowed: $${String.format("%,.0f", maxPersonal)}")
                    return@updateState state
                }

                val contract = BankLoanContract(
                    id = "loan_per_" + UUID.randomUUID().toString().take(6),
                    borrowerType = "PERSONAL",
                    ventureId = null,
                    ventureName = null,
                    principal = amount,
                    durationWeeks = durationWeeks,
                    durationLabel = durationLabel,
                    totalRepayable = totalRepayable,
                    remainingBalance = totalRepayable,
                    takenWeek = state.currentWeek,
                    takenYear = state.currentYear,
                    dueWeek = dueWk,
                    dueYear = dueYr,
                    weeksRemaining = durationWeeks,
                    isOverdue = false,
                    weeklyLatePenaltyPercent = 0.04f
                )

                showToast("🏦 Personal Loan of $${String.format("%,.0f", amount)} disbursed! Total repayable: $${String.format("%,.0f", totalRepayable)} (including interest) over $durationLabel.")
                state.copy(
                    cash = state.cash + amount,
                    banking = state.banking.copy(loanAmount = state.loanAmount + totalRepayable),
                    activeLoanContracts = state.activeLoanContracts + contract
                )
            }
        }
    }

    fun repayStructuredLoan(loanId: String, paymentAmount: Double) {
        updateState { state ->
            val contract = state.activeLoanContracts.find { it.id == loanId }
            if (contract == null) {
                showToast("Loan contract not found!")
                return@updateState state
            }
            if (state.cash < paymentAmount) {
                showToast("Insufficient cash reserves to make repayment!")
                return@updateState state
            }

            val actualPayment = paymentAmount.coerceAtMost(contract.remainingBalance)
            val newBalance = (contract.remainingBalance - actualPayment).coerceAtLeast(0.0)
            val isCleared = newBalance <= 0.0

            var updatedBiz = state.businessVentures
            if (contract.borrowerType in listOf("BUSINESS", "COMMERCIAL") && contract.ventureId != null) {
                val principalRatio = contract.principal / contract.totalRepayable
                val principalDeduction = if (isCleared) contract.principal else actualPayment * principalRatio
                updatedBiz = state.businessVentures.map { v ->
                    if (v.id == contract.ventureId) {
                        v.copy(outstandingLoan = (v.outstandingLoan - principalDeduction).coerceAtLeast(0.0))
                    } else v
                }
            }

            val updatedContracts = if (isCleared) {
                state.activeLoanContracts.filter { it.id != loanId }
            } else {
                state.activeLoanContracts.map { if (it.id == loanId) it.copy(remainingBalance = newBalance) else it }
            }

            val weeksElapsed = ((state.currentYear - contract.takenYear) * 52 + (state.currentWeek - contract.takenWeek)).coerceAtLeast(1)
            val speedFactor = (1.0 - (weeksElapsed.toDouble() / contract.durationWeeks.toDouble())).coerceIn(0.0, 1.0)

            val updatedBanking = if (contract.borrowerType == "PERSONAL") {
                val newEligibility = if (isCleared) {
                    val increase = 1000.0 + (9000.0 * speedFactor)
                    state.banking.personalLoanEligibility + increase
                } else {
                    state.banking.personalLoanEligibility
                }
                state.banking.copy(
                    loanAmount = (state.loanAmount - actualPayment).coerceAtLeast(0.0),
                    personalLoanEligibility = newEligibility
                )
            } else {
                val newEligibility = if (isCleared) {
                    val increase = 5000.0 + (10000.0 * speedFactor)
                    state.banking.businessLoanEligibility + increase
                } else {
                    state.banking.businessLoanEligibility
                }
                state.banking.copy(
                    businessLoanEligibility = newEligibility
                )
            }

            if (isCleared) {
                val nextLimit = if (contract.borrowerType == "PERSONAL") updatedBanking.personalLoanEligibility else updatedBanking.businessLoanEligibility
                val typeName = if (contract.borrowerType == "PERSONAL") "Personal" else "Business"
                showToast("🎉 $typeName Loan settled in $weeksElapsed weeks! Next borrowing eligibility increased to $${String.format("%,.0f", nextLimit)}.")
            } else {
                showToast("Repaid $${String.format("%,.0f", actualPayment)}. Remaining balance: $${String.format("%,.0f", newBalance)}.")
            }

            state.copy(
                cash = state.cash - actualPayment,
                banking = updatedBanking,
                businessVentures = updatedBiz,
                activeLoanContracts = updatedContracts
            )
        }
    }

    // --- Recruitment Hall Hiring ---
    fun recruitProfessional(role: String, name: String, salary: Double) {
        updateState { state ->
            val existingCount = state.hiredEmployees.count { it.role == role }
            if (existingCount >= 1 && (role == "Accountant" || role == "Business Manager" || role == "Lawyer" || role == "PR Manager" || role == "Personal Assistant" || role == "Social Media Manager")) {
                showToast("You already have an active $role!")
                return@updateState state
            }

            val traits = when (role) {
                "Accountant" -> listOf("Meticulous Tax-Wizard", "Strictly Compliant Optimizer", "Secretly Greedy Embezzler").random()
                "Financial Advisor" -> listOf("Conservative Growth Planner", "Undervalued Stock Specialist", "High-Risk Speculator").random()
                "Crypto Analyst" -> listOf("Bull Run Enthusiast", "Pragmatic Volatility Critic", "Rogue Pump-and-Dump Scammer").random()
                "Talent Agent" -> listOf("Aggressive Hollywood Negotiator", "Elite Industry Liaison", "Sponsor Magnet").random()
                "PR Manager" -> listOf("Master Spin Doctor", "Proactive Damage Controller", "Blunder-Prone Amateur").random()
                "Business Manager" -> listOf("Efficiency Expert", "Ruthless Cost Cutter", "Coordinated Growth Guru").random()
                "Lawyer" -> listOf("Ironclad Contract Shark", "Expert Class-Action Settler", "Meticulous Copyright Defender").random()
                "Tour Manager" -> listOf("Logistics Virtuoso", "Sober Roadie Legend", "Penny-Pincher Planner").random()
                "Music Producer" -> listOf("Hit-making Studio Purist", "Experimental Soundscape Innovator", "Gold-certified Hitmaker").random()
                "Personal Assistant" -> listOf("Super Organized Planner", "Strict Message Filterer", "Quiet Comforting Assistant").random()
                "Social Media Manager" -> listOf("Viral Meme Crafter", "24/7 Trend Hijacker", "Engagement Farming Pioneer").random()
                "Security" -> "Formidable (Protects you from stressors and emergencies)"
                else -> "Diligent & Helpful Employee"
            }

            val initialPermissions = emptyMap<String, Boolean>()

            val matchingCandidate = state.candidateRoster.find { it.role == role && it.name == name }
            val candidateRating = matchingCandidate?.rating ?: 4.0f
            val candidateStyle = matchingCandidate?.investorStyle ?: "Balanced"

            val experience = matchingCandidate?.experience ?: Random.nextInt(40, 95)
            val reliability = matchingCandidate?.reliability ?: Random.nextInt(50, 98).toFloat()
            val honesty = matchingCandidate?.honesty ?: Random.nextInt(30, 100).toFloat()
            val greed = matchingCandidate?.greed ?: Random.nextInt(15, 85).toFloat()
            val intelligence = matchingCandidate?.intelligence ?: Random.nextInt(45, 98).toFloat()
            val leadership = matchingCandidate?.leadership ?: Random.nextInt(30, 92).toFloat()
            val communication = matchingCandidate?.communication ?: Random.nextInt(45, 95).toFloat()
            val efficiency = matchingCandidate?.efficiency ?: Random.nextInt(45, 95).toFloat()
            val negotiation = matchingCandidate?.negotiation ?: Random.nextInt(40, 95).toFloat()
            val ambition = greed // Ambition is correlated with greed

            val finalTraitDesc = matchingCandidate?.traitDescription?.takeIf { it.isNotEmpty() } ?: traits
            val newEmp = Employee(
                id = UUID.randomUUID().toString().take(6),
                name = name,
                role = role,
                weeklySalary = matchingCandidate?.weeklySalary ?: salary,
                experience = experience,
                reliability = reliability,
                honesty = honesty,
                greed = greed,
                intelligence = intelligence,
                leadership = leadership,
                loyalty = 75f,
                traitDescription = finalTraitDesc,
                age = Random.nextInt(22, 58),
                contractWeeks = Random.nextInt(8, 26),
                integrity = honesty, // integrity starts matching honesty
                communication = communication,
                efficiency = efficiency,
                negotiation = negotiation,
                ambition = ambition,
                stressLevel = 10f,
                happiness = 85f,
                workload = 20f,
                reputation = Random.nextInt(50, 90).toFloat(),
                careerHistory = listOf("Week ${state.currentWeek}: Recruited by ${state.artistName} as $role!"),
                permissions = initialPermissions,
                rating = candidateRating,
                investorStyle = candidateStyle
            )

            val currentHired = if (role == "Crypto Analyst") {
                state.hiredEmployees.filterNot { it.role.equals("Crypto Analyst", ignoreCase = true) }
            } else {
                state.hiredEmployees
            }
            showToast("Welcome aboard, ${newEmp.name} has joined as your $role!")
            state.copy(hiredEmployees = currentHired + newEmp)
        }
    }

    fun fireProfessional(empId: String) {
        updateState { state ->
            val emp = state.hiredEmployees.find { it.id == empId }
            if (emp != null) {
                showToast("Dismissed ${emp.name} from their position as ${emp.role}.")
            }
            val remaining = state.hiredEmployees.filter { it.id != empId }
            val hasCrypto = remaining.any {
                it.role.equals("Crypto Analyst", ignoreCase = true) ||
                (it.role.contains("Crypto", ignoreCase = true) && it.role.contains("Analyst", ignoreCase = true)) ||
                (it.role.contains("Analyst", ignoreCase = true) && !it.role.contains("Financial", ignoreCase = true))
            }
            val hasAccountant = remaining.any { it.role.equals("Accountant", ignoreCase = true) || it.role.contains("Accountant", ignoreCase = true) }
            val hasSMM = remaining.any { it.role.contains("Social", ignoreCase = true) || it.role.contains("SMM", ignoreCase = true) }
            val newWealth = if (!hasSMM) state.wealth.copy(smmAutoPost = false) else state.wealth
            state.copy(
                hiredEmployees = remaining,
                cryptoAutoInvestEnabled = if (!hasCrypto) false else state.cryptoAutoInvestEnabled,
                accountantAutoInvestEnabled = if (!hasAccountant) false else state.accountantAutoInvestEnabled,
                wealth = newWealth
            )
        }
    }

    fun rewardProfessionalBonus(empId: String) {
        updateState { state ->
            val bonusCost = 500.0
            if (state.cash < bonusCost) {
                showToast("Insufficient reserves to pay bonus.")
                return@updateState state
            }
            val updated = state.hiredEmployees.map { emp ->
                if (emp.id == empId) {
                    showToast("Paid $500 bonus to ${emp.name}! Loyalty increased.")
                    val updatedHistory = emp.careerHistory + "Week ${state.currentWeek}: Received a $500 cash performance bonus!"
                    emp.copy(
                        loyalty = (emp.loyalty + 20f).coerceAtMost(100f),
                        happiness = (emp.happiness + 15f).coerceAtMost(100f),
                        careerHistory = updatedHistory
                    )
                } else emp
            }
            state.copy(
                cash = state.cash - bonusCost,
                hiredEmployees = updated
            )
        }
    }

    fun promoteEmployee(empId: String) {
        updateState { state ->
            val updated = state.hiredEmployees.map { emp ->
                if (emp.id == empId) {
                    val salaryIncrease = emp.weeklySalary * 0.15
                    val newSalary = emp.weeklySalary + salaryIncrease
                    showToast("Promoted ${emp.name}! Salary increased to $${String.format("%.0f", newSalary)}/wk.")
                    val updatedHistory = emp.careerHistory + "Week ${state.currentWeek}: Promoted! Salary increased to $${String.format("%.0f", newSalary)}/wk."
                    emp.copy(
                        weeklySalary = newSalary,
                        loyalty = (emp.loyalty + 15f).coerceAtMost(100f),
                        experience = (emp.experience + 5).coerceAtMost(100),
                        negotiation = (emp.negotiation + 8f).coerceAtMost(100f),
                        reputation = (emp.reputation + 6f).coerceAtMost(100f),
                        happiness = (emp.happiness + 20f).coerceAtMost(100f),
                        careerHistory = updatedHistory
                    )
                } else emp
            }
            state.copy(hiredEmployees = updated)
        }
    }

    fun demoteEmployee(empId: String) {
        updateState { state ->
            val updated = state.hiredEmployees.map { emp ->
                if (emp.id == empId) {
                    val salaryDecrease = emp.weeklySalary * 0.15
                    val newSalary = (emp.weeklySalary - salaryDecrease).coerceAtLeast(100.0)
                    showToast("Demoted ${emp.name}. Salary decreased to $${String.format("%.0f", newSalary)}/wk.")
                    val updatedHistory = emp.careerHistory + "Week ${state.currentWeek}: Demoted. Salary decreased to $${String.format("%.0f", newSalary)}/wk."
                    emp.copy(
                        weeklySalary = newSalary,
                        loyalty = (emp.loyalty - 25f).coerceAtLeast(0f),
                        happiness = (emp.happiness - 30f).coerceAtLeast(0f),
                        careerHistory = updatedHistory
                    )
                } else emp
            }
            state.copy(hiredEmployees = updated)
        }
    }

    fun trainEmployee(empId: String) {
        updateState { state ->
            val targetEmp = state.hiredEmployees.find { it.id == empId } ?: return@updateState state
            val isFinancialSpecialist = targetEmp.role.contains("Crypto", ignoreCase = true) ||
                targetEmp.role.contains("Accountant", ignoreCase = true) ||
                targetEmp.role.contains("Financial", ignoreCase = true)
            val trainingCost = if (isFinancialSpecialist) 8500.0 else 4500.0

            if (state.cash < trainingCost) {
                showToast("Insufficient liquid reserves for specialized training ($${String.format("%,.0f", trainingCost)}).")
                return@updateState state
            }

            var celebrationToast: String? = null
            val updated = state.hiredEmployees.map { emp ->
                if (emp.id == empId) {
                    val wasAlreadyStubborn = emp.hasStubbornTrait
                    val newExp = (emp.experience + 10).coerceAtMost(100)
                    val newIntel = (emp.intelligence + 10f).coerceAtMost(100f)
                    val newRel = (emp.reliability + 8f).coerceAtMost(100f)
                    val newEff = (emp.efficiency + 10f).coerceAtMost(100f)
                    val newComm = (emp.communication + 8f).coerceAtMost(100f)
                    val newRating = (emp.rating + 0.35f).coerceAtMost(5.0f)
                    val calculatedSkill = ((newExp * 0.40f) + (newIntel * 0.30f) + (newRel * 0.20f) + (emp.leadership * 0.10f)).toInt().coerceIn(20, 99)

                    val unlockedStubborn = (calculatedSkill >= 80 || newRating >= 4.0f)
                    val updatedTrait = if (unlockedStubborn && !emp.traitDescription.contains("Stubborn")) {
                        emp.traitDescription + " · ★ Stubborn Market Vision (80%+)"
                    } else emp.traitDescription

                    val updatedHistory = emp.careerHistory + "Week ${state.currentWeek}: Completed intensive $${String.format("%,.0f", trainingCost)} executive masterclass (Skill: $calculatedSkill%, Rating: ${String.format("%.1f", newRating)}★)."

                    if (unlockedStubborn && !wasAlreadyStubborn) {
                        celebrationToast = "🎉 ${emp.name} reached ${calculatedSkill}% rating! Unlocked the Stubborn Trait & 70/30 Market Forecasting!"
                    } else {
                        celebrationToast = "Enrolled ${emp.name} in masterclass ($${String.format("%,.0f", trainingCost)}). Skill is now $calculatedSkill% (${String.format("%.1f", newRating)}★)."
                    }

                    emp.copy(
                        efficiency = newEff,
                        intelligence = newIntel,
                        communication = newComm,
                        experience = newExp,
                        reliability = newRel,
                        rating = newRating,
                        traitDescription = updatedTrait,
                        careerHistory = updatedHistory
                    )
                } else emp
            }

            celebrationToast?.let { showToast(it) }

            state.copy(
                cash = state.cash - trainingCost,
                hiredEmployees = updated
            )
        }
    }

    fun toggleEmployeePermission(empId: String, permKey: String, enabled: Boolean) {
        updateState { state ->
            val updated = state.hiredEmployees.map { emp ->
                if (emp.id == empId) {
                    val newPerms = emp.permissions.toMutableMap()
                    newPerms[permKey] = enabled
                    showToast("${emp.name}'s permission for '$permKey' set to $enabled.")
                    emp.copy(permissions = newPerms)
                } else emp
            }
            state.copy(hiredEmployees = updated)
        }
    }

    // --- Spiritual Life Core Systems ---
    fun pray() {
        updateState { state ->
            if (state.energy < 10) {
                showToast("Not enough energy to pray. Take some rest.")
                return@updateState state
            }
            showToast("Spent quiet time in prayer. Gained inner peace, discipline, and humility.")
            state.copy(
                energy = state.energy - 10,
                discipline = (state.discipline + 2.5f).coerceAtMost(100f),
                humility = (state.humility + 3.0f).coerceAtMost(100f),
                stress = (state.stress - 12f).coerceAtLeast(0f)
            )
        }
    }

    fun titheCash(amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash reserves to tithe $${String.format("%,.0f", amount)}")
                return@updateState state
            }
            val repayAmount = amount.coerceAtMost(state.titheDebt)
            showToast("Paid a tithe of $${String.format("%,.0f", amount)}. Gained generosity & integrity.")
            state.copy(
                cash = state.cash - amount,
                generosity = (state.generosity + 4.5f).coerceAtMost(100f),
                integrity = (state.integrity + 3.5f).coerceAtMost(100f),
                trustworthiness = (state.trustworthiness + 5.0f).coerceAtMost(100f),
                spiritual = state.spiritual.copy(spiritualTotalTithesPaid = state.spiritualTotalTithesPaid + amount),
                banking = state.banking.copy(titheDebt = (state.titheDebt - repayAmount).coerceAtLeast(0.0))
            )
        }
    }

    fun donateToCharity(amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash reserves for this donation.")
                return@updateState state
            }
            showToast("Donated $${String.format("%,.0f", amount)} to Charity & Service. Gained massive public image.")
            state.copy(
                cash = state.cash - amount,
                generosity = (state.generosity + 6.0f).coerceAtMost(100f),
                humility = (state.humility + 4.0f).coerceAtMost(100f),
                publicImage = (state.publicImage + 8.5f).coerceAtMost(100f)
            )
        }
    }

    fun addAdCompanyBudget(amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash reserves to add funds!")
                return@updateState state
            }
            showToast("Added $${String.format("%,.0f", amount)} to Advertising Budget!")
            state.copy(
                cash = state.cash - amount,
                adCompanyBudget = state.adCompanyBudget + amount
            )
        }
    }

    fun haltAdCampaign(campaignId: String) {
        updateState { state ->
            showToast("Campaign stopped.")
            state.copy(activeAdCampaigns = state.activeAdCampaigns.filter { it.id != campaignId })
        }
    }

    fun launchAdCampaign(agencyId: String, targetSongId: String) {
        updateState { state ->
            val cost = when (agencyId) {
                "beatwave" -> 8000.0
                "starvision" -> 22000.0
                "urban" -> 6500.0
                "soundspot" -> 4200.0
                else -> 0.0
            }
            if (state.adCompanyBudget < cost) {
                showToast("Insufficient ad budget! Need $$${String.format("%,.0f", cost)}")
                return@updateState state
            }
            val targetSong = state.songs.find { it.id == targetSongId }
            val songName = targetSong?.title ?: "Unknown Song"
            showToast("Launched $agencyId for $songName!")
            val newCamp = AdCampaign(
                id = java.util.UUID.randomUUID().toString(),
                agencyId = agencyId,
                agencyName = when (agencyId) {
                    "beatwave" -> "BeatWave Media"
                    "starvision" -> "StarVision TV"
                    "urban" -> "UrbanBillboards"
                    "soundspot" -> "SoundSpot Radio"
                    else -> "Generic"
                },
                targetType = "SONG",
                targetId = targetSongId,
                weeklyBudget = cost,
                durationWeeks = 4,
                weeksRemaining = 4,
                objective = "STREAMS",
                estimatedReachPerWeek = 5000L
            )
            state.copy(activeAdCampaigns = state.activeAdCampaigns + newCamp, adCompanyBudget = state.adCompanyBudget - cost)
        }
    }

    fun markMailAsRead(mailId: String) {
        updateState { state ->
            val updatedMailbox = state.mailbox.map {
                if (it.id == mailId) it.copy(isRead = true)
                else it
            }
            state.copy(mailbox = updatedMailbox)
        }
    }

    fun markAllMailAsRead() {
        updateState { state ->
            val updatedMailbox = state.mailbox.map {
                it.copy(isRead = true)
            }
            state.copy(mailbox = updatedMailbox)
        }
    }

    fun deleteMail(mailId: String) {
        updateState { state ->
            val targetMail = state.mailbox.find { it.id == mailId }
            var updatedNpcs = state.npcs
            if (targetMail != null && !targetMail.isAccepted) {
                val peer = state.npcs.find {
                    it.name.equals(targetMail.sender, ignoreCase = true) ||
                    targetMail.sender.contains(it.name, ignoreCase = true) ||
                    it.id == targetMail.labelId
                }
                if (peer != null) {
                    val penalty = when {
                        targetMail.type == "FEATURE" || targetMail.type == "COLLAB" || targetMail.subject.contains("Feature", ignoreCase = true) || targetMail.subject.contains("Collab", ignoreCase = true) -> 8f
                        targetMail.type == "NETWORK_REFERRAL_GIG" || targetMail.type == "NETWORK_REFERRAL_FEATURE" || targetMail.subject.contains("Referral", ignoreCase = true) -> 6f
                        targetMail.type == "NPC_HELP_REQUEST" || targetMail.subject.contains("Help", ignoreCase = true) || targetMail.subject.contains("Assistance", ignoreCase = true) -> 10f
                        else -> 0f
                    }
                    if (penalty > 0f) {
                        updatedNpcs = state.npcs.map { n ->
                            if (n.id == peer.id) n.copy(relationshipWithPlayer = (n.relationshipWithPlayer - penalty).coerceAtLeast(0f))
                            else n
                        }
                        showToast("Declined request from ${peer.name}. Relationship decreased by -${penalty.toInt()}%.")
                    }
                }
            }
            state.copy(
                mailbox = state.mailbox.filter { it.id != mailId },
                npcs = updatedNpcs
            )
        }
    }

    fun toggleFollowNpcArtist(npcId: String) {
        updateState { state ->
            val npc = state.npcs.find { it.id == npcId } ?: return@updateState state
            val willFollow = !npc.isFollowedByPlayer
            val updatedNpcs = state.npcs.map {
                if (it.id == npcId) {
                    val relDelta = if (willFollow) 6f else -3f
                    it.copy(
                isFollowedByPlayer = willFollow,
                relationshipWithPlayer = (it.relationshipWithPlayer + relDelta).coerceIn(0f, 100f)
            )
                } else it
            }
            val toastMsg = if (willFollow) "Followed ${npc.name}! Relationship +6%." else "Unfollowed ${npc.name}."
            showToast(toastMsg)

            val newCastPosts = if (willFollow) {
                val postEngagement = castPostEngagement(
                    fans = npc.fans,
                    popularity = npc.popularity,
                    variation = kotlin.random.Random.nextFloat()
                )
                val samplePost = CastPost(
                    id = java.util.UUID.randomUUID().toString().take(6),
                    authorName = npc.name,
                    authorHandle = "@${npc.name.lowercase().replace(" ", "")}",
                    content = "${npc.bestSongTitle} session was crazy this week! Big love to all the supporters 🎧🔥",
                    likes = postEngagement.likes,
                    reposts = postEngagement.reposts,
                    commentsCount = postEngagement.comments,
                    week = state.currentWeek,
                    year = state.currentYear,
                    isByPlayer = false
                )
                listOf(samplePost) + state.castPosts
            } else state.castPosts

            state.copy(npcs = updatedNpcs, castPosts = newCastPosts)
        }
    }

    fun incrementCastTimeSpent(seconds: Int = 1) {
        updateState { state ->
            state.copy(castTimeSpentThisWeek = state.castTimeSpentThisWeek + seconds)
        }
    }

    fun setCastNicheFocus(niche: String) {
        updateState { state ->
            state.copy(castNicheFocus = niche)
        }
    }

    // --- BANKING SYSTEM FUNCTIONS ---
    fun depositToSavings(amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash reserves to deposit!")
                return@updateState state
            }
            showToast("Deposited $${String.format("%,.2f", amount)} into Savings!")
            state.copy(
                cash = state.cash - amount,
                banking = state.banking.copy(savingsBalance = state.savingsBalance + amount)
            )
        }
    }

    fun withdrawFromSavings(amount: Double) {
        updateState { state ->
            if (state.savingsBalance < amount) {
                showToast("Insufficient savings balance to withdraw!")
                return@updateState state
            }
            showToast("Withdrew $${String.format("%,.2f", amount)} from Savings!")
            state.copy(
                cash = state.cash + amount,
                banking = state.banking.copy(savingsBalance = state.savingsBalance - amount)
            )
        }
    }

    fun applyForLoan(amount: Double) {
        updateState { state ->
            val assetValue = state.ownedCars.sumOf { it.cost } + state.ownedHouses.sumOf { it.cost } + state.ownedInstruments.sumOf { it.cost }
            val loanCapacity = (state.cash + assetValue) * 2.5 + 50000.0
            val maxAllowedLoan = loanCapacity.coerceIn(50000.0, 5000000.0)

            if (state.loanAmount + amount > maxAllowedLoan) {
                showToast("Loan application denied! Maximum active loan cap reached: $${String.format("%,.0f", maxAllowedLoan)}")
                return@updateState state
            }

            showToast("Loan of $${String.format("%,.2f", amount)} approved and disbursed!")
            state.copy(
                cash = state.cash + amount,
                banking = state.banking.copy(loanAmount = state.loanAmount + amount)
            )
        }
    }

    fun repayLoan(amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash reserves to make repayment!")
                return@updateState state
            }
            val repayAmount = amount.coerceAtMost(state.loanAmount)
            if (repayAmount <= 0.0) {
                showToast("No active loan balance to repay!")
                return@updateState state
            }
            val paidInterest = repayAmount.coerceAtMost(state.accumulatedLoanInterest)
            showToast("Repaid $${String.format("%,.2f", repayAmount)} of active loan!")
            state.copy(
                cash = state.cash - repayAmount,
                banking = state.banking.copy(loanAmount = state.loanAmount - repayAmount, accumulatedLoanInterest = state.accumulatedLoanInterest - paidInterest)
            )
        }
    }

    fun payTaxDebt(amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash reserves to make tax payment!")
                return@updateState state
            }
            val repayAmount = amount.coerceAtMost(state.taxDebt)
            if (repayAmount <= 0.0) {
                showToast("No tax debt balance to pay!")
                return@updateState state
            }
            showToast("Paid $${String.format("%,.2f", repayAmount)} towards Tax Debt!")
            state.copy(
                cash = state.cash - repayAmount,
                banking = state.banking.copy(taxDebt = state.taxDebt - repayAmount)
            )
        }
    }

    // --- ADDITIONAL SMARTPHONE 2.0 SERVICES ---
    fun applyForMortgage(amount: Double) {
        updateState { state ->
            val maxAllowedMortgage = (state.ownedHouses.sumOf { it.cost } * 0.8) + 200000.0
            if (state.mortgageAmount + amount > maxAllowedMortgage) {
                showToast("Mortgage application denied! Maximum active limit is $${String.format("%,.0f", maxAllowedMortgage)}")
                return@updateState state
            }
            showToast("Mortgage of $${String.format("%,.2f", amount)} approved and disbursed!")
            state.copy(
                cash = state.cash + amount,
                banking = state.banking.copy(mortgageAmount = state.mortgageAmount + amount)
            )
        }
    }

    fun repayMortgage(amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash reserves!")
                return@updateState state
            }
            val repayAmount = amount.coerceAtMost(state.mortgageAmount)
            if (repayAmount <= 0.0) {
                showToast("No active mortgage balance to repay!")
                return@updateState state
            }
            showToast("Repaid $${String.format("%,.2f", repayAmount)} of active mortgage!")
            state.copy(
                cash = state.cash - repayAmount,
                banking = state.banking.copy(mortgageAmount = state.mortgageAmount - repayAmount)
            )
        }
    }

    fun toggleInsurance(assetType: String, assetId: String) {
        updateState { state ->
            if (assetType == "car") {
                val insured = state.insuredCars.contains(assetId)
                val newInsured = if (insured) {
                    state.insuredCars.filter { it != assetId }
                } else {
                    state.insuredCars + assetId
                }
                showToast(if (insured) "Canceled Car Insurance!" else "Enrolled Car in Premium Insurance Plan!")
                state.copy(insuredCars = newInsured)
            } else {
                val insured = state.insuredHouses.contains(assetId)
                val newInsured = if (insured) {
                    state.insuredHouses.filter { it != assetId }
                } else {
                    state.insuredHouses + assetId
                }
                showToast(if (insured) "Canceled Property Insurance!" else "Enrolled Property in Premium Insurance Plan!")
                state.copy(insuredHouses = newInsured)
            }
        }
    }

    fun scheduleActivity(activity: String) {
        updateState { state ->
            if (state.scheduledActivities.contains(activity)) {
                showToast("Activity is already scheduled for this week!")
                return@updateState state
            }
            showToast("Scheduled '$activity' for the upcoming week.")
            state.copy(scheduledActivities = state.scheduledActivities + activity)
        }
    }

    fun clearScheduledActivities() {
        updateState { state ->
            state.copy(scheduledActivities = emptyList())
        }
    }

    // --- MISSING REPUTATION, MEDIA & WEALTH METHODS ---

    private fun addHistoryEntryToState(
        state: GameStateEntity,
        title: String,
        description: String,
        memoryType: String = "CAREER",
        strength: String = "NORMAL",
        targetSongTitle: String? = null,
        targetAlbumTitle: String? = null,
        targetTourName: String? = null,
        targetNpcName: String? = null,
        targetBrandName: String? = null,
        targetAwardName: String? = null,
        targetJobTitle: String? = null,
        targetBusinessName: String? = null,
        valueAffected: Float = 0f
    ): GameStateEntity {
        val entry = WorldHistoryEntry(
            id = "hist_" + java.util.UUID.randomUUID().toString().take(8),
            week = state.currentWeek,
            year = state.currentYear,
            title = title,
            description = description,
            memoryType = memoryType,
            strength = strength,
            targetSongTitle = targetSongTitle,
            targetAlbumTitle = targetAlbumTitle,
            targetNpcName = targetNpcName,
            targetBusinessName = targetBusinessName,
            valueAffected = valueAffected
        )
        return state.copy(worldHistory = state.worldHistory + entry)
    }

    data class WorkforceSimulationResult(
        val updatedCash: Double,
        val newSavingsBalance: Double,
        val updatedPayrollReserve: Double,
        val updatedMail: List<MailMessage>,
        val updatedEmployeesList: List<Employee>,
        val finalStocks: List<Stock>,
        val finalCryptos: List<Crypto>,
        val updatedMeTubeVideos: List<MeTubeVideo>,
        val updatedCastPosts: List<CastPost>,
        val smmAutoPostedBrandDeal: Boolean,
        val currentPublicImage: Float,
        val updatedNegativeSentiment: Float,
        val currentTrustworthiness: Float,
        val currentBusinessReputation: Float,
        val currentReliability: Float,
        val employeesUnpaid: Boolean
    )

    private fun simulateEmployeeWorkforce(
        state: GameStateEntity,
        initialCash: Double,
        initialSavings: Double,
        initialStocks: List<Stock>,
        initialCryptos: List<Crypto>,
        initialMeTubeVideos: List<MeTubeVideo>,
        initialCastPosts: List<CastPost>,
        initialMail: List<MailMessage>,
        reportBuilder: StringBuilder,
        activeTours: List<Tour>,
        totalWeeklyOutflow: Double,
        weeklySalary: Double,
        totalStreamsPayout: Double,
        tourPayout: Double,
        merchRevenue: Double,
        meTubeEarnings: Double,
        newLoanAmount: Double,
        initialPublicImage: Float,
        initialNegativeSentiment: Float,
        initialTrustworthiness: Float,
        initialBusinessReputation: Float,
        initialReliability: Float,
        initialIndustryRespect: Float,
        brandDealPostedEarlierThisWeek: Boolean
    ): WorkforceSimulationResult {
        var mutCash = initialCash
        var mutSavings = initialSavings
        var mutReserve = state.wealth.payrollReserve
        val mutMail = initialMail.toMutableList()
        val mutStocks = initialStocks.map { it.copy() }.toMutableList()
        val mutCryptos = initialCryptos.map { it.copy() }.toMutableList()
        val mutVideos = initialMeTubeVideos.toMutableList()
        val mutPosts = initialCastPosts.toMutableList()
        var brandDealAutoCompleted = false
        var mutImage = initialPublicImage
        var mutNegSentiment = initialNegativeSentiment
        var mutTrust = initialTrustworthiness
        var mutRep = initialBusinessReputation
        var mutRel = initialReliability

        val hasCryptoAnalyst = state.hiredEmployees.any {
            it.role.equals("Crypto Analyst", ignoreCase = true) ||
            (it.role.contains("Crypto", ignoreCase = true) && it.role.contains("Analyst", ignoreCase = true)) ||
            (it.role.contains("Analyst", ignoreCase = true) && !it.role.contains("Financial", ignoreCase = true))
        }

        val hasAccountant = state.hiredEmployees.any {
            it.role.equals("Accountant", ignoreCase = true) || it.role.contains("Accountant", ignoreCase = true)
        }

        // --- 1. CRYPTO ANALYST AUTOMATION (Take Profit & Stop Loss on ALL held cryptos) ---
        if (hasCryptoAnalyst && state.cryptoAutoInvestEnabled) {
            val profitTarget = state.cryptoAutoSellProfitMargin.coerceIn(0.05f, 5.0f).toDouble()
            val stopLossThreshold = state.cryptoStopLossMargin.coerceIn(0.05f, 0.95f).toDouble()

            // Check if user has an 80%+ rated Crypto Analyst with Stubborn Trait
            val stubbornCryptoAnalyst = state.hiredEmployees.find {
                (it.role.equals("Crypto Analyst", ignoreCase = true) || (it.role.contains("Crypto", ignoreCase = true) && it.role.contains("Analyst", ignoreCase = true))) &&
                (it.skillRatingPercent >= 80 || it.rating >= 4.0f)
            }

            for (i in mutCryptos.indices) {
                val c = mutCryptos[i]
                if (c.quantity > 0.00001 && c.averagePurchasePrice > 0.0) {
                    val gainRatio = (c.currentPrice - c.averagePurchasePrice) / c.averagePurchasePrice
                    if (gainRatio >= profitTarget) {
                        // TAKE PROFIT EXECUTED
                        val sellProceeds = c.quantity * c.currentPrice
                        val netProfit = sellProceeds - (c.quantity * c.averagePurchasePrice)
                        mutSavings += sellProceeds
                        mutCryptos[i] = c.copy(quantity = 0.0, averagePurchasePrice = 0.0)

                        val mail = MailMessage(
                            id = "crypto_tp_" + java.util.UUID.randomUUID().toString().take(6),
                            sender = "Crypto Analyst",
                            subject = "🚀 Take-Profit Executed: ${c.name} (+${String.format("%.1f", gainRatio * 100)}%)",
                            body = "Boss,\n\nOur market monitoring triggered an automatic Take-Profit order on ${c.name}.\n\n• Price: $${String.format("%,.4f", c.currentPrice)} (Avg Buy: $${String.format("%,.4f", c.averagePurchasePrice)})\n• Position Liquidated: $${String.format("%,.2f", sellProceeds)}\n• Net Realized Profit: +$${String.format("%,.2f", netProfit)}\n\nProceeds have been credited to your liquid savings balance.",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 4,
                            expiresYear = state.currentYear
                        )
                        mutMail.add(mail)
                        reportBuilder.append("💎 Crypto Analyst: Auto-sold ${c.name} locking in +$${String.format("%,.2f", netProfit)} profit!\n")
                    } else if (gainRatio <= -stopLossThreshold) {
                        // Check if Stubborn Trait Analyst intervenes to protect upside rebound
                        val isOverruledByStubborn = stubbornCryptoAnalyst != null && (c.analystPrediction == "RISE" || c.isCheapOpportunity)
                        if (isOverruledByStubborn) {
                            val mail = MailMessage(
                                id = "crypto_sl_overrule_" + java.util.UUID.randomUUID().toString().take(6),
                                sender = "${stubbornCryptoAnalyst!!.name} (Lead Analyst)",
                                subject = "🛑 Stop-Loss Overruled: ${c.name} [Stubborn Trait]",
                                body = "Boss,\n\n${c.name} dipped past our stop-loss (${String.format("%.1f", -gainRatio * 100)}% drawdown), but my 80%+ quant models project a sharp rebound (Predicted: RISE, ${c.predictionConfidence}% confidence).\n\nPer my high conviction, I OVERRULED the stop-loss liquidation to prevent selling at the bottom. We are holding the position for the projected upward cycle swing.",
                                type = "NORMAL",
                                expiresWeek = state.currentWeek + 3,
                                expiresYear = state.currentYear
                            )
                            mutMail.add(mail)
                            reportBuilder.append("🧠 Crypto Analyst: Overruled stop-loss on ${c.name} [Stubborn Trait - Rebound Projected].\n")
                        } else {
                            // STOP LOSS EXECUTED
                            val sellProceeds = c.quantity * c.currentPrice
                            val lossAmount = (c.quantity * c.averagePurchasePrice) - sellProceeds
                            mutSavings += sellProceeds
                            mutCryptos[i] = c.copy(quantity = 0.0, averagePurchasePrice = 0.0)

                            val mail = MailMessage(
                                id = "crypto_sl_" + java.util.UUID.randomUUID().toString().take(6),
                                sender = "Crypto Analyst",
                                subject = "🛡️ Stop-Loss Triggered: ${c.name} (-${String.format("%.1f", -gainRatio * 100)}%)",
                                body = "Boss,\n\n${c.name} reached our stop-loss threshold at $${String.format("%,.4f", c.currentPrice)}.\n\nTo preserve your remaining capital against severe downward momentum, we liquidated the holding for $${String.format("%,.2f", sellProceeds)}.\n\nRisk management protected your portfolio from total drawdown.",
                                type = "NORMAL",
                                expiresWeek = state.currentWeek + 4,
                                expiresYear = state.currentYear
                            )
                            mutMail.add(mail)
                            reportBuilder.append("🛡️ Crypto Analyst: Executed stop-loss liquidation on ${c.name} to preserve capital.\n")
                        }
                    }
                }
            }

            // AUTO-BUY ORDER EXECUTION (With Opportunistic Cheap Allocation Scaling)
            if (hasCryptoAnalyst && state.cryptoAutoInvestEnabled && state.cryptoAutoInvestPercent > 0.01f) {
                val candidateCoins = mutCryptos.filter { it.currentPrice > 0.0 }
                if (candidateCoins.isNotEmpty()) {
                    // Check if high conviction cheap coin is detected
                    val cheapCoin = candidateCoins.find { it.isCheapOpportunity }
                    val isOpportunistic = cheapCoin != null && stubbornCryptoAnalyst != null
                    val chosenCoin = if (isOpportunistic) cheapCoin!! else (candidateCoins.minByOrNull { it.quantity * it.averagePurchasePrice } ?: candidateCoins.random())

                    // If opportunistic, scale allocation (e.g. 1.8x base percent), capped strictly to avoid depleting reserve
                    val effectiveAllocPercent = if (isOpportunistic) (state.cryptoAutoInvestPercent * 1.8f).coerceAtMost(0.48f) else state.cryptoAutoInvestPercent
                    val totalAvailableFunds = if (mutSavings >= 100.0) mutSavings else mutCash
                    val availableCapital = totalAvailableFunds * effectiveAllocPercent.toDouble()

                    if (availableCapital >= 50.0) {
                        val coinIndex = mutCryptos.indexOfFirst { it.id == chosenCoin.id }
                        if (coinIndex >= 0) {
                            if (mutSavings >= availableCapital) {
                                mutSavings -= availableCapital
                            } else {
                                mutCash -= availableCapital
                            }
                            val addedQty = availableCapital / chosenCoin.currentPrice
                            val prevCost = chosenCoin.quantity * chosenCoin.averagePurchasePrice
                            val newQty = chosenCoin.quantity + addedQty
                            val newAvg = (prevCost + availableCapital) / newQty
                            mutCryptos[coinIndex] = chosenCoin.copy(quantity = newQty, averagePurchasePrice = newAvg)

                            val opportunisticTag = if (isOpportunistic) " [🎯 OPPORTUNISTIC VALUE BUY]" else ""
                            val mail = MailMessage(
                                id = "crypto_buy_" + java.util.UUID.randomUUID().toString().take(6),
                                sender = if (isOpportunistic) "${stubbornCryptoAnalyst!!.name} (Lead Analyst)" else "Crypto Analyst",
                                subject = "📊 Weekly Automated Crypto Acquisition: ${chosenCoin.name}$opportunisticTag",
                                body = "Boss,\n\n${if (isOpportunistic) "Recognizing a rare dislocated dip on ${chosenCoin.name}, I executed an opportunistic scaled entry." else "As scheduled, we deployed funds into ${chosenCoin.name}."}\n\n• Capital Deployed: $${String.format("%,.2f", availableCapital)} (${(effectiveAllocPercent * 100).toInt()}% allocation)\n• Acquisition Price: $${String.format("%,.4f", chosenCoin.currentPrice)}\n• Total Position: ${String.format("%,.4f", newQty)} ${chosenCoin.name}.",
                                type = "NORMAL",
                                expiresWeek = state.currentWeek + 3,
                                expiresYear = state.currentYear
                            )
                            mutMail.add(mail)
                            reportBuilder.append("💎 Crypto Analyst: Deployed $${String.format("%,.2f", availableCapital)} into ${chosenCoin.name}$opportunisticTag.\n")
                        }
                    }
                }
            }
        }

        // --- 2. STOCK ACCOUNTANT AUTOMATION ---
        if (hasAccountant) {
            val stockProfitTarget = state.accountantAutoSellProfitMargin.coerceIn(0.05f, 5.0f).toDouble()
            val stockStopLoss = state.accountantStopLossMargin.coerceIn(0.05f, 0.95f).toDouble()

            // Check if user has an 80%+ rated Accountant with Stubborn Trait
            val stubbornAccountant = state.hiredEmployees.find {
                (it.role.contains("Accountant", ignoreCase = true) || it.role.contains("Financial", ignoreCase = true)) &&
                (it.skillRatingPercent >= 80 || it.rating >= 4.0f)
            }

            for (i in mutStocks.indices) {
                val s = mutStocks[i]
                if (s.quantity > 0 && s.averagePurchasePrice > 0.0) {
                    val gainRatio = (s.currentPrice - s.averagePurchasePrice) / s.averagePurchasePrice
                    if (gainRatio >= stockProfitTarget) {
                        val sellProceeds = s.quantity * s.currentPrice
                        val netProfit = sellProceeds - (s.quantity * s.averagePurchasePrice)
                        mutSavings += sellProceeds
                        mutStocks[i] = s.copy(quantity = 0, averagePurchasePrice = 0.0)

                        val mail = MailMessage(
                            id = "stock_tp_" + java.util.UUID.randomUUID().toString().take(6),
                            sender = "Accountant",
                            subject = "📈 Stock Profit Target Reached: ${s.name} (+${String.format("%.1f", gainRatio * 100)}%)",
                            body = "Boss,\n\nAutomated Take-Profit executed on ${s.name} shares at $${String.format("%,.2f", s.currentPrice)}.\n\n• Proceeds: $${String.format("%,.2f", sellProceeds)}\n• Net Profit: +$${String.format("%,.2f", netProfit)}\n\nCapital reallocated to your savings account.",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 4,
                            expiresYear = state.currentYear
                        )
                        mutMail.add(mail)
                        reportBuilder.append("📊 Accountant: Auto-sold ${s.name} shares for +$${String.format("%,.2f", netProfit)} profit!\n")
                    } else if (gainRatio <= -stockStopLoss) {
                        // Check if Stubborn Trait Accountant intervenes based on projected rebound or value
                        val isOverruledByStubborn = stubbornAccountant != null && (s.analystPrediction == "RISE" || s.isCheapOpportunity)
                        if (isOverruledByStubborn) {
                            val mail = MailMessage(
                                id = "stock_sl_overrule_" + java.util.UUID.randomUUID().toString().take(6),
                                sender = "${stubbornAccountant!!.name} (Lead Accountant)",
                                subject = "🛑 Stock Stop-Loss Overruled: ${s.name} [Stubborn Trait]",
                                body = "Boss,\n\n${s.name} breached our stop-loss (${String.format("%.1f", -gainRatio * 100)}% drawdown), but my 80%+ fundamental models project an imminent rebound (Predicted: RISE, ${s.predictionConfidence}% confidence).\n\nPer my high conviction, I OVERRULED the stop-loss liquidation to protect our equity basis and prevent selling at a temporary market dislocation.",
                                type = "NORMAL",
                                expiresWeek = state.currentWeek + 3,
                                expiresYear = state.currentYear
                            )
                            mutMail.add(mail)
                            reportBuilder.append("📊 Accountant: Overruled stop-loss on ${s.name} [Stubborn Trait - Rebound Projected].\n")
                        } else {
                            val sellProceeds = s.quantity * s.currentPrice
                            mutSavings += sellProceeds
                            mutStocks[i] = s.copy(quantity = 0, averagePurchasePrice = 0.0)

                            val mail = MailMessage(
                                id = "stock_sl_" + java.util.UUID.randomUUID().toString().take(6),
                                sender = "Accountant",
                                subject = "🛡️ Stock Stop-Loss Liquidated: ${s.name}",
                                body = "Boss,\n\n${s.name} reached our stop-loss threshold. Position liquidated for $${String.format("%,.2f", sellProceeds)} to prevent deeper balance-sheet drawdowns.",
                                type = "NORMAL",
                                expiresWeek = state.currentWeek + 4,
                                expiresYear = state.currentYear
                            )
                            mutMail.add(mail)
                            reportBuilder.append("🛡️ Accountant: Executed stop-loss liquidation on ${s.name}.\n")
                        }
                    }
                }
            }

            if (state.accountantAutoInvestEnabled && state.accountantAutoInvestPercent > 0.01f) {
                // Check if high conviction cheap stock is detected
                val candidateStockList = mutStocks.filter { it.currentPrice > 0.0 }
                if (candidateStockList.isNotEmpty()) {
                    val cheapStock = candidateStockList.find { it.isCheapOpportunity }
                    val isOpportunistic = cheapStock != null && stubbornAccountant != null
                    val candidateStock = if (isOpportunistic) cheapStock!! else (candidateStockList.minByOrNull { (it.quantity * it.averagePurchasePrice) } ?: candidateStockList.random())

                    val effectiveAllocPercent = if (isOpportunistic) (state.accountantAutoInvestPercent * 1.8f).coerceAtMost(0.48f) else state.accountantAutoInvestPercent
                    val totalAvailableFunds = if (mutSavings >= 200.0) mutSavings else mutCash
                    val stockCapital = totalAvailableFunds * effectiveAllocPercent.toDouble()

                    if (stockCapital >= 100.0) {
                        val sIdx = mutStocks.indexOfFirst { it.id == candidateStock.id }
                        if (sIdx >= 0 && candidateStock.currentPrice > 0.0) {
                            val sharesToBuy = (stockCapital / candidateStock.currentPrice).toInt().coerceAtLeast(1)
                            val totalBuyCost = sharesToBuy * candidateStock.currentPrice
                            if (mutSavings >= totalBuyCost) mutSavings -= totalBuyCost else mutCash -= totalBuyCost

                            val prevCost = candidateStock.quantity * candidateStock.averagePurchasePrice
                            val newTotalShares = candidateStock.quantity + sharesToBuy
                            val newAvg = (prevCost + totalBuyCost) / newTotalShares
                            mutStocks[sIdx] = candidateStock.copy(quantity = newTotalShares, averagePurchasePrice = newAvg)

                            val opportunisticTag = if (isOpportunistic) " [🎯 OPPORTUNISTIC VALUE BUY]" else ""
                            reportBuilder.append("📊 Accountant: Invested $${String.format("%,.2f", totalBuyCost)} in ${sharesToBuy} shares of ${candidateStock.name}$opportunisticTag.\n")
                        }
                    }
                }
            }
        }

        // --- 3. SOCIAL MEDIA MANAGER (SMM) AUTO-POSTS ---
        val hasSMM = state.hiredEmployees.any { it.role.contains("Social", ignoreCase = true) || it.role.contains("SMM", ignoreCase = true) }

        val smmPostCopy = socialManagerPostCopy(
            artistName = state.artistName,
            category = state.smmPostCategory,
            autoPostEnabled = hasSMM && state.smmAutoPost,
            sponsorDue = state.activeBrandDealName.takeIf { !brandDealPostedEarlierThisWeek }
        )
        if (smmPostCopy != null) {
            val postEngagement = castPostEngagement(
                fans = state.followers,
                popularity = state.popularity,
                variation = kotlin.random.Random.nextFloat()
            )
            val autoPost = CastPost(
                id = "cast_smm_" + java.util.UUID.randomUUID().toString().take(6),
                authorName = state.artistName,
                authorHandle = "@" + state.artistName.replace(" ", "").lowercase(),
                content = smmPostCopy.content,
                likes = postEngagement.likes,
                reposts = postEngagement.reposts,
                commentsCount = postEngagement.comments,
                week = state.currentWeek,
                year = state.currentYear,
                isByPlayer = true
            )
            mutPosts.add(0, autoPost)
            if (smmPostCopy.isSponsored) {
                brandDealAutoCompleted = true
                reportBuilder.append("📱 Social Media Manager: Published the required sponsored Cast post for ${state.activeBrandDealName}.\n")
            }

            val vidType = if (state.smmPostCategory.contains("Music")) "MUSIC_VIDEO" else "VLOG"
            val autoVid = MeTubeVideo(
                id = "vid_smm_" + java.util.UUID.randomUUID().toString().take(6),
                title = "${state.artistName} - ${state.smmPostCategory} Update",
                type = vidType,
                views = (state.followers * 0.1).toLong().coerceAtLeast(100),
                likes = (state.followers * 0.02).toLong().coerceAtLeast(10),
                weekPublished = state.currentWeek,
                yearPublished = state.currentYear
            )
            mutVideos.add(0, autoVid)
            reportBuilder.append("📱 Social Media Manager: Auto-posted to Cast & MeTube (${state.smmPostCategory}).\n")
        }

        // --- EMPLOYEE LOYALTY & DEMANDS ---
        val processedEmployees = state.hiredEmployees.map { emp ->
            var updatedLoyalty = emp.loyalty
            // Loyalty decays naturally depending on the employee's greed vs their salary/artist wealth
            val greedDecay = (emp.greed / 100f) * 1.5f
            updatedLoyalty = (updatedLoyalty - greedDecay).coerceAtLeast(0f)

            // If loyalty drops below 25, there's a chance they resign
            if (updatedLoyalty < 25f && kotlin.random.Random.nextFloat() < 0.15f) {
                reportBuilder.append("🚪 STAFF RESIGNATION: Your ${emp.role} (${emp.name}) resigned abruptly! Their loyalty dropped too low due to a lack of raises/bonuses or high greed.\n")
                null // Will filter out nulls
            } else if (updatedLoyalty < 40f && kotlin.random.Random.nextFloat() < 0.03f) {
                // Check if they already sent a demand recently
                val hasPendingDemand = mutMail.any { it.sender.contains(emp.name) && !it.isRead }
                if (!hasPendingDemand) {
                    val demandMailId = "raise_demand_${kotlin.random.Random.nextInt(1000, 9999)}"
                    mutMail.add(
                        MailMessage(
                            id = demandMailId,
                            sender = "${emp.name} (${emp.role})",
                            subject = "Important: Compensation Review",
                            body = "Dear ${state.artistName},\n\nI've been working hard in my role as ${emp.role}, but market rates are higher than my current salary of $${String.format("%,.0f", emp.weeklySalary)}. If you don't grant me a raise or a bonus via the Team tab soon, I'll have to explore other opportunities.\n\nRegards,\n${emp.name}",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 4,
                            expiresYear = state.currentYear
                        )
                    )
                }
                emp.copy(loyalty = updatedLoyalty)
            } else {
                emp.copy(loyalty = updatedLoyalty)
            }
        }.filterNotNull()

        return WorkforceSimulationResult(
            updatedCash = mutCash,
            newSavingsBalance = mutSavings,
            updatedPayrollReserve = mutReserve,
            updatedMail = mutMail,
            updatedEmployeesList = processedEmployees,
            finalStocks = mutStocks,
            finalCryptos = mutCryptos,
            updatedMeTubeVideos = mutVideos,
            updatedCastPosts = mutPosts,
            smmAutoPostedBrandDeal = brandDealAutoCompleted,
            currentPublicImage = mutImage,
            updatedNegativeSentiment = mutNegSentiment,
            currentTrustworthiness = mutTrust,
            currentBusinessReputation = mutRep,
            currentReliability = mutRel,
            employeesUnpaid = false
        )
    }

    private fun generateMonthlyFinancialReview(
        state: GameStateEntity,
        currentCash: Double,
        currentWeeklyCashFlow: Double
    ): String {
        val totalAssets = calculateTotalAssets(state)
        val totalLiabilities = calculateTotalLiabilities(state)
        val netWorth = totalAssets - totalLiabilities
        return "MONTHLY FINANCIAL REVIEW (Year ${state.currentYear}, Week ${state.currentWeek})\n" +
                "• Liquid Cash: $" + String.format("%.2f", currentCash) + "\n" +
                "• Net Worth: $" + String.format("%.2f", netWorth) + "\n" +
                "• Estimated Weekly Cash Flow: $" + String.format("%.2f", currentWeeklyCashFlow) + "\n" +
                "• Total Portfolio Assets: $" + String.format("%.2f", totalAssets) + "\n" +
                "• Total Liabilities & Debts: $" + String.format("%.2f", totalLiabilities)
    }

    private fun simulateWorldEcosystem(
        state: GameStateEntity,
        reportBuilder: StringBuilder,
        updatedMail: MutableList<MailMessage>,
        finalNPCs: List<NPCEntity>
    ): Pair<GameStateEntity, List<NPCEntity>> {
        return Pair(state, finalNPCs)
    }

    fun setCryptoAutoInvest(enabled: Boolean, percent: Float, profitMargin: Float, stopLoss: Float) {
        updateState { state ->
            showToast(if (enabled) "Enabled Crypto Automation Strategy (${(percent * 100).toInt()}% bank balance, +${(profitMargin * 100).toInt()}% TP, -${(stopLoss * 100).toInt()}% SL)" else "Disabled Crypto Automation.")
            state.copy(
                cryptoAutoInvestEnabled = enabled,
                cryptoAutoInvestPercent = percent,
                cryptoAutoSellProfitMargin = profitMargin,
                cryptoStopLossMargin = stopLoss
            )
        }
    }

    fun setAccountantAutoInvest(enabled: Boolean, percent: Float, profitMargin: Float, stopLoss: Float) {
        updateState { state ->
            showToast(if (enabled) "Enabled Stock Automation Strategy (${(percent * 100).toInt()}% bank balance, +${(profitMargin * 100).toInt()}% TP, -${(stopLoss * 100).toInt()}% SL)" else "Disabled Stock Automation.")
            state.copy(
                accountantAutoInvestEnabled = enabled,
                accountantAutoInvestPercent = percent,
                accountantAutoSellProfitMargin = profitMargin,
                accountantStopLossMargin = stopLoss
            )
        }
    }

    fun calculateNetWorth(state: GameStateEntity): Double {
        return calculateTotalAssets(state) - calculateTotalLiabilities(state)
    }

    fun calculateTotalAssets(state: GameStateEntity): Double {
        val stockVal = state.stocks.sumOf { it.currentPrice * it.quantity.toDouble() }
        val cryptoVal = state.cryptos.sumOf { it.currentPrice * it.quantity }
        val realEstateVal = state.ownedHouses.sumOf { it.cost * 1.1 }
        val vehiclesVal = state.ownedCars.sumOf { it.cost * 0.85 }
        val instrumentsVal = state.ownedInstruments.sumOf { it.cost }
        val emergencyReserve = state.wealth.emergencyFundBalance
        val payrollReserve = state.wealth.payrollReserve
        val businessReserve = state.wealth.businessReserve
        val bonds = state.wealth.bondsBalance
        val savings = state.banking.savingsBalance
        return state.cash + savings + stockVal + cryptoVal + realEstateVal + vehiclesVal + instrumentsVal + emergencyReserve + payrollReserve + businessReserve + bonds
    }

    fun calculateTotalLiabilities(state: GameStateEntity): Double {
        return state.loanAmount + state.taxDebt + state.mortgageAmount + state.recoupableAdvanceBalance
    }

    fun transferWealthFunds(fromCategory: String, toCategory: String, amount: Double) {
        updateState { state ->
            if (amount <= 0) return@updateState state

            var newCash = state.cash
            var newSavings = state.savingsBalance
            var newPayroll = state.wealth.payrollReserve
            var newBusiness = state.wealth.businessReserve
            var newEmergency = state.wealth.emergencyFundBalance
            var newBonds = state.wealth.bondsBalance
            var bondMembershipExpired = false

            val normFrom = fromCategory.trim().uppercase()
            val normTo = toCategory.trim().uppercase()

            // Deduct
            when {
                normFrom in listOf("CASH", "LIQUID CASH") -> {
                    if (newCash < amount) {
                        showToast("Insufficient Liquid Cash! (Available: $${String.format("%,.2f", newCash)})")
                        return@updateState state
                    }
                    newCash -= amount
                }
                normFrom in listOf("SAVINGS", "SAVINGS ACCOUNT", "HIGH-YIELD SAVINGS") -> {
                    if (newSavings < amount) {
                        showToast("Insufficient Savings Balance! (Available: $${String.format("%,.2f", newSavings)})")
                        return@updateState state
                    }
                    newSavings -= amount
                }
                normFrom in listOf("PAYROLL", "PAYROLL RESERVE") -> {
                    if (newPayroll < amount) {
                        showToast("Insufficient Payroll Reserve! (Available: $${String.format("%,.2f", newPayroll)})")
                        return@updateState state
                    }
                    newPayroll -= amount
                }
                normFrom in listOf("BUSINESS", "BUSINESS RESERVE") -> {
                    if (newBusiness < amount) {
                        showToast("Insufficient Business Reserve! (Available: $${String.format("%,.2f", newBusiness)})")
                        return@updateState state
                    }
                    newBusiness -= amount
                }
                normFrom in listOf("EMERGENCY", "EMERGENCY FUND", "EMERGENCY RESERVE") -> {
                    if (newEmergency < amount) {
                        showToast("Insufficient Emergency Fund! (Available: $${String.format("%,.2f", newEmergency)})")
                        return@updateState state
                    }
                    newEmergency -= amount
                }
                normFrom in listOf("BONDS", "GOV. BONDS", "BONDS BALANCE", "GOVERNMENT BONDS") -> {
                    if (newBonds < amount) {
                        showToast("Insufficient Bonds Balance! (Available: $${String.format("%,.2f", newBonds)})")
                        return@updateState state
                    }
                    newBonds -= amount
                    if (newBonds <= 0.0) {
                        newBonds = 0.0
                        bondMembershipExpired = true
                    }
                }
                else -> {
                    showToast("Unknown source account: $fromCategory")
                    return@updateState state
                }
            }

            // Add
            when {
                normTo in listOf("CASH", "LIQUID CASH") -> newCash += amount
                normTo in listOf("SAVINGS", "SAVINGS ACCOUNT", "HIGH-YIELD SAVINGS") -> newSavings += amount
                normTo in listOf("PAYROLL", "PAYROLL RESERVE") -> newPayroll += amount
                normTo in listOf("BUSINESS", "BUSINESS RESERVE") -> newBusiness += amount
                normTo in listOf("EMERGENCY", "EMERGENCY FUND", "EMERGENCY RESERVE") -> newEmergency += amount
                normTo in listOf("BONDS", "GOV. BONDS", "BONDS BALANCE", "GOVERNMENT BONDS") -> {
                    if (!state.hasBondLicense) {
                        val costLabel = if (state.wasFormerBondMember) "$2.5M (former member renewal)" else "$5.0M (initial license)"
                        showToast("🏛️ Sovereign Bond Membership License required! ($costLabel). Purchase in Banking.")
                        return@updateState state
                    }
                    if (amount < 500000.0) {
                        showToast("🏛️ Minimum deposit for Government Bonds is $500,000! (Attempted: $${String.format("%,.0f", amount)})")
                        return@updateState state
                    }
                    newBonds += amount
                }
                else -> {
                    showToast("Unknown destination account: $toCategory")
                    return@updateState state
                }
            }

            val expiryMsg = if (bondMembershipExpired) " (Withdrew all funds: Sovereign Bond Membership expired! Renewal fee: $2.5M)" else ""
            showToast("Transferred $${String.format("%,.2f", amount)} from $fromCategory to $toCategory!$expiryMsg")
            state.copy(
                cash = newCash,
                banking = state.banking.copy(savingsBalance = newSavings),
                wealth = state.wealth.copy(
                    payrollReserve = newPayroll,
                    businessReserve = newBusiness,
                    emergencyFundBalance = newEmergency,
                    bondsBalance = newBonds,
                    hasBondLicense = if (bondMembershipExpired) false else state.wealth.hasBondLicense,
                    wasFormerBondMember = if (bondMembershipExpired) true else state.wealth.wasFormerBondMember
                )
            )
        }
    }

    fun purchaseBondMembershipLicense() {
        updateState { state ->
            val cost = if (state.wasFormerBondMember) 2500000.0 else 5000000.0
            val feeLabel = if (state.wasFormerBondMember) "Renewal ($2.5M)" else "Initial ($5.0M)"
            if (state.cash < cost) {
                showToast("Insufficient cash reserves for Sovereign Bond License ($${String.format("%,.0f", cost)} needed)!")
                return@updateState state
            }
            showToast("🏛️ Sovereign Bond Membership License ($feeLabel) acquired! You can now deposit into High-Yield Government Bonds (10% monthly yield, min $500k deposit).")
            state.copy(
                cash = state.cash - cost,
                wealth = state.wealth.copy(
                    hasBondLicense = true,
                    wasFormerBondMember = false
                )
            )
        }
    }

    fun consumeEnergy(amount: Int) {
        updateState { state ->
            if (state.energy > 0) {
                state.copy(energy = (state.energy - amount).coerceAtLeast(0))
            } else state
        }
    }

    fun takeConcertPhoto(caption: String) {
        updateState { state ->
            showToast("Captured new memory photo!")
            state.copy(savedPhotos = state.savedPhotos + caption)
        }
    }

    fun addSavedPhoto(photoUriOrPath: String) {
        updateState { state ->
            showToast("Photo saved to device storage & gallery!")
            val updated = (listOf(photoUriOrPath) + state.savedPhotos).distinct()
            state.copy(savedPhotos = updated)
        }
    }

    fun savePhotoToPhone(photoUriOrPath: String) {
        addSavedPhoto(photoUriOrPath)
    }

    fun removeSavedPhoto(photo: String) {
        updateState { state ->
            state.copy(savedPhotos = state.savedPhotos.filter { it != photo })
        }
    }

    fun saveActiveNoteText(content: String) {
        updateState { state ->
            showToast("Saved notes to persistent ledger.")
            state.copy(activeNoteText = content)
        }
    }

    fun updateMerchPrice(styleId: String, newPrice: Double) {
        updateState { state ->
            val updatedStyles = state.merchStyles.map { style ->
                if (style.id == styleId) style.copy(sellPrice = newPrice) else style
            }
            showToast("Updated Merch Price to $" + String.format("%.2f", newPrice))
            state.copy(merchStyles = updatedStyles)
        }
    }

    fun restockMerchStyle(styleId: String, quantity: Int) {
        updateState { state ->
            val style = state.merchStyles.find { it.id == styleId } ?: return@updateState state
            val cost = style.unitCost * quantity
            if (state.cash < cost) {
                showToast("Insufficient cash to restock merchandise (Need $" + String.format("%.2f", cost) + ")")
                return@updateState state
            }
            val updatedStyles = state.merchStyles.map {
                if (it.id == styleId) it.copy(stockRemaining = it.stockRemaining + quantity) else it
            }
            showToast("Restocked $quantity units of '${style.name}'!")
            state.copy(
                cash = state.cash - cost,
                merchStyles = updatedStyles
            )
        }
    }

    fun designMerchStyle(name: String, category: String, unitCost: Double, sellPrice: Double, initialStock: Int) {
        updateState { state ->
            val setupCost = 1500.0 + (unitCost * initialStock)
            if (state.cash < setupCost) {
                showToast("Insufficient cash to manufacture new merch line (Need $" + String.format("%.2f", setupCost) + ")")
                return@updateState state
            }
            val newStyle = MerchStyle(
                id = "merch_" + java.util.UUID.randomUUID().toString().take(6),
                name = name,
                type = category,
                unitCost = unitCost,
                sellPrice = sellPrice,
                stockRemaining = initialStock,
                totalSold = 0,
                totalRevenue = 0.0
            )
            showToast("Successfully launched new merch style '$name'!")
            state.copy(
                cash = state.cash - setupCost,
                merchStyles = state.merchStyles + newStyle
            )
        }
    }

    fun buyWikipediaPage() {
        updateState { state ->
            if (state.cash < 2500.0) {
                showToast("Insufficient funds for official Wikipedia entry creation ($2,500 needed).")
                return@updateState state
            }
            showToast("Official verified Wikipedia page published! Reputation and fame boosted.")
            state.copy(
                cash = state.cash - 2500.0,
                legacy = state.legacy.copy(hasWikipediaPage = true),
                popularity = (state.popularity + 5f).coerceAtMost(100f)
            )
        }
    }

    fun buyBiography() {
        updateState { state ->
            if (state.cash < 15000.0) {
                showToast("Insufficient funds for published official biography ($15,000 needed).")
                return@updateState state
            }
            showToast("Published hardcover authorized biography worldwide! Massive respect boost.")
            state.copy(
                cash = state.cash - 15000.0,
                legacy = state.legacy.copy(hasBiography = true),
                industryRespect = (state.industryRespect + 12f).coerceAtMost(100f)
            )
        }
    }

    fun buyDocumentary() {
        updateState { state ->
            if (state.cash < 50000.0) {
                showToast("Insufficient funds to produce feature documentary ($50,000 needed).")
                return@updateState state
            }
            showToast("Feature music documentary premiered in festivals and streaming platforms!")
            state.copy(
                cash = state.cash - 50000.0,
                legacy = state.legacy.copy(hasDocumentary = true),
                popularity = (state.popularity + 15f).coerceAtMost(100f)
            )
        }
    }

    fun buyNetflixSeries() {
        updateState { state ->
            if (state.cash < 200000.0) {
                showToast("Insufficient funds for multi-part streaming docuseries ($200,000 needed).")
                return@updateState state
            }
            showToast("Global streaming series released! Global recognition unlocked.")
            state.copy(
                cash = state.cash - 200000.0,
                legacy = state.legacy.copy(hasNetflixSeries = true),
                popularity = (state.popularity + 25f).coerceAtMost(100f)
            )
        }
    }

    fun toggleScholarships() {
        updateState { state ->
            val active = !state.legacy.hasScholarships
            showToast(if (active) "Endowed Community Music Scholarships fund!" else "Closed Scholarship Endowment.")
            state.copy(
                legacy = state.legacy.copy(hasScholarships = active),
                cash = if (active) (state.cash - 10000.0).coerceAtLeast(0.0) else state.cash
            )
        }
    }

    fun buyMuseum() {
        updateState { state ->
            if (state.cash < 1000000.0) {
                showToast("Insufficient funds to construct artist memorial museum ($1,000,000 needed).")
                return@updateState state
            }
            showToast("Grand Opening of the Official Artist Museum & Archive!")
            state.copy(
                cash = state.cash - 1000000.0,
                legacy = state.legacy.copy(hasMuseum = true),
                industryRespect = 100f
            )
        }
    }

    fun buyHallOfFame() {
        updateState { state ->
            if (state.cash < 2500000.0) {
                showToast("Insufficient funds for Hall of Fame induction sponsorship ($2,500,000 needed).")
                return@updateState state
            }
            showToast("Inducted into the Global Music Hall of Fame! Legendary immortality achieved.")
            state.copy(
                cash = state.cash - 2500000.0,
                legacy = state.legacy.copy(hasHallOfFame = true)
            )
        }
    }

    fun hireSecurityCompany(companyId: String) {
        updateState { state ->
            val cost = when (companyId) {
                "Vanguard Tactical" -> 5000.0
                "Aegis Elite Command" -> 15000.0
                else -> 0.0
            }
            if (cost > 0 && state.cash < cost) {
                showToast("Insufficient cash to retain security agency ($" + String.format("%.2f", cost) + " needed).")
                return@updateState state
            }
            showToast(if (companyId.isNotEmpty()) "Enrolled with $companyId for 24/7 VIP Protection!" else "Canceled security contracts.")
            state.copy(
                cash = if (cost > 0) state.cash - cost else state.cash,
                security = state.security.copy(hiredSecurityCompany = companyId.ifEmpty { "None" })
            )
        }
    }

    fun toggleCyberInsurance() {
        updateState { state ->
            val enrolled = !state.security.hasCyberInsurance
            showToast(if (enrolled) "Enrolled in Enterprise Cloud & Identity Cyber Insurance!" else "Canceled Cyber Insurance Policy.")
            state.copy(security = state.security.copy(hasCyberInsurance = enrolled))
        }
    }

    fun toggleStudioInsurance() {
        updateState { state ->
            val enrolled = !state.security.hasStudioInsurance
            showToast(if (enrolled) "Enrolled in Studio Hardware & Sound Equipment Insurance!" else "Canceled Studio Equipment Insurance.")
            state.copy(security = state.security.copy(hasStudioInsurance = enrolled))
        }
    }

    fun recoverStolenAccount() {
        updateState { state ->
            val lostEntry = state.spiritualJournal.find { it.startsWith("phone_stolen_followers_lost:") || it.startsWith("cast_account_hijacked:") }
            val lostAmount = lostEntry?.substringAfter(":")?.toLongOrNull() ?: 15000L
            val fee = if (state.hasCyberInsurance || state.hiredSecurityCompany.isNotEmpty()) 0.0 else 1500.0
            if (state.cash < fee) {
                showToast("Not enough cash ($1,500 required) for forensic account recovery!")
                return@updateState state
            }
            val updatedJournal = state.spiritualJournal.filter { !it.startsWith("phone_stolen_followers_lost:") && !it.startsWith("cast_account_hijacked:") }
            showToast("Shield security team neutralized hijackers! Restored Cast account and recovered ${String.format("%,d", lostAmount)} followers.")
            state.copy(
                cash = state.cash - fee,
                publicImage = (state.publicImage + 10f).coerceAtMost(100f),
                sentiment = state.sentiment.copy(
                    followers = state.followers + lostAmount,
                    negativeSentiment = (state.negativeSentiment - 15f).coerceAtLeast(0f)
                ),
                spiritual = state.spiritual.copy(
                    spiritualJournal = updatedJournal
                )
            )
        }
    }

    fun recoverStolenCrypto() {
        updateState { state ->
            val stolenEntry = state.spiritualJournal.find { it.startsWith("genesis_crypto_stolen:") }
            if (stolenEntry == null) {
                showToast("No active stolen crypto records found!")
                return@updateState state
            }
            val stolenUsd = stolenEntry.substringAfter("genesis_crypto_stolen:").toDoubleOrNull() ?: 0.0
            val securityStaff = state.hiredEmployees.find { it.role == "Security" && it.loyalty > 25f }
            val hasShield = state.hiredSecurityCompany.isNotEmpty() || state.hasCyberInsurance || securityStaff != null
            if (!hasShield) {
                showToast("You need an active Shield contract or a hired Security staff member to trace on-chain transactions and recover coins!")
                return@updateState state
            }
            val agentName = if (state.hiredSecurityCompany.isNotEmpty()) state.hiredSecurityCompany else if (securityStaff != null) securityStaff.name else "Cyber-Shield"
            val updatedJournal = state.spiritualJournal.filter { !it.startsWith("genesis_crypto_stolen:") }

            val updatedCryptos = if (state.cryptos.isNotEmpty()) {
                val first = state.cryptos.first()
                val addQuantity = if (first.currentPrice > 0.0) stolenUsd / first.currentPrice else 1.0
                state.cryptos.mapIndexed { idx, c -> if (idx == 0) c.copy(quantity = c.quantity + addQuantity) else c }
            } else {
                state.cryptos
            }
            showToast("Success! $agentName traced the blockchain ledger and recovered $${String.format("%,.2f", stolenUsd)} in stolen coins back to your Genesis wallet!")
            state.copy(
                cryptos = updatedCryptos,
                spiritual = state.spiritual.copy(
                    spiritualJournal = updatedJournal
                )
            )
        }
    }

    fun runUnderworldAction(actionId: String) {
        updateState { state ->
            showToast("Initiated black-market operative service: $actionId")
            state
        }
    }

    fun applyToWhoIsGotTalent() {
        updateState { state ->
            if (state.wgtApplied && state.wgtRegistrationStatus in listOf("REGISTERED_SELECTED", "REGISTERED_ROLLED_OVER", "ADVANCED_SEMI_FINALS", "ADVANCED_FINALE")) {
                showToast("You are already registered! Current status: ${state.wgtRegistrationStatus}")
                return@updateState state
            }
            val fee = 2000.0
            if (state.cash < fee) {
                showToast("Insufficient cash to register for Who Is Got Talent ($2,000 needed).")
                return@updateState state
            }

            var curWeb = state.webHub
            if (curWeb.wgtAuditionWeek == 0) {
                val isFirstHalf = state.currentWeek <= 20
                val sNum = if (isFirstHalf) 1 else 2
                val sYear = state.currentYear
                val audW = if (isFirstHalf) kotlin.random.Random.nextInt(7, 12) else kotlin.random.Random.nextInt(28, 33)
                val semiW = audW + 4
                val finW = (semiW + 4).coerceAtMost(if (isFirstHalf) 20 else 40)
                curWeb = curWeb.copy(
                    wgtSeason = sNum,
                    wgtSeasonYear = sYear,
                    wgtAuditionYear = sYear,
                    wgtAuditionWeek = audW,
                    wgtSemiFinalsWeek = semiW,
                    wgtGrandFinaleWeek = finW
                )
            }

            val targetSeason = curWeb.wgtSeason
            val targetYear = curWeb.wgtSeasonYear
            val targetAuditionW = curWeb.wgtAuditionWeek

            // Cutoff is 2 weeks before the audition week
            val isEarly = (state.currentYear < targetYear) || (state.currentYear == targetYear && state.currentWeek <= targetAuditionW - 2)

            if (isEarly) {
                val mail = MailMessage(
                    id = "wgt_reg_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "WGT Executive Production",
                    subject = "⭐ AUDITION SELECTED: Who Is Got Talent Season $targetSeason",
                    body = "Dear ${state.artistName.ifEmpty { "Artist" }},\n\nYour early application for Who Is Got Talent (Season $targetSeason) has passed preliminary screening! You have been officially SELECTED for the live stage auditions.\n\n• Live Audition Broadcast: Week $targetAuditionW, Year $targetYear\n• Semi-Finals Broadcast: Week ${curWeb.wgtSemiFinalsWeek}, Year $targetYear\n• Grand Finale Showdown: Week ${curWeb.wgtGrandFinaleWeek}, Year $targetYear\n• Grand Prize: $30,000 Cash + National Acclaim\n\nYour spot is confirmed. Prepare your vocals, songs, and stage performance!\n\nGood luck,\nWGT Executive Producers",
                    type = "NORMAL",
                    expiresWeek = targetAuditionW + 2,
                    expiresYear = targetYear
                )
                showToast("Application Confirmed! Auditions in Week $targetAuditionW, Year $targetYear.")
                state.copy(
                    cash = state.cash - fee,
                    mailbox = state.mailbox + mail,
                    webHub = curWeb.copy(
                        wgtApplied = true,
                        wgtRegistrationStatus = "REGISTERED_SELECTED",
                        wgtAuditionYear = targetYear,
                        wgtAuditionWeek = targetAuditionW,
                        wgtWon = false
                    )
                )
            } else {
                // Applied within 2 weeks or after cutoff!
                // No refund, but rolled over to the next show so player doesn't pay again!
                val nextSeason = if (targetSeason == 1) 2 else 1
                val nextYear = if (targetSeason == 1) targetYear else targetYear + 1
                val nextAudW = if (nextSeason == 1) kotlin.random.Random.nextInt(7, 12) else kotlin.random.Random.nextInt(28, 33)
                val nextSemiW = nextAudW + 4
                val nextFinW = (nextSemiW + 4).coerceAtMost(if (nextSeason == 1) 20 else 40)

                val mail = MailMessage(
                    id = "wgt_rollover_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "WGT Executive Production",
                    subject = "📋 REGISTRATION ROLLED OVER: Who Is Got Talent",
                    body = "Dear ${state.artistName.ifEmpty { "Artist" }},\n\nThank you for applying to Who Is Got Talent. As per network competition rules, applications for the Season $targetSeason live auditions closed 2 weeks prior to the show date.\n\nBecause your registration was received past the cutoff, your application fee is non-refundable; however, we have automatically ROLLED OVER your registration to the NEXT audition show (Season $nextSeason, scheduled for Week $nextAudW, Year $nextYear).\n\nYou are fully confirmed and do NOT need to pay again to compete in the upcoming season. Your ticket is secured!\n\nSincerely,\nWGT Contestant Relations",
                    type = "NORMAL",
                    expiresWeek = 52,
                    expiresYear = nextYear
                )
                showToast("Application received past 2-week cutoff. Rolled over to Season $nextSeason without additional fee!")
                state.copy(
                    cash = state.cash - fee,
                    mailbox = state.mailbox + mail,
                    webHub = curWeb.copy(
                        wgtApplied = true,
                        wgtRegistrationStatus = "REGISTERED_ROLLED_OVER",
                        wgtSeason = nextSeason,
                        wgtSeasonYear = nextYear,
                        wgtAuditionYear = nextYear,
                        wgtAuditionWeek = nextAudW,
                        wgtSemiFinalsWeek = nextSemiW,
                        wgtGrandFinaleWeek = nextFinW,
                        wgtWon = false
                    )
                )
            }
        }
    }

    fun performWgtAudition() {
        updateState { state ->
            val curStatus = state.wgtRegistrationStatus
            if (!state.wgtApplied || curStatus !in listOf("REGISTERED_SELECTED", "REGISTERED_ROLLED_OVER", "ADVANCED_SEMI_FINALS")) {
                showToast("You are not currently scheduled for a live stage performance.")
                return@updateState state
            }
            if (curStatus == "REGISTERED_SELECTED" || curStatus == "REGISTERED_ROLLED_OVER") {
                val vocalComp = (state.voiceSkill * 0.40f).coerceIn(0f, 40f)
                val writingComp = (state.writingSkill * 0.25f).coerceIn(0f, 25f)
                val charismaComp = ((state.popularity + state.publicImage) * 0.15f).coerceIn(0f, 20f)
                val energyMod = if (state.energy < 25) -15f else 5f
                val stressMod = (state.stress * 0.15f).coerceIn(0f, 15f)
                val luck = kotlin.random.Random.nextFloat() * 15f
                val totalScore = (vocalComp + writingComp + charismaComp + energyMod - stressMod + luck).coerceIn(15f, 98f)

                val passed = totalScore >= 62f
                if (passed) {
                    val newFans = kotlin.random.Random.nextLong(6000, 12000)
                    val mail = MailMessage(
                        id = "wgt_aud_pass_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = "WGT Broadcast Network",
                        subject = "🌟 WGT AUDITIONS: You Advanced to the Semi-Finals!",
                        body = "Spectacular performance! The judges gave you high marks with a score of ${String.format("%.1f", totalScore)}/100!\n\n• Fans Gained: +${String.format("%,d", newFans)}\n• Semi-Finals Broadcast: Week ${state.webHub.wgtSemiFinalsWeek}, Year ${state.webHub.wgtSeasonYear}\n\nKeep training your vocal technique and songwriting depth!",
                        type = "NORMAL",
                        expiresWeek = state.webHub.wgtSemiFinalsWeek + 2,
                        expiresYear = state.webHub.wgtSeasonYear
                    )
                    showToast("Passed WGT Audition! Scored ${String.format("%.1f", totalScore)}/100.")
                    state.copy(
                        fans = state.fans + newFans,
                        popularity = (state.popularity + 6f).coerceAtMost(100f),
                        industryRespect = (state.industryRespect + 5f).coerceAtMost(100f),
                        mailbox = state.mailbox + mail,
                        webHub = state.webHub.copy(wgtRegistrationStatus = "ADVANCED_SEMI_FINALS")
                    )
                } else {
                    val newFans = kotlin.random.Random.nextLong(1200, 2500)
                    val mail = MailMessage(
                        id = "wgt_aud_fail_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = "WGT Broadcast Network",
                        subject = "📺 WGT Auditions: Did Not Advance",
                        body = "You performed your audition piece scoring ${String.format("%.1f", totalScore)}/100. The judges praised your commitment, but competition was intensely fierce and you were not selected for the Semi-Finals this season.\n\n• Broadcast Exposure: +${String.format("%,d", newFans)} fans\n• Next season registration opens after the Grand Finale!",
                        type = "NORMAL",
                        expiresWeek = state.currentWeek + 4,
                        expiresYear = state.currentYear
                    )
                    showToast("Audition Complete. Scored ${String.format("%.1f", totalScore)}/100 (Eliminated).")
                    state.copy(
                        fans = state.fans + newFans,
                        mailbox = state.mailbox + mail,
                        webHub = state.webHub.copy(wgtRegistrationStatus = "ELIMINATED_AUDITION")
                    )
                }
            } else {
                // Semi-Finals stage
                val vocalComp = (state.voiceSkill * 0.40f).coerceIn(0f, 40f)
                val writingComp = (state.writingSkill * 0.25f).coerceIn(0f, 25f)
                val charismaComp = ((state.popularity + state.publicImage) * 0.15f).coerceIn(0f, 20f)
                val energyMod = if (state.energy < 25) -15f else 5f
                val stressMod = (state.stress * 0.15f).coerceIn(0f, 15f)
                val luck = kotlin.random.Random.nextFloat() * 15f
                val totalScore = (vocalComp + writingComp + charismaComp + energyMod - stressMod + luck).coerceIn(15f, 98f)

                val passed = totalScore >= 72f
                if (passed) {
                    val newFans = kotlin.random.Random.nextLong(15000, 28000)
                    val mail = MailMessage(
                        id = "wgt_semi_pass_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = "WGT Broadcast Network",
                        subject = "🏆 WGT SEMI-FINALS: You are a National Grand Finalist!",
                        body = "Incredible showcase! You captivated the nation scoring ${String.format("%.1f", totalScore)}/100. You have officially advanced to the GRAND FINALE in Week ${state.webHub.wgtGrandFinaleWeek}!\n\n• Exposure Surge: +${String.format("%,d", newFans)} Fans\n• Popularity: +12%\n\nThe $30,000 championship trophy is within reach!",
                        type = "NORMAL",
                        expiresWeek = state.webHub.wgtGrandFinaleWeek + 2,
                        expiresYear = state.webHub.wgtSeasonYear
                    )
                    showToast("Semi-Finals Victory! Scored ${String.format("%.1f", totalScore)}/100. Finalist!")
                    state.copy(
                        fans = state.fans + newFans,
                        popularity = (state.popularity + 12f).coerceAtMost(100f),
                        industryRespect = (state.industryRespect + 10f).coerceAtMost(100f),
                        mailbox = state.mailbox + mail,
                        webHub = state.webHub.copy(wgtRegistrationStatus = "ADVANCED_FINALE")
                    )
                } else {
                    val newFans = kotlin.random.Random.nextLong(4000, 8000)
                    val mail = MailMessage(
                        id = "wgt_semi_fail_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = "WGT Broadcast Network",
                        subject = "📺 WGT Semi-Finals: Honorable Exit",
                        body = "You scored ${String.format("%.1f", totalScore)}/100 in the Semi-Finals live broadcast. The viewer vote narrowly favored other acts, ending your run just short of the Grand Finale.\n\n• Exposure: +${String.format("%,d", newFans)} Fans",
                        type = "NORMAL",
                        expiresWeek = state.currentWeek + 4,
                        expiresYear = state.currentYear
                    )
                    showToast("Semi-Finals complete. Scored ${String.format("%.1f", totalScore)}/100 (Eliminated).")
                    state.copy(
                        fans = state.fans + newFans,
                        mailbox = state.mailbox + mail,
                        webHub = state.webHub.copy(wgtRegistrationStatus = "ELIMINATED_SEMI_FINALS")
                    )
                }
            }
        }
    }

    fun applyForWgtMovieActingRole(movieTitle: String = "Echoes of the Neon Streets", studioName: String = "WGT Pictures & Global Cinema") {
        updateState { state ->
            val applicationFee = 50_000.0
            if (state.cash < applicationFee) {
                showToast("Insufficient funds! Acting role casting fee is $50,000.")
                return@updateState state
            }

            // Requirements evaluation:
            // 1. Fans > 70,000
            val hasFans = state.fans > 70_000L
            // 2. At least 5 songs with at least 1M streams each
            val millionStreamSongsCount = state.songs.count { it.isReleased && it.totalStreams >= 1_000_000L }
            val hasMillionHits = millionStreamSongsCount >= 5
            // 3. Vocal skill >= 80
            val hasVocalSkill = state.voiceSkill >= 80f
            // 4. Followers on Cast >= 50,000
            val hasCastFollowers = state.followers >= 50_000L
            // 5. Good relationship (>60) with at least 10 Artists
            val goodArtistRelationsCount = state.npcs.count { !it.isRetired && it.relationshipWithPlayer >= 60f }
            val hasArtistNetwork = goodArtistRelationsCount >= 10
            // 6. YouTube / MeTube subscribers >= 100,000
            val hasMeTubeSubs = state.meTubeSubscribers >= 100_000L

            val metCount = listOf(hasFans, hasMillionHits, hasVocalSkill, hasCastFollowers, hasArtistNetwork, hasMeTubeSubs).count { it }

            val rTier: String
            val rTitle: String
            val rPay: Double
            val rFame: Long
            val rPop: Float
            val rRespect: Float

            when {
                metCount == 6 -> {
                    // Lead Role: All requirements met!
                    rTier = "LEAD"
                    rTitle = "Lead Protagonist"
                    rPay = kotlin.random.Random.nextDouble(100_000.0, 200_000.0).roundToInt().toDouble()
                    rFame = 12_000L
                    rPop = 15f
                    rRespect = 12f
                }
                metCount in 3..5 -> {
                    // Middle Role
                    rTier = "SUPPORTING"
                    rTitle = "Supporting Major Role"
                    rPay = kotlin.random.Random.nextDouble(70_000.0, 80_000.0).roundToInt().toDouble()
                    rFame = 6_000L
                    rPop = 8f
                    rRespect = 6f
                }
                else -> {
                    // Small Role / Cameo
                    rTier = "CAMEO"
                    rTitle = "Featured Cameo / Guest Artist"
                    rPay = kotlin.random.Random.nextDouble(25_000.0, 30_000.0).roundToInt().toDouble()
                    rFame = 2_500L
                    rPop = 4f
                    rRespect = 3f
                }
            }

            val mail = MailMessage(
                id = "wgt_movie_" + java.util.UUID.randomUUID().toString().take(6),
                sender = "$studioName Casting Board",
                subject = "🎬 CASTING APPROVAL: '$rTitle' in '$movieTitle'",
                body = "Dear ${state.artistName.ifEmpty { "Artist" }},\n\nThe executive casting directors at $studioName have completed auditions for '$movieTitle'.\n\n• Casting Evaluation: $metCount/6 Primary Industry Benchmarks Satisfied\n• Assigned Role: $rTitle ($rTier Tier)\n• Production Contract Payout: \$${String.format("%,.0f", rPay)} (Directly wired to your account)\n• Audience Impact: +${String.format("%,d", rFame)} new cinematic fans, +${rPop.toInt()}% Popularity, +${rRespect.toInt()}% Industry Acclaim\n\nYour presence brings tremendous star power to this feature production. Cameras roll immediately!\n\nSincerely,\nExecutive Casting Director, $studioName",
                type = "NORMAL",
                cashOffer = rPay,
                expiresWeek = state.currentWeek + 6,
                expiresYear = state.currentYear
            )

            val netCashChange = rPay - applicationFee
            val toastMsg = when (rTier) {
                "LEAD" -> "🌟 CAST IN LEAD ROLE! Earned \$${String.format("%,.0f", rPay)} in '$movieTitle'!"
                "SUPPORTING" -> "🎭 CAST IN SUPPORTING ROLE! Earned \$${String.format("%,.0f", rPay)} in '$movieTitle'!"
                else -> "🎬 CAST IN CAMEO ROLE! Earned \$${String.format("%,.0f", rPay)} in '$movieTitle'!"
            }
            showToast(toastMsg)

            state.copy(
                cash = state.cash + netCashChange,
                fans = state.fans + rFame,
                popularity = (state.popularity + rPop).coerceAtMost(100f),
                industryRespect = (state.industryRespect + rRespect).coerceAtMost(100f),
                mailbox = state.mailbox + mail
            )
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    fun submitDemoToApexRecords(songId: String?) {
        updateState { state ->
            val song = state.songs.find { it.id == songId } ?: state.songs.maxByOrNull { it.rating }
            val songTitle = song?.title ?: "Master Studio Demo"
            val songRating = song?.rating ?: 3.5f

            val isAccepted = songRating >= 3.2f || state.popularity >= 15f || state.fans >= 10000L
            val advanceAmount = if (state.popularity > 50f) 500000.0 else if (state.popularity > 25f) 250000.0 else 100000.0

            val responseMail = if (isAccepted) {
                MailMessage(
                    id = "apex_offer_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "Apex Records A&R",
                    subject = "📝 RECORDING AGREEMENT OFFER: '$songTitle'",
                    body = "Dear ${state.artistName.ifEmpty { "Artist" }},\n\nOur executive A&R committee reviewed the master recording of '$songTitle'. We are thoroughly impressed with the acoustic polish and commercial potential.\n\nApex Records formally extends an exclusive Recording Agreement offer:\n\n• Non-Returnable Signing Advance: $${String.format("%,.2f", advanceAmount)}\n• Master Royalty Rate: 50%\n• Required Deliverables: 2 Singles, 1 Studio Album\n• Global DSP Playlist Priority & Marketing Campaign\n\nVisit the Record Label Office or Web Portal to review and sign your binding contract!",
                    type = "NORMAL",
                    expiresWeek = state.currentWeek + 6,
                    expiresYear = state.currentYear
                )
            } else {
                MailMessage(
                    id = "apex_rej_" + java.util.UUID.randomUUID().toString().take(6),
                    sender = "Apex Records A&R",
                    subject = "Demo Feedback: '$songTitle'",
                    body = "Dear ${state.artistName.ifEmpty { "Artist" }},\n\nThank you for sharing '$songTitle' with our A&R scouts. While the creative concept shows promise, we are looking for higher sonic production quality and stronger organic streaming traction before offering a roster slot.\n\nKeep honing your sound, release independent tracks to build momentum, and submit again in the future.",
                    type = "NORMAL",
                    expiresWeek = state.currentWeek + 4,
                    expiresYear = state.currentYear
                )
            }

            showToast("Dispatched private master demo of '$songTitle' directly to Apex Records A&R!")
            state.copy(
                mailbox = state.mailbox + responseMail,
                webHub = state.webHub.copy(lastDemoSubmissionWeek = state.currentWeek)
            )
        }
    }

    fun registerAsVoter() {
        updateState { state ->
            if (state.isVoterRegistered) {
                showToast("You are already registered! Official Citizen Voter ID: ${state.voterIdNumber}")
                return@updateState state
            }
            val newVoterId = "OEC-${(1000..9999).random()}-VT"
            showToast("Voter registration approved! Official Citizen Voter ID: $newVoterId")
            state.copy(
                isVoterRegistered = true,
                voterIdNumber = newVoterId
            )
        }
    }

    fun voteInOmegaElection(candidateId: String, candidateName: String, party: String) {
        updateState { state ->
            var registeredState = state
            if (!state.isVoterRegistered) {
                val newVoterId = "OEC-${(1000..9999).random()}-VT"
                registeredState = state.copy(
                    isVoterRegistered = true,
                    voterIdNumber = newVoterId
                )
            }
            showToast("Ballot officially cast for $candidateName ($party)! Dynamic district returns are live (-2% energy).")
            registeredState.copy(
                hasVotedInCurrentElection = true,
                energy = (registeredState.energy - 2).coerceAtLeast(0),
                webHub = registeredState.webHub.copy(electionVotedCandidateId = candidateId)
            )
        }
    }

    fun acceptPoliticianEndorsement(candidateId: String, candidateName: String, amount: Double) {
        updateState { state ->
            showToast("Accepted campaign endorsement deal with $candidateName! Received $${String.format("%,.0f", amount)} campaign funds.")
            state.copy(
                cash = state.cash + amount,
                tookEndorsementMoney = true,
                endorsementMoneyAmount = amount,
                endorsedPoliticianId = candidateId,
                endorsedPoliticianName = candidateName,
                endorsedCandidateId = candidateId
            )
        }
    }

    fun payArrestBail() {
        updateState { state ->
            if (!state.isUnderArrest) {
                showToast("You are not currently under judicial arrest or detention.")
                return@updateState state
            }
            if (state.cash < state.arrestBailAmount) {
                showToast("Insufficient cash to post bail bond ($${String.format("%,.0f", state.arrestBailAmount)} needed).")
                return@updateState state
            }
            showToast("Posted bail of $${String.format("%,.0f", state.arrestBailAmount)} with State Crime Command! Immediate release granted. All venture, studio, tour, and WGT restrictions vacated.")
            state.copy(
                cash = state.cash - state.arrestBailAmount,
                isUnderArrest = false,
                arrestSentenceWeeksRemaining = 0,
                arrestReason = "",
                arrestBailAmount = 0.0,
                arrestAttorneyHired = null,
                webHub = state.webHub.copy(isSccWanted = false)
            )
        }
    }

    fun hireDefenseAttorney(attorney: DefenseAttorneyOption) {
        updateState { state ->
            if (!state.isUnderArrest) {
                showToast("No active judicial charges requiring legal defense.")
                return@updateState state
            }
            if (state.cash < attorney.retainerCost) {
                showToast("Insufficient cash to retain ${attorney.name} ($${String.format("%,.0f", attorney.retainerCost)} needed).")
                return@updateState state
            }
            val discountedBail = (state.arrestBailAmount * (1f - attorney.bailDiscountPercent)).coerceAtLeast(5000.0)
            val reducedSentence = (state.arrestSentenceWeeksRemaining - attorney.sentenceReductionWeeks).coerceAtLeast(1)
            showToast("Retained ${attorney.name} (${attorney.firm})! Bail reduced to $${String.format("%,.0f", discountedBail)}, sentence reduced to $reducedSentence weeks.")
            state.copy(
                cash = state.cash - attorney.retainerCost,
                arrestBailAmount = discountedBail,
                arrestSentenceWeeksRemaining = reducedSentence,
                arrestAttorneyHired = attorney.name
            )
        }
    }

    fun serveDetentionWeek() {
        updateState { state ->
            if (!state.isUnderArrest) {
                showToast("You are not in state detention.")
                return@updateState state
            }
            val newWeeks = (state.arrestSentenceWeeksRemaining - 1).coerceAtLeast(0)
            if (newWeeks <= 0) {
                showToast("Detention sentence completed! Released from State Crime Command correctional custody. All rights restored.")
                state.copy(
                    isUnderArrest = false,
                    arrestSentenceWeeksRemaining = 0,
                    arrestReason = "",
                    arrestBailAmount = 0.0,
                    arrestAttorneyHired = null,
                    creativeInspiration = (state.creativeInspiration + 20f).coerceAtMost(100f),
                    discipline = (state.discipline + 15f).coerceAtMost(100f),
                    webHub = state.webHub.copy(isSccWanted = false)
                )
            } else {
                showToast("Served 1 week in state correctional custody. $newWeeks weeks remaining.")
                state.copy(
                    arrestSentenceWeeksRemaining = newWeeks,
                    creativeInspiration = (state.creativeInspiration + 5f).coerceAtMost(100f),
                    discipline = (state.discipline + 3f).coerceAtMost(100f)
                )
            }
        }
    }

    fun executeBlackHatPurchase(itemId: String) {
        updateState { state ->
            showToast("Executed encrypted transaction on the deep web network for $itemId.")
            state
        }
    }

    fun requestGrandAwardsInvitation() {
        updateState { state ->
            showToast("Submitted formal VIP credentials inquiry to the Grand Music Awards Board.")
            state
        }
    }

    fun paySccGovernmentFine() {
        updateState { state ->
            val fine = state.arrestBailAmount.takeIf { it > 0.0 } ?: 5000.0
            if (state.cash < fine) {
                showToast("Insufficient cash to pay SCC regulatory fine ($${String.format("%,.0f", fine)} needed).")
                return@updateState state
            }
            showToast("Paid SCC regulatory compliance fine in full ($${String.format("%,.0f", fine)}). Legal compliance and citizen standing restored.")
            state.copy(
                cash = state.cash - fine,
                isUnderArrest = false,
                arrestSentenceWeeksRemaining = 0,
                arrestReason = "",
                arrestBailAmount = 0.0,
                arrestAttorneyHired = null,
                webHub = state.webHub.copy(isSccWanted = false)
            )
        }
    }

    fun uploadSong(songId: String, platformId: String) {
        updateState { state ->
            val cost = when(platformId) {
                "songify" -> 2500.0
                "jams" -> 800.0
                "megaphone" -> 50.0
                "tunes" -> 0.0
                "sharp" -> 500.0
                else -> 0.0
            }
            if (state.cash < cost) {
                showToast("Not enough cash to upload to $platformId!")
                state
            } else {
                val updatedSongs = state.songs.map {
                    if (it.id == songId) {
                        it.copy(isUploadedMap = it.isUploadedMap + (platformId to 0L))
                    } else it
                }
                showToast("Song uploaded to $platformId!")
                state.copy(cash = state.cash - cost, songs = updatedSongs)
            }
        }
    }

    fun promoteUnreleasedSong(songId: String, activity: String, tierIndex: Int = 0) {
        updateState { state ->
            val song = state.songs.find { it.id == songId }
            if (song == null) {
                showToast("Song not found!")
                return@updateState state
            }
            if (song.isReleased) {
                showToast("This song is already released! Launch an Active Promo Campaign below to build chart streams.")
                return@updateState state
            }

            // Multiple prices per activity: affordable for new artists, scaling up for maximum hype
            val (cost, hypeBoost, fansGained, campaignName) = when (activity) {
                "social" -> when (tierIndex) {
                    1 -> listOf(1500.0, 16.0, 180.0, "Influencer Sound Reel Push")
                    2 -> listOf(5000.0, 30.0, 750.0, "Viral Trend Creator Blitz")
                    else -> listOf(250.0, 6.0, 25.0, "Community Social Post")
                }
                "video" -> when (tierIndex) {
                    1 -> listOf(3000.0, 22.0, 350.0, "Cinematic Visualizer Premiere")
                    2 -> listOf(10000.0, 40.0, 1500.0, "High-Budget CGI Teaser Premiere")
                    else -> listOf(500.0, 9.0, 50.0, "Lo-Fi Studio Snippet")
                }
                "ad" -> when (tierIndex) {
                    1 -> listOf(6000.0, 26.0, 900.0, "Multi-Platform DSP Banner Ads")
                    2 -> listOf(20000.0, 48.0, 3500.0, "Global Billboard & Streaming Takeover")
                    else -> listOf(1000.0, 12.0, 120.0, "Local Indie Geo-Targeted Ads")
                }
                "radio" -> when (tierIndex) {
                    1 -> listOf(4500.0, 24.0, 650.0, "Syndicated Regional Radio Interview")
                    2 -> listOf(16000.0, 44.0, 2800.0, "Prime-Time National Radio & Press Tour")
                    else -> listOf(800.0, 10.0, 100.0, "College & Community Radio Plug")
                }
                else -> listOf(250.0, 5.0, 20.0, "Promotional Blitz")
            }

            val finalCost = cost as Double
            val finalHype = (hypeBoost as Double).toFloat()
            val finalFans = (fansGained as Double).toLong()
            val finalName = campaignName as String

            if (state.cash < finalCost) {
                showToast("Insufficient cash for $finalName! Costs $${String.format("%,.0f", finalCost)}.")
                return@updateState state
            }

            val campaignPower = (song.preReleaseCampaignPower + finalHype).coerceAtMost(100f)
            val campaignReach = song.preReleaseCampaignReach + finalFans
            val updatedSongs = state.songs.map {
                if (it.id == songId) it.copy(preReleaseCampaignPower = campaignPower, preReleaseCampaignReach = campaignReach) else it
            }

            showToast("Launched $finalName! Its awareness and reach will build over the release countdown. (-5% energy)")
            state.copy(
                cash = state.cash - finalCost,
                energy = (state.energy - 5).coerceAtLeast(0),
                songs = updatedSongs
            )
        }
    }

    fun startActiveSongPromo(songId: String, campaignType: String) {
        updateState { state ->
            val song = state.songs.find { it.id == songId }
            if (song == null) {
                showToast("Song not found!")
                return@updateState state
            }
            if (!song.isReleased) {
                showToast("This song is unreleased! Build pre-release campaign hype above first.")
                return@updateState state
            }

            val (cost, weeks, boost, label) = when (campaignType) {
                "dsp" -> listOf(2500.0, 4, 0.12f, "DSP Playlist Pitching & Social Ads")
                "radio" -> listOf(8500.0, 4, 0.25f, "Major Radio Airplay & Streaming Blitz")
                "global" -> listOf(25000.0, 6, 0.45f, "Global Media Push & Chart Campaign")
                else -> listOf(2500.0, 4, 0.12f, "Active Promo")
            }

            val finalCost = cost as Double
            val finalWeeks = weeks as Int
            val finalBoost = boost as Float
            val finalLabel = label as String

            if (state.cash < finalCost) {
                showToast("Insufficient cash! $finalLabel costs $${String.format("%,.0f", finalCost)}.")
                return@updateState state
            }

            val updatedSongs = state.songs.map {
                if (it.id == songId) it.copy(promoWeeksLeft = finalWeeks, promoWeeklyBoost = finalBoost) else it
            }

            showToast("Launched $finalLabel for '${song.title}' ($finalWeeks weeks active promotion, -5% energy)!")
            state.copy(
                cash = state.cash - finalCost,
                energy = (state.energy - 5).coerceAtLeast(0),
                songs = updatedSongs
            )
        }
    }

    fun promoteUnreleasedAlbum(albumId: String, activity: String, tierIndex: Int = 0) {
        updateState { state ->
            val album = state.albums.find { it.id == albumId }
            if (album == null) {
                showToast("Album not found!")
                return@updateState state
            }
            if (album.isReleased) {
                showToast("This album is already released! Run an Active Promo campaign below.")
                return@updateState state
            }

            val (cost, hypeBoost, fansGained, campaignName) = when (activity) {
                "social" -> when (tierIndex) {
                    1 -> listOf(2500.0, 18.0, 250.0, "Album Teaser Trend Challenge")
                    2 -> listOf(7500.0, 32.0, 1000.0, "Nationwide Album Countdown Blitz")
                    else -> listOf(500.0, 8.0, 50.0, "Social Album Announcement")
                }
                "video" -> when (tierIndex) {
                    1 -> listOf(5000.0, 25.0, 500.0, "Docu-Style Studio Mini-Series")
                    2 -> listOf(15000.0, 45.0, 2200.0, "Global Listening Party Live Stream")
                    else -> listOf(1000.0, 12.0, 100.0, "Behind-The-Scenes Snippet")
                }
                "ad" -> when (tierIndex) {
                    1 -> listOf(8000.0, 28.0, 1200.0, "Cross-Platform Streaming Banners")
                    2 -> listOf(25000.0, 50.0, 4500.0, "Times Square Billboard & DSP Takeover")
                    else -> listOf(1500.0, 14.0, 200.0, "Targeted Geo-Streaming Ads")
                }
                "radio" -> when (tierIndex) {
                    1 -> listOf(6000.0, 25.0, 900.0, "National Syndicated Radio Tour")
                    2 -> listOf(20000.0, 46.0, 3500.0, "Flagship Morning Show Feature Circuit")
                    else -> listOf(1200.0, 12.0, 150.0, "Regional Radio Station Interviews")
                }
                else -> listOf(500.0, 8.0, 40.0, "Album Promotion")
            }

            val finalCost = cost as Double
            val finalHype = (hypeBoost as Double).toFloat()
            val finalFans = (fansGained as Double).toLong()
            val finalName = campaignName as String

            if (state.cash < finalCost) {
                showToast("Insufficient cash! Costs $${String.format("%,.0f", finalCost)}.")
                return@updateState state
            }

            val newHype = (album.hype + finalHype).coerceAtMost(100f)
            val updatedAlbums = state.albums.map {
                if (it.id == albumId) it.copy(hype = newHype) else it
            }

            showToast("Launched $finalName! Album hype is now ${newHype.toInt()}%. (-5% energy)")
            state.copy(
                cash = state.cash - finalCost,
                energy = (state.energy - 5).coerceAtLeast(0),
                fans = state.fans + finalFans,
                albums = updatedAlbums
            )
        }
    }

    fun startActiveAlbumPromo(albumId: String, campaignType: String) {
        updateState { state ->
            val album = state.albums.find { it.id == albumId }
            if (album == null) {
                showToast("Album not found!")
                return@updateState state
            }

            val (cost, weeks, boost, label) = when (campaignType) {
                "dsp" -> listOf(3500.0, 4, 0.15f, "DSP Feature Pitch & Streaming Ads")
                "radio" -> listOf(12000.0, 4, 0.30f, "Heavy Radio Airplay & Syndicated Press")
                "global" -> listOf(35000.0, 6, 0.50f, "Global Billboard Blitz & Chart Push")
                else -> listOf(3500.0, 4, 0.15f, "Album Promo")
            }

            val finalCost = cost as Double
            val finalWeeks = weeks as Int
            val finalBoost = boost as Float
            val finalLabel = label as String

            if (state.cash < finalCost) {
                showToast("Insufficient cash! $finalLabel costs $${String.format("%,.0f", finalCost)}.")
                return@updateState state
            }

            val updatedAlbums = state.albums.map {
                if (it.id == albumId) it.copy(promoWeeksLeft = finalWeeks, promoWeeklyBoost = finalBoost) else it
            }

            showToast("Launched $finalLabel for '${album.title}' ($finalWeeks weeks active promotion, -5% energy)!")
            state.copy(
                cash = state.cash - finalCost,
                energy = (state.energy - 5).coerceAtLeast(0),
                albums = updatedAlbums
            )
        }
    }

    fun promoteSongCastSocial(songId: String, tierIndex: Int = 0) = promoteUnreleasedSong(songId, "social", tierIndex)
    fun promoteSongMeTubeTeaser(songId: String, tierIndex: Int = 0) = promoteUnreleasedSong(songId, "video", tierIndex)
    fun promoteSongAdCampaign(songId: String, tierIndex: Int = 0) = promoteUnreleasedSong(songId, "ad", tierIndex)
    fun promoteSongRadioInterview(songId: String, tierIndex: Int = 0) = promoteUnreleasedSong(songId, "radio", tierIndex)

    fun promoteTour(tourId: String, amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Not enough cash to promote tour!")
                state
            } else {
                val updatedTours = state.tours.map {
                    if (it.id == tourId) it.copy(promotionBudget = it.promotionBudget + amount) else it
                }
                showToast("Tour promoted! (-5% energy)")
                state.copy(
                    cash = state.cash - amount,
                    energy = (state.energy - 5).coerceAtLeast(0),
                    tours = updatedTours
                )
            }
        }
    }

    fun buyPhone(modelName: String, cost: Double) {
        updateState { state ->
            if (state.ownedPhones.contains(modelName)) {
                showToast("You already own $modelName.")
                state
            } else if (state.cash < cost) {
                showToast("Insufficient cash to buy $modelName.")
                state
            } else {
                showToast("Successfully purchased $modelName!")
                val newJournal = state.spiritual.spiritualJournal.filter { it != "active_phone_theft" }
                val newOwned = state.ownedPhones + modelName
                state.copy(
                    cash = state.cash - cost,
                    isPhoneLocked = false,
                    spiritual = state.spiritual.copy(spiritualJournal = newJournal),
                    ownedPhones = newOwned,
                    activePhone = modelName
                )
            }
        }
    }

    fun equipPhone(modelName: String) {
        updateState { state ->
            if (state.ownedPhones.contains(modelName)) {
                showToast("Equipped $modelName")
                state.copy(activePhone = modelName)
            } else {
                showToast("You don't own $modelName")
                state
            }
        }
    }

    fun upgradePlatform(platformId: String, cost: Double) {
        updateState { state ->
            if (state.upgradedPlatforms.contains(platformId)) {
                showToast("Already upgraded!")
                state
            } else if (state.cash < cost) {
                showToast("Not enough cash to upgrade!")
                state
            } else {
                showToast("Upgraded $platformId successfully!")
                state.copy(cash = state.cash - cost, upgradedPlatforms = state.upgradedPlatforms + platformId)
            }
        }
    }

    fun askOutCapitalClubMember(memberId: String) {
        updateState { state ->
            if (state.energy < 10) {
                showToast("Too exhausted to ask someone out right now (10% energy needed)!")
                return@updateState state
            }
            val member = state.capitalClubMembers.find { it.id == memberId } ?: return@updateState state
            if (member.isLockedOut) return@updateState state
            val currentPartners = state.capitalClubMembers.filter { it.isDating || it.isSpouse }
            var updatedMembers = state.capitalClubMembers
            if (currentPartners.isNotEmpty()) {
                updatedMembers = updatedMembers.map {
                    if (it.isDating || it.isSpouse) it.copy(relationshipScore = maxOf(0, it.relationshipScore - 60)) else it
                }
            }
            // Advanced Crush App AI Logic
            var accepted = false
            var responseMsg = "Hey, thanks for reaching out, but I don't think we're a match right now."

            // Rejections based on specific member tags and player stats
            if (member.tags.contains("Finance") || member.tags.contains("Wealth") || member.tag == "WEALTH") {
                if (state.cash < 500000.0) {
                    responseMsg = "I only date in a certain tax bracket. No offense, but you're not there yet."
                } else {
                    accepted = true
                }
            } else if (member.tags.contains("Fame") || member.tags.contains("High Profile") || member.tag == "FAME") {
                if (state.popularity < 75f) {
                    responseMsg = "You're cute, but my PR team says you don't have enough followers for this to make sense."
                } else {
                    accepted = true
                }
            } else if (member.tags.contains("Creative") || member.tags.contains("Studio") || member.tag == "ART") {
                if (state.writingSkill < 60f || state.voiceSkill < 60f) {
                    responseMsg = "I need a partner who truly understands the depth of real art. I don't feel that from you."
                } else {
                    accepted = true
                }
            } else if (member.tags.contains("Style") || member.tag == "STYLE") {
                if (state.publicImage < 70f) {
                    responseMsg = "Your public image is a bit messy for my brand right now. Let's pass."
                } else {
                    accepted = true
                }
            } else if (state.negativeSentiment > 35f) {
                responseMsg = "I've seen what they're saying about you online... I can't be associated with that kind of drama."
            } else {
                accepted = java.util.Random().nextFloat() < 0.7f // 70% default chance if they don't have strict requirements or if met
                if (accepted) responseMsg = "Hey! I'd love to go out with you. Let's get to know each other better. :)"
            }

            val initialScore = if (accepted) 10 else 0
            val finalScore = if (state.activePhone == "hiPhone" && accepted) (initialScore * 1.15).toInt() else initialScore

            val newMembers = updatedMembers.map {
                if (it.id == memberId) {
                    it.copy(
                        isDating = accepted,
                        isLockedOut = !accepted,
                        relationshipScore = finalScore,
                        lastInteractionWeek = if (accepted) state.currentWeek else it.lastInteractionWeek,
                        lastInteractionYear = if (accepted) state.currentYear else it.lastInteractionYear
                    )
                } else it
            }
            val newChatContact = ChatConversation(
                npcId = member.id,
                npcName = member.name,

                unreadCount = 1,

                messages = listOf(
                    ChatMessage(
                        id = java.util.UUID.randomUUID().toString(),
                        senderId = member.id, senderName = member.name,
                        message = responseMsg,
                        timestamp = System.currentTimeMillis(),


                    )
                )
            )
            val updatedContacts = state.chatConversations.filter { it.npcId != member.id } + newChatContact
            state.copy(
                energy = (state.energy - 10).coerceAtLeast(0),
                capitalClubMembers = newMembers,
                chatConversations = updatedContacts
            )
        }
    }

    fun interactWithCapitalClubPartner(memberId: String, action: String) {
        updateState { state ->
            val member = state.capitalClubMembers.find { it.id == memberId } ?: return@updateState state
            if (!member.isDating && !member.isSpouse) return@updateState state
            val energyCost = when (action) {
                "propose" -> 15
                "go_on_date" -> 12
                "buy_gift" -> 6
                "ask_for_gift" -> 6
                else -> 5
            }
            if (state.energy < energyCost) {
                showToast("Too exhausted ($energyCost% energy needed)! Get some rest.")
                return@updateState state
            }
            var relationshipChange = 0
            var moneyChange = 0.0
            var responseMsg = ""
            when (action) {
                "propose" -> {
                    if (member.relationshipScore >= 90) {
                        responseMsg = "Yes! A million times yes! <3"
                        relationshipChange = 10
                    } else {
                        responseMsg = "I'm sorry, I don't think we're ready for that step yet..."
                        relationshipChange = -20
                    }
                }
                "buy_gift" -> {
                    if (state.banking.savingsBalance >= 5000.0) {
                        moneyChange = -5000.0
                        relationshipChange = 15
                        responseMsg = "Oh my goodness, you shouldn't have! I love it so much!"
                    } else {
                        responseMsg = "It's the thought that counts..."
                    }
                }
                "ask_for_gift" -> {
                    val outcome = PartnerRelationshipSimulation.evaluateGiftRequest(member)
                    relationshipChange = outcome.relationshipChange
                    moneyChange = outcome.cashReceived
                    responseMsg = outcome.response
                }
                "go_on_date" -> {
                    if (state.banking.savingsBalance >= 1000.0) {
                        moneyChange = -1000.0
                        relationshipChange = 5
                        responseMsg = "That was an amazing evening. Thank you."
                    } else {
                        responseMsg = "Maybe we can just stay in?"
                    }
                }
            }

            if (state.activePhone == "hiPhone" && relationshipChange > 0) {
                relationshipChange = (relationshipChange * 1.15).toInt()
            }

            var brokeUp = false
            val newMembers = state.capitalClubMembers.map {
                if (it.id == memberId) {
                    val newScore = (it.relationshipScore + relationshipChange).coerceIn(0, 100)
                    if (newScore < 15 && it.isDating && !it.isSpouse) {
                        brokeUp = true
                        responseMsg = "This isn't working out anymore. I think we should see other people. It's over."
                        it.copy(relationshipScore = 0, isDating = false, isSpouse = false, isLockedOut = true)
                    } else if (newScore < 10 && it.isSpouse) {
                        brokeUp = true
                        responseMsg = "I can't do this anymore. I'm contacting my lawyers for a divorce."
                        it.copy(relationshipScore = 0, isDating = false, isSpouse = false, isLockedOut = true)
                    } else {
                        it.copy(
                            relationshipScore = newScore,
                            isSpouse = if (action == "propose" && relationshipChange > 0) true else it.isSpouse,
                            lastInteractionWeek = state.currentWeek,
                            lastInteractionYear = state.currentYear
                        )
                    }
                } else it
            }
            val updatedContacts = state.chatConversations.map { contact ->
                if (contact.npcId == memberId && responseMsg.isNotEmpty()) {
                    contact.copy(

                        unreadCount = 1,
                        messages = contact.messages + ChatMessage(
                            id = java.util.UUID.randomUUID().toString(),
                            senderId = memberId, senderName = member.name,
                            message = responseMsg,
                            timestamp = System.currentTimeMillis(),


                        )
                    )
                } else contact
            }
            state.copy(
                energy = (state.energy - energyCost).coerceAtLeast(0),
                capitalClubMembers = newMembers,
                chatConversations = updatedContacts,
                banking = state.banking.copy(savingsBalance = state.banking.savingsBalance + moneyChange)
            )
        }
    }



}
