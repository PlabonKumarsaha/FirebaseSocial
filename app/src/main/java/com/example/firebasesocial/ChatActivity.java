package com.example.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasesocial.adapters.AdapterChat;
import com.example.firebasesocial.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    //check if user has seen message or not
    ValueEventListener seenListener;
    DatabaseReference userReferenceForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String hisUID;
    String myUID;
    String hisImage;

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        //recylerview properties
        chat_recylerview.setHasFixedSize(true);
        chat_recylerview.setLayoutManager(linearLayoutManager);


        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        firebaseDatabase = FirebaseDatabase.getInstance();
        userdatabaseReference = firebaseDatabase.getReference("User");

        //search user to get user info
        Query userQuery = userdatabaseReference.orderByChild("uid").equalTo(hisUID);
        //get USername and picture
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until user infro is receieved
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ds.child("name").getValue().toString();
                    hisImage = ds.child("image").getValue().toString();

                    String onlineSatus = ds.child("onlineStatus").getValue().toString();

                    nameTV.setText(name);
                    try{

                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_deafult_face).into(profileIV);

                    } catch (Exception e){

                        Picasso.get().load(R.drawable.ic_deafult_face).into(profileIV);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get text from edit text
                String message = msgET.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    //text empty

                    Toast.makeText(ChatActivity.this,"message is empty",Toast.LENGTH_SHORT).show();
                } else{

                    sendMessage(message);
                }
            }
        });

        readMessage();
        seenMessage();
    }

    private void seenMessage() {
        userReferenceForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userReferenceForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUID) && chat.getSender().equals(hisUID)) {

                        HashMap<String, Object> hasSeenHashmap = new HashMap<>();
                        hasSeenHashmap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void readMessage() {

        chatList = new ArrayList<>();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Chats");
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUID) && chat.getSender().equals(hisUID) ||
                            chat.getReceiver().equals(hisUID) && chat.getSender().equals(myUID)){
                        chatList.add(chat);
                    }

                    //adapter
                    adapterChat = new AdapterChat(ChatActivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recylerview
                    chat_recylerview.setAdapter(adapterChat);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {

        //get the time stamp to show the last active time
        String timestamp =String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        userReferenceForSeen.removeEventListener(seenListener);
        super.onPause();
    }

    @Override
    protected void onStart() {

        checkUserStatus();
        //set online
        checkOnlineStatus("Online");
        super.onStart();
    }

    @Override
    protected void onResume() {

        //set online
        checkOnlineStatus("Online");
        super.onResume();
    }

    private void sendMessage(String message) {

        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("sender",myUID);
        hashMap.put("receiver",hisUID);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        dbRef.child("Chats").push().setValue(hashMap);
        msgET.setText("");
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


    public void checkOnlineStatus(String status){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(myUID);
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update the status of current user
        dbRef.updateChildren(hashMap);
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