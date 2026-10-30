package com.example.data

import java.util.UUID
import kotlin.random.Random

data class MeTubeAndBrandResult(
    val updatedMeTubeVideos: List<MeTubeVideo>,
    val newWatchTime: Double,
    val newSubscribers: Long,
    val meTubeEarnings: Double,
    val activeBrandDealName: String?,
    val brandDealWeeklyPayout: Double,
    val brandDealWeeksRemaining: Int,
    val brandDealPostedThisWeek: Boolean,
    val addedCash: Double,
    val addedSalary: Double
)

object MeTubeAndBrandHelper {

    fun processMeTubeAndBrandDeals(
        state: GameStateEntity,
        currentPublicImage: Float,
        updatedSpiritualJournal: List<String>,
        updatedMail: MutableList<MailMessage>,
        reportBuilder: StringBuilder
    ): MeTubeAndBrandResult {
        var newWatchTime = state.meTubeWatchTime
        var newSubscribers = state.meTubeSubscribers
        var meTubeEarnings = 0.0
        var addedCash = 0.0
        var addedSalary = 0.0

        val playerVideos = state.meTubeVideos.filter { it.artistName == null || it.artistName == state.artistName }
        val rivalVideos = state.meTubeVideos.filter { it.artistName != null && it.artistName != state.artistName }

        val isChannelMonetized = state.meTubeSubscribers >= 1000L

        val updatedPlayerVideos = playerVideos.map { video ->
            val baseSubscribersMult = 1.0 + (newSubscribers / 10000.0)
            val newViews = (Random.nextInt(50, 500) * baseSubscribersMult * (if (video.type == "MUSIC_VIDEO") 3.0 else 1.2)).toLong()
            val newLikes = (newViews * (0.05 + Random.nextFloat() * 0.08)).toLong()

            // Monetization requirement: Must have at least 1,000 subscribers to earn ad revenue
            val revenue = if (isChannelMonetized) newViews * 0.0035 else 0.0
            meTubeEarnings += revenue

            video.copy(
                views = video.views + newViews,
                likes = video.likes + newLikes,
                revenueEarned = video.revenueEarned + revenue
            )
        }

        val updatedRivalVideos = rivalVideos.map { video ->
            val newViews = Random.nextInt(200, 3000).toLong()
            val newLikes = (newViews * (0.06 + Random.nextFloat() * 0.07)).toLong()
            video.copy(
                views = video.views + newViews,
                likes = video.likes + newLikes
            )
        }

        val updatedMeTubeVideos = (updatedPlayerVideos + updatedRivalVideos).toMutableList()

        if (updatedPlayerVideos.isNotEmpty()) {
            val gainedSubs = (updatedPlayerVideos.sumOf { it.views } * 0.001).toLong().coerceAtLeast(5L)
            newSubscribers += gainedSubs
            newWatchTime += (updatedPlayerVideos.size * 3.5)
            val isNowMonetized = newSubscribers >= 1000L

            if (isNowMonetized) {
                val meTubeLabelCut = if (state.signedLabelId != null) {
                    meTubeEarnings * (if (state.signedLabelId == "syndicate_360") 0.25 else 0.15)
                } else 0.0
                val netMeTubeEarnings = meTubeEarnings - meTubeLabelCut
                meTubeEarnings = netMeTubeEarnings
                reportBuilder.append("📺 MeTube Partner Program: Gained +${String.format("%,d", gainedSubs)} subscribers (Total: ${String.format("%,d", newSubscribers)}). Monetized ad revenue: +$${String.format("%,.2f", netMeTubeEarnings)}.\n")
                if (meTubeLabelCut > 0.0) {
                    reportBuilder.append("   Record Label 360 Cut on Video Ads: -$${String.format("%,.2f", meTubeLabelCut)} taken by the label.\n")
                }
                addedSalary += netMeTubeEarnings
            } else {
                meTubeEarnings = 0.0
                reportBuilder.append("📺 MeTube: Gained +${String.format("%,d", gainedSubs)} subscribers (Total: ${String.format("%,d", newSubscribers)}/1,000). Ad revenue: $0.00 (Channel not monetized yet; requires 1,000 subscribers to unlock MeTube Partner Program ad payouts).\n")
            }
        }

        // 40% chance of generating a new MeTube video by another rival artist!
        if (Random.nextFloat() < 0.40f && state.npcs.isNotEmpty()) {
            val npc = state.npcs.filter { it.id != "council" && it.name != "Music Council" }.randomOrNull()
            if (npc != null) {
                val genreType = npc.genrePreference
                val videoType = listOf("MUSIC_VIDEO", "VLOG", "INTERVIEW", "LIVE_PERFORMANCE").random()
                val titles = when (genreType) {
                    "Pop" -> listOf("Summer Kiss (Official Video)", "My Sunset Drive Live Stadium Performance!", "How to write a Pop anthem in 10 mins", "Aria Nova VLOG: My LA Mansion tour!")
                    "Hip Hop" -> listOf("Grind Never Stops - Official Music Video", "Reviewing latest tracks with my crew!", "Studio Vlog: making beats in Atlanta", "CYPHER 2026 feat. DJ Blitz")
                    "Rock" -> listOf("Electric Thunder - Guitar Shredding Tutorial!", "Unboxing my custom Gibson Les Paul Studio", "Shattered Glass (LIVE at Wembley Stadium)", "Rock VLOG: Life on a tour coach bus")
                    "Electronic" -> listOf("Modular Synth Jam #42", "How I produce massive bass drops", "Midnight Club Set LIVE 2026", "Techno Studio Session with DJ Pulse")
                    "Indie" -> listOf("Acoustic Melody on the Front Porch", "Coffee Shop Melodies (Vinyl Studio Session)", "My songwriting process explained", "Day in the Life of an Independent Folk Artist")
                    else -> listOf("Vlog #12: Backstage madness!", "Official Live Session Video", "Making of my new album!")
                }
                val videoTitle = titles.random()
                val rivalViews = Random.nextLong(2000, 50000)
                val rivalLikes = (rivalViews * (0.05f + Random.nextFloat() * 0.06f)).toLong()
                val newRivalVideo = MeTubeVideo(
                    id = UUID.randomUUID().toString().take(6),
                    title = videoTitle,
                    type = videoType,
                    views = rivalViews,
                    likes = rivalLikes,
                    comments = listOf(
                        "This artist is a legend! 😍",
                        "The production quality is insane.",
                        "Much better than the mainstream trash out there!",
                        "Absolute masterpiece!"
                    ).shuffled().take(2),
                    revenueEarned = 0.0,
                    weekPublished = state.currentWeek,
                    yearPublished = state.currentYear,
                    artistName = npc.name,
                    thumbnailId = "ic_video"
                )
                updatedMeTubeVideos.add(newRivalVideo)
                reportBuilder.append("📺 MeTube Feed: Rival artist ${npc.name} published a new video: '${videoTitle}'!\n")
            }
        }

        // Brand Deal Promotion System (Subscribers threshold 20K+)
        var activeBrandDealName = state.activeBrandDealName
        var brandDealWeeklyPayout = state.brandDealWeeklyPayout
        var brandDealWeeksRemaining = state.brandDealWeeksRemaining
        var brandDealPostedThisWeek = state.brandDealPostedThisWeek

        if (activeBrandDealName != null) {
            if (currentPublicImage < 40f) {
                reportBuilder.append("💼 Brand Deal Fallout: $activeBrandDealName terminated your contract because your public image fell below 40%!\n")
                updatedMail.add(
                    MailMessage(
                        id = "withdrawn_brand_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = activeBrandDealName,
                        subject = "⚠ Contract Terminated: Public Fallout",
                        body = "Dear ${state.artistName}, we are disappointed by your recent public reputation slip. As per our standards, we are terminating our promotion contract immediately.",
                        type = "NORMAL",
                        expiresWeek = state.currentWeek + 2,
                        expiresYear = state.currentYear
                    )
                )
                activeBrandDealName = null
                brandDealWeeklyPayout = 0.0
                brandDealWeeksRemaining = 0
                brandDealPostedThisWeek = false
            } else {
                if (brandDealPostedThisWeek) {
                    val brandLabelCut = if (state.signedLabelId != null) {
                        brandDealWeeklyPayout * (if (state.signedLabelId == "syndicate_360") 0.25 else 0.15)
                    } else 0.0
                    val netBrandDealPayout = brandDealWeeklyPayout - brandLabelCut
                    addedCash += netBrandDealPayout
                    reportBuilder.append("💼 Brand Deal: Earned weekly payout of $${String.format("%,.2f", netBrandDealPayout)} from $activeBrandDealName for your promotional activities!\n")
                    if (brandLabelCut > 0.0) {
                        reportBuilder.append("   Record Label 360 Cut on Sponsor: -$${String.format("%,.2f", brandLabelCut)} taken by the label.\n")
                    }
                } else {
                    reportBuilder.append("💼 Brand Deal Warning: You did not post any promotions for $activeBrandDealName this week! No weekly payout was issued.\n")
                }

                brandDealWeeksRemaining -= 1
                if (brandDealWeeksRemaining <= 0) {
                    reportBuilder.append("🎉 Brand Deal Complete: Your endorsement contract with $activeBrandDealName has been successfully completed!\n")
                    activeBrandDealName = null
                    brandDealWeeklyPayout = 0.0
                    brandDealWeeksRemaining = 0
                    brandDealPostedThisWeek = false
                } else {
                    brandDealPostedThisWeek = false
                }
            }
        }

        // Recruitment offer
        val talentAgent = state.hiredEmployees.find { it.role == "Talent Agent" }
        val prManager = state.hiredEmployees.find { it.role == "PR Manager" }
        val bizManager = state.hiredEmployees.find { it.role == "Business Manager" }

        val talentAgentBonus = if (talentAgent != null) (talentAgent.experience / 100f) * 0.25f else 0f
        val prManagerBonus = if (prManager != null) (prManager.experience / 100f) * 0.15f else 0f
        val bizManagerBonus = if (bizManager != null) (bizManager.experience / 100f) * 0.10f else 0f

        val baseOfferChance = 0.35f
        val finalOfferChance = (baseOfferChance + talentAgentBonus + prManagerBonus + bizManagerBonus).coerceAtMost(0.90f)

        if (newSubscribers >= 5000 && activeBrandDealName == null && Random.nextFloat() < finalOfferChance) {
            val hasPendingDeal = updatedMail.any { it.type == "METUBE_BRAND_DEAL" && !it.isAccepted }
            if (!hasPendingDeal) {
                val latestCategory = updatedSpiritualJournal.find { it.startsWith("latest_metube_category_") }?.substringAfter("latest_metube_category_") ?: "Music & Art"
                val negotiatorBonus = if (talentAgent != null) (1.0 + (talentAgent.experience / 100f) * 0.40)
                                      else if (bizManager != null) (1.0 + (bizManager.experience / 100f) * 0.25)
                                      else 1.0
                val payout = (2500.0 + (newSubscribers * 0.04) + (state.integrity * 10.0)) * negotiatorBonus

                val generatedDeal = when (latestCategory) {
                    "Tech & Gadgets" -> {
                        val deals = listOf(
                            Triple("CyberPulse Neural Tech", "💼 CyberPulse AI Architecture Sponsorship", "Dear ${state.artistName},\n\nCyberPulse builds high-performance machine learning hardware. We are excited to offer you a 4-week sponsorship paying \$${String.format("%,.2f", payout * 1.15)} weekly to showcase our developer rigs. Tag #CyberPulse #AI #ad."),
                            Triple("Titanium Tech", "💼 Secure-Key Encryption Sponsorship", "Hello ${state.artistName},\n\nWe love your recent Tech & Gadgets content. We are offering a 4-week partnership paying \$${String.format("%,.2f", payout)} weekly to promote our secure portable hard-drives. Maintain a public image > 40% to keep payouts active! Tag #ad."),
                            Triple("Chronos Smartwatch", "💼 Chronos Wearables Ambassador Deal", "Hi ${state.artistName},\n\nYour tech posts caught our attention! We propose a 4-week promotion campaign for the Chronos Smartwatch paying \$${String.format("%,.2f", payout)} weekly. Let us know if you accept. #ad required."),
                            Triple("Apex Computing", "💼 Quantum Laptop Promotion Campaign", "Dear ${state.artistName},\n\nApex Computing wants to sign you as an ambassador for our ultra-high specs developer laptops. \$${String.format("%,.2f", payout)} weekly for 4 weeks. No other computer brands permitted!"),
                            Triple("Lumina VR", "💼 Lumina VR Virtual Reality Sponsorship", "Hello ${state.artistName},\n\nLumina Tech offers \$${String.format("%,.2f", payout)} weekly for 4 weeks to promote our VR headsets. Keep your reputation high and integrate mentions into your content.")
                        )
                        deals.random()
                    }
                    "Fashion & Lifestyle" -> {
                        val deals = listOf(
                            Triple("Velvet Aura Apparel", "💼 Velvet Aura Summer Ambassador Deal", "Dear ${state.artistName},\n\nYour elegant Fashion & Lifestyle posts are a perfect match for our style! Velvet Aura proposes a 4-week ambassador deal at \$${String.format("%,.2f", payout)} weekly. Keep public image > 40%!"),
                            Triple("Horizon Activewear", "💼 Horizon Sportswear Winter Ambassador", "Hi! Tyler from Horizon Sport. We propose a 4-week brand promotion at \$${String.format("%,.2f", payout)} weekly to showcase our athletic lines."),
                            Triple("Solis Eyewear", "💼 Solis Sunglasses Premium Promotion", "Hello! Solis Eyewear offers a 4-week campaign paying \$${String.format("%,.2f", payout)} weekly. Boost your style with our polarized shades!"),
                            Triple("Luna Cosmetics", "💼 Luna Organic Skincare Endorsement", "Dear Artist, Luna Cosmetics is looking for an organic ambassador. \$${String.format("%,.2f", payout)} weekly for 4 weeks to promote organic skin therapy."),
                            Triple("Elegance Boutique", "💼 Elegance Boutique Apparel Sponsorship", "Greetings, Elegance Boutique offers a 4-week promotion paying \$${String.format("%,.2f", payout)} weekly. Let's make fashion history together.")
                        )
                        deals.random()
                    }
                    "Gaming & Entertainment" -> {
                        val deals = listOf(
                            Triple("Volt Gaming Cola", "💼 Volt Energy Drink Promotion Contract", "Greetings ${state.artistName},\n\nVolt Cola is launching a high-voltage gaming energy drink. We want to sponsor your channel for 4 weeks at \$${String.format("%,.2f", payout)} weekly. Energize your fans!"),
                            Triple("Quest Studios", "💼 Quest Horizon RPG Launch Partnership", "Hey! Quest Studio wants to sponsor your Gaming posts. We will pay \$${String.format("%,.2f", payout)} weekly for 4 weeks to play our breakout RPG, Quest Horizon."),
                            Triple("Omega Esports", "💼 Omega Esports Jersey Endorsement", "Hello, Omega Esports proposes a 4-week deal at \$${String.format("%,.2f", payout)} weekly. Show off our latest pro apparel to your community."),
                            Triple("StreamCore", "💼 StreamCore Capture Card Brand Deal", "Greetings, StreamCore wants to sign you for 4 weeks at \$${String.format("%,.2f", payout)} weekly. Show your fans the ultimate capturing tool."),
                            Triple("Titan Arena", "💼 Titan Mobile Game Stream Sponsorship", "Hi, Titan Mobile offers a 4-week campaign at \$${String.format("%,.2f", payout)} weekly to review our action mobile arena. Tag #ad.")
                        )
                        deals.random()
                    }
                    "Finance & Crypto" -> {
                        val deals = listOf(
                            Triple("Sovereign Bank App", "💼 Sovereign Digital Wallet Promotion", "Hello ${state.artistName},\n\nSovereign Digital Bank offers a 4-week ambassador contract at \$${String.format("%,.2f", payout)} weekly to promote secure savings accounts. High integrity required."),
                            Triple("Ledger Shield", "💼 Ledger Shield Cold Wallet Partnership", "Greetings! Ledger Shield offers \$${String.format("%,.2f", payout)} weekly for 4 weeks. Inform your followers on cold wallet security."),
                            Triple("Apex Tax Advisory", "💼 Apex Tax Sheltering Software Ambassador", "Dear Artist, Apex Tax is looking for an influencer to promote our legal tax-planning tools. \$${String.format("%,.2f", payout)} weekly for 4 weeks."),
                            Triple("WealthVantage", "💼 WealthVantage Balanced Portfolio Promo", "Hello, WealthVantage proposes a 4-week campaign at \$${String.format("%,.2f", payout)} weekly promoting diversified mutual funds."),
                            Triple("Zion Gold Trust", "💼 Zion Gold Asset Accumulation Campaign", "Dear ${state.artistName}, Zion Trust offers a 4-week contract at \$${String.format("%,.2f", payout)} weekly. Promote stable gold assets.")
                        )
                        deals.random()
                    }
                    else -> { // "Music & Art"
                        val deals = listOf(
                            Triple("Pulse Headwear", "💼 Pulse Acoustics Headphones Sponsor", "Hello ${state.artistName},\n\nWe love your music. Pulse Acoustics is offering a 4-week ambassador deal paying \$${String.format("%,.2f", payout)} weekly to promote our studio headphones. Keep public image > 40%!"),
                            Triple("Aura Synthesizers", "💼 Aura Synth Analog Keyboards Promotion", "Hi! Aura Synthesizers proposes a 4-week gear endorsement deal paying \$${String.format("%,.2f", payout)} weekly. Mention Aura Synth on your next track."),
                            Triple("Starlight Strings", "💼 Starlight Acoustic String Partnership", "Hello! Starlight Instruments offers a 4-week campaign paying \$${String.format("%,.2f", payout)} weekly to promote our premium custom strings."),
                            Triple("Sonic Flow", "💼 Sonic Flow DAW Studio Software Endorsement", "Dear Artist, Sonic Flow offers a 4-week promotion paying \$${String.format("%,.2f", payout)} weekly to feature our editing tools."),
                            Triple("Beat Crafters", "💼 Beat Crafters Drum Pads Partnership", "Greetings! Beat Crafters Drum Pads offers a 4-week campaign paying \$${String.format("%,.2f", payout)} weekly. Let's make beats!")
                        )
                        deals.random()
                    }
                }

                updatedMail.add(
                    MailMessage(
                        id = "brand_deal_offer_" + java.util.UUID.randomUUID().toString().take(6),
                        sender = generatedDeal.first,
                        subject = generatedDeal.second,
                        body = generatedDeal.third,
                        type = "METUBE_BRAND_DEAL",
                        cashOffer = payout,
                        expiresWeek = state.currentWeek + 3,
                        expiresYear = state.currentYear
                    )
                )
                reportBuilder.append("✉ Brand Offer: A brand targeting your '${latestCategory}' niche reached out in your Mailbox!\n")
            }
        }

        return MeTubeAndBrandResult(
            updatedMeTubeVideos = updatedMeTubeVideos,
            newWatchTime = newWatchTime,
            newSubscribers = newSubscribers,
            meTubeEarnings = meTubeEarnings,
            activeBrandDealName = activeBrandDealName,
            brandDealWeeklyPayout = brandDealWeeklyPayout,
            brandDealWeeksRemaining = brandDealWeeksRemaining,
            brandDealPostedThisWeek = brandDealPostedThisWeek,
            addedCash = addedCash,
            addedSalary = addedSalary
        )
    }
}
