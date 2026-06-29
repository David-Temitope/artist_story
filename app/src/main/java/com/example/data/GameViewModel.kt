package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.gameStateDao()

    // Login/Auth State
    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _currentArtistName = MutableStateFlow<String?>(null)
    val currentArtistName: StateFlow<String?> = _currentArtistName.asStateFlow()

    // Observed Game State
    val gameState: StateFlow<GameStateEntity?> = _currentUserEmail
        .flatMapLatest { email ->
            if (email != null) dao.getGameState(email) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin State: all users
    val allGameStates: StateFlow<List<GameStateEntity>> = dao.getAllGameStates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly summary report log
    private val _weeklyReport = MutableStateFlow<String>("")
    val weeklyReport: StateFlow<String> = _weeklyReport.asStateFlow()

    // Custom alerts
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.emit(msg)
        }
    }

    fun loginOrCreateAccount(email: String, artistName: String) {
        viewModelScope.launch {
            val trimmedEmail = email.trim().lowercase()
            val trimmedName = artistName.trim()
            if (trimmedEmail.isEmpty() || trimmedName.isEmpty()) {
                showToast("Email and Name cannot be empty!")
                return@launch
            }

            _currentUserEmail.value = trimmedEmail
            _currentArtistName.value = trimmedName

            // Check if user exists
            val existing = dao.getGameState(trimmedEmail).first()
            if (existing == null) {
                // Initialize default game state
                val isAdmin = trimmedEmail == "davemcthunder@gmail.com" || trimmedEmail == "oluwasegundave001@gmail.com"
                
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
                    )
                }

                val defaultState = GameStateEntity(
                    email = trimmedEmail,
                    artistName = trimmedName,
                    isAdmin = isAdmin,
                    songs = emptyList(),
                    sideJobs = GameData.SIDE_JOBS,
                    stocks = GameData.STOCKS,
                    cryptos = GameData.CRYPTOS,
                    npcs = proceduralNPCs,
                    casualFans = 200,
                    hardcoreFans = 10,
                    collectors = 2,
                    concertGoers = 5,
                    internationalFans = 0,
                    onlineCommunity = 50,
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
                    followers = 1500,
                    industryFollowers = 50,
                    mediaAttention = 10f,
                    trendingScore = 5f,
                    conversationVolume = 120,
                    positiveSentiment = 60f,
                    negativeSentiment = 10f,
                    neutralSentiment = 30f,
                    stress = 20f,
                    burnout = 0f,
                    creativeInspiration = 100f,
                    motivation = 80f,
                    happiness = 75f,
                    workLifeBalance = 50f,
                    genreTrends = mapOf(
                        "genre_pop" to 1.0f,
                        "genre_hiphop" to 1.1f,
                        "genre_rock" to 0.9f,
                        "genre_rnb" to 1.0f,
                        "genre_electronic" to 1.0f,
                        "genre_country" to 0.8f,
                        "genre_jazz" to 0.7f,
                        "genre_indie" to 0.9f
                    ),
                    timelineEvents = listOf("Year 1, Week 1: Started your music career as an aspiring independent artist!"),
                    mailbox = listOf(
                        MailMessage(
                            id = "welcome_msg",
                            sender = "Music Council",
                            subject = "Welcome to Artist Story!",
                            body = "Welcome $trimmedName! Start by doing some Side Jobs in the Gigs section to earn cash, then rent a Studio to write your first hit song! Reach 45% influence on social media and 60% popularity to get Record Label deals.",
                            type = "NORMAL",
                            expiresWeek = 5,
                            expiresYear = 1
                        )
                    )
                )
                dao.insertGameState(defaultState)
                _weeklyReport.value = "Welcome to your musical journey, $trimmedName!"
            } else {
                _weeklyReport.value = "Loaded saved game for ${existing.artistName}."
            }
        }
    }

    fun logout() {
        _currentUserEmail.value = null
        _currentArtistName.value = null
    }

    // Helper to edit current state and save it
    fun updateState(block: (GameStateEntity) -> GameStateEntity) {
        val email = _currentUserEmail.value ?: return
        viewModelScope.launch {
            val current = dao.getGameState(email).first() ?: return@launch
            val updated = block(current)
            dao.insertGameState(updated)
        }
    }

    // Helper to update specific player's state (for admin)
    fun adminUpdateState(playerEmail: String, block: (GameStateEntity) -> GameStateEntity) {
        viewModelScope.launch {
            val current = dao.getGameState(playerEmail).first() ?: return@launch
            val updated = block(current)
            dao.insertGameState(updated)
        }
    }

    // --- GAME ACTIONS ---

    // 1. Practice & Skills
    fun practiceVoice() {
        updateState { state ->
            if (state.energy < 20) {
                showToast("Not enough energy (20 required)!")
                state
            } else {
                val bonus = Random.nextFloat() * 1.5f + 0.5f
                val newVoice = (state.voiceSkill + bonus).coerceAtMost(100f)
                showToast("Practiced singing! Voice skill increased by +${String.format("%.1f", bonus)}%")
                state.copy(
                    energy = state.energy - 20,
                    voiceSkill = newVoice,
                    practiceMissedWeeks = 0
                )
            }
        }
    }

    fun practiceWriting() {
        updateState { state ->
            if (state.energy < 20) {
                showToast("Not enough energy (20 required)!")
                state
            } else {
                val bonus = Random.nextFloat() * 1.5f + 0.5f
                val newWriting = (state.writingSkill + bonus).coerceAtMost(100f)
                showToast("Practiced songwriting! Writing skill increased by +${String.format("%.1f", bonus)}%")
                state.copy(
                    energy = state.energy - 20,
                    writingSkill = newWriting,
                    practiceMissedWeeks = 0
                )
            }
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
                    energy = state.energy - 15,
                    fans = newFans,
                    casualFans = (state.casualFans + casualGain).coerceAtLeast(0),
                    hardcoreFans = (state.hardcoreFans + hardcoreGain).coerceAtLeast(0),
                    onlineCommunity = (state.onlineCommunity + onlineGain).coerceAtLeast(0),
                    collectors = (state.collectors + collectorsGain).coerceAtLeast(0),
                    influence = newInfluence,
                    publicImage = newPublicImage,
                    stress = newStress,
                    songs = updatedSongs,
                    practiceMissedWeeks = 0
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
                        // Check remaining time slots
                        val currentUsed = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
                        if (currentUsed + job.timeCost > state.maxTimeSlots) {
                            showToast("Not enough time slots available! Quit other side jobs or active tours.")
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

    // 4. Studio - Creating Songs & Music Videos
    fun createSong(title: String, budget: Double, genreId: String, producerId: String?) {
        updateState { state ->
            if (state.energy < 20) {
                showToast("Not enough energy (20 required) to record in the studio!")
                return@updateState state
            }
            if (state.cash < budget) {
                showToast("Insufficient cash for studio budget!")
                return@updateState state
            }
            val producer = GameData.PRODUCERS.find { it.id == producerId }
            var prodCost = producer?.cost ?: 0.0
            
            // Respect Discount: High industry respect makes producers eager to work with you!
            if (state.industryRespect > 70f && prodCost > 0.0) {
                prodCost *= 0.80 // 20% discount
            }
            val totalCost = budget + prodCost

            if (state.cash < totalCost) {
                showToast("Insufficient cash to pay producer + studio session!")
                return@updateState state
            }

            val genre = GameData.GENRES.find { it.id == genreId } ?: GameData.GENRES[0]

            // Calculate song rating with mental health and experience modifiers
            val instrumentBonus = state.ownedInstruments.sumOf { it.ratingBonus.toDouble() }.toFloat()
            val prodBonus = producer?.ratingBonus ?: 0f
            val skillBase = (state.voiceSkill + state.writingSkill) / 2f

            // Budget effect: sweet spot around $5000, but high budget gives exponential boost
            val budgetEffect = (budget / 5000.0).coerceAtMost(1.8).toFloat()

            // Experience and Mental multipliers (burnout severely chokes creative rating)
            val expBonus = (state.songs.size * 0.05f).coerceAtMost(0.6f)
            val mentalMultiplier = (state.creativeInspiration / 100f) * (1f - (state.burnout / 100f) * 0.5f)
            
            // Hired Music Producer workforce boost
            val hiredProducer = state.hiredEmployees.find { it.role == "Music Producer" }
            val hiredProducerBonus = if (hiredProducer != null && hiredProducer.loyalty > 25f) (0.25f + (hiredProducer.experience / 300f)) else 0f

            // Formula for star rating 1.0 to 5.0
            val ratingCalculated = (((skillBase / 25f) + prodBonus + instrumentBonus + budgetEffect + expBonus + hiredProducerBonus) * mentalMultiplier)
            val finalRating = ratingCalculated.coerceIn(1.0f, 5.0f)

            val newSong = Song(
                id = UUID.randomUUID().toString(),
                title = title.trim().ifEmpty { "Unreleased Hit" },
                genreId = genreId,
                budget = totalCost,
                producerId = producerId,
                rating = finalRating,
                weekCreated = state.currentWeek,
                yearCreated = state.currentYear
            )

            // Timeline Event & Reputation Boost
            val isHit = finalRating >= 4.2f
            val milestoneMsg = "Year ${state.currentYear}, Week ${state.currentWeek}: Recorded the song '${newSong.title}' and rated it ${String.format("%.1f", finalRating)} stars!"
            val updatedTimeline = state.timelineEvents + milestoneMsg + listOfNotNull(
                if (isHit) "Year ${state.currentYear}, Week ${state.currentWeek}: 🔥 hit single alert! '${newSong.title}' was highly acclaimed by critics!" else null
            )

            val newInspiration = (state.creativeInspiration - 25f).coerceAtLeast(10f) // Song creation drains inspiration
            val newStress = (state.stress + 10f).coerceAtMost(100f)

            // Update genre trends: a hit song elevates its genre's popularity!
            val updatedTrends = state.genreTrends.toMutableMap()
            if (isHit) {
                updatedTrends[genreId] = (updatedTrends[genreId] ?: 1.0f + 0.15f).coerceAtMost(2.5f)
            }

            showToast("Recorded song '${newSong.title}' with rating ${String.format("%.1f", finalRating)}⭐!")
            state.copy(
                energy = state.energy - 20,
                cash = state.cash - totalCost,
                songs = state.songs + newSong,
                popularity = (state.popularity + finalRating * 1.5f).coerceAtMost(100f),
                industryRespect = (state.industryRespect + (if (isHit) 6f else 1.5f)).coerceAtMost(100f),
                criticalAcclaim = (state.criticalAcclaim + (if (isHit) 8f else 1.5f)).coerceAtMost(100f),
                creativeInspiration = newInspiration,
                stress = newStress,
                timelineEvents = updatedTimeline,
                genreTrends = updatedTrends
            )
        }
    }

    fun createMusicVideo(songId: String, tier: String) {
        val cost = when (tier) {
            "Low" -> 2000.0
            "Standard" -> 5000.0
            "High" -> 10000.0
            "Premium" -> 20000.0
            "Cinematic" -> 50000.0
            else -> 0.0
        }

        val popularityBonus = when (tier) {
            "Low" -> 5f
            "Standard" -> 15f
            "High" -> 30f
            "Premium" -> 50f
            "Cinematic" -> 100f
            else -> 0f
        }

        updateState { state ->
            if (state.cash < cost) {
                showToast("Insufficient cash for this music video tier!")
                return@updateState state
            }

            val song = state.songs.find { it.id == songId }
            if (song == null) {
                showToast("Song not found!")
                return@updateState state
            }

            val updatedSongs = state.songs.map { s ->
                if (s.id == songId) {
                    s.copy(videoTier = tier)
                } else s
            }

            showToast("Created a $tier Music Video for '${song.title}'! Popularity increased!")
            state.copy(
                cash = state.cash - cost,
                songs = updatedSongs,
                popularity = (state.popularity + popularityBonus).coerceAtMost(100f)
            )
        }
    }

    // 5. Song Platform Upload
    fun uploadSong(songId: String, platformId: String) {
        val uploadCost = when (platformId) {
            "tunes" -> 0.0
            "sharp" -> 500.0
            "jams" -> 700.0
            "megaphone" -> 1000.0
            "songify" -> 1500.0
            else -> 0.0
        }

        updateState { state ->
            if (state.cash < uploadCost) {
                showToast("Insufficient cash to upload to this platform!")
                return@updateState state
            }

            val song = state.songs.find { it.id == songId }
            if (song == null) {
                showToast("Song not found!")
                return@updateState state
            }

            if (song.isUploadedMap.containsKey(platformId)) {
                showToast("Song is already uploaded on this platform!")
                return@updateState state
            }

            val updatedSongs = state.songs.map { s ->
                if (s.id == songId) {
                    val map = s.isUploadedMap.toMutableMap()
                    map[platformId] = 0L // Initialize listeners at 0
                    s.copy(isUploadedMap = map)
                } else s
            }

            showToast("Successfully uploaded '${song.title}' to ${platformId.uppercase()}!")
            state.copy(
                cash = state.cash - uploadCost,
                songs = updatedSongs
            )
        }
    }

    // 6. Tours Management
    fun bookTour(level: String, promoBudget: Double) {
        val cost = when (level) {
            "Simple" -> 2000.0
            "Pro" -> 5000.0
            "Premium" -> 10000.0
            else -> 0.0
        }

        updateState { state ->
            // Apply car reduction to cost
            val reduction = state.ownedCars.maxByOrNull { it.costReductionPercent }?.costReductionPercent ?: 0f
            val labelReduction = if (state.signedLabelId != null) 0.50f else 0f // label covers 50%
            val finalSetupCost = cost * (1f - reduction - labelReduction).coerceAtLeast(0.1f) + promoBudget

            if (state.cash < finalSetupCost) {
                showToast("Insufficient cash to book this tour!")
                return@updateState state
            }

            val totalWeeks = when (level) {
                "Simple" -> 2
                "Pro" -> 6
                "Premium" -> 15
                else -> 1
            }

            val energyCost = when (level) {
                "Simple" -> 15
                "Pro" -> 25
                "Premium" -> 40
                else -> 10
            }

            val timeCost = when (level) {
                "Simple" -> 2
                "Pro" -> 3
                "Premium" -> 5
                else -> 1
            }

            val payPerAttendee = when (level) {
                "Simple" -> 5.0
                "Pro" -> 15.0
                "Premium" -> 15.0 // pays $15 per attendee
                else -> 5.0
            }

            // Check if player has time slots available
            val activeSlots = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost } +
                    state.tours.filter { it.status == "ACTIVE" }.sumOf { it.weeklyTimeCost }
            if (activeSlots + timeCost > state.maxTimeSlots) {
                showToast("Insufficient free time slots to book this tour! Current slots used: $activeSlots / ${state.maxTimeSlots}")
                return@updateState state
            }

            val newTour = Tour(
                id = UUID.randomUUID().toString(),
                level = level,
                totalWeeks = totalWeeks,
                currentWeek = 0,
                weeklyEnergyCost = energyCost,
                weeklyTimeCost = timeCost,
                setupCost = finalSetupCost,
                payPerAttendee = payPerAttendee,
                promotionBudget = promoBudget,
                status = "BOOKED",
                bookedWeek = state.currentWeek,
                bookedYear = state.currentYear
            )

            showToast("Booked $level Tour for $totalWeeks weeks starting next week!")
            state.copy(
                cash = state.cash - finalSetupCost,
                tours = state.tours + newTour
            )
        }
    }

    fun promoteTour(tourId: String, amount: Double) {
        updateState { state ->
            if (state.cash < amount) {
                showToast("Insufficient cash!")
                state
            } else {
                val updatedTours = state.tours.map { t ->
                    if (t.id == tourId) {
                        t.copy(promotionBudget = t.promotionBudget + amount)
                    } else t
                }
                showToast("Invested $${amount} in promoting tour!")
                state.copy(
                    cash = state.cash - amount,
                    tours = updatedTours
                )
            }
        }
    }

    // 7. Trading
    fun buyStock(stockId: String, qty: Int) {
        updateState { state ->
            val stock = GameData.STOCKS.find { it.id == stockId } ?: return@updateState state
            val cost = stock.currentPrice * qty
            if (state.cash < cost) {
                showToast("Insufficient cash to purchase stocks!")
                return@updateState state
            }

            val updatedStocks = state.stocks.map { s ->
                if (s.id == stockId) s.copy(quantity = s.quantity + qty) else s
            }

            showToast("Bought $qty shares of ${stock.name}!")
            state.copy(
                cash = state.cash - cost,
                stocks = updatedStocks,
                businessSkill = (state.businessSkill + 0.5f).coerceAtMost(100f)
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

            val payout = stock.currentPrice * qty
            val updatedStocks = state.stocks.map { s ->
                if (s.id == stockId) s.copy(quantity = s.quantity - qty) else s
            }

            // Check if profit made
            val basePrice = GameData.STOCKS.find { it.id == stockId }?.currentPrice ?: stock.currentPrice
            val isProfit = stock.currentPrice >= basePrice
            val skillChange = if (isProfit) 1.5f else -1.0f

            showToast("Sold $qty shares of ${stock.name} for $${String.format("%.2f", payout)}!")
            state.copy(
                cash = state.cash + payout,
                stocks = updatedStocks,
                businessSkill = (state.businessSkill + skillChange).coerceIn(10f, 100f)
            )
        }
    }

    fun buyCrypto(cryptoId: String, amountUsd: Double) {
        updateState { state ->
            if (state.cash < amountUsd) {
                showToast("Insufficient cash!")
                return@updateState state
            }
            val crypto = GameData.CRYPTOS.find { it.id == cryptoId } ?: return@updateState state
            val buyQty = amountUsd / crypto.currentPrice

            val updatedCryptos = state.cryptos.map { c ->
                if (c.id == cryptoId) c.copy(quantity = c.quantity + buyQty) else c
            }

            showToast("Bought ${String.format("%.4f", buyQty)} of ${crypto.name}!")
            state.copy(
                cash = state.cash - amountUsd,
                cryptos = updatedCryptos,
                businessSkill = (state.businessSkill + 0.5f).coerceAtMost(100f)
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

            val payout = crypto.currentPrice * qtyToSell
            val updatedCryptos = state.cryptos.map { c ->
                if (c.id == cryptoId) c.copy(quantity = c.quantity - qtyToSell) else c
            }

            // Simple profit calculation (above base/market standard)
            val isProfit = Random.nextBoolean() // crypto is risky!
            val skillChange = if (isProfit) 2.0f else -1.5f

            showToast("Sold ${String.format("%.4f", qtyToSell)} of ${crypto.name} for $${String.format("%.2f", payout)}!")
            state.copy(
                cash = state.cash + payout,
                cryptos = updatedCryptos,
                businessSkill = (state.businessSkill + skillChange).coerceIn(10f, 100f)
            )
        }
    }

    // 8. Record Label System
    fun createOwnRecordLabel(name: String) {
        updateState { state ->
            if (state.cash < 5000000.0 || state.popularity < 70f || state.influence < 80f) {
                showToast("Requirements not met: Must have $5M, 70% popularity, 80% influence!")
                return@updateState state
            }

            val updatedCash = state.cash - 2000000.0 // Costs $2M
            val newLabel = OwnRecordLabel(name = name)

            showToast("Created own Record Label: '$name'!")
            state.copy(
                cash = updatedCash,
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
            val playerState = dao.getGameState(playerEmail).first() ?: return@launch
            val updatedRequests = playerState.purchaseRequests.map { req ->
                if (req.id == requestId) req.copy(status = "APPROVED") else req
            }

            val req = playerState.purchaseRequests.find { it.id == requestId } ?: return@launch
            if (req.status == "APPROVED") return@launch

            val (newCash, newMaxSlots, newMaxEnergy) = when (req.type) {
                "CASH" -> Triple(playerState.cash + req.amount, playerState.maxTimeSlots, playerState.maxEnergy)
                "TIME_SLOT" -> Triple(playerState.cash, playerState.maxTimeSlots + req.amount, playerState.maxEnergy)
                "ENERGY" -> Triple(playerState.cash, playerState.maxTimeSlots, playerState.maxEnergy + req.amount)
                else -> Triple(playerState.cash, playerState.maxTimeSlots, playerState.maxEnergy)
            }

            val updatedPlayer = playerState.copy(
                cash = newCash,
                maxTimeSlots = newMaxSlots,
                maxEnergy = newMaxEnergy,
                purchaseRequests = updatedRequests
            )

            dao.insertGameState(updatedPlayer)
            showToast("Approved request for $playerEmail!")
        }
    }

    fun adminRejectRequest(playerEmail: String, requestId: String) {
        viewModelScope.launch {
            val playerState = dao.getGameState(playerEmail).first() ?: return@launch
            val updatedRequests = playerState.purchaseRequests.map { req ->
                if (req.id == requestId) req.copy(status = "REJECTED") else req
            }

            dao.insertGameState(playerState.copy(purchaseRequests = updatedRequests))
            showToast("Rejected request for $playerEmail!")
        }
    }

    // Restart game
    fun restartGame() {
        updateState { state ->
            state.copy(
                cash = 10000.0,
                fans = 0,
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
                ownedCars = emptyList(),
                ownedHouses = emptyList(),
                ownedInstruments = emptyList(),
                songs = emptyList(),
                tours = emptyList(),
                sideJobs = GameData.SIDE_JOBS,
                stocks = GameData.STOCKS,
                cryptos = GameData.CRYPTOS,
                ownLabel = null,
                signedLabelId = null,
                signedLabelCut = 0.0f,
                mailbox = listOf(
                    MailMessage(
                        id = "restart_msg",
                        sender = "System",
                        subject = "Career Restarted",
                        body = "Your career has been restarted. Good luck reaching the stars this time!",
                        type = "NORMAL",
                        expiresWeek = 5,
                        expiresYear = 1
                    )
                )
            )
        }
        _weeklyReport.value = "Your artist story was successfully restarted!"
    }


    // --- WEEKLY TICK ENGINE ("NEXT WEEK") ---
    fun progressToNextWeek() {
        updateState { state ->
            val reportBuilder = java.lang.StringBuilder()
            reportBuilder.append("=== WEEK ${state.currentWeek} SUMMARY (YEAR ${state.currentYear}) ===\n")

            // Initialize all mutable stats at the top for unified system integration
            var updatedCash = state.cash
            var currentStress = state.stress
            var currentBurnout = state.burnout
            var currentInspiration = state.creativeInspiration
            var currentMotivation = state.motivation
            var currentHappiness = state.happiness
            var currentWorkLifeBalance = state.workLifeBalance
            
            var currentIndustryRespect = state.industryRespect
            var currentPublicImage = state.publicImage
            var currentCriticalAcclaim = state.criticalAcclaim
            var currentTrustworthiness = state.trustworthiness
            var currentLegacyPoints = state.legacyPoints

            var currentBusinessReputation = state.businessReputation
            var currentDiscipline = state.discipline
            var currentIntegrity = state.integrity
            var currentGenerosity = state.generosity
            var currentHumility = state.humility
            var currentReliability = state.reliability
            
            var updatedVoice = state.voiceSkill
            var updatedWriting = state.writingSkill
            var updatedInfluence = state.influence

            // 1. Check & increment week
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

            // Shifting economic cycle (10% chance per week)
            if (Random.nextFloat() < 0.10f) {
                val cycles = listOf("Boom", "Stable Growth", "Inflation Peak", "Recession", "Depression")
                economyCycle = cycles.random()
                when (economyCycle) {
                    "Boom" -> {
                        inflationRate = 0.05f
                        fuelPrice = 1.75f
                        touristIndex = 140
                        reportBuilder.append("📈 ECONOMIC BOOM: Markets are surging! Luxury sales are soaring, tourist traffic is up (+40%), but gas prices jumped to $1.75/L.\n")
                    }
                    "Stable Growth" -> {
                        inflationRate = 0.02f
                        fuelPrice = 1.25f
                        touristIndex = 100
                        reportBuilder.append("🌍 ECONOMIC STABILITY: The economy is growing steadily. Gas prices are normal at $1.25/L.\n")
                    }
                    "Inflation Peak" -> {
                        inflationRate = 0.08f
                        fuelPrice = 2.10f
                        touristIndex = 90
                        reportBuilder.append("🚨 INFLATION SPIKE: Prices are climbing rapidly! Upkeep costs are high, fuel costs $2.10/L. Employees may demand wage adjustments.\n")
                    }
                    "Recession" -> {
                        inflationRate = 0.005f
                        fuelPrice = 1.05f
                        touristIndex = 65
                        reportBuilder.append("📉 RECESSION ALERT: Consumer spending has crashed (-35%). Merch revenue, show turnout, and asset values have declined. Gas fell to $1.05/L.\n")
                    }
                    "Depression" -> {
                        inflationRate = -0.015f
                        fuelPrice = 0.85f
                        touristIndex = 40
                        reportBuilder.append("💀 DEPRESSION ALERT: Extreme economic downturn. Ticket demand is down 50%. High stress, but gas is dirt cheap at $0.85/L.\n")
                    }
                }
            }

            // Simulate election season (5% weekly chance, lasts 3 weeks)
            if (!isElectionSeason && Random.nextFloat() < 0.05f) {
                isElectionSeason = true
                reportBuilder.append("🗳️ ELECTION SEASON: Fictional general elections have begun! High media clutter. Ad rates increased, but public interest in entertainment has shifted.\n")
            } else if (isElectionSeason && Random.nextFloat() < 0.35f) {
                isElectionSeason = false
                reportBuilder.append("🗳️ ELECTION WRAPUP: Election season is officially over. Media rates normalized.\n")
            }

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
                "Rainy" -> reportBuilder.append("🌧️ WEATHER REPORT: Light rain throughout the region. Outdoor gig turnouts could face a slight -15% drag.\n")
                "Heavy Storm" -> reportBuilder.append("⚡ WEATHER REPORT: Torrential storms! Road travel is slow, outdoor gigs see -45% turnout, but people stayed home streaming (+25% MeTube views)!\n")
                "Heatwave" -> reportBuilder.append("☀️ WEATHER REPORT: Extreme Heatwave alert! Higher exhaustion risk. Conserving energy is recommended.\n")
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

            // 1c. Manage Genre Trends
            val currentTrends = state.genreTrends.toMutableMap()
            if (currentTrends.isEmpty()) {
                GameData.GENRES.forEach { currentTrends[it.id] = 1.0f }
            }
            if (Random.nextFloat() > 0.85f) {
                val randomGenreId = currentTrends.keys.toList().random()
                val trendShift = Random.nextFloat() * 0.4f - 0.15f
                val previousTrend = currentTrends[randomGenreId] ?: 1.0f
                val newTrend = (previousTrend + trendShift).coerceIn(0.4f, 2.5f)
                currentTrends[randomGenreId] = newTrend
                val genreName = GameData.GENRES.find { it.id == randomGenreId }?.name ?: "Unknown"
                if (trendShift > 0.1f) {
                    reportBuilder.append("📈 Trend Alert: $genreName music is surging globally! Released songs in this genre gain massive streaming boosts.\n")
                } else if (trendShift < -0.05f) {
                    reportBuilder.append("📉 Trend Alert: $genreName music interest is dropping in mainstream markets.\n")
                }
            }

            // 2. Resource & Time Deductions
            val sideJobCost = state.sideJobs.filter { it.isActive }.sumOf { it.timeCost }
            val activeTours = state.tours.filter { it.status == "ACTIVE" }
            val tourSlotsCost = activeTours.sumOf { it.weeklyTimeCost }
            val totalUsedSlots = sideJobCost + tourSlotsCost

            val jobEnergyCost = state.sideJobs.filter { it.isActive }.sumOf { it.energyCost }
            
            // Tour Manager reduces energy costs of touring
            val hasTourManager = state.hiredEmployees.any { it.role == "Tour Manager" && it.loyalty > 25f }
            val tourEnergyDiscount = if (hasTourManager) 0.65f else 1.0f
            val tourEnergyCost = (activeTours.sumOf { it.weeklyEnergyCost } * tourEnergyDiscount).toInt()
            
            val totalEnergyDeducted = (jobEnergyCost + tourEnergyCost - calendarBonusEnergy).coerceAtLeast(0)

            val paRestBonus = if (state.hiredEmployees.any { it.role == "Personal Assistant" }) 15 else 0
            val newEnergy = (state.maxEnergy - totalEnergyDeducted + paRestBonus).coerceIn(0, state.maxEnergy)
            if (newEnergy == 0 && totalEnergyDeducted > 0) {
                reportBuilder.append("⚠️ EXHAUSTED: You worked past your limits. Extreme fatigue caused your voice and writing skills to decay by 3%!\n")
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
            val houseMaintenance = state.ownedHouses.sumOf { 
                val isInsured = state.insuredHouses.contains(it.id)
                val rate = if (isInsured) 0.009 else 0.005
                it.cost * rate 
            }
            val totalMaintenance = carMaintenance + houseMaintenance
            val totalWeeklyOutflow = baseCostOfLiving + totalMaintenance + mortgagePayment
            
            updatedCash -= totalWeeklyOutflow
            reportBuilder.append("🏠 Weekly Expenses: Paid $${String.format("%,.2f", baseCostOfLiving)} basic living costs (reflecting Year ${state.currentYear} inflation).\n")
            if (totalMaintenance > 0.0) {
                reportBuilder.append("🔧 Asset Upkeep: Paid $${String.format("%,.2f", totalMaintenance)} for car/house maintenance.\n")
            }

            // Progressive Year-End Taxes
            var newTaxDebt = state.taxDebt
            if (nextWeek == 1) {
                val assetValue = state.ownedCars.sumOf { it.cost } + state.ownedHouses.sumOf { it.cost } + state.ownedInstruments.sumOf { it.cost }
                val taxRate = when {
                    state.cash > 1000000.0 -> 0.35
                    state.cash > 250000.0 -> 0.25
                    state.cash > 50000.0 -> 0.15
                    else -> 0.08
                }
                val taxDue = (state.cash * taxRate) + (assetValue * 0.01) // Progressive tax + asset wealth tax
                newTaxDebt += taxDue
                reportBuilder.append("📉 TAX SEASON: Progressive tax assessment of Year ${state.currentYear} completed. Tax calculated: $${String.format("%,.2f", taxDue)}. Settle this debt in the Banking app to avoid weekly 3% penalties!\n")
            }

            // Accrue savings interest
            var newSavingsBalance = state.savingsBalance
            if (newSavingsBalance > 0.0) {
                val interestEarned = newSavingsBalance * state.savingsInterestRate
                newSavingsBalance += interestEarned
                reportBuilder.append("🏦 BANKING: Earned +$${String.format("%,.2f", interestEarned)} weekly interest on your Savings Account.\n")
            }

            // Accrue loan interest
            var newLoanAmount = state.loanAmount
            if (newLoanAmount > 0.0) {
                val interestAccrued = newLoanAmount * state.loanInterestRate
                newLoanAmount += interestAccrued
                reportBuilder.append("🚨 BANKING: Your active Bank Loan accrued +$${String.format("%,.2f", interestAccrued)} in weekly interest.\n")
            }

            // Unpaid tax penalties
            if (newTaxDebt > 0.0) {
                val lateFee = newTaxDebt * 0.03
                newTaxDebt += lateFee
                reportBuilder.append("⚠️ TAX DELINQUENCY: Surtax penalty of 3% (+$${String.format("%,.2f", lateFee)}) added to your outstanding Tax Debt.\n")
            }

            // Life Hazards & Emergencies (12% weekly probability)
            if (Random.nextFloat() < 0.12f) {
                val lawyer = state.hiredEmployees.find { it.role == "Lawyer" }
                val hasLawyer = lawyer != null && lawyer.loyalty > 25f
                val incidents = listOf(
                    {
                        updatedCash -= 1200.0
                        currentStress = (currentStress + 18f).coerceAtMost(100f)
                        currentHappiness = (currentHappiness - 12f).coerceAtLeast(10f)
                        reportBuilder.append("🚨 EMERGENCY: Faced a sudden medical issue. Paid $1,200 in healthcare fees. Stress increased.\n")
                    },
                    {
                        val baseCost = 4000.0
                        val mitigation = if (hasLawyer) (0.5f + (lawyer!!.experience / 200f)).coerceIn(0.5f, 0.95f) else 0.0f
                        val actualCost = baseCost * (1f - mitigation)
                        updatedCash -= actualCost
                        currentTrustworthiness = (currentTrustworthiness - (12f * (1f - mitigation))).coerceAtLeast(10f)
                        currentPublicImage = (currentPublicImage - (8f * (1f - mitigation))).coerceAtLeast(10f)
                        currentStress = (currentStress + 15f).coerceAtMost(100f)
                        if (hasLawyer) {
                            reportBuilder.append("⚖️ DISPUTE RESOLVED: A musician asserted co-writing credits. Your Lawyer ${lawyer!!.name} intervened and settled out of court for $${String.format("%,.0f", actualCost)} instead of $$baseCost (Saved $${String.format("%,.0f", baseCost - actualCost)}!).\n")
                        } else {
                            reportBuilder.append("⚖️ DISPUTE: A musician asserted co-writing credits on your past tracks. Settled out of court for $4,000. Trustworthiness took a hit.\n")
                        }
                    },
                    {
                        updatedCash -= 2500.0
                        currentInspiration = (currentInspiration - 15f).coerceAtLeast(10f)
                        reportBuilder.append("🎙️ STUDIO ACCIDENT: A power surge damaged your studio monitors. Bought replacements for $2,500.\n")
                    },
                    {
                        currentPublicImage = (currentPublicImage + 15f).coerceAtMost(100f)
                        reportBuilder.append("🔥 VIRAL BOOST: A famous creator danced to your unreleased demo clip on social media! Public Image improved by +15%!\n")
                    },
                    {
                        val baseCost = 3500.0
                        val mitigation = if (hasLawyer) (0.6f + (lawyer!!.intelligence / 250f)).coerceIn(0.6f, 0.95f) else 0.0f
                        val actualCost = baseCost * (1f - mitigation)
                        updatedCash -= actualCost
                        currentIndustryRespect = (currentIndustryRespect - (8f * (1f - mitigation))).coerceAtLeast(10f)
                        if (hasLawyer) {
                            reportBuilder.append("📋 COMPLIANCE COMPLETED: Regulatory check discovered minor filing errors. Your Lawyer ${lawyer!!.name} handled audits and reduced compliance fine to $${String.format("%,.0f", actualCost)} instead of $$baseCost (Saved $${String.format("%,.0f", baseCost - actualCost)}!).\n")
                        } else {
                            reportBuilder.append("📋 COMPLIANCE: A regulatory check discovered minor business filing errors. Settled penalties of $3,500.\n")
                        }
                    }
                )
                incidents.random().invoke()
            }

            // 3b. Side Job Income
            var weeklySalary = 0.0
            state.sideJobs.filter { it.isActive }.forEach { job ->
                // Recession reduces gig pays
                val payoutMult = if (eventEffect == "RECESSION") 0.7 else 1.0
                weeklySalary += job.weeklyPay * payoutMult
            }
            if (weeklySalary > 0.0) {
                reportBuilder.append("💼 Side Jobs: Earned $${String.format("%,.2f", weeklySalary)} from active gigs.\n")
            }

            // 3b. Entertainment Empire Income (Owned Businesses)
            var businessRevenue = 0.0
            state.ownedBusinesses.forEach { biz ->
                businessRevenue += biz.weeklyProfit
            }
            if (businessRevenue > 0.0) {
                reportBuilder.append("🏢 Entertainment Empire: Your business portfolio generated $${String.format("%,.2f", businessRevenue)} in weekly profits!\n")
                weeklySalary += businessRevenue
            }

            // 4. Advanced Music Discovery: Update Songs streams and platform payouts
            var totalStreamsPayout = 0.0
            var viralHitOccurred = false
            var viralHitTitle = ""

            val updatedSongs = state.songs.map { song ->
                if (song.isUploadedMap.isNotEmpty()) {
                    val updatedMap = song.isUploadedMap.mapValues { (platformId, currentStreams) ->
                        val qualityBoost = when (song.videoTier) {
                            "Low" -> 1.2f
                            "Standard" -> 1.5f
                            "High" -> 2.1f
                            "Premium" -> 2.8f
                            "Cinematic" -> 3.6f
                            else -> 1.0f
                        }
                        
                        val trendMult = currentTrends[song.genreId] ?: 1.0f
                        
                        val eventMult = when (eventEffect) {
                            "LOCKDOWN" -> 1.4f // Streaming surges
                            "RECESSION" -> 0.8f // Streaming drops
                            else -> 1.0f
                        }

                        // Word of mouth based on critical review star rating
                        val wordOfMouth = when {
                            song.rating >= 4.5f -> 2.2f // Great review multiplier
                            song.rating >= 4.0f -> 1.4f
                            song.rating < 2.5f -> 0.4f // Negative reviews decay listening speed
                            else -> 1.0f
                        }

                        // Prestige multiplier from industry respect, public standing & positive/negative sentiment
                        val sentimentFactor = (state.positiveSentiment - state.negativeSentiment) / 100f
                        val sentimentMult = 1f + (sentimentFactor * 0.25f)
                        val prestigeFactor = (1f + (currentIndustryRespect / 100f) * 0.3f + (currentPublicImage / 100f) * 0.2f) * sentimentMult
                        
                        val baseGrowth = (state.fans * 0.015 + state.popularity * 10 + state.influence * 5).coerceAtLeast(10.0)
                        
                        // Viral Breakthrough Check (Extremely rare and exciting procedural event!)
                        val discoverabilityScore = (song.rating * 15) + (if (song.videoTier != null) 20 else 0) + (state.influence * 0.3)
                        val viralMultiplier = if (discoverabilityScore > 82 && Random.nextFloat() > 0.95f) {
                            viralHitOccurred = true
                            viralHitTitle = song.title
                            5.5f
                        } else {
                            1.0f
                        }

                        val finalWeeklyStream = (baseGrowth * song.rating * qualityBoost * trendMult * eventMult * wordOfMouth * prestigeFactor * viralMultiplier * (Random.nextFloat() * 0.3f + 0.85f)).toLong()
                        val newStreamsTotal = currentStreams + finalWeeklyStream

                        val rate = when (platformId) {
                            "tunes" -> 0.20
                            "sharp" -> 0.50
                            "jams" -> 1.50
                            "megaphone" -> 2.50
                            "songify" -> 3.00
                            else -> 0.0
                        }

                        val weeklyIncome = if (platformId == "tunes") {
                            (finalWeeklyStream / 100.0) * 20.0
                        } else {
                            finalWeeklyStream * rate
                        }

                        totalStreamsPayout += weeklyIncome
                        newStreamsTotal
                    }

                    song.copy(
                        isUploadedMap = updatedMap,
                        totalStreams = updatedMap.values.sum()
                    )
                } else song
            }

            if (viralHitOccurred) {
                reportBuilder.append("🚀 ALGORITHM BREAKTHROUGH: Your song '$viralHitTitle' broke the internet and went completely viral! Gained massive streaming surge!\n")
                currentPublicImage = (currentPublicImage + 15f).coerceAtMost(100f)
                currentIndustryRespect = (currentIndustryRespect + 10f).coerceAtMost(100f)
            }

            if (totalStreamsPayout > 0.0) {
                val labelCut = if (state.signedLabelId != null) totalStreamsPayout * state.signedLabelCut else 0.0
                val netIncome = totalStreamsPayout - labelCut
                reportBuilder.append("🎵 Music Income: Streaming platforms paid $${String.format("%,.2f", totalStreamsPayout)} gross.")
                if (labelCut > 0.0) {
                    reportBuilder.append(" Record label took their $${String.format("%,.2f", labelCut)} cut.")
                }
                reportBuilder.append("\n")
                weeklySalary += netIncome
            }

            // 5. Update Tours status & pay
            var tourPayout = 0.0
            var completedToursCount = 0
            val updatedTours = state.tours.map { tour ->
                val nextStatus = when {
                    tour.status == "BOOKED" && state.currentWeek >= tour.bookedWeek -> "ACTIVE"
                    tour.status == "ACTIVE" && tour.currentWeek >= tour.totalWeeks -> "COMPLETED"
                    else -> tour.status
                }

                if (nextStatus == "ACTIVE") {
                    val currentWeekPlus = tour.currentWeek + 1
                    val baseMaxAtt = when (tour.level) {
                        "Simple" -> 200
                        "Pro" -> 1000
                        "Premium" -> 20000
                        else -> 100
                    }

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
                        reportBuilder.append("⚠️ CONCERT COLLISION: A massive rival superstar held a surprise performance in your tour region this week! High local hotel rates and divided media attention.\n")
                        0.65f
                    } else 1.0f

                    // Public opinion / reputation impact on tour attendance
                    val reputationTourMult = when {
                        state.publicImage < 40f -> 0.55f
                        state.publicImage > 75f -> 1.35f
                        else -> 1.0f
                    }

                    val attVal = (baseMaxAtt * (popularityFactor * 0.6 + influenceFactor * 0.4 + promoEffect * 0.3) * socialPromoBonus * newsPromoBonus * eventTourMult * economyTourMult * weatherTourMult * collisionMult * reputationTourMult).toInt()
                    val finalAttendance = attVal.coerceIn(0, baseMaxAtt)

                    // Fuel price impacts tour transport costs
                    val baseTransportFuelSurcharge = ((fuelPrice - 1.25f) * 350.0).coerceAtLeast(0.0)
                    val earnings = (finalAttendance * tour.payPerAttendee) - baseTransportFuelSurcharge
                    tourPayout += earnings

                    if (eventEffect == "LOCKDOWN") {
                        reportBuilder.append("🎤 Tour (${tour.level} - Week $currentWeekPlus/${tour.totalWeeks}): Suspended due to Pandemic Lockdown! 0 attendees.\n")
                    } else {
                        reportBuilder.append("🎤 Tour (${tour.level} - Week $currentWeekPlus/${tour.totalWeeks}): $finalAttendance fans attended!")
                        if (baseTransportFuelSurcharge > 0.0) {
                            reportBuilder.append(" Gas surcharge subtracted -$${String.format("%.2f", baseTransportFuelSurcharge)}.")
                        }
                        reportBuilder.append(" Net tour pay: $${String.format("%,.2f", earnings)}.\n")
                    }

                    tour.copy(
                        currentWeek = currentWeekPlus,
                        baseAttendance = finalAttendance.coerceAtLeast(tour.baseAttendance),
                        status = if (currentWeekPlus >= tour.totalWeeks) "COMPLETED" else "ACTIVE"
                    )
                } else if (tour.status == "BOOKED") {
                    tour
                } else {
                    if (tour.status == "ACTIVE" && nextStatus == "COMPLETED") completedToursCount++
                    tour.copy(status = nextStatus)
                }
            }

            if (tourPayout > 0.0) {
                val labelCut = if (state.signedLabelId != null) tourPayout * state.signedLabelCut else 0.0
                val netTourPayout = tourPayout - labelCut
                if (labelCut > 0.0) {
                    reportBuilder.append("📋 Label took $${String.format("%,.2f", labelCut)} cut from tour earnings.\n")
                }
                weeklySalary += netTourPayout
            }

            // 6. Merch sales weekly
            val merchBaseSales = (state.fans * 0.01 + state.popularity * 2.5).coerceAtLeast(0.0)
            val eventMerchMult = if (eventEffect == "RECESSION") 0.6 else 1.0
            val merchRevenue = merchBaseSales * (Random.nextFloat() * 1.5 + 0.5) * eventMerchMult
            if (merchRevenue > 0.0) {
                reportBuilder.append("👕 Merch: Generated $${String.format("%,.2f", merchRevenue)} in merchandise sales.\n")
                weeklySalary += merchRevenue
            }

            // 7. Update Stock & Crypto Markets with historical charts
            val totalActiveToursInTown = updatedTours.count { it.status == "ACTIVE" } + (if (Random.nextBoolean()) 2 else 0)
            val updatedStocks = state.stocks.map { stock ->
                val priceChange = if (stock.connectedTo == "MediaGiant") {
                    (totalActiveToursInTown * 1.5 - Random.nextDouble(1.0, 4.0))
                } else {
                    Random.nextDouble(-3.0, 4.5)
                }
                val newPrice = (stock.currentPrice + priceChange).coerceAtLeast(1.0)
                val newHistory = (stock.history + newPrice).takeLast(10)

                if (stock.quantity > 0 && Random.nextFloat() > 0.3f) {
                    val divPayment = stock.quantity * newPrice * stock.weeklyDividendPercent
                    reportBuilder.append("📈 Stock: Received $${String.format("%,.2f", divPayment)} dividend from ${stock.name}.\n")
                    weeklySalary += divPayment
                }

                stock.copy(currentPrice = newPrice, history = newHistory)
            }

            val updatedCryptos = state.cryptos.map { crypto ->
                val volatilityPercent = Random.nextDouble(-12.0, 15.0) / 100.0
                val newPrice = (crypto.currentPrice * (1.0 + volatilityPercent)).coerceAtLeast(0.01)
                val newHistory = (crypto.history + newPrice).takeLast(10)
                crypto.copy(currentPrice = newPrice, history = newHistory)
            }

            // 7b. Exchange Limit & S&L Orders Execution
            val remainingStockOrders = mutableListOf<StockOrder>()
            val stocksModifiedMap = updatedStocks.associateBy { it.id }.toMutableMap()
            var currentWorkingCash = updatedCash

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
                            weeklySalary += revenue
                            stocksModifiedMap[order.stockId] = stock.copy(quantity = stock.quantity - order.quantity)
                            reportBuilder.append("🔔 Stock Exchange: LIMIT SELL triggered! Sold ${order.quantity} shares of ${stock.name} at $${String.format("%.2f", stock.currentPrice)}/share.\n")
                            executed = true
                        }
                    }
                    if (!executed) remainingStockOrders.add(order)
                }
            }
            var finalStocks = updatedStocks.map { stocksModifiedMap[it.id] ?: it }

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
                            weeklySalary += revenue
                            cryptosModifiedMap[order.cryptoId] = crypto.copy(quantity = crypto.quantity - order.quantity)
                            reportBuilder.append("🔔 Genesis Exchange: LIMIT SELL triggered! Sold ${String.format("%.4f", order.quantity)} of ${crypto.name} at $${String.format("%.2f", price)}.\n")
                            executed = true
                        }
                    } else if (order.type == "STOP_LOSS" && price <= order.targetPrice) {
                        if (crypto.quantity >= order.quantity) {
                            val revenue = price * order.quantity
                            weeklySalary += revenue
                            cryptosModifiedMap[order.cryptoId] = crypto.copy(quantity = crypto.quantity - order.quantity)
                            reportBuilder.append("🚨 Genesis Exchange: STOP LOSS triggered! Sold ${String.format("%.4f", order.quantity)} of ${crypto.name} at $${String.format("%.2f", price)} to limit loss.\n")
                            executed = true
                        }
                    } else if (order.type == "TAKE_PROFIT" && price >= order.targetPrice) {
                        if (crypto.quantity >= order.quantity) {
                            val revenue = price * order.quantity
                            weeklySalary += revenue
                            cryptosModifiedMap[order.cryptoId] = crypto.copy(quantity = crypto.quantity - order.quantity)
                            reportBuilder.append("💰 Genesis Exchange: TAKE PROFIT triggered! Sold ${String.format("%.4f", order.quantity)} of ${crypto.name} at $${String.format("%.2f", price)}.\n")
                            executed = true
                        }
                    }
                    if (!executed) remainingCryptoOrders.add(order)
                }
            }
            var finalCryptos = updatedCryptos.map { cryptosModifiedMap[it.id] ?: it }
            updatedCash = currentWorkingCash

            // 7c. MeTube Channel Updates & Revenue Calculation
            var newWatchTime = state.meTubeWatchTime
            var newSubscribers = state.meTubeSubscribers
            var meTubeEarnings = 0.0

            val updatedMeTubeVideos = state.meTubeVideos.map { video ->
                val baseSubscribersMult = 1.0 + (newSubscribers / 10000.0)
                val newViews = (Random.nextInt(50, 500) * baseSubscribersMult * (if (video.type == "MUSIC_VIDEO") 3.0 else 1.2)).toLong()
                val newLikes = (newViews * (0.05 + Random.nextFloat() * 0.08)).toLong()
                
                val revenue = newViews * 0.008
                meTubeEarnings += revenue

                video.copy(
                    views = video.views + newViews,
                    likes = video.likes + newLikes
                )
            }

            if (updatedMeTubeVideos.isNotEmpty()) {
                val gainedSubs = (updatedMeTubeVideos.sumOf { it.views } * 0.002).toLong().coerceAtLeast(5L)
                newSubscribers += gainedSubs
                newWatchTime += (updatedMeTubeVideos.size * 3.5)
                reportBuilder.append("📺 MeTube: Your video channel gained +${String.format("%,d", gainedSubs)} subscribers. Ad revenue earned: +$${String.format("%,.2f", meTubeEarnings)}.\n")
            }

            val updatedMail = state.mailbox.toMutableList()

            // 7d. Upgraded Employee AI Workforce Simulator
            val finalEmployees = mutableListOf<Employee>()
            var totalEmployeeSalary = 0.0
            
            // Business Manager contract negotiation (reduces overall salaries of other employees by 15%)
            val bizManager = state.hiredEmployees.find { it.role == "Business Manager" }
            val hasBizManager = bizManager != null && bizManager.loyalty > 30f
            val salaryNegDiscount = if (hasBizManager) (0.85f - (bizManager!!.negotiation / 1000f)).coerceIn(0.7f, 0.9f) else 1.0f

            state.hiredEmployees.forEach { emp ->
                val actualSalary = if (emp.role != "Business Manager") emp.weeklySalary * salaryNegDiscount else emp.weeklySalary
                totalEmployeeSalary += actualSalary
            }

            // Apply Business Manager Cost Cutting (upkeep & expenses reduced by 20%)
            if (hasBizManager) {
                val expenseSavings = totalWeeklyOutflow * 0.20
                updatedCash += expenseSavings
                reportBuilder.append("💼 Business Manager Efficiency: ${bizManager!!.name} cut corporate overhead, saving $${String.format("%,.2f", expenseSavings)} on asset upkeep and basic living costs!\n")
            }

            // Pay Employee Salaries
            var employeesUnpaid = false
            if (totalEmployeeSalary > 0.0) {
                if (updatedCash >= totalEmployeeSalary) {
                    updatedCash -= totalEmployeeSalary
                    reportBuilder.append("👥 Payroll Processed: Paid $${String.format("%,.2f", totalEmployeeSalary)} weekly salaries to your active staff.\n")
                    state.hiredEmployees.forEach { emp ->
                        val newLoyalty = (emp.loyalty + 1f + (emp.happiness / 50f)).coerceAtMost(100f)
                        val newWeeks = (emp.contractWeeks - 1).coerceAtLeast(0)
                        finalEmployees.add(emp.copy(loyalty = newLoyalty, contractWeeks = newWeeks))
                    }
                } else {
                    employeesUnpaid = true
                    currentBusinessReputation = (currentBusinessReputation - 10f).coerceAtLeast(0f)
                    currentReliability = (currentReliability - 8f).coerceAtLeast(0f)
                    reportBuilder.append("🚨 STAFF UNPAID: Insufficient funds to cover staff salaries ($${String.format("%,.2f", totalEmployeeSalary)})! Employees are furious!\n")
                    state.hiredEmployees.forEach { emp ->
                        val newLoyalty = (emp.loyalty - 20f)
                        val newWeeks = (emp.contractWeeks - 1).coerceAtLeast(0)
                        if (newLoyalty <= 0f) {
                            reportBuilder.append("💔 Resignation: Your ${emp.role}, ${emp.name}, has resigned immediately due to unpaid salaries!\n")
                        } else {
                            finalEmployees.add(emp.copy(loyalty = newLoyalty, contractWeeks = newWeeks))
                        }
                    }
                }
            } else {
                state.hiredEmployees.forEach { emp ->
                    val newWeeks = (emp.contractWeeks - 1).coerceAtLeast(0)
                    finalEmployees.add(emp.copy(contractWeeks = newWeeks))
                }
            }

            // Run Active Employee Work Duties & Role Behaviors
            val afterDutyEmployees = mutableListOf<Employee>()
            var accountantReportAdded = false

            // We make finalStocks and finalCryptos mutable so employees can trade automatically if enabled
            val mutStocks = finalStocks.toMutableList()
            val mutCryptos = finalCryptos.toMutableList()

            finalEmployees.forEach { emp ->
                var updatedEmp = emp
                val historyLog = emp.careerHistory.toMutableList()

                // 1. Contract Renegotiation Demands / Expiration
                if (emp.contractWeeks == 0 && !employeesUnpaid) {
                    val payDemandMult = 1.15 + (emp.ambition / 500.0)
                    val newSalaryDemand = emp.weeklySalary * payDemandMult
                    if (emp.loyalty < 45f) {
                        reportBuilder.append("💔 Contract Expired & Resigned: ${emp.name} (${emp.role}) refused to renew contract due to low loyalty and has resigned!\n")
                        return@forEach // Resigns, skip adding to active employees list
                    } else {
                        reportBuilder.append("💼 Contract Demands: ${emp.name} (${emp.role})'s contract expired. They negotiated a new contract at $${String.format("%.0f", newSalaryDemand)}/wk. Loyalty and happiness restored.\n")
                        updatedEmp = emp.copy(
                            weeklySalary = newSalaryDemand,
                            contractWeeks = Random.nextInt(10, 24),
                            loyalty = 85f,
                            happiness = 90f
                        )
                        historyLog.add("Week ${state.currentWeek}: Contract expired. Renegotiated salary to $${String.format("%.0f", newSalaryDemand)}/wk for a new term.")
                    }
                }

                // 2. Role Specific Behaviors
                when (emp.role) {
                    "Accountant" -> {
                        // Calculates financials and sends email report
                        val grossIncome = weeklySalary + totalStreamsPayout + tourPayout + merchRevenue + meTubeEarnings
                        val grossExpenses = totalWeeklyOutflow + totalEmployeeSalary
                        val netProfit = grossIncome - grossExpenses

                        var accountantError = 0.0
                        if (emp.intelligence < 55f && Random.nextFloat() < 0.20f) {
                            accountantError = 800.0 + (100 - emp.experience) * 15
                            updatedCash -= accountantError
                            reportBuilder.append("🚨 ACCOUNTING MISTAKE: ${emp.name} made an error filing tax reserves, costing an IRS penalty of $${String.format("%.0f", accountantError)}.\n")
                            historyLog.add("Week ${state.currentWeek}: Made an accounting calculation filing error, costing client $${String.format("%.0f", accountantError)}")
                        }

                        // Dishonest accountant embezzlement
                        var embezzledAmount = 0.0
                        if (emp.integrity < 40f && emp.loyalty < 55f && netProfit > 0.0) {
                            embezzledAmount = netProfit * 0.03
                            updatedCash -= embezzledAmount
                            reportBuilder.append("💸 FRAUD ALERT: Minor bookkeeping discrepancy found in weekly payouts. (Secretly embezzled $${String.format("%.0f", embezzledAmount)}).\n")
                            historyLog.add("Week ${state.currentWeek}: Secretly diverted cash reserves to personal swiss accounts.")
                        }

                        // Permissions: Auto Tax Reserve
                        var taxReservedAmount = 0.0
                        if (emp.permissions["auto_tax"] == true && netProfit > 0.0) {
                            taxReservedAmount = netProfit * 0.15
                        }

                        // Permissions: Auto Emergency Savings
                        var savingsReservedAmount = 0.0
                        if (emp.permissions["auto_savings"] == true && netProfit > 0.0) {
                            savingsReservedAmount = netProfit * 0.10
                        }

                        // Permissions: Auto stock buying (low price stock, max spending limit $2000)
                        var autoStockBoughtName = "None"
                        var autoStockBoughtCost = 0.0
                        if (emp.permissions["auto_stocks"] == true && updatedCash > 12000.0) {
                            val targetStock = mutStocks.filter { it.currentPrice < 150.0 }.minByOrNull { it.currentPrice }
                            if (targetStock != null) {
                                val qty = 15
                                val cost = targetStock.currentPrice * qty
                                if (updatedCash >= cost) {
                                    updatedCash -= cost
                                    autoStockBoughtCost = cost
                                    autoStockBoughtName = "${qty} shares of ${targetStock.name}"
                                    val updatedStockObj = targetStock.copy(quantity = targetStock.quantity + qty)
                                    val index = mutStocks.indexOfFirst { it.id == targetStock.id }
                                    if (index != -1) mutStocks[index] = updatedStockObj
                                    reportBuilder.append("🔔 Auto Investing: Accountant ${emp.name} automatically purchased $autoStockBoughtName for $${String.format("%.2f", cost)}!\n")
                                    historyLog.add("Week ${state.currentWeek}: Executed automatic stock portfolio purchase of $autoStockBoughtName.")
                                }
                            }
                        }

                        // Generate beautiful accountant weekly financial report in mail
                        val emailBody = """
                            Hi ${state.artistName},
                            
                            Here is the official weekly financial statement for Year ${state.currentYear}, Week ${state.currentWeek}:
                            
                            --- CASH FLOW STATEMENT ---
                            Gross Weekly Revenues: $${String.format("%,.2f", grossIncome)}
                              - Gigs & Gigs: $${String.format("%,.2f", weeklySalary)}
                              - Music Streaming (Net): $${String.format("%,.2f", totalStreamsPayout)}
                              - Concert Touring: $${String.format("%,.2f", tourPayout)}
                              - Merch & Goods Sales: $${String.format("%,.2f", merchRevenue)}
                              - MeTube Ad Revenue: $${String.format("%,.2f", meTubeEarnings)}
                            
                            Gross Weekly Expenditures: $${String.format("%,.2f", grossExpenses)}
                              - Living Cost & Upkeep: $${String.format("%,.2f", totalWeeklyOutflow)}
                              - Employee Payroll: $${String.format("%,.2f", totalEmployeeSalary)}
                            
                            --- OPERATIONS METRICS ---
                            Net Cash Profit/Loss: $${String.format("%,.2f", netProfit - accountantError - embezzledAmount)}
                            Current Liquid Assets: $${String.format("%,.2f", updatedCash)}
                            
                            --- STEWARDSHIP RESERVES ---
                            Virtual Tax Reserve: $${String.format("%,.2f", taxReservedAmount)} (15% Net profit reserved)
                            Emergency Savings Pool: $${String.format("%,.2f", savingsReservedAmount)} (10% Net profit secured)
                            Auto Stocks Purchased: $autoStockBoughtName ($${String.format("%.2f", autoStockBoughtCost)})
                            
                            --- FINANCIAL OUTLOOK & RISKS ---
                            ${if (updatedCash < 2500.0) "⚠️ WARNING: Extreme bankruptcy risk! Cash reserves are critically low. Please freeze employee hiring and stop studio rentals." else "✅ Healthy: Cash reserves are adequate. Recommended action is business asset expansion."}
                            ${if (accountantError > 0.0) "⚠️ IRS Warning: Filing discrepancy noted. Settled penalty of $" + accountantError + "." else ""}
                            
                            Report generated by your dedicated Accountant, ${emp.name}. (Accuracy Rating: ${String.format("%.0f", emp.intelligence)}%).
                        """.trimIndent()

                        updatedMail.add(
                            MailMessage(
                                id = "fin_report_" + UUID.randomUUID().toString().take(6),
                                sender = emp.name + " (Accountant)",
                                subject = "📊 Weekly Financial Statement: Y${state.currentYear} W${state.currentWeek}",
                                body = emailBody,
                                type = "NORMAL",
                                expiresWeek = state.currentWeek + 4,
                                expiresYear = state.currentYear
                            )
                        )
                        accountantReportAdded = true
                    }
                    "Financial Advisor" -> {
                        // Identifies undervalued stocks and recommends them
                        if (mutStocks.isNotEmpty()) {
                            val bestStock = mutStocks.minByOrNull { it.currentPrice }
                            if (bestStock != null && Random.nextFloat() < 0.85f) {
                                val adviceBody = """
                                    Hello ${state.artistName},
                                    
                                    I have conducted a deep fundamental research analysis on the equity markets this week.
                                    
                                    We have identified ${bestStock.name} (${bestStock.connectedTo}) as highly undervalued at $${String.format("%.2f", bestStock.currentPrice)} per share! 
                                    
                                    My volatility and cash-flow models indicate an estimated growth of 15% to 45% over the coming quarter. We strongly recommend expanding your portfolio positions in this equity.
                                    
                                    Best regards,
                                    ${emp.name}, Your Financial Advisor
                                """.trimIndent()

                                updatedMail.add(
                                    MailMessage(
                                        id = "advisor_rec_" + UUID.randomUUID().toString().take(6),
                                        sender = emp.name + " (Financial Advisor)",
                                        subject = "📈 Investment Advisory: Undervalued Stock Opportunity",
                                        body = adviceBody,
                                        type = "NORMAL",
                                        expiresWeek = state.currentWeek + 2,
                                        expiresYear = state.currentYear
                                    )
                                )
                            }
                        }
                    }
                    "Crypto Analyst" -> {
                        // Sends buy/sell alerts
                        if (mutCryptos.isNotEmpty() && Random.nextFloat() < 0.80f) {
                            val coin = mutCryptos.random()
                            val isScammer = emp.traitDescription == "Rogue Pump-and-Dump Scammer"
                            val confidence = if (isScammer) Random.nextInt(92, 100) else Random.nextInt(55, 88)
                            
                            // Scammer disappears with player cash if auto crypto permission is enabled
                            if (isScammer && emp.permissions["auto_crypto"] == true && updatedCash > 10000.0 && Random.nextFloat() < 0.15f) {
                                val stolenCash = (updatedCash * 0.15).coerceAtMost(6000.0)
                                updatedCash -= stolenCash
                                reportBuilder.append("🚨 CRYPTO SCAM: Analyst ${emp.name} vanished with $${String.format("%,.0f", stolenCash)} of investment funds! Check mail.\n")
                                updatedMail.add(
                                    MailMessage(
                                        id = "scam_exit_" + UUID.randomUUID().toString().take(6),
                                        sender = "Security Council",
                                        subject = "🚨 Alert: Crypto Analyst Disappeared",
                                        body = "Dear client, we regret to inform you that your hired Crypto Analyst, ${emp.name}, has deleted all accounts and exited. Audits show they utilized their 'auto_crypto' authority to drain $${String.format("%,.2f", stolenCash)} into an untraceable hardware wallet.",
                                        type = "NORMAL",
                                        expiresWeek = state.currentWeek + 4,
                                        expiresYear = state.currentYear
                                    )
                                )
                                return@forEach // RESIGNS IMMEDIATELY, NOT added to active list
                            }

                            // Scam advice vs honest advice
                            val alertMsg = if (isScammer && Random.nextFloat() < 0.4f) {
                                "🚀 MOON ALERT! Buy ${coin.name} immediately! Massive institutional whales are pumping the liquidity pool. Confidence: $confidence%. (Secret Pump-and-Dump scheme!)"
                            } else {
                                "📊 Crypto Alert: ${coin.name} shows high consolidation around $${String.format("%.4f", coin.currentPrice)} with standard volatility. Confidence Rating: $confidence%."
                            }

                            updatedMail.add(
                                MailMessage(
                                    id = "crypto_alert_" + UUID.randomUUID().toString().take(6),
                                    sender = emp.name + " (Crypto Analyst)",
                                    subject = "🪙 Crypto Alert: ${coin.name} Market Analysis",
                                    body = alertMsg,
                                    type = "NORMAL",
                                    expiresWeek = state.currentWeek + 2,
                                    expiresYear = state.currentYear
                                )
                            )
                        }
                    }
                    "Talent Agent" -> {
                        // Generates Sponsorship deals
                        if (Random.nextFloat() < 0.35f) {
                            val brandName = listOf("Rhythm Energy", "Sync Headwear", "Nova Apparels", "Beat Cola").random()
                            val brandOffer = 8000.0 + (state.followers * 0.05) + (emp.negotiation * 120.0)
                            updatedMail.add(
                                MailMessage(
                                    id = "brand_deal_" + UUID.randomUUID().toString().take(6),
                                    sender = brandName,
                                    subject = "✨ Sponsorship Proposal via Talent Agent",
                                    body = "Hello ${state.artistName}! Your Talent Agent, ${emp.name}, negotiated an endorsement deal with us. We will pay you $${String.format("%,.0f", brandOffer)} up-front to promote our product. This will increase your popularity!",
                                    type = "FEATURE",
                                    cashOffer = brandOffer,
                                    expiresWeek = state.currentWeek + 3,
                                    expiresYear = state.currentYear
                                )
                            )
                            reportBuilder.append("✨ Brand Deal: Talent Agent ${emp.name} secured a sponsorship offer from $brandName. Check mailbox!\n")
                            historyLog.add("Week ${state.currentWeek}: Secured sponsorship deal with $brandName for $${String.format("%.0f", brandOffer)}.")
                        }
                    }
                    "PR Manager" -> {
                        // Handles public image repair (recovers public image each week)
                        val recovery = 1.5f + (emp.efficiency / 15f)
                        currentPublicImage = (currentPublicImage + recovery).coerceAtMost(100f)
                        currentTrustworthiness = (currentTrustworthiness + (recovery * 0.5f)).coerceAtMost(100f)
                        reportBuilder.append("📢 Public Relations: PR Manager ${emp.name} conducted active press maintenance (+${String.format("%.1f", recovery)}% Public Image).\n")
                        historyLog.add("Week ${state.currentWeek}: Conducted public relations sweeps, repairing client's image.")
                    }
                    "Personal Assistant" -> {
                        // PA filters out unaccepted expired mails, and restores player energy
                        val filteredCount = updatedMail.size
                        updatedMail.removeAll { !it.isAccepted && (it.expiresYear < state.currentYear || (it.expiresYear == state.currentYear && it.expiresWeek <= state.currentWeek)) }
                        val diff = filteredCount - updatedMail.size
                        if (diff > 0) {
                            reportBuilder.append("🧹 Message Filtering: PA ${emp.name} cleared $diff expired junk messages from your Inbox.\n")
                        }
                        reportBuilder.append("🔋 Scheduling Assistance: PA ${emp.name} organized your calendar, giving you a quiet breather (+15 Energy rest benefit)!\n")
                        historyLog.add("Week ${state.currentWeek}: Scheduled calendar blocks and cleared mailbox spam.")
                    }
                }

                // 3. Competitor Poaching attempts (if loyalty is very low)
                if (emp.loyalty < 40f && Random.nextFloat() < 0.12f) {
                    val raiseOffered = emp.weeklySalary * 1.30
                    reportBuilder.append("🚨 Competitor Poaching: A competitor offered your ${emp.role}, ${emp.name}, a higher salary ($${String.format("%.0f", raiseOffered)}/wk)! Since their loyalty was low, they accepted and resigned immediately.\n")
                    updatedMail.add(
                        MailMessage(
                            id = "poached_" + UUID.randomUUID().toString().take(6),
                            sender = emp.name,
                            subject = "💔 Resignation: Poached by Competitor",
                            body = "Dear ${state.artistName}, I am officially resigning from my role as your ${emp.role}. I have received an offer with a substantial compensation package from a competing agency. Thank you for the opportunity.",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    return@forEach // RESIGNS, do not add to active list
                }

                afterDutyEmployees.add(updatedEmp.copy(careerHistory = historyLog))
            }

            val updatedEmployeesList = afterDutyEmployees
            finalStocks = mutStocks
            finalCryptos = mutCryptos

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
            if (accruedDebts > 0.0 && Random.nextFloat() > 0.5f) newNotifications.add("⚠️ Debt Warning: Your Record Label has accrued debts of $${String.format("%.2f", accruedDebts)}!")
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

            val ownLabel = state.ownLabel?.let { label ->
                var expenses = label.managers.sumOf { it.weeklyCost } + label.promoTeams.sumOf { it.weeklyCost }
                val remainingCash = state.cash + weeklySalary
                val updatedLabel = if (remainingCash < expenses) {
                    val penaltyDebt = expenses * 1.05
                    accruedDebts += penaltyDebt
                    currentBusinessReputation = (currentBusinessReputation - 8f).coerceAtLeast(0f)
                    currentReliability = (currentReliability - 5f).coerceAtLeast(0f)
                    currentIntegrity = (currentIntegrity - 3f).coerceAtLeast(0f)
                    reportBuilder.append("⚠️ LABEL OVERHEAD FAIL: Insufficient cash to pay label employees! Debt increased by +$${String.format("%,.2f", penaltyDebt)}. Business Reputation & Reliability collapsed!\n")
                    label
                } else {
                    weeklySalary -= expenses
                    currentBusinessReputation = (currentBusinessReputation + 0.6f).coerceAtMost(100f)
                    currentReliability = (currentReliability + 0.4f).coerceAtMost(100f)
                    reportBuilder.append("🏢 Label Overhead: Paid $${String.format("%,.2f", expenses)} weekly overhead to managers & promoters. Built Business Reputation.\n")
                    label
                }

                val popBoost = label.promoTeams.sumOf { it.popularityBoost.toDouble() }.toFloat()
                reportBuilder.append("🏢 Label Promo: Promo teams gained +${String.format("%.2f", popBoost)}% popularity for your imprint!\n")

                updatedLabel.copy(accruedDebts = accruedDebts)
            }

            // 10. Generate Mail / Contracts / Hidden Opportunities / Rare Life-Changing Events

            // A. Low Reputation Penalties: Active sponsorship withdrawal & brand fallout
            if (currentBusinessReputation < 40f) {
                val hadFeatureOffers = updatedMail.any { it.type == "FEATURE" && !it.isAccepted }
                if (hadFeatureOffers) {
                    updatedMail.removeAll { it.type == "FEATURE" && !it.isAccepted }
                    updatedMail.add(
                        MailMessage(
                            id = "withdrawn_" + UUID.randomUUID().toString().take(6),
                            sender = "Brand Council",
                            subject = "⚠️ Offers Terminated: Business Distrust",
                            body = "Dear ${state.artistName}, due to reports of poor business practices, failing to pay staff, and lack of reliability, we have officially withdrawn all active brand & partnership offers. Trust takes years to build, but seconds to lose.",
                            type = "NORMAL",
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("🚨 SPONSOR BACKLASH: Shady business conduct has forced brands to withdraw pending offers. Check mail.\n")
                    updatedCash -= 2000.0
                }
            }

            // B. High Reputation Sponsorships (Luxury Brands, Banks, Sponsors)
            if (currentBusinessReputation >= 75f && Random.nextFloat() > 0.7f) {
                val brandName = listOf("Aura Tech", "Valerius Fashion", "Titan Capital", "Apex Bank").random()
                val luxuryOffer = 40000.0 + (state.popularity * 1500.0) + (currentBusinessReputation * 800.0)
                updatedMail.add(
                    MailMessage(
                        id = "brand_" + UUID.randomUUID().toString().take(6),
                        sender = brandName,
                        subject = "✨ Prestige Sponsorship Offer",
                        body = "Hello ${state.artistName}. Your stellar business reputation and integrity make you a perfect ambassador. We want to offer you an endorsement deal paying $${String.format("%,.0f", luxuryOffer)} up-front! This will boost your Public Image by +15% and Industry Respect by +10%.",
                        type = "FEATURE",
                        cashOffer = luxuryOffer,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("✨ ELITE DEAL: Your pristine Business Reputation has attracted a major luxury sponsorship! Check Mailbox!\n")
            }

            // C. Opportunity Discovery from Exposure Networks (NPC Introductions)
            val closeFriends = state.npcs.filter { it.relationshipWithPlayer >= 70f && !it.isRetired }
            if (closeFriends.isNotEmpty() && Random.nextFloat() > 0.65f) {
                val friend = closeFriends.random()
                val referralPay = 15000.0 + (friend.popularity * 250.0)
                updatedMail.add(
                    MailMessage(
                        id = "referral_" + UUID.randomUUID().toString().take(6),
                        sender = "${friend.name} (Referral)",
                        subject = "🤝 Introduction: High-Paying Gig",
                        body = "Hey! Our close friend ${friend.name} recommended you for a private songwriting & production gig with an industry elite group. They praised your high reliability and stewardship. Upfront fee of $${String.format("%,.0f", referralPay)}. Gained +5% Industry Respect.",
                        type = "FEATURE",
                        cashOffer = referralPay,
                        expiresWeek = state.currentWeek + 2,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("🤝 NETWORKING REFERRAL: '${friend.name}' referred you to an exclusive gig in their network! Check Mailbox!\n")
            }

            // D. Standard Record label offers (based on popularity, influence, and business reputation!)
            if (state.popularity >= 60f && state.influence >= 45f && Random.nextFloat() > 0.65f && state.signedLabelId == null) {
                val labelCut = (Random.nextInt(18, 38) / 100f) - (currentBusinessReputation / 1000f)
                val adjustedCut = labelCut.coerceIn(0.12f, 0.40f)
                val labelName = listOf("Def Jam Records", "Apex Records", "Sub Pop", "Sony Music").random()
                val contractId = UUID.randomUUID().toString()
                updatedMail.add(
                    MailMessage(
                        id = contractId,
                        sender = labelName,
                        subject = "Record Deal Offer (${(adjustedCut*100).toInt()}% Cut)",
                        body = "Hey ${state.artistName}! We've been tracking your career. With your level of market appeal, we'd love to sign you to $labelName. We take a ${(adjustedCut*100).toInt()}% cut of your songs and tours, but we'll instantly boost your popularity/influence by +30% and cover 50% of your tour booking costs. Reply to sign!",
                        type = "LABEL_OFFER",
                        expiresWeek = state.currentWeek + 4,
                        expiresYear = state.currentYear,
                        labelCut = adjustedCut,
                        labelId = labelName.lowercase().replace(" ", "_")
                    )
                )
                reportBuilder.append("✉️ New Mail: Received a Record Label Contract offer in your Mailbox!\n")
            }

            // E. Rare life-changing high-status events
            if (state.popularity > 75f && state.publicImage > 70f && Random.nextFloat() > 0.88f) {
                val roll = Random.nextInt(3)
                if (roll == 0) {
                    updatedMail.add(
                        MailMessage(
                            id = "world_cup_" + UUID.randomUUID().toString().take(6),
                            sender = "Global Sports Assoc.",
                            subject = "🎤 World Cup Anthem Opportunity!",
                            body = "Congratulations! You have been selected to compose and perform the official World Cup Anthem! This will be broadcast to billions. We pay an upfront fee of $120,000, and it will immediately grant you +250,000 global fans and +15% critical acclaim! Click accept to secure the offer.",
                            type = "FEATURE",
                            cashOffer = 120000.0,
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("🌟 RARE LIFE EVENT: You were invited to create the official World Cup Anthem! Check your Mailbox!\n")
                } else if (roll == 1) {
                    updatedMail.add(
                        MailMessage(
                            id = "hollywood_" + UUID.randomUUID().toString().take(6),
                            sender = "Paramount Studios",
                            subject = "🎬 Hollywood Movie Theme Feature!",
                            body = "Hello! We are producing an upcoming summer blockbuster movie and want you to write the title soundtrack track. This pays $75,000 upfront, boosts industry respect by +20%, and critical acclaim by +15%. Click accept to register.",
                            type = "FEATURE",
                            cashOffer = 75000.0,
                            expiresWeek = state.currentWeek + 3,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("🌟 RARE LIFE EVENT: Hollywood studio reached out to license a soundtrack theme from you! Check Mailbox!\n")
                } else {
                    updatedMail.add(
                        MailMessage(
                            id = "royal_" + UUID.randomUUID().toString().take(6),
                            sender = "The Royal Secretariat",
                            subject = "👑 Royal Command Performance Invitation!",
                            body = "Greetings. It is our pleasure to invite you to perform live before the Royal Family at the upcoming Charity Gala. This prestigious command performance pays $90,000 in royal honorariums and grants you +35% industry respect and +20% public image! Click accept to accept.",
                            type = "FEATURE",
                            cashOffer = 90000.0,
                            expiresWeek = state.currentWeek + 2,
                            expiresYear = state.currentYear
                        )
                    )
                    reportBuilder.append("🌟 RARE LIFE EVENT: Invited to a prestigious Royal Command Performance! Check Mailbox!\n")
                }
            }

            // F. Regular feature requests
            if (state.fans > 5000 && Random.nextFloat() > 0.72f) {
                val feeOffer = Random.nextDouble(5000.0, 25000.0)
                updatedMail.add(
                    MailMessage(
                        id = UUID.randomUUID().toString(),
                        sender = "NPC Artist",
                        subject = "Featuring Request",
                        body = "Yo, love your music! Would you be down to write a guest verse/collab on my upcoming album? We will pay you $${String.format("%.2f", feeOffer)} up-front and it will boost your influence. Click accept!",
                        type = "FEATURE",
                        cashOffer = feeOffer,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("✉️ New Mail: Guest feature request received in Mailbox!\n")
            }

            // 11. Fully Simulated NPC Careers (Mentorship, Rivals & Competitive Dynamics)
            val updatedNPCs = state.npcs.map { npc ->
                var currentNPCWealth = npc.wealth
                var currentNPCFans = npc.fans
                var currentNPCPopularity = npc.popularity
                var currentNPCStress = npc.stress
                var currentNPCMentalHealth = npc.mentalHealth
                var npcAge = npc.age
                var currentRelation = npc.relationshipWithPlayer

                // Age NPCs up annually
                if (nextWeek == 1) {
                    npcAge += 1
                }

                // Decide NPC actions based on traits
                var actionMsg: String? = null
                val randVal = Random.nextFloat()
                
                when {
                    // Retirement checks
                    npcAge >= 40 && randVal > 0.95f -> {
                        actionMsg = "👴 Legacy: Veteran artist ${npc.name} has officially retired from recording to become an executive mentor!"
                        return@map npc.copy(age = npcAge, isRetired = true)
                    }
                    npc.isRetired -> {
                        // Retired mentor action: 15% chance to host a free masterclass if friend
                        if (currentRelation > 75f && Random.nextFloat() < 0.15f) {
                            actionMsg = "🎓 Mentorship: Your retired mentor, ${npc.name}, hosted an exclusive songwriting masterclass for you! Gained +5% writing skill."
                            updatedVoice = (updatedVoice + 1.5f).coerceAtMost(100f)
                            updatedWriting = (updatedWriting + 3.5f).coerceAtMost(100f)
                        }
                        currentNPCWealth += Random.nextDouble(1000.0, 4000.0)
                        return@map npc.copy(age = npcAge, wealth = currentNPCWealth)
                    }
                    // Rival actions: trash-talking & active chart competition
                    currentRelation < 30f && randVal < 0.18f -> {
                        val subRoll = Random.nextInt(3)
                        if (subRoll == 0) {
                            actionMsg = "🚨 Rival Roast: Your rival, ${npc.name}, publicly roasted your production skills on a viral podcast! You lost -6% Public Image and gained +12 Stress."
                            currentPublicImage = (currentPublicImage - 6f).coerceAtLeast(10f)
                            currentStress = (currentStress + 12f).coerceAtMost(100f)
                        } else if (subRoll == 1) {
                            actionMsg = "⚔️ CHART ATTACK: Your rival, ${npc.name}, released a surprise single on your release day to dilute your streaming momentum! Diluted trending momentum (-10 Trending Score)."
                            currentStress = (currentStress + 8f).coerceAtMost(100f)
                            // We will reduce trendingScore or similar
                        } else {
                            actionMsg = "📣 Rival Dispute: ${npc.name} started a heated debate about your artistic credibility on social media. Conversation Volume spiked, but Public Image fell by -4%."
                            currentPublicImage = (currentPublicImage - 4f).coerceAtLeast(10f)
                            currentStress = (currentStress + 6f).coerceAtMost(100f)
                        }
                    }
                    // Release song based on personality
                    randVal > 0.88f -> {
                        val qualityRating = ((npc.creativity / 25f) + (Random.nextFloat() * 1.5f)).coerceIn(1.0f, 5.0f)
                        val songTitle = listOf("Neon Nights", "Shattered Illusion", "Cyberpunk Heart", "Hustler Anthem", "Faded Silhouette", "Platinum Reign").random()
                        val StreamsEarned = (npc.fans * 0.15 + npc.popularity * 4000).toLong()
                        currentNPCFans += (qualityRating * 2500).toLong()
                        currentNPCPopularity = (currentNPCPopularity + qualityRating * 1.2f).coerceAtMost(100f)
                        currentNPCWealth += (StreamsEarned * 0.003)
                        actionMsg = "🎵 Music Release: ${npc.name} released a new single '${songTitle}'! Rated ${String.format("%.1f", qualityRating)}⭐ by critics."
                    }
                    // Set out on tour
                    randVal < 0.05f -> {
                        val attendance = (npc.fans * 0.22).toLong().coerceIn(1000, 150000)
                        currentNPCWealth += (attendance * 45.0)
                        currentNPCPopularity = (currentNPCPopularity + 5f).coerceAtMost(100f)
                        currentNPCStress = (currentNPCStress + 15f).coerceAtMost(100f)
                        actionMsg = "🎤 Concert Tour: ${npc.name} started a regional stadium tour! Estimated attendance: ${String.format("%,d", attendance)} fans."
                    }
                    // Scandal
                    randVal in 0.45f..0.47f -> {
                        currentNPCPopularity = (currentNPCPopularity - 12f).coerceAtLeast(10f)
                        currentNPCStress = (currentNPCStress + 20f).coerceAtMost(100f)
                        currentNPCMentalHealth = (currentNPCMentalHealth - 15f).coerceAtLeast(10f)
                        actionMsg = "🚨 Scandal Alert: ${npc.name} was caught in a heated viral social media feud! Public image suffered."
                    }
                }

                if (actionMsg != null) {
                    reportBuilder.append("$actionMsg\n")
                }

                // Normal drift
                val driftPop = Random.nextFloat() * 2f - 0.9f
                npc.copy(
                    age = npcAge,
                    popularity = (currentNPCPopularity + driftPop).coerceIn(10f, 100f),
                    fans = (currentNPCFans + (driftPop * 400).toLong()).coerceAtLeast(100),
                    wealth = currentNPCWealth,
                    stress = (currentNPCStress - 2f).coerceIn(0f, 100f),
                    mentalHealth = (currentNPCMentalHealth + 1f).coerceIn(0f, 100f),
                    relationshipWithPlayer = currentRelation
                )
            }

            // 12. Update Player's Mental Health Metrics

            // Side jobs & Active tours impact stress/balance
            val totalStressLoad = (state.sideJobs.filter { it.isActive }.size * 6f) + (activeTours.size * 12f)
            currentStress = (currentStress + totalStressLoad).coerceIn(0f, 100f)
            currentWorkLifeBalance = (currentWorkLifeBalance - (totalStressLoad * 1.5f)).coerceIn(0f, 100f)

            // Burnout trigger
            if (currentStress >= 70f) {
                currentBurnout = (currentBurnout + 8f).coerceAtMost(100f)
                currentInspiration = (currentInspiration - 12f).coerceAtLeast(5f)
                currentMotivation = (currentMotivation - 10f).coerceAtLeast(10f)
                currentHappiness = (currentHappiness - 8f).coerceAtLeast(10f)
                reportBuilder.append("⚠️ BURNOUT WARNING: Your stress levels are extremely high! Writing skills restricted and creative inspiration choked.\n")
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
            val gainedFans = (state.popularity * 2.5f + state.influence * 1.5f + (Random.nextFloat() * 15f)).toLong()
            val totalFansNew = (previousFans + gainedFans).coerceAtLeast(200)

            // Segment distribution formulas
            val updatedCasual = (totalFansNew * 0.55).toLong()
            val updatedHardcore = (totalFansNew * 0.15).toLong() // Loyal Fans
            val updatedCollectors = (totalFansNew * 0.05).toLong()
            val updatedConcertGoers = (totalFansNew * 0.15).toLong()
            val updatedInternational = (totalFansNew * 0.06).toLong()
            val updatedOnline = (totalFansNew * 0.04).toLong()

            // Redesigned Social Media Ecosystem Updates
            val updatedFollowers = (state.followers + (gainedFans * 1.6).toLong()).coerceAtLeast(1500L)
            val updatedIndustryFollowers = (state.industryFollowers + (currentIndustryRespect * 0.15f + currentCriticalAcclaim * 0.05f).toLong()).coerceIn(10L, 50000L)
            
            val activeTourBonus = if (activeTours.isNotEmpty()) 15f else 0f
            val viralBonus = if (viralHitOccurred) 35f else 0f
            val updatedTrendingScore = ((state.trendingScore * 0.72f) + (gainedFans / 350f) + activeTourBonus + viralBonus).coerceIn(0f, 100f)
            
            val updatedConversationVolume = (updatedFollowers * 0.005f * (1f + updatedTrendingScore / 60f)).toLong().coerceAtLeast(50L)
            
            // Dynamic Public Sentiment updates
            val basePosSentiment = (currentPublicImage * 0.6f + currentTrustworthiness * 0.4f).coerceIn(5f, 100f)
            val baseNegSentiment = ((100f - currentTrustworthiness) * 0.5f + (if (currentStress > 70f) 18f else 0f) + (if (accruedDebts > 0.0) 15f else 0f)).coerceIn(0f, 100f)
            val updatedPositiveSentiment = basePosSentiment
            val updatedNegativeSentiment = baseNegSentiment
            val updatedNeutralSentiment = (100f - updatedPositiveSentiment - updatedNegativeSentiment).coerceIn(0f, 100f)

            // Stewardship Character Attributes Progression
            val disciplineGain = if (practiceMissed == 0) 0.6f else -1.2f
            val updatedDiscipline = (currentDiscipline + disciplineGain).coerceIn(10f, 100f)
            
            val reliabilityGain = if (accruedDebts <= 0.0) 0.5f else -1.5f
            val updatedReliability = (currentReliability + reliabilityGain).coerceIn(10f, 100f)
            
            val updatedIntegrity = currentIntegrity.coerceIn(10f, 100f)
            val updatedGenerosity = (currentGenerosity - 0.05f).coerceIn(10f, 100f)
            val updatedHumility = currentHumility.coerceIn(10f, 100f)

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

            _weeklyReport.value = reportBuilder.toString()

            var finalState = state.copy(
                currentWeek = nextWeek,
                currentYear = nextYear,
                cash = updatedCash + weeklySalary,
                songs = updatedSongs,
                tours = updatedTours,
                energy = newEnergy,
                sideJobs = state.sideJobs,
                stocks = finalStocks,
                cryptos = finalCryptos,
                practiceMissedWeeks = practiceMissed,
                voiceSkill = updatedVoice,
                writingSkill = updatedWriting,
                influence = updatedInfluence,
                ownLabel = ownLabel,
                mailbox = updatedMail,
                npcs = updatedNPCs,
                
                // Living world properties
                activeGlobalEvent = activeEvent,
                globalEventEffect = eventEffect,
                genreTrends = currentTrends,
                
                // Smartphone digital ecosystem preservation
                meTubeVideos = updatedMeTubeVideos,
                meTubeSubscribers = newSubscribers,
                meTubeWatchTime = newWatchTime,
                meTubeRevenue = state.meTubeRevenue + meTubeEarnings,
                hiredEmployees = updatedEmployeesList,
                pendingStockOrders = remainingStockOrders,
                pendingCryptoOrders = remainingCryptoOrders,
                castTrendingHashtags = trendingHashtags,
                notificationLogs = (newNotifications + state.notificationLogs).take(20),

                // Fan segmentation
                fans = finalFansNew,
                casualFans = updatedCasual,
                hardcoreFans = updatedHardcore,
                collectors = updatedCollectors,
                concertGoers = updatedConcertGoers,
                internationalFans = updatedInternational,
                onlineCommunity = updatedOnline,

                // Social Media Ecosystem
                followers = updatedFollowers,
                industryFollowers = updatedIndustryFollowers,
                trendingScore = updatedTrendingScore,
                conversationVolume = updatedConversationVolume,
                positiveSentiment = updatedPositiveSentiment,
                negativeSentiment = updatedNegativeSentiment,
                neutralSentiment = updatedNeutralSentiment,

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

                // Mental health
                stress = currentStress,
                burnout = currentBurnout,
                creativeInspiration = currentInspiration,
                motivation = currentMotivation,
                happiness = currentHappiness,
                workLifeBalance = currentWorkLifeBalance,

                // Banking System updates
                savingsBalance = newSavingsBalance,
                loanAmount = newLoanAmount,
                taxDebt = newTaxDebt,
                economyCycle = economyCycle,
                inflationRate = inflationRate,
                fuelPrice = fuelPrice,
                touristIndex = touristIndex,
                isElectionSeason = isElectionSeason,
                weatherCondition = weatherCondition,
                mortgageAmount = newMortgageAmount,
                scheduledActivities = emptyList()
            )

            // Trigger Annual Awards Show
            if (triggerAwardsShow) {
                finalState = runAnnualAwardsShow(finalState)
            }

            finalState
        }
    }

    private fun runAnnualAwardsShow(state: GameStateEntity): GameStateEntity {
        val report = StringBuilder()
        report.append("\n🏆🏆 THE ANNUAL ARTIST STORY AWARDS CEREMONY 🏆🏆\n\n")

        // Most Popular Artist
        val sortedByPopularity = (state.npcs + NPCEntity(
            "player", state.artistName, state.popularity, state.influence, state.fans
        )).sortedByDescending { it.popularity }
        val popWinner = sortedByPopularity.first()
        report.append("🥇 MOST POPULAR ARTIST OF THE YEAR:\n")
        report.append("👉 Winner: **${popWinner.name}** with ${String.format("%.1f", popWinner.popularity)}% Popularity!\n\n")

        // Most Streamed Song
        val playerBestSong = state.songs.maxByOrNull { it.totalStreams }
        val candidates = state.npcs.map { it.name to it.bestSongStreams }.toMutableList()
        if (playerBestSong != null) {
            candidates.add(state.artistName to playerBestSong.totalStreams)
        }
        val sortedByStreams = candidates.sortedByDescending { it.second }
        val streamsWinner = sortedByStreams.first()
        report.append("🎵 MOST STREAMED SONG OF THE YEAR:\n")
        report.append("👉 Winner: **${streamsWinner.first}** with ${streamsWinner.second} total streams!\n\n")

        // Highest Tour Attendance
        val playerBestTour = state.tours.filter { it.status == "COMPLETED" }.maxByOrNull { it.baseAttendance }
        val tourCandidates = state.npcs.map { it.name to (it.fans / 20).toInt() }.toMutableList() // Simulated
        if (playerBestTour != null) {
            tourCandidates.add(state.artistName to playerBestTour.baseAttendance)
        }
        val sortedByTours = tourCandidates.sortedByDescending { it.second }
        val tourWinner = sortedByTours.first()
        report.append("🎤 HIGHEST TOUR ATTENDANCE AWARD:\n")
        report.append("👉 Winner: **${tourWinner.first}** with ${tourWinner.second} attendees!\n\n")

        // Most Hardworking Artist (completed tours or active side jobs)
        val playerHardworkScore = state.tours.size * 2 + state.songs.size
        val hardworkingWinnerName = if (playerHardworkScore > 8) state.artistName else "Jax Riff"
        report.append("🔨 MOST HARDWORKING ARTIST AWARD:\n")
        report.append("👉 Winner: **$hardworkingWinnerName**!\n\n")

        // Best Selling Artist (merch sales)
        val playerBestSelling = Random.nextBoolean() && state.popularity > 50f
        val merchWinner = if (playerBestSelling) state.artistName else "Busta Rhyme"
        report.append("👕 BEST SELLING ARTIST (MERCH sales):\n")
        report.append("👉 Winner: **$merchWinner**!\n\n")

        // Player bonuses if player won awards
        var playerWonSomething = false
        var bonusCash = 0.0
        var bonusPop = 0f

        if (popWinner.name == state.artistName) {
            playerWonSomething = true
            bonusCash += 100000.0
            bonusPop += 15f
        }
        if (streamsWinner.first == state.artistName) {
            playerWonSomething = true
            bonusCash += 100000.0
            bonusPop += 10f
        }
        if (tourWinner.first == state.artistName) {
            playerWonSomething = true
            bonusCash += 100000.0
            bonusPop += 10f
        }
        if (hardworkingWinnerName == state.artistName) {
            playerWonSomething = true
            bonusCash += 50000.0
            bonusPop += 5f
        }
        if (merchWinner == state.artistName) {
            playerWonSomething = true
            bonusCash += 50000.0
            bonusPop += 5f
        }

        if (playerWonSomething) {
            report.append("🎉 Congratulations! You won awards! Gained +$${bonusCash} and +${bonusPop}% popularity boost!\n")
        } else {
            report.append("😢 Work harder next year! No awards won this time, but your popularity increased for competing.\n")
            bonusPop += 2f
        }

        _weeklyReport.value = _weeklyReport.value + "\n" + report.toString()

        return state.copy(
            cash = state.cash + bonusCash,
            popularity = (state.popularity + bonusPop).coerceAtMost(100f)
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

            if (mail.type == "FEATURE") {
                updatedCash += mail.cashOffer
                updatedInfluence = (state.influence + 8f).coerceAtMost(100f)
                showToast("Accepted feature collaboration! Earned $${String.format("%.2f", mail.cashOffer)}!")
            } else if (mail.type == "LABEL_OFFER") {
                updatedSignedLabelId = mail.labelId
                updatedSignedLabelCut = mail.labelCut
                updatedPopularity = (state.popularity + 30f).coerceAtMost(100f)
                updatedInfluence = (state.influence + 30f).coerceAtMost(100f)
                showToast("Signed with ${mail.sender}! Popularity and Influence boosted!")
            } else if (mail.type == "SHADY_BRAND") {
                updatedCash += mail.cashOffer
                updatedTrustworthiness = (state.trustworthiness - 15f).coerceAtLeast(0f)
                updatedPublicImage = (state.publicImage - 10f).coerceAtLeast(0f)
                showToast("Took shady supplement sponsorship! Gained $${String.format("%.2f", mail.cashOffer)}, but Trustworthiness and Public Image took a hit.")
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
            }

            state.copy(
                mailbox = updatedMail,
                cash = updatedCash,
                signedLabelId = updatedSignedLabelId,
                signedLabelCut = updatedSignedLabelCut,
                popularity = updatedPopularity,
                influence = updatedInfluence,
                trustworthiness = updatedTrustworthiness,
                publicImage = updatedPublicImage,
                industryRespect = updatedIndustryRespect,
                stress = updatedStress,
                energy = updatedEnergy,
                npcs = updatedNPCs
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
                showToast("Spent a week on vacation in Hawaii! Refreshed mental health!")
                state.copy(
                    cash = state.cash - 5000.0,
                    stress = (state.stress - 50f).coerceAtLeast(0f),
                    burnout = (state.burnout - 20f).coerceAtLeast(0f),
                    creativeInspiration = 100f,
                    happiness = (state.happiness + 35f).coerceAtMost(100f),
                    motivation = (state.motivation + 25f).coerceAtMost(100f),
                    energy = state.maxEnergy,
                    timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Took a refreshing luxury vacation to recharge mental battery."
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
                showToast("Attended therapy session! Released stress and clarified goals.")
                state.copy(
                    cash = state.cash - 1000.0,
                    stress = (state.stress - 30f).coerceAtLeast(0f),
                    burnout = (state.burnout - 15f).coerceAtLeast(0f),
                    happiness = (state.happiness + 15f).coerceAtMost(100f),
                    motivation = (state.motivation + 15f).coerceAtMost(100f)
                )
            }
        }
    }

    fun hobbiesAndRest() {
        updateState { state ->
            if (state.energy < 40) {
                showToast("Not enough energy (40% required) to rest and play hobbies!")
                state
            } else {
                showToast("Spent time on personal hobbies and relaxing at home!")
                state.copy(
                    energy = state.energy - 40,
                    stress = (state.stress - 15f).coerceAtLeast(0f),
                    creativeInspiration = (state.creativeInspiration + 25f).coerceAtMost(100f),
                    happiness = (state.happiness + 10f).coerceAtMost(100f)
                )
            }
        }
    }

    // 1b. Financial Education & Business Stewardship Actions
    fun readBusinessBook() {
        updateState { state ->
            if (state.energy < 25) {
                showToast("Not enough energy (25 required) to read!")
                state
            } else if (state.cash < 100.0) {
                showToast("Not enough cash ($100 required) for a financial book!")
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
                    energy = state.energy - 25,
                    cash = state.cash - 100.0,
                    businessSkill = (state.businessSkill + skillGain).coerceAtMost(100f),
                    discipline = (state.discipline + 1.2f).coerceAtMost(100f),
                    reliability = (state.reliability + 0.6f).coerceAtMost(100f),
                    stress = (state.stress + 2f).coerceAtMost(100f) // studying requires focus!
                )
            }
        }
    }

    fun hireBusinessConsultant() {
        updateState { state ->
            if (state.energy < 15) {
                showToast("Not enough energy (15 required) to consult!")
                state
            } else if (state.cash < 1500.0) {
                showToast("Not enough cash ($1,500 required) to hire a financial consultant!")
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
                    energy = state.energy - 15,
                    cash = state.cash - 1500.0,
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
                state.copy(
                    cash = state.cash - cost,
                    ownedBusinesses = state.ownedBusinesses + newAsset,
                    legacyPoints = state.legacyPoints + (cost / 100).toLong(),
                    timelineEvents = state.timelineEvents + "Year ${state.currentYear}, Week ${state.currentWeek}: Acquired the $type business '$name'!"
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
                state
            } else {
                // Interconnected logic: payout scales with NPC wealth and your business skill!
                // Greedy NPCs with high businessIntel try to pay less, but wealthy NPCs can afford more.
                val greedFactor = if (npc.personality == "Hustler" || npc.personality == "Diva") 0.85 else 1.05
                val scaleMultiplier = 1.0 + (state.businessSkill / 100f) + (npc.wealth / 250000.0).coerceAtMost(1.0)
                val payout = 3500.0 * scaleMultiplier * greedFactor
                
                showToast("Produced a track for ${npc.name}! Earned $${String.format("%,.0f", payout)}.")
                
                // Update NPC relationship and state
                val updatedNPCs = state.npcs.map { n ->
                    if (n.id == npcId) {
                        n.copy(
                            relationshipWithPlayer = (n.relationshipWithPlayer + 15f).coerceAtMost(100f),
                            popularity = (n.popularity + 2f).coerceAtMost(100f)
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
    }

    fun strengthenFriendship(npcId: String) {
        updateState { state ->
            val npc = state.npcs.find { it.id == npcId }
            if (npc == null) return@updateState state

            if (state.energy < 25) {
                showToast("Not enough energy (25 required) to socialize with colleagues!")
                state
            } else {
                var relBoost = 15f
                var toastMsg = "Met up and socialized with ${npc.name}! Relationship improved."
                var energyDrain = 25

                // Interconnected personality check:
                if (npc.personality == "Charismatic" || npc.personality == "Hustler") {
                    relBoost = 25f
                    toastMsg = "Had an awesome networking dinner with ${npc.name}! They loved your hustle. Relationship +25f."
                } else if (npc.personality == "Diva" && state.publicImage < 40f) {
                    relBoost = 0f
                    energyDrain = 5
                    toastMsg = "Met up with ${npc.name}, but they acted cold and dismissed you because your Public Image is too low. Drained 5 energy."
                } else if (npc.personality == "Rebel") {
                    relBoost = 18f
                    toastMsg = "Hung out with Rebel ${npc.name} at a subterranean indie club. Creative inspiration boosted (+10)!"
                }

                showToast(toastMsg)
                val updatedNPCs = state.npcs.map { n ->
                    if (n.id == npcId) {
                        n.copy(relationshipWithPlayer = (n.relationshipWithPlayer + relBoost).coerceAtMost(100f))
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
                influence = (state.influence + influenceGain).coerceAtMost(100f), // Controversy fuels social engagement
                publicImage = (state.publicImage - publicImageLoss).coerceAtLeast(0f), // But hurts public image
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
            state.copy(spiritualPrayerSetting = enabled)
        }
    }

    fun toggleTitheReminder(enabled: Boolean) {
        updateState { state ->
            state.copy(spiritualTitheSetting = enabled, spiritualTitheReminderActive = enabled)
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

    // --- MeTube Platform ---
    fun uploadMeTubeVideo(title: String, type: String) {
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
                revenueEarned = initialViews * 0.008,
                weekPublished = state.currentWeek,
                yearPublished = state.currentYear
            )
            
            val addedRevenue = initialViews * 0.008
            showToast("Successfully uploaded video to MeTube! Gained views & attention!")
            state.copy(
                energy = state.energy - 15,
                cash = state.cash - cost,
                meTubeVideos = state.meTubeVideos + newVideo,
                meTubeSubscribers = state.meTubeSubscribers + Random.nextInt(50, 300),
                meTubeRevenue = state.meTubeRevenue + addedRevenue,
                trendingScore = (state.trendingScore + 5f).coerceAtMost(100f)
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
    fun sendChatMessage(npcId: String, playerMessageText: String) {
        updateState { state ->
            val npc = state.npcs.find { n -> n.id == npcId } ?: return@updateState state
            
            // Build player message
            val playerMsg = ChatMessage(
                id = UUID.randomUUID().toString().take(6),
                senderId = "PLAYER",
                senderName = state.artistName,
                message = playerMessageText,
                timestamp = System.currentTimeMillis()
            )
            
            // Build simulated NPC message response based on personality
            val replyText = when (npc.personality) {
                "Charismatic" -> "Hey! Always awesome hearing from you. Let's keep making waves! 🌊"
                "Ambitious" -> "What's the play? I'm focusing on dominating the charts. Let's talk business!"
                "Rebel" -> "Sup. I'm just cooking some experimental beats. Don't let the label suit guys box you in."
                "Hustler" -> "Time is money, friend! If you have a gig referral, hit me up. Otherwise, stay grinding."
                "Diva" -> "My schedule is packed, but for you, I have a minute. What do you need? ✨"
                else -> "Appreciate the message! Let's touch base soon."
            }
            
            val npcMsg = ChatMessage(
                id = UUID.randomUUID().toString().take(6),
                senderId = npcId,
                senderName = npc.name,
                message = replyText,
                timestamp = System.currentTimeMillis() + 100
            )
            
            val conversations = state.chatConversations.toMutableList()
            val existingIndex = conversations.indexOfFirst { it.npcId == npcId }
            
            if (existingIndex != -1) {
                val conv = conversations[existingIndex]
                conversations[existingIndex] = conv.copy(
                    messages = conv.messages + playerMsg + npcMsg,
                    unreadCount = conv.unreadCount + 1
                )
            } else {
                conversations.add(
                    ChatConversation(
                        npcId = npcId,
                        npcName = npc.name,
                        messages = listOf(playerMsg, npcMsg),
                        unreadCount = 1
                    )
                )
            }
            
            // Slightly improve relationship
            val updatedNPCs = state.npcs.map { n ->
                if (n.id == npcId) {
                    n.copy(relationshipWithPlayer = (n.relationshipWithPlayer + 2f).coerceAtMost(100f))
                } else n
            }
            
            state.copy(
                chatConversations = conversations,
                npcs = updatedNPCs
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
    fun createCastPost(content: String) {
        updateState { state ->
            if (state.energy < 10) {
                showToast("Not enough energy to draft a social media post! Requires 10 energy.")
                return@updateState state
            }
            
            val isControversial = content.contains("diss", ignoreCase = true) || content.contains("scandal", ignoreCase = true) || content.contains("hate", ignoreCase = true)
            val baseLikes = (state.followers * (0.02f + Random.nextFloat() * 0.05f)).toLong().coerceAtLeast(10L)
            
            val newPost = CastPost(
                id = UUID.randomUUID().toString().take(6),
                authorName = state.artistName,
                authorHandle = "@${state.artistName.lowercase().replace(" ", "")}",
                content = content,
                likes = baseLikes,
                reposts = (baseLikes * 0.15).toLong(),
                commentsCount = (baseLikes * 0.1).toLong(),
                week = state.currentWeek,
                year = state.currentYear,
                isByPlayer = true
            )
            
            val imageShift = if (isControversial) -5f else +3f
            val sentimentShift = if (isControversial) -8f else +5f
            
            showToast("Successfully published post on Cast social platform!")
            state.copy(
                energy = state.energy - 10,
                castPosts = listOf(newPost) + state.castPosts.take(29),
                followers = (state.followers + (baseLikes * 0.25).toLong()),
                publicImage = (state.publicImage + imageShift).coerceIn(10f, 100f),
                positiveSentiment = (state.positiveSentiment + sentimentShift).coerceIn(5f, 100f),
                trendingScore = (state.trendingScore + 6f).coerceAtMost(100f)
            )
        }
    }

    fun likeCastPost(postId: String) {
        updateState { state ->
            val updated = state.castPosts.map { post ->
                if (post.id == postId) {
                    val isLiked = !post.isLikedByPlayer
                    post.copy(
                        isLikedByPlayer = isLiked,
                        likes = post.likes + (if (isLiked) 1 else -1)
                    )
                } else post
            }
            state.copy(castPosts = updated)
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

    // --- Recruitment Hall Hiring ---
    fun recruitProfessional(role: String, name: String, salary: Double) {
        updateState { state ->
            val existingCount = state.hiredEmployees.count { it.role == role }
            if (existingCount >= 1 && (role == "Accountant" || role == "Business Manager" || role == "Lawyer" || role == "PR Manager" || role == "Personal Assistant")) {
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
                "Security" -> "Formidable (Protects you from stressors and emergencies)"
                else -> "Diligent & Helpful Employee"
            }
            
            val initialPermissions = when (role) {
                "Accountant" -> mapOf("auto_stocks" to false, "auto_rebalance" to false, "auto_tax" to false, "auto_savings" to false)
                "Crypto Analyst" -> mapOf("auto_crypto" to false)
                "Business Manager" -> mapOf("auto_contracts" to false)
                "PR Manager" -> mapOf("auto_social" to false)
                else -> emptyMap()
            }

            val experience = Random.nextInt(40, 95)
            val reliability = Random.nextInt(50, 98).toFloat()
            val honesty = Random.nextInt(30, 100).toFloat()
            val greed = Random.nextInt(15, 85).toFloat()
            val intelligence = Random.nextInt(45, 98).toFloat()
            val leadership = Random.nextInt(30, 92).toFloat()
            val communication = Random.nextInt(45, 95).toFloat()
            val efficiency = Random.nextInt(45, 95).toFloat()
            val negotiation = Random.nextInt(40, 95).toFloat()
            val ambition = greed // Ambition is correlated with greed

            val newEmp = Employee(
                id = UUID.randomUUID().toString().take(6),
                name = name,
                role = role,
                weeklySalary = salary,
                experience = experience,
                reliability = reliability,
                honesty = honesty,
                greed = greed,
                intelligence = intelligence,
                leadership = leadership,
                loyalty = 75f,
                traitDescription = traits,
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
                permissions = initialPermissions
            )
            
            showToast("Welcome aboard, ${newEmp.name} has joined as your $role!")
            state.copy(hiredEmployees = state.hiredEmployees + newEmp)
        }
    }

    fun fireProfessional(empId: String) {
        updateState { state ->
            val emp = state.hiredEmployees.find { it.id == empId }
            if (emp != null) {
                showToast("Dismissed ${emp.name} from their position as ${emp.role}.")
            }
            state.copy(hiredEmployees = state.hiredEmployees.filter { it.id != empId })
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
            val trainingCost = 1000.0
            if (state.cash < trainingCost) {
                showToast("Insufficient reserves for professional training ($1,000).")
                return@updateState state
            }
            val updated = state.hiredEmployees.map { emp ->
                if (emp.id == empId) {
                    showToast("Enrolled ${emp.name} in training! Skills significantly upgraded.")
                    val updatedHistory = emp.careerHistory + "Week ${state.currentWeek}: Completed intensive corporate & professional training."
                    emp.copy(
                        efficiency = (emp.efficiency + 10f).coerceAtMost(100f),
                        intelligence = (emp.intelligence + 10f).coerceAtMost(100f),
                        communication = (emp.communication + 10f).coerceAtMost(100f),
                        careerHistory = updatedHistory
                    )
                } else emp
            }
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
            showToast("Paid a tithe of $${String.format("%,.0f", amount)}. Gained generosity & integrity.")
            state.copy(
                cash = state.cash - amount,
                generosity = (state.generosity + 4.5f).coerceAtMost(100f),
                integrity = (state.integrity + 3.5f).coerceAtMost(100f),
                trustworthiness = (state.trustworthiness + 5.0f).coerceAtMost(100f),
                spiritualTotalTithesPaid = state.spiritualTotalTithesPaid + amount
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

    fun deleteMail(mailId: String) {
        updateState { state ->
            state.copy(mailbox = state.mailbox.filter { it.id != mailId })
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
                savingsBalance = state.savingsBalance + amount
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
                savingsBalance = state.savingsBalance - amount
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
                loanAmount = state.loanAmount + amount
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
            showToast("Repaid $${String.format("%,.2f", repayAmount)} of active loan!")
            state.copy(
                cash = state.cash - repayAmount,
                loanAmount = state.loanAmount - repayAmount
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
                taxDebt = state.taxDebt - repayAmount
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
                mortgageAmount = state.mortgageAmount + amount
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
                mortgageAmount = state.mortgageAmount - repayAmount
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

    fun saveActiveNoteText(text: String) {
        updateState { state ->
            state.copy(activeNoteText = text)
        }
    }

    fun addSpiritualJournalEntry(entry: String) {
        updateState { state ->
            state.copy(spiritualJournal = listOf(entry) + state.spiritualJournal.take(19))
        }
    }

    fun takeConcertPhoto(description: String) {
        updateState { state ->
            state.copy(savedPhotos = listOf(description) + state.savedPhotos.take(19))
        }
    }
}
