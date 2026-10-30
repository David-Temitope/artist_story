package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GenreTrendSimulatorTest {
    private val genres = listOf("pop", "rock", "afro", "jazz")

    @Test fun eachSupportedTrendDurationCountsDownAndThenRotates() {
        for (duration in GenreTrendSimulator.availableDurations) {
            var update = GenreTrendSimulator.advance(emptyMap(), 0, 0, genres, "pop", duration)
            assertTrue(update.rotated)
            assertEquals(duration, update.weeksRemaining)
            repeat(duration - 1) {
                update = GenreTrendSimulator.advance(update.multipliers, update.weeksRemaining, update.durationWeeks, genres, "rock", 3)
                assertFalse(update.rotated)
                assertEquals("pop", update.activeGenreId)
            }
            update = GenreTrendSimulator.advance(update.multipliers, update.weeksRemaining, update.durationWeeks, genres, "rock", 3)
            assertTrue(update.rotated)
            assertEquals("rock", update.activeGenreId)
            assertEquals(3, update.weeksRemaining)
        }
    }

    @Test fun onlyTheActiveGenreGetsTheFullDemandMultiplier() {
        val update = GenreTrendSimulator.advance(emptyMap(), 0, 0, genres, "afro", 6)
        assertEquals(1.55f, update.multipliers.getValue("afro"), 0.001f)
        assertEquals(0.92f, update.multipliers.getValue("pop"), 0.001f)
        assertEquals(1f, update.multipliers.getValue("rock"), 0.001f)
    }
}
