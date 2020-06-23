package com.tes.theengineeringsolutions.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tes.theengineeringsolutions.Models.LocalTestDatabase;
import com.tes.theengineeringsolutions.R;

import java.util.HashMap;
import java.util.List;

/*
 * created by pratik katariya
 * on 5/11/2019
 * updated on 6/11/19
 * updated on 7/11/19
 * updated on 10/11/2019
 */

public class QuizActivity extends AppCompatActivity {

    private TextView testViewTestName;
    private TextView testViewTestCode;
    private TextView testViewQuestions;
    private TextView textViewTime;
    private TextView textViewQuestionAnswered;
    private TextView textViewQuestionRemaining;
    private TextView textViewQuestionCount;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private MaterialButton nextBtn;
    private MaterialButton previousBtn;
    private List<LocalTestDatabase> questionList;
    private int questionCountTotal;
    private int questionCounter;
    private HashMap<Integer, Integer> questionAnswered;
    private String testcode;

    private FirebaseFirestore firebaseFirestore;

    private void initializeFields() {
        testViewQuestions = findViewById(R.id.testSession_tv_question);
        textViewQuestionCount = findViewById(R.id.testSession_tv_question_count);
        textViewQuestionAnswered = findViewById(R.id.quizActivity_tv_answered);
        textViewQuestionRemaining = findViewById(R.id.quizActivity_tv_remaining);
        testViewTestName = findViewById(R.id.testSession_tv_test_name);
        testViewTestCode = findViewById(R.id.testSession_tv_test_code);
        textViewTime = findViewById(R.id.textView_time);

        testViewTestName.setText(getIntent().getStringExtra("TEST_NAME"));
        testViewTestCode.setText(getIntent().getStringExtra("TEST_CODE"));
        textViewTime.setText(getIntent().getStringExtra("TEST_TOTAL_QUESTION").substring(0,2)+"  \u2022  "+ getIntent().getStringExtra("TEST_DURATION").substring(0,2) +" MINS");

        testcode = getIntent().getStringExtra("TEST_CODE");

        rbGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.rb_option1);
        rb2 = findViewById(R.id.rb_option2);
        rb3 = findViewById(R.id.rb_option3);
        rb4 = findViewById(R.id.rb_option4);
        nextBtn = findViewById(R.id.testSession_mb_next);
        previousBtn = findViewById(R.id.testSession_mb_previous);

        questionList = LocalTestDatabase.listAll(LocalTestDatabase.class);
        questionCountTotal = questionList.size();
        questionAnswered = new HashMap<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_activity);

        initializeFields();

        if (questionCountTotal > 0) {
            showQuestion();
            setPaperDetails();
        }

        nextBtn.setOnClickListener(v -> {
            if (questionCounter < questionCountTotal - 1) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    saveAnswers();
                    questionCounter++;
                    showQuestion();
                    setCheckedRb();
                    setPaperDetails();
                } else {
                    questionCounter++;
                    showQuestion();
                    setCheckedRb();
                    setPaperDetails();
                }
            } else {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    saveAnswers();
                    setPaperDetails();
                    showResult();
                }
                showResult();
            }
        });

        previousBtn.setOnClickListener(v -> {
            if (questionCounter > 0) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked())
                    saveAnswers();
                questionCounter--;
                showQuestion();
                setCheckedRb();
                setPaperDetails();
            }
        });

    }

    private void showResult() {
        questionAnswered.forEach((K, V) -> {
            Log.e("HASHMAP_VALUE", " key " + K + " value " + V);
        });
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("would you like to submit questions? ")
                .setMessage("submitting would show the result? ")
                .setPositiveButton("submit", (dialog, which) -> {
                    if (questionAnswered.size() > 0) {
                        showIntent();
                        Toast.makeText(QuizActivity.this, "value updated", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(this, "must select one question", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("cancel", (dialog, which) -> {

                });
        alertDialog.show();
    }

    private void saveAnswers() {
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNumber = rbGroup.indexOfChild(rbSelected) + 1;
        questionAnswered.put(questionCounter, answerNumber);
        Log.e("QUIZACT_SAVEANS", "question answered = " + questionCounter + "question number = " + answerNumber);
    }

    private void setCheckedRb() {
        if (questionAnswered.containsKey(questionCounter)) {
            Log.e("ContainsKey", questionCounter + " question answered" + questionAnswered.get(questionCounter));

            int rbIndex = questionAnswered.get(questionCounter);

            switch (rbIndex) {
                case 1:
                    rb1.setChecked(true);
                    break;
                case 2:
                    rb2.setChecked(true);
                    break;
                case 3:
                    rb3.setChecked(true);
                    break;
                case 4:
                    rb4.setChecked(true);
                    break;
            }
        } else {
            rbGroup.clearCheck();
        }
    }

    public void showQuestion() {
        LocalTestDatabase localTestDatabase = questionList.get(questionCounter);
        if (questionCounter < questionCountTotal && questionCounter >= 0) {
            testViewQuestions.setText("Q" + localTestDatabase.getQuestionNo() + ". " + localTestDatabase.getQuestions());
            rb1.setText(localTestDatabase.getChoice1());
            rb2.setText(localTestDatabase.getChoice2());
            rb3.setText(localTestDatabase.getChoice3());
            rb4.setText(localTestDatabase.getChoice4());
        }
    }

    private void setPaperDetails() {
        textViewQuestionCount.setText("Question: " + (questionCounter + 1) + "/" + questionCountTotal);
        textViewQuestionAnswered.setText("Answered: " + questionAnswered.size());
        textViewQuestionRemaining.setText("Remaining: " + (questionCountTotal - questionAnswered.size()) + "/" + questionCountTotal);
    }

    private void showIntent() {
        Intent intent = new Intent(QuizActivity.this, UploadResultsActivity.class);
        intent.putExtra("QUESTION_ANSWERED", questionAnswered);
        intent.putExtra("TEST_CODE", testViewTestCode.getText().toString());
        intent.putExtra("TOTAL_QUESTIONS", questionCountTotal);
        intent.putExtra("SUBJECT_NAME", testViewTestName.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to exit? ");
        builder.setMessage("Existing will loose your score");
        builder.setPositiveButton("Save", (dialog, id) -> {
            if (questionAnswered.size() > 0) {
               showIntent();
            } else
                Toast.makeText(this, "at least select one answer", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Exit", (dialog, id) -> {
            QuizActivity.super.onBackPressed();
        });
        builder.show();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        LocalTestDatabase.deleteAll(LocalTestDatabase.class);
//    }
}
