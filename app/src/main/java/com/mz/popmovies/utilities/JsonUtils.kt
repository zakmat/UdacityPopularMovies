package com.mz.popmovies.utilities

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import com.mz.popmovies.DetailActivity
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import java.net.HttpURLConnection
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by mz on 2017-02-04.
 */
/**
 * Utility functions to handle themovieDB JSON data.
 */
object JsonUtils {
    private val TAG = JsonUtils::class.java.simpleName
    private const val POSTER = "poster_path"
    private const val MOVIE_ID = "id"
    private const val TITLE = "title"
    private const val OVERVIEW = "overview"
    private const val RELEASE_DATE = "release_date"
    private const val RATING = "vote_average"
    private const val AUTHOR = "author"
    private const val CONTENT = "content"
    private const val STATUS_CODE = "status_code"
    private const val RESULTS = "results"
    private const val NAME = "name"
    private const val KEY = "key"
    private const val SITE = "site"

    /**
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing movie data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    @Throws(JSONException::class)
    fun getFullMovieDataFromJson(context: Context?, movieJsonStr: String?): ArrayList<MovieEntry>? {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        val RESULTS = "results"
        val MOVIE_ID = "id"
        val POSTER = "poster_path"
        val TITLE = "title"
        val OVERVIEW = "overview"
        val RELEASE_DATE = "release_date"
        val RATING = "vote_average"
        val STATUS_CODE = "status_code"

        /* String array to hold each day's weather String */
        val movieJson = JSONObject(movieJsonStr)

        /* Is there an error? */if (movieJson.has(STATUS_CODE)) {
            val errorCode = movieJson.getInt(STATUS_CODE)
            if (errorCode != HttpURLConnection.HTTP_OK) {
                handleBadStatus(errorCode)
                return null
            }
        }
        val movieArray = movieJson.getJSONArray(RESULTS)
        val parsedMoviesData = ArrayList<MovieEntry>(movieArray.length())
        for (i in 0 until movieArray.length()) {
            val movie = movieArray.getJSONObject(i)
            val movie_id = movie.getInt(MOVIE_ID)
            val title = movie.getString(TITLE)
            val overview = movie.getString(OVERVIEW)
            val poster = movie.getString(POSTER)
            val dateStr = movie.getString(RELEASE_DATE)
            var releaseDate: Date? = null
            try {
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                releaseDate = df.parse(dateStr)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val votesAverage = movie.getDouble(RATING)
            parsedMoviesData.add(MovieEntry(movie_id, title, overview, poster, releaseDate, votesAverage))
            val relDateString = releaseDate?.toString() ?: "????-??-??"
            Log.i(TAG, "$title $relDateString $poster")
        }
        return parsedMoviesData
    }

    @JvmStatic
    @Throws(JSONException::class)
    fun <CustomEntry> getEntriesFromJson(klass: Class<CustomEntry>, detailActivity: DetailActivity?, jsonRequest: String?): Array<CustomEntry>? {
        if (klass == ReviewEntry::class.java) {
            return getReviewsDataFromJson(detailActivity, jsonRequest)?.toArray() as Array<CustomEntry>?
        } else if (klass == TrailerEntry::class.java) {
            return getTrailersDataFromJson(detailActivity, jsonRequest)?.toArray() as Array<CustomEntry>?
        }
        Log.i(TAG, "No known JSON parser for class " + klass.simpleName)
        return null
    }

    @Throws(JSONException::class)
    fun getReviewsDataFromJson(detailActivity: DetailActivity?, jsonReviewsResponse: String?): ArrayList<ReviewEntry>? {
        val reviewJson = JSONObject(jsonReviewsResponse)

        /* Is there an error? */if (reviewJson.has(STATUS_CODE)) {
            val errorCode = reviewJson.getInt(STATUS_CODE)
            if (errorCode != HttpURLConnection.HTTP_OK) {
                handleBadStatus(errorCode)
                return null
            }
        }
        val reviewArray = reviewJson.getJSONArray(RESULTS)
        val parsedReviewsData = ArrayList<ReviewEntry>(reviewArray.length())
        for (i in 0 until reviewArray.length()) {
            val review = reviewArray.getJSONObject(i)
            val review_id = review.getString(MOVIE_ID)
            val author = review.getString(AUTHOR)
            val content = review.getString(CONTENT)
            parsedReviewsData.add(ReviewEntry(review_id, author, content))
            Log.i("JsonUtils", author)
        }
        return parsedReviewsData
    }

    @Throws(JSONException::class)
    fun getTrailersDataFromJson(detailActivity: DetailActivity?, jsonTrailersResponse: String?): ArrayList<TrailerEntry>? {
        val trailerJson = JSONObject(jsonTrailersResponse)

        /* Is there an error? */if (trailerJson.has(STATUS_CODE)) {
            val errorCode = trailerJson.getInt(STATUS_CODE)
            if (errorCode != HttpURLConnection.HTTP_OK) {
                handleBadStatus(errorCode)
                return null
            }
        }
        val trailerArray = trailerJson.getJSONArray(RESULTS)
        val parsedTrailersData = ArrayList<TrailerEntry>(trailerArray.length())
        for (i in 0 until trailerArray.length()) {
            val trailer = trailerArray.getJSONObject(i)
            val trailer_id = trailer.getString(MOVIE_ID)
            val name = trailer.getString(NAME)
            val key = trailer.getString(KEY)
            val site = trailer.getString(SITE)
            parsedTrailersData.add(TrailerEntry(trailer_id, name, key, site))
            Log.i("JsonUtils", "$name $key $site")
        }
        return parsedTrailersData
    }

    private fun handleBadStatus(errorCode: Int) {
        when (errorCode) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> Log.e(TAG, "Not authorized to use API. Probably api_key missing or invalid")
            HttpURLConnection.HTTP_NOT_FOUND -> Log.e(TAG, "HTTP not found. Check code for typos or errors")
            else -> Log.e(TAG, "HTTP error: $errorCode")
        }
    }
}