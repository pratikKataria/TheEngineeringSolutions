package com.tes.theengineeringsolutions.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.tes.theengineeringsolutions.about.About;
import com.tes.theengineeringsolutions.about.Developer;
import com.tes.theengineeringsolutions.admin.StudentsResults;
import com.tes.theengineeringsolutions.auth.LoginActivity;
import com.tes.theengineeringsolutions.Fragments.HomeFragment;
import com.tes.theengineeringsolutions.Fragments.ResultFragment;
import com.tes.theengineeringsolutions.Fragments.TestFragment;
import com.tes.theengineeringsolutions.Models.ConnectivityReceiver;
import com.tes.theengineeringsolutions.Models.Encryption;
import com.tes.theengineeringsolutions.R;
import com.tes.theengineeringsolutions.utils.SnackBarNoSwipe;

import java.util.HashMap;
import java.util.Map;

import static com.tes.theengineeringsolutions.Models.ConnectivityReceiver.isConnected;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String Tag = "MAINACTIVITY";
    private static String TAG = "MAINACTIVITY";
    private MaterialButton mMaterialButton;
    private DrawerLayout mDrawerLayout; // layout to implement side nav bar
    private NavigationView mNavigationView; // side var bar
    private ActionBarDrawerToggle actionBarDrawerToggle;//  actionbar
    private BottomNavigationView bottomNavigationView;//bottom navigation bar
    private Toolbar mToolbar;// top toolbar in activity


    private void initializeFields() {

        //find the toolbar view inside the activity layout
        mToolbar = findViewById(R.id.activityMain_toolbar);
        //find the drawer view inside the activity layout
        mDrawerLayout = findViewById(R.id.activityMain_drawer_layout);
        //find the drawer view inside the activity layout
        mNavigationView = findViewById(R.id.activityMain_navigation_view);
        //find the view inside activity
        bottomNavigationView = findViewById(R.id.activityMain_bottom_navigation_view);

    }

    //onStart check for  firebase Auth instance
    // if user is logged out then send user to login
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mFirebaseUser = FirebaseAuth.getInstance();
        if (mFirebaseUser.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkConnection();
        initializeFields();

        // sets the Toolbar to act as the ActionBar for this Fragment window
        //Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(mToolbar);

        //Putting ToolBar into ActionBar
        //then used to set icon in the toolbar
        final ActionBar actionBar = getSupportActionBar();

        //Display icon in the toolbar
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, //host activity
                mDrawerLayout,/*Drawer layout object*/
                mToolbar,/*top tool bar icon animation*/
                R.string.Open,/*open drawer description fo accessibility*/
                R.string.Close/*close drawer description for accessibility*/);

        // Setup toggle to display hamburger icon with nice animation
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_yellow);
        //user to animate the hamburger icon
        actionBarDrawerToggle.syncState();

        //Tie Drawer events to the ActionBarToggle
//        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        //set Home fragment by default
        getSupportFragmentManager().beginTransaction().replace(R.id.activityMain_frame_layout, new HomeFragment()).commit();

        //on click event on bottom navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            //fragment
            Fragment fragment;
            //get id and match to the appropriate menu item in @menu/bottom_navigation_view
            //load fragment into frame layout
            //return true if fragment is clicked
            switch (menuItem.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.test:
                    fragment = new TestFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.result:
                    fragment = new ResultFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        });

        mNavigationView.getMenu().getItem(0).setChecked(true);

        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(this, MainActivity.class));
                    break;
                case R.id.side_nav_developer:
                    startActivity(new Intent(this, Developer.class));
                    break;
                case R.id.side_nav_about:
                    startActivity(new Intent(this, About.class));
                    break;
                case R.id.side_nav_logout:
                    logoutUser();
                    break;
                case R.id.side_nav_result:
                    startActivity(new Intent(this, StudentsResults.class));
                    break;
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }


    private void loadFragment(Fragment fragment) {
        // get fragment manger
        FragmentManager fragmentManager = getSupportFragmentManager();
        //replace current fragment to on click event
        fragmentManager.beginTransaction().replace(R.id.activityMain_frame_layout, fragment).addToBackStack(null).commit();
    }

    private void logoutUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        // register connection status listener
        mNavigationView.getMenu().getItem(0).setChecked(true);
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
    }

    private void checkConnection() {
        boolean isConnected = isConnected();
        showSnack(isConnected);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        Snackbar snackbar;
        View parentLayout = findViewById(R.id.viewSnack);
        int color;
        if (isConnected) {
            message = "Good ! Connected to Internet";
            snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", v ->
                    checkConnection()
            );
            color = Color.RED;
        }

        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.setBehavior(new SnackBarNoSwipe());
        snackbar.show();
    }

}
