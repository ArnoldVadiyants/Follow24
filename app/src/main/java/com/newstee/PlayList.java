package com.newstee;

import com.newstee.model.data.News;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnold on 29.04.2016.
 */
public class PlayList {

    private List<News>mNewsList = new ArrayList<>();


    private String mArgument = "";

    public String getArgument() {
        return mArgument;
    }

    public void setArgument(String mArgument) {
        this.mArgument = mArgument;
    }

    private String mListTitle = "";
    private News mCurrent;

    public List<News> getNewsList() {
        return mNewsList;
    }
    public int getPosition(String id)
    {
        if(id == null)
        {
            return -1;
        }
        String s = id.trim();
        for(News n : mNewsList)
        {
            if(n.getId()== null)
            {
                continue;
            }
           if(n.getId().trim().equals(s))
           {
               return mNewsList.indexOf(n);
           }
        }
        return  -1;
    }
    public News getNewsItem(String id) {
        for (News n : mNewsList) {
            if (n.getId().equals(id)) {
                return n;
            }
        }
        return null;
    }
    public String getListTitle() {
        return mListTitle;
    }
    public void setNewsList(List<News> mNewsList,String listTitle) {
        this.mNewsList = mNewsList;
        this.mListTitle = listTitle;
    }
    public boolean contains(News news)
    {
        if(mNewsList.contains(news))
        {
            return true;
        }
        else
        {
            for(News n : mNewsList )
            {
                if(n.getId().equals(news.getId()))
                {
                   return true;
                }
            }
        }
        return false;
    }

    public void addToPlayList(News news)
    {
       if(!contains(news))
       {
           mNewsList.add(news);
       }
    }



    public void deleteFromPlaylist(News news)
    {
        if(!mNewsList.remove(news))
        {
            for(News n : mNewsList )
            {
                if(n.getId().equals(news.getId()))
                {
                    mNewsList.remove(n);
                }
            }
        }
    }
    private PlayList()
    {

    }

    private static PlayList sPlayList;
    public static PlayList getInstance()
    {
        if(sPlayList == null)
        {
            sPlayList = new PlayList();
        }

            return sPlayList;
    }

    public News getCurrent() {
        return mCurrent;
    }

    public void setCurrent(News current) {
        this.mCurrent = current;
    }
}
