package com.example.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView chat_recylerview;
    ImageView profileIV;
    TextView nameTV,userStatusTV;
    EditText msgET;
    ImageButton sendImgBtn;

    //firebase auth
    FirebaseAuth firebaseAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userdatabaseReference;

    String hisUID;
    String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolBar);
        chat_recylerview = findViewById(R.id.chat_recylerview);
        profileIV = findViewById(R.id.chatRecieverIV);
        nameTV = findViewById(R.id.userNameChatTV);
        userStatusTV = findViewById(R.id.userStatusChatTV);
        msgET = findViewById(R.id.msgET);
        sendImgBtn = findViewById(R.id.sentBTn);

        firebaseAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        firebaseDatabase = FirebaseDatabase.getInstance();
        userdatabaseReference = firebaseDatabase.getReference();

    }


    public void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            //user is signed in..stay here

            //set email of logged in user
            //  ProfileTv.setText(user.getEmail());
            myUID = user.getUid(); //get id of current user


        } else{

            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}