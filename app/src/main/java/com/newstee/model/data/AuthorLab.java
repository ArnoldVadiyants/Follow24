package com.newstee.model.data;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;


public class AuthorLab {

    private List<Author> mAuthors = new ArrayList<>();
    private static AuthorLab sAuthorLab;
    private Context mAppContext;
    private Gson gson = new GsonBuilder().create();
   private AuthorLab() {
       // mAppContext = appContext;
    }

    public static AuthorLab getInstance(){
        if (sAuthorLab == null) {
            sAuthorLab = new AuthorLab();
        }
        return sAuthorLab;
    }

    public Author getAuthor(String id) {
        for (Author a : mAuthors) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }
    public List<Author> getAuthors() {
        return mAuthors;
    }
    public void setAuthors(List<Author> authors) {
        this.mAuthors = authors;
    }

/*
    private  class AuthorAsyncTask extends AsyncTask<String,Integer, DataAuthor>

    {
        ProgressDialog pDialog;


        @Override
        protected DataAuthor doInBackground(String... params) {

            Call<DataAuthor> call = newsTeeApiInterface.getAuthors();
            DataAuthor dataAuthor = new DataAuthor();
            try {
                Response<DataAuthor> response = call.execute();
                dataAuthor = response.body();
                mAuthors = dataAuthor.getData();
               
            *//*    if(songId.equals("3") )
                {
                    songId = "2";
                }
                for(Author a : Author)
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

            return dataAuthor;
        }

        @Override
        protected void onPreExecute() {
            //     player.reset();
            // Showing progress dialog before sending http request

       *//*     pDialog = new ProgressDialog(mAppContext);
            pDialog.setMessage(mAppContext.getString(R.string.loadingAuthor));
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();*//*
            mIDataLoading.onPreLoad();}

        @Override
        protected void onPostExecute( DataAuthor dataAuthor) {
            super.onPostExecute(dataAuthor);
            Toast toast = Toast.makeText(mAppContext, dataAuthor.getResult(), Toast.LENGTH_SHORT);
            toast.show();
        //    pDialog.dismiss();
            mIDataLoading.onPostLoad();

            //
            //  Uri trackUri =Uri.parse(songUrl);
       *//*         ContentUris.withAppendedId(
                android.provider.MediaStore.Author.Media.EXTERNAL_CONTENT_URI,
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


