package com.mandiri.movie.ui.details.overview

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.mandiri.movie.MovieDetailsArgs
import com.mandiri.movie.R
import com.mandiri.movie.model.data.Movie
import com.mandiri.movie.model.data.Review
import com.mandiri.movie.model.data.ReviewList
import com.mandiri.movie.model.remote.repositories.RemoteMovieRepository
import com.mandiri.movie.model.room.repositories.RoomMovieRepository
import com.mandiri.movie.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OverviewViewModel(application: Application, arguments: Bundle?) :
    AndroidViewModel(application) {

    private var _currentMovie = MutableLiveData<Movie>()
    private var _currentReview = MutableLiveData<ReviewList>()

    val currentMovie: LiveData<Movie>
        get() = _currentMovie
    val currentReview: LiveData<ReviewList>
        get() = _currentReview

    private var args = arguments?.let { MovieDetailsArgs.fromBundle(it) }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            args?.let {
                if (it.movieLocalId == 0)
                    getMovieDetailsRemote(it.movieRemoteId)
                else getMovieDetailsLocal(it.movieLocalId)
            }
        }
    }

    private fun getMovieDetailsRemote(movieId: Int) {
        viewModelScope.launch {
            if (isNetworkAvailable(getApplication())) {
                val movie = RemoteMovieRepository().getMovieDetails(movieId)
                val review = RemoteMovieRepository().getReviews(movieId)


                _currentReview.value = review



                movie.genresString = stringListToString(getAnyNameList(movie.genres))
                movie.productionCountryString =
                    stringListToListedString(getAnyNameList(movie.productionCountryList))
                _currentMovie.value = movie
            } else {
                networkUnavailableNotification(getApplication())
            }
        }
    }


    private fun getMovieDetailsLocal(movieId: Int) {
        viewModelScope.launch {
            _currentMovie.value = RoomMovieRepository(getApplication()).getMovieById(movieId)
        }
    }

    fun onNavigationItemSelected(view: View, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.media_menu_item -> {
                args?.let {
                    val action = OverviewFragmentDirections.actionMovieMedia()
                    action.movieRemoteId = it.movieRemoteId
                    action.movieLocalId = it.movieLocalId
                    Navigation.findNavController(view).navigate(action)
                }
            }

            R.id.cast_menu_item -> {
                args?.let {
                    val action = OverviewFragmentDirections.actionMovieCredits()
                    action.movieRemoteId = it.movieRemoteId
                    action.movieLocalId = it.movieLocalId
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }
        return true
    }
}