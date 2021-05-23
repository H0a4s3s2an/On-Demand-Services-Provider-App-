package com.example.sample.providerapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anupamchugh.smoothcountdown.SmoothCircularProgressView;
import com.anupamchugh.smoothcountdown.SmoothProgressAnimationListener;
import com.example.sample.providerapp.Common.Common;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Interface.IFCMService;
import com.example.sample.providerapp.Interface.IGoogleAPI;
import com.example.sample.providerapp.Model.Booking;
import com.example.sample.providerapp.Model.FCMResponse;
import com.example.sample.providerapp.Model.History;
import com.example.sample.providerapp.Model.Notification;
import com.example.sample.providerapp.Model.Sender;
import com.example.sample.providerapp.Model.Token;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestActivity extends AppCompatActivity {
    TextView job_text;
    TextView job_text_second;
    TextView category_text;
    Location location;
    IGoogleAPI mservice;
    Button accept, reject;
    String userId;
    IFCMService ifcmService;
    ProgressBar smoothCircularProgress;
    ImageView img_map_route;
    double lat;
    double lng;
    String address;
    String fname,lname,phone,photo,uid,selected_category;
    double distance_value,time_value;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int COUNTDOWN_SECONDS = 30;
    private CountDownTimer mCountDownTimer;
    private MediaPlayer mRequestPlayer;
    private int mCountDownSeconds;
    private ValueEventListener mRequestStatusListener;
    private DatabaseReference mStatusReference;
    private DatabaseReference mStatusrejectReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        mStatusReference= FirebaseDatabase.getInstance().getReference("Booking").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("acceptedRequest");
        mStatusrejectReference= FirebaseDatabase.getInstance().getReference("Booking").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rejectedRequest");
        if(getWindow() != null){
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        mCountDownSeconds = COUNTDOWN_SECONDS;
        job_text = (TextView) findViewById(R.id.job_text);
        job_text_second = (TextView) findViewById(R.id.job_text_second);
        category_text = (TextView) findViewById(R.id.category_text);
        accept = (Button) findViewById(R.id.accept);
        reject = (Button) findViewById(R.id.reject);
        img_map_route=(ImageView)findViewById(R.id.img_map_route);
        ifcmService = Common.getFCMService();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(userId)) {
                    cancelBooking(userId);
                }
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestActivity.this, SessionActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("loc",address);
                intent.putExtra("customer",userId);
                intent.putExtra("fname",fname);
                intent.putExtra("lname",lname);
                intent.putExtra("phone",phone);
                intent.putExtra("photo",photo);
                intent.putExtra("distance_value",distance_value);
                intent.putExtra("time_value",time_value);
                intent.putExtra("uid",uid);
                startActivity(intent);
//                SharedPreferences countSettings = getSharedPreferences("accept", Context.MODE_PRIVATE);
//                int acceptRequest = countSettings.getInt("counts",0);
//                acceptRequest++;
//                final SharedPreferences.Editor edit = countSettings.edit();
//                edit.putInt("counts",acceptRequest);
//                edit.commit();
                mStatusReference.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Integer acceptedRequest = mutableData.getValue(Integer.class);
                        if(acceptedRequest==null)
                        {
                            mutableData.setValue(1);
                        }else {
                            mutableData.setValue(acceptedRequest + 1);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
//                booking.setAcceptedRequest(acceptRequest);
//                mStatusReference.child("acceptedRequest").setValue(acceptRequest);
                mRequestPlayer.stop();
            }
        });
        smoothCircularProgress = (ProgressBar) findViewById(R.id.smoothCircularProgress);
        mservice = Common.getGoogleAPI();
        if (getIntent() != null) {
            lat = getIntent().getDoubleExtra("lat", -1.0);
            lng = getIntent().getDoubleExtra("lng", -1.0);
            userId = getIntent().getStringExtra("customer");
            fname=getIntent().getStringExtra("fname");
            lname=getIntent().getStringExtra("lname");
            phone=getIntent().getStringExtra("phone");
            photo=getIntent().getStringExtra("photo");
            uid=getIntent().getStringExtra("uid");
            selected_category=getIntent().getStringExtra("category");
            SharedHelper.putKey(this,"job_description",selected_category);
            getDirection(lat, lng);
        }
        if(photo!=null)
        {
            Picasso.get()
                    .load(getIntent().getStringExtra("photo"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(img_map_route);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(img_map_route);
        }
        category_text.setText(selected_category);
        setUpCountDown();
    }

    private void setUpCountDown() {
        mRequestPlayer = MediaPlayer.create(RequestActivity.this, R.raw.request);
        mRequestPlayer.setLooping(true);
        mRequestPlayer.start();

        mCountDownTimer = new CountDownTimer(mCountDownSeconds * 1000, 100) {

            public void onTick(long millisUntilFinished) {
                mCountDownSeconds = (int)(millisUntilFinished/1000);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    smoothCircularProgress.setProgress((int)millisUntilFinished/100, true);
                }else {
                    smoothCircularProgress.setProgress((int) millisUntilFinished / 100);
                }
            }

            public void onFinish() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                   smoothCircularProgress.setProgress(0);
                }else {
                    smoothCircularProgress.setProgress(0);
                }
//                    sendCancelCommand(userId);
            }
        }.start();
    }
    private void sendCancelCommand(String userId) {
        mRequestPlayer.stop();
        mRequestPlayer.release();
        SharedPreferences countSettings = getSharedPreferences("reject", Context.MODE_PRIVATE);
        int rejectRequest = countSettings.getInt("counts",0);
        rejectRequest++;
        final SharedPreferences.Editor edit = countSettings.edit();
        edit.putInt("counts",rejectRequest);
        edit.commit();
        Booking booking=new Booking();
        booking.setRejectedRequest(rejectRequest);
        mStatusReference.child("rejectedRequest").setValue(rejectRequest);
        Token token = new Token(userId);
        Notification notification = new Notification("Busy", "Provider is not responding at the moment please choose another one");
        Sender sender = new Sender(token.getToken(), notification);
        ifcmService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1) {
                            Toast.makeText(RequestActivity.this, " Not responding", Toast.LENGTH_SHORT).show();
//                            Moveback();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
    }

    private void Moveback() {
        startActivity(new Intent(RequestActivity.this,DashboardActivity.class));
        finish();
    }


    private void cancelBooking(String userId) {
//        SharedPreferences countSettings = getSharedPreferences("reject", Context.MODE_PRIVATE);
//        int rejectRequest = countSettings.getInt("counts",0);
//        rejectRequest++;
//        final SharedPreferences.Editor edit = countSettings.edit();
//        edit.putInt("counts",rejectRequest);
//        edit.commit();
//        Booking booking=new Booking();
//        booking.setRejectedRequest(rejectRequest);
//        mStatusReference.child("rejectedRequest").setValue(rejectRequest);
        mStatusrejectReference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer rejectedRequest = mutableData.getValue(Integer.class);
                if(rejectedRequest==null)
                {
                    mutableData.setValue(1);
                }else {
                    mutableData.setValue(rejectedRequest + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
        mRequestPlayer.stop();
        Token token = new Token(userId);
        Notification notification = new Notification("Cancel", "Provider has cancelled your request");
        Sender sender = new Sender(token.getToken(), notification);
        ifcmService.sendMessage(sender)
                .enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body().success == 1) {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();
                            String strDate = dateFormat.format(date).toString();
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("History");
                            History history=new History();
                            history.setPrice(0);
                            history.setStatus("Status: Incomplete");
                            history.setTime(strDate);
                            history.setImage("https://pixabay.com/en/head-the-dummy-avatar-man-tie-659651/");
                            history.setHistoryId(uid);
                            String userId=databaseReference.push().getKey();
                            databaseReference.child(uid).child(userId).setValue(history);
                            Toast.makeText(RequestActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
    }

    private void getDirection(final double lat, final double lng) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location mlocation) {
                location = mlocation;
                if (location != null) {
                    String requestApi = null;
                    requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                            "mode=driving&" +
                            "transit_routing_preference=less_driving&" +
                            "origin=" + location.getLatitude() + "," + location.getLongitude() + "&" +
                            "destination=" + lat + "," + lng + "&" +
                            "key=" + getResources().getString(R.string.google_direction_api);
                    mservice.getPath(requestApi)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        JSONArray routes = jsonObject.getJSONArray("routes");
                                        JSONObject object = routes.getJSONObject(0);
                                        JSONArray legs = object.getJSONArray("legs");
                                        JSONObject legsObject = legs.getJSONObject(0);
                                        JSONObject distance = legsObject.getJSONObject("distance");
                                        String distance_text=distance.getString("text");
                                        distance_value=Double.parseDouble(distance_text.replaceAll("[^0-9\\\\.]+",""));
                                        JSONObject time_object = legsObject.getJSONObject("duration");
                                        String time_text=time_object.getString("text");
                                        time_value=Double.parseDouble(time_text.replaceAll("[^0-9\\\\.]+",""));
//                                        job_text.setText(distance.getString("text"));
                                        address = legsObject.getString("end_address");
                                        job_text_second.setText(address);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        teardownAudio();
    }

    private void teardownAudio() {
        if (mRequestPlayer != null) {
            if (mRequestPlayer.isPlaying()) {
                mRequestPlayer.stop();
            }
            mRequestPlayer.reset();
            mRequestPlayer.release();
            mRequestPlayer = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
}