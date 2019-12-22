package com.tes.theengineeringsolutions.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import com.tes.theengineeringsolutions.Fragments.HomeFragment;
import com.tes.theengineeringsolutions.Fragments.ResultFragment;
import com.tes.theengineeringsolutions.Fragments.TestFragment;
import com.tes.theengineeringsolutions.Models.ConnectivityReceiver;
import com.tes.theengineeringsolutions.R;

import static com.tes.theengineeringsolutions.Models.ConnectivityReceiver.isConnected;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static String TAG = "MAINACTIVITY";
    private MaterialButton mMaterialButton;
    private DrawerLayout mDrawerLayout; // layout to implement side nav bar
    private NavigationView mNavigationView; // side var bar
    private ActionBarDrawerToggle actionBarDrawerToggle;//  actionbar
    private BottomNavigationView bottomNavigationView;//bottom navigation bar
    private Toolbar mToolbar;// top toolbar in activity
    //Fragments Classes
    private HomeFragment homeFragment;
    private TestFragment testFragment;
    private ResultFragment resultFragment;

    private void initializeFields() {
        //find the toolbar view inside the activity layout
        mToolbar = findViewById(R.id.activityMain_toolbar);
        //find the drawer view inside the activity layout
        mDrawerLayout = findViewById(R.id.activityMain_drawer_layout);
        //find the drawer view inside the activity layout
        mNavigationView = findViewById(R.id.activityMain_navigation_view);
        //find the view inside activity
        bottomNavigationView = findViewById(R.id.activityMain_bottom_navigation_view);

        //home fragment instance
        homeFragment = new HomeFragment();
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

        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
         switch (menuItem.getItemId()) {
             case R.id.home:
                 startActivity(new Intent(this, MainActivity.class));
                 break;
             case R.id.about:
                 startActivity(new Intent(this, Developer.class));
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
        //        mMaterialButton = findViewById(R.id.mainActivity_btn_logout);
//        mMaterialButton.setOnClickListener(v -> {
//            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//            firebaseAuth.signOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        });
    }


    private void checkConnection() {
        boolean isConnected = isConnected();
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
        snackbar.setBehavior(new NoSwipeBehavior());
        snackbar.show();
    }
    class NoSwipeBehavior extends BaseTransientBottomBar.Behavior {
        @Override
        public boolean canSwipeDismissView(View child) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


}
