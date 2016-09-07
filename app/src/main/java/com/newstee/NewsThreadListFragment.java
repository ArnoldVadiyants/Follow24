package com.newstee;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Arnold on 24.02.2016.
 */
public class NewsThreadListFragment extends NewsListFragment {


    public static NewsListFragment newInstance(String argument, String category, String idForArgument) {
        NewsListFragment fragment = new NewsThreadListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_PARAMETER, argument);
        args.putString(ARG_NEWS_BY_ID, idForArgument);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewsListFragment newInstance(String argument,String category) {
        NewsListFragment fragment = new NewsThreadListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_PARAMETER,argument);
        fragment.setArguments(args);
        return fragment;
    }

    public NewsThreadListFragment() {
    }

    @Override
    public void setStatusImageButton(final ImageButton statusImageButton, final int newsStatus) {
        switch (newsStatus) {
            case Constants.STATUS_NOT_ADDED:
                statusImageButton.setImageResource(R.drawable.news_to_add_button);
                break;
            case Constants.STATUS_WAS_ADDED:
                statusImageButton.setImageResource(R.drawable.ic_is_added);
                break;
            case Constants.STATUS_IS_PLAYING:
             //   statusImageButton.setImageResource(R.drawable.news_is_playing_button);
                break;
        }

    }

    @Override
    String getEmpty() {
        return "";
    }

    @Override
    int getTextColor() {
        return getResources().getColor(android.R.color.background_dark);
    }
    @Override
    int getSecondaryTextColor() {
        return getResources().getColor(android.R.color.secondary_text_light);
    }
    @Override
    void setLikeView(TextView likeTextView, ImageView likeImageView, boolean isLiked) {
        int color1;
        int color2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color1 = getResources().getColor(android.R.color.holo_red_light,null);
            color2 = getResources().getColor(android.R.color.secondary_text_light,null);
        }
        else
        {
            color1 = getResources().getColor(android.R.color.holo_red_light);
            color2 = getResources().getColor(android.R.color.secondary_text_light);
        }
        if (isLiked) {
            likeTextView.setTextColor(color1);
            likeImageView.setImageResource(R.drawable.ic_is_liked);
        } else {
            likeTextView.setTextColor(color2);
            likeImageView.setImageResource(R.drawable.ic_like);
        }
    }


}
