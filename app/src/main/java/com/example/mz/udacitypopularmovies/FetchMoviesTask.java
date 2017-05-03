package com.example.mz.udacitypopularmovies;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mz.udacitypopularmovies.data.MovieContract;
import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.utilities.JsonUtils;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.Date;

/**
 * Created by mateusz.zak on 03.05.2017.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, MovieEntry[]> {
    private MainActivity mainActivity;
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    public FetchMoviesTask(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mainActivity.mLoadingIndicator.setVisibility(View.VISIBLE);
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
        Log.i(LOG_TAG, "QueryType: " + queryType + " is compared with " + mainActivity.getResources().getString(R.string.favourites_label));
        if (queryType.equals(mainActivity.getResources().getString(R.string.favourites_label))) {

            Cursor cursor = mainActivity.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
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
                    .getFullMovieDataFromJson(mainActivity, jsonMovieResponse);

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
        mainActivity.mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieData != null) {
            mainActivity.mMovieAdapter.setMovieData(movieData);
        } else {
            Toast.makeText(mainActivity.getApplicationContext(), R.string.fetch_error_message, Toast.LENGTH_LONG).show();
        }
    }
}
