package com.mz.popmovies.data

import android.net.Uri
import android.provider.BaseColumns
import com.mz.popmovies.data.MovieContract

/**
 * Created by mateusz.zak on 07.04.2017.
 */
object MovieContract : BaseColumns {
    const val CONTENT_AUTHORITY = "com.mz.popmovies"
    val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
    const val PATH_MOVIE = "movie"
    const val PATH_VIDEOS = "video"
    const val PATH_REVIEWS = "review"

    object MovieEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build()
        const val TABLE_NAME = "movie"
        const val COLUMN_MOVIE_ID = "movie_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_OVERVIEW = "overview"
        const val COLUMN_POSTERPATH = "poster_path"
        const val COLUMN_RELEASE_DATE = "release_date"
        const val COLUMN_VOTE_AVERAGE = "vote_average"
    }

    object ReviewEntry : BaseColumns {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build()
        const val TABLE_NAME = "review"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
    }

    object VideoEntry {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build()
        const val TABLE_NAME = "video"
        const val COLUMN_NAME = "name"
        const val COLUMN_KEY = "key"
        const val COLUMN_SITE = "site"
    }
}