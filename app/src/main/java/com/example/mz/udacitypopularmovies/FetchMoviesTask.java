package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.mz.udacitypopularmovies.data.MovieContract;
import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.utilities.JsonUtils;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mateusz.zak on 03.05.2017.
 */
public class FetchMoviesTask extends AsyncTaskLoader<ArrayList<MovieEntry>> {
    private final String mPage;
    private final String mQueryType;
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    public FetchMoviesTask(Context context, String queryType, String page) {
        super(context);
        mQueryType = queryType;
        mPage = page;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        //mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public ArrayList<MovieEntry> loadInBackground() {
        Log.i(LOG_TAG, "QueryType: " + mQueryType + " is compared with " + super.getContext().getResources().getString(R.string.favourites_label));
        if (mQueryType.equals(super.getContext().getResources().getString(R.string.favourites_label))) {

            Cursor cursor = super.getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry.COLUMN_TITLE);

            if (cursor == null) {
                Log.w(LOG_TAG, "Null cursor retrieved");
                return null;
            }
            Log.i(LOG_TAG, "Retrieved non-null cursor with " + cursor.getCount() + " elements");

            ArrayList<MovieEntry> movieData = getDataFromCursor(cursor);
            cursor.close();
            return movieData;
        }
        URL movieRequestUrl = NetworkUtils.buildMovieRequest(mQueryType, mPage);

        try {
            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl);

            Log.i(LOG_TAG, "Retrieved " + jsonMovieResponse.length() + " bytes of data");

            ArrayList<MovieEntry> movieData = JsonUtils
                    .getFullMovieDataFromJson(super.getContext(), jsonMovieResponse);

            return movieData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<MovieEntry> getDataFromCursor(Cursor cursor) {
        if (cursor == null) {
            Log.i(LOG_TAG, "Cursor is null");
            return null;
        }
        ArrayList<MovieEntry> movies = new ArrayList<MovieEntry>(cursor.getCount());
        int index = 0;
        while (cursor.moveToNext()) {

            String movie_id = cursor.getString(1);
            String title = cursor.getString(2);
            String overview = cursor.getString(3);
            String poster = cursor.getString(4);
            Double voteAverage = cursor.getDouble(5);
            Date releaseDate = new Date(cursor.getLong(6));

            MovieEntry movie = new MovieEntry(Integer.valueOf(movie_id), title, overview, poster, releaseDate, voteAverage);
            movies.add(index, movie);
            index++;
        }
        return movies;
    }
}
