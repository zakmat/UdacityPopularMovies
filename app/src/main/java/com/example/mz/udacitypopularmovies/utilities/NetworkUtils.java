package com.example.mz.udacitypopularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.mz.udacitypopularmovies.data.ReviewEntry;
import com.example.mz.udacitypopularmovies.data.TrailerEntry;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String DBMOVIE_IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/";
    private static final String DBMOVIE_BASE_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final String DBMOVIE_REVIEW_URL =
            "https://api.themoviedb.org/3/movie/%d/reviews";
    private static final String DBMOVIE_VIDEO_URL =
            "https://api.themoviedb.org/3/movie/%d/videos";

    /* The format we want our API to return */
    private static final String APIKEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String PAGE_PARAM = "page";

    //TODO: Please enter here your api-key
    private static final String apiKey = "";
    private static final String language = "en-US";

    public static Uri buildPosterRequest(Integer requestedSize, String posterPath) {
        Uri builtUri = Uri.parse(DBMOVIE_IMAGE_BASE_URL).buildUpon().appendPath("w" + requestedSize.toString()).appendEncodedPath(posterPath).build();

        Log.v(TAG, "Built URI: " + builtUri.toString());
        return builtUri;
    }

    /**
     * Builds the URL used to retrieve thumbnails jserver using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param queryType The queryType that will be queried for - possible values are "popular"
     * @return The URL to use to query the weather server.
     */
    public static URL buildMovieRequest(String queryType, String page) {
        Uri builtUri = Uri.parse(DBMOVIE_BASE_URL).buildUpon().appendPath(queryType)
                .appendQueryParameter(APIKEY_PARAM, apiKey)
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(PAGE_PARAM, page)
                .build();

        return buildUrlFromURI(builtUri);
    }

    public static URL buildMovieRequest(Class klass, Integer movieId) {
        Log.v(TAG, "Int of movie id:" + movieId.toString());
        String dbMovieUrl;
        if (klass.equals(ReviewEntry.class)) {
            dbMovieUrl = DBMOVIE_REVIEW_URL;
        } else if (klass.equals(TrailerEntry.class)) {
            dbMovieUrl = DBMOVIE_VIDEO_URL;
        } else {
            Log.v(TAG, "No appropriate URL for " + klass.getSimpleName() + " request");
            return null;
        }
        Uri builtUri = Uri.parse(String.format(dbMovieUrl, movieId)).buildUpon()
                .appendQueryParameter(APIKEY_PARAM, apiKey).build();
        return buildUrlFromURI(builtUri);

    }

    private static URL buildUrlFromURI(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URL " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static Uri buildYoutubeRequest(String key) {
        Uri builtUri = Uri.parse("http://www.youtube.com").buildUpon().appendPath("watch")
                .appendQueryParameter("v", key).build();
        Log.i("DetailActivity", builtUri.toString());
        return builtUri;
    }
}
