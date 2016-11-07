package com.newstee.model.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.newstee.R;

/**
 * Created by Arnold on 02.11.2016.
 */

public  class TagItem {
    public final String id;
    public final String title;
    public final boolean isAdded;
    public final Context appContext;


    public TagItem(Context appContext, String id, String title, boolean isAdded) {
        this.appContext = appContext;
        this.id = id;
        this.title = title;
        this.isAdded = isAdded;
    }

    public Drawable getButtonSrc() {
        Drawable dr;

        if (isAdded) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dr = appContext.getResources().getDrawable(R.drawable.ic_is_added, null);
            } else {
                dr = appContext.getResources().getDrawable(R.drawable.ic_is_added);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dr = appContext.getResources().getDrawable(R.drawable.news_to_add_button, null);
            } else {
                dr = appContext.getResources().getDrawable(R.drawable.news_to_add_button);
            }
        }

        return dr;

    }
}