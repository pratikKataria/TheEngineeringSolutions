package com.tes.theengineeringsolutions.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
import com.tes.theengineeringsolutions.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class UploadResultsActivity extends AppCompatActivity {

    HashMap<Integer, Integer> questionAnswered;
    List<LocalTestDatabase> questionList;
    private FirebaseFirestore firebaseFirestore;

    private ProgressBar progressBar;
    private MaterialButton materialButton;

    private int totalQuestion;

    private void initializeFields() {

        progressBar = findViewById(R.id.progressbar);
        materialButton = findViewById(R.id.uploadResult_mb_retry);
        firebaseFirestore = FirebaseFirestore.getInstance();
        populateMap();
        questionList = new ArrayList<>();
        populateTestList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_results);

        initializeFields();
        animation();

        String testCode = getIntent().getStringExtra("TEST_CODE");
        totalQuestion = getIntent().getIntExtra("TOTAL_QUESTIONS", 0);
        String subject = getIntent().getStringExtra("SUBJECT_NAME");

        Date date = new Date();
        String stringDate = new SimpleDateFormat("EE").format(date) + "-" + new SimpleDateFormat("dd").format(date) + "-" + new SimpleDateFormat("MM").format(date) + "-" + new SimpleDateFormat("YYYY").format(date);

        int[] answers = checkAnswers(questionAnswered);
        float percent = ((((float) answers[0]) / totalQuestion)) * 100;

        int[] colors = {
                R.color.orange,
                R.color.green,
                R.color.darkBrown,
                R.color.skin,
                R.color.lightGrey,
                R.color.blue,
                R.color.greyMaterial,
                R.color.darkMaterial,
                R.color.lightGreen,
                R.color.dullSkin,
                R.color.lightGreenMaterial,
                R.color.mudBrown,
        };
        Random random = new Random();


        Map<String, String> fields = new HashMap<>();
        fields.put("subject", subject);
        fields.put("subject_code", testCode);

        fields.put("date", stringDate);

        if (percent > 20)
            fields.put("result", "pass");
        else fields.put("result", "fail");
        fields.put("score", answers[0] + "");

        if (percent > 0 && percent < 10) {
            fields.put("percentage", "0" + ((int) percent));
        } else fields.put("percentage", ((int) percent) + "");

        fields.put("question_correct", answers[0] + "");
        fields.put("questions_incorrect", answers[1] + "");
        fields.put("total_questions", totalQuestion + "");
        fields.put("questions_unanswered", (totalQuestion - questionAnswered.size()) + "");
        fields.put("color", colors[random.nextInt(11)] + "");

        Map<String, Object> testResult = new HashMap<>();
        testResult.put(testCode, fields);

        if (FirebaseAuth.getInstance().getUid() != null) {
            uploadResult(testResult);
        }
    }

    private void uploadResult(Map<String, Object> testResult) {
        DocumentReference documentReference = firebaseFirestore.collection("Results").document(FirebaseAuth.getInstance().getUid());
        documentReference.set(testResult, SetOptions.merge()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UploadResultsActivity.this, "result uploaded", Toast.LENGTH_SHORT).show();
                Toast.makeText(UploadResultsActivity.this, "wait files getting ready", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(UploadResultsActivity.this, QuizResult.class);
                    intent.putExtra("QUESTION_ANSWERED", questionAnswered);
                    intent.putExtra("TOTAL_QUESTIONS", totalQuestion);
                    startActivity(intent);
                    finish();
                }, 2000);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UploadResultsActivity.this, "fail to upload", Toast.LENGTH_SHORT).show();
                materialButton.setVisibility(View.VISIBLE);
                materialButton.setOnClickListener(v -> {
                            progressBar.setVisibility(View.VISIBLE);
                            Map<String, Object> reupload = testResult;
                            uploadResult(reupload);
                        }
                );
            }
        });
    }

    private int[] checkAnswers(HashMap<Integer, Integer> qAnswered) {
        int[] decisions = {0, 0};
        for (LocalTestDatabase q : questionList) {
            if (qAnswered.containsKey(q.getQuestionNo() - 1)) {
                if (q.getAnswer() == qAnswered.get(q.getQuestionNo() - 1)) {
                    decisions[0] = decisions[0] + 1;
                    Log.d("CORRRRRRRRRRECT QUIZRESUKT", "" + decisions[0]);
                } else decisions[1] = decisions[1] + 1;
            }
        }
        return decisions;
    }

    private void populateTestList() {
        for (int questionNumber : questionAnswered.keySet()) {
            String questionNumberString = (questionNumber + 1) + "";
            questionList.addAll(LocalTestDatabase.findWithQuery(LocalTestDatabase.class, "SELECT * FROM LOCAL_TEST_DATABASE WHERE QUESTION_NO == ?", questionNumberString));
        }
//        HashMap<Integer, Integer> map = new HashMap<>();
//        map.put(1, 2);
//        map.put(2, 3);
//        map.put(3, 1);
//        map.put(4, 2);
//        map.put(5, 1);
//        questionAnswered = map;
    }

    private void populateMap() {
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            if (extra.containsKey("QUESTION_ANSWERED")) {
                questionAnswered = (HashMap<Integer, Integer>) getIntent().getSerializableExtra("QUESTION_ANSWERED");
            }
        } else {
            questionAnswered = new HashMap<>();
        }
    }

    private void animation() {
        int[] IDS = {R.id.pulsator1, R.id.pulsator2, R.id.pulsator3, R.id.pulsator4};
        for (int id : IDS) {
            startPulsator(id);
        }

        new Handler().postDelayed(() -> linearLayoutAnimation(R.id.linearLayout2), 2000);
        new Handler().postDelayed(() -> linearLayoutAnimation(R.id.linearLayout3), 4000);
        new Handler().postDelayed(() -> linearLayoutAnimation(R.id.linearLayout4), 5000);
    }

    private void startPulsator(int id) {
        PulsatorLayout layout = (PulsatorLayout) findViewById(id);
        layout.start();
    }

    private void linearLayoutAnimation(int id) {
        LinearLayout linearLayout = findViewById(id);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        linearLayout.startAnimation(animation);
        linearLayout.setVisibility(View.VISIBLE);
    }
}
