package com.mz.popmovies.data

import com.mz.popmovies.utilities.NetworkUtils.buildMovieRequest
import android.os.Parcelable
import android.os.Parcel
import java.net.URL

/**
 * Created by mz on 2017-02-20.
 */
data class TrailerEntry (
    private val trailer_id: String?,
    val name: String?,
    val key: String?,
    private val site: String?
)
