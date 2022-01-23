package com.mz.popmovies.repository

import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import com.mz.popmovies.data.remote.MoviesService

class Repository(val api: MoviesService) {
    val movies: MutableList<MovieEntry> = ArrayList()
    suspend fun getMovie(movieId: Int): MovieEntry? {
        if (movies.isEmpty()) {
            movies.addAll(fetchMovies("popular", 1))
        }
        return movies.find { it.movie_id == movieId }
    }

    suspend fun getReviews(movieId: Int): List<ReviewEntry> {
        return api.getReviews(movieId)
    }

    suspend fun getTrailers(movieId: Int): List<TrailerEntry> {
        return api.getTrailers(movieId)
    }

    suspend fun fetchMovies(query: String, page: Int): List<MovieEntry> {
        val fetchedMovies = api.getMovies(query, page)
        movies.addAll(fetchedMovies)
        return fetchedMovies
    }
}