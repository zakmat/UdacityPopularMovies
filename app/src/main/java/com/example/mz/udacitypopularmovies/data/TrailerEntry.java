package com.example.mz.udacitypopularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mz on 2017-02-20.
 */

public class TrailerEntry implements Parcelable {

    public String trailer_id;
    public String name;
    public String key;
    public String site;


    public TrailerEntry(Parcel in) {
        this.trailer_id = in.readString();
        this.name = in.readString();
        this.key = in.readString();
        this.site = in.readString();
    }

    public static final Creator<TrailerEntry> CREATOR
            = new Creator<TrailerEntry>() {
        public TrailerEntry createFromParcel(Parcel in) {
            return new TrailerEntry(in);
        }

        public TrailerEntry[] newArray(int size) {
            return new TrailerEntry[size];
        }
    };

    public TrailerEntry(String trailer_id, String name, String key, String site) {
        this.trailer_id = trailer_id;
        this.name = name;
        this.key = key;
        this.site = site;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trailer_id);
        dest.writeString(this.name);
        dest.writeString(this.key);
        dest.writeString(this.site);
    }
}
