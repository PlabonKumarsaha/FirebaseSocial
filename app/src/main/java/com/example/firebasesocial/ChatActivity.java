package com.example.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

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
                    String image = ds.child("image").getValue().toString();

                    nameTV.setText(name);
                    try{

                        Picasso.get().load(image).placeholder(R.drawable.ic_deafult_face).into(profileIV);

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

    }

    @Override
    protected void onStart() {

        checkUserStatus();
        super.onStart();
    }

    private void sendMessage(String message) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("sender",myUID);
        hashMap.put("receiver",hisUID);
        hashMap.put("message",message);
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