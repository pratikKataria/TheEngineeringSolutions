package com.tes.theengineeringsolutions.admin;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tes.theengineeringsolutions.R;
import com.tes.theengineeringsolutions.activities.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/* CREATED BY PRATIK KATARIYA
 * 1-DEC-2019
 * */

public class UploadTestFile extends AppCompatActivity {

    static final int PICK_CSV_CODE = 0;

    EditText editTextDisplayName; //displayname
    EditText editTextSubject;//test subject
    EditText editTextSubjectCode; //test subject code
    EditText editTextTestCounter; //test number ex - T01, T02, T03 ...
    TextView mDateSelector; //date of test
    EditText editTextPassword; //lock the test
    EditText editTextNumberOfQuestions; //no of questions
    EditText editTextDuration; // test time duration
    ProgressBar progressBar; //show progress of file being upload
    MaterialButton mUploadBtn; //upload button
    MaterialButton mSelectFileBtn; //select file button
    ImageButton imageButtonRefresh;
    Uri fileUri; //uri address of the file in internal storage
    ChipGroup chipGroup;//chip holder group
    Chip chip = null;//chips in group
    StorageReference mStorageRef; // file base storage
    FirebaseFirestore firebaseFirestore; //firebase firestore database
    Long testCounter = 0L;
    private int mYear, mMonth, mDay;


    private static Map<Integer, String> colors = new HashMap<>();
    private int[] keys = {
            R.color.orange,
            R.color.green,
            R.color.darkBrown,
            R.color.skin,
            R.color.lightGrey,
            R.color.blue,
            R.color.greyMaterial,
            R.color.darkMaterial,
            R.color.dullSkin,
            R.color.AliceBlue,
            R.color.chipWrongColor,
            R.color.lightGreenMaterial,
    };


    private void init_fields() {
        //link all the view with instance
        editTextDisplayName = findViewById(R.id.activityTestFile_et_test_disp_name);
        editTextSubject = findViewById(R.id.activityTestFile_et_test_subject);
        editTextSubjectCode = findViewById(R.id.activityTestFile_et_test_subcode);
        editTextTestCounter = findViewById(R.id.activityTestFile_et_test_series);
        mDateSelector = findViewById(R.id.activityTestFile_tv_date_selector);
        mUploadBtn = findViewById(R.id.activityTestFile_mb_upload);
        mSelectFileBtn = findViewById(R.id.activityTestFile_mb_choose_File);
        progressBar = findViewById(R.id.activityTestFile_progress_bar);
        chipGroup = findViewById(R.id.activityTestFile_selected_file_chip);
        editTextPassword = findViewById(R.id.activityTestFile_et_test_password);
        editTextNumberOfQuestions = findViewById(R.id.activityTestFile_et_no_of_question);
        editTextDuration = findViewById(R.id.activityTestFile_et_test_duration);
        imageButtonRefresh = findViewById(R.id.activityTestFile_ib_refresh);

        firebaseFirestore = FirebaseFirestore.getInstance(); //instance of firestore database
        mStorageRef = FirebaseStorage.getInstance().getReference();


        colors.put(R.color.orange,    "#E97939");
        colors.put(R.color.green,     "#607F55");
        colors.put(R.color.darkBrown, "#3C3B1D");
        colors.put(R.color.lightGrey, "#596164");
        colors.put(R.color.greyMaterial, "#838294");
        colors.put(R.color.darkMaterial, "#252831");

        colors.put(R.color.skin,      "#F8C9B5");
        colors.put(R.color.blue,      "#79CBE8");
        colors.put(R.color.dullSkin,  "#D1A38B");
        colors.put(R.color.AliceBlue, "#B4DDF6");
        colors.put(R.color.chipWrongColor,     "#FFD8D8");
        colors.put(R.color.lightGreenMaterial, "#BDD8AF");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_test_file);

        //initialize all view
        init_fields();

