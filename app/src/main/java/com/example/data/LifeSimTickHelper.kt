package com.example.data

import kotlin.random.Random

data class LifeSimTickResult(
    val updatedCash: Double,
    val updatedEnergy: Int,
    val updatedStress: Float,
    val updatedAge: Int,
    val isLivingWithParents: Boolean,
    val isHomeless: Boolean,
    val currentRentalHouseId: String?,
    val updatedEducation: PlayerEducation,
    val updatedCurrentJob: PlayerJob?,
    val updatedActorCareer: ActorCareerState,
    val updatedFamilyMembers: List<FamilyMember>,
    val reportLines: List<String>
)

object LifeSimTickHelper {

    fun processWeeklyLifeSim(
        state: GameStateEntity,
        initialCash: Double,
        initialEnergy: Int,
        initialStress: Float,
        isNewYear: Boolean
    ): LifeSimTickResult {
        var cash = initialCash
        var energy = initialEnergy
        var stress = initialStress
        val reports = mutableListOf<String>()

        // 1. Age & Living situation
        var playerAge = state.playerAge
        if (isNewYear) {
            playerAge++
            reports.add("🎂 BIRTHDAY: Happy Birthday! You are now $playerAge years old.")
        }

        var isLivingWithParents = state.isLivingWithParents
        var isHomeless = state.isHomeless
        var currentRentalHouseId = state.currentRentalHouseId

        // At age 22, parents push player out
        if (playerAge >= 22 && isLivingWithParents) {
            isLivingWithParents = false
            reports.add("🚪 EVICTED FROM HOME: You reached age 22. Your parents packed your bags and told you it's time to be independent! You must rent or buy a house.")
            if (currentRentalHouseId == null && state.ownedHouses.isEmpty()) {
                isHomeless = true
            }
        }

        // Homelessness penalties
        if (isHomeless && !isLivingWithParents && currentRentalHouseId == null && state.ownedHouses.isEmpty()) {
            stress = (stress + 15f).coerceAtMost(100f)
            energy = (energy - 10).coerceAtLeast(0)
            reports.add("🏕️ HOMELESS: Living on the streets or crashing on strangers' couches is taking a heavy toll (+15% Stress, -10 Energy). Rent a house in the More menu!")
        } else if (currentRentalHouseId != null || state.ownedHouses.isNotEmpty() || isLivingWithParents) {
            isHomeless = false
        }

        // Housing rent & maintenance
        if (currentRentalHouseId != null) {
            val house = LifeSimData.HOUSES.find { it.id == currentRentalHouseId }
            if (house != null) {
                val totalHousingCost = house.weeklyRent + house.weeklyExpenses
                cash -= totalHousingCost
                reports.add("🏠 Housing: Paid \$${String.format("%,.0f", totalHousingCost)} for weekly rent and utility expenses for ${house.name}.")
            }
        }

        // 2. School / Education Progression
        var edu = state.education
        if (edu.isEnrolled && !edu.isGraduated) {
            val course = LifeSimData.COURSES.find { it.name == edu.courseName }
            if (course != null) {
                val nextCourseWeek = edu.currentWeekInCourse + 1
                val isBreak1 = nextCourseWeek in course.break1Start..course.break1End
                val isBreak2 = nextCourseWeek in course.break2Start..course.break2End
                val isOnBreak = isBreak1 || isBreak2

                // Energy cost: 10% during active school weeks, 0% during breaks
                if (!isOnBreak) {
                    energy = (energy - 10).coerceAtLeast(0)
                }

                // Tuition payment
                var paidTuition = true
                if (!edu.hasScholarship && !edu.parentsPaying) {
                    if (cash >= edu.weeklyTuition) {
                        cash -= edu.weeklyTuition
                        reports.add("🎓 Tuition: Paid \$${String.format("%,.0f", edu.weeklyTuition)} for ${edu.universityName}.")
                    } else {
                        paidTuition = false
                        reports.add("⚠️ TUITION DEFAULT: You could not afford weekly tuition (\$${String.format("%,.0f", edu.weeklyTuition)}). Your academic progression stalled this week!")
                    }
                } else if (edu.hasScholarship) {
                    reports.add("🎓 Academic Scholarship covered this week's \$${String.format("%,.0f", edu.weeklyTuition)} tuition in full.")
                } else if (edu.parentsPaying) {
                    reports.add("🎓 Your parents settled this week's \$${String.format("%,.0f", edu.weeklyTuition)} university fees.")
                }

                // CGPA gain
                var newCgpa = edu.cgpa
                if (paidTuition && !isOnBreak) {
                    newCgpa = (edu.cgpa + course.weeklyCgpaGain).coerceAtMost(5.0)
                }

                if (isOnBreak) {
                    reports.add("🏖️ UNIVERSITY BREAK: Classes are on holiday break. Enjoy recharging without school energy drain.")
                }

                // Graduation check
                if (nextCourseWeek >= course.totalWeeks) {
                    edu = edu.copy(
                        currentWeekInCourse = course.totalWeeks,
                        cgpa = newCgpa,
                        isGraduated = true,
                        degreeEarned = "${edu.courseName} Degree"
                    )
                    reports.add("🎓 GRADUATION: Congratulations! You graduated from ${edu.universityName} with a ${edu.courseName} degree! Final CGPA: ${String.format("%.2f", newCgpa)} / 5.0.")
                } else {
                    edu = edu.copy(
                        currentWeekInCourse = nextCourseWeek,
                        cgpa = newCgpa
                    )
                }
            }
        }

        // 3. Regular Job Salary & Energy
        var job = state.currentJob
        if (job != null) {
            cash += job.weeklySalary
            energy = (energy - job.energyCostPercent).coerceAtLeast(0)
            job = job.copy(weeksEmployed = job.weeksEmployed + 1)
            reports.add("💼 Job Salary: Earned \$${String.format("%,.0f", job.weeklySalary)} from ${job.companyName} as ${job.title} (-${job.energyCostPercent}% Energy).")
        }

        // 4. Actor Career & Movie Shoots
        var actor = state.actorCareer
        val updatedContracts = mutableListOf<MovieContract>()
        var totalActingBoxOfficeEarned = 0.0

        actor.activeContracts.forEach { contract ->
            val weeksRemaining = contract.weeksRemaining - 1
            var auraDelta = 0
            if (contract.hasActedThisWeek) {
                // Good performance increases aura
                auraDelta = 1
            } else {
                // Skipping acting duty hurts reputation
                auraDelta = -1
                reports.add("🎬 SHOOT ABSENCE: You failed to attend filming for '${contract.title}' this week! Directors and producers are disappointed (-1 Aura).")
            }

            // Payout calculation based on box office & profit share
            val weeklyBoxOffice = contract.estimatedBoxOfficeWeekly * (0.85 + Random.nextDouble() * 0.3)
            val playerShare = weeklyBoxOffice * (contract.playerProfitSharePercent / 100.0)
            totalActingBoxOfficeEarned += playerShare

            val newActedCount = if (contract.hasActedThisWeek) contract.actedWeeksCount + 1 else contract.actedWeeksCount

            if (weeksRemaining <= 0) {
                reports.add("🎬 MOVIE WRAP: Filming completed for '${contract.title}'! You earned \$${String.format("%,.0f", playerShare)} this week.")
                // Extra aura bonus for fully completed contract
                if (newActedCount >= contract.totalDurationWeeks) {
                    val fullRunBonus = when {
                        contract.totalDurationWeeks >= 7 -> 6
                        contract.totalDurationWeeks >= 4 -> 3
                        else -> 1
                    }
                    actor = actor.copy(
                        aura = (actor.aura + fullRunBonus).coerceIn(0, 100),
                        completedMoviesCount = actor.completedMoviesCount + 1
                    )
                    reports.add("🌟 ACCLAIMED PERFORMANCE: You performed in every single week of '${contract.title}'! Box office praise granted +$fullRunBonus Aura!")
                }
            } else {
                updatedContracts.add(
                    contract.copy(
                        weeksRemaining = weeksRemaining,
                        hasActedThisWeek = false,
                        actedWeeksCount = newActedCount
                    )
                )
            }
            actor = actor.copy(aura = (actor.aura + auraDelta).coerceIn(0, 100))
        }

        if (totalActingBoxOfficeEarned > 0) {
            cash += totalActingBoxOfficeEarned
            reports.add("🎬 Box Office: Earned \$${String.format("%,.0f", totalActingBoxOfficeEarned)} in acting profit royalties.")
        }

        // Refresh available auditions periodically
        val availableAuditions = if (actor.availableAuditions.isEmpty() || Random.nextFloat() < 0.35f) {
            LifeSimData.generateMovieAuditions(actor.tier)
        } else {
            actor.availableAuditions
        }

        // Direct offers based on high aura
        val incomingOffers = if (actor.aura >= 50 && Random.nextFloat() < 0.25f) {
            LifeSimData.generateMovieAuditions(actor.tier).take(1)
        } else {
            actor.incomingDirectOffers
        }

        actor = actor.copy(
            activeContracts = updatedContracts,
            availableAuditions = availableAuditions,
            incomingDirectOffers = incomingOffers,
            totalActingEarnings = actor.totalActingEarnings + totalActingBoxOfficeEarned,
            experienceYears = actor.experienceYears + 0.02
        )

        // 5. Family & NPC Pregnancy Simulation
        val updatedFamily = state.familyMembers.map { member ->
            if (member.isPregnant) {
                val weeksLeft = member.pregnancyWeeksLeft - 1
                if (weeksLeft <= 0) {
                    reports.add("👶 NEW BABY BORN: ${member.firstName} has safely delivered a healthy baby ${member.pregnancyChildGender.lowercase()}! The family is rejoicing.")
                    member.copy(isPregnant = false, pregnancyWeeksLeft = 0)
                } else {
                    member.copy(pregnancyWeeksLeft = weeksLeft)
                }
            } else {
                // Natural spontaneous pregnancy chance (2% chance for female partner/mother)
                if ((member.role == "Mother" || member.role == "Wife" || member.role == "Girlfriend") && member.age in 18..44 && Random.nextFloat() < 0.015f) {
                    val childGender = if (Random.nextBoolean()) "Male" else "Female"
                    reports.add("💖 PREGNANCY ANNOUNCEMENT: ${member.firstName} (${member.role}) announced she is pregnant! The baby is due in 36 weeks.")
                    member.copy(isPregnant = true, pregnancyWeeksLeft = 36, pregnancyChildGender = childGender)
                } else {
                    member
                }
            }
        }.toMutableList()

        // If a pregnancy finished, add the newborn baby to the corresponding family category!
        state.familyMembers.filter { it.isPregnant && it.pregnancyWeeksLeft == 1 }.forEach { mother ->
            val childGender = mother.pregnancyChildGender
            val babyName = if (childGender == "Male") listOf("Liam", "Noah", "Oliver", "James", "Ethan").random() else listOf("Sophia", "Olivia", "Emma", "Ava", "Mia").random()
            val (category, role) = when (mother.role) {
                "Mother" -> Pair("Parents/Siblings", if (childGender == "Male") "Brother" else "Sister")
                "Wife", "Girlfriend" -> Pair("Children", if (childGender == "Male") "Son" else "Daughter")
                else -> Pair("Friends", if (childGender == "Male") "Nephew / Godson" else "Niece / Goddaughter")
            }
            updatedFamily.add(
                FamilyMember(
                    id = "fam_baby_${System.currentTimeMillis()}_${Random.nextInt(999)}",
                    firstName = babyName,
                    lastName = if (category == "Children" || category == "Parents/Siblings") state.playerLastName else mother.lastName,
                    relationshipCategory = category,
                    role = role,
                    age = 0,
                    gender = childGender,
                    relationshipScore = 100,
                    wealth = 0.0,
                    occupation = "Infant"
                )
            )
        }

        return LifeSimTickResult(
            updatedCash = cash,
            updatedEnergy = energy,
            updatedStress = stress,
            updatedAge = playerAge,
            isLivingWithParents = isLivingWithParents,
            isHomeless = isHomeless,
            currentRentalHouseId = currentRentalHouseId,
            updatedEducation = edu,
            updatedCurrentJob = job,
            updatedActorCareer = actor,
            updatedFamilyMembers = updatedFamily,
            reportLines = reports
        )
    }
}
