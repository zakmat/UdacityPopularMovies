package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
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

/**
 * Created by mz on 2017-02-04.
 */
public class MovieAdapter extends ArrayAdapter<MovieEntry> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context c, List<MovieEntry> movieEntries) {
        super(c, R.layout.thumb_with_caption, movieEntries);
        setNotifyOnChange(true);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieEntry entry = getItem(position);
        Context context = getContext();

        View rootView = LayoutInflater.from(context).inflate(R.layout.thumb_with_caption, parent, false);


        ImageView iconView = (ImageView) rootView.findViewById(R.id.iv_thumbnail);
        Uri builtUri = NetworkUtils.buildPosterRequest(new Integer(185), entry.posterPath);
        Picasso.with(context).load(builtUri).fit().into(iconView);
        TextView titleView = (TextView) rootView.findViewById(R.id.tv_title);
        titleView.setText(entry.title);
        return rootView;
    }

    public void setMovieData(MovieEntry[] movieData) {
        Log.i(LOG_TAG, "before setMovieData there is " +  getCount() + " elements in a view");
        if (movieData == null) {
            clear();
        }
        else {
            addAll(Arrays.asList(movieData));
        }
        Log.i(LOG_TAG, "after setMovieData there is " +  getCount() + " elements in a view");
    }

}
