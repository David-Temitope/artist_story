package com.example.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DilemmaChoiceRulesTest {
    private val choice = DilemmaChoice(
        id = "negotiation",
        actionText = "Negotiate the deal",
        shortTitle = "NEGOTIATE",
        teaserConsequence = "A skill check",
        requiredBusinessSkill = 25f,
        requiredIntegrity = 30f
    )

    @Test
    fun choiceRequiresBothBusinessSkillAndIntegrity() {
        assertTrue(choice.isAvailableFor(businessSkill = 25f, integrity = 30f))
        assertFalse(choice.isAvailableFor(businessSkill = 24.9f, integrity = 100f))
        assertFalse(choice.isAvailableFor(businessSkill = 100f, integrity = 29.9f))
    }
}
