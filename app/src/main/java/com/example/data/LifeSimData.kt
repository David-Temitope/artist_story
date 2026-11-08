package com.example.data

import kotlin.random.Random

object LifeSimData {

    val PORTRAITS = listOf(
        "cast_street_luxury_1788205929331.jpg",
        "cast_rooftop_night_1788205863239.jpg",
        "cast_club_crowd_1788205897086.jpg",
        "cast_penthouse_sunset_1788205963283.jpg"
    )

    val UNIVERSITIES = listOf(
        UniversityInfo(
            id = "uni_peoples",
            name = "The People's University",
            weeklyFee = 2500.0,
            description = "The premier institution. Guaranteed top-paying job placement with CGPA over 3.5.",
            minCgpaForJobGuarantee = 3.5,
            prestigeLevel = 3
        ),
        UniversityInfo(
            id = "uni_apex",
            name = "Apex University",
            weeklyFee = 1500.0,
            description = "High-prestige academy. Job guaranteed for high achievers with CGPA over 4.0.",
            minCgpaForJobGuarantee = 4.0,
            prestigeLevel = 2
        ),
        UniversityInfo(
            id = "uni_mcfreer",
            name = "McFreer University",
            weeklyFee = 1000.0,
            description = "Accessible college. Offers certificates, but corporate employers are hesitant.",
            minCgpaForJobGuarantee = 4.8,
            prestigeLevel = 1
        )
    )

    val COURSES = listOf(
        UniversityCourse(
            id = "course_theater",
            name = "Theater Arts",
            totalWeeks = 52,
            weeklyCgpaGain = 0.09,
            break1Start = 17,
            break1End = 20,
            break2Start = 37,
            break2End = 45
        ),
        UniversityCourse(
            id = "course_medicine",
            name = "Medicine",
            totalWeeks = 94,
            weeklyCgpaGain = 0.05,
            break1Start = 20,
            break1End = 26,
            break2Start = 49,
            break2End = 55
        ),
        UniversityCourse(
            id = "course_law",
            name = "Law",
            totalWeeks = 104,
            weeklyCgpaGain = 0.045,
            break1Start = 24,
            break1End = 40,
            break2Start = 67,
            break2End = 85
        ),
        UniversityCourse(
            id = "course_engineering",
            name = "Engineering",
            totalWeeks = 124,
            weeklyCgpaGain = 0.04,
            break1Start = 25,
            break1End = 45,
            break2Start = 74,
            break2End = 100
        ),
        UniversityCourse(
            id = "course_cs",
            name = "Computer Science",
            totalWeeks = 73,
            weeklyCgpaGain = 0.06,
            break1Start = 20,
            break1End = 30,
            break2Start = 46,
            break2End = 56
        )
    )

    val HOUSES = listOf(
        HouseProperty(
            id = "house_studio_apt",
            name = "Cozy Studio Apartment",
            description = "Affordable downtown studio. Perfect for starting out away from home.",
            type = "Apartment",
            buyPrice = 75000.0,
            weeklyRent = 250.0,
            weeklyExpenses = 50.0,
            energyBonus = 5
        ),
        HouseProperty(
            id = "house_suburban",
            name = "Suburban 2-Bedroom Home",
            description = "Quiet neighborhood with backyard, garage, and comfortable living room.",
            type = "Suburban House",
            buyPrice = 280000.0,
            weeklyRent = 700.0,
            weeklyExpenses = 150.0,
            energyBonus = 12
        ),
        HouseProperty(
            id = "house_luxury_condo",
            name = "Skyline Luxury Condo",
            description = "Floor-to-ceiling glass windows overlooking downtown, 24/7 concierge, gym & pool.",
            type = "Luxury Condo",
            buyPrice = 650000.0,
            weeklyRent = 1600.0,
            weeklyExpenses = 350.0,
            energyBonus = 20
        ),
        HouseProperty(
            id = "house_mansion",
            name = "Hilltop Estate Mansion",
            description = "Private gated villa with infinity pool, private theater, tennis court, and security gate.",
            type = "Mansion",
            buyPrice = 2200000.0,
            weeklyRent = 5000.0,
            weeklyExpenses = 1100.0,
            energyBonus = 35
        ),
        HouseProperty(
            id = "house_penthouse",
            name = "Centennial Ultra Penthouse",
            description = "The pinnacle of elite luxury living with private helipad, rooftop lounge, and butler suite.",
            type = "Penthouse",
            buyPrice = 6500000.0,
            weeklyRent = 14000.0,
            weeklyExpenses = 2800.0,
            energyBonus = 50
        )
    )

