package com.example.gerry.fypv001.firebaseMessaging;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.gerry.fypv001.MainActivity;
import com.example.gerry.fypv001.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by gerry on 14/02/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String title;
    private String body;
    private int notificationId;
    private NotificationManager notificationManager;
    private SharedPreferences prefs;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        notificationId = prefs.getInt("notificationNumber", 0);
        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
        Context con = getApplicationContext();
        initChannels(con);
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        displayNotification(title, body);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
/*
            if (/* Check if data needs to be processed by long running job  true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();

            }
                  }
*/
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
        }
    }

    public void displayNotification(String title, String body){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.splash_icon)
                .setContentTitle(title)
                .setContentText(body)
                //.setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent, true);
        notificationId = ((int) Math.random()*10);
        notificationManager.notify(notificationId, mBuilder.build());
        SharedPreferences.Editor editor = prefs.edit();
        notificationId++;
        editor.putInt("notificationNumber", notificationId);
        editor.commit();
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }
}
