package com.example.data

import com.squareup.moshi.JsonClass
import kotlin.random.Random

@JsonClass(generateAdapter = true)
data class DilemmaChoice(
    val id: String,
    val actionText: String, // Role-playing character response: e.g. "Take the deal.", "Play it safe.", "Bet everything on the single."
    val shortTitle: String, // e.g. "ACCEPT THE ADVANCE", "DECLINE & STAY INDIE", "NEGOTIATE HARDER"
    val teaserConsequence: String, // Communicates potential consequences without giving away all numbers
    val requirementText: String? = null, // e.g. "Requires Business Savvy ≥ 30"
    val requiredBusinessSkill: Float = 0f,
    val requiredIntegrity: Float = 0f,
    val isHighlighted: Boolean = false,
    val cashDelta: Double = 0.0,
    val reputationDelta: Float = 0.0f,
    val fansDelta: Long = 0L,
    val stressDelta: Float = 0.0f,
    val energyDelta: Int = 0,
    val relationshipDeltaNpc: String? = null,
    val outcomeNarrative: String = ""
)

fun DilemmaChoice.isAvailableFor(businessSkill: Float, integrity: Float): Boolean =
    businessSkill >= requiredBusinessSkill && integrity >= requiredIntegrity

@JsonClass(generateAdapter = true)
data class CareerDilemma(
    val id: String,
    val category: String, // "RECORD DEAL", "MEDIA CONTROVERSY", "CREATIVE DISPUTE", "TOUR SPONSOR", "WEALTH DILEMMA", "BACKSTAGE DRAMA"
    val headline: String,
    val situationNarrative: String, // Situation: Atmospheric short story
    val stakeholders: String, // Context: Who is involved?
    val stakes: String, // Context: What is at stake?
    val urgencyTag: String = "DECISION REQUIRED",
    val week: Int = 1,
    val year: Int = 1,
    val choices: List<DilemmaChoice>
)

object CareerDilemmaGenerator {

