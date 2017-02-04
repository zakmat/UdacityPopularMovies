package com.example.mz.udacitypopularmovies.utilities;


import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.mz.udacitypopularmovies.data.MovieEntry;

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

    /**
     *
     * @param movieJsonStr JSON response from server
     *
     * @return Array of Strings describing movie data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static MovieEntry[] getFullMovieDataFromJson(Context context, String movieJsonStr)
            throws JSONException {

        /* Weather information. Each day's forecast info is an element of the "list" array */
        final String RESULTS = "results";


        final String POSTER = "poster_path";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String RATING = "vote_average";

        final String STATUS_CODE = "status_code";

        /* String array to hold each day's weather String */
        MovieEntry[] parsedMoviesData = null;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        /* Is there an error? */
        if (movieJson.has(STATUS_CODE)) {
            int errorCode = movieJson.getInt(STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    Log.e("JsonUtils", "Not authorized to use API. Probably api_key missing or invalid");
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray movieArray = movieJson.getJSONArray(RESULTS);

        parsedMoviesData = new MovieEntry[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {

            /* These are the values that will be collected */
            String title;
            String overview;
            String poster;
            Date releaseDate = null;
            double votesAverage;

            JSONObject movie = movieArray.getJSONObject(i);

            title = movie.getString(TITLE);
            overview = movie.getString(OVERVIEW);
            poster = movie.getString(POSTER);
            String dateStr = movie.getString(RELEASE_DATE);
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                releaseDate = df.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            votesAverage = movie.getDouble(RATING);

            parsedMoviesData[i] = new MovieEntry(title, overview, poster, releaseDate, votesAverage);
            Log.i("JsonUtils", title + " " + releaseDate.toString() + " " + poster);
        }

        return parsedMoviesData;
    }

    /**
     * Parse the JSON and convert it into ContentValues that can be inserted into our database.
     *
     * @param context         An application context, such as a service or activity context.
     * @param forecastJsonStr The JSON to parse into ContentValues.
     *
     * @return An array of ContentValues parsed from the JSON.
     */
    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}