package com.example.sample.providerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.utilities.Utilities;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity {
    public Context context = SignInActivity.this;
    public Activity activity = SignInActivity.this;
    String TAG = "SignInActivity";
    TextView mail;
    TextView password;
    Button next;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    Utilities utils = new Utilities();
    NetworkUtils helper;
    Boolean isInternet;
    CustomDialog dialog;
    TextView fog_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");
        helper = new NetworkUtils(context);
        isInternet = helper.isNetworkAvailable();
        databaseReference = FirebaseDatabase.getInstance().getReference("Driver");
        firebaseAuth = FirebaseAuth.getInstance();
        mail = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        fog_password = (TextView) findViewById(R.id.fog_password);
        fog_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mail.getText().toString().equals("") || mail.getText().toString().equalsIgnoreCase("name@example.com")) {
                    displayMessage("Enter your Email ID.");
                } else if (!Utilities.isValidEmail(mail.getText().toString())) {
                    displayMessage("Email is not valid");
                } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase("******")) {
                    displayMessage("Password could not be Empty");
                } else if (password.length() < 6) {
                    displayMessage("password must be atleast 6 number");
                } else {
                    if (isInternet) {
                        dialog = new CustomDialog(context);
                        dialog.show();
                        dialog.setCancelable(false);
                        firebaseAuth.signInWithEmailAndPassword(mail.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                dialog.dismiss();
                                getProfile();
                                Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Sign in Fail");
                                dialog.dismiss();
                                displayMessage("Check your username and password");
                            }
                        });
                    } else {
                        displayMessage("Something went wrong");
                    }
                }
            }
        });
    }

    private void resetPassword() {
        startActivity(new Intent(this, PasswordResetActivity.class));
    }

    private void getProfile() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Driver");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    String fname = datas.child("firstname").getValue().toString();
                    String lname = datas.child("lastname").getValue().toString();
                    String email = datas.child("email").getValue().toString();
                    String service = datas.child("serviceId").getValue().toString();
                    String photo = datas.child("img").getValue().toString();
                    String phone = datas.child("number").getValue().toString();
                    SharedHelper.putKey(context, "fname", fname);
                    SharedHelper.putKey(context, "lname", lname);
                    SharedHelper.putKey(context, "email", email);
                    SharedHelper.putKey(context, "service", service);
                    SharedHelper.putKey(context, "phone", phone);
                    SharedHelper.putKey(context, "photo", photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("", databaseError.toString());
            }
        });
    }

    public void Signup(View view) {
      Intent intent = new Intent(this,AccountKitActivity.class);
        startActivity(intent);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        boolean fromActivity = false;
        if (fromActivity) {
            Intent mainIntent = new Intent(SignInActivity.this, FlatActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            SignInActivity.this.finish();
        } else {
            Intent mainIntent = new Intent(SignInActivity.this, FlatActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            SignInActivity.this.finish();
        }
    }
}


