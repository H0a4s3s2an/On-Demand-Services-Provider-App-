package com.example.sample.providerapp;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnActiveListener;
import com.ebanx.swipebtn.SwipeButton;
import com.example.sample.providerapp.Common.Common;
import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Helper.DirectionJsonParser;
import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Interface.IFCMService;
import com.example.sample.providerapp.Interface.IGoogleAPI;
import com.example.sample.providerapp.Model.Booking;
import com.example.sample.providerapp.Model.FCMResponse;
import com.example.sample.providerapp.Model.History;
import com.example.sample.providerapp.Model.Notification;
import com.example.sample.providerapp.Model.Sender;
import com.example.sample.providerapp.Model.Token;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.ActivityRecognition.API;

public class SessionActivity extends AppCompatActivity implements OnMapReadyCallback{
    SwipeButton swipe_btn;
    double userlat, userlng;
    private GoogleMap mMap;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private LocationRequest locationRequest;
    private GoogleApiClient apiClient;
    private Location location;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 5000;
    private static final int My_PERMISSION_REQUEST_CODE = 7000;
    private Circle radiusMarker;
    private Marker driverMarker;
    private Polyline direction;
    IGoogleAPI mservice;
    IFCMService ifcmService;
    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationCallback locationCallback;
    public Context context;
    TextView tv_address;
    ImageButton ib_navigat;
    ImageButton ib_menu;
    private boolean mMenuShown;
    FrameLayout mDarkScreen;
    ImageButton mCancelButton;
    ImageButton mCallButton;
    View mMainContent;
    ConstraintLayout mHiddenPanel;
    GeoFire geoFire;
    String userId,firstname,lastname,phone,profile_image,loc,uid;
    double distance_value,time_value;
    ImageView iv_profile;
    TextView tv_rider_name;
    ImageButton ib_cancel_trip;
    ImageButton ib_call_rider;
    private static final int REQUEST_CALL = 10;
    int cancelcount=0;
    private DatabaseReference mStatusReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        swipe_btn = (SwipeButton) findViewById(R.id.swipe_btn);
        mDarkScreen=(FrameLayout)findViewById(R.id.dark_screen);
        mCancelButton=(ImageButton)findViewById(R.id.ib_cancel_request);
        mCallButton=(ImageButton)findViewById(R.id.ib_call_rider);
        mHiddenPanel=(ConstraintLayout)findViewById(R.id.hidden_panel);
        iv_profile=(ImageView)findViewById(R.id.iv_profile);
        tv_rider_name=(TextView)findViewById(R.id.tv_rider_name);
        ib_cancel_trip=(ImageButton)findViewById(R.id.ib_cancel_request);
        ib_call_rider=(ImageButton)findViewById(R.id.ib_call_rider);
        ib_call_rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRider();
            }
        });
        ib_cancel_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBooking();
            }
        });
        ifcmService=Common.getFCMService();
        mDarkScreen.setClickable(false);
        mMainContent=(View)findViewById(R.id.cl_main_content);
        mDarkScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUpDown();
            }
        });
        tv_address=(TextView)findViewById(R.id.tv_address);
        ib_navigat=(ImageButton)findViewById(R.id.ib_navigate);
        ib_menu=(ImageButton)findViewById(R.id.ib_menu);
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUpDown();
            }
        });
        ib_navigat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationQuery=getIntent().getStringExtra("loc");
