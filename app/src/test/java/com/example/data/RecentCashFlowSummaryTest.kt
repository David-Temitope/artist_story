package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecentCashFlowSummaryTest {
    @Test
    fun `uses last four actual weekly cash movements including losses`() {
        val summary = RecentCashFlowSummaryCalculator.summarize(
            listOf("100", "-40", "80", "-20", "60")
        )

        assertEquals(20.0, summary.average, 0.001)
        assertEquals(4, summary.sampleCount)
    }

    @Test
    fun `ignores malformed and non-finite history entries`() {
        val summary = RecentCashFlowSummaryCalculator.summarize(
            listOf("bad", "NaN", "Infinity", "-25", "75"),
            windowSize = 5
        )

        assertEquals(25.0, summary.average, 0.001)
        assertEquals(2, summary.sampleCount)
    }

    @Test
    fun `empty history has zero baseline and no samples`() {
        val summary = RecentCashFlowSummaryCalculator.summarize(emptyList())

        assertEquals(0.0, summary.average, 0.0)
        assertEquals(0, summary.sampleCount)
        assertTrue(summary.average.isFinite())
    }
}
