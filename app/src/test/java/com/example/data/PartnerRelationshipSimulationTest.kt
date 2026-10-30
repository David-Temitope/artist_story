package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PartnerRelationshipSimulationTest {
    private fun partner(
        tag: String = "ART",
        score: Int = 80,
        lastWeek: Int = 0,
        lastYear: Int = 0,
        gender: String = "Female"
    ) = CapitalClubMember(
        id = "partner",
        name = "Alex",
        age = 28,
        gender = gender,
        title = "Designer",
        matchPct = "90%",
        tag = tag,
        imageUrl = "",
        quote = "",
        tags = emptyList(),
        synergyText = "",
        isDating = true,
        relationshipScore = score,
        lastInteractionWeek = lastWeek,
        lastInteractionYear = lastYear
    )

    @Test
    fun meaningfulRecentContactPreventsNeglectDecay() {
        val result = PartnerRelationshipSimulation.advanceWeek(
            partner(lastWeek = 1, lastYear = 1),
            week = 2,
            year = 1
        )

        assertEquals(80, result.member.relationshipScore)
        assertFalse(result.crossedNeglectThreshold)
    }

    @Test
    fun repeatedChatInOneWeekCannotFarmPositiveRelationshipGains() {
        assertEquals(
            0f,
            PartnerRelationshipSimulation.relationshipDeltaForWeek(5f, 2, 1, 2, 1)
        )
        assertEquals(
            -4f,
            PartnerRelationshipSimulation.relationshipDeltaForWeek(-4f, 2, 1, 2, 1)
        )
        assertEquals(
            5f,
            PartnerRelationshipSimulation.relationshipDeltaForWeek(5f, 1, 1, 2, 1)
        )
    }

    @Test
    fun firstConversationCostsEnergyOnlyOncePerGameWeek() {
        assertEquals(1, PartnerRelationshipSimulation.energyCostForFirstContactThisWeek(0, 0, 1, 1))
        assertEquals(0, PartnerRelationshipSimulation.energyCostForFirstContactThisWeek(1, 1, 1, 1))
        assertEquals(0, PartnerRelationshipSimulation.energyCostForFirstContactThisWeek(2, 1, 2, 1))
        assertEquals(1, PartnerRelationshipSimulation.energyCostForFirstContactThisWeek(1, 1, 2, 1))
    }

    @Test
    fun neglectedRelationshipsUseOneConsistentDecayRule() {
        val result = PartnerRelationshipSimulation.advanceWeek(partner(tag = "WEALTH"), week = 2, year = 1)

        assertEquals(77, result.member.relationshipScore)
    }

    @Test
    fun severeNeglectEndsTheRelationshipAndReportsTheSameOutcome() {
        val result = PartnerRelationshipSimulation.advanceWeek(partner(score = 6), week = 2, year = 1)

        assertEquals(0, result.member.relationshipScore)
        assertFalse(result.member.isDating)
        assertTrue(result.member.isLockedOut)
        assertTrue(result.brokeUp)
    }

    @Test
    fun financialHelpDependsOnTrustAndCircumstancesRatherThanGender() {
        val wealthyWoman = PartnerRelationshipSimulation.evaluateGiftRequest(
            partner(tag = "WEALTH", score = 80, gender = "Female")
        )
        val wealthyMan = PartnerRelationshipSimulation.evaluateGiftRequest(
            partner(tag = "WEALTH", score = 80, gender = "Male")
        )
        val newPartner = PartnerRelationshipSimulation.evaluateGiftRequest(partner(score = 40))

        assertEquals(wealthyWoman, wealthyMan)
        assertEquals(2_000.0, wealthyWoman.cashReceived, 0.0)
        assertTrue(newPartner.cashReceived == 0.0)
        assertTrue(newPartner.relationshipChange < wealthyWoman.relationshipChange)
    }
}
