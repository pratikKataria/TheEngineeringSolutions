package com.tes.theengineeringsolutions.activities.about;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tes.theengineeringsolutions.R;

public class About extends AppCompatActivity {

    private ImageButton expandBtn;
    private ImageButton backbtn;

    private TextView textViewBio;
    private TextView textViewAddrss;
    private TextView textViewContact;
    private boolean expanded = false;

    private void init_fields() {
        expandBtn = findViewById(R.id.expandCardBtn);
        backbtn = findViewById(R.id.backBtn);
        textViewBio = findViewById(R.id.textView_Bio);
        textViewAddrss = findViewById(R.id.textViewAddress);
        textViewContact = findViewById(R.id.textView_contact);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init_fields();

        expandBtn.setOnClickListener(v -> {
            if (!expanded) show();
            else collapse();
        });

        backbtn.setOnClickListener(v-> onBackPressed());
    }


    private void show() {
        textViewBio.setVisibility(View.VISIBLE);
        textViewAddrss.setVisibility(View.VISIBLE);
        textViewContact.setVisibility(View.VISIBLE);
        expandBtn.setImageDrawable(getDrawable(R.drawable.ic_collapse_white_24dp));
        expanded = true;
    }

    private void collapse() {
        textViewBio.setVisibility(View.GONE);
        textViewAddrss.setVisibility(View.GONE);
        textViewContact.setVisibility(View.GONE);
        expandBtn.setImageDrawable(getDrawable(R.drawable.ic_expand_more_black_24dp));
        expanded = false;
    }
}
