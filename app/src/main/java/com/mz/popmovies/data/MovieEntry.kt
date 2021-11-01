package com.mz.popmovies.data

import android.os.Parcelable
import com.mz.popmovies.utilities.NetworkUtils
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by mz on 2017-02-04.
 */
@Parcelize
@Serializable
data class MovieEntry(
    @SerialName("id")
    val movie_id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double
) : Parcelable {
    val thumbnail: String?
        get() = if (posterPath != null) NetworkUtils.buildPosterRequest(
            342,
            posterPath
        ).toString() else null
}

@Serializable
data class Response<T>(
    val page: Int = 1,
    val results: List<T>
)

@Serializable
data class TrailerEntry(
    @SerialName("id")
    private val trailer_id: String,
    val name: String,
    val key: String,
    private val site: String
) {
    val thumbnail: String?
        get() = if (key.isNotBlank()) NetworkUtils.buildYoutubeThumbnail(key).toString() else null
}

@Serializable
data class ReviewEntry(
    @SerialName("id")
    private val review_id: String,
    val author: String,
    val content: String
)
