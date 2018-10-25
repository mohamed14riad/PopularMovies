package com.mohamed14riad.popularmovies.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mohamed14riad.popularmovies.R;
import com.mohamed14riad.popularmovies.ui.fragments.DetailsFragment;
import com.mohamed14riad.popularmovies.models.Movie;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (getIntent() != null && getIntent().hasExtra("selectedMovie")) {
            Movie movie = getIntent().getParcelableExtra("selectedMovie");

            DetailsFragment detailsFragment = DetailsFragment.newInstance(movie);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_container, detailsFragment, "DetailsFragment")
                    .commit();
        } else {
            Toast.makeText(this, "Error While Loading Movie Details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
