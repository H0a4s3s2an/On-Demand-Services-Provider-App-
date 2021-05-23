package com.example.sample.providerapp;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.MazdoorAccount;
import com.example.sample.providerapp.utilities.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static String TAG="EditProdile";
    public Context context=EditProfileActivity.this;
    public Activity activity=EditProfileActivity.this;
    CustomDialog customDialog;
    NetworkUtils helper;
    boolean isInternet;
    private ImageView profile;
    EditText profile_fname;
    EditText profile_lname;
    EditText profile_email;
    EditText profile_phone;
    EditText service_type;
    Button save;
    TextView changePasswordTxt;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    Uri filePathUri;
    private static final int Image_Request_Code = 432;
    String Storage_Path = "All_Image_Uploads/";
    Utilities utils = new Utilities();
    String id;
    Boolean isImageChanged = false;
    MazdoorAccount mazdoorAccount=new MazdoorAccount();
    String fname,lname,mobile,mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
        profile_fname=(EditText)findViewById(R.id.profile_fname);
        profile_lname=(EditText)findViewById(R.id.profile_lname);
        profile_email=(EditText)findViewById(R.id.profile_email);
        profile_phone=(EditText)findViewById(R.id.profile_phone);
        profile=(ImageView)findViewById(R.id.img_profile);
        service_type=(EditText)findViewById(R.id.service_type);
        save=(Button)findViewById(R.id.edit_profile_proceed);
        if(getIntent()!=null)
        {
            fname=getIntent().getStringExtra("fname");
            lname=getIntent().getStringExtra("lname");
            mobile=getIntent().getStringExtra("phone");
            mail=getIntent().getStringExtra("email");
        }
        profile_fname.setText(fname);
        profile_lname.setText(lname);
        profile_email.setText(mail);
        profile_phone.setText(mobile);
        service_type.setText(SharedHelper.getKey(context,"service"));
        if (!SharedHelper.getKey(context, "photo").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "photo").equalsIgnoreCase(null)
                && SharedHelper.getKey(context, "photo") != null)
        {
            Picasso.get()
                    .load(SharedHelper.getKey(context, "photo"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile);
        }
        changePasswordTxt=(TextView)findViewById(R.id.changePasswordTxt);
        changePasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this,ChangePasswordActivity.class));
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(profile_fname.getText().toString());
                Matcher lastName = ps.matcher(profile_lname.getText().toString());


                if (profile_email.getText().toString().equals("") || profile_email.getText().toString().length() == 0) {
                    displayMessage("Enter Your Email ID.");
                } else if (profile_phone.getText().toString().equals("") || profile_phone.getText().toString().length() == 0) {
                    displayMessage("Enter Yout Mobile Number.");
                } else if (profile_phone.getText().toString().length() < 10 || profile_phone.getText().toString().length() > 20) {
                    displayMessage("Mobile number length must be in between 10 to 20 digits.  ");
                } else if (profile_fname.getText().toString().equals("") || profile_fname.getText().toString().length() == 0) {
                    displayMessage("First name is Empty.");
                } else if (profile_lname.getText().toString().equals("") || profile_lname.getText().toString().length() == 0) {
                    displayMessage("Last name is Empty.");
                } else if (firstName.matches()) {
                    displayMessage("First name do not accept numbers.");
                } else if (lastName.matches()) {
                    displayMessage("Last name do not accept numbers.");
                } else {
                    if (isInternet) {
                        updateProfile();
                    } else {
                        displayMessage("Something went wrong");
                    }
                }
            }
        });
        helper=new NetworkUtils(context);
        isInternet=helper.isNetworkAvailable();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference("Driver");
        firebaseAuth=FirebaseAuth.getInstance();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission())
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE}, 432);
                else
                    goToImageIntent();
            }
        });
    }

    private void goToImageIntent() {
        Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Image_Request_Code);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 432)
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                    goToImageIntent();
    }

    private void updateProfile() {
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
            customDialog.show();
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                customDialog.dismiss();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    if(dataSnapshot1.exists())
                    {
                        dataSnapshot1.getRef().child("firstname").setValue(profile_fname.getText().toString());
                        dataSnapshot1.getRef().child("lastname").setValue(profile_lname.getText().toString());
                        dataSnapshot1.getRef().child("img").setValue(SharedHelper.getKey(context,"photo"));
//                        String fname = dataSnapshot1.child("firstname").getValue().toString();
//                        String lname = dataSnapshot1.child("lastname").getValue().toString();
//                        String photo=dataSnapshot1.child("img").getValue().toString();
//                        SharedHelper.putKey(context,"fname",fname);
//                        SharedHelper.putKey(context,"lname",lname);
//                        SharedHelper.putKey(context,"photo",photo);
                        startActivity(new Intent(EditProfileActivity.this,ProfileActivity.class));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToDashBoardActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(EditProfileActivity.this, DashboardActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        EditProfileActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Image_Request_Code && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePathUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                profile.setImageBitmap(bitmap);
                uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if(filePathUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference ref = storageReference.child("images/"+  System.currentTimeMillis() + "." + GetFileExtension(filePathUri));
            ref.putFile(filePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
//                           databaseReference=FirebaseDatabase.getInstance().getReference("Driver");
//                           databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                               @Override
//                               public void onDataChange(DataSnapshot dataSnapshot) {
//                                   for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
//                                   {
//                                       id=dataSnapshot1.getKey();
//                                   }
//                               }
//
//                               @Override
//                               public void onCancelled(DatabaseError databaseError) {
//
//                               }
//                           });
                            progressDialog.dismiss();
                            SharedHelper.putKey(context,"photo",taskSnapshot.getDownloadUrl().toString());
//                            Query query=databaseReference.orderByChild("img");
//                            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
//                                    {
//                                        dataSnapshot1.getRef().child("img").setValue(taskSnapshot.getDownloadUrl().toString());
//
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
                            displayMessage("upload Successfull");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            displayMessage("Upload Failed");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
