package com.example.android.popularmovies1;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.example.android.popularmovies1.data.MoviesContract;
import com.example.android.popularmovies1.Model.Movie;
import com.example.android.popularmovies1.Model.Review;
import com.example.android.popularmovies1.Model.Video;
import com.example.android.popularmovies1.Utilities.NetworkUtils;
import com.example.android.popularmovies1.Utilities.TheMovieDbJsonUtils;
import com.example.android.popularmovies1.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    //Create a data binding instance called mBinding of type ActivityMainBinding
    private static ActivityDetailBinding mBinding;

    //Variables to store the data passed in the intent
    private static String id = "";

    private static String title = "";
    private static String synopsis = "";
    private static String date = "";
    private static String rating = "";
    private static String path = "";

    //Array of Video objects
    private static Video[] videosArray;
    private static Review[] reviewsArray;

    //Adapters declaration
    private static VideosAdapter mVideosAdapter;
    private static ReviewsAdapter mReviewsAdapter;

    //Views declaration
    private static ListView mVideosListView;
    private static ListView mReviewsListView;
    //Base url to reproduce YouTube videos
    private final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    //Uri for the current movie on which the user made click
    private Uri mCurrentMovieUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Get the intent
        Intent intent = getIntent();

        //If the intent has extra data, get them from the intent and store them in the variables
        if (intent.hasExtra(MainActivity.EXTRA_MOVIE_ID)) {
            id = intent.getStringExtra(MainActivity.EXTRA_MOVIE_ID);
        }
        if (intent.hasExtra(MainActivity.EXTRA_ORIGINAL_TITLE)) {
            title = intent.getStringExtra(MainActivity.EXTRA_ORIGINAL_TITLE);
        }
        if (intent.hasExtra(MainActivity.EXTRA_SYNOPSIS)) {
            synopsis = intent.getStringExtra(MainActivity.EXTRA_SYNOPSIS);
        }
        if (intent.hasExtra(MainActivity.EXTRA_DATE)) {
            date = intent.getStringExtra(MainActivity.EXTRA_DATE);
        }
        if (intent.hasExtra(MainActivity.EXTRA_RATING)) {
            rating = intent.getStringExtra(MainActivity.EXTRA_RATING);
        }
        if (intent.hasExtra(MainActivity.EXTRA_POSTER_PATH)) {
            path = intent.getStringExtra(MainActivity.EXTRA_POSTER_PATH);
        }

        //Store the data passed with the intent in a movie object
        Movie movie = new Movie(id, title, path, synopsis, rating, date);

        // Set the Content View using DataBindingUtil to the activity_detail layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        //Find videos List view
        mVideosListView = mBinding.videosLv;
        //Find reviews List view
        mReviewsListView = mBinding.reviewsLv;
        //Find favorites Toggle Button
        ToggleButton mFavoritesToggleButton = findViewById(R.id.favorite_toggle);

        /*Check if the movie is on Favorites or not*/
        //Form the query Uri
        mCurrentMovieUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI, Integer.parseInt(id));
        //Do the query
        Cursor c = getContentResolver().query(mCurrentMovieUri, null, null, null, null);
        //If it is
        assert c != null;
        if (c.getCount() > 0) {
            //Sets the toggle button to checked
            mFavoritesToggleButton.setChecked(true);
        }

        c.close();

        //Sets an onclickListener on toggle button
        mFavoritesToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { //Saves the movie info into favorites table in DB
                    //Add movie info to values
                    ContentValues values = new ContentValues();
                    values.put(MoviesContract.MovieEntry.COLUMN_ID, Integer.parseInt(id));
                    values.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
                    values.put(MoviesContract.MovieEntry.COLUMN_COVER, path);
                    values.put(MoviesContract.MovieEntry.COLUMN_YEAR, date);
                    values.put(MoviesContract.MovieEntry.COLUMN_RATING, rating);
                    values.put(MoviesContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);

                    //Insert values into db
                    getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, values);

                } else { //Deletes movie info from favorites table in DB
                    getContentResolver().delete(mCurrentMovieUri, null, null);

                }
            }
        });

        //Show movie info in layout
        displayMovieInfo(movie);

        //Checks if there is internet connection and depending on the result performs different tasks
        checkConnectionAndPerformCorrespondingTasks();

        //Initialize the adapters
        mVideosAdapter = new VideosAdapter(DetailActivity.this, new ArrayList<String>());
        mReviewsAdapter = new ReviewsAdapter(DetailActivity.this, new ArrayList<Review>());

        //Relates the adapters to the corresponding ListView
        mVideosListView.setAdapter(mVideosAdapter);
        mReviewsListView.setAdapter(mReviewsAdapter);

    }

    //Shows movie info in the layout
    private void displayMovieInfo(Movie movieInfo) {
        // Use mBinding to set the Text in all the textViews using the data in info
        mBinding.title.setText(movieInfo.getOriginalTitle());
        mBinding.date.setText(movieInfo.getReleaseDate().substring(0, 4));
        String ratingString = getResources().getString(R.string.user_rating);
        mBinding.rating.setText(String.format(ratingString, movieInfo.getUserRating()));
        mBinding.synopsis.setText(movieInfo.getSynopsis());
        //Use Picasso and mBinding to show the image into imageView
        Picasso.with(this).load(movieInfo.getPosterPath()).into(mBinding.poster);
    }

    //Call to AsyncTasks to load related content from Internet
    private void loadMovieRelatedContent() {
        //Execute AsyncTask to call api in background
        new DetailActivity.FetchVideosTask(DetailActivity.this).execute();
        new DetailActivity.FetchReviewsTask(DetailActivity.this).execute();
    }

    private void checkConnectionAndPerformCorrespondingTasks() {
        //Checks if there is connection available. Returns true if there is connection and false if not.
        boolean thereIsConnection = NetworkUtils.isNetworkAvailable(this);

        if (thereIsConnection) { //If there is internet connection
            //Performs all the task needed when there is connection
            tasksToDoWhenThereIsConnection();
        } else { //If there is no internet connection
            tasksToDoWhenThereIsNotConnection();
        }
    }

    //Performs all the task needed when there is internet connection
    private void tasksToDoWhenThereIsNotConnection() {
        //Hides videos list View
        mBinding.videosLv.setVisibility(View.GONE);
        //Sets videos label to Related content label
        mBinding.videosLabel.setText(R.string.related_content_label);
        //Sets to visible the TextView that will show a message indicating the problem to the user
        mBinding.videosErrorMessage.setVisibility(View.VISIBLE);
        //Hides reviews label TextView
        mBinding.reviewsLabel.setVisibility(View.GONE);

        //Add the error message to the TextView
        mBinding.videosErrorMessage.setText(R.string.related_content_no_internet_connection_available);
    }

    //Performs all the task needed when there is internet connection
    private void tasksToDoWhenThereIsConnection() {
        //Sets to invisible the TextView that informs the user if there is a problem with connection
        mBinding.videosErrorMessage.setVisibility(View.GONE);
        //Sets videos label to Videos Label
        mBinding.videosLabel.setText(R.string.videos_label);
        //Shows videos List View
        mBinding.videosLv.setVisibility(View.VISIBLE);
        //Shows review label
        mBinding.reviewsLabel.setVisibility(View.VISIBLE);

        // Call loadMovieRelatedContent to perform the network request to get the videos and reviews info
        loadMovieRelatedContent();

        //Onclick plays video
        mVideosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Get info of the video in the position clicked from videosArray
                String videoKey = videosArray[position].getVideoKey();

                //Url to be open with intent
                String videoUrl = YOUTUBE_URL + videoKey;

                //Intent that opens the selected video url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(videoUrl));
                startActivity(intent);
            }
        });

        mReviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String reviewUrl = reviewsArray[position].getReviewUrl();

                //Intent that opens the selected video url
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(reviewUrl));
                startActivity(intent);
            }
        });
    }

    // Class that extends AsyncTask to perform network requests in background - To get Videos info
    static class FetchVideosTask extends AsyncTask<String, Void, String[]> {

        // Weak references will still allow the Activity to be garbage-collected
        //Source: several StackOverFlow articles
        private final WeakReference<Activity> weakActivity;

        // only retain a weak reference to the activity
        FetchVideosTask(Activity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }

        //To do in background
        @Override
        protected String[] doInBackground(String... params) {

            Activity activity = weakActivity.get();

            String contentType = activity.getString(R.string.videos);

            //Form query URLs passing "videos" and "reviews" parameters
            URL videosRequestUrl = NetworkUtils.buildUrl(id, contentType);

            try {
                //Try getting Videos data from Api and store them in jsonVideosResponse
                String jsonVideosResponse = NetworkUtils
                        .getResponseFromHttpUrl(videosRequestUrl);

                //Extracts video data from the web response and stores them in an array of strings
                videosArray = TheMovieDbJsonUtils.parseJsonVideosResponse(activity, jsonVideosResponse);

                //Array of Strings to store video titles
                String[] videoTitles = new String[videosArray.length];

                //Stores in each element of videoTitles array one title string extracted from the Video Object videosArray
                for (int i = 0; i < videosArray.length; i++) {
                    videoTitles[i] = videosArray[i].getVideoTitle();
                }

                return videoTitles;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] videos) {
            Activity activity = weakActivity.get();

            if (activity == null
                    || activity.isFinishing()) {
                // activity is no longer valid, don't do anything!
                return;
            }
            if (videos != null) {
                //Updates the adapter and notify changes
                mVideosAdapter.updateVideos(videos);
                mVideosAdapter.notifyDataSetChanged();

                //Gets the number of videos in the array
                int videosCount = mVideosAdapter.getCount();

                //Adapts the ListView height to that number of videos
                LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 180 * videosCount);
                mVideosListView.setLayoutParams(mParam);
            }
        }
    }

    // Class that extends AsyncTask to perform network requests in background - To get Videos info
    static class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {
        // Weak references will still allow the Activity to be garbage-collected
        //Source: several StackOverFlow articles
        private final WeakReference<Activity> weakActivity;

        // only retain a weak reference to the activity
        FetchReviewsTask(Activity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }

        @Override
        protected void onPostExecute(Review[] reviews) {
            Activity activity = weakActivity.get();

            if (activity == null
                    || activity.isFinishing()) {
                // activity is no longer valid, don't do anything!
                return;
            }
            if (reviews != null) {
                //Loads the adapter with data and notify that data have changed
                mReviewsAdapter.addAll(reviews);
                mReviewsAdapter.notifyDataSetChanged();

                //Gets the number of reviews in the array
                int reviewsCount = mReviewsAdapter.getCount();

                if (reviewsCount > 0) {
                    //Adapts the ListView height to that number of reviews
                    LinearLayout.LayoutParams mParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 450 * reviewsCount);
                    mReviewsListView.setLayoutParams(mParam);
                } else {
                    mBinding.reviewsLabel.setVisibility(View.GONE);
                }
            }
        }

        //To do in background
        @Override
        protected Review[] doInBackground(String... params) {

            Activity activity = weakActivity.get();

            String contentType = activity.getString(R.string.reviews);

            //Form query URLs passing "videos" and "reviews" parameters
            URL reviewsRequestUrl = NetworkUtils.buildUrl(id, contentType);

            try {
                //Try getting Videos data from Api and store them in jsonVideosResponse
                String jsonReviewsResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewsRequestUrl);

                //Extracts video data from the web response and stores them in an array of review objects
                reviewsArray = TheMovieDbJsonUtils.parseJsonReviewsResponse(activity, jsonReviewsResponse);

                //Returns an array of Review objects
                return reviewsArray;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
