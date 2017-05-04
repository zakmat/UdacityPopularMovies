package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mz.udacitypopularmovies.data.MovieEntry;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<MovieEntry>>, MovieAdapter.MovieAdapterOnClickHandler {

    private static final String SELECTED_CATEGORY = "SELECTED_CATEGORY";
    private static final String GRID_STATE = "GRID_STATE";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String SELECTED_MOVIES = "SELECTED_MOVIES";
    private static final String QUERY_TYPE = "query_type";
    private static final String PAGE_NUM = "page";
    private static final int MOVIE_LOADER_ID = 22;
    MovieAdapter mMovieAdapter;
    @BindView(R.id.rv_posters)
    RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.spinner)
    Spinner mSpinner;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState called");
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_CATEGORY, mSpinner.getSelectedItemPosition());
        outState.putParcelable(GRID_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(SELECTED_MOVIES, mMovieAdapter.getItems());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<MovieEntry> savedMovies = savedInstanceState.getParcelableArrayList(SELECTED_MOVIES);

        mMovieAdapter.setMovieData(savedMovies);
        Log.d(LOG_TAG, "onRestoreInstanceState called");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mMovieAdapter = new MovieAdapter(MainActivity.this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        mRecyclerView.setAdapter(mMovieAdapter);
        setSupportActionBar(mToolbar);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getSelectedItem().toString();
                Log.v("SPINNER", parent.getItemAtPosition(position).toString());
                if (selectedItem.equals(getResources().getString(R.string.popular_value))) {
                    loadMoviesData(getResources().getString(R.string.popular_label), "1");
                } else if (selectedItem.equals(getResources().getString(R.string.top_rated_value))) {
                    loadMoviesData(getResources().getString(R.string.top_rated_label), "1");
                } else if (selectedItem.equals(getResources().getString(R.string.favourites_value))) {
                    loadMoviesData(getResources().getString(R.string.favourites_label), "1");
                } else {
                    Log.e("SPINNER", "Not recognized item selected");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (savedInstanceState != null) {

            Parcelable gridState = savedInstanceState.getParcelable(GRID_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(gridState);

            int selectedCategory = savedInstanceState.getInt(SELECTED_CATEGORY);
            mSpinner.setSelection(selectedCategory, false);
        } else {
            mSpinner.setSelection(0, false);
        }
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    int visibleItemCount = mRecyclerView.getLayoutManager().getChildCount();
                    int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
                    int firstVisibleItem = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    Log.v("...", "Total item count " + totalItemCount + " " + visibleItemCount);
                    if (mLoadingIndicator.getVisibility() == View.INVISIBLE && (visibleItemCount + firstVisibleItem) >= totalItemCount) {
                        //Do pagination.. i.e. fetch new data
                        String nextPage = String.valueOf(totalItemCount / 20 + 1);
                        Log.v(">>>", "Getting page number: " + nextPage);

                        String selectedValue = mSpinner.getSelectedItem().toString();
                        if (selectedValue.equals(getResources().getString(R.string.popular_value))) {
                            loadMoviesData(getResources().getString(R.string.popular_label), nextPage);
                        } else if (selectedValue.equals(getResources().getString(R.string.top_rated_value))) {
                            loadMoviesData(getResources().getString(R.string.top_rated_label), nextPage);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.favourites_value))) {
            loadMoviesData(getResources().getString(R.string.favourites_label), "1");
        }
    }

    private void loadMoviesData(String queryType, String page) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_TYPE, queryType);
        queryBundle.putString(PAGE_NUM, page);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<ArrayList<MovieEntry>> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (page.equals("1")) {
            //reset movies upon changing category
            mMovieAdapter.setMovieData(null);
        }
        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER_ID, queryBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(MOVIE_LOADER_ID, queryBundle, this).forceLoad();
        }
    }

    @Override
    public void OnClick(MovieEntry entry) {
        Context context = MainActivity.this;
        Class destinationClass = DetailActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(MovieEntry.class.getSimpleName(), entry);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<MovieEntry>> onCreateLoader(int id, Bundle args) {
        String queryType = args.getString(QUERY_TYPE);
        String pageNumber = args.getString(PAGE_NUM);
        Log.v(LOG_TAG, "OnCreateLoader called");
        return new FetchMoviesTask(this, queryType, pageNumber);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieEntry>> loader, ArrayList<MovieEntry> movieData) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieData != null) {
            mMovieAdapter.setMovieData(movieData);
        } else {
            Toast.makeText(getApplicationContext(), R.string.fetch_error_message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieEntry>> loader) {
        Log.v(LOG_TAG, "OnLoaderReset called");
    }
}
