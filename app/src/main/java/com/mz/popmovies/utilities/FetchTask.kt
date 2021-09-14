package com.mz.popmovies.utilities

import com.mz.popmovies.utilities.NetworkUtils.buildMovieRequest
import com.mz.popmovies.utilities.NetworkUtils.getResponseFromHttpUrl
import com.mz.popmovies.utilities.JsonUtils.getEntriesFromJson
import com.mz.popmovies.DetailActivity
import android.os.AsyncTask
import android.util.Log
import com.mz.popmovies.data.TrailerEntry
import android.widget.Toast
import com.mz.popmovies.R
import com.mz.popmovies.data.ReviewEntry
import java.lang.Exception

/**
 * Created by mateusz.zak on 08.04.2017.
 */
class FetchTask<CustomEntry>(private val entryClass: Class<CustomEntry>, private val detailActivity: DetailActivity) : AsyncTask<Void, Void, Array<CustomEntry>?>() {
    private val LOG_TAG = FetchTask::class.java.simpleName
    override fun onPreExecute() {
        super.onPreExecute()
        detailActivity.showLoadingIndicator(true)
    }

    protected override fun doInBackground(vararg params: Void): Array<CustomEntry>? {
        val networkRequest = buildMovieRequest(entryClass, detailActivity.movieId)
        return try {
            val networkResponse = getResponseFromHttpUrl(networkRequest!!)
            val entries = getEntriesFromJson(entryClass, detailActivity, networkResponse)
            Log.i(LOG_TAG, "Retrieved " + networkResponse!!.length + " bytes of data that were parsed as " + entries!!.size + " entries")
            entries
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(entries: Array<CustomEntry>?) {
        detailActivity.showLoadingIndicator(false)
        if (entries != null) {
            if (entryClass == ReviewEntry::class.java) {
//                detailActivity.setEntries(entries as Array<ReviewEntry>?)
            } else if (entryClass == TrailerEntry::class.java) {
//                detailActivity.setEntries(entries as Array<TrailerEntry>?)
            } else {
                Log.v(LOG_TAG, "Invalid class type:  " + entryClass.simpleName)
            }
        } else {
            Toast.makeText(detailActivity.applicationContext, R.string.fetch_error_message, Toast.LENGTH_LONG).show()
        }
    }
}