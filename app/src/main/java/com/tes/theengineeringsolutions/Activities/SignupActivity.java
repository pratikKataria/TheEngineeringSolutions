package com.tes.theengineeringsolutions.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.tes.theengineeringsolutions.R;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mCnfPassword;
    private TextView mLogin;
    private MaterialButton mSignupBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;

    private void initializeFields() {
        mEmail = findViewById(R.id.signupActivity_et_email);
        mUsername = findViewById(R.id.signupActivity_et_username);
        mPassword = findViewById(R.id.signupActivity_et_password);
        mCnfPassword = findViewById(R.id.signupActivity_et_cnf_password);
        mSignupBtn = findViewById(R.id.signupActivity_btn_signup);
        mProgressBar = findViewById(R.id.signupActivity_progress_bar);
        mLogin = findViewById(R.id.activitySignup_tv_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeFields();

        mLogin.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));

        mSignupBtn.setOnClickListener(v -> {
            if (mUsername.getText().toString().isEmpty()) {
                mUsername.setError("should not be empty");
                mUsername.requestFocus();
                return;
            }

            if (mEmail.getText().toString().isEmpty()) {
                mEmail.setError("should not be empty");
                mEmail.requestFocus();
                return;
            }

            if (mPassword.getText().toString().isEmpty()) {
                mPassword.setError("should not be empty");
                mPassword.requestFocus();
                return;
            }

            if (mCnfPassword.getText().toString().isEmpty()) {
                mCnfPassword.setError("should not be empty");
                mCnfPassword.requestFocus();
                return;
            }

            if (mCnfPassword.length() < 6) {
                mCnfPassword.setError("greater then 6");
                mCnfPassword.requestFocus();
                return;
            }

            if (!mPassword.getText().toString().equals(mCnfPassword.getText().toString())) {
                Toast.makeText(this, "password does not match", Toast.LENGTH_SHORT).show();
                mPassword.requestFocus();
                return;
            }

            if (mPassword.length() < 6) {
                mPassword.setError("greater then 6");
                mPassword.requestFocus();
                return;
            }

            String username = mUsername.getText().toString();
            String password = mPassword.getText().toString();
            String email = mEmail.getText().toString();

            mProgressBar.setVisibility(View.VISIBLE);
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "SINGED UP SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    mProgressBar.setVisibility(View.GONE);
                                    finish();
                                } else Toast.makeText(this, "LOGIN ERROR", Toast.LENGTH_SHORT).show();
                            });
                        } else Toast.makeText(this, "SINGED IN ERROR", Toast.LENGTH_SHORT).show();
                    });

        });
    }
}
