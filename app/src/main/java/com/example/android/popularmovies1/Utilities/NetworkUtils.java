package com.example.android.popularmovies1.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.android.popularmovies1.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Utility functions to communicate with The Movie Db server
 * Created by arcoiris on 24/02/2018.
 */
public final class NetworkUtils {


    //For console.log
    //private static final String TAG = NetworkUtils.class.getSimpleName();
    //For URL creation
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "?api_key=" + BuildConfig.MY_MOVIE_DB_API_KEY;

    /**
     * Builds the URL that we are going to use to call the movies api to get the movie poster paths ordered
     * by popular or top rated movies
     *
     * @param sortBy popular or top rated.
     * @return The URL created
     */
    public static URL buildUrl(String sortBy) {

        String urlStr = BASE_URL + sortBy + API_KEY;

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL that we are going to use to call the movies api to get the videos and reviews
     * related with the movie
     *
     * @param movieId movie Id
     * @param contentType videos or reviews
     * @return The URL created
     */
    public static URL buildUrl(String movieId, String contentType) {

        String urlStr = BASE_URL + movieId + "/" + contentType + API_KEY;

        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * Code from Udacity Android Development Challenge Exercise S02.01 Networking
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    //Check if there is connection available. Returns true if there is connection and false if not.
    //Resource: https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
