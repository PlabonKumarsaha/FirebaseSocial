package com.example.firebasesocial;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.StorageTaskScheduler.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ImageView avatar_id, coverIV;
    TextView nameTV, emailTV, PhoneTV;
    //int grantResults[];

    //storage
    StorageReference storageReference;
    //path of storing images
    String storagepath = "Users_Profile_Cover_Imgs/";

    FloatingActionButton fab;
    ProgressDialog pd;

    //for checking  profile or cover photo
    String profileOrCoverPhooto ;

    Uri image_uri;


    //permit contsatnts
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 400;

    //array of permission to be requested
    String cameraPermission[];
    String storagePermission[];


    public ProfileFragment() {
        // Required empty public constructor
        /*FirebaseDatabase.getInstance().setPersistenceEnabled(true);*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (firebaseDatabase == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
//
            //databaseReference = database.getReference("User");

            // firebaseDatabase = database.getReference();
            // ...
        }

       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //path was taken from firebase storage>>data
        databaseReference = firebaseDatabase.getReference("User");
        storageReference = FirebaseStorage.getInstance().getReference(); //firebase storage refernce


        //init array of permission

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        avatar_id = view.findViewById(R.id.avatar_id);
        coverIV = view.findViewById(R.id.coverIV);
        nameTV = view.findViewById(R.id.nameTV);
        emailTV = view.findViewById(R.id.emailTV);
        PhoneTV = view.findViewById(R.id.PhoneTV);

        fab = view.findViewById(R.id.fab);

       // grantResults = new int[10];

        //init pd

        pd = new ProgressDialog(getActivity());
        /*
        we have to get info of cucrrenlty signed in user.We can get it by user's email or uid
        Here we are going to use user email for this

        by using ordered by child query we will show the details of a node.
        whoes key anmed email has value equal to the currently signed in email
         */

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until we get required data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();


                    //set data
                    nameTV.setText(name);
                    emailTV.setText(email);
                    PhoneTV.setText(phone);
                    try {
                        Picasso.get().load(image).into(avatar_id);
                    } catch (Exception e) {

                        //if there is an exception while getting image,then set the defaul one
                        Picasso.get().load(R.drawable.ic_deafult_face).into(avatar_id);

                    }

                    try {
                        Picasso.get().load(cover).into(coverIV);
                    } catch (Exception e) {

                        //if there is an exception while getting image,then set the defaul one
                        Picasso.get().load(R.drawable.ic_deafult_face).into(coverIV);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //fab button click
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showEditProfileDialog();
            }
        });
        return view;
    }

    private boolean checkStoragePermission() {
        //check for storage permission
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_DENIED);
        return result;
    }

    private void requestStoragePermission() {
        //request run time storage permit
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);

    }


    private boolean checkCameraPermission() {
        //check for storage permission
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        //request run time storage permit
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);

    }


    private void showEditProfileDialog() {

        //option to show in dialog
        String options[] = {"Edit Profile Picture", "Edit cover photo", "Edit Name", "Edit phone"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Choose Action");
        //set item to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    //edit profile clicked
                    pd.setMessage("Updating profile picture");
                    profileOrCoverPhooto = "image";
                    showImagePicDialog();


                } else if (i == 1) {

                    pd.setMessage("Updating cover picture");
                    profileOrCoverPhooto = "cover";
                    showImagePicDialog();
                } else if (i == 2) {

                    pd.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name");

                } else if (i == 3) {

                    pd.setMessage("Updating  phone no");
                    showNamePhoneUpdateDialog("phone");
                }


            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(final String key) {

        //this key value will either carry name or phone no

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update"+key);
        //see layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter :"+key); //hint edit name or phone
        linearLayout.addView(editText);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String value = editText.getText().toString().trim();

                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String,Object>resut = new HashMap<>();
                    resut.put(key,value);
                    databaseReference.child(user.getUid()).updateChildren(resut)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Updated .."+key,Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });


                }else{

                    Toast.makeText(getActivity(),"Please enter"+key,Toast.LENGTH_SHORT).show();

                }


            }

        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                pd.dismiss();

            }

        });

        //create and show dialog
        builder.create().show();


    }


    private void showImagePicDialog() {

        //option to show in dialog
        String options[] = {"Camera", "Gallery"};

        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("choose Image from");
        //set item to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    //camera clicked
                    if(!checkCameraPermission()){

                        requestCameraPermission();
                    } else {

                        pickFromCamera();
                    }


                } else if (i == 1) {
                    //gallery clicked
                    if(!checkStoragePermission()){
                        requestStoragePermission();

                    } else{

                        pickFromGallery();
                    }

                }


            }
        });

        //create and show dialog
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //method invoked when user presses allow or denies permission request  dialog
        //her we will handle permsiion case(allowed or denied)

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                //picking up from camera, first check if camera permission allowed or not
                if (grantResults.length > 0) {
                    boolean cameeraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameeraAccepted && writeStorageAccepted) {

                        //permission enabled
                        pickFromCamera();
                    } else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable camer and storage prmission", Toast.LENGTH_SHORT).show();
                    }

                }

            }
            break;
            case STORAGE_REQUEST_CODE: {

                //picking up from gallery, first check if camera permission allowed or not
                if (grantResults.length > 0) {


                    boolean cameeraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if ( cameeraAccepted && writeStorageAccepted) {

                        //permission enabled
                        pickFromGallery();
                    } else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable camer and storage prmission", Toast.LENGTH_SHORT).show();
                    }

                }

            }



        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //this method will be called after  picking image ffrom gallery
        if(requestCode == RESULT_OK){

            if(requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {

                //if the picture is gotten from gallery
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);


                if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {


                    uploadProfileCoverPhoto(image_uri);
                    //if image is seletec from camera

                    // uploadProfileCoverPhoto(image_uri);
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri image_uri) {
        //function works for uploading profile photo

        //give image unique name
        String filepathName = storagepath+""+profileOrCoverPhooto+"_"+user.getUid();

        StorageReference storageReference1 = storageReference.child(filepathName);
        storageReference1.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //images is uploaded in storage,now it's url and show storage in database
                Task<Uri>uriTask =taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()){
                    Uri downloadUri = uriTask.getResult();
                    Log.d("bug",downloadUri.toString());

                    //if image is uploaded  or not and url is recieved
                    if(uriTask.isSuccessful()){
                        //image uploaded
                        Toast.makeText(getActivity(),"Image uploaded debug",Toast.LENGTH_SHORT).show();


                        HashMap<String,Object> results = new HashMap<>();
                        //first parameter shows us about wether the image is cover or profile and the url signifies where the image is stored

                        results.put(profileOrCoverPhooto,downloadUri.toString());
                        databaseReference.child(user.getUid()).updateChildren(results)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                Toast.makeText(getActivity(),"Image uploaded",Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getActivity(),"Error uplaoding image...",Toast.LENGTH_SHORT).show();


                            }
                        });

                        //results.put(profileOrCoverPhooto,downloadUri.toString());
                    } else{
                        //error
                        pd.dismiss();
                        Toast.makeText(getActivity(),"erro ocured",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void pickFromGallery() {

   /*     ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        //put image
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);*/

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("images/*");
       // galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
    }


    private void pickFromCamera(){

   /*     //pick gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
      */

   ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);
    }
}
