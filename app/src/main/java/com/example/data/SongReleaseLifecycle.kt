package com.example.data

data class SongReleaseAdvance(val song: Song, val releasedNow: Boolean)

/** Advances a scheduled song by one in-game week and stamps its actual release date. */
object SongReleaseLifecycle {
    fun advance(song: Song, releaseWeek: Int, releaseYear: Int): SongReleaseAdvance {
        if (song.isReleased) return SongReleaseAdvance(song, false)
        val weeksLeft = (song.weeksUntilRelease - 1).coerceAtLeast(0)
        if (weeksLeft > 0) return SongReleaseAdvance(song.copy(weeksUntilRelease = weeksLeft), false)
        return SongReleaseAdvance(
            song.copy(
                isReleased = true,
                weeksUntilRelease = 0,
                weeksSinceRelease = 0,
                weekCreated = releaseWeek,
                yearCreated = releaseYear
            ),
            true
        )
    }
}
