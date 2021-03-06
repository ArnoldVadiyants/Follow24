package com.newstee.helper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.newstee.Constants;
import com.newstee.network.interfaces.NewsTeeApiInterface;

/**
 * Created by Arnold on 08.05.2016.
 */
public class InternetHelper {
    private static InternetHelper sInternetHelper;
    private Context mAppContext;
    InternetHelper(Context context)
    {
        mAppContext = context;
    }
    public static InternetHelper getInstance(Context context){
        if (sInternetHelper == null) {
            sInternetHelper = new InternetHelper(context);
        }
        return sInternetHelper;
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public static void goToDeveloperSite(Context context)
    {
        context.startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.LINK_TO_DEVELOPER_SITE)));
    }

    public static String toCorrectLink(String link)
    {
        String baseUrl = NewsTeeApiInterface.BASE_URL;
        if (!link.contains(baseUrl))
            link = baseUrl + link;
        return link;
    }
}
