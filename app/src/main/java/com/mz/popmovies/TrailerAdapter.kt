package com.mz.popmovies

import com.mz.popmovies.utilities.NetworkUtils.buildYoutubeThumbnail
import com.mz.popmovies.utilities.NetworkUtils.buildYoutubeRequest
import androidx.recyclerview.widget.RecyclerView
import com.mz.popmovies.data.TrailerEntry
import android.view.ViewGroup
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import android.content.Intent
import android.util.Log
import android.view.View
import com.mz.popmovies.databinding.TrailerDetailsBinding
import java.util.*

/**
 * Created by mz on 2017-02-04.
 */
class TrailerAdapter : RecyclerView.Adapter<TrailerAdapter.ViewHolder>() {
    private var trailers: ArrayList<TrailerEntry> = ArrayList()
    private lateinit var binding: TrailerDetailsBinding
    fun setTrailerData(trailerData: List<TrailerEntry>) {
        Log.i(LOG_TAG, "before setTrailerData there is $itemCount elements in a view")
        trailers.addAll(trailerData)
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

    override fun getItemCount() = trailers.size

    inner class ViewHolder(rootView: View?) : RecyclerView.ViewHolder(rootView!!) {
        private val LOG_TAG = ViewHolder::class.java.simpleName

        fun bind(position: Int) {
            val trailer = trailers[position]
            val thumbnailLink = buildYoutubeThumbnail(trailer.key)
            Picasso.get().load(thumbnailLink).placeholder(R.mipmap.placeholder).into(binding.trailerIcon)
            binding.trailerName.text = trailer.name
            binding.root.setOnClickListener{
                openYoutubeTrailer()
            }
        }

        fun openYoutubeTrailer() {
            val adapterPosition = adapterPosition
            Log.e(LOG_TAG, "Clicked on position : $adapterPosition")
            val trailer = trailers[adapterPosition]
            val youtubeMovie = buildYoutubeRequest(trailer.key)
            val viewTrailerIntent = Intent(Intent.ACTION_VIEW, youtubeMovie)
            if (viewTrailerIntent.resolveActivity(super.itemView.context.packageManager) != null) {
                super.itemView.context.startActivity(viewTrailerIntent)
            }
            else {
                Log.w(LOG_TAG, "Youtube activity not resolved")
            }
        }
    }

    companion object {
        private val LOG_TAG = TrailerAdapter::class.java.simpleName
    }
}