package com.newstee.model.data;

/**
 * Created by Arnold on 09.04.2016.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newstee.Constants;
import com.newstee.network.interfaces.NewsTeeApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NewsLab {

    private List<News> mNews = new ArrayList<>();
    private List<News> mArticles = new ArrayList<>();



    private List<News> mStories = new ArrayList<>();
    private List<News> mRecommendedNews = new ArrayList<>();
    private static NewsLab sNewsLab;
    private Gson gson = new GsonBuilder().create();
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(NewsTeeApiInterface.BASE_URL)
            .build();
    private NewsTeeApiInterface newsTeeApiInterface = retrofit.create(NewsTeeApiInterface.class);

    private NewsLab() {

        //  loadNews();
    }

    public static NewsLab getInstance() {
        if (sNewsLab == null) {
            sNewsLab = new NewsLab();
        }
        return sNewsLab;
    }

    /* public void loadNews()
     {

     }*/
    public List<News> getRecommendedNews() {
        return mRecommendedNews;
    }

    public void setRecommendedNews(List<News> mRecommendedNews) {
        this.mRecommendedNews = mRecommendedNews;
    }

       public List<News> getNews(String type) {
           if (type.equals(Constants.CATEGORY_NEWS)) {
               return mNews;
           } else if (type.equals(Constants.CATEGORY_STORY)) {
               return mStories;
           } else if (type.equals(Constants.CATEGORY_ARTICLE)) {
               return mArticles;
           }
           else
           {
               return new ArrayList<>();
           }

    }
    public List<News> getNews() {
        ArrayList<News>allNews = new ArrayList<>();
        allNews.addAll(mNews);
        allNews.addAll(mArticles);
        allNews.addAll(mStories);
        return allNews;
    }
    public void addNewses(List<News> news,String type) {
        if (type.equals(Constants.CATEGORY_NEWS)) {
           mNews.addAll(news);
        } else if (type.equals(Constants.CATEGORY_STORY)) {
            mStories.addAll(news);

        } else if (type.equals(Constants.CATEGORY_ARTICLE)) {
            mArticles.addAll(news);

        }
    }

    public void setNews(List<News> news, String type) {

        if (type.equals(Constants.CATEGORY_NEWS)) {
            this.mNews = news;
        } else if (type.equals(Constants.CATEGORY_STORY)) {
            this.mStories = news;

        } else if (type.equals(Constants.CATEGORY_ARTICLE)) {
            this.mArticles = news;

        }
    }

    public News getNewsItem(String id) {
        List<News> news = getNews();
        for (News n : news) {
            if (n == null) {
                continue;
            }
            if (n.getId().equals(id)) {
                return n;
            }
        }
        return null;
    }

    public News getNewsItem(String id, List<News> news) {
        for (News n : news) {
            if (n == null) {
                continue;
            }
            if (n.getId().equals(id)) {
                return n;
            }
        }
        return null;
    }



    public List<News> getNewsByTags(List<String>tags) {
        return getNewsByTags( tags,mNews);
    }
    public List<News> getNewsByTags(List<String>tags,List<News>news) {
        List<News> newsByTag = new ArrayList<>();

        for (News n : news) {
            List<String> newsTags = n.getIdTags();
            for (String s : newsTags) {
                if (tags.contains(s)) {
                    newsByTag.add(n);
                    break;
                }
            }
        }
        return newsByTag;
    }
    public List<News> getNewsAndArticles() {
        List<News> news = new ArrayList<>();
       news.addAll(mNews);
        news.addAll(mArticles);
        return news;
    }


 /*   private  class NewsAsyncTask extends AsyncTask<String,Integer, DataNews>

    {
        ProgressDialog pDialog;


        @Override
        protected DataNews doInBackground(String... params) {
            TagLab.getInstance(mAppContext, new NewsTeeDataLoader() {
                @Override
                public void onPreLoad() {

                }

                @Override
                public void onPostLoad() {

                }
            });
            AuthorLab.getInstance(mAppContext, new NewsTeeDataLoader() {
                @Override
                public void onPreLoad() {

                }

                @Override
                public void onPostLoad() {

                }
            });
            AudioLab.getInstance(mAppContext, new NewsTeeDataLoader() {
                @Override
                public void onPreLoad() {

                }

                @Override
                public void onPostLoad() {

                }
            });
            Call<DataNews> call = newsTeeApiInterface.getNews();
            DataNews dataNews = new DataNews();
            try {
                Response<DataNews> response = call.execute();
                dataNews = response.body();
                mNews = dataNews.getNews();
               
            *//*    if(songId.equals("3") )
                {
                    songId = "2";
                }
                for(News a : News)
                {
                    if(a.getId().equals(songId))
                    {
                        songUrl = a.getSource();
                        break;
                    }
                }*//*
            } catch (IOException e) {
                e.printStackTrace();
            }

            return dataNews;
        }

        @Override
        protected void onPreExecute() {
            //     player.reset();
            // Showing progress dialog before sending http request
            pDialog = new ProgressDialog(mAppContext);
            pDialog.setMessage(mAppContext.getString(R.string.loadingNews));
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
    //        pDialog.show();
        mIDataLoading.onPreLoad();
        }
        @Override
        protected void onPostExecute( DataNews dataNews) {
            super.onPostExecute(dataNews);
        //    pDialog.dismiss();
            Toast toast = Toast.makeText(mAppContext, dataNews.getResult(), Toast.LENGTH_SHORT);
            toast.show();

            mIDataLoading.onPostLoad();
            //      pDialog.dismiss();
            //  Uri trackUri =Uri.parse(songUrl);
       *//*         ContentUris.withAppendedId(
                android.provider.MediaStore.News.Media.EXTERNAL_CONTENT_URI,
                currSong);*//*
            //set the data source
      *//*      try{

                player.setDataSource(songUrl);
                player.prepare();
                player.start();
            }
            catch(Exception e){
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }

        }
    }
*/


}