package com.mandiri.movie.ui.details.credits

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreditsViewModelFactory (
    private val application: Application,
    private val arguments: Bundle?
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CreditsViewModel(application, arguments) as T
    }
}