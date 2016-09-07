package com.newstee.model.data;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Arnold on 16.04.2016.
 */
public abstract class NewsTeeDataLoader extends AsyncTask<String, String, Boolean> {
    Context mAppContext;
abstract void onPreLoad();
    abstract void onPostLoad();
   public NewsTeeDataLoader(Context context)
    {
        mAppContext = context;
    }


}
