package com.mz.popmovies

import android.content.Context
import android.database.Cursor
import androidx.loader.content.AsyncTaskLoader
import com.mz.popmovies.data.MovieContract
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.remote.MoviesService
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*

/**
 * Created by mateusz.zak on 03.05.2017.
 */
//TODO: this was responsible for retrieving favourite movies from offline storage.
// Restore functionality using Room database
class FetchMoviesTask(context: Context, private val mQueryType: String, private val mPage: String) :
    AsyncTaskLoader<List<MovieEntry>>(context) {
    override fun loadInBackground(): List<MovieEntry> {
        Timber.i(
            "QueryType: $mQueryType is compared with " + super.getContext().resources.getString(
                R.string.favourites_label
            )
        )
        if (mQueryType == super.getContext().resources.getString(R.string.favourites_label)) {
            val cursor = super.getContext().contentResolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_TITLE
            )
            if (cursor == null) {
                Timber.w("Null cursor retrieved")
                return emptyList()
            }
            Timber.i("Retrieved non-null cursor with ${cursor.count} elements")
            val movieData = getDataFromCursor(cursor)
            cursor.close()
            return movieData
        }
        val service = MoviesService.create()
        return runBlocking { service.getMovies(mQueryType, 1) }
    }

    private fun getDataFromCursor(cursor: Cursor?): List<MovieEntry> {
        if (cursor == null) {
            Timber.i("Cursor is null")
            return emptyList()
        }
        val movies = ArrayList<MovieEntry>(cursor.count)
        var index = 0
        while (cursor.moveToNext()) {
            val movie_id = cursor.getString(1)
            val title = cursor.getString(2)
            val overview = cursor.getString(3)
            val poster = cursor.getString(4)
            val voteAverage = cursor.getDouble(5)
            val releaseDate = Date(cursor.getLong(6)).toString()
            val movie = MovieEntry(
                Integer.valueOf(movie_id),
                title,
                overview,
                poster,
                releaseDate,
                voteAverage
            )
            movies.add(index, movie)
            index++
        }
        return movies
    }
}