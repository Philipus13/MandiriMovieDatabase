package com.mandiri.movie.model.room.dao

import androidx.room.*
import com.mandiri.movie.model.data.Genre

@Dao
interface GenresDao {

    @Transaction
    fun updateGenres(genreList: List<Genre>) {
        deleteAllGenres()
        insertAll(genreList)
    }

    @Insert
    fun insertAll(genreList: List<Genre>)

    @Query("SELECT * FROM genre LIMIT 1")
    suspend fun getAnyGenre(): Genre?

    @Query("SELECT * FROM genre WHERE id IN (:genreIdList)")
    fun getGenresByIdList(genreIdList: List<Int>): List<Genre>

    @Query("DELETE FROM genre")
    fun deleteAllGenres()
}