package com.mz.popmovies

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mz.popmovies.data.MovieContract
import com.mz.popmovies.data.MovieEntry
import com.mz.popmovies.data.ReviewEntry
import com.mz.popmovies.data.TrailerEntry
import com.mz.popmovies.databinding.ActivityDetailBinding
import com.mz.popmovies.utilities.FetchTask
import com.mz.popmovies.utilities.NetworkUtils
import com.squareup.picasso.Picasso
import kotlin.math.roundToLong

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
//    var mTitleTextView: TextView? = null

//    @JvmField
//    @BindView(R.id.movie_release_date)
//    var mReleaseDateTextView: TextView? = null
//
//    @JvmField
//    @BindView(R.id.movie_vote_average)
//    var mVoteAverageTextView: TextView? = null
//
//    @JvmField
//    @BindView(R.id.movie_plot_synopsis)
//    var mPlotSynopsisTextView: TextView? = null
//
//    @JvmField
//    @BindView(R.id.movie_poster)
//    var mImageView: ImageView? = null
//
//    @JvmField
//    @BindView(R.id.pb_review_loading_indicator)
//    var mLoadingIndicator: ProgressBar? = null

//    @JvmField
//    @BindView(R.id.fab_favourite)
//    var mFavourite: FloatingActionButton? = null

//    @JvmField
//    @BindView(R.id.rv_reviews)
//    var rv_reviews: RecyclerView? = null

    //    @JvmField
