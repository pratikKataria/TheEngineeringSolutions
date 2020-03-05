package com.tes.theengineeringsolutions.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tes.theengineeringsolutions.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UploadNotesActivity extends AppCompatActivity {

    private final static int PICK_DOCUMENT_FILE = 101;
    private EditText editTextFileName;
    private MaterialButton uploadBtn;
    private MaterialButton addFilesBtn;
    private ProgressBar uploadProgressBar;
    private TextView textViewSelectedFileName;
    private Uri fileUri;
    String postUID;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notes);

        editTextFileName = findViewById(R.id.activity_upload_notes_et_file_name);
        uploadBtn = findViewById(R.id.activity_upload_notes_mb_upload_file);
        textViewSelectedFileName = findViewById(R.id.activity_upload_notes_tv_selected_file);
        addFilesBtn = findViewById(R.id.activity_upload_notes_mb_add_file);

        uploadProgressBar = findViewById(R.id.activity_upload_notes_pb_upload_progress);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        addFilesBtn.setOnClickListener(v -> showFileChooser());

        uploadBtn.setOnClickListener(v -> {
            if (editTextFileName.getText().toString().isEmpty()) {
                editTextFileName.setError("should not be empty");
                editTextFileName.requestFocus();
                return;
            }
            uploadFile();
        });

    }

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "select a File to Upload"), PICK_DOCUMENT_FILE);
        } catch (android.content.ActivityNotFoundException xe) {
            Toast.makeText(this, "please install a File Manager", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (requestCode == PICK_DOCUMENT_FILE && resultCode == RESULT_OK && data != null) {
            Log.e("NOTES ACTIVITY ", requestCode+"");
            fileUri = data.getData();

            postUID = getSaltString();

            String fileName = queryName(fileUri);
            textViewSelectedFileName.setText(fileName);
            uploadBtn.setEnabled(true);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String queryName(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void uploadFile() {
        if (fileUri != null) {
            String exe = getFileExtension(fileUri);
            if (exe.equals("doc") || exe.equals("pdf") || exe.equals("xls")) {
                uploadProgressBar.setVisibility(View.VISIBLE);
                StorageReference storageReference = mStorageRef.child("Notes").child(editTextFileName.getText().toString()+postUID+"."+getFileExtension(fileUri));
                storageReference.putFile(fileUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Task<Uri> file = task.getResult().getMetadata().getReference().getDownloadUrl();
                        file.addOnSuccessListener(uri -> {
                            Map<String, Object> notesDetails = new HashMap<>();
                            notesDetails.put("notesId", postUID);
                            notesDetails.put("fileName", editTextFileName.getText().toString());
                            notesDetails.put("fileUri", fileUri.toString());
                            notesDetails.put("created", new Date());

                            uploadProgressBar.setVisibility(View.GONE);
                            uploadTestDetails(notesDetails);

                        });
                    }
                }) .addOnFailureListener(e -> {
                    uploadProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadNotesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, "only doc can be used to upload", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadTestDetails(Map<String, Object> notesDetails) {
        firebaseFirestore.collection("Notes").document(postUID).set(notesDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UploadNotesActivity.this, "upload successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                uploadProgressBar.setVisibility(View.GONE);
                Toast.makeText(UploadNotesActivity.this, "error while uploading ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 12) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
