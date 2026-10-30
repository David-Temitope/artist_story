package com.example.data

data class GenreTrendUpdate(
    val multipliers: Map<String, Float>,
    val activeGenreId: String,
    val weeksRemaining: Int,
    val durationWeeks: Int,
    val rotated: Boolean
)

/** Keeps one market-wide genre trend active for a deliberate, saved multiweek run. */
object GenreTrendSimulator {
    val availableDurations = listOf(3, 4, 6)
    const val activeMultiplier = 1.55f
    const val coolingMultiplier = 0.92f

    fun advance(
        multipliers: Map<String, Float>,
        weeksRemaining: Int,
        durationWeeks: Int,
        genreIds: List<String>,
        nextGenreId: String,
        nextDurationWeeks: Int
    ): GenreTrendUpdate {
        if (genreIds.isEmpty()) return GenreTrendUpdate(multipliers, "", 0, 0, false)

        val current = genreIds.maxByOrNull { multipliers[it] ?: 1f }
            ?.takeIf { (multipliers[it] ?: 1f) > 1.1f }
        if (weeksRemaining > 1 && current != null && durationWeeks > 0) {
            val stable = genreIds.associateWith { id -> if (id == current) activeMultiplier else (multipliers[id] ?: 1f).coerceIn(0.9f, 1.1f) }
            return GenreTrendUpdate(stable, current, weeksRemaining - 1, durationWeeks, false)
        }

        val target = nextGenreId.takeIf { it in genreIds && it != current }
            ?: genreIds.firstOrNull { it != current }
            ?: genreIds.first()
        val duration = nextDurationWeeks.takeIf { it in availableDurations } ?: 4
        val rotated = genreIds.associateWith { id -> when (id) {
            target -> activeMultiplier
            current -> coolingMultiplier
            else -> 1f
        } }
        return GenreTrendUpdate(rotated, target, duration, duration, true)
    }
}
