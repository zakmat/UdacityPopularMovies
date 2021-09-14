package com.mz.popmovies

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import com.mz.popmovies.R
import com.mz.popmovies.data.MovieContract
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.utilities.NetworkUtils
import com.mz.popmovies.utilities.JsonUtils
import java.lang.Exception
import java.util.*

/**
 * Created by mateusz.zak on 03.05.2017.
 */
class FetchMoviesTask(context: Context, private val mQueryType: String, private val mPage: String) : AsyncTaskLoader<ArrayList<MovieEntry>?>(context) {
    private val LOG_TAG = FetchMoviesTask::class.java.simpleName
    override fun onStartLoading() {
        super.onStartLoading()
        //mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    override fun loadInBackground(): ArrayList<MovieEntry>? {
        Log.i(LOG_TAG, "QueryType: " + mQueryType + " is compared with " + super.getContext().resources.getString(R.string.favourites_label))
        if (mQueryType == super.getContext().resources.getString(R.string.favourites_label)) {
            val cursor = super.getContext().contentResolver.query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry.COLUMN_TITLE)
            if (cursor == null) {
                Log.w(LOG_TAG, "Null cursor retrieved")
                return null
            }
            Log.i(LOG_TAG, "Retrieved non-null cursor with " + cursor.count + " elements")
            val movieData = getDataFromCursor(cursor)
            cursor.close()
            return movieData
        }
        val movieRequestUrl = NetworkUtils.buildMovieRequest(mQueryType, mPage)
        return try {
            val jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl!!)
            if (jsonMovieResponse == null) {
                return null
            }
            Log.i(LOG_TAG, "Retrieved " + jsonMovieResponse.length + " bytes of data")
            JsonUtils
                    .getFullMovieDataFromJson(super.getContext(), jsonMovieResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getDataFromCursor(cursor: Cursor?): ArrayList<MovieEntry>? {
        if (cursor == null) {
            Log.i(LOG_TAG, "Cursor is null")
            return null
        }
        val movies = ArrayList<MovieEntry>(cursor.count)
        var index = 0
        while (cursor.moveToNext()) {
            val movie_id = cursor.getString(1)
            val title = cursor.getString(2)
            val overview = cursor.getString(3)
            val poster = cursor.getString(4)
            val voteAverage = cursor.getDouble(5)
            val releaseDate = Date(cursor.getLong(6))
            val movie = MovieEntry(Integer.valueOf(movie_id), title, overview, poster, releaseDate, voteAverage)
            movies.add(index, movie)
            index++
        }
        return movies
    }
}