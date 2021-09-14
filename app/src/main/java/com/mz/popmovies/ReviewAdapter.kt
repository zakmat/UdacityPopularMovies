package com.mz.popmovies

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.mz.popmovies.ReviewAdapter.ReviewViewHolder
import android.view.ViewGroup
import com.mz.popmovies.R
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.databinding.ReviewDetailsBinding
import java.util.*

/**
 * Created by mz on 2017-02-04.
 */
class ReviewAdapter : RecyclerView.Adapter<ReviewViewHolder>() {
    private var mReviews: ArrayList<ReviewEntry>? = null
    lateinit var binding: ReviewDetailsBinding
    fun setReviewData(reviewData: Array<ReviewEntry>?) {
        Log.i(LOG_TAG, "before setMovieData there is $itemCount elements in a view")
        if (reviewData == null) {
            mReviews = null
        } else if (mReviews == null) {
            mReviews = ArrayList(reviewData.asList())
        } else {
            mReviews!!.addAll(reviewData.asList())
        }
        notifyDataSetChanged()
        Log.i(LOG_TAG, "after setMovieData there is $itemCount elements in a view")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ReviewDetailsBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return if (mReviews == null) {
            0
        } else {
            mReviews!!.size
        }
    }

    inner class ReviewViewHolder(rootView: View?) : RecyclerView.ViewHolder(rootView!!) {
        private val LOG_TAG = ReviewViewHolder::class.java.simpleName

        fun bind(position: Int) {
            if (mReviews == null) {
                Log.d(LOG_TAG, "Called bind with null mReviews")
                return
            }
            val entry = mReviews!![position]
            binding.reviewAuthor.text = entry.author
            binding.reviewContent.text = entry.content
        }
    }

    companion object {
        private val LOG_TAG = ReviewAdapter::class.java.simpleName
    }
}