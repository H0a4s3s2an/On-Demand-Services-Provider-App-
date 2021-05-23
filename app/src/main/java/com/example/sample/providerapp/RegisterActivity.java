package com.example.sample.providerapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.sample.providerapp.Helper.CustomDialog;
import com.example.sample.providerapp.Helper.NetworkUtils;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.MazdoorAccount;
import com.example.sample.providerapp.utilities.Utilities;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    public Context context = RegisterActivity.this;
    public Activity activity = RegisterActivity.this;
    String TAG = "RegisterActivity";
    Button registration;
    EditText email, first_name, last_name, mobile_no, password;
    CustomDialog customDialog;
    NetworkUtils helper;
    Boolean isInternet;
    Spinner serviceSpinner;
    private String blockCharacterSet = "~#^|$%&*!()_-*.,@/";
    Utilities utils = new Utilities();
    DatabaseReference databaseReference;
    DatabaseReference spin_category;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    Query query;
    boolean fromActivity=false;
     List<String> name;
    MazdoorAccount mazdoor=new MazdoorAccount();
    String servie_id;
    String fname;
    String lname;
    String mail;
    String phone;
    String photo;
    String serve;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register");
        email = (EditText) findViewById(R.id.email);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        password = (EditText) findViewById(R.id.password);
        registration = (Button) findViewById(R.id.registration);
        helper = new NetworkUtils(context);
        serviceSpinner=(Spinner)findViewById(R.id.service_spinner);
        isInternet = helper.isNetworkAvailable();
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference("Driver");
        spin_category=firebaseDatabase.getInstance().getReference("Category");
        spin_category.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               name = new ArrayList<String>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String name1=dataSnapshot1.child("title").getValue(String.class);
                    name.add(name1);
                }
                ArrayAdapter<String> namesAdapter=new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_item,name);
                namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                serviceSpinner.setAdapter(namesAdapter);
             serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 @Override
                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                      servie_id=parent.getItemAtPosition(position).toString();

                 }

                 @Override
                 public void onNothingSelected(AdapterView<?> parent) {
displayMessage("Select the service");
                 }
             });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
         query=databaseReference.orderByChild("number").equalTo(getIntent().getStringExtra("number"));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    displayMessage("Your Mobile number is already Registered.");
                    registration.setEnabled(false);
                    registration.setBackgroundColor(R.color.button_text_color);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,databaseError.getMessage().toString());
            }
        });
        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(first_name.getText().toString());
                Matcher lastName = ps.matcher(last_name.getText().toString());

                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase("name@example.com")) {
                    displayMessage("Enter a valid Email.");
                } else if (!Utilities.isValidEmail(email.getText().toString())) {
                    displayMessage("Not a valid Email");
                } else if (first_name.getText().toString().equals("") || first_name.getText().toString().equalsIgnoreCase("First Name")) {
                    displayMessage("First name required.");
                } else if (firstName.matches()) {
                    displayMessage("First name do not accept numbers");
                } else if (last_name.getText().toString().equals("") || last_name.getText().toString().equalsIgnoreCase("Last Name")) {
                    displayMessage("Last name required.");
                } else if (lastName.matches()) {
                    displayMessage("Last name do not accept numbers.");
                } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase("******")) {
                    displayMessage("Password could not be empty");
                } else if (password.length() < 6) {
                    displayMessage("password size must be at least 6 numbers.");
                }else if(spin_category==null)
                {
                    displayMessage("Select the service you provided");
                }
                else {
                    if (isInternet) {
                        customDialog=new CustomDialog(context);
                        customDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                customDialog.dismiss();
                                mazdoor.setNumber(getIntent().getStringExtra("number"));
                                mazdoor.setEmail(email.getText().toString());
                                mazdoor.setFirstname(first_name.getText().toString());
                                mazdoor.setLastname(last_name.getText().toString());
                                mazdoor.setRating("0.0");
                                mazdoor.setImg("");
                                mazdoor.setServiceId(servie_id);
                                String user_id = databaseReference.push().getKey();
                                mazdoor.setUid(user_id);
                                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child(user_id).setValue(mazdoor);
                                getProfile();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                displayMessage("something went wrong");
                                customDialog.dismiss();
                            }
                        });
                    } else {
                        displayMessage("something went wrong");
                        customDialog.dismiss();
                    }
                }
            }
        });
    }

    private void getProfile() {
//        databaseReference = FirebaseDatabase.getInstance().getReference("Driver");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                     fname = datas.child("firstname").getValue().toString();
                     lname = datas.child("lastname").getValue().toString();
                     mail = datas.child("email").getValue().toString();
                     serve= datas.child("serviceId").getValue().toString();
                     photo=datas.child("img").getValue().toString();
                     phone = datas.child("number").getValue().toString();
                     id=datas.child("uid").getValue().toString();
                    if(fname.equals("") || lname.equals("") || mail.equals("") || serve.equals("") || phone.equals(""))
                    {
                        startActivity(new Intent(RegisterActivity.this,FlatActivity.class));
                    }
                    else
                    {
                        SharedHelper.putKey(context,"id",id);
                        SharedHelper.putKey(context,"fname",fname);
                        SharedHelper.putKey(context,"lname",lname);
                        SharedHelper.putKey(context,"email",mail);
                        SharedHelper.putKey(context,"service",serve);
                        SharedHelper.putKey(context,"photo",photo);
                        SharedHelper.putKey(context,"phone",phone);
                        goToDashBoardActivity();
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("",databaseError.toString());
            }
        });
    }

    private void goToDashBoardActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(RegisterActivity.this,DashboardActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        RegisterActivity.this.finish();
    }
//
//    private void Signup() {
//
//    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (fromActivity) {
            Intent mainIntent = new Intent(RegisterActivity.this, FlatActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        } else {
            Intent mainIntent = new Intent(RegisterActivity.this, FlatActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            RegisterActivity.this.finish();
        }
    }
}

