package com.example.sample.providerapp.utilities;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Hassan Javaid on 8/18/2018.
 */

public class LocationUtils {
    private static final int MAX_REFRESH_INTERVAL = 10; //Seconds
    private static final int FASTEST_REFRESH_INTERVAL = 3;  //Seconds

    private static LocationRequest mLocationRequest = createLocationRequest();

    private static LocationRequest createLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(MAX_REFRESH_INTERVAL * 1000);
        locationRequest.setFastestInterval(FASTEST_REFRESH_INTERVAL * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setSmallestDisplacement(DISPLACEMENT);

        return locationRequest;
    }

    private static LocationSettingsRequest createLocationSettingRequest() {
        return new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest).build();
    }

    public static void checkIfLocationEnabled(Activity activity, OnSuccessListener<LocationSettingsResponse> onSuccessListener,
                                              OnFailureListener onFailureListener){
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(createLocationSettingRequest());

        task.addOnSuccessListener(activity, onSuccessListener);

        task.addOnFailureListener(activity,onFailureListener);
    }

    public static LocationRequest getLocationRequest() {
        if(mLocationRequest==null){
            mLocationRequest = createLocationRequest();
        }

        return mLocationRequest;
    }

    public static boolean isLocationEnabled(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

}
