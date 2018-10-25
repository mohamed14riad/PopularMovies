package com.mohamed14riad.popularmovies.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.mohamed14riad.popularmovies.R;
import com.mohamed14riad.popularmovies.ui.fragments.ListFragment;

public class MainActivity extends AppCompatActivity {

    public static boolean twoPaneMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();

        if (findViewById(R.id.details_container) != null) {
            twoPaneMode = true;
        } else {
            twoPaneMode = false;
        }

        ListFragment listFragment = ListFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.list_container, listFragment, "ListFragment")
                .commit();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
