package com.mz.popmovies

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mz.popmovies.data.MovieContract
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.ui.DetailScreen
import com.mz.popmovies.ui.MovieViewModel
import com.mz.popmovies.ui.theme.PopMoviesTheme
import com.mz.popmovies.utilities.NetworkUtils
import timber.log.Timber

class DetailActivity : AppCompatActivity() {
    // Investigate: Simple by viewModels does fails to re-launch Job inside getMovie
    private val viewModel : MovieViewModel by lazy { Injection.provideViewModelFactory(this).create(MovieViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PopMoviesTheme {
                DetailScreen(
                    viewModel.state.value,
                    onBack = { finish() },
                    onClick = { key -> openYoutubeTrailer(key) })
            }
        }
        if (intent.hasExtra("MovieEntry")) {
            val mMovie: MovieEntry = intent.getParcelableExtra("MovieEntry")!!
            Timber.i("Received intent for ${mMovie.title}")
            viewModel.getMovie(mMovie.movie_id)
        }
    }

//        mFavourite!!.isSelected = isFavouriteMovie(incomingEntry)


    private fun prepareContentValues(incomingEntry: MovieEntry?): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, incomingEntry!!.movie_id)
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, incomingEntry.overview)
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, incomingEntry.posterPath)
//        contentValues.put(
//            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
//            incomingEntry.releaseDate?.time
//        )
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, incomingEntry.title)
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, incomingEntry.voteAverage)
        Timber.v(
            "Vote Average of saved movie: " + incomingEntry.voteAverage + " from Content value: " + contentValues.getAsDouble(
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
            )
        )
        return contentValues
    }

    fun onClickFavorite(view: View?) {
        viewModel.toggleFavourite()
    }

    //TODO: onClick open youtube trailer
    private fun openYoutubeTrailer(key: String) {
        val youtubeMovie = NetworkUtils.buildYoutubeRequest(key)
        val viewTrailerIntent = Intent(Intent.ACTION_VIEW, youtubeMovie)
        if (viewTrailerIntent.resolveActivity(this.baseContext.packageManager) != null) {
            baseContext.startActivity(viewTrailerIntent)
        } else {
            Timber.w("Youtube activity not resolved")
        }
    }
}