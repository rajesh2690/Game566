package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM studio_save WHERE id = 1 LIMIT 1")
    fun getStudioSave(): Flow<StudioSave?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudioSave(save: StudioSave)

    @Query("SELECT * FROM released_movies ORDER BY id DESC")
    fun getAllReleasedMovies(): Flow<List<ReleasedMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReleasedMovie(movie: ReleasedMovie)

    @Query("DELETE FROM released_movies")
    suspend fun clearReleasedMovies()

    @Query("DELETE FROM studio_save WHERE id = 1")
    suspend fun deleteStudioSave()
}
