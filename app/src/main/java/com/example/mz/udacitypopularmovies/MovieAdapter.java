package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mz on 2017-02-04.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieEntryViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final static Integer MEDIUM_SIZE = 185;

    private ArrayList<MovieEntry> mMovieEntries;
    private final MovieAdapterOnClickHandler mClickHandler;

    public ArrayList<MovieEntry> getItems() {
        return mMovieEntries;
    }

    public interface MovieAdapterOnClickHandler {
        void OnClick(MovieEntry entry);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public void setMovieData(ArrayList<MovieEntry> movieData) {
        Log.i(LOG_TAG, "before setMovieData there is " + getItemCount() + " elements in a view");
        if (movieData == null) {
            mMovieEntries = null;
        } else if (mMovieEntries == null) {
            mMovieEntries = movieData;
        }
        else {
            mMovieEntries.addAll(movieData);
        }
        notifyDataSetChanged();
        Log.i(LOG_TAG, "after setMovieData there is " + getItemCount() + " elements in a view");
    }

    @Override
    public MovieEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.thumb_with_caption;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForGridItem, parent, shouldAttachToParentImmediately);
        return new MovieEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieEntryViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mMovieEntries == null) {
            return 0;
        } else {
            return mMovieEntries.size();
        }
    }

    public class MovieEntryViewHolder extends RecyclerView.ViewHolder {
        private final String LOG_TAG = MovieEntryViewHolder.class.getSimpleName();
        @BindView(R.id.tv_title)
        TextView movieTitleTextView;
        @BindView(R.id.iv_thumbnail)
        ImageView posterImageView;

        public MovieEntryViewHolder(View rootView) {
            super(rootView);
            ButterKnife.bind(this, rootView);
        }


        public void bind(int position) {
            if (mMovieEntries == null) {
                Log.d(LOG_TAG, "Called bind with null mMovieEntries");
                return;
            }
            MovieEntry entry = mMovieEntries.get(position);
            Uri poster = NetworkUtils.buildPosterRequest(MEDIUM_SIZE, entry.posterPath);
            movieTitleTextView.setText(entry.title);
            Context context = super.itemView.getContext().getApplicationContext();
            Log.i(LOG_TAG, "bind movie: \"" + entry.title + "\"");
            Picasso.with(context).load(poster).into(posterImageView);
        }

        @OnClick
        public void onClick() {
            int adapterPosition = getAdapterPosition();
            Log.e(LOG_TAG, "Clicked on position : "+ adapterPosition);
            mClickHandler.OnClick(mMovieEntries.get(adapterPosition));
        }
    }
}
