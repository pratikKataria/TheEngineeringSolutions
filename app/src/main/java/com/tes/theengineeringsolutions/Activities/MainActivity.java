package com.tes.theengineeringsolutions.Activities;

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
import com.tes.theengineeringsolutions.Fragments.HomeFragment;
import com.tes.theengineeringsolutions.Fragments.ResultFragment;
import com.tes.theengineeringsolutions.Fragments.TestFragment;
import com.tes.theengineeringsolutions.Models.ConnectivityReceiver;
import com.tes.theengineeringsolutions.Models.Encryption;
import com.tes.theengineeringsolutions.R;

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
    private SharedPreferences sharedPreferences;
    private Editor editor;
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

        sharedPreferences = getSharedPreferences("DOCUMENT_VERIFICATION", 0);
        editor = sharedPreferences.edit();
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

        Log.e(Tag, "SHARED PREFERENCES NOT WORKING");
        if (!sharedPreferences.getBoolean("isVerified", false)) {
            check_if_document_present();
        }

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
                case R.id.developer:
                    startActivity(new Intent(this, Developer.class));
                    break;
                case R.id.about:
                    startActivity(new Intent(this, About.class));
                    break;
                case R.id.logout:
                    logoutUser();
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
        editor.putBoolean("isVerified", false);
        editor.commit();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void check_if_document_present() {
        String fireUID = FirebaseAuth.getInstance().getUid();
        if (fireUID != null) {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(fireUID);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Map<String, Object> header = documentSnapshot.getData();
                        if (header != null && header.containsKey("user_info")) {
                            editor.putBoolean("isVerified", true);
                            editor.commit();
                            Toast.makeText(this, "Document Verified", Toast.LENGTH_SHORT).show();
                        } else {
                            showAlertDialog();
                        }
                    } else {
                        showAlertDialog();
                    }
                }
            });
        }
    }

    void showAlertDialog() {
        View alertLayout = getLayoutInflater().inflate(R.layout.alert_dialog_document_varification, null);
        final EditText userNameEditText = alertLayout.findViewById(R.id.username);
        final EditText email = alertLayout.findViewById(R.id.email);
        final EditText mPassEditText = alertLayout.findViewById(R.id.password);
        final MaterialButton continueBtn = alertLayout.findViewById(R.id.alert_dialog_mb_continue);
        final ProgressBar progressBar = alertLayout.findViewById(R.id.progressbar);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setCancelable(false);

        AlertDialog dialog = alert.create();


        continueBtn.setOnClickListener(v -> {

            if (userNameEditText.getText().toString().isEmpty()) {
                userNameEditText.setError("should not be empty");
                userNameEditText.requestFocus();
                return;
            }

            if (email.getText().toString().isEmpty()) {
                email.setError("should not be empty");
                email.requestFocus();
                return;
            }
            if (mPassEditText.getText().toString().isEmpty()) {
                mPassEditText.setError("should not be empty");
                mPassEditText.requestFocus();
                return;
            }
            if (mPassEditText.getText().toString().length() > 6) {
                mPassEditText.setError("should not be less then 6");
                mPassEditText.requestFocus();
                return;
            }

            String emailString = email.getText().toString();
            String passString = mPassEditText.getText().toString();
            String usernameString = userNameEditText.getText().toString();
            String currentUid = FirebaseAuth.getInstance().getUid();
            String fireBaseEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            progressBar.setVisibility(View.VISIBLE);

            String pass = sharedPreferences.getString("5f4dcc3b5aa765d61d8327deb882cf99", "");
            if (emailString.equals(fireBaseEmail)) {
                if (passString.equals(new Encryption().decrypt(pass, "5f4dcc3b5aa765d61d8327deb882cf99"))) {
                    if (FirebaseAuth.getInstance().getUid() != null) {
                        Map<String, String> userIDS = new HashMap<>();
                        userIDS.put(emailString, currentUid);
                        Map<String, String> userDocuments = new HashMap<>();
                        userDocuments.put("user_id", currentUid);
                        userDocuments.put("user_name", usernameString);
                        userDocuments.put("email_address", emailString);
                        userDocuments.put("password", passString);
                        userDocuments.put("Branch", "nd");
                        userDocuments.put("user_address", "nd");
                        userDocuments.put("gender", "nd");
                        Log.e(TAG, "document Reference firebase email  " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(currentUid);
                        Map<String, Object> header = new HashMap<>();
                        header.put("user_info",userDocuments);
                        documentReference.set(header, SetOptions.merge()).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.e(TAG, "document Reference");
                                FirebaseFirestore.getInstance().collection("Admin").document("UIDS").set(userIDS, SetOptions.merge()).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        dialog.dismiss();
                                        progressBar.setVisibility(View.GONE);
                                        editor.putBoolean("isVerified", true);
                                        editor.commit();
                                        Toast.makeText(this, "Admin permission granted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(this, "fail to get Admin permision", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Document uploaded sucessfully", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "fail to upload document", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "wrong password", Toast.LENGTH_SHORT).show();
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "wrong email", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    class NoSwipeBehavior extends BaseTransientBottomBar.Behavior {
        @Override
        public boolean canSwipeDismissView(View child) {
            return false;
        }
    }
}
