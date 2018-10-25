package com.mohamed14riad.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FavoritesProvider extends ContentProvider {
    private DatabaseHelper databaseHelper;

    private static final int FAVORITES = 100;
    private static final int FAVORITE_WITH_ID = 101;

    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(Contract.AUTHORITY, Contract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case FAVORITES: {
                return Contract.FavoritesEntry.DIRECTORY_TYPE;
            }
            case FAVORITE_WITH_ID: {
                return Contract.FavoritesEntry.ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        Uri returnUri;

        switch (URI_MATCHER.match(uri)) {
            case FAVORITES: {
                long id = sqLiteDatabase.insert(Contract.FavoritesEntry.TABLE_FAVORITES, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.FavoritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed To Insert Row Into: " + uri);
                }
            }
            break;
            default: {
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

            }
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Cursor returnCursor;

        switch (URI_MATCHER.match(uri)) {
            case FAVORITES: {
                returnCursor = sqLiteDatabase.query(
                        Contract.FavoritesEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
            }
            break;
            case FAVORITE_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                returnCursor = sqLiteDatabase.query(
                        Contract.FavoritesEntry.TABLE_FAVORITES,
                        projection,
                        Contract.FavoritesEntry.COLUMN_ID + " = ?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
            }
            break;
            default: {
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
            }
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        int rowsDeleted;

        switch (URI_MATCHER.match(uri)) {
            case FAVORITES: {
                rowsDeleted = sqLiteDatabase.delete(Contract.FavoritesEntry.TABLE_FAVORITES, selection, selectionArgs);
            }
            break;
            case FAVORITE_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                rowsDeleted = sqLiteDatabase.delete(
                        Contract.FavoritesEntry.TABLE_FAVORITES,
                        Contract.FavoritesEntry.COLUMN_ID + " = ?",
                        new String[]{id});
            }
            break;
            default: {
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
            }
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        int rowsUpdated;

        switch (URI_MATCHER.match(uri)) {
            case FAVORITES: {
                rowsUpdated = sqLiteDatabase.update(
                        Contract.FavoritesEntry.TABLE_FAVORITES,
                        contentValues,
                        selection,
                        selectionArgs);
            }
            break;
            case FAVORITE_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                rowsUpdated = sqLiteDatabase.update(
                        Contract.FavoritesEntry.TABLE_FAVORITES,
                        contentValues,
                        Contract.FavoritesEntry.COLUMN_ID + " = ?",
                        new String[]{id});
            }
            break;
            default: {
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
            }
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
