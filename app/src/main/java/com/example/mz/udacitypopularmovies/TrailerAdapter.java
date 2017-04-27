package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mz.udacitypopularmovies.data.TrailerEntry;
import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mz on 2017-02-04.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private ArrayList<TrailerEntry> mTrailers;

    public TrailerAdapter() {
    }

    public void setTrailerData(TrailerEntry[] trailerData) {
        Log.i(LOG_TAG, "before setTrailerData there is " + getItemCount() + " elements in a view");
        if (trailerData == null) {
            mTrailers = null;
        } else if (mTrailers == null) {
            mTrailers = new ArrayList<>(Arrays.asList(trailerData));
        } else {
            mTrailers.addAll(Arrays.asList(trailerData));
        }
        notifyDataSetChanged();
        Log.i(LOG_TAG, "after setTrailerData there is " + getItemCount() + " elements in a view");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.trailer_details;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForItem, parent, shouldAttachToParentImmediately);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null) {
            return 0;
        } else {
            return mTrailers.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final String LOG_TAG = ViewHolder.class.getSimpleName();
        @BindView(R.id.trailer_icon)
        ImageView iv_trailer_thumbnail;
        @BindView(R.id.trailer_name)
        TextView tv_trailer_name;

        public ViewHolder(View rootView) {
            super(rootView);
            ButterKnife.bind(this, rootView);
        }


        public void bind(int position) {
            if (mTrailers == null) {
                Log.d(LOG_TAG, "Called bind with null mReviews");
                return;
            }
            TrailerEntry trailer = mTrailers.get(position);

            Uri thumbnailLink = NetworkUtils.buildYoutubeThumbnail(trailer.key);
            Context context = super.itemView.getContext().getApplicationContext();
            Picasso.with(context).load(thumbnailLink).placeholder(R.mipmap.placeholder).into(iv_trailer_thumbnail);
            tv_trailer_name.setText(trailer.name);
        }

        @OnClick
        void openYoutubeTrailer() {
            int adapterPosition = getAdapterPosition();
            Log.e(LOG_TAG, "Clicked on position : " + adapterPosition);
            TrailerEntry trailer = mTrailers.get(adapterPosition);
            if (trailer == null) {
                return;
            }
            Uri youtubeMovie = NetworkUtils.buildYoutubeRequest(trailer.key);
            super.itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, youtubeMovie));

        }
    }
}
