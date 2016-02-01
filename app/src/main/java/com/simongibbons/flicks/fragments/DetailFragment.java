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
import android.widget.TextView;

import com.simongibbons.flicks.FlicksApplication;
import com.simongibbons.flicks.R;
import com.simongibbons.flicks.adapters.ReviewAdapter;
import com.simongibbons.flicks.adapters.VideoAdapter;
import com.simongibbons.flicks.api.TheMovieDbAPI;
import com.simongibbons.flicks.database.MovieColumns;
import com.simongibbons.flicks.database.MovieProvider;
import com.simongibbons.flicks.database.ReviewColumns;
import com.simongibbons.flicks.database.VideoColumns;
import com.simongibbons.flicks.ui.AdapterLinearLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private int movieId;

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

    VideoAdapter videoAdapter;

    private static final int VIDEO_LOADER = 1;
    private static final String[] VIDEO_COLUMNS = {
            VideoColumns._ID,
            VideoColumns.NAME,
            VideoColumns.YOUTUBE_KEY
    };
    public static final int COL_VIDEO_ID = 0;
    public static final int COL_VIDEO_NAME = 1;
    public static final int COL_YOUTUBE_KEY = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        movieId = getArguments().getInt("movie_id", 0);

        final String[] MOVIE_COLUMNS = {
                MovieColumns.NAME,
                MovieColumns.OVERVIEW,
                MovieColumns.RATING,
                MovieColumns.POSTER_PATH,
                MovieColumns.RELEASE_DATE
        };

        final int COL_NAME = 0;
        final int COL_OVERVIEW = 1;
        final int COL_RATING = 2;
        final int COL_POSTER_PATH = 3;
        final int COL_RELEASE_DATE = 4;

        Cursor movieCursor = getActivity().getContentResolver().query(
                MovieProvider.Movie.withId(movieId),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

        if(movieCursor != null && movieCursor.moveToFirst()) {
            TextView titleView = (TextView) view.findViewById(R.id.detail_fragment_title);
            titleView.setText(movieCursor.getString(COL_NAME));

            TextView descriptionView = (TextView) view.findViewById(R.id.detail_fragment_overview);
            descriptionView.setText(movieCursor.getString(COL_OVERVIEW));

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
            Date date = new Date(movieCursor.getLong(COL_RELEASE_DATE));

            TextView releaseDateView = (TextView) view.findViewById(R.id.detail_fragment_release_date);
            releaseDateView.setText(dateFormat.format(date));

            TextView ratingView = (TextView) view.findViewById(R.id.detail_fragment_rating);
            ratingView.setText(String.format(getString(R.string.format_rating), movieCursor.getFloat(COL_RATING)));

            ImageView posterView = (ImageView) view.findViewById(R.id.detail_fragment_poster);
            Picasso.with(getActivity())
                    .load("https://image.tmdb.org/t/p/w185" + movieCursor.getString(COL_POSTER_PATH))
                    .into(posterView);

            movieCursor.close();
        }


        // Setup loading reviews
        reviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        AdapterLinearLayout reviewList = (AdapterLinearLayout) view.findViewById(R.id.detail_fragment_review_list);
        reviewList.setAdapter(reviewAdapter);

        // Setup loading videos
        videoAdapter = new VideoAdapter(getActivity(), null, 0);
        AdapterLinearLayout videoList = (AdapterLinearLayout) view.findViewById(R.id.detail_fragment_trailer_list);
        videoList.setAdapter(videoAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);

        FlicksApplication app = (FlicksApplication) getActivity().getApplication();

        TheMovieDbAPI.loadReviewsIntoDb(getActivity(), app.getOkHttpClient(), movieId);
        TheMovieDbAPI.loadVideosIntoDb(getActivity(), app.getOkHttpClient(), movieId);
    }

    // Functions to implement a loader to populate the desired views.

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case REVIEW_LOADER: {
                Uri uri = MovieProvider.Reviews.withId(movieId);

                return new CursorLoader(getActivity(),
                        uri,
                        REVIEW_COLUMNS,
                        null,
                        null,
                        null);
            }

            case VIDEO_LOADER: {
                Uri uri = MovieProvider.Videos.withId(movieId);

                return new CursorLoader(getActivity(),
                        uri,
                        VIDEO_COLUMNS,
                        null,
                        null,
                        null);
            }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case REVIEW_LOADER: {
                reviewAdapter.swapCursor(data);
                break;
            }
            case VIDEO_LOADER: {
                videoAdapter.swapCursor(data);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case REVIEW_LOADER: {
                reviewAdapter.swapCursor(null);
                break;
            }
            case VIDEO_LOADER: {
                videoAdapter.swapCursor(null);
                break;
            }
        }
    }
}
