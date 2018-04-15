package com.example.android.popularmovies1.Model;

/*Class to instantiate Video objects*/
public class Video {
    /* String variables to store each video data */
    private final String videoTitle;
    private final String videoKey;

    /* Constructor */
    public Video(String videoTitle, String videoKey) {
        this.videoTitle = videoTitle;
        this.videoKey = videoKey;
    }

    /* Getter and Setter methods */
    public String getVideoTitle() {
        return videoTitle;
    }

    public String getVideoKey() {
        return videoKey;
    }


}
