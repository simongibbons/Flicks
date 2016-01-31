package com.simongibbons.flicks.api;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.simongibbons.flicks.BuildConfig;
import com.simongibbons.flicks.database.MovieProvider;
import com.simongibbons.flicks.database.ReviewColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TheMovieDbAPI {

    static final String BASE_URL =
            "https://api.themoviedb.org/3/discover/movie";

    static final String PAGE_PARAM = "page";
    static final String API_KEY_PARAM = "api_key";
    static final String SORT_PARAM = "sort_by";


    // Tags for sort methods
    static public final int SORT_POPULARITY = 0;
    static public final int SORT_NUM_RATINGS = 1;

    static final Map<Integer, String> sort_map = new HashMap<>();
    static {
        sort_map.put(SORT_POPULARITY, "popularity.desc");
        sort_map.put(SORT_NUM_RATINGS, "vote_average.desc");
    }

    public static String buildPosterUrl(String posterPath) {
        return "https://image.tmdb.org/t/p/w185" + posterPath;
    }

    public static void loadMoviePage(int nextPage, final List<MovieData> movieList, int sort_mode,
                                     OkHttpClient okHttpClient,
                                     final Handler handler, final Runnable onCompletion) {
        if(onCompletion == null && handler == null) {
            return;
        }

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, sort_map.get(sort_mode))
                .appendQueryParameter("vote_count.gte", "30")
                .appendQueryParameter(PAGE_PARAM, Integer.toString(nextPage))
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(builtUri.toString())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    JSONArray movieJSONArray = result.getJSONArray("results");

                    for(int i = 0 ; i < movieJSONArray.length() ; ++i) {
                        movieList.add(MovieData.getMovieDataFromJson(movieJSONArray.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                if(handler != null && onCompletion != null) {
                    handler.post(onCompletion);
                }
            }
        });
    }

    public static void loadReviewsIntoDb(final Context context, OkHttpClient okHttpClient, int movieId) {
        Uri uri = Uri.parse("https://api.themoviedb.org/3/").buildUpon()
                .appendPath("movie")
                .appendPath(Integer.toString(movieId))
                .appendPath("reviews")
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject result = new JSONObject(response.body().string());
                    JSONArray reviewJsonArray = result.getJSONArray("results");
                    int movieId = result.getInt("id");

                    for(int i = 0 ; i < reviewJsonArray.length() ; ++i) {
                        ContentValues cv = new ContentValues();
                        JSONObject jsonObject = reviewJsonArray.getJSONObject(i);

                        cv.put(ReviewColumns.MOVIEID, movieId);
                        cv.put(ReviewColumns.AUTHOR, jsonObject.getString("author"));
                        cv.put(ReviewColumns.REVIEW, jsonObject.getString("content"));
                        cv.put(ReviewColumns.REVIEWID, jsonObject.getString("id"));

                        context.getContentResolver().insert(MovieProvider.Reviews.withId(movieId), cv);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }
}
