package com.mz.popmovies

import android.util.Log
import com.mz.popmovies.utilities.NetworkUtils.buildPosterRequest
import androidx.recyclerview.widget.RecyclerView
import com.mz.popmovies.MovieAdapter.MovieEntryViewHolder
import android.view.ViewGroup
import com.mz.popmovies.R
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.databinding.ThumbWithCaptionBinding
import java.util.ArrayList

/**
 * Created by mz on 2017-02-04.
 */
class MovieAdapter(private val mClickHandler: MovieAdapterOnClickHandler) : RecyclerView.Adapter<MovieEntryViewHolder>() {
    var items: ArrayList<MovieEntry>? = null
        private set

    interface MovieAdapterOnClickHandler {
        fun OnClick(entry: MovieEntry)
    }

    fun setMovieData(movieData: ArrayList<MovieEntry>?) {
        Log.i(LOG_TAG, "before setMovieData there is $itemCount elements in a view")
        if (movieData == null) {
            items = null
        } else if (items == null) {
            items = movieData
        } else {
            items!!.addAll(movieData)
        }
        notifyDataSetChanged()
        Log.i(LOG_TAG, "after setMovieData there is $itemCount elements in a view")
    }

    lateinit var binding: ThumbWithCaptionBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieEntryViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        binding = ThumbWithCaptionBinding.inflate(inflater, parent, false)

        return MovieEntryViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MovieEntryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items?.size?: 0

    inner class MovieEntryViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView), View.OnClickListener {
        private val LOG_TAG = MovieEntryViewHolder::class.java.simpleName

        fun bind(position: Int) {
            if (items == null) {
                Log.d(LOG_TAG, "Called bind with null mMovieEntries")
                return
            }
            val entry = items!![position]
            val poster = buildPosterRequest(MEDIUM_SIZE, entry.posterPath)
            binding.tvTitle.text = entry.title
            val context = super.itemView.context.applicationContext
            Log.i(LOG_TAG, "bind movie: \"" + entry.title + "\"")
            Picasso.get().load(poster).into(binding.ivThumbnail)
            binding.root.setOnClickListener(this)
        }

//        @OnClick
        fun onClick() {
            val adapterPosition = adapterPosition
            Log.e(LOG_TAG, "Clicked on position : $adapterPosition")
            mClickHandler.OnClick(items!![adapterPosition])
        }

        override fun onClick(v: View?) {
            Log.e(LOG_TAG, "Clicked on position : $adapterPosition")
            mClickHandler.OnClick(items!![layoutPosition])
        }
    }

    companion object {
        private val LOG_TAG = MovieAdapter::class.java.simpleName
        private const val MEDIUM_SIZE = 185
    }
}