package com.example.android.popularmovies1;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resource used: https://developer.android.com/guide/topics/ui/layout/gridview.html
 * Created by arcoiris on 01/03/2018.
 */

class MoviePosterAdapter extends BaseAdapter {

    //For console.log
    //private static final String TAG = MainActivity.class.getSimpleName();

    private final Context mContext;
    private final List<String> mPosterPathsArrayList;


    public MoviePosterAdapter(Context mContext, ArrayList<String> items) {
        this.mContext = mContext;
        this.mPosterPathsArrayList = items;
    }

    @Override
    public int getCount() {
        return mPosterPathsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void updateMovies(String[] posterPaths) {
        mPosterPathsArrayList.clear();
        Collections.addAll(mPosterPathsArrayList, posterPaths);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        //Variable to store image path url
        String path;

        if (view == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //Get screen width - Source: https://stackoverflow.com/questions/10329673/gridview-layoutparams-to-be-the-same-in-different-screen-sizes-is-it-possible/10329787#10329787
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            //set imageView width to half the screen size and height to 3/4 of screen size
            imageView.setLayoutParams(new GridView.LayoutParams(screenWidth / 2, screenWidth / 2 + screenWidth / 4));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) view;
        }
        //Gets the path for the position from the poster paths arrayList
        path = mPosterPathsArrayList.get(position);
        //Loads the image for that path using Picasso
        Picasso.with(mContext).load(path).into(imageView);
        return imageView;
    }
}
