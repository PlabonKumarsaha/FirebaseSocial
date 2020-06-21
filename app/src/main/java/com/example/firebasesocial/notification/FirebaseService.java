package com.example.firebasesocial.notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();

        if(user!=null){
            updateToken(tokenRefresh);
        }
    }

    private void updateToken(String tokenRefresh) {
        
    }
}
