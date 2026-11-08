package com.example.data

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class LifeSimFeaturesTest {

    @Test
    fun testEvictionAtAge22LeadsToHomelessness() {
        val state = GameStateEntity(
            email = "test@genesis.com",
            artistName = "Alex Vance",
            playerAge = 21,
            isLivingWithParents = true,
            isHomeless = false,
            currentRentalHouseId = null,
            ownedHouses = emptyList()
        )

        // Advance into new year (age 21 -> 22)
        val tick = LifeSimTickHelper.processWeeklyLifeSim(
            state = state,
            initialCash = 10000.0,
            initialEnergy = 100,
            initialStress = 20f,
            isNewYear = true
        )

        assertEquals(22, tick.updatedAge)
        assertFalse("Player must no longer live with parents after turning 22", tick.isLivingWithParents)
        assertTrue("Player without house at 22 must be marked homeless", tick.isHomeless)
        assertTrue(tick.reportLines.any { it.contains("EVICTED FROM HOME") })
    }

    @Test
    fun testSchoolEnergyAndTuitionDuringTermAndBreaks() {
        // Enrolled in Theater Arts (52 weeks, break1: 17..20)
        val eduActive = PlayerEducation(
            isEnrolled = true,
            universityName = "The People's University",
            courseName = "Theater Arts",
            currentWeekInCourse = 5,
            totalCourseWeeks = 52,
            cgpa = 0.5,
            weeklyTuition = 2500.0
        )

        val stateActive = GameStateEntity(
            email = "test@genesis.com",
            artistName = "Alex Vance",
            education = eduActive
        )

        // Normal active school week
        val tickActive = LifeSimTickHelper.processWeeklyLifeSim(
            state = stateActive,
            initialCash = 10000.0,
            initialEnergy = 100,
            initialStress = 20f,
            isNewYear = false
        )

        assertEquals("School deducts 10% energy on active weeks", 90, tickActive.updatedEnergy)
        assertEquals("Tuition is deducted from cash", 7500.0, tickActive.updatedCash, 0.01)
        assertTrue("CGPA increases when tuition is paid on active week", tickActive.updatedEducation.cgpa > 0.5)

        // On holiday break (Week 18)
        val eduBreak = eduActive.copy(currentWeekInCourse = 17)
        val stateBreak = stateActive.copy(education = eduBreak)
        val tickBreak = LifeSimTickHelper.processWeeklyLifeSim(
            state = stateBreak,
            initialCash = 10000.0,
            initialEnergy = 100,
            initialStress = 20f,
            isNewYear = false
        )

        assertEquals("School does not deduct energy during break", 100, tickBreak.updatedEnergy)
        assertTrue(tickBreak.reportLines.any { it.contains("UNIVERSITY BREAK") })
    }

    @Test
    fun testPregnancyDeliveryAddsNewbornToFamily() {
        val mother = FamilyMember(
            id = "fam_mother",
            firstName = "Elena",
            lastName = "Vance",
            relationshipCategory = "Parents/Siblings",
            role = "Mother",
            age = 44,
            gender = "Female",
            isPregnant = true,
            pregnancyWeeksLeft = 1,
            pregnancyChildGender = "Female"
        )

        val state = GameStateEntity(
            email = "test@genesis.com",
            artistName = "Alex Vance",
            playerLastName = "Vance",
            familyMembers = listOf(mother)
        )

        val tick = LifeSimTickHelper.processWeeklyLifeSim(
            state = state,
            initialCash = 10000.0,
            initialEnergy = 100,
            initialStress = 20f,
            isNewYear = false
        )

        val updatedMother = tick.updatedFamilyMembers.find { it.id == "fam_mother" }
        assertNotNull(updatedMother)
        assertFalse("Mother should no longer be pregnant after delivery", updatedMother!!.isPregnant)

        val newborn = tick.updatedFamilyMembers.find { it.role == "Sister" && it.age == 0 }
        assertNotNull("Delivered baby girl should be added to Parents/Siblings as Sister", newborn)
        assertEquals("Vance", newborn!!.lastName)
    }

    @Test
    fun testActorShootContractAndWeeklyActAction() {
        val contract = MovieContract(
            id = "contract_1",
            title = "Midnight Thriller",
            genre = "Action",
            studioCompany = "Paramount Global",
            roleType = "Medium Role",
            totalDurationWeeks = 3,
            weeksRemaining = 3,
            playerProfitSharePercent = 10.0,
            estimatedBoxOfficeWeekly = 50000.0,
            requiredEnergyPercent = 7,
            hasActedThisWeek = true
        )

        val actorState = ActorCareerState(
            tier = "C-List",
            aura = 20,
            activeContracts = listOf(contract)
        )

        val state = GameStateEntity(
            email = "test@genesis.com",
            artistName = "Alex Vance",
            actorCareer = actorState
        )

        val tick = LifeSimTickHelper.processWeeklyLifeSim(
            state = state,
            initialCash = 10000.0,
            initialEnergy = 100,
            initialStress = 20f,
            isNewYear = false
        )

        assertTrue("Acting profit share increases cash balance", tick.updatedCash > 10000.0)
        assertTrue("Acting scenes increases actor aura", tick.updatedActorCareer.aura > 20)
        assertEquals(2, tick.updatedActorCareer.activeContracts.first().weeksRemaining)
    }
}
