package com.example.mz.udacitypopularmovies.utilities;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mz.udacitypopularmovies.DetailActivity;
import com.example.mz.udacitypopularmovies.R;
import com.example.mz.udacitypopularmovies.data.ReviewEntry;
import com.example.mz.udacitypopularmovies.data.TrailerEntry;

import java.net.URL;

/**
 * Created by mateusz.zak on 08.04.2017.
 */

public class FetchTask<CustomEntry> extends AsyncTask<Void, Void, CustomEntry[]> {
    private final Class<CustomEntry> entryClass;

    private DetailActivity detailActivity;
    private final String LOG_TAG = FetchTask.class.getSimpleName();

    public FetchTask(Class<CustomEntry> klass, DetailActivity detailActivity) {
        this.entryClass = klass;
        this.detailActivity = detailActivity;
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
    protected void onPostExecute(CustomEntry[] entries) {
        detailActivity.showLoadingIndicator(false);
        if (entries != null) {
            if (entryClass.equals(ReviewEntry.class)) {
                detailActivity.setEntries((ReviewEntry[]) entries);
            } else if (entryClass.equals(TrailerEntry.class)) {
                detailActivity.setEntries((TrailerEntry[]) entries);

            } else {
                Log.v(LOG_TAG, "Invalid class type:  " + entryClass.getSimpleName());

            }
        } else {
            Toast.makeText(detailActivity.getApplicationContext(), R.string.fetch_error_message, Toast.LENGTH_LONG).show();
        }
    }
}
