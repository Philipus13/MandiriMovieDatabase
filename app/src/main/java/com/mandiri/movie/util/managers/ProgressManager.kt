package com.mandiri.movie.util.managers

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

open class ProgressManager: CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()

    open fun loading() {
        launch {
            loading.value = true
            error.value = false
        }
    }

    open fun load() {
        launch { loading.value = true }
    }

    open fun loaded() {
        launch { loading.value = false }
    }

    open fun error() {
        launch {
            loading.value = false
            error.value = true
        }
    }

    open fun success() {
        launch {
            loading.value = false
            error.value = false
        }
    }
}