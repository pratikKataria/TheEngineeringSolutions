package com.tes.theengineeringsolutions.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_message);

        firebaseFirestore = FirebaseFirestore.getInstance();

        EditText editTextHeading = findViewById(R.id.activity_post_message_et_heading);
        EditText editTextDescription = findViewById(R.id.activity_post_message_et_description);
        MaterialButton materialButton = findViewById(R.id.activity_post_message_mb_post);

        materialButton.setOnClickListener(v -> {
            if (editTextHeading.getText().toString().isEmpty()) {
                editTextHeading.setError("should not be empty");
                editTextHeading.requestFocus();
                return;
            }

            if (editTextDescription.getText().toString().isEmpty()) {
                editTextDescription.setError("should not be empty");
                editTextDescription.requestFocus();
                return;
            }

            String title = editTextHeading.getText().toString();
            String body = editTextDescription.getText().toString();
            Date date = new Date();


            Map<String, Object> postObject = new HashMap<>();
            postObject.put("heading", title);
            postObject.put("description", body);
            postObject.put("Date", date);

            DocumentReference documentReference = firebaseFirestore.collection("InboxPost").document(getSaltString());
            documentReference.set(postObject).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(PostMessageActivity.this, "post uplaoded successfully", Toast.LENGTH_SHORT).show();
                } else {
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
