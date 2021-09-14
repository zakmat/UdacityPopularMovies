package com.mz.popmovies.data

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import com.mz.popmovies.data.MoviesDbHelper
import android.database.sqlite.SQLiteDatabase

/**
 * Created by mateusz.zak on 07.04.2017.
 */
class MoviesDbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID.toString() + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE.toString() + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW.toString() + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTERPATH.toString() + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE.toString() + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE.toString() + " TIMESTAMP " +
                ");"
        db.execSQL(SQL_CREATE_MOVIES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "movies.db"
        private const val DATABASE_VERSION = 1
    }
}