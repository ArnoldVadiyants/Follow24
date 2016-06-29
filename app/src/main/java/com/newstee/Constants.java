package com.newstee;

import com.vk.sdk.VKScope;

/**
 * Created by Arnold on 24.02.2016.
 */
public class Constants {

    public final static String EMPTY_LINK_IMAGE = "https://pp.vk.me/c633526/v633526270/25483/J3y6ShLQxbA.jpg";
    public final static int STATUS_NOT_ADDED = 0;
    public final static int STATUS_WAS_ADDED = 1;
    public final static int STATUS_IS_PLAYING = 2;
    public final static String RESULT_SUCCESS = "SUCCESS";
    public final static String RESULT_FAIL = "FAILURE";
    public final static String CATEGORY_ALL = "All";
    public final static String CATEGORY_NEWS = "News";
    public final static String CATEGORY_ARTICLE = "Articles";
    public final static String CATEGORY_STORY = "Story";
    public final static String ARGUMENT_NONE = "none";
    public final static String ARGUMENT_NEWS_ADDED = "added";
    public final static String ARGUMENT_NEWS_LIKED = "liked";
    public final static String ARGUMENT_NEWS_BY_STORY = "by_story";
    public final static String ARGUMENT_NEWS_BY_TAG= "by_tag";
    public final static String ARGUMENT_NEWS_BY_CANAL = "by_canal";
    public final static String ARGUMENT_NEWS_RECOMMENDED = "recommended";
    public final static String ARGUMENT_NEWS_RECENT = "recent";
    public final static String ARGUMENT_PLAYLIST = "playlist";
    public static final String[] SCOPE = new String[]{VKScope.WALL,VKScope.PHOTOS
    };



}
