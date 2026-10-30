package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseCompetitionHelperTest {
    @Test fun sameGenreAudienceOverlapIsHigherThanDifferentGenre() {
        assertEquals(0.80, ReleaseCompetitionHelper.genreOverlap("Pop", "Pop"), 0.0)
        assertEquals(0.40, ReleaseCompetitionHelper.genreOverlap("Pop", "Afro"), 0.0)
        assertEquals(0.15, ReleaseCompetitionHelper.genreOverlap("Rock", "Pop"), 0.0)
    }

    @Test fun largerAudienceAndBetterSongRetainMoreSharedAttention() {
        val rivalDemand = ReleaseCompetitionHelper.demand(100_000, 4.2f, 60f) * 0.80
        val weaker = ReleaseCompetitionHelper.retainedShare(ReleaseCompetitionHelper.demand(10_000, 3.0f, 20f), rivalDemand)
        val stronger = ReleaseCompetitionHelper.retainedShare(ReleaseCompetitionHelper.demand(120_000, 4.8f, 80f), rivalDemand)
        assertTrue(stronger > weaker)
        assertTrue(weaker >= 0.20)
        assertTrue(stronger <= 1.0)
    }

    @Test fun preReleaseCampaignBuildsAwarenessGradually() {
        assertEquals(4f, ReleaseCompetitionHelper.campaignHypeLift(12f, 3), 0.001f)
        assertEquals(6f, ReleaseCompetitionHelper.campaignHypeLift(48f, 2), 0.001f)
    }
}
