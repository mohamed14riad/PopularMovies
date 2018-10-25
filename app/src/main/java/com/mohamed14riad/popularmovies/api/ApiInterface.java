package com.mohamed14riad.popularmovies.api;

import com.mohamed14riad.popularmovies.models.MoviesResponse;
import com.mohamed14riad.popularmovies.models.ReviewsResponse;
import com.mohamed14riad.popularmovies.models.TrailersResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("movie/popular")
    Observable<MoviesResponse> getPopularMovies();

    @GET("movie/top_rated")
    Observable<MoviesResponse> getTopRatedMovies();

    @GET("movie/{id}/videos")
    Observable<TrailersResponse> getTrailers(@Path("id") int id);

    @GET("movie/{id}/reviews")
    Observable<ReviewsResponse> getReviews(@Path("id") int id);
}
