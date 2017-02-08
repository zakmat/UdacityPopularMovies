package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import android.text.format.DateFormat;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mPlotSynopsisTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.movie_release_date);
        mVoteAverageTextView = (TextView) findViewById(R.id.movie_vote_average);
        mPlotSynopsisTextView = (TextView) findViewById(R.id.movie_plot_synopsis);
        mImageView = (ImageView) findViewById(R.id.movie_poster);
        Intent incomingIntent = getIntent();
        String extraName = MovieEntry.class.getSimpleName();
        if (incomingIntent.hasExtra(extraName)) {
            MovieEntry incomingEntry = incomingIntent.getParcelableExtra(extraName);
            mTitleTextView.setText(incomingEntry.title);
            Context context = DetailActivity.this;
            mReleaseDateTextView.setText(DateFormat.format("MMM d, yyyy", incomingEntry.releaseDate));
            mVoteAverageTextView.setText("TMDb: "+ Double.valueOf(incomingEntry.voteAverage).toString() + "/10");
            mPlotSynopsisTextView.setText(incomingEntry.overview);
            Uri poster = NetworkUtils.buildPosterRequest(new Integer(342), incomingEntry.posterPath);
            Picasso.with(context).load(poster).into(mImageView);
        }
    }
}
