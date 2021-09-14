package com.mz.popmovies.data

import com.mz.popmovies.utilities.NetworkUtils.buildMovieRequest
import android.os.Parcelable
import android.os.Parcel
import java.net.URL

/**
 * Created by mz on 2017-02-20.
 */
data class ReviewEntry(
    private val review_id: String?,
    val author: String?,
    val content: String?
)
