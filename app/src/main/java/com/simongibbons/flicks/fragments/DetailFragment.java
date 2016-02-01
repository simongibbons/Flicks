package com.simongibbons.flicks.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.simongibbons.flicks.FlicksApplication;
import com.simongibbons.flicks.R;
import com.simongibbons.flicks.adapters.ReviewAdapter;
import com.simongibbons.flicks.api.MovieData;
import com.simongibbons.flicks.api.TheMovieDbAPI;
import com.simongibbons.flicks.database.MovieProvider;
import com.simongibbons.flicks.database.ReviewColumns;
import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }

    ReviewAdapter reviewAdapter;

    private static final int REVIEW_LOADER = 0;
    private static final String[] REVIEW_COLUMNS = {
            ReviewColumns._ID,
            ReviewColumns.REVIEW
    };
    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_TEXT = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get data passed from arguments
        MovieData movie = (MovieData) getArguments().get("movie");

        if(movie != null) {
            TextView titleView = (TextView) view.findViewById(R.id.detail_fragment_title);
            titleView.setText(movie.title);

            TextView descriptionView = (TextView) view.findViewById(R.id.detail_fragment_overview);
            descriptionView.setText(movie.overview);

            TextView releaseDateView = (TextView) view.findViewById(R.id.detail_fragment_release_date);
            releaseDateView.setText(movie.releaseDate);

            TextView ratingView = (TextView) view.findViewById(R.id.detail_fragment_rating);
            ratingView.setText(String.format(getString(R.string.format_rating), movie.rating));

            ImageView posterView = (ImageView) view.findViewById(R.id.detail_fragment_poster);
            Picasso.with(getActivity())
                    .load("https://image.tmdb.org/t/p/w185" + movie.posterPath)
                    .into(posterView);
        }

        // Setup loading reviews
        reviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        ListView reviewList = (ListView) view.findViewById(R.id.detail_fragment_review_list);
        reviewList.setAdapter(reviewAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(REVIEW_LOADER, null, this);

        int movieId = ((MovieData) getArguments().get("movie")).id;

        FlicksApplication app = (FlicksApplication) getActivity().getApplication();

        TheMovieDbAPI.loadReviewsIntoDb(getActivity(), app.getOkHttpClient(), movieId);
        TheMovieDbAPI.loadVideosIntoDb(getActivity(), app.getOkHttpClient(), movieId);
    }

    // Functions to implement a loader to populate the desired views.

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case REVIEW_LOADER: {
                MovieData movie = (MovieData) getArguments().get("movie");

                Uri uri = MovieProvider.Reviews.withId(movie.id);

                return new CursorLoader(getActivity(),
                        uri,
                        REVIEW_COLUMNS,
                        null,
                        null,
                        null);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        reviewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reviewAdapter.swapCursor(null);
    }
}
