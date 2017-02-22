package com.example.mz.udacitypopularmovies.utilities;


import android.content.Context;
import android.util.Log;

import com.example.mz.udacitypopularmovies.DetailActivity;
import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.data.ReviewEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by mz on 2017-02-04.
 */

/**
 * Utility functions to handle themovieDB JSON data.
 */
public final class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName().toString();

    static final String POSTER = "poster_path";
    static final String MOVIE_ID = "id";
    static final String TITLE = "title";
    static final String OVERVIEW = "overview";
    static final String RELEASE_DATE = "release_date";
    static final String RATING = "vote_average";
    static final String AUTHOR = "author";
    static final String CONTENT = "content";

    static final String STATUS_CODE = "status_code";
    static final String RESULTS = "results";

    /**
     * @param movieJsonStr JSON response from server
     * @return Array of Strings describing movie data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static MovieEntry[] getFullMovieDataFromJson(Context context, String movieJsonStr)
            throws JSONException {
        JSONObject movieJson = new JSONObject(movieJsonStr);
        /* Is there an error? */
        if (movieJson.has(STATUS_CODE)) {
            int errorCode = movieJson.getInt(STATUS_CODE);
            if (errorCode != HttpURLConnection.HTTP_OK) {
                handleBadStatus(errorCode);
                return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(RESULTS);
        MovieEntry[]  parsedMoviesData = new MovieEntry[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movie = movieArray.getJSONObject(i);

            int movie_id = movie.getInt(MOVIE_ID);
            String title = movie.getString(TITLE);
            String overview = movie.getString(OVERVIEW);
            String poster = movie.getString(POSTER);
            String dateStr = movie.getString(RELEASE_DATE);
            Date releaseDate = null;
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                releaseDate = df.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            double votesAverage = movie.getDouble(RATING);

            parsedMoviesData[i] = new MovieEntry(movie_id, title, overview, poster, releaseDate, votesAverage);
            Log.i(TAG, title + " " + releaseDate.toString() + " " + poster);
        }

        return parsedMoviesData;
    }

    public static ReviewEntry[] getReviewsDataFromJson(DetailActivity detailActivity, String jsonReviewsResponse)
            throws JSONException {
        JSONObject reviewJson = new JSONObject(jsonReviewsResponse);

        /* Is there an error? */
        if (reviewJson.has(STATUS_CODE)) {
            int errorCode = reviewJson.getInt(STATUS_CODE);
            if (errorCode != HttpURLConnection.HTTP_OK) {
                handleBadStatus(errorCode);
                return null;
            }
        }

        JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

        ReviewEntry[] parsedReviewsData = new ReviewEntry[reviewArray.length()];

        for (int i = 0; i < reviewArray.length(); i++) {
            JSONObject review = reviewArray.getJSONObject(i);

            String review_id = review.getString(MOVIE_ID);
            String author = review.getString(AUTHOR);
            String content = review.getString(CONTENT);

            parsedReviewsData[i] = new ReviewEntry(review_id, author, content);
            Log.i("JsonUtils", author);
        }

        return parsedReviewsData;
    }

    private static void handleBadStatus(int errorCode) {
        switch (errorCode) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                Log.e(TAG, "Not authorized to use API. Probably api_key missing or invalid");
                return;
            case HttpURLConnection.HTTP_NOT_FOUND:
                Log.e(TAG, "HTTP not found. Check code for typos or errors");
                return;
            default:
                Log.e(TAG, "HTTP error: " + errorCode);
                return;
        }
    }
}