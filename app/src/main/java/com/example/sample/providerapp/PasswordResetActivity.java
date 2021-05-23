package com.example.sample.providerapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.utilities.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    CustomDialog customDialog;
    private EditText reset_email;
    private Button reset_button;
    String email;
    Utilities utils=new Utilities();
    private boolean fromActivity=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Reset Password");
        firebaseAuth=FirebaseAuth.getInstance();
        reset_email=(EditText)findViewById(R.id.reset_email);
        reset_button=(Button) findViewById(R.id.reset_button);
        customDialog=new CustomDialog(this);
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=reset_email.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(TextUtils.isEmpty(email))
                {
                    displayMessage("Enter Email Address");
                }
                else if(!email.matches(emailPattern))
                {
                    displayMessage("Email is Invalid.");
                }
                else
                {
                    customDialog.show();
                    customDialog.setCancelable(false);
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            customDialog.dismiss();
                            if(task.isSuccessful())
                            {
                                displayMessage("We have send you instruction to rest your password ");
                            }
                            else
                            {
                                displayMessage("Failed to send Email.");
                            }
                        }
                    });
                }
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (fromActivity) {
            Intent mainIntent = new Intent(PasswordResetActivity.this, FlatActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            PasswordResetActivity.this.finish();
        } else {
            Intent mainIntent = new Intent(PasswordResetActivity.this, FlatActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            PasswordResetActivity.this.finish();
        }
    }
}
