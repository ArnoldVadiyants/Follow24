package com.newstee.model.data;

/**
 * Created by Arnold on 12.04.2016.
 */

import android.content.Context;

import com.newstee.Constants;
import com.newstee.helper.SessionManager;

import java.util.ArrayList;
import java.util.List;


public class TagLab {
    private List<Tag> mTags = new ArrayList<>();
    private static TagLab sTagLab;
   /* private Context mAppContext;
    private Gson gson = new GsonBuilder().create();
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(NewsTeeApiInterface.BASE_URL)
            .build();
    private NewsTeeApiInterface newsTeeApiInterface = retrofit.create(NewsTeeApiInterface.class);*/
    private TagLab(/*Context appContext, NewsTeeDataLoader iDataLoading*/) {
   /*     mAppContext = appContext;
        mIDataLoading = iDataLoading;*/
     //   loadTags();
    }

    public static TagLab getInstance(/*Context context, NewsTeeDataLoader iDataLoading*/){
        if (sTagLab == null) {
            sTagLab = new TagLab(/*context.getApplicationContext(), iDataLoading*/);
        }
        return sTagLab;
    }
   /* public void loadTags()
    {
TagAsyncTask tagAsyncTask = new TagAsyncTask();
        tagAsyncTask.execute();
    }*/
    public List<Tag> getTags(Context context) {
        List<Tag> tags = new ArrayList<>();
        tags.addAll(mTags);
        SessionManager manager = new SessionManager(context);
        String country = manager.getCountrySettingsValue();

        if(country.equals(Constants.UKRAINE_VALUE))
        {
            Tag tag = getTagByTitle(Constants.TAG_TITLE_RUSSIA,tags);
            if(tag !=null)
            {
                tags.remove(tag);
            }
        }
        else if(country.equals(Constants.RUSSIA_VALUE))
        {
            Tag tag = getTagByTitle(Constants.TAG_TITLE_UKRAINE,tags);
            if(tag !=null)
            {
                tags.remove(tag);
            }
        }
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.mTags = tags;
    }
    public Tag getTag(String id) {
        for (Tag t : mTags) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }
    public static Tag getTagByTitle(String title, List<Tag>tags) {
        for (Tag t : tags) {
            if (t.getNameTag().equals(title)) {
                return t;
            }
        }
        return null;
    }

  /*  private  class TagAsyncTask extends AsyncTask<String,Integer, DataTag>

    {
        ProgressDialog pDialog;


        @Override
        protected DataTag doInBackground(String... params) {

            Call<DataTag> call = newsTeeApiInterface.getTags();
            DataTag dataTag = new DataTag();
            try {
                Response<DataTag> response = call.execute();
                dataTag = response.body();
                mTags = dataTag.getData();
               
            *//*    if(songId.equals("3") )
                {
                    songId = "2";
                }
                for(Tag a : Tag)
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

            return dataTag;
        }

        @Override
        protected void onPreExecute() {
            //     player.reset();
            // Showing progress dialog before sending http request
          *//*  pDialog = new ProgressDialog(mAppContext);
            pDialog.setMessage(mAppContext.getString(R.string.loadingTags));
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();*//*
            mIDataLoading.onPreLoad();}

        @Override
        protected void onPostExecute( DataTag dataTag) {
            super.onPostExecute(dataTag);
            Toast toast = Toast.makeText(mAppContext, dataTag.getResult(), Toast.LENGTH_SHORT);
            toast.show();
          //  pDialog.dismiss();
            mIDataLoading.onPostLoad();
            //      pDialog.dismiss();
            //  Uri trackUri =Uri.parse(songUrl);
       *//*         ContentUris.withAppendedId(
                android.provider.MediaStore.Tag.Media.EXTERNAL_CONTENT_URI,
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

*//*


        }
    }*/
}

