package com.mz.popmovies.utilities

import android.net.Uri
import com.mz.popmovies.BuildConfig
import timber.log.Timber

/**
 * These utilities will be used to communicate with the weather servers.
 */
object NetworkUtils {
    private val TAG = NetworkUtils::class.java.simpleName
    const val DBMOVIE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val DBMOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/"
    const val DBMOVIE_REVIEW_URL = "https://api.themoviedb.org/3/movie/%d/reviews"
    const val DBMOVIE_VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos"

    /* The format we want our API to return */
    const val APIKEY_PARAM = "api_key"
    const val LANGUAGE_PARAM = "language"
    const val PAGE_PARAM = "page"
    const val apiKey = BuildConfig.TMDB_API_TOKEN
    const val language = "en-US"

    @JvmStatic
    fun buildPosterRequest(requestedSize: Int, posterPath: String?): Uri {
        val builtUri = Uri.parse(DBMOVIE_IMAGE_BASE_URL).buildUpon().appendPath("w$requestedSize")
            .appendEncodedPath(posterPath).build()
        Timber.v(TAG, "Built URI: $builtUri")
        return builtUri
    }

    @JvmStatic
    fun buildYoutubeRequest(key: String?): Uri {
        val builtUri = Uri.parse("https://www.youtube.com").buildUpon().appendPath("watch")
            .appendQueryParameter("v", key).build()
        Timber.i("DetailActivity", builtUri.toString())
        return builtUri
    }

    @JvmStatic
    fun buildYoutubeThumbnail(key: String?): Uri {
        val builtUri = Uri.parse("https://img.youtube.com/vi").buildUpon().appendPath(key)
            .appendPath("default.jpg").build()
        Timber.i("DetailActivity", builtUri.toString())
        return builtUri
    }
}