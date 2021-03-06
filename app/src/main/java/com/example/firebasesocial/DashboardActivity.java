package com.example.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.FragmentTransitionSupport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.firebasesocial.notification.Token;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class DashboardActivity extends AppCompatActivity {

    //firebase auth
    FirebaseAuth firebaseAuth;
    //TextView ProfileTv;
    ActionBar actionBar;
    FrameLayout content;

    String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //actionbar and it's title
         actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        content = findViewById(R.id.content);

        //init
        firebaseAuth = FirebaseAuth.getInstance();
     //   ProfileTv = findViewById(R.id.ProfileTv);


        //bottom navigatin work
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //this is set as the deafult fragment..will be shwn when it starts
        actionBar.setTitle("Home");
        //update token
        checkUserStatus();
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,fragment1,"");
        ft1.commit();



        //updateToken(FirebaseInstanceId.getInstance().getToken());


    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        Log.d("token",token);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mtoken = new Token(token);
        databaseReference.child(mUid).setValue(mtoken);

    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            //handle item click
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    //home framant tranaction
                    actionBar.setTitle("Home");
                    HomeFragment fragment1 = new HomeFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content,fragment1,"");
                    ft1.commit();
                    return true;

                case R.id.nav_profile:
                    //profile framant tranaction
                    actionBar.setTitle("Profile");
                    ProfileFragment fragment2 = new ProfileFragment();
                    FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                    ft2.replace(R.id.content,fragment2,"");
                    ft2.commit();
                    return true;

                case R.id.nav_users:
                    //home framant tranaction
                    actionBar.setTitle("Users");
                    UsersFragment fragment3 = new UsersFragment();
                    FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                    ft3.replace(R.id.content,fragment3,"");
                    ft3.commit();
                    return true;


                case R.id.nav_chat:
                    //home framant tranaction
                    actionBar.setTitle("chat");
                    ChatListFragment fragment4 = new ChatListFragment();
                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.content,fragment4,"");
                    ft4.commit();
                    return true;


            }
            return false;
        }
    };

    public void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            //user is signed in..stay here

            mUid = user.getUid();
            //set email of logged in user
          //  ProfileTv.setText(user.getEmail());

            //save uid of currently signed in user in shared prefernce
            SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Current_USER_ID",mUid);
            editor.apply();
            updateToken(FirebaseInstanceId.getInstance().getToken());

        } else{

            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {

        //check on start of the app
        checkUserStatus();
        super.onStart();
    }

    //inflate option menu



    /*
    1.make profile activity launcher
    2.app starts checking if the user is already signed in otherwise go to mainactivity.
    3.create login activity


     */
}
