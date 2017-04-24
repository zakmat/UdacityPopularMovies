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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.Arrays;

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

    private MovieEntry mMovie;
    private ArrayList<TrailerEntry> mTrailers;
    private ArrayList<ReviewEntry> mReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
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
    }

    private void setStarRating(double voteAverage) {
        int starRates = (int) Math.round(voteAverage / 2.0);
        if (starRates > 0)
            ((ImageView) findViewById(R.id.movie_rating_star1)).setImageResource(R.drawable.star_on_24px);
        if (starRates > 1)
            ((ImageView) findViewById(R.id.movie_rating_star2)).setImageResource(R.drawable.star_on_24px);
        if (starRates > 2)
            ((ImageView) findViewById(R.id.movie_rating_star3)).setImageResource(R.drawable.star_on_24px);
        if (starRates > 3)
            ((ImageView) findViewById(R.id.movie_rating_star4)).setImageResource(R.drawable.star_on_24px);
        if (starRates > 4)
            ((ImageView) findViewById(R.id.movie_rating_star5)).setImageResource(R.drawable.star_on_24px);
    }

    private ContentValues prepareContentValues(MovieEntry incomingEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, incomingEntry.movie_id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, incomingEntry.overview);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTERPATH, incomingEntry.posterPath);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, incomingEntry.releaseDate.getTime());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, incomingEntry.title);
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, incomingEntry.voteAverage);

        return contentValues;
    }


    public void onClickFavorite(View view) {
        ContentValues cv = prepareContentValues(mMovie);
        ContentValues[] values = new ContentValues[]{cv};
        if (getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values) > 0) {
            Toast.makeText(getBaseContext(), "Movie added to the favourites", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "Insert into database failed", Toast.LENGTH_LONG).show();
            Log.w("DETAIL_ACTIVITY", "Insert into database failed");
        }
    }

    public void showLoadingIndicator(boolean visible) {
        mLoadingIndicator.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public int getMovieId() {
        return mMovie.movie_id;
    }

    public View inflate(Context context, View convertView, ViewGroup parent, Object entry) {
        if (entry instanceof ReviewEntry) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.review_details, parent, false);

                // Lookup view for data population
                TextView tvAuthor = (TextView) convertView.findViewById(R.id.review_author);
                TextView tvContent = (TextView) convertView.findViewById(R.id.review_content);
                // Populate the data into the template view using the data object
                ReviewEntry review = (ReviewEntry) entry;
                tvAuthor.setText(review.author);
                tvContent.setText(review.content);
            }
        } else if (entry instanceof TrailerEntry) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.trailer_details, parent, false);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TrailerEntry trailer = (TrailerEntry) v.getTag();

                        Uri builtUri = Uri.parse("http://www.youtube.com").buildUpon().appendPath("watch")
                                .appendQueryParameter("v", trailer.key).build();
                        Log.i("DetailActivity", builtUri.toString());
                        startActivity(new Intent(Intent.ACTION_VIEW, builtUri));
                    }
                });
            }
            TextView tvName = (TextView) convertView.findViewById(R.id.trailer_name);
            // Populate the data into the template view using the data object
            TrailerEntry trailer = (TrailerEntry) entry;
            tvName.setText(trailer.name);
        }
        convertView.setTag(entry);
        return convertView;
    }

    private void loadReviewsData() {
        new FetchTask<>(ReviewEntry.class, this).execute();
    }

    private void loadTrailersData() {
        new FetchTask<>(TrailerEntry.class, this).execute();
    }

    public void setEntries(TrailerEntry[] trailers) {
        mTrailers = new ArrayList<TrailerEntry>(Arrays.asList(trailers));
        LinearLayout trailerContainer = (LinearLayout) findViewById(R.id.trailer_container);
        for (TrailerEntry trailer : trailers) {
            View convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.trailer_details, trailerContainer, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrailerEntry trailer = (TrailerEntry) v.getTag();

                    Uri builtUri = Uri.parse("http://www.youtube.com").buildUpon().appendPath("watch")
                            .appendQueryParameter("v", trailer.key).build();
                    Log.i("DetailActivity", builtUri.toString());
                    startActivity(new Intent(Intent.ACTION_VIEW, builtUri));
                }
            });
            TextView tvName = (TextView) convertView.findViewById(R.id.trailer_name);
            // Populate the data into the template view using the data object
            tvName.setText(trailer.name);
            convertView.setTag(trailer);
            trailerContainer.addView(convertView);
        }

    }


    public void setEntries(ReviewEntry[] reviews) {
        mReviews = new ArrayList<ReviewEntry>(Arrays.asList(reviews));
        ViewGroup reviewContainer = (ViewGroup) findViewById(R.id.review_container);
        for (ReviewEntry review : reviews) {
            View convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.review_details, reviewContainer, false);
            // Lookup view for data population
            TextView tvAuthor = (TextView) convertView.findViewById(R.id.review_author);
            TextView tvContent = (TextView) convertView.findViewById(R.id.review_content);
            tvAuthor.setText(review.author);
            tvContent.setText(review.content);
            convertView.setTag(review);
            reviewContainer.addView(convertView);
        }

    }
}
