package com.mz.popmovies

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mz.popmovies.MovieAdapter.MovieAdapterOnClickHandler
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<MovieEntry>>,
    MovieAdapterOnClickHandler {
    private lateinit var mMovieAdapter: MovieAdapter
    private lateinit var binding: ActivityMainBinding

    public override fun onSaveInstanceState(outState: Bundle) {
        Log.d(LOG_TAG, "onSaveInstanceState called")
        super.onSaveInstanceState(outState)
        outState.putInt(SELECTED_CATEGORY, binding.spinner.selectedItemPosition)
        outState.putParcelable(GRID_STATE, binding.rvPosters.layoutManager!!.onSaveInstanceState())
        outState.putParcelableArrayList(SELECTED_MOVIES, mMovieAdapter.items)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedMovies: ArrayList<MovieEntry> =
            savedInstanceState.getParcelableArrayList(SELECTED_MOVIES)!!
        mMovieAdapter.setMovieData(savedMovies)
        Log.d(LOG_TAG, "onRestoreInstanceState called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        mMovieAdapter = MovieAdapter(this@MainActivity)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.rvPosters.layoutManager = GridLayoutManager(this, 4)
        } else {
            binding.rvPosters.layoutManager = GridLayoutManager(this, 2)
        }
        binding.rvPosters.adapter = mMovieAdapter
        binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.selectedItem.toString()
                Log.v("SPINNER", parent.getItemAtPosition(position).toString())
                when (selectedItem) {
                    resources.getString(R.string.popular_value) -> {
                        loadMoviesData(resources.getString(R.string.popular_label), "1")
                    }
                    resources.getString(R.string.top_rated_value) -> {
                        loadMoviesData(resources.getString(R.string.top_rated_label), "1")
                    }
                    resources.getString(R.string.favourites_value) -> {
                        loadMoviesData(resources.getString(R.string.favourites_label), "1")
                    }
                    else -> {
                        Log.e("SPINNER", "Not recognized item selected")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        if (savedInstanceState != null) {
            val gridState = savedInstanceState.getParcelable<Parcelable>(GRID_STATE)
            binding.rvPosters.layoutManager!!.onRestoreInstanceState(gridState)
            val selectedCategory = savedInstanceState.getInt(SELECTED_CATEGORY)
            binding.spinner.setSelection(selectedCategory, false)
        } else {
            binding.spinner.setSelection(0, false)
        }
        binding.rvPosters.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) //check for scroll down
                {
                    val visibleItemCount = binding.rvPosters.layoutManager!!.childCount
                    val totalItemCount = binding.rvPosters.layoutManager!!.itemCount
                    val firstVisibleItem =
                        (binding.rvPosters.layoutManager as GridLayoutManager?)!!.findFirstVisibleItemPosition()
                    Log.v("...", "Total item count $totalItemCount $visibleItemCount")
                    if (binding.pbLoadingIndicator.visibility == View.INVISIBLE && visibleItemCount + firstVisibleItem >= totalItemCount) {
                        //Do pagination.. i.e. fetch new data
                        val nextPage = (totalItemCount / 20 + 1).toString()
                        Log.v(">>>", "Getting page number: $nextPage")
                        val selectedValue = binding.spinner.selectedItem.toString()
                        if (selectedValue == resources.getString(R.string.popular_value)) {
                            loadMoviesData(resources.getString(R.string.popular_label), nextPage)
                        } else if (selectedValue == resources.getString(R.string.top_rated_value)) {
                            loadMoviesData(resources.getString(R.string.top_rated_label), nextPage)
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (binding.spinner.selectedItem.toString() == resources.getString(R.string.favourites_value)) {
            loadMoviesData(resources.getString(R.string.favourites_label), "1")
        }
    }

    private fun loadMoviesData(queryType: String, page: String) {
        val queryBundle = Bundle()
        queryBundle.putString(QUERY_TYPE, queryType)
        queryBundle.putString(PAGE_NUM, page)
        val loaderManager = LoaderManager.getInstance(this)
        val movieLoader = loaderManager.getLoader<ArrayList<MovieEntry>>(MOVIE_LOADER_ID)
        if (page == "1") {
            //reset movies upon changing category
            mMovieAdapter.setMovieData(null)
        }
        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, queryBundle, this).forceLoad()
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, queryBundle, this).forceLoad()
        }
    }

    override fun OnClick(entry: MovieEntry) {
        val context: Context = this@MainActivity
        val destinationClass: Class<*> = DetailActivity::class.java
        val intent = Intent(context, destinationClass)
        intent.putExtra("MovieEntry", entry)
        startActivity(intent)
    }

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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<MovieEntry>> {
        val queryType = args!!.getString(QUERY_TYPE)
        val pageNumber = args.getString(PAGE_NUM)
        Log.v(LOG_TAG, "OnCreateLoader called")
        return FetchMoviesTask(this, queryType!!, pageNumber!!)
    }

    override fun onLoadFinished(
        loader: Loader<List<MovieEntry>>,
        movieData: List<MovieEntry>
    ) {
        binding.pbLoadingIndicator.visibility = View.INVISIBLE
        mMovieAdapter.setMovieData(movieData)
    }

    override fun onLoaderReset(loader: Loader<List<MovieEntry>>) {
        Log.v(LOG_TAG, "OnLoaderReset called")
    }

    companion object {
        private const val SELECTED_CATEGORY = "SELECTED_CATEGORY"
        private const val GRID_STATE = "GRID_STATE"
        private val LOG_TAG = MainActivity::class.java.simpleName
        private const val SELECTED_MOVIES = "SELECTED_MOVIES"
        private const val QUERY_TYPE = "query_type"
        private const val PAGE_NUM = "page"
        private const val MOVIE_LOADER_ID = 22
    }
}