package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.tes.theengineeringsolutions.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private MaterialButton mSendBtn;
    private EditText mEmail;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;

    private void initializeFields() {
        mEmail = findViewById(R.id.activityLogin_et_email);
        mSendBtn = findViewById(R.id.activityLogin_mbtn_login);
        mProgressBar = findViewById(R.id.forgetPassActivity_progress_bar);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_acitivity);

        initializeFields();

        mSendBtn.setOnClickListener(v -> {
            if (mEmail.getText().toString().isEmpty()) {
                mEmail.setError("should not be empty");
                mEmail.requestFocus();
                return;
            }
            sendPasswordResetLink(mEmail.getText().toString());
        });
    }

    private void sendPasswordResetLink(String email) {
            mProgressBar.setVisibility(View.VISIBLE);
            mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    onBackPressed();
                }
            });
    }
}
