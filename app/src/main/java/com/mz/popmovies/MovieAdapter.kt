package com.mz.popmovies

import android.util.Log
import com.mz.popmovies.utilities.NetworkUtils.buildPosterRequest
import androidx.recyclerview.widget.RecyclerView
import com.mz.popmovies.MovieAdapter.ViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import coil.load
import com.squareup.picasso.Picasso
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.databinding.ThumbWithCaptionBinding
import java.util.ArrayList

/**
 * Created by mz on 2017-02-04.
 */
class MovieAdapter(private val mClickHandler: MovieAdapterOnClickHandler) : RecyclerView.Adapter<ViewHolder>() {
    var items: ArrayList<MovieEntry> = ArrayList()
        private set

    interface MovieAdapterOnClickHandler {
        fun OnClick(entry: MovieEntry)
    }

    fun setMovieData(movieData: List<MovieEntry>?) {
        Log.i(LOG_TAG, "before setMovieData there is $itemCount elements in a view")
        if (movieData == null) {
            items.clear()
        }
        else {
            items.addAll(movieData)
        }
        notifyDataSetChanged()
        Log.i(LOG_TAG, "after setMovieData there is $itemCount elements in a view")
    }

    lateinit var binding: ThumbWithCaptionBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ThumbWithCaptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = items?.size?: 0

    inner class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        private lateinit var entry: MovieEntry

        fun bind(position: Int) {
            entry = items[position]
            val poster = buildPosterRequest(MEDIUM_SIZE, entry.posterPath)
            binding.tvTitle.text = "${entry.title}[$position]"
            Log.i(LOG_TAG, "bind movie: $position \"" + entry.title + "\"")
            binding.ivThumbnail.load(poster)
            binding.root.setOnClickListener{
                Log.e(LOG_TAG, "Clicked on movie : ${entry.title}")
                mClickHandler.OnClick(entry)
            }
        }
    }

    companion object {
        private const val LOG_TAG = "MovieAdapter"
        private const val MEDIUM_SIZE = 185
    }
}