package com.mohamed14riad.popularmovies.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mohamed14riad.popularmovies.R;
import com.mohamed14riad.popularmovies.adapters.TrailersAdapter;
import com.mohamed14riad.popularmovies.api.ApiInterface;
import com.mohamed14riad.popularmovies.api.ApiUtil;
import com.mohamed14riad.popularmovies.data.FavoritesHelper;
import com.mohamed14riad.popularmovies.models.Movie;
import com.mohamed14riad.popularmovies.models.Review;
import com.mohamed14riad.popularmovies.models.ReviewsResponse;
import com.mohamed14riad.popularmovies.models.Trailer;
import com.mohamed14riad.popularmovies.models.TrailersResponse;
import com.mohamed14riad.popularmovies.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailsFragment extends Fragment
        implements View.OnClickListener, TrailersAdapter.OnTrailerClickListener {

    private View rootView = null;

    private Movie movie = null;

    private ImageView moviePoster = null;
    private TextView movieTitle = null, movieRating = null, movieReleaseDate = null,
            movieOverview = null, trailersLabel = null, reviewsLabel = null;

    private RecyclerView trailersRecyclerView = null;

    private LinearLayout reviewsLayout = null;

    private FloatingActionButton favoriteButton = null;

    private static final String BASE_BACKDROP_URL = AppConstants.BASE_BACKDROP_URL;

    private ApiInterface apiService = null;
    private Disposable trailerSubscription = null, reviewSubscription = null;

    private List<Trailer> trailerList = null;
    private List<Review> reviewList = null;

    private TrailersAdapter trailersAdapter = null;

    private FavoritesHelper favoritesHelper = null;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Movie movie) {
        DetailsFragment detailsFragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable("movie", movie);
        detailsFragment.setArguments(args);

        return detailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null && getArguments().containsKey("movie")) {
            movie = getArguments().getParcelable("movie");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        moviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        movieRating = (TextView) rootView.findViewById(R.id.movie_rating);
        movieReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        movieOverview = (TextView) rootView.findViewById(R.id.movie_overview);
        trailersLabel = (TextView) rootView.findViewById(R.id.trailers_label);
        reviewsLabel = (TextView) rootView.findViewById(R.id.reviews_label);

        reviewsLayout = (LinearLayout) rootView.findViewById(R.id.reviews_layout);

        favoriteButton = (FloatingActionButton) rootView.findViewById(R.id.favorite_button);
        favoriteButton.setOnClickListener(this);

        apiService = ApiUtil.getApiService();

        trailerList = new ArrayList<>();
        reviewList = new ArrayList<>();

        trailersAdapter = new TrailersAdapter(getContext(), this);

        trailersRecyclerView = (RecyclerView) rootView.findViewById(R.id.trailers_recycler_view);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trailersRecyclerView.setAdapter(trailersAdapter);

        favoritesHelper = new FavoritesHelper(getContext());

        setToolbar();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (movie != null) {
            showInitialData();
            getTrailersAndReviews();
        }
    }

    private void setToolbar() {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        collapsingToolbar.setTitle(getString(R.string.movie_details));
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedToolbar);
        collapsingToolbar.setTitleEnabled(true);

        if (toolbar != null) {
            // Not in twoPaneMode
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void showInitialData() {
        Uri backdropUrl = null;
        String backdropPath = movie.getBackdropPath();
        if (backdropPath != null && !backdropPath.isEmpty()) {
            backdropUrl = Uri.parse(BASE_BACKDROP_URL.concat(backdropPath));
        }

        Glide.with(this)
                .asBitmap()
                .load(backdropUrl)
                .apply(new RequestOptions().placeholder(R.color.colorPrimary).error(R.color.colorPrimary))
                .into(moviePoster);
        movieTitle.setText(movie.getMovieTitle());
        movieRating.setText(String.valueOf(movie.getVoteAverage()).concat("/10"));
        movieReleaseDate.setText("Release Date: ".concat(movie.getReleaseDate()));
        movieOverview.setText(movie.getOverview());

        if (favoritesHelper.isFavorite(movie.getMovieId())) {
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_white));
        } else {
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_white));
        }
    }

    private void getTrailersAndReviews() {
        boolean isConnected = AppConstants.isConnected(getContext());

        if (isConnected) {
            trailerSubscription = apiService.getTrailers(movie.getMovieId())
                    .map(TrailersResponse::getResults)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTrailerFetchSuccess, this::onTrailerFetchFailed);

            reviewSubscription = apiService.getReviews(movie.getMovieId())
                    .map(ReviewsResponse::getResults)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onReviewFetchSuccess, this::onReviewFetchFailed);
        }
    }

    private void onTrailerFetchSuccess(List<Trailer> trailers) {
        trailerList.clear();
        trailerList = trailers;

        if (trailerList.isEmpty()) {
            trailersLabel.setVisibility(View.GONE);
            trailersRecyclerView.setVisibility(View.GONE);
        } else {
            trailersLabel.setVisibility(View.VISIBLE);
            trailersRecyclerView.setVisibility(View.VISIBLE);

            trailersAdapter.addAll(trailerList);
        }
    }

    private void onTrailerFetchFailed(Throwable e) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Failed To Get Trailers.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Log.e("onFailure", "Failed To Get Trailers.\n" + e.getMessage());
        }
    }

    private void onReviewFetchSuccess(List<Review> reviews) {
        reviewList.clear();
        reviewList = reviews;

        if (reviewList.isEmpty()) {
            reviewsLabel.setVisibility(View.GONE);
            reviewsLayout.setVisibility(View.GONE);
        } else {
            reviewsLabel.setVisibility(View.VISIBLE);
            reviewsLayout.setVisibility(View.VISIBLE);

            showReviews(reviewList);
        }
    }

    private void onReviewFetchFailed(Throwable e) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Failed To Get Reviews.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Log.e("onFailure", "Failed To Get Reviews.\n" + e.getMessage());
        }
    }

    private void showReviews(List<Review> reviews) {
        reviewsLayout.removeAllViews();

        LayoutInflater inflater = getLayoutInflater();
        for (Review review : reviews) {
            View viewContainer = inflater.inflate(R.layout.review, reviewsLayout, false);

            TextView reviewAuthor = viewContainer.findViewById(R.id.review_author);
            TextView reviewContent = viewContainer.findViewById(R.id.review_content);

            reviewAuthor.setText(review.getAuthor());
            reviewContent.setText(review.getContent());

            reviewContent.setOnClickListener(this);

            reviewsLayout.addView(viewContainer);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.review_content:
                onReviewClick(view);
                break;
            case R.id.favorite_button:
                onFavoriteClick();
                break;
        }
    }

    @Override
    public void onTrailerClick(int position) {
        Trailer trailer = trailerList.get(position);
        String videoUrl = Trailer.getUrl(trailer);
        if (videoUrl != null && !videoUrl.isEmpty()) {
            Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            startActivity(Intent.createChooser(playVideoIntent, "Open with"));
        } else {
            Snackbar.make(rootView.findViewById(R.id.details_fragment), "This Trailer Has No URL", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void onReviewClick(View reviewContent) {
        TextView content = (TextView) reviewContent;
        if (content.getMaxLines() == 5) {
            content.setMaxLines(500);
        } else {
            content.setMaxLines(5);
        }
    }

    private void onFavoriteClick() {
        if (favoritesHelper.isFavorite(movie.getMovieId())) {
            favoritesHelper.unFavorite(movie.getMovieId());
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_white));
        } else {
            favoritesHelper.setFavorite(movie);
            favoriteButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_white));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (trailerSubscription != null && !trailerSubscription.isDisposed()) {
            trailerSubscription.dispose();
        }

        if (reviewSubscription != null && !reviewSubscription.isDisposed()) {
            reviewSubscription.dispose();
        }
    }
}
