package com.tes.theengineeringsolutions.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.tes.theengineeringsolutions.R;

public class ActivityLogin extends AppCompatActivity {

    private TextInputEditText mEmailAdd;
    private TextInputEditText mPass;
    private MaterialButton mLoginBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;

    private void initializeField() {
        mEmailAdd = findViewById(R.id.activityLogin_et_email);
        mPass = findViewById(R.id.activityLogin_et_password);
        mLoginBtn = findViewById(R.id.activityLogin_mbtn_login);
        mProgressBar = findViewById(R.id.activityLogin_progressBar);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeField();

        if (mFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(ActivityLogin.this, SignupActivity.class));
            finish();
        }

        mLoginBtn.setOnClickListener(v -> {
            if (mEmailAdd.getText().toString().isEmpty()) {
                mEmailAdd.setError("field required");
                mEmailAdd.requestFocus();
                return;
            }
            if (mPass.getText().toString().isEmpty()) {
                mPass.setError("pass should not be empty");
                mPass.requestFocus();
                return;
            }

            mProgressBar.setVisibility(View.VISIBLE);
            String email = mEmailAdd.getText().toString();
            String password = mPass.getText().toString();
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(ActivityLogin.this, task -> {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        mProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            startActivity(new Intent(ActivityLogin.this, SignupActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
