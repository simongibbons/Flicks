package com.simongibbons.flicks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieData {
    public String title = "";
    public String posterPath = "";

    public static List<MovieData> getMovieDataFromJson(String moviesJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesJsonArray = moviesJson.getJSONArray(TMDB_RESULTS);

        List<MovieData> results = new ArrayList<>();

        for (int i = 0; i < moviesJsonArray.length(); ++i) {
            JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);

            MovieData movie = new MovieData();

            movie.title = movieJsonObject.getString(TMDB_TITLE);
            movie.posterPath = movieJsonObject.getString(TMDB_POSTER_PATH);

            results.add(movie);
        }

        return results;
    }
}