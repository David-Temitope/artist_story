package com.example.data

import java.util.UUID
import kotlin.random.Random

data class NpcSimulationResult(
    val updatedNPCs: List<NPCEntity>,
    val finalStress: Float,
    val finalPublicImage: Float,
    val finalVoice: Float,
    val finalWriting: Float,
    val finalHappiness: Float = 75f,
    val releasedSingles: List<NpcSingleRelease> = emptyList()
)

data class NpcSingleRelease(
    val artistId: String,
    val artistName: String,
    val title: String,
    val genre: String,
    val rating: Float,
    val hype: Float,
    val firstWeekStreams: Long
)

object NpcSimulationHelper {

    fun processWeeklyNpcs(
        state: GameStateEntity,
        nextWeek: Int,
        initialStress: Float,
        initialPublicImage: Float,
        initialVoice: Float,
        initialWriting: Float,
        initialHappiness: Float,
        updatedSongs: List<Song>,
        updatedMail: MutableList<MailMessage>,
        reportBuilder: StringBuilder
    ): NpcSimulationResult {
        var currentStress = initialStress
        var currentPublicImage = initialPublicImage
        var updatedVoice = initialVoice
        var updatedWriting = initialWriting
        var currentPlayerHappiness = initialHappiness
        val releasedSingles = mutableListOf<NpcSingleRelease>()

        val updatedNPCs = state.npcs.map { npc ->
            var currentNPCWealth = npc.wealth
            var currentNPCFans = npc.fans
            var currentNPCPopularity = npc.popularity
            var currentNPCStress = npc.stress
            var currentNPCMentalHealth = npc.mentalHealth
            var npcAge = npc.age
            var currentRelation = npc.relationshipWithPlayer
            var currentBestSongTitle = npc.bestSongTitle
            var currentBestSongRating = npc.bestSongRating
            var currentBestSongStreams = npc.bestSongStreams
            var currentBestSongGenre = npc.bestSongGenre
            var currentBestSongHype = npc.bestSongHype
            var currentBestSongReleaseWeek = npc.bestSongReleaseWeek
            var currentBestSongReleaseYear = npc.bestSongReleaseYear

            // Psychology Metrics State
            var currentIntegrity = npc.integrity
            var currentGenerosity = npc.generosity
            var currentPride = npc.pride
            var currentHumility = npc.humility
            var currentLoyalty = npc.loyalty
            var currentTrust = npc.trust
            var currentJealousy = npc.jealousy
            var currentGreed = npc.greed
            var currentEgo = npc.ego
            var currentPatience = npc.patience
            var currentHappiness = npc.happiness
            var currentConfidence = npc.confidence
            var currentEmotionalState = npc.emotionalState

            // Age NPCs up annually
            if (nextWeek == 1) {
                npcAge += 1
            }

            // 1. Decay Memories
            val updatedMemories = npc.memories.map { memory ->
                val newImpact = if (memory.emotionalImpact > 0f) {
                    (memory.emotionalImpact - memory.decayRate).coerceAtLeast(0f)
                } else {
                    (memory.emotionalImpact + memory.decayRate).coerceAtMost(0f)
                }
                memory.copy(emotionalImpact = newImpact)
            }.filter { it.isCoreMemory || Math.abs(it.emotionalImpact) > 5f }

            // 2. Trigger Independent Life Events (1.5% chance each week)
            val updatedLifeEvents = npc.lifeEvents.toMutableList()
            val eventRand = Random.nextFloat()
            if (eventRand < 0.015f && !npc.isRetired) {
                val eventRoll = Random.nextInt(6)
                var eventMsg = ""
                when (eventRoll) {
                    0 -> { // Marriage
                        eventMsg = "💍 Marriage: ${npc.name} got married in a beautiful private ceremony!"
                        currentHappiness = (currentHappiness + 20f).coerceAtMost(100f)
                        currentNPCStress = (currentNPCStress - 15f).coerceAtLeast(0f)
                        updatedLifeEvents.add("Married (Week ${state.currentWeek}, Year ${state.currentYear})")
                    }
                    1 -> { // Divorce
                        eventMsg = "💔 Breakup: ${npc.name} went through a public and messy divorce."
                        currentHappiness = (currentHappiness - 25f).coerceAtLeast(0f)
                        currentNPCStress = (currentNPCStress + 30f).coerceAtMost(100f)
                        currentNPCWealth = (currentNPCWealth * 0.6)
                        updatedLifeEvents.add("Divorced (Week ${state.currentWeek}, Year ${state.currentYear})")
                    }
                    2 -> { // Child Born
                        eventMsg = "👶 Parenthood: ${npc.name} welcomed a new baby into their family!"
                        currentHappiness = (currentHappiness + 25f).coerceAtMost(100f)
                        currentPatience = (currentPatience + 10f).coerceAtMost(100f)
                        updatedLifeEvents.add("Had a child (Week ${state.currentWeek}, Year ${state.currentYear})")
                    }
                    3 -> { // Lawsuit / Legal Trouble
                        eventMsg = "⚖ Legal Trouble: ${npc.name} was hit with a major copyright and contract lawsuit."
                        currentNPCStress = (currentNPCStress + 25f).coerceAtMost(100f)
                        currentNPCWealth = (currentNPCWealth - 45000.0).coerceAtLeast(5000.0)
                        currentPride = (currentPride - 10f).coerceAtLeast(0f)
                        updatedLifeEvents.add("Sued by former manager (Week ${state.currentWeek}, Year ${state.currentYear})")
                    }
                    4 -> { // Health scare
                        eventMsg = "🏥 Health Hiatus: ${npc.name} checked into a retreat following vocal strain and exhaustion."
                        currentNPCMentalHealth = (currentNPCMentalHealth + 15f).coerceAtMost(100f)
                        currentNPCStress = (currentNPCStress - 20f).coerceAtLeast(0f)
                        updatedLifeEvents.add("Vocal surgery recovery (Week ${state.currentWeek}, Year ${state.currentYear})")
                    }
                    5 -> { // Massive Viral Milestone
                        eventMsg = "🏆 Viral Milestone: An archival track by ${npc.name} went viral globally on social platforms!"
                        currentNPCFans += 25000L
                        currentNPCPopularity = (currentNPCPopularity + 8f).coerceAtMost(100f)
                        currentConfidence = (currentConfidence + 15f).coerceAtMost(100f)
                        currentNPCWealth += 35000.0
                        updatedLifeEvents.add("Diamond archival certification (Week ${state.currentWeek}, Year ${state.currentYear})")
                    }
                }
                if (eventMsg.isNotEmpty()) {
                    reportBuilder.append("$eventMsg\n")
                    if (currentRelation >= 70f && eventRoll in 0..4) {
                        when (eventRoll) {
                            0, 2 -> {
                                currentPlayerHappiness = (currentPlayerHappiness + 2f).coerceAtMost(100f)
                                reportBuilder.append("🤝 Because you and ${npc.name} are close, you shared in this family milestone (+2 happiness).\n")
                            }
                            1, 3, 4 -> {
                                currentStress = (currentStress + 2f).coerceAtMost(100f)
                                reportBuilder.append("🤝 Your close relationship with ${npc.name} made their difficult news weigh on you (+2 stress).\n")
                            }
                        }
                    }
                }
            }

            // 3. Evolve Emotional State
            currentEmotionalState = when {
                currentNPCStress > 75f -> "Burned Out"
                currentHappiness > 75f -> "Thriving"
                currentNPCPopularity > 80f && currentEgo > 70f -> "Arrogant"
                currentRelation < 25f && currentJealousy > 60f -> "Envious"
                currentRelation > 75f -> "Affectionate"
                currentNPCMentalHealth < 30f -> "Melancholic"
                else -> "Content"
            }

            val weeksSinceLastRelease = if (currentBestSongReleaseYear > 0) {
                (state.currentYear - currentBestSongReleaseYear) * 52 + (state.currentWeek - currentBestSongReleaseWeek)
            } else Int.MAX_VALUE

            // 4. Decide NPC actions based on traits & emotions (Psychology-driven actions)
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
                // Rival actions: influenced by jealousy, pride, and low relationship
                currentRelation < 30f && randVal < (0.10f + currentJealousy / 500f) -> {
                    val subRoll = Random.nextInt(2)
                    if (subRoll == 0) {
                        actionMsg = "🚨 Rival Roast: Your rival, ${npc.name}, publicly roasted your production skills on a viral podcast! You lost -6% Public Image and gained +12 Stress."
                        currentPublicImage = (currentPublicImage - 6f).coerceAtLeast(10f)
                        currentStress = (currentStress + 12f).coerceAtMost(100f)
                    } else {
                        actionMsg = "📣 Rival Dispute: ${npc.name} started a heated debate about your artistic credibility on social media. Conversation Volume spiked, but Public Image fell by -4%."
                        currentPublicImage = (currentPublicImage - 4f).coerceAtLeast(10f)
                        currentStress = (currentStress + 6f).coerceAtMost(100f)
                    }
                }
                // Release song based on work ethic and personality
                weeksSinceLastRelease >= 6 && randVal > (0.95f - npc.workEthic / 2000f) -> {
                    val qualityRating = ((npc.creativity / 25f) + (Random.nextFloat() * 1.5f)).coerceIn(1.0f, 5.0f)
                    val songTitle = listOf("Neon Nights", "Shattered Illusion", "Cyberpunk Heart", "Hustler Anthem", "Faded Silhouette", "Platinum Reign", "Vortex of Dreams", "Retro Groove", "Electric Soul", "Digital Symphony", "Street Dreams", "Golden Horizon").random()
                    val genre = npc.genrePreference.ifBlank { "Pop" }
                    val hype = (18f + npc.confidence * 0.16f + npc.popularity * 0.12f + npc.discipline * 0.08f).coerceIn(15f, 55f)
                    val streamsEarned = ((npc.fans * 0.12 + npc.popularity * 1800) * (0.65 + qualityRating * 0.10) * (0.65 + hype / 100.0)).toLong().coerceAtLeast(1000L)
                    currentNPCFans += (qualityRating * 2500).toLong()
                    currentNPCPopularity = (currentNPCPopularity + qualityRating * 1.2f).coerceAtMost(100f)
                    currentNPCWealth += (streamsEarned * 0.0025)
                    currentBestSongTitle = songTitle
                    currentBestSongRating = qualityRating
                    currentBestSongStreams = streamsEarned
                    currentBestSongGenre = genre
                    currentBestSongHype = hype
                    currentBestSongReleaseWeek = nextWeek
                    currentBestSongReleaseYear = if (nextWeek == 1 && state.currentWeek == 52) state.currentYear + 1 else state.currentYear
                    releasedSingles.add(NpcSingleRelease(npc.id, npc.name, songTitle, genre, qualityRating, hype, streamsEarned))
                    actionMsg = "🎵 RELEASE: ${npc.name} released '$songTitle' ($genre), rated ${String.format("%.1f", qualityRating)}⭐, with ${hype.toInt()}% launch hype."
                }
                // Set out on tour (higher chance if high risk tolerance)
                randVal < (0.02f + npc.riskTolerance / 2000f) -> {
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

            // If currently touring, 25% chance to finish tour
            var finalIsOnTour = npc.isOnTour
            var finalTourMarkets = npc.tourMarkets
            if (randVal < 0.05f) {
                finalIsOnTour = true
                if (!npc.isOnTour || finalTourMarkets.isEmpty()) {
                    finalTourMarkets = npcTourMarkets(Random.nextInt(0, 6))
                }
            } else if (finalIsOnTour && Random.nextFloat() < 0.25f) {
                finalIsOnTour = false
                finalTourMarkets = emptyList()
                reportBuilder.append("🎤 Tour Complete: ${npc.name} successfully concluded their concert tour!\n")
            }

            // Dynamic stream accumulation for existing tracks
            if (randVal <= 0.88f && !npc.isRetired) {
                currentBestSongStreams += (currentNPCFans * (0.04f + Random.nextFloat() * 0.04f)).toLong()
            }

            // Normal drift
            // Smart NPC Decisions: Based on their business intel and creativity
            var driftPop = Random.nextFloat() * 2f - 0.9f

            // Active moves:
            // Highly creative artists might drop surprise music that boosts their fame but stresses them out
            if (npc.wealth > 100000.0 && !npc.isOnTour && Random.nextFloat() < 0.05f) {
                if (npc.creativity > 70f && npc.stress < 50f) {
                    currentNPCStress += 20f
                    currentNPCWealth -= 50000.0
                    driftPop += 5.0f
                    reportBuilder.append("🚀 COMPETITION: ${npc.name} just dropped a surprise viral hit! Their popularity surged.\n")
                } else if (npc.businessIntel > 75f) {
                    // Smart business moves, brand deals
                    currentNPCWealth += 150000.0
                    driftPop += 2.0f
                    reportBuilder.append("💼 COMPETITION: ${npc.name} secured a massive brand deal and grew their influence.\n")
                }
            }

            // If they are broke, they might go on an emergency low-tier tour
            if (currentNPCWealth < 10000.0 && !npc.isOnTour && npc.popularity > 20f && Random.nextFloat() < 0.2f) {
                finalIsOnTour = true
                finalTourMarkets = npcTourMarkets(Random.nextInt(0, 6))
                reportBuilder.append("🎤 COMPETITION: ${npc.name} went on an emergency club tour to avoid bankruptcy!\n")
            }
            if (finalIsOnTour && finalTourMarkets.isEmpty()) {
                finalTourMarkets = npcTourMarkets(Random.nextInt(0, 6))
            }
            if (!finalIsOnTour) finalTourMarkets = emptyList()

            // 5. Weekly Self-Reflection Note Generator
            val reflectionsList = mutableListOf<String>()
            if (currentNPCWealth < 15000.0) reflectionsList.add("I'm running dangerously low on funds. I need to secure a major gig or catalog deal.")
            if (currentNPCStress > 60f) reflectionsList.add("The industry demands are getting to me. I need to take a break or invest in a wellness retreat.")
            if (currentRelation < 30f) reflectionsList.add("I can't stand ${state.artistName}. They think they own the charts, but I'll prove them wrong.")
            else if (currentRelation > 75f) reflectionsList.add("It's rare to find a genuine friend like ${state.artistName} in this fake business. I should support them.")
            if (currentNPCPopularity > 85f) reflectionsList.add("Everyone wants a piece of me right now. I must stay grounded and focus on the art.")
            val generatedSelfReflection = if (reflectionsList.isNotEmpty()) reflectionsList.random() else "I need to focus on writing my next big hit."

            // --- NPC RELATIONSHIP STATUS ---
            // Relationship only decreases when player actively rejects collab/feature, referrals, or refuses help. No passive maintenance decay.
            val updatedRelation = currentRelation

            // --- TRUST DRIFT SUB-SYSTEM ---
            val playerIntegrityBonus = (state.integrity - 50f) / 150f
            val playerTrustworthinessBonus = (state.trustworthiness - 50f) / 150f
            val playerReliabilityBonus = (state.reliability - 50f) / 150f

            val updatedBusinessTrust = (npc.businessTrust + playerIntegrityBonus).coerceIn(0f, 100f)
            val updatedFinancialTrust = (npc.financialTrust + playerTrustworthinessBonus).coerceIn(0f, 100f)
            val updatedLeadershipTrust = (npc.leadershipTrust + playerIntegrityBonus).coerceIn(0f, 100f)
            val updatedConfidentiality = (npc.confidentiality + playerTrustworthinessBonus).coerceIn(0f, 100f)
            val updatedReliability = (npc.reliability + playerReliabilityBonus).coerceIn(0f, 100f)
            val updatedPublicReputation = (npc.publicReputation + (npc.popularity - npc.publicReputation) * 0.1f).coerceIn(0f, 100f)

            npc.copy(
                age = npcAge,
                popularity = (currentNPCPopularity + driftPop).coerceIn(10f, 100f),
                fans = (currentNPCFans + (driftPop * 400).toLong()).coerceAtLeast(100),
                wealth = currentNPCWealth,
                stress = (currentNPCStress - 2f).coerceIn(0f, 100f),
                mentalHealth = (currentNPCMentalHealth + 1f).coerceIn(0f, 100f),
                relationshipWithPlayer = updatedRelation,
                isOnTour = finalIsOnTour,
                tourMarkets = finalTourMarkets,
                bestSongTitle = currentBestSongTitle,
                bestSongRating = currentBestSongRating,
                bestSongStreams = currentBestSongStreams,
                bestSongGenre = currentBestSongGenre,
                bestSongHype = currentBestSongHype,
                bestSongReleaseWeek = currentBestSongReleaseWeek,
                bestSongReleaseYear = currentBestSongReleaseYear,

                // Psychology State Updates
                integrity = currentIntegrity,
                generosity = currentGenerosity,
                pride = currentPride,
                humility = currentHumility,
                loyalty = currentLoyalty,
                trust = currentTrust,
                jealousy = currentJealousy,
                greed = currentGreed,
                ego = currentEgo,
                patience = currentPatience,
                happiness = currentHappiness,
                confidence = currentConfidence,
                emotionalState = currentEmotionalState,
                memories = updatedMemories,
                lifeEvents = updatedLifeEvents,
                latestSelfReflection = generatedSelfReflection,
                businessTrust = updatedBusinessTrust,
                financialTrust = updatedFinancialTrust,
                leadershipTrust = updatedLeadershipTrust,
                confidentiality = updatedConfidentiality,
                reliability = updatedReliability,
                publicReputation = updatedPublicReputation
            )
        }

        // --- RELATIONSHIP & OPPORTUNITY NETWORK ENGINE: GOSSIP & NETWORK DIFFUSION ---
        val finalNPCs = updatedNPCs.toMutableList()
        val randomNPC1 = updatedNPCs.randomOrNull()
        val randomNPC2 = updatedNPCs.randomOrNull()
        val gossipCandidates = listOfNotNull(randomNPC1, randomNPC2).distinct()

        gossipCandidates.forEach { npcA ->
            val npcA_rel = npcA.relationshipWithPlayer
            if (npcA_rel > 70f || npcA_rel < 30f) {
                // Find another NPC who shares at least one network with npcA
                val partner = updatedNPCs.firstOrNull { npcB ->
                    npcB.id != npcA.id && npcB.networks.any { net -> npcA.networks.contains(net) }
                }
                if (partner != null) {
                    val partnerIdx = finalNPCs.indexOfFirst { it.id == partner.id }
                    if (partnerIdx != -1) {
                        val oldPartner = finalNPCs[partnerIdx]
                        val influenceFactor = (npcA_rel - 50f) * 0.1f // positive if friend, negative if rival
                        val change = influenceFactor.coerceIn(-5f, 5f)

                        val newPartnerRel = (oldPartner.relationshipWithPlayer + change).coerceIn(0f, 100f)
                        val newPartnerTrust = (oldPartner.personalTrust + (change * 0.8f)).coerceIn(0f, 100f)

                        finalNPCs[partnerIdx] = oldPartner.copy(
                            relationshipWithPlayer = newPartnerRel,
                            personalTrust = newPartnerTrust
                        )

                        if (change > 1.2f) {
                            reportBuilder.append("📣 Word of Mouth: ${npcA.name} spoke highly of you to ${oldPartner.name} in their shared ${npcA.networks.firstOrNull() ?: "social circle"}. ${oldPartner.name}'s relationship score with you rose!\n")
                        } else if (change < -1.2f) {
                            reportBuilder.append("🗣 Gossip: Your rival, ${npcA.name}, spread rumors about you to ${oldPartner.name} in their shared ${npcA.networks.firstOrNull() ?: "social circle"}. ${oldPartner.name}'s trust in you fell.\n")
                        }
                    }
                }
            }
        }

        // --- DYNAMIC OPPORTUNITY REFERRAL ENGINE (REFERRAL CHAINS) ---
        val highlyConnectedNpcs = finalNPCs.filter { it.relationshipWithPlayer >= 65f }
        if (highlyConnectedNpcs.isNotEmpty() && Random.nextFloat() < 0.25f && !updatedMail.any { it.id.startsWith("referral_") }) {
            val recommender = highlyConnectedNpcs.random()
            val randomNetwork = recommender.networks.randomOrNull() ?: "Music Industry Network"

            // If player has not done something significant, the other party is not impressed and declines
            val playerHasProvenCareer = updatedSongs.any { it.isReleased && it.totalStreams >= 15000L } && state.fans >= 2500L
            if (!playerHasProvenCareer) {
                reportBuilder.append("🤝 Connection Outreach: ${recommender.name} mentioned your name to contacts in $randomNetwork, but the other party was unimpressed: 'They haven't proved themselves with any notable releases or streams yet. We cannot consider business at this stage.'\n")
            } else {
                val mailId = "referral_" + UUID.randomUUID().toString().take(6)
                val isBrand = Random.nextFloat() > 0.5f
                val integrityBonus = if (state.integrity >= 70f) 1.20 else 1.00

                if (isBrand) {
                    val brandNames = listOf("Aether Apparel", "Veloce Automobiles", "Horizon Watches", "Vanguard Audio", "Nova Cosmetics")
                    val brand = brandNames.random()
                    val payout = (recommender.relationshipWithPlayer * 250.0 * integrityBonus).coerceAtLeast(2500.0)

                    val referralChainStr = "${recommender.name} -> Elite Brands Agency -> ${brand} Representative"

                    val brandMail = MailMessage(
                        id = mailId,
                        sender = brand,
                        subject = "Promotional Campaign Inquiry (via ${recommender.name})",
                        body = "Dear ${state.artistName},\n\nWe are contacting you following an introduction from your colleague, ${recommender.name}.\n\nAfter reviewing your catalog metrics and verified audience in the $randomNetwork, we would like to engage you for a promotional campaign. Budget allocation is \$${String.format("%,.0f", payout)}.\n\nReferral Chain:\n$referralChainStr\n\nPlease reply to confirm your participation.",
                        type = "METUBE_BRAND_DEAL",
                        cashOffer = payout,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                    updatedMail.add(brandMail)
                    reportBuilder.append("💼 Connection Inquiry: ${recommender.name} introduced you to ${brand}, who sent a promotional campaign proposal to your Mailbox.\n")
                } else {
                    val collabNames = listOf("Aria Sterling", "Zion Kidd", "Luna Sol", "Jax Teller", "Nico Black")
                    val collabArtist = collabNames.random()
                    val featFee = (recommender.relationshipWithPlayer * 200.0 * integrityBonus).coerceAtLeast(2000.0)

                    val referralChainStr = "${recommender.name} -> Studio Engineer -> ${collabArtist} Management"

                    val featMail = MailMessage(
                        id = mailId,
                        sender = collabArtist,
                        subject = "Collaboration Inquiry (via ${recommender.name})",
                        body = "Hello ${state.artistName},\n\n${recommender.name} shared your releases with my team. We like the direction of your music and would like to invite you as a featured collaborator on my upcoming single. Collaboration fee is \$${String.format("%,.0f", featFee)}.\n\nReferral Chain:\n$referralChainStr\n\nLet me know if you are interested in moving forward.",
                        type = "FEATURE",
                        cashOffer = featFee,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                    updatedMail.add(featMail)
                    reportBuilder.append("🎵 Connection Inquiry: ${recommender.name} connected you with ${collabArtist} for a collaboration feature. Check Mailbox.\n")
                }
            }
        }

        return NpcSimulationResult(
            updatedNPCs = finalNPCs,
            finalStress = currentStress,
            finalPublicImage = currentPublicImage,
            finalVoice = updatedVoice,
            finalWriting = updatedWriting,
            finalHappiness = currentPlayerHappiness,
            releasedSingles = releasedSingles
        )
    }
}
