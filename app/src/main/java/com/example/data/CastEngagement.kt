package com.example.data

internal data class CastPostEngagement(
    val likes: Long,
    val reposts: Long,
    val comments: Long
)

internal data class SocialManagerPostCopy(val content: String, val isSponsored: Boolean)

/** Returns copy only when the manager is configured to post. */
internal fun socialManagerPostCopy(
    artistName: String,
    category: String,
    autoPostEnabled: Boolean,
    sponsorDue: String?
): SocialManagerPostCopy? {
    if (!autoPostEnabled) return null
    val regularCopy = "Working on some incredible ${category.lowercase()} content. Stay tuned!\n#${artistName.replace(" ", "")} #${category.replace(" ", "")}"
    val sponsor = sponsorDue?.takeIf { it.isNotBlank() }
        ?: return SocialManagerPostCopy(regularCopy, isSponsored = false)
    return SocialManagerPostCopy(
        content = "$regularCopy\nPaid partnership with $sponsor. #ad",
        isSponsored = true
    )
}

/** Creates bounded, audience-scaled engagement for a Cast post. */
internal fun castPostEngagement(fans: Long, popularity: Float, variation: Float): CastPostEngagement {
    if (fans <= 0L) return CastPostEngagement(likes = 0L, reposts = 0L, comments = 0L)
    val safePopularity = popularity.coerceIn(0f, 100f)
    val safeVariation = variation.coerceIn(0f, 1f)
    val reachRate = 0.01f + (safePopularity / 100f) * 0.06f
    val variationMultiplier = 0.75f + safeVariation * 0.5f
    val likes = (fans * reachRate * variationMultiplier).toLong().coerceAtLeast(1L)
    val repostRate = 0.03f + (safePopularity / 100f) * 0.07f
    val commentRate = 0.01f + (safePopularity / 100f) * 0.03f
    return CastPostEngagement(
        likes = likes,
        reposts = (likes * repostRate).toLong(),
        comments = (likes * commentRate).toLong()
    )
}

/** Returns this post's remaining weekly audience response at its current age. */
internal fun castEngagementMultiplier(ageInWeeks: Int): Float = when {
    ageInWeeks <= 0 -> 0f
    ageInWeeks > 8 -> 0f
    else -> 0.18f * Math.pow(0.45, (ageInWeeks - 1).toDouble()).toFloat()
}
