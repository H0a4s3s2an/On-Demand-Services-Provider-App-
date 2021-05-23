package com.example.sample.providerapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sample.providerapp.Common.Common;
import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Interface.IFCMService;
import com.example.sample.providerapp.Model.Billing;
import com.example.sample.providerapp.Model.FCMResponse;
import com.example.sample.providerapp.Model.History;
import com.example.sample.providerapp.Model.Notification;
import com.example.sample.providerapp.Model.Sender;
import com.example.sample.providerapp.Model.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceActivity extends AppCompatActivity {
    TextView tv_trip_total;
    String userId;
    Button bt_okay;
    Context context;
    IFCMService ifcmService;
    Double total;
    List<String> list=new ArrayList<String>();
    DatabaseReference databaseReference,billdb;
    String providerId,image,price,pay,uid;
    int i;
    Billing billing=new Billing();
    String providername;
    String userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        tv_trip_total=(TextView)findViewById(R.id.tv_trip_total);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invoice");
        databaseReference=FirebaseDatabase.getInstance().getReference("Driver");
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    providerId=dataSnapshot.getKey();
                    image=dataSnapshot1.child("img").getValue().toString();
                    providername=dataSnapshot1.child("firstname").getValue().toString();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ifcmService=Common.getFCMService();
        bt_okay=(Button)findViewById(R.id.bt_okay);
        bt_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payNow(uid);
            }
        });
        if(getIntent()!=null)
        {
            total=getIntent().getDoubleExtra("total",0.0);
            price=String.valueOf(total);
            tv_trip_total.setText(String.format("Rs %.2f",getIntent().getDoubleExtra("total",0.0)));
            userId=getIntent().getStringExtra("userId");
            uid=getIntent().getStringExtra("uid");
            userImage=getIntent().getStringExtra("userImage");
        }
    }

    private void payNow(String uid) {
        if(NetworkUtils.isNetworkAvailable(this))
        {
            billdb=FirebaseDatabase.getInstance().getReference("Payment");
            billing.setPrice(price);
            billing.setImage(image);
            billing.setUid(providerId);
            billing.setName(providername);
            String id = databaseReference.push().getKey();
            billdb.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(id).setValue(billing);
            DatabaseReference tokens= FirebaseDatabase.getInstance().getReference("Tokens");
            tokens.orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Token token = dataSnapshot1.getValue(Token.class);
                        String json_lat_lng = new Gson().toJson(billing);
                        String userToken = FirebaseInstanceId.getInstance().getToken();
                        Notification notification = new Notification(userToken, json_lat_lng);
                        Sender sender = new Sender(token.getToken(), notification);
                        ifcmService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                                if (response.body().success == 1) {
                                    Toast.makeText(InvoiceActivity.this, "Send", Toast.LENGTH_SHORT).show();
                                    MoveBack();
                                    ProviderHistory();
                                } else {
                                    Toast.makeText(InvoiceActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {
                                Toast.makeText(InvoiceActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            NetworkUtils.showNoNetworkDialog(this);
        }
    }
    private void ProviderHistory() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String strDate = dateFormat.format(date).toString();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("History");
        History history=new History();
        history.setPrice(total);
        history.setCategory(SharedHelper.getKey(this,"job_description"));
        history.setImage(userImage);
        history.setTime(strDate);
        history.setHistoryId(uid);
        String userId=databaseReference.push().getKey();
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userId).setValue(history);
    }
    private void MoveBack() {
        startActivity(new Intent(InvoiceActivity.this,DashboardActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
