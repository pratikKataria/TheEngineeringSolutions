package com.tes.theengineeringsolutions.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tes.theengineeringsolutions.R;

public class SignupActivity extends AppCompatActivity {

    private MaterialButton materialButton;
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mCnfPassword;
    private MaterialButton mSignupBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;

    private void initializeFields() {
        mEmail      = findViewById(R.id.signupActivity_et_email);
        mUsername   = findViewById(R.id.signupActivity_et_username);
        mPassword   = findViewById(R.id.signupActivity_et_password);
        mCnfPassword = findViewById(R.id.signupActivity_et_cnf_password);
        mSignupBtn   = findViewById(R.id.signupActivity_btn_signup);
        mProgressBar = findViewById(R.id.signupActivity_progress_bar);
        materialButton = findViewById(R.id.logout_test);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeFields();

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

            if (!mPassword.getText().toString().equals(mCnfPassword.getText().toString())) {
                Toast.makeText(this, "password does not match", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(this, "user signup succesfully", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                            if (mFirebaseAuth != null) {

                            }
                        } else Toast.makeText(this, "signup error", Toast.LENGTH_SHORT).show();
                    });

        });

        materialButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}
