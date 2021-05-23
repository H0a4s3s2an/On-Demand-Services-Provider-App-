package com.example.sample.providerapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sample.providerapp.BottomFragemnts.EarningFragment;
import com.example.sample.providerapp.BottomFragemnts.HomeFragment;
import com.example.sample.providerapp.BottomFragemnts.JobFragment;
import com.example.sample.providerapp.BottomFragemnts.SettingFragment;
import com.example.sample.providerapp.Helper.SharedHelper;
import com.example.sample.providerapp.Model.MazdoorAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {
    TextView username;
    ImageView profile;
    View navHeader;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private MazdoorAccount mazdoor;
    public static boolean mIsVisible;
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    private static final String TAG_HOME = "home";
    private static final String TAG_EARNING = "earning";
    private static final String TAG_SETTING = "setting";
    private static final String TAG_JOB = "job";
    public static FragmentManager fragmentManager;
    boolean push = false;
    private boolean shouldLoadHomeFragOnBackPress = true;
    Fragment fragment;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mIsVisible = false;
//        username.setText(getIntent().getStringExtra("name"));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);
        username=(TextView)header.findViewById(R.id.usernameTxt);
        profile=(ImageView)header.findViewById(R.id.profile);
        username.setText(SharedHelper.getKey(DashboardActivity.this,"fname")+" "+SharedHelper.getKey(DashboardActivity.this,"lname"));
        if (!SharedHelper.getKey(this, "photo").equalsIgnoreCase("")
                && !SharedHelper.getKey(this, "photo").equalsIgnoreCase(null)
                && SharedHelper.getKey(this, "photo") != null)
        {
            Picasso.get()
                    .load(SharedHelper.getKey(this, "photo"))
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile);
        }
        else
        {
            Picasso.get()
                    .load(R.drawable.ic_dummy_user)
                    .placeholder(R.drawable.ic_dummy_user)
                    .error(R.drawable.ic_dummy_user)
                    .into(profile);
        }
//        header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadMethod();
//            }
//        });
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedScreen(R.id.homee);
        setUpFirebaseAuthListening();
    }

    private void setUpFirebaseAuthListening() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Log.d("Auth Listener", "DASHBOARD- onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user == null){
                    //Cleanup other resources as well
                    Log.d("DASHBOARD- Signed", "Out");
                    mazdoor = null;
                    Intent loginActivityIntent = new Intent(DashboardActivity.this, FlatActivity.class);
                    startActivity(loginActivityIntent);
                    finish();
                }
            }
        };
    }

    private void loadMethod() {
        startActivity(new Intent(DashboardActivity.this,EditProfileActivity.class));
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.homee:
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                fragment = new HomeFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("push", push);
                fragment.setArguments(bundle);
                break;
            case R.id.pro:
                startActivity(new Intent(this,ProfileActivity.class));
                break;
            case R.id.earn:
                navItemIndex = 2;
                CURRENT_TAG = TAG_EARNING;
                fragment = new EarningFragment();
                break;
            case R.id.jobee:
                navItemIndex = 3;
                CURRENT_TAG = TAG_JOB;
                fragment=new JobFragment();
                break;
            case R.id.help:
                navItemIndex = 4;
                CURRENT_TAG = TAG_SETTING;
                fragment=new SettingFragment();
                break;
//            case R.id.chit:
//                fragment = new ChatFragment();
//                break;

        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
//            ft.addToBackStack(fragment.getTag());
            ft.commit();
        }
        else
        {
            fragment = new HomeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment).addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
//        if (shouldLoadHomeFragOnBackPress) {
//            // checking if user is on other navigation menu
//            // rather than home
//            if (navItemIndex != 0) {
//                navItemIndex = 0;
//                CURRENT_TAG = TAG_HOME;
//                fragment = new HomeFragment();
//                GoToFragment();
//                return;
//            } else {
//                System.exit(0);
//            }
//        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void GoToFragment() {
        DashboardActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawers();
                FragmentManager manager = getSupportFragmentManager();
                @SuppressLint("CommitTransaction")
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setTitle(item.getTitle());
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
