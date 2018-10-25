package com.mohamed14riad.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "movies.db";

    public static final String AUTHORITY = "com.mohamed14riad.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    // An empty private constructor makes sure that the class is not going to be initialised.
    private Contract() {
    }

    public static abstract class FavoritesEntry implements BaseColumns {
        public static final String TABLE_FAVORITES = "favorites";
        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_BACKDROP_PATH = "movie_backdrop_path";
        public static final String COLUMN_TITLE = "movie_title";
        public static final String COLUMN_RATING = "movie_rating";
        public static final String COLUMN_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_OVERVIEW = "movie_overview";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_POSTER_PATH + " TEXT DEFAULT \"\", " +
                COLUMN_BACKDROP_PATH + " TEXT DEFAULT \"\", " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_RATING + " REAL NOT NULL, " +
                COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL)";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_FAVORITES;

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        // directory type
        public static final String DIRECTORY_TYPE = "vnd.android.cursor.dir" + "/" + AUTHORITY + "/" + PATH_FAVORITES;

        // single item type
        public static final String ITEM_TYPE = "vnd.android.cursor.item" + "/" + AUTHORITY + "/" + PATH_FAVORITES;
    }

}