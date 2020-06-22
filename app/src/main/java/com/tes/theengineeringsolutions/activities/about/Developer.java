package com.tes.theengineeringsolutions.activities.about;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tes.theengineeringsolutions.R;

public class Developer extends AppCompatActivity {

    private ImageButton expandBtn;
    private ImageButton backbtn;

    private TextView textViewBio;
    private LinearLayout linearLayoutLinks;
    private boolean expanded = false;

    private void init_fields() {
        expandBtn = findViewById(R.id.expandCardBtn);
        backbtn = findViewById(R.id.backBtn);
        textViewBio = findViewById(R.id.textView_Bio);
        linearLayoutLinks = findViewById(R.id.linearLayoutLinks);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        init_fields();

        expandBtn.setOnClickListener(v -> {
           if (!expanded) show();
           else collapse();
        });

        backbtn.setOnClickListener(v-> onBackPressed());
    }

    private void show() {
        textViewBio.setVisibility(View.VISIBLE);
        linearLayoutLinks.setVisibility(View.VISIBLE);
        expandBtn.setImageDrawable(getDrawable(R.drawable.ic_collapse_white_24dp));
        expanded = true;
    }

    private void collapse() {
        textViewBio.setVisibility(View.GONE);
        linearLayoutLinks.setVisibility(View.GONE);
        expandBtn.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_24dp));
        expanded = false;
    }
}
