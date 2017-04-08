package com.example.mz.udacitypopularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mateusz.zak on 07.04.2017.
 */

public class MovieContract implements BaseColumns {

    public static final String CONTENT_AUTHORITY = "com.example.mz.udacitypopularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEOS = "video";
    public static final String PATH_REVIEWS = "review";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTERPATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String TABLE_NAME = "review";

        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
    }

    public static final class VideoEntry {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();
        public static final String TABLE_NAME = "video";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_SITE = "site";
    }
}
