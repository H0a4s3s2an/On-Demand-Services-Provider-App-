package com.example.sample.providerapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.example.sample.providerapp.Common.Common;
import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Interface.IFCMService;
import com.example.sample.providerapp.Model.FCMResponse;
import com.example.sample.providerapp.Model.Notification;
import com.example.sample.providerapp.Model.Sender;
import com.example.sample.providerapp.Model.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobProgressActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private long pauseOffset;
    private boolean running;
    private long timeWhenStopped = 0;
    ImageView iv_profile;
    TextView textView2,tv_rider_name;
    String userId,firstname,lastname,image_profile,location,uid;
    Context context;
    IFCMService ifcmService;
    SwipeButton swipe_btn;
    double distance_value;
    double time_value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Progress");
        chronometer = findViewById(R.id.chronometer);
        swipe_btn=(SwipeButton)findViewById(R.id.swipe_btn);
        setSwipeButton();
        iv_profile=(ImageView)findViewById(R.id.iv_profile);
        textView2=(TextView)findViewById(R.id.textView2);
        tv_rider_name=(TextView)findViewById(R.id.tv_rider_name);
        ifcmService= Common.getFCMService();
//        chronometer.setFormat("Time: %s");
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText(t);
            }
        });
        chronometer.setText("00:00:00");
        if(getIntent()!=null)
        {
            uid=getIntent().getStringExtra("uid");
            userId=getIntent().getStringExtra("userId");
            firstname=getIntent().getStringExtra("fname");
            lastname=getIntent().getStringExtra("lname");
            image_profile=getIntent().getStringExtra("photo");
            location=getIntent().getStringExtra("loc");
            distance_value=getIntent().getDoubleExtra("distance_value",-1.0);
            time_value=getIntent().getDoubleExtra("time_value",-1.0);
        }
        textView2.setText(firstname+" "+lastname);
//        tv_rider_name.setText(distance_value);
        if(image_profile!=null)
        {

            Picasso.get()
                    .load(image_profile)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(iv_profile);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(iv_profile);
        }
    }
    private void setSwipeButton()
    {
        swipe_btn.setOnActiveListener(new OnActiveListener() {
            @Override
            public void onActive() {
                if (NetworkUtils.isNetworkAvailable(JobProgressActivity.this)) {
                    int elapsedMillis = (int) (SystemClock.elapsedRealtime() - chronometer.getBase());
                    double minutes=(elapsedMillis / (1000.0 * 60)) % 60;
                    JobFinished(userId);
                    Intent intent=new Intent(JobProgressActivity.this,InvoiceActivity.class);
                    intent.putExtra("distance_value",distance_value);
                    intent.putExtra("time_value",time_value);
                    intent.putExtra("time",minutes);
                    intent.putExtra("total",Common.calculatePrice(distance_value,time_value,minutes));
                    intent.putExtra("userId",userId);
                    intent.putExtra("uid",uid);
                    intent.putExtra("userImage",image_profile);
                    startActivity(intent);
                }

                else {
                    NetworkUtils.showNoNetworkDialog(JobProgressActivity.this);
                }
            }
        });

        final AnimationDrawable drawable = new AnimationDrawable();
        final Handler handler = new Handler();

        drawable.addFrame(ContextCompat.getDrawable(JobProgressActivity.this, R.drawable.shape_rounded_red), 1000);
        drawable.addFrame(ContextCompat.getDrawable(JobProgressActivity.this, R.drawable.shape_rounded_dark), 1000);
        drawable.setOneShot(false);

        swipe_btn.setSlidingButtonBackground(drawable);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawable.start();
            }
        }, 100);
    }
    private void JobFinished(String userId) {
        Token token=new Token(userId);
        Notification notification=new Notification("Job_Finished",String.format("The Provider has finished th job", FirebaseAuth.getInstance().getCurrentUser()));
        Sender sender=new Sender(token.getToken(),notification);
        ifcmService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                if(response.body().success!=1)
                {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });
    }

    private void JobStarted(String userId) {
        Token token=new Token(userId);
        Notification notification=new Notification("Job_Started",String.format("The Provider has just Started the Job", FirebaseAuth.getInstance().getCurrentUser()));
        Sender sender=new Sender(token.getToken(),notification);
        ifcmService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                if(response.body().success!=1)
                {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

            }
        });
    }

    public void startChronometer(View view) {
//        if (!running) {
//            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
//            chronometer.start();
//            running = true;
//        }
        chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chronometer.start();
        JobStarted(userId);
    }

    public void pauseChronometer(View view) {
//        if (running) {
//            chronometer.stop();
//            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
//            running = false;
//        }
        timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
        chronometer.stop();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
