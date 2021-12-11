package com.mz.popmovies.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import timber.log.Timber

/**
 * Created by mateusz.zak on 08.04.2017.
 */
class MovieProvider : ContentProvider() {
    private var mOpenHelper: MoviesDbHelper? = null
    override fun onCreate(): Boolean {
        mOpenHelper = MoviesDbHelper(context)
        return true
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        val db = mOpenHelper!!.writableDatabase
        Timber.d("bulkInsert invoked")
        return when (sUriMatcher.match(uri)) {
            CODE_MOVIE, CODE_FAVOURITE_MOVIES -> {
                Timber.d("... for CODE_MOVIE")
                db.beginTransaction()
                var rowsInserted = 0
                try {
                    for (value in values) {
                        val _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value)
                        if (_id != -1L) {
                            rowsInserted++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                if (rowsInserted > 0) {
                    context!!.contentResolver.notifyChange(uri, null)
                }
                rowsInserted
            }
            CODE_REVIEWS, CODE_VIDEOS -> super.bulkInsert(uri, values)
            else -> super.bulkInsert(uri, values)
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor: Cursor
        cursor = when (sUriMatcher.match(uri)) {
            CODE_MOVIE -> {

                //this is useful for checking if movie is in the favouries list
                val movie_id = uri.lastPathSegment
                val selectionArguments = arrayOf(movie_id)
                mOpenHelper!!.readableDatabase.query(
                    MovieContract.MovieEntry.TABLE_NAME,
                    projection,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    selectionArguments,
                    null,
                    null,
                    sortOrder
                )
            }
            CODE_FAVOURITE_MOVIES ->                 //this is used for displaying favourites
                mOpenHelper!!.readableDatabase.query(
                    MovieContract.MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            CODE_REVIEWS -> {
                val movie_id = uri.lastPathSegment
                val selectionArguments = arrayOf(movie_id)
                mOpenHelper!!.readableDatabase.query(
                    MovieContract.ReviewEntry.TABLE_NAME,
                    projection,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    selectionArguments,
                    null,
                    null,
                    sortOrder
                )
            }
            CODE_VIDEOS -> {
                val movie_id = uri.lastPathSegment
                val selectionArguments = arrayOf(movie_id)
                mOpenHelper!!.readableDatabase.query(
                    MovieContract.VideoEntry.TABLE_NAME,
                    projection,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    selectionArguments,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        throw RuntimeException("getType is not implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (sUriMatcher.match(uri)) {
            CODE_MOVIE -> {
                val movie_id = uri.lastPathSegment
                val selectionArguments = arrayOf(movie_id)
                return mOpenHelper!!.readableDatabase.delete(
                    MovieContract.MovieEntry.TABLE_NAME,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    selectionArguments
                )
            }
            CODE_FAVOURITE_MOVIES -> throw UnsupportedOperationException(
                "Unknown uri: $uri"
            )
        }
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    companion object {
        const val CODE_MOVIE = 100
        const val CODE_FAVOURITE_MOVIES = 101
        private const val CODE_VIDEOS = 102
        private const val CODE_REVIEWS = 103
        private val sUriMatcher = buildUriMatcher()
        private fun buildUriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = MovieContract.CONTENT_AUTHORITY
            matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", CODE_MOVIE)
            matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_FAVOURITE_MOVIES)
            matcher.addURI(authority, MovieContract.PATH_VIDEOS + "/#", CODE_VIDEOS)
            matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", CODE_REVIEWS)
            return matcher
        }
    }
}