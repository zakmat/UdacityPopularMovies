package com.example.mz.udacitypopularmovies.data;

import java.util.Date;

/**
 * Created by mz on 2017-02-04.
 */

public class MovieEntry {
    public String title;
    public String overview;
    public String posterPath;
    public int posterResource;
    public Date releaseDate;
    public double voteAverage;

    public MovieEntry(String title, String overview, String poster, Date date, double voteAverage)
    {
        this.title = title;
        this.overview = overview;
        this.posterPath = poster;
        this.releaseDate = date;
        this.voteAverage = voteAverage;
    }
}
