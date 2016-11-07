package com.newstee.helper;



    import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.newstee.Constants;

public class SessionManager {




        // LogCat tag
        private static String TAG = SessionManager.class.getSimpleName();

        // Shared PreferencesActivity
        SharedPreferences pref;

        Editor editor;
        Context appContext;

        // Shared pref mode
        int PRIVATE_MODE = 0;

        // Shared preferences file name
        private static final String PREF_NAME = "AndroidHiveLogin";
        private static final String KEY_LIKED_IDS = "liked_ids";
        private static final String KEY_IS_CAR_MODE = "car_mode";
        private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
        private static final String KEY_RECENT_IDS = "recent_ids";
        private static final String KEY_COUNTRY = "country";


        public SessionManager(Context context) {
            this.appContext = context;
            pref = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }
        public void setLikedIds(String likedIds)
        {
            editor.putString(KEY_LIKED_IDS, likedIds);

            // commit changes
            editor.commit();

         //   Log.d(TAG, "UserRegister login session modified!");
        }
        public void setRecentIds(String recentIds)
        {
            editor.putString(KEY_RECENT_IDS, recentIds);

            // commit changes
            editor.commit();

            //   Log.d(TAG, "UserRegister login session modified!");
        }
        public String getRecentIds(){
            return pref.getString(KEY_RECENT_IDS, "-999");
        }
        public String getLikedIds(){
            return pref.getString(KEY_LIKED_IDS, "-999");
        }
        public void setCarMode(boolean isCarMode)
        {
            editor.putBoolean(KEY_IS_CAR_MODE, isCarMode);

            // commit changes
            editor.commit();

            Log.d(TAG, "UserRegister login session modified!");
        }
        public boolean isCarMode(){
            return pref.getBoolean(KEY_IS_CAR_MODE, false);
        }
public void setCountrySettings(@Nullable String value)
{
    String valueInt;
    if(value == null)
    {
        valueInt = "2";
    }
    else if(value.equals(Constants.RUSSIA_VALUE))
    {
        valueInt = "0";
    }
    else if(value.equals(Constants.UKRAINE_VALUE))
    {
        valueInt = "1";

    }
    else
    {
        valueInt = "2";

    }
    PreferenceManager.getDefaultSharedPreferences(appContext).edit().putString(KEY_COUNTRY,valueInt).commit();

}

    /**
     *
     * @return return ru or ua or null or ""
     */
        @Nullable
        public String getCountrySettings()
        {
          String value=  PreferenceManager.getDefaultSharedPreferences(appContext).getString(KEY_COUNTRY,"2");
            switch (value) {
                case "0":
                    return Constants.RUSSIA_VALUE;
                case "1":
                    return Constants.UKRAINE_VALUE;
                case "2":
                    return Constants.AUTO_DEFINE_VALUE;
                default:
                    return "";
            }
        }



        public void setLogin(boolean isLoggedIn) {

            editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

            // commit changes
            editor.commit();

            Log.d(TAG, "UserRegister login session modified!");
        }

        public boolean isLoggedIn(){
            return pref.getBoolean(KEY_IS_LOGGED_IN, false);
        }
    }

