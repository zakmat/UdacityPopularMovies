package com.mz.popmovies.data.remote

import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.Response
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import com.mz.popmovies.utilities.NetworkUtils
import io.ktor.client.*
import io.ktor.client.request.*

class MoviesServiceImpl(
    private val client: HttpClient
) : MoviesService {
    override suspend fun getMovies(queryType: String, page: Int): List<MovieEntry> {
        return client.get<Response<MovieEntry>> {
            url("${NetworkUtils.DBMOVIE_BASE_URL}$queryType").apply {
                parameter(NetworkUtils.APIKEY_PARAM, NetworkUtils.apiKey)
                parameter(NetworkUtils.LANGUAGE_PARAM, NetworkUtils.language)
                parameter(NetworkUtils.PAGE_PARAM, page)
            }
        }.results
    }

    override suspend fun getReviews(movieId: Int): List<ReviewEntry> {
        return client.get<Response<ReviewEntry>> {
            url(String.format(NetworkUtils.DBMOVIE_REVIEW_URL, movieId)).apply {
                parameter(NetworkUtils.APIKEY_PARAM, NetworkUtils.apiKey)
            }
        }.results
    }

    override suspend fun getTrailers(movieId: Int): List<TrailerEntry> {
        return client.get<Response<TrailerEntry>> {
            url(String.format(NetworkUtils.DBMOVIE_VIDEO_URL, movieId)).apply {
                parameter(NetworkUtils.APIKEY_PARAM, NetworkUtils.apiKey)
            }
        }.results
    }
}