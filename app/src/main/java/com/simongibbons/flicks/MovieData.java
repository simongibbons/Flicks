package com.simongibbons.flicks;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieData implements Parcelable {
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

    // Methods for Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(posterPath);
    }

    public static final Parcelable.Creator<MovieData> CREATOR
            = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            MovieData movieData = new MovieData();
            movieData.title = in.readString();
            movieData.posterPath = in.readString();

            return movieData;
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}