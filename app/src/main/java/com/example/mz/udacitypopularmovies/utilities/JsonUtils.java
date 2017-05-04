package com.example.mz.udacitypopularmovies.utilities;


import android.content.Context;
import android.util.Log;

import com.example.mz.udacitypopularmovies.DetailActivity;
import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.data.ReviewEntry;
import com.example.mz.udacitypopularmovies.data.TrailerEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
/**
 * Created by mz on 2017-02-04.
 */

/**
 * Utility functions to handle themovieDB JSON data.
 */
public final class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    private static final String POSTER = "poster_path";
    private static final String MOVIE_ID = "id";
    private static final String TITLE = "title";
    private static final String OVERVIEW = "overview";
    private static final String RELEASE_DATE = "release_date";
    private static final String RATING = "vote_average";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";

    private static final String STATUS_CODE = "status_code";
    private static final String RESULTS = "results";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String SITE = "site";

    /**
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing movie data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<MovieEntry> getFullMovieDataFromJson(Context context, String movieJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String RESULTS = "results";


        final String MOVIE_ID = "id";
        final String POSTER = "poster_path";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";

        final String STATUS_CODE = "status_code";

        /* String array to hold each day's weather String */

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
        ArrayList<MovieEntry> parsedMoviesData = new ArrayList<MovieEntry>(movieArray.length());

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

            parsedMoviesData.add(new MovieEntry(movie_id, title, overview, poster, releaseDate, votesAverage));
            String relDateString = releaseDate != null ? releaseDate.toString() : "????-??-??";
            Log.i(TAG, title + " " + relDateString + " " + poster);
        }

        return parsedMoviesData;
    }

    public static <CustomEntry> CustomEntry[] getEntriesFromJson(Class<CustomEntry> klass, DetailActivity detailActivity, String jsonRequest) throws JSONException {
        if (klass.equals(ReviewEntry.class)) {
            return (CustomEntry[]) getReviewsDataFromJson(detailActivity, jsonRequest);
        } else if (klass.equals(TrailerEntry.class)) {
            return (CustomEntry[]) getTrailersDataFromJson(detailActivity, jsonRequest);
        }

        Log.i(TAG, "No known JSON parser for class " + klass.getSimpleName());
        return null;
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

    public static TrailerEntry[] getTrailersDataFromJson(DetailActivity detailActivity, String jsonTrailersResponse)
            throws JSONException {
        JSONObject trailerJson = new JSONObject(jsonTrailersResponse);

        /* Is there an error? */
        if (trailerJson.has(STATUS_CODE)) {
            int errorCode = trailerJson.getInt(STATUS_CODE);
            if (errorCode != HttpURLConnection.HTTP_OK) {
                handleBadStatus(errorCode);
                return null;
            }
        }

        JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);

        TrailerEntry[] parsedTrailersData = new TrailerEntry[trailerArray.length()];

        for (int i = 0; i < trailerArray.length(); i++) {
            JSONObject trailer = trailerArray.getJSONObject(i);

            String trailer_id = trailer.getString(MOVIE_ID);
            String name = trailer.getString(NAME);
            String key = trailer.getString(KEY);
            String site = trailer.getString(SITE);

            parsedTrailersData[i] = new TrailerEntry(trailer_id, name, key, site);
            Log.i("JsonUtils", name + " " + key + " " + site);
        }

        return parsedTrailersData;
    }
    private static void handleBadStatus(int errorCode) {
        switch (errorCode) {
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                Log.e(TAG, "Not authorized to use API. Probably api_key missing or invalid");
                break;
            case HttpURLConnection.HTTP_NOT_FOUND:
                Log.e(TAG, "HTTP not found. Check code for typos or errors");
                break;
            default:
                Log.e(TAG, "HTTP error: " + errorCode);
                break;
        }
    }
}