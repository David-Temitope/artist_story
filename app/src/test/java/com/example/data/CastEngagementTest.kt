package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CastEngagementTest {
    @Test
    fun engagementStartsAtOneEighteenPercentAndDeclinesEachWeek() {
        assertEquals(0.18f, castEngagementMultiplier(1), 0.0001f)
        assertTrue(castEngagementMultiplier(2) < castEngagementMultiplier(1))
        assertTrue(castEngagementMultiplier(8) > 0f)
    }

    @Test
    fun engagementStopsAfterTheFirstEightWeeks() {
        assertEquals(0f, castEngagementMultiplier(0), 0f)
        assertEquals(0f, castEngagementMultiplier(9), 0f)
        assertEquals(0f, castEngagementMultiplier(52), 0f)
    }

    @Test
    fun npcPostEngagementScalesWithFansAndPopularity() {
        val smallAccount = castPostEngagement(fans = 100L, popularity = 20f, variation = 0.5f)
        val largeAccount = castPostEngagement(fans = 100_000L, popularity = 80f, variation = 0.5f)

        assertTrue(smallAccount.likes in 1L..100L)
        assertTrue(largeAccount.likes > smallAccount.likes)
        assertTrue(largeAccount.reposts < largeAccount.likes)
        assertTrue(largeAccount.comments < largeAccount.likes)
    }

    @Test
    fun npcWithoutFansGetsNoEngagementAndInputsAreBounded() {
        assertEquals(CastPostEngagement(0L, 0L, 0L), castPostEngagement(0L, 50f, 0.5f))
        val result = castPostEngagement(fans = 1L, popularity = 500f, variation = -4f)
        assertTrue(result.likes >= 1L)
        assertTrue(result.reposts <= result.likes)
        assertTrue(result.comments <= result.likes)
    }

    @Test
    fun managerOnlyCompletesSponsorWithAnEnabledTaggedSponsoredPost() {
        assertEquals(null, socialManagerPostCopy("Nova", "Music", false, "Aurora Audio"))

        val post = socialManagerPostCopy("Nova Ray", "Music", true, "Aurora Audio")!!
        assertTrue(post.isSponsored)
        assertTrue(post.content.contains("Aurora Audio"))
        assertTrue(post.content.contains("#ad"))

        val regularPost = socialManagerPostCopy("Nova Ray", "Music", true, null)!!
        assertEquals(false, regularPost.isSponsored)
        assertTrue(!regularPost.content.contains("#ad"))
    }
}
