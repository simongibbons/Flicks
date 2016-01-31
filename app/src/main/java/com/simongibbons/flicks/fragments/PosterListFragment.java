package com.simongibbons.flicks.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.simongibbons.flicks.FlicksApplication;
import com.simongibbons.flicks.MarginDecoration;
import com.simongibbons.flicks.api.MovieData;
import com.simongibbons.flicks.adapters.PosterListAdapter;
import com.simongibbons.flicks.R;
import com.simongibbons.flicks.api.TheMovieDbAPI;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PosterListFragment extends Fragment  {

    List<MovieData> movieList;

    private final String LOG_TAG = this.getClass().getSimpleName();



    private GridLayoutManager manager;
    private PosterListAdapter adapter;
    private RecyclerView recyclerView;

    public PosterListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.poster_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int groupId = item.getGroupId();

        Log.v("groupId", Integer.toString(groupId));

        if(groupId == R.id.menu_sort) {
            int id = item.getItemId();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = preferences.edit();

            switch (id) {
                case R.id.menu_sort_popularity: {
                    editor.putInt("sort_mode", TheMovieDbAPI.SORT_POPULARITY);
                    Log.v("Mode Change", "Setting sort mode to popularity");
                    break;
                }
                case R.id.menu_sort_rating: {
                    editor.putInt("sort_mode", TheMovieDbAPI.SORT_NUM_RATINGS);
                    Log.v("Mode Change", "Setting sort mode to ratings");
                    break;
                }

            }

            editor.commit();
            adapter.notifySortModeChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_poster_list, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.posterlist_recyclerview);

        // TODO change this to adapt to screen width!
        manager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(manager);

        // Add a decorator to put a margin between cards
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));

        // Create the array adapter
        if(savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList("movieList");
        } else {
            movieList = new ArrayList<>();
        }
        FlicksApplication app = (FlicksApplication) getActivity().getApplication();
        adapter = new PosterListAdapter(getActivity(), movieList, app.getOkHttpClient());
        recyclerView.setAdapter(adapter);

        // Add a on scroll listener to add more data when we run out.
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            private int previousTotal = 0;
            private boolean loading = true;
            private int visibleThreshold = 3;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = manager.getItemCount();
                int firstVisibleItem = manager.findFirstVisibleItemPosition();

                if(loading) {
                    if(totalItemCount != previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if(!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {

                    adapter.loadMoreMovies();
                    loading = true;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("movieList", (ArrayList<? extends Parcelable>) movieList);
    }


}