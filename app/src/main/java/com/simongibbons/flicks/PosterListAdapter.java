package com.simongibbons.flicks;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PosterListAdapter extends RecyclerView.Adapter<PosterListAdapter.ViewHolder> {

    private Context mContext;
    private List<MovieData> movieList = new ArrayList<>();
    private int nextPage = 1;

    public PosterListAdapter(Context context, List<MovieData> movieList) {
        mContext = context;
        this.movieList = movieList;

        if(movieList != null) {
            nextPage = (movieList.size() / 20) + 1;
        }

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
                .load(TheMovieDbAPI.buildPosterUrl(movie.posterPath))
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
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable uiCallback = new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
                nextPage += 1;
            }
        };

        TheMovieDbAPI.loadMoviePage(nextPage, movieList, handler, uiCallback);
    }



}