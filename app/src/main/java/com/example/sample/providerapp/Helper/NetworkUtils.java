package com.example.sample.providerapp.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.example.sample.providerapp.R;
import com.example.sample.providerapp.SessionActivity;

/**
 * Created by Hassan Javaid on 4/18/2018.
 */

public class NetworkUtils {
    private Context context;

    public NetworkUtils(Context context) {
        this.context = context;
    }
    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static void showNoNetworkDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.no_network_title));
        builder.setMessage(activity.getString(R.string.no_network_message));

        builder.setPositiveButton(activity.getString(R.string.okay_action), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
