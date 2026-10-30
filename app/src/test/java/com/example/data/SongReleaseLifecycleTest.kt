package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SongReleaseLifecycleTest {
    private fun scheduledSong(weeks: Int) = Song(
        id = "scheduled",
        title = "Release Test",
        genreId = "genre_pop",
        budget = 100.0,
        producerId = null,
        rating = 4.0f,
        weekCreated = 1,
        yearCreated = 1,
        isReleased = false,
        weeksUntilRelease = weeks
    )

    @Test fun countdownReleasesOnTheDueWeekAndDoesNotSkipTheNextWeekOfStreams() {
        val waiting = SongReleaseLifecycle.advance(scheduledSong(2), 2, 1)
        assertFalse(waiting.releasedNow)
        assertEquals(1, waiting.song.weeksUntilRelease)

        val due = SongReleaseLifecycle.advance(waiting.song, 3, 1)
        assertTrue(due.releasedNow)
        assertTrue(due.song.isReleased)
        assertEquals(3, due.song.weekCreated)
        assertEquals(0, due.song.weeksSinceRelease)
    }

    @Test fun scheduledReleaseDateRollsIntoTheNextYear() {
        val due = SongReleaseLifecycle.advance(scheduledSong(1), 1, 2)
        assertEquals(1, due.song.weekCreated)
        assertEquals(2, due.song.yearCreated)
    }
}
