package com.example.gerry.fypv001.firebaseMessaging;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by gerry on 14/02/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.d("FirebaseToken:", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String token){
        Intent in = new Intent("GetToken");
        in.putExtra("Token", token);
        //in.setAction("GetToken");
//sendBroadcast(in);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }
}
