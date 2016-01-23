package com.simongibbons.flicks;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieData {
    public String title;
    public String posterPath;

    public static MovieData getMovieDataFromJson(JSONObject jsonObject)
            throws JSONException {

        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";

        MovieData movie = new MovieData();
        movie.title = jsonObject.getString(TMDB_TITLE);
        movie.posterPath = jsonObject.getString(TMDB_POSTER_PATH);

        return movie;
    }
}