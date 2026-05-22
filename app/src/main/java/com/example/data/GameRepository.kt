package com.example.data

import kotlinx.coroutines.flow.Flow

class GameRepository(private val gameDao: GameDao) {
    val studioSave: Flow<StudioSave?> = gameDao.getStudioSave()
    val releasedMovies: Flow<List<ReleasedMovie>> = gameDao.getAllReleasedMovies()

    suspend fun saveStudioState(save: StudioSave) {
        gameDao.insertStudioSave(save)
    }

    suspend fun saveReleasedMovie(movie: ReleasedMovie) {
        gameDao.insertReleasedMovie(movie)
    }

    suspend fun restartGame(startingName: String) {
        // Clear old records optionally or reset save
        val defaultSave = StudioSave(
            id = 1,
            name = startingName,
            money = 10_000_000.0, // starting with $10 Million!
            fans = 12_000,        // starting with some base local enthusiasts
            reputation = 0.5f,    // average reputation
            completedMoviesCount = 0,
            totalEarnings = 0.0,
            timelineWeek = 1,
            currentStage = ActiveProjectStage.NONE
        )
        gameDao.insertStudioSave(defaultSave)
        // Keep or clear history? Keep achievements, but let's clear movie history so they get a fresh cinematic calendar.
        gameDao.clearReleasedMovies()
    }
}
