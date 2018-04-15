package com.example.android.popularmovies1.Model;

/**
 * Class to instantiate Review objects
 * Created by arcoiris on 26/03/2018.
 */

public class Review {
    private final String author;
    private final String reviewText;
    private final String reviewUrl;

    /* Constructor */
    public Review(String reviewId, String author, String reviewText, String reviewUrl) {
        String reviewId1 = reviewId;
        this.author = author;
        this.reviewText = reviewText;
        this.reviewUrl = reviewUrl;
    }

// --Commented out by Inspection START (15/04/2018 16:14):
//    /* Getter and Setter methods */
//    public String getReviewId() {
//        return reviewId;
//    }
// --Commented out by Inspection STOP (15/04/2018 16:14)

    /*
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    */

    public String getAuthor() {
        return author;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getReviewUrl() {
        return reviewUrl;
    }
}
