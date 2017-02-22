package com.example.mz.udacitypopularmovies.data;

import android.graphics.Movie;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by mz on 2017-02-04.
 */

public class MovieEntry implements Parcelable {

    public int movie_id;
    public String title;
    public String overview;
    public String posterPath;
    public Date releaseDate;
    public double voteAverage;

    public MovieEntry(int movie_id, String title, String overview, String poster, Date date, double voteAverage)
    {
        this.movie_id = movie_id;
        this.title = title;
        this.overview = overview;
        this.posterPath = poster;
        this.releaseDate = date;
        this.voteAverage = voteAverage;
    }

    public MovieEntry(Parcel in) {
        this.movie_id = in.readInt();
        this.title = in.readString();
        this.overview = in.readString();
        this.posterPath = in.readString();
        this.releaseDate = new Date(in.readLong());
        this.voteAverage = in.readDouble();
    }

    public static final Parcelable.Creator<MovieEntry> CREATOR
            = new Parcelable.Creator<MovieEntry>() {
        public MovieEntry createFromParcel(Parcel in) {
            return new MovieEntry(in);
        }

        public MovieEntry[] newArray(int size) {
            return new MovieEntry[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movie_id);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.posterPath);
        dest.writeLong(this.releaseDate.getTime());
        dest.writeDouble(this.voteAverage);
    }
}
