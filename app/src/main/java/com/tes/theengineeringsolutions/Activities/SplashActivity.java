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
//        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
//        startActivity(new Intent(this, QuizActivity.class));
            finish();
//        }, 4500);
    }
}
