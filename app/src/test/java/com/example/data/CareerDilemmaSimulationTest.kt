package com.example.data

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Test

class CareerDilemmaSimulationTest {
    @Test
    fun sameLifeWeekAndSeedProducesSameDilemma() {
        val state = GameStateEntity(email = "local-save", artistName = "Mika")

        val first = CareerDilemmaGenerator.generateDilemmaForState(state, Random(142L))
        val second = CareerDilemmaGenerator.generateDilemmaForState(state, Random(142L))

        assertEquals(first.id, second.id)
        assertEquals(first.headline, second.headline)
    }

    @Test
    fun dilemmaIsBoundToTheCurrentLifeWeek() {
        val weekOne = GameStateEntity(email = "local-save", artistName = "Mika", currentWeek = 1)
        val weekTwo = weekOne.copy(currentWeek = 2)

        val first = CareerDilemmaGenerator.generateDilemmaForState(weekOne, Random(142L))
        val second = CareerDilemmaGenerator.generateDilemmaForState(weekTwo, Random(142L))

        assertEquals(1, first.week)
        assertEquals(2, second.week)
    }
}
