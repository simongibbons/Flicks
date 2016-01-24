package com.simongibbons.flicks;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PosterListFragment extends Fragment {

    List<MovieData> movieList;

    public PosterListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_poster_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.posterlist_recyclerview);

        // Add a decorator to put a margin between cards
        recyclerView.addItemDecoration(new MarginDecoration(getActivity()));

        if(savedInstanceState != null) {
            movieList = savedInstanceState.getParcelableArrayList("movieList");
        } else {
            movieList = new ArrayList<>();
        }

        recyclerView.setAdapter(new PosterListAdapter(getActivity(), movieList));

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("movieList", (ArrayList<? extends Parcelable>) movieList);
    }


}