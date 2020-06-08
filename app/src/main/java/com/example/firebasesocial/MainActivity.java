package com.example.firebasesocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //views
    Button  register_btn,login_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register_btn = findViewById(R.id.register_btn);
        login_btn = findViewById(R.id.login_btn);

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //start registation activity
                startActivity(new Intent(MainActivity.this,RegistrationActivity.class));

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //start login activity
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }
}
