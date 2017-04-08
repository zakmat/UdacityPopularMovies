package com.example.mz.udacitypopularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mz.udacitypopularmovies.data.MovieContract;
import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.data.ReviewEntry;
import com.example.mz.udacitypopularmovies.data.TrailerEntry;
import com.example.mz.udacitypopularmovies.utilities.JsonUtils;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mPlotSynopsisTextView;
    private ImageView mImageView;
    private ProgressBar mLoadingIndicator;
    private String moviePoster;
    private ReviewsAdapter mReviewAdapter;
    private TrailersAdapter mTrailerAdapter;

    private MovieEntry mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ArrayList<ReviewEntry> reviews = new ArrayList<>();
        mReviewAdapter = new ReviewsAdapter(DetailActivity.this, reviews);
        ListView listView = (ListView) findViewById(R.id.lv_reviews);
        listView.setAdapter(mReviewAdapter);

        ArrayList<TrailerEntry> trailers = new ArrayList<>();
        mTrailerAdapter = new TrailersAdapter(DetailActivity.this, trailers);
        ListView trailer_listView = (ListView) findViewById(R.id.lv_videos);
        trailer_listView.setAdapter(mTrailerAdapter);
        mTitleTextView = (TextView) findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.movie_vote_average);
        mPlotSynopsisTextView = (TextView) findViewById(R.id.movie_plot_synopsis);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_review_loading_indicator);
        mImageView = (ImageView) findViewById(R.id.movie_poster);
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
        mPlotSynopsisTextView.setText(incomingEntry.overview);
        moviePoster = incomingEntry.posterPath;
        Uri poster = NetworkUtils.buildPosterRequest(Integer.valueOf(342), incomingEntry.posterPath);
        Picasso.with(context).load(poster).into(mImageView);
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

    public class FetchReviewsTask extends AsyncTask<Void, Void, ReviewEntry[]> {
        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ReviewEntry[] doInBackground(Void... params) {

            URL reviewRequest = NetworkUtils.buildMovieReviewsRequest(Integer.valueOf(mMovie.movie_id));

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequest);

                Log.i(LOG_TAG, "Retrieved " + jsonMovieResponse.length() + " bytes of data");

                ReviewEntry[] reviewData = JsonUtils
                        .getReviewsDataFromJson(DetailActivity.this, jsonMovieResponse);

                return reviewData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ReviewEntry[] reviewData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (reviewData != null) {
                mReviewAdapter.setReviewData(reviewData);
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.fetch_error_message, Toast.LENGTH_LONG).show();
            }
        }
    }


    public class ReviewsAdapter extends ArrayAdapter<ReviewEntry> {
        private final String TAG = ReviewsAdapter.class.getSimpleName().toString();
        ArrayList<ReviewEntry> mReviews;
        public ReviewsAdapter(Context context, ArrayList<ReviewEntry> reviews) {
            super(context, 0, reviews);
            mReviews = reviews;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ReviewEntry review = getItem(position);
            Log.i(TAG, "GetView called for position " + position + " review's author is: " + review.author);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_details, parent, false);
            }
            // Lookup view for data population
            TextView tvAuthor = (TextView) convertView.findViewById(R.id.review_author);
            TextView tvContent = (TextView) convertView.findViewById(R.id.review_content);
            // Populate the data into the template view using the data object
            tvAuthor.setText(review.author);
            tvContent.setText(review.content);
            // Return the completed view to render on screen
            return convertView;
        }

        private void setReviewData(ReviewEntry[] reviewData) {
            Log.i(TAG, "before setReviewData there is " + getCount() + " elements in a view");
            if (reviewData == null) {
                mReviews.clear();
            } else {
                mReviews.addAll(Arrays.asList(reviewData));
            }
            notifyDataSetChanged();
            Log.i(TAG, "after setReviewData there is " + getCount() + " elements in a view");
        }
    }

    private void loadReviewsData() {
        new FetchReviewsTask().execute();
    }

    public class FetchTrailersTask extends AsyncTask<Void, Void, TrailerEntry[]> {
        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected TrailerEntry[] doInBackground(Void... params) {

            URL trailerRequest = NetworkUtils.buildMovieVideosRequest(Integer.valueOf(mMovie.movie_id));

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequest);

                Log.i(LOG_TAG, "Retrieved " + jsonMovieResponse.length() + " bytes of data");

                TrailerEntry[] trailerData = JsonUtils
                        .getTrailersDataFromJson(DetailActivity.this, jsonMovieResponse);

                return trailerData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(TrailerEntry[] trailerData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (trailerData != null) {
                mTrailerAdapter.setTrailerData(trailerData);
            }
            else {
                Toast.makeText(getApplicationContext(), R.string.fetch_error_message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class TrailersAdapter extends ArrayAdapter<TrailerEntry> {
        private final String TAG = TrailersAdapter.class.getSimpleName().toString();
        ArrayList<TrailerEntry> mTrailers;
        public TrailersAdapter(Context context, ArrayList<TrailerEntry> trailers) {
            super(context, 0, trailers);
            mTrailers = trailers;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TrailerEntry trailer = getItem(position);
            Log.i(TAG, "GetView called for position " + position + " trailer's name is: " + trailer.name);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_details, parent, false);
            }
            // Lookup view for data population
            //TODO: put some static image as a trailer icon
            if (moviePoster != null) {
                Uri poster = NetworkUtils.buildPosterRequest(Integer.valueOf(342), moviePoster);
                Context context = parent.getContext();
                Picasso.with(context).load(poster).into(mImageView);
            }
            //TODO: find a way how to use regular youtube thumbnail
            TextView tvName = (TextView) convertView.findViewById(R.id.trailer_name);
            // Populate the data into the template view using the data object
            tvName.setText(trailer.name);
            // Return the completed view to render on screen
            return convertView;
        }

        private void setTrailerData(TrailerEntry[] trailerData) {
            Log.i(TAG, "before setTrailerData there is " + getCount() + " elements in a view");
            if ((trailerData) == null) {
                mTrailers.clear();
            } else {
                mTrailers.addAll(Arrays.asList(trailerData));
            }
            notifyDataSetChanged();
            Log.i(TAG, "after setTrailerData there is " + getCount() + " elements in a view");
        }
    }
        private void loadTrailersData() {
            new FetchTrailersTask().execute();
        }
}
