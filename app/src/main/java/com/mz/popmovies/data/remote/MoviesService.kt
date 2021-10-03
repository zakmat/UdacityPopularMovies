package com.mz.popmovies.data.remote

import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json

interface MoviesService {
    suspend fun getMovies(queryType: String, page: Int): List<MovieEntry>
    suspend fun getReviews(movieId: Int): List<ReviewEntry>
    suspend fun getTrailers(movieId: Int): List<TrailerEntry>

    companion object {
        fun create(): MoviesService {
            return MoviesServiceImpl(
                client = HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(Json {
                            ignoreUnknownKeys = true
                        }
                        )
                    }
                }
            )
        }
    }
}