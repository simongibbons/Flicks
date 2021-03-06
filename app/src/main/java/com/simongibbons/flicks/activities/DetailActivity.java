package com.simongibbons.flicks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.simongibbons.flicks.R;
import com.simongibbons.flicks.api.MovieData;
import com.simongibbons.flicks.fragments.DetailFragment;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if(intent != null) {
            MovieData movieData = intent.getParcelableExtra("movie");
            ActionBar actionBar = getSupportActionBar();
            if(movieData != null && actionBar != null ) {
                actionBar.setTitle(movieData.title);
            }
        }

        if (savedInstanceState == null) {
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(
                    android.R.id.content, detailFragment).commit();
        }
    }

}
