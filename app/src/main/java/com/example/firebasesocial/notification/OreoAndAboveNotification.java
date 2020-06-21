package com.example.firebasesocial.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class OreoAndAboveNotification extends ContextWrapper {

    private static final String ID= "some_id";
    private static final String NAME = "FirebaseAPP";
    private NotificationManager notificationManager;

    public OreoAndAboveNotification(Context base, NotificationManager notificationManager) {
        super(base);
        this.notificationManager = notificationManager;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(ID,NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

    }
}
