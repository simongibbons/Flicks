package com.simongibbons.flicks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PosterListAdapter extends RecyclerView.Adapter<PosterListAdapter.ViewHolder> {

    private Context mContext;
    final OkHttpClient okHttpClient = new OkHttpClient();
    final Handler handler = new Handler(Looper.getMainLooper());
    private List<MovieData> movieList = new ArrayList<>();
    private int nextPage = 1;

    public PosterListAdapter(Context context) {
        mContext = context;

        loadMoreMovies();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_poster, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieData movie = movieList.get(position);

        Picasso.with(mContext)
                .load("https://image.tmdb.org/t/p/w185" + movie.posterPath)
                .into(holder.imageView);

        holder.imageView.setContentDescription(movie.title);

        // Check if we need to grab more data
        if(position == (getItemCount() - 1) ){
            loadMoreMovies();
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView imageView;
        protected CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.grid_item_poster_imageview);
            this.cardView = (CardView) view;

            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("movie", movieList.get(getAdapterPosition()));

            mContext.startActivity(intent);
        }
    }


    private void loadMoreMovies() {
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

        okHttpClient.newCall(request).enqueue(new LoadMovieCallback());
    }

    private class LoadMovieCallback implements Callback {
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

            // Let the UI know that we have new movies
            handler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

            // Update the page count so the next time we run we get new content.
            nextPage += 1;
        }
    }

}