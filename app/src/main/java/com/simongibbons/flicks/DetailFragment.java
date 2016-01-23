package com.simongibbons.flicks;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailFragment extends Fragment {



    public DetailFragment() {
        // Required empty public constructor
    }


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
        }

        return view;
    }

}