//    @BindView(R.id.rv_trailers)
//    var rv_trailers: RecyclerView? = null
    private lateinit var mMovie: MovieEntry
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var trailerAdapter: TrailerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        reviewAdapter = ReviewAdapter()
        binding.rvReviews.adapter = reviewAdapter
        val gridLayout = GridLayoutManager(this, 1)
        binding.rvReviews.layoutManager = gridLayout
        val horizontalLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTrailers.layoutManager = horizontalLayoutManager
        trailerAdapter = TrailerAdapter()
        binding.rvTrailers.adapter = trailerAdapter
        val incomingIntent = intent
        if (incomingIntent.hasExtra("MovieEntry")) {
            mMovie = incomingIntent.getParcelableExtra("MovieEntry")!!
            Log.i("Detail activity", "Received intent for ${mMovie.title}")
            fillMovieDetails(mMovie)
            loadReviewsData()
            loadTrailersData()
        }
    }

    private fun fillMovieDetails(incomingEntry: MovieEntry) {
        binding.movieTitle.text = incomingEntry.title
        binding.movieReleaseDate.text = DateFormat.format("MMM d, yyyy", incomingEntry.releaseDate)
        binding.movieVoteAverage.text = "TMDb: ${incomingEntry.voteAverage}/10"
        setStarRating(incomingEntry.voteAverage)
        binding.moviePlotSynopsis.text = incomingEntry.overview
        val poster = NetworkUtils.buildPosterRequest(342, incomingEntry.posterPath)

//        val newPoster = "https://image.tmdb.org/t/p/w342//bOFaAXmWWXC3Rbv4u4uM9ZSzRXP.jpg"
        Picasso.get().load(poster).placeholder(R.drawable.favourite_24px).into(binding.moviePoster)
//        mFavourite!!.isSelected = isFavouriteMovie(incomingEntry)
    }

    private fun setStarRating(voteAverage: Double) {
        val rating = voteAverage.roundToLong()
        if (rating == 1L) (findViewById<View>(R.id.movie_rating_star1) as ImageView).setImageResource(
            R.drawable.star_half_24dp
        )
        if (rating >= 2) (findViewById<View>(R.id.movie_rating_star1) as ImageView).setImageResource(
            R.drawable.star_full_24dp
        )
        if (rating == 3L) (findViewById<View>(R.id.movie_rating_star2) as ImageView).setImageResource(
            R.drawable.star_half_24dp
        )
        if (rating >= 4) (findViewById<View>(R.id.movie_rating_star2) as ImageView).setImageResource(
            R.drawable.star_full_24dp
        )
        if (rating == 5L) (findViewById<View>(R.id.movie_rating_star3) as ImageView).setImageResource(
            R.drawable.star_half_24dp
        )
        if (rating >= 6) (findViewById<View>(R.id.movie_rating_star3) as ImageView).setImageResource(
            R.drawable.star_full_24dp
        )
        if (rating == 7L) (findViewById<View>(R.id.movie_rating_star4) as ImageView).setImageResource(
            R.drawable.star_half_24dp
        )
        if (rating >= 8) (findViewById<View>(R.id.movie_rating_star4) as ImageView).setImageResource(
            R.drawable.star_full_24dp
        )
        if (rating == 9L) (findViewById<View>(R.id.movie_rating_star5) as ImageView).setImageResource(
            R.drawable.star_half_24dp
        )
        if (rating >= 10) (findViewById<View>(R.id.movie_rating_star5) as ImageView).setImageResource(
            R.drawable.star_full_24dp
        )
    }

    private fun prepareContentValues(incomingEntry: MovieEntry?): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, incomingEntry!!.movie_id)
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, incomingEntry.overview)
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, incomingEntry.posterPath)
        contentValues.put(
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            incomingEntry.releaseDate?.time
        )
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, incomingEntry.title)
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, incomingEntry.voteAverage)
        Log.v(
            LOG_TAG,
            "Vote Average of saved movie: " + incomingEntry.voteAverage + " from Content value: " + contentValues.getAsDouble(
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
            )
        )
        return contentValues
    }

    fun onClickFavorite(view: View?) {
        if (isFavouriteMovie(mMovie)) {
            removeFromFavourites(mMovie)
        } else {
            addToFavourites(mMovie)
        }
    }

    private fun isFavouriteMovie(mMovie: MovieEntry): Boolean {
        val uriForMovie = MovieContract.MovieEntry.CONTENT_URI.buildUpon()
            .appendPath(mMovie.movie_id.toString()).build()
        Log.v(LOG_TAG, "Uri for querying CP for specific movie: $uriForMovie")
        val cursor = contentResolver.query(uriForMovie, null, null, null, null)
        return cursor!!.count > 0
    }

    private fun removeFromFavourites(mMovie: MovieEntry) {
        val uriForMovie = MovieContract.MovieEntry.CONTENT_URI.buildUpon()
            .appendPath(mMovie.movie_id.toString()).build()
        val selArgs = arrayOf(mMovie.movie_id.toString())
        val numOfDeletedRows = contentResolver.delete(uriForMovie, null, null)
        Log.v(
            LOG_TAG,
            "Removed movie: \"" + mMovie.title + "\" from db (" + numOfDeletedRows + " occurences)"
        )
        if (numOfDeletedRows > 0) {
            Toast.makeText(baseContext, "Movie removed from favourites", Toast.LENGTH_LONG).show()
//            mFavourite!!.isSelected = false
        } else {
            Toast.makeText(baseContext, "Removing from favourites failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun addToFavourites(mMovie: MovieEntry) {
        val cv = prepareContentValues(mMovie)
        val values = arrayOf(cv)
        if (contentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values) > 0) {
            Toast.makeText(baseContext, "Movie added to the favourites", Toast.LENGTH_LONG).show()
//            mFavourite!!.isSelected = true
        } else {
            Toast.makeText(baseContext, "Adding to favourites failed", Toast.LENGTH_LONG).show()
            Log.e(LOG_TAG, "Insert into database failed")
        }
    }

    fun showLoadingIndicator(visible: Boolean) {
        binding.pbReviewLoadingIndicator.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    val movieId: Int
        get() = mMovie.movie_id

    private fun loadReviewsData() {
        FetchTask(ReviewEntry::class.java, this).execute()
    }

    private fun loadTrailersData() {
        FetchTask(TrailerEntry::class.java, this).execute()
    }

    fun setEntries(trailers: Array<TrailerEntry>?) {
        trailerAdapter.setTrailerData(trailers)
    }

    fun setEntries(reviews: Array<ReviewEntry>?) {
        reviewAdapter.setReviewData(reviews)
    }

    companion object {
        private val LOG_TAG = DetailActivity::class.java.simpleName
    }
}