    fun generateDilemmaForState(
        state: GameStateEntity,
        random: Random = Random.Default
    ): CareerDilemma {
        val week = state.currentWeek
        val year = state.currentYear
        val releasedCount = state.songs.count { it.isReleased }
        val hasLabel = state.signedLabelId != null
        val isFamous = state.fans >= 75000
        val isMidTier = state.fans in 15000 until 75000 && releasedCount >= 2
        val isEarlyStage = !isFamous && !isMidTier

        val pool = mutableListOf<CareerDilemma>()

        if (isEarlyStage) {
            // Early-stage career dilemmas (realistic gritty indie struggles)
            // 1. Underground Venue Promoter Split
            pool.add(
                CareerDilemma(
                    id = "dilemma_early_promoter_${week}_${year}",
                    category = "LOCAL GIG DISPUTE",
                    headline = "Underground Venue Promoter Shortchanges Your Door Split",
                    situationNarrative = "After performing a packed two-hour set at a local underground music club, the venue promoter claims bar sales were slow and offers you only $150 of the promised $450 door split.",
                    stakeholders = "Club Promoter (Vince), Local Scene Regulars, Your Bandmates",
                    stakes = "Immediate gig cash vs. Maintaining peaceful venue relationships for future local bookings.",
                    urgencyTag = "BACKSTAGE SETTLEMENT",
                    week = week,
                    year = year,
                    choices = listOf(
                        DilemmaChoice(
                            id = "choice_stand_ground",
                            actionText = "Stand your ground and demand the full agreed $450 payout.",
                            shortTitle = "DEMAND FULL PAYMENT",
                            teaserConsequence = "Secures the cash owed to you. Burning a bridge with this venue promoter.",
                            cashDelta = 450.0,
                            reputationDelta = 2f,
                            stressDelta = 6f,
                            outcomeNarrative = "You stood firm and quoted the written agreement. The promoter grudgingly paid out the full $450, though he warned he won't book you again."
                        ),
                        DilemmaChoice(
                            id = "choice_compromise_door",
                            actionText = "Accept a $250 compromise and preserve the booking relationship.",
                            shortTitle = "ACCEPT PARTIAL SPLIT",
                            teaserConsequence = "Recovers partial expenses without burning local venue bridges.",
                            cashDelta = 250.0,
                            stressDelta = 2f,
                            outcomeNarrative = "You shook hands on $250. The promoter appreciated your professionalism and promised priority weekend slots for upcoming events."
                        ),
                        DilemmaChoice(
                            id = "choice_leverage_crowd",
                            actionText = "Call out the promoter politely on social media to build scene leverage.",
                            shortTitle = "RALLY LOCAL SCENE",
                            teaserConsequence = "Builds community solidarity among fellow underground artists. Escalates tension.",
                            reputationDelta = 5f,
                            fansDelta = 350L,
                            stressDelta = 5f,
                            outcomeNarrative = "You posted a thoughtful breakdown of indie artist door splits. The local music community rallied behind you, winning respect from peers."
                        )
                    )
                )
            )

            // 2. Day Job vs Studio Time
            pool.add(
                CareerDilemma(
                    id = "dilemma_early_dayjob_${week}_${year}",
                    category = "WORK-LIFE BALANCE",
                    headline = "Day Job Manager Demands Weekend Overtime During Studio Sessions",
                    situationNarrative = "Your day job manager offers you mandatory double-overtime shifts this weekend with $600 extra pay. However, you had scheduled your only dedicated weekend studio recording block to finish your vocal demos.",
                    stakeholders = "Day Job Supervisor, Your Studio Collaborator, Your Energy Reserve",
                    stakes = "Immediate survival cash vs. Precious songwriting momentum and vocal consistency.",
                    urgencyTag = "SCHEDULE CLASH",
                    week = week,
                    year = year,
                    choices = listOf(
                        DilemmaChoice(
                            id = "choice_take_overtime",
                            actionText = "Take the overtime shifts to build a safety cash cushion.",
                            shortTitle = "WORK OVERTIME SHIFT",
                            teaserConsequence = "Earns immediate cash buffer. Postpones songwriting progress and increases fatigue.",
                            cashDelta = 600.0,
                            stressDelta = 8f,
                            energyDelta = -20,
                            outcomeNarrative = "You worked 24 hours of grueling weekend overtime. Your bank account is healthier, though you had to delay your vocal tracking."
                        ),
                        DilemmaChoice(
                            id = "choice_commit_studio",
                            actionText = "Decline the overtime and lock yourself in the studio.",
                            shortTitle = "FOCUS ON MUSIC",
                            teaserConsequence = "Guarantees uninterrupted creative flow. Tightens your personal weekly budget.",
                            reputationDelta = 3f,
                            stressDelta = -4f,
                            outcomeNarrative = "You spent the entire weekend recording and refining chord progressions. Your craft leveled up significantly."
                        ),
                        DilemmaChoice(
                            id = "choice_split_shifts",
                            actionText = "Work night shifts and record during early morning hours.",
                            shortTitle = "GRIND BOTH",
                            teaserConsequence = "Maintains income and finishes tracks, but severely depletes physical stamina.",
                            cashDelta = 300.0,
                            stressDelta = 14f,
                            energyDelta = -35,
                            outcomeNarrative = "Operating on coffee and sheer willpower, you completed both the shifts and the track recording, leaving you exhausted but accomplished."
                        )
                    )
                )
            )
        }

        if (isMidTier || isFamous) {
            // Mid-stage and established dilemmas
            // 1. Creative vs Commercial Dilemma
            pool.add(
                CareerDilemma(
                    id = "dilemma_creative_${week}_${year}",
                    category = "CREATIVE DIRECTION",
                    headline = "Major Hitmaker Wants to Overhaul Your Signature Sound",
                    situationNarrative = "Grammy-winning producer Damon Vance stopped by your session. He promises a guaranteed radio #1 smash hit if you replace your acoustic instrumentation with synthetic viral drops and simplify your chorus lyrics for algorithm traction.",
                    stakeholders = "Damon Vance (Super-Producer), SoundWave Radio Network, Your Core Fans",
                    stakes = "Mainstream radio dominance vs. Authentic artistic integrity and hardcore fan loyalty.",
                    urgencyTag = "STUDIO DILEMMA",
                    week = week,
                    year = year,
                    choices = listOf(
                        DilemmaChoice(
                            id = "choice_commercial",
                            actionText = "Bet everything on the single.",
                            shortTitle = "GO FULL COMMERCIAL",
                            teaserConsequence = "Higher immediate streams and massive mainstream radio reach. Disappoints purist fans.",
                            cashDelta = 15000.0,
                            fansDelta = 8500L,
                            reputationDelta = -6f,
                            stressDelta = 8f,
                            outcomeNarrative = "You embraced the commercial makeover. The song exploded on top-40 charts, bringing thousands of casual listeners, though underground critics accused you of selling out."
                        ),
                        DilemmaChoice(
                            id = "choice_purist",
                            actionText = "Play it safe with your true sound.",
                            shortTitle = "REJECT THE TREND",
                            teaserConsequence = "Protects your artistic identity. Earns critical respect and solidifies core community trust.",
                            reputationDelta = 10f,
                            fansDelta = 1200L,
                            stressDelta = -5f,
                            outcomeNarrative = "You stood by your original artistic vision. Indie music publications hailed your uncompromising integrity, creating an intensely loyal cult following."
                        ),
                        DilemmaChoice(
                            id = "choice_hybrid",
                            actionText = "Call the producer personally to compromise.",
                            shortTitle = "FIND THE MIDDLE GROUND",
                            teaserConsequence = "Balances melodic authenticity with polished mixing. Requires high creative stamina.",
                            requiredBusinessSkill = 20f,
                            cashDelta = 6000.0,
                            fansDelta = 4000L,
                            reputationDelta = 4f,
                            stressDelta = 12f,
                            outcomeNarrative = "Through late-night negotiation sessions, you crafted a hybrid cut that retained your authentic message while achieving sleek broadcast fidelity."
                        )
                    )
                )
            )

            // 2. Media Rumors / Paparazzi Dilemma
            pool.add(
                CareerDilemma(
                    id = "dilemma_media_${week}_${year}",
                    category = "MEDIA CONTROVERSY",
                    headline = "Tabloid Gossip Account Circulates Scurrilous Backstage Rumors",
                    situationNarrative = "A sensationalist gossip blog with 4 million followers claims you threw a lavish VIP tantrum and alienated hotel staff after a late-night recording session. The post is picking up rapid retweets across social feeds.",
                    stakeholders = "PopPulse Gossip Desk, Brand Sponsors, Online Commentators",
                    stakes = "Public brand sentiment, corporate sponsorship viability, mental peace.",
                    urgencyTag = "PR CRISIS",
                    week = week,
                    year = year,
                    choices = listOf(
                        DilemmaChoice(
                            id = "choice_address_public",
                            actionText = "Address it publicly with receipts.",
                            shortTitle = "POST A TRANSPARENT VIDEO",
                            teaserConsequence = "Clears your name with facts. Fuels online discussion cycles for 48 hours.",
                            reputationDelta = 8f,
                            fansDelta = 3000L,
                            stressDelta = 5f,
                            outcomeNarrative = "You released a calm, video statement setting the record straight. Fans rallied behind your honesty, exposing the gossip outlet's baseless fabrications."
                        ),
                        DilemmaChoice(
                            id = "choice_ignore",
                            actionText = "Ignore the rumors and let music speak.",
                            shortTitle = "STAY SILENT",
                            teaserConsequence = "Avoids feeding the algorithm drama. Some unverified rumors linger in search results.",
                            reputationDelta = -2f,
                            stressDelta = -8f,
                            outcomeNarrative = "You maintained complete silence. The tabloid cycle moved on to the next celebrity target by Tuesday, sparing your mental energy."
                        ),
                        DilemmaChoice(
                            id = "choice_legal_strike",
                            actionText = "Dispatch legal counsel with a cease & desist.",
                            shortTitle = "SEND CEASE & DESIST",
                            teaserConsequence = "Forces immediate retraction. Costs legal fees and signals zero tolerance.",
                            cashDelta = -4500.0,
                            reputationDelta = 6f,
                            stressDelta = 2f,
                            outcomeNarrative = "Your legal team issued an airtight formal retraction demand. The outlet deleted the offending article with a public apology."
                        )
                    )
                )
            )
        }

        // Tour Sponsor / Brand Endorsement Dilemma (Superstar only)
        if (isFamous) {
            pool.add(
                CareerDilemma(
                    id = "dilemma_sponsor_${week}_${year}",
                    category = "TOUR SPONSORSHIP",
                    headline = "High-Roller Energy Drink Brand Offers Exclusive Tour Title Partnership",
                    situationNarrative = "Volt Surge Beverage Co. has tendered a lucrative $60,000 tour underwriting package. However, their contract demands invasive giant neon logos across your stage backdrop and mandatory social product placements.",
                    stakeholders = "Volt Surge Marketing VP, Tour Production Crew, Concert Attendees",
                    stakes = "Significant tour cash subsidy vs. Visual stage aesthetic and fan commercial fatigue.",
                    urgencyTag = "COMMERCIAL CONTRACT",
                    week = week,
                    year = year,
                    choices = listOf(
                        DilemmaChoice(
                            id = "choice_take_sponsor",
                            actionText = "Take the deal and pocket the guaranteed bag.",
                            shortTitle = "ACCEPT $60,000 TITLE SPONSOR",
                            teaserConsequence = "Immediate major cash infusion. Reduces visual stage elegance and artistic prestige.",
                            cashDelta = 60000.0,
                            reputationDelta = -5f,
                            stressDelta = 6f,
                            outcomeNarrative = "You signed the Volt Surge sponsorship contract. The cash flow unlocked massive stage pyrotechnics, despite slight fan grumbling about the logo size."
                        ),
                        DilemmaChoice(
                            id = "choice_decline_sponsor",
                            actionText = "Decline to keep the stage pristine.",
                            shortTitle = "REJECT CORPORATE BRANDING",
                            teaserConsequence = "Keeps the live experience 100% focused on pure musicianship. Requires self-funding.",
                            reputationDelta = 8f,
                            stressDelta = -4f,
                            outcomeNarrative = "You politely declined the commercial deal. Fans celebrated your commitment to an uncompromised, art-first concert atmosphere."
                        ),
                        DilemmaChoice(
                            id = "choice_counter_sponsor",
                            actionText = "Negotiate for discreet VIP-only integration.",
                            shortTitle = "COUNTER-OFFER DISCREET TERMS",
                            teaserConsequence = "Requires negotiating finesse. Secures partial funding without ruining the stage.",
                            requiredBusinessSkill = 25f,
                            cashDelta = 32000.0,
                            reputationDelta = 3f,
                            stressDelta = 4f,
                            outcomeNarrative = "Your counter-proposal was accepted: Volt Surge gained exclusive backstage hospitality rights while keeping your main stage setup clean."
                        )
                    )
                )
            )
        }

        // Record Label Advance vs Independence Dilemma (Only when artist is established with a proven catalog)
        if (!hasLabel && isFamous) {
            pool.add(
                CareerDilemma(
                    id = "dilemma_label_${week}_${year}",
                    category = "RECORD DEAL",
                    headline = "Major Label A&R Offers Upfront Signing Advance",
                    situationNarrative = "A senior vice president at Apex Records invites you to dinner in a penthouse suite. They slid an advance agreement across the table with a $120,000 cash check, but the boilerplate gives them 75% of your master rights.",
                    stakeholders = "Apex Records Executive Board, Your Future Catalog Value",
                    stakes = "Instant millionaire lifestyle vs. Lifetime master ownership and creative freedom.",
                    urgencyTag = "CAREER DEFINING MOMENT",
                    week = week,
                    year = year,
                    choices = listOf(
                        DilemmaChoice(
                            id = "choice_take_advance",
                            actionText = "Take the deal and secure instant financial security.",
                            shortTitle = "SIGN 360 ADVANCE DEAL",
                            teaserConsequence = "Massive upfront cash payout. You surrender long-term master royalty ownership.",
                            cashDelta = 120000.0,
                            reputationDelta = -4f,
                            fansDelta = 15000L,
                            stressDelta = 10f,
                            outcomeNarrative = "You signed the major label deal. Cash flooded your bank account and label radio power took effect, but you are now bound by corporate delivery quotas."
                        ),
                        DilemmaChoice(
                            id = "choice_stay_independent",
                            actionText = "Stay independent and bet on yourself.",
                            shortTitle = "WALK AWAY INDEPENDENT",
                            teaserConsequence = "You retain 100% of your masters and future equity. Growth remains gradual.",
                            reputationDelta = 12f,
                            fansDelta = 2500L,
                            stressDelta = -6f,
                            outcomeNarrative = "You stood up and shook their hand, choosing to stay independent. You retain complete ownership of every note you ever write."
                        ),
                        DilemmaChoice(
                            id = "choice_counter_distribution",
                            actionText = "Propose a master-retaining distribution deal.",
                            shortTitle = "DEMAND DISTRIBUTION ONLY",
                            teaserConsequence = "Secures label global shipping & playlist pitching while keeping your master rights.",
                            requiredBusinessSkill = 35f,
                            cashDelta = 35000.0,
                            fansDelta = 8000L,
                            reputationDelta = 6f,
                            stressDelta = 6f,
                            outcomeNarrative = "You outmaneuvered the executives with an airtight distribution-only licensing pitch, winning global distribution while retaining 100% copyright."
                        )
                    )
                )
            )
        }

        check(pool.isNotEmpty()) { "No career dilemma is eligible for the current player state." }
        return pool[random.nextInt(pool.size)]
    }
}