//                launchWazeNavigation(locationQuery);
                launchGoogleNavigation(locationQuery);
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mservice = Common.getGoogleAPI();
        setUpGoogleMap();
        loadRiderPhone();
        SetUpLocation();
        setSwipeButton();
        if (getIntent() != null) {
            userlat = getIntent().getDoubleExtra("lat", -1.0);
            userlng = getIntent().getDoubleExtra("lng", -1.0);
            tv_address.setText(getIntent().getStringExtra("loc"));
            userId=getIntent().getStringExtra("customer");
            firstname=getIntent().getStringExtra("fname");
            lastname=getIntent().getStringExtra("lname");
            profile_image=getIntent().getStringExtra("photo");
            phone=getIntent().getStringExtra("phone");
            distance_value=getIntent().getDoubleExtra("distance_value",-1.0);
            time_value=getIntent().getDoubleExtra("time_value",-1.0);
            uid=getIntent().getStringExtra("uid");
        }
        enableHiddenButtons(false);
        if(profile_image!=null)
        {
            Picasso.get()
                    .load(getIntent().getStringExtra("photo"))
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
        tv_rider_name.setText(firstname+" "+lastname);
        mStatusReference= FirebaseDatabase.getInstance().getReference("Booking").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("cancelBooking");
    }

    private void callRider() {
        if(phone != null ){

            //Check if we have permission
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE);

            if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                callPhoneNumber(true);
            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CALL);
            }


        }else {
            Toast.makeText(SessionActivity.this,"Call unavailable try again soon", Toast.LENGTH_SHORT).show();
            loadRiderPhone();
        }
    }

    private void callPhoneNumber(boolean call) {
        try {
            if(call) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }else {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void cancelBooking() {
        showCancelDialog(this);
    }
    public void showCancelDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Cancel booking");
        builder.setMessage("Are you sure you want to cancel Booking");

        builder.setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CancelBooking(userId);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void CancelBooking(String userId) {
        if(NetworkUtils.isNetworkAvailable(this))
        {
//            SharedPreferences countSettings = getSharedPreferences("cancelBooking", Context.MODE_PRIVATE);
//            int cancelBooking = countSettings.getInt("counts",0);
//            cancelBooking++;
//            final SharedPreferences.Editor edit = countSettings.edit();
//            edit.putInt("counts",cancelBooking);
//            edit.commit();
//            Booking booking=new Booking();
//            booking.setCancelBooking(cancelBooking);
//            mStatusReference.child("cancelBooking").setValue(cancelBooking);
            mStatusReference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Integer cancelBooking = mutableData.getValue(Integer.class);
                    if(cancelBooking==null)
                    {
                        mutableData.setValue(1);
                    }else {
                        mutableData.setValue( cancelBooking + 1);
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
            Token token=new Token(userId);
            Notification notification=new Notification("Booking_Cancelled",String.format("The Provider Cancel your Booking",firstname+""+lastname));
            Sender sender=new Sender(token.getToken(),notification);
            ifcmService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(@NonNull Call<FCMResponse> call, @NonNull Response<FCMResponse> response) {
                    if(response.body().success!=1)
                    {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String strDate = dateFormat.format(date).toString();
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("History");
                        History history=new History();
                        history.setPrice(0);
                        history.setStatus("Status: Booking-Cancelled");
                        history.setTime(strDate);
                        history.setImage("https://pixabay.com/en/head-the-dummy-avatar-man-tie-659651/");
                        history.setHistoryId(uid);
                        String userId=databaseReference.push().getKey();
                        databaseReference.child(uid).child(userId).setValue(history);
                        Toast.makeText(SessionActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                        moveback();
                    }
                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {

                }
            });
        }else
        {
            NetworkUtils.showNoNetworkDialog(this);
        }
        SharedHelper.putKey(this,"count", String.valueOf(cancelcount));
    }

    private void moveback() {
        startActivity(new Intent(SessionActivity.this,DashboardActivity.class));
    }

    private void enableHiddenButtons(boolean enable) {
        if(enable){
            mCallButton.setEnabled(true);
            mCancelButton.setEnabled(true);
        }else {
            mCallButton.setEnabled(false);
            mCancelButton.setEnabled(false);
        }
    }
private void setSwipeButton()
{
    swipe_btn.setOnActiveListener(new OnActiveListener() {
        @Override
        public void onActive() {
            if (NetworkUtils.isNetworkAvailable(SessionActivity.this)) {
                sendArriveNotification(userId);
                Intent intent=new Intent(SessionActivity.this,JobProgressActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("fname",firstname);
                intent.putExtra("lname",lastname);
                intent.putExtra("photo",profile_image);
                intent.putExtra("loc",getIntent().getStringArrayExtra("loc"));
                intent.putExtra("distance_value",distance_value);
                intent.putExtra("time_value",time_value);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }

            else {
                NetworkUtils.showNoNetworkDialog(SessionActivity.this);
            }
        }
        });

    final AnimationDrawable drawable = new AnimationDrawable();
    final Handler handler = new Handler();

    drawable.addFrame(ContextCompat.getDrawable(SessionActivity.this, R.drawable.shape_rounded_red), 1000);
    drawable.addFrame(ContextCompat.getDrawable(SessionActivity.this, R.drawable.shape_rounded_dark), 1000);
    drawable.setOneShot(false);

    swipe_btn.setSlidingButtonBackground(drawable);
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            drawable.start();
        }
    }, 100);
}


    private void slideUpDown() {
        if (mMenuShown) {
            // Hide the Panel
            mDarkScreen.setClickable(false);
            enableHiddenButtons(false);


            mDarkScreen.setVisibility(View.INVISIBLE);
            mMainContent.animate().translationY(0).setDuration(300);

            mMenuShown = false;
        }
        else {
            // Show the panel
            mDarkScreen.setClickable(true);
            enableHiddenButtons(true);

            mDarkScreen.setVisibility(View.VISIBLE);
            mMainContent.animate().translationY(-1 * mHiddenPanel.getHeight()).setDuration(300);

            mMenuShown = true;
        }
    }

    private void launchGoogleNavigation(String locationQuery) {
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="
                + locationQuery + "&mode=d"));

        navigationIntent.setPackage("com.google.android.apps.maps");

        if (navigationIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(navigationIntent);
        } else {
            //Open store link
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")));
            }
        }
    }

    private void launchWazeNavigation(String locationQuery) {
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://waze.com/ul?ll="
                + locationQuery + "&navigate=yes"));


        if (navigationIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(navigationIntent);
        } else {
            //Open store link
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.waze")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.waze")));
            }

        }
    }
    private void SetUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, My_PERMISSION_REQUEST_CODE);
        } else {
            buildLocationCallback();
            createLocationRequest();
            displayLocation();
        }
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                location = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
                displayLocation();
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case My_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest();
                    displayLocation();
                }
        }
    }

    private void setUpGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location mlocation) {
                location=mlocation;
                if (location != null) {
                    final double latitude = location.getLatitude();
                    final double longitude = location.getLongitude();
                        driverMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude,longitude))
                                .title("You")
                                .icon(BitmapDescriptorFactory.defaultMarker()));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),17.0f));
                            getDirection(new LatLng(latitude,longitude));
                } else {
                    Log.d("Error", "Cannot get your location");
                }
            }
        });

    }

    private void getDirection(LatLng location) {
        String requestApi = null;
        requestApi = "https://maps.googleapis.com/maps/api/directions/json?" +
                "mode=driving&" +
                "transit_routing_preference=less_driving&" +
                "origin=" + location.latitude + "," + location.longitude + "&" +
                "destination=" + userlat + "," + userlng + "&" +
                "key=" + getResources().getString(R.string.google_direction_api);
        mservice.getPath(requestApi)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        new ParserTask().execute(response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    private void loadRiderPhone() {

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        radiusMarker=mMap.addCircle(new CircleOptions()
                .center(new LatLng(userlat,userlng))
                .radius(50)
                .strokeColor(Color.RED)
                .fillColor(0x22000FF)
                .strokeWidth(5.0f));
        geoFire=new GeoFire(FirebaseDatabase.getInstance().getReference("Drivers"));
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(userlat,userlng),0.05f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void sendArriveNotification(String userId) {
        Token token=new Token(userId);
        Notification notification=new Notification("Arrived",String.format("The Provider has arrived at you location",FirebaseAuth.getInstance().getCurrentUser()));
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

    @SuppressLint("StaticFieldLeak")
    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
        ProgressDialog customDialog=new ProgressDialog(SessionActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            customDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionJsonParser parser = new DirectionJsonParser();
                routes=parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            customDialog.dismiss();
            ArrayList points=null;
            PolylineOptions polylineOptions=null;
            for(int i=0;i<lists.size();i++)
            {
                points=new ArrayList();
                polylineOptions=new PolylineOptions();
                List<HashMap<String,String>> path= lists.get(i);
                for(int j=0;j<path.size();j++)
                {
                    HashMap<String,String> point=path.get(j);
                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));
                    LatLng position=new LatLng(lat,lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(20);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }
        }
    }
    @Override
    public void onBackPressed() {
            moveTaskToBack(true);
        }
}
