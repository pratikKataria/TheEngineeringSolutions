package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PostMessageActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);

        firebaseFirestore = FirebaseFirestore.getInstance();

        EditText editTextHeading = findViewById(R.id.activity_post_message_et_heading);
        EditText editTextDescription = findViewById(R.id.activity_post_message_et_description);
        MaterialButton materialButton = findViewById(R.id.activity_post_message_mb_post);
        ProgressBar progressBar = findViewById(R.id.progressbar);


        materialButton.setOnClickListener(v -> {
            if (editTextHeading.getText().toString().isEmpty()) {
                editTextHeading.setError("should not be empty");
                editTextHeading.requestFocus();
                return;
            }


            String title = editTextHeading.getText().toString();
            String body = editTextDescription.getText().toString();
            Date date = new Date();

            postId = getSaltString();

            Map<String, Object> postObject = new HashMap<>();
            postObject.put("postId", postId);
            postObject.put("heading", title);
            postObject.put("description", body);
            postObject.put("created", date);
            DocumentReference documentReference = firebaseFirestore.collection("InboxPost").document(postId);
            documentReference.set(postObject).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostMessageActivity.this, "post uplaoded successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PostMessageActivity.this, "failed to upload", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
