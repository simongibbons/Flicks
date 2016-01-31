package com.simongibbons.flicks.api;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieData implements Parcelable {
    public int id;
    public String title;
    public String posterPath;
    public String overview;
    public double rating;
    public String releaseDate;

    public static MovieData getMovieDataFromJson(JSONObject jsonObject)
            throws JSONException {

        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_RATING = "vote_average";
        final String TMDB_ID = "id";

        MovieData movie = new MovieData();
        movie.title = jsonObject.getString(TMDB_TITLE);
        movie.posterPath = jsonObject.getString(TMDB_POSTER_PATH);
        movie.overview = jsonObject.getString(TMDB_OVERVIEW);
        movie.rating = jsonObject.getDouble(TMDB_RATING);
        movie.releaseDate = jsonObject.getString(TMDB_RELEASE_DATE);
        movie.id = jsonObject.getInt(TMDB_ID);

        return movie;
    }

    // Methods for Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(rating);
        dest.writeString(releaseDate);
        dest.writeInt(id);
    }

    public static final Parcelable.Creator<MovieData> CREATOR
            = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            MovieData movieData = new MovieData();

            movieData.title = in.readString();
            movieData.posterPath = in.readString();
            movieData.overview = in.readString();
            movieData.rating = in.readDouble();
            movieData.releaseDate = in.readString();
            movieData.id = in.readInt();

            return movieData;
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}