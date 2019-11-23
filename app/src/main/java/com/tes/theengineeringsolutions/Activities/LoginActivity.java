package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ProgressBar;
import com.tes.theengineeringsolutions.R;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mEmailAdd;
    private TextInputEditText mPass;
    private TextView mRegister;
    private TextView mForgetPass;
    private MaterialButton mLoginBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    private void initializeField() {
        mEmailAdd = findViewById(R.id.activityLogin_et_email);
        mPass = findViewById(R.id.activityLogin_et_password);
        mLoginBtn = findViewById(R.id.activityLogin_mbtn_login);
        mProgressBar = findViewById(R.id.activityLogin_progressBar);
        mRegister = findViewById(R.id.activityLogin_tv_register);
        mForgetPass = findViewById(R.id.activityLogin_tv_forget_pass);
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

        //to register user
        //set onClick to the register Text view
        mRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });

        //forget pass text view on click event
        mForgetPass.setOnClickListener(v -> {

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
            if (mPass.getText().toString().length() < 6){
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
