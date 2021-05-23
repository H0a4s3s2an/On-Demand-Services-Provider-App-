package com.example.sample.providerapp.FCM;
import android.content.Intent;

import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.User;
import com.example.sample.providerapp.RequestActivity;
import com.example.sample.providerapp.utilities.Utilities;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";
    Utilities utils = new Utilities();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        User customer_location=new Gson().fromJson(remoteMessage.getNotification().getBody(), User.class);
        Intent intent=new Intent(getBaseContext(),RequestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("lat",customer_location.getLatitude());
        intent.putExtra("lng",customer_location.getLongitude());
        intent.putExtra("customer",remoteMessage.getNotification().getTitle());
        intent.putExtra("fname",customer_location.getFname());
        intent.putExtra("lname",customer_location.getLname());
        intent.putExtra("phone",customer_location.getMobile());
        intent.putExtra("photo",customer_location.getPicture());
        intent.putExtra("uid",customer_location.getUid());
        intent.putExtra("category",customer_location.getEmail());
        startActivity(intent);
    }
}
