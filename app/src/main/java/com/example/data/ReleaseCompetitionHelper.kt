package com.example.data

/** Deterministic, offline estimate of attention during a shared release week. */
object ReleaseCompetitionHelper {
    fun genreOverlap(playerGenre: String, rivalGenre: String): Double = when {
        playerGenre.equals(rivalGenre, ignoreCase = true) -> 0.80
        playerGenre in listOf("Pop", "Afro", "R&B") && rivalGenre in listOf("Pop", "Afro", "R&B") -> 0.40
        else -> 0.15
    }

    fun demand(fans: Long, rating: Float, hype: Float): Double =
        fans.coerceAtLeast(1L) * (0.25 + rating.coerceIn(1f, 5f) / 5.0 * 0.4 + hype.coerceIn(0f, 100f) / 100.0 * 0.35)

    fun retainedShare(playerDemand: Double, overlappingRivalDemand: Double): Double =
        if (overlappingRivalDemand <= 0.0) 1.0
        else (0.20 + 0.80 * playerDemand / (playerDemand + overlappingRivalDemand)).coerceIn(0.20, 1.0)

    fun campaignHypeLift(campaignPower: Float, weeksPlanned: Int): Float =
        (campaignPower / weeksPlanned.coerceAtLeast(1)).coerceIn(0f, 6f)
}
