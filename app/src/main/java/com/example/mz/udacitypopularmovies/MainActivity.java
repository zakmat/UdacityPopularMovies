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
        new FetchMoviesTask(this).execute(queryType, page);
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
}
