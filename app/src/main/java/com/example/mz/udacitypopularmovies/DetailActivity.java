package com.example.mz.udacitypopularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mz.udacitypopularmovies.data.MovieContract;
import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.data.ReviewEntry;
import com.example.mz.udacitypopularmovies.data.TrailerEntry;
import com.example.mz.udacitypopularmovies.utilities.FetchTask;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    @BindView(R.id.movie_title)
    TextView mTitleTextView;
    @BindView(R.id.movie_release_date)
    TextView mReleaseDateTextView;
    @BindView(R.id.movie_vote_average)
    TextView mVoteAverageTextView;
    @BindView(R.id.movie_plot_synopsis)
    TextView mPlotSynopsisTextView;
    @BindView(R.id.movie_poster)
    ImageView mImageView;
    @BindView(R.id.pb_review_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.fab_favourite)
    FloatingActionButton mFavourite;
    @BindView(R.id.rv_reviews)
    RecyclerView rv_reviews;
    @BindView(R.id.rv_trailers)
    RecyclerView rv_trailers;

    private MovieEntry mMovie;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        GridLayoutManager gridLayout = new GridLayoutManager(this, 1);
        rv_reviews.setLayoutManager(gridLayout);
        reviewAdapter = new ReviewAdapter();
        rv_reviews.setAdapter(reviewAdapter);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_trailers.setLayoutManager(horizontalLayoutManager);
        trailerAdapter = new TrailerAdapter();
        rv_trailers.setAdapter(trailerAdapter);

        Intent incomingIntent = getIntent();
        String extraName = MovieEntry.class.getSimpleName();
        if (incomingIntent.hasExtra(extraName)) {
            mMovie = incomingIntent.getParcelableExtra(extraName);
            fillMovieDetails(mMovie);
            loadReviewsData();
            loadTrailersData();
        }
    }

    private void fillMovieDetails(MovieEntry incomingEntry) {

        mTitleTextView.setText(incomingEntry.title);
        Context context = DetailActivity.this;
        mReleaseDateTextView.setText(DateFormat.format("MMM d, yyyy", incomingEntry.releaseDate));
        mVoteAverageTextView.setText("TMDb: " + Double.valueOf(incomingEntry.voteAverage).toString() + "/10");
        setStarRating(incomingEntry.voteAverage);
        mPlotSynopsisTextView.setText(incomingEntry.overview);
        Uri poster = NetworkUtils.buildPosterRequest(342, incomingEntry.posterPath);
        Picasso.with(context).load(poster).into(mImageView);
        mFavourite.setSelected(isFavouriteMovie(incomingEntry));
    }

    private void setStarRating(double voteAverage) {
        long rating = Math.round(voteAverage);
        if (rating == 1)
            ((ImageView) findViewById(R.id.movie_rating_star1)).setImageResource(R.drawable.star_half_24dp);
        if (rating >= 2)
            ((ImageView) findViewById(R.id.movie_rating_star1)).setImageResource(R.drawable.star_full_24dp);
        if (rating == 3)
            ((ImageView) findViewById(R.id.movie_rating_star2)).setImageResource(R.drawable.star_half_24dp);
        if (rating >= 4)
            ((ImageView) findViewById(R.id.movie_rating_star2)).setImageResource(R.drawable.star_full_24dp);
        if (rating == 5)
            ((ImageView) findViewById(R.id.movie_rating_star3)).setImageResource(R.drawable.star_half_24dp);
        if (rating >= 6)
            ((ImageView) findViewById(R.id.movie_rating_star3)).setImageResource(R.drawable.star_full_24dp);
        if (rating == 7)
            ((ImageView) findViewById(R.id.movie_rating_star4)).setImageResource(R.drawable.star_half_24dp);
        if (rating >= 8)
            ((ImageView) findViewById(R.id.movie_rating_star4)).setImageResource(R.drawable.star_full_24dp);
        if (rating == 9)
            ((ImageView) findViewById(R.id.movie_rating_star5)).setImageResource(R.drawable.star_half_24dp);
        if (rating >= 10)
            ((ImageView) findViewById(R.id.movie_rating_star5)).setImageResource(R.drawable.star_full_24dp);
    }

    private ContentValues prepareContentValues(MovieEntry incomingEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, incomingEntry.movie_id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, incomingEntry.overview);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, incomingEntry.posterPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, incomingEntry.releaseDate.getTime());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, incomingEntry.title);
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, incomingEntry.voteAverage);
        Log.v(LOG_TAG, "Vote Average of saved movie: " + incomingEntry.voteAverage + " from Content value: " + contentValues.getAsDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));

        return contentValues;
    }

    public void onClickFavorite(View view) {
        if (isFavouriteMovie(mMovie)) {
            removeFromFavourites(mMovie);
        } else {
            addToFavourites(mMovie);
        }

    }

    private boolean isFavouriteMovie(MovieEntry mMovie) {
        Uri uriForMovie = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovie.movie_id)).build();
        Log.v(LOG_TAG, "Uri for querying CP for specific movie: " + uriForMovie.toString());
        Cursor cursor = getContentResolver().query(uriForMovie, null, null, null, null);
        return cursor.getCount() > 0;
    }

    private void removeFromFavourites(MovieEntry mMovie) {
        Uri uriForMovie = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(mMovie.movie_id)).build();
        String[] selArgs = {Integer.toString(mMovie.movie_id)};
        int numOfDeletedRows = getContentResolver().delete(uriForMovie, null, null);
        Log.v(LOG_TAG, "Removed movie: \"" + mMovie.title + "\" from db (" + numOfDeletedRows + " occurences)");
        if (numOfDeletedRows > 0) {
            Toast.makeText(getBaseContext(), "Movie removed from favourites", Toast.LENGTH_LONG).show();
            mFavourite.setSelected(false);
        } else {
            Toast.makeText(getBaseContext(), "Removing from favourites failed", Toast.LENGTH_LONG).show();
        }
    }

    private void addToFavourites(MovieEntry mMovie) {
        ContentValues cv = prepareContentValues(mMovie);
        ContentValues[] values = new ContentValues[]{cv};
        if (getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values) > 0) {
            Toast.makeText(getBaseContext(), "Movie added to the favourites", Toast.LENGTH_LONG).show();
            mFavourite.setSelected(true);
        } else {
            Toast.makeText(getBaseContext(), "Adding to favourites failed", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Insert into database failed");
        }
    }

    public void showLoadingIndicator(boolean visible) {
        mLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public int getMovieId() {
        return mMovie.movie_id;
    }

    private void loadReviewsData() {
        new FetchTask<>(ReviewEntry.class, this).execute();
    }

    private void loadTrailersData() {
        new FetchTask<>(TrailerEntry.class, this).execute();
    }

    public void setEntries(TrailerEntry[] trailers) {
        trailerAdapter.setTrailerData(trailers);
    }

    public void setEntries(ReviewEntry[] reviews) {
        reviewAdapter.setReviewData(reviews);
    }
}
