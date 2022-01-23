package com.mz.popmovies.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import com.mz.popmovies.data.remote.MoviesService
import com.mz.popmovies.repository.Repository
import kotlinx.coroutines.launch
import timber.log.Timber

class MovieViewModel(val repository: Repository) : ViewModel() {
    private val _state = mutableStateOf(MovieDetailState())
    val state: State<MovieDetailState> = _state

    fun getMovie(movieId: Int) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(true)
                _state.value = _state.value.copy(
                    false,
                    repository.getMovie(movieId),
                    repository.getTrailers(movieId),
                    repository.getReviews(movieId)
                )

            } catch (e: Exception) {
                Timber.d(e, "Failed to load movie id: $movieId")
                _state.value = _state.value.copy(false)
            }
        }
    }

    fun toggleFavourite() {
        val movie_id = _state.value.movie!!.movie_id
//        if (isFavouriteMovie(movie_id)) {
//            removeFromFavourites(movie_id)
//        } else {
//            addToFavourites(movie_id)
//        }

    }

//    private fun isFavouriteMovie(movie_id: Int): Boolean {
//        val uriForMovie = MovieContract.MovieEntry.CONTENT_URI.buildUpon()
//            .appendPath(movie_id.toString()).build()
//        Log.v(LOG_TAG, "Uri for querying CP for specific movie: $uriForMovie")
//        val cursor = contentResolver.query(uriForMovie, null, null, null, null)
//        return cursor!!.count > 0
//    }
//
//    private fun removeFromFavourites(movie_id: Int) {
//        val uriForMovie = MovieContract.MovieEntry.CONTENT_URI.buildUpon()
//            .appendPath(movie_id.toString()).build()
//        val selArgs = arrayOf(movie_id.toString())
//        val numOfDeletedRows = contentResolver.delete(uriForMovie, null, null)
//        Log.v(
//            LOG_TAG,
//            "Removed movie: \"" + mMovie.title + "\" from db (" + numOfDeletedRows + " occurences)"
//        )
//        if (numOfDeletedRows > 0) {
//            Toast.makeText(baseContext, "Movie removed from favourites", Toast.LENGTH_LONG).show()
////            mFavourite!!.isSelected = false
//        } else {
//            Toast.makeText(baseContext, "Removing from favourites failed", Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun addToFavourites(mMovie: MovieEntry) {
//        val cv = prepareContentValues(mMovie)
//        val values = arrayOf(cv)
//        if (contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values) > 0) {
//            Toast.makeText(baseContext, "Movie added to the favourites", Toast.LENGTH_LONG).show()
////            mFavourite!!.isSelected = true
//        } else {
//            Toast.makeText(baseContext, "Adding to favourites failed", Toast.LENGTH_LONG).show()
//            Log.e(DetailActivity.LOG_TAG, "Insert into database failed")
//        }
//    }

    data class MovieDetailState(
        val isLoading: Boolean = true,
        val movie: MovieEntry? = null,
        val trailers: List<TrailerEntry> = emptyList(),
        val reviews: List<ReviewEntry> = emptyList()
    )

}