package com.simongibbons.flicks;

import android.net.Uri;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TheMovieDbAPI {

    public static String buildPosterUrl(String posterPath) {
        return "https://image.tmdb.org/t/p/w185" + posterPath;
    }

    public static void loadMoviePage(int nextPage, final List<MovieData> movieList,
                                     final Handler handler, final Runnable onCompletion) {
        if(onCompletion == null && handler == null) {
            return;
        }

        final OkHttpClient okHttpClient = new OkHttpClient();

        final String BASE_URL =
                "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc";
        final String PAGE_PARAM = "page";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
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
}
