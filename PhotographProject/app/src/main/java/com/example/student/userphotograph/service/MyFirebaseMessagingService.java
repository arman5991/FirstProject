package com.example.student.userphotograph.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.example.student.userphotograph.R;
import com.example.student.userphotograph.utilityes.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private int phone;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        float lat = Float.parseFloat(((remoteMessage.getData().get("lat"))));
        float lng = Float.parseFloat(((remoteMessage.getData().get("lng"))));
        phone = Integer.parseInt(remoteMessage.getData().get("phone"));

        SharedPreferences shared = getSharedPreferences("location", MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putFloat("key_lat", lat);
        edit.putFloat("key_lng", lng);
        edit.putInt("key_phone", phone);
        edit.apply();
        sendNotification(remoteMessage.getData().get("token"), (remoteMessage.getData().get("title")));
    }

    private void sendNotification(String token, String title) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification")
                        .setContentText("This is a test notification");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this, 0, callIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setContentTitle(title);
        mBuilder.setSound(uri);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        Intent notifyIntentAccept =
                new Intent(this, NotificationActionService.class);
        notifyIntentAccept.putExtra("Token", token);
        notifyIntentAccept.setAction(Constants.ACTION_ACCEPT);
        PendingIntent accept = PendingIntent.getService(this, 0, notifyIntentAccept, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notifyIntentReject =
                new Intent(this, NotificationActionService.class);
        notifyIntentReject.putExtra("Token", token);
        notifyIntentReject.putExtra("Title", title);
        notifyIntentReject.setAction(Constants.ACTION_REJECT);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notifyIntentReject, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.addAction(R.drawable.ic_accept, "Accept", accept);
        mBuilder.addAction(R.drawable.ic_reject, "Reject", pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}