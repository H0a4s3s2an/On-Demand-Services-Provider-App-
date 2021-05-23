package com.example.sample.providerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.MazdoorAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    public  static final int SPLASH_TIME_OUT=5000;
    public Activity activity=SplashActivity.this;
    public Context context=SplashActivity.this;
    NetworkUtils network;
    boolean isInternet;
    Handler handler;
    AlertDialog alertDialog;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    MazdoorAccount mazdoor=new MazdoorAccount();
     String user_id;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference("Driver");
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        network=new NetworkUtils(context);
        isInternet=network.isNetworkAvailable();
        handler=new Handler();
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(network.isNetworkAvailable()) {

                    if(firebaseAuth.getUid()!=null)
                    {
                        getProfile();
                        startActivity(new Intent(SplashActivity.this,DashboardActivity.class));
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(), FlatActivity.class));
                    }
                    if(alertDialog!=null && alertDialog.isShowing())
                    {
                        alertDialog.dismiss();
                    }
                }
                else
                {
                    showDialog();
                    handler.postDelayed(this,3000);
                }
            }
        },5000);
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
                    String service= datas.child("serviceId").getValue().toString();
                    String photo=datas.child("img").getValue().toString();
                    String phone = datas.child("number").getValue().toString();
                    SharedHelper.putKey(context,"fname",fname);
                    SharedHelper.putKey(context,"lname",lname);
                    SharedHelper.putKey(context,"email",email);
                    SharedHelper.putKey(context,"service",service);
                    SharedHelper.putKey(context,"phone",phone);
                    SharedHelper.putKey(context,"photo",photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("",databaseError.toString());
            }
        });
    }
//            Intent intent=new Intent(SplashActivity.this,DashboardActivity.class);
//            intent.putExtra("id",user.getUid());
//            intent.putExtra("name",);
//            intent.putExtra("email",user.getEmail());
//            intent.putExtra("phone",user.getPhoneNumber());
//            intent.putExtra("photo",user.getPhotoUrl());
//            startActivity(intent);
//
//        }
//        if(firebaseAuth.getCurrentUser().getUid()!=null) {
//            SharedHelper.putKey(context,"id",databaseReference.getKey());
//            SharedHelper.putKey(context,"first_name",mazdoor.getFirstname());
//            SharedHelper.putKey(context,"last_name",mazdoor.getLastname());
//            SharedHelper.putKey(context,"email",mazdoor.getEmail());
//            SharedHelper.putKey(context,"phone",mazdoor.getNumber());
//            SharedHelper.putKey(context,"service",mazdoor.getServiceId());
//            SharedHelper.putKey(context,"photo",mazdoor.getImg());
//           Intent intent=new Intent();
//           intent.putExtra("did",SharedHelper.getKey(context,"id"));
//           intent.putExtra("firstname",SharedHelper.getKey(context,"first_name"));
//           intent.putExtra("lastname",SharedHelper.getKey(context,"last_name"));
//           intent.putExtra("mail",SharedHelper.getKey(context,"email"));
//           intent.putExtra("phone",SharedHelper.getKey(context,"phone"));
//           intent.putExtra("photo",SharedHelper.getKey(context,"photo"));
//           intent.putExtra("service",SharedHelper.getKey(context,"service"));
//           startActivity(new Intent(this,DashboardActivity.class));
//
//        }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
}
