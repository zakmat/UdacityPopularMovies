package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.graphics.Movie;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mz.udacitypopularmovies.data.MovieEntry;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.addAll;

/**
 * Created by mz on 2017-02-04.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieEntryViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private MovieEntry mMovieEntries[];
    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void OnClick(MovieEntry entry);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public void setMovieData(MovieEntry[] movieData) {
        Log.i(LOG_TAG, "before setMovieData there is " + getItemCount() + " elements in a view");
        if (movieData == null) {
        } else {
            mMovieEntries = movieData;
        }
        notifyDataSetChanged();
        Log.i(LOG_TAG, "after setMovieData there is " + getItemCount() + " elements in a view");
    }

    @Override
    public MovieEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.thumb_with_caption;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForGridItem, parent, shouldAttachToParentImmediately);
        MovieEntryViewHolder viewHolder = new MovieEntryViewHolder(view);
        return viewHolder;
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
            return mMovieEntries.length;
        }
    }

    public class MovieEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final String LOG_TAG = MovieEntryViewHolder.class.getSimpleName();
        TextView movieTitleTextView;
        ImageView posterImageView;

        public MovieEntryViewHolder(View rootView) {
            super(rootView);

            movieTitleTextView = (TextView) rootView.findViewById(R.id.tv_title);
            posterImageView = (ImageView) rootView.findViewById(R.id.iv_thumbnail);
            rootView.setOnClickListener(this);
        }


        public void bind(int position) {
            if (mMovieEntries == null) {
                Log.d(LOG_TAG, "Called bind with null mMovieEntries");
                return;
            }
            MovieEntry entry = mMovieEntries[position];
            Uri poster = NetworkUtils.buildPosterRequest(new Integer(185), entry.posterPath);
            movieTitleTextView.setText(entry.title);
            Context context = super.itemView.getContext();
            Picasso picasso = new Picasso.Builder(context)
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            //Here your log
                            exception.printStackTrace();
                        }
                    })
                    .build();
            Log.i(LOG_TAG, "bind movie: \"" + entry.title + "\"");
            picasso.load(poster).into(posterImageView);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Log.e(LOG_TAG, "Clicked on position : "+ adapterPosition);
            mClickHandler.OnClick(mMovieEntries[adapterPosition]);
        }
    }
}
