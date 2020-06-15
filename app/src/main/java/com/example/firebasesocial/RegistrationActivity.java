package com.example.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    //initializing views
    EditText emailEditText,emailPassText;
    Button btn_register;
    TextView have_accountTV;

    //progressbar to dispaly while registering user

    ProgressDialog progressDialog;


    //firebase instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //actionbar and it's title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //enabling abckbutton
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        have_accountTV = findViewById(R.id.have_accountTV);
        emailEditText = findViewById(R.id.emailEditText);
        emailPassText = findViewById(R.id.emailPassText);

        btn_register = findViewById(R.id.btn_register);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering ....");

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailEditText.getText().toString().trim();
                String password = emailPassText.getText().toString().trim();

                //validate
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                   emailEditText.setError("Invalid email");
                   emailEditText.setFocusable(true);

                }
                else if(password.length()<6){

                    emailPassText.setError(" Password too short");
                    emailPassText.setFocusable(true);
                } else{


                    userRegister(email,password);
                }


            }
        });

        //handle login textview..click listener

        have_accountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                finish();
            }
        });




    }

    private void userRegister(final String email, final String password) {

        //email and passwprd pattern are valid now whow progess dialog


        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            // Sign in success, dissmiss the dialouge and start the registation activity
                          progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();

                            //get user gmail and uid from auth
                           String email =user.getEmail();
                           String uid = user.getUid();
                           //when a user registers , store the info in hashmap and store info
                            //in firebase and relatime databse too
                            HashMap<Object,String>hashmap = new HashMap<>();

                            //put infro in hashmap
                            hashmap.put("email",email);
                            hashmap.put("uid",uid);
                            hashmap.put("name",""); //will be added in user profile
                            hashmap.put("onlineStatus","Online"); //will be added in user profile
                            hashmap.put("phone","");
                            hashmap.put("image","");
                            hashmap.put("cover","");

                            //firebase instance
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                            //path to store userdata named Users
                            DatabaseReference reference = firebaseDatabase.getReference("User");

                            //put data within hashmap in databse
                            reference.child(uid).setValue(hashmap);

                            Toast.makeText(RegistrationActivity.this, "Registered!",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, DashboardActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //error ,dismiss the whole process
                progressDialog.dismiss();
                Toast.makeText(RegistrationActivity.this, ""+e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    //go to previous activity

    public boolean onSupportNavigateUp(){

        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
