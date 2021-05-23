package com.example.sample.providerapp.BottomFragemnts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sample.providerapp.FlatActivity;
import com.example.sample.providerapp.R;
import com.example.sample.providerapp.utilities.DialogUtilities;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by Hassan Javaid on 9/13/2018.
 */

public class SettingFragment extends Fragment implements DialogInterface.OnClickListener {
    Button sign_out;
    private static final int SIGN_OUT = 100;
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_setting,container,false);
        sign_out=(Button)rootView.findViewById(R.id.sign_out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        return rootView;
    }

    private void signOut() {

        DialogUtilities.showConfirmationDialog(getActivity(), this,"Sign out", SIGN_OUT);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        AuthUI.getInstance().signOut(getActivity())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getActivity(), FlatActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Sign Out Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
