package com.tes.theengineeringsolutions.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.tes.theengineeringsolutions.R;

public class SignupActivity extends AppCompatActivity {

    private MaterialButton materialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        materialButton = findViewById(R.id.logout_test);

        materialButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }
}
