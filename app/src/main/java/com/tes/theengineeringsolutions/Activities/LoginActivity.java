package com.tes.theengineeringsolutions.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Pair;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.tes.theengineeringsolutions.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EMAIL_PATTERN = "(\\W|^)[\\w.+\\-]*@gmail\\.com(\\W|$)";
    private static final String regexPassword = "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[~`!@#\\$%\\^&\\*\\(\\)\\-_\\+=\\{\\}\\[\\]\\|\\;:\"<>,./\\?]).{8,}";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeField();

        mLoginBtn.setOnClickListener(this);
        mForgetPass.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activityLogin_mbtn_login:
                signInUser();
                break;
            case R.id.activityLogin_tv_forget_pass:
                openForgetPassword();
                break;
            case R.id.activityLogin_tv_register:
                openRegisterActivity();
                break;
        }
    }

    private void signInUser() {
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
                        try {
                            throw  task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException fic) {
                            Toast.makeText(this, "incorrect credentials", Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(2000).setInterpolator(new AccelerateInterpolator());
        return bounds;
    }

    private void openForgetPassword() {
        Intent sharedIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);

        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(mForgetPass, "textTransition");
        pairs[1] = new Pair<View, String>(mEmailAdd, "editTextTransition");
        pairs[2] = new Pair<View, String>(mLoginBtn, "buttonTransition");

        getWindow().setSharedElementEnterTransition(enterTransition());

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
        startActivity(sharedIntent, options.toBundle());
    }

    private void openRegisterActivity() {
        startActivity(new Intent(this, SignupActivity.class));
    }


}
