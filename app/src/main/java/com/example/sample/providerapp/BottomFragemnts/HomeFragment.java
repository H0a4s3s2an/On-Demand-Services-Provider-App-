package com.example.sample.providerapp.BottomFragemnts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sample.providerapp.DashboardActivity;
import com.example.sample.providerapp.FCM.MyFirebaseInstanceIDService;
import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.MazdoorAccount;
import com.example.sample.providerapp.Model.Token;
import com.example.sample.providerapp.utilities.DialogUtilities;
import com.example.sample.providerapp.utilities.LocationUtils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.example.sample.providerapp.R;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.john.waveview.WaveView;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;
import static com.google.android.gms.location.ActivityRecognition.API;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * Created by Hassan Javaid on 6/8/2018.
 */

public class HomeFragment extends Fragment implements OnMapReadyCallback, DialogInterface.OnClickListener {
    private GoogleMap mMap;
    private static final int My_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;
    private LocationRequest locationRequest;
    private GoogleApiClient apiClient;
    private Location location;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 5000;
    private static final int REQUEST_LOCATION = 1;
    public static final int LOCATION_SETTINGS = 2;
    DatabaseReference drivers, onlineRef, currentUserRef;
    GeoFire geoFire;
    Marker marker;
    SupportMapFragment mapFragment;
    SwitchCompat mOnlineSwitch;
    WaveView mWaveView;
    TextView mOnlineText;
    CoordinatorLayout mCoordinatorLayout;
    private CountDownTimer mCountDownTimer;
    private static final int MAX_GPS_SEARCH_SECONDS = 30;
    private boolean mLocationFound;
    MazdoorAccount mazdoorAccount;
    FusedLocationProviderClient fusedLocationProviderClient;
    com.google.android.gms.location.LocationCallback locationCallback;
    public static volatile boolean mIsOnline;
    private boolean mShowedProgressDialog,mTryingOnline,mReturningFromSettingsIntent,check;
    private static final int REQUEST_GEO = 3;
    boolean onlineSwitch;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        mIsOnline = false;
        mShowedProgressDialog = false;
        mTryingOnline = false;
        mReturningFromSettingsIntent=false;
        setUpCountDown();
            return rootview;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setUpCountDown() {
        mCountDownTimer = new CountDownTimer(MAX_GPS_SEARCH_SECONDS * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (mLocationFound) {
                    mCountDownTimer.cancel();
                }
            }

            public void onFinish() {
                if (!mLocationFound) {
                    removeLocationUpdates();
                }
            }
        };
    }

    private void removeLocationUpdates() {
        Log.d("Location Listener", "Removed");
        try {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpGoogleMap();

    }

    private void setUpGoogleMap() {
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mOnlineSwitch = (SwitchCompat) getView().findViewById(R.id.online_switch);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Settings",MODE_PRIVATE);
        mOnlineSwitch.setChecked(sharedPref.getBoolean("switchValue",false));
        mWaveView = (WaveView) getView().findViewById(R.id.wave_view);
        mOnlineText = (TextView) getView().findViewById(R.id.tv_online);
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        currentUserRef = FirebaseDatabase.getInstance().getReference("Drivers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserRef.onDisconnect().removeValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mCoordinatorLayout = (CoordinatorLayout) getView().findViewById(R.id.cl_home);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        final SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        mOnlineSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION);

                    if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                        retrieveLocation();
                    }else {
                        requestPermissions(
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION);
                    }
                    editor.putBoolean("switchValue", true).commit();
                    Toast.makeText(getActivity(), "Online now", Toast.LENGTH_SHORT).show();
                    mWaveView.setProgress(90);
                    FirebaseDatabase.getInstance().goOnline();
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    DisplayLocation();
                } else {
                    editor.putBoolean("switchValue", false).commit();
                    Toast.makeText(getActivity(), "Offline now", Toast.LENGTH_SHORT).show();
                    mWaveView.setProgress(10);
                    FirebaseDatabase.getInstance().goOffline();
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    mMap.clear();
                }
            }
        });
        drivers = FirebaseDatabase.getInstance().getReference("Drivers");
        geoFire = new GeoFire(drivers);
        SetUpLocation();
        updateFirebaseToken();
    }

    private void updateFirebaseToken() {
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        firebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    private void SetUpLocation() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    My_PERMISSION_REQUEST_CODE);
        } else {
            buildLocationRequest();
            buildLocationCallback();
            if (mOnlineSwitch.isChecked())
                DisplayLocation();
        }
    }

    private void buildLocationCallback() {
        locationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location mlocation : locationResult.getLocations()) {
                    location = mlocation;
                }
                DisplayLocation();
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
    }

    private void DisplayLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location mlocation) {
                location = mlocation;
                if (location != null) {
                    if (mOnlineSwitch.isChecked()) {
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();
                        geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(latitude, longitude), new GeoFire.CompletionListener() {
                            @Override
                            public void onComplete(String key, DatabaseError error) {
                                if (marker != null)
                                    marker.remove();
                                marker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(latitude, longitude))
                                        .title("You")
                                        .icon(BitmapDescriptorFactory.defaultMarker()));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),12));
                            }
                        });
                    }
                } else {
                    Log.d("Error", "Cannot get your location");
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retrieveLocation();

                } else if (shouldShowRequestPermissionRationale( android.Manifest.permission.ACCESS_FINE_LOCATION)){
                    DialogUtilities.showDeniedDialog(getActivity(), this,"Permission Denied");
                }
                return;
            }

            case REQUEST_GEO:{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionGoOnline();
                }else {
                    showProgressDialog(false);
                }
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkPermissionGoOnline() {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            mTryingOnline = true;
            //LocationUtils.checkIfLocationEnabled(getActivity(), this, this);
            if(LocationUtils.isLocationEnabled(getContext())){
                onSuccess(null);
            }else {
                onFailure(new Exception("null"));
            }
        }else {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_GEO);
        }
    }
    private void retrieveLocation() {
        if(NetworkUtils.isNetworkAvailable(getContext())) {
            // LocationUtils.checkIfLocationEnabled(getActivity(), this, this);
            if(LocationUtils.isLocationEnabled(getContext())){
                onSuccess(null);
            }else {
                onFailure(new Exception("null"));
            }
        }else{
            //No internet message
            DialogUtilities.showNoNetworkDialog(getActivity());
        }
    }
    private void onSuccess(LocationSettingsResponse locationSettingsResponse) {
        if(!mTryingOnline) {

            try {

                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void onFailure(@NonNull Exception e) {
        if(e.getMessage().equals("null")){
            showProgressDialog(false);
            DialogUtilities.requestLocationSetting(getActivity(), this);
        }else {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case CommonStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.

                    showProgressDialog(false);
                    DialogUtilities.requestLocationSetting(getActivity(), this);

                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we won't show the dialog.
                    showProgressDialog(false);
                    DialogUtilities.showUnsupportedDialog(getActivity());
                    break;
            }
        }
    }
    private void showProgressDialog(boolean show){
        if(show) {
            mShowedProgressDialog = true;
            DialogUtilities.showProgressDialog(getContext(), false, null,DashboardActivity.mIsVisible);
        }else {
            DialogUtilities.dismissProgressDialog(mShowedProgressDialog);
            mShowedProgressDialog = false;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == LOCATION_SETTINGS){
            if(mTryingOnline){
                showProgressDialog(true);
            }
            mReturningFromSettingsIntent = true;
            Intent viewIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(viewIntent);
        }else {
            //Try again get permission click
            tryGetMazdoorLocation();
        }

    }

    private void tryGetMazdoorLocation() {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            retrieveLocation();
        }else {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }
}
