package com.newstee;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.newstee.helper.InternetHelper;
import com.newstee.helper.SQLiteHandler;
import com.newstee.helper.SessionManager;
import com.newstee.model.data.AuthorLab;
import com.newstee.model.data.DataAuthor;
import com.newstee.model.data.DataCountryCode;
import com.newstee.model.data.DataIds;
import com.newstee.model.data.DataNews;
import com.newstee.model.data.DataTag;
import com.newstee.model.data.DataUserAuthentication;
import com.newstee.model.data.IpLab;
import com.newstee.model.data.NewsLab;
import com.newstee.model.data.Tag;
import com.newstee.model.data.TagLab;
import com.newstee.model.data.User;
import com.newstee.model.data.UserLab;
import com.newstee.network.FactoryApi;
import com.newstee.network.interfaces.NewsTeeApiInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Arnold on 10.05.2016.
 */
abstract public class MainLoadAsyncTask extends AsyncTask<String, String, Boolean>

{
    private static final String NOT_NULL_ARGUMENT = ",-999";
    public static final String[] LoadArguments = {Constants.ARGUMENT_COUNTRY_CODE,Constants.ARGUMENT_AUTHORS, Constants.ARGUMENT_NEWS_ADDED,
            Constants.ARGUMENT_NEWS_RECENT, Constants.ARGUMENT_NEWS_LIKED,
            Constants.CATEGORY_NEWS,Constants.CATEGORY_ARTICLE,  Constants.CATEGORY_STORY, Constants.ARGUMENT_NEWS_RECOMMENDED, Constants.ARGUMENT_TAGS};

    private SessionManager session;
    private static String TAG = "MainLoadAsyncTask";
    private boolean isLogin = false;
    private SQLiteHandler db;
    Context mAppContext;

    MainLoadAsyncTask(Context context) {

        mAppContext = context;
        session = new SessionManager(mAppContext);
        db = new SQLiteHandler(mAppContext);
    }

    abstract void hideContent();

    abstract void showContent();

