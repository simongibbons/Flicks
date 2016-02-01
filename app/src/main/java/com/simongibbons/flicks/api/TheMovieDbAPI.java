package com.simongibbons.flicks.api;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.simongibbons.flicks.BuildConfig;
import com.simongibbons.flicks.R;
import com.simongibbons.flicks.database.MovieColumns;
import com.simongibbons.flicks.database.MovieProvider;
import com.simongibbons.flicks.database.ReviewColumns;
import com.simongibbons.flicks.database.VideoColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

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

    public static void loadMoviePage(final Context context, int nextPage,
                                     final List<MovieData> movieList, int sort_mode,
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

                    Vector<ContentValues> cVVector = new Vector<>(movieJSONArray.length());

                    DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.tmdb_date_format), Locale.ENGLISH);

                    for(int i = 0 ; i < movieJSONArray.length() ; ++i) {
                        JSONObject movieObject = movieJSONArray.getJSONObject(i);

                        ContentValues cv = new ContentValues();
                        cv.put(MovieColumns.MOVIE_ID, movieObject.getInt("id"));
                        cv.put(MovieColumns.RATING, movieObject.getDouble("vote_average"));
                        cv.put(MovieColumns.OVERVIEW, movieObject.getString("overview"));
                        cv.put(MovieColumns.NAME, movieObject.getString("title"));
                        cv.put(MovieColumns.POSTER_PATH, movieObject.getString("poster_path"));
                        try {
                            Date date = dateFormat.parse(movieObject.getString("release_date"));
                            cv.put(MovieColumns.RELEASE_DATE, date.getTime());
                        } catch (ParseException e) {
                            Log.v("Date Error", "couldn't parse date");
                        }


                        cVVector.add(cv);
                    }

                    if(cVVector.size() > 0) {
                        ContentValues [] cVArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cVArray);

                        context.getContentResolver().bulkInsert(MovieProvider.Movies.CONTENT_URI, cVArray);
                    }


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

                    Vector<ContentValues> cVVector = new Vector<>(reviewJsonArray.length());

                    for(int i = 0 ; i < reviewJsonArray.length() ; ++i) {
                        ContentValues cv = new ContentValues();
                        JSONObject jsonObject = reviewJsonArray.getJSONObject(i);

                        cv.put(ReviewColumns.MOVIE_ID, movieId);
                        cv.put(ReviewColumns.AUTHOR, jsonObject.getString("author"));
                        cv.put(ReviewColumns.REVIEW, jsonObject.getString("content"));
                        cv.put(ReviewColumns.REVIEW_ID, jsonObject.getString("id"));

                        cVVector.add(cv);
                    }

                    if(cVVector.size() > 0) {
                        ContentValues[] cVArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cVArray);

                        Uri uri = MovieProvider.Reviews.withId(movieId);

                        context.getContentResolver().bulkInsert(uri, cVArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void loadVideosIntoDb(final Context context, OkHttpClient okHttpClient, int movieId) {
        Uri uri = Uri.parse("https://api.themoviedb.org/3/").buildUpon()
                .appendPath("movie")
                .appendPath(Integer.toString(movieId))
                .appendPath("videos")
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(uri.toString())
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
                    JSONArray reviewJsonArray = result.getJSONArray("results");
                    int movieId = result.getInt("id");

                    Vector<ContentValues> cVVector = new Vector<>(reviewJsonArray.length());

                    for(int i = 0 ; i < reviewJsonArray.length() ; ++i) {
                        JSONObject jsonObject = reviewJsonArray.getJSONObject(i);

                        ContentValues cv = new ContentValues();

                        cv.put(VideoColumns.MOVIE_ID, movieId);
                        cv.put(VideoColumns.VIDEO_ID, jsonObject.getString("id"));
                        cv.put(VideoColumns.YOUTUBE_KEY, jsonObject.getString("key"));
                        cv.put(VideoColumns.NAME, jsonObject.getString("name"));
                        cv.put(VideoColumns.TYPE, jsonObject.getString("type"));

                        cVVector.add(cv);
                    }

                    if(cVVector.size() > 0) {
                        ContentValues[] cVArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cVArray);

                        Uri uri = MovieProvider.Videos.withId(movieId);

                        context.getContentResolver().bulkInsert(uri, cVArray);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
