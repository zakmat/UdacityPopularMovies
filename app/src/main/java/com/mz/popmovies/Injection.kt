package com.mz.popmovies

import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.mz.popmovies.data.remote.MoviesService
import com.mz.popmovies.repository.Repository
import com.mz.popmovies.ui.ViewModelFactory

object Injection {
    private val repository : Repository by lazy { Repository(MoviesService.create()) }
    private fun provideTmdbRepository(): Repository {
        return repository
    }

    fun provideViewModelFactory(owner: SavedStateRegistryOwner): ViewModelProvider.Factory {
        return ViewModelFactory(owner, provideTmdbRepository())
    }
}
