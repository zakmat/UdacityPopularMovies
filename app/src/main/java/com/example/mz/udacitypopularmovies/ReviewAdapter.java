package com.example.mz.udacitypopularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mz.udacitypopularmovies.data.ReviewEntry;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mz on 2017-02-04.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private ArrayList<ReviewEntry> mReviews;

    public ReviewAdapter() {
    }

    public void setReviewData(ReviewEntry[] reviewData) {
        Log.i(LOG_TAG, "before setMovieData there is " + getItemCount() + " elements in a view");
        if (reviewData == null) {
            mReviews = null;
        } else if (mReviews == null){
            mReviews = new ArrayList<>(Arrays.asList(reviewData));
        }
        else {
           mReviews.addAll(Arrays.asList(reviewData));
        }
        notifyDataSetChanged();
        Log.i(LOG_TAG, "after setMovieData there is " + getItemCount() + " elements in a view");
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.review_details;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForGridItem, parent, shouldAttachToParentImmediately);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        } else {
            return mReviews.size();
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final String LOG_TAG = ReviewViewHolder.class.getSimpleName();
        @BindView(R.id.review_author)
        TextView tv_reviewAuthor;
        @BindView(R.id.review_content)
        TextView tv_reviewContent;

        public ReviewViewHolder(View rootView) {
            super(rootView);
            ButterKnife.bind(this, rootView);
        }


        public void bind(int position) {
            if (mReviews == null) {
                Log.d(LOG_TAG, "Called bind with null mReviews");
                return;
            }
            ReviewEntry entry = mReviews.get(position);
            tv_reviewAuthor.setText(entry.author);
            tv_reviewContent.setText(entry.content);
        }
    }
}
