package com.example.sample.providerapp.FCM;

import android.util.Log;

import com.example.sample.providerapp.Model.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
//    Utilities utils = new Utilities();
      DatabaseReference firebaseDatabase;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        updateTokenToServer(refreshedToken);
//        SharedHelper.putKey(getApplicationContext(),"device_token",""+refreshedToken);
//        Log.d(TAG, "onTokenRefresh: ");
//        utils.print(TAG, "onTokenRefresh" + refreshedToken);
    }

    private void updateTokenToServer(String refreshedToken) {
         firebaseDatabase=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(refreshedToken);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            firebaseDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
        }
    }
}
