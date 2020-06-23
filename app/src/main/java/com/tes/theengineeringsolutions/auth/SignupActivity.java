package com.tes.theengineeringsolutions.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.tes.theengineeringsolutions.Models.Encryption;
import com.tes.theengineeringsolutions.R;
import com.tes.theengineeringsolutions.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mCnfPassword;
    private TextView mLogin;
    private MaterialButton mSignupBtn;
    private ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private void initializeFields() {
        mEmail = findViewById(R.id.signupActivity_et_email);
        mUsername = findViewById(R.id.signupActivity_et_username);
        mPassword = findViewById(R.id.signupActivity_et_password);
        mCnfPassword = findViewById(R.id.signupActivity_et_cnf_password);
        mSignupBtn = findViewById(R.id.signupActivity_btn_signup);
        mProgressBar = findViewById(R.id.signupActivity_progress_bar);
        mLogin = findViewById(R.id.activitySignup_tv_login);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeFields();

        mLogin.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
        validateUser();
    }

    private void validateUser() {
        mSignupBtn.setOnClickListener(v -> {

            if (mUsername.getText().toString().isEmpty()) {
                mUsername.setError("should not be empty");
                mUsername.requestFocus();
                return;
            }

            if (mEmail.getText().toString().isEmpty()) {
                mEmail.setError("should not be empty");
                mEmail.requestFocus();
                return;
            }

            if (mPassword.getText().toString().isEmpty()) {
                mPassword.setError("should not be empty");
                mPassword.requestFocus();
                return;
            }

            if (mCnfPassword.getText().toString().isEmpty()) {
                mCnfPassword.setError("should not be empty");
                mCnfPassword.requestFocus();
                return;
            }

            if (mCnfPassword.length() < 6) {
                mCnfPassword.setError("greater then 6");
                mCnfPassword.requestFocus();
                return;
            }

            if (!mPassword.getText().toString().equals(mCnfPassword.getText().toString())) {
                Toast.makeText(this, "password does not match", Toast.LENGTH_SHORT).show();
                mPassword.requestFocus();
                return;
            }

            if (mPassword.length() < 6) {
                mPassword.setError("greater then 6");
                mPassword.requestFocus();
                return;
            }

            String password = mPassword.getText().toString();
            String email = mEmail.getText().toString();

            signupUser(email, password);

        });
    }

    private void signupUser(String email, String password) {
        //make progress bar visible
        mProgressBar.setVisibility(View.VISIBLE);

        //signup using email and password
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "SINGED UP SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                storePass(password);
                                storeUserCredentials(email);
                            } else {
                                Toast.makeText(this, "LOGIN ERROR", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        //FirebaseAuthUserCollisionException: if there already exists an account with the given email address.
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException existEmail) {
                            Toast.makeText(this, "user already exist", Toast.LENGTH_SHORT).show();
                            Toast.makeText(this, "try to login", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        } catch (FirebaseAuthInvalidCredentialsException malformedException) {
                            Toast.makeText(this, "check your email Address", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }//else
                });//createUserWithEmailAndPassword
    }

    public void storeUserCredentials(String email) {
        WriteBatch batch = firebaseFirestore.batch();

        String uidString = mFirebaseAuth.getUid();
        Map<String, String> map = new HashMap<>();
        map.put(email, uidString);
        DocumentReference adminUidsDatabaseReference = firebaseFirestore.collection("Admin").document("UIDS");

        batch.set(adminUidsDatabaseReference, map, SetOptions.merge());

        Map<String, Object> rootMap = new HashMap<>();
        Map<String, Object> userDocuments = new HashMap<>();
        userDocuments.put("user_id", mFirebaseAuth.getUid());
        userDocuments.put("user_name", mUsername.getText().toString());
        userDocuments.put("email_address", mEmail.getText().toString());
        userDocuments.put("password", mPassword.getText().toString());
        userDocuments.put("Branch", "nd");
        userDocuments.put("user_address", "nd");
        userDocuments.put("gender", "nd");
        rootMap.put("user_info", userDocuments);
        DocumentReference userInfoDatabaseReference = firebaseFirestore.collection("User").document(mFirebaseAuth.getUid());

        batch.set(userInfoDatabaseReference, rootMap);
        batch.commit().addOnSuccessListener(success -> {
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
        }).addOnFailureListener(failed -> {
            retry();
        });
    }

    private void retry() {
    }

//    public void setUIDS(String email) {
//        if (mFirebaseAuth.getUid() != null) {
//            String uidString = mFirebaseAuth.getUid();
//            Map<String, String> map = new HashMap<>();
//            map.put(email, uidString);
//            DocumentReference documentReference = firebaseFirestore.collection("Admin").document("UIDS");
//            documentReference.set(map, SetOptions.merge()).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    createUserDocument();
//                    Toast.makeText(SignupActivity.this, "uid SET", Toast.LENGTH_SHORT).show();
//                } else {
//                    mProgressBar.setVisibility(View.GONE);
//                    Toast.makeText(SignupActivity.this, "Unable to set uid", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    public void createUserDocument() {
//        if (mFirebaseAuth.getUid() != null) {
//            Map<String, Object> rootMap = new HashMap<>();
//            Map<String, Object> userDocuments = new HashMap<>();
//            userDocuments.put("user_id", mFirebaseAuth.getUid());
//            userDocuments.put("user_name", mUsername.getText().toString());
//            userDocuments.put("email_address", mEmail.getText().toString());
//            userDocuments.put("password", mPassword.getText().toString());
//            userDocuments.put("Branch", "nd");
//            userDocuments.put("user_address", "nd");
//            userDocuments.put("gender", "nd");
//            rootMap.put("user_info", userDocuments);
//            DocumentReference documentReference = firebaseFirestore.collection("User").document(mFirebaseAuth.getUid());
//            documentReference.set(rootMap).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
//                    finish();
//                    Toast.makeText(SignupActivity.this, "document uploaded successfully", Toast.LENGTH_LONG).show();
//                } else
//                    Toast.makeText(SignupActivity.this, "fail to upload documents", Toast.LENGTH_LONG).show();
//            });
//        }
//    }

    private void storePass(String pass) {
        SharedPreferences sharedPreferences = getSharedPreferences("DOCUMENT_VERIFICATION", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("5f4dcc3b5aa765d61d8327deb882cf99", new Encryption().encrypt(pass, "5f4dcc3b5aa765d61d8327deb882cf99"));
        editor.apply();
    }
}
