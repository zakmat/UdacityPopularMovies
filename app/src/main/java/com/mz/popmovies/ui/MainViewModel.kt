package com.mz.popmovies.ui

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.remote.MoviesService
import com.mz.popmovies.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = Repository(MoviesService.create())

    private val _state = mutableStateOf(LoadedMoviesState())
    val state: State<LoadedMoviesState> = _state

    data class LoadedMoviesState(
        val isLoading: Boolean = false,
        val category: String = "popular",
        val movies: List<MovieEntry> = emptyList()
    )

    private fun nextPage() = _state.value.movies.size / 20 + 1

    fun getMovies() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                _state.value = _state.value.copy(
                    isLoading = false,
                    movies = repository.fetchMovies(_state.value.category, nextPage())
                )

            } catch (e: Exception) {
                Log.d(
                    "MovieDetailViewModel",
                    "Failed to load ${_state.value.category} movies (page: ${nextPage()})",
                    e
                )
                _state.value = _state.value.copy(false)
            }
        }
    }

    fun changeCategory(category: String) {
        if (category == _state.value.category) {
            return
        }
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                _state.value = _state.value.copy(
                    isLoading = false,
                    category = category,
                    movies = repository.fetchMovies(category, 1)
                )

            } catch (e: Exception) {
                Log.d(
                    "MovieDetailViewModel",
                    "Failed to load ${_state.value.category} movies (page: ${nextPage()})",
                    e
                )
                _state.value = _state.value.copy(false)
            }
        }
    }
}
