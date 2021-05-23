package com.example.sample.providerapp.BottomFragemnts;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sample.providerapp.DashboardActivity;
import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Interface.OnLoadingDismissListener;
import com.example.sample.providerapp.Model.Booking;
import com.example.sample.providerapp.R;
import com.example.sample.providerapp.utilities.DialogUtilities;
import com.example.sample.providerapp.utilities.ProgressBarAnimation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

/**
 * Created by Hassan Javaid on 6/8/2018.
 */

public class EarningFragment extends Fragment {
    ProgressBar pb_acceptance;
    private DatabaseReference mDriverDatabase, mDriverStatsReference,paymentReference;
    private Booking mStats;
    TextView tv_acceptance;
    private boolean  mDisplayedError, mStillLoading, mShowedProgressBar;
    private OnLoadingDismissListener mOnLoadingDismissListener;
    ConstraintLayout mErrorContent;
    ConstraintLayout cl_earnings_content;
    private static final int MINIMUM_REVIEW_COUNT = 10;
    TextView tv_rating;
    TextView tv_total_rides;
    TextView tv_cancel_jobs;
    ProgressBar pb_loading_rating;
    private static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.#");
    private static DecimalFormat REAL_FORMAT = new DecimalFormat("0.#");
    TextView tv_trip_earnings;
    CustomDialog customDialog;
    public EarningFragment() {/*Required empty public constructor*/}
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mOnLoadingDismissListener = (OnLoadingDismissListener) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_earning, container, false);
        pb_acceptance=(ProgressBar)rootView.findViewById(R.id.pb_acceptance);
        tv_acceptance=(TextView)rootView.findViewById(R.id.tv_acceptance);
        mErrorContent=(ConstraintLayout)rootView.findViewById(R.id.cl_error);
        cl_earnings_content=(ConstraintLayout)rootView.findViewById(R.id.cl_earnings_content);
        tv_rating=(TextView)rootView.findViewById(R.id.tv_rating);
        tv_total_rides=(TextView)rootView.findViewById(R.id.tv_total_rides);
        tv_cancel_jobs=(TextView)rootView.findViewById(R.id.tv_cancel_jobs);
        pb_loading_rating=(ProgressBar)rootView.findViewById(R.id.pb_loading_rating);
        tv_trip_earnings=(TextView)rootView.findViewById(R.id.tv_trip_earnings);
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDriverDatabase = FirebaseDatabase.getInstance().getReference("Rating");
        mDriverStatsReference = FirebaseDatabase.getInstance().getReference("Booking");
        paymentReference=FirebaseDatabase.getInstance().getReference("Payment");
        loadAllViews();

    }

    private void loadAllViews() {
        mDisplayedError = false;
        mShowedProgressBar = true;
        DialogUtilities.showProgressDialog(getContext(), true, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mOnLoadingDismissListener != null) {
                    mOnLoadingDismissListener.onLoadingDismiss(mStillLoading);
                }
            }
        }, DashboardActivity.mIsVisible);

        loadStatsViews();
        loadDriverRating();
        loadTotalPrice();
        loadStatus();
    }

    private void loadStatus() {
        mDriverStatsReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                       Integer totaljob = dataSnapshot.child("acceptedRequest").getValue(Integer.class);
                       if(totaljob == null)
                       {
                           tv_total_rides.setText("0");
                       }
                       tv_total_rides.setText(String.valueOf(totaljob));
                       Integer rejectjobs = dataSnapshot.child("rejectedRequest").getValue(Integer.class);
                       Integer cancelBooking = dataSnapshot.child("cancelBooking").getValue(Integer.class);
                       if(rejectjobs == null)
                       {
                           rejectjobs = 0;
                       }
                       if(cancelBooking == null)
                       {
                           cancelBooking = 0;
                       }
                       Integer TotalCancelJobs = rejectjobs + cancelBooking;
                       tv_cancel_jobs.setText(String.valueOf(TotalCancelJobs));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadTotalPrice() {

        paymentReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    double price = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        double totalPrice = Double.parseDouble(dataSnapshot1.child("price").getValue(String.class));
                        price += totalPrice;
                    }
                    tv_trip_earnings.setText(REAL_FORMAT.format(price));
                }
                else
                {
                    tv_trip_earnings.setText("0.00");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadDriverRating() {
        mDriverDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    double count = 0;
                    double average = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        double rating = Double.parseDouble(dataSnapshot1.child("rating").getValue().toString());
                        double totalChild = dataSnapshot.getChildrenCount();
                        count = count + rating ;
                        average = count / totalChild ;
                    }
                    tv_rating.setText(REAL_FORMATTER.format(average));
                    if (!mDisplayedError) {
                        displayResults(true);
                        pb_loading_rating.setVisibility(View.INVISIBLE);
                        tv_rating.setVisibility(View.VISIBLE);
                    } else {
                        displayResults(false);
                    }
                }
                else
                {
                    tv_rating.setText("N/A");
                    pb_loading_rating.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                displayResults(false);
            }
        });
    }

    private void loadStatsViews() {
        mDriverStatsReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    mStats = dataSnapshot.getValue(Booking.class);

                    if (mStats != null) {
                        processAcceptanceRate();

                    } else {
                        displayResults(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    displayResults(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                displayResults(false);
            }
        });

    }

    private void processAcceptanceRate() {
        int acceptancePercent = (int)(((double)mStats.getAcceptedRequest() / ((double)mStats.getAcceptedRequest()+(double)mStats.getRejectedRequest())) * 100.0);
        if(acceptancePercent==0)
        {
            tv_acceptance.setText(acceptancePercent);
        }
        String acceptanceRateString = acceptancePercent + "%";
        tv_acceptance.setText(acceptanceRateString);
//        tv_total_rides.setText(mStats.getAcceptedRequest());
        ProgressBarAnimation anim = new ProgressBarAnimation(pb_acceptance, 0, acceptancePercent);
        anim.setDuration(1000);
        pb_acceptance.startAnimation(anim);
    }
    public void displayResults(boolean isSuccess){
        mStillLoading = false;
        showProgressDialog(false);
        if(isSuccess){
            mErrorContent.setVisibility(View.INVISIBLE);
            cl_earnings_content.setVisibility(View.VISIBLE);
        }else {
            mDisplayedError = true;
            mErrorContent.setVisibility(View.VISIBLE);
            cl_earnings_content.setVisibility(View.INVISIBLE);
        }
    }
    private void showProgressDialog(boolean show){
        if(show) {
            mShowedProgressBar = true;
            DialogUtilities.showProgressDialog(getContext(), false, null, DashboardActivity.mIsVisible);
        }else {
            DialogUtilities.dismissProgressDialog(mShowedProgressBar);
            mShowedProgressBar = false;
        }
    }

}