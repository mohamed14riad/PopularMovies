package com.mohamed14riad.popularmovies.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mohamed14riad.popularmovies.R;
import com.mohamed14riad.popularmovies.adapters.MoviesAdapter;
import com.mohamed14riad.popularmovies.api.ApiInterface;
import com.mohamed14riad.popularmovies.api.ApiUtil;
import com.mohamed14riad.popularmovies.data.FavoritesHelper;
import com.mohamed14riad.popularmovies.models.Movie;
import com.mohamed14riad.popularmovies.models.MoviesResponse;
import com.mohamed14riad.popularmovies.ui.activities.MovieDetailsActivity;
import com.mohamed14riad.popularmovies.utils.AppConstants;
import com.mohamed14riad.popularmovies.utils.SortingDialogFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.mohamed14riad.popularmovies.ui.activities.MainActivity.twoPaneMode;

public class ListFragment extends Fragment
        implements SortingDialogFragment.SortingOptionsListener, MoviesAdapter.OnItemClickListener {

    /* First You Should Insert Your themoviedb.org API KEY In AppConstants Class */
    private static final String API_KEY = AppConstants.API_KEY;

    private Snackbar snackbar = null;
    private ProgressBar progressBar = null;

    private List<Movie> movieList = null;

    private FavoritesHelper favoritesHelper = null;

    private MoviesAdapter moviesAdapter = null;
    private RecyclerView moviesRecyclerView = null;

    private ApiInterface apiService = null;
    private Disposable movieSubscription = null;

    private String selectedOption = null;
    private SharedPreferences sharedPreferences = null;

    private boolean isItemSelected = false;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        String PREFERENCES_NAME = "movies_Preferences";
        sharedPreferences = getContext().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        if (sharedPreferences.getString("selected_option", null) == null) {
            // Setup For First Time
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_option", getString(R.string.most_popular));
            editor.apply();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        if (API_KEY.isEmpty()) {
            Toast.makeText(getContext(), "Please Obtain Your API KEY First From themoviedb.org", Toast.LENGTH_LONG).show();
            return null;
        }

        moviesAdapter = new MoviesAdapter(getContext(), this);

        moviesRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_recycler_view);
        moviesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        moviesRecyclerView.setAdapter(moviesAdapter);

        snackbar = Snackbar.make(moviesRecyclerView, "Check Your Internet Connection!", Snackbar.LENGTH_INDEFINITE);
        progressBar = (ProgressBar) rootView.findViewById(R.id.main_progress_bar);

        movieList = new ArrayList<>();
        movieList.clear();

        favoritesHelper = new FavoritesHelper(getContext());

        apiService = ApiUtil.getApiService();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!API_KEY.isEmpty()) {
            selectedOption = sharedPreferences.getString("selected_option", getString(R.string.most_popular));
            if (movieList.isEmpty() || selectedOption.equals(getString(R.string.favorites))) {
                onSortingOptionSelect(selectedOption);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!API_KEY.isEmpty()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selected_option", selectedOption);
            editor.apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_item:
                displaySortingOptions();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displaySortingOptions() {
        DialogFragment sortingDialogFragment = SortingDialogFragment.newInstance(this, selectedOption);
        sortingDialogFragment.show(getFragmentManager(), "Sorting");
    }

    @Override
    public void onSortingOptionSelect(String selectedOption) {
        this.selectedOption = selectedOption;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selected_option", this.selectedOption);
        editor.apply();

        boolean isConnected = AppConstants.isConnected(getContext());

        if (selectedOption.equals(getString(R.string.most_popular))) {
            if (isConnected) {
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                progressBar.setVisibility(View.VISIBLE);

                movieSubscription = apiService.getPopularMovies()
                        .map(MoviesResponse::getResults)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onMovieFetchSuccess, this::onMovieFetchFailed);
            } else {
                showSnackBar(false);
            }
        } else if (selectedOption.equals(getString(R.string.highest_rated))) {
            if (isConnected) {
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                progressBar.setVisibility(View.VISIBLE);

                movieSubscription = apiService.getTopRatedMovies()
                        .map(MoviesResponse::getResults)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onMovieFetchSuccess, this::onMovieFetchFailed);
            } else {
                showSnackBar(false);
            }
        } else if (selectedOption.equals(getString(R.string.favorites))) {
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }

            List<Movie> favorites = favoritesHelper.getFavorites();

            movieList.clear();
            movieList = favorites;
            moviesAdapter.addAll(movieList);

            if (movieList.isEmpty()) {
                Snackbar.make(moviesRecyclerView, "Favorites List Is Empty.", Snackbar.LENGTH_LONG).show();
            } else {
                if (twoPaneMode) {
                    if (!isItemSelected) {
                        loadDetailsFragment(0);
                    }
                }
            }
        }
    }

    private void onMovieFetchSuccess(List<Movie> movies) {
        progressBar.setVisibility(View.GONE);

        movieList.clear();
        movieList = movies;
        moviesAdapter.addAll(movieList);

        if (twoPaneMode && !movieList.isEmpty()) {
            if (!isItemSelected) {
                loadDetailsFragment(0);
            }
        }
    }

    private void onMovieFetchFailed(Throwable e) {
        progressBar.setVisibility(View.GONE);

        if (getContext() != null) {
            Toast.makeText(getContext(), "Failed To Get Movies.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Log.e("onFailure", "Failed To Get Movies.\n" + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (movieSubscription != null && !movieSubscription.isDisposed()) {
            movieSubscription.dispose();
        }
    }

    private void showSnackBar(boolean isConnected) {
        if (!isConnected && !selectedOption.equals(getString(R.string.favorites))) {
            snackbar.show();
        }
    }

    @Override
    public void onItemClick(int position) {
        isItemSelected = true;

        if (twoPaneMode) {
            loadDetailsFragment(position);
        } else {
            startDetailsActivity(position);
        }
    }

    private void loadDetailsFragment(int position) {
        if (!movieList.isEmpty()) {
            DetailsFragment detailsFragment = DetailsFragment.newInstance(movieList.get(position));
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_container, detailsFragment, "DetailsFragment")
                    .commit();
        }
    }

    private void startDetailsActivity(int position) {
        if (!movieList.isEmpty()) {
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra("selectedMovie", movieList.get(position));
            startActivity(intent);
        }
    }
}
