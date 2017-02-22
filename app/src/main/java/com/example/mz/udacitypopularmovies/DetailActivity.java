package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.data.ReviewEntry;
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
    private int mMovieId;
    private ReviewsAdapter mReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ArrayList<ReviewEntry> reviews = new ArrayList<ReviewEntry>();
        mReviewAdapter = new ReviewsAdapter(DetailActivity.this, reviews);
        ListView listView = (ListView) findViewById(R.id.lv_reviews);
        listView.setAdapter(mReviewAdapter);
        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        mTitleTextView = (TextView) findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.movie_vote_average);
        mPlotSynopsisTextView = (TextView) findViewById(R.id.movie_plot_synopsis);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_review_loading_indicator);
        mImageView = (ImageView) findViewById(R.id.movie_poster);
        Intent incomingIntent = getIntent();
        String extraName = MovieEntry.class.getSimpleName();
        if (incomingIntent.hasExtra(extraName)) {
            MovieEntry incomingEntry = incomingIntent.getParcelableExtra(extraName);
            mMovieId = incomingEntry.movie_id;
            mTitleTextView.setText(incomingEntry.title);
            Context context = DetailActivity.this;
            mReleaseDateTextView.setText(DateFormat.format("MMM d, yyyy", incomingEntry.releaseDate));
            mVoteAverageTextView.setText("TMDb: "+ Double.valueOf(incomingEntry.voteAverage).toString() + "/10");
            mPlotSynopsisTextView.setText(incomingEntry.overview);
            Uri poster = NetworkUtils.buildPosterRequest(new Integer(342), incomingEntry.posterPath);
            Picasso.with(context).load(poster).into(mImageView);
            loadReviewsData();
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

            URL reviewRequest = NetworkUtils.buildMovieReviewsRequest(Integer.valueOf(mMovieId));

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
}
