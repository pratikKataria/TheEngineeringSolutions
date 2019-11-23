package com.tes.theengineeringsolutions.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tes.theengineeringsolutions.R;

public class MainActivity extends AppCompatActivity {

    private MaterialButton mMaterialButton;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mFirebaseUser= FirebaseAuth.getInstance();
        if (mFirebaseUser.getCurrentUser() == null) {
            startActivity( new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMaterialButton = findViewById(R.id.mainActivity_btn_logout);
        mMaterialButton.setOnClickListener(v -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }
}
