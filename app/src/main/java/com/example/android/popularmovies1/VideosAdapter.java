package com.example.android.popularmovies1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class VideosAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<String> mVideosArrayList;

    public VideosAdapter(Context mContext,  ArrayList<String> items) {
        this.mContext = mContext;
        this.mVideosArrayList = items;
    }

    @Override
    public int getCount() {
        return mVideosArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.videos_list_item_view, viewGroup,false);
        }

        //find view
        TextView mVideoTitle = view.findViewById(R.id.video_title_tv);

        String videoTitle = mVideosArrayList.get(position);

        //Set content in view
        mVideoTitle.setText(videoTitle);

        return view;
    }

    public void updateVideos(String[] videos) {
        mVideosArrayList.clear();
        Collections.addAll(mVideosArrayList,videos);
    }
}
