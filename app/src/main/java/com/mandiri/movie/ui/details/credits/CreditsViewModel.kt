package com.mandiri.movie.ui.details.credits

import android.app.Application
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.mandiri.movie.MovieDetailsArgs
import com.mandiri.movie.R
import com.mandiri.movie.model.data.Credits
import com.mandiri.movie.model.remote.repositories.RemoteMovieRepository
import com.mandiri.movie.model.room.repositories.RoomMovieRepository
import com.mandiri.movie.util.DEFAULT_ID_VALUE
import com.mandiri.movie.util.isNetworkAvailable
import com.mandiri.movie.util.networkUnavailableNotification

class CreditsViewModel(application: Application, arguments: Bundle?) :
    AndroidViewModel(application) {

    private val args = arguments?.let { MovieDetailsArgs.fromBundle(it) }

    private val _credits = args?.let {
        if (it.movieLocalId == DEFAULT_ID_VALUE) {
            if (isNetworkAvailable(getApplication())) {
                RemoteMovieRepository().getCreditsFlow(args.movieRemoteId)
                    .asLiveData(viewModelScope.coroutineContext)
            } else {
                networkUnavailableNotification(getApplication())
                null
            }
        } else
            RoomMovieRepository(getApplication()).getCreditsFlowById(args.movieLocalId)
                .asLiveData(viewModelScope.coroutineContext)
    }

    val credits: LiveData<Credits>?
        get() = _credits

    fun onNavigationItemSelected(view: View, menuItem: MenuItem): Boolean {
        return args?.let {
            when (menuItem.itemId) {
                R.id.media_menu_item -> {
                    val action = CreditsFragmentDirections.actionMovieMedia()
                    action.movieRemoteId = it.movieRemoteId
                    action.movieLocalId = it.movieLocalId
                    Navigation.findNavController(view).navigate(action)
                }
                R.id.overview_menu_item -> {
                    val action = CreditsFragmentDirections.actionMovieOverview()
                    action.movieRemoteId = it.movieRemoteId
                    action.movieLocalId = it.movieLocalId
                    Navigation.findNavController(view).navigate(action)
                }
            }
            true
        } ?: run { false }
    }
}