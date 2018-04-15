package com.example.android.popularmovies1.Utilities;

import android.content.Context;

import com.example.android.popularmovies1.Model.Movie;
import com.example.android.popularmovies1.Model.Review;
import com.example.android.popularmovies1.Model.Video;
import com.example.android.popularmovies1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility functions to handle TheMovieDb JSON data.
 * Created by arcoiris on 28/02/2018.
 */

public final class TheMovieDbJsonUtils {
    //For console.log
    //private static final String TAG = TheMovieDbJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from the web response and returns an array of Movie Objects
     * containing each of those objects one movie info
     *
     * @param jsonMovieResponse JSON response from server
     * @return Array of Movie Objects
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Movie[] parseJsonMovieResponse(Context context, String jsonMovieResponse)
            throws JSONException {

        //Api field names
        final String API_MOVIE_ID = context.getString(R.string.api_movie_id);
        final String API_RESULTS = context.getString(R.string.api_results);
        final String API_ORIGINAL_TITLE = context.getString(R.string.api_original_title);
        final String API_POSTER_PATH = context.getString(R.string.api_poster_path);
        final String API_SYNOPSIS = context.getString(R.string.api_synopsis);
        final String API_USER_RATING = context.getString(R.string.api_user_rating);
        final String API_RELEASE_DATE = context.getString(R.string.api_release_date);

        //Creates Json object from web response
        JSONObject movieJson = new JSONObject(jsonMovieResponse);
        //Extracts the json array "results"
        JSONArray moviesArray = movieJson.getJSONArray(API_RESULTS);

        //Array of Objects to store Movie objects
        Movie[] movieObjectsArray = new Movie[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++) {
             /* Strings to store each movie data */
            String movieId;
            String originalTitle;
            String posterPath;
            String synopsis;
            String userRating;
            String releaseDate;

             /* Get the JSON object representing the movie i */
            JSONObject movieObject = moviesArray.getJSONObject(i);

            /* Get the string values from the JSON object representing the movie */
            movieId = movieObject.getString(API_MOVIE_ID);
            originalTitle = movieObject.getString(API_ORIGINAL_TITLE);
            posterPath = movieObject.getString(API_POSTER_PATH);
            synopsis = movieObject.getString(API_SYNOPSIS);
            userRating = movieObject.getString(API_USER_RATING);
            releaseDate = movieObject.getString(API_RELEASE_DATE);

            //Create a new instance of Movie and store it in the position i of the array of Movie objects
            movieObjectsArray[i] = new Movie(movieId,originalTitle, posterPath, synopsis, userRating, releaseDate);
        }
        return movieObjectsArray;
    }

    /**
     * This method parses JSON from the web response and returns an array of Video objects
     *
     * @param jsonVideosResponse JSON response from server
     * @return Array of Strings
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Video[] parseJsonVideosResponse(Context context, String jsonVideosResponse)
            throws JSONException {
        //Api field names
        final String API_RESULTS = context.getString(R.string.api_results);
        final String API_VIDEO_NAME = context.getString(R.string.api_video_name);
        final String API_VIDEO_KEY = context.getString(R.string.api_video_key);

        //Creates Json object from web response
        JSONObject videosJson = new JSONObject(jsonVideosResponse);
        //Extracts the json array "results"
        JSONArray videosArray = videosJson.getJSONArray(API_RESULTS);

        //Array of Objects to store Movie objects
        Video[] videoObjectsArray = new Video[videosArray.length()];

        //String[][] videos = new String[videosArray.length()][2];

        for (int i=0; i<videosArray.length();i++){
            /* Strings to store each movie data */
            String videoTitle;
            String videoKey;

            /* Get the JSON object representing the video i */
            JSONObject videoObject = videosArray.getJSONObject(i);

            /* Get the string values from the JSON object representing the video */
            videoTitle = videoObject.getString(API_VIDEO_NAME);
            videoKey = videoObject.getString(API_VIDEO_KEY);

            //Create a new instance of Video and store it in the position i of the array of Video objects
            videoObjectsArray[i] = new Video(videoTitle,videoKey);
        }

        return videoObjectsArray;
    }

    /**
     * This method parses JSON from the web response and returns an object with reviews info
     *
     * @param jsonReviewsResponse JSON response from server
     * @return Array of Strings
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static Review[] parseJsonReviewsResponse(Context context, String jsonReviewsResponse)
            throws JSONException {

        //Api field names
        final String API_RESULTS = context.getString(R.string.api_results);
        final String API_REVIEW_ID = context.getString(R.string.api_review_id);
        final String API_REVIEW_AUTHOR = context.getString(R.string.api_review_author);
        final String API_REVIEW_CONTENT = context.getString(R.string.api_review_content);
        final String API_REVIEW_URL = context.getString(R.string.api_review_url);

        //Creates Json object from web response
        JSONObject reviewJson = new JSONObject(jsonReviewsResponse);
        //Extracts the json array "results"
        JSONArray reviewsArray = reviewJson.getJSONArray(API_RESULTS);

        //Array of Objects to store Movie objects
        Review[] reviewObjectsArray = new Review[reviewsArray.length()];

        for (int i = 0; i < reviewsArray.length(); i++) {
             /* Strings to store each movie data */
            String reviewId;
            String author;
            String content;
            String url;

             /* Get the JSON object representing the movie i */
            JSONObject reviewObject = reviewsArray.getJSONObject(i);

            /* Get the string values from the JSON object representing the movie */
            reviewId = reviewObject.getString(API_REVIEW_ID);
            author = reviewObject.getString(API_REVIEW_AUTHOR);
            content = reviewObject.getString(API_REVIEW_CONTENT);
            url = reviewObject.getString(API_REVIEW_URL);

            //Create a new instance of Movie and store it in the position i of the array of Movie objects
            reviewObjectsArray[i] = new Review(reviewId, author, content, url);
        }
        return reviewObjectsArray;
    }
}
