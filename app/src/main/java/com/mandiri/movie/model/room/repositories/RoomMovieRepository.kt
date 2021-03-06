package com.mandiri.movie.model.room.repositories

import android.app.Application
import android.content.Context
import android.net.Uri
import com.mandiri.movie.model.data.Credits
import com.mandiri.movie.model.data.CustomMovieList
import com.mandiri.movie.model.data.Images
import com.mandiri.movie.model.data.Movie
import com.mandiri.movie.model.remote.repositories.RemoteMovieRepository
import com.mandiri.movie.model.room.databases.CreditsDatabase
import com.mandiri.movie.model.room.databases.ImagesDatabase
import com.mandiri.movie.model.room.databases.MovieDatabase
import com.mandiri.movie.util.deleteFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

class RoomMovieRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val movieListRepository = CustomMovieListRepository(application)

    private val movieDao = MovieDatabase.getInstance(application).movieDao()
    private val imagesDao = ImagesDatabase.getInstance(application).imagesDao()
    private val creditsDao = CreditsDatabase.getInstance(application).creditsDao()

    // -------- INSERTION --------//

    suspend fun insertOrUpdateMovie(
        context: Context,
        movie: Movie,
        customMovieLists: List<CustomMovieList>
    ) {
        for (list in customMovieLists) {
            val movieRoomId = movieDao.insertOrUpdateMovie(movie).toInt()
            insertOrUpdateImages(context, movie, movieRoomId)
            insertOrUpdateCredits(context, movie, movieRoomId)
            movieListRepository.addMovieToMovieList(list, movieRoomId)
        }
    }

    private suspend fun insertOrUpdateImages(context: Context, movie: Movie, movieRoomId: Int) {
        // insert if the images are retrieved not null from remote
        val images = RemoteMovieRepository().getImages(movie.remoteId)
        images.generateFileUris(context)
        images.movieRoomId = movieRoomId
        imagesDao.insertOrUpdateImages(images)
    }

    private suspend fun insertOrUpdateCredits(context: Context, movie: Movie, movieRoomId: Int) {
        // insert if the credits are retrieved not null from remote
        val credits = RemoteMovieRepository().getCredits(movie.remoteId)
        credits.generateFileUris(context)
        credits.movieRoomId = movieRoomId
        creditsDao.insertOrUpdateCredits(credits)
    }

    // -------- GETTERS --------//

    suspend fun getImagesById(movieId: Int) = imagesDao.getImagesById(movieId)

    fun getCreditsFlowById(movieId: Int) = flow {
        emit(creditsDao.getCreditsById(movieId))
    }

    suspend fun getMovieById(movieId: Int) = movieDao.getMovieById(movieId)

    suspend fun getMoviesFromIdList(movieIdList: List<Int>) =
        movieDao.getMoviesFromIdList(movieIdList)


    // -------- DELETION ---------- //

    fun deleteMovieById(movieId: Int) {
        launch { deleteMovieByIdBG(movieId) }
    }

    private suspend fun deleteMovieByIdBG(movieId: Int) {
        withContext(Dispatchers.Default) {
            deleteImageFiles(imagesDao.getImagesById(movieId))
            imagesDao.deleteImagesByMovieId(movieId)
            deleteCreditsFiles(creditsDao.getCreditsById(movieId))
            creditsDao.deleteCreditsByMovieId(movieId)
            deleteMovieFiles(movieDao.getMovieById(movieId))
            movieDao.deleteMovieById(movieId)
        }
    }

    private fun deleteMovieFiles(movie: Movie) {
        movie.posterImageUriString?.let { deleteFile(File(Uri.parse(it).path!!)) }
        movie.backdropImageUriString?.let { deleteFile(File(Uri.parse(it).path!!)) }
    }

    private fun deleteImageFiles(images: Images) {
        images.posterList?.let { posterList ->
            for (poster in posterList)
                poster.imageUriString?.let { deleteFile(File(Uri.parse(it).path!!)) }
        }
        images.backdropList?.let { backdropList ->
            for (backdrop in backdropList)
                backdrop.imageUriString?.let { deleteFile(File(Uri.parse(it).path!!)) }
        }
    }

    private fun deleteCreditsFiles(credits: Credits) {
        credits.castList?.let { castList ->
            for (person in castList)
                person.profileImageUriString?.let { deleteFile(File(Uri.parse(it).path!!)) }
        }
        credits.crewList?.let { crewList ->
            for (person in crewList)
                person.profileImageUriString?.let { deleteFile(File(Uri.parse(it).path!!)) }
        }
    }
}