package com.mohamed14riad.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AppConstants {
    /* Insert your themoviedb.org API KEY here */
    public static final String API_KEY = "";

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/w500";
    public static final String BASE_BACKDROP_URL = "https://image.tmdb.org/t/p/w780";

    public static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=%s";
    public static final String YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/%s/0.jpg";

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
