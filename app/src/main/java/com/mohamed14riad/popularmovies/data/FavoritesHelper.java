package com.mohamed14riad.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.mohamed14riad.popularmovies.models.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoritesHelper {
    private Context context;

    public FavoritesHelper(Context context) {
        this.context = context;
    }

    public List<Movie> getFavorites() {
        Cursor cursor = context.getContentResolver()
                .query(Contract.FavoritesEntry.CONTENT_URI, null, null, null, null);

        List<Movie> favorites = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();

                movie.setMovieId(cursor.getInt(0));
                movie.setPosterPath(cursor.getString(1));
                movie.setBackdropPath(cursor.getString(2));
                movie.setMovieTitle(cursor.getString(3));
                movie.setVoteAverage(cursor.getDouble(4));
                movie.setReleaseDate(cursor.getString(5));
                movie.setOverview(cursor.getString(6));

                favorites.add(movie);
            } while (cursor.moveToNext());
        }

        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Error : Close Cursor");
        }

        return favorites;
    }

    public boolean isFavorite(int id) {
        String stringId = String.valueOf(id);

        Uri uri = Contract.FavoritesEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Found in Database
            return true;
        } else {
            // Not Found
            return false;
        }
    }

    public void setFavorite(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.FavoritesEntry.COLUMN_ID, movie.getMovieId());
        contentValues.put(Contract.FavoritesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(Contract.FavoritesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(Contract.FavoritesEntry.COLUMN_TITLE, movie.getMovieTitle());
        contentValues.put(Contract.FavoritesEntry.COLUMN_RATING, movie.getVoteAverage());
        contentValues.put(Contract.FavoritesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(Contract.FavoritesEntry.COLUMN_OVERVIEW, movie.getOverview());

        Uri returnedUri = context.getContentResolver().insert(Contract.FavoritesEntry.CONTENT_URI, contentValues);
    }

    public void unFavorite(int id) {
        String stringId = String.valueOf(id);

        Uri uri = Contract.FavoritesEntry.CONTENT_URI.buildUpon().appendPath(stringId).build();

        int rowsDeleted = context.getContentResolver().delete(uri, null, null);
    }
}
