package com.example.mz.udacitypopularmovies.utilities;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mz.udacitypopularmovies.DetailActivity;
import com.example.mz.udacitypopularmovies.R;

import java.net.URL;

/**
 * Created by mateusz.zak on 08.04.2017.
 */

public class FetchTask<CustomEntry> extends AsyncTask<Void, Void, CustomEntry[]> {
    private final Class<CustomEntry> entryClass;

    private DetailActivity detailActivity;
    private CustomAdapter<CustomEntry> adapter;
    private final String LOG_TAG = FetchTask.class.getSimpleName();

    public FetchTask(Class<CustomEntry> klass, DetailActivity detailActivity, CustomAdapter adapter) {
        this.entryClass = klass;
        this.detailActivity = detailActivity;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        detailActivity.showLoadingIndicator(true);
    }

    @Override
    protected CustomEntry[] doInBackground(Void... params) {
        URL networkRequest = NetworkUtils.buildMovieRequest(this.entryClass, detailActivity.getMovieId());

        try {
            String networkResponse = NetworkUtils
                    .getResponseFromHttpUrl(networkRequest);

            CustomEntry[] entries = JsonUtils.getEntriesFromJson(this.entryClass, detailActivity, networkResponse);
            Log.i(LOG_TAG, "Retrieved " + networkResponse.length() + " bytes of data that were parsed as " + entries.length + " entries");

            return entries;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(CustomEntry[] reviewData) {
        detailActivity.showLoadingIndicator(false);
        if (reviewData != null) {
            adapter.setData(reviewData);
        } else {
            Toast.makeText(detailActivity.getApplicationContext(), R.string.fetch_error_message, Toast.LENGTH_LONG).show();
        }
    }
}
