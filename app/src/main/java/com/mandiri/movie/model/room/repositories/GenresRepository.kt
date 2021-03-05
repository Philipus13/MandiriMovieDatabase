package com.mandiri.movie.model.room.repositories

import android.content.Context
import com.mandiri.movie.model.data.Genre
import com.mandiri.movie.model.room.databases.GenresDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class GenresRepository(context: Context) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val genreDao = GenresDatabase.getInstance(context).genresDao()

    fun updateGenres(genreList: List<Genre>) {
        genreDao.updateGenres(genreList)
    }

    suspend fun getAnyGenre() = genreDao.getAnyGenre()

    fun getGenresByIdList(genreIdList: List<Int>) = genreDao.getGenresByIdList(genreIdList)
}