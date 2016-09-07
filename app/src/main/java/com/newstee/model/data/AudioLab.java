package com.newstee.model.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newstee.network.interfaces.NewsTeeApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Arnold on 12.04.2016.
 */
public class AudioLab {




    private List<Audio> mAudio = new ArrayList<>();
    private static AudioLab sAudioLab;
    private Context mAppContext;
    private Gson gson = new GsonBuilder().create();
    private Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(NewsTeeApiInterface.BASE_URL)
            .build();
    private NewsTeeApiInterface newsTeeApiInterface = retrofit.create(NewsTeeApiInterface.class);
    private AudioLab(/*Context appContext, NewsTeeDataLoader iDataLoading*/) {
    /*    mAppContext = appContext;
        mIDataLoading = iDataLoading;
        loadAudio();*/
    }

    public static AudioLab getInstance(/*Context context, NewsTeeDataLoader iDataLoading*/){
        if (sAudioLab == null) {
            sAudioLab = new AudioLab(/*context.getApplicationContext(), iDataLoading*/);
        }
        return sAudioLab;
    }
    /*public void loadAudio()
    {
        AudioAsyncTask task = new AudioAsyncTask();
        task.execute();
    }*/
    public Audio getAudioItem(String id) {
        for(Audio a : mAudio)
        {
            if(a.getId().equals(id))
            {
               return a;
            }
        }
        return null;
    }
    public List<Audio> getAudio() {
        return mAudio;
    }
    public void setAudio(List<Audio> audio) {
        this.mAudio = audio;
    }


  /*  private  class AudioAsyncTask extends AsyncTask<String,Integer, DataAudio>

    {
        ProgressDialog pDialog;


        @Override
        protected DataAudio doInBackground(String... params) {

            Call<DataAudio> call = newsTeeApiInterface.getAudio();
            DataAudio dataAudio = new DataAudio();
            try {
                Response<DataAudio> response = call.execute();
               dataAudio = response.body();
               mAudio = dataAudio.getData();

            *//*    if(songId.equals("3") )
                {
                    songId = "2";
                }
                for(Audio a : audio)
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

            return dataAudio;
        }

        @Override
        protected void onPreExecute() {
            //     player.reset();
            // Showing progress dialog before sending http request
          *//*  pDialog = new ProgressDialog(mAppContext);
            pDialog.setMessage(mAppContext.getString(R.string.loadingAudio));
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();*//*
            mIDataLoading.onPreLoad();
        }
        @Override
        protected void onPostExecute( DataAudio dataAudio) {
            super.onPostExecute(dataAudio);
            Toast toast = Toast.makeText(mAppContext, dataAudio.getResult(), Toast.LENGTH_SHORT);
            toast.show();
      //      pDialog.dismiss();
            mIDataLoading.onPostLoad();
            //      pDialog.dismiss();
            //  Uri trackUri =Uri.parse(songUrl);
       *//*         ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
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
    }
*/

}
