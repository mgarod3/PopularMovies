package com.example.android.popularmovies1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies1.R;

/**
 * {@link ContentProvider} for PopularMovies app.
 */
public class MoviesProvider extends ContentProvider {

    //Tag for the log messages
    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the favorites table
     */
    private static final int FAVORITES = 100;
    /**
     * URI matcher code for the content URI for a single movie in the favorites table
     */
    private static final int FAVORITE_ID = 101;

    /**
     * URI matcher object to match a context URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer.
    static {
        // The calls to addURI() of the form "content://com.example.android.popularmovies1.movies/favorites" go here.
        // They will be mapped to the integer code {@link #FAVORITES}.
        // URI to provide access to MULTIPLE rows of the favorites table.
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVORITES, FAVORITES);

        // Content URI of the form "content://com.example.android.popularmovies1.movies/favorites/#"
        // They will map to the integer code {@link #FAVORITE_ID}.
        // This URI is used to provide access to ONE single row of the favorites table.
        // The "#" wildcard can be substituted for an integer, the movie id.
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_FAVORITES + "/#", FAVORITE_ID);
    }

    // This is a global variable, so it can be referenced from other ContentProvider methods.
    private MoviesDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                // Query the favorites table selecting all columns
                cursor = database.query(MoviesContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FAVORITE_ID:
                // For the FAVORITE_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.popularmovies1.movies/favorites/5",
                // the selection will be "COLUMN_ID=?" and the selection argument will be a
                // String array containing the actual ID of 5 in this case.
                selection = MoviesContract.MovieEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the movies table where the COLUMN_ID equals to the integer in the uri
                // and return a Cursor containing that row of the table.
                cursor = database.query(MoviesContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.error_cannot_query_unknown_uri) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return insertMovie(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.insertion_not_supported) + uri);
        }
    }

    /**
     * Insert a movie into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertMovie(Uri uri, ContentValues values) {

        //get content from values object
        int id = values.getAsInteger(MoviesContract.MovieEntry.COLUMN_ID);
/*      String title = values.getAsString(MoviesContract.MovieEntry.COLUMN_TITLE);
        String cover = values.getAsString(MoviesContract.MovieEntry.COLUMN_COVER);
        String date = values.getAsString(MoviesContract.MovieEntry.COLUMN_YEAR);
        String rating = values.getAsString(MoviesContract.MovieEntry.COLUMN_RATING);
        String synopsis = values.getAsString(MoviesContract.MovieEntry.COLUMN_SYNOPSIS);*/

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //Inserts values in the table and receives the number of the new row inserted or -1 if there was an error inserting
        long rowId = database.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);

        if (rowId == -1) {
            Log.d(LOG_TAG, getContext().getResources().getString(R.string.log_failed_to_insert_row) + uri);
            return null;
        }
        //Notify all listeners that the content has change for the uri
        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return updateMovie(uri, contentValues, selection, selectionArgs);
            case FAVORITE_ID:
                // For the FAVORITE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "COLUMN_ID=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MoviesContract.MovieEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMovie(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.update_not_supported) + uri);
        }
    }

    /**
     * Update movies in the database with the given content values. Apply the changes to the row
     * specified in the selection and selection arguments.
     * Return the number of rows that were successfully updated.
     */
    private int updateMovie(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int id;
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        //Check if values has contents for the different elements and validate the contents
        if (values.containsKey(MoviesContract.MovieEntry.COLUMN_ID)) {
            //get content from values object
            id = values.getAsInteger(MoviesContract.MovieEntry.COLUMN_ID);
            //check id not null
            if (id == -1) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.validation_id_null));
            }
        }
        //Check if values has contents for the different elements and validate the contents
        if (values.containsKey(MoviesContract.MovieEntry.COLUMN_TITLE)) {
            //get content from values object
            String title = values.getAsString(MoviesContract.MovieEntry.COLUMN_TITLE);
            //check title not null
            if (title == null) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.validation_title_null));
            }
        }
        if (values.containsKey(MoviesContract.MovieEntry.COLUMN_COVER)) {
            //get content from values object
            String cover = values.getAsString(MoviesContract.MovieEntry.COLUMN_COVER);
            //check cover not null
            if (cover == null) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.validation_cover_null));
            }
        }
        if (values.containsKey(MoviesContract.MovieEntry.COLUMN_YEAR)) {
            //get content from values object
            Integer date = values.getAsInteger(MoviesContract.MovieEntry.COLUMN_YEAR);
            //check date not null
            if (date == null ) {
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.validation_date_null));
            }
        }
        if (values.containsKey(MoviesContract.MovieEntry.COLUMN_RATING)) {
            //get content from values object
            Integer rating = values.getAsInteger(MoviesContract.MovieEntry.COLUMN_RATING);
            //check rating not null
            if (rating == null ) {
                new IllegalArgumentException(getContext().getResources().getString(R.string.validation_rating_null));
            }
        }
        if (values.containsKey(MoviesContract.MovieEntry.COLUMN_SYNOPSIS)) {
            //get content from values object
            Integer synopsis = values.getAsInteger(MoviesContract.MovieEntry.COLUMN_SYNOPSIS);
            //check synopsis not null
            if (synopsis == null) {
                new IllegalArgumentException(getContext().getResources().getString(R.string.validation_synopsis_null));
            }
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated > 0) {
            //Notify all listeners that the content has change for the uri
            getContext().getContentResolver().notifyChange(uri, null);
        }

        //Return the number of rows that were affected
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                // Delete all rows that match the selection and selection args
                numRowsDeleted = database.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE_ID:
                // Delete a single row given by the ID in the URI
                selection = MoviesContract.MovieEntry.COLUMN_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                numRowsDeleted = database.delete(MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.deletion_not_supported) + uri);
        }

        if (numRowsDeleted > 0) {
            //Notify all listeners that the content has change for the uri
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return MoviesContract.MovieEntry.CONTENT_LIST_TYPE;
            case FAVORITE_ID:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(getContext().getResources().getString(R.string.unknown_uri) + " " + uri + " " + getContext().getResources().getString(R.string.with_match) + " " + match);
        }
    }
}
