package com.tes.theengineeringsolutions.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Handler;
import android.widget.ProgressBar;

import com.tes.theengineeringsolutions.R;

/*Created by pratik__katariya on 23/11/2019
* */

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_layout);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 2500);
    }

//    private void isDocPresent() {
//        String fireUID = FirebaseAuth.getInstance().getUid();
//        Log.d(TAG, fireUID+"fireuid");
//        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(fireUID);
//        documentReference.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Log.d(TAG, fireUID+"fireuid");
//                showAlertDialog();
//                Toast.makeText(MainActivity.this, "document varified", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d(TAG, fireUID+"fireuid");
////                showAlertDialog();
//            }
//        });
//    }

//    void showAlertDialog() {
//        View alertLayout = getLayoutInflater().inflate(R.layout.alert_dialog_document_varification, null);
//        final EditText userNameEditText = alertLayout.findViewById(R.id.username);
//        final EditText email = alertLayout.findViewById(R.id.email);
//        final EditText mPassEditText = alertLayout.findViewById(R.id.password);
//        final MaterialButton continueBtn = alertLayout.findViewById(R.id.alert_dialog_mb_continue);
//        final MaterialButton cancelBtn = alertLayout.findViewById(R.id.alert_dialog_mb_cancel);
//        final ProgressBar progressBar = alertLayout.findViewById(R.id.progressbar);
//
//
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setView(alertLayout);
//        alert.setCancelable(false);
//
//        AlertDialog dialog = alert.create();
//
//
//        continueBtn.setOnClickListener(v -> {
//            Toast.makeText(this, "continue clicked", Toast.LENGTH_SHORT).show();
//            progressBar.setVisibility(View.VISIBLE);
//            if (userNameEditText.getText().toString().isEmpty()) {
//                userNameEditText.setError("should not be empty");
//                userNameEditText.requestFocus();
//                return;
//            }
//            if (email.getText().toString().isEmpty()) {
//                email.setError("should not be empty");
//                email.requestFocus();
//                return;
//            }
//            if (mPassEditText.getText().toString().isEmpty()) {
//                mPassEditText.setError("should not be empty");
//                mPassEditText.requestFocus();
//                return;
//            }
//            if (mPassEditText.getText().toString().length() > 6) {
//                mPassEditText.setError("should not be less then 6");
//                mPassEditText.requestFocus();
//                return;
//            }
//
//            String emailString = email.getText().toString();
//            String passString = mPassEditText.getText().toString();
//            String usernameString = userNameEditText.getText().toString();
//            String currentUid = FirebaseAuth.getInstance().getUid();
//            String fireBaseEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
//            Log.e(TAG, "equals " + currentUid + " " + fireBaseEmail);
//            if (emailString.equals(fireBaseEmail)) {
//                if (FirebaseAuth.getInstance().getUid() != null) {
//                    Map<String, String> userIDS = new HashMap<>();
//                    userIDS.put(emailString, currentUid);
//                    Map<String, Object> userDocuments = new HashMap<>();
//                    userDocuments.put("user_id", currentUid);
//                    userDocuments.put("user_name", usernameString);
//                    userDocuments.put("email_address", emailString);
//                    userDocuments.put("password", passString);
//                    userDocuments.put("Branch", "nd");
//                    userDocuments.put("user_address", "nd");
//                    userDocuments.put("gender", "nd");
//                    Log.e(TAG, "document Reference firebase email  " +  FirebaseAuth.getInstance().getCurrentUser().getEmail());
//                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User").document(currentUid);
//                    documentReference.set(userDocuments).addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful()) {
//                            Log.e(TAG, "document Reference");
//                            FirebaseFirestore.getInstance().collection("Admin").document("UIDS").set(userIDS, SetOptions.merge()).addOnCompleteListener(task2 -> {
//                                if (task2.isSuccessful()) {
//                                    progressBar.setVisibility(View.GONE);
//                                    Toast.makeText(this, "Admin permission granted", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    progressBar.setVisibility(View.GONE);
//                                    Toast.makeText(this, "fail to get Admin permision", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(MainActivity.this, "Document uploaded sucessfully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(MainActivity.this, "fail to upload document", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        });
//        cancelBtn.setOnClickListener(v -> dialog.dismiss());
//        dialog.show();
//    }

    }
