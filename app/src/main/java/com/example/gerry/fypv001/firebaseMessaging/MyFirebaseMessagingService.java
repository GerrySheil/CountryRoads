package com.example.gerry.fypv001.firebaseMessaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("FirebaseMessaging:", "From: " + remoteMessage.getFrom());
        Log.d("FirebaseMessaging:", "Message data payload: " + remoteMessage.getNotification().getBody());
        Log.d("FirebaseMessaging:", "Message Title: " + remoteMessage.getNotification().getTitle());
        title = remoteMessage.getNotification().getTitle();
        body = remoteMessage.getNotification().getBody();
        Context con = getApplicationContext();
        initChannels(con);
        notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        displayNotification(title, body);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("FirebaseMessaging:", "Message data payload: " + remoteMessage.getData());
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
                Log.d("FirebaseMessaging:", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
        }
    }

    public void displayNotification(String title, String body){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.splash_icon)
                .setContentTitle(title)
                .setContentText(body)
                //.setStyle(new NotificationCompat.BigTextStyle()
                        //.bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationId = ((int) Math.random());
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }
}
