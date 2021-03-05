package com.mandiri.movie.ui.movielists.personal.customlists.addtolists

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.navigation.Navigation
import com.mandiri.movie.NavGraphDirections
import com.mandiri.movie.R
import com.mandiri.movie.model.data.CustomMovieList
import com.mandiri.movie.model.data.Movie
import com.mandiri.movie.model.remote.repositories.RemoteMovieRepository
import com.mandiri.movie.model.room.repositories.CustomMovieListRepository
import com.mandiri.movie.model.room.repositories.RoomMovieRepository
import com.mandiri.movie.util.isNetworkAvailable
import com.mandiri.movie.util.networkUnavailableNotification
import com.mandiri.movie.util.showProgressSnackBar
import com.mandiri.movie.util.showToast
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToListsTaskManager(
    private val application: Application,
    private val root: View,
    private val popupWindow: AddToListsPopupWindow
) : AddToListsPopupWindow.ListsConfirmedClickListener {

    private val remoteMovieRepository = RemoteMovieRepository()
    private val movieListRepository = CustomMovieListRepository(application)
    private val roomMovieRepository = RoomMovieRepository(application)

    init {
        popupWindow.setListsConfirmedClickListener(this)
        getCustomLists()
    }

    private fun getCustomLists() {
        CoroutineScope(Dispatchers.IO).launch {
            val customLists = movieListRepository.getAllCustomMovieLists()
            withContext(Dispatchers.Main) {
                popupWindow.setupLists(customLists)
            }
        }
    }

    override fun onConfirmListsClicked(
        movie: Movie,
        checkedLists: List<CustomMovieList>
    ): Boolean {
        return when {
            checkedLists.isNullOrEmpty() -> {
                showToast(
                    application,
                    application.getString(R.string.select_a_list),
                    Toast.LENGTH_SHORT
                )
                false
            }
            isNetworkAvailable(application) -> {
                CoroutineScope(Dispatchers.IO).launch {
                    showProgressSnackBar(
                        root,
                        application.getString(R.string.being_uploaded_to_list)
                    )
                    insertMovie(movie, checkedLists)
                    onMovieInserted()
                }
                true
            }
            else -> {
                networkUnavailableNotification(application)
                false
            }
        }
    }

    private suspend fun insertMovie(movie: Movie, checkedLists: List<CustomMovieList>) {
        // to get all the details of movie
        try {
            val fullMovie = remoteMovieRepository.getMovieDetails(movie.remoteId)
            fullMovie.finalizeInitialization(application)
            roomMovieRepository.insertOrUpdateMovie(
                application,
                fullMovie,
                checkedLists
            )
        } catch (e: Exception) {
            onInsertFailed()
        }

    }

    private fun onMovieInserted() {
        Snackbar.make(
            root,
            root.context.getString(R.string.successfully_uploaded_to_list),
            Snackbar.LENGTH_LONG
        )
            .setAction(root.context.getString(R.string.action_check_lists)) {
                val action = NavGraphDirections.actionGlobalCustomListsFragment()
                Navigation.findNavController(root).navigate(action)
            }.show()
    }

    private fun onInsertFailed() {
        Snackbar.make(
            root,
            root.context.getString(R.string.failed_to_upload_to_list),
            Snackbar.LENGTH_LONG
        ).show()
    }
}