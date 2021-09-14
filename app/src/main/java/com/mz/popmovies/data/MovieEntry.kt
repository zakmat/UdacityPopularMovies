package com.mz.popmovies.data

import android.os.Parcelable
import android.os.Parcel
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Created by mz on 2017-02-04.
 */
@Parcelize
data class MovieEntry(
    val movie_id: Int,
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val releaseDate: Date?,
    val voteAverage: Double
) : Parcelable

