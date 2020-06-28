package com.tes.theengineeringsolutions.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.hanks.htextview.evaporate.EvaporateTextView;
import com.tes.theengineeringsolutions.Models.ConnectivityReceiver;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
import com.tes.theengineeringsolutions.R;
import com.tes.theengineeringsolutions.activities.MyApplication;
import com.tes.theengineeringsolutions.utils.ColorUtils;
import com.tes.theengineeringsolutions.utils.SharedPrefsUtils;
import com.tes.theengineeringsolutions.utils.SnackBarNoSwipe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tes.theengineeringsolutions.Models.ConnectivityReceiver.isConnected;

public class UploadResultsActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private List<LocalTestDatabase> questionList = new ArrayList<>();
    private String subjectCode;
    private String subject;
    private int totalQuestion;
    private EvaporateTextView evaporateTextView;
    private MaterialButton retryButton;
    private LottieAnimationView lottieAnimationView;

    private void init_fields() {
        subjectCode = getIntent().getStringExtra("TEST_CODE");
        subject = getIntent().getStringExtra("SUBJECT_NAME");
        totalQuestion = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);

        getTestResultList();
        batchWriteCloudFirestore();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_results);
        evaporateTextView = findViewById(R.id.animatedText);
        retryButton = findViewById(R.id.retryButton);
        lottieAnimationView = findViewById(R.id.lottieAnimation);

        evaporateTextView.animateText("Uploading test file");

        new Handler().postDelayed(() -> {
            evaporateTextView.animateText("Getting Information");
            checkConnection();
            init_fields();
        }, 1000);
    }

    private void getTestResultList() {
        for (String questionNumber : SharedPrefsUtils.keysList(this)) {
            questionList.addAll(LocalTestDatabase.findWithQuery(LocalTestDatabase.class, "SELECT * FROM LOCAL_TEST_DATABASE WHERE QUESTION_NO == ?", questionNumber));
        }
    }

    private Map<String, String> getStudentResultMap(String subject, String subjectCode, int totalQuestions) {

        int[] countedNumberCorrectAndIncorrectAnswers = checkAnswers();
        float percent = ((((float) countedNumberCorrectAndIncorrectAnswers[0]) / totalQuestion)) * 100;

        Map<String, String> resultHashMap = new HashMap<>();
        resultHashMap.put("subject", subject);
        resultHashMap.put("subject_code", subjectCode);
        resultHashMap.put("date", new SimpleDateFormat("EE '-' dd '-' MM '-' YYYY").format(new Date()));
        resultHashMap.put("score", countedNumberCorrectAndIncorrectAnswers[0] + "");
        resultHashMap.put("question_correct", countedNumberCorrectAndIncorrectAnswers[0] + "");
        resultHashMap.put("questions_incorrect", countedNumberCorrectAndIncorrectAnswers[1] + "");
        resultHashMap.put("total_questions", totalQuestions + "");
        resultHashMap.put("questions_unanswered", (totalQuestions - SharedPrefsUtils.preferenceSize(this)) + "");
        resultHashMap.put("color", ColorUtils.getColors());
        resultHashMap.put("badge", "no badge");
        resultHashMap.put("result", percent > 25 ? "pass" : "fail");
        resultHashMap.put("percentage", Math.abs(percent) < 10 ? "0" + (int) percent : (int) percent + "");

        return resultHashMap;
    }

    private int[] checkAnswers() {
        int[] countNumberOfCorrectAndIncorrectQuestions = {0, 0};

        for (LocalTestDatabase currentQuestion : questionList) {
            int answer = SharedPrefsUtils.getIntegerPreference(this, currentQuestion.getQuestionNo() + "", -1);
            if (answer != -1 && answer == currentQuestion.getAnswer())
                countNumberOfCorrectAndIncorrectQuestions[0] = countNumberOfCorrectAndIncorrectQuestions[0] + 1;
            else
                countNumberOfCorrectAndIncorrectQuestions[1] = countNumberOfCorrectAndIncorrectQuestions[1] + 1;
        }
        return countNumberOfCorrectAndIncorrectQuestions;
    }

    private void batchWriteCloudFirestore() {
        evaporateTextView.animateText("Upload started");
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        Map<String, String> data = getStudentResultMap(subject, subjectCode, totalQuestion);
        Map<String, Object> header = new HashMap<>();
        header.put(subjectCode, data);

        DocumentReference resultDocumentReference = FirebaseFirestore.getInstance().collection("Results").document(FirebaseAuth.getInstance().getUid());
        writeBatch.set(resultDocumentReference, header, SetOptions.merge());

        writeBatch.commit().addOnSuccessListener(aVoid -> {
            evaporateTextView.animateText("Test uploaded");
            showQuizResultActivity();
        }).addOnFailureListener(e -> {
            evaporateTextView.animateText("Failed to upload" + e.getMessage());
            lottieAnimationView.pauseAnimation();
            retryButton.setVisibility(View.VISIBLE);
            retryButton.setOnClickListener(v -> {
                batchWriteCloudFirestore();
                lottieAnimationView.playAnimation();
                retryButton.setVisibility(View.GONE);
            });
        });
    }

    private void showQuizResultActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(UploadResultsActivity.this, QuizResult.class);
            startActivity(intent);
            finish();
        }, 600);
    }


    private void checkConnection() {
        boolean isConnected = isConnected();
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }


    private void showSnack(boolean isConnected) {
        String message;
        Snackbar snackbar;
        View parentLayout = findViewById(android.R.id.content);
        int color;
        if (isConnected) {
            message = "Good ! Connected to Internet";
            snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT);
            color = Color.GREEN;
        } else {
            message = "Sorry! Not connected to internet";
            snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", v ->
                    checkConnection()
            );
            color = Color.RED;
        }

        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.setBehavior(new SnackBarNoSwipe());
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to exit ?");
        builder.setMessage("You will loose your score if you exit");
        builder.setPositiveButton("YES", (dialog, which) -> UploadResultsActivity.super.onBackPressed())
                .setNegativeButton("NO", (dialog, which) -> {
                });
    }


    //    private void setProgress(boolean isPass) {
//        if (isPass) {
//            Map<String, Object> header = new HashMap<>();
//            Map<String, Integer> data = new HashMap<>();
//            DocumentReference reference = FirebaseFirestore.getInstance().collection("User").document(FirebaseAuth.getInstance().getUid());
//            reference.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot != null && documentSnapshot.exists()) {
//                        Map<String, Object> data1 = documentSnapshot.getData();
//                        if (data1 != null && data1.containsKey("test_progress")) {
//                            if (((Map<String, Object>) data1.get("test_progress")).containsKey(stringDate.substring(7))) {
//                                long number = (Long) ((Map<String, Object>) data1.get("test_progress")).get(stringDate.substring(7));
//                                data.put(stringDate.substring(7), ((int) number + 1));
//                                header.put("test_progress", data);
//                                reference.set(header, SetOptions.merge());
//                                Toast.makeText(this, "progress upgraded", Toast.LENGTH_SHORT).show();
//                            } else {
//                                data.put(stringDate.substring(7), 0);
//                                header.put("test_progress", data);
//                                reference.set(header, SetOptions.merge());
//                                Toast.makeText(this, "progress set", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            data.put(stringDate.substring(7), 1);
//                            header.put("test_progress", data);
//                            reference.set(header, SetOptions.merge());
//                            Toast.makeText(this, "progress set", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } else {
//                    String e = task.getException().getMessage();
//                    Log.e("TESTFRAGMENT", e);
//                    Toast.makeText(this, "failed to set progress", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

}
