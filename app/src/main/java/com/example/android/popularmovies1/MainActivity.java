package com.example.android.popularmovies1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.popularmovies1.data.MoviesContract;
import com.example.android.popularmovies1.Model.Movie;
import com.example.android.popularmovies1.Utilities.NetworkUtils;
import com.example.android.popularmovies1.Utilities.TheMovieDbJsonUtils;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //For console.log
    //private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MOVIE_ID = "Movie Id";
    public static final String EXTRA_ORIGINAL_TITLE = "Original title";
    public static final String EXTRA_SYNOPSIS = "Synopsis";
    public static final String EXTRA_DATE = "Release date";
    public static final String EXTRA_RATING = "User rating";
    public static final String EXTRA_POSTER_PATH = "Poster path";
    // Selection criteria
    private static final String SELECTION = null;
    /**
     * The Movie Db API response provides only a relative path to the movie poster images,
     * for example: “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”
     * This is the Base url we need to add to that relative path in order to have an absolute path
     */
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    /**
     * Recommended image size for most phones. To be added after IMAGE_BASE_URL and the relative path
     */
    private static final String IMAGE_SIZE = "w185";
    private static final String COMPLETE_IMAGE_BASE_URL = IMAGE_BASE_URL + IMAGE_SIZE;
    //Variable to store sortBy value
    private static String sortBy;
    //Array of Movie objects
    private static Movie[] moviesArray;
    //Adapter declaration
    private static MoviePosterAdapter mAdapter;
    private final String NO_FAVORITES = "NoFavorites";
    private final String NO_INTERNET = "NoInternet";
    //Projection - rows that we will retrieve
    private final String[] projection = {
            MoviesContract.MovieEntry.COLUMN_ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_COVER,
            MoviesContract.MovieEntry.COLUMN_YEAR,
            MoviesContract.MovieEntry.COLUMN_RATING,
            MoviesContract.MovieEntry.COLUMN_SYNOPSIS
    };
    //Views declaration
    private TextView noInternetOrFavorites;
    private GridView mGridView;

    //Updates the adapter and notify changes
    private static void updateAdapter(String[] posterPaths) {
        mAdapter.updateMovies(posterPaths);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Finds the Views
        mGridView = findViewById(R.id.grid_view);
        noInternetOrFavorites = findViewById(R.id.no_internet_tv);

        //Get preferences settings
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.settings_order_by_label), MODE_PRIVATE);
        String restoredText = prefs.getString(getResources().getString(R.string.settings_title), null);
        if (restoredText != null) {
            sortBy = prefs.getString(getResources().getString(R.string.settings_title), getResources().getString(R.string.settings_order_by_most_popular_value));//"Popular" is the default value.
        }
        //Creates the adapter
        mAdapter = new MoviePosterAdapter(MainActivity.this, new ArrayList<String>());
        //Sets the adapter
        mGridView.setAdapter(mAdapter);

        String FAVORITES = "favorites";
        if (sortBy.equals(FAVORITES)) {
            queryFavoritesInDB();
        } else {
            //Checks if there is internet connection and depending on the result performs different tasks
            checkConnectionAndPerformCorrespondingTasks();
        }
    }

    // Override onCreateOptionsMenu to inflate the menu for this Activity
    // Return true to display the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    //Checks when the menu buttons are pressed and performs the corresponding tasks for each button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Get the id of the item that was clicked
        int itemThatWasClickedId = item.getItemId();

        switch (itemThatWasClickedId) {
            case R.id.popular:
                sortBy = getResources().getString(R.string.settings_order_by_most_popular_value);
                setPreferences(sortBy);
                checkConnectionAndPerformCorrespondingTasks();
                return true;
            case R.id.top_rated:
                sortBy = getResources().getString(R.string.settings_order_by_top_rated_value);
                setPreferences(sortBy);
                checkConnectionAndPerformCorrespondingTasks();
                return true;
            case R.id.favorites:
                sortBy = getResources().getString(R.string.settings_favorites_value);
                setPreferences(sortBy);
                //Query DB to load favorite Movies' posters
                queryFavoritesInDB();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPreferences(String sortByMenu) {
        SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.settings_order_by_label), MODE_PRIVATE).edit();
        editor.putString(getResources().getString(R.string.settings_title), sortByMenu);
        editor.apply();
    }

    private void loadMovieData() {
        //Execute AsyncTask to call api in background
        new FetchMovieTask(MainActivity.this).execute(sortBy);
    }

    //Performs all the task needed when there is internet connection
    private void tasksToDoWhenThereIsConnection() {
        //Sets to invisible the TextView that informs the user if there is a problem with connection
        noInternetOrFavorites.setVisibility(View.INVISIBLE);
        //Shows GridView
        mGridView.setVisibility(View.VISIBLE);
        // Call loadMovieData to perform the network request to get the movies info
        /* Once all of our views are setup, we can load the movies data. */
        loadMovieData();

        //Loads onClickListener on GridView
        loadGridViewListener();
    }

    //Performs all the task needed when there is internet connection
    private void tasksToDoWhenThereIsNotConnectionOrFavorites(String problem) {
        //Hides GridView
        mGridView.setVisibility(View.GONE);
        //Sets to visible the TextView that will show a message indicating the problem to the user
        noInternetOrFavorites.setVisibility(View.VISIBLE);

        //Add the message to the TextView depending on the problem
        switch (problem) {
            case NO_FAVORITES:
                noInternetOrFavorites.setText(R.string.no_favorites);
                break;
            case NO_INTERNET:
                noInternetOrFavorites.setText(R.string.no_internet_connection_available);
                break;
        }
    }

    private void checkConnectionAndPerformCorrespondingTasks() {
        //Checks if there is connection available. Returns true if there is connection and false if not.
        boolean thereIsConnection = NetworkUtils.isNetworkAvailable(this);

        if (thereIsConnection) { //If there is internet connection
            //Performs all the task needed when there is connection
            tasksToDoWhenThereIsConnection();
        } else { //If there is no internet connection
            tasksToDoWhenThereIsNotConnectionOrFavorites(NO_INTERNET);
        }
    }

    private void queryFavoritesInDB() {
        Cursor c = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, projection, SELECTION, null, null);
        if (!c.moveToFirst()) {
            tasksToDoWhenThereIsNotConnectionOrFavorites(NO_FAVORITES);
        } else {
            //load cursor data in moviesArray
            int idColumn = c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_ID);
            int titleColumn = c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
            int coverColumn = c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_COVER);
            int dateColumn = c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_YEAR);
            int ratingColumn = c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING);
            int synopsisColumn = c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_SYNOPSIS);

            moviesArray = new Movie[c.getCount()];
            while (c.moveToNext()) {
                //Gets cursor position
                int i = c.getPosition();

                //Gets data from cursor in that position
                String favId = c.getString(idColumn);
                String favTitle = c.getString(titleColumn);
                String favCover = c.getString(coverColumn);
                String favDate = c.getString(dateColumn);
                String favRating = c.getString(ratingColumn);
                String favSynopsis = c.getString(synopsisColumn);

                c.close();

                //Stores data into a movie object
                Movie movie = new Movie(favId, favTitle, favCover, favSynopsis, favRating, favDate);

                //Stores the movie object in an array ob objects
                moviesArray[i] = movie;
            }

            //Array of Strings to store poster paths
            String[] posterPaths = new String[moviesArray.length];

            //Stores in each element of posterPaths array one path string extracted from the Movie Object moviesArray
            for (int i = 0; i < posterPaths.length; i++) {
                posterPaths[i] = moviesArray[i].getPosterPath();
            }

            //Updates the adapter and notify changes
            updateAdapter(posterPaths);

            //Loads onClickListener on GridView
            loadGridViewListener();
        }
    }

    private void loadGridViewListener() {
        //Sets an onItemClickListener on GridView items in order to get the position when the user clicks on one poster image
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Get info of the movie in the position clicked from moviesArray
                String movieId = moviesArray[position].getMovieId();
                String originalTitle = moviesArray[position].getOriginalTitle();
                String synopsis = moviesArray[position].getSynopsis();
                String date = moviesArray[position].getReleaseDate();
                String rating = moviesArray[position].getUserRating();
                String posterPath = moviesArray[position].getPosterPath();

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(EXTRA_MOVIE_ID, movieId);
                intent.putExtra(EXTRA_ORIGINAL_TITLE, originalTitle);
                intent.putExtra(EXTRA_SYNOPSIS, synopsis);
                intent.putExtra(EXTRA_DATE, date);
                intent.putExtra(EXTRA_RATING, rating);
                intent.putExtra(EXTRA_POSTER_PATH, posterPath);
                startActivity(intent);
            }
        });
    }

    // Class that extends AsyncTask to perform network requests in background
    static class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        // Weak references will still allow the Activity to be garbage-collected
        //Source: several StackOverFlow articles
        private final WeakReference<Activity> weakActivity;

        // only retain a weak reference to the activity
        FetchMovieTask(Activity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }

        //To do in background
        @Override
        protected String[] doInBackground(String... params) {

            Activity activity = weakActivity.get();

            sortBy = params[0];

            //Form query URL passing sortBy parameter
            URL movieRequestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                //Try getting data from Api and stores them in jsonMovieResponse
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                //Extracts data from the web response and stores them in an array of Movie objects
                moviesArray = TheMovieDbJsonUtils.parseJsonMovieResponse(activity, jsonMovieResponse);

                //TODO later update rating movie info into Favorites table in DB with Internet data

                //Define an array of Strings to store poster paths equal to moviesArray size
                String[] posterPaths = new String[moviesArray.length];

                //Stores in each element of posterPaths array one path string extracted from the Movie Object moviesArray
                for (int i = 0; i < posterPaths.length; i++) {
                    moviesArray[i].setPosterPath(COMPLETE_IMAGE_BASE_URL + moviesArray[i].getPosterPath());
                    posterPaths[i] = moviesArray[i].getPosterPath();
                }

                return posterPaths;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] posterPaths) {
            Activity activity = weakActivity.get();
            if (activity == null
                    || activity.isFinishing()) {
                // activity is no longer valid, don't do anything!
                return;
            }
            if (posterPaths != null) {
                //Updates the adapter and notify changes
                updateAdapter(posterPaths);
            }
        }
    }
}
