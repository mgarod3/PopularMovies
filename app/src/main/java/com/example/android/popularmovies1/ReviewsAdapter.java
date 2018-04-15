package com.example.android.popularmovies1;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.popularmovies1.Model.Review;

import java.util.List;

class ReviewsAdapter extends ArrayAdapter<Review> {

    public ReviewsAdapter(Activity context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.reviews_list_item_view, parent, false);
        }
        //find views
        TextView mAuthorTv = convertView.findViewById(R.id.review_author_tv);
        TextView mTextTv = convertView.findViewById(R.id.review_content_tv);

        //Number of reviews
        int reviewsNum = getCount();

        //Get current review object
        Review currentReview = getItem(position);

        if (reviewsNum>0){

            //Extract author and content review info from object
            assert currentReview != null;
            String author = currentReview.getAuthor();
            String content = currentReview.getReviewText();

            //Shorten content
            if (content.length()>150) {
                content = content.substring(0, 150);
            }
            content = content + "... READ MORE";

            //Add text to the TextViews
            mAuthorTv.setText(author);
            mTextTv.setText(content);
        }





        //Return the View
        return convertView;

    }
}
