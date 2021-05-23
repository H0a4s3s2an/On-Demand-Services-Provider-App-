package com.example.sample.providerapp.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.sample.providerapp.R;

import static com.example.sample.providerapp.BottomFragemnts.HomeFragment.LOCATION_SETTINGS;

/**
 * Created by Hassan Javaid on 9/9/2018.
 */

public class DialogUtilities {
    private static ProgressDialog mProgressDialog;
    public static void requestLocationSetting(final Activity activity, final DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Location setting are disabled");
        builder.setMessage("In order to go online enable location");

        builder.setPositiveButton("LOCATION SETTING", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onClickListener.onClick(dialog, LOCATION_SETTINGS);
            }});

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}});
        AlertDialog dialog = builder.create();
        dialog.show();
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
    public static void showUnsupportedDialog(Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("LOCATION NOT SUPPORTED");
        builder.setMessage("This device in incapable of using location services");

        builder.setPositiveButton(activity.getString(R.string.okay_action), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}});

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void showProgressDialog(Context mContext, boolean cancelable, DialogInterface.OnDismissListener onDismissListener,
                                          boolean contextVisible){
        if(contextVisible) {
            mProgressDialog = new ProgressDialog(mContext);

            if (onDismissListener != null) {
                mProgressDialog.setOnDismissListener(onDismissListener);
            }

            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.customdialog);
            mProgressDialog.setCancelable(cancelable);

        }
    }
    public static void dismissProgressDialog(boolean dialogVisible){
        if(dialogVisible) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }
    public static void showDeniedDialog(Activity activity, DialogInterface.OnClickListener onClickListener,
                                        String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage("Without this Setting you can't go online");

        builder.setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}});

        builder.setNegativeButton("OKAY", onClickListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static void showConfirmationDialog(Activity activity, final DialogInterface.OnClickListener onClickListener,
                                              String title, final int whichId){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage("Are you sure you want to" + " " + title.toLowerCase() + "?");
        builder.setPositiveButton(title.toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onClickListener.onClick(dialog, whichId);
            }});

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
