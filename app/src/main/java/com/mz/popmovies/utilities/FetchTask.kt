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
class FetchTask<CustomEntry>(private val entryClass: Class<CustomEntry>, private val detailActivity: DetailActivity) : AsyncTask<Void, Void, List<CustomEntry>?>() {
    private val LOG_TAG = FetchTask::class.java.simpleName
    override fun onPreExecute() {
        super.onPreExecute()
        detailActivity.showLoadingIndicator(true)
    }

    protected override fun doInBackground(vararg params: Void): List<CustomEntry>? {
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

    override fun onPostExecute(entries: List<CustomEntry>?) {
        detailActivity.showLoadingIndicator(false)
        if (entries != null) {
            if (entryClass == ReviewEntry::class.java) {
                val reviews = entries?.map {it as ReviewEntry}
                detailActivity.setEntries(reviews.toTypedArray())
            } else if (entryClass == TrailerEntry::class.java) {
                val trailers = entries?.map {it as TrailerEntry}
                detailActivity.setEntries(trailers.toTypedArray())
            } else {
                Log.v(LOG_TAG, "Invalid class type:  " + entryClass.simpleName)
            }
        } else {
            Toast.makeText(detailActivity.applicationContext, R.string.fetch_error_message, Toast.LENGTH_LONG).show()
        }
    }
}