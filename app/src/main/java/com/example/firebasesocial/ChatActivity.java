package com.example.firebasesocial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView chat_recylerview;
    ImageView profileIV;
    TextView nameTV,userStatusTV;
    EditText msgET;
    ImageButton sendImgBtn;

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
    }
}