    private boolean hasInternet() {
        boolean hasInternet = false;
        if (!InternetHelper.getInstance(mAppContext).isOnline()) {
            if (mAppContext instanceof Activity) {
                ((Activity) mAppContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mAppContext, mAppContext.getResources().getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            hasInternet = true;
        }

        return hasInternet;
    }

    private void login() {
        NewsTeeApiInterface api = FactoryApi.getInstance(mAppContext);
        if (session.isLoggedIn()) {
            HashMap<String, String> userData = db.getUserDetails();
            String password = userData.get(SQLiteHandler.KEY_PASSWORD);
            String email = userData.get(SQLiteHandler.KEY_EMAIL);
            System.out.println("@@@@@@ Пароль " + password + "@@@@ mail" + email);
            Call<DataUserAuthentication> userC = null;
            if (email == null) {
                String key = userData.get(SQLiteHandler.KEY_SOCIAL_NETWORK_KEY);
                String snId = userData.get(SQLiteHandler.KEY_SOCIAL_NETWORK_ID);
                switch (key) {
                    case SQLiteHandler.KEY_GG_ID:
                        userC = api.signIn(snId, null, null, null, "ru");
                        break;
                    case SQLiteHandler.KEY_FB_ID:
                        userC = api.signIn(null, snId, null, null, "ru");
                        break;
                    case SQLiteHandler.KEY_VK_ID:
                        userC = api.signIn(null, null, snId, null, "ru");
                        break;
                    case SQLiteHandler.KEY_TW_ID:
                        userC = api.signIn(null, null, null, snId, "ru");
                        break;
                }

            } else {
                userC = api.signIn(email, password, "ru");
            }

            if (userC == null) {
                return;
            }
            try {
                Response<DataUserAuthentication> userR = userC.execute();
                String result = userR.body().getResult();
                final String msg = userR.body().getMessage();
                if (result.equals(Constants.RESULT_SUCCESS)) {
                    User u = userR.body().getData();;
                    UserLab.getInstance().setUser(u);
                } else {
                    db.deleteUsers();
                    session.setLogin(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        for (int i = 0; i < LoadArguments.length; i++) {
            update(LoadArguments[i]);
        }
        UserLab.getInstance().setIsUpdated(true);
    }

    private void update(String argument) {
        if (!hasInternet()) {
            return;
        }
        String countryValue = new SessionManager(mAppContext).getCountrySettings();
        NewsTeeApiInterface api = FactoryApi.getInstance(mAppContext);
        switch (argument) {
            case Constants.ARGUMENT_COUNTRY_CODE:
                Call<DataCountryCode> countryC = api.getUserIpData();

                try {
                    Response<DataCountryCode> countryR = countryC.execute();
                    DataCountryCode dataCountryCode = countryR.body();
                    IpLab.getInstance().setCountryCode(dataCountryCode.getData().getCountryCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.ARGUMENT_AUTHORS:
                Call<DataAuthor> authorC = api.getAuthors();

                try {
                    Response<DataAuthor> authorR = authorC.execute();
                    DataAuthor dataAuthor = authorR.body();
                    AuthorLab.getInstance().setAuthors(dataAuthor.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.ARGUMENT_NEWS_ADDED:
                if (!isLogin) {
                    login();
                    isLogin = true;
                }
                String addedIds = UserLab.getInstance().getUser().getNewsAddedIds();
                if (addedIds != null) {

                    addedIds = addedIds+NOT_NULL_ARGUMENT;
                    Call<DataNews> newsByIdC = api.getNewsByIds(addedIds);
                    try {
                        Response<DataNews> newsByIdR = newsByIdC.execute();
                        UserLab.getInstance().setAddedNews(newsByIdR.body().getNews());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Constants.ARGUMENT_NEWS_RECENT:
                if (!isLogin) {
                    login();
                    isLogin = true;
                }
                String recentIds = UserLab.getInstance().getRecentNewsIdsFromDevice(mAppContext);
                if (recentIds != null) {
                    if (!recentIds.equals("-999")) {
                        Call<DataNews> newsByIdC = api.getNewsByIdsNoSort(recentIds);
                        try {
                            Response<DataNews> newsByIdR = newsByIdC.execute();
                            UserLab.getInstance().setRecentNews(newsByIdR.body().getNews());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case Constants.ARGUMENT_NEWS_LIKED:
                if (!isLogin) {
                    login();
                    isLogin = true;
                }
                String likedIds;
                if (session.isLoggedIn()) {
                    likedIds = UserLab.getInstance().getUser().getNewsLikedIds();
                } else {
                    likedIds = UserLab.getInstance().getLikedNewsFromDevice(mAppContext);

                }

                if (likedIds != null) {
                    likedIds=likedIds+ NOT_NULL_ARGUMENT;
                    Call<DataNews> newsByIdC = api.getNewsByIds(likedIds);
                    try {
                        Response<DataNews> newsByIdR = newsByIdC.execute();
                        UserLab.getInstance().setLikedNews(newsByIdR.body().getNews());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
         /*   case Constants.ARGUMENT_NEWS_NONE:
                Call<DataNews> newsC = api.getNews(NewsListFragment.MAX_PER_PAGE, 0);

                try {
                    Response<DataNews> newsR = newsC.execute();
                    NewsLab.getInstance().setNews(newsR.body().getNews());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;*/
            case Constants.CATEGORY_NEWS:
                Call<DataNews> newNewsC = api.getNewsByType(Constants.CATEGORY_NEWS,NewsListFragment.MAX_PER_PAGE, 0,countryValue);

                try {
                    Response<DataNews> newsNewsR = newNewsC.execute();
                    NewsLab.getInstance().setNews(newsNewsR.body().getNews(),Constants.CATEGORY_NEWS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.CATEGORY_ARTICLE:
                Call<DataNews> newsArtC = api.getNewsByType(Constants.CATEGORY_ARTICLE,NewsListFragment.MAX_PER_PAGE, 0,countryValue);

                try {
                    Response<DataNews> newsArtR = newsArtC.execute();
                    NewsLab.getInstance().setNews(newsArtR.body().getNews(),Constants.CATEGORY_ARTICLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.CATEGORY_STORY:
                Call<DataNews> newsStC = api.getNewsByType(Constants.CATEGORY_STORY,NewsListFragment.MAX_PER_PAGE, 0,countryValue);

                try {
                    Response<DataNews> newsStR = newsStC.execute();
                    NewsLab.getInstance().setNews(newsStR.body().getNews(),Constants.CATEGORY_STORY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.ARGUMENT_NEWS_RECOMMENDED:
                Call<DataIds> idsC = api.getRecommended();
                try {
                    Response<DataIds> idsR = idsC.execute();
                    String ids = idsR.body().getData();

                    if (ids != null) {
                        if (!ids.equals("")) {

                            Call<DataNews> newsRecC = api.getNewsByIds(ids);
                            try {
                                Response<DataNews> newsRecR = newsRecC.execute();
                                NewsLab.getInstance().setRecommendedNews(newsRecR.body().getNews());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.ARGUMENT_TAGS:
                if (!isLogin) {
                    login();
                    isLogin = true;
                }
                Call<DataTag> tagC = api.getTags();

                try {
                    Response<DataTag> tagR = tagC.execute();
                    TagLab.getInstance().setTags(tagR.body().getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String tagIds = UserLab.getInstance().getUser().getTagsIds();
                if (tagIds != null) {
                    String mas[] = tagIds.split(",");
                    for (int i = 0; i < mas.length; i++) {
                        mas[i] = mas[i].trim();
                    }
                    List<Tag> tags = TagLab.getInstance().getTags(mAppContext);
                    List<Tag> addedTags = new ArrayList<>();
                    for (Tag t : tags) {
                        for (int i = 0; i < mas.length; i++) {
                            if (t.getId().equals(mas[i])) {
                                addedTags.add(t);
                            }
                        }
                    }
                    UserLab.getInstance().setAddedTags(addedTags);
                }
                break;

        }
    }
   /* private void update() {
        if (mAppContext instanceof Activity) {
            ((Activity) mAppContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!InternetHelper.getInstance(mAppContext).isOnline()) {
                        Toast.makeText(mAppContext, mAppContext.getResources().getString(R.string.check_internet_con), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            });
        }


        NewsTeeApiInterface api = FactoryApi.getInstance(mAppContext);
        if (session.isLoggedIn()) {
            HashMap<String, String> userData = db.getUserDetails();
            String password = userData.get(SQLiteHandler.KEY_PASSWORD);
            String email = userData.get(SQLiteHandler.KEY_EMAIL);
            System.out.println("@@@@@@ Пароль " + password + "@@@@ mail" + email);
            Call<DataUserAuthentication> userC = null;
            if (email == null) {
                String key = userData.get(SQLiteHandler.KEY_SOCIAL_NETWORK_KEY);
                String snId = userData.get(SQLiteHandler.KEY_SOCIAL_NETWORK_ID);
                switch (key) {
                    case SQLiteHandler.KEY_GG_ID:
                        userC = api.signIn(snId, null, null, null, "ru");
                        break;
                    case SQLiteHandler.KEY_FB_ID:
                        userC = api.signIn(null, snId, null, null, "ru");
                        break;
                    case SQLiteHandler.KEY_VK_ID:
                        userC = api.signIn(null, null, snId, null, "ru");
                        break;
                    case SQLiteHandler.KEY_TW_ID:
                        userC = api.signIn(null, null, null, snId, "ru");
                        break;
                }

            } else {
                userC = api.signIn(email, password, "ru");
            }

            if (userC == null) {
                return;
            }
            try {
                Response<DataUserAuthentication> userR = userC.execute();
                String result = userR.body().getResult();
                final String msg = userR.body().getMessage();
                if (result.equals(Constants.RESULT_SUCCESS)) {
                    User u = userR.body().getData();;
                    UserLab.getInstance().setUser(u);
                } else {
                    db.deleteUsers();
                    session.setLogin(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       *//* Call<DataAuthor> authorC = api.getAuthors();

        try {
            Response<DataAuthor> authorR = authorC.execute();
            DataAuthor dataAuthor = authorR.body();
            AuthorLab.getInstance().setAuthors(dataAuthor.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
       *//* String recentIds = UserLab.getInstance().getRecentNewsIdsFromDevice(mAppContext);
        if (recentIds != null) {
            if (!recentIds.equals("-999")) {
                Call<DataNews> newsByIdC = api.getNewsByIdsNoSort(recentIds);
                try {
                    Response<DataNews> newsByIdR = newsByIdC.execute();
                    UserLab.getInstance().setRecentNews(newsByIdR.body().getNews());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*//*
      *//*  String addedIds = UserLab.getInstance().getUser().getNewsAddedIds();
        if (addedIds != null) {
            Call<DataNews> newsByIdC = api.getNewsByIds(addedIds);
            try {
                Response<DataNews> newsByIdR = newsByIdC.execute();
                UserLab.getInstance().setAddedNews(newsByIdR.body().getNews());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*//*
      *//*  String likedIds = "";
        if (session.isLoggedIn()) {
            likedIds = UserLab.getInstance().getUser().getNewsLikedIds();
        } else {
            likedIds = UserLab.getInstance().getLikedNewsFromDevice(mAppContext);

        }

        if (likedIds != null) {
            Call<DataNews> newsByIdC = api.getNewsByIds(likedIds);
            try {
                Response<DataNews> newsByIdR = newsByIdC.execute();
                UserLab.getInstance().setLikedNews(newsByIdR.body().getNews());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*//*

     *//*   Call<DataNews> newsC = api.getNews(NewsListFragment.MAX_PER_PAGE,0);

        try {
            Response<DataNews> newsR = newsC.execute();
            NewsLab.getInstance().setNews(newsR.body().getNews());
        } catch (IOException e) {
            e.printStackTrace();
        }*//*
       *//* Call<DataIds> idsC = api.getRecommended();
        try {
            Response<DataIds>  idsR = idsC.execute();
            String ids =   idsR.body().getData();

            if(ids != null)
            {
                if(!ids.equals(""))
                {

                    Call<DataNews> newsRecC = api.getNewsByIds(ids);
                    try {
                        Response<DataNews>  newsRecR = newsRecC.execute();
                       NewsLab.getInstance().setRecommendedNews(newsRecR.body().getNews());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*//*
       *//* Call<DataTag> tagC = api.getTags();

        try {
            Response<DataTag> tagR = tagC.execute();
            TagLab.getInstance().setTags(tagR.body().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tagIds = UserLab.getInstance().getUser().getTagsIds();
        if (tagIds != null) {
            String mas[] = tagIds.split(",");
            for (int i = 0; i < mas.length; i++) {
                mas[i] = mas[i].trim();
            }
            List<Tag> tags = TagLab.getInstance().getTags();
            List<Tag> addedTags = new ArrayList<>();
            for (Tag t : tags) {
                for (int i = 0; i < mas.length; i++) {
                    if (t.getId().equals(mas[i])) {
                        addedTags.add(t);
                    }
                }
            }
            UserLab.getInstance().setAddedTags(addedTags);
            UserLab.getInstance().setIsUpdated(true);
        }*//*
    }*/


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        hideContent();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        showContent();

    }

    @Override
    protected Boolean doInBackground(String... params) {
        int count = params.length;
        if (count == 0) {
            update();
        } else {
            for (int i = 0; i < count; i++)
                update(params[i]);
        }

        return true;
    }
}