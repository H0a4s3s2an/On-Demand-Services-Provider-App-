package com.example.sample.providerapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.MazdoorAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Driver;

public class ProfileActivity extends AppCompatActivity{
    Button edit_profile_proceed;
    TextView text_fname;
    TextView text_lname;
    TextView mobile_no;
    TextView email;
    private static String TAG="Profile";
    public Context context=ProfileActivity.this;
    public Activity activity=ProfileActivity.this;
    CustomDialog customDialog;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ImageView img_profile;
    String fname,lname,mobile,mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        text_fname=(TextView)findViewById(R.id.text_fname);
        text_lname=(TextView)findViewById(R.id.text_lname);
        mobile_no=(TextView)findViewById(R.id.mobile_no);
        email=(TextView)findViewById(R.id.email);
        img_profile=(ImageView)findViewById(R.id.img_profile);
        edit_profile_proceed=(Button)findViewById(R.id.visit_profile_proceed);
        edit_profile_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("fname",fname);
                intent.putExtra("lname",lname);
                intent.putExtra("email",mail);
                intent.putExtra("phone",mobile);
                startActivity(intent);
            }
        });
        customDialog=new CustomDialog(context);
        databaseReference= FirebaseDatabase.getInstance().getReference("Driver");
        firebaseAuth=FirebaseAuth.getInstance();
        if (!SharedHelper.getKey(context, "photo").equalsIgnoreCase("")
                && !SharedHelper.getKey(context, "photo").equalsIgnoreCase(null)
                && SharedHelper.getKey(context, "photo") != null)
        {
            Picasso.get()
                    .load(SharedHelper.getKey(context, "photo"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(img_profile);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(img_profile);
        }
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    fname = dataSnapshot1.child("firstname").getValue().toString();
                    lname = dataSnapshot1.child("lastname").getValue().toString();
                    mobile = dataSnapshot1.child("number").getValue().toString();
                    mail = dataSnapshot1.child("email").getValue().toString();
                    text_fname.setText(dataSnapshot1.child("firstname").getValue().toString());
                    text_lname.setText(dataSnapshot1.child("lastname").getValue().toString());
                    mobile_no.setText(dataSnapshot1.child("number").getValue().toString());
                    email.setText(dataSnapshot1.child("email").getValue().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
