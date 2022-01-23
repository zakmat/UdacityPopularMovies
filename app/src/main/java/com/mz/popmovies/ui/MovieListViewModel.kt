package com.mz.popmovies.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.remote.MoviesService
import com.mz.popmovies.repository.Repository
import kotlinx.coroutines.launch
import timber.log.Timber

const val PAGE_SIZE = 20

class MovieListViewModel(val repository: Repository) : ViewModel() {
    private val _state = mutableStateOf(LoadedMoviesState())
    val state: State<LoadedMoviesState> = _state

    data class LoadedMoviesState(
        val isLoading: Boolean = false,
        val category: String = "popular",
        val page: Int = 1,
        val movies: List<MovieEntry> = emptyList()
    )

    fun getMoreMovies() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, page = _state.value.page + 1)
            _state.value = _state.value.copy(
                isLoading = false,
                movies = _state.value.movies + repository.fetchMovies(
                    _state.value.category,
                    _state.value.page
                )
            )
        }
    }

    fun getMovies() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                _state.value = _state.value.copy(
                    isLoading = false,
                    movies = repository.fetchMovies(_state.value.category, _state.value.page)
                )

            } catch (e: Exception) {
                Timber.d(
                    e,
                    "Failed to load ${_state.value.category} movies (page: ${_state.value.page})"
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
                    page = 1,
                    movies = repository.fetchMovies(category, 1)
                )

            } catch (e: Exception) {
                Timber.d(
                    e,
                    "Failed to load ${_state.value.category} movies (page: ${_state.value.page})"
                )
                _state.value = _state.value.copy(false)
            }
        }
    }
}
