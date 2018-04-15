package com.example.android.popularmovies1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MoviesDbHelper extends SQLiteOpenHelper {
    // Database name
    private static final String DB_NAME = "movies.db";
    // Database version
    private static final int DB_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                    MoviesContract.MovieEntry.COLUMN_ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP +
                    MoviesContract.MovieEntry.COLUMN_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    MoviesContract.MovieEntry.COLUMN_COVER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    MoviesContract.MovieEntry.COLUMN_YEAR + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    MoviesContract.MovieEntry.COLUMN_RATING + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    MoviesContract.MovieEntry.COLUMN_SYNOPSIS + TEXT_TYPE + NOT_NULL+ ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME;

    public MoviesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
