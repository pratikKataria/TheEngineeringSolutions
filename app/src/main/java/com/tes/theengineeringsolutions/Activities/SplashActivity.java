package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.ProgressBar;

import com.tes.theengineeringsolutions.R;

/*Created by pratik__katariya on 23/11/2019
* */

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    int i =0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_layout);

        progressBar = findViewById(R.id.splashActivity_progress_bar);

//        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, ActivityLogin.class));
            finish();
//        }, 3000);
    }
}
