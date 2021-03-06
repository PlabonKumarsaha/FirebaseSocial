package com.example.firebasesocial.notification;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {

   /* @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();

        if(user!=null){
            updateToken(tokenRefresh);
        }
    }*/

    public void onNewToken(@NonNull String s) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN",tokenRefresh);
        Log.d("USER",user.toString());
        if (user != null) {
            updateToken(tokenRefresh);
        }

    }

    private void updateToken(String tokenRefresh) {

        Log.d("TOKEN",tokenRefresh);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        databaseReference.child(user.getUid()).setValue(token);
    }
}
