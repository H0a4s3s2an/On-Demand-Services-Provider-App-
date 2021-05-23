package com.example.sample.providerapp;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.MazdoorAccount;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Driver;

import dmax.dialog.SpotsDialog;

public class FlatActivity extends AppCompatActivity {
    private static final String TAG ="FlatActivity" ;
    private Button register;
    private int App_REQUEST_CODE = 432;
    CustomDialog dialog;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users;
    MazdoorAccount mazdoor;
    UIManager uiManager;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat);
        register = (Button) findViewById(R.id.register);
        login=(Button)findViewById(R.id.login);
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void login() {
        startActivity(new Intent(this,SignInActivity.class));
        finish();
    }

    private void signIn() {
        Intent intent = new Intent(FlatActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE, AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configurationBuilder.build());
        startActivityForResult(intent, App_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == App_REQUEST_CODE) {
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {
                Toast.makeText(this, "" + loginResult.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } else if (loginResult.wasCancelled()) {
                Toast.makeText(this, "login cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                if (loginResult.getAccessToken() != null) {
                    dialog=new CustomDialog(this);
                    dialog.show();
                    dialog.setCancelable(false);
                }
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            Log.e(TAG, "onSuccess: Account Kit" + account.getId());
                            Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
                            if (AccountKit.getCurrentAccessToken().getToken() != null) {
                                SharedHelper.putKey(FlatActivity.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());
                                PhoneNumber phoneNumber = account.getPhoneNumber();
                                String phoneNumberString = phoneNumber.toString();
                                SharedHelper.putKey(FlatActivity.this, "mobile", phoneNumberString);
                                registerAPI();
                            }
                            else
                            {
                                Intent goToLogin = new Intent(FlatActivity.this, FlatActivity.class);
                                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(goToLogin);
                                finish();
                            }
//                          final  String userId=account.getId();
//                            users.orderByKey().equalTo(account.getId())
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(final DataSnapshot dataSnapshot) {
//                                            if(!dataSnapshot.child(account.getId()).exists())
//                                            {
//                                                mazdoor=new MazdoorAccount();
//                                                mazdoor.setNumber(account.getPhoneNumber().toString());
//                                                mazdoor.setRating("0.0");
//                                                users.child(account.getId())
//                                                        .setValue(mazdoor).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        MazdoorAccount local=dataSnapshot.getValue(MazdoorAccount.class);
//                                                        startActivity(new Intent(FlatActivity.this,DashboardActivity.class));
//                                                        dialog.dismiss();
//                                                        finish();
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(FlatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                            }
//                                            else
//                                            {
//                                                users.child(account.getId())
//                                                        .setValue(mazdoor).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        MazdoorAccount localmazdoor=dataSnapshot.getValue(MazdoorAccount.class);
//                                                        startActivity(new Intent(FlatActivity.this,MainActivity.class));
//                                                        dialog.dismiss();
//                                                        finish();
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(FlatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                            }
//                                        }
//
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//                                            Toast.makeText(FlatActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Toast.makeText(FlatActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        }

    }

    private void registerAPI() {
        dialog=new CustomDialog(this);
        dialog.show();
        Intent intent=new Intent(this,RegisterActivity.class);
        intent.putExtra("number",SharedHelper.getKey(FlatActivity.this,"mobile"));
        startActivity(intent);

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
    protected void onStop() {
        super.onStop();
    }
}