        //onclick event on selectFile button
        mSelectFileBtn.setOnClickListener(v -> showFileChooser());
        //onClick event on upload button
        mUploadBtn.setOnClickListener(v -> {

            if (editTextDisplayName.getText().toString().equals("")) {
                editTextDisplayName.setError("should not be empty");
                editTextDisplayName.requestFocus();
                return;
            }

            if (editTextSubject.getText().toString().equals("")) {
                editTextSubject.setError("should not be empty");
                editTextSubject.requestFocus();
                return;
            }

            if (editTextSubjectCode.getText().toString().equals("")) {
                editTextSubjectCode.setError("should not be empty");
                editTextSubjectCode.requestFocus();
                return;
            }

            if (editTextTestCounter.getText().toString().equals("")) {
                editTextTestCounter.setError("refresh test counter");
                return;
            }

            if (editTextPassword.getText().toString().equals("")) {
                editTextPassword.setError("should not be empty");
                editTextPassword.requestFocus();
                return;
            }

            if (editTextNumberOfQuestions.getText().toString().equals("")) {
                editTextNumberOfQuestions.setError("should not be empty");
                editTextNumberOfQuestions.requestFocus();
                return;
            }

            if (editTextDuration.getText().toString().equals("")) {
                editTextDuration.setError("should not be empty");
                editTextDuration.requestFocus();
                return;
            }

            uploadFile();
        });

        imageButtonRefresh.setOnClickListener(v-> setTestSeriesValue());

