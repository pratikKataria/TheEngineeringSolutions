package com.tes.theengineeringsolutions.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Pair;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.tes.theengineeringsolutions.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private TextInputEditText mEmailAdd;
    private TextInputEditText mPass;
    private TextView mRegister;
    private TextView mForgetPass;
    private MaterialButton mLoginBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;

    private void initializeField() {
        mEmailAdd = findViewById(R.id.activityLogin_et_email);
        mLoginBtn = findViewById(R.id.activityLogin_mbtn_login);
        mForgetPass = findViewById(R.id.activityLogin_tv_forget_pass);
        mPass = findViewById(R.id.activityLogin_et_password);
        mProgressBar = findViewById(R.id.activityLogin_progressBar);
        mRegister = findViewById(R.id.activityLogin_tv_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    //check correct format of email address
    private boolean validateEmail(String email) {
        // use regular expression for pattern matcher to match
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        //returns true if it match in correct format
        return matcher.matches();
    }

    private Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(2000).setInterpolator(new DecelerateInterpolator());
        return bounds;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeField();

        //to register user
        //set onClick to the register Text view
        mRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });

        //forget pass text view on click event
        mForgetPass.setOnClickListener(v -> {
            Intent sharedIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);

            Pair[] pairs = new Pair[3];
            pairs[0] = new Pair<View, String>(mForgetPass, "textTransition");
            pairs[1] = new Pair<View, String>(mEmailAdd, "editTextTransition");
            pairs[2] = new Pair<View, String>(mLoginBtn, "buttonTransition");

            getWindow().setSharedElementEnterTransition(enterTransition());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(sharedIntent, options.toBundle());
        });

        //on login button clicked
        mLoginBtn.setOnClickListener(v -> {

            //if edit text is empty return
            if (mEmailAdd.getText().toString().isEmpty()) {
                mEmailAdd.setError("field required");
                mEmailAdd.requestFocus();
                return;
            }

            //if email address is not in format
            if (!validateEmail(mEmailAdd.getText().toString())) {
                mEmailAdd.setError("check your email address");
                mEmailAdd.requestFocus();
                return;
            }

            //if password is empty return
            if (mPass.getText().toString().isEmpty()) {
                mPass.setError("pass should not be empty");
                mPass.requestFocus();
                return;
            }

            // password length is greater then 6
            if (mPass.getText().toString().length() < 6) {
                mPass.setError("greater then 6");
                mPass.requestFocus();
                return;
            }

            //show progress bar
            mProgressBar.setVisibility(View.VISIBLE);

            //get the email add parse into string
            String email = mEmailAdd.getText().toString();

            //get password and parse to string
            String password = mPass.getText().toString();

            //firebaseauth is firebase instance by using sign in with email and password
            //user will sign in
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
