package com.mz.popmovies

import com.mz.popmovies.utilities.NetworkUtils.buildYoutubeThumbnail
import com.mz.popmovies.utilities.NetworkUtils.buildYoutubeRequest
import androidx.recyclerview.widget.RecyclerView
import com.mz.popmovies.data.TrailerEntry
import android.view.ViewGroup
import com.mz.popmovies.R
import android.view.LayoutInflater
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.mz.popmovies.databinding.TrailerDetailsBinding
import java.util.*

/**
 * Created by mz on 2017-02-04.
 */
class TrailerAdapter : RecyclerView.Adapter<TrailerAdapter.ViewHolder>() {
    private var mTrailers: ArrayList<TrailerEntry>? = null
    private lateinit var binding: TrailerDetailsBinding
    fun setTrailerData(trailerData: Array<TrailerEntry>?) {
        Log.i(LOG_TAG, "before setTrailerData there is $itemCount elements in a view")
        if (trailerData == null) {
            mTrailers = null
        } else if (mTrailers == null) {
            mTrailers = ArrayList(trailerData.asList())
        } else {
            mTrailers!!.addAll(trailerData.asList())
        }
        notifyDataSetChanged()
        Log.i(LOG_TAG, "after setTrailerData there is $itemCount elements in a view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = TrailerDetailsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = mTrailers?.size ?: 0

    inner class ViewHolder(rootView: View?) : RecyclerView.ViewHolder(rootView!!) {
        private val LOG_TAG = ViewHolder::class.java.simpleName

        fun bind(position: Int) {
            if (mTrailers == null) {
                Log.d(LOG_TAG, "Called bind with null mReviews")
                return
            }
            val trailer = mTrailers!![position]
            val thumbnailLink = buildYoutubeThumbnail(trailer.key)
            val context = super.itemView.context.applicationContext
            Picasso.get().load(thumbnailLink).placeholder(R.mipmap.placeholder).into(binding.trailerIcon)
            binding.trailerName.text = trailer.name
        }

//        @OnClick
        fun openYoutubeTrailer() {
            val adapterPosition = adapterPosition
            Log.e(LOG_TAG, "Clicked on position : $adapterPosition")
            val trailer = mTrailers!![adapterPosition] ?: return
            val youtubeMovie = buildYoutubeRequest(trailer.key)
            val viewTrailerIntent = Intent(Intent.ACTION_VIEW, youtubeMovie)
            if (viewTrailerIntent.resolveActivity(super.itemView.context.packageManager) != null) {
                super.itemView.context.startActivity(viewTrailerIntent)
            }
        }
    }

    companion object {
        private val LOG_TAG = TrailerAdapter::class.java.simpleName
    }
}