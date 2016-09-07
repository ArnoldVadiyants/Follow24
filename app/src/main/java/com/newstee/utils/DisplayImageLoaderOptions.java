package com.newstee.utils;

import com.newstee.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Arnold on 12.04.2016.
 */
public class DisplayImageLoaderOptions {
    private static    DisplayImageOptions sInstance;
    private static    DisplayImageOptions sRoundedInstance;
    private DisplayImageLoaderOptions() {

    }
    public static   DisplayImageOptions getInstance() {
        if (sInstance == null) {
            sInstance = new DisplayImageOptions.Builder()

                    .showImageOnLoading(R.drawable.loading_animation)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
        }
        return sInstance;
    }
    public static   DisplayImageOptions getRoundedInstance(){
        if (sRoundedInstance == null) {
            sRoundedInstance = new DisplayImageOptions.Builder()
                    .displayer(new RoundedBitmapDisplayer(100))
                   .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                    .showImageOnLoading(R.drawable.loading_animation)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
        }
        return sRoundedInstance;
    }

}
