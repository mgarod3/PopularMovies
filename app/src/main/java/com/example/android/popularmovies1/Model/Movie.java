package com.example.android.popularmovies1.Model;

/**
 * Class to instantiate Movie objects
 * Created by arcoiris on 01/03/2018.
 */

public class Movie {
    /* String variables to store each movie data */
    private final String movieId;
    private final String originalTitle;
    private String posterPath;
    private final String synopsis;
    private final String userRating;
    private final String releaseDate;

    /* Constructor */
    public Movie(String movieId, String originalTitle, String posterPath, String synopsis, String userRating, String releaseDate) {
        this.movieId = movieId;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    /* Getter and Setter methods */

    public String getMovieId() {
        return movieId;
    }

// --Commented out by Inspection START (15/04/2018 16:14):
//    public void setMovieId(String movieId) {
//        this.movieId = movieId;
//    }
// --Commented out by Inspection STOP (15/04/2018 16:14)

    public String getOriginalTitle() {
        return originalTitle;
    }


// --Commented out by Inspection START (15/04/2018 16:14):
//    public void setOriginalTitle(String originalTitle) {
//        this.originalTitle = originalTitle;
//    }
// --Commented out by Inspection STOP (15/04/2018 16:14)

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

// --Commented out by Inspection START (15/04/2018 16:14):
//    public void setSynopsis(String synopsis) {
//        this.synopsis = synopsis;
//    }
// --Commented out by Inspection STOP (15/04/2018 16:14)

    public String getUserRating() {
        return userRating;
    }

// --Commented out by Inspection START (15/04/2018 16:14):
//    public void setUserRating(String userRating) {
//        this.userRating = userRating;
//    }
// --Commented out by Inspection STOP (15/04/2018 16:14)

    public String getReleaseDate() {
        return releaseDate;
    }

// --Commented out by Inspection START (15/04/2018 16:14):
//    public void setReleaseDate(String releaseDate) {
//        this.releaseDate = releaseDate;
//    }
// --Commented out by Inspection STOP (15/04/2018 16:14)
}
