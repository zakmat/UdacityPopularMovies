package com.mz.popmovies

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.ui.MainScreen
import com.mz.popmovies.ui.MovieListViewModel
import com.mz.popmovies.ui.theme.PopMoviesTheme

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MovieListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PopMoviesTheme {
                MainScreen(
                    movies = viewModel.state.value.movies,
                    isLoading = viewModel.state.value.isLoading,
                    onClick = { navigateToDetails(it) },
                    onCategoryChanged = { viewModel.changeCategory(it) },
                    onEndReached = { viewModel.getMoreMovies() },
                    onSettings = { navigateToSettings()}
                )
            }
        }
        viewModel.getMovies()
        //TODO: favourite support
    }

    private fun navigateToDetails(entry: MovieEntry) {
        val context: Context = this@MainActivity
        val destinationClass: Class<*> = DetailActivity::class.java
        val intent = Intent(context, destinationClass)
        intent.putExtra("MovieEntry", entry)
        startActivity(intent)
    }

    private fun navigateToSettings() {
        val context: Context = this@MainActivity
        val destinationClass: Class<*> = SettingsActivity::class.java
        val startSettingsActivity = Intent(context, destinationClass)
        startActivity(startSettingsActivity)
    }
}