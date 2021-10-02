package com.mz.popmovies.utilities

import android.net.Uri
import android.util.Log
import com.mz.popmovies.BuildConfig
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * These utilities will be used to communicate with the weather servers.
 */
object NetworkUtils {
    private val TAG = NetworkUtils::class.java.simpleName
    private const val DBMOVIE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    private const val DBMOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/"
    private const val DBMOVIE_REVIEW_URL = "https://api.themoviedb.org/3/movie/%d/reviews"
    private const val DBMOVIE_VIDEO_URL = "https://api.themoviedb.org/3/movie/%d/videos"

    /* The format we want our API to return */
    private const val APIKEY_PARAM = "api_key"
    private const val LANGUAGE_PARAM = "language"
    private const val PAGE_PARAM = "page"
    private const val apiKey = BuildConfig.TMDB_API_TOKEN
    private const val language = "en-US"
    @JvmStatic
    fun buildPosterRequest(requestedSize: Int, posterPath: String?): Uri {
        val builtUri = Uri.parse(DBMOVIE_IMAGE_BASE_URL).buildUpon().appendPath("w$requestedSize").appendEncodedPath(posterPath).build()
        Log.v(TAG, "Built URI: $builtUri")
        return builtUri
    }

    /**
     * Builds the URL used to retrieve thumbnails jserver using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param queryType The queryType that will be queried for - possible values are "popular"
     * @return The URL to use to query the weather server.
     */
    fun buildMovieRequest(queryType: String?, page: String?): URL? {
        val builtUri = Uri.parse(DBMOVIE_BASE_URL).buildUpon().appendPath(queryType)
                .appendQueryParameter(APIKEY_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build()
        return buildUrlFromURI(builtUri)
    }

    @JvmStatic
    fun buildMovieRequest(klass: Class<*>, movieId: Int): URL? {
        Log.v(TAG, "Int of movie id:$movieId")
        val dbMovieUrl: String
        dbMovieUrl = if (klass == ReviewEntry::class.java) {
            DBMOVIE_REVIEW_URL
        } else if (klass == TrailerEntry::class.java) {
            DBMOVIE_VIDEO_URL
        } else {
            Log.v(TAG, "No appropriate URL for " + klass.simpleName + " request")
            return null
        }
        val builtUri = Uri.parse(String.format(dbMovieUrl, movieId)).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, apiKey).build()
        return buildUrlFromURI(builtUri)
    }

    private fun buildUrlFromURI(uri: Uri): URL? {
        var url: URL? = null
        try {
            url = URL(uri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        Log.v(TAG, "Built URL $url")
        return url
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val `in` = urlConnection.inputStream
            val scanner = Scanner(`in`)
            scanner.useDelimiter("\\A")
            val hasInput = scanner.hasNext()
            if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    @JvmStatic
    fun buildYoutubeRequest(key: String?): Uri {
        val builtUri = Uri.parse("https://www.youtube.com").buildUpon().appendPath("watch")
                .appendQueryParameter("v", key).build()
        Log.i("DetailActivity", builtUri.toString())
        return builtUri
    }

    @JvmStatic
    fun buildYoutubeThumbnail(key: String?): Uri {
        val builtUri = Uri.parse("https://img.youtube.com/vi").buildUpon().appendPath(key).appendPath("default.jpg").build()
        Log.i("DetailActivity", builtUri.toString())
        return builtUri
    }
}