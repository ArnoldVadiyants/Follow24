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
import com.newstee.model.data.DataIds;
import com.newstee.model.data.DataNews;
import com.newstee.model.data.DataTag;
import com.newstee.model.data.DataUserAuthentication;
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
abstract public class LoadAsyncTask extends AsyncTask<String, String, Boolean>

{
    private SessionManager session;
    private static String TAG = "LoadAsyncTask";
    private SQLiteHandler db;
    Context mAppContext;

    LoadAsyncTask(Context context) {

        mAppContext = context;
        session = new SessionManager(mAppContext);
        db = new SQLiteHandler(mAppContext);
    }

    abstract void hideContent();

    abstract void showContent();

    private void update() {
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
                    User u = userR.body().getData().get(0);
                    UserLab.getInstance().setUser(u);
                } else {
                    db.deleteUsers();
                    session.setLogin(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Call<DataAuthor> authorC = api.getAuthors();

        try {
            Response<DataAuthor> authorR = authorC.execute();
            DataAuthor dataAuthor = authorR.body();
            AuthorLab.getInstance().setAuthors(dataAuthor.getData());
        } catch (IOException e) {
            e.printStackTrace();
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
        String addedIds = UserLab.getInstance().getUser().getNewsAddedIds();
        if (addedIds != null) {
            Call<DataNews> newsByIdC = api.getNewsByIds(addedIds);
            try {
                Response<DataNews> newsByIdR = newsByIdC.execute();
                UserLab.getInstance().setAddedNews(newsByIdR.body().getNews());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String likedIds = "";
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
        }

        Call<DataNews> newsC = api.getNews();

        try {
            Response<DataNews> newsR = newsC.execute();
            NewsLab.getInstance().setNews(newsR.body().getNews());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<DataIds> idsC = api.getRecommended();
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
        }
    }


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

update();
        return true;
    }
}