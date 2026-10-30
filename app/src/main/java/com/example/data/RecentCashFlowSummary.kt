package com.example.data

data class RecentCashFlowSummary(
    val average: Double,
    val sampleCount: Int
)

/** Summarizes recorded weekly net cash movement without inventing a future payout. */
object RecentCashFlowSummaryCalculator {
    fun summarize(history: List<String>, windowSize: Int = 4): RecentCashFlowSummary {
        val window = history.takeLast(windowSize.coerceAtLeast(1))
        val validSamples = window.mapNotNull { it.toDoubleOrNull()?.takeIf { amount -> amount.isFinite() } }
        val average = validSamples.average().takeIf { it.isFinite() } ?: 0.0
        return RecentCashFlowSummary(average = average, sampleCount = validSamples.size)
    }
}
