package com.example.data

import java.util.Locale

internal fun normalizeTourMarket(value: String): String? {
    val parts = value.split(',').map { it.trim() }.filter { it.isNotEmpty() }
    if (parts.size < 2) return null
    return "${parts.first().lowercase(Locale.ROOT)}, ${parts.drop(1).joinToString(", ").lowercase(Locale.ROOT)}"
}

internal fun parseTourMarkets(value: String): List<String> {
    val rawMarkets = value.split(';').map { it.trim() }.filter { it.isNotEmpty() }
    if (rawMarkets.isEmpty()) return emptyList()
    val displayMarkets = rawMarkets.map { raw ->
        val parts = raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.size < 2) return emptyList()
        "${parts.first()}, ${parts.drop(1).joinToString(", ")}"
    }
    return displayMarkets.distinctBy { normalizeTourMarket(it) }
}

internal fun tourMarketsOverlap(first: String, second: String): Boolean {
    val firstMarket = normalizeTourMarket(first) ?: return false
    val secondMarket = normalizeTourMarket(second) ?: return false
    val firstCountry = firstMarket.substringAfterLast(", ")
    val secondCountry = secondMarket.substringAfterLast(", ")
    // Concert demand competes locally in the same city and across the same country.
    return firstMarket == secondMarket || firstCountry == secondCountry
}

internal fun playerTourAudienceShare(playerFans: Long, rivalFans: Long): Float = when {
    playerFans > rivalFans -> 0.70f
    playerFans < rivalFans -> 0.30f
    else -> 0.50f
}

internal fun npcTourMarkets(routeIndex: Int): List<String> {
    val routePool = listOf(
        listOf("Lagos, Nigeria", "Abuja, Nigeria", "Accra, Ghana"),
        listOf("Nairobi, Kenya", "Kampala, Uganda", "Dar es Salaam, Tanzania"),
        listOf("London, United Kingdom", "Manchester, United Kingdom", "Paris, France"),
        listOf("New York, United States", "Atlanta, United States", "Toronto, Canada"),
        listOf("Mumbai, India", "Delhi, India", "Tokyo, Japan"),
        listOf("Johannesburg, South Africa", "Cape Town, South Africa", "Lagos, Nigeria")
    )
    return routePool[Math.floorMod(routeIndex, routePool.size)]
}
