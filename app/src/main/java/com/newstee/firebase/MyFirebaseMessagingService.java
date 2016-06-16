package com.newstee.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.newstee.MainActivity;
import com.newstee.R;
import com.newstee.helper.SQLiteHandler;
import com.newstee.helper.SessionManager;

import java.util.Map;
import java.util.Random;

/**
 * Created by Arnold on 26.05.2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        //  Map<String,String> map =  remoteMessage.getData();

        //  String s = map.get("title");
        sendNotification(remoteMessage.getData());
        Log.d(TAG, "@@@@@@ Data " + remoteMessage.getData().toString());
        //  Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     * <p>
     * // * @param messageBody FCM message body received.
     */
    private void sendNotification(Map<String, String> data) {
        //  Pattern p = Pattern.compile("\\{([^}]*)\\}\\{([^}]*)\\}\\{([^}]*)\\}\\{([^}]*)\\}");
        //   Matcher m = p.matcher(messageBody);
        String title = "";
        String content = "";
        String user_id = "";
        String link_picture = "";
     /*   JSONObject json;
        try {
            json = new JSONObject(messageBody);
            title = json.getString("title");
            user_id =  json.getString("user_id");
            link_picture = json.getString("link_picture");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        title = data.get("title");
        user_id = data.get("user_id");
        link_picture = data.get("link_picture");
        Boolean notif = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications",true);
        if(!notif.booleanValue())
        {
            return;
        }
        if (!(new SessionManager(this).isLoggedIn())) {
            return;
        }
        String id = new SQLiteHandler(getApplicationContext()).getUserDetails().get(SQLiteHandler.KEY_ID);
        //   JSONObject json = new JSONObject
        if (id == null) {
            return;
        }
        if (!user_id.equals(id)) {
            return;
        }


      /*  while (m.find()) {
            title =m.group(1);
            content = m.group(2);
             user_id = m.group(3);
            link_picture = m.group(4);*/


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(com.nostra13.universalimageloader.core.ImageLoader.getInstance().loadImageSync(link_picture))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(new Random().nextInt() /* ID of notification */, notificationBuilder.build());
    }
}