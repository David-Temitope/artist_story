package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TourCompetitionTest {
    @Test
    fun parsesOrderedCityCountryMarketsAndRejectsIncompleteOnes() {
        assertEquals(
            listOf("Lagos, Nigeria", "Accra, Ghana"),
            parseTourMarkets(" Lagos, Nigeria ; Accra, Ghana ")
        )
        assertTrue(parseTourMarkets("Lagos; Accra, Ghana").isEmpty())
    }

    @Test
    fun detectsSameCityAndCountryButNotDifferentCountries() {
        assertTrue(tourMarketsOverlap("Lagos, Nigeria", "Lagos, Nigeria"))
        assertTrue(tourMarketsOverlap("Lagos, Nigeria", "Abuja, Nigeria"))
        assertFalse(tourMarketsOverlap("Lagos, Nigeria", "Accra, Ghana"))
    }

    @Test
    fun largerArtistKeepsSeventyPercentOfSharedAudience() {
        assertEquals(0.70f, playerTourAudienceShare(100_000L, 50_000L), 0f)
        assertEquals(0.30f, playerTourAudienceShare(50_000L, 100_000L), 0f)
        assertEquals(0.50f, playerTourAudienceShare(50_000L, 50_000L), 0f)
    }

    @Test
    fun npcTourRoutesAreValidAndCycleThroughCatalog() {
        assertEquals(npcTourMarkets(0), npcTourMarkets(6))
        assertTrue(npcTourMarkets(2).all { normalizeTourMarket(it) != null })
    }
}
