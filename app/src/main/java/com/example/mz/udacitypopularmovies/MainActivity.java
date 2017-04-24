package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.mz.udacitypopularmovies.data.MovieContract;
import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.utilities.JsonUtils;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mLoadingIndicator;
    private Toolbar mToolbar;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieAdapter = new MovieAdapter(MainActivity.this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_posters);
        GridLayoutManager gridLayout = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayout);
        mRecyclerView.setAdapter(mMovieAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setSelection(0, false);
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

        loadMoviesData(getResources().getString(R.string.popular_label), "1");
    }

    private void loadMoviesData(String queryType, String page) {
        if (page.equals("1")) {
            mMovieAdapter.setMovieData(null);
        }
        new FetchMoviesTask().execute(queryType, page);
    }

    @Override
    public void OnClick(MovieEntry entry) {
        Context context = MainActivity.this;
        Class destinationClass = DetailActivity.class;
        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(MovieEntry.class.getSimpleName(), entry);
        startActivity(intent);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieEntry[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieEntry[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            String page;
            if (params.length == 1) {
                page = "1";
            } else {
                page = params[1];
            }
            String queryType = params[0];
            Log.i(LOG_TAG, "QueryType: " + queryType + " is compared with " + R.string.favourites_label);
            if (queryType.equals(getResources().getString(R.string.favourites_label))) {

                Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_TITLE);

                if (cursor == null) {
                    Log.w(LOG_TAG, "Null cursor retrieved");
                    return null;
                }
                Log.i(LOG_TAG, "Retrieved non-null cursor with " + cursor.getCount() + " elements");

                MovieEntry[] movieData = getDataFromCursor(cursor);
                cursor.close();
                return movieData;
            }
            URL movieRequestUrl = NetworkUtils.buildMovieRequest(queryType, page);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                Log.i(LOG_TAG, "Retrieved " + jsonMovieResponse.length() + " bytes of data");

                MovieEntry[] movieData = JsonUtils
                        .getFullMovieDataFromJson(MainActivity.this, jsonMovieResponse);

                return movieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private MovieEntry[] getDataFromCursor(Cursor cursor) {
            if (cursor == null) {
                Log.i(LOG_TAG, "Cursor is null");
                return null;
            }
            MovieEntry[] movies = new MovieEntry[cursor.getCount()];
            int index = 0;
            while (cursor.moveToNext()) {

                String movie_id = cursor.getString(1);
                String title = cursor.getString(2);
                String overview = cursor.getString(3);
                String poster = cursor.getString(4);
                Double voteAverage = cursor.getDouble(5);
                Date releaseDate = new Date(cursor.getLong(6));

                MovieEntry movie = new MovieEntry(Integer.valueOf(movie_id), title, overview, poster, releaseDate, voteAverage);
                movies[index++] = movie;
            }
            return movies;
        }

        @Override
        protected void onPostExecute(MovieEntry[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                mMovieAdapter.setMovieData(movieData);
            } else {
                Toast.makeText(getApplicationContext(), R.string.fetch_error_message, Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
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
}
