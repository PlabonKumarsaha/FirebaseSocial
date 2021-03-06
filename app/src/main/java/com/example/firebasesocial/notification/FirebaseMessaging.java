package com.example.firebasesocial.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.firebasesocial.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //get current user from shared prefernce
        SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentuser = sharedPreferences.getString("Current_USER_ID","none");

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null && sent.equals(firebaseUser.getUid())){

           if(!savedCurrentuser.equals(user)){
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                   sendOAndAboveNotification(remoteMessage);
               } else{
                   sendNormalNotification(remoteMessage);
               }
           }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {


        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUID",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defsoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification oreoAndAboveNotification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = oreoAndAboveNotification1.getNotification(title,body,pendingIntent,defsoundURI,icon);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if(i>0){
            j = i;
        }
        oreoAndAboveNotification1.getManager().notify(j,builder.build());



    }

    private void sendNormalNotification (RemoteMessage remoteMessage){

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUID",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defsoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defsoundURI)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if(i>0){
            j = i;
        }
        notificationManager.notify(j,builder.build());


    }
}