    val JOB_POSTINGS = listOf(
        // Computer Science
        JobPosting("job_cs_intern", "IT Intern", "Computer Science", "NextWave Tech", "Startup", 2.5, 3000.0, 5000.0),
        JobPosting("job_cs_frontend", "Frontend Developer", "Computer Science", "HyperScale UI", "Mid-Size Corp", 3.0, 5000.0, 10000.0),
        JobPosting("job_cs_backend", "Backend Engineer", "Computer Science", "CloudVault Systems", "Global Enterprise", 3.2, 7000.0, 15000.0),
        JobPosting("job_cs_security", "Ethical Hacker / SecOps", "Computer Science", "Aegis Cyber Defence", "Elite Conglomerate", 3.6, 10000.0, 18000.0),

        // Law
        JobPosting("job_law_jury", "Jury Legal Analyst", "Law", "Metropolitan Court House", "Public Service", 2.8, 10000.0, 20000.0),
        JobPosting("job_law_private", "Private Lawyer", "Law", "Sterling & Partners LLP", "Mid-Size Corp", 3.2, 15000.0, 25000.0),
        JobPosting("job_law_judge", "Circuit Court Judge", "Law", "Supreme District Bench", "Global Enterprise", 3.8, 20000.0, 35000.0),
        JobPosting("job_law_da", "District Attorney (DA)", "Law", "Department of Justice", "Elite Conglomerate", 4.2, 30000.0, 50000.0),

        // Engineering
        JobPosting("job_eng_mech", "Mechanical Engineer", "Engineering", "Titan Dynamics", "Mid-Size Corp", 3.0, 18000.0, 30000.0),
        JobPosting("job_eng_civil", "Civil Project Engineer", "Engineering", "Vanguard Infra Builders", "Global Enterprise", 3.2, 20000.0, 30000.0),
        JobPosting("job_eng_petro", "Petroleum Reservoir Engineer", "Engineering", "OmniPetro Energy", "Elite Conglomerate", 3.5, 25000.0, 30000.0),

        // Medicine
        JobPosting("job_med_lab", "Lab Assistant", "Medicine", "BioGen Diagnostics", "Mid-Size Corp", 2.8, 7000.0, 12000.0),
        JobPosting("job_med_consult", "Medical Consultant", "Medicine", "St. Jude Premier Clinic", "Global Enterprise", 3.4, 15000.0, 20000.0),
        JobPosting("job_med_surgeon", "Lead Neurosurgeon", "Medicine", "Central Memorial Hospital", "Elite Conglomerate", 4.0, 25000.0, 30000.0)
    )

    fun createInitialFamily(lastName: String, random: Random = Random(System.currentTimeMillis())): List<FamilyMember> {
        return listOf(
            FamilyMember(
                id = "fam_mother",
                firstName = "Elena",
                lastName = lastName,
                relationshipCategory = "Parents/Siblings",
                role = "Mother",
                age = 44,
                gender = "Female",
                relationshipScore = 95,
                wealth = 35000.0,
                occupation = "High School Teacher"
            ),
            FamilyMember(
                id = "fam_father",
                firstName = "Marcus",
                lastName = lastName,
                relationshipCategory = "Parents/Siblings",
                role = "Father",
                age = 47,
                gender = "Male",
                relationshipScore = 90,
                wealth = 42000.0,
                occupation = "Architect"
            ),
            FamilyMember(
                id = "fam_sibling",
                firstName = if (random.nextBoolean()) "Lucas" else "Maya",
                lastName = lastName,
                relationshipCategory = "Parents/Siblings",
                role = if (random.nextBoolean()) "Brother" else "Sister",
                age = 15,
                gender = if (random.nextBoolean()) "Male" else "Female",
                relationshipScore = 80,
                wealth = 500.0,
                occupation = "Student"
            ),
            FamilyMember(
                id = "fam_friend_1",
                firstName = "Jordan",
                lastName = "Cole",
                relationshipCategory = "Friends",
                role = "Best Friend",
                age = 18,
                gender = "Male",
                relationshipScore = 88,
                wealth = 2000.0,
                occupation = "Barista & Dreamer"
            )
        )
    }

    fun generateMovieAuditions(tier: String, random: Random = Random(System.currentTimeMillis())): List<MovieContract> {
        val titles = listOf(
            "Midnight In Brooklyn", "Neon Shadow", "The Silent Witness", "Iron Heart",
            "Love Across Borders", "Quantum Heist", "The Last Empire", "Echoes of Fame"
        )
        val studios = listOf("Paramount Global", "Warner Studios", "Sony Pictures", "Universal Media", "A24 Indie Films")
        val genres = listOf("Drama", "Action", "Thriller", "Sci-Fi", "Comedy")

        val (roleType, durationRange, profitShareRange, energyCost) = when (tier) {
            "A-List" -> Quadruple("Lead / Big Role", 7..12, 18.0..25.0, 15)
            "B-List" -> Quadruple("Supporting Role", 4..8, 12.0..18.0, 10)
            "C-List" -> Quadruple("Minor Role / Promo", 2..5, 6.0..12.0, 7)
            else -> Quadruple("Extra / Small Role", 1..2, 2.0..5.0, 5)
        }

        return (1..3).map { idx ->
            val duration = durationRange.random(random)
            val share = String.format("%.1f", profitShareRange.start + random.nextDouble() * (profitShareRange.endInclusive - profitShareRange.start)).toDouble()
            val boxOffice = when (tier) {
                "A-List" -> 150000.0 + random.nextDouble() * 250000.0
                "B-List" -> 80000.0 + random.nextDouble() * 120000.0
                "C-List" -> 35000.0 + random.nextDouble() * 55000.0
                else -> 10000.0 + random.nextDouble() * 20000.0
            }
            MovieContract(
                id = "contract_${System.currentTimeMillis()}_$idx",
                title = titles.random(random),
                genre = genres.random(random),
                studioCompany = studios.random(random),
                roleType = roleType,
                totalDurationWeeks = duration,
                weeksRemaining = duration,
                playerProfitSharePercent = share,
                estimatedBoxOfficeWeekly = boxOffice,
                requiredEnergyPercent = energyCost,
                requiredTimeSlots = 3
            )
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
