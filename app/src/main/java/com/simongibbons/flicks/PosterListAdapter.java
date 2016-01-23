package com.simongibbons.flicks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PosterListAdapter extends RecyclerView.Adapter<PosterListAdapter.ViewHolder> {

    private Context mContext;
    private List<MovieData> movieList = new ArrayList<>();
    private int nextPage = 1;

    public PosterListAdapter(Context context) {
        mContext = context;

        PopularMoviesTask task = new PopularMoviesTask();
        task.execute(nextPage);
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
            PopularMoviesTask task = new PopularMoviesTask();
            task.execute(nextPage);
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;
        protected CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.grid_item_poster_imageview);
            this.cardView = (CardView) view;
        }
    }

    public class PopularMoviesTask extends AsyncTask<Integer, Void, List<MovieData> > {

        final String LOG_TAG = PopularMoviesTask.class.getSimpleName();

        @Override
        protected List<MovieData> doInBackground(Integer... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;


            try {
                final String BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc";
                final String PAGE_PARAM = "page";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(PAGE_PARAM, params[0].toString())
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        private List<MovieData> getMovieDataFromJson(String moviesJsonStr) throws JSONException {

            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "title";
            final String TMDB_POSTER_PATH = "poster_path";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesJsonArray = moviesJson.getJSONArray(TMDB_RESULTS);

            List<MovieData> results = new ArrayList<>();

            for(int i = 0 ; i < moviesJsonArray.length() ; ++i) {
                JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);

                MovieData movie = new MovieData();

                movie.title = movieJsonObject.getString(TMDB_TITLE);
                movie.posterPath = movieJsonObject.getString(TMDB_POSTER_PATH);

                results.add(movie);
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<MovieData> result) {
            super.onPostExecute(result);

            if(result != null) {
                for (MovieData movie : result) {
                    movieList.add(movie);
                }
                nextPage += 1;
            }

            notifyDataSetChanged();
        }
    }

}