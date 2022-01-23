package com.mz.popmovies.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.mz.popmovies.repository.Repository

class ViewModelFactory(owner: SavedStateRegistryOwner, private val repository: Repository)
: AbstractSavedStateViewModelFactory(owner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            val vm = MovieViewModel(repository)
            @Suppress("UNCHECKED_CAST")
            return cache.getOrPut(key) { vm } as T

        }
        if (modelClass.isAssignableFrom(MovieListViewModel::class.java)) {
            val vm = MovieListViewModel(repository)
            @Suppress("UNCHECKED_CAST")
            return cache.getOrPut(key) { vm } as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
    companion object {
        val cache = HashMap<String, ViewModel>()
    }
}