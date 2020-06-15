package com.example.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText,emailPassText;
    TextView nothave_accountTV , recoverPassTv;
    Button btn_Login;

    SignInButton googleLogin_btn;

    //firebase isnatnce
    private FirebaseAuth mAuth;

    //progessbar

    ProgressDialog progressDialog;

    //google api signin variables
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //actionbar and it's title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //before auth..config google sign in

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);



        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        emailEditText = findViewById(R.id.emailEditText);
        emailPassText = findViewById(R.id.emailPassText);
        nothave_accountTV = findViewById(R.id.nothave_accountTV);
        btn_Login = findViewById(R.id.btn_Login);

        recoverPassTv= findViewById(R.id.recoverPassTv);

        googleLogin_btn =findViewById(R.id.googleLogin_btn);

        //login button click

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //input data
                String email = emailEditText.getText().toString();
                String pass = emailPassText.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    //invalid email pattern set error
                    emailEditText.setError("Invalid email");
                    emailEditText.setFocusable(true);
                } else{

                    //if valid email pattern
                    loginUser(email,pass);
                }
            }
        });

        //dont have an account

        nothave_accountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in..");

        recoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showRecoveredPasswordDialog();
            }
        });

        //handle google sign up button click
        googleLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });


    }


    private void loginUser(String email, String pass) {

        //show progess dialog
        progressDialog.setMessage("Logging in..");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //if user is logged in, so start login activity
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                //error,get and show error msg
                Toast.makeText(getApplicationContext(),
                        e.getMessage().toString(),Toast.LENGTH_SHORT).show();


            }
        });

        //recover pass from text click



    }

    private void showRecoveredPasswordDialog() {

        //allert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover password");

        //set layout as linear layout
        LinearLayout layout =new LinearLayout(this);
        final EditText editText = new EditText(this);
        editText.setHint("Email");
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        editText.setMinEms(16);

        layout.addView(editText);
        layout.setPadding(10,10,10,10);
        builder.setView(layout);

        //button recover

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String email = editText.getText().toString().trim();
                beginRecover(email);

            }
        });

        //button cancel

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.dismiss();

            }
        });

        //show dialog
        builder.create().show();
    }

    private void beginRecover(String email) {

        progressDialog.setMessage("Sending email..");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                }else{

                    Toast.makeText(LoginActivity.this,"Email sending failed",Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                //show proper failed message
                Toast.makeText(LoginActivity.this,"Email sending failed "+e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }


    public boolean onSupportNavigateUp(){

        onBackPressed();
        return super.onSupportNavigateUp();
    }


//google api sign up method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
               // Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                          //  Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){

                                //get user gmail and uid from auth
                                String email =user.getEmail();
                                String uid = user.getUid();
                                //when a user registers , store the info in hashmap and store info
                                //in firebase and relatime databse too
                                HashMap<Object,String> hashmap = new HashMap<>();

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

                            }



                            //if authentication is sucessful

                            Toast.makeText(getApplicationContext(),user.getEmail(),Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                          //  Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                         //   updateUI(null);
                            Toast.makeText(getApplicationContext(),"Failed to connect!",Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //get and show error messages
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
