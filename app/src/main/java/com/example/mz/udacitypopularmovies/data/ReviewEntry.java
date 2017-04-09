package com.example.mz.udacitypopularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.mz.udacitypopularmovies.utilities.NetworkUtils;

import java.net.URL;

/**
 * Created by mz on 2017-02-20.
 */

public class ReviewEntry implements Parcelable {

    private String review_id;
    public String author;
    public String content;


    private ReviewEntry(Parcel in) {
        this.review_id = in.readString();
        this.author = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<ReviewEntry> CREATOR
            = new Parcelable.Creator<ReviewEntry>() {
        public ReviewEntry createFromParcel(Parcel in) {
            return new ReviewEntry(in);
        }

        public ReviewEntry[] newArray(int size) {
            return new ReviewEntry[size];
        }
    };

    public ReviewEntry(String review_id, String author, String content) {
        this.review_id = review_id;
        this.author = author;
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.review_id);
        dest.writeString(this.author);
        dest.writeString(this.content);
    }

    static public URL buildRequest(int movie_id) {
        return NetworkUtils.buildMovieRequest(ReviewEntry.class, movie_id);
    }
}
