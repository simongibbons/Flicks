package com.simongibbons.flicks.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.simongibbons.flicks.activities.DetailActivity;
import com.simongibbons.flicks.api.MovieData;
import com.simongibbons.flicks.R;
import com.simongibbons.flicks.api.TheMovieDbAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class PosterListAdapter extends RecyclerView.Adapter<PosterListAdapter.ViewHolder> {

    private Context context;
    private List<MovieData> movieList = new ArrayList<>();
    private int nextPage = 1;
    private OkHttpClient okHttpClient;
    private int sort_mode;

    public PosterListAdapter(Context context, List<MovieData> movieList, OkHttpClient okHttpClient) {
        this.context = context;
        this.movieList = movieList;
        this.okHttpClient = okHttpClient;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        sort_mode = preferences.getInt("sort_mode", TheMovieDbAPI.SORT_POPULARITY);

        if(movieList != null) {
            nextPage = (movieList.size() / 20) + 1;
        }

        loadMoreMovies();
    }

    public void notifySortModeChanged() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int new_sort_mode = preferences.getInt("sort_mode", TheMovieDbAPI.SORT_POPULARITY);

        if(new_sort_mode != sort_mode) {
            sort_mode = new_sort_mode;

            nextPage = 1;

            movieList = new ArrayList<>();
            loadMoreMovies();
        }
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

        holder.imageView.setImageDrawable(null);

        Picasso.with(context)
                .load(TheMovieDbAPI.buildPosterUrl(movie.posterPath))
                .into(holder.imageView);

        holder.imageView.setContentDescription(movie.title);
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
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("movie", movieList.get(getAdapterPosition()));

            context.startActivity(intent);
        }
    }


    public void loadMoreMovies() {
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable uiCallback = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                nextPage += 1;
            }
        };

        TheMovieDbAPI.loadMoviePage(context, nextPage, movieList,
                sort_mode,
                okHttpClient, handler, uiCallback);
    }



}