        //chip on click listener
        if (chip != null)
            chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));

        //onDate Selector clicked
        mDateSelector.setOnClickListener(v -> showDateSelector());

        setTestSeriesValue();
    }

    private void setTestSeriesValue() {
        if (firebaseFirestore != null) {
            DocumentReference documentReference = firebaseFirestore.collection("Admin").document("TestCount");
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Long count = 0L;
                    Map<String, Object> map = task.getResult().getData();

                    if (map != null && map.containsKey("test_counter")) {
                        count = (Long) task.getResult().getData().get("test_counter");
                    }
                    testCounter = count;

                    editTextTestCounter.setText("T" + testCounter);
                    editTextTestCounter.setEnabled(false);
                } else {
                    String taskExcep = task.getException().toString();
                    Toast.makeText(UploadTestFile.this, "failed to update" + taskExcep, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //pop up the file manager when clicked select file

    private void showFileChooser() {
        //intent will open the action
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //select all type of file
        intent.setType("*/*");
        //open default file manager
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            //start activity for intent
            startActivityForResult(Intent.createChooser(intent, "Select a File to upload"), PICK_CSV_CODE);
        } catch (android.content.ActivityNotFoundException xe) {
            Toast.makeText(this, "Please intall a File Manager", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if get file is selected then it create request code of the type pickxlscode
        if (requestCode == PICK_CSV_CODE && resultCode == RESULT_OK && data != null) {
            //update fileUri with selected file
            fileUri = data.getData();

            //file name
            String fileName = queryName(fileUri);
            //enable upload button
            mUploadBtn.setEnabled(true);
            //create chip instance
            chip = createChip(chipGroup, fileName);
            //show selected file as chip
            chipGroup.addView(chip);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //create new chip every time we select the file
    //this is create to show which file is selected
    private Chip createChip(final ChipGroup chipGroup, String text) {
        //chip created programmatically and inflate using layout
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip, chipGroup, false);
        //file name
        chip.setText(text);
        //no click event
        chip.setClickable(false);
        //on close button pressed remove chip form that group
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
        //return chip to add
        return chip;
    }

    private void showDateSelector() {
        //get current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //actual date dialog creator
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, (view, year, month, dayOfMonth)
                -> mDateSelector.setText(dayOfMonth + "-" + (month + 1) + "-" + year), mYear, mMonth, mDay);
        //show dialog
        datePickerDialog.show();
    }

    //upload file to the storage
    private void uploadFile() {
        ///check if fileUri is not null
        if (fileUri != null) {
            //if selected file is of type xls then proceed
            if (getFileExtension(fileUri).equals("csv")) {
                //make progress visible
                progressBar.setVisibility(View.VISIBLE);
                //create new storage Reference add file to child below
                String subjectCode = editTextSubject.getText().toString() + "-" + editTextSubjectCode.getText().toString() + "-" + editTextTestCounter.getText().toString();
                StorageReference storageReference = mStorageRef.child("Admin/Test_Files").child(subjectCode + ".csv");
                //put file to the storage reference
                storageReference.putFile(fileUri).addOnCompleteListener(task -> {
                    //if task is successful then hide progress open the MainActivity
                    if (task.isSuccessful()) {
                        Task<Uri> file = task.getResult().getMetadata().getReference().getDownloadUrl();
                        file.addOnSuccessListener(uri -> {

                            Random random = new Random();

                            Map<String, Object> quizDetails = new HashMap<>();
                            quizDetails.put("display_name", editTextDisplayName.getText().toString());
                            quizDetails.put("file_uri", uri.toString());
                            quizDetails.put("file_name", queryName(fileUri));
                            quizDetails.put("password", editTextPassword.getText().toString());
                            quizDetails.put("number_of_questions", editTextNumberOfQuestions.getText().toString());
                            quizDetails.put("test_duration", editTextDuration.getText().toString());
                            quizDetails.put("subject_code", editTextSubject.getText().toString() +"-"+
                                    editTextSubjectCode.getText().toString() +"-"+
                                    editTextTestCounter.getText().toString());
                            quizDetails.put("date", mDateSelector.getText().toString());
                            quizDetails.put("created", new Date());

                            uploadTestDetails(quizDetails);

                        });//file add on success listener
                    }
                });
            } else {
                Toast.makeText(this, "only exel file is allowed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadTestDetails(Map<String, Object> quizContract) {
        String subjectCode = editTextSubject.getText().toString() + "-" + editTextSubjectCode.getText().toString() + "-" + editTextTestCounter.getText().toString();

        firebaseFirestore.collection("Admin").document(subjectCode).set(quizContract).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(UploadTestFile.this, "successful", Toast.LENGTH_SHORT).show();
                setTestValueForUser();
            } else {
                String error = task1.getException().getMessage();
                Toast.makeText(UploadTestFile.this, "firestore error " + error, Toast.LENGTH_SHORT).show();
            }
        });//firebase firestore
    }

    private void updateTestId() {
        DocumentReference documentReference = firebaseFirestore.collection("Admin").document("TestCount");
        documentReference
                .update("test_counter", testCounter + 1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UploadTestFile.this, "updated Value", Toast.LENGTH_SHORT).show();
            } else {
                String excep = task.getException().toString();
                Toast.makeText(UploadTestFile.this, "failed to update" + excep, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTestValueForUser() {
        List<String> uidList = new ArrayList<>();
        DocumentReference documentReference = firebaseFirestore.collection("Admin").document("UIDS");
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                /*if exist*/
                if (documentSnapshot.exists()) {
                    Map<String, Object> map = documentSnapshot.getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        uidList.add(entry.getValue().toString());
                        String testUID = editTextSubject.getText().toString() + "-" + editTextSubjectCode.getText().toString() + "-" + editTextTestCounter.getText().toString();
                        Map<String, Object> test = new HashMap<>();
                        Map<String, Object> isCompleted = new HashMap<>();
                        test.put(testUID, false);
                        isCompleted.put("test_completed", test);
                        firebaseFirestore.collection("User").document(entry.getValue().toString()).set(isCompleted, SetOptions.merge()).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                updateTestId();
                                Toast.makeText(UploadTestFile.this, "upload successfull", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(UploadTestFile.this, MainActivity.class));
                            } else {
                                Toast.makeText(UploadTestFile.this, "fail to send", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(UploadTestFile.this, "fail to send test", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //give the exact name of the file
    private String queryName(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    //give the exact extension of the selected file
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
