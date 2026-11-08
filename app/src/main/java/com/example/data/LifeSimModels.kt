package com.example.data

import com.squareup.moshi.JsonClass

/**
 * Life Simulation Models for Genesis:
 * - Character Details (First/Last name, Gender, Country, Avatar)
 * - Family & Relationships (Mother, Father, Siblings, Spouse/Partner, Children, Friends)
 * - Pregnancy Simulation (36 weeks)
 * - Housing (Rent/Buy, Expenses, Taxes, Homelessness)
 * - University & Courses (Tuition, Breaks, CGPA, Scholarships, Parents pay)
 * - Job Market (CS, Law, Engineering, Medicine with company tiers & grade requirements)
 * - Actor Career (Tiers D to A, Auditions, Aura 0-100, Contracts, Weekly Act mechanics)
 */

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class FamilyMember(
    val id: String,
    val firstName: String,
    val lastName: String,
    val relationshipCategory: String, // "Parents/Siblings", "Spouse", "Children", "Friends"
    val role: String, // "Mother", "Father", "Brother", "Sister", "Wife", "Girlfriend", "Husband", "Boyfriend", "Son", "Daughter", "Best Friend", "Friend"
    val age: Int,
    val gender: String, // "Female", "Male"
    val relationshipScore: Int = 85, // 0 to 100
    val wealth: Double = 25000.0,
    val isPregnant: Boolean = false,
    val pregnancyWeeksLeft: Int = 0,
    val pregnancyChildGender: String = "Female",
    val occupation: String = "Employed",
    val avatarRes: String = ""
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class HouseProperty(
    val id: String,
    val name: String,
    val description: String,
    val type: String, // "Apartment", "Townhouse", "Suburban House", "Luxury Condo", "Mansion", "Penthouse"
    val buyPrice: Double,
    val weeklyRent: Double,
    val weeklyExpenses: Double, // upkeep and property tax
    val energyBonus: Int,
    val isRented: Boolean = false,
    val isOwned: Boolean = false,
    val isPrimaryResidence: Boolean = false
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class UniversityCourse(
    val id: String,
    val name: String, // "Theater Arts", "Medicine", "Law", "Engineering", "Computer Science"
    val totalWeeks: Int,
    val weeklyCgpaGain: Double, // 0.09, 0.05, 0.045, 0.04, 0.06
    val break1Start: Int,
    val break1End: Int,
    val break2Start: Int,
    val break2End: Int
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class UniversityInfo(
    val id: String,
    val name: String,
    val weeklyFee: Double,
    val description: String,
    val minCgpaForJobGuarantee: Double,
    val prestigeLevel: Int // 1 (McFreer), 2 (Apex), 3 (The People's University)
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class PlayerEducation(
    val isEnrolled: Boolean = false,
    val universityId: String = "",
    val universityName: String = "",
    val courseName: String = "",
    val currentWeekInCourse: Int = 0,
    val totalCourseWeeks: Int = 0,
    val cgpa: Double = 0.0, // max 5.0
    val isGraduated: Boolean = false,
    val hasScholarship: Boolean = false,
    val parentsPaying: Boolean = false,
    val weeklyTuition: Double = 0.0,
    val degreeEarned: String? = null
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class JobPosting(
    val id: String,
    val title: String,
    val field: String, // "Computer Science", "Law", "Engineering", "Medicine"
    val companyName: String,
    val companyTier: String, // "Startup", "Mid-Size Corp", "Global Enterprise", "Elite Conglomerate"
    val minCgpaRequired: Double,
    val weeklySalaryMin: Double,
    val weeklySalaryMax: Double,
    val energyCostPercent: Int = 20
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class PlayerJob(
    val id: String,
    val title: String,
    val field: String,
    val companyName: String,
    val weeklySalary: Double,
    val energyCostPercent: Int = 20,
    val weeksEmployed: Int = 0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class MovieContract(
    val id: String,
    val title: String,
    val genre: String, // "Action", "Drama", "Sci-Fi", "Comedy", "Thriller"
    val studioCompany: String,
    val roleType: String, // "Small Role", "Medium Role", "Lead / Big Role"
    val totalDurationWeeks: Int,
    val weeksRemaining: Int,
    val playerProfitSharePercent: Double, // e.g. 5.0 to 25.0%
    val estimatedBoxOfficeWeekly: Double,
    val requiredEnergyPercent: Int,
    val requiredTimeSlots: Int = 3,
    val hasActedThisWeek: Boolean = false,
    val actedWeeksCount: Int = 0
)

@JsonClass(generateAdapter = true)
@androidx.annotation.Keep
data class ActorCareerState(
    val tier: String = "D-List", // "D-List", "C-List", "B-List", "A-List"
    val aura: Int = 0, // 0 to 100
    val experienceYears: Double = 0.0,
    val completedMoviesCount: Int = 0,
    val totalActingEarnings: Double = 0.0,
    val activeContracts: List<MovieContract> = emptyList(),
    val availableAuditions: List<MovieContract> = emptyList(),
    val incomingDirectOffers: List<MovieContract> = emptyList()
)
