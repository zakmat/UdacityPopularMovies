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
import com.mz.popmovies.ui.theme.PopMoviesTheme
import com.mz.popmovies.ui.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PopMoviesTheme {
                MainScreen(
                    movies = viewModel.state.value.movies,
                    onClick = { navigateToDetails(it) },
                    onCategoryChanged = { viewModel.changeCategory(it) }
                )
            }
        }
        viewModel.getMovies()
        //TODO: orientation should change grid size 2 vs 4
//        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            binding.rvPosters.layoutManager = GridLayoutManager(this, 4)
//        } else {
//            binding.rvPosters.layoutManager = GridLayoutManager(this, 2)
//        }
        //TODO: favourite support
        //TODO: allow scrolling as below
//        binding.rvPosters.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy > 0) //check for scroll down
//                {
//                    val visibleItemCount = binding.rvPosters.layoutManager!!.childCount
//                    val totalItemCount = binding.rvPosters.layoutManager!!.itemCount
//                    val firstVisibleItem =
//                        (binding.rvPosters.layoutManager as GridLayoutManager?)!!.findFirstVisibleItemPosition()
//                    Log.v("...", "Total item count $totalItemCount $visibleItemCount")
//                    if (binding.pbLoadingIndicator.visibility == View.INVISIBLE && visibleItemCount + firstVisibleItem >= totalItemCount) {
//                        //Do pagination.. i.e. fetch new data
//                        val nextPage = (totalItemCount / 20 + 1).toString()
//                        Log.v(">>>", "Getting page number: $nextPage")
//                        val selectedValue = binding.spinner.selectedItem.toString()
//                        if (selectedValue == resources.getString(R.string.popular_value)) {
//                            loadMoviesData(resources.getString(R.string.popular_label), nextPage)
//                        } else if (selectedValue == resources.getString(R.string.top_rated_value)) {
//                            loadMoviesData(resources.getString(R.string.top_rated_label), nextPage)
//                        }
//                    }
//                }
//            }
//        })
    }

    fun navigateToDetails(entry: MovieEntry) {
        val context: Context = this@MainActivity
        val destinationClass: Class<*> = DetailActivity::class.java
        val intent = Intent(context, destinationClass)
        intent.putExtra("MovieEntry", entry)
        startActivity(intent)
    }

    //TODO: bind options menu to access preferences
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val startSettingsActivity = Intent(this, SettingsActivity::class.java)
            startActivity(startSettingsActivity)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
    }
}