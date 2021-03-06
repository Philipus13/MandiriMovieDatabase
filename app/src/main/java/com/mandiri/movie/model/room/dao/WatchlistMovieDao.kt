package com.mandiri.movie.model.room.dao

import androidx.room.*
import com.mandiri.movie.model.data.WatchlistMovie

@Dao
interface WatchlistMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateMovie(watchlistMovie: WatchlistMovie): Long

    @Query("SELECT * FROM watchlist_movie")
    fun getAllMovies(): List<WatchlistMovie>

    @Query("SELECT movieId FROM watchlist_movie")
    suspend fun getAllMovieIds(): List<Int>

    @Query("DELETE FROM watchlist_movie WHERE movieId = :movieId")
    fun deleteMovieById(movieId: Int)
}